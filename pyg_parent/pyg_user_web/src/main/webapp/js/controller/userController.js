app.controller("userController", function ($scope, userService, $controller) {
    $controller("baseController", {$scope: $scope});

    $scope.entity = {};
    //获取验证码
    $scope.sendSms = function () {
        if(undefined==$scope.entity.phone||""==$scope.entity.phone){
            alert("请输入手机号");
            return;
        }
        userService.sendSms($scope.entity.phone).success(function (res) {
            alert(res.message);
        })
    };

    //注册
    $scope.regist=function () {
        userService.regist($scope.code, $scope.entity).success(function (res) {
            if(res.success) {
                location.href = "login.html";
            }else {
                alert(res.message);
            }
        })
    };

    //回显登录用户姓名
    $scope.findLoginUser=function(){
        userService.findLoginUser().success(
            function(response){
                $scope.loginName=response.loginName;
            }
        );
    }

});