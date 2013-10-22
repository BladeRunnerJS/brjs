
/**
 * @class
 * @interface
 * <p>Represents a generic component that can be added to a {@link br.workbench.ui.WorkbenchPanel}</p>
 */
br.workbench.ui.WorkbenchComponent = function()
{
};

/**
 * @return the top level element
 * @type DOMElement
 */
br.workbench.ui.WorkbenchComponent.prototype.getElement = function()
{
	throw new br.Errors.CustomError(br.Errors.UNIMPLEMENTED_INTERFACE, "WorkbenchComponent.getElement() has not been implemented.");
};
