'use strict';

var eventjugglerModule = angular.module('eventjugglerAdmin', [ 'eventjugglerAdminServices' ]);

eventjugglerModule.config([ '$routeProvider', function($routeProvider) {
    $routeProvider.when('/applications/:key', {
        templateUrl : 'partials/application-detail.html',
        controller : ApplicationDetailCtrl
    }).when('/applications', {
        templateUrl : 'partials/application-list.html',
        controller : ApplicationListCtrl
    }).when('/users/:userId', {
        templateUrl : 'partials/user-detail.html',
        controller : UserDetailCtrl
    }).when('/users/:userId', {
        templateUrl : 'partials/user-detail.html',
        controller : UserDetailCtrl
    }).when('/users', {
        templateUrl : 'partials/user-list.html',
        controller : UserListCtrl
    }).otherwise({
        templateUrl : 'partials/home.html',
        controller : WelcomeCtrl
    });
} ]);

eventjugglerModule.config(function($httpProvider) {
    $httpProvider.responseInterceptors.push('errorInterceptor');
});

eventjugglerModule.factory('errorInterceptor', function($q, $window, $rootScope) {
    return function(promise) {
        return promise.then(function(response) {
            $rootScope.httpProviderError = null;
            return response;
        }, function(response) {
            $rootScope.httpProviderError = response.status;
            return $q.reject(response);
        });
    };
});