require('br-presenter/_resources-test-at/html/test-form.html');
(function(){
	'use strict';

	require('jsmockito');

	var FellTest = TestCase('FellTest');
	var fell = require('fell');
	var Destination;
	var mockDestination;
	var TEST_MESSAGE = 'test message';
	var TEST_ARGUMENT_1 = 'this is argument 1';
	var TEST_ARGUMENT_2 = 'and over here is argument 2';


	FellTest.prototype.setUp = function() {

		JsHamcrest.Integration.JsTestDriver();
		JsMockito.Integration.JsTestDriver();

		Destination = function(){};
		Destination.prototype.onLog = function(){};

		mockDestination = mock(Destination);

		fell.configure('debug', {}, [mockDestination]);
	};

	FellTest.prototype.testLogConfiguredToFatal = function() {

		fell.configure('fatal', {}, [mockDestination]);

		fell.fatal(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		verify(mockDestination, once()).onLog(fell.DEFAULT_COMPONENT, 'fatal', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);

		fell.error(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		fell.warn(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		fell.info(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		fell.debug(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);

		verify(mockDestination, never()).onLog(fell.DEFAULT_COMPONENT, 'error');
		verify(mockDestination, never()).onLog(fell.DEFAULT_COMPONENT, 'warn');
		verify(mockDestination, never()).onLog(fell.DEFAULT_COMPONENT, 'info');
		verify(mockDestination, never()).onLog(fell.DEFAULT_COMPONENT, 'debug');
	};

	FellTest.prototype.testLogConfiguredToError = function() {

		fell.configure('error', {}, [mockDestination]);

		fell.fatal(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		verify(mockDestination, once()).onLog(fell.DEFAULT_COMPONENT, 'fatal', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);
		fell.error(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		verify(mockDestination, once()).onLog(fell.DEFAULT_COMPONENT, 'error', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);

		fell.warn(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		fell.info(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		fell.debug(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);

		verify(mockDestination, never()).onLog(fell.DEFAULT_COMPONENT, 'warn');
		verify(mockDestination, never()).onLog(fell.DEFAULT_COMPONENT, 'info');
		verify(mockDestination, never()).onLog(fell.DEFAULT_COMPONENT, 'debug');
	};

	FellTest.prototype.testLogConfiguredToWarn = function() {

		fell.configure('warn', {}, [mockDestination]);

		fell.fatal(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		verify(mockDestination, once()).onLog(fell.DEFAULT_COMPONENT, 'fatal', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);
		fell.error(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		verify(mockDestination, once()).onLog(fell.DEFAULT_COMPONENT, 'error', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);
		fell.warn(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		verify(mockDestination, once()).onLog(fell.DEFAULT_COMPONENT, 'warn', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);

		fell.info(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		fell.debug(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);

		verify(mockDestination, never()).onLog(fell.DEFAULT_COMPONENT, 'info');
		verify(mockDestination, never()).onLog(fell.DEFAULT_COMPONENT, 'debug');
	};

	FellTest.prototype.testLogConfiguredToInfo = function() {

		fell.configure('info', {}, [mockDestination]);

		fell.fatal(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		verify(mockDestination, once()).onLog(fell.DEFAULT_COMPONENT, 'fatal', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);
		fell.error(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		verify(mockDestination, once()).onLog(fell.DEFAULT_COMPONENT, 'error', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);
		fell.warn(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		verify(mockDestination, once()).onLog(fell.DEFAULT_COMPONENT, 'warn', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);
		fell.info(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		verify(mockDestination, once()).onLog(fell.DEFAULT_COMPONENT, 'info', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);

		fell.debug(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);

		verify(mockDestination, never()).onLog(fell.DEFAULT_COMPONENT, 'debug');
	};

	FellTest.prototype.testLogConfiguredToDebug = function() {

		fell.fatal(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		verify(mockDestination, once()).onLog(fell.DEFAULT_COMPONENT, 'fatal', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);
		fell.error(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		verify(mockDestination, once()).onLog(fell.DEFAULT_COMPONENT, 'error', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);
		fell.warn(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		verify(mockDestination, once()).onLog(fell.DEFAULT_COMPONENT, 'warn', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);
		fell.info(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		verify(mockDestination, once()).onLog(fell.DEFAULT_COMPONENT, 'info', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);
		fell.debug(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		verify(mockDestination, once()).onLog(fell.DEFAULT_COMPONENT, 'debug', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);
	};

	FellTest.prototype.testLogToSpecificLogger = function() {

		var loggerA = fell.getLogger('a');
		var loggerB = fell.getLogger('b');

		loggerA.debug(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);

		verify(mockDestination, once()).onLog('a', 'debug', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);
		verify(mockDestination, never()).onLog('b');
	};

	FellTest.prototype.testLogToMultipleDestinations = function() {

		var anotherDestination = mock(Destination);
		fell.configure('debug', {}, [mockDestination, anotherDestination]);

		fell.debug(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);

		verify(mockDestination, once()).onLog(fell.DEFAULT_COMPONENT, 'debug', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);
		verify(anotherDestination, once()).onLog(fell.DEFAULT_COMPONENT, 'debug', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);
	};

	FellTest.prototype.testConfiguringMultipleLogLevels = function() {

		fell.configure('warn', {
			'a': 'debug',
			'b': 'fatal'
		}, [mockDestination]);

		var loggerA = fell.getLogger('a');
		var loggerB = fell.getLogger('b');
		var loggerC = fell.getLogger('c');

		loggerA.debug(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		verify(mockDestination, once()).onLog('a', 'debug', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);

		loggerB.error(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		verify(mockDestination, never()).onLog('b', 'error', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);

		loggerB.fatal(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		verify(mockDestination, once()).onLog('b', 'fatal', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);

		loggerC.info(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		verify(mockDestination, never()).onLog('c', 'info', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);

		loggerC.warn(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		verify(mockDestination, once()).onLog('c', 'warn', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);
	};

	FellTest.prototype.testLoggerNamespacing = function() {

		fell.configure('warn', {
			'a': 'fatal',
			'b.a': 'debug'
		}, [mockDestination]);

		var loggerA = fell.getLogger('a');
		var loggerAChild = fell.getLogger('a.a');
		var loggerAA = fell.getLogger('aa');
		var loggerB = fell.getLogger('b');
		var loggerBChild = fell.getLogger('b.a');

		loggerA.error(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		loggerAChild.error(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		loggerAA.error(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		verify(mockDestination, never()).onLog('a', 'error');
		verify(mockDestination, never()).onLog('a.a', 'error');
		verify(mockDestination, once()).onLog('aa', 'error', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);

		loggerA.fatal(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		loggerAChild.fatal(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		verify(mockDestination, once()).onLog('a', 'fatal', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);
		verify(mockDestination, once()).onLog('a.a', 'fatal', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);

		loggerB.debug(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		loggerBChild.debug(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		verify(mockDestination, never()).onLog('b', 'debug');
		verify(mockDestination, once()).onLog('b.a', 'debug', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);
	};

	FellTest.prototype.testChangingLogLevel = function() {

		fell.configure('fatal', {}, [mockDestination]);

		fell.changeLevel('error');
		fell.error(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);

		verify(mockDestination, once()).onLog(fell.DEFAULT_COMPONENT, 'error', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);
	};

	FellTest.prototype.testAddingADestination = function() {

		fell.configure('fatal', {}, []);
		fell.addDestination(mockDestination);

		fell.fatal(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);

		verify(mockDestination, once()).onLog(fell.DEFAULT_COMPONENT, 'fatal', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);

	};

	FellTest.prototype.testRemovingADestination = function() {

		fell.configure('fatal', {}, [mockDestination]);
		fell.removeDestination(mockDestination);

		fell.fatal(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);

		verify(mockDestination, never()).onLog(fell.DEFAULT_COMPONENT, 'fatal', [TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2]);

	};

	FellTest.prototype.testLogStore = function() {

		var store = new fell.destination.LogStore(2);
		var loggerA = fell.getLogger('a');
		var anotherTestMessage = 'this is another test message';
		var yetAnotherTestMessage = 'and this is yet another';

		fell.configure('debug', {}, [store]);

		fell.fatal(TEST_MESSAGE, TEST_ARGUMENT_1, TEST_ARGUMENT_2);
		loggerA.debug(anotherTestMessage, TEST_ARGUMENT_1);
		fell.warn(yetAnotherTestMessage, TEST_ARGUMENT_2);

		var messages = store.allMessages();

		assertEquals(2, messages.length);
		assertEquals('debug', messages[0].level);
		assertEquals('a', messages[0].component);
		assertEquals('number', typeof messages[0].time);
		assertEquals([anotherTestMessage, TEST_ARGUMENT_1], messages[0].data);
		assertEquals('warn', messages[1].level);
		assertEquals(fell.DEFAULT_COMPONENT, messages[1].component);
		assertEquals('number', typeof messages[0].time);
		assertEquals([yetAnotherTestMessage, TEST_ARGUMENT_2], messages[1].data);
	};

}());
