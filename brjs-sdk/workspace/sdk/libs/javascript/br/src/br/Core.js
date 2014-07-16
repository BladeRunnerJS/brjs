"use strict";

/**
* Provides access to Object Oriented JavaScript utilities
* Uses <code>Topiarist</code> to provide most of the OO functionality.
*
* @see {@link http://bladerunnerjs.github.io/topiarist/}
*
* @module br/Core
*/

var topiarist = require('topiarist');
topiarist.exportTo(exports);


/**
* Extend one class from another.
*
* @see {@link http://bladerunnerjs.github.io/topiarist/}
* @name extend
* @memberof module:br/Core
* @static
* @function
* @param {Class} subclass
* @param {Class} superclass
*/
// topiarist.extend(subclass, superclass)

/**
* Delcares that a class has implemented an interface and throws an exception
* if it does not.
*
* Should be called after the class and all of its methods have been defined.
*
* @see {@link http://bladerunnerjs.github.io/topiarist/}
*   This method is the same as topiarist.implement(class, interface)
* @method
* @param {Class} class The class implementing the interface.
* @param {Class} interface The interface
*/
exports.hasImplemented = exports.implement;

/**
* Delcares that a class will implement an interface.
* This is similar to {@link exports.hasImplemented} but can be called before
* a class' methods have been defined.
*
* @param {Class} implementor The class implementing the interface.
* @param {Class} theInterface The interface
*/
exports.implement = function(implementor, theInterface) {
	// We do this on a timeout so you can implement the methods later.
	var br = topiarist;
	var error = new Error();
	setTimeout(function() {
		try {
			br.implement(implementor, theInterface);
		} catch (e) {
			error.message = e.message;
			error.name = e.name;
			throw error;
		}
	}, 0);
};

/**
* Provides multiple inheritance by copying functionality from the parent to the class.
*
* @see {@link http://bladerunnerjs.github.io/topiarist/}
* @name inherit
* @memberof module:br/Core
* @static
* @function
* @param {Class} class
* @param {Class} parent
*/
// topiarist.inherit(class, parent)

/**
* Provides mixin inheritance, sandboxing mixin methods that are copied onto the class.
*
* @see {@link http://bladerunnerjs.github.io/topiarist/}
* @name mixin
* @memberof module:br/Core
* @static
* @function
* @param {Class} class
* @param {Class} mixin
*/
// topiarist.mixin(class, mixin)

/**
* Returns true if the instance is of a type which has been declared to be
* descended from the parent, e.g. because it’s extended or implemented or mixed-in.
*
* @see {@link http://bladerunnerjs.github.io/topiarist/}
* @name isA
* @memberof module:br/Core
* @static
* @function
* @param {Object} instance
* @param {Class} parent
*/
// topiarist.isA(instance, parent)

/**
* Returns true if the class has been declared to be descended from the parent,
* e.g. through extension, implementation, etc.
*
* @see {@link http://bladerunnerjs.github.io/topiarist/}
* @name classIsA
* @memberof module:br/Core
* @static
* @function
* @param {Class}
* @param {Class} parent
*/
// topiarist.classIsA(class, parent)

/**
* Returns true if the instance supports everything on the interface.
*
* @see {@link http://bladerunnerjs.github.io/topiarist/}
* @name fulfills
* @memberof module:br/Core
* @static
* @function
* @param {Class} instance
* @param {Class} interfac}
*/
// topiarist.fulfills(instance, interface)

/**
* Returns true if instances of the class will be created supporting everything on the interface.
*
* @see {@link http://bladerunnerjs.github.io/topiarist/}
* @name classFulfills
* @memberof module:br/Core
* @static
* @function
* @param {Object} instance
* @param {Class} interface
*/
// topiarist.classFulfills(instance, interface)

exports.thirdparty = function(library){};
