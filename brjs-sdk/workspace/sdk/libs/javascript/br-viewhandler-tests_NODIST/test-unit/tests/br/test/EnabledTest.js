EnabledTest = TestCase("EnabledTest");

require('br/test/ViewFixture');
var ViewFixture = require('br/test/ViewFixture');

EnabledTest.prototype.setUp = function()
{
	this.m_oViewFixture = new ViewFixture("view.*");
	this.m_oViewFixture.setViewElement(this.getElement());
};

EnabledTest.prototype.getElement = function()
{
	var eElement = document.createElement('div');
	eElement.innerHTML =
		"<form id='the-form'>" +
			"<select id='enabled-select'>" +
				"<option value='1234'>AB12</option>" +
			"</select>" +
			"<select id='disabled-select' disabled='disabled'>" +
				"<option value='5678'>CD34</option>" +
			"</select>" +
			"<input id='enabled-input' type='text' />" +
			"<input id='disabled-input' type='text' disabled='disabled' />" +
		"</form>";

	return eElement;
};

EnabledTest.prototype.test_formElementWithoutDisabledAttributeIsEnabled = function()
{
	this.m_oViewFixture.doThen("view.(select#enabled-select).enabled", true);
	this.m_oViewFixture.doThen("view.(input#enabled-input).enabled", true);
};

EnabledTest.prototype.test_formElementWithDisabledAttributeIsDisabled = function()
{
	this.m_oViewFixture.doThen("view.(select#disabled-select).enabled", false);
};

EnabledTest.prototype.test_childOfDisabledElementIsDisabled = function()
{
	this.m_oViewFixture.doThen("view.(select#disabled-select).enabled", false);
	this.m_oViewFixture.doThen("view.(select#disabled-select option).enabled", false);
};

EnabledTest.prototype.test_canEnableDisabledFormElements = function()
{
	this.m_oViewFixture.doThen("view.(input#disabled-input).enabled", false);
	this.m_oViewFixture.doWhen("view.(input#disabled-input).enabled", true);
	this.m_oViewFixture.doThen("view.(input#disabled-input).enabled", true);

	this.m_oViewFixture.doThen("view.(select#disabled-select).enabled", false);
	this.m_oViewFixture.doWhen("view.(select#disabled-select).enabled", true);
	this.m_oViewFixture.doThen("view.(select#disabled-select).enabled", true);
};

EnabledTest.prototype.test_canDisableEnabledFormElements = function()
{
	this.m_oViewFixture.doThen("view.(input#enabled-input).enabled", true);
	this.m_oViewFixture.doWhen("view.(input#enabled-input).enabled", false);
	this.m_oViewFixture.doThen("view.(input#enabled-input).enabled", false);

	this.m_oViewFixture.doThen("view.(select#enabled-select).enabled", true);
	this.m_oViewFixture.doWhen("view.(select#enabled-select).enabled", false);
	this.m_oViewFixture.doThen("view.(select#enabled-select).enabled", false);
};
