app.service("indexService", function ($http) {
    this.findLoginUser=function () {
        return $http.get("../login/findLoginUser")
    }
});