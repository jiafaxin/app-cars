package com.autohome.app.cars.provider.config;

import com.autohome.autolog4j.common.JacksonUtil;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.env.Environment;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;



@RestController
@ControllerAdvice
public class GlobalErrorHandlerController implements ErrorController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(Exception.class)
    public Object globalError(Exception ex, HttpServletRequest request) {
        this.output(ex,request);

        ReturnErrorValue returnValue = this.getGeneralExReturnValue(ex);
        if (returnValue != null) {
            return returnValue;
        }

        return ReturnErrorValue.buildErrorResult(BusinessErrorEnum.UNKNOWN_ERR);
    }

    private void output(Exception ex, HttpServletRequest request){
        try {

            logger.warn("globalErrorHandler,path:{},param:{},reqType:{},ex:{}",request.getRequestURI(),
                    request.getQueryString(),request.getMethod(),exception2String(ex));
        } catch (Exception e){}
    }

    private ReturnErrorValue getGeneralExReturnValue(Throwable ex) {
        ReturnErrorValue returnValue = null;
        if (ex instanceof MissingServletRequestParameterException) {
            MissingServletRequestParameterException missingServletRequestParameterException =
                    (MissingServletRequestParameterException) ex;
            returnValue = ReturnErrorValue.buildErrorResult(BusinessErrorEnum.PARAM_ERROR.getId(),
                    String.format("缺少入参%s", missingServletRequestParameterException.getParameterName()));
        } else if (ex instanceof MethodArgumentTypeMismatchException) {
            MethodArgumentTypeMismatchException methodArgumentTypeMismatchException =
                    (MethodArgumentTypeMismatchException) ex;
            returnValue = ReturnErrorValue.buildErrorResult(BusinessErrorEnum.PARAM_ERROR.getId(),
                    String.format("入参%s的值类型错误", methodArgumentTypeMismatchException.getName()));
        } else if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) ex;
            BindingResult exceptions = methodArgumentNotValidException.getBindingResult();
            if (exceptions.hasErrors()) {
                List<ObjectError> errors = exceptions.getAllErrors();
                if (!errors.isEmpty()) {
                    FieldError fieldError = (FieldError) errors.get(0);
                    returnValue = ReturnErrorValue.buildErrorResult(BusinessErrorEnum.PARAM_ERROR.getId(),
                            fieldError.getDefaultMessage());
                }
            }
        }
        return returnValue;
    }


    public static String exception2String(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String errorMsg = stringWriter.toString();
        return errorMsg;
    }

    @Data
    public static class ReturnErrorValue {

        /**
         * 通用响应码, 0-代表成功  非0代表异常
         */
        private int returncode;

        /**
         * 消息,返回错误原因信息
         */
        private String message;

        public static ReturnErrorValue buildErrorResult(int returnCode, String msg) {
            ReturnErrorValue rt = new ReturnErrorValue();
            rt.setReturncode(returnCode);
            rt.setMessage(msg);
            return rt;
        }
        public static ReturnErrorValue buildErrorResult(BusinessErrorEnum errorEnum) {
            ReturnErrorValue rt = new ReturnErrorValue();
            rt.setReturncode(errorEnum.getId());
            rt.setMessage(errorEnum.getDesc());
            return rt;
        }
    }

    public enum BusinessErrorEnum {
        OK(0, "成功"),
        DISABLED(105, "_appid已停用~！"),
        SIGN_ERROR(107, "验签未通过"),
        PARAM_ERROR(400, "参数异常"),
        INVALID_LOGIN_TOKEN(401, "登录失效，请重新登录。"),
        LOGIN_TOKEN_NOT_EXISTS(402, "未登录"),
        FORBIDDEN(403, "请求被拒绝"),
        LOGIN_TOKEN_CHECK_TIMEOUT(405, "未登录"),
        TOO_MANY_REQUESTS(429, "访问过于频繁"),
        UNKNOWN_ERR(500, "服务器繁忙，请稍后再试"),
        COMMON_TP_API_REQUEST_ERROR(3001, "接口调用失败"),
        FREQUENTLY(1003, "操作过于频繁")
        ;

        private int id;
        private String desc;

        BusinessErrorEnum(int id, String desc) {
            this.id = id;
            this.desc = desc;
        }

        public int getId() {
            return id;
        }

        public String getDesc() {
            return desc;
        }
    }

}