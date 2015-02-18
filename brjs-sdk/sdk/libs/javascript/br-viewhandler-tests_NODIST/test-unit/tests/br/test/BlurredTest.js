BlurredTest = TestCase("BlurredTest");

require('br/test/ViewFixture');
var Errors = require('br/Errors');
var ViewFixture = require('br/test/ViewFixture');

BlurredTest.prototype.setUp = function()
{
	this.m_oViewFixture = new ViewFixture("view.*");
	this.m_eElement = this.getElement();
	document.body.appendChild(this.m_eElement);
	this.m_oViewFixture.setViewElement(this.m_eElement);
};

BlurredTest.prototype.tearDown = function()
{
	document.body.removeChild(this.m_eElement);
};

BlurredTest.prototype.getElement = function()
{
	var eElement = document.createElement("div");
	eElement.id = "visibility-test";
	eElement.innerHTML =
		"<form id='the-form'>" +
			"<select>" +
				"<option value='1234'>AB12</option>" +
			"</select>" +
			"<select disabled='disabled'>" +
				"<option value='5678'>CD34</option>" +
			"</select>" +
			"<input type='text' />" +
			"<input type='button' value='click me' />" +
		"</form>" +
		"<div id='a-parent'>" +
			"<p>Paragraph Text</p>" +
			"<a id='good-link' href='#'>Anchor Text</a>" +
		"</div>";

	return eElement;
};

BlurredTest.prototype.test_focusableElementsAreBlurredInitially = function()
{
	this.m_oViewFixture.doThen("view.(#the-form select:first).blurred", true);
	this.m_oViewFixture.doThen("view.(input[type=text]).blurred", true);
	this.m_oViewFixture.doThen("view.(input[type=button]).blurred", true);
	this.m_oViewFixture.doThen("view.(div a#good-link).blurred", true);
	this.m_oViewFixture.doThen("view.(#the-form select[disabled]).blurred", true);
};

BlurredTest.prototype.test_canFocusOnElementsByUnblurring = function()
{
	this.m_oViewFixture.doThen("view.(div a).blurred", true);
	this.m_oViewFixture.doThen("view.(input[type=button]).blurred", true);
	this.m_oViewFixture.doWhen("view.(input[type=button]).blurred", false);
	this.m_oViewFixture.doThen("view.(input[type=button]).blurred", false);

	this.m_oViewFixture.doThen("view.(div a).blurred", true);
	this.m_oViewFixture.doWhen("view.(div a).blurred", false);
	this.m_oViewFixture.doThen("view.(div a).blurred", false);
	this.m_oViewFixture.doThen("view.(input[type=button]).blurred", true);
};

BlurredTest.prototype.test_cannotGetBlurredOnNonFocusableElements = function()
{
	var _self = this;
	assertException(function() {
		_self.m_oViewFixture.doThen("view.(form).blurred", true);
	}, Errors.INVALID_TEST);
	assertException(function() {
		_self.m_oViewFixture.doThen("view.(div p).blurred", true);
	}, Errors.INVALID_TEST);
};

BlurredTest.prototype.test_cannotSetBlurredOnNonFocusableElements = function()
{
	var _self = this;
	assertException(function() {
		_self.m_oViewFixture.doWhen("view.(form).blurred", true);
	}, Errors.INVALID_TEST);
	assertException(function() {
		_self.m_oViewFixture.doWhen("view.(div p).blurred", false);
	}, Errors.INVALID_TEST);
};

BlurredTest.prototype.test_cannotSetBlurredOnDisabledElements = function()
{
	var _self = this;
	assertException(function() {
		_self.m_oViewFixture.doWhen("view.(#the-form select[disabled]).blurred", false);
	}, Errors.INVALID_TEST);
};
