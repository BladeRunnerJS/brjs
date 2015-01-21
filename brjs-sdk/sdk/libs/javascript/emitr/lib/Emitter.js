"use strict";

var slice = Array.prototype.slice;

var metaEvents = require('./events');
var MultiMap = require('./MultiMap');

var getPrototypeOf = require('./shams').getPrototypeOf;

///////////////////////////////////////////////////////////////////////////
var ONCE_FUNCTION_MARKER = {};

function notify(listeners, args) {
	if (listeners.length === 0) { return false; }
	// take a copy in case one of the callbacks modifies the listeners array.
	listeners = listeners.slice();
	for (var i = 0, len = listeners.length; i < len; ++i) {
		var listener = listeners[i];
		try {
			listener.callback.apply(listener.context, args);
		} catch(e) {}
	}
	return true;
}

function notifyRemoves(emitter, listenerRecords) {
	for (var i = 0, len = listenerRecords.length; i < len; ++i) {
		var listenerRecord = listenerRecords[i];
		emitter.trigger(new metaEvents.RemoveListenerEvent(listenerRecord.eventIdentifier, listenerRecord.callback, listenerRecord.registeredContext));
	}
}

/**
 * This constructor function can be used directly, but most commonly, you will
 * call it from within your own constructor.
 *
 * e.g. <code>Emitter.call(this);</code>
 *
 * It will set up the emitter state if called, but it is optional.
 *
 * @constructor
 * @class Emitter
 * @classdesc
 * Emitter provides event emitting capabilities, similar to Backbone.
 * For more information see <a href="http://caplin.github.io/Emitter">the project page</a>.
 */
function Emitter() {
	this._emitterListeners = new MultiMap();
	this._emitterMetaEventsOn = false;
};

