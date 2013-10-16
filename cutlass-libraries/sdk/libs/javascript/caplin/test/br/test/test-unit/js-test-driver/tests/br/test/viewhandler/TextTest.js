TextTest = TestCase("TextTest");

TextTest.prototype.setUp = function()
{
	this.m_oViewFixture = new br.test.ViewFixture("view.*");
	this.m_eElement = this.getElement();
	document.body.appendChild(this.m_eElement);
	this.m_oViewFixture.setViewElement(this.m_eElement);
};

TextTest.prototype.tearDown = function()
{
	document.body.removeChild(this.m_eElement);
};

TextTest.prototype.getElement = function()
{
	var eElement = document.createElement("div");
	eElement.id = "text-test";
	eElement.innerHTML =
		"<form id='the-form'>" +
			"<select id='empty-select'>" +
			"</select>" +
			"<select id='short-select'>" +
				"<option value=''></option>" +
				"<option value=''>Last Option</option>" +
			"</select>" +

			"<input id='gender-m' type='radio' name='gender' value='male' />" +
			"<input id='gender-f' type='radio' name='gender' value='female' checked='checked' />" +
		"</form>" +

		"<div id='no-text'>" +
			"<span class='inner'></span>" +
		"</div>" +
		"<div id='some-text'>" +
			"<div class='header'>Header Text</div>" +
			"<p>Para Text<span class='inner'>Inner Text</span></p>" +
		"</div>";

	return eElement;
};

TextTest.prototype.test_canGetTextOfElementsWithNoChildren = function()
{
	this.m_oViewFixture.doThen("view.(#empty-select).text", "");
	this.m_oViewFixture.doThen("view.(#short-select option:first).text", "");
	this.m_oViewFixture.doThen("view.(#short-select option:last).text", "Last Option");
	this.m_oViewFixture.doThen("view.(div#no-text span.inner).text", "");
	this.m_oViewFixture.doThen("view.(div#some-text span.inner).text", "Inner Text");
};

TextTest.prototype.test_canGetTextOfElementWithChildren = function()
{
	this.m_oViewFixture.doThen("view.(#short-select).text", "Last Option");
	this.m_oViewFixture.doThen("view.(div#some-text).text", "Header TextPara TextInner Text");
};

TextTest.prototype.test_canSetTextOfElementWithNoChildren = function()
{
	this.m_oViewFixture.doThen("view.(#short-select option:first).text", "");
	this.m_oViewFixture.doWhen("view.(#short-select option:first).text", "An option");
	this.m_oViewFixture.doThen("view.(#short-select option:first).text", "An option");

	this.m_oViewFixture.doThen("view.(#some-text .inner).text", "Inner Text");
	this.m_oViewFixture.doWhen("view.(#some-text .inner).text", "Other Text");
	this.m_oViewFixture.doThen("view.(#some-text .inner).text", "Other Text");
};

TextTest.prototype.test_cannotUseTextOnInputElements = function()
{
	var _self = this;
	assertException(function() {
		_self.m_oViewFixture.doThen("view.(input:first).text", "blah");
	}, br.Errors.INVALID_TEST);
};
