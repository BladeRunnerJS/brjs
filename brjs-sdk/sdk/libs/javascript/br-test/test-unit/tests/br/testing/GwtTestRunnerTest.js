(function() {
    var Errors = require("br/Errors");
    var GwtTestRunner = require("br/test/GwtTestRunner");
    var Core = require("br/Core");
    var jasmine = require("jasmine");
    var Mock4JS = require('mock4js');

    var TestFixtureFactory = require("br/test/TestFixtureFactory");

    GwtTestRunnerTest = TestCase("GwtTestRunnerTest");

    GwtTestRunnerTest.prototype.setUp = function()
    {
        Mock4JS.addMockSupport(window);
        Mock4JS.clearMocksToVerify();

        this.m_oOrigTestingPackage = window.testing;

        this.m_fOrigFixtures = window.fixtures;
        fixtures = null;

        this.m_fOrigHandleError = GwtTestRunner.prototype._handleError;
        GwtTestRunner.prototype._handleError = function(e)
        {
            this.m_bTestFailed = true;
            throw(e);
        };

        GwtTestRunner.m_mTests = {};
        GwtTestRunner.m_mSuites = {};
    };

    GwtTestRunnerTest.prototype.tearDown = function()
    {
        testing = this.m_oOrigTestingPackage;
        window.fixtures = this.m_fOrigFixtures;
        GwtTestRunner.prototype._handleError = this.m_fOrigHandleError;

        Mock4JS.verifyAllMocks();
    };

    GwtTestRunnerTest.prototype.getFixture = function(oTestRunner, sFixtureName)
    {
        return oTestRunner.m_oFixtureFactory.getFixture(sFixtureName);
    };

    GwtTestRunnerTest.prototype.stubMockFixture = function(oTestRunner, sFixtureName)
    {
        var oMockFixture = this.getFixture(oTestRunner, sFixtureName);

        oMockFixture.stubs().doGiven(ANYTHING, ANYTHING);
        oMockFixture.stubs().doWhen(ANYTHING, ANYTHING);
        oMockFixture.stubs().doThen(ANYTHING, ANYTHING);
        oMockFixture.stubs().tearDown();
    };

    GwtTestRunnerTest.prototype.test_nonExistentFactoryClassCausesException = function()
    {
        assertException(function(){
            new GwtTestRunner("br.test.NonExistentTestFixtureFactory");
        }, "InvalidFactoryError");
    };

    GwtTestRunnerTest.prototype.test_factoryClassOfWrongTypeCausesException = function()
    {
        var InvalidTestFixtureFactory = function(){};

        assertException(function(){
            new GwtTestRunner("InvalidTestFixtureFactory");
        }, "InvalidFactoryError");
    };

    GwtTestRunnerTest.prototype.test_whenFirstCausesException = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();

        assertException(function(){
            oTestRunner.doWhen("fixture.prop => 'value'");
        }, "InvalidPhaseError");
    };

    GwtTestRunnerTest.prototype.test_thenFirstCausesException = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();

        assertException(function(){
            oTestRunner.doThen("fixture.prop = 'value'");
        }, "InvalidPhaseError");
    };

    GwtTestRunnerTest.prototype.test_andFirstCausesException = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();

        assertException(function(){
            oTestRunner.doAnd("fixture.prop = 'value'");
        }, "InvalidPhaseError");
    };

    GwtTestRunnerTest.prototype.test_givenOnlyCausesException = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();
        this.stubMockFixture(oTestRunner, "fixture");

        oTestRunner.doGiven("fixture.prop = 'value'");

        assertException(function(){
            oTestRunner.endTest();
        }, "UnterminatedTestError");
    };

    GwtTestRunnerTest.prototype.test_givenWhenOnlyCausesException = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();
        this.stubMockFixture(oTestRunner, "fixture");

        oTestRunner.doGiven("fixture.prop = 'value'");
        oTestRunner.doWhen("fixture.prop => 'value'");

        assertException(function(){
            oTestRunner.endTest();
        }, "UnterminatedTestError");
    };

    GwtTestRunnerTest.prototype.test_givenAfterWhenCausesException = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();
        this.stubMockFixture(oTestRunner, "fixture");

        oTestRunner.doGiven("fixture.prop = 'value'");
        oTestRunner.doWhen("fixture.prop => 'value'");

        assertException(function(){
            oTestRunner.doGiven("fixture.prop = 'value'");
        }, "InvalidPhaseError");
    };

    GwtTestRunnerTest.prototype.test_givenAfterThenCausesException = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();
        this.stubMockFixture(oTestRunner, "fixture");

        oTestRunner.doGiven("fixture.prop = 'value'");
        oTestRunner.doThen("fixture.prop = 'value'");

        assertException(function(){
            oTestRunner.doGiven("fixture.prop = 'value'");
        }, "InvalidPhaseError");
    };

    GwtTestRunnerTest.prototype.test_whenAfterThenCausesException = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();
        this.stubMockFixture(oTestRunner, "fixture");

        assertException(function(){
            oTestRunner.doWhen("fixture.prop => 'value'");
        }, "InvalidPhaseError");
    };

    GwtTestRunnerTest.prototype.test_givenWhenThenIsAllowed = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();
        this.stubMockFixture(oTestRunner, "fixture");

        oTestRunner.doGiven("fixture.prop = 'value'");
        oTestRunner.doWhen("fixture.prop => 'value'");
        oTestRunner.doThen("fixture.prop = 'value'");
        oTestRunner.endTest();
    };

    GwtTestRunnerTest.prototype.test_givenThenIsAllowed = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();
        this.stubMockFixture(oTestRunner, "fixture");

        oTestRunner.doGiven("fixture.prop = 'value'");
        oTestRunner.doThen("fixture.prop = 'value'");
        oTestRunner.endTest();
    };

    GwtTestRunnerTest.prototype.test_andsCanBeChained = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();
        this.stubMockFixture(oTestRunner, "fixture");

        oTestRunner.doGiven("fixture.prop = 'value'");
        oTestRunner.doAnd("fixture.prop = 'value'");
        oTestRunner.doAnd("fixture.prop = 'value'");

        oTestRunner.doWhen("fixture.prop => 'value'");
        oTestRunner.doAnd("fixture.prop => 'value'");
        oTestRunner.doAnd("fixture.prop => 'value'");

        oTestRunner.doThen("fixture.prop = 'value'");
        oTestRunner.doAnd("fixture.prop = 'value'");
        oTestRunner.doAnd("fixture.prop = 'value'");

        oTestRunner.endTest();
    };

    GwtTestRunnerTest.prototype.test_whensMustUseBecomesAndNotEquals = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();
        this.stubMockFixture(oTestRunner, "fixture");

        oTestRunner.doGiven("fixture.prop = 'value'");

        assertException(function(){
            oTestRunner.doWhen("fixture = 'value'");
        }, "");
    };

    GwtTestRunnerTest.prototype.test_statementsMustHaveAProperty = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();

        assertException(function(){
            oTestRunner.doGiven(" = 'value'");
        }, "");
    };

    GwtTestRunnerTest.prototype.test_statementsMustHaveAValue = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();

        assertException(function(){
            oTestRunner.doGiven("fixture.prop = ");
        }, "");
    };

    GwtTestRunnerTest.prototype.test_fixtureMustExist = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();

        assertException(function(){
            oTestRunner.doGiven("nonExistentFixture.prop = 'value'");
        }, "");
    };

    GwtTestRunnerTest.prototype.test_propertyMustExist = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();

        assertException(function(){
            oTestRunner.doGiven("fixture.nonExistentProperty = 'value'");
        }, "");
    };

    GwtTestRunnerTest.prototype.test_fixtureNameCanContainEqualsSign = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();
        this.stubMockFixture(oTestRunner, "another=fixture");

        oTestRunner.doGiven("another=fixture.prop = 'value'");
    };

    GwtTestRunnerTest.prototype.test_stringValuesAreAllowed = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();

        this.getFixture(oTestRunner, "fixture").expects(once()).doGiven("prop", "value");

        oTestRunner.doGiven("fixture.prop = 'value'");
    };

    GwtTestRunnerTest.prototype.test_emptyStringValuesAreAllowed = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();

        this.getFixture(oTestRunner, "fixture").expects(once()).doGiven("prop", "");

        oTestRunner.doGiven("fixture.prop = ''");
    };

    GwtTestRunnerTest.prototype.test_apostrophesInStringValuesAreAllowed = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();

        this.getFixture(oTestRunner, "fixture").expects(once()).doGiven("prop", "prop's value");
        oTestRunner.doGiven("fixture.prop = 'prop's value'");
    };

    GwtTestRunnerTest.prototype.test_numbersValuesAreAllowed = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();

        this.getFixture(oTestRunner, "fixture").expects(once()).doGiven("prop", 42);

        oTestRunner.doGiven("fixture.prop = 42");
    };

    GwtTestRunnerTest.prototype.test_booleansValuesAreAllowed = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();

        this.getFixture(oTestRunner, "fixture").expects(once()).doGiven("prop", true);
        this.getFixture(oTestRunner, "fixture").expects(once()).doGiven("prop", false);

        oTestRunner.doGiven("fixture.prop = true");
        oTestRunner.doGiven("fixture.prop = false");
    };

    GwtTestRunnerTest.prototype.test_arrayValuesAreAllowed = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();

        this.getFixture(oTestRunner, "fixture").expects(once()).doGiven("prop", ["value", 42, true]);

        oTestRunner.doGiven("fixture.prop = ['value', 42, true]");
    };

    GwtTestRunnerTest.prototype.test_stringsMustBeQuoted = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();

        assertException(function(){
            oTestRunner.doGiven("fixture.prop = foo bar");
        }, "");
    };

    GwtTestRunnerTest.prototype.test_normalFixturesCantBeUsedAsPropertyFixtures = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();

        assertException(function(){
            oTestRunner.doGiven("fixture = 'value'");
        }, "");
    };

    GwtTestRunnerTest.prototype.test_propertyFixturesDontRequireADot = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();

        this.getFixture(oTestRunner, "propertyFixture").expects(once()).doGiven("", "value");

        oTestRunner.doGiven("propertyFixture = 'value'");
    };

    GwtTestRunnerTest.prototype.test_ifYouHaveADotYouMustProvideAPropertyNameToo = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();

        assertException(function(){
            oTestRunner.doGiven("propertyFixture. = 'value'");
        }, "");
    };

    GwtTestRunnerTest.prototype.test_StatementValueCanContainSymbols = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();

        this.getFixture(oTestRunner, "propertyFixture").expects(once()).doGiven("prop", "value & key");
        oTestRunner.doGiven("propertyFixture.prop = 'value & key'");

        this.getFixture(oTestRunner, "propertyFixture").expects(once()).doGiven("prop", "value :@~#?!£$%^&* key");
        oTestRunner.doGiven("propertyFixture.prop = 'value :@~#?!£$%^&* key'");

    };

    GwtTestRunnerTest.prototype.test_propertyFixturesCanAlsoBeUsedNormally = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();

        this.getFixture(oTestRunner, "propertyFixture").expects(once()).doGiven("prop", "value");

        oTestRunner.doGiven("propertyFixture.prop = 'value'");
    };

    GwtTestRunnerTest.prototype.test_subFixturesCanBeAddressedViaParent = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();

        this.getFixture(oTestRunner, "parentFixture").getFirstMockFixture().expects(once()).doGiven("prop", "value");
        oTestRunner.doGiven("parentFixture.subFixture1.prop = 'value'");

        Mock4JS.verifyAllMocks();

        this.getFixture(oTestRunner, "parentFixture").getSecondMockFixture().expects(once()).doGiven("prop", "value2");
        oTestRunner.doGiven("parentFixture.subFixture2.prop = 'value2'");
    };

    GwtTestRunnerTest.prototype.test_subFixturesCanBeAddresedViaChainedParents = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();

        this.getFixture(oTestRunner, "grandParentFixture").getChildMockFixture().getFirstMockFixture().expects(once()).doGiven("prop", "value");
        oTestRunner.doGiven("grandParentFixture.childFixture.subFixture1.prop = 'value'");
    };

    GwtTestRunnerTest.prototype.test_continuesFromRunsEarlierLinkInTestChain = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();
        this.stubMockFixture(oTestRunner, "fixture");

        describe("test-suite #1", function()
        {
            it("test #1", function()
            {
                oTestRunner.doGiven("fixture.prop = 'value1'");
            });
        });

        this.getFixture(oTestRunner, "fixture").expects(once()).doGiven("prop", "value1");
        this.getFixture(oTestRunner, "fixture").expects(once()).doGiven("prop", "value2");

        oTestRunner.doGiven("test.continuesFrom = 'test #1'");
        oTestRunner.doGiven("fixture.prop = 'value2'");
        oTestRunner.doThen("fixture.prop = 'value2'");
        oTestRunner.endTest();
    };

    GwtTestRunnerTest.prototype.test_continuesFromsCanBeChainedMultipleTimes = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();
        this.stubMockFixture(oTestRunner, "fixture");

        describe("test-suite #1", function() {
            it('test #1', function() {
                oTestRunner.doGiven("fixture.prop = 'value1'");
            });

            it('test #2', function() {
                oTestRunner.doGiven("test.continuesFrom = 'test #1'");
                oTestRunner.doGiven("fixture.prop = 'value2'");
            });
        });

        this.getFixture(oTestRunner, "fixture").expects(once()).doGiven("prop", "value1");
        this.getFixture(oTestRunner, "fixture").expects(once()).doGiven("prop", "value2");
        this.getFixture(oTestRunner, "fixture").expects(once()).doGiven("prop", "value3");

        oTestRunner.doGiven("test.continuesFrom = 'test #2'");
        oTestRunner.doGiven("fixture.prop = 'value3'");
        oTestRunner.doThen("fixture.prop = 'value3'");
        oTestRunner.endTest();
    };

    GwtTestRunnerTest.prototype.test_continuesFromsCantBeChainedAcrossSuitesWithoutNamespacing = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();
        this.stubMockFixture(oTestRunner, "fixture");

        describe("test-suite #1", function() {
            it('test #1', function()
            {
                oTestRunner.doGiven("fixture.prop = 'value1'");
            });
        });

        describe("test-suite #2", function() {
            it('test #2', function()
            {
                oTestRunner.doGiven("test.continuesFrom = 'test #1'");
                oTestRunner.doGiven("fixture.prop = 'value2'");
            });
        });

        assertException(function() {
            oTestRunner.doGiven("test.continuesFrom = 'test #2'");
            oTestRunner.doGiven("fixture.prop = 'value3'");
            oTestRunner.doThen("fixture.prop = 'value3'");
        }, Errors.INVALID_TEST);
    };

    GwtTestRunnerTest.prototype.test_continuesFromsCanBeChainedAcrossSuitesUsingNamespacing = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();
        this.stubMockFixture(oTestRunner, "fixture");

        describe("test-suite #1", function() {
            it('test #1', function()
            {
                oTestRunner.doGiven("fixture.prop = 'value1'");
            });
        });

        describe("test-suite #2", function() {
            it('test #2', function()
            {
                oTestRunner.doGiven("test.continuesFrom = 'test-suite #1::test #1'");
                oTestRunner.doGiven("fixture.prop = 'value2'");
            });
        });

        this.getFixture(oTestRunner, "fixture").expects(once()).doGiven("prop", "value1");
        this.getFixture(oTestRunner, "fixture").expects(once()).doGiven("prop", "value2");
        this.getFixture(oTestRunner, "fixture").expects(once()).doGiven("prop", "value3");

        oTestRunner.doGiven("test.continuesFrom = 'test #2'");
        oTestRunner.doGiven("fixture.prop = 'value3'");
        oTestRunner.doThen("fixture.prop = 'value3'");
        oTestRunner.endTest();
    };

    GwtTestRunnerTest.prototype.test_chainedTestsCanReferToOtherLocalTestsUsingLocalNames = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();
        this.stubMockFixture(oTestRunner, "fixture");

        describe("test-suite #1", function() {
            it('test #1', function()
            {
                oTestRunner.doGiven("fixture.prop = 'value1'");
            });

            it('test #2', function()
            {
                oTestRunner.doGiven("test.continuesFrom = 'test #1'");
                oTestRunner.doGiven("fixture.prop = 'value2'");
            });
        });

        describe("test-suite #2", function() {
        });

        this.getFixture(oTestRunner, "fixture").expects(once()).doGiven("prop", "value1");
        this.getFixture(oTestRunner, "fixture").expects(once()).doGiven("prop", "value2");
        this.getFixture(oTestRunner, "fixture").expects(once()).doGiven("prop", "value3");

        oTestRunner.doGiven("test.continuesFrom = 'test-suite #1::test #2'");
        oTestRunner.doGiven("fixture.prop = 'value3'");
        oTestRunner.doThen("fixture.prop = 'value3'");
        oTestRunner.endTest();
    };

    GwtTestRunnerTest.prototype.test_exceptionIsThrownIfSuiteAlreadyDefined = function()
    {
        var passTest = false;
        try {
            describe("my suite", function() { });
            describe("my suite", function() { });
        } catch (ex) {
            passTest = true;
        }

        if (!passTest) fail("Expected test failure - suite is already defined");
    };

    GwtTestRunnerTest.prototype.test_exceptionIsThrownIfContinuingFromANonExistentTest = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();

        describe("some test suite", function() {
        });

        assertException(function(){
            oTestRunner.doGiven("test.continuesFrom = 'test #1'");
        }, "");
    };

    GwtTestRunnerTest.prototype.testExceptionIsThrownIfDescriptionContainsInvalidCharacter = function()
    {
        var passTest = false;
        try {
            describe(":")
        } catch (ex) {
            passTest = true;
        }

        if (!passTest) fail("Expected test failure - invalid char in description");
    };

    GwtTestRunnerTest.prototype.test_valuesWithNewLinesAreParsedProperly = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();

        this.getFixture(oTestRunner, "propertyFixture").expects(once()).doGiven("prop", "1\n2");
        this.getFixture(oTestRunner, "propertyFixture").expects(once()).doGiven("prop", "1\n2\n3\n4");

        oTestRunner.doGiven("propertyFixture.prop = '1\n2'");
        oTestRunner.doGiven("propertyFixture.prop = '1\n2\n3\n4'");
    };

    GwtTestRunnerTest.prototype.test_xitCanBeUsedToDisableTestsWithoutBreakingChaining = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();
        this.stubMockFixture(oTestRunner, "fixture");

        describe("test-suite #1", function() {
            xit('test #1', function() {
                oTestRunner.doGiven("fixture.prop = 'value1'");
            });

            it('test #2', function() {
                oTestRunner.doGiven("test.continuesFrom = 'test #1'");
                oTestRunner.doGiven("fixture.prop = 'value2'");
            });
        });

        this.getFixture(oTestRunner, "fixture").expects(once()).doGiven("prop", "value1");
        this.getFixture(oTestRunner, "fixture").expects(once()).doGiven("prop", "value2");
        this.getFixture(oTestRunner, "fixture").expects(once()).doGiven("prop", "value3");

        oTestRunner.doGiven("test.continuesFrom = 'test #2'");
        oTestRunner.doGiven("fixture.prop = 'value3'");
        oTestRunner.doThen("fixture.prop = 'value3'");
        oTestRunner.endTest();
    };

    // TODO: create unit tests that try to use something other than fixture
    // TODO: create unit tests that use prop2 instead of prop
    GwtTestRunnerTest.prototype.test_xdescribeCanBeUsedToDisableTestSuitesWithoutBreakingChaining = function()
    {
        var oTestRunner = new GwtTestRunner(TestFixtureFactory);
        oTestRunner.startTest();
        this.stubMockFixture(oTestRunner, "fixture");

        xdescribe("test-suite #1", function() {
            it('test #1', function()
            {
                oTestRunner.doGiven("fixture.prop = 'value1'");
            });
        });

        describe("test-suite #2", function() {
            it('test #2', function()
            {
                oTestRunner.doGiven("test.continuesFrom = 'test-suite #1::test #1'");
                oTestRunner.doGiven("fixture.prop = 'value2'");
            });
        });

        this.getFixture(oTestRunner, "fixture").expects(once()).doGiven("prop", "value1");
        this.getFixture(oTestRunner, "fixture").expects(once()).doGiven("prop", "value2");
        this.getFixture(oTestRunner, "fixture").expects(once()).doGiven("prop", "value3");

        oTestRunner.doGiven("test.continuesFrom = 'test #2'");
        oTestRunner.doGiven("fixture.prop = 'value3'");
        oTestRunner.doThen("fixture.prop = 'value3'");
        oTestRunner.endTest();
    };

    GwtTestRunnerTest.prototype.test_DottedNotationForGlobalsIsAllowed = function()
    {
        window.br = window.br || {};
        window.br.test = window.br.test || {};
        window.br.test.TestFixtureFactory = window.br.test.TestFixtureFactory || require("br/test/TestFixtureFactory");

        var oTestRunner = new GwtTestRunner("br.test.TestFixtureFactory");
        oTestRunner.startTest();
        this.stubMockFixture(oTestRunner, "fixture");

        oTestRunner.doGiven("fixture.prop = 'value'");
        oTestRunner.doWhen("fixture.prop => 'value'");
        oTestRunner.doThen("fixture.prop = 'value'");
        oTestRunner.endTest();
    };

	GwtTestRunnerTest.prototype.test_continuesFromsCanBeChainedMultipleTimes = function()
	{
		var oTestRunner = new GwtTestRunner(TestFixtureFactory);
		oTestRunner.startTest();
		this.stubMockFixture(oTestRunner, "fixture");

		describe("test-suite #1", function() {
			it('test #1', function() {
			oTestRunner.doGiven("fixture.prop = 'value1'");
			});

			it('test #2', function() {
			oTestRunner.doGiven("test.continuesFrom = 'test #1'");
			oTestRunner.doGiven("fixture.prop = 'value2'");
			});

			it('test #3', function() {
			oTestRunner.doGiven("test.continuesFrom = 'test #2'");
			oTestRunner.doGiven("fixture.prop = 'value2'");
			});

			it('test #4', function() {
			oTestRunner.doGiven("test.continuesFrom = 'test #3'");
			oTestRunner.doGiven("fixture.prop = 'value2'");
			});


			it('test #5', function() {
			oTestRunner.doGiven("test.continuesFrom = 'test #4'");
			oTestRunner.doGiven("fixture.prop = 'value2'");
			});


			it('test #6', function() {
			oTestRunner.doGiven("test.continuesFrom = 'test #5'");
			oTestRunner.doGiven("fixture.prop = 'value2'");
			});

			it('test #7', function() {
			oTestRunner.doGiven("test.continuesFrom = 'test #6'");
			oTestRunner.doGiven("fixture.prop = 'value2'");
			});

			it('test #8', function() {
			oTestRunner.doGiven("test.continuesFrom = 'test #7'");
			oTestRunner.doGiven("fixture.prop = 'value2'");
			});

			it('test #9', function() {
			oTestRunner.doGiven("test.continuesFrom = 'test #8'");
			oTestRunner.doGiven("fixture.prop = 'value2'");
			});

			it('test #10', function() {
				oTestRunner.doGiven("test.continuesFrom = 'test #9'");
				oTestRunner.doGiven("fixture.prop = 'value2'");
			});

			it('test #11', function() {
				oTestRunner.doGiven("test.continuesFrom = 'test #10'");
				oTestRunner.doGiven("fixture.prop = 'value2'");
			});
		});

		oTestRunner.doGiven("test.continuesFrom = 'test #11'");
		oTestRunner.doGiven("fixture.prop = 'value3'");
		oTestRunner.doThen("fixture.prop = 'value3'");
		oTestRunner.endTest();
	};

})();
