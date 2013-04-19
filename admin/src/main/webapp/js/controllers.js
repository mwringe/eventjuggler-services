'use strict';

function WelcomeCtrl($scope) {
    $scope.message = "Hello world";
}

function UserCtrl($scope, Auth) {
    $scope.auth = Auth;
}

function ApplicationListCtrl($scope, Application, $routeParams) {
    $scope.applications = Application.query();
}

function ApplicationDetailCtrl($scope, Application, Provider, $routeParams, $location) {
    var navigationToApplications = function() {
        $location.url("/applications");
    };

    if ($routeParams.key == "new") {
        $scope.application = {};
        $scope.create = true;
    } else {
        $scope.application = Application.get({
            "key" : $routeParams.key
        });
    }

    $scope.providers = Provider.query();

    $scope.addProvider = function(providerId) {
        if (!$scope.application.providers) {
            $scope.application.providers = [];
        }

        $scope.application.providers.push({
            "providerId" : providerId
        });
    }

    $scope.getProviderDescription = function(providerId) {
        for ( var i = 0; i < $scope.providers.length; i++) {
            if ($scope.providers[i].id == providerId) {
                return $scope.providers[i];
            }
        }
    }

    $scope.removeProvider = function(i) {
        $scope.application.providers.splice(i, 1);
    }

    $scope.save = function() {
        if ($scope.create) {
            Application.save($scope.application, navigationToApplications);
        } else {
            Application.update({
                "key" : $scope.application.key
            }, $scope.application, navigationToApplications);
        }
    };

    $scope.cancel = function() {
        navigationToApplications();
    }

    $scope.remove = function() {
        $scope.application.$remove(navigationToApplications);
    }
}

function UserListCtrl($scope, User) {
    $scope.users = User.query();
}

function UserDetailCtrl($scope, Auth, User, $routeParams, $location) {
    var navigationToUsers = function() {
        if (Auth.loggedIn) {
            $location.url("/users");
        } else {
            $location.url("/");
        }
    };

    if ($routeParams.userId == "new") {
        $scope.user = {};
        $scope.create = true;
    } else {
        $scope.user = User.get({
            "userId" : $routeParams.userId
        });
    }

    $scope.save = function() {
        User.save({
            "userId" : $scope.user.userId
        }, $scope.user, navigationToUsers);
    };

    $scope.cancel = function() {
        navigationToUsers();
    }

    $scope.remove = function() {
        $scope.user.$remove(navigationToUsers);
    }
}