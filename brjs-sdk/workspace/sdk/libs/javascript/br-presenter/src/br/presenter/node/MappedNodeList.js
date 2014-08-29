/**
 * @module br/presenter/node/MappedNodeList
 */

/**
 * @class
 * @alias module:br/presenter/node/MappedNodeList
 * @extends module:br/presenter/node/NodeList
 * 
 * @classdesc
 * <code>MappedNodeList</code> is a {@link module:br/presenter/node/NodeList}.
 * 
 * <p>The <code>MappedNodeList</code> class is useful when you want to refer to items in
 * a {@link module:br/presenter/node/NodeList} (which stores items in an array) using a name, rather
 * than then ordinal position within the array. The mappings are only accessible via the <code>PresentationNode</code>
 * representation of the list and not from the view i.e. can't refer to the mappings in templates.
 *
 * <p>The contents of the <code>MappedNodeList</code> class should only be modified using the {@link #updateList}
 * method, and this will cause the view to immediately update to reflect the contents of the new
 * Map. </p>
 *
 * <p>If the optional second parameter to the constructor, <code>fNodeClass</code>, is
 * provided, then the <code>MappedNodeList</code> will need all the nodes it is expected to contain to be
 * instances of <code>fNodeClass</code> (i.e. subclasses are allowed), otherwise it throws
 * a {@link module:br/Errors/CustomError}.
 * </p>
 * 
 * @param {Map} mPresentationNodes The initial map of {@link module:br/presenter/node/PresentationNode} instances.
 * @param {Function} fNodeClass (optional) The class/interface that all nodes in this list should be an instance of.
 */
br.presenter.node.MappedNodeList = function(mPresentationNodes, fNodeClass)
{
	/** @private */
	this.m_mMappings = {};

	var pPresentationNodes = this._doMapping(mPresentationNodes);
	br.presenter.node.NodeList.call(this, pPresentationNodes, fNodeClass);
};

br.Core.extend(br.presenter.node.MappedNodeList, br.presenter.node.NodeList);

/**
 * Returns the string-to-node mapping. Treat as immutable.
 * @type Object
 */
br.presenter.node.MappedNodeList.prototype.getPresentationNodesMap = function ()
{
	return this.m_mMappings;
};

/**
 * Updates the node list with a new Map of {@link module:br/presenter/node/PresentationNode} instances.
 *
 * <p>Care must be taken to always invoke this method when the contents of the node list change. The
 * array returned by {@link #getPresentationNodesArray} should be treated as being immutable.</p>
 *
 * @param {Array} mPresentationNodes The new map of {@link module:br/presenter/node/PresentationNode} instances.
 */
br.presenter.node.MappedNodeList.prototype.updateList = function (mPresentationNodes)
{
	var pPresentationNodes = this._doMapping(mPresentationNodes);
	br.presenter.node.NodeList.prototype.updateList.call(this, pPresentationNodes);
	return this;
};

/**
 * @private
 */
br.presenter.node.MappedNodeList.prototype._doMapping = function(mPresentationNodes)
{
	if(mPresentationNodes instanceof Array)
	{
		throw new br.Errors.InvalidParametersError("Cannot use an array to update values in a MappedNodeList, use a map.");
	}
	this._cleanUpMappings();
	var pResult = [];
	for (var sKey in mPresentationNodes)
	{
		var oNode = mPresentationNodes[sKey];
		pResult.push(oNode);
		this[sKey] = oNode;
		this.m_mMappings[sKey] = oNode;
	}

	this._setPathsOfNewlyAddedNodes();
	return pResult;
};

/**
 * @private
 */
br.presenter.node.MappedNodeList.prototype._setPathsOfNewlyAddedNodes = function()
{
    if(this.getPath() !== undefined)
    {
        for(var s in this.m_mMappings)
        {
            if(this.m_mMappings[s].getPath() === undefined)
            {
                this.m_mMappings[s]._$setPath(this.getPath() + "." + s, this.__oPresenterComponent);
            }
        }
    }
};

/**
* @private
*/
br.presenter.node.MappedNodeList.prototype._cleanUpMappings = function()
{
	for(var sKey in this.m_mMappings)
	{
		if(this[sKey])
		{
			delete this[sKey];
		}
	}
	this.m_mMappings = {};
};
