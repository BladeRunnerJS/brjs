/**
 * @namespace
 * The topiary namespace contains a number of functions for
 * creating and querying a class hierarchy.
 * @name topiary
 */
;(function (definition) {
	// export mechanism that works in node, browser and some other places.
	if (typeof define === "function") {
		if (define.amd) {
			define(definition);
		} else {
			define('topiary', definition);
		}
	} else if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
		// node style commonJS.
		module.exports = definition();
	} else {
		// setting a global, as in e.g. a browser.
		this.topiary = definition();
	}
})(function () {
	"use strict";

	var ERROR_MESSAGES = {
		SUBCLASS_NOT_CONSTRUCTOR: "Subclass was not a constructor.",
		SUPERCLASS_NOT_CONSTRUCTOR: "Superclass was not a constructor when extending {0}.",
		PROTOTYPE_NOT_CLEAN: 'Prototype must be clean to extend another class. {1} has already been defined on the prototype of {0}.',
		NOT_CONSTRUCTOR: '{0} definition for {1} must be a constructor, was {2}.',
		DOES_NOT_IMPLEMENT: "Class {0} does not implement the attributes '{1}' from protocol {2}.",
		PROPERTY_ALREADY_PRESENT: 'Could not copy {0} from {1} to {2} as it was already present.',
		NULL: "{0} for {1} must not be null or undefined.",
		ALREADY_PRESENT: 'Could not copy {0} from {1} to {2} as it was already present.',
		WRONG_TYPE: '{0} for {1} should have been of type {2}, was {3}.'
	};

	// Main API ////////////////////////////////////////////////////////////////////////////////////

	var internalUseNames = ["__multiparents__", "__interfaces__", "__assignable_from_cache__", "__id__"];
	/**
	 * Sets up the prototype chain for inheritance.
	 *
	 * <p>As well as setting up the prototype chain, this also copies so called 'static'
	 * definitions from the superclass to the subclass and makes sure that constructor
	 * will return the correct thing.</p>
	 *
	 * @throws Error if the prototype has been modified before extend is called.
	 *
	 * @memberOf topiary
	 * @param classDefinition {function} The constructor of the subclass.
	 * @param superclass {function} The constructor of the superclass.
	 *
	 */
	function extend(classDefinition, superclass) {
		assertArgumentOfType('function', classDefinition, ERROR_MESSAGES.SUBCLASS_NOT_CONSTRUCTOR);
		var subclassName = className(classDefinition, "Subclass");
		assertArgumentOfType('function', superclass, ERROR_MESSAGES.SUPERCLASS_NOT_CONSTRUCTOR, subclassName);
		assertNothingInObject(classDefinition.prototype, ERROR_MESSAGES.PROTOTYPE_NOT_CLEAN, subclassName);

		for (var staticPropertyName in superclass) {
			if (superclass.hasOwnProperty(staticPropertyName)) {
				// this is because we shouldn't copy nonenumerables, but removing enumerability isn't
				// shimmable in ie8.  We need to make sure we don't inadvertantly copy across any
				// of the 'internal' fields we are using to keep track of things.
				if (internalUseNames.indexOf(staticPropertyName) >= 0) {
					continue;
				}

				classDefinition[staticPropertyName] = superclass[staticPropertyName];
			}
		}

		defineProperty(classDefinition, 'superclass', { enumerable: false, value: superclass });

		classDefinition.prototype = create(superclass.prototype, {
			"constructor": {
				enumerable: false,
				value: classDefinition
			}
		});

		// this is purely to work around a bad ie8 shim, when ie8 is no longer needed it can be deleted.
		if (classDefinition.prototype.hasOwnProperty("__proto__")) {
			delete classDefinition.prototype["__proto__"];
		}

		return classDefinition;
	}

	/**
	 * Mixes functionality in to a class.
	 *
	 * <p>Only functions are mixed in.</p>
	 *
	 * <p>Code in the mixin is sandboxed and only has access to a 'mixin instance' rather than
	 * the real instance.</p>
	 *
	 * @memberOf topiary
	 * @param target {function}
	 * @param mix {function|Object}
	 */
	function mixin(target, mix) {
		assertArgumentOfType('function', target, ERROR_MESSAGES.NOT_CONSTRUCTOR, 'Target', 'mixin');

		mix = toFunction(mix, new TypeError(msg(ERROR_MESSAGES.WRONG_TYPE, 'Mix', 'mixin', 'non-null object or function', mix === null ? 'null' : typeof mix)));
		var targetPrototype = target.prototype, mixinProperties = mix.prototype, resultingProperties = {};
		var mixins = nonenum(target, '__multiparents__', []);
		var myMixId = mixins.length;

		for (var property in mixinProperties) {
			// property might spuriously be 'constructor' if you are in ie8 and using a shim.
			if (typeof mixinProperties[property] === 'function' && property !== 'constructor') {
				if (property in targetPrototype === false) {
					resultingProperties[property] = getSandboxedFunction(myMixId, mix, mixinProperties[property]);
				} else if (targetPrototype[property].__original__ !== mixinProperties[property]) {
					throw new Error(msg(ERROR_MESSAGES.PROPERTY_ALREADY_PRESENT, property, className(mix, 'mixin'), className(target, 'target')));
				}
			} // we only mixin functions
		}

		copy(resultingProperties, targetPrototype);
		mixins.push(mix);
		return target;
	}

	/**
	 * Provides multiple inheritance through copying.
	 *
	 * <p>This is discouraged; you should prefer to use aggregation first,
	 * single inheritance (extends) second, mixins third and this as
	 * a last resort.</p>
	 *
	 * @memberOf topiary
	 * @param target {function} the class that should receive the functionality.
	 * @param parent {function|Object} the parent that provides the functionality.
	 */
	function inherit(target, parent) {
		assertArgumentOfType('function', target, ERROR_MESSAGES.NOT_CONSTRUCTOR, 'Target', 'inherit');
		parent = toFunction(parent, new TypeError(msg(ERROR_MESSAGES.WRONG_TYPE, 'Parent', 'inherit', 'non-null object or function', parent === null ? 'null' : typeof parent)));

		if (isAssignableFrom(target, parent)) {
			return target;
		}

		var resultingProperties = {};
		var targetPrototype = target.prototype;
		for (var propertyName in parent.prototype) {
			// These properties should be nonenumerable in modern browsers, but shims might
			// create them in ie8.
			if (propertyName === "constructor" || propertyName === "__proto__") continue;

			var notInTarget = targetPrototype[propertyName] === undefined;
			var parentHasNewerImplementation = notInTarget || isOverriderOf(propertyName, parent, target);
			if (parentHasNewerImplementation) {
				resultingProperties[propertyName] = parent.prototype[propertyName];
			} else {
				var areTheSame = targetPrototype[propertyName] === parent.prototype[propertyName];
				var targetIsUpToDate = areTheSame || isOverriderOf(propertyName, target, parent);
				if (targetIsUpToDate === false) {
					// target is not up to date, but we can't bring it up to date.
					throw new Error(msg(ERROR_MESSAGES.ALREADY_PRESENT, propertyName, className(parent, 'parent'), className(target, 'target')));
				}
				// otherwise we don't need to do anything.
			}
		}

		copy(resultingProperties, targetPrototype);
		var multiparents = nonenum(target, '__multiparents__', []);
		multiparents.push(parent);
		return target;
	}

	/**
	 * Declares that the provided class implements the provided protocol.
	 *
	 * <p>This involves checking that it does in fact implement the protocol and updating an
	 * internal list of interfaces attached to the class definition.</p>
	 *
	 * <p>It should be called after implementations are provided, i.e. at the end of the class definition.</p>
	 *
	 * @throws Error if there are any attributes on the protocol that are not matched on the class definition.
	 *
	 * @memberOf topiary
	 * @param classDefinition {function} A constructor that should create objects matching the protocol.
	 * @param protocol {function} A constructor representing an interface that the class should implement.
	 */
	function implement(classDefinition, protocol) {
		assertArgumentOfType('function', classDefinition, ERROR_MESSAGES.NOT_CONSTRUCTOR, 'Class', 'implement');
		assertArgumentOfType('function', protocol, ERROR_MESSAGES.NOT_CONSTRUCTOR, 'Protocol', 'implement');

		var interfaces = nonenum(classDefinition, '__interfaces__', []);
		var missing = missingAttributes(classDefinition, protocol);
		if (missing.length > 0) {
			throw new Error(msg(ERROR_MESSAGES.DOES_NOT_IMPLEMENT, className(classDefinition, "provided"), missing.join("', '"), className(protocol, "provided")));
		} else {
			interfaces.push(protocol);
		}
		return classDefinition;
	}

	/** @private */
	function fallbackIsAssignableFrom(classDefinition, parent) {
		if (classDefinition === parent || classDefinition.prototype instanceof parent) {
			return true;
		}
		var i, mixins = classDefinition.__multiparents__ || [], interfaces = classDefinition.__interfaces__ || [];

		// parent
		var superPrototype = (classDefinition.superclass && classDefinition.superclass.prototype) || getPrototypeOf(classDefinition.prototype);
		if (superPrototype != null && superPrototype !== classDefinition.prototype && isAssignableFrom(superPrototype.constructor, parent)) {
			return true;
		}

		// mixin chain
		for (i = 0; i < mixins.length; ++i) {
			if (isAssignableFrom(mixins[i], parent)) {
				return true;
			}
		}
		// interfaces chain
		for (i = 0; i < interfaces.length; ++i) {
			if (isAssignableFrom(interfaces[i], parent)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks to see if a class is a descendant of another class / interface / mixin.
	 *
	 * <ul><li>A class is a descendant of another class if the other class is in its prototype chain.
	 * </li><li>A class is a descendant of an interface if it has called implement that class or
	 * any class that this class is a descendant of has called implement for that class.
	 * </li><li>A class is a descendant of a mixin if it has called mixin for that mixin or
	 * any class that this class is a descendant of has called mixin for that mixin.
	 * </li></ul>
	 *
	 * @memberOf topiary
	 * @param classDefinition {function} the child class.
	 * @param constructor {function} the class to check if this class is a descendant of.
	 * @returns {boolean} true if the class is a descendant, false otherwise.
	 */
	function isAssignableFrom(classDefinition, constructor) {
		// sneaky edge case where we're checking against an object literal we've mixed in or against a prototype of something.
		if (typeof constructor === 'object' && constructor.hasOwnProperty('constructor')) { constructor = constructor.constructor; }

		assertArgumentOfType('function', classDefinition, ERROR_MESSAGES.NOT_CONSTRUCTOR, 'Class', 'isAssignableFrom');
		assertArgumentOfType('function', constructor, ERROR_MESSAGES.NOT_CONSTRUCTOR, 'Parent', 'isAssignableFrom');

		// This is just a caching wrapper around fallbackIsAssignableFrom.
		var cache = nonenum(classDefinition, '__assignable_from_cache__', {});
		var parentId = classId(constructor);
		if (cache[parentId] == null) {
			cache[parentId] = fallbackIsAssignableFrom(classDefinition, constructor);
		}
		return cache[parentId];
	}

	/**
	 * Checks to see if an instance is defined to be a child of a parent.
	 *
	 * @memberOf topiary
	 * @param instance {Object} An instance object to check.
	 * @param parent {function} A potential parent (see isAssignableFrom).
	 * @returns {boolean} true if this instance has been constructed from something that is assignable from the parent or is null, false otherwise.
	 */
	function isA(instance, parent) {
		// sneaky edge case where we're checking against an object literal we've mixed in or against a prototype of something.
		if (typeof parent == 'object' && parent.hasOwnProperty('constructor')) { parent = parent.constructor; }
		assertArgumentOfType('function', parent, ERROR_MESSAGES.NOT_CONSTRUCTOR, 'Parent', 'isA');
		if (instance == null) return false;
		if (instance instanceof parent) {
			return true;
		}
		return isAssignableFrom(instance.constructor, parent);
	}

	/**
	 * Does duck typing to determine if an instance object implements a protocol.
	 * <p>The protocol may be either an adhoc protocol, in which case it is an object
	 * or it can be a formal protocol in which case it's a function.</p>
	 *
	 * <p>In an adhoc protocol, you can use Number, Object, String and Boolean to indicate
	 * the type required on the instance.</p>
	 *
	 * @memberOf topiary
	 * @param instance {Object} the object to check.
	 * @param protocol {function|Object} the description of the properties that the object should have.
	 * @returns {boolean} true if all the properties on the protocol were on the instance and of the right type.
	 */
	function fulfills(instance, protocol) {
		assertArgumentNotNullOrUndefined(instance, ERROR_MESSAGES.NULL, 'Object', 'fulfills');
		assertArgumentNotNullOrUndefined(protocol, ERROR_MESSAGES.NULL, 'Protocol', 'fulfills');

		var protocolIsConstructor = typeof protocol === 'function';
		if (protocolIsConstructor && isA(instance, protocol)) {
			return true;
		}

		var requirement = protocolIsConstructor ? protocol.prototype : protocol;
		for (var item in requirement) {
			var type = typeof instance[item];
			var required = requirement[item];
			if (type !== typeof required) {
				if (type === 'number' && required === Number) {
					return true;
				} else if (type === 'object' && required === Object) {
					return true;
				} else if (type === 'string' && required === String) {
					return true;
				} else if (type === 'boolean' && required === Boolean) {
					return true;
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks that a class provides a prototype that will fulfill a protocol.
	 *
	 * @memberOf topiary
	 * @param classDefinition {function}
	 * @param protocol {function|Object}
	 * @returns {boolean}
	 */
	function classFulfills(classDefinition, protocol) {
		assertArgumentNotNullOrUndefined(classDefinition, ERROR_MESSAGES.NULL, 'Class', 'classFulfills');
		assertArgumentNotNullOrUndefined(protocol, ERROR_MESSAGES.NULL, 'Protocol', 'classFulfills');
		return fulfills(classDefinition.prototype, protocol);
	}

	// Auxillaries /////////////////////////////////////////////////////////////////////////////////

	var slice = Array.prototype.slice;

	function assertArgumentOfType(type, argument) {
		var actualType = typeof argument;
		if (actualType !== type) {
			var args = slice.call(arguments, 2);
			args.push(actualType);
			throw new TypeError(msg.apply(null, args));
		}
	}

	function assertNothingInObject(object) {
		for (var propertyName in object) {
			var args = slice.call(arguments, 1);
			args.push(propertyName);
			throw new Error(msg.apply(null, args));
		}
	}

	function assertArgumentNotNullOrUndefined(item) {
		if (item == null) {
			var args = slice.call(arguments, 1);
			throw new TypeError(msg.apply(null, args));
		}
	}

	function isOverriderOf(propertyName, sub, ancestor) {
		if (sub.prototype[propertyName] === ancestor.prototype[propertyName]) return false;
		var parents = getImmediateParents(sub);
		for (var i = 0; i < parents.length; ++i) {
			var parent = parents[i];
			if (parent.prototype[propertyName] === ancestor.prototype[propertyName]) return true;
			if (isOverriderOf(propertyName, parent, ancestor)) return true;
		}
		return false;
	}

	function getImmediateParents(sub) {
		var parents = (sub.__multiparents__ || []).slice();
		var parentPrototype = (sub.superclass && sub.superclass.prototype) || getPrototypeOf(sub.prototype);
		if (parentPrototype !== null && parentPrototype.constructor !== null && parentPrototype.constructor !== sub) {
			parents.push(parentPrototype.constructor);
		}
		return parents;
	}

	/**
	 * Interpolates a string with the arguments, used for error messages.
	 * @private **/
	function msg(str) {
		if (str == null) { return null; }
		for (var i = 1, len = arguments.length; i < len; ++i) {
			str = str.replace("{" + (i - 1) + "}", String(arguments[i]));
		}
		return str;
	}

	/**
	 * Returns a nonenumerable property if it exists, or creates one
	 * and returns that if it does not.
	 * @private
	 */
	function nonenum(object, propertyName, defaultValue) {
		var value = object[propertyName];
		if (value === undefined) {
			value = defaultValue;
			defineProperty(object, propertyName, {
				enumerable: false,
				value: value
			});
		}
		return value;
	}

	/**
	 * Easier for us if we treat everything as functions with prototypes.
	 * This function makes plain objects behave that way.
	 * @private
	 */
	function toFunction(obj, couldNotCastError) {
		if (obj == null) throw couldNotCastError;
		var result;
		if (typeof obj === 'object') {
			if (obj.hasOwnProperty('constructor')) {
				if (obj.constructor.prototype !== obj) throw couldNotCastError;
				result = obj.constructor;
			} else {
				var EmptyInitialiser = function () {};
				EmptyInitialiser.prototype = obj;
				defineProperty(obj, 'constructor', {
					enumerable: false, value: EmptyInitialiser
				});
				result = EmptyInitialiser;
			}
		} else if (typeof obj === 'function') {
			result = obj;
		} else {
			throw couldNotCastError;
		}
		return result;
	}

	/** @private */
	var currentId = 0;
	/**
	 * Returns the nonenumerable property __id__ of an object
	 * if it exists, otherwise adds one and returns that.
	 * @private
	 */
	function classId(func) {
		var result = func.__id__;
		if (result == null) {
			result = nonenum(func, '__id__', currentId++);
		}
		return result;
	}

	var nameFromToStringRegex = /^function\s?([^\s(]*)/;

	/**
	 * Gets the classname of an object or function if it can.  Otherwise returns the provided default.
	 *
	 * Getting the name of a function is not a standard feature, so while this will work in many
	 * cases, it should not be relied upon except for informational messages (e.g. logging and Error
	 * messages).
	 *
	 * @private
	 */
	function className(object, defaultName) {
		var result = "";
		if (typeof object === 'function') {
			result = object.name || object.toString().match(nameFromToStringRegex)[1];
		} else if (typeof object.constructor === 'function') {
			result = className(object.constructor, defaultName);
		}
		return result || defaultName;
	}

	/**
	 * Returns an array of all of the properties on a protocol that are not on classdef
	 * or are of a different type on classdef.
	 * @private
	 */
	function missingAttributes(classdef, protocol) {
		var result = [], obj = classdef.prototype, requirement = protocol.prototype;
		for (var item in requirement) {
			if (typeof obj[item] !== typeof requirement[item]) {
				result.push(item);
			}
		}
		return result;
	}

	/**
	 * Copies all properties from the source to the target (including inherited properties)
	 * and optionally makes them not enumerable.
	 * @private
	 */
	function copy(source, target, hidden) {
		for (var key in source) {
			defineProperty(target, key, {
				enumerable: hidden !== true,
				configurable: true, writable: true,
				value: source[key]
			});
		}
		return target;
	}

	/**
	 * Turns a function into a method by using 'this' as the first argument.
	 * @private
	 */
	function makeMethod(func) {
		return function () {
			var args = [this].concat(slice.call(arguments));
			return func.apply(null, args);
		};
	}

	/**
	 * Mixin functions are sandboxed into their own instance.
	 * @private
	 */
	function getSandboxedFunction(myMixId, mix, func) {
		var result = function () {
			var mixInstances = nonenum(this, '__multiparentInstances__', []);
			var mixInstance = mixInstances[myMixId];
			if (mixInstance == null) {
				if (typeof mix === 'function') {
					mixInstance = new mix();
				} else {
					mixInstance = create(mix);
				}
				// could add a nonenum pointer to __this__ or something if we wanted to
				// allow escape from the sandbox.
				mixInstances[myMixId] = mixInstance;
			}
			return func.apply(mixInstance, arguments);
		};
		nonenum(result, '__original__', func);
		nonenum(result, '__source__', mix);
		return result;
	}


	// Partial 'shams' to work around ie8s lack of es5 //////////////////////////////////////////////

	// These shams only cover the cases used within topiary.
	// When IE8 support is no longer needed, all these can be dropped in favour of the es5 methods.

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

	var create = Object.create ? Object.create : function(proto, descriptors) {
		var myConstructor = function() {};
		myConstructor.prototype = proto;

		var result = new myConstructor();

		var keys = Object.keys(descriptors);
		for (var i = 0; i < keys.length; ++i) {
			var key = keys[i];
			defineProperty(result, key, descriptors[key]);
		}

		return result;
	};

	function getPrototypeOf(obj) {
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
	}


	// Exporting ///////////////////////////////////////////////////////////////////////////////////

	var methods = {
		'extend': extend, 'inherit': inherit, 'mixin': mixin, 'implement': implement,
		'isAssignableFrom': isAssignableFrom, 'isA': isA, 'fulfills': fulfills,
		'classFulfills': classFulfills
	};

	/* jshint evil:true */
	var global = (new Function('return this;'))();

	var exporting = {
		'exportTo': function(to) {
			copy(methods, to || global, true);
		},
		'install': function() {
			copy({
				isA: makeMethod(methods.isA),
				fulfills: makeMethod(methods.fulfills)
			}, Object.prototype, true);
			copy({
				'isAssignableFrom': makeMethod(methods.isAssignableFrom),
				'implements': makeMethod(methods.implement),
				'fulfills': makeMethod(methods.classFulfills),
				'extends': makeMethod(methods.extend),
				'mixin': makeMethod(methods.mixin),
				'inherits': makeMethod(methods.inherit)
			}, Function.prototype, true);
		}
	};
	copy(methods, exporting);

	// not sure if this works in node-jasmine....
	if ('jasmine' in global) {
		var err = {};
		var getErr = function (key) {
			return function () {
				var message = ERROR_MESSAGES[key];
				var args = slice.call(arguments);
				args.unshift(message);
				var result = msg.apply(null, args);
				if (result === null) {
					throw new Error("No such error message " + key);
				}
				return result;
			};
		};
		for (var key in ERROR_MESSAGES) {
			err[key] = getErr(key);
		}
		exporting._err = err;
	}

	return exporting;
});