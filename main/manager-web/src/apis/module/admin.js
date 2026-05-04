import { getServiceUrl } from "../api";
import RequestService from "../httpRequest";

export default {
	// User list
	getUserList(params, callback) {
		const queryParams = new URLSearchParams({
			page: params.page,
			limit: params.limit,
			mobile: params.mobile,
		}).toString();

		RequestService.sendRequest()
			.url(`${getServiceUrl()}/admin/users?${queryParams}`)
			.method("GET")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail((err) => {
				console.error("Request failed:", err);
				RequestService.reAjaxFun(() => {
					this.getUserList(callback);
				});
			})
			.send();
	},
	// delete user
	deleteUser(id, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/admin/users/${id}`)
			.method("DELETE")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail((err) => {
				console.error("Delete failed:", err);
				RequestService.reAjaxFun(() => {
					this.deleteUser(id, callback);
				});
			})
			.send();
	},
	// reset user password
	resetUserPassword(id, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/admin/users/${id}`)
			.method("PUT")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail((err) => {
				console.error("Reset password failed:", err);
				RequestService.reAjaxFun(() => {
					this.resetUserPassword(id, callback);
				});
			})
			.send();
	},
	// Get parameter list
	getParamsList(params, callback) {
		const queryParams = new URLSearchParams({
			page: params.page,
			limit: params.limit,
			paramCode: params.paramCode || "",
		}).toString();

		RequestService.sendRequest()
			.url(`${getServiceUrl()}/admin/params/page?${queryParams}`)
			.method("GET")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail((err) => {
				console.error("failed to get parameter list:", err);
				RequestService.reAjaxFun(() => {
					this.getParamsList(params, callback);
				});
			})
			.send();
	},
	// Save
	addParam(data, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/admin/params`)
			.method("POST")
			.data(data)
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail((err) => {
				console.error("add parameter failed:", err);
				RequestService.reAjaxFun(() => {
					this.addParam(data, callback);
				});
			})
			.send();
	},
	// modify
	updateParam(data, callback, failCallback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/admin/params`)
			.method("PUT")
			.data(data)
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.fail((err) => {
				RequestService.clearRequestTime();
				failCallback(err);
			})
			.networkFail((err) => {
				console.error("Update parameter failed:", err);
				RequestService.reAjaxFun(() => {
					this.updateParam(data, callback);
				});
			})
			.send();
	},
	// delete
	deleteParam(ids, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/admin/params/delete`)
			.method("POST")
			.data(ids)
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail((err) => {
				console.error("Delete parameters failed:", err);
				RequestService.reAjaxFun(() => {
					this.deleteParam(ids, callback);
				});
			})
			.send();
	},
	// Get ws server list
	getWsServerList(params, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/admin/server/server-list`)
			.method("GET")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail((err) => {
				console.error("Failed to get ws server list:", err);
				RequestService.reAjaxFun(() => {
					this.getWsServerList(params, callback);
				});
			})
			.send();
	},
	// Send ws server action command
	sendWsServerAction(data, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/admin/server/emit-action`)
			.method("POST")
			.data(data)
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail((err) => {
				RequestService.reAjaxFun(() => {
					this.sendWsServerAction(data, callback);
				});
			})
			.send();
	},
};
