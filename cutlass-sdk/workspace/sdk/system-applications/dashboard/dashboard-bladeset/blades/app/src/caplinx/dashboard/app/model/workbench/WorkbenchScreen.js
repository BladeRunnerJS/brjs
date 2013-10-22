caplinx.dashboard.app.model.workbench.WorkbenchScreen = function(oPresentationModel)
{
	this.m_oPresentationModel = oPresentationModel;
	
	this.visible = new caplin.presenter.property.WritableProperty(false);
	this.workbenchUrl = new caplin.presenter.property.WritableProperty();
	this.iFrameHeight = new caplin.presenter.property.WritableProperty( "200px" );
	$(window).resize( this._setIframeSize.bind( this ) );
	this._setIframeSize();
};
caplin.extend(caplinx.dashboard.app.model.app.AppDetailScreen, caplin.presenter.node.PresentationNode);

caplinx.dashboard.app.model.workbench.WorkbenchScreen.prototype.getWorkbenchScreenUrl = function(sAppName, sBladesetName, sBladeName)
{
	return "../#apps/" + sAppName + "/" + sBladesetName + "/" + sBladeName + "/workbench";
};

caplinx.dashboard.app.model.workbench.WorkbenchScreen.prototype.displayWorkbench = function(sAppName, sBladesetName, sBladeName)
{
	var sWorkbenchUrl = this.m_oPresentationModel.getPageUrlService().getRootUrl() + sAppName + "/" + sBladesetName + "-bladeset/blades/" + sBladeName + "/workbench";
	this.workbenchUrl.setValue(sWorkbenchUrl);
	this.m_oPresentationModel.setCurrentSection("sdk");
	this.m_oPresentationModel.hideAllScreens();
	this.visible.setValue(true);
};

caplinx.dashboard.app.model.workbench.WorkbenchScreen.prototype.popoutWorkbench = function()
{
	this.m_oPresentationModel.getWindowOpenerService().openWindow(this.workbenchUrl.getValue());
};
caplinx.dashboard.app.model.workbench.WorkbenchScreen.prototype._setIframeSize = function()
{
	var nIframeHeight = ( $(window).height() - 150 ) + "px";
	this.iFrameHeight.setValue( nIframeHeight );
};
