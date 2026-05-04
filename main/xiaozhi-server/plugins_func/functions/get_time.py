from datetime import datetime
import cnlunar
from plugins_func.register import register_function, ToolType, ActionResponse, Action

get_lunar_function_desc = {
    "type": "function",
    "function": {
        "name": "get_lunar",
        "description": (
            "For lunar calendar and almanac information for a specific date. "
            "The user can specify query content, such as lunar date, heavenly stems and earthly branches, solar terms, zodiac, constellation, eight characters, auspicious/inauspicious activities, etc. "
            "If no query content is specified, the default is to query the stem-branch year and lunar date. "
            "For basic queries like 'What is today's lunar date', 'Today's lunar date', please use the information in context directly, do not call this tool."
        ),
        "parameters": {
            "type": "object",
            "properties": {
                "date": {
                    "type": "string",
                    "description": "The date to query, format is Q-MM-DD, e.g., 2024-01-01. If not provided, the current date is used.",
                },
                "query": {
                    "type": "string",
                    "description": "The content to query, e.g., lunar date, heavenly stems and earthly branches, festival, solar term, zodiac, constellation, eight characters, auspicious/inauspicious activities, etc.",
                },
            },
            "required": [],
        },
    },
}


def get_lunar(date=None, query=None):
    @register_function("get_lunar", get_lunar_function_desc, ToolType.WAIT)
    def get_lunar(date=None, query=None):
        """
        Used to get the current lunar calendar, and almanac information such as heavenly stems and earthly branches, solar terms, zodiac, constellation, eight characters, auspicious/inauspicious activities, etc.
        """
        from core.utils.cache.manager import cache_manager, CacheType

        # If a date parameter is provided, use the specified date; otherwise use the current date
        if date:
            try:
                now = datetime.strptime(date, "%Y-%m-%d")
            except ValueError:
                return ActionResponse(
                    Action.REQLLM,
                    "Date format error, please use Q-MM-DD format, e.g.: 2024-01-01",
                    None,
                )
        else:
            now = datetime.now()

        current_date = now.strftime("%Y-%m-%d")

        # If query is None, use default text
        if query is None:
            query = "Default query for stem-branch year and lunar date"

        # Try to get lunar info from cache
        lunar_cache_key = f"lunar_info_{current_date}"
        cached_lunar_info = cache_manager.get(CacheType.LUNAR, lunar_cache_key)
        if cached_lunar_info:
            return ActionResponse(Action.REQLLM, cached_lunar_info, None)

        response_text = f"Respond to the user's query request based on the following information, and provide information related to {query}:\n"

        lunar = cnlunar.Lunar(now, godType="8char")
        response_text += (
            "Lunar info:\n"
            "%s year %s%s\n"
            % (lunar.lunarYearCn, lunar.lunarMonthCn[:-1], lunar.lunarDayCn)
            + "Heavenly Stems and Earthly Branches: %s year %s month %s day\n"
            % (lunar.year8Char, lunar.month8Char, lunar.day8Char)
            + "Zodiac: %s\n" % (lunar.chineseYearZodiac)
            + "Eight Characters: %s\n"
            % (
                " ".join(
                    [
                        lunar.year8Char,
                        lunar.month8Char,
                        lunar.day8Char,
                        lunar.twohour8Char,
                    ]
                )
            )
            + "Today's Festivals: %s\n"
            % (
                ",".join(
                    filter(
                        None,
                        (
                            lunar.get_legalHolidays(),
                            lunar.get_otherHolidays(),
                            lunar.get_otherLunarHolidays(),
                        ),
                    )
                )
            )
            + "Today's Solar Term: %s\n" % (lunar.todaySolarTerms)
            + "Next Solar Term: %s %s year %s month %s day\n"
            % (
                lunar.nextSolarTerm,
                lunar.nextSolarTermYear,
                lunar.nextSolarTermDate[0],
                lunar.nextSolarTermDate[1],
            )
            + "This Year's Solar Terms: %s\n"
            % (
                ", ".join(
                    [
                        f"{term}({date[0]} month {date[1]} day)"
                        for term, date in lunar.thisYearSolarTermsDic.items()
                    ]
                )
            )
            + "Zodiac Clash: %s\n" % (lunar.chineseZodiacClash)
            + "Constellation: %s\n" % (lunar.starZodiac)
            + "Nayin: %s\n" % lunar.get_nayin()
            + "Pengzu Taboo: %s\n" % (lunar.get_pengTaboo(delimit=", "))
            + "Day Officer: %s position\n" % lunar.get_today12DayOfficer()[0]
            + "Day God: %s(%s)\n"
            % (lunar.get_today12DayOfficer()[1], lunar.get_today12DayOfficer()[2])
            + "The 28 Stars: %s\n" % lunar.get_the28Stars()
            + "Lucky Gods Direction: %s\n" % " ".join(lunar.get_luckyGodsDirection())
            + "Today's Fetal God: %s\n" % lunar.get_fetalGod()
            + "Auspicious: %s\n" % ", ".join(lunar.goodThing[:10])
            + "Inauspicious: %s\n" % ", ".join(lunar.badThing[:10])
            + "(By default, returns the stem-branch year and lunar date; only returns today's auspicious/inauspicious activities when requested.)"
        )

        cache_manager.set(CacheType.LUNAR, lunar_cache_key, response_text)

        return ActionResponse(Action.REQLLM, response_text, None)
