/**
 * @module br/workbench/ui/WorkbenchComponent
 */

var Errors = require( 'br/Errors' );

/**
 * @interface
 * @class
 * @alias module:br/workbench/ui/WorkbenchComponent
 * 
 * @classdesc
 * Represents a generic component that can be added to a {@link module:br/workbench/ui/WorkbenchPanel}.
 */
function WorkbenchComponent() {
}

/**
 * @return the top level element
 * @type DOMElement
 */
WorkbenchComponent.prototype.getElement = function() {
	throw new Errors.CustomError(Errors.UNIMPLEMENTED_INTERFACE, "WorkbenchComponent.getElement() has not been implemented.");
};

module.exports = WorkbenchComponent;
