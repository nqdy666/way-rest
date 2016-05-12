package com.nd.gaea.waf.client;

import com.nd.gaea.client.support.DeliverBearerAuthorizationProvider;
import com.nd.gaea.client.support.WafClientContextHolder;
import com.nd.gaea.rest.security.authens.UserInfo;
import com.nd.gaea.waf.security.gaea.GaeaContext;
import com.nd.gaea.waf.security.gaea.GaeaToken;
import com.nd.gaea.waf.security.gaea.GaeaTokenParser;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;

/**
 * Created by vime on 2016/2/19.
 */
public class DeliverRequestProcessor implements IRequestProcessor {
    @Override
    public void process(IRequest request) {
        String zuulType = GaeaContext.get(GaeaContext.ZUUL_TYPE_FIELD, String.class);
        if(!GaeaContext.ZUUL_TYPE_VALUE_CLOUD.equals(zuulType)){
            String callType = GaeaContext.get(GaeaContext.CALL_TYPE_FIELD, String.class);
            GaeaToken gaeaToken = GaeaContext.getGaeaToken();
            UserInfo userInfo = GaeaContext.getUserInfo();
            if(GaeaContext.CALL_TYPE_VALUE_GAEA.equals(callType) || StringUtils.isEmpty(callType)){
                //gaea
                if (gaeaToken != null) {
                    request.setHeader(GaeaTokenParser.GAEA_AUTHORIZATION, MessageFormat.format("GAEA id=\"{0}\"", gaeaToken.encodeGaeaId()));
                }
                if (userInfo != null) {
                    String trueRealm = GaeaContext.getRealm();
                    if(trueRealm == null){
                        trueRealm = "";
                    }
                    request.setHeader("Authorization", MessageFormat.format("USER user_id=\"{0}\",realm=\"{1}\"", userInfo.getUserId(), trueRealm));
                }else{
                    String gt = gaeaToken.getGaeaToken();
                    if(StringUtils.isEmpty(gt)){
                        gt = WafClientContextHolder.getToken().getBearerToken();
                    }
                    request.setHeader("Authorization", "Bearer \"" + gt + "\"");
                }
            }else if(GaeaContext.CALL_TYPE_VALUE_SDP.equals(callType)){
                //sdp
                String gt = gaeaToken.getGaeaToken();
                if(StringUtils.isEmpty(gt)){
                    gt = WafClientContextHolder.getToken().getBearerToken();
                }
                if (gaeaToken != null) {
                    request.setHeader("Authorization", "Bearer \"" + gt + "\"");
                }
                if (userInfo != null) {
                    request.setHeader(DeliverBearerAuthorizationProvider.USERID, userInfo.getUserId());
                }
            }
            request.setHeader("Accept", "application/sdp+json");
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Accept-Language", LocaleContextHolder.getLocale().toString());
        }
    }
}
