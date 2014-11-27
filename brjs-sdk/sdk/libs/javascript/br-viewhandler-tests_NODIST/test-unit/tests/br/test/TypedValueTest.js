TypedValueTest = TestCase("TypedValueTest");

require('br/test/ViewFixture');
var Errors = require('br/Errors');
var ViewFixture = require('br/test/ViewFixture');

TypedValueTest.prototype.setUp = function()
{
	this.m_oViewFixture = new ViewFixture("view.*");
	this.m_oViewFixture.setViewElement(this.getElement());
};

TypedValueTest.prototype.tearDown = function()
{
};

TypedValueTest.prototype.getElement = function()
{
	var eElement = document.createElement('div');
	eElement.innerHTML =
		"<form id='the-form'>" +
			"<select id='filled-select'>" +
				"<option value=''>ABC</option>" +
				"<option value='456'></option>" +
			"</select>" +

			"<input id='empty-text' type='text' value='' />" +
			"<input id='partial-text' type='text' value='some t' />" +
			"<textarea id='textArea' rows='10' cols='10'>Hello, </textarea>" +
		"</form>" +

		"<div class='no-value'>" +
		"</div>" +
		"<div><textarea id='anotherTextArea' rows='10' cols='10'></textarea></div>";

	return eElement;
};

TypedValueTest.prototype.test_canTypeValueIntoEmptyTextBox = function()
{
	this.m_oViewFixture.doGiven("view.(input#empty-text).value", "");
	this.m_oViewFixture.doWhen("view.(input#empty-text).typedValue", "hello");
	this.m_oViewFixture.doThen("view.(input#empty-text).value", "hello");
};

TypedValueTest.prototype.test_canTypeOnTheEndOfExistingTextInATextBox = function()
{
	this.m_oViewFixture.doGiven("view.(input#partial-text).value", "some t");
	this.m_oViewFixture.doWhen("view.(input#partial-text).typedValue", "ext");
	this.m_oViewFixture.doThen("view.(input#partial-text).value", "some text");

	this.m_oViewFixture.doGiven("view.(textarea#textArea).value", "Hello, ");
	this.m_oViewFixture.doWhen("view.(textarea#textArea).typedValue", "World!");
	this.m_oViewFixture.doThen("view.(textarea#textArea).value", "Hello, World!");
};

TypedValueTest.prototype.test_cannotGetTypedValue = function()
{
	var _self = this;
	assertException(function() {
		_self.m_oViewFixture.doThen("view.(input#partial-text).typedValue", "some t");
	}, Errors.INVALID_TEST);
	assertException(function() {
		_self.m_oViewFixture.doThen("view.(textarea#textArea).typedValue", "Hello, ");
	}, Errors.INVALID_TEST);
};

TypedValueTest.prototype.test_usingTypedValueOnNonInputElementsThrowsException = function()
{
	var _self = this;
	assertException(function() {
		_self.m_oViewFixture.doWhen("view.(form).typedValue", "type");
	}, Errors.INVALID_TEST);
	assertException(function() {
		_self.m_oViewFixture.doWhen("view.(.no-value).typedValue", "text");
	}, Errors.INVALID_TEST);
};

TypedValueTest.prototype.test_canTypeEnterIntoATextBox = function()
{
	this.m_oViewFixture.doGiven("view.(textarea#anotherTextArea).value", "");
	this.m_oViewFixture.doWhen("view.(textarea#anotherTextArea).typedValue", "hello\rworld");
	this.m_oViewFixture.doThen("view.(textarea#anotherTextArea).value", "hello\nworld");
};

