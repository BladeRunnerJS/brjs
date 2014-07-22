ChildrenCountTest = TestCase("ChildrenCountTest");

var Errors = require('br/Errors');
var ViewFixture = require('br/test/ViewFixture');

ChildrenCountTest.prototype.setUp = function()
{
	this.m_oViewFixture = new ViewFixture("view.*");
	this.m_eElement = this.getElement();
	document.body.appendChild(this.m_eElement);
	this.m_oViewFixture.setViewElement(this.m_eElement);
};

ChildrenCountTest.prototype.tearDown = function()
{
	document.body.removeChild(this.m_eElement);
};

ChildrenCountTest.prototype.getElement = function()
{
	var eElement = document.createElement('div');
	eElement.innerHTML =
		"<form id='the-form'>" +
			"<input type='checkbox' name='hobbies' value='football' />" +
			"<input type='checkbox' name='hobbies' value='gaming' />" +
			"<input type='radio' name='gender' value='male' />" +
			"<input type='radio' name='gender' value='female' checked='checked'/>" +
		"</form>" +

		"<div id='main' class='level1'>" +
			"<div class='level2'></div>" +
			"<div class='level2'></div>" +
			"<div id='nested' class='level2'>" +
				"<span class='level3'></span>" +
			"</div>" +
		"</div>";
	return eElement;
};

ChildrenCountTest.prototype.test_canGetCorrectChildCountForElements = function()
{
	this.m_oViewFixture.doThen("view.(form).childrenCount", 4);
	this.m_oViewFixture.doThen("view.(div#main).childrenCount", 3);
	this.m_oViewFixture.doThen("view.(div.level2:first).childrenCount", 0);
	this.m_oViewFixture.doThen("view.(div#nested).childrenCount", 1);
	this.m_oViewFixture.doThen("view.(span.level3).childrenCount", 0);
};

ChildrenCountTest.prototype.test_cannotSetChildCountForElement = function()
{
	var _self = this;
	assertException(function() {
		_self.m_oViewFixture.doWhen("view.(form).childrenCount", 0);
	}, Errors.INVALID_TEST);
	assertException(function() {
		_self.m_oViewFixture.doWhen("view.(div#main).childrenCount", 100);
	}, Errors.INVALID_TEST);
	assertException(function() {
		_self.m_oViewFixture.doWhen("view.(div.level2:first).childrenCount", 42);
	}, Errors.INVALID_TEST);
};
