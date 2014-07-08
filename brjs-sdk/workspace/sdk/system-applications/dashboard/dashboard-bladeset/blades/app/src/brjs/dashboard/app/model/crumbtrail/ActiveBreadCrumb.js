brjs.dashboard.app.model.crumbtrail.ActiveBreadCrumb = function(sName)
{
	// call super constructor
	brjs.dashboard.app.model.crumbtrail.BreadCrumb.call(this, "active-breadcrumb", sName, null);
};
br.Core.extend(brjs.dashboard.app.model.crumbtrail.ActiveBreadCrumb, brjs.dashboard.app.model.crumbtrail.BreadCrumb);
