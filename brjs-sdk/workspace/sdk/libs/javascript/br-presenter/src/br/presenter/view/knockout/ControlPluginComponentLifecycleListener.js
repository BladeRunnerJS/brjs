/**
 * @module br/presenter/view/knockout/ControlPluginComponentLifecycleListener
 */

/**
 * @private
 * @class
 * @param {Object} oControlAdaptor
 */
br.presenter.view.knockout.ControlPluginComponentLifecycleListener = function(oControlAdaptor)
{
	this.m_oControlAdaptor = oControlAdaptor;
	this.m_oControlAdaptor.bDestroyed = false;
	this.m_bViewReady = false;
};

br.presenter.view.knockout.ControlPluginComponentLifecycleListener.prototype.onOpen = function(nWidth, nHeight) {
	if (this.m_bViewReady === false) {
		this.m_bViewReady = true;
		this.m_oControlAdaptor.onViewReady();
	}
};

br.presenter.view.knockout.ControlPluginComponentLifecycleListener.prototype.onClose = function() {
	this.m_oControlAdaptor.destroy();
	this.m_oControlAdaptor.bDestroyed = true;
};

br.presenter.view.knockout.ControlPluginComponentLifecycleListener.prototype.ensureViewReady = function() {
	if (this.m_bViewReady === false) {
		this.m_bViewReady = true;
		this.m_oControlAdaptor.onViewReady();
	}
};