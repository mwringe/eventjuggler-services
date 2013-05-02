'use strict';

var eventjugglerModule = angular.module('eventjugglerAdmin', [ 'eventjugglerAdminServices' ]);

eventjugglerModule.config([ '$routeProvider', function($routeProvider) {
    $routeProvider.when('/activities', {
        templateUrl : 'partials/activities.html',
        controller : ActivitiesCtrl
    }).when('/applications/new', {
        templateUrl : 'partials/application-detail.html',
        resolve : {
            application : function() {
                return {};
            }
        },
        controller : ApplicationDetailCtrl
    }).when('/applications/:key', {
        templateUrl : 'partials/application-detail.html',
        resolve : {
            application : function(ApplicationLoader) {
                return ApplicationLoader();
            }
        },
        controller : ApplicationDetailCtrl
    }).when('/applications', {
        templateUrl : 'partials/application-list.html',
        resolve : {
            applications : function(ApplicationListLoader) {
                return ApplicationListLoader();
            }
        },
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
        templateUrl : 'partials/home.html'
    });
} ]);

eventjugglerModule.config(function($httpProvider) {
    $httpProvider.responseInterceptors.push('errorInterceptor');
});

eventjugglerModule.factory('errorInterceptor', function($q, $window, $rootScope, $location) {
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