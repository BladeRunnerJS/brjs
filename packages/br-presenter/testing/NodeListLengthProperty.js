'use strict';

var Core = require('br/Core');
var WritableProperty = require('br-presenter/property/WritableProperty');

/**
 * @module br/presenter/testing/NodeListLengthProperty
 */

/**
 * @class
 * @alias module:br/presenter/testing/NodeListLengthProperty
 * @extends module:br/presenter/property/WritableProperty
 */
function NodeListLengthProperty(oNodeList) {
	// call super constructor
	WritableProperty.call(this, oNodeList.getPresentationNodesArray().length);

	this.m_oNodeList = oNodeList;
	this.addChangeListener(this._onPropertyChanged.bind(this));
}

Core.extend(NodeListLengthProperty, WritableProperty);

NodeListLengthProperty.prototype._onPropertyChanged = function() {
	var nNewLength = this.getValue();
	var pNodes = this.m_oNodeList.getPresentationNodesArray().slice(0, nNewLength);

	for (var i = pNodes.length; i < nNewLength; ++i) {
		var fNodeClass = this.m_oNodeList.m_fPermittedClass;
		pNodes.push(new fNodeClass());
	}

	this.m_oNodeList.updateList(pNodes);
};

module.exports = NodeListLengthProperty;
