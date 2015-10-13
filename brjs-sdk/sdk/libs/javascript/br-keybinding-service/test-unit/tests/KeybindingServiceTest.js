(function() {
	'use strict';

	var Errors = require('br/Errors');

	var KeybindingService = require('br/KeybindingService');
	var Mousetrap = require('mousetrap');
	var keybindingService;

	var testCase = {
		setUp: function() {
			keybindingService = new KeybindingService();
		},

		'test registerAction registers the action without a shortcut key': function() {
			var cb = function() {};
			keybindingService.registerAction('action.foo', cb);

			var action = keybindingService.getAction('action.foo');
			assertTrue(typeof action !== 'undefined');
			assertEquals(cb, action.cb);
			assertEquals(null, action.keyShortcut);
		},

		'test registerAction assigns a callback to a previously binded action': function() {
			keybindingService.bindAction('action.foo', 'a');

			var cb = function() {};
			keybindingService.registerAction('action.foo', cb);

			var action = keybindingService.getAction('action.foo');
			assertTrue(typeof action !== 'undefined');
			assertEquals(cb, action.cb);
			assertEquals('a', action.keyShortcut);
		},

		'test same action cannot be registered twice': function() {
			keybindingService.registerAction('action.foo', function() {});

			assertException(
				function() {
					keybindingService.registerAction('action.foo', function() {});
				},
				Errors.INVALID_PARAMETERS
			);
		},

		'test removeAction removes the action': function() {
			keybindingService.registerAction('action.foo', function() {});

			var action = keybindingService.getAction('action.foo');
			assertTrue(typeof action !== 'undefined');

			keybindingService.removeAction('action.foo');

			action = keybindingService.getAction('action.foo');
			assertTrue(typeof action === 'undefined');
		},

		'test cannot remove action that does not exist': function() {
			assertException(
				function() {
					keybindingService.removeAction('action.foo');
				},
				Errors.INVALID_PARAMETERS
			);
		},

		'test bindAction creates an action if one does not exist yet': function() {
			keybindingService.bindAction('action.foo', 'a');

			var action = keybindingService.getAction('action.foo');
			assertTrue(typeof action !== 'undefined');
			assertEquals(null, action.cb);
			assertEquals('a', action.keyShortcut);
		},

		'test bindAction assigns a key shortcut to an registered action': function() {
			keybindingService.registerAction('action.foo', function() {});
			keybindingService.bindAction('action.foo', 'a');

			var action = keybindingService.getAction('action.foo');
			assertEquals('a', action.keyShortcut);
		},

		'test unbindAction removes the key shortcut from an action': function() {
			keybindingService.registerAction('action.foo', function() {});
			keybindingService.bindAction('action.foo', 'a');

			keybindingService.unbindAction('action.foo');

			var action = keybindingService.getAction('action.foo');
			assertEquals(null, action.keyShortcut);
		},

		'test a binded action will be triggerable after calling registerAction': function() {
			keybindingService.bindAction('action.foo', 'a');

			var wasCalled = false;
			keybindingService.registerAction('action.foo', function() {
				wasCalled = true;
			});

			Mousetrap.trigger('a');

			assertTrue(wasCalled);
		},

		'test a register action will be triggerable after calling bindAction': function() {
			var wasCalled = false;
			keybindingService.registerAction('action.foo', function() {
				wasCalled = true;
			});

			keybindingService.bindAction('action.foo', 'a');

			Mousetrap.trigger('a');

			assertTrue(wasCalled);
		},

		'test removed action will not be triggerable': function() {
			var wasCalled = false;
			keybindingService.registerAction('action.foo', function() {
				wasCalled = true;
			});
			keybindingService.bindAction('action.foo', 'a');

			keybindingService.removeAction('action.foo');

			Mousetrap.trigger('a');

			assertFalse(wasCalled);
		},

		'test unbinded action will not be triggerable': function() {
			var wasCalled = false;
			keybindingService.registerAction('action.foo', function() {
				wasCalled = true;
			});
			keybindingService.bindAction('action.foo', 'a');

			keybindingService.unbindAction('action.foo');

			Mousetrap.trigger('a');

			assertFalse(wasCalled);
		}
	};

	TestCase('KeybindingServiceTest', testCase);
}());
