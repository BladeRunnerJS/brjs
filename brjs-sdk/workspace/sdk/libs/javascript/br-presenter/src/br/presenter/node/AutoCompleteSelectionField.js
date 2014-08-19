/**
 * @module br/presenter/node/AutoCompleteSelectionField
 */

/**
 * @class
 * @alias module:br/presenter/node/AutoCompleteSelectionField
 * @extends module:br/presenter/node/SelectionField
 * 
 * @classdesc
 * Provides a model for auto complete data, typically used with an Auto Complete Box.
 * 
 * @param {module:br/presenter/property/Property} oProperty The initial value of the auto complete field.
 * @param {module:br/presenter/node/AutoCompleteProvider} oAutoCompleteProvider The provider of the auto complete information.
 */
br.presenter.node.AutoCompleteSelectionField = function(oProperty, oAutoCompleteProvider)
{
	if (!br.Core.fulfills(oAutoCompleteProvider, br.presenter.node.AutoCompleteProvider))
	{
		throw new br.Errors.InvalidParametersError("oAutoCompleteProvider must implement br.presenter.node.AutoCompleteProvider");
	}
	br.presenter.node.SelectionField.call(this, [], oProperty);
	this.m_oAutoCompleteProvider = oAutoCompleteProvider;
};

br.Core.extend(br.presenter.node.AutoCompleteSelectionField, br.presenter.node.SelectionField);

/**
 * @private
 */
br.presenter.node.AutoCompleteSelectionField.prototype.getAutoCompleteList = function(sTerm, fCallback)
{
	this.m_oAutoCompleteProvider.getList(sTerm, fCallback);
};

/**
 * @private
 */
br.presenter.node.AutoCompleteSelectionField.prototype.isValidOption = function(sOption)
{
	return this.m_oAutoCompleteProvider.isValidOption(sOption);
};
