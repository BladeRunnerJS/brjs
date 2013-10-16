br.thirdparty("jasmine");
br.thirdparty("caplin-br");

/**
 * @private
 * @constructor
 *
 * @implements br.test.FixtureRegistry
 */
br.test.GwtTestRunner = function(sFixtureFactoryClass) {
	var Utility = require('br/core/Utility');
	
	this.m_pFixtures = [];
	
	var fFixtureFactoryClass;
	try {
		fFixtureFactoryClass = Utility.locate(sFixtureFactoryClass);
	} catch(e) {
		throw new br.Errors.CustomError("InvalidFactoryError", "An error occured in br.test.GwtTestRunner when creating the fixture factory " +
				"(" + sFixtureFactoryClass + "): " + e.message);
	}
	
	try {
		this.m_oFixtureFactory = new fFixtureFactoryClass();
	} catch(e) {
		throw new br.Errors.CustomError("InvalidFactoryError", "An error occured in br.test.GwtTestRunner when creating the fixture factory " +
				"(" + sFixtureFactoryClass + "): " + e.message);
	}

	if(!br.fulfills(this.m_oFixtureFactory, br.test.FixtureFactory)) {
		throw new br.Errors.CustomError("InvalidFactoryError", "The provided fixture factory (" + sFixtureFactoryClass +
				") does not implement br.test.FixtureFactory");
	}

	this.m_oFixtureFactory.addFixtures(this);
	this.addFixture("test", new br.test.TestFixture(this));
	this.addFixture("time", new br.test.TimeFixture(br.test.TimeUtility));
	
	this.m_fDoGiven = br.test.GwtTestRunner.createTestMethod(this, "doGiven"),
	this.m_fDoWhen = br.test.GwtTestRunner.createTestMethod(this, "doWhen"),
	this.m_fDoThen = br.test.GwtTestRunner.createTestMethod(this, "doThen"),
	this.m_fDoAnd = br.test.GwtTestRunner.createTestMethod(this, "doAnd");
	this.m_fStartingContinuesFrom = br.test.GwtTestRunner.createTestMethod(this, "startingContinuesFrom");
	this.m_fFinishedContinuesFrom = br.test.GwtTestRunner.createTestMethod(this, "finishedContinuesFrom");
};

br.provide(br.test.GwtTestRunner, br.test.FixtureRegistry);

br.test.GwtTestRunner.m_mTests = {};
br.test.GwtTestRunner.m_mSuites = {};
br.test.GwtTestRunner.INIT_PHASE = 1;
br.test.GwtTestRunner.GIVEN_PHASE = 2;
br.test.GwtTestRunner.WHEN_PHASE = 3;
br.test.GwtTestRunner.THEN_PHASE = 4;


// *** Static Methods ***

/**
 * Static method that needs to be called before any Jasmine tests will execute.
 */
br.test.GwtTestRunner.initialize = function()
{
	if(!window.fixtures)
	{
		window.fixtures = br.test.GwtTestRunner.createTestMethod(br.test.GwtTestRunner, "initializeTest");
	}
};

/**
 * @private
 */
br.test.GwtTestRunner.createTestMethod = function(oTestRunner, sMethod)
{
	return function(sStatement)
	{
		oTestRunner[sMethod](sStatement);
	};
};

/**
 * @private
 */
