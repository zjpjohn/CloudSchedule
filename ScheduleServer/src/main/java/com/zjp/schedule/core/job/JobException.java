package com.zjp.schedule.core.job;

/**
 * 作业执行异常
 * 
 * @author Huiyugeng
 * 
 */
public class JobException extends Exception {

	private static final long serialVersionUID = 1L;


	public JobException() {
		super();
	}


	public JobException(String message) {
		super(message);
	}


	public JobException(Throwable cause) {
		super(cause);
	}


	public JobException(String message, Throwable cause) {
		super(message, cause);
	}

}
