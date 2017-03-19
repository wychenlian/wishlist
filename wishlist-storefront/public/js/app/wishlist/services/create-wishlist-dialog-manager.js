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

/** Authorization manager.  */
angular.module('ds.wishlist')
    .factory('CreateWishListDialogManager', ['$uibModal',
        function($uibModal){

            var createWishlistDialog;

            function closeDialog(){
                if (createWishlistDialog) {
                    try {
                        createWishlistDialog.close();
                    } catch (err){

                    }
                }
            }

            function openDialog(options) {
                // make sure only 1 instance exists in opened state
                closeDialog();
                createWishlistDialog = $uibModal.open(options);
                return createWishlistDialog.result;
            }

            return {


                /**
                 * Creates and opens the authorization dialog for sign in/create account.
                 * Returns the promise returned by $uibModal.result (see angular bootstrap) - the success handler will
                 * be invoked if the the dialog was closed and the "reject" handler will be invoked if the dialog was
                 * dismissed.
                 * @param dialogConfig
                 * @param dialogOptions
                 * @param loginOptions - options for "post login" processing, such as the target URL
                 */
                open: function(dialogConfig, dialogOptions) {

                    var modalOpts = angular.extend({
                            templateUrl: 'js/app/wishlist/templates/createwishlist.html',
                            controller: 'CreateWishListModalDialogCtrl',
                            resolve: {

                            }
                        }, dialogConfig || {});

                    if (dialogOptions && dialogOptions.required) {
                        modalOpts.keyboard = false;
                        modalOpts.backdrop = 'static';
                    }
                    return openDialog(modalOpts);
                },

                close: function() {
                    closeDialog();
                }
            };

        }
    ]);