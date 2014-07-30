"use strict";

/**
* @module br/component/Component
*/

var Errors = require('br/Errors');

/**
* @interface
* @class This interface must be implemented by a presentation-level class. A
*        presentation-level class represents something that occupies physical
*        space on the page, such as the content of a panel or a
*        dialog box.
*
* <p> Each implementation of a Component represents a different <b>Component type</b>,
* for example a Grid or a Trade Panel.
*
* <p>A component receives a Frame, on which it can set its content element and attach
* event listeners.
*
* <p>Components that can have their state saved should also implement br.component.Serializable.
*
* @class
* @alias module:br/component/Component
*/
function Component() {}

/**
 * @param {br.component.Frame} frame A frame provided by the layout manager that this component can
 * attach its visual DOM elements into.  Will be called only once.
 */
Component.prototype.setDisplayFrame = function(frame) {
	throw new Errors.UnimplementedAbstractMethodError("Component.setDisplayFrame: Your component needs to implement setDisplayFrame.");
};

module.exports = Component;
