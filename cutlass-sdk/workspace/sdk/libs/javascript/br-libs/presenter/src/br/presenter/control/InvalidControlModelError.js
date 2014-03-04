/**
 * @class
 * The exception thrown when {@link br.presenter.control.ControlAdaptor#setPresentationNode} is invoked with an
 * incompatible presentation node for the control being used.
 * 
 * @constructor
 * @param {String} sControlAdaptor The class name of the control adaptor that's been invoked.
 * @param {String} sAcceptedControlModel The particular class of the presentation node this control adaptor accepts.
 * @extends br.Errors.CustomError
 */
br.presenter.control.InvalidControlModelError = function(sControlAdaptor, sAcceptedControlModel)
{
	var message = "Attempt to bind '" + sControlAdaptor + "' to a presentation node that is not of type '" + sAcceptedControlModel + "'";
	br.Errors.CustomError.call(this, "InvalidControlModelError", message);
};

br.Core.extend(br.presenter.control.InvalidControlModelError, br.Errors.CustomError);