br.test.GwtTestRunner.createProxyDescribeFunction = function(fOrigDescribeFunction, bIsXDescribe)
{
	return function(description, closure)
	{
		var pInvalidChars = ["\\","/",":","*","?","<",">"]
		for (var i = 0; i < pInvalidChars.length; i++) 
		{ 
			var cInvalidChar = pInvalidChars[i];
			if (description.indexOf(cInvalidChar) > -1)
			{
				throw new br.Errors.CustomError("InvalidSuiteError", "Invalid character '" + cInvalidChar + "' in test suite '"+ description + "'.");
			}
		}
		
		if (br.test.GwtTestRunner.m_mSuites[description])
		{
			throw new br.Errors.CustomError("InvalidSuiteError", "The test suite '" + description + "' has already been defined.");
		}
		else
		{
			br.test.GwtTestRunner.m_mSuites[description] = closure;
			var jasmineDescribeReturnValue = fOrigDescribeFunction.call(this, description, closure);
			
			if(bIsXDescribe)
			{
				var fOrigIt = it;
				var fOrigFixtures = fixtures;
				var fOrigGetEnv = jasmine.getEnv;
				
				try
				{
					it = br.test.GwtTestRunner.capturingItFunction;
					fixtures = function() {};
					jasmine.getEnv = function()
					{
						return {
							currentSuite: {
								getFullName: function() {
									return description;
								}
							}
						};
					};
					
					closure();
				}
				finally
				{
					it = fOrigIt;
					fixtures = fOrigFixtures;
					jasmine.getEnv = fOrigGetEnv;
				}
			}
			
			return jasmineDescribeReturnValue;
		}
	};
};

/**
 * @private
 */
br.test.GwtTestRunner.createProxyItFunction = function(fOrigItFunction)
{
	return function(description, closure)
	{
		var sSuiteFullName = jasmine.getEnv().currentSuite.getFullName();
		var sSuiteNamespacedTestName = sSuiteFullName + "::" + description;
		
		if (br.test.GwtTestRunner.m_mTests[sSuiteNamespacedTestName] && !description.match("encountered a declaration exception"))
		{
			throw new br.Errors.CustomError("DuplicateTestError", "The test '" + sSuiteNamespacedTestName + "' has already been defined.");
		}
		else
		{
			closure.suiteName = sSuiteFullName;
			br.test.GwtTestRunner.m_mTests[sSuiteNamespacedTestName] = closure;
			var jasmineItReturnValue = fOrigItFunction.call(this, description, closure);
			
			return jasmineItReturnValue;
		}
	};
};


// *** FixtureRegistry Interface ***

br.test.GwtTestRunner.prototype.addFixture = function(sScope, oFixture)
{
	if(!oFixture.setUp)debugger;
	this.m_pFixtures.push({scopeMatcher:new RegExp("^" + sScope + "(\\..+|$)"), scopeLength:sScope.length + 1, fixture:oFixture});
	oFixture.addSubFixtures(new br.test.SubFixtureRegistry(this, sScope));
};

// *** Public Methods ***

/**
 * @private
 */
br.test.GwtTestRunner.initializeTest = function(sFixtureFactoryClass)
{
	var oTestRunner = new br.test.GwtTestRunner(sFixtureFactoryClass);
	
	beforeEach(this.createTestMethod(oTestRunner, "startTest"));
	afterEach(this.createTestMethod(oTestRunner, "endTest"));
};

/**
 * @private
 */
br.test.GwtTestRunner.prototype.startTest = function()
{
	require("br/AliasRegistry").clear();
	require("br/ServiceRegistry").clear();

	given = this.m_fDoGiven;
	when = this.m_fDoWhen;
	then = this.m_fDoThen;
	and = this.m_fDoAnd;
	startingContinuesFrom = this.m_fStartingContinuesFrom;
	finishedContinuesFrom = this.m_fFinishedContinuesFrom;
	
	this.m_bTestFailed = false;
	this.m_nTestPhase = br.test.GwtTestRunner.INIT_PHASE;
	
	if(this.m_oFixtureFactory.setUp) {
		try {
			this.m_oFixtureFactory.setUp();
		}
		catch(e) {
			throw new br.Errors.CustomError("TestSetUpError", e.message,
					"Error occured in br.test.GwtTestRunner.prototype.startTest() calling this.m_oFixtureFactory.setUp()");
		}
	}

	for(var i = 0, l = this.m_pFixtures.length; i < l; ++i)
	{
		var oFixture = this.m_pFixtures[i].fixture;
		try {
			oFixture.setUp();
		}
		catch(e) {
			throw new br.Errors.CustomError("TestSetUpError", e.message,
					"Error occured in br.test.GwtTestRunner.prototype.startTest() calling oFixture.setUp()");
		}
	}
};

