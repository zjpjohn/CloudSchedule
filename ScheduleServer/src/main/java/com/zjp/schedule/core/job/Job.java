package com.zjp.schedule.core.job;


/**
 * 作业
 * 
 * @author Huiyugeng
 * 
 */
public interface Job {

	/**
	 * 获得作业名称
	 * 
	 * @return 作业名称
	 */
	public String getName();

	/**
	 * 作业执行内容
	 * 
	 * @return 作业是否执行成功 true: 作业执行成功; false: 作业执行失败
	 * 
	 * @throws JobException 作业执行异常
	 */
	public boolean execute() throws JobException;

}
