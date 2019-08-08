app.service("typeTemplateService", function ($http) {
    this.findAll=function () {
        return $http.get("../typeTemplate/findAll")
    };
    this.findPage = function (page, size) {
        return $http.get('../typeTemplate/findPage/' + page + '/' + size)
    };
    this.save = function (entity) {
        return $http.post("../typeTemplate/add", entity)
    };
    this.findOne = function (id) {
        return $http.get("../typeTemplate/findOne/" + id)
    };
    this.delete = function (ids) {
        return $http.get("../typeTemplate/delete/" + ids)
    }
});