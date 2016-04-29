package com.nd.gaea.client.entity;

import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Date;

/**
 * 封装uc返回的bearerToken信息
 * @author 110825
 * @since 0.9.5
 */
@SuppressWarnings("serial")
public class WafBearerToken implements Serializable {

    private String userId;//应用ID
    private String bearerToken;//bearerToken
    private Date expiresAt;//过期时间
    private String refreshToken;//过期后用于刷新的token

    protected WafBearerToken() {
    }

    public WafBearerToken(String userId, String bearerToken, String refreshToken, Date expiresAt) {
        Assert.notNull(userId,"userId cannot be null.");
        Assert.notNull(bearerToken,"bearerToken cannot be null.");
        Assert.notNull(refreshToken,"refreshToken cannot be null.");

        this.userId = userId;
        this.bearerToken = bearerToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
    }

    public String getUserId() {
        return userId;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getBearerToken() {
        return bearerToken;
    }

    /**
     * 
     * 判断bearer_token是否过期
     * @param	无
     * @return	boolean 
     * @throws
     */
    public boolean isExpire() {
        Date start = new Date();
        Date end = getExpiresAt();
        return (end.getTime() - start.getTime()) < 0L;
    }

}
