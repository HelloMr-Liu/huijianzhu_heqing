package com.huijianzhu.heqing.vo;


import com.huijianzhu.heqing.enums.SYSTEM_RESULT_STATE;

import java.io.Serializable;

/*================================================================
说明：系统全局响应结果

作者          时间            注释
刘梓江		2020-3-31       创建
==================================================================*/
public class SystemResult implements Serializable {


	// 响应业务状态
	// 200 为操作成功
	// 100 为数据校验异常
	// 500 为程序内部错误
	private Integer code;

	// 响应中的数据
	private Object result;
	
	/**=================================================================
     * 功能：构建响应结果
     * 
     * 参数：code			Integer		状态码
     *		msg				String		响应消息
     *		result			Object		响应数据
     *
     * 返回：SystemResult		业务操作响应结果
     ===================================================================*/
	public static SystemResult build(Integer code, Object result) {
		return new SystemResult(code,result);
	}
	
	/**=================================================================
     * 功能：返回成功信息，不带消息，带数据
     * 
     * 参数：result			Object		响应数据
     * 
     * 返回：SystemResult		业务操作响应结果
     ===================================================================*/
	public static SystemResult ok(Object result) {
		return new SystemResult(result);
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

	public SystemResult(Integer code, Object result) {
		this.code = code;
		this.result = result;
	}

	public SystemResult(Object result) {
		this.code = SYSTEM_RESULT_STATE.SUCCESS.KEY;
		this.result = result;
	}

	public Integer getCode() {return code;}
	public void setCode(Integer code) {
		this.code = code;
	}

	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}


}
