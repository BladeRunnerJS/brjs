caplinx.dashboard.app.model.crumbtrail.ActiveBreadCrumb = function(sName)
{
	// call super constructor
	caplinx.dashboard.app.model.crumbtrail.BreadCrumb.call(this, "active-breadcrumb", sName, null);
};
br.Core.extend(caplinx.dashboard.app.model.crumbtrail.ActiveBreadCrumb, caplinx.dashboard.app.model.crumbtrail.BreadCrumb);