/**
 * @private
 */
br.test.GwtTestRunner.prototype.endTest = function()
{
	for(var i = 0, l = this.m_pFixtures.length; i < l; ++i)
	{
		var oFixture = this.m_pFixtures[i].fixture;
		try {
			oFixture.tearDown();
		}
		catch(e) {
			throw new br.Errors.CustomError("TestTearDownError", e.message,
					"Error occured in br.test.GwtTestRunner.prototype.endTest() calling oFixture.tearDown()");
		}
		
	}
	
	if (document.body.hasChildNodes())
	{
		for (var i = 0, j = document.body.childNodes.length; i < j; i++)
		{
			document.body.removeChild(document.body.childNodes[0]);
		}
	}
	
	if(!this.m_bTestFailed && ((this.m_nTestPhase == br.test.GwtTestRunner.GIVEN_PHASE) ||
		(this.m_nTestPhase == br.test.GwtTestRunner.WHEN_PHASE)))
	{
		throw new br.Errors.CustomError("UnterminatedTestError", "Tests must finish with one or more 'THEN' statements");
	}
};

/**
 * @private
 */
br.test.GwtTestRunner.prototype.startingContinuesFrom = function(description)
{
	this.m_nTestPhase = br.test.GwtTestRunner.INIT_PHASE;
	
	var sSuiteNamespacedTestName;
	if(description.match(/::/))
	{
		sSuiteNamespacedTestName = description;
	}
	else
	{
		var sSuiteFullName = (this.currentSuiteName) ? this.currentSuiteName : jasmine.getEnv().currentSpec.suite.getFullName();
		sSuiteNamespacedTestName = sSuiteFullName + "::" + description;
	}
	
	var fTest = br.test.GwtTestRunner.m_mTests[sSuiteNamespacedTestName];
	
	if(!fTest)
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "attempt to continue from a test that doesn't exist: '" + sSuiteNamespacedTestName + "'");
	}
	
	this.currentSuiteName = fTest.suiteName;
	fTest();
	this.currentSuiteName = null;
};

/**
 * @private
 */
br.test.GwtTestRunner.prototype.finishedContinuesFrom = function()
{
	this.m_nTestPhase = br.test.GwtTestRunner.GIVEN_PHASE;
};

/**
 * @private
 */
br.test.GwtTestRunner.prototype.doGiven = function(sStatement)
{
	var TimeUtility = br.test.TimeUtility;
	try
	{
		TimeUtility.captureTimerFunctions();
		
		var oStatement = this._parseStatement(sStatement, br.test.GwtTestRunner.GIVEN_PHASE);
		oStatement.fixture.doGiven(oStatement.propertyName, oStatement.propertyValue);
		
		TimeUtility.nextStep();
	}
	catch(e)
	{
		this._handleError(e);
	}
	finally
	{
		TimeUtility.releaseTimerFunctions();
	}
};

/**
 * @private
 */
br.test.GwtTestRunner.prototype.doWhen = function(sStatement)
{
	var TimeUtility = br.test.TimeUtility;
	try
	{
		TimeUtility.captureTimerFunctions();
		
		var oStatement = this._parseStatement(sStatement, br.test.GwtTestRunner.WHEN_PHASE);
		oStatement.fixture.doWhen(oStatement.propertyName, oStatement.propertyValue);
		
		TimeUtility.nextStep();
	}
	catch(e)
	{
		this._handleError(e);
	}
	finally
	{
		TimeUtility.releaseTimerFunctions();
	}
};

/**
 * @private
 */
