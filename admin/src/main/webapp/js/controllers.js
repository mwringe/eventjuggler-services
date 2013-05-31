'use strict';

function GlobalCtrl($scope, Auth, $location) {
    $scope.auth = Auth;

    $scope.$watch(function() {
        return $location.path();
    }, function() {
        $scope.path = $location.path().substring(1).split("/");
    });
}

function ActivitiesEventsCtrl($scope, events) {
    $scope.events = events;
}

function ActivitiesStatisticsCtrl($scope, statistics) {
    $scope.statistics = statistics;
}

function ApplicationListCtrl($scope, applications) {
    $scope.applications = applications;
}

function ApplicationDetailCtrl($scope, applications, application, Application, realms, providers, $location) {
    $scope.application = angular.copy(application);
    $scope.applications = applications;
    $scope.realms = realms;
    $scope.providers = providers;

    $scope.create = !application.key;

    $scope.changed = $scope.create;

    $scope.$watch('application', function() {
        if (!angular.equals($scope.application, application)) {
            $scope.changed = true;
        }
    }, true);

    $scope.save = function() {
        if ($scope.applicationForm.$valid) {
            if (!$scope.application.key) {
                Application.save($scope.application, function(data, headers) {
                    var l = headers().location;
                    var key = l.substring(l.lastIndexOf("/") + 1);
                    $location.url("/applications/" + key);
                });
            } else {
                Application.update($scope.application, function() {
                    $scope.changed = false;
                    application = angular.copy($scope.application);
                    if ($scope.create) {
                        $location.url("/applications/" + $scope.application.key);
                    }
                });
            }
        }
    };

    $scope.reset = function() {
        $scope.application = angular.copy(application);
        $scope.changed = false;
    };

    $scope.cancel = function() {
        $location.url("/applications");
    };

    $scope.remove = function() {
        $scope.application.$remove(function() {
            $location.url("/applications");
        });
    };

    $scope.availableProviders = [];

    $scope.addProvider = function() {
        if (!$scope.application.providers) {
            $scope.application.providers = [];
        }

        $scope.application.providers.push({
            "providerId" : $scope.newProviderId
        });

        $scope.newProviderId = null;
    };

    $scope.getProviderDescription = function(providerId) {
        for ( var i = 0; i < $scope.providers.length; i++) {
            if ($scope.providers[i].id == providerId) {
                return $scope.providers[i];
            }
        }
    };

    $scope.removeProvider = function(i) {
        $scope.application.providers.splice(i, 1);
    };

    var updateAvailableProviders = function() {
        $scope.availableProviders.splice(0, $scope.availableProviders.length);

        for ( var i in $scope.providers) {
            var add = true;

            for ( var j in $scope.application.providers) {
                if ($scope.application.providers[j].providerId == $scope.providers[i].id) {
                    add = false;
                    break;
                }
            }

            if (add) {
                $scope.availableProviders.push($scope.providers[i]);
            }
        }
    };

    $scope.$watch("providers.length + application.providers.length", updateAvailableProviders);
}

function RealmListCtrl($scope, realms) {
    $scope.realms = realms;
}

function UserListCtrl($scope, realms, realm, users) {
    $scope.realms = realms;
    $scope.realm = realm;
    $scope.users = users;
}

function UserDetailCtrl($scope, realms, realm, user, User, $location) {
    $scope.realms = realms;
    $scope.realm = realm;
    $scope.user = angular.copy(user);
    $scope.create = !user.userId;

    $scope.changed = $scope.create;

    $scope.$watch('user', function() {
        if (!angular.equals($scope.user, user)) {
            $scope.changed = true;
        }
    }, true);

    $scope.save = function() {
        if ($scope.userForm.$valid) {
            User.save({
                realmId : realm
            }, $scope.user, function() {
                $scope.changed = false;
                user = angular.copy($scope.user);

                if ($scope.create) {
                    $location.url("/realms/" + realm + "/users/" + user.userId);
                }
            });
        }
    };

    $scope.reset = function() {
        $scope.user = angular.copy(user);
        $scope.changed = false;
    };

    $scope.cancel = function() {
        $location.url("/realms/" + realm + "/users");
    };

    $scope.remove = function() {
        $scope.user.$remove({
            realmId : realm,
            userId : $scope.user.userId
        }, function() {
            $location.url("/realms/" + realm + "/users");
        });
    };
}