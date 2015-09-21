'use strict';

var NodeListListener = require('br/presenter/node/NodeListListener');
var PropertyListener = require('br/presenter/property/PropertyListener');
var Core = require('br/Core');

function ConditionalChangeListener(fCallback, oConditionProperty, vConditionValue) {
	this.m_fCallback = fCallback;
	this.m_oConditionProperty = oConditionProperty;
	this.m_vConditionValue = vConditionValue;

	this.m_oConditionProperty.addChangeListener(this._onConditionPropertyChanged.bind(this));
}

Core.inherit(ConditionalChangeListener, PropertyListener);
Core.inherit(ConditionalChangeListener, NodeListListener);

ConditionalChangeListener.prototype.onPropertyChanged = function() {
	this._sendUpdates();
};

ConditionalChangeListener.prototype.onNodeListChanged = function() {
	this._sendUpdates();
};

ConditionalChangeListener.prototype._onConditionPropertyChanged = function() {
	this._sendUpdates();
};

ConditionalChangeListener.prototype._sendUpdates = function() {
	if (this.m_oConditionProperty.getValue() === this.m_vConditionValue) {
		this.m_fCallback();
	}
};

module.exports = ConditionalChangeListener;
