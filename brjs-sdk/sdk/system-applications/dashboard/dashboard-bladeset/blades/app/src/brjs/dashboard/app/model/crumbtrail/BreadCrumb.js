brjs.dashboard.app.model.crumbtrail.BreadCrumb = function(sClass, sName, sUrl)
{
	// call super constructor
	br.presenter.node.TemplateNode.call(this, "brjs.dashboard.app." + sClass);
	
	this.className = new br.presenter.property.WritableProperty(sClass);
	this.name = new br.presenter.property.WritableProperty(sName);
	this.url = new br.presenter.property.WritableProperty(sUrl);
};
br.Core.extend(brjs.dashboard.app.model.crumbtrail.BreadCrumb, br.presenter.node.TemplateNode);
