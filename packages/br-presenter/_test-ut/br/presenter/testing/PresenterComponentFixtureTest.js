require('../../../../_resources-test-at/html/test-form.html');
(function() {
    var assertFails = require('jsunitextensions').assertFails;

    var PresenterComponentFixture = require('br-presenter/testing/PresenterComponentFixture');
    var ComponentFixture = require('br-component/testing/ComponentFixture');
    var Core = require("br/Core");
    var Mock4JS = require("mock4js");

    PresenterComponentFixtureTest = TestCase("PresenterComponentFixtureTest");

    PresenterComponentFixtureTest.prototype.setUp = function()
    {
        Mock4JS.addMockSupport(window);
        Mock4JS.clearMocksToVerify();

        this.m_sTestViewID = "test-id";
        this.m_sTestPM = "acme.MyComponent";

        this.m_fOrigComponentDoGiven = ComponentFixture.prototype.doGiven;
        this.m_pDoGivenInvocations = [];
        var self = this;
        ComponentFixture.prototype.doGiven = function(sProperty, vValue)
        {
            self.m_pDoGivenInvocations.push({property:sProperty, value:vValue});
        };
    };

    PresenterComponentFixtureTest.prototype.tearDown = function()
    {
        Mock4JS.verifyAllMocks();

        ComponentFixture.prototype.doGiven = this.m_fOrigComponentDoGiven;
    };


    /* ********************************************************************
     *						 Instantiating Fixture
     **********************************************************************/

    PresenterComponentFixtureTest.prototype.test_fixtureMustBeInstantiatedWithTemplateIDAndPresentationModel = function()
    {
        var self = this;
        assertFails("1a PresenterComponentFixture cannot be constucted with null view template ID",
                function() {
                    new PresenterComponentFixture(null, self.m_sTestPM);
                }
        );
        assertFails("1b PresenterComponentFixture cannot be constucted with undefined view template ID",
            function() {
                new PresenterComponentFixture(undefined, self.m_sTestPM);
            }
        );
        assertFails("1c PresenterComponentFixture cannot be constucted with null presentation model",
            function() {
                new PresenterComponentFixture(self.m_sTestViewID, null);
            }
        );
        assertFails("1d PresenterComponentFixture cannot be constucted with undefined presentation model",
            function() {
                new PresenterComponentFixture(self.m_sTestViewID, undefined);
            }
        );
        assertNoException("1e PresenterComponentFixture should accept valid view template id and presentation model",
            function() {
                new PresenterComponentFixture(self.m_sTestViewID, self.m_sTestPM);
            }
        );
        assertFails("1f PresenterComponentFixture cannot be constucted with empty string view template ID",
                function() {
                    new PresenterComponentFixture("", self.m_sTestPM);
                }
        );
        assertFails("1g PresenterComponentFixture cannot be constucted with empty string presentation model",
                function() {
                    new PresenterComponentFixture(self.m_sTestViewID, "");
                }
            );
    };

    /* ********************************************************************
     *						 Handling opened
     **********************************************************************/

    PresenterComponentFixtureTest.prototype.test_canHandlePropertyOpened = function()
    {
        var oFixture = new PresenterComponentFixture(this.m_sTestViewID, this.m_sTestPM);
        var bCanHandleProperty = oFixture.canHandleProperty('opened');
        assertTrue(bCanHandleProperty);
    };

    //PresenterComponentFixtureTest.prototype.test_doGivenOpenedFalseThrowsException = function()
    //{
    //	var oPresenterComponentFixture = new br.presenter.testing.PresenterComponentFixture(this.m_sTestViewID,
    //			this.m_sTestPM);
    //
    //	assertFails("2a", function() {oPresenterComponentFixture.doGiven("opened", false);});
    //};

    PresenterComponentFixtureTest.prototype.test_doGivenOpenedProxiesThroughToTheComponentFixture = function()
    {
        var oPresenterComponentFixture = new PresenterComponentFixture(this.m_sTestViewID,
                this.m_sTestPM);

        oPresenterComponentFixture.doGiven("opened", true);
        assertEquals("3a", [{property:'opened', value:true}], this.m_pDoGivenInvocations);
    };

    // TODO with the current design it is not possible to confirm whether presenter_knockout.applyBindings is invoked
    //PresenterComponentFixtureTest.prototype.test_doGivenOpenedDoesNotBindTheView = function()
    //{
    //};

    /* ********************************************************************
     *						 Handling viewOpened
     **********************************************************************/

    PresenterComponentFixtureTest.prototype.test_canHandlePropertyViewOpened = function()
    {
        var oFixture = new PresenterComponentFixture(this.m_sTestViewID, this.m_sTestPM);
        var bCanHandleProperty = oFixture.canHandleProperty('viewOpened');
        assertTrue(bCanHandleProperty);
    };

    //PresenterComponentFixtureTest.prototype.test_doGivenViewOpenedFalseThrowsException = function()
    //{
    //	var oPresenterComponentFixture = new br.presenter.testing.PresenterComponentFixture(this.m_sTestViewID,
    //			this.m_sTestPM);
    //
    //	assertFails("4a", function() {oPresenterComponentFixture.doGiven("viewOpened", false);});
    //};

    PresenterComponentFixtureTest.prototype.test_doGivenViewOpenedProxiesThroughToTheComponentFixture = function()
    {
        var oPresenterComponentFixture = new PresenterComponentFixture(this.m_sTestViewID,
                this.m_sTestPM);

        oPresenterComponentFixture.doGiven("viewOpened", true);
        assertEquals("5a", [{property:'opened', value:true}], this.m_pDoGivenInvocations);
    };

    //TODO with the current design it is not possible to confirm that presenter_knockout.applyBindings is invoked
    //PresenterComponentFixtureTest.prototype.test_doGivenViewOpenedBindsViewViaKnockout = function()
    //{
    //};

    /* ********************************************************************
     *						 doWhen and doThen
     **********************************************************************/

    PresenterComponentFixtureTest.prototype.test_doWhenAndDoThenThrowException = function()
    {
        var oPresenterComponentFixture = new PresenterComponentFixture(this.m_sTestViewID,
                this.m_sTestPM);

        assertFails("6a", function() {oPresenterComponentFixture.doWhen("opened", true);});

        assertFails("6b", function() {oPresenterComponentFixture.doThen("opened", true);});

        assertFails("6c", function() {oPresenterComponentFixture.doWhen("viewOpened", true);});

        assertFails("6d", function() {oPresenterComponentFixture.doThen("viewOpened", true);});
    };
})();


