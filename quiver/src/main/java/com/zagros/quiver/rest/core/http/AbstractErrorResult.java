
package com.zagros.quiver.rest.core.http;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

public class AbstractErrorResult extends VolleyError {

    public static final String VOLLEY_ERROR_GENERAL = "general_error";
    public static final String VOLLEY_ERROR_TIMEOUT = "timeout";
    public static final String VOLLEY_ERROR_SERVER_ERROR = "server_error";
    public static final String VOLLEY_ERROR_NO_CONNECTION = "no_connection";
    public static final String VOLLEY_ERROR_NETWORK_FAILED = "network_error";
    public static final String VOLLEY_ERROR_AUTH_FAILED = "auth_failed";
    public static final String VOLLEY_ERROR_PARSER_FAILED = "parser_error";

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int mHttpErrorCode;
    private String mHttpErrorMessage;
    private String mErrorCode;
    private String mErrorMessage;
    private String mContent;

    public AbstractErrorResult() {
    }

    public AbstractErrorResult(VolleyError volleyError) {
        super(volleyError.getMessage(), volleyError);
        if (volleyError.networkResponse != null) {

            mHttpErrorCode = volleyError.networkResponse.statusCode;
            if (volleyError.networkResponse.data != null) {
                mContent = new String(volleyError.networkResponse.data);
            }
        }
        if (volleyError instanceof AuthFailureError) {
            mErrorCode = VOLLEY_ERROR_AUTH_FAILED;
        } else if (volleyError instanceof NoConnectionError) {
            mErrorCode = VOLLEY_ERROR_NO_CONNECTION;
        } else if (volleyError instanceof NetworkError) {
            mErrorCode = VOLLEY_ERROR_NETWORK_FAILED;
        } else if (volleyError instanceof ServerError) {
            mErrorCode = VOLLEY_ERROR_SERVER_ERROR;
        } else if (volleyError instanceof TimeoutError) {
            mErrorCode = VOLLEY_ERROR_TIMEOUT;
        } else if (volleyError instanceof ParseError) {
            mErrorCode = VOLLEY_ERROR_PARSER_FAILED;
        } else if (volleyError instanceof VolleyError) {
            mErrorCode = VOLLEY_ERROR_GENERAL;
        }

        mErrorMessage = volleyError.getMessage();

    }

    public String getErrorCode() {
        return mErrorCode;
    }

    public void setErrorCode(String aErrorMessage) {
        this.mErrorCode = aErrorMessage;
    }

    public int getHttpErrorCode() {
        return mHttpErrorCode;
    }

    public void setHttpErrorCode(int aHttpErrorCode) {
        this.mHttpErrorCode = aHttpErrorCode;
    }

    public String getHttpErrorMessage() {
        return mHttpErrorMessage;
    }

    public void setHttpErrorMessage(String aHttpErrorMessage) {
        this.mHttpErrorMessage = aHttpErrorMessage;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public void setErrorMessage(String aErrorMessage) {
        this.mErrorMessage = aErrorMessage;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String aContent) {
        this.mContent = aContent;
    }

}
