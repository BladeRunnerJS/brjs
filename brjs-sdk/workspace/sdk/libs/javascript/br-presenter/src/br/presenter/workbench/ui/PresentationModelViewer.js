/**
 * @module br/presenter/workbench/ui/PresentationModelViewer
 */

var KnockoutPresentationModelViewer = require('br/knockout/workbench/PresentationModelViewer');
var PresenterModelTree = require('br/presenter/workbench/ui/PresenterModelTree');
var PresenterJsTreeModelFactory = require('br/presenter/workbench/ui/PresenterJsTreeModelFactory');
var KnockoutTreeModelFactory = require('br/knockout/workbench/KnockoutJsTreeModelFactory');

br.presenter.workbench.ui.PresentationModelViewer = function(viewOrPresentationModel, TreeModelClass) 
{
	var treeModel;
	
	if(!TreeModelClass || (TreeModelClass instanceof PresenterModelTree))
	{
		treeModel = PresenterJsTreeModelFactory.createTreeModelFromPresentationModel(viewOrPresentationModel);
	}
	else
	{
		treeModel = KnockoutTreeModelFactory.createTreeModelFromKnockoutViewModel(viewOrPresentationModel);
	}
	
	KnockoutPresentationModelViewer.call(this,treeModel);
};

br.Core.extend(br.presenter.workbench.ui.PresentationModelViewer, KnockoutPresentationModelViewer);
br.Core.implement(br.presenter.workbench.ui.PresentationModelViewer, br.workbench.ui.WorkbenchComponent);
