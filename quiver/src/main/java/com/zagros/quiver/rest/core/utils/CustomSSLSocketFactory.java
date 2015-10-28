
package com.zagros.quiver.rest.core.utils;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.X509TrustManager;

/**
 * This socket factory will create ssl socket that accepts self signed
 * certificate
 */
public class CustomSSLSocketFactory implements LayeredSocketFactory {

    private static CustomSSLSocketFactory mInstance = null;
    private SSLContext mSslcontext = null;

    public static CustomSSLSocketFactory getSocketFactory() {
        if (mInstance == null) {
            mInstance = new CustomSSLSocketFactory();
        }
        return mInstance;
    }

    private static SSLContext createEasySSLContext() throws IOException {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }

                        public void checkServerTrusted(X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }

                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            }, new SecureRandom());
            return context;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    private SSLContext getSSLContext() throws IOException {
        if (this.mSslcontext == null) {
            this.mSslcontext = createEasySSLContext();
        }
        return this.mSslcontext;
    }

    public Socket connectSocket(Socket sock, String host, int port,
                                InetAddress localAddress, int localPort, HttpParams params)
            throws IOException, UnknownHostException, ConnectTimeoutException {
        int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
        int soTimeout = HttpConnectionParams.getSoTimeout(params);

        InetSocketAddress remoteAddress = new InetSocketAddress(host, port);
        SSLSocket sslsock = (SSLSocket) ((sock != null) ? sock : createSocket());

        if ((localAddress != null) || (localPort > 0)) {
            // we need to bind explicitly
            if (localPort < 0) {
                localPort = 0; // indicates "any"
            }
            InetSocketAddress isa = new InetSocketAddress(localAddress,
                    localPort);
            sslsock.bind(isa);
        }

        sslsock.connect(remoteAddress, connTimeout);
        sslsock.setSoTimeout(soTimeout);
        return sslsock;

    }

    public Socket createSocket() throws IOException {
        return getSSLContext().getSocketFactory().createSocket();
    }

    public boolean isSecure(Socket socket) throws IllegalArgumentException {
        return true;
    }

    public Socket createSocket(Socket socket, String host, int port,
                               boolean autoClose) throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override
    public boolean equals(Object obj) {
        return ((obj != null) && obj.getClass().equals(
                CustomSSLSocketFactory.class));
    }

    @Override
    public int hashCode() {
        return CustomSSLSocketFactory.class.hashCode();
    }

}
