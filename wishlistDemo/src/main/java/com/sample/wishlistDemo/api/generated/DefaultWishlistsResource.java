
package com.sample.wishlistDemo.api.generated;

import javax.inject.Singleton;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.core.Response;

import com.sample.wishlistDemo.document.DocumentService;
import com.sample.wishlistDemo.document.PriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
* Resource class containing the custom logic. Please put your logic here!
*/
@Component("apiWishlistsResource")
@Singleton
public class DefaultWishlistsResource implements com.sample.wishlistDemo.api.generated.WishlistsResource
{
	private static final Logger logger = LoggerFactory.getLogger(DefaultWishlistsResource.class);
	@javax.ws.rs.core.Context
	private javax.ws.rs.core.UriInfo uriInfo;

	@Autowired
	private DocumentService documentService;

	@Autowired
	private PriceService priceService;


	@Override
	public Response get() {
		return null;
	}

	/* post a new wishlist / */
	@Override
	public Response post(final YaasAwareParameters yaasAware, final Wishlist wishlist)
	{
		Map<String, String> wishlistInfo = new HashMap<>();
		Wishlist curWishlist = null;
		boolean isExists = false;
		try{
			logger.info("post wishlists --getWishlistById begin: wishlistId:{}",wishlist.getId());
			curWishlist = documentService.getWishlistByDataId(wishlist.getId());
			logger.info("post wishlists --getWishlistById end: wishlist:{}",curWishlist);
			isExists = true;
		}catch (Exception e){
			logger.info("post wishlists --getWishlistById exception: wishlist:{},e:{}",wishlist,e.getMessage());
			if (!e.getMessage().contains("404")){
				return Response.serverError().build();
			}
		}

		if (isExists){
			curWishlist = mergeToCurrentWishlist(wishlist,curWishlist);
			try{
				logger.info("update wishlists begin: wishlist:{}",wishlist);
				documentService.putWishlist(curWishlist);
				logger.info("update wishlists begin: wishlist:{},wishlistId={}",wishlist,wishlist.getId());
			}catch (Exception e){
				logger.error("update wishlists failed,e:{}",e.getMessage());
				return Response.serverError().build();
			}
		}else{
			try{
				logger.info("insert wishlists begin: wishlist:{}",wishlist);
				documentService.postWishlist(wishlist);
				logger.info("insert wishlists begin: wishlist:{},wishlistId={}",wishlist,wishlist.getId());
			}catch (Exception e){
				logger.error("insert wishlists failed,e:{}",e.getMessage());
				return Response.serverError().build();
			}
		}
		return Response.ok().entity(wishlistInfo).build();
	}

	/* GET /{wishlistId} */
	@Override
	public Response getByWishlistId(final YaasAwareParameters yaasAware, final java.lang.String wishlistId)
	{
		 Wishlist wishlist=null;
		try {
			logger.info("query wishlist by wishlistId begin:wishlistId={}",wishlistId);
			wishlist = documentService.getWishlistByDataId(wishlistId);
			logger.info("query wishlist by wishlistId end:wishlistId={},wishlist={}",wishlistId,wishlist);
		}catch (Exception e){
			logger.error("query wishlist by wishlistId exception:e:{}",e.getMessage());
			return Response.serverError().build();
		}
		return Response.ok().entity(wishlist).build();
	}

	/* PUT /{wishlistId} */
	@Override
	public Response putByWishlistId(final YaasAwareParameters yaasAware, final java.lang.String wishlistId, final Wishlist wishlist)
	{
		try {
			logger.info("putByWishlistId begin:wishlistId={}",wishlistId);
			documentService.putWishlistItem(wishlist.getItems().get(0),wishlistId);
			logger.info("putByWishlistId end:wishlistId={},wishlist={}",wishlistId,wishlist);
		}catch (Exception e) {
			logger.error("putByWishlistId exception:e:{}", e.getMessage());
			return Response.serverError().build();
		}
		return Response.ok().build();
	}


	/* DELETE /{wishlistId} */
	@Override
	public Response deleteByWishlistId(final java.lang.String wishlistId)
	{
		try {
			logger.info("deleteWishlist begin:wishlistId={}",wishlistId);
			documentService.deleteWishlist(wishlistId);
			logger.info("deleteWishlist end:wishlistId={}",wishlistId);
		}catch (Exception e) {
			logger.error("deleteWishlist exception:e:{}", e.getMessage());
			return Response.serverError().build();
		}
		return Response.noContent().build();
	}

