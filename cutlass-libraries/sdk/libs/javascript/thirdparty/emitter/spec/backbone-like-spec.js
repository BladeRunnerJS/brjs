// A conversion of some of the backbone event tests.
// removed event maps, once, all, triggering many events at once,
//    listening to many events at once, chaining
// We can implement these features and tests later if required.

/*global require, describe, beforeEach, it, expect*/

var global = (function() {return this;})();
checkEmitterBehavesLikeBackbone(global.Emitter || require("../lib/Emitter"));

function checkEmitterBehavesLikeBackbone(Emitter) {
	describe('A backbone-like Emitter,', function() {
		var emitter;
		var MyEmitter;

		beforeEach(function() {
			MyEmitter = function() {
				this.counter = 0;
			};
			Emitter.mixInto(MyEmitter);
			emitter = new MyEmitter();
		});

		describe('with a listener registered for an event', function() {
			var otherAction;
			function callback() {
				this.counter++;
				otherAction();
			}

			beforeEach(function() {
				otherAction = function() {};
				emitter.on('event', callback);
			});

			it('should call the listener for every time the event is triggered', function() {
				emitter.trigger('event');
				expect(emitter.counter).toBe(1);
				emitter.trigger('event');
				emitter.trigger('event');
				emitter.trigger('event');
				emitter.trigger('event');
				expect(emitter.counter).toBe(5);
			});

			it('should stop calling the listener after off is called', function() {
				emitter.trigger('event');
				emitter.off('event');
				emitter.trigger('event');
				expect(emitter.counter).toBe(1);
			});

			it('should stop receiving events if it calls off during an event firing', function() {
				otherAction = function() {
					emitter.off('event', callback);
				};
				emitter.trigger('event');
				emitter.trigger('event');
				emitter.trigger('event');
				expect(emitter.counter).toBe(1);
			});

			it('works with nested triggers.', function() {
				function incr1() {
					this.counter++;
					this.off('event', incr1);
					this.trigger('event');
				}
				emitter.on('event', incr1);
				emitter.trigger('event');
				expect(emitter.counter).toBe(3);
			});

		});

		describe('with two callbacks registered,', function() {
			var callback;

			beforeEach(function() {
				callback = function() {
					this.counterA++;
				};

				emitter.counterA = 0;
				emitter.counterB = 0;

				emitter.on('event', callback);
				emitter.on('event', function() {
					this.counterB++;
				});
			});

			it('should fire both, then when one is unbound, it should fire just the one that is still bound', function() {
				emitter.trigger('event');
				emitter.off('event', callback);
				emitter.trigger('event');
				expect(emitter.counterA).toBe(1);
				expect(emitter.counterB).toBe(2);
			});

		});

		it('binds callbacks to contexts.', function() {
			var called = false;
			var TestClass = function() {};
			TestClass.prototype.wasCalled = function() {
				called = true;
			};
			emitter.on('event', function() {
				this.wasCalled();
			}, new TestClass());
			emitter.trigger('event');

			expect(called).toBe(true);
		});

		it('does not alter the callback list during an event trigger', function() {
			var counter = 0;
			function incr() {
				counter++;
			}
			emitter.on('event', function() {
				this.on('event', incr);
				this.on('event', incr);
			});
			emitter.trigger('event');

			expect(counter).toBe(0);

			emitter.off();

			emitter.on('event', function() {
				emitter.off('event', incr);
				emitter.off('event', incr);
			});

			emitter.on('event', incr);
			emitter.on('event', incr);

			emitter.trigger('event');

			expect(counter).toBe(2);
		});

		it('should allow all events for a specific context to be removed', function() {
			var otherContext = {counter: 0};
			function callback() {
				this.counter++;
			}
			emitter.on('x', callback);
			emitter.on('y', callback);
			emitter.on('x', callback, otherContext);
			emitter.on('y', callback, otherContext);
			emitter.off(null, null, otherContext);
			emitter.trigger('x');
			emitter.trigger('y');
			expect(emitter.counter).toBe(2);
			expect(otherContext.counter).toBe(0);
		});

		it('should remove consecutive events with off context', function() {
			var context = {
				called: false
			};
			function callback() {
				this.called = true;
			}
			emitter.on('event', callback, context);
			emitter.on('event', callback, context);
			emitter.off(null, null, context);
			emitter.trigger('event');
			expect(context.called).toBe(false);
		});

		it('with two listeners added with once, when and event is triggered once and then retriggered by one of them, each listener should have been called once.', function() {
			emitter.counterA = 0;
			emitter.counterB = 0;
			var incrA = function() {
				emitter.counterA += 1;
				emitter.trigger('event');
			};
			var incrB = function() {
				emitter.counterB += 1;
			};

			emitter.once('event', incrA);
			emitter.once('event', incrB);

			emitter.trigger('event');

			expect(emitter.counterA).toBe(1);
			expect(emitter.counterB).toBe(1);
		});

		describe('with a listener that counts invocations and two emitters,', function() {
			var counter, f, a, b;

			beforeEach(function() {
				counter = 0;
				f = function() {
					counter++;
				};

				a = new Emitter();
				b = new Emitter();
			});

			it('adding a listener with once shouldn\'t stop it working on a separate object with on.', function() {
				a.once('event', f);
				b.on('event', f);

				a.trigger('event');

				b.trigger('event');
				b.trigger('event');

				expect(counter).toBe(3);
			});

			it('adding a listener with once shouldn\'t stop it from working even on the same object and event with on', function() {
				a.once('event', f);
				a.on('event', f);

				a.trigger('event');
				a.trigger('event');

				expect(counter).toBe(3);
			});

			it('a listener added with once can still be removed with off before the event fires.', function() {
				a.once('event', f);
				a.off('event', f);
				a.trigger('event');

				expect(counter).toBe(0);
			});

			it('a listener added with once can be removed by context.', function() {
				var context = {};
				a.once('event', f, context);
				a.clearListeners(context);
				a.trigger('event');

				expect(counter).toBe(0);
			});

		});
	});
}