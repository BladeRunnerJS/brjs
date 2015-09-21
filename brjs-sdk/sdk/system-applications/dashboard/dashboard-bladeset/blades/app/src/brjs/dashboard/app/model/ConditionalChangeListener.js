'use strict';

var NodeListListener = require('br/presenter/node/NodeListListener');
var PropertyListener = require('br/presenter/property/PropertyListener');
var Core = require('br/Core');

function ConditionalChangeListener(oListener, sMethod, oConditionProperty, vConditionValue) {
	this.m_oListener = oListener;
	this.m_sMethod = sMethod;
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
		this.m_oListener[this.m_sMethod].call(this.m_oListener);
	}
};

module.exports = ConditionalChangeListener;
