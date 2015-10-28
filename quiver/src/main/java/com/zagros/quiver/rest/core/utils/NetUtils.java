
package com.zagros.quiver.rest.core.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.zagros.quiver.R;
import com.zagros.quiver.rest.core.RestCall;
import com.zagros.quiver.rest.core.RestCallBuilder;
import com.zagros.quiver.rest.core.http.AbstractErrorResult;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Utility class supporting the Subscription Library Network
 *
 * @author Mostafa.Hadian
 */
public class NetUtils {

    private static String mUserAgent = null;
    private static final String TAG = NetUtils.class.getSimpleName();

    public static DefaultHttpClient getHttpClient() {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry
                .register(new Scheme("https", CustomSSLSocketFactory.getSocketFactory(), 443));

        HttpParams params = new BasicHttpParams();
        params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
        params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
        params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
        // HttpConnectionParams.setConnectionTimeout(params, (int)
        // Constants.INTERVAL_5_SECONDS);
        // HttpConnectionParams.setSoTimeout(params, (int)
        // Constants.INTERVAL_15_SECONDS);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);

        DefaultHttpClient httpClient = new DefaultHttpClient(cm, params);
        httpClient.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
            public long getKeepAliveDuration(HttpResponse aResponse, HttpContext aContext) {
                // Keep the connection alive for up to 60 seconds - this works
                // nicely
                // for the
                // client, but might be a resource hog for the server.
                return 60000;
            }
        });

        // Set the timeouts to reasonable, but tolerant values - 5s for
        // connection,
        // 15s for data
        // HttpParams params = httpClient.getParams();

        return httpClient;
    }

    public static HttpClient getUnstrustedHttpsClient() {
        // TODO remove this when Telnic guys fix certificate
        final class MySSLSocketFactory extends SSLSocketFactory {
            SSLContext sslContext = SSLContext.getInstance("TLS");

            public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException,
                    KeyManagementException, KeyStoreException, UnrecoverableKeyException {
                super(truststore);

                TrustManager tm = new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                };

                sslContext.init(null, new TrustManager[]{
                        tm
                }, null);
            }

            @Override
            public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
                    throws IOException, UnknownHostException {
                return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
            }

            @Override
            public Socket createSocket() throws IOException {
                return sslContext.getSocketFactory().createSocket();
            }
        }
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return NetUtils.getHttpClient();
        }
    }

    /**
     * Detects whether the device is connected making a query to the
     * connectivity manager.
     */
    public static synchronized boolean isActiveNetworkStateConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        boolean result = false;
        if (info != null) {
            if (info.getState() == NetworkInfo.State.CONNECTED) {
                result = true;
            }
        }

        return result;
    }

    /**
     * Create an alert dialog configured for deletion confirmation
     *
     * @param aParent     parent activity to which the dialog will belong
     * @param aError
     * @param aDialogId   id of waiting dialog in the parent,
     * @param ServiceCall service call object
     * @return the alert dialog
     */

    public static void showRetryDialog(final Context aParent, final RestCall<?> aRequest,
                                       final AbstractErrorResult aError) {
        if (aParent != null) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(aParent);
            dialog.setTitle(R.string.subscription_retry_dialog_title);
            dialog.setMessage(R.string.subscription_retry_dialog_messages_error_failed_no_internet);
            dialog.setPositiveButton(R.string.subscription_retry_dialog_retry,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            RestCallBuilder.resend(aRequest);
                        }
                    });
            dialog.setNegativeButton(R.string.subscription_retry_dialog_cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            aRequest.getListener().onFailure(aRequest, aError);
                        }
                    });
            dialog.setCancelable(false);

            dialog.create().show();
        } else {
            aRequest.getListener().onFailure(aRequest, aError);
        }
    }

    /**
     * To convert the InputStream to String we use the BufferedReader.readLine()
     * method. We iterate until the BufferedReader return null which means
     * there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
     *
     * @param is
     * @return
     */
    public static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            Log.e(NetUtils.class.getSimpleName(),
                    "exception on reading stream", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(NetUtils.class.getSimpleName(),
                        "exception on closing stream", e);
            }
        }

        return sb.toString();
    }

    public static String getUserAgent(Context mContext) {
        if (mUserAgent == null) {
            mUserAgent = mContext.getApplicationInfo().name;
            try {
                String packageName = mContext.getPackageName();
                String version = mContext.getPackageManager().getPackageInfo(packageName, 0).versionName;
                mUserAgent = mUserAgent + " (" + packageName + "/" + version + ")";
                Log.d(TAG, "User agent set to: " + mUserAgent);
            } catch (PackageManager.NameNotFoundException e) {
                Log.d(TAG, "Unable to find self by package name", e);
            }
        }
        return mUserAgent;
    }
}
