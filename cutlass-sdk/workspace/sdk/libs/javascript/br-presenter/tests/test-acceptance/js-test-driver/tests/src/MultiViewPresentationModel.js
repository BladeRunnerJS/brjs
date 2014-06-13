MultiViewPresentationModel = function()
{
	this.selectionField = new br.presenter.node.SelectionField(['a','b', 'c'], "b");
	this.selectionField.controlName.setValue("aSelectionField");

	this.multiSelectField = new br.presenter.node.MultiSelectionField(["A","B","C"], ["A","C"]);
	this.multiSelectField.controlName.setValue("aMultiSelectionField");
};
br.Core.extend(MultiViewPresentationModel, br.presenter.PresentationModel);
