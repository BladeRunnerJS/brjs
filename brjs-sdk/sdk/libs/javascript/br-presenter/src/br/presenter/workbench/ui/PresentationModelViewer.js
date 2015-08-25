'use strict';

var WorkbenchComponent = require('br/workbench/ui/WorkbenchComponent');
var Core = require('br/Core');

/**
 * @module br/presenter/workbench/ui/PresentationModelViewer
 */

var KnockoutPresentationModelViewer = require('br/knockout/workbench/PresentationModelViewer');
var PresenterModelTree = require('br/presenter/workbench/ui/PresenterModelTree');
var PresenterJsTreeModelFactory = require('br/presenter/workbench/ui/PresenterJsTreeModelFactory');
var KnockoutTreeModelFactory = require('br/knockout/workbench/KnockoutJsTreeModelFactory');

/**
 * @class
 * @alias module:br/presenter/workbench/ui/PresentationModelViewer
 */
function PresentationModelViewer(viewOrPresentationModel, TreeModelClass) {
	var treeModel;

	if (!TreeModelClass || (TreeModelClass instanceof PresenterModelTree)) {
		treeModel = PresenterJsTreeModelFactory.createTreeModelFromPresentationModel(viewOrPresentationModel);
	} else {
		treeModel = KnockoutTreeModelFactory.createTreeModelFromKnockoutViewModel(viewOrPresentationModel);
	}

	KnockoutPresentationModelViewer.call(this, treeModel);
}

Core.extend(PresentationModelViewer, KnockoutPresentationModelViewer);
Core.implement(PresentationModelViewer, WorkbenchComponent);

module.exports = PresentationModelViewer;
