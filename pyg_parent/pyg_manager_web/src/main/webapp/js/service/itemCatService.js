app.service("itemCatService", function ($http) {
    this.findAll=function () {
        return $http.get("../itemCat/findAll")
    };
    this.findByParentId = function (id) {
        return $http.get('../itemCat/findByParentId/' + id)
    };
    this.save = function (entity) {
        return $http.post("../itemCat/add", entity)
    };
    this.findOne = function (id) {
        return $http.get("../itemCat/findOne/" + id)
    };
    this.delete = function (ids) {
        return $http.get("../itemCat/delete/" + ids)
    }
});