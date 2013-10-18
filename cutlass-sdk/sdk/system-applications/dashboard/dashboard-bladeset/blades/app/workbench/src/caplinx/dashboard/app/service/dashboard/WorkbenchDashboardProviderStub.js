caplinx.dashboard.app.service.dashboard.WorkbenchDashboardProviderStub = function()
{
	this.m_mApps = {
		"Example App #1":{
			"Example Bladeset #1":["Blade 1", "Blade 2"],
			"Example Bladeset #2":["Blade X", "Blade Y", "Blade Z"]
		},
		"Example App #2":{
			"My Bladeset":["Blade A", "Blade B"],
		}
	};
	this.m_bSuccessMode = true;
	this.RESPONSE_DELAY = 50;
	this.TEST_CHUNK_DELAY = 2000;
};
br.inherit(caplinx.dashboard.app.service.dashboard.WorkbenchDashboardProviderStub, caplinx.dashboard.app.service.dashboard.DashboardService);

caplinx.dashboard.app.service.dashboard.WorkbenchDashboardProviderStub.prototype.setIsLoadingProperty = function() { }

caplinx.dashboard.app.service.dashboard.WorkbenchDashboardProviderStub.prototype.setLoadingTextProperty = function() { }

caplinx.dashboard.app.service.dashboard.WorkbenchDashboardProviderStub.prototype.setSuccessMode = function()
{
	this.m_bSuccessMode = true;
};

caplinx.dashboard.app.service.dashboard.WorkbenchDashboardProviderStub.prototype.setFailureMode = function()
{
	this.m_bSuccessMode = false;
};

caplinx.dashboard.app.service.dashboard.WorkbenchDashboardProviderStub.prototype.getWarUrl = function(sApp)
{
	return this.getAppImageUrl(sApp);
};

caplinx.dashboard.app.service.dashboard.WorkbenchDashboardProviderStub.prototype.getAppImageUrl = function(sApp)
{
	return "images/bladeset_dashboard-bladeset/blade_app/theme_standard/img/plchldrApp-img3.png_image.bundle";
};

caplinx.dashboard.app.service.dashboard.WorkbenchDashboardProviderStub.prototype.getApps = function(fCallback, fErrorCallback)
{
	if(!this.m_bSuccessMode)
	{
		this._invokeErrorCallback("getApps", fErrorCallback);
	}
	else
	{
		var pApps = [];
		for(var sAppName in this.m_mApps)
		{
			pApps.push(sAppName);
		}
		
		window.setTimeout(function() {
			fCallback(pApps);
		}, this.RESPONSE_DELAY);
	}
};

caplinx.dashboard.app.service.dashboard.WorkbenchDashboardProviderStub.prototype.getApp = function(sApp, fCallback, fErrorCallback)
{
	if(!this.m_bSuccessMode)
	{
		this._invokeErrorCallback("getApp", fErrorCallback);
	}
	else
	{
		var oApp = this.m_mApps[sApp];
		
		window.setTimeout(function() {
			fCallback(oApp);
		}, this.RESPONSE_DELAY);
	}
};

caplinx.dashboard.app.service.dashboard.WorkbenchDashboardProviderStub.prototype.importMotif = function(sNewApp, sNamespace, eFileInputElem, fCallback, fErrorCallback)
{
	if(!this.m_bSuccessMode)
	{
		this._invokeErrorCallback("importMotif", fErrorCallback);
	}
	else
	{
		this.m_mApps[sNewApp] = {"FX":["Tile", "Ticket", "Grid", "Blotter"], "FI":["Ticket", "Grid", "Blotter"]};
		
		window.setTimeout(function() {
			fCallback();
		}, this.RESPONSE_DELAY);
	}
};

caplinx.dashboard.app.service.dashboard.WorkbenchDashboardProviderStub.prototype.resetDatabase = function(sApp, fCallback, fErrorCallback)
{
	if(!this.m_bSuccessMode)
	{
		this._invokeErrorCallback("resetDatabase", fErrorCallback);
	}
	else
	{
		window.setTimeout(function() {
			fCallback();
		}, this.RESPONSE_DELAY);
	}
};

caplinx.dashboard.app.service.dashboard.WorkbenchDashboardProviderStub.prototype.importBlades = function(sSourceApp, mBlades, sTargetApp, fCallback, fErrorCallback)
{
	if(!this.m_bSuccessMode)
	{
		this._invokeErrorCallback("importBlades", fErrorCallback);
	}
	else
	{
		for(var sBladeset in mBlades)
		{
			this.m_mApps[sTargetApp][sBladeset] = mBlades[sBladeset];
		}
		
		window.setTimeout(function() {
			fCallback();
		}, this.RESPONSE_DELAY);
	}
};

caplinx.dashboard.app.service.dashboard.WorkbenchDashboardProviderStub.prototype.createApp = function(sApp, sNamespace, fCallback, fErrorCallback)
{
	if(!this.m_bSuccessMode)
	{
		this._invokeErrorCallback("createApp", fErrorCallback);
	}
	else
	{
		this.m_mApps[sApp] = {"Bladeset A":["Blade One", "Blade Two"], "Bladeset B":["Blade Foo", "Blade Bar"]};
		
		window.setTimeout(function() {
			fCallback();
		}, this.RESPONSE_DELAY);
	}
};

