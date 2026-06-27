package com.clou.gatewaydemo.result;

import lombok.Data;

import java.io.Serializable;

import static com.clou.common.constant.code.errorCode;
import static com.clou.common.constant.code.susseCode;

/**
 * 后端统一返回结果
 * @author LENOVO
 * @param <T>
 */
@Data
public class Result<T> implements Serializable {



	private Integer code; // 编码：1成功，0和其它数字为失败
	private String msg; // 错误信息
	private T data; // 数据

	public static <T> Result<T> success() {
		Result<T> result = new Result<T>();
		result.code = susseCode;
		return result;
	}

	public static <T> Result<T> success(T object) {
		Result<T> result = new Result<T>();
		result.data = object;
		result.code = susseCode;
		return result;
	}

	public static <T> Result<T> error(String msg) {
		Result<T> result = new Result<T>();
		result.msg = msg;
		result.code = errorCode;
		result.data = null;
		return result;
	}

	// 在Result类中添加
	public Result<T> setCode(Integer code) {
		this.code = code;
		return this;
	}

}
