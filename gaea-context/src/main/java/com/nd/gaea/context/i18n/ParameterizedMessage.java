package com.nd.gaea.context.i18n;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author way
 *         Created on 2016/6/12.
 */
public class ParameterizedMessage {

    private String key;
    private Map<String, Object> parameters;

    public ParameterizedMessage(String key) {
        Assert.hasText(key);
        this.key = key;
    }

    public ParameterizedMessage(String key, Map<String, Object> parameters) {
        Assert.hasText(key);
        this.key = key;
        this.parameters = parameters;
    }

    public ParameterizedMessage(String key, Object[] args) {
        Assert.hasText(key);
        this.key = key;
        this.parameters = new HashMap();
        if(args != null) {
            for(int i = 0; i < args.length; ++i) {
                this.parameters.put("" + i, args[i]);
            }
        }

    }

    public String getKey() {
        return this.key;
    }

    public Map<String, Object> getParameters() {
        return (Map)(CollectionUtils.isEmpty(this.parameters)?new HashMap(): Collections.unmodifiableMap(this.parameters));
    }

    public String populate(MessageSource messageSource) {
        HashMap params = new HashMap();
        if(!CollectionUtils.isEmpty(this.parameters)) {
            Iterator i$ = this.parameters.keySet().iterator();

            while(i$.hasNext()) {
                String key = (String)i$.next();
                Object value = this.parameters.get(key);
                if(value instanceof ParameterizedMessage) {
                    params.put(key, ((ParameterizedMessage)value).populate(messageSource));
                } else {
                    params.put(key, value);
                }
            }
        }

        return messageSource.getMessage(this.key, params);
    }

    public String populate() {
        HashMap params = new HashMap();
        if(!CollectionUtils.isEmpty(this.parameters)) {
            Iterator i$ = this.parameters.keySet().iterator();

            while(i$.hasNext()) {
                String key = (String)i$.next();
                Object value = this.parameters.get(key);
                if(value instanceof ParameterizedMessage) {
                    params.put(key, ((ParameterizedMessage)value).populate());
                } else {
                    params.put(key, value);
                }
            }
        }

        return MessageProvider.getMessage(this.key, params);
    }

    public int hashCode() {
        boolean prime = true;
        byte result = 1;
        int result1 = 31 * result + (this.key == null?0:this.key.hashCode());
        result1 = 31 * result1 + (this.parameters == null?0:this.parameters.hashCode());
        return result1;
    }

    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        } else if(obj == null) {
            return false;
        } else if(this.getClass() != obj.getClass()) {
            return false;
        } else {
            ParameterizedMessage other = (ParameterizedMessage)obj;
            if(this.key == null) {
                if(other.key != null) {
                    return false;
                }
            } else if(!this.key.equals(other.key)) {
                return false;
            }

            if(this.parameters == null) {
                if(other.parameters != null) {
                    return false;
                }
            } else if(!this.parameters.equals(other.parameters)) {
                return false;
            }

            return true;
        }
    }
}
