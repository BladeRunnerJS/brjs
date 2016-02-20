(function () {
    'use strict';

    var AppTest = TestCase('AppTest');
    var AppController = require('@aspectRequirePrefix/AppController');

    AppTest.prototype.testSomething = function () {
        assertEquals('hello world!', AppController.getHello());
    };
}());
