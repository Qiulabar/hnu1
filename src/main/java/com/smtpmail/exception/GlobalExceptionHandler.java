package com.smtpmail.exception;

import com.smtpmail.common.Result;

import java.util.HashMap;
import java.util.Map;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.smtpmail.controller")
public class GlobalExceptionHandler {

	/**
	 * JSR303校验异常处理器
	 */
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public Result handleException(MethodArgumentNotValidException e) {
		BindingResult bindingResult = e.getBindingResult();
		Map<String,String> map = new HashMap<>();
		bindingResult.getFieldErrors().forEach(fieldError->{
			map.put(fieldError.getField(),fieldError.getDefaultMessage());
		});
		return Result.error().data("resultMap",map);
	}

	/**
	 * UnLoginException异常处理器
	 */
	@ExceptionHandler(value = UnLoginException.class)
	public Result handleException(UnLoginException e) {
		e.printStackTrace();
		return Result.error().code(400).message(e.getMessage());
	}

	/**
	 * 全局异常处理器
	 */
	@ExceptionHandler(value = Exception.class)
	public Result handleException(Exception e) {
		e.printStackTrace();
		return Result.error().message(e.getMessage());
	}
}
