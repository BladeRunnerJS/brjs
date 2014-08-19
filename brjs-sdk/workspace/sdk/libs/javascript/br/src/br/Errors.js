"use strict";

/**
* @module br/Errors
*/

var br = require('br/Core');

/**
* @class
* @alias module:br/Errors
* 
* @classdesc
* Constructs a new <code>Error</code> of the provided type.
* 
* <code>br/Errors</code> extends the built in <code>Error</code> and allows the
* error type to be specified in the constructor. The <code>name</code>
* property is set to the specified type.
* 
* @param {String} type The error type to be thrown.
* @param {String} message A human-readable description of the error.
* @param {String} [fileName] (Optional) The name of the file containing the code that caused the error.
* @param {int} [lineNumber] (Optional) The line number of the code that caused the error.
*/
function CustomError(type, message, fileName, lineNumber) {
	this.name = type || "";
	this.message = message || "";
	this.fileName = fileName;
	this.lineNumber = lineNumber;

	// If the browser we're in provides an ability to get the stack, then get it here.
	var e = new Error();
	if (e.stack) {
		this.realStack = e.stack;
		this.stack = "Error: "+type+": "+message+"\n\tat "+getStack(e).join("\n\tat ");
	}
}

br.extend(CustomError, Error);

/**
* Returns the string representation of this error
*/
CustomError.prototype.toString = function toString() {
	return this.stack || this.message;
};

exports.CustomError = CustomError;

/**
 * This error type is thrown when a method has been invoked at an illegal or
 * inappropriate time.
 */
exports.ILLEGAL_STATE = "IllegalStateError";

/**
 * This error type is thrown from acceptance test fixtures and indicates a
 * problem with the test rather than the code under test. For example, if a
 * particular fixture can only be used in a 'given' clause but is invoked in a
 * 'then' clause, this error will be thrown. This will result in a test 'error'
 * rather than a test 'failure'.
 */
exports.INVALID_TEST = "InvalidTestError";
exports.ILLEGAL_TEST_CLAUSE = "IllegalTestClauseError";

/**
 * This error is thrown when an interface method is called that should have
 * been implemented in the interface implementor class.
 */
exports.UNIMPLEMENTED_INTERFACE = "UnimplementedInterfaceError";

/**
 * This error is thrown when an abstract method is called that should have
 * been implemented in the extending class.
 */
exports.UNIMPLEMENTED_ABSTRACT_METHOD = "UnimplementedAbstractMethodError";

/**
 * This error is thrown when an operation is being attempted on an a class instance
 * and it does not have the required implementation.
 */
exports.NOT_SUPPORTED = "NotSupportedError";

/**
 * This error type is thrown when a method is called with one or more invalid
 * parameters. This could either be because a required parameter is not provided
 * or a provided parameter is of the wrong type or is invalid for another reason
 * (eg a string representation of a date that doesn't parse to an actual date).
 */
exports.INVALID_PARAMETERS = "InvalidParametersError";

/**
 * This error type indicates that a request for data has failed.
 */
exports.REQUEST_FAILED = "RequestFailedError";

/**
 * This error type indicates that some required data was invalid.
 */
exports.INVALID_DATA = "InvalidDataError";

function getCustomErrorConstructor(type) {
	var customErrorConstructor = function(message, filename, lineNumber) {
		CustomError.call(this, type, message, filename, lineNumber);
	};
	br.extend(customErrorConstructor, CustomError);
	return customErrorConstructor;
}

for (var key in exports) {
	if (typeof exports[key] === 'string') {
		var className = exports[key];
		exports[className] = getCustomErrorConstructor(className);
	}
}

exports.EVAL = "EvalError";
exports.EvalError = EvalError;
exports.RANGE = "RangeError";
exports.RangeError = RangeError;
exports.REFERENCE = "ReferenceError";
exports.ReferenceError = ReferenceError;
exports.SYNTAX = "SyntaxError";
exports.SyntaxError = SyntaxError;
exports.TYPE = "TypeError";
exports.TypeError = TypeError;


// static private methods /////////////////////////////////////////////////////

/** @private */
function normaliseStack(stackString) {
	var stack;

	if(stackString) {
		stack = stackString.split("\n");
		for (var i = stack.length - 1; i >= 0; --i) {
			if (stack[i] === 'Error' || stack[i] === '') {
				stack.splice(i, 1);
			} else {
				var header = stack[i].match(/^\s*at\s+/);
				if (header !== null) {
					stack[i] = stack[i].substring(header[0].length);
				}
			}
		}
	}
	return stack;
}

/** @private */
var irrelevantStack = normaliseStack((new (getCustomErrorConstructor('irrelevant'))()).realStack);

/** @private */
function getStack(e) {
	var stack = normaliseStack(e.stack);
	if (irrelevantStack !== undefined) {
		var line = 0;
		while (stack[0] === irrelevantStack[line++]) {
			stack.shift();
		}
	}
	return stack;
}
