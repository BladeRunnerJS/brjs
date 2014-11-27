brjs.dashboard.app.model.apps.AppSummaryPresentationNode = function(sAppName, sAppInfoUrl, sImageUrl)
{
	this.appName = new br.presenter.property.Property(sAppName);
	this.appInfoUrl = new br.presenter.property.Property(sAppInfoUrl);
	this.imageUrl = new br.presenter.property.Property(sImageUrl);
};
br.Core.extend(brjs.dashboard.app.model.apps.AppSummaryPresentationNode, br.presenter.node.PresentationNode);
