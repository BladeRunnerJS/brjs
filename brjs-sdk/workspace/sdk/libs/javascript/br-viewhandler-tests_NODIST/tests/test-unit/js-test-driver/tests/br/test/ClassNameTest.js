ClassNameTest = TestCase("ClassNameTest");

require('br/test/ViewFixture');

ClassNameTest.prototype.setUp = function()
{
	this.m_oViewFixture = new br.test.ViewFixture("view.*");
	this.m_oViewFixture.setViewElement(this.getElement());
};

ClassNameTest.prototype.getElement = function()
{
	var eElement = document.createElement('div');
	eElement.innerHTML =
		"<div id='no-class' class=''></div>" +
		"<div id='single-class' class='one'></div>" +
		"<div id='double-class' class='one two'></div>" +
		"<div id='triple-class' class='one two three'></div>" +
		"";
	return eElement;
};

ClassNameTest.prototype.test_canGetCorrectClassNameOfElementWithNoClass = function()
{
	this.m_oViewFixture.doThen("view.(#no-class).className", "");
};

ClassNameTest.prototype.test_canGetCorrectClassNameOfElementWithOneClass = function()
{
	this.m_oViewFixture.doThen("view.(#single-class).className", "one");
};

ClassNameTest.prototype.test_canGetCorrectClassNameOfElementWithMoreThanOneClass = function()
{
	this.m_oViewFixture.doThen("view.(#double-class).className", "one two");
	this.m_oViewFixture.doThen("view.(#triple-class).className", "one two three");
};

ClassNameTest.prototype.test_canSetAnElementsClassToEmpty = function()
{
	this.m_oViewFixture.doThen("view.(#single-class).className", "one");
	this.m_oViewFixture.doWhen("view.(#single-class).className", "");
	this.m_oViewFixture.doThen("view.(#single-class).className", "");

	this.m_oViewFixture.doThen("view.(#triple-class).className", "one two three");
	this.m_oViewFixture.doWhen("view.(#single-class).className", "");
	this.m_oViewFixture.doThen("view.(#single-class).className", "");
};

ClassNameTest.prototype.test_canSetClassNameOnAnElementWithNoClasses = function()
{
	this.m_oViewFixture.doThen("view.(#no-class).className", "");
	this.m_oViewFixture.doWhen("view.(#no-class).className", "testing");
	this.m_oViewFixture.doThen("view.(#no-class).className", "testing");
};

ClassNameTest.prototype.test_canChangeClassNameOnAnElementWithExistingClasses = function()
{
	this.m_oViewFixture.doThen("view.(#double-class).className", "one two");
	this.m_oViewFixture.doWhen("view.(#double-class).className", "testing");
	this.m_oViewFixture.doThen("view.(#double-class).className", "testing");
};
