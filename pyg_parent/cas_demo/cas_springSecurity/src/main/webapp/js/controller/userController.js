app.controller("userController", function ($scope,userService, $controller) {
    $controller("baseController", {$scope: $scope});

    $scope.loginUser = {};
    $scope.findLoginUser = function () {
        userService.findLoginUser().success(function (res) {
            $scope.loginUser = res;
        })
    }
});