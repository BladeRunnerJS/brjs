// emitr built for browser 2014-12-10T10:43:19.131Z
;(function (name, factory) {
	if (typeof module === 'object') {
		// Does not work with strict CommonJS, but only CommonJS-like environments
		// that support module.exports - like Node.
		module.exports = factory();
	} else if (typeof define === 'function') {
		define(factory);
	} else {
		// For an environment with no require/define framework loaded - e.g. a browser.
		var global = Function("return this")();
		global[name] = factory();
	}
})
( "emitr", function() {
	var global = Function("return this")();
	
	function realm(parentRequire) {
		var moduleDefinitions = {}, incompleteExports = {}, moduleExports = {}, modulesFromParent = {};
	
		function derelativise(context, path) {
			var result = (context === "" || path.charAt(0) !== '.') ? [] : context.split("/");
			var working = path.split("/"), item;
			while (item = working.shift()) {
				if (item === "..") {
					result.pop();
				} else if (item !== ".") {
					result.push(item);
				}
			}
			return result.join("/");
		}
	
		function define(id, definition) {
			if (id in moduleDefinitions) {
				throw new Error('Module ' + id + ' has already been defined.');
			}
			if (modulesFromParent[id] === true) {
				throw new Error('Module ' + id + ' has already been loaded from a parent realm.');
			}
			moduleDefinitions[id] = definition;
		}
	
		function require(context, id) {
			id = derelativise(context, id).replace(/\.js$/, "");
	
			if (moduleExports[id] != null) { return moduleExports[id]; }
	
			if (incompleteExports[id] != null) {
				// there is a circular dependency, we do the best we can in the circumstances.
				return incompleteExports[id].exports;
			}
	
			var definition = moduleDefinitions[id];
			if (definition == null) {
				if (parentRequire != null) {
					var result = parentRequire(id);
					modulesFromParent[id] = true;
					return result;
				}
				throw new Error("No definition for module " + id + " has been loaded.");
			}
	
			var module = { exports: {}, id: id };
			incompleteExports[id] = module;
			try {
				if (typeof definition === 'function') {
					var definitionContext = id;
					var idx = id.lastIndexOf("/");
					if (idx >= 0) {
						definitionContext = id.substring(0, idx);
					}
					var returnValue = definition.call(module, require.bind(null, definitionContext), module.exports, module);
					moduleExports[id] = returnValue || module.exports;
				} else {
					moduleExports[id] = definition;
				}
			} finally {
				delete incompleteExports[id];
			}
			return moduleExports[id];
		}
	
		return {
			define: define, require: require.bind(null, '')
		};
	}
	
	var defaultRealm = realm(global.require || function(moduleId) {
		if (global[moduleId]) {
			return global[moduleId];
		}
		throw new Error("No definition for module " + moduleId + " could be found in the global top level.");
	});
	
	var require = defaultRealm.require;
	var _define = defaultRealm.define;

	_define("emitr", function(require, exports, module) {
		module.exports = require("./lib/index");
	});
	_define("emitr/lib/index", function(require, exports, module) {
		module.exports = require('./Emitter');
		module.exports.meta = require('./events');
		module.exports.Event = require('./Event');
	});
	_define("emitr/lib/Emitter", function(require, exports, module) {
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
		
	});
	_define("emitr/lib/Event", function(require, exports, module) {
		"use strict";
		
		var shams = require('./shams');
		// Event ///////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Creates a base Event object.
		 * @constructor
		 * @memberOf Emitter
		 * @class Event
		 * @classdesc
		 * Event provides a convenient base class for events.
		 */
		var Event = function() {};
		
		/**
		 * Extend provides a shorthand for creating subclasses of the class
		 * whose constructor it is attached to.
		 *
		 * You can pass in an object that represents the things that
		 * should be added to the prototype (in which case, the special
		 * member 'constructor' if present will become the constructor),
		 * or a function that represents the constructor whose prototype
		 * should be modified, or nothing at all, in which case a new
		 * constructor will be created that calls the superclass constructor.
		 *
		 * @memberOf Emitter.Event
		 * @param {object|function} [properties] an object containing methods to be added to the prototype, or the constructor function, or nothing at all.
		 * @returns {function} a constructor function for the newly created subclass.
		 */
		Event.extend = function inlineExtend(properties) {
			var superclass = this, subclassConstructor;
			if (typeof superclass !== 'function') { throw new TypeError("extend: Superclass must be a constructor function, was a " + typeof superclass); }
		
			if (typeof properties === 'function') {
				subclassConstructor = properties;
			} else if (properties != null && properties.hasOwnProperty('constructor')) {
				subclassConstructor = properties.constructor;
			} else {
				subclassConstructor = function() {
					superclass.apply(this, arguments);
				};
			}
			subclassConstructor.superclass = superclass;
			subclassConstructor.prototype = shams.create(superclass.prototype, {
				constructor: {
					enumerable: false, value: subclassConstructor
				}
			});
			
			//IE8 bug. https://developer.mozilla.org/en-US/docs/ECMAScript_DontEnum_attribute
			if (subclassConstructor.prototype.constructor !== subclassConstructor) {
				subclassConstructor.prototype.constructor = subclassConstructor;
			}
		
			if (typeof properties === 'object') {
				if (shams.getPrototypeOf(properties) !== Object.prototype) {
					throw new Error("extend: Can't extend something that already has a prototype chain.");
				}
				for (var instanceProperty in properties) {
					if (instanceProperty !== 'constructor' && properties.hasOwnProperty(instanceProperty)) {
						subclassConstructor.prototype[instanceProperty] = properties[instanceProperty];
					}
				}
			}
			for (var staticProperty in superclass) {
				if (superclass.hasOwnProperty(staticProperty)) {
					subclassConstructor[staticProperty] = superclass[staticProperty];
				}
			}
		
			return subclassConstructor;
		};
		/**
		 * A simple toString is provided to aid in debugging.
		 * @returns {string} a representation of all the fields on the object.
		 */
		Event.prototype.toString = function() {
			var result = [];
			for (var key in this) {
				// toString should show inherited properties too.
				//noinspection JSUnfilteredForInLoop
				if (typeof result[key] !== 'function') {
					//noinspection JSUnfilteredForInLoop
					result.push(key + ": " + this[key] + ",");
				}
			}
			return result.join(" ");
		};
		
		module.exports = Event;
	});
	_define("emitr/lib/events", function(require, exports, module) {
		"use strict";
		
		var Event = require('./Event');
		
		var MetaEvent = Event.extend(
				/**
				 * @memberOf Emitter.meta
				 * @class MetaEvent
				 * @param {*} event The event this MetaEvent is about
				 * @classdesc
				 * A parent class for all meta events.
				 */
						function(event) {
					/**
					 * Event provides the identifier of the event that this MetaEvent is about.
					 * @name Emitter.meta.MetaEvent#event
					 * @type {*}
					 */
					this.event = event;
				}
		);
		/**
		 * @memberOf Emitter.meta
		 * @extends Emitter.meta.MetaEvent
		 * @class ListenerEvent
		 * @classdesc
		 * A parent class for all MetaEvents about listeners.
		 */
		var ListenerEvent = MetaEvent.extend(
				function(event, listener, context) {
					MetaEvent.call(this, event);
					/**
					 * The listener this ListenerEvent is about.
					 * @name Emitter.meta.ListenerEvent#listener
					 * @type {function}
					 */
					this.listener = listener;
					/**
					 * The context associated with the listener.
					 * @name Emitter.meta.ListenerEvent#context
					 * @type {?object}
					 */
					this.context = context;
				}
		);
		/**
		 * @memberOf Emitter.meta
		 * @class AddListenerEvent
		 * @extends Emitter.meta.ListenerEvent
		 */
		var AddListenerEvent = ListenerEvent.extend();
		/**
		 * @memberOf Emitter.meta
		 * @class RemoveListenerEvent
		 * @extends Emitter.meta.ListenerEvent
		 */
		var RemoveListenerEvent = ListenerEvent.extend();
		/**
		 * @memberOf Emitter.meta
		 * @class DeadEvent
		 * @extends Emitter.meta.MetaEvent
		 */
		var DeadEvent = MetaEvent.extend(
				function(event, args) {
					MetaEvent.call(this, event);
					this.data = args;
				}
		);
		
		/**
		 * Where the meta events live.
		 * @memberOf Emitter
		 * @namespace meta
		 */
		module.exports = {
			MetaEvent: MetaEvent,
			ListenerEvent: ListenerEvent,
			AddListenerEvent: AddListenerEvent,
			RemoveListenerEvent: RemoveListenerEvent,
			DeadEvent: DeadEvent
		};
		
	});
	_define("emitr/lib/shams", function(require, exports, module) {
		// Partial 'sham' to work around ie8s lack of es5 //////////////////////////////////////////////
		// When IE8 support is no longer needed, all these can be dropped in favour of the es5 methods.
		
		exports.getPrototypeOf = function getPrototypeOf(obj) {
			if (Object.getPrototypeOf) {
				var proto = Object.getPrototypeOf(obj);
		
				// to avoid bad shams...
				if (proto !== obj) return proto;
			}
		
			// this is what most shams do, but sometimes it's wrong.
			if (obj.constructor && obj.constructor.prototype && obj.constructor.prototype !== obj) {
				return obj.constructor.prototype;
			}
		
			// this works only if we've been kind enough to supply a superclass property
			// (which we do when we extend classes).
			if (obj.constructor && obj.constructor.superclass) {
				return obj.constructor.superclass.prototype;
			}
		
			// can't find a good prototype.
			return null;
		};
		
		var defineProperty = function(obj, prop, descriptor) {
			obj[prop] = descriptor.value;
		};
		if (Object.defineProperty) {
			try {
				// IE8 throws an error here.
				Object.defineProperty({}, 'x', {});
				defineProperty = Object.defineProperty;
			} catch (e) {}
		}
		exports.defineProperty = defineProperty;
		
		exports.create = function create(proto, descriptors) {
			var result;
		
			if(Object.create) {
				result = Object.create(proto, descriptors);
		
				var dunderProtoPassedIn = (proto && proto.__proto__) || (descriptors && descriptors.__proto__);
		
				if(result.__proto__ && !dunderProtoPassedIn) {
					//ES5 shim added this and it's a lie so delete it.
					delete result.__proto__;
				}
			} else {
				var myConstructor = function() {};
				myConstructor.prototype = proto;
		
				result = new myConstructor();
		
				var keys = Object.keys(descriptors);
				for (var i = 0; i < keys.length; ++i) {
					var key = keys[i];
					defineProperty(result, key, descriptors[key]);
				}
			}
		
			return result;
		};
		
	});
	_define("emitr/lib/MultiMap", function(require, exports, module) {
		var Map = require('./Map');
		
		function MultiMap() {
			this._map = new Map();
		}
		MultiMap.prototype = {
			'getValues': function getValues(key) {
				var val;
				if (arguments.length === 0) {
					// return all values for all keys.
					val = [];
					this._map.forEach(function(values) {
						val.push.apply(val, values);
					});
				} else {
					// return all the values for the provided key.
					val = this._map.get(key);
					if (val === undefined) {
						val = [];
						this._map.set(key, val);
					}
				}
				return val;
			},
			'clear': function clear() {
				this._map = new Map();
			},
			'add': function add(key, value) {
				this.getValues(key).push(value);
			},
			'filter': function filter(key, filterFunction) {
				if (this._map.has(key) === false) { return; }
				var values = this._map.get(key).filter(filterFunction);
		
				if (values.length === 0) {
					this._map['delete'](key);
				} else {
					this._map.set(key, values);
				}
			},
			'filterAll': function(filterFunction) {
		
				//TODO: The following line can be removed and instead a third 'map' parameter 
				// can be added to the forEach callback once the following webkit bug is resovled
				// https://bugs.webkit.org/show_bug.cgi?id=138563
		
				var map = this._map;
				this._map.forEach(function(values, key) {
					var newValues = values.filter(filterFunction);
					if (newValues.length === 0) {
						map['delete'](key);
					} else {
						map.set(key, newValues);
					}
				});
			},
			'removeLastMatch': function removeLast(key, matchFunction) {
				if (this._map.has(key) === false) { return false; }
				var values = this._map.get(key);
				for (var i = values.length - 1; i >= 0; --i) {
					if (matchFunction(values[i])) {
						values.splice(i, 1);
						return true;
					}
				}
				return false;
			},
			'hasAny': function has(key) {
				return this._map.has(key);
			},
			'delete': function del(key) {
				this._map['delete'](key);
			}
		};
		
		module.exports = MultiMap;
	});
	_define("emitr/lib/Map", function(require, exports, module) {
		"use strict";
		
		var global = Function("return this")();
		
		var Map = global.Map;
		
		// Uses a map for string keys and two arrays for nonstring keys.
		// Another alternative would have been to add a nonenumerable id to everything that was set.
		function MapShim() {
			this._map = {};
			this._keys = [];
			this._values = [];
		}
		MapShim.prototype = {
			'set': function set(key, value) {
				if (typeof key === 'string') {
					this._map[key] = value;
					return value;
				}
				var idx = this._keys.indexOf(key);
				if (idx < 0) {
					idx = this._keys.length;
					this._keys[idx] = key;
				}
				this._values[idx] = value;
				return value;
			},
			'get': function get(key) {
				if (typeof key === 'string') {
					return this._map[key];
				}
				return this._values[this._keys.indexOf(key)];
			},
			'delete': function(key) {
				if (typeof key === 'string') {
					delete this._map[key];
					return;
				}
				var idx = this._keys.indexOf(key);
				if (idx >= 0) {
					this._keys.splice(idx, 1);
					this._values.splice(idx, 1);
				}
			},
			'has': function(key) {
				return (typeof key === 'string' && key in this._map) || (this._keys.indexOf(key) >= 0);
			},
			'forEach': function(callback) {
				for (var key in this._map) {
					if (this._map.hasOwnProperty(key)) {
						callback(this._map[key], key, this);
					}
				}
				for (var i = this._keys.length - 1; i >= 0; --i) {
					callback(this._values[i], this._keys[i], this);
				}
			}
		};
		
		// Older versions of Firefox had Map, but didn't have forEach, so we'll use the shim there too.
		if (Map === undefined || Map.prototype.forEach === undefined) {
			Map = MapShim;
		}
		
		module.exports = Map;
	});
	

	return require("emitr");
});