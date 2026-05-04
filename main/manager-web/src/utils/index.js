import { Message } from "element-ui";
import router from "../router";
import Constant from "../utils/constant";

/**
 * Check if the user is logged in
 */
export function checkUserLogin(fn) {
	let token = localStorage.getItem(Constant.STORAGE_KEY.TOKEN);
	let userType = localStorage.getItem(Constant.STORAGE_KEY.USER_TYPE);
	if (isNull(token) || isNull(userType)) {
		goToPage("console", true);
		return;
	}
	if (fn) {
		fn();
	}
}

/**
 * Check if empty
 * @param data
 * @returns {boolean}
 */
export function isNull(data) {
	if (data === undefined || data === null) {
		return true;
	}
	if (
		typeof data === "string" &&
		(data.length === 0 ||
			data === "" ||
			data === "undefined" ||
			data === "null")
	) {
		return true;
	}
	if (data instanceof Array && data.length === 0) {
		return true;
	}
	return false;
}

/**
 * Check if not empty
 * @param data
 * @returns {boolean}
 */
export function isNotNull(data) {
	return !isNull(data);
}

/**
 * Display top red notification
 * @param msg
 */
export function showDanger(msg) {
	if (isNull(msg)) {
		return;
	}
	Message({
		message: msg,
		type: "error",
		showClose: true,
	});
}

/**
 * Show top orange notification
 * @param msg
 */
export function showWarning(msg) {
	if (isNull(msg)) {
		return;
	}
	Message({
		message: msg,
		type: "warning",
		showClose: true,
	});
}

/**
 * Show top green notification
 * @param msg
 */
export function showSuccess(msg) {
	Message({
		message: msg,
		type: "success",
		showClose: true,
	});
}

/**
 * Page Navigation
 * @param path
 * @param isRepalce
 */
export function goToPage(path, isRepalce) {
	if (isRepalce) {
		router.replace(path);
	} else {
		router.push(path);
	}
}

/**
 * Get current vue page name
 * @param path
 * @param isRepalce
 */
export function getCurrentPage() {
	let hash = location.hash.replace("#", "");
	if (hash.indexOf("?") > 0) {
		hash = hash.substring(0, hash.indexOf("?"));
	}
	return hash;
}

/**
 * Generate a random number from [min,max]
 * @param min
 * @param max
 * @returns {number}
 */
export function randomNum(min, max) {
	return Math.round(Math.random() * (max - min) + min);
}

/**
 * Get uuid
 */
export function getUUID() {
	return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, (c) => {
		return (c === "x" ? (Math.random() * 16) | 0 : "r&0x3" | "0x8").toString(
			16,
		);
	});
}

/**
 * Validate phone number format
 * @param {string} mobile Phone Number
 * @param {string} areaCode Area Code
 * @returns {boolean}
 */
