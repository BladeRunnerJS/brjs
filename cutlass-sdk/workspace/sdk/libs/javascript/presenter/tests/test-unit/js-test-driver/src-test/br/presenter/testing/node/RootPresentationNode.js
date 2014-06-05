br.presenter.testing.node.RootPresentationNode = function()
{
	this.m_oPrivateProperty1 = new br.presenter.property.WritableProperty();
	
	this.property1 = new br.presenter.property.WritableProperty("p1");
	this.child = new br.presenter.testing.node.ChildPresentationNode();
};
br.Core.extend(br.presenter.testing.node.RootPresentationNode, br.presenter.PresentationModel);
