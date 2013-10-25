caplinx.dashboard.app.model.crumbtrail.HomeBreadCrumb = function()
{
	// call super constructor
	caplinx.dashboard.app.model.crumbtrail.BreadCrumb.call(this, "home-breadcrumb", null, "../#");
};
br.extend(caplinx.dashboard.app.model.crumbtrail.HomeBreadCrumb, caplinx.dashboard.app.model.crumbtrail.BreadCrumb);
