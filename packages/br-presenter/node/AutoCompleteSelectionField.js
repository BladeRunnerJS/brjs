'use strict';

var SelectionField = require('br-presenter/node/SelectionField');
var Errors = require('br/Errors');
var AutoCompleteProvider = require('br-presenter/node/AutoCompleteProvider');
var Core = require('br/Core');

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
function AutoCompleteSelectionField(oProperty, oAutoCompleteProvider) {
	if (!Core.fulfills(oAutoCompleteProvider, AutoCompleteProvider)) {
		throw new Errors.InvalidParametersError('oAutoCompleteProvider must implement br.presenter.node.AutoCompleteProvider');
	}
	SelectionField.call(this, [], oProperty);
	this.m_oAutoCompleteProvider = oAutoCompleteProvider;
}

Core.extend(AutoCompleteSelectionField, SelectionField);

/**
 * @private
 */
AutoCompleteSelectionField.prototype.getAutoCompleteList = function(sTerm, fCallback) {
	this.m_oAutoCompleteProvider.getList(sTerm, fCallback);
};

/**
 * @private
 */
AutoCompleteSelectionField.prototype.isValidOption = function(sOption) {
	return this.m_oAutoCompleteProvider.isValidOption(sOption);
};

module.exports = AutoCompleteSelectionField;
