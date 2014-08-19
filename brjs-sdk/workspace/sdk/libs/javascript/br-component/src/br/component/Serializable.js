/**
* @module br/component/Serializable
*/

var Errors = require('br/Errors');

/**
 * @beta
 * @class
 * @interface
 * @alias module:br/component/Serializable
 * 
 * @description
 * The <code>Serializable</code> interface is implemented by classes that provide a mechanism
 * to serialize their state and recreate instances from a serialized form.
 *
 * <p>The only restriction on the serialized form is that it is a string.  A class may change
 * its serialized form, however if it does so and wishes to maintain backwards compatibility
 * with current serialized forms, it will need the deserialize method to cope with multiple
 * different versions of serialized form.  For this reason, it's good practice to put the
 * serialization version at the beginning of the serialized form.
 *
 * <p>It is not the responsibility of a Serializable class to include its classname in its
 * serialized form.
 */
function Serializable() {}

/**
 * @returns a string representation of the current state of this component. May not be null.
 * @type {string}
 */
Serializable.prototype.serialize = function() {
	throw new Errors.UnimplementedAbstractMethodError("Serializable.serialize: Implementations of Serializable must provide a serialize method that returns a string containing the serialized form of that object.");
};

/**
 * @param {string} serializedForm a string representation of the state of a component.  May not be null.
 * @type {br.component.Serializable}
 * @returns a newly created instance of this class that has a state matching this serialized form.
 */
Serializable.deserialize = function(serializedForm) {
	throw new Errors.UnimplementedAbstractMethodError("Serializable.deserialize: Implementations of Serializable must provide a serialize method that accept a string containing the serialized form of an object of this class and returns a instance of this class.");
};

module.exports = Serializable;
