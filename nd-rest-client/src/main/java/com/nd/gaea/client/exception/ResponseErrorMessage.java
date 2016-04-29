package com.nd.gaea.client.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

/**
 * @author vime
 * @since 0.9.6
 */
public class ResponseErrorMessage extends ErrorMessage implements Cloneable {

    private String hostId;
    private String requestId;
    private Date serverTime;
    private Throwable throwable;

    public ResponseErrorMessage() {
    }

    public ResponseErrorMessage(Throwable throwable) {
        this.throwable = throwable;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Date getServerTime() {
        return serverTime;
    }

    public void setServerTime(Date serverTime) {
        this.serverTime = serverTime;
    }

    @JsonIgnore
    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("<");
        builder.append("code:");
        builder.append(getCode());
        builder.append(", message:");
        builder.append(getMessage());
        builder.append(", host_id:");
        builder.append(hostId);
        builder.append(", server_time:");
        builder.append(serverTime);
        builder.append(", request_id:");
        builder.append(requestId);

        builder.append(", detail:");
        builder.append(getDetail());

        builder.append(">");
        return builder.toString();
    }

    public ResponseErrorMessage clone() {
        try {
            return (ResponseErrorMessage) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