br.test.GwtTestRunner.prototype.doThen = function(sStatement)
{
	var TimeUtility = br.test.TimeUtility;

	try
	{
		TimeUtility.captureTimerFunctions();
		
		var oStatement = this._parseStatement(sStatement, br.test.GwtTestRunner.THEN_PHASE);
		oStatement.fixture.doThen(oStatement.propertyName, oStatement.propertyValue);
		
		TimeUtility.nextStep();
	}
	catch(e)
	{
		this._handleError(e);
	}
	finally
	{
		TimeUtility.releaseTimerFunctions();
	}
};

/**
 * @private
 */
br.test.GwtTestRunner.prototype.doAnd = function(sStatement, oMessage)
{
	switch(this.m_nTestPhase)
	{
		case br.test.GwtTestRunner.GIVEN_PHASE:
			this.doGiven(sStatement);
			break;
		
		case br.test.GwtTestRunner.WHEN_PHASE:
			this.doWhen(sStatement);
			break;
		
		case br.test.GwtTestRunner.THEN_PHASE:
			this.doThen(sStatement, oMessage);
			break;
		
		default:
			this._throwError("InvalidPhaseError", sStatement, "'AND' statements can not occur until a 'GIVEN', 'WHEN' or 'THEN' statement has been made.");
	}
};


// *** Private Methods ***

/**
 * @private
 */
br.test.GwtTestRunner.prototype._handleError = function(e)
{
	this.m_bTestFailed = true;
	
	if(e.getMessage)
	{
		fail(e.getMessage());
	}
	else
	{
		throw(e);
	}
};

/**
 * @private
 */
br.test.GwtTestRunner.prototype._updatePhase = function(nPhase, sStatement)
{
	if(nPhase == br.test.GwtTestRunner.GIVEN_PHASE)
	{
		if(this.m_nTestPhase == br.test.GwtTestRunner.INIT_PHASE)
		{
			this.m_nTestPhase = br.test.GwtTestRunner.GIVEN_PHASE;
		}
		else if(this.m_nTestPhase != br.test.GwtTestRunner.GIVEN_PHASE)
		{
			this._throwError("InvalidPhaseError", sStatement, "'GIVEN' statements must occur before 'WHEN' and 'THEN' statements.");
		}
	}
	else if(nPhase == br.test.GwtTestRunner.WHEN_PHASE)
	{
		if(this.m_nTestPhase == br.test.GwtTestRunner.GIVEN_PHASE)
		{
			this.m_nTestPhase = br.test.GwtTestRunner.WHEN_PHASE;
		}
		else if(this.m_nTestPhase != br.test.GwtTestRunner.WHEN_PHASE)
		{
			this._throwError("InvalidPhaseError", sStatement, "'WHEN' statements must occur after 'GIVEN' statements, but before 'THEN' statements.");
		}
	}
	else if(nPhase == br.test.GwtTestRunner.THEN_PHASE)
	{
		if((this.m_nTestPhase == br.test.GwtTestRunner.GIVEN_PHASE) ||
			(this.m_nTestPhase == br.test.GwtTestRunner.WHEN_PHASE))
		{
			this.m_nTestPhase = br.test.GwtTestRunner.THEN_PHASE;
		}
		else if(this.m_nTestPhase != br.test.GwtTestRunner.THEN_PHASE)
		{
			this._throwError("InvalidPhaseError", sStatement, "'THEN' statements must occur after 'GIVEN' and 'WHEN' statements.");
		}
	}
};

/**
 * @private
 */
