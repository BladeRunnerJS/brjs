var WorkbenchComponent = require('br/workbench/ui/WorkbenchComponent');
var jQuery = require('jstree');
var br = require('br/Core');

function PresentationModelViewer(treeModel) {
	this._componentElement = this._renderView();
	this._treeModel = treeModel;
	jQuery(this._componentElement.querySelector('#tree-view')).jstree(treeModel);
};
br.implement(PresentationModelViewer, WorkbenchComponent);

PresentationModelViewer.prototype.getElement = function() {
	return this._componentElement;
};

PresentationModelViewer.prototype.applySearch = function(searchTerm) {
	this._collapseChildNodes(this._treeModel.core.data);
	this._expandMatchingNodes(new RegExp(searchTerm, "i"), this._treeModel.core.data, []);
	
	if(jQuery(this._componentElement.querySelector('#tree-view')).jstree(true).destroy) {
		jQuery(this._componentElement.querySelector('#tree-view')).jstree(true).destroy();
	}
	jQuery(this._componentElement.querySelector('#tree-view')).jstree(this._treeModel);
};

PresentationModelViewer.prototype._renderView = function() {
	var componentElement = document.createElement('div');
	componentElement.id = 'presentation-model-viewer';
	componentElement.innerHTML =
		'<div id="search-area">' +
		'	<form>' +
		'		<input type="text" />' +
		'		<button type="submit" class="btn">Find</button>' +
		'	</form>' +
		'</div>' +
		'<div id="tree-view">' +
		'</div>';
	componentElement.querySelector('#search-area form').onsubmit = this._onSearch.bind(this);
	
	return componentElement;
};

PresentationModelViewer.prototype._onSearch = function() {
	this.applySearch(this._componentElement.querySelector('#search-area input[type="text"]').value);
	return false;
};

PresentationModelViewer.prototype._collapseChildNodes = function(nodes) {
	for(var i = 0, l = nodes.length; i < l; ++i) {
		var node = nodes[i];
		
		if(node.state) {
			node.state.opened = false;
			this._collapseChildNodes(node.children);
		}
	}
};

PresentationModelViewer.prototype._expandMatchingNodes = function(searchTerm, nodes, parentNodes) {
	for(var i = 0, l = nodes.length; i < l; ++i) {
		var node = nodes[i];
		
		if(node.text.match(searchTerm)) {
			for(var pi = 0, pl = parentNodes.length; pi < pl; ++pi) {
				parentNodes[pi].state.opened = true;
			}
		}
		
		if(node.children) {
			var childParentNodes = parentNodes.slice();
			childParentNodes.push(node);
			this._expandMatchingNodes(searchTerm, node.children, childParentNodes);
		}
	}
};

module.exports = PresentationModelViewer;
