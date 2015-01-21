/**
 * @namespace
 * The topiarist namespace contains a number of functions for creating and querying a class hierarchy.
 * @name topiarist
 */
;(function(definition) {
	// export mechanism that works in node, browser and some other places.
	if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
		// node style commonJS.
		module.exports = definition();
	} else if (typeof define === 'function') {
		if (define.amd) {
			define(definition);
		} else {
			define('topiarist', definition);
		}
	} else {
		// setting a global, as in e.g. a browser.
		this.topiarist = definition();
	}
})(function() {
	'use strict';

	var ERROR_MESSAGES = {
		SUBCLASS_NOT_CONSTRUCTOR: 'Subclass was not a constructor.',
		SUPERCLASS_NOT_CONSTRUCTOR: 'Superclass was not a constructor when extending {0}.',
		PROTOTYPE_NOT_CLEAN: 'Prototype must be clean to extend another class. {1} has already been defined on the ' +
			'prototype of {0}.',
		NOT_CONSTRUCTOR: '{0} definition for {1} must be a constructor, was {2}.',
		DOES_NOT_IMPLEMENT: 'Class {0} does not implement the attributes \'{1}\' from protocol {2}.',
		PROPERTY_ALREADY_PRESENT: 'Could not copy {0} from {1} to {2} as it was already present.',
		NULL: '{0} for {1} must not be null or undefined.',
		ALREADY_PRESENT: 'Could not copy {0} from {1} to {2} as it was already present.',
		WRONG_TYPE: '{0} for {1} should have been of type {2}, was {3}.',
		TWO_CONSTRUCTORS: 'Two different constructors provided for {0}, use only one of the classDefinition argument ' +
			'and extraProperties.constructor.',
		BAD_INSTALL: 'Can only install to the global environment or a constructor, can\'t install to a {0}.'
	};

	// Main API ////////////////////////////////////////////////////////////////////////////////////

	// only used for compatibility with shimmed, non es5 browsers.
	var internalUseNames = ['__multiparents__', '__interfaces__', '__assignable_from_cache__', '__id__'];

	/**
	 * Sets up the prototype chain for inheritance.
	 *
	 * <p>As well as setting up the prototype chain, this also copies so called 'class' definitions from the superclass
	 *  to the subclass and makes sure that constructor will return the correct thing.</p>
	 *
	 * @throws Error if the prototype has been modified before extend is called.
	 *
	 * @memberOf topiarist
	 * @param {?function} classDefinition The constructor of the subclass.
	 * @param {!function} superclass The constructor of the superclass.
	 * @param {?object} [extraProperties] An object of extra properties to add to the subclasses prototype.
	 */
	function extend(classDefinition, superclass, extraProperties) {
		var subclassName = className(classDefinition, 'Subclass');

		// Find the right classDefinition - either the one provided, a new one or the one from extraProperties.
		var extraPropertiesHasConstructor = typeof extraProperties !== 'undefined' &&
			extraProperties.hasOwnProperty('constructor') &&
			typeof extraProperties.constructor === 'function';

		if (classDefinition != null) {
			if (extraPropertiesHasConstructor && classDefinition !== extraProperties.constructor) {
				throw new Error(msg(ERROR_MESSAGES.TWO_CONSTRUCTORS, subclassName));
			}
		} else if (extraPropertiesHasConstructor) {
			classDefinition = extraProperties.constructor;
		} else {
			classDefinition = function() {
				superclass.apply(this, arguments);
			};
		}

		// check arguments
		assertArgumentOfType('function', classDefinition, ERROR_MESSAGES.SUBCLASS_NOT_CONSTRUCTOR);
		assertArgumentOfType('function', superclass, ERROR_MESSAGES.SUPERCLASS_NOT_CONSTRUCTOR, subclassName);
		assertNothingInObject(classDefinition.prototype, ERROR_MESSAGES.PROTOTYPE_NOT_CLEAN, subclassName);

		// copy class properties
		for (var staticPropertyName in superclass) {
			if (superclass.hasOwnProperty(staticPropertyName)) {
				// this is because we shouldn't copy nonenumerables, but removing enumerability isn't shimmable in ie8.
				// We need to make sure we don't inadvertently copy across any of the 'internal' fields we are using to
				//  keep track of things.
				if (internalUseNames.indexOf(staticPropertyName) >= 0) {
					continue;
				}

				classDefinition[staticPropertyName] = superclass[staticPropertyName];
			}
		}

		// create the superclass property on the subclass constructor
		Object.defineProperty(classDefinition, 'superclass', { enumerable: false, value: superclass });

		// create the prototype with a constructor function.
		classDefinition.prototype = Object.create(superclass.prototype, {
			"constructor": { enumerable: false,	value: classDefinition }
		});

		// copy everything from extra properties.
		if (extraProperties != null) {
			for (var property in extraProperties) {
				if (extraProperties.hasOwnProperty(property) && property !== 'constructor') {
					classDefinition.prototype[property] = extraProperties[property];
				}
			}
		}

		// this is purely to work around a bad ie8 shim, when ie8 is no longer needed it can be deleted.
		if (classDefinition.prototype.hasOwnProperty('__proto__')) {
			delete classDefinition.prototype['__proto__'];
		}

		clearAssignableCache(classDefinition, superclass);

		return classDefinition;
	}

	/**
	 * Mixes functionality in to a class.
	 *
	 * <p>Only functions are mixed in.</p>
	 *
	 * <p>Code in the mixin is sandboxed and only has access to a 'mixin instance' rather than the real instance.</p>
	 *
	 * @memberOf topiarist
	 * @param {function} target
	 * @param {function|Object} mix
	 */
	function mixin(target, mix) {
		assertArgumentOfType('function', target, ERROR_MESSAGES.NOT_CONSTRUCTOR, 'Target', 'mixin');

		mix = toFunction(
			mix,
			new TypeError(
				msg(
					ERROR_MESSAGES.WRONG_TYPE,
					'Mix',
					'mixin',
					'non-null object or function',
					mix === null ? 'null' : typeof mix
				)
			)
		);

		var targetPrototype = target.prototype, mixinProperties = mix.prototype, resultingProperties = {};
		var mixins = nonenum(target, '__multiparents__', []);
		var myMixId = mixins.length;

		for (var property in mixinProperties) {
			// property might spuriously be 'constructor' if you are in ie8 and using a shim.
			if (typeof mixinProperties[property] === 'function' && property !== 'constructor') {
				if (property in targetPrototype === false) {
					resultingProperties[property] = getSandboxedFunction(myMixId, mix, mixinProperties[property]);
				} else if (targetPrototype[property].__original__ !== mixinProperties[property]) {
					throw new Error(
						msg(
							ERROR_MESSAGES.PROPERTY_ALREADY_PRESENT,
							property,
							className(mix, 'mixin'),
							className(target, 'target')
						)
					);
				}
			} // we only mixin functions
		}

		copy(resultingProperties, targetPrototype);
		mixins.push(mix);

		clearAssignableCache(target, mix);

		return target;
	}

	/**
	 * Provides multiple inheritance through copying.
	 *
	 * <p>This is discouraged; you should prefer to use aggregation first, single inheritance (extends) second, mixins
	 *  third and this as a last resort.</p>
	 *
	 * @memberOf topiarist
	 * @param {function} target the class that should receive the functionality.
	 * @param {function|Object} parent the parent that provides the functionality.
	 */
	function inherit(target, parent) {
		assertArgumentOfType('function', target, ERROR_MESSAGES.NOT_CONSTRUCTOR, 'Target', 'inherit');
		parent = toFunction(
			parent,
			new TypeError(
				msg(
					ERROR_MESSAGES.WRONG_TYPE,
					'Parent',
					'inherit',
					'non-null object or function',
					parent === null ? 'null' : typeof parent
				)
			)
		);

		if (classIsA(target, parent)) {
			return target;
		}

		var resultingProperties = {};
		var targetPrototype = target.prototype;
		for (var propertyName in parent.prototype) {
			// These properties should be nonenumerable in modern browsers, but shims might create them in ie8.
			if (propertyName === 'constructor' || propertyName === '__proto__' || propertyName === 'toString') {
				continue;
			}

			var notInTarget = targetPrototype[propertyName] === undefined;
			var parentHasNewerImplementation = notInTarget || isOverriderOf(propertyName, parent, target);
			if (parentHasNewerImplementation) {
				resultingProperties[propertyName] = parent.prototype[propertyName];
			} else {
				var areTheSame = targetPrototype[propertyName] === parent.prototype[propertyName];
				var targetIsUpToDate = areTheSame || isOverriderOf(propertyName, target, parent);
				if (targetIsUpToDate === false) {
					// target is not up to date, but we can't bring it up to date.
					throw new Error(
						msg(
							ERROR_MESSAGES.ALREADY_PRESENT,
							propertyName,
							className(parent, 'parent'),
							className(target, 'target')
						)
					);
				}
				// otherwise we don't need to do anything.
			}
		}

		copy(resultingProperties, targetPrototype);
		var multiparents = nonenum(target, '__multiparents__', []);
		multiparents.push(parent);

		clearAssignableCache(target, parent);

		return target;
	}

	/**
	 * Declares that the provided class will implement the provided protocol.
	 *
	 * <p>This involves immediately updating an internal list of interfaces attached to the class definition,
	 * and after a <code>setTimeout(0)</code> verifying that it does in fact implement the protocol.</p>
	 *
	 * <p>It can be called before the implementations are provided, i.e. immediately after the constructor.</p>
	 *
	 * @throws Error if there are any attributes on the protocol that are not matched on the class definition.
	 *
	 * @memberOf topiarist
	 * @param {function} classDefinition A constructor that should create objects matching the protocol.
	 * @param {function} protocol A constructor representing an interface that the class should implement.
	 */
	function implement(classDefinition, protocol) {
		doImplement(classDefinition, protocol);

		setTimeout(function() {
			assertHasImplemented(classDefinition, protocol);
		}, 0);

		return classDefinition;
	}

	/**
	 * Declares that the provided class implements the provided protocol.
	 *
	 * <p>This involves checking that it does in fact implement the protocol and updating an internal list of
	 *  interfaces attached to the class definition.</p>
	 *
	 * <p>It should be called after implementations are provided, i.e. at the end of the class definition.</p>
	 *
	 * @throws Error if there are any attributes on the protocol that are not matched on the class definition.
	 *
	 * @memberOf topiarist
	 * @param {function} classDefinition A constructor that should create objects matching the protocol.
	 * @param {function} protocol A constructor representing an interface that the class should implement.
	 */
	function hasImplemented(classDefinition, protocol) {
		doImplement(classDefinition, protocol);
		assertHasImplemented(classDefinition, protocol);

		return classDefinition;
	}

	/** @private */
	function doImplement(classDefinition, protocol) {
		assertArgumentOfType('function', classDefinition, ERROR_MESSAGES.NOT_CONSTRUCTOR, 'Class', 'hasImplemented');
		assertArgumentOfType('function', protocol, ERROR_MESSAGES.NOT_CONSTRUCTOR, 'Protocol', 'hasImplemented');

		var interfaces = nonenum(classDefinition, '__interfaces__', []);
		interfaces.push(protocol);

		clearAssignableCache(classDefinition, protocol);

		return classDefinition;
	}

	function assertHasImplemented(classDefinition, protocol) {
		var missing = missingAttributes(classDefinition, protocol);
		if (missing.length > 0) {
			throw new Error(
				msg(
					ERROR_MESSAGES.DOES_NOT_IMPLEMENT,
					className(classDefinition, 'provided'),
					missing.join('\', \''),
					className(protocol, 'provided')
				)
			);
		}
	}

	function fallbackIsAssignableFrom(classDefinition, parent) {
		if (classDefinition === parent || classDefinition.prototype instanceof parent) {
			return true;
		}
		var i, mixins = classDefinition.__multiparents__ || [], interfaces = classDefinition.__interfaces__ || [];

		// parent
		var superPrototype = (classDefinition.superclass && classDefinition.superclass.prototype) ||
			getPrototypeOf(classDefinition.prototype);

		if (
			superPrototype != null &&
			superPrototype !== classDefinition.prototype &&
			classIsA(superPrototype.constructor, parent)
		) {
			return true;
		}

		// mixin chain
		for (i = 0; i < mixins.length; ++i) {
			if (classIsA(mixins[i], parent)) {
				return true;
			}
		}

		// interfaces chain
		for (i = 0; i < interfaces.length; ++i) {
			if (classIsA(interfaces[i], parent)) {
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
	 * @memberOf topiarist
	 * @param {function} classDefinition the child class.
	 * @param {function} constructor the class to check if this class is a descendant of.
	 * @returns {boolean} true if the class is a descendant, false otherwise.
	 */
	function classIsA(classDefinition, constructor) {
		// sneaky edge case where we're checking against an object literal we've mixed in or against a prototype of
		//  something.
		if (typeof constructor === 'object' && constructor.hasOwnProperty('constructor')) {
			constructor = constructor.constructor;
		}

		assertArgumentOfType('function', classDefinition, ERROR_MESSAGES.NOT_CONSTRUCTOR, 'Class', 'classIsA');
		assertArgumentOfType('function', constructor, ERROR_MESSAGES.NOT_CONSTRUCTOR, 'Parent', 'classIsA');

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
	 * @memberOf topiarist
	 * @param {Object} instance An instance object to check.
	 * @param {function} parent A potential parent (see classIsA).
	 * @returns {boolean} true if this instance has been constructed from something that is assignable from the parent
	 *  or is null, false otherwise.
	 */
	function isA(instance, parent) {
		if(instance == null) {
			return false;
		}

		// sneaky edge case where we're checking against an object literal we've mixed in or against a prototype of
		//  something.
		if (typeof parent === 'object' && parent.hasOwnProperty('constructor')) {
			parent = parent.constructor;
		}

		if((instance.constructor === parent) || (instance instanceof parent)) {
			return true;
		}

		return classIsA(instance.constructor, parent);
	}

	/**
	 * Does duck typing to determine if an instance object implements a protocol.
	 * <p>The protocol may be either an adhoc protocol, in which case it is an object or it can be a formal protocol in
	 *  which case it's a function.</p>
	 *
	 * <p>In an adhoc protocol, you can use Number, Object, String and Boolean to indicate the type required on the
	 *  instance.</p>
	 *
	 * @memberOf topiarist
	 * @param {Object} instance the object to check.
	 * @param {function|Object} protocol the description of the properties that the object should have.
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

			if (required === Number) {
				if (type !== 'number') {
					return false;
				}
			} else if (required === Object) {
				if (type !== 'object') {
					return false;
				}
			} else if (required === String) {
				if (type !== 'string') {
					return false;
				}
			} else if (required === Boolean) {
				if (type !== 'boolean') {
					return false;
				}
			} else {
				if (type !== typeof required) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Checks that a class provides a prototype that will fulfil a protocol.
	 *
	 * @memberOf topiarist
	 * @param {function} classDefinition
	 * @param {function|Object} protocol
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
		if (sub.prototype[propertyName] === ancestor.prototype[propertyName]) {
			return false;
		}

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
	 * @private
	 */
	function msg(str) {
		if (str == null) {
			return null;
		}

		for (var i = 1, len = arguments.length; i < len; ++i) {
			str = str.replace('{' + (i - 1) + '}', String(arguments[i]));
		}

		return str;
	}

	/**
	 * Returns a nonenumerable property if it exists, or creates one and returns that if it does not.
	 * @private
	 */
	function nonenum(object, propertyName, defaultValue) {
		var value = object[propertyName];

		if (typeof value === 'undefined') {
			value = defaultValue;
			Object.defineProperty(object, propertyName, {
				enumerable: false,
				value: value
			});
		}

		return value;
	}

	/**
	 * Easier for us if we treat everything as functions with prototypes. This function makes plain objects behave that
	 *  way.
	 * @private
	 */
	function toFunction(obj, couldNotCastError) {
		if (obj == null) {
			throw couldNotCastError;
		}

		var result;
		if (typeof obj === 'object') {
			if (obj.hasOwnProperty('constructor')) {
				if (obj.constructor.prototype !== obj) throw couldNotCastError;
				result = obj.constructor;
			} else {
				var EmptyInitialiser = function() {};
				EmptyInitialiser.prototype = obj;
				Object.defineProperty(obj, 'constructor', {
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
	 * Returns the nonenumerable property __id__ of an object if it exists, otherwise adds one and returns that.
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
	 * Gets the classname of an object or function if it can.  Otherwise returns the provided default. Getting the name
	 *  of a function is not a standard feature, so while this will work in many cases, it should not be relied upon
	 *  except for informational messages (e.g. logging and Error messages).
	 * @private
	 */
	function className(object, defaultName) {
		if (object == null) {
			return defaultName;
		}

		var result = '';
		if (typeof object === 'function') {
			if (object.name) {
				result = object.name;
			} else {
				var match = object.toString().match(nameFromToStringRegex);
				if (match !== null) {
					result = match[1];
				}
			}
		} else if (typeof object.constructor === 'function') {
			result = className(object.constructor, defaultName);
		}

		return result || defaultName;
	}

	/**
	 * Returns an array of all of the properties on a protocol that are not on classdef or are of a different type on
	 *  classdef.
	 * @private
	 */
	function missingAttributes(classdef, protocol) {
		var result = [], obj = classdef.prototype, requirement = protocol.prototype;
		var item;
		for (item in requirement) {
			if (typeof obj[item] !== typeof requirement[item]) {
				result.push(item);
			}
		}

		for (item in protocol) {
			var protocolItemType = typeof protocol[item];
			if (protocol.hasOwnProperty(item) && protocolItemType === 'function' && typeof classdef[item] !== protocolItemType) {
				// If we're in ie8, our internal variables won't be nonenumerable, so we include a check for that here.
				if (internalUseNames.indexOf(item) < 0) {
					result.push(item + ' (class method)');
				}
			}
		}

		return result;
	}

	/**
	 * Copies all properties from the source to the target (including inherited properties) and optionally makes them
	 *  not enumerable.
	 * @private
	 */
	function copy(source, target, hidden) {
		for (var key in source) {
			Object.defineProperty(target, key, {
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
		return function() {
			var args = [this].concat(slice.call(arguments));
			return func.apply(null, args);
		};
	}

	/**
	 * Mixin functions are sandboxed into their own instance.
	 * @private
	 */
	function getSandboxedFunction(myMixId, mix, func) {
		var result = function() {
			var mixInstances = nonenum(this, '__multiparentInstances__', []);
			var mixInstance = mixInstances[myMixId];
			if (mixInstance == null) {
				if (typeof mix === 'function') {
					mixInstance = new mix();
				} else {
					mixInstance = Object.create(mix);
				}
				// could add a nonenum pointer to __this__ or something if we wanted to allow escape from the sandbox.
				mixInstances[myMixId] = mixInstance;
			}
			return func.apply(mixInstance, arguments);
		};

		nonenum(result, '__original__', func);
		nonenum(result, '__source__', mix);

		return result;
	}

	/**
	 * Clears the `__assignable_from_cache__` cache for target and parent.
	 * @private
	 */
	function clearAssignableCache(target, parent) {
		if ('__assignable_from_cache__' in target) {
			delete target.__assignable_from_cache__[classId(parent)];
		}
	}


	function getPrototypeOf(obj) {
		if (Object.getPrototypeOf) {
			var proto = Object.getPrototypeOf(obj);

			// to avoid bad shams...
			if (proto !== obj) {
				return proto;
			}
		}

		// this is what most shams do, but sometimes it's wrong.
		if (obj.constructor && obj.constructor.prototype && obj.constructor.prototype !== obj) {
			return obj.constructor.prototype;
		}

		// this works only if we've been kind enough to supply a superclass property (which we do when we extend classes)
		if (obj.constructor && obj.constructor.superclass) {
			return obj.constructor.superclass.prototype;
		}

		// can't find a good prototype.
		return null;
	}


	// Exporting ///////////////////////////////////////////////////////////////////////////////////

	var methods = {
		'extend': extend, 'inherit': inherit, 'mixin': mixin, 'implement': implement,
		'hasImplemented': hasImplemented, 'classIsA': classIsA, 'isAssignableFrom': classIsA,
		'isA': isA, 'fulfills': fulfills, 'classFulfills': classFulfills
	};

	/* jshint evil:true */
	var global = (new Function('return this;'))();

	var exporting = {
		'exportTo': function(to) {
			copy(methods, to || global, true);
		},
		'install': function(target) {
			if (arguments.length > 0 && typeof target !== 'function') {
				throw new Error(msg(ERROR_MESSAGES.BAD_INSTALL, typeof target));
			}
			var isGlobalInstall = arguments.length < 1

			copy({
				isA: makeMethod(methods.isA),
				fulfills: makeMethod(methods.fulfills)
			}, isGlobalInstall ? Object.prototype : target.prototype, true);

			var itemsToInstallToFunction = {
				'classIsA': makeMethod(methods.classIsA),
				'implements': makeMethod(methods.implement),
				'hasImplemented': makeMethod(methods.hasImplemented),
				'fulfills': makeMethod(methods.classFulfills),
				// we can 'extend' a superclass to make a subclass.
				'extend': function(properties) {
					if (typeof properties === 'function') {
						return extend(properties, this);
					}
					return extend(null, this, properties);
				},
				'mixin': makeMethod(methods.mixin),
				'inherits': makeMethod(methods.inherit)
			};
			if (isGlobalInstall) {
				// no point in having subclass.extends unless it's global.
				itemsToInstallToFunction['extends'] = makeMethod(methods.extend);
			}

			copy(itemsToInstallToFunction, isGlobalInstall ? Function.prototype : target, isGlobalInstall);

			return target;
		}
	};
	exporting['export'] = exporting.exportTo; // for backwards compatibility

	methods.Base = exporting.install(function BaseClass() {});

	copy(methods, exporting);

	// not sure if this works in node-jasmine....
	if ('jasmine' in global) {
		var err = {};
		var getErr = function(key) {
			return function() {
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
