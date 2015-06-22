'use strict';

var PresentationNode = require('br/presenter/node/PresentationNode');
var Core = require('br/Core');
var EditableProperty = require('br/presenter/property/EditableProperty');

function SplashScreen(oPresentationModel) {
	this.m_oLocalStorage = oPresentationModel.getLocalStorage();

	this.isVisible = new EditableProperty(true);
	this.permanentlyHideSplashScreen = new EditableProperty(false);
	this.permanentlyHideSplashScreen.addChangeListener(this, '_toggleLocalStoragePermanentHide');


	this.m_sLocalStoragePermanentlyHideSplashScreen = 'dashboard_permanentlyHideSplashScreen';

	var bHideScreen = (this._canUseLocalStorage()) ? this.m_oLocalStorage.getItem(this.m_sLocalStoragePermanentlyHideSplashScreen) : false;

	/* some browsers return strings from local storage rather than the boolean value */
	if (typeof bHideScreen == 'string') {
		bHideScreen = JSON.parse(bHideScreen);
	}
	if (bHideScreen == true) {
		this.isVisible.setValue(false);
	}
}

Core.extend(SplashScreen, PresentationNode);

SplashScreen.prototype._toggleLocalStoragePermanentHide = function() {
	this.m_oLocalStorage.setItem(this.m_sLocalStoragePermanentlyHideSplashScreen, this.permanentlyHideSplashScreen.getValue());
};

SplashScreen.prototype._closeSplashScreen = function() {
	this.isVisible.setValue(false);
};

SplashScreen.prototype._canUseLocalStorage = function() {
	return (this.m_oLocalStorage.getItem != undefined && this.m_oLocalStorage.setItem != undefined);
};

module.exports = SplashScreen;
