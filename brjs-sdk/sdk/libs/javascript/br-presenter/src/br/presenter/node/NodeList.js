/**
 * @module br/presenter/node/NodeList
 */

/**
 * Constructs a new instance of <code>NodeList</code> containing the given
 * {@link module:br/presenter/node/PresentationNode} instances.
 * 
 * @class
 * @alias module:br/presenter/node/NodeList
 * @extends module:br/presenter/node/PresentationNode
 * 
 * @classdesc
 * <code>NodeList</code> is a {@link module:br/presenter/node/PresentationNode}, that is itself a
 * list of presentation nodes.
 * 
 * <p>The <code>NodeList</code> class is useful when you have a list of nodes of the same type
 * that all need to be rendered to screen. It supports the case where some or all of the nodes
 * need to be rendered with different templates.</p>
 * 
 * <p>The contents of the <code>NodeList</code> class can be modified using the {@link #updateList}
 * method, and this will cause the view to immediately update to reflect the contents of the new
 * list. Node lists whose presentation nodes aren't all rendered with the same template can achieve
 * this by having each of the nodes within the list implement the
 * {@link module:br/presenter/node/TemplateAware} interface.</p>
 * 
 * <p>If <code>fNodeClass</code> is provided, then only instances of that class can be added to the
 * node list; An {@link module:br/Errors/CustomError} will be thrown if this is violated.</p>
 *
 * @param {Array} pPresentationNodes The initial array of {@link module:br/presenter/node/PresentationNode} instances
 * @param {Function} fNodeClass (optional) The class/interface that all nodes in this list should be an instance of
 */
br.presenter.node.NodeList = function(pPresentationNodes, fNodeClass)
{
	// call super constructor
	br.presenter.view.knockout.KnockoutNodeList.call(this);

	/** @private */
	this.m_fPermittedClass = fNodeClass || br.presenter.node.PresentationNode;

	/** @private */
	this.m_pUpdateListeners = [];

	/** @private */
	this.m_oObservable = new br.util.Observable();

	/** @private */
	this.m_oChangeListenerFactory = new br.util.ListenerFactory(br.presenter.node.NodeListListener, "onNodeListChanged");

	this._copiesAndChecksNodesAndClearsNodePaths(pPresentationNodes);
};
br.Core.extend(br.presenter.node.NodeList, br.presenter.node.PresentationNode);
br.Core.inherit(br.presenter.node.NodeList, br.presenter.view.knockout.KnockoutNodeList);

/**
 * Returns the list of {@link module:br/presenter/node/PresentationNode} instances as an array.
 *
 * @type Array
 */
br.presenter.node.NodeList.prototype.getPresentationNodesArray = function()
{
	return this.m_pItems;
};

/**
 * Returns the name of the template used to render the given presentation node.
 *
 * @param {module:br/presenter/node/PresentationNode} oPresentationNode The presentation node being queried.
 * @type String
 */
br.presenter.node.NodeList.prototype.getTemplateForNode = function(oPresentationNode)
{
	return oPresentationNode.getTemplateName();
};

/**
 * Updates the node list with a new array of {@link module:br/presenter/node/PresentationNode} instances.
 *
 * <p>Care must be taken to always invoke this method when the contents of the node list change. The
 * array returned by {@link #getPresentationNodesArray} should be treated as being immutable.</p>
 *
 * @param {Array} pPresentationNodes The new list of {@link module:br/presenter/node/PresentationNode} instances.
 */
br.presenter.node.NodeList.prototype.updateList = function(pPresentationNodes)
{
	this._copiesAndChecksNodesAndClearsNodePaths(pPresentationNodes);
	this._setPathsOfNewlyAddedNodes();

	for(var i = 0; i < this.m_pUpdateListeners.length; i++){
		 this.m_pUpdateListeners[i]();
	}

	this.updateView(this.m_pItems);
	this.m_oObservable.notifyObservers("onNodeListChanged");
	return this;
};

/**
 * Add a {@link module:br/presenter/node/NodeListListener} that will be notified
 * each time the node list is updated.
 *
 * @param {module:br/presenter/node/NodeListListener} oListener The listener to be added.
 * @param {boolean} bNotifyImmediately Whether to invoke the listener immediately using the current node list.
 * @type br.presenter.node.NodeList
 */
br.presenter.node.NodeList.prototype.addListener = function(oListener, bNotifyImmediately)
{
	if(!br.Core.fulfills(oListener, br.presenter.node.NodeListListener))
	{
		throw new br.Errors.InvalidParametersError("oListener was not an instance of NodeListListener");
	}

	this.m_oObservable.addObserver(oListener);

	if(bNotifyImmediately) {
		oListener.onNodeListChanged();
	}

	return this;
};