export function validateMobile(mobile, areaCode) {
	// Remove all non-digit characters
	const cleanMobile = mobile.replace(/\D/g, "");

	// Use different validation rules based on the area code
	switch (areaCode) {
		case "+86": // Mainland China
			return /^1[3-9]\d{9}$/.test(cleanMobile);
		case "+852": // China Hong Kong
			return /^[569]\d{7}$/.test(cleanMobile);
		case "+853": // China Macau
			return /^6\d{7}$/.test(cleanMobile);
		case "+886": // China Taiwan
			return /^9\d{8}$/.test(cleanMobile);
		case "+1": // US/Canada
			return /^[2-9]\d{9}$/.test(cleanMobile);
		case "+44": // United Kingdom
			return /^7[1-9]\d{8}$/.test(cleanMobile);
		case "+81": // Japan
			return /^[7890]\d{8}$/.test(cleanMobile);
		case "+82": // South Korea
			return /^1[0-9]\d{7}$/.test(cleanMobile);
		case "+65": // Singapore
			return /^[89]\d{7}$/.test(cleanMobile);
		case "+61": // Australia
			return /^[4578]\d{8}$/.test(cleanMobile);
		case "+49": // Germany
			return /^1[5-7]\d{8}$/.test(cleanMobile);
		case "+33": // France
			return /^[67]\d{8}$/.test(cleanMobile);
		case "+39": // Italy
			return /^3[0-9]\d{8}$/.test(cleanMobile);
		case "+34": // Spain
			return /^[6-9]\d{8}$/.test(cleanMobile);
		case "+55": // Brazil
			return /^[1-9]\d{10}$/.test(cleanMobile);
		case "+91": // India
			return /^[6-9]\d{9}$/.test(cleanMobile);
		case "+971": // UAE
			return /^[5]\d{8}$/.test(cleanMobile);
		case "+966": // Saudi Arabia
			return /^[5]\d{8}$/.test(cleanMobile);
		case "+880": // Bangladesh
			return /^1[3-9]\d{8}$/.test(cleanMobile);
		case "+234": // Nigeria
			return /^[789]\d{9}$/.test(cleanMobile);
		case "+254": // Kenya
			return /^[17]\d{8}$/.test(cleanMobile);
		case "+255": // Tanzania
			return /^[67]\d{8}$/.test(cleanMobile);
		case "+7": // Kazakhstan
			return /^[67]\d{9}$/.test(cleanMobile);
		default:
			// Other international numbers: at least 5 digits, at most 15 digits
			return /^\d{5,15}$/.test(cleanMobile);
	}
}

/**
 * Generate SM2 keypair (hex format)
 * @returns {Object} Object containing public and private keys
 */
export function generateSm2KeyPairHex() {
	// Use sm-crypto library to generate SM2 keypair
	const sm2 = require("sm-crypto").sm2;
	const keypair = sm2.generateKeyPairHex();

	return {
		publicKey: keypair.publicKey,
		privateKey: keypair.privateKey,
		clientPublicKey: keypair.publicKey, // Client public key
		clientPrivateKey: keypair.privateKey, // Client private key
	};
}

/**
 * SM2 public key encryption
 * @param {string} publicKey publicKey (hexadecimal format)
 * @param {string} plainText plaintext
 * @returns {string} encrypted ciphertext (hexadecimal format)
 */
export function sm2Encrypt(publicKey, plainText) {
	if (!publicKey) {
		throw new Error("Public key cannot be null or undefined");
	}

	if (!plainText) {
		throw new Error("Plaintext cannot be empty");
	}

	const sm2 = require("sm-crypto").sm2;
	// SM2 encryption, add 04 prefix to indicate uncompressed public key
	const encrypted = sm2.doEncrypt(plainText, publicKey, 1);
	// Convert to hexadecimal format (consistent with the backend, add 04 prefix)
	const result = "04" + encrypted;

	return result;
}

/**
 * SM2 private key decryption
 * @param {string} privateKey privateKey (hexadecimal format)
 * @param {string} cipherText cipherText (hexadecimal format)
 * @returns {string} decrypted plaintext
 */
export function sm2Decrypt(privateKey, cipherText) {
	const sm2 = require("sm-crypto").sm2;
	// remove 04 prefix (consistent with the backend)
	const dataWithoutPrefix = cipherText.startsWith("04")
		? cipherText.substring(2)
		: cipherText;
	// SM2 decryption
	return sm2.doDecrypt(dataWithoutPrefix, privateKey, 1);
}

/**
 * Debounce function
 * @param {Function} fn The function to debounce
 * @param {number} delay Delay time (milliseconds), default 500ms
 * @param {boolean} immediate Whether to execute immediately, default false
 * @returns {Function} The debounced function
 */
export function debounce(fn, delay = 500, immediate = false) {
	let timer = null;

	return function (...args) {
		const context = this;

		if (timer) {
			clearTimeout(timer);
		}

		if (immediate && !timer) {
			fn.apply(context, args);
		}

		timer = setTimeout(() => {
			if (!immediate) {
				fn.apply(context, args);
			}
			timer = null;
		}, delay);
	};
}
