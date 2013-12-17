br.presenter.testing.node.LimitedDescendantNode = function()
{
	this.childProperty = new br.presenter.property.WritableProperty("c");
};
br.Core.extend(br.presenter.testing.node.LimitedDescendantNode, br.presenter.testing.node.LimitedParentNode);
