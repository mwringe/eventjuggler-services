'use strict';

var eventjugglerServices = angular.module('eventjugglerAdminServices', [ 'ngResource' ]);

eventjugglerServices.factory('Application', function($resource) {
    return $resource('/ejs-identity/api/admin/applications/:key', {
        key : '@key'
    }, {
        update : {
            method : 'PUT'
        }
    });
});

eventjugglerServices.factory('Provider', function($resource) {
    return $resource('/ejs-identity/api/admin/providers');
});

eventjugglerServices.factory('User', function($resource) {
    return $resource('/ejs-identity/api/im/users/:userId', {
        userId : '@userId'
    }, {
        save : {
            method : 'PUT'
        }
    });
});