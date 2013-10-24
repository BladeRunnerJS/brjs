/**
 * Constructs a new <code>Nodes</code> instance containing the given list
 * of {@link br.presenter.node.PresentationNode} objects.
 * 
 * @class
 * A class used to hold collections of nodes, and providing utility methods for
 * performing operations over those collections.
 * 
 * @constructor
 * @param {Array} pNodes (optional) The set of nodes.
 */
br.presenter.node.Nodes = function(pNodes)
{
	/** @private */
	this.m_pNodes = pNodes || [];
};

/**
 * Returns the underlying array this collection is built from.
 * @type Array
 */
br.presenter.node.Nodes.prototype.getNodesArray = function()
{
	return this.m_pNodes;
};

/**
 * Returns all properties for the nodes within this collection.
 * @type br.presenter.property.Properties
 */
br.presenter.node.Nodes.prototype.properties = function()
{
	var oProperties = new br.presenter.property.Properties();
	
	for(var i = 0, l = this.m_pNodes.length; i < l; ++i)
	{
		var oPresentationNode = this.m_pNodes[i];
		
		oProperties.add(oPresentationNode.properties());
	}
	
	return oProperties;
};
