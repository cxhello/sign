package com.cxhello.sign.util;

import com.cxhello.sign.enums.StatusCodeEnum;

/**
 * @author cxhello
 * @date 2021/7/29
 */
public class ResultUtils {

    public static Result success() {
        return new Result(StatusCodeEnum.SUCCESS.getCode(), StatusCodeEnum.SUCCESS.getMsg());
    }

    public static Result fail(String msg) {
        return new Result(StatusCodeEnum.FAIL.getCode(), msg);
    }

    public static Result result(Integer code, String msg) {
        return new Result(code, msg);
    }

    public static <T> Result<T> result(T data){
        return new Result<T>(StatusCodeEnum.SUCCESS.getCode(), StatusCodeEnum.SUCCESS.getMsg(), data);
    }

}
