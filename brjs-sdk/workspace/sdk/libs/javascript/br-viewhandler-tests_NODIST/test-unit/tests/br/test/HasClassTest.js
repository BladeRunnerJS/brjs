require('jsunitextensions');
HasClassTest = TestCase("HasClassTest");

var Errors = require('br/Errors');
var ViewFixture = require('br/test/ViewFixture');

HasClassTest.prototype.setUp = function()
{
	this.m_oViewFixture = new ViewFixture("view.*");
	this.m_oViewFixture.setViewElement(this.getElement());
};

HasClassTest.prototype.getElement = function()
{
	var eElement = document.createElement('div');
	eElement.innerHTML = "<div id='single-div' class='one two three'></div>";
	return eElement;
};

HasClassTest.prototype.test_firstClassInListMatchesSuccessfully = function()
{
	this.m_oViewFixture.doThen("view.(#single-div).hasClass", "one");
};

HasClassTest.prototype.test_secondClassInListMatchesSuccessfully = function()
{
	this.m_oViewFixture.doThen("view.(#single-div).hasClass", "two");
};

HasClassTest.prototype.test_thirdClassInListMatchesSuccessfully = function()
{
	this.m_oViewFixture.doThen("view.(#single-div).hasClass", "three");
};

HasClassTest.prototype.test_nonExistentClassThrowsAnError = function()
{
	var self = this;
	assertAssertError(function() {
		self.m_oViewFixture.doThen("view.(#single-div).hasClass", "no-such-class");
	});
};

HasClassTest.prototype.test_cannotSetHasClass = function()
{
	var self = this;
	assertException(function() {
		self.m_oViewFixture.doWhen("view.(#single-div).hasClass", "never-applied-class");
	}, Errors.INVALID_TEST);
};
