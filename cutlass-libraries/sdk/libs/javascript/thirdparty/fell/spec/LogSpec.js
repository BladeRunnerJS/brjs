describe('A Log object', function(){
	var global = (function() {return this;})();
	var JsHamcrest = global.JsHamcrest || require('jshamcrest').JsHamcrest;
	JsHamcrest.Integration.jasmine();

	var fell = global.fell || require("../lib/fell");
	var Log = fell.Log;
	var LogStore = fell.destination.LogStore;

	var store;

	beforeEach(function() {
		store = new LogStore();
	});

	describe('when newly created, and configured with level info', function() {
		beforeEach(function() {
			Log.configure("info", {}, [store]);
		});

		it('does not output debug level messages.', function() {
			Log.Levels.forEach(function(level) {
				Log[level]("logging message at level {0}", level);
			});

			assertThat(store, LogStore.containsAll(
					LogStore.event('fatal', Log.DEFAULT_COMPONENT, ["logging message at level {0}", 'fatal']),
					LogStore.event('error', Log.DEFAULT_COMPONENT, ["logging message at level {0}", 'error']),
					LogStore.event('warn', Log.DEFAULT_COMPONENT, ["logging message at level {0}", 'warn']),
					LogStore.event('info', Log.DEFAULT_COMPONENT, ["logging message at level {0}", 'info'])
			));

			assertThat(store, not(LogStore.contains(LogStore.event('debug'))));
		});

		describe('and the level is changed to error,', function() {
			it('then only error and fatal messages are logged.', function() {
				Log.changeLevel('error');

				Log.Levels.forEach(function(level) {
					Log[level]("logging message at level {0}", level);
				});

				assertThat(store, LogStore.containsAll(
						LogStore.event('fatal'),
						LogStore.event('error')
				));

				assertThat(store, not(LogStore.containsAny(
						LogStore.event('info'),
						LogStore.event('warn'),
						LogStore.event('debug')
				)));
			});

			it('when the level is changed back, then the right messages are logged.', function() {
				Log.changeLevel('info');

				Log.Levels.forEach(function(level) {
					Log[level]("logging message at level {0}", level);
				});

				assertThat(store, LogStore.containsAll(
						LogStore.event('fatal', Log.DEFAULT_COMPONENT, ["logging message at level {0}", 'fatal']),
						LogStore.event('error', Log.DEFAULT_COMPONENT, ["logging message at level {0}", 'error']),
						LogStore.event('warn', Log.DEFAULT_COMPONENT, ["logging message at level {0}", 'warn']),
						LogStore.event('info', Log.DEFAULT_COMPONENT, ["logging message at level {0}", 'info'])
				));

				assertThat(store, not(LogStore.contains(LogStore.event('debug'))));
			});
		});

		it('will provide a logger for a particular component configured to the same log level.', function() {
			var log = Log.getLogger("test");
			log.warn("hello at warn level");
			log.debug("hello at debug level (should not be logged).");

			assertThat(store, LogStore.contains(
					LogStore.event('warn', "test")
			));
			assertThat(store, not(LogStore.contains(LogStore.event('debug'))));
		});

		describe('when a second destination is added', function() {
			var newStore;
			beforeEach(function() {
				newStore = new LogStore();
				Log.addDestination(newStore);
			});

			it('then it should log to both.', function() {
				Log.warn("Hello");

				var aDefaultWarnHelloLogEvent = LogStore.contains(LogStore.event("warn", Log.DEFAULT_COMPONENT, ["Hello"]));
				assertThat(newStore, aDefaultWarnHelloLogEvent);
				assertThat(store, aDefaultWarnHelloLogEvent);
			});

			it('and then the first is removed, it should only log to the new one.', function() {
				Log.removeDestination(store);

				Log.warn("Hello");

				var aDefaultWarnHelloLogEvent = LogStore.contains(LogStore.event("warn", Log.DEFAULT_COMPONENT, ["Hello"]));
				assertThat(store, not(aDefaultWarnHelloLogEvent));
				assertThat(newStore, aDefaultWarnHelloLogEvent);
			});
		});
	});

	describe('when configured with some components', function() {
		beforeEach(function() {
			Log.configure("error", {
				"first.second.third": 'info',
				"first": "fatal",
				"other.second": "debug"
			}, [store]);
		});

		it('and a logger is requested for a child of a configured component, should provide a logger with the correct level set.', function() {
			var log = Log.getLogger('first.second.third.fourth');
			log.info('hi');
			log.debug('hi');

			assertThat(store, LogStore.contains(
					LogStore.event('info', 'first.second.third.fourth')
			));
			assertThat(store, not(LogStore.contains(LogStore.event('debug'))));
		});

		it('and a logger is requested for a configured component, should provide a logger with the correct level set.', function() {
			var log = Log.getLogger('other.second');
			log.debug('hi');

			assertThat(store, LogStore.contains(
					LogStore.event('debug', 'other.second')
			));
		});

		it('and a logger is requested for a component in between configurations, should provide a logger with the correct level set.', function() {
			var log = Log.getLogger('first.second');
			log.error('hi');
			log.fatal('hi');

			assertThat(store, LogStore.contains(
					LogStore.event('fatal', 'first.second')
			));
			assertThat(store, not(LogStore.contains(LogStore.event('error'))));
		});

		it('and a logger is requested for a component that doesn\'t match a configuration, should provide a logger with the correct default level.', function() {
			var log = Log.getLogger('firsty');
			log.error('hi');
			log.warn('hi');

			assertThat(store, LogStore.contains(
					LogStore.event('error', 'firsty')
			));
			assertThat(store, not(LogStore.contains(LogStore.event('warn'))));
		});
	});
});



