app.service("orderService", function ($http) {
    this.saveOrder=function (upOrder) {
        return $http.post("../order/saveOrder", upOrder);
    };

});