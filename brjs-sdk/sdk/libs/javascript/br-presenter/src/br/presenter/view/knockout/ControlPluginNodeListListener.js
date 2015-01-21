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
br.presenter.view.knockout.ControlPluginNodeListListener = function(fControlHandler)
{
	this.m_fControlHandler = fControlHandler;
};

br.Core.inherit(br.presenter.view.knockout.ControlPluginNodeListListener, br.presenter.node.NodeListListener);

br.presenter.view.knockout.ControlPluginNodeListListener.prototype.onNodeListRendered = function()
{
	var fControlHandler = this.m_fControlHandler;
	fControlHandler();
};
