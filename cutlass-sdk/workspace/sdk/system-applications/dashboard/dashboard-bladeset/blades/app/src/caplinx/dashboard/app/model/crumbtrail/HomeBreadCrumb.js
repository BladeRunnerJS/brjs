caplinx.dashboard.app.model.crumbtrail.HomeBreadCrumb = function()
{
	// call super constructor
	caplinx.dashboard.app.model.crumbtrail.BreadCrumb.call(this, "home-breadcrumb", null, "../#");
};
caplin.extend(caplinx.dashboard.app.model.crumbtrail.HomeBreadCrumb, caplinx.dashboard.app.model.crumbtrail.BreadCrumb);
