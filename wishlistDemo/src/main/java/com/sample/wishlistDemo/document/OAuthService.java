package com.sample.wishlistDemo.document;

import java.util.Map;
import java.util.Optional;

import javax.annotation.ManagedBean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@ManagedBean
@PropertySource("classpath:document.properties")
public class OAuthService {

    @Value("${OAUTH2_TOKEN_ENDPOINT_URL}")
    private String oauthUri;
    @Value("${tenant}")
    private String tenant;
    @Value("${appId}")
    private String appId;
    @Value("${type}")
    private String type;
    @Value("${clientId}")
    private String clientId;
    @Value("${clientSecret}")
    private String clientSecret;
    @Value("${scopes}")
    private String scopes;

    private String grantType = "client_credentials";

    public OAuthService( ){
    }

    public OAuthService build(  ){
        this.scopes = "hybris.tenant"+"="+tenant+" "+scopes;
        return this;
    }

    public Optional<Map<String,String>> getToken() throws Exception{
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("content-type", "application/x-www-form-urlencoded" );
            RestTemplate restTemplate = new RestTemplate();
            String body =
                    "grant_type="+grantType+
                            "&client_id="+clientId+
                            "&client_secret="+clientSecret+
                            "&scope="+scopes;
            HttpEntity<Object> request = new HttpEntity<>( body, headers );
            Map<String,String> tokenMap = restTemplate.postForObject(oauthUri, request, Map.class );
            return Optional.of(tokenMap);
        } catch (RestClientException e) {
            throw new Exception("call yaas service failed");
        }
    }
}
