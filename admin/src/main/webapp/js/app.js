'use strict';

var eventjugglerModule = angular.module('eventjugglerAdmin', [ 'eventjugglerAdminServices', 'ui.bootstrap' ]);

eventjugglerModule.config([ '$routeProvider', function($routeProvider) {
    $routeProvider.when('/activities/events', {
        templateUrl : 'partials/activities-events.html',
        resolve : {
            events : function(ActivitiesEventsLoader) {
                return ActivitiesEventsLoader();
            }
        },
        controller : ActivitiesEventsCtrl
    }).when('/activities/pages', {
        templateUrl : 'partials/activities-pages.html',
        resolve : {
            statistics : function(ActivitiesStatisticsLoader) {
                return ActivitiesStatisticsLoader();
            }
        },
        controller : ActivitiesStatisticsCtrl
    }).when('/activities', {
        templateUrl : 'partials/activities-statistics.html',
        resolve : {
            statistics : function(ActivitiesStatisticsLoader) {
                return ActivitiesStatisticsLoader();
            }
        },
        controller : ActivitiesStatisticsCtrl
    }).when('/applications/new', {
        templateUrl : 'partials/application-detail.html',
        resolve : {
            applications : function(ApplicationListLoader) {
                return ApplicationListLoader();
            },
            application : function() {
                return {};
            },
            providers : function(ProviderListLoader) {
                return ProviderListLoader();
            }
        },
        controller : ApplicationDetailCtrl
    }).when('/applications/:key', {
        templateUrl : 'partials/application-detail.html',
        resolve : {
            applications : function(ApplicationListLoader) {
                return ApplicationListLoader();
            },
            application : function(ApplicationLoader) {
                return ApplicationLoader();
            },
            providers : function(ProviderListLoader) {
                return ProviderListLoader();
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
    }).when('/users/new', {
        templateUrl : 'partials/user-detail.html',
        resolve : {
            user : function() {
                return {};
            }
        },
        controller : UserDetailCtrl
    }).when('/users/:userId', {
        templateUrl : 'partials/user-detail.html',
        resolve : {
            user : function(UserLoader) {
                return UserLoader();
            }
        },
        controller : UserDetailCtrl
    }).when('/users', {
        templateUrl : 'partials/user-list.html',
        resolve : {
            users : function(UserListLoader) {
                return UserListLoader();
            }
        },
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