FocusedTest = TestCase("FocusedTest");

require('br/test/ViewFixture');
var Errors = require('br/Errors');
var ViewFixture = require('br/test/ViewFixture');

FocusedTest.prototype.setUp = function()
{
	this.m_oViewFixture = new ViewFixture("view.*");
	this.m_eElement = this.getElement();
	document.body.appendChild(this.m_eElement);
	this.m_oViewFixture.setViewElement(this.m_eElement);
};

FocusedTest.prototype.tearDown = function()
{
	document.body.removeChild(this.m_eElement);
};

FocusedTest.prototype.getElement = function()
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

FocusedTest.prototype.test_focusableElementsAreUnfocusedInitially = function()
{
	this.m_oViewFixture.doThen("view.(#the-form select:first).focused", false);
	this.m_oViewFixture.doThen("view.(input[type=text]).focused", false);
	this.m_oViewFixture.doThen("view.(input[type=button]).focused", false);
	this.m_oViewFixture.doThen("view.(div a#good-link).focused", false);
	this.m_oViewFixture.doThen("view.(#the-form select[disabled]).focused", false);
};

FocusedTest.prototype.test_canChangeFocusOnElements = function()
{
	this.m_oViewFixture.doThen("view.(div a).focused", false);
	this.m_oViewFixture.doThen("view.(input[type=button]).focused", false);
	this.m_oViewFixture.doWhen("view.(input[type=button]).focused", true);
	this.m_oViewFixture.doThen("view.(input[type=button]).focused", true);

	this.m_oViewFixture.doThen("view.(div a).focused", false);
	this.m_oViewFixture.doWhen("view.(div a).focused", true);
	this.m_oViewFixture.doThen("view.(div a).focused", true);
	this.m_oViewFixture.doThen("view.(input[type=button]).focused", false);
};

FocusedTest.prototype.test_cannotGetFocusedOnNonFocusableElements = function()
{
	var _self = this;
	assertException(function() {
		_self.m_oViewFixture.doThen("view.(form).focused", false);
	}, Errors.INVALID_TEST);
	assertException(function() {
		_self.m_oViewFixture.doThen("view.(div p).focused", false);
	}, Errors.INVALID_TEST);
};

FocusedTest.prototype.test_cannotSetFocusedOnNonFocusableElements = function()
{
	var _self = this;
	assertException(function() {
		_self.m_oViewFixture.doWhen("view.(form).focused", false);
	}, Errors.INVALID_TEST);
	assertException(function() {
		_self.m_oViewFixture.doWhen("view.(div p).focused", false);
	}, Errors.INVALID_TEST);
};

FocusedTest.prototype.test_cannotSetFocusedOnDisabledElements = function()
{
	var _self = this;
	assertException(function() {
		_self.m_oViewFixture.doWhen("view.(#the-form select[disabled]).focused", true);
	}, Errors.INVALID_TEST);
};
