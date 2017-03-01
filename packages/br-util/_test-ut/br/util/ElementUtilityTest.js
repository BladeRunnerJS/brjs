require('br-presenter/_resources-test-at/html/test-form.html');
(function() {
	var ElementUtility = require('br-util/ElementUtility');

	var testCaseName = 'ElementUtilityTest';
	var testCase = {
		'test #setNodeText sets the text contents on an empty element': function() {
			var element = document.createElement('DIV');

			ElementUtility.setNodeText(element, 'foo');

			assertEquals(element.innerHTML, 'foo');
		},

		'test #setNodeText sets the text contents on a non empty element': function() {
			var element = document.createElement('DIV');
			element.innerHTML = 'bar';

			ElementUtility.setNodeText(element, 'foo');

			assertEquals(element.innerHTML, 'foo');
		},

		'test #getAncestorElementWithClass returns current element when it has matching class': function() {
			var element = document.createElement('DIV');
			element.className = "myClass";

			var returnElement = ElementUtility.getAncestorElementWithClass(element, "myClass");

			assertEquals(element, returnElement);
		},

		'test #getAncestorElementWithClass returns current element when it has multiple classes including the matching class': function() {
			var element = document.createElement('DIV');
			element.className = "myClass myOtherClass";

			var returnElement = ElementUtility.getAncestorElementWithClass(element, "myClass");

			assertEquals(element, returnElement);
		},

		'test #getAncestorElementWithClass returns null when using an svg element with no class': function() {
			var element = document.createElementNS("http://www.w3.org/2000/svg", "svg");

			var returnElement = ElementUtility.getAncestorElementWithClass(element, "myClass");

			assertEquals(null, returnElement);
		},

		'test #getAncestorElementWithClass returns current element when using an svg element containing the matching class': function() {
			var element = document.createElementNS("http://www.w3.org/2000/svg", "svg");
			element.setAttribute("class", "myClass myOtherClass");

			var returnElement = ElementUtility.getAncestorElementWithClass(element, "myOtherClass");

			assertEquals(element, returnElement);
		}

	};

	return new TestCase(testCaseName, testCase);
})();
