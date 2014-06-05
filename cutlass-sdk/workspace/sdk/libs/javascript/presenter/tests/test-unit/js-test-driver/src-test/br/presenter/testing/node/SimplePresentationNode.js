br.presenter.testing.node.SimplePresentationNode = function(vPropValue)
{
	this.property = new br.presenter.property.WritableProperty(vPropValue);
};
br.Core.extend(br.presenter.testing.node.SimplePresentationNode, br.presenter.node.PresentationNode);
