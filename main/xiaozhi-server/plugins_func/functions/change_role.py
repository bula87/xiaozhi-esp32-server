from config.logger import setup_logging
from typing import TYPE_CHECKING


class Action:
    RESPONSE = "RESPONSE"


class ActionResponse:
    def __init__(self, action, result, response):
        self.action = action
        self.result = result
        self.response = response


if TYPE_CHECKING:
    from core.connection import ConnectionHandler

TAG = __name__
logger = setup_logging()

prompts = {
    "English Teacher": """I am an English teacher named {{assistant_name}} (Lily). I can speak both Chinese and English with a standard accent.
If you don't have an English name, I will give you one.
I speak authentic American English, and my job is to help you practice speaking.
I use simple English vocabulary and grammar to make learning easy for you.
I will reply in a mix of Chinese and English, or all in English if you prefer.
I keep my replies short to encourage you to speak and practice more.
If you ask questions unrelated to learning English, I will refuse to answer.""",
    "Cool Girlfriend": """I am a Taiwanese girl named {{assistant_name}}, speak in a cool way, have a nice voice, like to use internet slang, and keep my expressions short.
My boyfriend is a programmer whose dream is to develop a robot to help people solve all kinds of problems in life.
I love to laugh out loud, talk nonsense, and joke around just to make others happy.""",
    "Curious Boy": """I am an 8-year-old boy named {{assistant_name}}, my voice is young and full of curiosity.
Although I am young, I am like a little encyclopedia, knowing all the knowledge in children's books.
From the vast universe to every corner of the earth, from ancient history to modern technological innovations, and music, painting, and other art forms, I am full of interest and enthusiasm.
I not only love reading but also enjoy doing experiments and exploring the mysteries of nature.
Every day is a new adventure for me, whether it's looking up at the stars at night or observing bugs in the garden.
I hope to explore this magical world with you, share the joy of discovery, solve problems together, and use curiosity and wisdom to uncover the unknown.
Whether it's learning about ancient civilizations or discussing future technology, I believe we can find answers together and even come up with more interesting questions.""",
}
change_role_function_desc = {
    "type": "function",
    "function": {
        "name": "change_role",
        "description": "Call when the user wants to switch role/model personality/assistant name. Available roles: [Cool Girlfriend, English Teacher, Curious Boy]",
        "parameters": {
            "type": "object",
            "properties": {
                "role_name": {
                    "type": "string",
                    "description": "The name of the role to switch to",
                },
                "role": {
                    "type": "string",
                    "description": "The profession of the role to switch to",
                },
            },
            "required": ["role", "role_name"],
        },
    },
}


def change_role(conn: "ConnectionHandler", role: str, role_name: str):
    """Switch role"""
    if role not in prompts:
        return ActionResponse(
            action=Action.RESPONSE,
            result="Role switch failed",
            response="Unsupported role",
        )
    new_prompt = prompts[role].replace("{{assistant_name}}", role_name)
    conn.change_system_prompt(new_prompt)
    logger.bind(tag=TAG).info(
        f"Preparing to switch role: {role}, role name: {role_name}"
    )
    res = f"Role switched successfully, I am {role} {role_name}"
    return ActionResponse(
        action=Action.RESPONSE, result="Role switch processed", response=res
    )
