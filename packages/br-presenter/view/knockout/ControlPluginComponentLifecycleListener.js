'use strict';

/**
 * @module br/presenter/view/knockout/ControlPluginComponentLifecycleListener
 */

/**
 * @private
 * @class
 * @alias module:br/presenter/view/knockout/ControlPluginComponentLifecycleListener
 * 
 * @param {Object} oControlAdaptor
 */
function ControlPluginComponentLifecycleListener(oControlAdaptor) {
	this.m_oControlAdaptor = oControlAdaptor;
	this.m_oControlAdaptor.bDestroyed = false;
	this.m_bViewReady = false;
}

ControlPluginComponentLifecycleListener.prototype.onOpen = function(nWidth, nHeight) {
	if (this.m_bViewReady === false) {
		this.m_bViewReady = true;
		this.m_oControlAdaptor.onViewReady();
	}
};

ControlPluginComponentLifecycleListener.prototype.onClose = function() {
	this.m_oControlAdaptor.destroy();
	this.m_oControlAdaptor.bDestroyed = true;
};

ControlPluginComponentLifecycleListener.prototype.ensureViewReady = function() {
	if (this.m_bViewReady === false) {
		this.m_bViewReady = true;
		this.m_oControlAdaptor.onViewReady();
	}
};

module.exports = ControlPluginComponentLifecycleListener;
