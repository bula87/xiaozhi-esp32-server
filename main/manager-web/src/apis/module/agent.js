import { getServiceUrl } from "../api";
import RequestService from "../httpRequest";

export default {
	// Get agent list
	getAgentList(callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent/list`)
			.method("GET")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail(() => {
				RequestService.reAjaxFun(() => {
					this.getAgentList(callback);
				});
			})
			.send();
	},
	// Add Agent
	addAgent(agentName, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent`)
			.method("POST")
			.data({ agentName: agentName })
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail(() => {
				RequestService.reAjaxFun(() => {
					this.addAgent(agentName, callback);
				});
			})
			.send();
	},
	// Delete agent
	deleteAgent(agentId, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent/${agentId}`)
			.method("DELETE")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail(() => {
				RequestService.reAjaxFun(() => {
					this.deleteAgent(agentId, callback);
				});
			})
			.send();
	},
	// Get agent configuration
	getDeviceConfig(agentId, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent/${agentId}`)
			.method("GET")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail((err) => {
				console.error("Failed to get configuration:", err);
				RequestService.reAjaxFun(() => {
					this.getDeviceConfig(agentId, callback);
				});
			})
			.send();
	},
	// Configure Agent
	updateAgentConfig(agentId, configData, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent/${agentId}`)
			.method("PUT")
			.data(configData)
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail(() => {
				RequestService.reAjaxFun(() => {
					this.updateAgentConfig(agentId, configData, callback);
				});
			})
			.send();
	},
	// New method: Get agent template
	getAgentTemplate(callback) {
		// Remove templateName parameter
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent/template`)
			.method("GET")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail((err) => {
				console.error("Failed to get template:", err);
				RequestService.reAjaxFun(() => {
					this.getAgentTemplate(callback);
				});
			})
			.send();
	},

	// Added: Get Agent Template Pagination List
	getAgentTemplatesPage(params, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent/template/page`)
			.method("GET")
			.data(params)
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail((err) => {
				console.error("Failed to get template pagination list:", err);
				RequestService.reAjaxFun(() => {
					this.getAgentTemplatesPage(params, callback);
				});
			})
			.send();
	},
	// Get intelligent agent conversation list
	getAgentSessions(agentId, params, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent/${agentId}/sessions`)
			.method("GET")
			.data(params)
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail(() => {
				RequestService.reAjaxFun(() => {
					this.getAgentSessions(agentId, params, callback);
				});
			})
			.send();
	},
	// Get Agent Chat History
	getAgentChatHistory(agentId, sessionId, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent/${agentId}/chat-history/${sessionId}`)
			.method("GET")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail(() => {
				RequestService.reAjaxFun(() => {
					this.getAgentChatHistory(agentId, sessionId, callback);
				});
			})
			.send();
	},
	// Get audio download ID
	getAudioId(audioId, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent/audio/${audioId}`)
			.method("POST")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail(() => {
				RequestService.reAjaxFun(() => {
					this.getAudioId(audioId, callback);
				});
			})
			.send();
	},
	// Get the agent's MCP access point address
	getAgentMcpAccessAddress(agentId, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent/mcp/address/${agentId}`)
			.method("GET")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.fail((err) => {
				callback(err);
			})
			.networkFail(() => {
				RequestService.reAjaxFun(() => {
					this.getAgentMcpAccessAddress(agentId, callback);
				});
			})
			.send();
	},
	// Get the agent's MCP tools list
	getAgentMcpToolsList(agentId, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent/mcp/tools/${agentId}`)
			.method("GET")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail(() => {
				RequestService.reAjaxFun(() => {
					this.getAgentMcpToolsList(agentId, callback);
				});
			})
			.send();
	},
	// add agent voice print
	addAgentVoicePrint(voicePrintData, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent/voice-print`)
			.method("POST")
			.data(voicePrintData)
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail(() => {
				RequestService.reAjaxFun(() => {
					this.addAgentVoicePrint(voicePrintData, callback);
				});
			})
			.send();
	},
	// Get the voice print list for the specified agent
	getAgentVoicePrintList(id, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent/voice-print/list/${id}`)
			.method("GET")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail(() => {
				RequestService.reAjaxFun(() => {
					this.getAgentVoicePrintList(id, callback);
				});
			})
			.send();
	},
	// delete agent voiceprint
	deleteAgentVoicePrint(id, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent/voice-print/${id}`)
			.method("DELETE")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail(() => {
				RequestService.reAjaxFun(() => {
					this.deleteAgentVoicePrint(id, callback);
				});
			})
			.send();
	},
	// Update Agent Voice Print
	updateAgentVoicePrint(voicePrintData, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent/voice-print`)
			.method("PUT")
			.data(voicePrintData)
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail(() => {
				RequestService.reAjaxFun(() => {
					this.updateAgentVoicePrint(voicePrintData, callback);
				});
			})
			.send();
	},
	// Get chat history of the specified agent user type
	getRecentlyFiftyByAgentId(id, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent/${id}/chat-history/user`)
			.method("GET")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail(() => {
				RequestService.reAjaxFun(() => {
					this.getRecentlyFiftyByAgentId(id, callback);
				});
			})
			.send();
	},
	// Get chat history of the specified agent user type
	getContentByAudioId(id, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent/${id}/chat-history/audio`)
			.method("GET")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail(() => {
				RequestService.reAjaxFun(() => {
					this.getContentByAudioId(id, callback);
				});
			})
			.send();
	},
	// Add the following method at the end of the file (before the last method, before the curly brace):
	// Add Agent Template
	addAgentTemplate(templateData, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent/template`)
			.method("POST")
			.data(templateData)
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail(() => {
				RequestService.reAjaxFun(() => {
					this.addAgentTemplate(templateData, callback);
				});
			})
			.send();
	},

	// Update Agent Template
	updateAgentTemplate(templateData, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent/template`)
			.method("PUT")
			.data(templateData)
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail(() => {
				RequestService.reAjaxFun(() => {
					this.updateAgentTemplate(templateData, callback);
				});
			})
			.send();
	},

	// Delete Agent Template
	deleteAgentTemplate(id, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent/template/${id}`)
			.method("DELETE")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail(() => {
				RequestService.reAjaxFun(() => {
					this.deleteAgentTemplate(id, callback);
				});
			})
			.send();
	},

	// Batch delete agent templates
	batchDeleteAgentTemplate(ids, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent/template/batch-remove`) // Change to the new URL
			.method("POST")
			.data(Array.isArray(ids) ? ids : [ids]) // Ensure it is an array format
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail(() => {
				RequestService.reAjaxFun(() => {
					this.batchDeleteAgentTemplate(ids, callback);
				});
			})
			.send();
	},
	// Add the method to get a single template after the getAgentTemplate method
	getAgentTemplateById(templateId, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent/template/${templateId}`)
			.method("GET")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail((err) => {
				console.error("Failed to get single template:", err);
				RequestService.reAjaxFun(() => {
					this.getAgentTemplateById(templateId, callback);
				});
			})
			.send();
	},

	// Get chat history download link UUID
	getDownloadUrl(agentId, sessionId, callback) {
		RequestService.sendRequest()
			.url(
				`${getServiceUrl()}/agent/chat-history/getDownloadUrl/${agentId}/${sessionId}`,
			)
			.method("POST")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail(() => {
				RequestService.reAjaxFun(() => {
					this.getDownloadUrl(agentId, sessionId, callback);
				});
			})
			.send();
	},

	// Search Agent
	searchAgent(keyword, searchType, callback) {
		RequestService.sendRequest()
			.url(
				`${getServiceUrl()}/agent/list?keyword=${encodeURIComponent(keyword)}&searchType=${searchType}`,
			)
			.method("GET")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail(() => {
				RequestService.reAjaxFun(() => {
					this.searchAgent(keyword, searchType, callback);
				});
			})
			.send();
	},
	// Get agent tags
	getAgentTags(agentId, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent/${agentId}/tags`)
			.method("GET")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail(() => {
				RequestService.reAjaxFun(() => {
					this.getAgentTags(agentId, callback);
				});
			})
			.send();
	},
	// Save agent tags
	saveAgentTags(agentId, tags, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/agent/${agentId}/tags`)
			.method("PUT")
			.data(tags)
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail(() => {
				RequestService.reAjaxFun(() => {
					this.saveAgentTags(agentId, tags, callback);
				});
            })
			.send();
    },
}
