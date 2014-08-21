br.Core.thirdparty('mock4js');

OptionsNodeListTest = TestCase('OptionsNodeListTest');

OptionsNodeListTest.prototype.setUp = function() {
	Mock4JS.addMockSupport(window);
	Mock4JS.clearMocksToVerify();
};

OptionsNodeListTest.prototype.tearDown = function() {
	Mock4JS.verifyAllMocks();
};

OptionsNodeListTest.prototype.test_canCreateEmptyOptionsNodeListWithNoParams = function() {
	var optionList = new br.presenter.node.OptionsNodeList();
	assertEquals([], optionList.getOptions());
};

OptionsNodeListTest.prototype.test_canCreateEmptyOptionsNodeListWithEmptyArray = function() {
	var optionList = new br.presenter.node.OptionsNodeList([]);
	assertEquals([], optionList.getOptions());
};

OptionsNodeListTest.prototype.test_canCreateOptionsNodeListWithAnArrayOfValues = function() {
	var optionList = new br.presenter.node.OptionsNodeList(['a', 'b', 'c']);
	var optionNodeArray = optionList.getOptions();

	assertEquals(3, optionNodeArray.length);
	assertEquals(optionNodeArray[0].value.getValue(), 'a');
	assertEquals(optionNodeArray[0].label.getValue(), 'a');
};

OptionsNodeListTest.prototype.test_canCreateOptionsNodeListWithAMapOfValues = function() {
	var optionList = new br.presenter.node.OptionsNodeList({'a': 'l1', 'b': 'l2'});
	var optionNodeArray = optionList.getOptions();

	assertEquals(2, optionNodeArray.length);
	assertEquals(optionNodeArray[0].value.getValue(), 'a');
	assertEquals(optionNodeArray[0].label.getValue(), 'l1');
};

OptionsNodeListTest.prototype.test_failIfOptionsNodeListIsInstantiatedWithAProperty = function() {
	var property = new br.presenter.property.WritableProperty(['a']);

	assertException('Properties not allowed inside OptionsNodeList', function(){
		var oOptions = new br.presenter.node.OptionsNodeList(property);
	}, br.Errors.INVALID_PARAMETERS);
};

OptionsNodeListTest.prototype.test_getFirstOption = function() {
	var optionList = new br.presenter.node.OptionsNodeList(['a', 'b', 'c']);
	assertEquals('a', optionList.getFirstOption().value.getValue());

	var emptyOptionList = new br.presenter.node.OptionsNodeList([]);
	assertNull(emptyOptionList.getFirstOption());
};

OptionsNodeListTest.prototype.test_getOptionByValue = function() {
	var optionList = new br.presenter.node.OptionsNodeList({'aValue': 'aLabel', 'bValue': 'bLabel'});

	var optionFound = optionList.getOptionByValue('aValue');
	assertEquals('aLabel', optionFound.label.getValue());

	optionFound = optionList.getOptionByValue('dValue');
	assertNull(optionFound);
};

OptionsNodeListTest.prototype.test_getOptionByLabel = function() {
	var optionList = new br.presenter.node.OptionsNodeList({'aValue': 'aLabel', 'bValue': 'bLabel', 'cValue': 'aLabel'});

	var optionFound = optionList.getOptionByLabel('aLabel');
	assertEquals('aValue', optionFound.value.getValue());

	optionFound = optionList.getOptionByLabel('dLabel');
	assertNull(optionFound);

	var optionFound = optionList.getOptionByLabel('ALABEL', false);
	assertNull(optionFound);

	var optionFound = optionList.getOptionByLabel('ALABEL', true);
	assertEquals('aValue', optionFound.value.getValue());
};

OptionsNodeListTest.prototype.test_canSetNewOptionsOnExistingOptionsNodeList = function() {
	var optionList = new br.presenter.node.OptionsNodeList(['a', 'b', 'c']);
	var optionNodeArray = optionList.getOptions();

	assertEquals(3, optionNodeArray.length);
	assertEquals(optionNodeArray[0].value.getValue(), 'a');
	assertEquals(optionNodeArray[0].label.getValue(), 'a');

	optionList.setOptions(['d', 'e']);
	var optionNodeArray = optionList.getOptions();

	assertEquals(2, optionNodeArray.length);
	assertEquals(optionNodeArray[0].value.getValue(), 'd');
	assertEquals(optionNodeArray[0].label.getValue(), 'd');
};