Emitter.prototype = {
	/**
	 * Registers a listener for an event.
	 *
	 * If context is provided, then the <code>this</code> pointer will refer to it
	 * inside the callback.
	 *
	 * @param {*} eventIdentifier The identifier of the event that the callback should listen to.
	 * @param {function} callback The function that should be called whenever the event is triggered.  May not be null.
	 * @param {?Object} [context] An optional context that defines what 'this' should be inside the callback.
	 */
	on: function listen(eventIdentifier, callback, context) {
		if (typeof callback !== 'function') { throw new TypeError("on: Illegal Argument: callback must be a function, was " + (typeof callback)); }

		// This allows us to work even if the constructor hasn't been called.  Useful for mixins.
		if (this._emitterListeners === undefined) {
			this._emitterListeners = new MultiMap();
		}

		if (typeof eventIdentifier === 'function' && (eventIdentifier.prototype instanceof metaEvents.MetaEvent || eventIdentifier === metaEvents.MetaEvent)) {
			// Since triggering meta events can be expensive, we only
			// do so if a listener has been added to listen to them.
			this._emitterMetaEventsOn = true;
		}

		var currentListeners = this._emitterListeners.getValues(eventIdentifier);
		currentListeners = currentListeners.filter(function(listenerRecord) {
			return listenerRecord.registeredContext === context
				&& (listenerRecord.callback === callback
					|| (listenerRecord.callback._wrappedCallback !== undefined
						&& listenerRecord.callback._wrappedCallback === callback._wrappedCallback));
		});
		if (currentListeners.length > 0) {
			throw new Error('This callback is already listening to this event.');
		}

		this._emitterListeners.add(eventIdentifier, {
			eventIdentifier: eventIdentifier,
			callback: callback,
			registeredContext: context,
			context: context !== undefined ? context : this
		});

		if (this._emitterMetaEventsOn === true) {
			this.trigger(new metaEvents.AddListenerEvent(eventIdentifier, callback._onceFunctionMarker === ONCE_FUNCTION_MARKER ? callback._wrappedCallback : callback, context));
		}
	},

	/**
	 * Registers a listener to receive an event only once.
	 *
	 * If context is provided, then the <code>this</code> pointer will refer to it
	 * inside the callback.
	 *
	 * @param {*} eventIdentifier The identifier of the event that the callback should listen to.
	 * @param {function} callback The function that should be called the first time the event is triggered.  May not be null.
	 * @param {?Object} [context] An optional context that defines what 'this' should be inside the callback.
	 */
	once: function(eventIdentifier, callback, context) {
		if (typeof callback !== 'function') { throw new TypeError("onnce: Illegal Argument: callback must be a function, was " + (typeof callback)); }

		var off = this.off.bind(this), hasFired = false;

		function onceEventHandler() {
			if (hasFired === false) {
				hasFired = true;
				off(eventIdentifier, onceEventHandler, context);
				callback.apply(this, arguments);
			}
		}
		// We need this to enable us to remove the wrapping event handler
		// when off is called with the original callback.
		onceEventHandler._onceFunctionMarker = ONCE_FUNCTION_MARKER;
		onceEventHandler._wrappedCallback = callback;

		this.on(eventIdentifier, onceEventHandler, context);
	},

	/**
	 * Clear previously registered listeners.
	 *
	 * With no arguments, this clears all listeners from this Emitter.
	 *
	 * With one argument, this clears all listeners registered to a particular event.
	 *
	 * With two or three arguments, this clears a specific listener.
	 *
	 * @param {?*} eventIdentifier The identifier of the event to clear. If null, it will clear all events.
	 * @param {?function} callback The callback function to clear.
	 * @param {?Object} context The context object for the callback.
	 * @returns {boolean} true if any listeners were removed.  This is not finalised yet and may change (particularly if we want to enable chaining).
	 */
	off: function off(eventIdentifier, callback, context) {
		// not initialised - so no listeners of any kind
		if (this._emitterListeners == null) { return false; }

		if (arguments.length === 0) {
			// clear all listeners.
			if (this._emitterMetaEventsOn === true) {
				var allListeners = this._emitterListeners.getValues();
				notifyRemoves(this, allListeners);
			}
			this._emitterListeners.clear();
			return true;
		} else if (arguments.length === 1) {
			// clear all listeners for a particular eventIdentifier.
			if (this._emitterListeners.hasAny(eventIdentifier)) {
				var listeners = this._emitterListeners.getValues(eventIdentifier);
				this._emitterListeners['delete'](eventIdentifier);
				if (this._emitterMetaEventsOn === true) {
					notifyRemoves(this, listeners);
				}
				return true;
			}
			return false;
		} else if (eventIdentifier === null && callback === null) {
			// clear all listeners for a particular context.
			return this.clearListeners(context);
		} else {
			// clear a specific listener.
			if (typeof callback !== 'function') { throw new TypeError("off: Illegal Argument: callback must be a function, was " + (typeof callback)); }

			var removedAListener = this._emitterListeners.removeLastMatch(eventIdentifier, function(record) {
				var callbackToCompare = record.callback._onceFunctionMarker === ONCE_FUNCTION_MARKER ? record.callback._wrappedCallback : record.callback;
				var callbackMatches = callback === callbackToCompare;
				var contextMatches = record.registeredContext === context;
				return callbackMatches && contextMatches;
			});

			if (removedAListener && this._emitterMetaEventsOn === true) {
				this.trigger(new metaEvents.RemoveListenerEvent(eventIdentifier, callback, context));
			}
			return removedAListener;
		}
	},

	/**
	 * Fires an event, causing all the listeners registered for this event to be called.
	 *
	 * If the event is an object, this will also call any listeners registered for
	 * its class or any superclasses will also fire.
	 *
	 * @param {*} event The event to fire.
	 * @param {...*} [args] Optional arguments to pass to the listeners.
	 * @returns {boolean} true if any listeners were notified, false otherwise.  This is not finalised and may change (particularly if we want to allow chaining).
	 */
	trigger: function trigger(event) {
		var args;
		var anyListeners = false;
		if (this._emitterListeners != null) {
			args = slice.call(arguments, 1);
			if (this._emitterListeners.hasAny(event)) {
				anyListeners = true;
				notify(this._emitterListeners.getValues(event), args);
			}

			// navigate up the prototype chain emitting against the constructors.
			if (typeof event === 'object') {
				var last = event, proto = getPrototypeOf(event);
				while (proto !== null && proto !== last) {
					if (this._emitterListeners.hasAny(proto.constructor)) {
						anyListeners = true;
						notify(this._emitterListeners.getValues(proto.constructor), arguments);
					}
					last = proto;
					proto = getPrototypeOf(proto);
				}
			}
		}
		if (this._emitterMetaEventsOn === true && anyListeners === false && event instanceof metaEvents.DeadEvent === false) {
			this.trigger(new metaEvents.DeadEvent(event, args));
		}
		return anyListeners;
	},

	/**
	 * Clears all listeners registered for a particular context.
	 *
	 * @param {Object} context The context that all listeners should be removed for.  May not be null.
	 */
	clearListeners: function clearListeners(context) {
		if (context == null) { throw new Error('clearListeners: context must be provided.'); }
		// notify for every listener we throw out.
		var removedListeners, trackRemovals = false;
		if (this._emitterMetaEventsOn === true) {
			trackRemovals = true;
			removedListeners = [];
		}
		this._emitterListeners.filterAll(function(record) {
			var keepListener = record.registeredContext !== context;
			if (trackRemovals && keepListener === false) {
				removedListeners.push(record);
			}
			return keepListener;
		});
		if (trackRemovals && removedListeners.length > 0) {
			notifyRemoves(this, removedListeners);
		}
	}
};

/**
 * Copies the Emitter methods onto the provided object.
 *
 * If the passed destination is a function, it copies the methods
 * onto the prototype of the passed destination.
 *
 * @param {function|Object} destination the object to copy the Emitter
 *    methods to or the constructor that should have its prototype
 *    augmented with the Emitter methods.
 */
Emitter.mixInto = function(destination) {
	if (typeof destination === 'function') {
		destination = destination.prototype;
	}
	for (var key in Emitter.prototype) {
		// If in the future Emitter is changed to inherit from something,
		// we would want to copy those methods/properties too.
		//noinspection JSUnfilteredForInLoop
		if (destination.hasOwnProperty(key)) {
			throw new Error("Emitter.mixInto: Destination already has function " + key + " unable to mixin.");
		}
		//noinspection JSUnfilteredForInLoop
		destination[key] = Emitter.prototype[key];
	}
};

module.exports = Emitter;
