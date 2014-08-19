/**
 * @module br/presenter/node/NodeListListener
 */

/**
 * @description
 * Interface implemented by classes that wish to listen to {@link module:br/presenter/node/NodeList} change events.
 * 
 * @class
 * @interface
 */
br.presenter.node.NodeListListener = function()
{
};

/**
 * Callback method invoked when the list of nodes in a {@link module:br/presenter/node/NodeList} change.
 * 
 * <p>Implementation of this method is optional, and no action will be taken if the method is invoked but has not
 * been overridden.</p>
 */
br.presenter.node.NodeListListener.prototype.onNodeListChanged = function()
{
	// optional callback
};
