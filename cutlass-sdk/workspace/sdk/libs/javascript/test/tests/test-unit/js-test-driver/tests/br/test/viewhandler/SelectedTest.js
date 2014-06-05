SelectedTest = TestCase("SelectedTest");

SelectedTest.prototype.setUp = function()
{
	this.m_oViewFixture = new br.test.ViewFixture("view.*");
	this.m_eElement = this.getElement();
	document.body.appendChild(this.m_eElement);
	this.m_oViewFixture.setViewElement(this.m_eElement);
};

SelectedTest.prototype.tearDown = function()
{
	document.body.removeChild(this.m_eElement);
};

SelectedTest.prototype.getElement = function()
{
	var eElement = document.createElement("div");
	eElement.id = "visibility-test";
	eElement.innerHTML =
		"<form id='the-form'>" +
			"<select id='normal-select'>" +
				"<option value='1234'>AB34</option>" +
				"<option value='5678'>CD78</option>" +
			"</select>" +
			"<select id='grouped-select'>" +
				"<optgroup label='GroupA'>" +
					"<option value='abc'>123</option>" +
					"<option value='def' class='default' selected='selected'>456</option>" +
				"</optgroup>" +
				"<optgroup label='GroupB'>" +
					"<option value='ghi'>789</option>" +
					"<option value='jkl'>101</option>" +
				"</optgroup>" +
			"</select>" +

			"<input id='gender-m' type='radio' name='gender' value='male' />" +
			"<input id='gender-f' type='radio' name='gender' value='female' checked='checked' />" +
		"</form>" +

		"<div class='not-a-form'>" +
			"<span class='inner'></span>" +
		"</div>";

	return eElement;
};

SelectedTest.prototype.test_canGetDefaultSelectedOption = function()
{
	this.m_oViewFixture.doThen("view.(#grouped-select option:first).selected", false);
	this.m_oViewFixture.doThen("view.(#grouped-select option.default).selected", true);
	this.m_oViewFixture.doThen("view.(#grouped-select option:last).selected", false);

	this.m_oViewFixture.doThen("view.(#normal-select option:first).selected", true);
	this.m_oViewFixture.doThen("view.(#normal-select option:last).selected", false);
};

SelectedTest.prototype.test_canChangeSelectedOption = function()
{
	this.m_oViewFixture.doThen("view.(#grouped-select option:first).selected", false);
	this.m_oViewFixture.doThen("view.(#grouped-select option.default).selected", true);
	this.m_oViewFixture.doWhen("view.(#grouped-select option:first).selected", true);
	this.m_oViewFixture.doWhen("view.(#grouped-select option.default).selected", false);
	this.m_oViewFixture.doThen("view.(#grouped-select option:first).selected", true);
	this.m_oViewFixture.doThen("view.(#grouped-select option.default).selected", false);
};

SelectedTest.prototype.test_cannotUseSelectedOnNonOptionElements = function()
{
	var _self = this;
	assertException(function(){
		_self.m_oViewFixture.doThen("view.(input#gender-m).selected", false);
	}, br.Errors.INVALID_TEST);
	assertException(function(){
		_self.m_oViewFixture.doWhen("view.(div.not-a-form).selected", true);
	}, br.Errors.INVALID_TEST);
};
