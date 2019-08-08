app.service("contentService", function ($http) {
    this.findAll=function () {
        return $http.get("../content/findAll")
    };
    this.findPage = function (page, size) {
        return $http.get('../content/findPage/' + page + '/' + size)
    };
    this.save = function (entity) {
        return $http.post("../content/add", entity)
    };
    this.findOne = function (id) {
        return $http.get("../content/findOne/" + id)
    };
    this.delete = function (ids) {
        return $http.get("../content/delete/" + ids)
    };
});