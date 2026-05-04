import { getServiceUrl } from "../api";
import RequestService from "../httpRequest";

export default {
	// Get replacement word file list
	getFileList(params, callback) {
		const queryParams = new URLSearchParams({
			page: params.page,
			pageSize: params.pageSize,
		}).toString();

		RequestService.sendRequest()
			.url(`${getServiceUrl()}/correct-word/file/list?${queryParams}`)
			.method("GET")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail((err) => {
				console.error("Failed to get replacement word file list:", err);
				RequestService.reAjaxFun(() => {
					this.getFileList(params, callback);
				});
			})
			.send();
	},

	// Get all replacement word files (no pagination)
	selectAll(callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/correct-word/file/select`)
			.method("GET")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail((err) => {
				console.error("Failed to get all replacement word files:", err);
				RequestService.reAjaxFun(() => {
					this.selectAll(callback);
				});
			})
			.send();
	},

	// Download replacement word files
	downloadFile(id, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/correct-word/file/download/${id}`)
			.method("GET")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.fail((err) => {
				RequestService.clearRequestTime();
				callback(err);
			})
			.send();
	},

	// Add replacement word file
	addFile(data, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/correct-word/file`)
			.method("POST")
			.data(data)
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.fail((err) => {
				RequestService.clearRequestTime();
				callback(err);
			})
			.send();
	},

	// Update replacement word file
	updateFile(data, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/correct-word/file/${data.id}`)
			.method("PUT")
			.data({
				fileName: data.fileName,
				content: data.content,
			})
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.fail((err) => {
				RequestService.clearRequestTime();
				callback(err);
			})
			.send();
	},

	// Delete replacement word file
	deleteFile(id, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/correct-word/file/${id}`)
			.method("DELETE")
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail((err) => {
				console.error("Delete replacement word file failed:", err);
				RequestService.reAjaxFun(() => {
					this.deleteFile(id, callback);
				});
			})
			.send();
	},

	// Batch delete replacement word files
	batchDeleteFile(ids, callback) {
		RequestService.sendRequest()
			.url(`${getServiceUrl()}/correct-word/file/batch-delete`)
			.method("POST")
			.data(ids)
			.success((res) => {
				RequestService.clearRequestTime();
				callback(res);
			})
			.networkFail((err) => {
				console.error("Batch delete replacement word file failed:", err);
				RequestService.reAjaxFun(() => {
					this.batchDeleteFile(ids, callback);
				});
			})
			.send();
	},
};
