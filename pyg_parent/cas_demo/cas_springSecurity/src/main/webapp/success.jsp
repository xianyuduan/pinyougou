<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Insert title here</title>
    <script src="./plugins/angularjs/angular.min.js"></script>

    <script src="./js/base.js"></script>
    <script src="./js/service/userService.js"></script>
    <script src="./js/controller/baseController.js"></script>
    <script src="./js/controller/userController.js"></script>
</head>
<body ng-app="pyg" ng-controller="userController" ng-init="findLoginUser()">
success html-{{loginUser.username}}
<br>
<a href="/logout/cas">退出</a>

</body>
</html>
