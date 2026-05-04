from ..base import MemoryProviderBase, logger
import time
import json
import os
import yaml


from config.config_loader import get_project_dir
from config.manage_api_client import generate_and_save_chat_summary
from core.utils.util import check_model_key


def extract_json_data(data: str) -> str:
    """Extracts the JSON string from the raw result."""
    return data.strip()


short_term_memory_prompt = """
# Spacetime Memory Weaver

## Core Mission
Build a growing dynamic memory network, retain key information within limited space while intelligently maintaining the evolution trajectory of information.
Based on dialogue records, summarize important information of the user, in order to provide more personalized service in future conversations.

## Memory Rules
### 1. Three-dimensional memory assessment (must be executed every update)
| Dimension | Assessment Criteria | Weight Score |
|------------|--------------------|--------------|
| Timeliness | Information Freshness (by dialogue turn) | 40%    |
| Emotional Intensity | Count of 💖 tags/repeated mentions | 35%    |
| Association Density | Number of connections with other information | 25%    |
### 2. Dynamic Update Mechanism
**Name Change Example:**
Original Memory: "Former Name": ["Zhang San"], "Current Name": "Zhang Sanfeng"
Trigger Condition: When naming signals such as "I am X" or "Address me Y" are detected
Operation Flow:
1. Move the old name to the "Former Names" list
2. Record naming timeline: "2024-02-15 14:32: Enabled Zhang Sanfeng"
3. Add to memory cube: "Identity transformation from Zhang San to Zhang Sanfeng"
### 3. Space Optimization Strategy
- **Information Compression Technique**: Use symbol system to increase density
  - ✅"Zhang Sanfeng[North/Software Eng/🐱]"
  - ❌"Beijing software engineer, cat owner"
- **Elimination Warning**: Trigger when total word count ≥900
  1. Delete information with weight <60 and not mentioned in 3 rounds
  2. Merge similar entries (keep the most recent timestamp)

## Memory Structure
The output format must be a parsable json string, no need for explanations, comments, and descriptions. When saving memory, only extract information from the dialogue, do not mix in example content.
{
  "Spacetime Archive": {
    "Identity Map": {
      "Current Name": "",
      "Feature Tags": []
    },
    "Memory Cubes": [
      {
        "Event": "Joined New Company",
        "Timestamp": "2024-03-20",
        "Emotional Value": 0.9,
        "Associations": ["Afternoon Tea"],
        "Shelf Life": 30
      }
    ]
  },
  "Relationship Network": {
    "High-frequency Topics": {"Workplace": 12},
    "Hidden Connections": [""]
  },
  "Pending Response": {
    "Urgent Matters": ["Tasks requiring immediate handling"],
    "Potential Concern": ["Help that can be proactively provided"]
  },
  "Highlighting Quotes": [
    "The moment that moved people, strong emotional expression, the user's original words"
  ]
}
"""


TAG = __name__


class MemoryProvider(MemoryProviderBase):
    def __init__(self, config, summary_memory):
        super().__init__(config)
        self.short_memory = ""
        self.save_to_file = True
        self.memory_path = get_project_dir() + "data/.memory.yaml"
        self.load_memory(summary_memory)

    def init_memory(
        self, role_id, llm, summary_memory=None, save_to_file=True, **kwargs
    ):
        super().init_memory(role_id, llm, **kwargs)
        self.save_to_file = save_to_file
        self.load_memory(summary_memory)

    def load_memory(self, summary_memory):
        # return directly after obtaining the summary memory
        if summary_memory or not self.save_to_file:
            self.short_memory = summary_memory
            return

        all_memory = {}
        if os.path.exists(self.memory_path):
            with open(self.memory_path, "r", encoding="utf-8") as f:
                all_memory = yaml.safe_load(f) or {}
        if self.role_id in all_memory:
            self.short_memory = all_memory[self.role_id]

    def save_memory_to_file(self):
        all_memory = {}
        if os.path.exists(self.memory_path):
            with open(self.memory_path, "r", encoding="utf-8") as f:
                all_memory = yaml.safe_load(f) or {}
        all_memory[self.role_id] = self.short_memory
        with open(self.memory_path, "w", encoding="utf-8") as f:
            yaml.dump(all_memory, f, allow_unicode=True)

    async def save_memory(self, msgs, session_id=None):
        # Print the used model information
        model_info = getattr(self.llm, "model_name", str(self.llm.__class__.__name__))
        logger.bind(tag=TAG).debug(f"Save model using memory: {model_info}")
        api_key = getattr(self.llm, "api_key", None)
        memory_key_msg = check_model_key("Memory Summary Dedicated LLM", api_key)
        if memory_key_msg:
            logger.bind(tag=TAG).error(memory_key_msg)
        if self.llm is None:
            logger.bind(tag=TAG).error("LLM is not set for memory provider")
            return None

        if len(msgs) < 2:
            return None

        msgStr = ""
        for msg in msgs:
            content = msg.content

            # Extract content from JSON format if present (for ASR with emotion/language tags)
            try:
                if (
                    content
                    and content.strip().startswith("{")
                    and content.strip().endswith("}")
                ):
                    data = json.loads(content)
                    if "content" in data:
                        content = data["content"]
            except (json.JSONDecodeError, KeyError, TypeError):
                # If parsing fails, use original content
                pass

            if msg.role == "user":
                msgStr += f"User: {content}\n"
            elif msg.role == "assistant":
                msgStr += f"Assistant: {content}\n"

        if self.short_memory and len(self.short_memory) > 0:
            msgStr += "Historical memory:\n"
            msgStr += self.short_memory

        # Current time
        time_str = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
        msgStr += f"Current time:{time_str}"

        if self.save_to_file:
            try:
                result = self.llm.response_no_stream(
                    short_term_memory_prompt,
                    msgStr,
                    max_tokens=2000,
                    temperature=0.2,
                )
                json_str = extract_json_data(result)
                json.loads(json_str)  # Check if json format is correct
                self.short_memory = json_str
                self.save_memory_to_file()
            except Exception as e:
                logger.bind(tag=TAG).error(f"Error in saving memory: {e}")
        else:
            # When save_to_file is False, call the Java-side chat history summary interface
            summary_id = session_id if session_id else self.role_id
            await generate_and_save_chat_summary(summary_id)

        logger.bind(tag=TAG).info(
            f"Save memory successful - Role: {self.role_id}, Session: {session_id}"
        )

        return self.short_memory


async def query_memory(self, query: str) -> str:
    return self.short_memory
