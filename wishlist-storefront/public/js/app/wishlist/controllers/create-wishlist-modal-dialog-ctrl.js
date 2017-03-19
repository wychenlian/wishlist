/**
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
'use strict';

angular.module('ds.wishlist')
/**
 * Controller for handling authentication related modal dialogs (signUp/signIn).
 */
    .controller('CreateWishlistModalDialogCtrl', ['$rootScope', '$scope', 'AuthSvc',
        'settings', 'CreateWishListDialogManager', 'loginOpts', 'showAsGuest', '$state', '$window','WishlistSvc',
        function ($rootScope, $scope, AuthSvc, settings, CreateWishListDialogManager, loginOpts, showAsGuest, $state, $window,WishlistSvc) {

            $scope.wishlist = {
                title: '',
                description: ''
            };

            $scope.errors = {
                title: [],
                description: []
            };


            $scope.cookiesEnabled = $window.navigator.cookieEnabled;


            /** Closes the dialog.*/
            $scope.closeDialog = function(){
                CreateWishListDialogManager.close();
            };


            /** Shows dialog that allows the user to sign in so account specific information can be accessed. */
            $scope.createWishList = function (createWishListForm) {
                if (createWishListForm.$valid) {
                    $scope.wishlist.title = createWishListForm.title;
                    $scope.wishlist.description = createWishListForm.description;
                    WishlistSvc.createWishList($scope.wishlist.title,$scope.wishlist.description,$scope.wishListItem).then(function () {
                        // $scope.closeDialog();
                        CreateWishListDialogManager.close();
                    },function () {
                        // $scope.errors.title = "CREATE WISHLIST FAILED";
                    });
                }
            };


            $scope.clearErrors = function() {
                $scope.errors.title = [];
                $scope.errors.description = [];
            };

        }]);