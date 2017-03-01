'use strict';

var Errors = require('br/Errors');
var KnockoutObservable = require('br-presenter/view/knockout/KnockoutObservable');
var Core = require('br/Core');

/**
 * @module br/presenter/view/knockout/KnockoutNodeList
 */

var presenter_knockout = require('presenter-knockout');

/**
 * @private
 * @class
 * @alias module:br/presenter/view/knockout/KnockoutNodeList
 */
function KnockoutNodeList() {
	// call super constructor
	KnockoutObservable.call(this);
}

Core.extend(KnockoutNodeList, KnockoutObservable);

/**
 * @private
 */
KnockoutNodeList.prototype.getValueForKnockout = function() {
	if (arguments.length > 0) {
		throw new Errors.InvalidParametersError('getValueForKnockout cannot write as we do not support user editable NodeLists');
	}

	presenter_knockout.dependencyDetection.registerDependency(this); // The caller only needs to be notified of changes if they did a "read" operation
	return this.m_pItems;
};

KnockoutNodeList.prototype.peek = function() {
	return this.m_pItems;
};

/**
 * @private
 */
KnockoutNodeList.prototype.getTemplateName = function(oViewModel) {
	return oViewModel.getTemplateName();
};

module.exports = KnockoutNodeList;
