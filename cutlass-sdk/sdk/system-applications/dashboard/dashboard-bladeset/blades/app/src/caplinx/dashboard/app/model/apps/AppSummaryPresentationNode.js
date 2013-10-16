caplinx.dashboard.app.model.apps.AppSummaryPresentationNode = function(sAppName, sAppInfoUrl, sImageUrl)
{
	this.appName = new caplin.presenter.property.Property(sAppName);
	this.appInfoUrl = new caplin.presenter.property.Property(sAppInfoUrl);
	this.imageUrl = new caplin.presenter.property.Property(sImageUrl);
};
caplin.extend(caplinx.dashboard.app.model.apps.AppSummaryPresentationNode, caplin.presenter.node.PresentationNode);
