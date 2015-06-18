'use strict';

var Errors = require('br/Errors');
var Property = require('br/presenter/property/Property');
var Core = require('br/Core');
var Option = require('br/presenter/node/Option');
var NodeList = require('br/presenter/node/NodeList');

/**
 * @module br/presenter/node/OptionsNodeList
 */

/**
 * Constructs a new <code>OptionsNodeList</code> instance.
 * 
 * @class
 * @alias module:br/presenter/node/OptionsNodeList
 * @extends module:br/presenter/node/NodeList
 * 
 * @classdesc
 * The <code>OptionsNodeList</code> class is used to store the options available within
 * {@link module:br/presenter/node/SelectionField} and {@link br/presenter/node/MultiSelectionField}
 * instances.
 * 
 * @param {Object} vOptions The set of available options, either as an array (keys only) or a map (keys to label).
 */
function OptionsNodeList(vOptions) {
	var pOptions = this._getOptionObjects(vOptions);
	NodeList.call(this, pOptions, Option);
}

Core.extend(OptionsNodeList, NodeList);

/**
 * Retrieve the array of {@link module:br/presenter/node/Option} instances contained by this object.
 *
 * @returns {Array}
 */
OptionsNodeList.prototype.getOptions = function() {
	return this.getPresentationNodesArray();
};

/**
 * Retrieve an array of values for each {@link module:br/presenter/node/Option} contained within this object.
 *
 * @returns {Array}
 */
OptionsNodeList.prototype.getOptionValues = function() {
	var pNodes = this.getOptions();
	var result = [];
	for (var i = 0, max = pNodes.length; i < max; i++) {
		result.push(pNodes[i].value.getValue());
	}

	return result;
};

/**
 * Retrieve an array of labels for each {@link module:br/presenter/node/Option} contained within this object.
 *
 * @returns {Array}
 */
OptionsNodeList.prototype.getOptionLabels = function() {
	var pNodes = this.getOptions();
	var result = [];
	for (var i = 0, max = pNodes.length; i < max; i++) {
		result.push(pNodes[i].label.getValue());
	}

	return result;
};

/**
 * Reset the list of available options using the given array or map.
 *
 * @param {Object} vOptions The set of available options, either as an array (keys only) or a map (keys to label).
 */
OptionsNodeList.prototype.setOptions = function(vOptions) {
	this.updateList(vOptions);
};

/**
 * Retrieve the first option in the list &mdash; typically the default option.
 *
 * @returns {br.presenter.node.Option}
 */
OptionsNodeList.prototype.getFirstOption = function() {
	var pOptions = this.getOptions();
	if (pOptions.length == 0) {
		return null;
	}
	return pOptions[0];
};

/**
 * Retrieve the option with the given label. (If there is more than one option which has the given label,
 * the first instance is returned.)
 *
 * @param {String} sLabel Label to search.
 * @returns {@link module:br/presenter/node/Option}
 */
OptionsNodeList.prototype.getOptionByLabel = function(sLabel) {
	var pNodes = this.getOptions();
	for (var i = 0, max = pNodes.length; i < max; i++) {
		if (pNodes[i].label.getValue() === sLabel) {
			return pNodes[i];
		}
	}

	return null;
};

/**
 * Retrieve the option with the given unique value.
 *
 * @param {String} sValue Value to search.
 * @param {@link module:br/presenter/node/Option}
 */
OptionsNodeList.prototype.getOptionByValue = function(sValue) {
	var pNodes = this.getOptions();
	for (var i = 0, max = pNodes.length; i < max; i++) {
		if (pNodes[i].value.getValue() === sValue) {
			return pNodes[i];
		}
	}

	return null;
};

/**
 * @private
 */
OptionsNodeList.prototype.updateList = function(vOptions) {
	var pOptions = this._getOptionObjects(vOptions);
	NodeList.prototype.updateList.call(this, pOptions);
};

/**
 * @private
 */
OptionsNodeList.prototype._getOptionObjects = function(vOptions) {
	vOptions = vOptions || [];
	if (vOptions instanceof Property) {
		throw new Errors.InvalidParametersError('OptionsNodeList only accepts maps or arrays');
	}

	var pResult = [];

	if (Object.prototype.toString.call(vOptions) === '[object Array]') {
		for (var i = 0; i < vOptions.length; i++) {
			if (vOptions[i] instanceof Option) {
				pResult.push(vOptions[i]);
			} else {
				var option = new Option(vOptions[i], vOptions[i]);
				pResult.push(option);
			}
		}
	} else {
		for (var sKey in vOptions) {
			var option = new Option(sKey, vOptions[sKey]);
			pResult.push(option);
		}
	}
	return pResult;
};

module.exports = OptionsNodeList;
