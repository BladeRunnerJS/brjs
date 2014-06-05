br.presenter.testing.node.LimitedParentNode = function()
{
	this.parentProperty = new br.presenter.property.WritableProperty("p");
	this.nestedChild = new br.presenter.testing.node.LimitedDescendantNode();
};
br.Core.extend(br.presenter.testing.node.LimitedParentNode, br.presenter.node.PresentationNode);
