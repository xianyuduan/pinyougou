app.controller("cartController", function ($scope, cartService, $controller,$location,addressService,orderService) {
    $controller("baseController", {$scope: $scope});
    $scope.itemId=$location.search().itemId;
    $scope.num=$location.search().num;
    //添加货物到购物车
    $scope.addItemToCartList=function () {
        cartService.addItemToCartList($scope.itemId,$scope.num).success(function (res) {
            //res:{success:'',message:''}
            $scope.resultMap=res;
        })
    };

    //查询购物车货物
    $scope.findCartList=function () {
        $scope.totalFee=0;//总金额
        $scope.totalNum=0;//总件数
        cartService.findCartList().success(function (res) {
            $scope.cartList=res;
            for(var i=0;i<res.length;i++) {
                var orderItemList=res[i].orderItemList;
                for(var j=0;j<orderItemList.length;j++) {
                    $scope.totalFee+=orderItemList[j].totalFee;
                    $scope.totalNum+=orderItemList[j].num;
                }
            }
        })
    };

    //加减货物数量
    $scope.addNum=function (itemId,num) {
        cartService.addItemToCartList(itemId,num).success(function (res) {
            if(res.success) {
                $scope.findCartList();
            }else {
                alert(res.message);
            }
        })
    };

    //查询收件人地址

    $scope.findAddress=function () {
        addressService.findAddress().success(function (res) {
            $scope.addressList=res;
            //存储默认地址
            for(var i=0;i<res.length;i++) {
                if("1"==res[i].isDefault){
                    $scope.defaultAddress = res[i];
                }
            }
        })
    };

    //选择地址
    $scope.selectAddress=function (address) {
        $scope.defaultAddress=address;
    };
    //选择付款方式
    $scope.entity = {paymentType:"1"};
    $scope.selectPayType=function (type) {
        $scope.entity.paymentType=type;
    };


    //提交订单
    $scope.saveOrder=function () {
        $scope.entity.receiverAreaName=$scope.defaultAddress.address;//地址
        $scope.entity.receiverMobile=$scope.defaultAddress.mobile;//手机
        $scope.entity.receiver=$scope.defaultAddress.contact;//联系人
        orderService.saveOrder($scope.entity).success(function (res) {
            if(res.success) {
                if($scope.entity.paymentType=="1"){
                    location.href="pay.html";
                }else {
                    location.href="paysuccess.html";
                }
            }else {
                location.href="payfail.html";
            }
        })
    }
});