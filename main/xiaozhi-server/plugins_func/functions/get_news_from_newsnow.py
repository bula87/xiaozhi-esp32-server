import random
import requests
from config.logger import setup_logging
from plugins_func.register import register_function, ToolType, ActionResponse, Action
from markitdown import MarkItDown
from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from core.connection import ConnectionHandler


TAG = __name__
logger = setup_logging()

CHANNEL_MAP = {
    "V2EX": "v2ex-share",
    "Zhihu": "zhihu",
    "Weibo": "weibo",
    "Zaobao": "zaobao",
    "Coolapk": "coolapk",
    "MKTNews": "mktnews-flash",
    "Wallstreet News": "wallstreetcn-quick",
    "36kr": "36kr-quick",
    "Douyin": "douyin",
    "Hupu": "hupu",
    "Baidu Tieba": "tieba",
    "Toutiao": "toutiao",
    "IT Home": "ithome",
    "The Paper": "thepaper",
    "Satellite News Agency": "sputniknewscn",
    "Reference News": "cankaoxiaoxi",
    "Far View Forum": "pcbeta-windows11",
    "Cailian News": "cls-depth",
    "Snowball": "xueqiu-hotstock",
    "Gelonghui": "gelonghui",
    "Fabu Finance": "fastbull-express",
    "Solidot": "solidot",
    "Hacker News": "hackernews",
    "Product Hunt": "producthunt",
    "Bilibili": "bilibili-hot-search",
    "Kuaishou": "kuaishou",
    "Kaopu": "kaopu",
    "Jin10": "jin10",
    "Baidu": "baidu",
    "Nowcoder": "nowcoder",
    "SSPai": "sspai",
    "Juejin": "juejin",
    "ifeng": "ifeng",
    "Chongbuluo": "chongbuluo-latest",
}

# Default news sources string, used when not specified in config
DEFAULT_NEWS_SOURCES = "The Paper;Baidu Hot Search;Caixin"


def get_news_sources_from_config(conn):
    """Get news sources string from config"""
    try:
        # Try to get news sources from plugin config
        news_sources_config = (
            conn.config.get("plugins", {})
            .get("get_news_from_newsnow", {})
            .get("news_sources")
        )

        if isinstance(news_sources_config, str) and news_sources_config.strip():
            logger.bind(tag=TAG).debug(
                f"Using configured news sources: {news_sources_config}"
            )
            return news_sources_config
        else:
            logger.bind(tag=TAG).warning(
                "News sources config is empty or invalid, using default config"
            )

        return DEFAULT_NEWS_SOURCES

    except Exception as e:
        logger.bind(tag=TAG).error(
            f"Failed to get news sources config: {e}, using default config"
        )
        return DEFAULT_NEWS_SOURCES


# Get all available news source names from CHANNEL_MAP
available_sources = list(CHANNEL_MAP.keys())
example_sources_str = ", ".join(available_sources)

