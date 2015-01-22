require('jsunitextensions');
require('mock4js');

var ElementUtility = require('br/util/ElementUtility');

ElementUtilityTest = TestCase('ElementUtilityTest');

ElementUtilityTest.prototype.test_setNodeText_SetsTheTextContentsOnAEmptyElement = function() {
	var element = document.createElement('DIV');

	ElementUtility.setNodeText(element, 'foo');

	assertEquals(element.innerHTML, 'foo');
};

ElementUtilityTest.prototype.test_setNodeText_SetsTheTextContentsOnANonEmptyElement = function() {
	var element = document.createElement('DIV');
	element.innerHTML = 'bar';

	ElementUtility.setNodeText(element, 'foo');

	assertEquals(element.innerHTML, 'foo');
};
