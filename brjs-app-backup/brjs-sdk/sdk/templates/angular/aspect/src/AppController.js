'use strict';

function AppController($scope) {
    $scope.hello_world = 'Sucessfully loaded the application';
}

AppController.getHello = function () {
    return 'hello world!';
};

AppController.logHello = function () {
    console.log(AppController.getHello());
};

module.exports = AppController;
