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
public class Caculation
{

	@com.fasterxml.jackson.annotation.JsonProperty(value="totalPrice")
	private java.lang.Object _totalPrice;
	
	public java.lang.Object getTotalPrice()
	{
		return _totalPrice;
	}

	public void setTotalPrice(final java.lang.Object _totalPrice)
	{
		this._totalPrice = _totalPrice;
	}

}
