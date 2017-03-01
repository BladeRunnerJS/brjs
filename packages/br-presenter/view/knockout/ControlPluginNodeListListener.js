'use strict';

var NodeListListener = require('br-presenter/node/NodeListListener');
var Core = require('br/Core');

/**
 * @module br/presenter/view/knockout/ControlPluginNodeListListener
 */

/**
 * @private
 * @class
 * @alias module:br/presenter/view/knockout/ControlPluginNodeListListener
 * @implements module:br/presenter/node/NodeListListener
 * 
 * @param {Function} fControlHandler
 */
function ControlPluginNodeListListener(fControlHandler) {
	this.m_fControlHandler = fControlHandler;
}

Core.inherit(ControlPluginNodeListListener, NodeListListener);

ControlPluginNodeListListener.prototype.onNodeListRendered = function() {
	var fControlHandler = this.m_fControlHandler;
	fControlHandler();
};

module.exports = ControlPluginNodeListListener;
