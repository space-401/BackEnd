package com.app.kkiri.global.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@Configuration
public class RestTemplateConfiguration {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate(getRequestFactoryAdvanced());
	}
	private ClientHttpRequestFactory getRequestFactoryAdvanced() {
		RequestConfig config = RequestConfig.custom()
			.setSocketTimeout(6000)
			.setConnectTimeout(6000)
			.setConnectionRequestTimeout(6000)
			.build();

		CloseableHttpClient client = HttpClientBuilder.create()
			.setDefaultRequestConfig(config)
			.build();

		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(client);
		clientHttpRequestFactory.setReadTimeout(10000);

		return clientHttpRequestFactory;
	}
}