br.test.GwtTestRunner.prototype._parseStatement = function(sStatement, nPhase)
{

	var newlinePlaceholder = "<!--space--!>";
	
	sStatement = sStatement.replace("\n",newlinePlaceholder);
	
	this._updatePhase(nPhase, sStatement);
	
	/**
	 * Parses Statements in the format <fixtureName>.<propertyName> <operator> <propertyValue>
	 * uses '[\x21-\x7E]' rather than '.' to match any character so that newlines can be included too
	 */
	var pStatement = /(.+)(\=\>|\=)(.+)/i.exec(sStatement);
	
	for (var i = 0; i < pStatement.length; i++)
	{
		pStatement[i] = (pStatement[i].trim());
	}

	if(!pStatement || (pStatement.length != 4) || !pStatement[1] || !pStatement[2] || !pStatement[3])
	{
		this._throwError("IllegalStatementError", sStatement, "Statement should have the form <fixtureName>.<propertyName> <operator> <propertyValue>");
	}

	var oStatement =
	{
		property:(pStatement[1].trim()),
		operator:pStatement[2],
		propertyValue:this._getTypedPropertyValue(pStatement[3].replace(newlinePlaceholder,"\n"))
	};
	
	if(nPhase === br.test.GwtTestRunner.WHEN_PHASE && oStatement.operator != "=>")
	{
		this._throwError("IllegalStatementError", sStatement, "'When Statements should use => as an Operator");
	}
	
	if(oStatement.propertyValue === null)
	{
		this._throwError("InvalidPropertyValueError", sStatement, "'" + oStatement.propertyValue + 
				"' is not a valid type (only strings, numbers, booleans and undefined are supported)");
	}
	
	this._addFixtureToStatement(oStatement);
	if(!oStatement.fixture)
	{
		this._throwError("InvalidFixtureNameError", sStatement, "No Fixture has been specified matching '" + oStatement.propertyName + "'");
	}
	
	return oStatement;
};

/**
 * @private
 */
br.test.GwtTestRunner.prototype._addFixtureToStatement = function(oStatement)
{
	for(var i = 0, l = this.m_pFixtures.length; i < l; ++i)
	{
		var oNextFixture = this.m_pFixtures[i];
		
		if(oStatement.property.match(oNextFixture.scopeMatcher))
		{
			var sFixtureProperty = oStatement.property.substr(oNextFixture.scopeLength);
			var bCanHandleProperty = (sFixtureProperty.length > 0) ? oNextFixture.fixture.canHandleProperty(sFixtureProperty) :
				oNextFixture.fixture.canHandleExactMatch();
			
			if(bCanHandleProperty)
			{
				oStatement.fixture = oNextFixture.fixture;
				oStatement.propertyName = sFixtureProperty;
				break;
			}
		}
	}
};

/**
 * @private
 */
br.test.GwtTestRunner.prototype._getTypedPropertyValue = function(sValue)
{
	var vValue = null;
	
	if(sValue == "true")
	{
		vValue = true;
	}
	else if(sValue == "false")
	{
		vValue = false;
	}
	else if(sValue == "undefined")
	{
		vValue = undefined;
	}
	else if(sValue.match(/^'[.\s\S]*'$/))
	{
		vValue = sValue.substr(1, sValue.length - 2);
	}
	else if(!isNaN(sValue))
	{
		vValue = Number(sValue);
	}
	else if(sValue.match(/^\[.*\]$/))
	{
		var pItems = sValue.substr(1, sValue.length - 2).split(/ *, */);
		
		vValue = [];
		for(var i = 0, l = pItems.length; i < l; ++i)
		{
			vValue[i] = this._getTypedPropertyValue(pItems[i]);
		}
	}
	
	return vValue;
};

/**
 * @private
 */
br.test.GwtTestRunner.prototype._throwError = function(sType, sStatement, sMessage)
{
	throw new br.Errors.CustomError(sType, "Error handling statement '" + sStatement + "':\n\t" + sMessage);
};


/* ****************************
 * 		JASMINE OVERRIDES.
 * ****************************/

if(window.jasmine)
{
	describe = br.test.GwtTestRunner.createProxyDescribeFunction(describe);
	xdescribe = br.test.GwtTestRunner.createProxyDescribeFunction(xdescribe, true);
	
	jasmine.Env.prototype.it = br.test.GwtTestRunner.createProxyItFunction(jasmine.Env.prototype.it);
	jasmine.Env.prototype.xit = br.test.GwtTestRunner.createProxyItFunction(jasmine.Env.prototype.xit);
	
	br.test.GwtTestRunner.capturingItFunction = br.test.GwtTestRunner.createProxyItFunction(function() {});
}
