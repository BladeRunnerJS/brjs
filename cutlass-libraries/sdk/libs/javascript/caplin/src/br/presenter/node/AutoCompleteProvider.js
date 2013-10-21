
/**
 * @class Provides an interface for a provider that can provide auto complete suggestions, typically used 
 * with an {@link br.presenter.node.AutoCopleteSelectionField}
 * 
 * @interface
 */
br.presenter.node.AutoCompleteProvider = function()
{
};

/**
 * Provides the callback with a list of options that match the sTerm. 
 * Note that the sTerm will be used in a "term*" pattern. 
 * 
 * @param {String} sTerm The term to search. 
 * @param {Function} The callback to provide the array of found options as the first argument.
 */
br.presenter.node.AutoCompleteProvider.prototype.getList = function(sTerm, fCallback)
{
	throw new br.Errors.CustomError(br.Errors.UNIMPLEMENTED_INTERFACE, "br.presenter.node.AutoCompleteProvider.getList() has not been implemented.");
};

/**
 * Returns True if the specified option is a valid and selectable option.
 * 
 * @param {String} sOption The option to check.
 * @type boolean 
 * @return True if the option is selectable, false otherwise.
 */
br.presenter.node.AutoCompleteProvider.prototype.isValidOption = function(sValue)
{
	throw new br.Errors.CustomError(br.Errors.UNIMPLEMENTED_INTERFACE, "br.presenter.node.AutoCompleteProvider.getList() has not been implemented.");
};

