package com.nd.gaea.waf.security.gaea;

import com.nd.gaea.waf.security.DESUtil;
import org.springframework.util.StringUtils;

/**
 * Created by vime on 2016/2/19.
 */
public class GaeaToken {
    private String gaeaToken;
    private Long gaeaId;

    public GaeaToken(String gaeaToken, Long gaeaId) {
        this.gaeaToken = gaeaToken;
        this.gaeaId = gaeaId;
    }

    public GaeaToken(String gaeaToken, String encodeGaeaId) {
        if(StringUtils.isEmpty(encodeGaeaId)){
            throw new RuntimeException("gaea id can't be null");
        }
        this.gaeaToken = gaeaToken;
        this.gaeaId = Long.valueOf(DESUtil.decode(encodeGaeaId));
    }

    public String getGaeaToken() {
        return gaeaToken;
    }

    public Long getGaeaId() {
        return gaeaId;
    }

    public String encodeGaeaId(){
        return DESUtil.encode(String.valueOf(gaeaId));
    }

}
