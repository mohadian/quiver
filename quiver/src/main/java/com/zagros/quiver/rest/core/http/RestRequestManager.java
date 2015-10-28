
package com.zagros.quiver.rest.core.http;

import android.os.AsyncTask;
import android.test.AndroidTestCase;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.zagros.quiver.rest.core.RequestBuilder;
import com.zagros.quiver.rest.core.RestCall;
import com.zagros.quiver.rest.core.RestException;
import com.zagros.quiver.rest.core.parser.ParserException;
import com.zagros.quiver.rest.core.parser.RestResponseParser;
import com.zagros.quiver.rest.core.utils.NetUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.PriorityQueue;

/**
 * Singleton class that manages the rest requests queue, making the requests,
 * parsing the responses and notifying listeners.
 *
 * @author Mostafa.Hadian
 */
public class RestRequestManager {

    public static final String TAG = "RequestManager";
    private static final boolean QUEUEING_ENABLED = true;
    private final PriorityQueue<RequestTask> mRequestQueue;
    private final DefaultHttpClient mHttpClient;

    private static class RequestManagerHolder {
        public static final RestRequestManager INSTANCE = new RestRequestManager();
    }

    public static RestRequestManager getInstance() {
        return RequestManagerHolder.INSTANCE;
    }

    private RestRequestManager() {
        mRequestQueue = new PriorityQueue<RequestTask>();
        mHttpClient = NetUtils.getHttpClient();
    }

    /**
     * Adds a rest request to the queue. If the request is not anonymous and the
     * session is not valid, it throws a {@link SessionExpiredException}
     *
     * @param restCall
     * @return
     * @throws SessionExpiredException
     */
    public String addRequest(RestCall<?> restCall)
            throws SessionExpiredException {
        // If request needs valid user session, check it

        RequestTask task = new RequestTask(restCall);

        if (QUEUEING_ENABLED) {
            addTaskAndRunNext(task);
        } else {
            task.execute();
        }
        return restCall.getRequest().getRequestId();
    }

    private void addTaskAndRunNext(RequestTask requestTask) {
        synchronized (mRequestQueue) {
            mRequestQueue.add(requestTask);
            runNextTask();
        }
    }

    private void clearQueue() {
        if (mRequestQueue.size() > 0) {
            mRequestQueue.clear();
        }
    }

    private void runNextTask() {
        if (mRequestQueue.size() > 0) {
            RequestTask requestTask = mRequestQueue.poll();
            if (!requestTask.isCancelled()) {
                requestTask.execute();
            }
        }
    }

