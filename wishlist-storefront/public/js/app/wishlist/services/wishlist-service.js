/**
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */

'use strict';

angular.module('ds.wishlist')

    .factory('WishlistSvc', ['$rootScope', 'WishlistREST','ProductSvc', 'AccountSvc', '$q', 'GlobalData', '$location',
        function ($rootScope, WishlistREST, ProductSvc, AccountSvc, $q, GlobalData) {

            // Prototype for outbound "update wishlist item" call
            var Item = function (product, price, qty) {
                this.productId = product.id;
                this.productName = product.name;
                this.priceId = price.priceId;
                this.price=price.effectiveAmount;
                this.amount = qty;
                this.createdAt = new Date();
            };

            // Prototype for wishlist as used in UI
            var Wishlist = function () {
                this.items = [];
                this.url = null;
                this.title = null;
                this.description= null;
                this.owner = null;
                this.id = null;
                this.createdAt=null;
            };

            // application scope wishlist instance
            var localTemplItems = [];
            var wishlistId;

            function getWishlistFromServer (customerId) {
                var getWishlistFromServerDef = $q.defer();
                if(wishlistId){
                    getWishlistFromServerDef.resolve();
                }else{
                    WishlistREST.Wishlist.one('wishlists', customerId).get().then(function (authUserWishlist) {
                        wishlistId = authUserWishlist.id;
                        getWishlistFromServerDef.resolve();
                    }, function (response) {
                        if (response.status === '404') {   //not exists wishlist ,when add a product to wishlist will new a wishlst
                            console.warn('Could not find wishlist. A new wishlist will be created when the user adds an item.');
                            getWishlistFromServerDef.reject();
                        } else {
                            wishlist.error = true;
                        }
                    })
                }
                return getWishlistFromServerDef.promise;
            }

            /** Creates a new Wishlist Item.  If the wishlist hasn't been persisted yet, the
             * wishlist is created first.
             */
            function createWishlistItem(product, prices, qty, config) {
                var  createWishlistItemDefer = $q.defer();
                var item = new Item(product,prices[0],qty);
                localTemplItems.push(item);
                AccountSvc.getCurrentAccount().then(function (successAccount) {
                    if(successAccount)
                    {
                        sendWishlistToServer(successAccount.id).then(function () {
                            createWishlistItemDefer.resolve();
                        },function () {
                            createWishlistItemDefer.reject();
                        });
                    }else
                    {
                        createWishlistItemDefer.resolve();
                    }
                    },function () {
                    createWishlistItemDefer.resolve();
                });
                return createWishlistItemDefer.promise;
            }

            function sendWishlistToServer(customerId) {
                var  pushNewWishlistDefer = $q.defer();
                var wishlist = {"id":customerId,'owner':customerId,'items':localTemplItems,'createdAt':new Date()};
                WishlistREST.Wishlist.all('wishlists').post(wishlist).then(function (response) {
                    localTemplItems = [];
                    wishlistId = response.id;
                    pushNewWishlistDefer.resolve(wishlist);
                },function () {
                    //alert("add to wishlist failed,please try again");
                    pushNewWishlistDefer.reject();
                })
                return pushNewWishlistDefer.promise;
            }

            function resetWishlistItem(){
                localTemplItems = [];
            }

            var getWishlistItems = function () {
                var getWishlistItemsDefer = $q.defer();
                var wishlistId = GlobalData.customerAccount.id;
                var wishlistItemPromise = WishlistREST.Wishlist.all('wishlists/'+wishlistId+'/wishlistItems').getList();
                wishlistItemPromise.then(function(response) {
                    getWishlistItemsDefer.resolve(response);
                },function (response) {
                    alert(response.message);
                    getWishlistItemsDefer.reject();
                });
                return getWishlistItemsDefer.promise;
            };
            var wishlist={};

            return {

                /**
                 * Creates a new Wishlist instance that does not have an ID.
                 * This will prompt the creation of a new wishlist once items are added to the wishlist.
                 * Should be invoked once an existing wishlist has been successfully submitted to checkout.
                 */
                resetWishlist: function () {
                    wishlistId = null;
                    localTemplItems = [];
                    $rootScope.$emit('wishlist:updated', { wishlist: wishlist, source: 'reset' });
                },

                /** Returns the wishlist as stored in the local scope - no GET is issued.*/
                getLocalWishlist: function () {
                    return wishlistId;
                },

                getLocalWishlistId:function () {
                    return wishlistId;
                },

                /**
                 * Retrieves the current wishlist's state from service and returns a promise over that wishlist.
                 */
                getWishlist: function () {
                    return getWishlistFromServer(null);
                },

                /**
                 * Retrieve any existing wishlist that there might be for an authenticated user, and merges it with
                 * any content in the current wishlist.
                 */
                refreshWishlistAfterLogin: function (customerId) {
                    wishlistId = customerId;
                    //var deferred = $q.defer();
                    if (localTemplItems.length != 0) {
                        return sendWishlistToServer(wishlistId);
                    } else
                    {
                        return $q.when({});
                    }
                },


                /*
                 *   Adds a product to the wishlist, updates the wishlist (PUT) and then retrieves the updated
                 *   wishlist information (GET).
                 *   @param product to add
                 *   @param productDetailQty quantity to add
                 *   @param closeWishlistAfterTimeout if the
                 *   @return promise over success/failure
                 */
                addProductToWishlist: function (product, prices, productDetailQty, config) {
                    var addDefer = $q.defer();
                    if (productDetailQty > 0) {
                         createWishlistItem(product, prices, productDetailQty, config).then(function () {
                             addDefer.resolve();
                         },function () {
                             addDefer.reject();
                         })
                    }else
                    {
                        addDefer.reject();
                    }
                    return addDefer.promise;
                },

                /**
                 * get currency
                 */
                computeTotalPrice:function () {
                    var wishlistId = GlobalData.customerAccount.id;
                    var computeTotalPriceDefer = $q.defer();
                    if (wishlistId){
                        WishlistREST.Wishlist.one('wishlists/'+wishlistId+'/caculation').get().then(function (response) {
                             alert("total price : $" + response.totalPrice);
                            computeTotalPriceDefer.resolve();
                         },function () {
                            computeTotalPriceDefer.reject();
                        })
                        return computeTotalPriceDefer.promise;
                    }else {
                        return $q.when({});
                    }
                },

                query:function () {
                    return getWishlistItems();
                   // return getWishlistItems(params);
                }

            };

        }]);
