(function(){
	'use strict';

	require( 'jasmine' );

	var originalConsoleLog = console.log;
	var @bladeTitleDirective = require('@bladeRequirePrefix/@bladeTitleDirective');
	var angular = require('angular');
	require("angular-mocks");

	var app = angular.module('brApp', [])
		.directive('testDefault', function() {
				return new @bladeTitleDirective();
		} );

	describe('@bladeTitle Tests', function() {

		beforeEach(function() {
			console.log = jasmine.createSpy('console.log');
		});

		var $compile,
			$rootScope;
		var el, scope, controller;

		beforeEach(module("brApp"));
		// Store references to $rootScope and $compile
		// so they are available to all tests in this describe block
		beforeEach(inject(function(_$compile_, _$rootScope_){
			// The injector unwraps the underscores (_) from around the parameter names when matching
			$compile = _$compile_;
			$rootScope = _$rootScope_;

			el = angular.element("<test-default></test-default>");


			$compile(el)($rootScope.$new());
			// fire all the watches, so the scope expression {{1 + 1}} will be evaluated
			$rootScope.$digest();

			// Grab controller instance
			controller = el.controller("testDefault");

    		// Grab scope. Depends on type of scope.
    		// See angular.element documentation.
			scope = el.isolateScope() || el.scope()
		}));



		afterEach(function() {
			console.log = originalConsoleLog;
		});

		it('Should log hello on load', function() {
			expect(console.log).toHaveBeenCalledWith('Welcome to your new Blade.');
		});

		it('Replaces the element with the appropriate content', function() {
			// Check that the compiled element contains the templated content
			expect(el.html()).toContain("<p class=\"welcome-message");
		});
	});
}());
