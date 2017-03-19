package com.sample.wishlistDemo.api.generated;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Generated dto.
 */
@javax.annotation.Generated(value = "hybris", date = "Sat Mar 18 16:42:56 EDT 2017")
@XmlRootElement
@JsonAutoDetect(isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE,
		creatorVisibility = Visibility.NONE, fieldVisibility = Visibility.NONE)
public class WishlistItem
{

	@com.fasterxml.jackson.annotation.JsonProperty(value="productId")
	@javax.validation.constraints.Pattern(regexp="^.+")
	private java.lang.String _productId;

	@com.fasterxml.jackson.annotation.JsonProperty(value="productName")
	@javax.validation.constraints.Pattern(regexp="^.+")
	private java.lang.String _productName;

	@com.fasterxml.jackson.annotation.JsonProperty(value="priceId")
	@javax.validation.constraints.Pattern(regexp="^.+")
	private java.lang.String _priceId;

	@com.fasterxml.jackson.annotation.JsonProperty(value="price")
	@javax.validation.constraints.Pattern(regexp="^.+")
	private java.lang.String _price;

	@com.fasterxml.jackson.annotation.JsonProperty(value="amount")
	@javax.validation.constraints.DecimalMin(value="1")
	private java.lang.Integer _amount;

	@com.fasterxml.jackson.annotation.JsonProperty(value="createdAt")
	private java.util.Date _createdAt;
	
	public java.lang.String getProductId()
	{
		return _productId;
	}
	
	public java.lang.String getProductName()
	{
		return _productName;
	}
	
	public java.lang.String getPriceId()
	{
		return _priceId;
	}
	
	public java.lang.String getPrice()
	{
		return _price;
	}
	
	public java.lang.Integer getAmount()
	{
		return _amount;
	}
	
	public java.util.Date getCreatedAt()
	{
		return _createdAt;
	}

	public void setProductId(final java.lang.String _productId)
	{
		this._productId = _productId;
	}

	public void setProductName(final java.lang.String _productName)
	{
		this._productName = _productName;
	}

	public void setPriceId(final java.lang.String _priceId)
	{
		this._priceId = _priceId;
	}

	public void setPrice(final java.lang.String _price)
	{
		this._price = _price;
	}

	public void setAmount(final java.lang.Integer _amount)
	{
		this._amount = _amount;
	}

	public void setCreatedAt(final java.util.Date _createdAt)
	{
		this._createdAt = _createdAt;
	}

}
