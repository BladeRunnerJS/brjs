var KnockoutPresentationModelViewer = require('br/knockout/workbench/PresentationModelViewer');
var KnockoutModelTree = require('br/knockout/workbench/KnockoutModelTree');

br.presenter.workbench.ui.PresentationModelViewer = function(oPresentationModel, modelTree) 
{
	if(modelTree)
	{
		var KnockoutJsTreeModelFactory = require('br/knockout/workbench/KnockoutJsTreeModelFactory');
		var newTreeModel = KnockoutJsTreeModelFactory.createTreeModelFromKnockoutViewModel(oPresentationModel);
		KnockoutPresentationModelViewer.call(this,newTreeModel);
	}		
}

br.Core.implement(br.presenter.workbench.ui.PresentationModelViewer, br.workbench.ui.WorkbenchComponent);
br.Core.extend(br.presenter.workbench.ui.PresentationModelViewer, KnockoutPresentationModelViewer);