app.service("userService", function ($http) {
    this.findLoginUser = function () {
        return $http.get('user/findLoginUser');
    }
});