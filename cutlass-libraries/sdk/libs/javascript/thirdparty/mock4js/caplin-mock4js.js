/**
 * Mock4JS 0.2
 * http://mock4js.sourceforge.net/
 * 
 * phil@leggetter.co.uk 22/04/2009: Added Mock4JS._createMockDefinition and Mock4JS.mockObject
 * phil@leggetter.co.uk 01/10/2009: Added sMethodMatchRegEx to Mock4JS.mockObject and Mock4JS.mock so that methods can
 * 									be selectively mocked.
 */

Mock4JS = {
	/**
	 * Creates a mock class definition by looking at the methods on an object and
	 * adding them to the prototype of a new class/function.
	 * @param {Object} oObject
	 */
	_createMockDefinition: function createMockDefinition(oObject)
	{
		var oMockedDefinition = new Function();
		for( var x in oObject )
		{
			if( typeof oObject[ x ] == "function" )
			{
				oMockedDefinition.prototype[x] = function(){};
			}
		}
		return oMockedDefinition;
	},
	_mocksToVerify: [],
	_convertToConstraint: function(constraintOrValue) {
		if(constraintOrValue.argumentMatches) {
			return constraintOrValue; // it's already an ArgumentMatcher
		} else {
			return new MatchExactly(constraintOrValue);	// default to eq(...)
		}
	},
	/**
	 * Creates a mock for the given object instance by creating a fake class definition and
	 * mocking that.
	 * @param {Object} oObject the object to be mocked
	 * @param {String} sMethodMatchRegEx A regular expression to be applied to each of the methods. If the regular expression matches
	 * 					the method will be mocked. If it does not match the method will not be mocked. e.g.
	 * 					passing "/^_\\$/" would match methods beginning _$ like object._$myProtectedMethod().
	 */
	mockObject: function(oObject, sMethodMatchRegEx)
	{
		var oMockedClassDefinition = Mock4JS._createMockDefinition( oObject );
		var oNewMock = new Mock(oMockedClassDefinition, sMethodMatchRegEx);
		Mock4JS._mocksToVerify.push(oNewMock);
		return oNewMock;
	},
	addMockSupport: function(object, sMethodMatchRegEx) {
		/**
		 * Creates a mock for the given object definition.
		 * @param {Object} oObject the object definition to be mocked
		 * @param {String} sMethodMatchRegEx A regular expression to be applied to each of the methods. If the regular expression matches
		 * 					the method will be mocked. If it does not match the method will not be mocked. e.g.
		 * 					passing "/^_\\$/" would match methods beginning _$ like object._$myProtectedMethod().
		 */
		object.mock = function(mockedType, sMethodMatchRegEx)
		{
			if(!mockedType) {
				throw new Mock4JSException("Cannot create mock: type to mock cannot be found or is null");
			}
			var newMock = new Mock(mockedType, sMethodMatchRegEx);
			Mock4JS._mocksToVerify.push(newMock);
			return newMock;
		};
		
		/**
		 * Context access to Mock4JS.mockObject
		 * @param {Object} oObject the object to be mocked
		 * @param {String} sMethodMatchRegEx A regular expression to be applied to each of the methods. If the regular expression matches
		 * 					the method will be mocked. If it does not match the method will not be mocked. e.g.
		 * 					passing "/^_\\$/" would match methods beginning _$ like object._$myProtectedMethod().
		 */
		object.mockObject = function(oObject, sMethodMatchRegEx)
		{
			return Mock4JS.mockObject( oObject, sMethodMatchRegEx );
		};

		// syntactic sugar for expects()
		object.once = function() {
			return new CallCounter(1);
		}
		object.never = function() {
			return new CallCounter(0);
		}
		object.exactly = function(expectedCallCount) {
			return new CallCounter(expectedCallCount);
		}
		object.atLeastOnce = function() {
			return new InvokeAtLeastOnce();
		}
		
		// syntactic sugar for argument expectations
		object.ANYTHING = MatchAnything.instance;
		object.NOT_NULL = new MatchAnythingBut(new MatchExactly(null));
		object.NOT_UNDEFINED = new MatchAnythingBut(new MatchExactly(undefined));
		object.eq = function(expectedValue) {
			return new MatchExactly(expectedValue);
		}
		object.not = function(valueNotExpected) {
			var argConstraint = Mock4JS._convertToConstraint(valueNotExpected);
			return new MatchAnythingBut(argConstraint);
		}
		object.and = function() {
			var constraints = [];
			for(var i=0; i<arguments.length; i++) {
				constraints[i] = Mock4JS._convertToConstraint(arguments[i]);
			}
			return new MatchAllOf(constraints);
		}
		object.or = function() {
			var constraints = [];
			for(var i=0; i<arguments.length; i++) {
				constraints[i] = Mock4JS._convertToConstraint(arguments[i]);
			}
			return new MatchAnyOf(constraints);
		}
		object.stringContains = function(substring) {
			return new MatchStringContaining(substring);
		}
		object.stringWithRegex = function(regex) {
			return new MatchStringRegex(regex);
		}
		
		// syntactic sugar for will()
		object.returnValue = function(value) {
			return new ReturnValueAction(value);
		}
		object.throwException = function(exception) {
			return new ThrowExceptionAction(exception);
		}
	},
	clearMocksToVerify: function() {
		Mock4JS._mocksToVerify = [];
	},
	verifyAllMocks: function() {
		for(var i=0; i<Mock4JS._mocksToVerify.length; i++) {
			Mock4JS._mocksToVerify[i].verify();
		}
	},
	verifyAndClearAllMocks: function() {
		var mocksToVerify = Mock4JS._mocksToVerify;
		Mock4JS._mocksToVerify = [];
		for(var i=0; i<mocksToVerify.length; i++) {
			mocksToVerify[i].verify();
		}
	}
}