/**
 * Remove a previously added {@link module:br/presenter/node/NodeListListener}.
 *
 * @param {module:br/presenter/node/NodeListListener} oListener The listener being removed.
 * @type br.presenter.node.NodeList
 */
br.presenter.node.NodeList.prototype.removeListener = function(oListener)
{
	this.m_oObservable.removeObserver(oListener);
	return this;
};

/**
 * Remove all previously added {@link module:br/presenter/node/NodeListListener} instances.
 *
 * @type br.presenter.node.NodeList
 */
br.presenter.node.NodeList.prototype.removeAllListeners = function()
{
	this.m_oObservable.removeAllObservers();
	return this;
};

/**
 * Convenience method that allows listeners to be added for objects that do
 * not themselves implement {@link module:br/presenter/node/NodeListListener}.
 *
 * <p>Listeners added using <code>addChangeListener()</code> will only be notified
 * when {@link module:br/presenter/node/NodeListListener#onNodeListChanged} fires, and
 * will not be notified if any of the other
 * {@link module:br/presenter/node/NodeListListener} call-backs fire. The advantage to
 * using this method is that objects can choose to listen to call-back events on multiple
 * node lists.</p>
 *
 * @param {Object} oListener The listener to be added.
 * @param {String} sMethod The name of the method on the listener that will be invoked each time the property changes.
 * @param {boolean} bNotifyImmediately (optional) Whether to invoke the listener immediately for the current value.
 * @type br.presenter.node.NodeListListener
 */
br.presenter.node.NodeList.prototype.addChangeListener = function(oListener, sMethod, bNotifyImmediately)
{
	var oNodeListListener = this.m_oChangeListenerFactory.createListener(oListener, sMethod);
	this.addListener(oNodeListListener, bNotifyImmediately);

	return oNodeListListener;
};

/**
 * @private
 */
br.presenter.node.NodeList.prototype._$getObservable = function()
{
	return this.m_oObservable;
};

/**
 * @private
 */
br.presenter.node.NodeList.prototype._$setPath = function(sPath, oPresenterComponent)
{
	this.m_sPath = sPath;
	this.__oPresenterComponent = oPresenterComponent;
	this._setPathsOfNewlyAddedNodes();
};

/**
 * @private
 */
br.presenter.node.NodeList.prototype._$clearNodePaths = function()
{
	for(var i = 0; i < this.m_pItems.length; i++)
	{
		this.m_pItems[i]._$clearNodePaths();
	}
	this.m_sPath = undefined;
};


// *********************** Private Methods ***********************

/**
 * @private
 */
br.presenter.node.NodeList.prototype._getNodes = function(sNodeName, pPropertyList, pNodes)
{
	for(var i = 0, l = this.m_pItems.length; i < l; ++i)
	{
		var oPresentationNode = this.m_pItems[i];

		if(this._nodeMatchesQuery(oPresentationNode, i, sNodeName, pPropertyList))
		{
			pNodes.push(oPresentationNode);
		}

		oPresentationNode._getNodes(sNodeName, pPropertyList, pNodes);
	}
};

/**
 * @private
 */
br.presenter.node.NodeList.prototype._copyAndCheckNodes = function(pNodes)
{
	var pResult = [];
	if (!pNodes || !pNodes.length) return pResult;

	for(var i = 0; i < pNodes.length; i++){
		var oNode = pNodes[i];
		if(this.m_fPermittedClass && !(oNode instanceof this.m_fPermittedClass)) {
			var msg =  "Each nodelist item needs to implement br.presenter.node.PresentationNode or a valid fNodeClass";
			throw new br.Errors.CustomError(Error.UNIMPLEMENTED_INTERFACE, msg);
		}
		pResult.push(oNode);
	}
	return pResult;
};

/**
 * @private
 */
br.presenter.node.NodeList.prototype._copiesAndChecksNodesAndClearsNodePaths = function(pPresentationNodes)
{
	this.m_pItems = this._copyAndCheckNodes(pPresentationNodes);

	for(var i = 0; i < this.m_pItems.length; i++)
	{
		this.m_pItems[i]._$clearNodePaths();
	}
};

/**
 * @private
 */
br.presenter.node.NodeList.prototype._setPathsOfNewlyAddedNodes = function()
{
	if(this.getPath() !== undefined)
	{
		for(var i = 0; i < this.m_pItems.length; i++)
		{
			this.m_pItems[i]._$setPath(this.getPath() + "." + i, this.__oPresenterComponent);
		}
	}
};
