/**
 * Constructs a new instance of <code>PresentationNode</code>.
 *
 * @class
 * Base class of all complex objects (nodes) within a presentation model.
 *
 * <p>A {@link br.presenter.PresentationModel} is a tree of <code>PresentationNode</code>
 * instances, with instances of {@link br.presenter.property.Property} and <code>Function</code>
 * forming the leafs of the tree. Objects that do not extend <code>PresentationNode</code>
 * are not considered to be part of the presentation model, and are not accessible within the
 * view.</p>
 * <p> When a {@link br.presenter.PresentationModel} is created the <code>_$setPath</code> method is called
 * which throws an exception if the model does not adhere to a tree structure. It also creates a "path" label
 * on each node which identifies the node in standard object notation from the root node.
 * </p>
 * <p> The structure is not strictly a tree because nodes are allowed to hold references back up to their
 * direct ancestors. When any of the (recursive) search functions that find descendant nodes are called these
 *  "back links" are ignored, preventing infinite recursion.
 * </p>
 *
 *
 * @constructor
 */
br.presenter.node.PresentationNode = function()
{
};

/**
 * Returns all nested properties matching the search criteria reachable from this node.
 *
 * <p>Care is taken not to search up the tree in cyclic presentation models (where
 * some of the presentation nodes have back references to presentation nodes higher
 * up in the tree).</p>
 *
 * @param {String} sPropertyName The name of properties to match.
 * @param {Object} vValue The value of properties to match.
 * @type br.presenter.property.Properties
 *
 * @see #nodes
 */
br.presenter.node.PresentationNode.prototype.properties = function(sPropertyName, vValue)
{
	var pNodes = this.nodes().getNodesArray();
	// we need to get properties for the current node
	pNodes.push(this);

	var pProperties = [];

	for(var i = 0, l = pNodes.length; i < l; ++i)
	{
		var oNode = pNodes[i];

		for(var sKey in oNode)
		{
			var vItem = oNode[sKey];
			if(this._isPresenterChild(sKey, vItem)){
				if(vItem instanceof br.presenter.property.Property)
				{
					var oProperty = vItem;

					if((!sPropertyName || (sKey == sPropertyName)) && (!vValue || (oProperty.getValue() == vValue)))
					{
						pProperties.push(oProperty);
					}
				}
			}else{
				continue;
			}
		}
	}

	return new br.presenter.property.Properties(pProperties);
};

/**
 * Returns all nested nodes matching the search criteria reachable from this node.
 *
 * <p>Care is taken not to search up the tree in cyclic presentation models (where
 * some of the presentation nodes have back references to presentation nodes higher
 * up in the tree).</p>
 *
 * @param {String} sNodeName The name of nodes to match.
 * @param {Object} vProperties Only nodes having this array or map of properties will be matched.
 * @type br.presenter.node.Nodes
 *
 * @see #properties
 */
br.presenter.node.PresentationNode.prototype.nodes = function(sNodeName, vProperties)
{
	sNodeName = (sNodeName && (sNodeName != "*")) ? sNodeName : null;
	mProperties = this._convertToMap(vProperties);
	var pNodes = [];
	this._getNodes(sNodeName, mProperties, pNodes);
	return new br.presenter.node.Nodes(pNodes);
};

/**
 * Returns the path that would be required to bind this node from the view.
 *
 * <p>This method is used internally, but might also be useful in allowing the dynamic
 * construction of views for arbitrary presentation models.</p>
 *
 * @type String
 */
br.presenter.node.PresentationNode.prototype.getPath = function()
{
	return this.m_sPath;
};



/**
 * @deprecated This method has been replaced by #removeChildListeners which recurses the node tree.
 * Removes all listeners attached to the properties contained by this <code>PresentationNode</code>.
 */
br.presenter.node.PresentationNode.prototype.removeAllListeners = function()
{
	this.removeChildListeners();
};

/**
 * Removes all listeners attached to the properties contained by this <code>PresentationNode</code>, and any nodes it contains.
 */
br.presenter.node.PresentationNode.prototype.removeChildListeners = function()
{
	this.properties().removeAllListeners();
};

// *********************** Private Methods ***********************

br.presenter.node.PresentationNode.prototype._convertToMap = function(vProperties)
{
	var mProperties;

	if(vProperties instanceof Array)
	{
		mProperties = {};

		for(var i = 0, l = vProperties.length; i < l; ++i)
		{
			mProperties[vProperties[i]] = "*";
		}
	}
	else
	{
		mProperties = vProperties || {};
	}

	return mProperties;
};



/**
 * @private
 */
