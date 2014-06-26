br.Core.thirdparty("jquery");

require('br/test/ViewFixture');

ClickedTest = TestCase("ClickedTest");

ClickedTest.prototype.setUp = function()
{
	this.m_oViewFixture = new br.test.ViewFixture("view.*");
	this.m_eElement = this.getElement();
	document.body.appendChild(this.m_eElement);
	this.m_oViewFixture.setViewElement(this.m_eElement);
	this.m_oClickElementClicked = false;
};

ClickedTest.prototype.tearDown = function()
{
	document.body.removeChild(this.m_eElement);
};

ClickedTest.prototype.getElement = function()
{
	var eElement = document.createElement("div");
	eElement.id = "visibility-test";
	eElement.innerHTML =
		"<form id='the-form'>" +
			"<input id='click-target' type='button' value='visible' />" +
		"</form>";
	var oSelf = this;
	jQuery(eElement).find('#click-target').click(function(){oSelf.m_oClickElementClicked = true});
	return eElement;
};

ClickedTest.prototype.test_settingClickedFiresClickEvent = function()
{
	this.m_oViewFixture.doWhen("view.(#click-target).clicked", true);
	assertTrue(this.m_oClickElementClicked);
};

ClickedTest.prototype.test_cannotGetClickedProperty= function()
{
	var _self = this;
	assertException(function() {
		_self.m_oViewFixture.doThen("view.(#click-target).clicked", true);
	}, br.Errors.INVALID_TEST);
};
