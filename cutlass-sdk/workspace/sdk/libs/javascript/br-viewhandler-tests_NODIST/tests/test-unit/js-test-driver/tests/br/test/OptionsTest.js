OptionsTest = TestCase("OptionsTest");

require('br/test/ViewFixture');

OptionsTest.prototype.setUp = function()
{
	this.m_oViewFixture = new br.test.ViewFixture("view.*");
	this.m_oViewFixture.setViewElement(this.getElement());
};

OptionsTest.prototype.tearDown = function()
{
};

OptionsTest.prototype.getElement = function()
{
	var eElement = document.createElement('div');
	eElement.innerHTML =
		"<form id='the-form'>" +
			"<select id='empty-select'>" +
			"</select>" +
			"<select id='shallow-select'>" +
				"<option value=''></option>" +
				"<option value=''></option>" +
			"</select>" +
			"<select id='filled-select'>" +
				"<option value='1234'>AB34</option>" +
				"<option value='5678'>CD78</option>" +
			"</select>" +
			"<select id='grouped-select'>" +
				"<optgroup label='GroupA'>" +
					"<option value='abc'>123</option>" +
					"<option value='def'>456</option>" +
				"</optgroup>" +
				"<optgroup label='GroupB'>" +
					"<option value='ghi'>789</option>" +
					"<option value='jkl'>101</option>" +
				"</optgroup>" +
			"</select>" +
			"<select id='disabled-select' disabled='disabled'>" +
				"<option value='123'>ABC</option>" +
				"<option value='456'>CDE</option>" +
			"</select>" +

			"<input id='gender-m' type='radio' name='gender' value='male' />" +
			"<input id='gender-f' type='radio' name='gender' value='female' checked='checked' />" +
		"</form>" +

		"<div class='not-a-form'>" +
			"<span class='inner'></span>" +
		"</div>";

	return eElement;
};

OptionsTest.prototype.test_optionsOfAnEmptySelectIsAnEmptyArray = function()
{
	this.m_oViewFixture.doThen("view.(#empty-select).options", []);
};

OptionsTest.prototype.test_optionsValueOfASelectWithEmptyOptionElementsIsArrayOfEmptyStrings = function()
{
	this.m_oViewFixture.doThen("view.(#shallow-select).options", ["", ""]);
};


OptionsTest.prototype.test_canGetAllOptionsInASelectElement = function()
{
	this.m_oViewFixture.doThen("view.(#filled-select).options", ["AB34", "CD78"]);
	this.m_oViewFixture.doThen("view.(#grouped-select).options", ["123", "456", "789", "101"]);
};

OptionsTest.prototype.test_tryingToUseOptionsOnNonSelectElementThrowsException = function()
{
	var _self = this;
	assertException(function() {
		_self.m_oViewFixture.doThen("view.(input:first).options", ["some", "thing"]);
	}, br.Errors.INVALID_TEST);
	assertException(function() {
		_self.m_oViewFixture.doWhen("view.(div.not-a-form).options", []);
	}, br.Errors.INVALID_TEST);
};

OptionsTest.prototype.test_canEmptyASelectElementWithAnEmptyArray = function()
{
	this.m_oViewFixture.doThen("view.(#filled-select).options", ["AB34", "CD78"]);
	this.m_oViewFixture.doWhen("view.(#filled-select).options", []);
	this.m_oViewFixture.doThen("view.(#filled-select).options", []);

	this.m_oViewFixture.doThen("view.(#grouped-select).options", ["123", "456", "789", "101"]);
	this.m_oViewFixture.doWhen("view.(#grouped-select).options", []);
	this.m_oViewFixture.doThen("view.(#grouped-select).options", []);
};

OptionsTest.prototype.test_canReplaceOptionsOfASelectElement = function()
{
	this.m_oViewFixture.doThen("view.(#filled-select).options", ["AB34", "CD78"]);
	this.m_oViewFixture.doWhen("view.(#filled-select).options", ["hello"]);
	this.m_oViewFixture.doThen("view.(#filled-select).options", ["hello"]);

	this.m_oViewFixture.doThen("view.(#grouped-select).options", ["123", "456", "789", "101"]);
	this.m_oViewFixture.doWhen("view.(#grouped-select).options", [1, 2, 3]);
	this.m_oViewFixture.doThen("view.(#grouped-select).options", [1, 2, 3]);

	this.m_oViewFixture.doThen("view.(#empty-select).options", []);
	this.m_oViewFixture.doWhen("view.(#empty-select).options", ["now", "full"]);
	this.m_oViewFixture.doThen("view.(#empty-select).options", ["now", "full"]);
};

OptionsTest.prototype.test_tryingToSetOptionsWithNonArrayValueThrowsException = function()
{
	this.m_oViewFixture.doThen("view.(#filled-select).options", ["AB34", "CD78"]);

	var _self = this;
	assertException(function() {
		_self.m_oViewFixture.doWhen("view.(#filled-select).options", "naughty value");
	}, br.Errors.INVALID_TEST);
	assertException(function() {
		_self.m_oViewFixture.doWhen("view.(#filled-select).options", 43);
	}, br.Errors.INVALID_TEST);
	assertException(function() {
		_self.m_oViewFixture.doWhen("view.(#filled-select).options", null);
	}, br.Errors.INVALID_TEST);

	this.m_oViewFixture.doThen("view.(#filled-select).options", ["AB34", "CD78"]);
};
