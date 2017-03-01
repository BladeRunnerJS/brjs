(function() {
	var assertFails = require('jsunitextensions').assertFails;

    var Errors = require("br/Errors");
    var WritableProperty = require('br-presenter/property/WritableProperty');
    var OptionsNodeList = require('br-presenter/node/OptionsNodeList');
    var Core = require("br/Core");
    var Mock4JS = require("mock4js");

    OptionsNodeListTest = TestCase('OptionsNodeListTest');

    OptionsNodeListTest.prototype.setUp = function() {
    	Mock4JS.addMockSupport(window);
    	Mock4JS.clearMocksToVerify();
    };

    OptionsNodeListTest.prototype.tearDown = function() {
    	Mock4JS.verifyAllMocks();
    };

    OptionsNodeListTest.prototype.test_canCreateEmptyOptionsNodeListWithNoParams = function() {
    	var optionList = new OptionsNodeList();
    	assertEquals([], optionList.getOptions());
    };

    OptionsNodeListTest.prototype.test_canCreateEmptyOptionsNodeListWithEmptyArray = function() {
    	var optionList = new OptionsNodeList([]);
    	assertEquals([], optionList.getOptions());
    };

    OptionsNodeListTest.prototype.test_canCreateOptionsNodeListWithAnArrayOfValues = function() {
    	var optionList = new OptionsNodeList(['a', 'b', 'c']);
    	var optionNodeArray = optionList.getOptions();

    	assertEquals(3, optionNodeArray.length);
    	assertEquals(optionNodeArray[0].value.getValue(), 'a');
    	assertEquals(optionNodeArray[0].label.getValue(), 'a');
    };

    OptionsNodeListTest.prototype.test_canCreateOptionsNodeListWithAMapOfValues = function() {
    	var optionList = new OptionsNodeList({'a': 'l1', 'b': 'l2'});
    	var optionNodeArray = optionList.getOptions();

    	assertEquals(2, optionNodeArray.length);
    	assertEquals(optionNodeArray[0].value.getValue(), 'a');
    	assertEquals(optionNodeArray[0].label.getValue(), 'l1');
    };

    OptionsNodeListTest.prototype.test_failIfOptionsNodeListIsInstantiatedWithAProperty = function() {
    	var property = new WritableProperty(['a']);

    	assertException('Properties not allowed inside OptionsNodeList', function(){
    		var oOptions = new OptionsNodeList(property);
    	}, Errors.INVALID_PARAMETERS);
    };

    OptionsNodeListTest.prototype.test_getFirstOption = function() {
    	var optionList = new OptionsNodeList(['a', 'b', 'c']);
    	assertEquals('a', optionList.getFirstOption().value.getValue());

    	var emptyOptionList = new OptionsNodeList([]);
    	assertNull(emptyOptionList.getFirstOption());
    };

    OptionsNodeListTest.prototype.test_getOptionByValue = function() {
    	var optionList = new OptionsNodeList({'aValue': 'aLabel', 'bValue': 'bLabel'});

    	var optionFound = optionList.getOptionByValue('aValue');
    	assertEquals('aLabel', optionFound.label.getValue());

    	optionFound = optionList.getOptionByValue('dValue');
    	assertNull(optionFound);
    };

    OptionsNodeListTest.prototype.test_getOptionByLabel = function() {
    	var optionList = new OptionsNodeList({'aValue': 'aLabel', 'bValue': 'bLabel', 'cValue': 'aLabel'});

    	var optionFound = optionList.getOptionByLabel('aLabel');
    	assertEquals('aValue', optionFound.value.getValue());

    	optionFound = optionList.getOptionByLabel('dLabel');
    	assertNull(optionFound);

    	optionFound = optionList.getOptionByLabel('ALABEL', false);
    	assertNull(optionFound);

    	optionFound = optionList.getOptionByLabel('ALABEL', true);
    	assertEquals('aValue', optionFound.value.getValue());

    	assertFails( "'ignoreCase' argument must be a Boolean value", function() {
    		optionList.getOptionByLabel('ALABEL', 'foo');
    	});
    };

    OptionsNodeListTest.prototype.test_canSetNewOptionsOnExistingOptionsNodeList = function() {
    	var optionList = new OptionsNodeList(['a', 'b', 'c']);
    	var optionNodeArray = optionList.getOptions();

    	assertEquals(3, optionNodeArray.length);
    	assertEquals(optionNodeArray[0].value.getValue(), 'a');
    	assertEquals(optionNodeArray[0].label.getValue(), 'a');

    	optionList.setOptions(['d', 'e']);
    	optionNodeArray = optionList.getOptions();

    	assertEquals(2, optionNodeArray.length);
    	assertEquals(optionNodeArray[0].value.getValue(), 'd');
    	assertEquals(optionNodeArray[0].label.getValue(), 'd');
    };
})();