	/**
	 * caculate items total prices
	 * @param yaasAware
	 * @param wishlistId
	 * @return
	 */
	@Override
	public Response getByWishlistIdCaculation(@BeanParam @Valid YaasAwareParameters yaasAware, String wishlistId) {
		Wishlist wishlist=null;
		Caculation caculation = new Caculation();
		caculation.setTotalPrice(BigDecimal.ZERO);
		try {
			logger.info("getByWishlistIdCaculation begin:wishlistId={}",wishlistId);
			wishlist = documentService.getWishlistByDataId(wishlistId);
			logger.info("getByWishlistIdCaculation end:wishlistId={},wishlist={}",wishlistId,wishlist);
			if (wishlist == null || CollectionUtils.isEmpty(wishlist.getItems())){
				return Response.ok().entity(caculation).build();
			}
			BigDecimal total = new BigDecimal(0);
			List<WishlistItem> list = wishlist.getItems();
			for (WishlistItem item:list){
				//todo ??forbidden
//				BigDecimal price = priceService.getPriceByPriceId(item.getPriceId());
//				total = total.add(price.multiply(new BigDecimal(item.getAmount())));
//
				BigDecimal price = new BigDecimal(item.getPrice());
				total = total.add(price.multiply(new BigDecimal(item.getAmount())));
			}
			logger.info("caculate total price ,result={}",priceService);
			caculation.setTotalPrice(total.setScale(2,BigDecimal.ROUND_CEILING));
			return Response.ok().entity(caculation).build();
		}catch (Exception e){
			logger.error("getByWishlistIdCaculation exception:e:{}",e.getMessage());
			return Response.serverError().build();
		}
	}



	@Override
	public Response getByWishlistIdWishlistItems(@BeanParam @Valid YaasAwareParameters yaasAware, String wishlistId) {
		List<WishlistItem> wishlistItems = null;
		try{
			logger.info("queryWishListItemsByDataId begin: ,yaasAware:{}",yaasAware);
			wishlistItems= documentService.queryWishListItemsByDataId(wishlistId);
		}catch (Exception e){
			logger.error("queryWishListItemsByDataId failed,e:{}",e.getMessage());
			return Response.serverError().build();
		}
		return Response.ok()
				.entity(wishlistItems).build();
	}


	@Deprecated
	@Override
	public
	Response postByWishlistIdWishlistItems(final YaasAwareParameters yaasAware,
			final java.lang.String wishlistId, final WishlistItem wishlistItem){
		try {
			logger.info("putWishlistItem begin:wishlistId={}",wishlistId);
			documentService.putWishlistItem(wishlistItem,wishlistId);
			logger.info("putWishlistItem end:wishlistId={},wishlistItem={}",wishlistId,wishlistItem);
		}catch (Exception e) {
			logger.error("putWishlistItem exception:e:{}", e.getMessage());
			return Response.serverError().build();
		}
		return Response.ok().build();
	}


	@Deprecated
	@Override
	public Response putByWishlistIdByAttributeByIndexWishlistItem(@BeanParam @Valid YaasAwareParameters yaasAware, String wishlistId, String attribute, Integer index, @Valid WishlistItem wishlistItem) {
		try {
			logger.info("putByWishlistIdByAttributeByIndexWishlistItem begin:wishlistId={}",wishlistId);
			boolean result =documentService.putItemsByWishListIdAndIndex(wishlistId,index,attribute,wishlistItem);
			logger.info("putByWishlistIdByAttributeByIndexWishlistItem end:wishlistId={},wishlistItem={},result={}",wishlistId,wishlistItem,result);
			return Response.ok(result).build();
		}catch (Exception e) {
			logger.error("putByWishlistIdByAttributeByIndexWishlistItem exception:e:{}", e.getMessage());
			return Response.serverError().build();
		}
	}

	@Override
	public Response deleteByWishlistIdByAttributeByIndexWishlistItem(String wishlistId, String attribute, Integer index) {
		return null;
	}


	@Override
	public Response getByWishlistIdByAttributeByIndexWishlistItem(String wishlistId, String attribute, Integer index) {
		return null;
	}

	@Override
	public Response deleteByWishlistIdCaculation(String wishlistId) {
		return null;
	}

	@Override
	public Response putByWishlistIdCaculation(String wishlistId, @Valid Caculation caculation) {
		return null;
	}

	private Wishlist mergeToCurrentWishlist(Wishlist wishlist,Wishlist curWishlist){
		if (curWishlist == null || CollectionUtils.isEmpty(curWishlist.getItems())){
			curWishlist = wishlist;
			return curWishlist;
		}
		for (WishlistItem wishlistItem:wishlist.getItems()){
			boolean isNewItem = true;
			for (WishlistItem curWishlistItem : curWishlist.getItems()){
				if (curWishlistItem.getProductId().equals(wishlistItem.getProductId())){
					curWishlistItem.setAmount(curWishlistItem.getAmount()+wishlistItem.getAmount());
					isNewItem = false;
					break;
				}
			}
			if (isNewItem){
				curWishlist.getItems().add(wishlistItem);
			}
		}
		return curWishlist;
	}


}
