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
// topiarist.extend(subclass, superclass) - included by 'topiarist.exportTo(exports);' above

/**
* Declares that a class has implemented an interface and throws an exception
* if it does not.
*
* Should be called after the class and all of its methods have been defined.
*
* @see {@link http://bladerunnerjs.github.io/topiarist/}
*   This method is the same as topiarist.hasImplemented(class, interface)
* @method
* @param {Class} class The class implementing the interface.
* @param {Class} interface The interface
*/
// topiarist.hasImplemented(class, interface) - included by 'topiarist.exportTo(exports);' above

/**
* Declares that a class will implement an interface.
* This is similar to {@link exports.hasImplemented} but can be called before
* a class' methods have been defined.
* 
* @see {@link http://bladerunnerjs.github.io/topiarist/}
*   This method is the same as topiarist.implement(class, interface)
* @param {Class} class The class implementing the interface.
* @param {Class} interface The interface
*/
// topiarist.implement(class, interface) - included by 'topiarist.exportTo(exports);' above

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
// topiarist.inherit(class, parent) - included by 'topiarist.exportTo(exports);' above

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
// topiarist.mixin(class, mixin) - included by 'topiarist.exportTo(exports);' above

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
// topiarist.isA(instance, parent) - included by 'topiarist.exportTo(exports);' above

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
// topiarist.classIsA(class, parent) - included by 'topiarist.exportTo(exports);' above

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
// topiarist.fulfills(instance, interface) - included by 'topiarist.exportTo(exports);' above

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
// topiarist.classFulfills(instance, interface) - included by 'topiarist.exportTo(exports);' above

exports.thirdparty = function(library){};
