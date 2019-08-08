app.service("userService", function ($http) {
    this.sendSms = function (phone) {
        return $http.get("../user/sendSms/" + phone);
    };
    this.regist = function (code, entity) {
        return $http.post("../user/regist/" + code, entity);
    };

    //读取列表数据绑定到表单中
    this.findLoginUser=function(){
        return $http.get('../user/findLoginUser');
    }

});