Mock4JSUtil = {
	hasFunction: function(obj, methodName) {
		return obj != null && typeof obj == 'object' && typeof obj[methodName] == 'function';
	},
	join: function(list) {
		var result = "";
		for(var i=0; i<list.length; i++) {
			var item = list[i];
			if(this.hasFunction(item, "describe")) {
				result += item.describe();
			}
			else if(typeof list[i] == 'string') {
				result += "\""+list[i]+"\"";
			} else {
				result += this.stringValue(list[i]);
			}
			
			if(i<list.length-1) result += ", ";
		}
		return result;
	},
	stringValue: function(obj)
	{
		var str;
		
		if(obj === undefined)
		{
			str = "undefined";
		}
		else if(obj === null)
		{
			str = "null";
		}
		else
		{
			var str = obj.toString();
			
			if (str == "[object Object]") 
			{
				// urgh!
				str = "{";
				for (var i in obj) {
					if (typeof(obj[i]) != "function") {
						if (str !== "{")
						{	str += ",";
						}
						str += i+": "+obj[i]
					}
				}
				str +="}";
			}
		}
		
		return str;
	}
}

Mock4JSException = function(message) {
	this.message = message;
}

Mock4JSException.prototype = {
	toString: function() {
		return this.message;
	}
}

/**
 * Assert function that makes use of the constraint methods
 */ 
assertThat = function(expected, argumentMatcher) {
	if(!argumentMatcher.argumentMatches(expected)) {
		fail("Expected '"+expected+"' to be "+argumentMatcher.describe());
	}
}

/**
 * CallCounter
 */
function CallCounter(expectedCount) {
	this._expectedCallCount = expectedCount;
	this._actualCallCount = 0;
}

