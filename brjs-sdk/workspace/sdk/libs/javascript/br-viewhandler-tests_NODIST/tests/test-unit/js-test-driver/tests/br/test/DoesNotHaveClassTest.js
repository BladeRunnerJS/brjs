require("jsunitextensions");
DoesNotHaveClassTest = TestCase("DoesNotHaveClassTest");

require('br/test/ViewFixture');

DoesNotHaveClassTest.prototype.setUp = function()
{
	this.m_oViewFixture = new br.test.ViewFixture("view.*");
	this.m_oViewFixture.setViewElement(this.getElement());
};

DoesNotHaveClassTest.prototype.getElement = function()
{
	var eElement = document.createElement('div');
	eElement.innerHTML = "<div id='single-div' class='one two three'></div>";
	return eElement;
};

DoesNotHaveClassTest.prototype.test_firstClassInListMatchesSuccessfully = function()
{
	var self = this;
	assertAssertError("1a", function() {
		self.m_oViewFixture.doThen("view.(#single-div).doesNotHaveClass", "one");
	});
};

DoesNotHaveClassTest.prototype.test_secondClassInListMatchesSuccessfully = function()
{
	var self = this;
	assertAssertError("1a", function() {
		self.m_oViewFixture.doThen("view.(#single-div).doesNotHaveClass", "two");
	});
};

DoesNotHaveClassTest.prototype.test_thirdClassInListMatchesSuccessfully = function()
{
	var self = this;
	assertAssertError("1a", function() {
		self.m_oViewFixture.doThen("view.(#single-div).doesNotHaveClass", "three");
	});
};

DoesNotHaveClassTest.prototype.test_nonExistentClassPasses = function()
{
	this.m_oViewFixture.doThen("view.(#single-div).doesNotHaveClass", "no-such-class");
};

DoesNotHaveClassTest.prototype.test_cannotSetDoesNotHaveClass = function()
{
	var self = this;
	assertException(function() {
		self.m_oViewFixture.doWhen("view.(#single-div).doesNotHaveClass", "one");
	}, br.Errors.INVALID_TEST);
};
