MultiViewClickPresentationModel = function()
{
	var pHobbies = ["Cooking", "Extreme Ironing"];
	this.hobbies = new br.presenter.node.SelectionField(pHobbies);
};
br.Core.extend(MultiViewClickPresentationModel, br.presenter.PresentationModel);
