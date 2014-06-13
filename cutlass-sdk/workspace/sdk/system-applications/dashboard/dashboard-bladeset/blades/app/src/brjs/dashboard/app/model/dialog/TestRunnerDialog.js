brjs.dashboard.app.model.dialog.TestRunnerDialog = function(oPresentationModel)
{
	// call super constructor
	brjs.dashboard.app.model.dialog.DialogViewNode.call(this, "test-runner-dialog");
	
	this.m_oPresentationModel = oPresentationModel;

	var sTestResultsUrl = oPresentationModel.getDashboardService().getTestResultsUrl();
	this.testResultsLink = new br.presenter.node.Field(sTestResultsUrl);
	this.errorMessage = new br.presenter.node.Field("");
	this.testsPassed = new br.presenter.property.EditableProperty(false);
};
br.Core.extend(brjs.dashboard.app.model.dialog.TestRunnerDialog, brjs.dashboard.app.model.dialog.DialogViewNode);

brjs.dashboard.app.model.dialog.TestRunnerDialog.prototype.initializeForm = function()
{
	this.testResultsLink.visible.setValue(false);
	this.errorMessage.value.setValue("");
	this.errorMessage.visible.setValue(false);
	this.testsPassed.setValue(false);
};

brjs.dashboard.app.model.dialog.TestRunnerDialog.prototype.displayTestResults = function()
{
	this.testResultsLink.visible.setValue(true);
	this.testsPassed.setValue(true);
};

brjs.dashboard.app.model.dialog.TestRunnerDialog.prototype.displayTestError = function(sErrorMessage)
{
	this.testResultsLink.visible.setValue(true);
	this.errorMessage.value.setValue(sErrorMessage + " Check BladeRunner console for error messages.");
	this.errorMessage.visible.setValue(true);
};

brjs.dashboard.app.model.dialog.TestRunnerDialog.prototype.closeDialog = function()
{
	this.m_oPresentationModel.dialog.visible.setValue(false);
};