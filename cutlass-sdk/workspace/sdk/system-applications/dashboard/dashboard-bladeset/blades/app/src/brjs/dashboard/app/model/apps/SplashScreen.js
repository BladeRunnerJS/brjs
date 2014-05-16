brjs.dashboard.app.model.apps.SplashScreen = function(oPresentationModel)
{
	this.m_oLocalStorage = oPresentationModel.getLocalStorage();
	
	this.isVisible = new br.presenter.property.EditableProperty(true);
	this.permanentlyHideSplashScreen = new br.presenter.property.EditableProperty(false);
	this.permanentlyHideSplashScreen.addChangeListener(this, "_toggleLocalStoragePermanentHide");

	
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
br.Core.extend(brjs.dashboard.app.model.apps.SplashScreen, br.presenter.node.PresentationNode);

brjs.dashboard.app.model.apps.SplashScreen.prototype._toggleLocalStoragePermanentHide = function()
{
	this.m_oLocalStorage.setItem(this.m_sLocalStoragePermanentlyHideSplashScreen, this.permanentlyHideSplashScreen.getValue());
}

brjs.dashboard.app.model.apps.SplashScreen.prototype._closeSplashScreen = function()
{
	this.isVisible.setValue(false);
}

brjs.dashboard.app.model.apps.SplashScreen.prototype._canUseLocalStorage = function()
{
	return (this.m_oLocalStorage.getItem != undefined && this.m_oLocalStorage.setItem != undefined);
}
