require('br-presenter/_resources-test-at/html/test-form.html');
(function() {
    var assertAssertError = require('jsunitextensions').assertAssertError;
    var Errors = require("br/Errors");
    var AlertFixture = require('br-test/AlertFixture');
    AlertFixtureTest = TestCase("AlertFixtureTest");

    AlertFixtureTest.prototype.setUp = function()
    {

    };

    AlertFixtureTest.prototype.tearDown = function()
    {

    };

    AlertFixtureTest.prototype.test_doThenWithSingleAlert = function()
    {
        var oAlertFixture = new AlertFixture();
        oAlertFixture.setUp();
        alert("hello");

        oAlertFixture.doThen("alert.triggered", "hello");
        oAlertFixture.tearDown();
    };

    AlertFixtureTest.prototype.test_doThenWithMultipleAlerts = function()
    {
        var oAlertFixture = new AlertFixture();
        oAlertFixture.setUp();
        alert("hello");
        alert("world");
        alert("bye");

        oAlertFixture.doThen("alert.triggered", "hello");
        oAlertFixture.doThen("alert.triggered", "world");
        oAlertFixture.doThen("alert.triggered", "bye");
        oAlertFixture.tearDown();
    };

    AlertFixtureTest.prototype.test_doThenWithMultipleAlertsButAssertInWrongOrder = function()
    {
        var oAlertFixture = new AlertFixture();
        oAlertFixture.setUp();
        alert("hello");
        alert("world");

        assertAssertError("1a", function() {
            oAlertFixture.doThen("alert.triggered", "world");
        });

        assertAssertError("2a", function() {
            oAlertFixture.tearDown();
        });
    };

    AlertFixtureTest.prototype.test_tearDownThrowsAnErrorIfThereAreAlertsThatWereNotVerifiedInTheTest = function()
    {
        var oAlertFixture = new AlertFixture();
        oAlertFixture.setUp();
        alert("hello")
        alert("world")

        oAlertFixture.doThen("alert.triggered", "hello");

        assertAssertError("1a", function() {
            oAlertFixture.tearDown();
        });
    };

    AlertFixtureTest.prototype.test_doThenWithNoAlertTriggered = function()
    {
        var oAlertFixture = new AlertFixture();
        oAlertFixture.setUp();

        assertAssertError("1a", function() {
            oAlertFixture.doThen("alert.triggered", "hello");
        });

        oAlertFixture.tearDown();
    };


    AlertFixtureTest.prototype.test_doGivenThrowsException = function()
    {
        var oAlertFixture = new AlertFixture();
        oAlertFixture.setUp();

        assertException("1a", function() {
            oAlertFixture.doGiven("property-name", "property-value");
        }, Errors.INVALID_TEST);

        oAlertFixture.tearDown();
    };

    AlertFixtureTest.prototype.test_doWhenThrowsException = function()
    {
        var oAlertFixture = new AlertFixture();
        oAlertFixture.setUp();

        assertException("1a", function() {
            oAlertFixture.doWhen("property-name", "property-value");
        }, Errors.INVALID_TEST);

        oAlertFixture.tearDown();
    };
})();
