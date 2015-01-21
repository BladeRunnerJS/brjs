br.presenter.testing.node.UpwardRefsChildNode = function(oParent)
{
	this.oParent = oParent;
	this.oGrandChild = new br.presenter.testing.node.UpwardRefsGrandchildNode(this, oParent)
};
br.Core.extend(br.presenter.testing.node.UpwardRefsChildNode, br.presenter.node.PresentationNode);
