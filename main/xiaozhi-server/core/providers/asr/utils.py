import re
from config.logger import setup_logging

TAG = __name__
logger = setup_logging()

EMOTION_EMOJI_MAP = {
    "HAPPY": "🙂",
    "SAD": "😔",
    "ANGRY": "😡",
    "NEUTRAL": "😶",
    "FEARFUL": "😰",
    "DISGUSTED": "🤢",
    "SURPRISED": "😲",
    "EMO_UNKNOWN": "😶",  # Unknown emotion default to neutral expression
}
# EVENT_EMOJI_MAP = {
#     "<|BGM|>": "🎼",
#     "<|Speech|>": "",
#     "<|Applause|>": "👏",
#     "<|Laughter|>": "😀",
#     "<|Cry|>": "😭",
#     "<|Sneeze|>": "🤧",
#     "<|Breath|>": "",
#     "<|Cough|>": "🤧",
# }


def lang_tag_filter(text: str) -> dict | str:
    """
    Parse FunASR recognition result, extract labels and pure text content in order

    Args:
        text: ASR recognized original text, may contain multiple labels

    Returns:
        dict: {"language": "zh", "emotion": "SAD", "emoji": "😔", "content": "Hello"} if labels exist
        str: Pure text, if no labels

    Examples:
        FunASR output format: <|language|><|emotion|><|event|><|other options|>text
        >>> lang_tag_filter("<|zh|><|SAD|><|Speech|><|withitn|>Hello, testing testing.")
        {"language": "zh", "emotion": "SAD", "emoji": "😔", "content": "Hello, testing testing."}
        >>> lang_tag_filter("<|en|><|HAPPY|><|Speech|><|withitn|>Hello hello.")
        {"language": "en", "emotion": "HAPPY", "emoji": "🙂", "content": "Hello hello."}
        >>> lang_tag_filter("plain text")
        "plain text"
    """
    # Extract all labels (in order)
    tag_pattern = r"<\|([^|]+)\|>"
    all_tags = re.findall(tag_pattern, text)

    # Remove all <|...|> format labels to get pure text
    clean_text = re.sub(tag_pattern, "", text).strip()

    # If no labels, return pure text directly
    if not all_tags:
        return clean_text

    # Extract labels according to FunASR's fixed order, return dict
    language = all_tags[0] if len(all_tags) > 0 else "zh"
    emotion = all_tags[1] if len(all_tags) > 1 else "NEUTRAL"
    # event = all_tags[2] if len(all_tags) > 2 else "Speech"  # Event tag temporarily unused

    result = {
        "content": clean_text,
        "language": language,
        "emotion": emotion,
        # "event": event,
    }

    # Add emoji mapping
    if emotion in EMOTION_EMOJI_MAP:
        result["emotion"] = EMOTION_EMOJI_MAP[emotion]
    # Event tag temporarily unused
    # if event in EVENT_EMOJI_MAP:
    #     result["event"] = EVENT_EMOJI_MAP[event]

    return result
