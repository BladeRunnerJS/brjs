(function() {
	var ElementUtility = require('br/util/ElementUtility');

	var testCaseName = 'ElementUtilityTest';
	var testCase = {
		'test #setNodeText sets the text contents on an empty element': function() {
			var element = document.createElement('DIV');

			ElementUtility.setNodeText(element, 'foo');

			assertEquals(element.innerHTML, 'foo');
		},

		'test_setNodeText_SetsTheTextContentsOnANonEmptyElement': function() {
			var element = document.createElement('DIV');
			element.innerHTML = 'bar';

			ElementUtility.setNodeText(element, 'foo');

			assertEquals(element.innerHTML, 'foo');
		}

	};

	return new TestCase(testCaseName, testCase);
})();
