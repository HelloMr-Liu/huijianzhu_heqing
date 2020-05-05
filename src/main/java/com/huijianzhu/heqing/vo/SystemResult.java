package com.huijianzhu.heqing.vo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huijianzhu.heqing.enums.SYSTEM_RESULT_STATE;

import java.io.Serializable;
import java.util.List;

/*================================================================
说明：系统全局响应结果

作者          时间            注释
刘梓江		2020-3-31       创建
==================================================================*/
public class SystemResult implements Serializable {

	// 定义jackson对象
	private static final ObjectMapper MAPPER = new ObjectMapper();

	// 响应业务状态
	// 200 为操作成功
	// 100 为数据校验异常
	// 500 为程序内部错误
	private Integer status;

	// 响应消息
	// 默认为操作成功
	private String msg;

	// 响应中的数据
	private Object data;
	
	/**=================================================================
     * 功能：构建响应结果
     * 
     * 参数：status			Integer		状态码
     *		msg				String		响应消息
     *		data			Object		响应数据
     *
     * 返回：SystemResult		业务操作响应结果
     ===================================================================*/
	public static SystemResult build(Integer status, String msg, Object data) {
		return new SystemResult(status, msg, data);
	}
	
	/**=================================================================
     * 功能：返回成功信息，不带消息，带数据
     * 
     * 参数：data			Object		响应数据
     * 
     * 返回：SystemResult		业务操作响应结果
     ===================================================================*/
	public static SystemResult ok(Object data) {
		return new SystemResult(data);
	}
	
	/**=================================================================
     * 功能：返回成功信息，不带消息、数据
     * 
     * 返回：SystemResult		业务操作响应结果
     ===================================================================*/
	public static SystemResult ok() {
		return new SystemResult(null);
	}

	public SystemResult() {

	}
	
	/**=================================================================
     * 功能：构建响应结果，不带数据
     * 
     * 参数：status			Integer		状态码
     *		msg				String		响应消息
     *
     * 返回：SystemResult		业务操作响应结果
     ===================================================================*/
	public static SystemResult build(Integer status, String msg) {
		return new SystemResult(status, msg, null);
	}

	public SystemResult(Integer status, String msg, Object data) {
		this.status = status;
		this.msg = msg;
		this.data = data;
	}

	public SystemResult(Object data) {
		this.status = SYSTEM_RESULT_STATE.SUCCESS.KEY;
		this.msg = SYSTEM_RESULT_STATE.SUCCESS.VALUE;
		this.data = data;
	}

	public Integer getStatus() {return status;}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}


}
