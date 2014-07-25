require('jsunitextensions');
require('mock4js');

var ArrayUtility = require('br/util/ArrayUtility');

ArrayUtilityTest = TestCase('ArrayUtilityTest');

ArrayUtilityTest.prototype.test_inArray_ThrowsExceptionOnWrongArguments = function() {
	assertException(
		function() {
			ArrayUtility.inArray('not an array', 42);
		},
		'InvalidParametersError'
	);
};

ArrayUtilityTest.prototype.test_inArray_FindsValues = function() {
	var testArray = [1, null, 2, undefined, 3, NaN];

	assertTrue('finds 1', ArrayUtility.inArray(testArray, 1));
	assertTrue('finds null', ArrayUtility.inArray(testArray, null));
	assertTrue('finds undefined', ArrayUtility.inArray(testArray, undefined));
	assertTrue('finds NaN', ArrayUtility.inArray(testArray, NaN));

	assertFalse('doesn\'t find 42', ArrayUtility.inArray(testArray, 42));
};

ArrayUtilityTest.prototype.test_removeItem_ThrowsExceptionOnWrongArguments = function() {
	assertException(
		function() {
			ArrayUtility.removeItem('not an array', 42);
		},
		'InvalidParametersError'
	);
};

ArrayUtilityTest.prototype.test_removeItem_ThrowsExceptionIfValueToRemoveIsNaN = function() {
	assertException(
		function() {
			ArrayUtility.removeItem([1, 2, 3], NaN);
		},
		'InvalidParametersError'
	);
};

ArrayUtilityTest.prototype.test_removeItem_RemovesAnItemThatIsInArray = function() {
	var testArray = [1, 2, 3];

	assertEquals(ArrayUtility.removeItem(testArray, 1), [2, 3]);
};

ArrayUtilityTest.prototype.test_removeItem_ReturnsOriginalArrayIfNoItemIsFound = function() {
	var testArray = [1, 2, 3];

	assertEquals(ArrayUtility.removeItem(testArray, 42), testArray);
};
