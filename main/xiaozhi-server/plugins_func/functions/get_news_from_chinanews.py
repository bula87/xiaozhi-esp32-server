import random
import requests
import xml.etree.ElementTree as ET
from bs4 import BeautifulSoup
from config.logger import setup_logging
from plugins_func.register import register_function, ToolType, ActionResponse, Action
from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from core.connection import ConnectionHandler


TAG = __name__
logger = setup_logging()

GET_NEWS_FROM_CHINANEWS_FUNCTION_DESC = {
    "type": "function",
    "function": {
        "name": "get_news_from_chinanews",
        "description": (
            "Get the latest news and randomly select one news item to broadcast. "
            "The user can specify the news category, such as society, technology, international, etc. "
            "If not specified, the default is society news. "
            "The user can request detailed content, in which case the detailed news content will be fetched."
        ),
        "parameters": {
            "type": "object",
            "properties": {
                "category": {
                    "type": "string",
                    "description": "News category, e.g., society, technology, international. Optional parameter, if not provided, the default category is used.",
                },
                "detail": {
                    "type": "boolean",
                    "description": "Whether to get detailed content, default is false. If true, fetch the detailed content of the previous news.",
                },
                "lang": {
                    "type": "string",
                    "description": "Language code used by the user, e.g., zh_CN/zh_HK/en_US/ja_JP, default is zh_CN.",
                },
            },
            "required": ["lang"],
        },
    },
}


def fetch_news_from_rss(rss_url):
    """Fetch news list from RSS source"""
    try:
        response = requests.get(rss_url)
        response.raise_for_status()

        # Parse XML
        root = ET.fromstring(response.content)

        # Find all item elements (news entries)
        news_items = []
        for item in root.findall(".//item"):
            title = (
                item.find("title").text
                if item.find("title") is not None
                else "No Title"
            )
            link = item.find("link").text if item.find("link") is not None else "#"
            description = (
                item.find("description").text
                if item.find("description") is not None
                else "No Description"
            )
            pubDate = (
                item.find("pubDate").text
                if item.find("pubDate") is not None
                else "Unknown Time"
            )

            news_items.append(
                {
                    "title": title,
                    "link": link,
                    "description": description,
                    "pubDate": pubDate,
                }
            )

        return news_items
    except Exception as e:
        logger.bind(tag=TAG).error(f"Failed to fetch RSS news: {e}")
        return []


def fetch_news_detail(url):
    """Fetch news detail page content and summarize"""
    try:
        response = requests.get(url)
        response.raise_for_status()

        soup = BeautifulSoup(response.content, "html.parser")

        # Try to extract main content (the selector here may need adjustment based on the actual website structure)
        content_div = soup.select_one(
            ".content_desc, .content, article, .article-content"
        )
        if content_div:
            paragraphs = content_div.find_all("p")
            content = "\n".join(
                [p.get_text().strip() for p in paragraphs if p.get_text().strip()]
            )
            return content
        else:
            # If a specific content area is not found, try to get all paragraphs
            paragraphs = soup.find_all("p")
            content = "\n".join(
                [p.get_text().strip() for p in paragraphs if p.get_text().strip()]
            )
            return content[:2000]  # Limit length
    except Exception as e:
        logger.bind(tag=TAG).error(f"Failed to fetch news detail: {e}")
        return "Unable to fetch detailed content"


def map_category(category_text):
    """Map the user's input category to the category key in the config file"""
    if not category_text:
        return None

    # Category mapping dictionary, currently supports society, international, and finance news. For more types, see the config file.
    category_map = {
        # Society news
        "Society": "society_rss_url",
        "Society News": "society_rss_url",
        # International news
        "International": "world_rss_url",
        "International News": "world_rss_url",
        # Finance news
        "Finance": "finance_rss_url",
        "Finance News": "finance_rss_url",
        "Financial": "finance_rss_url",
        "Economy": "finance_rss_url",
    }

    # Convert to lowercase and remove spaces
    normalized_category = category_text.lower().strip()

    # Return the mapping result, if not matched, return the original input
    return category_map.get(normalized_category, category_text)


