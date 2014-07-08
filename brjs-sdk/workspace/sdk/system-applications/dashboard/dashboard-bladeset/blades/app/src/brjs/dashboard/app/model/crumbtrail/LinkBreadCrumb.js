brjs.dashboard.app.model.crumbtrail.LinkBreadCrumb = function(sName, sUrl)
{
	// call super constructor
	brjs.dashboard.app.model.crumbtrail.BreadCrumb.call(this, "link-breadcrumb", sName, sUrl);
};
br.Core.extend(brjs.dashboard.app.model.crumbtrail.LinkBreadCrumb, brjs.dashboard.app.model.crumbtrail.BreadCrumb);
