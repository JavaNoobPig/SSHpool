package com.javanoob.model;

public class OutputdataSecVer {
	private String statusCode;
	private String statusmsg;
	private String responseData;

	public OutputdataSecVer() {
	}

	public OutputdataSecVer(String statusCode, String statusmsg, String responseData) {
		super();
		this.statusCode = statusCode;
		this.statusmsg = statusmsg;
		this.responseData = responseData;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusmsg() {
		return statusmsg;
	}

	public void setStatusmsg(String statusmsg) {
		this.statusmsg = statusmsg;
	}

	public String getResponseData() {
		return responseData;
	}

	public void setResponseData(String responseData) {
		this.responseData = responseData;
	}

	@Override
	public String toString() {
		return "OutputdataSecVer [statusCode=" + statusCode + ", statusmsg=" + statusmsg + ", responseData="
				+ responseData + "]";
	}

}
