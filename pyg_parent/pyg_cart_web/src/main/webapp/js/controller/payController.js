app.controller("payController", function ($scope, payService, $controller) {
    $controller("baseController", {$scope: $scope});

    $scope.createNative = function () {
        //生成二维码
        payService.createNative().success(function (res) {
            var qr = new QRious({
                element:document.getElementById('qrious'),
                size:250,
                level:'H',
                value:res.code_url
            });

        });
        //查询支付结果
        payService.queryPayResult().success(function (res) {
            if(res.success) {
                location.href="paysuccess.html";
            }else {
                location.href="payfail.html"
            }
        })
    };

});