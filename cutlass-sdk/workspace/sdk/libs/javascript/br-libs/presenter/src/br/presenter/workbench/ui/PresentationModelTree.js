br.Core.thirdparty("extjs");

/**
 * 
 */
br.presenter.workbench.ui.PresentationModelTree = function(oPresentationModel)
{
	if (!oPresentationModel)
	{
		throw "PresentationModelViewer expects a presentation model";
	}
	this.m_PresentationModel = oPresentationModel;
	var oTree = this._buildTree(this.m_PresentationModel);
	this.m_oTreeSearcher = new br.presenter.workbench.ui.TreeSearcher(this.m_oRootNode);
	
	this.m_eElement = document.createElement("div");
	oTree.render(this.m_eElement);
};

br.Core.implement(br.presenter.workbench.ui.PresentationModelTree, br.workbench.ui.WorkbenchComponent);

br.presenter.workbench.ui.PresentationModelTree.prototype.search = function(sValue)
{
	this.m_oTreeSearcher.search(sValue);
};

br.presenter.workbench.ui.PresentationModelTree.prototype.getElement = function()
{
	return this.m_eElement;
};

br.presenter.workbench.ui.PresentationModelTree.prototype._buildTree = function(oPresentationModel)
{
	var oTree = new Ext.tree.TreePanel({});
	this.m_oRootNode = new Ext.tree.TreeNode({text : "Presentation Model", expanded: true});
	
	this._buildNodes(this.m_oRootNode, oPresentationModel, true);

	oTree.setRootNode(this.m_oRootNode);
	return oTree;
};

br.presenter.workbench.ui.PresentationModelTree.prototype._buildNodes = function(oTreeNode, oPresentationNode, bExpanded)
{
	var bExpanded = bExpanded;
	for (var sKey in oPresentationNode)
	{
		if (sKey.substr(0,2) !== "m_")
		{
			var vItem = oPresentationNode[sKey];
			if (vItem instanceof br.presenter.property.Property)
			{
				var sNodeLabel = sKey;
				if (vItem.getValue() !== undefined)
				{
					sNodeLabel += ":" + vItem.getValue();
				}
				var oChild = new Ext.tree.TreeNode({text : sNodeLabel, expanded: true});
				oTreeNode.appendChild(oChild);
				this._createListeners(vItem, oChild, sKey);
				
			}
			
			if (vItem instanceof br.presenter.node.PresentationNode)
			{
				if (vItem instanceof br.presenter.node.Field || vItem instanceof br.presenter.node.SelectionField)
				{
					bExpanded = false;
				}
				var oChild = new Ext.tree.TreeNode({text : sKey, expanded: bExpanded});
				oTreeNode.appendChild(oChild);
				this._buildNodes(oChild, vItem, bExpanded);
			}
			else if (vItem instanceof br.presenter.node.NodeList)
			{
				var oChild = new Ext.tree.TreeNode({text : sKey, expanded: true});
				oTreeNode.appendChild(oChild);
				var pNodes = getPresentationNodesArray();
				this._buildNodes(oChild, pNodes, true);
			}
		}
	}
};

br.presenter.workbench.ui.PresentationModelTree.prototype._createListeners = function(oProperty, oTreeNode, sKey)
{
	oProperty.addChangeListener(
		{
			_onChange : function() {
				var sNodeLabel = sKey;
				if (oProperty.getValue() !== undefined) 
				{
					sNodeLabel += ":" + oProperty.getValue();
				}
				oTreeNode.setText(sNodeLabel);
			}
		}, "_onChange");
};
