'use strict';

var Mousetrap = require('mousetrap');
var Errors = require('br/Errors');

/**
 * @module br/KeybindingService
 */

/**
 * @classdesc
 * A service that allows the binding of keyboard keys (shortcuts) to actions. The service is available under the alias
 *  `br.keybinding-service` and should be obtain trough the service registry and not constructed. This service uses the
 *  Mousetrap library to handle keyboard bindings. Refer to it's documentation to see what type of keys are supported.
 *
 * @class
 *
 * @see {@link http://craig.is/killing/mice}
 */
function KeybindingService() {
	this._actions = {};
}

/**
 * Register an action that can then be binded to a shortcut key. This method will typically be used by a component that
 *  wishes to provide some functionality that will be invoked with a keyboard shortcut.
 * @param {String} actionId A unique string ID of the action. It is recommended that the ID is prefixed with the
 *  namespace of the component.
 * @param {Function} actionCb The function that will be invoked for this action.
 * @throws {module:br/Errors#InvalidParametersError} If trying to add an action that was already added.
 */
KeybindingService.prototype.registerAction = function(actionId, actionCb) {
	// only throw if this action was added with a `registerAction` (not internally in `bindAction`)
	if (typeof this._actions[actionId] !== 'undefined' && this._actions[actionId].cb !== null) {
		throw new Errors.InvalidParametersError('Action with ID "' + actionId + '" already exists.');
	}

	if (typeof this._actions[actionId] === 'undefined') {
		this._actions[actionId] = {
			cb: actionCb,
			keyShortcut: null
		};
	} else {
		this._actions[actionId].cb = actionCb;

		Mousetrap.bind(this._actions[actionId].keyShortcut, this._actions[actionId].cb);
	}
};

/**
 * Remove the action.
 * @param {String} actionId]
 * @throws {module:br/Errors#InvalidParametersError} If trying to remove a non existing action.
 */
KeybindingService.prototype.removeAction = function(actionId) {
	if (typeof this._actions[actionId] === 'undefined') {
		throw new Errors.InvalidParametersError('Action with ID "' + actionId + '" does not exists.');
	}

	if (this._actions[actionId].keyShortcut !== null) {
		Mousetrap.unbind(this._actions[actionId].keyShortcut);
	}

	delete this._actions[actionId];
};

/**
 * Bind an action to a keyboard shortcut. This method will typically be used by the app code.
 * @param {String} actionId Action to bind.
 * @param {String|Array} keyShortcut Refer to Mousetrap's documentation to see what keys are supported.
 */
KeybindingService.prototype.bindAction = function(actionId, keyShortcut) {
	if (typeof this._actions[actionId] === 'undefined') {
		this._actions[actionId] = {
			cb: null,
			keyShortcut: keyShortcut
		};
	} else {
		if (this._actions[actionId].keyShortcut !== null) {
			Mousetrap.unbind(this._actions[actionId].keyShortcut);
		}

		this._actions[actionId].keyShortcut = keyShortcut;
		Mousetrap.bind(keyShortcut, this._actions[actionId].cb);
	}
};

/**
 * Unbind an action from a keyboard shortcut. The action will not be removed. To remove it completely `removeAtion`
 *  should be used.
 * @param {String} actionId Action to remove.
 */
KeybindingService.prototype.unbindAction = function(actionId) {
	if (typeof this._actions[actionId] === 'undefined' || this._actions[actionId].keyShortcut === null) {
		return;
	}

	Mousetrap.unbind(this._actions[actionId].keyShortcut);
	this._actions[actionId].keyShortcut = null;
};

/**
 * Get the information about a registered action.
 * @param {String} actionId Action to return.
 * @return {undefined|Object}
 */
KeybindingService.prototype.getAction = function(actionId) {
	return this._actions[actionId];
};

module.exports = KeybindingService;
