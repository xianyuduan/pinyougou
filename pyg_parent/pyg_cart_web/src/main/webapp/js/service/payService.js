app.service("payService", function ($http) {
    this.createNative=function (entity) {
        return $http.get("../pay/createNative");
    };
    this.queryPayResult=function () {
        return $http.get("../pay/queryPayResult");
    };

});