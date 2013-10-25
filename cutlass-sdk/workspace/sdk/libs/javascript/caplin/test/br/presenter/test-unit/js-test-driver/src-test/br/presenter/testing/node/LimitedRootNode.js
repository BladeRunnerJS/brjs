br.presenter.testing.node.LimitedRootNode = function()
{
	this.rootProperty = new br.presenter.property.WritableProperty("r");
	this.parentNode1 = new br.presenter.testing.node.LimitedParentNode();
	this.parentNode2 = new br.presenter.testing.node.LimitedParentNode();
	this.descendantNode1 = new br.presenter.testing.node.LimitedDescendantNode();
	this.descendantNode2 = new br.presenter.testing.node.LimitedDescendantNode();
};
br.extend(br.presenter.testing.node.LimitedRootNode, br.presenter.PresentationModel);
