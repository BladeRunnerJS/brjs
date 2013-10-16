caplinx.dashboard.app.model.crumbtrail.CrumbTrail = function(oPresentationModel)
{
	this.crumbs = new caplin.presenter.node.NodeList([], caplinx.dashboard.app.model.crumbtrail.BreadCrumb);
	this.visible = new caplin.presenter.property.WritableProperty();
	
	oPresentationModel.getPageUrlService().addPageUrlListener(this._onPageUrlUpdated.bind(this), true);
};
caplin.extend(caplinx.dashboard.app.model.crumbtrail.CrumbTrail, caplin.presenter.node.PresentationNode);

caplinx.dashboard.app.model.crumbtrail.CrumbTrail.prototype._onPageUrlUpdated = function(sPageUrl)
{
	var pCrumbTrail = [new caplinx.dashboard.app.model.crumbtrail.HomeBreadCrumb()];
	var bIsVisible = false;
	
	if(sPageUrl.match(/^#apps\/.*workbench$/))
	{
		bIsVisible = true;
		var pParts = sPageUrl.match(/^#apps\/(.*)workbench$/)[1].split("/");
		var sApp = pParts[0];
		var sBladeset = pParts[1];
		var sBlade = pParts[2];
		var sAppUrl = "../#apps/" + sApp;
		pCrumbTrail.push(new caplinx.dashboard.app.model.crumbtrail.LinkBreadCrumb(sApp, sAppUrl));
		pCrumbTrail.push(new caplinx.dashboard.app.model.crumbtrail.LinkBreadCrumb(sBladeset, sAppUrl));
		pCrumbTrail.push(new caplinx.dashboard.app.model.crumbtrail.LinkBreadCrumb(sBlade, sAppUrl));
		pCrumbTrail.push(new caplinx.dashboard.app.model.crumbtrail.ActiveBreadCrumb("Workbench"));
	}
	else if(sPageUrl.match(/^#apps\/.*/))
	{
		var sApp = sPageUrl.split("/")[1];
		
		bIsVisible = true;
		pCrumbTrail.push(new caplinx.dashboard.app.model.crumbtrail.ActiveBreadCrumb(sApp));
	}
	
	this.crumbs.updateList(pCrumbTrail);
	this.visible.setValue(bIsVisible);
};
