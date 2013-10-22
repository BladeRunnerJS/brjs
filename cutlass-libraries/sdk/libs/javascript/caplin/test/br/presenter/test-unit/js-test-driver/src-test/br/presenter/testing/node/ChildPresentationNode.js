br.presenter.testing.node.ChildPresentationNode = function()
{
	this.m_oPrivateProperty2 = new br.presenter.property.WritableProperty();
	
	this.property2 = new br.presenter.property.WritableProperty("p2");
	this.property3 = new br.presenter.property.WritableProperty("p3");
	this.grandchild = new br.presenter.testing.node.GrandChildPresentationNode();
};
br.extend(br.presenter.testing.node.ChildPresentationNode, br.presenter.node.PresentationNode);
