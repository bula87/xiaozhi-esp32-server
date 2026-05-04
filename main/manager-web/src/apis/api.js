import admin from "./module/admin.js";
import agent from "./module/agent.js";
import device from "./module/device.js";
import dict from "./module/dict.js";
import model from "./module/model.js";
import ota from "./module/ota.js";
import timbre from "./module/timbre.js";
import user from "./module/user.js";
import voiceClone from "./module/voiceClone.js";
import voiceResource from "./module/voiceResource.js";
import knowledgeBase from "./module/knowledgeBase.js";
import correctWord from "./module/correctWord.js";

/**
 * API Address
 * Automatically reads .env.development file during development
 * Automatically reads .env.production file during compilation
 */
const DEV_API_SERVICE = process.env.VUE_APP_API_BASE_URL;

/**
 * Returns API URL based on the development environment
 * @returns {string}
 */
export function getServiceUrl() {
	return DEV_API_SERVICE;
}

/** request service encapsulation */
export default {
	getServiceUrl,
	user,
	admin,
	agent,
	device,
	model,
	timbre,
	ota,
	dict,
	voiceResource,
	voiceClone,
	knowledgeBase,
	correctWord,
};
