br.Core.thirdparty('jsunitextensions');
br.Core.thirdparty('mock4js');

ArrayUtilityTest = TestCase('ArrayUtilityTest');

ArrayUtilityTest.prototype.test_inArray_ThrowsExceptionOnWrongArguments = function() {
	assertException(
		function() {
			br.util.ArrayUtility.inArray('not an array', 42);
		},
		'InvalidParametersError'
	);
};

ArrayUtilityTest.prototype.test_inArray_FindsValues = function() {
	var testArray = [1, null, 2, undefined, 3, NaN];

	assertTrue('finds 1', br.util.ArrayUtility.inArray(testArray, 1));
	assertTrue('finds null', br.util.ArrayUtility.inArray(testArray, null));
	assertTrue('finds undefined', br.util.ArrayUtility.inArray(testArray, undefined));
	assertTrue('finds NaN', br.util.ArrayUtility.inArray(testArray, NaN));

	assertFalse('doesn\'t find 42', br.util.ArrayUtility.inArray(testArray, 42));
};

ArrayUtilityTest.prototype.test_removeItem_ThrowsExceptionOnWrongArguments = function() {
	assertException(
		function() {
			br.util.ArrayUtility.removeItem('not an array', 42);
		},
		'InvalidParametersError'
	);
};

ArrayUtilityTest.prototype.test_removeItem_ThrowsExceptionIfValueToRemoveIsNaN = function() {
	assertException(
		function() {
			br.util.ArrayUtility.removeItem([1, 2, 3], NaN);
		},
		'InvalidParametersError'
	);
};

ArrayUtilityTest.prototype.test_removeItem_RemovesAnItemThatIsInArray = function() {
	var testArray = [1, 2, 3];

	assertEquals(br.util.ArrayUtility.removeItem(testArray, 1), [2, 3]);
};

ArrayUtilityTest.prototype.test_removeItem_ReturnsOriginalArrayIfNoItemIsFound = function() {
	var testArray = [1, 2, 3];

	assertEquals(br.util.ArrayUtility.removeItem(testArray, 42), testArray);
};