GET_NEWS_FROM_NEWSNOW_FUNCTION_DESC = {
    "type": "function",
    "function": {
        "name": "get_news_from_newsnow",
        "description": (
            "Get the latest news and randomly select one news item to broadcast. "
            f"The user can choose different news sources, the standard names are: {example_sources_str}. "
            "For example, if the user asks for Baidu News, it actually refers to Baidu Hot Search. If not specified, The Paper is used by default. "
            "The user can request detailed content, in which case the detailed news content will be fetched."
        ),
        "parameters": {
            "type": "object",
            "properties": {
                "source": {
                    "type": "string",
                    "description": f"Standard news source name, e.g., {example_sources_str}, etc. Optional parameter, if not provided, the default news source is used.",
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


def fetch_news_from_api(conn: "ConnectionHandler", source="thepaper"):
    """Fetch news list from API"""
    try:
        api_url = f"https://newsnow.busiyi.world/api/s?id={source}"

        news_config = conn.config.get("plugins", {}).get("get_news_from_newsnow", {})
        if news_config.get("url"):
            api_url = news_config["url"] + source

        headers = {"User-Agent": "Mozilla/5.0"}
        response = requests.get(api_url, headers=headers, timeout=10)
        response.raise_for_status()

        data = response.json()

        if "items" in data:
            return data["items"]
        else:
            logger.bind(tag=TAG).error(f"News API response format error: {data}")
            return []

    except Exception as e:
        logger.bind(tag=TAG).error(f"Failed to fetch news from API: {e}")
        return []


def fetch_news_detail(url):
    """Fetch news detail page content and clean HTML with MarkItDown"""
    try:
        headers = {"User-Agent": "Mozilla/5.0"}
        response = requests.get(url, headers=headers, timeout=10)
        response.raise_for_status()

        # Clean HTML content with MarkItDown
        md = MarkItDown(enable_plugins=False)
        result = md.convert(response)

        # Get cleaned text content
        clean_text = result.text_content

        # If cleaned content is empty, return a prompt message
        if not clean_text or len(clean_text.strip()) == 0:
            logger.bind(tag=TAG).warning(f"Cleaned news content is empty: {url}")
            return "Unable to parse news detail content, the website structure may be special or content is restricted."

        return clean_text
    except Exception as e:
        logger.bind(tag=TAG).error(f"Failed to fetch news detail: {e}")
        return "Unable to fetch detailed content"


@register_function(
    "get_news_from_newsnow",
    GET_NEWS_FROM_NEWSNOW_FUNCTION_DESC,
    ToolType.SYSTEM_CTL,
)
def get_news_from_newsnow(
    conn: "ConnectionHandler",
    source: str = "The Paper",
    detail: bool = False,
    lang: str = "zh_CN",
):
    """Fetch news and randomly select one to broadcast, or get the details of the previous news item"""
    try:
        # Get the current configured news sources
        news_sources = get_news_sources_from_config(conn)

        # If detail is True, get the details of the previous news item
        detail = str(detail).lower() == "true"
        if detail:
            if (
                not hasattr(conn, "last_newsnow_link")
                or not conn.last_newsnow_link
                or "url" not in conn.last_newsnow_link
            ):
                return ActionResponse(
                    Action.REQLLM,
                    "Sorry, no recent news found. Please fetch a news item first.",
                    None,
                )

            url = conn.last_newsnow_link.get("url")
            title = conn.last_newsnow_link.get("title", "Unknown Title")
            source_id = conn.last_newsnow_link.get("source_id", "thepaper")
            source_name = CHANNEL_MAP.get(source_id, "Unknown Source")

            if not url or url == "#":
                return ActionResponse(
                    Action.REQLLM,
                    "Sorry, this news does not have a valid link for detailed content.",
                    None,
                )

            logger.bind(tag=TAG).debug(
                f"Fetching news detail: {title}, Source: {source_name}, URL={url}"
            )

            # Fetch news detail
            detail_content = fetch_news_detail(url)

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
        # Convert the Chinese name to English ID
        english_source_id = None

        # Check if the input Chinese name is in the configured news sources
        news_sources_list = [
            name.strip() for name in news_sources.split(";") if name.strip()
        ]
        if source in news_sources_list:
            # If the input Chinese name is in the configured news sources, find the corresponding English ID in CHANNEL_MAP
            english_source_id = CHANNEL_MAP.get(source)

        # If the corresponding English ID is not found, use the default source
        if not english_source_id:
            logger.bind(tag=TAG).warning(
                f"Invalid news source: {source}, using default source The Paper"
            )
            english_source_id = "thepaper"
            source = "The Paper"

        logger.bind(tag=TAG).info(
            f"Fetching news: source={source}({english_source_id})"
        )

        # Get news list
        news_items = fetch_news_from_api(conn, english_source_id)

        if not news_items:
            return ActionResponse(
                Action.REQLLM,
                f"Sorry, failed to fetch news from {source}. Please try again later or try another news source.",
                None,
            )

        # Randomly select a news item
        selected_news = random.choice(news_items)

        # Save the current news link to the connection object for later detail queries
        if not hasattr(conn, "last_newsnow_link"):
            conn.last_newsnow_link = {}
        conn.last_newsnow_link = {
            "url": selected_news.get("url", "#"),
            "title": selected_news.get("title", "Unknown Title"),
            "source_id": english_source_id,
        }

        # Build news report
        news_report = (
            f"According to the following data, respond to the user's news query in {lang}:\n\n"
            f"News Title: {selected_news['title']}\n"
            f"(Please broadcast this news title to the user in a natural and fluent way. "
            f"Remind the user that they can request detailed content, in which case the detailed news content will be fetched.)"
        )

        return ActionResponse(Action.REQLLM, news_report, None)

    except Exception as e:
        logger.bind(tag=TAG).error(f"Error occurred while fetching news: {e}")
        return ActionResponse(
            Action.REQLLM,
            "Sorry, an error occurred while fetching news. Please try again later.",
            None,
        )