    /**
     * Asynchronous task to perform the request, parse the response and call
     * appropriate listener methods.
     */
    public class RequestTask extends AsyncTask<Void, Void, Void> implements
            Comparable<RequestTask> {

        private final RestCall<?> mRequest;
        private AbstractErrorResult mError;
        private boolean isNotifiedAlready = false;

        public RequestTask(final RestCall<?> restCall) {
            mRequest = restCall;
            isNotifiedAlready = false;
        }

        RestCall<?> getRequest() {
            return mRequest;
        }

        @Override
        protected void onCancelled() {

        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPreExecute() {
            isNotifiedAlready = false;
            if (mRequest != null && mRequest.getListener() != null)
                mRequest.getListener().onProgressStarted(mRequest);
        }

        @Override
        protected void onPostExecute(Void result) {
            requestPostExecution();
        }

        private void requestPostExecution() {
            try {
                // in case of 401, show the login form. Session expired on
                // server?
                if (!isNotifiedAlready) {
                    notifyListeners();
                }
            } finally {

            }
        }

        @SuppressWarnings("unchecked")
        public void notifyListeners() {
            isNotifiedAlready = true;
            if (mRequest != null && mRequest.getListener() != null) {

                mRequest.getListener().onProgressFinished(mRequest);
                if (isCancelled()) {
                    Log.d(TAG, "Subscription notifyListeners isCancelled");
                    return;
                }

                if (mError != null) {
                    if (mError.getHttpErrorCode() < 0) {
                        if (mError.getHttpErrorCode() == -1) {
                            handleNoInternetConnection(mError);
                        }
                    } else {
                        mRequest.getParser().setError(mError);
                        mRequest.getListener().onFailure(mRequest, mError);
                    }
                } else {
                    try {
                        mRequest.getListener().onSuccess(mRequest);
                    } catch (Exception ex) {
                        mError = new AbstractErrorResult();
                        mError.setErrorCode("-1");
                        mRequest.getListener().onFailure(mRequest, mError);
                    }
                }
            } else {
                // DO NOTHING !
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Set HTTP parameters
                HttpUriRequest httpRequest = null;

                httpRequest = RequestBuilder.getInitializedHttpRequest(mRequest.getRequest());

                if (isCancelled()) {
                    return null;
                }

                // Execute HTTP Post Request

                HttpResponse response;

                response = mHttpClient.execute(httpRequest);

                int statusCode = response.getStatusLine().getStatusCode();

                if (isCancelled()) {
                    NetUtils.convertStreamToString(response.getEntity()
                            .getContent());
                    return null;
                }

                switch (statusCode) {
                    case HttpURLConnection.HTTP_OK: // 200
                    case HttpURLConnection.HTTP_CREATED: // 201
                    case HttpURLConnection.HTTP_ACCEPTED: // 202
                    case HttpURLConnection.HTTP_NO_CONTENT: // 204
                        handleOkResponse(response);
                        break;
                    case HttpURLConnection.HTTP_UNAUTHORIZED: // 401
                        mError = new AbstractErrorResult();
                        mError.setHttpErrorCode(statusCode);
                        mError.setHttpErrorMessage(response.getStatusLine().getReasonPhrase());
                        handleErrorResponse(mRequest, response);
                        break;
                    case HttpURLConnection.HTTP_FORBIDDEN: // 403
                        mError = new AbstractErrorResult();
                        mError.setHttpErrorCode(statusCode);
                        mError.setHttpErrorMessage(response.getStatusLine().getReasonPhrase());
                        handleErrorResponse(mRequest, response);
                        break;
                    case HttpURLConnection.HTTP_BAD_REQUEST: // 400
                        mError = new AbstractErrorResult();
                        mError.setHttpErrorCode(statusCode);
                        mError.setHttpErrorMessage(response.getStatusLine().getReasonPhrase());
                        handleErrorResponse(mRequest, response);
                        break;
                    case HttpURLConnection.HTTP_PRECON_FAILED: // 412
                        mError = new AbstractErrorResult();
                        mError.setHttpErrorCode(statusCode);
                        mError.setHttpErrorMessage(response.getStatusLine().getReasonPhrase());
                        handleErrorResponse(mRequest, response);
                        break;
                    case HttpURLConnection.HTTP_NOT_FOUND: // 404
                        mError = new AbstractErrorResult();
                        mError.setHttpErrorCode(statusCode);
                        mError.setHttpErrorMessage(response.getStatusLine().getReasonPhrase());
                        handleErrorResponse(mRequest, response);
                        break;
                    default:
                        mError = new AbstractErrorResult();
                        mError.setHttpErrorCode(statusCode);
                        mError.setHttpErrorMessage(response.getStatusLine().getReasonPhrase());
                        handleErrorResponse(mRequest, response);

                }
            } catch (UnknownHostException e) {
                mError = new AbstractErrorResult();
                mError.setHttpErrorCode(-1);
                mError.setHttpErrorMessage("UnknownHostException");
                Log.e(TAG, "Subscription Response UnknownHostException ", e);
            } catch (SocketTimeoutException e) {
                mError = new AbstractErrorResult();
                mError.setHttpErrorCode(-1);
                mError.setHttpErrorMessage("SocketTimeoutException");
                Log.e(TAG, "Subscription Response SocketTimeoutException ", e);
            } catch (IllegalStateException e) {
                Log.e(TAG, "Subscription Response IllegalStateException ", e);
            } catch (ClientProtocolException e) {
                mError = new AbstractErrorResult();
                mError.setHttpErrorCode(-1);
                mError.setHttpErrorMessage("ClientProtocolException" + e.getMessage());
                Log.e(TAG, "Subscription Response ClientProtocolException ", e);
            } catch (IOException e) {
                mError = new AbstractErrorResult();
                mError.setHttpErrorCode(-1);
                mError.setHttpErrorMessage("IOException" + e.getMessage());
                Log.e(TAG, "Subscription Response IOException ", e);
            } catch (Exception e) {
                Log.e(TAG, "Subscription Response Excepion ", e);
            } finally {
                if (mRequest.getOwner() != null && mRequest.getOwner() instanceof AndroidTestCase) {
                    requestPostExecution();
                }
            }

            return null;
        }

        private void handleOkResponse(HttpResponse response) throws RestException {
            Log.d(TAG, "Subscription Response handleOkResponse " + response.getStatusLine());
            try {
                BasicResponseHandler responseHandler = new BasicResponseHandler();
                String result = responseHandler.handleResponse(response);

                RestResponseParser jsonParser = mRequest.getParser();

                if (result.length() > 0) {
                    NetworkResponse networkResponse = new NetworkResponse(response.getStatusLine()
                            .getStatusCode(), result.getBytes(), null, true);
                    try {
                        jsonParser.setResponseContent(networkResponse);
                        jsonParser.parse();
                    } catch (ParserException e) {
                        Log.e(TAG, "Exception", e);
                        mError = new AbstractErrorResult();
                        mError.setHttpErrorCode(0);
                        mError.setHttpErrorMessage(" HttpURLConnection.HTTP_UNSUPPORTED_TYPE"
                                + e.getMessage());
                        Log.e(TAG, "Subscription Response ParserException ", e);
                    }

                } else {
                    mError = new AbstractErrorResult();
                    mError.setHttpErrorCode(0);
                    mError.setHttpErrorMessage("EMPTY RESPONSE");
                }
            } catch (HttpResponseException e) {
                Log.e(TAG, "Exception", e);
                mError = new AbstractErrorResult();
                mError.setHttpErrorCode(0);
                mError.setHttpErrorMessage(" HttpURLConnection.HTTP_UNSUPPORTED_TYPE"
                        + e.getMessage());
                Log.e(TAG, "Subscription Response HttpResponseException ", e);
            } catch (IOException e) {
                Log.e(TAG, "Exception", e);
                mError = new AbstractErrorResult();
                mError.setHttpErrorCode(0);
                mError.setHttpErrorMessage(" HttpURLConnection.HTTP_NO_CONTENT" + e.getMessage());
                Log.e(TAG, "Subscription Response IOException ", e);
            } catch (StringIndexOutOfBoundsException e) {
                Log.e(TAG, "Exception", e);
                mError = new AbstractErrorResult();
                mError.setHttpErrorCode(0);
                mError.setHttpErrorMessage(" HttpURLConnection.HTTP_UNSUPPORTED_TYPE"
                        + e.getMessage());
                Log.e(TAG, "Subscription Response StringIndexOutOfBoundsException ", e);
            } finally {

            }
        }

        private void handleErrorResponse(RestCall<?> restCall, HttpResponse response)
                throws RestException {
            RestResponseParser jsonParser = null;
            try {
                HttpEntity entity = response.getEntity();
                InputStream instream = entity.getContent();

                String result = NetUtils.convertStreamToString(instream);

                jsonParser = restCall.getParser();

                if (result.length() > 0) {
                    NetworkResponse networkResponse = new NetworkResponse(response.getStatusLine()
                            .getStatusCode(), result.getBytes(), null, true);
                    try {
                        jsonParser.setResponseContent(networkResponse);
                        jsonParser.parseError();
                    } catch (ParserException e) {
                        Log.e(TAG, "Exception", e);
                        mError = new AbstractErrorResult();
                        mError.setHttpErrorCode(0);
                        mError.setHttpErrorMessage(" HttpURLConnection.HTTP_UNSUPPORTED_TYPE"
                                + e.getMessage());
                        if (jsonParser != null)
                            jsonParser.setError(mError);
                        Log.e(TAG, "Subscription Response ParserException ", e);
                    }

                }
                // if (jsonParser.getErrorResult() != null) {
                // Log.d(TAG,
                // "Subscription Response jsonParser.getErrorResult() != null"
                // + jsonParser.getErrorResult());
                // mError = jsonParser.getErrorResult();
                // } else {
                // Log.d(TAG,
                // "Subscription ResponsejsonParser.getErrorResult() == null");
                // mError = new AbstractErrorResult();
                // }
                //
                // if (jsonParser.getErrorResult() != null
                // &&
                // TextUtils.isDigitsOnly(jsonParser.getErrorResult().getErrorCode()))
                // {
                // Log.d(TAG,
                // "Subscription Response mError.setErrorCode(jsonParser.getErrorResult().getErrorCode())"
                // + jsonParser.getErrorResult().getErrorCode());
                // mError.setErrorCode(jsonParser.getErrorResult().getErrorCode());
                // }
                // else
                // {
                // Log.d(TAG,
                // "Subscription Response mError.setErrorCode(String.valueOf(response.getStatusLine().getStatusCode()))"
                // + response.getStatusLine().getStatusCode());
                // mError.setErrorCode(String.valueOf(response.getStatusLine().getStatusCode()));
                // }
                mError.setHttpErrorCode(response.getStatusLine().getStatusCode());
                mError.setHttpErrorMessage(response.getStatusLine().getReasonPhrase());

                if (instream != null) {
                    Log.d(TAG,
                            "Subscription Response instream.close()");
                    instream.close();
                }
            } catch (IllegalStateException e) {
                Log.e(TAG, "Exception", e);
                mError = new AbstractErrorResult();
                mError.setHttpErrorCode(0);
                mError.setHttpErrorMessage(" HttpURLConnection.HTTP_UNSUPPORTED_TYPE"
                        + e.getMessage());
                if (jsonParser != null)
                    jsonParser.setError(mError);
                Log.e(TAG, "Subscription Response IllegalStateException ", e);
            } catch (IOException e) {
                Log.e(TAG, "Exception", e);
                mError = new AbstractErrorResult();
                mError.setHttpErrorCode(0);
                mError.setHttpErrorMessage(" HttpURLConnection.HTTP_NO_CONTENT" + e.getMessage());
                if (jsonParser != null)
                    jsonParser.setError(mError);
                Log.e(TAG, "Subscription Response IOException ", e);
            } catch (Exception e) {
                Log.e(TAG, "Subscription Response Exception ", e);
            } finally {

            }
        }

        private void handleNoInternetConnection(AbstractErrorResult aError) {
            // NetUtils.showRetryDialog(SubscriptionManager.getInstance().getContext(),
            // mRequest,
            // aError);
        }

        private void handle401HttpError() {

        }

        @SuppressWarnings("unused")
        private AbstractErrorResult parseError(HttpResponse response) throws IllegalStateException,
                IOException {
            String body = "N/A";
            int statusCode = response.getStatusLine().getStatusCode();
            AbstractErrorResult errorResult = new AbstractErrorResult();
            errorResult.setHttpErrorCode(statusCode);
            errorResult.setHttpErrorMessage(response.getStatusLine().getReasonPhrase());
            try {
                body = NetUtils.convertStreamToString(response.getEntity()
                        .getContent());
                errorResult.setContent(body);
                JSONObject json = new JSONObject(body);
                return errorResult;
            } catch (JSONException e) {
                Log.e(TAG, "Subscription Response JSONException ", e);
            } catch (Exception e) {
                Log.e(TAG, "Subscription Response Exception ", e);
            }
            return null;
        }

        public int getPriority() {
            return mRequest.getRequest().getPriority().ordinal();
        }

        @Override
        public String toString() {
            return mRequest.toString();
        }

        @Override
        public int compareTo(RequestTask another) {
            int priority = mRequest.getRequest().getPriority().ordinal();
            int anothersPriority = another.mRequest.getRequest().getPriority().ordinal();
            if (priority == anothersPriority) {
                return 0;
            } else if (priority > anothersPriority) {
                return -1;
            }
            return +1;
        }
    }

}
