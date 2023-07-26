package com.checkmarx.sdk;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.SecureRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.checkmarx.sdk.api.v1.CheckmarxClient;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.checkmarx.sdk.config.SSLConfiguration;
import com.checkmarx.sdk.config.CheckmarxProxyConfig;
import com.checkmarx.sdk.interceptor.ServiceInterceptor;
import okhttp3.*;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Checkmarx {
	private static final Logger LOG = LoggerFactory.getLogger(Checkmarx.class);

	private static final String DEFAULT_BASE_URL = "https://api.dusti.co/v1/";
	private static final String DEFAULT_USER_AGENT = "checkmarx-sdk-java";
	private static final long DEFAULT_CONNECTION_TIMEOUT = 30L;
	private static final long DEFAULT_READ_TIMEOUT = 60L;
	private static final long DEFAULT_WRITE_TIMEOUT = 60L;

	private final Retrofit retrofit;

	private Checkmarx(Config config) throws Exception {
		if (config.token == null || config.token.isEmpty()) {
			throw new IllegalArgumentException("Checkmarx API token is empty");
		}

		OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(DEFAULT_CONNECTION_TIMEOUT, SECONDS)
			.readTimeout(DEFAULT_READ_TIMEOUT, SECONDS)
			.writeTimeout(DEFAULT_WRITE_TIMEOUT, SECONDS);

		configureProxy(builder, config);

		if (config.trustAllCertificates) {
			SSLContext sslContext = SSLContext.getInstance("TLS");
			TrustManager[] trustManagers = SSLConfiguration.buildUnsafeTrustManager();
			sslContext.init(null, trustManagers, new SecureRandom());
			SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
			builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustManagers[0]);
		} else if (config.sslCertificatePath != null && !config.sslCertificatePath.isEmpty()) {
			SSLContext sslContext = SSLContext.getInstance("TLS");
			X509TrustManager trustManager = SSLConfiguration.buildCustomTrustManager(config.sslCertificatePath);
			sslContext.init(null, new TrustManager[]{trustManager}, null);
			SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
			builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustManager);
		}

		builder.addInterceptor(new ServiceInterceptor(config.token, config.userAgent));
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		retrofit = new Retrofit.Builder().client(builder.build())
			.baseUrl(Checkmarx.DEFAULT_BASE_URL)
			.addConverterFactory(JacksonConverterFactory.create(objectMapper))
			.build();
	}

	private void configureProxy(OkHttpClient.Builder builder, Config config) {
		try {
			String proxyHost = config.proxyConfig.getProxyHost();
			int proxyPort = config.proxyConfig.getProxyPort();
			String proxyUser = config.proxyConfig.getProxyUser();
			String proxyPassword = config.proxyConfig.getProxyPassword();

			if (proxyHost != null && proxyPort != 0) {
				LOG.info("setup proxyHost and proxyPort");
				InetSocketAddress proxyAddress = new InetSocketAddress(
					proxyHost,
					proxyPort
				);
				builder.proxy(new Proxy(Proxy.Type.HTTP, proxyAddress));
			}

			// If the proxy is authenticated, then set up an authentication handler
			if (proxyUser != null && proxyPassword != null) {
				Authenticator proxyAuthenticator = new Authenticator() {
					@Override
					public Request authenticate(Route route, Response response) throws IOException {
						String credential = Credentials.basic(proxyUser, proxyPassword);
						return response.request().newBuilder()
							.header("Proxy-Authorization", credential)
							.build();
					}
				};
				builder.proxyAuthenticator(proxyAuthenticator);
			}

		} catch (Exception e) {
			System.err.println("Error configuring proxy: " + e.getMessage());
		}
	}

	public static Checkmarx newBuilder(Config config) throws Exception {
		return new Checkmarx(config);
	}

	public CheckmarxClient buildSync() {
		return retrofit.create(CheckmarxClient.class);
	}

	public static final class Config {
		String baseUrl;
		String token;
		String userAgent;
		boolean trustAllCertificates;
		String sslCertificatePath;
		CheckmarxProxyConfig proxyConfig;

		public Config(String token) {
			this(DEFAULT_BASE_URL, token, null);
		}

		public Config(String token, CheckmarxProxyConfig proxyConfig) {
			this(DEFAULT_BASE_URL, token, proxyConfig);
		}

		public Config(String baseUrl, String token, CheckmarxProxyConfig proxyConfig) {
			this(baseUrl, token, DEFAULT_USER_AGENT, proxyConfig);
		}

		public Config(String baseUrl, String token, String userAgent, CheckmarxProxyConfig proxyConfig) {
			this(baseUrl, token, userAgent, false, proxyConfig);
		}

		public Config(String baseUrl, String token, String userAgent, boolean trustAllCertificates, CheckmarxProxyConfig proxyConfig) {
			this(baseUrl, token, userAgent, trustAllCertificates, "", proxyConfig);
		}

		public Config(String baseUrl, String token, String userAgent, boolean trustAllCertificates, String sslCertificatePath, CheckmarxProxyConfig proxyConfig) {
			this.baseUrl = baseUrl;
			this.token = token;
			this.userAgent = userAgent;
			this.trustAllCertificates = trustAllCertificates;
			this.sslCertificatePath = sslCertificatePath;
			this.proxyConfig = proxyConfig;
		}
	}
}
