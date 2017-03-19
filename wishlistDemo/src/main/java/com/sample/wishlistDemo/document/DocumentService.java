package com.sample.wishlistDemo.document;

import com.sample.wishlistDemo.api.generated.PagedParameters;
import com.sample.wishlistDemo.api.generated.Wishlist;
import com.sample.wishlistDemo.api.generated.WishlistItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.ManagedBean;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created by root on 3/12/17.
 */
@ManagedBean
@Singleton
@PropertySource("classpath:document.properties")
public class DocumentService {
    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);
    @Value("${documentUri}")
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

    @Autowired
    private ProductService productService;

    @Autowired
    private PriceService priceService;



    /**
     * 新建wishlist
     * @param wishlist
     * @return :url
     * @throws Exception
     */
    public Map<String, String> postWishlist(Wishlist wishlist) throws Exception{
        try {
            String token = getTokenForServiceToCallDocuRepoOnBehalfOfProject();
            String url = generateBaseUrl();
            HttpEntity<Wishlist> request = new HttpEntity<>(wishlist, headerWithToken(token));
            Map<String, String> map = new RestTemplate().postForObject(url, request, Map.class);
            if (map != null && map.containsKey("id"))
                return map;
        } catch (RestClientException e) {
            logger.warn("documentService--putWishlist failed ,e:{}",e.getMessage());
            responseCvt(e.getMessage());
        }
        throw new Exception("unknown error");
    }

    /**
     * 新建wishlist
     * @param wishlist
     * @return :url
     * @throws Exception
     */
    public void putWishlist(Wishlist wishlist) throws Exception{
        try {
            String token = getTokenForServiceToCallDocuRepoOnBehalfOfProject();
            String url = generateBaseUrl();
            HttpEntity<Wishlist> request = new HttpEntity<>(wishlist, headerWithToken(token));
            new RestTemplate().exchange(url, HttpMethod.PUT, request, Map.class).getBody();
            return ;
        } catch (RestClientException e) {
            logger.warn("documentService--putWishlist failed ,e:{}",e.getMessage());
            responseCvt(e.getMessage());
        }
        throw new Exception("unknown error");
    }


    /**
     * put wishlistItem to wishlist
     * @param wishlistItem : wishlistItem entity
     * @param dataId : wishlist id // or owner
     * @return
     * @throws Exception
     */
    public void putWishlistItem(WishlistItem wishlistItem, String dataId) throws Exception{
        try {
            String token = getTokenForServiceToCallDocuRepoOnBehalfOfProject();
            String url = URI + "/" + tenant + "/" + appId + "/data/" + "wishlist/"+dataId+"/items";
            HttpEntity<WishlistItem> request = new HttpEntity<>(wishlistItem, headerWithToken(token));
            Map<String, String> map = new RestTemplate().postForObject(url, request, Map.class);
            if (map != null && map.containsKey("code") && map.get("code").equals("200"))
                return ;
        } catch (RestClientException e) {
            logger.warn("documentService--putWishlistItem failed ,e:{}",e.getMessage());
            responseCvt(e.getMessage());
        }
        throw new Exception("unknown error");
    }


    /**
     * query wistlistItem by wishlist id
     * @param dataId
     * @return
     * @throws Exception
     */
    public List<WishlistItem> queryWishListItemsByDataId(String dataId) throws Exception{
        try {
            String token = getTokenForServiceToCallDocuRepoOnBehalfOfProject();
            String url = URI + "/" + tenant + "/" + appId + "/data/" + "wishlist/"+dataId;
            HttpEntity<String> request = new HttpEntity<>(dataId, headerWithToken(token));
            Map<String,String> urlVal = new HashMap<>();
            urlVal.put("fields","items");
            Map<String,Object> result =  new RestTemplate().exchange(url, HttpMethod.GET, request, Map.class,urlVal).getBody();
            if (result != null && result.containsKey("items") && result.get("items") != null){
                return  (List<WishlistItem>) result.get("items");
            }
            return Collections.EMPTY_LIST;
        } catch (RestClientException e) {
            logger.warn("documentService--putWishlistItem failed ,e:{}",e.getMessage());
            if (e.getMessage().contains("404")){
                return Collections.EMPTY_LIST;
            }
            throw new Exception(e.getMessage());
        }
    }


    /**
     *
     * query wistlistItem by wishlist id
     * @param owner
     * @return
     * @throws Exception
     */
    //todo not work???
    public List<Wishlist> queryWishListsByOwner(String owner, PagedParameters paged) throws Exception{
        try {
            String token = getTokenForServiceToCallDocuRepoOnBehalfOfProject();
            String url = URI + "/" + tenant + "/" + appId + "/data/" + "wishlist/"+owner;
            Map<String,String> urlVal = new HashMap<>();
            if (paged.getPageNumber() != null && paged.getPageNumber() > 0) {
                urlVal.put("pageNumber", paged.getPageNumber().toString());
            }else{
                urlVal.put("pageNumber", "1");
            }
            if (paged.getPageSize() != null && paged.getPageSize() > 0) {
                urlVal.put("pageSize", paged.getPageNumber().toString());
            }else{
                urlVal.put("pageSize", String.valueOf(Integer.MAX_VALUE));
            }
            HttpEntity<String> request = new HttpEntity<>(null, headerWithToken(token));
            List<Wishlist> result =  new RestTemplate().exchange(url, HttpMethod.GET, request, List.class,urlVal).getBody();
            return org.springframework.util.CollectionUtils.isEmpty(result)?Collections.EMPTY_LIST:result;
        } catch (RestClientException e) {
            logger.warn("documentService--queryWishListsByOwner failed ,e:{}",e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    /**
     * query wishlist by id
     * @param dataId
     * @return
     * @throws Exception
     */
    public Wishlist getWishlistByDataId(String dataId) throws Exception{
        try {
            String token = getTokenForServiceToCallDocuRepoOnBehalfOfProject();
            String url = URI + "/" + tenant + "/" + appId + "/data/" + "wishlist/"+dataId;
            HttpEntity<String> request = new HttpEntity<>(null, headerWithToken(token));
            Wishlist result =  new RestTemplate().exchange(url, HttpMethod.GET, request, Wishlist.class).getBody();
            return result;
        } catch (RestClientException e) {
            logger.warn("documentService--getWishlistByDataId failed ,e:{}",e.getMessage());
            throw new Exception(e.getMessage());
        }
    }



    /**
     * delete wishlistItem
     * @param index : wishlistItem entity
     * @param dataId : wishlist id
     * @return
     * @throws Exception
     */
    public boolean deleteWishlistItem(String index,String dataId) throws Exception{
        try {
            String token = getTokenForServiceToCallDocuRepoOnBehalfOfProject();
            String url = URI + "/" + tenant + "/" + appId + "/data/" + "wishlist/"+dataId+"/items/"+index;
            HttpEntity<WishlistItem> request = new HttpEntity<>( headerWithToken(token));

            Map<String,Object> result =  new RestTemplate().exchange(url, HttpMethod.DELETE, request, Map.class).getBody();
            return (result != null && result.containsKey("code") && result.get("code").equals("200"));

        } catch (RestClientException e) {
            logger.warn("documentService--deleteWishlistItem failed ,e:{}",e.getMessage());
            throw new Exception(e.getMessage());
        }
    }


    /**
     * delete wishlist
     * @param dataId : wishlist id
     * @return
     * @throws Exception
     */
    public boolean deleteWishlist(String dataId) throws Exception{
        try {
            String token = getTokenForServiceToCallDocuRepoOnBehalfOfProject();
            String url = URI + "/" + tenant + "/" + appId + "/data/" + "wishlist/"+dataId;
            HttpEntity<WishlistItem> request = new HttpEntity<>( headerWithToken(token));
            Map<String,Object> result =  new RestTemplate().exchange(url, HttpMethod.DELETE, request, Map.class).getBody();
            return (result != null && result.containsKey("code") && result.get("code").equals("200"));
        } catch (RestClientException e) {
            logger.warn("documentService--deleteWishlist failed ,e:{}",e.getMessage());
            throw new Exception(e.getMessage());
        }
    }


    /**
     * query wistlistItem by wishlist id
     * @param dataId
     * @return
     * @throws Exception
     */
    public boolean putItemsByWishListIdAndIndex(String dataId,Integer index,String attribute,WishlistItem wishlistItem) throws Exception{
        try {
            String token = getTokenForServiceToCallDocuRepoOnBehalfOfProject();
            String url = URI + "/" + tenant + "/" + appId + "/data/" + "wishlist/"+dataId+"/"+attribute+"/"+index;
            HttpEntity<WishlistItem> request = new HttpEntity<>(wishlistItem, headerWithToken(token));
            Map<String,Object> map =  new RestTemplate().exchange(url, HttpMethod.PUT, request, Map.class).getBody();
            return (map != null && map.containsKey("code") && map.get("code").equals("200"));
        } catch (RestClientException e) {
            logger.warn("documentService--deleteWishlist failed ,e:{}",e.getMessage());
            throw new Exception(e.getMessage());
        }
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

    private void responseCvt(String errorMessage) throws Exception{
        if (errorMessage.contains("400")){
            throw new Exception("paramter error");
        }else if(errorMessage.contains("401") || errorMessage.contains("403")){
            throw new Exception("unauthorized");
        }else if (errorMessage.contains("409")){
            throw new Exception("modification failed");
        }else{
            throw new Exception(errorMessage);
        }
    }

    private String generateBaseUrl(){
        return URI + "/" + tenant + "/" + appId + "/data/" + "wishlist";
    }
}
