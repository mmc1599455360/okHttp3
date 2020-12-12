package com.example.http.okhttp;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 * https证书服务
 */
@Slf4j
public class SSLSocketClientUtils {

	/**
	 * 通过okhttpClient来设置证书
	 *
	 * @param clientBuilder
	 *            OKhttpClient.builder
	 * @param certificates
	 *            读取证书的InputStream
	 */
	public static void setCertificates(OkHttpClient.Builder clientBuilder,
                                       InputStream... certificates) {
		try {
			CertificateFactory certificateFactory = CertificateFactory
					.getInstance("X.509");
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(null);
			int index = 0;
			for (InputStream certificate : certificates) {
				String certificateAlias = Integer.toString(index++);
				keyStore.setCertificateEntry(certificateAlias,
						certificateFactory.generateCertificate(certificate));
				try {
					if (certificate != null) {
						certificate.close();
					}
				}
				catch (IOException e) {
					e.printStackTrace();
					log.error("设置https证书服务失败：{}", e.getMessage());
				}
			}
			TrustManagerFactory trustManagerFactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(keyStore);
			TrustManager[] trustManagers = trustManagerFactory
					.getTrustManagers();
			if (trustManagers.length != 1
					|| !(trustManagers[0] instanceof X509TrustManager)) {
				throw new IllegalStateException(
						"Unexpected default trust managers:"
								+ Arrays.toString(trustManagers));
			}
			X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustManagerFactory.getTrustManagers(),
					new SecureRandom());
			SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
			clientBuilder.sslSocketFactory(sslSocketFactory, trustManager);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取这个SSLSocketFactory
	 **/
	public static SSLSocketFactory getSSLSocketFactory() {
		try {
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, getTrustManager(), new SecureRandom());
			return sslContext.getSocketFactory();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取TrustManager
	 */
	private static TrustManager[] getTrustManager() {
		TrustManager[] trustAllCerts = new TrustManager[] {
				new X509TrustManager() {
					@Override
					public void checkClientTrusted(X509Certificate[] chain,
							String authType) {
					}

					@Override
					public void checkServerTrusted(X509Certificate[] chain,
							String authType) {
					}

					@Override
					public X509Certificate[] getAcceptedIssuers() {
						return new X509Certificate[] {};
					}
				}
		};
		return trustAllCerts;
	}

	/**
	 * 获取HostnameVerifier
	 */
	public static HostnameVerifier getHostnameVerifier() {
		HostnameVerifier hostnameVerifier = new HostnameVerifier() {
			@Override
			public boolean verify(String s, SSLSession sslSession) {
				return true;
			}
		};
		return hostnameVerifier;
	}
}
