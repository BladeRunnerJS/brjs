caplinx.dashboard.app.model.crumbtrail.BreadCrumb = function(sClass, sName, sUrl)
{
	// call super constructor
	br.presenter.node.TemplateNode.call(this, "caplinx.dashboard.app." + sClass);
	
	this.className = new br.presenter.property.WritableProperty(sClass);
	this.name = new br.presenter.property.WritableProperty(sName);
	this.url = new br.presenter.property.WritableProperty(sUrl);
};
br.extend(caplinx.dashboard.app.model.crumbtrail.BreadCrumb, br.presenter.node.TemplateNode);
