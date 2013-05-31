'use strict';

var eventjugglerModule = angular.module('eventjugglerAdmin', [ 'eventjugglerAdminServices', 'ui.bootstrap' ]);
var resourceRequests = 0;

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
            realms : function(RealmsLoader) {
                return RealmsLoader();
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
            realms : function(RealmsLoader) {
                return RealmsLoader();
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
    }).when('/realms/:realmId/users/new', {
        templateUrl : 'partials/user-detail.html',
        resolve : {
            realms : function(RealmsLoader) {
                return RealmsLoader();
            },
            realm : function(RealmLoader) {
                return RealmLoader();
            },
            user : function() {
                return {};
            }
        },
        controller : UserDetailCtrl
    }).when('/realms/:realmId/users/:userId', {
        templateUrl : 'partials/user-detail.html',
        resolve : {
            realms : function(RealmsLoader) {
                return RealmsLoader();
            },
            realm : function(RealmLoader) {
                return RealmLoader();
            },
            user : function(UserLoader) {
                return UserLoader();
            }
        },
        controller : UserDetailCtrl
    }).when('/realms/:realmId/users', {
        templateUrl : 'partials/user-list.html',
        resolve : {
            realms : function(RealmsLoader) {
                return RealmsLoader();
            },
            realm : function(RealmLoader) {
                return RealmLoader();
            },
            users : function(UserListLoader) {
                return UserListLoader();
            }
        },
        controller : UserListCtrl
    }).when('/realms', {
        templateUrl : 'partials/realm-list.html',
        resolve : {
            realms : function(RealmsLoader) {
                return RealmsLoader();
            }
        },
        controller : RealmListCtrl
    }).otherwise({
        templateUrl : 'partials/home.html'
    });
} ]);

eventjugglerModule.config(function($httpProvider) {
    $httpProvider.responseInterceptors.push('errorInterceptor');

    var spinnerFunction = function(data, headersGetter) {
        if (resourceRequests == 0) {
            $('#loading').show();
        }
        resourceRequests++;
        return data;
    };
    $httpProvider.defaults.transformRequest.push(spinnerFunction);

    $httpProvider.responseInterceptors.push('spinnerInterceptor');

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

eventjugglerModule.factory('spinnerInterceptor', function($q, $window, $rootScope, $location) {
    return function(promise) {
        return promise.then(function(response) {
            resourceRequests--;
            if (resourceRequests == 0) {
                $('#loading').hide();
            }
            return response;
        }, function(response) {
            resourceRequests--;
            if (resourceRequests == 0) {
                $('#loading').hide();
            }

            return $q.reject(response);
        });
    };
});