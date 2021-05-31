package com.qiusheng.agent.result;

import com.fasterxml.jackson.annotation.*;
import lombok.Builder;

import java.io.Serializable;
import java.util.Date;

/**
 * JSON返回结果。
 *
 * @author haitao.han
 * @since 11 六月 2019
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@JsonTypeName(value = "Result,http://www.dv.com")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "$type")
public class Result<T> implements Serializable {

    /**
     * 状态码（可选）
     */
    private int status;

    /**
     * 提示消息
     */
    private String msg;

    /**
     * 响应时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date requesttime;

    /**
     * 响应数据
     */
    private T data;

    //@JsonCreator
    protected Result(@JsonProperty("status") int status, @JsonProperty("msg") String msg,
                     @JsonProperty("data") T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    @JsonCreator
    protected Result(@JsonProperty(CodeConstant.RESULT_STATUS) int status, @JsonProperty("msg") String msg,
                     @JsonProperty("requesttime") Date requesttime, @JsonProperty("data") T data) {
        this.status = status;
        this.msg = msg;
        this.requesttime = requesttime;
        this.data = data;
    }

    public static <T> Result<T> create(int statusCode, String message, T data) {
        return new Result<>(statusCode, message, data);
    }

    public static <T> Result<T> create(int statusCode, String message, Date requesttime, T data) {
        return new Result<>(statusCode, message, requesttime, data);
    }

    public static <T> Result<T> create(Status statusCode, T data) {
        return create(statusCode.getStatus(), statusCode.getReason(), data);
    }

    public static <T> Result<T> success(T data) {
        Status statusCode = Status.success();
        return create(statusCode, data);
    }

    public static <T> Result<T> success(T data, String msg) {
        Status statusCode = Status.success();
        return create(statusCode.getStatus(), msg, data);
    }

    public static <T> Result<T> error(String message) {
        Status statusCode = Status.error(message);
        return create(statusCode, null);
    }

    public static <T> Result<T> error(int status, String message) {
        Status statusCode = Status.error(status, message);
        return create(statusCode, null);
    }

    public int getStatus() {
        return status;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return Status.create(status, msg).isSuccess();
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public Date getRequesttime() {
        return requesttime;
    }

    @Override
    public String toString() {
        return "Result{" + "data=" + data + ", status=" + status + ", msg='" + msg + ", requesttime='" + requesttime
                + '\'' + '}';
    }

    public Result<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Result<T> setStatus(int status) {
        this.status = status;
        return this;
    }
}
