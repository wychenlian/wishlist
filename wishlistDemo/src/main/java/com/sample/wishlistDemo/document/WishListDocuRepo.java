package com.sample.wishlistDemo.document;


import javax.annotation.ManagedBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:document.properties")
@ManagedBean
public class WishListDocuRepo {

    @Autowired
    private DocumentService dsw;

//    public List<Wishlist> findByTenant(String tenant) {
//        Wishlist[] tips = dsw.get();
//        return Arrays.asList(tips);
//    }
//
//    public String post(WishList t) {
//        return dsw.post(t);
//    }
//
//    public String put(WishList t) {
//        return dsw.put(t);
//    }
//
//    public void delete(String id) {
//        dsw.delete(id);
//    }
//
//    public void deleteAll() {
//        dsw.deleteAll();
//    }
//
//    public boolean exists(String id) {
//        return dsw.get(id).isPresent();
//    }
//
//    public Optional<WishList> findOne(String id) {
//        return dsw.get(id);
//
//    }
}