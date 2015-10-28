
package com.zagros.quiver.rest.core.builder.content;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;

public class EmptyContentBuilder implements RequestContentBuilder {
    private static final String mStrContent = "";

    public EmptyContentBuilder() {
    }

    @Override
    public HttpEntity getEntity() {
        StringEntity emptyEntity;
        try {
            emptyEntity = new StringEntity(mStrContent);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("invalid encoding for empty content builder");
        }
        return emptyEntity;
    }

    @Override
    public byte[] getContentBytes(String protocolCharset) throws UnsupportedEncodingException {
        return mStrContent.getBytes();
    }
}
