IsVisibleTest = TestCase("IsVisibleTest");

require('br/test/ViewFixture');
var Errors = require('br/Errors');
var ViewFixture = require('br/test/ViewFixture');

IsVisibleTest.prototype.setUp = function()
{
	this.m_oViewFixture = new ViewFixture("view.*");
	this.m_eElement = this.getElement();
	document.body.appendChild(this.m_eElement);
	this.m_oViewFixture.setViewElement(this.m_eElement);
};

IsVisibleTest.prototype.tearDown = function()
{
	document.body.removeChild(this.m_eElement);
};

IsVisibleTest.prototype.getElement = function()
{
	var eElement = document.createElement("div");
	eElement.id = "visibility-test";
	eElement.innerHTML =
		"<form id='the-form'>" +
			"<input id='visible-input' type='text' value='visible' />" +
			"<input id='hidden-input' type='hidden' />" +
		"</form>" +

		"<div id='visible-div'>Visible</div>" +
		"<div id='hidden-div' style='display: none;'>Invisible</div>" +
		"<div id='zero-div' style='width: 0; height: 0;'>Invisible</div>" +

		// To test that disabling parent elements disables children
		"<div id='hidden-parent' style='display: none;'>" +
			"<div class='parent-hidden'>Invisible</div>" +
		"</div>";

	return eElement;
};

IsVisibleTest.prototype.test_inputElementOfTypeTextIsVisible = function()
{
	this.m_oViewFixture.doThen("view.(input#visible-input).isVisible", true);
};

IsVisibleTest.prototype.test_inputElementOfTypeHiddenIsInvisible = function()
{
	this.m_oViewFixture.doThen("view.(input#hidden-input).isVisible", false);
};

IsVisibleTest.prototype.test_divElementWithoutStylingIsVisible = function()
{
	this.m_oViewFixture.doThen("view.(div#visible-div).isVisible", true);
};

IsVisibleTest.prototype.test_divElementWithDisplayNoneIsInvisible = function()
{
	this.m_oViewFixture.doThen("view.(div#hidden-div).isVisible", false);
};

IsVisibleTest.prototype.test_divElementWithZeroSizeIsInvisible = function()
{
	this.m_oViewFixture.doThen("view.(div#zero-div).isVisible", false);
};

IsVisibleTest.prototype.test_childOfInvisibleElementIsInvisible = function()
{
	this.m_oViewFixture.doThen("view.(div#hidden-parent).isVisible", false);
	this.m_oViewFixture.doThen("view.(div.parent-hidden).isVisible", false);
};

IsVisibleTest.prototype.test_cannotSetIsVisibleProperty = function()
{
	this.m_oViewFixture.doThen("view.(div#hidden-div).isVisible", false);

	var _self = this;
	assertException(function() {
		_self.m_oViewFixture.doWhen("view.(div#hidden-div).isVisible", true);
	}, Errors.INVALID_TEST);

	this.m_oViewFixture.doThen("view.(div#hidden-div).isVisible", false);
};
