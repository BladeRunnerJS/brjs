brjs.dashboard.app.model.workbench.WorkbenchScreen = function(oPresentationModel)
{
	this.m_oPresentationModel = oPresentationModel;

	this.visible = new br.presenter.property.WritableProperty(false);
	this.workbenchUrl = new br.presenter.property.WritableProperty();
	this.iFrameHeight = new br.presenter.property.WritableProperty( "200px" );
	$(window).resize( this._setIframeSize.bind( this ) );
	this._setIframeSize();
};
br.Core.extend(brjs.dashboard.app.model.workbench.WorkbenchScreen, br.presenter.node.PresentationNode);

brjs.dashboard.app.model.workbench.WorkbenchScreen.prototype.getWorkbenchScreenUrl = function(sAppName, sBladesetName, sBladeName)
{
	return "../#apps/" + sAppName + "/" + sBladesetName + "/" + sBladeName + "/workbench";
};

brjs.dashboard.app.model.workbench.WorkbenchScreen.prototype.displayWorkbench = function(sAppName, sBladesetName, sBladeName)
{
	var sWorkbenchUrl = this.m_oPresentationModel.getPageUrlService().getRootUrl() + sAppName + "/" + sBladesetName + "-bladeset/blades/" + sBladeName + "/workbench";
	this.workbenchUrl.setValue(sWorkbenchUrl);
	this.m_oPresentationModel.setCurrentSection("sdk");
	this.m_oPresentationModel.hideAllScreens();
	this.visible.setValue(true);
};

brjs.dashboard.app.model.workbench.WorkbenchScreen.prototype.popoutWorkbench = function()
{
	this.m_oPresentationModel.getWindowOpenerService().openWindow(this.workbenchUrl.getValue());
};

brjs.dashboard.app.model.workbench.WorkbenchScreen.prototype._setIframeSize = function()
{
	var nIframeHeight = ( $(window).height() - 150 ) + "px";
	this.iFrameHeight.setValue( nIframeHeight );
};
