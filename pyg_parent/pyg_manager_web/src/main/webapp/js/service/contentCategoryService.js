app.service("contentCategoryService", function ($http) {
    this.findAll=function () {
        return $http.get("../contentCategory/findAll")
    };
    this.findPage = function (page, size) {
        return $http.get('../contentCategory/findPage/' + page + '/' + size)
    };
    this.save = function (entity) {
        return $http.post("../contentCategory/add", entity)
    };
    this.findOne = function (id) {
        return $http.get("../contentCategory/findOne/" + id)
    };
    this.delete = function (ids) {
        return $http.get("../contentCategory/delete/" + ids)
    };
});