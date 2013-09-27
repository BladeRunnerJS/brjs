caplinx.dashboard.app.model.apps.SplashScreen = function(oPresentationModel)
{
	this.m_oLocalStorage = oPresentationModel.getLocalStorage();
	
	this.isVisible = new caplin.presenter.property.EditableProperty(true);
	this.permanentlyHideSplashScreen = new caplin.presenter.property.EditableProperty(false);
	this.permanentlyHideSplashScreen.addChangeListener(this, "_toggleLocalStoragePermanentHide");
	
	this.videoVisible = new caplin.presenter.property.EditableProperty(false);
	this.videoPlacholderVisible = new caplin.presenter.property.EditableProperty(true);
	
	this.m_sLocalStoragePermanentlyHideSplashScreen = "dashboard_permanentlyHideSplashScreen";

	var bHideScreen = (this._canUseLocalStorage()) ? this.m_oLocalStorage.getItem(this.m_sLocalStoragePermanentlyHideSplashScreen) : false;
	/* some browsers return strings from local storage rather than the boolean value */
	if (typeof bHideScreen == "string")
	{
		bHideScreen = JSON.parse(bHideScreen);
	}
	if (bHideScreen == true)
	{
		this.isVisible.setValue(false);
	}
};
caplin.extend(caplinx.dashboard.app.model.apps.SplashScreen, caplin.presenter.node.PresentationNode);

caplinx.dashboard.app.model.apps.SplashScreen.prototype._toggleLocalStoragePermanentHide = function()
{
	this.m_oLocalStorage.setItem(this.m_sLocalStoragePermanentlyHideSplashScreen, this.permanentlyHideSplashScreen.getValue());
}

caplinx.dashboard.app.model.apps.SplashScreen.prototype._closeSplashScreen = function()
{
	this.isVisible.setValue(false);
}

caplinx.dashboard.app.model.apps.SplashScreen.prototype._canUseLocalStorage = function()
{
	return (this.m_oLocalStorage.getItem != undefined && this.m_oLocalStorage.setItem != undefined);
}

caplinx.dashboard.app.model.apps.SplashScreen.prototype._showVideo = function()
{
	this.videoVisible.setValue(true);
	this.videoPlacholderVisible.setValue(false);
}

