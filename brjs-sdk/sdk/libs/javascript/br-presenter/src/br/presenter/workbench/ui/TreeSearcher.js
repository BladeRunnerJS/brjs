'use strict';

/**
 * @module br/presenter/workbench/ui/TreeSearcher
 */

/**
 * @private
 * @class
 * @alias module:br/presenter/workbench/ui/TreeSearcher
 */
function TreeSearcher(oRootNode) {
	this.m_oRootNode = oRootNode;
}

TreeSearcher.prototype.search = function(sValue) {
	var oRoot = this.m_oRootNode;
	var pToExpand = [];
	this._processSearch(oRoot, sValue, pToExpand);
	for (var i = 0; i < pToExpand.length; i++) {
		this._expand(pToExpand[i]);
	}
};

TreeSearcher.prototype._expand = function(oNode) {
	oNode.expand();
	// TODO: highlight selected nodes 
	// oNode.addClass("found-tree-node");
	if (oNode.parentNode) {
		this._expand(oNode.parentNode);
	}
};

TreeSearcher.prototype._processSearch = function(oNode, sSearchText, pToExpand) {
	if (oNode.text.match(sSearchText)) {
		pToExpand.push(oNode);
	} else {
		if (oNode.expanded == true && oNode.childNodes.length > 0) {
			oNode.collapse();
		}
	}

	var children = oNode.childNodes;
	for (var i = 0; i < children.length; i++) {
		var child = children[i];
		this._processSearch(child, sSearchText, pToExpand);
	}
};

module.exports = TreeSearcher;
