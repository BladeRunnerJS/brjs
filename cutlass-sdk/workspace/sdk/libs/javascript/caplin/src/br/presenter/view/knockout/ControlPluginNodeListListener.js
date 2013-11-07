/**
 * @private
 * @constructor
 * @param {Function} fControlHandler
 *
 * @implements br.presenter.node.NodeListListener
 */
br.presenter.view.knockout.ControlPluginNodeListListener = function(fControlHandler)
{
	this.m_fControlHandler = fControlHandler;
};

br.inherit(br.presenter.view.knockout.ControlPluginNodeListListener, br.presenter.node.NodeListListener);

br.presenter.view.knockout.ControlPluginNodeListListener.prototype.onNodeListRendered = function()
{
	var fControlHandler = this.m_fControlHandler;
	fControlHandler();
};
