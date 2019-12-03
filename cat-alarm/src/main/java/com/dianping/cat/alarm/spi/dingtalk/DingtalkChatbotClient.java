package com.dianping.cat.alarm.spi.dingtalk;

import com.alibaba.fastjson.JSONObject;
import com.dianping.cat.alarm.spi.dingtalk.message.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by dustin on 2017/3/17.
 */
public class DingtalkChatbotClient {

    public static SendResult send(Message message, String url) throws IOException {
        if (StringUtils.isBlank(url)) {
            return new SendResult();
        }
        HttpPost httppost = new HttpPost(url);
        httppost.addHeader("Content-Type", "application/json; charset=utf-8");
        StringEntity se = new StringEntity(message.toJsonString(), "utf-8");
        httppost.setEntity(se);
        SendResult sendResult = new SendResult();
        HttpClient httpsClient = null;
        try {
            httpsClient = getInstance(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpResponse response = httpsClient.execute(httppost);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = EntityUtils.toString(response.getEntity());
            JSONObject obj = JSONObject.parseObject(result);

            Integer errcode = obj.getInteger("errcode");
            sendResult.setErrorCode(errcode);
            sendResult.setErrorMsg(obj.getString("errmsg"));
            sendResult.setIsSuccess(errcode.equals(0));
        }

        return sendResult;
    }

    private static X509TrustManager tm = new X509TrustManager() {
                                           @Override
                                           public void checkClientTrusted(X509Certificate[] xcs,
                                                                          String string)
                                                                                        throws CertificateException {
                                           }

                                           @Override
                                           public void checkServerTrusted(X509Certificate[] xcs,
                                                                          String string)
                                                                                        throws CertificateException {
                                           }

                                           @Override
                                           public X509Certificate[] getAcceptedIssuers() {
                                               return null;
                                           }
                                       };

    @SuppressWarnings("deprecation")
    public static HttpClient getInstance(String url) throws KeyManagementException,
                                                    NoSuchAlgorithmException {
        HttpClient client = new DefaultHttpClient();
        if (url.indexOf("https://") != -1) {
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[] { tm }, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = client.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", ssf, 443));
            client = new DefaultHttpClient(ccm, client.getParams());
        }

        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
        return client;
    }
}
