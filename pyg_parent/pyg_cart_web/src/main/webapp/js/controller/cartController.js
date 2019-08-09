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
    $scope.totalValue={};
    //查询购物车货物
    $scope.findCartList=function () {
        $scope.totalFee=0;//总金额
        $scope.totalNum=0;//总件数
        cartService.findCartList().success(function (res) {
            $scope.cartList=res;
            $scope.totalValue = cartService.sum($scope.cartList);
        })
    };

    $scope.cartList=[];
    //查询已提交的临时订单，从redis中取，结算页显示信息，同时对购物车内容进行合并
    $scope.findUpdataCartList=function () {
        //对服务端的购物车进行合并
        cartService.findCartList();
        //通过url中的参数，生成结算页列表
        cartService.findUpdataCartList().success(function (response) {
            //页面变量赋值
            $scope.cartList = response;
            $scope.totalValue = cartService.sum($scope.cartList);

        });
    }

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
        $scope.upOrder={tbOrder:{},cartList:[]};
        $scope.upOrder.tbOrder=$scope.entity;
        $scope.upOrder.cartList=$scope.cartList;
        orderService.saveOrder($scope.upOrder).success(function (res) {
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
    $scope.cartListUp=[];
    //商品选中
    $scope.updateSelection=function ($event,orderItem) {
        if($event.target.checked){
            $scope.cartListUp.push(orderItem);
        }else {
            $scope.cartListUp.splice($scope.cartListUp.indexOf(orderItem),1);
        }
    }

    //upOrderItem(cartListUp) //upOrderItem 购物车结算提交
    $scope.upOrderItem=function () {
        cartService.upOrderItem($scope.cartListUp).success(function (response) {
            if (response.success){
                var s = angular.toJson($scope.cartListUp);
                location.href="getOrderInfo.html#?cartListUp="+s;
            }else {
                alert("提交失败")
            }

        });
    }



});