package com.zjp.schedule.core.listener;

/**
 * 作业监听器
 * 
 * @author Huiyugeng
 * 
 */
public interface JobListener {

	/**
	 * 作业执行前
	 */
	public void beforeJobExecute();

	/**
	 * 作业执行后
	 */
	public void afterJobExecute();
	
	/**
	 * 当作业执行返回false
	 */
	public void executeFail();
	
	/**
	 * 当作业执行抛出异常
	 */
	public void executeException();
	

}
