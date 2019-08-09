app.service("cartService", function ($http) {
    this.addItemToCartList=function (itemId,num) {
        return $http.get("../cart/addItemToCartList/" + itemId + "/" + num);
    };
    
    this.findCartList=function () {
        return $http.get("../cart/findCartList")
    };
    //upOrderItem 将购物车选中提交的商品，存入临时提交订单，入redis
    this.upOrderItem=function(cartListUp){
        return $http.post('../cart/upOrderItem',cartListUp);
    }
    // findUpdataCartList 查询已提交的临时订单，从redis中取，以购物车List<cart>的形式返回
    this.findUpdataCartList=function(){
        return $http.get('../cart/findUpdataCartList');
    }
    //求合计
    this.sum=function(cartList){
        var totalValue={totalNum:0, totalMoney:0.00 };//合计实体
        for(var i=0;i<cartList.length;i++){
            var cart=cartList[i];
            for(var j=0;j<cart.orderItemList.length;j++){
                var orderItem=cart.orderItemList[j];//购物车明细
                totalValue.totalNum+=orderItem.num;
                totalValue.totalMoney+= orderItem.totalFee;
            }
        }
        return totalValue;
    }
});