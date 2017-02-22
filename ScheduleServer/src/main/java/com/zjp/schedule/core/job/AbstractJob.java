package com.zjp.schedule.core.job;

/**
 * 作业抽象类
 * 
 * @author Huiyugeng
 * 
 */
public abstract class AbstractJob implements Job {
	/**
	 * 作业序列号, 用于全局唯一识别, 任务序列号=任务名称-当前时间戳
	 */
	private String serial;

	/**
	 * 作业名称
	 */
	private String name;


	public AbstractJob() {
		setName(this.toString());
	}


	public void setName(String name) {
		this.name = name;

		this.serial = name + "-" + Long.toString(System.currentTimeMillis());
	}

	public String getName() {
		return name;
	}

	public String getSerial() {
		return serial;
	}

	public String toString() {
		return name;
	}

	public abstract boolean execute() throws JobException;

}
