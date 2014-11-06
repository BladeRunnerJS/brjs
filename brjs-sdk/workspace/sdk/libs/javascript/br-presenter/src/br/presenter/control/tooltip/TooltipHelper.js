'use strict';

/**
 * @module br/presenter/control/tooltip/TooltipHelper
 */

var Errors = require('br/Errors');

/**
 * This is an interface and should not be constructed.
 *
 * @class
 * @interface
 * @alias module:br/presenter/control/tooltip/TooltipHelper
 *
 * @classdesc
 * Interface that must be implemented by classes that provide tooltip creation and update logic. Classes implementing
 *  this interface will be used by the {@link module:br/presenter/control/tooltip/TooltipControl} control, which will
 *  obtain it trough the `br.presenter.tooltip-helper` alias.
 */
function TooltipHelper() {
}

/**
 * Sets new text for the tooltip.
 * @param {String} message The message to display in the tooltip.
 * @return {module:br/presenter/control/tooltip/TooltipHelper} Returns the `this` pointer, so calls can be chained.
 */
TooltipHelper.prototype.updateTooltip = function(/*message*/) {
	throw new Errors.UnimplementedInterfaceError('TooltipHelper#updateTooltip() has not been implemented.');
};

/**
 * Sets the container element in which the tooltip will be contained.
 * @param {DOMElement} container The container element.
 * @return {module:br/presenter/control/tooltip/TooltipHelper} Returns the `this` pointer, so calls can be chained.
 */
TooltipHelper.prototype.containWithin = function(/*container*/) {
	throw new Errors.UnimplementedInterfaceError('TooltipHelper#containWithin() has not been implemented.');
};

/**
 * Creates the tooltip and points it to the passed in errorElement.
 * @param {DOMElement} errorElement The element to which the tooltip will 'point to'.
 * @return {module:br/presenter/control/tooltip/TooltipHelper} Returns the `this` pointer, so calls can be chained.
 */
TooltipHelper.prototype.pointTo = function(/*errorElement*/) {
	throw new Errors.UnimplementedInterfaceError('TooltipHelper#pointTo() has not been implemented.');
};

/**
 * Removes the tooltip element from the DOM.
 */
TooltipHelper.prototype.remove = function() {
	throw new Errors.UnimplementedInterfaceError('TooltipHelper#remove() has not been implemented.');
};

br.presenter.control.tooltip.TooltipHelper = TooltipHelper;
