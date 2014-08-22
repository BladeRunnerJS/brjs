(function() {
	var Errors = require('br/Errors');
	
	ErrorsTest = TestCase("ErrorsTest");
	
	ErrorsTest.prototype.test_customError = function() {
		var e = new Errors.CustomError("errorName", "errorMessage", "fileName.js", 300);
		assertEquals("errorName", e.name);
		assertEquals("errorMessage", e.message);
		assertEquals("fileName.js", e.fileName);
		assertEquals(300, e.lineNumber);
	};
	
	ErrorsTest.prototype.test_customErrorWithStringLineNumber = function() {
		var e = new Errors.CustomError("errorName", "errorMessage", "fileName.js", "300");
		assertEquals("300", e.lineNumber);
	};
})();