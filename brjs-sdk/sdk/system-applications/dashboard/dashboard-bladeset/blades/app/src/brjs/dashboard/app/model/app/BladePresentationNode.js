brjs.dashboard.app.model.app.BladePresentationNode = function(sBladeName, oParentBladeset, oPresentationModel)
{
	this.m_oPresentationModel = oPresentationModel;
	this.m_oParentBladeset = oParentBladeset;
	this.m_fOnTestProgress = this._onTestProgress.bind(this);
	this.m_fOnTestError = this._onTestError.bind(this);
	this.bladeName = new br.presenter.property.Property(sBladeName);
	this.workbenchUrl = new br.presenter.property.WritableProperty();

	if(this.m_oPresentationModel)
	{
		var appName = this.m_oPresentationModel.appDetailScreen.appName.getValue();
		var bladesetName = this.m_oParentBladeset.bladesetName.getValue();
		var bladeName = this.bladeName.getValue();
		var sWorkbenchPopoutUrl = "/" + appName + "/" + bladesetName + "/" + bladeName + "/workbench/";
		this.workbenchUrl.setValue(sWorkbenchPopoutUrl);
	}
};
br.Core.extend(brjs.dashboard.app.model.app.BladePresentationNode, br.presenter.node.PresentationNode);

brjs.dashboard.app.model.app.BladePresentationNode.prototype.runTests = function()
{
	if(this.m_oPresentationModel.getDashboardService().getTestRunInProgress())
	{
		this.m_oPresentationModel.dialog.displayNotification("Can't run tests. The test run is in progress.");
		return;
	}

	this.m_oPresentationModel.getDashboardService().setTestRunInProgress(true);
	var sApp = this.m_oPresentationModel.appDetailScreen.appName.getValue();
	var sBladeset = this.m_oParentBladeset.bladesetName.getValue();
	var sBlade = this.bladeName.getValue();
	this.m_oPresentationModel.dialog.displayNotification("Running tests... (this may take a few minutes). Please wait for test results.");
	this.m_oPresentationModel.getDashboardService().runBladeTests(sApp, sBladeset, sBlade, this.m_fOnTestProgress, this.m_fOnTestError);
};

brjs.dashboard.app.model.app.BladePresentationNode.prototype.popoutWorkbench = function()
{
	this.m_oPresentationModel.getWindowOpenerService().openWindow(this.workbenchUrl.getValue());
};

brjs.dashboard.app.model.app.BladePresentationNode.prototype._onTestProgress = function(sTestOutput)
{
	this.m_oPresentationModel.getDashboardService().setTestRunInProgress(false);
	this.m_oPresentationModel.dialog.showDialog("testRunnerDialog");
	this.m_oPresentationModel.dialog.testRunnerDialog.displayTestResults();
};

brjs.dashboard.app.model.app.BladePresentationNode.prototype._onTestError = function(sErrorMessage)
{
	this.m_oPresentationModel.getDashboardService().setTestRunInProgress(false);
	this.m_oPresentationModel.dialog.showDialog("testRunnerDialog");
	this.m_oPresentationModel.dialog.testRunnerDialog.displayTestError(sErrorMessage);
};
