'use strict';

var Log = require('../src/Log');
var mochito = require('mochito');

describe('Log class', function() {
	var log, store;

	beforeEach(function() {
		log = new Log();
		store = mochito.mock({
			onLog: function(){}
		});
	});

	it('can be used without throwing exceptions even if the log store has not been configured', function() {
		log.configure('info');
		log.info('hi');
	});

	describe('when newly created, and configured with level info', function() {
		beforeEach(function() {
			// given
			log.configure('info', {}, [store]);
		});

		it('makes all pertinent information available', function() {
			// when
			log.info('logging message at level {0}', '@info');

			// then
			mochito.verify(store, mochito.once()).onLog(log.DEFAULT_COMPONENT, 'info', ['logging message at level {0}', '@info']);
		});

		it('does not output debug level messages.', function() {
			// when
			log.Levels.forEach(function(level) {
				log[level]('some message');
			});

			// then
			mochito.verify(store, mochito.once()).onLog(mochito.anything(), 'fatal');
			mochito.verify(store, mochito.once()).onLog(mochito.anything(), 'error');
			mochito.verify(store, mochito.once()).onLog(mochito.anything(), 'warn');
			mochito.verify(store, mochito.once()).onLog(mochito.anything(), 'info');
			mochito.verify(store, mochito.never()).onLog(mochito.anything(), 'debug');
		});

		describe('and the level is changed to error,', function() {
			beforeEach(function() {
				log.changeLevel('error');
			});

			it('then only error and fatal messages are logged.', function() {
				// when
				log.Levels.forEach(function(level) {
					log[level]('some message');
				});

				// then
				mochito.verify(store, mochito.once()).onLog(mochito.anything(), 'fatal');
				mochito.verify(store, mochito.once()).onLog(mochito.anything(), 'error');
				mochito.verify(store, mochito.never()).onLog(mochito.anything(), 'warn');
				mochito.verify(store, mochito.never()).onLog(mochito.anything(), 'info');
				mochito.verify(store, mochito.never()).onLog(mochito.anything(), 'debug');
			});

			it('when the level is changed back, then the right messages are logged.', function() {
				// given
				log.changeLevel('info');

				// when
				log.Levels.forEach(function(level) {
					log[level]('some message');
				});

				// then
				mochito.verify(store, mochito.once()).onLog(mochito.anything(), 'fatal');
				mochito.verify(store, mochito.once()).onLog(mochito.anything(), 'error');
				mochito.verify(store, mochito.once()).onLog(mochito.anything(), 'warn');
				mochito.verify(store, mochito.once()).onLog(mochito.anything(), 'info');
				mochito.verify(store, mochito.never()).onLog(mochito.anything(), 'debug');
			});
		});

		it('will provide a logger for a particular component configured to the same log level.', function() {
			// given
			var logger = log.getLogger('test');

			// when
			logger.warn('hello at warn level');
			logger.debug('hello at debug level (should not be logged).');

			// then
			mochito.verify(store, mochito.once()).onLog(mochito.anything(), 'warn', ['hello at warn level']);
			mochito.verify(store, mochito.never()).onLog(mochito.anything(), 'debug');
		});

		describe('when a second destination is added', function() {
			var store2;

			beforeEach(function() {
				store2 = mochito.mock({
					onLog: function(){}
				});
				log.addDestination(store2);
			});

			it('then it should log to both.', function() {
				// when
				log.warn('Hello');

				// then
				mochito.verify(store, mochito.once()).onLog(mochito.anything(), 'warn', ['Hello']);
				mochito.verify(store2, mochito.once()).onLog(mochito.anything(), 'warn', ['Hello']);
			});

			it('and then the first is removed, it should only log to the new one.', function() {
				// given
				log.removeDestination(store);

				// when
				log.warn('Hello');

				// then
				mochito.verify(store, mochito.never()).onLog(mochito.anything(), 'warn', ['Hello']);
				mochito.verify(store2, mochito.once()).onLog(mochito.anything(), 'warn', ['Hello']);
			});
		});
	});

	describe('when configured with some components', function() {
		beforeEach(function() {
			log.configure('error', {
				'first.second.third': 'info',
				'first': 'fatal',
				'other.second': 'debug'
			}, [store]);
		});

		it('and a logger is requested for a child of a configured component, should provide a logger with the correct level set.', function() {
			// given
			var logger = log.getLogger('first.second.third.fourth');

			// when
			logger.info('hi');
			logger.debug('hi');

			// then
			mochito.verify(store, mochito.once()).onLog('first.second.third.fourth', 'info', ['hi']);
			mochito.verify(store, mochito.never()).onLog(mochito.anything(), 'debug');
		});

		it('and a logger is requested for a configured component, should provide a logger with the correct level set.', function() {
			// given
			var logger = log.getLogger('other.second');

			// when
			logger.debug('hi');

			// then
			mochito.verify(store, mochito.once()).onLog('other.second', 'debug', ['hi']);
		});

		it('and a logger is requested for a component in between configurations, should provide a logger with the correct level set.', function() {
			// given
			var logger = log.getLogger('first.second');

			// when
			logger.error('hi');
			logger.fatal('hi');

			// then
			mochito.verify(store, mochito.once()).onLog('first.second', 'fatal', ['hi']);
			mochito.verify(store, mochito.never()).onLog(mochito.anything(), 'error');
		});

		it('and a logger is requested for a component that doesn\'t match a configuration, should provide a logger with the correct default level.', function() {
			// given
			var logger = log.getLogger('firsty');

			// when
			logger.error('hi');
			logger.warn('hi');

			// then
			mochito.verify(store, mochito.once()).onLog('firsty', 'error', ['hi']);
			mochito.verify(store, mochito.never()).onLog(mochito.anything(), 'warn');
		});
	});
});