@register_function(
    "get_news_from_chinanews",
    GET_NEWS_FROM_CHINANEWS_FUNCTION_DESC,
    ToolType.SYSTEM_CTL,
)
def get_news_from_chinanews(
    conn: "ConnectionHandler",
    category: str = None,
    detail: bool = False,
    lang: str = "zh_CN",
):
    """Fetch news and randomly select one to broadcast, or get the details of the previous news item"""
    try:
        # If detail is True, get the details of the previous news item
        if detail:
            if (
                not hasattr(conn, "last_news_link")
                or not conn.last_news_link
                or "link" not in conn.last_news_link
            ):
                return ActionResponse(
                    Action.REQLLM,
                    "Sorry, no recent news found. Please fetch a news item first.",
                    None,
                )

            link = conn.last_news_link.get("link")
            title = conn.last_news_link.get("title", "Unknown Title")

            if link == "#":
                return ActionResponse(
                    Action.REQLLM,
                    "Sorry, this news does not have a valid link for detailed content.",
                    None,
                )

            logger.bind(tag=TAG).debug(f"Fetching news detail: {title}, URL={link}")

            # Fetch news detail
            detail_content = fetch_news_detail(link)

            if (
                not detail_content
                or detail_content == "Unable to fetch detailed content"
            ):
                return ActionResponse(
                    Action.REQLLM,
                    f"Sorry, unable to fetch detailed content for '{title}'. The link may be invalid or the website structure may have changed.",
                    None,
                )

            # Build detail report
            detail_report = (
                f"According to the following data, respond to the user's news detail query in {lang}:\n\n"
                f"News Title: {title}\n"
                f"Detail Content: {detail_content}\n\n"
                f"(Please summarize the above news content, extract key information, and broadcast it to the user in a natural and fluent way. Do not mention that this is a summary, just tell it as a complete news story.)"
            )

            return ActionResponse(Action.REQLLM, detail_report, None)

        # Otherwise, get the news list and randomly select one
        # Get RSS URL from config
        rss_config = conn.config.get("plugins", {}).get("get_news_from_chinanews", {})
        default_rss_url = rss_config.get(
            "default_rss_url", "https://www.chinanews.com.cn/rss/society.xml"
        )

        # Map the user's input category to the config key
        mapped_category = map_category(category)

        # If a category is provided, try to get the corresponding URL from config
        rss_url = default_rss_url
        if mapped_category and mapped_category in rss_config:
            rss_url = rss_config[mapped_category]

        logger.bind(tag=TAG).info(
            f"Fetching news: original category={category}, mapped category={mapped_category}, URL={rss_url}"
        )

        # Get news list
        news_items = fetch_news_from_rss(rss_url)

        if not news_items:
            return ActionResponse(
                Action.REQLLM,
                "Sorry, failed to fetch news information. Please try again later.",
                None,
            )

        # Randomly select a news item
        selected_news = random.choice(news_items)

        # Save the current news link to the connection object for later detail queries
        if not hasattr(conn, "last_news_link"):
            conn.last_news_link = {}
        conn.last_news_link = {
            "link": selected_news.get("link", "#"),
            "title": selected_news.get("title", "Unknown Title"),
        }

        # Build news report
        news_report = (
            f"According to the following data, respond to the user's news query in {lang}:\n\n"
            f"News Title: {selected_news['title']}\n"
            f"Published Time: {selected_news['pubDate']}\n"
            f"News Content: {selected_news['description']}\n"
            f"(Please broadcast this news to the user in a natural and fluent way, you may summarize the content appropriately. "
            f"Just read the news directly, no extra content is needed. "
            f"If the user asks for more details, inform them they can say 'Please introduce this news in detail' to get more content.)"
        )

        return ActionResponse(Action.REQLLM, news_report, None)

    except Exception as e:
        logger.bind(tag=TAG).error(f"Error occurred while fetching news: {e}")
        return ActionResponse(
            Action.REQLLM,
            "Sorry, an error occurred while fetching news. Please try again later.",
            None,
        )
