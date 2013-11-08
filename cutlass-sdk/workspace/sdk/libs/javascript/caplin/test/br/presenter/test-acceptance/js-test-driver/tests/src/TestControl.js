TestControl = function()
{
	this.m_eElement = document.createElement("span");
	this.m_nOnViewReadyInvocationCount = 0;
};
br.inherit(TestControl, br.presenter.control.ControlAdaptor);

TestControl.prototype.setElement = function(parentElement) {
	parentElement.appendChild(this.m_eElement);
};

TestControl.prototype.setPresentationNode = function(oField)
{
	this.m_oField = oField;
	this.m_eElement.innerHTML = oField.value.getValue();
};

TestControl.prototype.setOptions = function() {};

TestControl.prototype.onViewReady = function()
{
	this.m_eElement.innerHTML = this.m_oField.value.getValue() + ": onViewReady() invoked " + ++this.m_nOnViewReadyInvocationCount + " time(s)";
};

TestControl.onAfterClassLoad = function() {
	br.presenter.control.ControlAdaptorFactory.registerConfiguredControlAdaptor('caplin-test-control', TestControl, {});
};


TestControl.onAfterClassLoad();
