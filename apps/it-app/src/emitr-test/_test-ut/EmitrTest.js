require('br-presenter/_resources-test-at/html/test-form.html');
(function(){
	'use strict';

	require('jsmockito');

	var EmitrTest = TestCase('EmitrTest');
	var emitr = require('emitr');
	var testEmitter;
	var mockTestListener;
	var spyTestFunction;
	var anotherSpyTestFunction;
	var argument1 = 1;
	var argument2 = 'a';
	var argument3 = {a: 'hello'};

	EmitrTest.prototype.setUp = function() {

		JsHamcrest.Integration.JsTestDriver();
		JsMockito.Integration.JsTestDriver();

		function TestEmitter() {}
		emitr.mixInto(TestEmitter);
		testEmitter = new TestEmitter();

		function TestListener() {}
		TestListener.prototype.onTestEvent = function() {};
		mockTestListener = mock(TestListener);

		spyTestFunction = spy(function() {});
		anotherSpyTestFunction = spy(function() {});
	};

	EmitrTest.prototype.testOnWithoutContext = function() {

		testEmitter.on('test-event', spyTestFunction);

		testEmitter.trigger('test-event', argument1, argument2, argument3);

		verify(spyTestFunction, once())(argument1, argument2, argument3);
	};

	EmitrTest.prototype.testOnWithContext = function() {

		testEmitter.on('test-event', mockTestListener.onTestEvent, mockTestListener);

		testEmitter.trigger('test-event', argument1, argument2, argument3);

		verify(mockTestListener, once()).onTestEvent(argument1, argument2, argument3);
	};

	EmitrTest.prototype.testOffWithoutContext = function() {

		testEmitter.on('test-event', spyTestFunction);
		testEmitter.off('test-event', spyTestFunction);

		testEmitter.trigger('test-event', argument1, argument2, argument3);

		verify(spyTestFunction, never())();
	};

	EmitrTest.prototype.testOffWithContext = function() {

		testEmitter.on('test-event', mockTestListener.onTestEvent, mockTestListener);
		testEmitter.off('test-event', mockTestListener.onTestEvent, mockTestListener);

		testEmitter.trigger('test-event', argument1, argument2, argument3);

		verify(mockTestListener, never()).onTestEvent();
	};

	EmitrTest.prototype.testOffWithDifferentFunctionDoesNotClearListener = function() {

		testEmitter.on('test-event', spyTestFunction);
		testEmitter.on('test-event', anotherSpyTestFunction);
		testEmitter.off('test-event', anotherSpyTestFunction);

		testEmitter.trigger('test-event', argument1, argument2, argument3);

		verify(spyTestFunction, once())(argument1, argument2, argument3);
		verify(anotherSpyTestFunction, never())();
	};

	EmitrTest.prototype.testOffWithNoFunctionClearsAllListenersForEvent = function() {

		testEmitter.on('test-event', spyTestFunction);
		testEmitter.on('test-event', mockTestListener.onTestEvent, mockTestListener);
		testEmitter.on('another-test-event', anotherSpyTestFunction);
		testEmitter.off('test-event');

		testEmitter.trigger('test-event', argument1, argument2, argument3);
		testEmitter.trigger('another-test-event', argument1, argument2, argument3);

		verify(spyTestFunction, never())();
		verify(mockTestListener, never()).onTestEvent();
		verify(anotherSpyTestFunction, once())(argument1, argument2, argument3);
	};

	EmitrTest.prototype.testOffWithNoArgumentsClearsAllListeners = function() {

		testEmitter.on('test-event', spyTestFunction);
		testEmitter.on('test-event', mockTestListener.onTestEvent, mockTestListener);
		testEmitter.on('another-test-event', anotherSpyTestFunction);
		testEmitter.off();

		testEmitter.trigger('test-event', argument1, argument2, argument3);
		testEmitter.trigger('another-test-event', argument1, argument2, argument3);

		verify(spyTestFunction, never())();
		verify(mockTestListener, never()).onTestEvent();
		verify(anotherSpyTestFunction, never())();
	};

	EmitrTest.prototype.testAddListenerMetaEvent = function() {

		testEmitter.on(emitr.meta.AddListenerEvent, function(addEvent) {
			spyTestFunction(addEvent.listener, addEvent.context);
		}, this);

		testEmitter.on('test-event',  mockTestListener.onTestEvent, mockTestListener);

		verify(spyTestFunction, once())(mockTestListener.onTestEvent, mockTestListener);
	};
}());