CallCounter.prototype = {
	addActualCall: function() {
		this._actualCallCount++;
		if(this._actualCallCount > this._expectedCallCount) {
			throw new Mock4JSException("unexpected invocation");
		}
	},
	
	verify: function() {
		if(this._actualCallCount < this._expectedCallCount) {
			if (this._actualCallCount === 0) 
			{
				throw new Mock4JSException("expected method was not invoked");
			}
			else
			{
				var sActualCallCount = (this._actualCallCount == 1) ? "1 time" : this._actualCallCount + " times";
				var sExpectedCallCount = (this._expectedCallCount == 1) ? "1 time" : this._expectedCallCount + " times";
				
				throw new Mock4JSException("expected method was only invoked " + sActualCallCount +
					" but was expected to be invoked " + sExpectedCallCount);
			}
		}
	},
	
	describe: function() {
		if(this._expectedCallCount == 0) {
			return "not expected";
		} else if(this._expectedCallCount == 1) {
			var msg = "expected once";
			if(this._actualCallCount >= 1) {
				msg += " and has been invoked";
			}
			return msg;
		} else {
			var msg = "expected "+this._expectedCallCount+" times";
			if(this._actualCallCount > 0) {
				msg += ", invoked "+this._actualCallCount + " times";
			}
			return msg;
		}
	}
}

function InvokeAtLeastOnce() {
	this._hasBeenInvoked = false;
}

InvokeAtLeastOnce.prototype = {
	addActualCall: function() {
		this._hasBeenInvoked = true;
	},
	
	verify: function() {
		if(this._hasBeenInvoked === false) {
			throw new Mock4JSException(describe());
		}
	},
	
	describe: function() {
		var desc = "expected at least once";
		if(this._hasBeenInvoked) desc+=" and has been invoked";
		return desc;
	}
}

/**
 * ArgumentMatchers
 */

function MatchExactly(expectedValue) {
	this._expectedValue = expectedValue;
}

MatchExactly.prototype = {
	argumentMatches: function(actualArgument) {
		if(this._expectedValue instanceof Array) {
			if(!(actualArgument instanceof Array)) return false;
			if(this._expectedValue.length != actualArgument.length) return false;
			for(var i=0; i<this._expectedValue.length; i++) {
				if(this._expectedValue[i] !== actualArgument[i]) return false;
			}
			return true;
		} else if (this._expectedValue && this._expectedValue.constructor == Object.prototype.constructor) {
			return this.mapEquals(this._expectedValue, actualArgument);
		} else if (this._expectedValue === actualArgument) {
			return true;
		} else {
			if (this._expectedValue && this._expectedValue instanceof Object)
			{
				// walk through the objects and check if they are identical - please note that this
				// has added a dependency on jsUnitExtensions.js from mock4js, therefore this
				// cannot be resubmitted at present
				try {
					assertMapEquals("Maps were not equal", this._expectedValue, actualArgument);
					return true;
				} catch (e) {
					return false;
				}
			}
			else
			{
				return false;
			}
		}
	},
	mapEquals: function(a, b) {
		if (a===b) 
		{
			return true;
		}
		for (var i in a) {
			if (a[i] !== b[i] && a[i] !== MatchAnything.instance && b[i] !== MatchAnything.instance) 
			{
				return false;
			}
		}
		for (var i in b) {
			if (a[i] !== b[i] && a[i] !== MatchAnything.instance && b[i] != MatchAnything.instance) 
			{
				return false;
			}
		}
		return true;
	},
	describe: function() {
		if(typeof this._expectedValue == "string") {
			return "eq(\""+this._expectedValue+"\")";
		} else {
			return "eq("+Mock4JSUtil.stringValue(this._expectedValue)+")";
		}
	}
}

function MatchAnything() {
}

MatchAnything.prototype = {
	argumentMatches: function(actualArgument) {
		return true;
	},
	describe: function() {
		return "ANYTHING";
	}
}

MatchAnything.instance = new MatchAnything();

function MatchAnythingBut(matcherToNotMatch) {
	this._matcherToNotMatch = matcherToNotMatch;
}

MatchAnythingBut.prototype = {
	argumentMatches: function(actualArgument) {
		return !this._matcherToNotMatch.argumentMatches(actualArgument);
	},
	describe: function() {
		return "not("+this._matcherToNotMatch.describe()+")";
	}
}

function MatchAllOf(constraints) {
	this._constraints = constraints;
}


MatchAllOf.prototype = {
	argumentMatches: function(actualArgument) {
		for(var i=0; i<this._constraints.length; i++) {
			var constraint = this._constraints[i];
			if(!constraint.argumentMatches(actualArgument)) return false;
		}
		return true;
	},
	describe: function() {
		return "and("+Mock4JSUtil.join(this._constraints)+")";
	}
}

