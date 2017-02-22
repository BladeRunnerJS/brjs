(function () {
    'use strict';

    require('jasmine');

    var originalConsoleLog = console.log;
    var AppController = require('@aspectRequirePrefix/AppController');
    var angular = require("angular")
    require("angular-mocks")

    angular.module('brApp', [])
        .controller('appController', ['$scope', function ($scope) {
            return new AppController($scope)
        }]);

    describe('App Tests', function () {
        beforeEach(function () {
            console.log = jasmine.createSpy('console.log');
        });

        afterEach(function () {
            console.log = originalConsoleLog;
        });

        beforeEach(module('brApp'));

        var $controller;

        beforeEach(inject(function (_$controller_) {
            // The injector unwraps the underscores (_) from around the parameter names when matching
            $controller = _$controller_;
        }));


        it('Should say hello', function () {
            AppController.logHello();
            expect(console.log).toHaveBeenCalledWith('hello world!');
        });

        describe('$scope.hello_world', function () {
            it('Hello world is set', function () {
                var $scope = {};
                var controller = $controller('appController', {$scope: $scope});
                expect($scope.hello_world).toEqual('Sucessfully loaded the application');
            });
        });
    });
}());
