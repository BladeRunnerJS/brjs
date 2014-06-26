ValueTest = TestCase("ValueTest");

require('br/test/ViewFixture');

ValueTest.prototype.setUp = function()
{
	this.m_oViewFixture = new br.test.ViewFixture("view.*");
	this.m_oViewFixture.setViewElement(this.getElement());
};

ValueTest.prototype.tearDown = function()
{
};

ValueTest.prototype.getElement = function()
{
	var eElement = document.createElement('div');
	eElement.innerHTML =
		"<form id='the-form'>" +
			"<select id='filled-select'>" +
				"<option>ABC</option>" +
				"<option value=''>DEF</option>" +
				"<option selected='selected' value='456'></option>" +
			"</select>" +

			"<input id='empty-text' type='text' value='' />" +
			"<input id='some-text' type='text' value='some text' />" +
			"<textarea rows='10' cols='10'>Hello!</textarea>" +
		"</form>" +

		"<div class='no-value'>" +
		"</div>";

	return eElement;
};

ValueTest.prototype.test_canGetValueOfElements = function()
{
	this.m_oViewFixture.doThen("view.(input#empty-text).value", "");
	this.m_oViewFixture.doThen("view.(input#some-text).value", "some text");
	this.m_oViewFixture.doThen("view.(textarea).value", "Hello!");

	this.m_oViewFixture.doThen("view.(#filled-select option:first).value", "ABC");
	this.m_oViewFixture.doThen("view.(#filled-select option:nth-child(2)).value", "");
	this.m_oViewFixture.doThen("view.(#filled-select option:last).value", "456");
	this.m_oViewFixture.doThen("view.(#filled-select).value", "456");
};

ValueTest.prototype.test_canSetValueOfTextBoxElements = function()
{
	this.m_oViewFixture.doThen("view.(input#empty-text).value", "");
	this.m_oViewFixture.doWhen("view.(input#empty-text).value", "hello");
	this.m_oViewFixture.doThen("view.(input#empty-text).value", "hello");

	this.m_oViewFixture.doThen("view.(textarea).value", "Hello!");
	this.m_oViewFixture.doWhen("view.(textarea).value", "Hola!");
	this.m_oViewFixture.doThen("view.(textarea).value", "Hola!");
};

ValueTest.prototype.test_canSetValueOfOptionElements = function()
{
	this.m_oViewFixture.doThen("view.(#filled-select option:first).value", "ABC");
	this.m_oViewFixture.doWhen("view.(#filled-select option:first).value", "ABCD");
	this.m_oViewFixture.doThen("view.(#filled-select option:first).value", "ABCD");

	this.m_oViewFixture.doThen("view.(#filled-select option:eq(1)).value", "");
	this.m_oViewFixture.doWhen("view.(#filled-select option:eq(1)).value", "DEFG");
	this.m_oViewFixture.doThen("view.(#filled-select option:eq(1)).value", "DEFG");

	this.m_oViewFixture.doThen("view.(#filled-select option:last).value", "456");
	this.m_oViewFixture.doWhen("view.(#filled-select option:last).value", "4567");
	this.m_oViewFixture.doThen("view.(#filled-select option:last).value", "4567");
};


ValueTest.prototype.test_usingValueOnNonInputElementsThrowsException = function()
{
	var _self = this;
	assertException(function() {
		_self.m_oViewFixture.doWhen("view.(form).value", "text");
	}, br.Errors.INVALID_TEST);
	assertException(function() {
		_self.m_oViewFixture.doWhen("view.(.no-value).value", "text");
	}, br.Errors.INVALID_TEST);
};
