brjs.dashboard.app.model.crumbtrail.HomeBreadCrumb = function()
{
	// call super constructor
	brjs.dashboard.app.model.crumbtrail.BreadCrumb.call(this, "home-breadcrumb", null, "#");
};
br.Core.extend(brjs.dashboard.app.model.crumbtrail.HomeBreadCrumb, brjs.dashboard.app.model.crumbtrail.BreadCrumb);
