var GwtTestRunner = require('br/test/GwtTestRunner');

GwtTestRunner.initialize();

describe("User performs text events", function() {
	fixtures("ViewFixtureTestsFixtureFactory");
	
	beforeEach(function() {
		setupPage()
	});
	afterEach(function() {
		tearDownPage();
	});

	it("simulates a user typing 1234 into a textbox", function() {
		given("page.loaded = true");
			and("test.page.(input#textInput).value = ''");
			and("test.page.(input#textInput).clicked = true");
			and("test.page.(input#textInput).focused = true");
		clearEvents()
		when("test.page.(input#textInput).typedValue => '1234'");
		verifyEvents([ 
			// user types '1'
			evt('input#textInput', 'keydown'),evt('input#textInput', 'keypress'),
				evt('input#textInput', 'keyup'),
			// user types '2'
			evt('input#textInput', 'keydown'),evt('input#textInput', 'keypress'),
				evt('input#textInput', 'keyup'),
			// user types '3'
			evt('input#textInput', 'keydown'),evt('input#textInput', 'keypress'),
				evt('input#textInput', 'keyup'),
			// user types '4'
			evt('input#textInput', 'keydown'),evt('input#textInput', 'keypress'),
				evt('input#textInput', 'keyup') 
		]);
		then("test.page.(input#textInput).value = '1234'");
			and("test.page.(input#textInput).focused = true");
		clearEvents()
	});
	
	it("simulates a user typing into a textbox and selecting another element", function() {
		given("test.continuesFrom = 'simulates a user typing 1234 into a textbox'");
		when("test.page.(input#anotherTextInput).clicked => true");		
		verifyEventsUnordered([ 
		  	evt('input#textInput', 'change'),
		  	evt('input#anotherTextInput', 'focusin')
		])
		then("test.page.(input#textInput).focused = false");
			and("test.page.(input#anotherTextInput).focused = true");
	});
	
	it("simulates a user typing into a textbox, tabbing to another element and typing more", function() {
		given("test.continuesFrom = 'simulates a user typing 1234 into a textbox'");
		when("test.page.(input#textInput).typedValue => '\t'");	
			and("test.page.(input#anotherTextInput).typedValue => '5'");		
		verifyEventsUnordered([ 
			evt('input#textInput', 'keydown'),
			evt('input#textInput', 'keypress'),
			evt('input#textInput', 'keyup'),
		  	evt('input#textInput', 'change'),
		  	evt('input#anotherTextInput', 'focusin'),
			evt('input#anotherTextInput', 'keydown'),
			evt('input#anotherTextInput', 'keypress'),
			evt('input#anotherTextInput', 'keyup')
		])
		then("test.page.(input#textInput).focused = false");
			and("test.page.(input#anotherTextInput).focused = true");
	});
	
	it("simulates a user typing multiline text into a text area", function() {
		given("page.loaded = true");
			and("test.page.(textArea#textArea).value = ''");
			and("test.page.(textArea#textArea).focused = true");
		clearEvents()
		when("test.page.(textArea#textArea).typedValue => '12'");
			and("test.page.(textArea#textArea).typedValue => '\n'");		
			and("test.page.(textArea#textArea).typedValue => '34'");		
		verifyEvents([ 
			// user types '1'
			evt('textArea#textArea', 'keydown'),evt('textArea#textArea', 'keypress'),
				evt('textArea#textArea', 'keyup'),
			// user types '2'
			evt('textArea#textArea', 'keydown'),evt('textArea#textArea', 'keypress'),
				evt('textArea#textArea', 'keyup'),
			// user presses enter
			evt('textArea#textArea', 'keydown'),evt('textArea#textArea', 'keypress'),
		  		evt('textArea#textArea', 'keyup'),
			// user types '3'
			evt('textArea#textArea', 'keydown'),evt('textArea#textArea', 'keypress'),
				evt('textArea#textArea', 'keyup'),
			// user types '4'
			evt('textArea#textArea', 'keydown'),evt('textArea#textArea', 'keypress'),
				evt('textArea#textArea', 'keyup')
		]);	
		then("test.page.(textArea#textArea).value = '12\n34'");
			and("test.page.(textArea#textArea).focused = true");
	});
	
});
