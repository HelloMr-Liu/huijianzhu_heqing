package com.huijianzhu.heqing.exception;

import com.huijianzhu.heqing.enums.SYSTEM_RESULT_STATE;
import com.huijianzhu.heqing.vo.SystemResult;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;


@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * -----------------------------------------------------------------
     * 功能：处理全局异常
     * <p>
     * 返回：SystemResult 异常结果信息
     * -------------------------------------------------------------------
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public SystemResult errorHandler(Exception e) {
        // 打印栈堆信息
        e.printStackTrace();
        return SystemResult.build(SYSTEM_RESULT_STATE.ERROR.KEY, SYSTEM_RESULT_STATE.ERROR.VALUE);
    }

    /**
     * -----------------------------------------------------------------
     * 功能：处理数据校验异常
     * <p>
     * 返回：SystemResult 异常结果信息
     * -------------------------------------------------------------------
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public SystemResult dataException(BindException bind) {
        // 返回错误信息
        return SystemResult.build(SYSTEM_RESULT_STATE.VALIED_ERROR.KEY, bind.getFieldError().getDefaultMessage());
    }

    /**
     * -----------------------------------------------------------------
     * 功能：处理加了@RequestBody数据校验异常
     * <p>
     * 返回：SystemResult 异常结果信息
     * -------------------------------------------------------------------
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public SystemResult MethodArgumentNotValidException(MethodArgumentNotValidException dataValid) {
        // 返回错误信息
        return SystemResult.build(SYSTEM_RESULT_STATE.VALIED_ERROR.KEY, dataValid.getBindingResult().getFieldError().getDefaultMessage());
    }

    /**
     * 处理请求单个参数不满足校验规则的异常信息
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseBody
    public SystemResult constraintViolationExceptionHandler(HttpServletRequest request,
                                                            ConstraintViolationException exception) {
        // 执行校验，获得校验结果
        Set<ConstraintViolation<?>> validResult = exception.getConstraintViolations();
        // 返回错误信息
        return SystemResult.build(SYSTEM_RESULT_STATE.VALIED_ERROR.KEY, validResult.iterator().next().getMessage());
    }
}
