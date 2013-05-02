'use strict';

function UserCtrl($scope, Auth) {
    $scope.auth = Auth;
}

function ActivitiesCtrl($scope, Activities) {
    $scope.events = Activities.events.query({
        "max" : 10
    });
    $scope.statistics = Activities.statistics.get();
}

function ApplicationListCtrl($scope, applications) {
    $scope.applications = applications;
}

function ApplicationDetailCtrl($scope, application, providers, Application, $location) {
    var navigationToApplications = function() {
        $location.url("/applications");
    };

    $scope.application = application;
    $scope.providers = providers;

    $scope.create = !$scope.application.key;

    $scope.save = function() {
        if (!$scope.application.key) {
            Application.save($scope.application, navigationToApplications);
        } else {
            application.$update(navigationToApplications);
        }
    };

    $scope.cancel = function() {
        navigationToApplications();
    };

    $scope.remove = function() {
        $scope.application.$remove(navigationToApplications);
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

function UserListCtrl($scope, users) {
    $scope.users = users;
}

function UserDetailCtrl($scope, Auth, user, User, $routeParams, $location) {
    var navigationToUsers = function() {
        if (Auth.loggedIn) {
            $location.url("/users");
        } else {
            $location.url("/");
        }
    };

    $scope.user = user;
    $scope.create = !user.userId;

    $scope.save = function() {
        User.save($scope.user, navigationToUsers);
    };

    $scope.cancel = function() {
        navigationToUsers();
    };

    $scope.remove = function() {
        $scope.user.$remove(navigationToUsers);
    };
}