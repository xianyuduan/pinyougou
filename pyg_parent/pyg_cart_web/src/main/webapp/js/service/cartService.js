app.service("cartService", function ($http) {
    this.addItemToCartList=function (itemId,num) {
        return $http.get("../cart/addItemToCartList/" + itemId + "/" + num);
    };
    
    this.findCartList=function () {
        return $http.get("../cart/findCartList")
    };

});