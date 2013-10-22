caplinx.dashboard.app.model.crumbtrail.BreadCrumb = function(sClass, sName, sUrl)
{
	// call super constructor
	caplin.presenter.node.TemplateNode.call(this, "caplinx.dashboard.app." + sClass);
	
	this.className = new caplin.presenter.property.WritableProperty(sClass);
	this.name = new caplin.presenter.property.WritableProperty(sName);
	this.url = new caplin.presenter.property.WritableProperty(sUrl);
};
caplin.extend(caplinx.dashboard.app.model.crumbtrail.BreadCrumb, caplin.presenter.node.TemplateNode);
