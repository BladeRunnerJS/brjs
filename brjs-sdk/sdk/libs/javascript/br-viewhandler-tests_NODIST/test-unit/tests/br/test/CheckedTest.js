CheckedTest = TestCase("CheckedTest");

var Errors = require('br/Errors');
var ViewFixture = require('br/test/ViewFixture');

CheckedTest.prototype.setUp = function()
{
	this.m_oViewFixture = new ViewFixture("view.*");
	this.m_eElement = this.getElement();
	document.body.appendChild(this.m_eElement);
	this.m_oViewFixture.setViewElement(this.m_eElement);
};

CheckedTest.prototype.tearDown = function()
{
	document.body.removeChild(this.m_eElement);
};

CheckedTest.prototype.getElement = function()
{
	var eElement = document.createElement('div');
	eElement.innerHTML =
		"<form id='the-form'>" +

			// checkboxes
			"<input id='hobby1' type='checkbox' name='hobbies' value='football' />" +
			"<input id='hobby2' type='checkbox' name='hobbies' value='painting' checked='checked' />" +
			"<input id='hobby3' type='checkbox' name='hobbies' value='gaming' />" +

			// radio buttons
			"<input id='gender-m' type='radio' name='gender' value='male' />" +
			"<input id='gender-f' type='radio' name='gender' value='female' checked='checked'/>" +
		"</form>" +

		"<div class='uncheckable'>" +
		"</div>";

	return eElement;
};

CheckedTest.prototype.test_canGetCheckedStatusOfCheckBoxes = function()
{
	this.m_oViewFixture.doThen("view.(input#hobby1).checked", false);
	this.m_oViewFixture.doThen("view.(input#hobby2).checked", true);
	this.m_oViewFixture.doThen("view.(input#hobby3).checked", false);
};

CheckedTest.prototype.test_canGetCheckedStatusOfRadios = function()
{
	this.m_oViewFixture.doThen("view.(input#gender-m).checked", false);
	this.m_oViewFixture.doThen("view.(input#gender-f).checked", true);
};

CheckedTest.prototype.test_canSetCheckBoxesToBeChecked = function()
{
	this.m_oViewFixture.doThen("view.(input#hobby1).checked", false);
	this.m_oViewFixture.doWhen("view.(input#hobby1).checked", true);
	this.m_oViewFixture.doThen("view.(input#hobby1).checked", true);

	this.m_oViewFixture.doThen("view.(input#hobby2).checked", true);
	this.m_oViewFixture.doWhen("view.(input#hobby2).checked", false);
	this.m_oViewFixture.doThen("view.(input#hobby2).checked", false);
};

CheckedTest.prototype.test_canSetCheckedItemInARadioGroup = function()
{
	this.m_oViewFixture.doThen("view.(input#gender-m).checked", false);
	this.m_oViewFixture.doWhen("view.(input#gender-m).checked", true);
	this.m_oViewFixture.doThen("view.(input#gender-m).checked", true);
};

CheckedTest.prototype.test_cannotGetCheckedStatusOfUncheckableElement = function()
{
	var _self = this;
	assertException(function () {
		_self.m_oViewFixture.doThen("view.(.uncheckable).checked", false);
	}, Errors.INVALID_TEST);
};

CheckedTest.prototype.test_cannotSetCheckedStatusOfUncheckableElement = function() {
	var _self = this;
	assertException(function () {
		_self.m_oViewFixture.doWhen("view.(.uncheckable).checked", false);
	}, Errors.INVALID_TEST);
	assertException(function () {
		_self.m_oViewFixture.doWhen("view.(.uncheckable).checked", true);
	}, Errors.INVALID_TEST);
};

CheckedTest.prototype.test_cannotSetCheckedStatusToNonBooleanValue = function() {
	var _self = this;
	assertException("1a", function () {
		_self.m_oViewFixture.doWhen("view.(input#hobby1).checked", 1);
	}, Errors.INVALID_TEST);
	assertException("1b", function () {
		_self.m_oViewFixture.doWhen("view.(input#hobby1).checked", 0);
	}, Errors.INVALID_TEST);
	assertException("1c", function () {
		_self.m_oViewFixture.doWhen("view.(input#hobby2).checked", "false");
	}, Errors.INVALID_TEST);
	assertException("1d", function () {
		_self.m_oViewFixture.doWhen("view.(input#hobby2).checked", "on");
	}, Errors.INVALID_TEST);

	assertException("2a", function () {
		_self.m_oViewFixture.doWhen("view.(input#gender-m).checked", 1);
	}, Errors.INVALID_TEST);
	assertException("2b", function () {
		_self.m_oViewFixture.doWhen("view.(input#gender-m).checked", "true");
	}, Errors.INVALID_TEST);
	assertException("2c", function () {
		_self.m_oViewFixture.doWhen("view.(input#gender-f).checked", 0);
	}, Errors.INVALID_TEST);
	assertException("2d", function () {
		_self.m_oViewFixture.doWhen("view.(input#gender-f).checked", "off");
	}, Errors.INVALID_TEST);
};
