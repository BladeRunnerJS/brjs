var GwtTestRunner = require('br/test/GwtTestRunner');
var EventLogging = require('br/test/EventLogging');
var TestUtils = require('br/test/TestUtils');
// TODO: fixtures() needs to be made CommonJS friendly
window.ViewFixtureTestsFixtureFactory = require('br/test/ViewFixtureTestsFixtureFactory');

GwtTestRunner.initialize();

describe("User performs click events", function() {
	fixtures('ViewFixtureTestsFixtureFactory');
	
	beforeEach(function() {
		TestUtils.setupPage()
	});
	
	afterEach(function() {
		TestUtils.tearDownPage();
	});

	it("sets up the page", function() {
		given("page.loaded = true");
		when("test.page.(a#defaultFocus).clicked => true");
			when("test.page.(a#defaultFocus).focused => true");
		then("page.loaded = true");
		EventLogging.clearEvents();
	});
	
	it("calls click when a link is clicked", function() {
		given("test.continuesFrom = 'sets up the page'");
		when("test.page.(a#aLink).clicked => true");
		EventLogging.verifyEvents([ 
			EventLogging.evt('a#aLink', 'click') 
		]);
		then("page.loaded = true");
	});

	it("gives the focus to a clicked text input", function() {
		given("test.continuesFrom = 'sets up the page'");
		when("test.page.(input#textInput).clicked => true");
		EventLogging.verifyEvents([ 
			EventLogging.evt('input#textInput', 'focusin')
		])
		then("test.page.(input#textInput).focused = true");
		then("page.loaded = true");
	});
	
	it("gives the focus to a clicked text area", function() {
		given("test.continuesFrom = 'sets up the page'");
		when("test.page.(textarea#textArea).clicked => true");
		EventLogging.verifyEventsUnordered([ 
			EventLogging.evt('textarea#textArea', 'focusin'),
			EventLogging.evt('textarea#textArea', 'click')
		])
		then("test.page.(textarea#textArea).focused = true");
		then("page.loaded = true");
	});
	
	it("gives the focus to a clicked text input and then looses focus when another is clicked", function() {
		given("test.continuesFrom = 'sets up the page'");
		when("test.page.(input#textInput).clicked => true");
			and("test.page.(input#anotherTextInput).clicked => true");
			EventLogging.verifyEvents([
			EventLogging.evt('input#textInput', 'focusin'),
			EventLogging.evt('input#textInput', 'focusout'),
			EventLogging.evt('input#anotherTextInput', 'focusin')
		])
		then("test.page.(input#textInput).focused = false");
			and("test.page.(input#anotherTextInput).focused = true");
	});
	
	it("gives the focus to a button and submits the form", function() {
		given("test.continuesFrom = 'sets up the page'");
		when("test.page.(button#button).clicked => true");
//		EventLogging.verifyEvents([ 
//			EventLogging.evt('button#button', 'click'),
//			EventLogging.evt('form#testForm', 'submit')
//		])
		then("test.page.(button#button).focused = true");
	});
	
	it("gives the focus to a submit button and submits the form", function() {
		given("test.continuesFrom = 'sets up the page'");
		when("test.page.(input#submitButton).clicked => true");
		EventLogging.verifyEvents([ 
			EventLogging.evt('input#submitButton', 'click'),
			EventLogging.evt('form#testForm', 'submit')
		]);
		then("test.page.(input#submitButton).focused = true");
	});
	
	it("gives the focus to a button", function() {
		given("test.continuesFrom = 'sets up the page'");
		when("test.page.(button#nonFormButton).clicked => true");
		EventLogging.verifyEvents([ 
			EventLogging.evt('button#nonFormButton', 'click')
		]);
		then("test.page.(button#nonFormButton).focused = true");
	});
	
	it("gives the focus to a submit button", function() {
		given("test.continuesFrom = 'sets up the page'");
		when("test.page.(input#nonFormSubmitButton).clicked => true");
		EventLogging.verifyEvents([ 
			EventLogging.evt('input#nonFormSubmitButton', 'click')
		]);
		then("test.page.(input#nonFormSubmitButton).focused = true");
	});
	
	it("calls change and click when a checkbox is clicked", function() {
		given("test.continuesFrom = 'sets up the page'");
		when("test.page.(input#checkbox1).clicked => true");
		EventLogging.verifyEventsUnordered([ 
		  EventLogging.evt('input#checkbox1', 'click'),
		  EventLogging.evt('input#checkbox1', 'change')
		])
		then("test.page.(input#checkbox1).focused = true");
			and("test.page.(input#checkbox1).checked = true");
	});
		
	it("calls change and click when a radio button is clicked", function() {
		given("test.continuesFrom = 'sets up the page'");
		when("test.page.(input#radioButton1).clicked => true");
		EventLogging.verifyEventsUnordered([ 
		  EventLogging.evt('input#radioButton1', 'click'),
		  EventLogging.evt('input#radioButton1', 'change')
		]);
		then("test.page.(input#radioButton1).focused = true");
			and("test.page.(input#radioButton1).checked = true");
	});
	
	it("only calls focus when the current value of a drop down is re-selected", function() {
		given("test.continuesFrom = 'sets up the page'");
			and("test.page.(select#dropDown option#dropDownItem1).selected = true");
		EventLogging.clearEvents()
		when("test.page.(select#dropDown).clicked => true");
			and("test.page.(select#dropDown option#dropDownItem1).selected => true");
			EventLogging.verifyEvents([ 
			EventLogging.evt('select#dropDown', 'focusin')
		])
		then("test.page.(select#dropDown).focused = true");
			and("test.page.(select#dropDown option#dropDownItem1).selected = true");
	});
	
	it("calls change when the value of a drop down is changed", function() {
		given("test.continuesFrom = 'sets up the page'");
		when("test.page.(select#dropDown).clicked => true");
			and("test.page.(select#dropDown option#dropDownItem2).selected => true");
			EventLogging.verifyEvents([ 
			EventLogging.evt('select#dropDown', 'focusin'),
			EventLogging.evt('select#dropDown', 'change')
		])
		then("test.page.(select#dropDown).focused = true");
			and("test.page.(select#dropDown option#dropDownItem1).selected = false");
			and("test.page.(select#dropDown option#dropDownItem2).selected = true");
	});
	
	it("calls click when the current value of a multiple select drop down is re-selected", function() {
		given("test.continuesFrom = 'sets up the page'");
			and("test.page.(select#dropDownMultipleSelect option#dropDownMultipleSelect1).selected = true");
		EventLogging.clearEvents()
		when("test.page.(select#dropDownMultipleSelect).clicked => true");
			and("test.page.(select#dropDownMultipleSelect option#dropDownMultipleSelect1).selected => true");
			EventLogging.verifyEvents([ 
			EventLogging.evt('select#dropDownMultipleSelect', 'focusin')
		])
		then("test.page.(select#dropDownMultipleSelect).focused = true");
			and("test.page.(select#dropDownMultipleSelect option#dropDownMultipleSelect1).selected = true");
	});

});
