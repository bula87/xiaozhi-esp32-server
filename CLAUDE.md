# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Running the Server
- **Development mode**: `cd main/xiaozhi-server && python app.py`
- **Docker deployment**: `docker-compose up -d` (refer to docker-setup.sh for setup)
- **Build Docker images**: 
  - Server: `docker build -f Dockerfile-server -t xiaozhi-server .`
  - Web: `docker build -f Dockerfile-web -t xiaozhi-web .`

### Testing
- **Performance testing**: `python main/xiaozhi-server/performance_tester.py`
- **Audio interaction test**: Open `main/xiaozhi-server/test/test_page.html` in Google Chrome
- **Individual module testing**: Use specific testers in `main/xiaozhi-server/performance_tester/`:
  - ASR: `python performance_tester_asr.py`
  - TTS: `python performance_tester_tts.py`
  - LLM: `python performance_tester_llm.py`
  - VLLM: `python performance_tester_vllm.py`

### Project Structure
```
main/
├── xiaozhi-server/          # Main backend server (Python)
│   ├── app.py               # Entry point
│   ├── config/              # Configuration management
│   ├── core/                # Core functionality (WebSocket, HTTP, auth, connection)
│   ├── utils/               # Utility functions (audio, cache, etc.)
│   └── plugins_func/        # Plugin system
├── manager-web/             # Frontend management interface (Vue.js)
├── manager-mobile/          # Mobile management interface
└── manager-api/             # API service
```

## Architecture Overview

### Core Components
1. **WebSocket Server** (`core/websocket_server.py`): Handles real-time communication with ESP32 devices
2. **HTTP Server** (`core/http_server.py`): Provides OTA updates and vision analysis endpoints
3. **Authentication System** (`core/auth.py` and `core/utils/auth.py`): JWT-based auth for APIs and WebSocket
4. **Configuration System** (`config/`): YAML-based config with API fallback support
5. **Plugin System** (`plugins_func/`): Extensible function calling framework

### Key Features
- **Multi-protocol support**: MQTT+UDP gateway, WebSocket, HTTP
- **AI Integration**: ASR, LLM, VLLM, TTS, Voiceprint recognition
- **Memory Systems**: Local short-term, mem0ai, PowerMem
- **Knowledge Base**: RAGFlow integration
- **Tool Calling**: MCP protocol support, custom tool functions
- **Management Console**: Web interface for user/device/configuration management

### Data Flow
1. ESP32 devices connect via WebSocket to `xiaozhi-server`
2. Audio input processed through ASR → Intent Recognition → LLM → TTS
3. Visual input processed through VLLM → LLM
4. Configuration managed through manager-web interface
5. Plugins extend functionality via `plugins_func/` directory

### Configuration
- Primary config: `data/.config.yaml` (or `config.yaml` in root)
- API-driven config: Enable `read_config_from_api: true` for dynamic configuration
- Environment-specific configs: `.env` files in manager directories

## Important Notes
- FFmpeg must be installed for audio processing (`check_ffmpeg_installed()` in app.py)
- Authentication keys can be auto-generated or set via `server.auth_key` or `manager-api.secret`
- MCP endpoints require validation and proper formatting (`/mcp/` → `/call/` conversion)
- Garbage collection runs automatically every 5 minutes via `gc_manager`