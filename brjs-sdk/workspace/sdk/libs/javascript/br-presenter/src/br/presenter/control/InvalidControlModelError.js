/**
 * @module br/presenter/control/InvalidControlModelError
 */

/**
 * @class
 * @alias module:br/presenter/control/InvalidControlModelError
 * @extends module:br/Errors/CustomError
 * 
 * @classdesc
 * The exception thrown when {@link module:br/presenter/control/ControlAdaptor#setPresentationNode} is invoked with an
 * incompatible presentation node for the control being used.
 * 
 * @param {String} sControlAdaptor The class name of the control adaptor that's been invoked.
 * @param {String} sAcceptedControlModel The particular class of the presentation node this control adaptor accepts.
 */
br.presenter.control.InvalidControlModelError = function(sControlAdaptor, sAcceptedControlModel)
{
	var message = "Attempt to bind '" + sControlAdaptor + "' to a presentation node that is not of type '" + sAcceptedControlModel + "'";
	br.Errors.CustomError.call(this, "InvalidControlModelError", message);
};

br.Core.extend(br.presenter.control.InvalidControlModelError, br.Errors.CustomError);
