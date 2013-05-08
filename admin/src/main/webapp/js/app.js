'use strict';

var eventjugglerModule = angular.module('eventjugglerAdmin', [ 'eventjugglerAdminServices', 'ui.bootstrap' ]);

eventjugglerModule.config([ '$routeProvider', function($routeProvider) {
    $routeProvider.when('/activities/events', {
        templateUrl : 'partials/activities-events.html',
        controller : ActivitiesCtrl
    }).when('/activities/pages', {
        templateUrl : 'partials/activities-pages.html',
        controller : ActivitiesCtrl
    }).when('/activities', {
        templateUrl : 'partials/activities-statistics.html',
        controller : ActivitiesCtrl
    }).when('/applications/:key', {
        templateUrl : 'partials/application-detail.html',
        controller : ApplicationDetailCtrl
    }).when('/applications', {
        templateUrl : 'partials/application-list.html'
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