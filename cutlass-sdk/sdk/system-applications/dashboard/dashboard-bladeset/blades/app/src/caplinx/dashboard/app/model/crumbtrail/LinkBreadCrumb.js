caplinx.dashboard.app.model.crumbtrail.LinkBreadCrumb = function(sName, sUrl)
{
	// call super constructor
	caplinx.dashboard.app.model.crumbtrail.BreadCrumb.call(this, "link-breadcrumb", sName, sUrl);
};
caplin.extend(caplinx.dashboard.app.model.crumbtrail.LinkBreadCrumb, caplinx.dashboard.app.model.crumbtrail.BreadCrumb);
