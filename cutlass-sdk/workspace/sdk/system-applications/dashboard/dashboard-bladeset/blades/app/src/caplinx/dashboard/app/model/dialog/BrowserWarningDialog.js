caplinx.dashboard.app.model.dialog.BrowserWarningDialog = function(oPresentationModel)
{
	// call super constructor
	caplinx.dashboard.app.model.dialog.DialogViewNode.call(this, "browser-warning-dialog");
	
	this.isClosable.setValue(false);
	this.hasBackground.setValue( false );
	this.browserVersionsHtml = new br.presenter.property.EditableProperty("");
	this.m_pMinimumBrowserVersions = null;
};
br.extend(caplinx.dashboard.app.model.dialog.BrowserWarningDialog, caplinx.dashboard.app.model.dialog.DialogViewNode);

caplinx.dashboard.app.model.dialog.BrowserWarningDialog.prototype.initializeForm = function()
{
	// do nothing
};

caplinx.dashboard.app.model.dialog.BrowserWarningDialog.prototype.setMinimumBrowserVersions = function(pMinimumBrowserVersions)
{
	this.m_pMinimumBrowserVersions = pMinimumBrowserVersions;
	var sVersionsHtml = "<ul>";
	for (var browser in pMinimumBrowserVersions)
	{
		var browserVersion = pMinimumBrowserVersions[browser];
		sVersionsHtml += "<li><span class='browser'>"+browser+"</span><span class='browserVersion'>"+browserVersion+"</span></li>";
	}
	sVersionsHtml += "</ul>";
	this.browserVersionsHtml.setValue(sVersionsHtml);
};
