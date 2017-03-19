package com.sample.wishlistDemo.document;

import com.sample.wishlistDemo.api.generated.PagedParameters;
import com.sample.wishlistDemo.api.generated.Wishlist;
import com.sample.wishlistDemo.api.generated.WishlistItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.ManagedBean;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 3/12/17.
 */
@ManagedBean
@Singleton
@PropertySource("classpath:document.properties")
public class PriceService {
    @Value("${priceUri}")
    private String URI;
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

    @Autowired
    private OAuthService oAuthService;

    /**
     * get price
     * @param priceId
     * @return :url
     * @throws Exception
     */
    public BigDecimal getPriceByPriceId(String priceId) throws Exception{
        try {
            String token = getTokenForServiceToCallDocuRepoOnBehalfOfProject();
            String url = URI + "/" + tenant + "/prices/" + priceId;
            HttpEntity<Wishlist> request = new HttpEntity<>(null, headerWithToken(token));
            Map<String,Object> map=  new RestTemplate().exchange(url, HttpMethod.GET, request, Map.class).getBody();
            if (map != null && map.containsKey("effectiveAmount")) {
                return new BigDecimal(String.valueOf(map.get("effectiveAmount")));
            }
            throw new Exception("get price failed");
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        throw new Exception("error");
    }


    private HttpHeaders headerWithToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json");
        headers.add("Authorization", "Bearer " + token);
        return headers;
    }

    private String getTokenForServiceToCallDocuRepoOnBehalfOfProject() throws Exception{
        Map<String, String> tokenMap = oAuthService.getToken().orElse(null);
        if (tokenMap == null)
            throw new Exception("get access token failed");
        return tokenMap.get("access_token");
    }
}
