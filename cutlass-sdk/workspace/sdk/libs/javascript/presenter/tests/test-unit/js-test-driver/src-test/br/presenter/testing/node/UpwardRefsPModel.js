br.presenter.testing.node.UpwardRefsPModel = function()
{
	this.child = new br.presenter.testing.node.UpwardRefsChildNode(this);
};
br.Core.extend(br.presenter.testing.node.UpwardRefsPModel, br.presenter.PresentationModel);
