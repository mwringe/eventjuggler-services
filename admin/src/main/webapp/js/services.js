'use strict';

var eventjugglerServices = angular.module('eventjugglerAdminServices', [ 'ngResource' ]);

eventjugglerServices.factory('Application', function($resource) {
    return $resource('/identity-broker/admin/applications/:key', {
        key : '@key'
    }, {
        update : {
            method : 'PUT'
        }
    });
});

eventjugglerServices.factory('Provider', function($resource) {
    return $resource('/identity-broker/admin/providers');
});