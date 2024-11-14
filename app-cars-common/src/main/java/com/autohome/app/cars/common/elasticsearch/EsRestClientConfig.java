package com.autohome.app.cars.common.elasticsearch;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class EsRestClientConfig {

    @Value("${spring.elasticsearch.uris}")
    private String host;

    @Value("${spring.elasticsearch.port}")
    private Integer port;

    @Value("${spring.elasticsearch.username}")
    private String username;

    @Value("${spring.elasticsearch.password}")
    private String password;

    @Value("${spring.elasticsearch2.uris}")
    private String host2;

    @Value("${spring.elasticsearch2.port}")
    private Integer port2;

    @Bean(name = "appesEsClient", destroyMethod = "close")
    public RestHighLevelClient appesEsClient() {
        RestClientBuilder restBuilder = RestClient.builder(new HttpHost(host, port));
        final CredentialsProvider creadential = new BasicCredentialsProvider();
        creadential.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        restBuilder.setHttpClientConfigCallback(httpClientBuilder ->
                httpClientBuilder.setDefaultCredentialsProvider(creadential)
                        .setDefaultIOReactorConfig(IOReactorConfig.custom().setIoThreadCount(1).build()));
        restBuilder.setRequestConfigCallback(requestConfigBuilder ->
                requestConfigBuilder.setConnectTimeout(10000) // time until a connection with the server is established.
                        .setSocketTimeout(60000) // time of inactivity to wait for packets[data] to receive.
                        .setConnectionRequestTimeout(0));
        return new RestHighLevelClient(restBuilder);
    }


    @Bean(name = "seriesUvEsClient", destroyMethod = "close")
    public RestHighLevelClient seriesUvEsClient() {
        RestClientBuilder restBuilder = RestClient.builder(new HttpHost(host2, port2));
        restBuilder.setHttpClientConfigCallback(httpClientBuilder ->
                httpClientBuilder.setDefaultIOReactorConfig(IOReactorConfig.custom().setIoThreadCount(1).build()));
        restBuilder.setRequestConfigCallback(requestConfigBuilder ->
                requestConfigBuilder.setConnectTimeout(10000) // time until a connection with the server is established.
                        .setSocketTimeout(60000) // time of inactivity to wait for packets[data] to receive.
                        .setConnectionRequestTimeout(0));
        return new RestHighLevelClient(restBuilder);
    }

}
