br.Core.thirdparty('mock4js');

OptionsNodeListTest = TestCase("OptionsNodeListTest");

OptionsNodeListTest.prototype.setUp = function()
{
	Mock4JS.addMockSupport(window);
	Mock4JS.clearMocksToVerify();
};

OptionsNodeListTest.prototype.tearDown = function()
{
	Mock4JS.verifyAllMocks();
};

OptionsNodeListTest.prototype.test_canCreateEmptyOptionsNodeListWithNoParams = function()
{
	var oOptionList = new br.presenter.node.OptionsNodeList();
	assertEquals([], oOptionList.getOptions());
};

OptionsNodeListTest.prototype.test_canCreateEmptyOptionsNodeListWithEmptyArray = function()
{
	var oOptionList = new br.presenter.node.OptionsNodeList([]);
	assertEquals([], oOptionList.getOptions());
};

OptionsNodeListTest.prototype.test_canCreateOptionsNodeListWithAnArrayOfValues = function()
{
	var oOptionList = new br.presenter.node.OptionsNodeList(["a","b","c"]);
	var oOptionNodeArray = oOptionList.getOptions();
	assertEquals(3, oOptionNodeArray.length);
	assertEquals(oOptionNodeArray[0].value.getValue(), "a");
	assertEquals(oOptionNodeArray[0].label.getValue(), "a");
};

OptionsNodeListTest.prototype.test_canCreateOptionsNodeListWithAMapOfValues = function()
{
	var oOptionList = new br.presenter.node.OptionsNodeList({"a": "l1", "b": "l2"});
	var oOptionNodeArray = oOptionList.getOptions();
	assertEquals(2, oOptionNodeArray.length);
	assertEquals(oOptionNodeArray[0].value.getValue(), "a");
	assertEquals(oOptionNodeArray[0].label.getValue(), "l1");
};

OptionsNodeListTest.prototype.test_failIfOptionsNodeListIsInstantiatedWithAProperty = function()
{
	var oProperty = new br.presenter.property.WritableProperty(["a"]);
	assertException("Properties not allowed inside OptionsNodeList", function(){
		var oOptions = new br.presenter.node.OptionsNodeList(oProperty);
	}, br.Errors.INVALID_PARAMETERS);
};

OptionsNodeListTest.prototype.test_getFirstOption = function()
{
	var oOptionList = new br.presenter.node.OptionsNodeList(["a","b","c"]);
	assertEquals("a", oOptionList.getFirstOption().value.getValue());
	var oEmptyOptionList = new br.presenter.node.OptionsNodeList([]);
	assertNull(oEmptyOptionList.getFirstOption());
};

OptionsNodeListTest.prototype.test_getOptionByValue = function()
{
	var oOptionList = new br.presenter.node.OptionsNodeList({"aValue": "aLabel", "bValue": "bLabel"});

	var oOptionFound = oOptionList.getOptionByValue('aValue');
	assertEquals("aLabel", oOptionFound.label.getValue());
	
	oOptionFound = oOptionList.getOptionByValue('dValue');
	assertNull(oOptionFound);
};

OptionsNodeListTest.prototype.test_getOptionByLabel = function()
{
	var oOptionList = new br.presenter.node.OptionsNodeList({"aValue": "aLabel", "bValue": "bLabel", "cValue": "aLabel"});
	
	var oOptionFound = oOptionList.getOptionByLabel('aLabel');
	assertEquals("aValue", oOptionFound.value.getValue());
	
	oOptionFound = oOptionList.getOptionByLabel('dLabel');
	assertNull(oOptionFound);
};

OptionsNodeListTest.prototype.test_canSetNewOptionsOnExistingOptionsNodeList = function()
{
	var oOptionList = new br.presenter.node.OptionsNodeList(["a","b","c"]);
	var oOptionNodeArray = oOptionList.getOptions();
	assertEquals(3, oOptionNodeArray.length);
	assertEquals(oOptionNodeArray[0].value.getValue(), "a");
	assertEquals(oOptionNodeArray[0].label.getValue(), "a");

	oOptionList.setOptions(["d", "e"]);
	var oOptionNodeArray = oOptionList.getOptions();
	assertEquals(2, oOptionNodeArray.length);
	assertEquals(oOptionNodeArray[0].value.getValue(), "d");
	assertEquals(oOptionNodeArray[0].label.getValue(), "d");
};
