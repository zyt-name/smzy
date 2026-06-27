package com.clou.shopdemo.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsConfig {

    @Value("${config.ip.elasticsearch}")
    private String elasticsearchIp;

    @Value("${config.port.elasticsearch}")
    private String elasticsearchPort;

    @Bean
    public RestHighLevelClient client(){
        return new RestHighLevelClient(RestClient.builder(
               HttpHost.create("http://" + elasticsearchIp + ":" + elasticsearchPort)
       ));
    }
}
