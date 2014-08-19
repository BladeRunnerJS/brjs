/**
 * @module br/presenter/property/PropertyListener
 */

/**
 * @interface
 * @description
 * Interface implemented by classes that wish to listen to property change events.
 *
 * @class
 */
br.presenter.property.PropertyListener = function()
{
};

/**
 * Callback method invoked when the value of a {@link module:br/presenter/property/Property} is updated, even when it hasn't changed.
 *
 * <p>This event is there to accommodate cases where we want to know about calls to set the value of a property even if the
 * new value being set is the same as the old one.</p>
 *
 * <p>Implementation of this method is optional, and no action will be taken if the method is invoked but has not
 * been overridden.</p>
 */
br.presenter.property.PropertyListener.prototype.onPropertyUpdated = function()
{
	// optional callback
};

/**
 * Callback method invoked when the value of a {@link module:br/presenter/property/Property} changes.
 * 
 * <p>Implementation of this method is optional, and no action will be taken if the method is invoked but has not
 * been overridden.</p>
 */
br.presenter.property.PropertyListener.prototype.onPropertyChanged = function()
{
	// optional callback
};

/**
 * Callback method invoked when an attempt to update an {@link module:br/presenter/property/EditableProperty}
 * does not result in a validation error.
 * 
 * <p>Implementation of this method is optional, and no action will be taken if the method is invoked but has not
 * been overridden.</p>
 */
br.presenter.property.PropertyListener.prototype.onValidationSuccess = function()
{
	// optional callback
};

/**
 * Callback method invoked when an attempt to update an {@link module:br/presenter/property/EditableProperty}
 * results in a validation error. 
 * 
 * <p>Implementation of this method is optional, and no action will be taken if the method is invoked but has not
 * been overridden.</p>
 * 
 * @param {Object} vPropertyValue The value that led to the validation error.
 * @param {String} sErrorMessage The description of the validation error.
 */
br.presenter.property.PropertyListener.prototype.onValidationError = function(vPropertyValue, sErrorMessage)
{
	// optional callback
};

/**
 * Callback method invoked once validation is complete.
 * 
 * <p>Implementation of this method is optional, and no action will be taken if the method is invoked but has not
 * been overridden.</p>
 */
br.presenter.property.PropertyListener.prototype.onValidationComplete = function()
{
	// optional callback
};
