br.presenter.testing.node.RootPresentationNodeContainingList = function()
{
	this.m_oPrivateProperty1 = new br.presenter.property.WritableProperty();
	this.m_oOnlyChild = new br.presenter.testing.node.ChildPresentationNode();
	
	this.property1 = new br.presenter.property.WritableProperty();
	this.children = new br.presenter.node.NodeList([this.m_oOnlyChild]);
};
br.extend(br.presenter.testing.node.RootPresentationNodeContainingList, br.presenter.PresentationModel);