function MatchAnyOf(constraints) {
	this._constraints = constraints;
}

MatchAnyOf.prototype = {
	argumentMatches: function(actualArgument) {
		for(var i=0; i<this._constraints.length; i++) {
			var constraint = this._constraints[i];
			if(constraint.argument(actualArgument)) return true;
		}
		return false;
	},
	describe: function() {
		return "or("+Mock4JSUtil.join(this._constraints)+")";
	}
}


function MatchStringContaining(stringToLookFor) {
	this._stringToLookFor = stringToLookFor;
}

MatchStringContaining.prototype = {
	argumentMatches: function(actualArgument) {
		if(typeof actualArgument != 'string') throw new Mock4JSException("stringContains() must be given a string, actually got a "+(typeof actualArgument));
		return (actualArgument.indexOf(this._stringToLookFor) != -1);
	},
	describe: function() {
		return "a string containing \""+this._stringToLookFor+"\"";
	}
}

function MatchStringRegex(regex) {
	this._regex = regex;
}

MatchStringRegex.prototype = {
		argumentMatches: function(actualArgument) {
			if(typeof actualArgument != 'string') throw new Mock4JSException("stringWithRegex() must be given a string, actually got a "+(typeof actualArgument));
			return (actualArgument.match(this._regex) != null);
		},
		describe: function() {
			return "a string matching the regex \""+this._regex+"\"";
		}
}


/**
 * StubInvocation
 */
function StubInvocation(expectedMethodName, expectedArgs, actionSequence) {
	this._expectedMethodName = expectedMethodName;
	this._expectedArgs = expectedArgs;
	this._actionSequence = actionSequence;
}

StubInvocation.prototype = {
	matches: function(invokedMethodName, invokedMethodArgs) {
		if (invokedMethodName != this._expectedMethodName) {
			return false;
		}
		
		if (invokedMethodArgs.length != this._expectedArgs.length) {
			return false;
		}
		
		for(var i=0; i<invokedMethodArgs.length; i++) {
			var expectedArg = this._expectedArgs[i];
			var invokedArg = invokedMethodArgs[i];
			if(!expectedArg.argumentMatches(invokedArg)) {
				return false;
			}
		}
		
		return true;
	},
	
	invoked: function() {
		try {
			return this._actionSequence.invokeNextAction();
		} catch(e) {
			if(e instanceof Mock4JSException) {
				throw new Mock4JSException(this.describeInvocationNameAndArgs()+" - "+e.message);
			} else {
				throw e;
			}
		}
	},
	
	will: function() {
		this._actionSequence.addAll.apply(this._actionSequence, arguments);
	},
	
	describeInvocationNameAndArgs: function() {
		return this._expectedMethodName+"("+Mock4JSUtil.join(this._expectedArgs)+")";
	},
	
	describe: function() {
		return "stub: "+this.describeInvocationNameAndArgs();
	},
	
	verify: function() {
	}
}

/**
 * ExpectedInvocation
 */
function ExpectedInvocation(expectedMethodName, expectedArgs, expectedCallCounter) {
	this._stubInvocation = new StubInvocation(expectedMethodName, expectedArgs, new ActionSequence());
	this._expectedCallCounter = expectedCallCounter;
}

ExpectedInvocation.prototype = {
	matches: function(invokedMethodName, invokedMethodArgs) {
		try {
			return this._stubInvocation.matches(invokedMethodName, invokedMethodArgs);
		} catch(e) {
			throw new Mock4JSException("method "+this._stubInvocation.describeInvocationNameAndArgs()+": "+e.message);
		}
	},
	
	invoked: function() {
		try {
			this._expectedCallCounter.addActualCall();
		} catch(e) {
			throw new Mock4JSException(e.message+": "+this._stubInvocation.describeInvocationNameAndArgs());
		}
		return this._stubInvocation.invoked();
	},
	
	will: function() {
		this._stubInvocation.will.apply(this._stubInvocation, arguments);
	},
	
	describe: function() {
		return this._expectedCallCounter.describe()+": "+this._stubInvocation.describeInvocationNameAndArgs();
	},
	
	verify: function() {
		try {
			this._expectedCallCounter.verify();
		} catch(e) {
			throw new Mock4JSException(e.message+": "+this._stubInvocation.describeInvocationNameAndArgs());
		}
	}
}

