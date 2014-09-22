/**
 * @module br/presenter/node/NodeListListener
 */

/**
 * @class
 * @interface
 * @alias module:br/presenter/node/NodeListListener
 * 
 * @classdesc
 * Interface implemented by classes that wish to listen to {@link module:br/presenter/node/NodeList} change events.
 */
function NodeListListener() {
}

/**
 * Callback method invoked when the list of nodes in a {@link module:br/presenter/node/NodeList} change.
 * 
 * <p>Implementation of this method is optional, and no action will be taken if the method is invoked but has not
 * been overridden.</p>
 */
NodeListListener.prototype.onNodeListChanged = function() {
	// optional callback
};

br.presenter.node.NodeListListener = NodeListListener;
