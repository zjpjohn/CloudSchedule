package com.zjp.schedule.core.listener;

/**
 * 任务监听器
 * 
 * @author Huiyugeng
 *
 */
public interface TaskListener {
	/**
	 * Task执行start后执行
	 */
	public void start();

	/**
	 * Task执行stop后执行
	 */
	public void finish();
}