/**
 * MethodActions
 */
function ReturnValueAction(valueToReturn) {
	this._valueToReturn = valueToReturn;
}

ReturnValueAction.prototype = {
	invoke: function() {
		return this._valueToReturn;
	},
	describe: function() {
		return "returns "+this._valueToReturn;
	}
}

function ThrowExceptionAction(exceptionToThrow) {
	this._exceptionToThrow = exceptionToThrow;
}

ThrowExceptionAction.prototype = {
	invoke: function() {
		throw this._exceptionToThrow;
	},
	describe: function() {
		return "throws "+this._exceptionToThrow;
	}
}

function ActionSequence() {
	this._ACTIONS_NOT_SETUP = "_ACTIONS_NOT_SETUP";
	this._actionSequence = this._ACTIONS_NOT_SETUP;
	this._indexOfNextAction = 0;
}

ActionSequence.prototype = {
	invokeNextAction: function() {
		if(this._actionSequence === this._ACTIONS_NOT_SETUP) {
			return;
		} else {
			if(this._indexOfNextAction >= this._actionSequence.length) {
				throw new Mock4JSException("no more values to return");
			} else {
				var action = this._actionSequence[this._indexOfNextAction];
				this._indexOfNextAction++;
				return action.invoke();
			}
		}
	},
	
	addAll: function() {
		this._actionSequence = [];
		for(var i=0; i<arguments.length; i++) {
			if(typeof arguments[i] != 'object' && arguments[i].invoke === undefined) {
				throw new Error("cannot add a method action that does not have an invoke() method");
			}
			this._actionSequence.push(arguments[i]);
		}
	}
}

function StubActionSequence() {
	this._ACTIONS_NOT_SETUP = "_ACTIONS_NOT_SETUP";
	this._actionSequence = this._ACTIONS_NOT_SETUP;
	this._indexOfNextAction = 0;
} 

StubActionSequence.prototype = {
	invokeNextAction: function() {
		if(this._actionSequence === this._ACTIONS_NOT_SETUP) {
			return;
		} else if(this._actionSequence.length == 1) {
			// if there is only one method action, keep doing that on every invocation
			return this._actionSequence[0].invoke();
		} else {
			if(this._indexOfNextAction >= this._actionSequence.length) {
				throw new Mock4JSException("no more values to return");
			} else {
				var action = this._actionSequence[this._indexOfNextAction];
				this._indexOfNextAction++;
				return action.invoke();
			}
		}
	},
	
	addAll: function() {
		this._actionSequence = [];
		for(var i=0; i<arguments.length; i++) {
			if(typeof arguments[i] != 'object' && arguments[i].invoke === undefined) {
				throw new Error("cannot add a method action that does not have an invoke() method");
			}
			this._actionSequence.push(arguments[i]);
		}
	}
}

 
/**
 * Mock
 */
function Mock(mockedType, sMethodMatchRegEx) {
	sMethodMatchRegEx = sMethodMatchRegEx || null;
	if(mockedType === undefined || mockedType.prototype === undefined) {
		throw new Mock4JSException("Unable to create Mock: must create Mock using a class not prototype, eg. 'new Mock(TypeToMock)' or using the convenience method 'mock(TypeToMock)'");
	}
	this._mockedType = mockedType.prototype;
	this._expectedCallCount;
	this._isRecordingExpectations = false;
	this._expectedInvocations = [];

	// setup proxy
	var IntermediateClass = new Function();
	IntermediateClass.prototype = mockedType.prototype;
	var ChildClass = new Function();
	ChildClass.prototype = new IntermediateClass();
	this._proxy = new ChildClass();
	this._proxy.mock = this;
	
	for(property in mockedType.prototype) {
		if(this._methodShouldBeMocked(mockedType.prototype, property, sMethodMatchRegEx)) {
			var methodName = property;
			this._proxy[methodName] = this._createMockedMethod(methodName);
			this[methodName] = this._createExpectationRecordingMethod(methodName);
		}
	}
}