caplinx.dashboard.app.service.dashboard.WorkbenchDashboardProviderStub.prototype.createBladeset = function(sApp, sBladeset, fCallback, fErrorCallback)
{
	if(!this.m_bSuccessMode)
	{
		this._invokeErrorCallback("createBladeset", fErrorCallback);
	}
	else
	{
		this.m_mApps[sApp][sBladeset] = ["My Blade #1", "My Blade #2"];
		
		window.setTimeout(function() {
			fCallback();
		}, this.RESPONSE_DELAY);
	}
};

caplinx.dashboard.app.service.dashboard.WorkbenchDashboardProviderStub.prototype.createBlade = function(sApp, sBladeset, sBlade, fCallback, fErrorCallback)
{
	if(!this.m_bSuccessMode)
	{
		this._invokeErrorCallback("createBlade", fErrorCallback);
	}
	else
	{
		this.m_mApps[sApp][sBladeset].push(sBlade);
		
		window.setTimeout(function() {
			fCallback();
		}, this.RESPONSE_DELAY);
	}
};

caplinx.dashboard.app.service.dashboard.WorkbenchDashboardProviderStub.prototype.runBladesetTests = function(sApp, sBladeset, fProgressListener, fErrorCallback)
{
	if(!this.m_bSuccessMode)
	{
		this._invokeErrorCallback("runBladesetTests", fErrorCallback);
	}
	else
	{
		var oThis = this;
		window.setTimeout(function() {
			oThis._sendTestOutput(fProgressListener, oThis.m_mApps[sApp][sBladeset]);
		}, this.TEST_CHUNK_DELAY);
	}
};

caplinx.dashboard.app.service.dashboard.WorkbenchDashboardProviderStub.prototype.runBladeTests = function(sApp, sBladeset, sBlade, fProgressListener, fErrorCallback)
{
	if(!this.m_bSuccessMode)
	{
		this._invokeErrorCallback("runBladeTests", fErrorCallback);
	}
	else
	{
		var oThis = this;
		window.setTimeout(function() {
			oThis._sendTestOutput(fProgressListener, [sBlade]);
		}, this.TEST_CHUNK_DELAY);
	}
};

caplinx.dashboard.app.service.dashboard.WorkbenchDashboardProviderStub.prototype.getCurrentReleaseNote = function(fCallback, fErrorCallback)
{
	if(!this.m_bSuccessMode)
	{
		this._invokeErrorCallback("getCurrentReleaseNote", fErrorCallback);
	}
	else
	{
		window.setTimeout(function() {
			fCallback(
				"<h2>Version: 1.0 (Workbench Edition)</h2>" +
				"<p>" +
				"	This is a fake release note." +
				"</p>"
			);
		}, this.RESPONSE_DELAY);
	}
};

caplinx.dashboard.app.service.dashboard.WorkbenchDashboardProviderStub.prototype.getSdkVersion = function(fCallback, fErrorCallback)
{
	if(!this.m_bSuccessMode)
	{
		this._invokeErrorCallback("getSdkVersion", fErrorCallback);
	}
	else
	{
		window.setTimeout(function() {
			fCallback( "Workbench" );
		}, this.RESPONSE_DELAY);
	}
};

caplinx.dashboard.app.service.dashboard.WorkbenchDashboardProviderStub.prototype.getTestResultsUrl = function()
{
	return "about:blank";
};

caplinx.dashboard.app.service.dashboard.WorkbenchDashboardProviderStub.prototype._sendTestOutput = function(fProgressListener, pBlades)
{
	var sBlade = pBlades.pop();
	var bTestOutputComplete = pBlades.length == 0;
	
	fProgressListener(this._getFakeTestResults(sBlade), bTestOutputComplete);
	
	if(!bTestOutputComplete)
	{
		var oThis = this;
		window.setTimeout(function() {
			oThis._sendTestOutput(fProgressListener, pBlades);
		}, this.TEST_CHUNK_DELAY);
	}
};

caplinx.dashboard.app.service.dashboard.WorkbenchDashboardProviderStub.prototype._getFakeTestResults = function(sBlade)
{
	var sTestResult =
		"Testing " + sBlade + " (UTs):\n" +
		"Microsoft Internet Explorer: Reset\n" +
		"Microsoft Internet Explorer: Reset\n" +
		"......................................................................\n" +
		"......................................................................\n" +
		"......................................................................\n" +
		".............................\n" +
		"Total 239 tests (Passed: 239; Fails: 0; Errors: 0) (2672.00 ms)\n" +
		"  Microsoft Internet Explorer 8.0 Windows: Run 239 tests (Passed: 239; Fails: 0; Errors 0) (2672.00 ms)\n" +
		"09-Aug-2012 09:52:18 com.google.jstestdriver.ActionRunner runActions\n" +
		"INFO: \n" +
		"Tests Passed.\n" +
		"\n";
	
	return sTestResult;
};

caplinx.dashboard.app.service.dashboard.WorkbenchDashboardProviderStub.prototype._invokeErrorCallback = function(sOperation, fErrorCallback)
{
	window.setTimeout(function() {
		fErrorCallback("There was an unexpected error invoking '" + sOperation + "'.");
	}, this.RESPONSE_DELAY);
};