br.presenter.node.PresentationNode.prototype._$setPath = function(sPath, oPresenterComponent)
{
	this.m_sPath = sPath;

	for(var sChildToBeSet in this)
	{
		var oChildToBeSet = this[sChildToBeSet];

		if(this._isPresenterChild(sChildToBeSet, oChildToBeSet) )
		{
			var sCurrentPath = oChildToBeSet.getPath();
			var sChildPath = sPath + '.' + sChildToBeSet;

			if(sCurrentPath === undefined)
			{
				oChildToBeSet._$setPath(sChildPath, oPresenterComponent);
			}
			else if(sCurrentPath !== sChildPath)
			{
				this._checkAncestor(sCurrentPath, sChildPath);
			}
		}
	}

	this.__oPresenterComponent = oPresenterComponent;
};

/*
 * PN's form a tree but we want to allow a child node to hold a reference to an ancestor node.
 * The methods that recurse the PN structure will ignore such links thus avoiding infinite recursion.
 * We recognize an ancestor node because its path must be a prefix of its childrens paths
 */
br.presenter.node.PresentationNode.prototype._checkAncestor = function(sOtherPath, sChildPath)
{
	if(sOtherPath === ""){ // the toplevel - PresentationModel
		return;
	}

	if(sChildPath.indexOf(sOtherPath) != 0){
		var msg = "OtherPath: '" + sOtherPath + "  'ChildPath:'" + sChildPath + "' are both references to the same instance in PresentationNode.";
		throw new br.Errors.IllegalStateError(msg);
	}
}

br.presenter.node.PresentationNode.prototype._isPresenterChild = function(sChildToBeSet, oChildToBeSet)
{
	return (oChildToBeSet  && oChildToBeSet._$setPath);
};

/**
 * @private
 */
br.presenter.node.PresentationNode.prototype._$clearPropertiesPath = function()
{
	var pProperties = this.properties();
	for(var i = 0; i < pProperties.m_pProperties.length; i++){
		pProperties.m_pProperties[i]._$setPath(undefined);
	}
};

/**
 * @private
 */
br.presenter.node.PresentationNode.prototype._$clearNodePaths = function()
{
	// It's possible for nodes to be newly created and then passed into
	// a NodeList before the nodes have had their path set.  This causes
	// problems here.
	// For now, we don't clear children of nodes that don't have their
	// path set.  This is probably wrong and will need to be fixed properly.
	if (this.m_sPath === undefined) return;

	var pNodes = this.nodes().getNodesArray();
	this._$clearPropertiesPath();
	for(var i = 0; i < pNodes.length; i++)
	{
		pNodes[i]._$clearNodePaths();
	}
	this.m_sPath = undefined;
};

/**
 * @private
 */
br.presenter.node.PresentationNode.prototype._getNodes = function(sNodeName, mProperties, pNodes)
{
	for(var sKey in this)
	{
		var vItem = this[sKey];
		if(!this._isPresenterChild(sKey, vItem)){
			continue;
		}

		if(vItem instanceof br.presenter.node.PresentationNode){

			if(this._isUpwardReference(this, vItem)){
				continue;
			}

			var oPresentationNode = vItem;
			if(this._containsNode(pNodes, oPresentationNode)){
				continue;
			}

			if(this._nodeMatchesQuery(oPresentationNode, sKey, sNodeName, mProperties)){
				pNodes.push(oPresentationNode);
			}

			oPresentationNode._getNodes(sNodeName, mProperties, pNodes);
		}
	}
};

/*
 * We know that the only duplicate references to nodes are ancestor nodes.[enforced by _$setPath()]
 * These must have shorter paths than any children.
 */
br.presenter.node.PresentationNode.prototype._isUpwardReference = function(oParentNode, oChildNode)
{
	var sChildPath = oChildNode.getPath();
	var sParentPath = oParentNode.getPath();
	if(sChildPath === undefined && sParentPath === undefined){
		return false;
	}
	// This is a temporary thing to make the tests pass.
	if (sChildPath === undefined || sParentPath === undefined) {
		return false;
	}
	return (sChildPath.length < sParentPath.length);
};


br.presenter.node.PresentationNode.prototype._containsNode = function(pNodes, oNode)
{
	for (var i = 0, end = pNodes.length; i < end ; i++){
		if(pNodes[i] === oNode){
			return true;
		}
	}
	return false;
}

/**
 * @private
 */
br.presenter.node.PresentationNode.prototype._nodeMatchesQuery = function(oPresentationNode, sActualNodeName, sNodeName, mProperties)
{
	if((sNodeName) && (sNodeName != sActualNodeName))
	{
		return false;
	}

	for(var sProperty in mProperties)
	{
		var sPropertyValue = mProperties[sProperty];
		var oProperty = oPresentationNode[sProperty];

		if(!(oProperty instanceof br.presenter.property.Property))
		{
			return false;
		}
		else if((sPropertyValue != "*") && (sPropertyValue != oProperty.getValue()))
		{
			return false;
		}
	}

	return true;
};