Mock.prototype = {
	
	proxy: function() {
		return this._proxy;
	},
	
	expects: function(expectedCallCount) {
		this._expectedCallCount = expectedCallCount;
		this._isRecordingExpectations = true;
		this._isRecordingStubs = false;
		return this;
	},
	
	stubs: function() {
		this._isRecordingExpectations = false;
		this._isRecordingStubs = true;
		return this;
	},
	
	verify: function() {
		for(var i=0; i<this._expectedInvocations.length; i++) {
			var expectedInvocation = this._expectedInvocations[i];
			try {
				expectedInvocation.verify();
			} catch(e) {
				var failMsg = e.message+this._describeMockSetup();
				throw new Mock4JSException(failMsg);
			}
		}
	},
	
	_methodShouldBeMocked: function(mockedType, property, sMethodMatchRegEx) {
		try {
			var isMethod = typeof(mockedType[property]) == 'function';
			var isPublic = property.charAt(0) != "_"; 
			var regExMatch = false;
			if( sMethodMatchRegEx != null )
			{
				var oRegEx = new RegExp(sMethodMatchRegEx);
				regExMatch = oRegEx.test(property);
			}
			return isMethod && (isPublic || regExMatch);
		} catch(e) {
			return false;
		}
	},

	_createExpectationRecordingMethod: function(methodName) {
		return function() {
			// ensure all arguments are instances of ArgumentMatcher
			var expectedArgs = [];
			for(var i=0; i<arguments.length; i++) {
				if(arguments[i] !== null && arguments[i] !== undefined && arguments[i].argumentMatches) {
					expectedArgs[i] = arguments[i];
				} else {
					expectedArgs[i] = new MatchExactly(arguments[i]);
				}
			}
			
			// create stub or expected invocation
			var expectedInvocation;
			if(this._isRecordingExpectations) {
				expectedInvocation = new ExpectedInvocation(methodName, expectedArgs, this._expectedCallCount);
			} else {
				expectedInvocation = new StubInvocation(methodName, expectedArgs, new StubActionSequence());
			}
			
			this._expectedInvocations.push(expectedInvocation);
			
			this._isRecordingExpectations = false;
			this._isRecordingStubs = false;
			return expectedInvocation;
		}
	},
	
	_createMockedMethod: function(methodName) {
		return function() {
			// go through expectation list backwards to ensure later expectations override earlier ones
			for(var i=this.mock._expectedInvocations.length-1; i>=0; i--) {
				var expectedInvocation = this.mock._expectedInvocations[i];
				if(expectedInvocation.matches(methodName, arguments)) {
					try {
						return expectedInvocation.invoked();
					} catch(e) {
						if(e instanceof Mock4JSException) {
							this.mock._logTestFailure(methodName, arguments);
							throw new Mock4JSException(e.message+this.mock._describeMockSetup());
						} else {
							// the user setup the mock to throw a specific error, so don't modify the message
							throw e;
						}
					}
				}
			}
			this.mock._logTestFailure(methodName, arguments);
			var failMsg = this.mock._getFailureMessage(methodName, arguments);
			throw new Mock4JSException(failMsg);
		};
	},
	
	_getFailureMessage: function(methodName, argumentArray) {
		return "unexpected invocation: "+methodName+"("+Mock4JSUtil.join(argumentArray)+")"+this._describeMockSetup();
	},

	_logTestFailure: function(methodName, argumentArray) {
		var failMsg = this._getFailureMessage(methodName, argumentArray);
		if (window.top.testManager){
			window.top.testManager.m_pExceptionArray.push(new Mock4JSException(failMsg));
		}
	},
	
	_describeMockSetup: function() {
		var msg = "\n\nAllowed:";
		for(var i=0; i<this._expectedInvocations.length; i++) {
			var expectedInvocation = this._expectedInvocations[i];
			msg += "\n" + expectedInvocation.describe();
		}
		return msg;
	}
}
     	
