package com.food.ordering.ssn.utils;

public class Response<T>{
	private Integer code;
	private String message;
	private T data;

	public Response() {
		code = Constant.CodeFailure;
		message = Constant.MessageFailure;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Response{" +
				"code=" + code +
				", message='" + message + '\'' +
				", data=" + data +
				'}';
	}
}
