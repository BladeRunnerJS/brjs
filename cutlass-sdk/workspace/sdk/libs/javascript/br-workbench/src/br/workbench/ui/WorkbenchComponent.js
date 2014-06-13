var Errors = require( 'br/Errors' );

/**
 * @name br.workbench.ui.WorkbenchComponent
 * @class
 * @interface
 * <p>Represents a generic component that can be added to a {@link br.workbench.ui.WorkbenchPanel}</p>
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
