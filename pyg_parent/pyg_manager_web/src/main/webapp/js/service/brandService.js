app.service("brandService", function ($http) {
    this.findAll=function () {
        return $http.get("../brand/findAll")
    };
    this.findPage = function (page, size) {
        return $http.get('../brand/findPage/' + page + '/' + size)
    };
    this.save = function (entity) {
        return $http.post("../brand/add", entity)
    };
    this.findOne = function (id) {
        return $http.get("../brand/findOne/" + id)
    };
    this.delete = function (ids) {
        return $http.get("../brand/delete/" + ids)
    }
});