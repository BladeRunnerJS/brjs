var WorkbenchComponent = require('br/workbench/ui/WorkbenchComponent');
var jQuery = require('jstree');
var br = require('br/Core');

function PresentationModelViewer(viewModel) {
	this.componentElement = document.createElement('div');
	jQuery(this.componentElement).jstree(viewModel);
};
br.implement(PresentationModelViewer, WorkbenchComponent);

PresentationModelViewer.prototype.getElement = function() {
	return this.componentElement;
};

module.exports = PresentationModelViewer;
