(function() {
    var GwtTestRunner = require('br/test/GwtTestRunner');
    var TestUtils = require('br/test/TestUtils');
    var EventLogging = require('br/test/EventLogging');
    // TODO: fixtures() needs to be made CommonJS friendly
    window.ViewFixtureTestsFixtureFactory = require('br/test/ViewFixtureTestsFixtureFactory');

    GwtTestRunner.initialize();

    describe("User performs text events", function() {
        fixtures('ViewFixtureTestsFixtureFactory');
        
        beforeEach(function() {
            TestUtils.setupPage()
        });
        
        afterEach(function() {
            TestUtils.tearDownPage();
        });

        it("simulates a user typing 1234 into a textbox", function() {
            given("page.loaded = true");
                and("test.page.(input#textInput).value = ''");
                and("test.page.(input#textInput).clicked = true");
                and("test.page.(input#textInput).focused = true");
            EventLogging.clearEvents()
            when("test.page.(input#textInput).typedValue => '1234'");
            EventLogging.verifyEvents([ 
                // user types '1'
                EventLogging.evt('input#textInput', 'keydown'),EventLogging.evt('input#textInput', 'keypress'),
                EventLogging.evt('input#textInput', 'keyup'),
                // user types '2'
                EventLogging.evt('input#textInput', 'keydown'),EventLogging.evt('input#textInput', 'keypress'),
                EventLogging.evt('input#textInput', 'keyup'),
                // user types '3'
                EventLogging.evt('input#textInput', 'keydown'),EventLogging.evt('input#textInput', 'keypress'),
                EventLogging.evt('input#textInput', 'keyup'),
                // user types '4'
                EventLogging.evt('input#textInput', 'keydown'),EventLogging.evt('input#textInput', 'keypress'),
                EventLogging.evt('input#textInput', 'keyup') 
            ]);
            then("test.page.(input#textInput).value = '1234'");
                and("test.page.(input#textInput).focused = true");
                EventLogging.clearEvents()
        });
        
        it("simulates a user typing into a textbox and selecting another element", function() {
            given("test.continuesFrom = 'simulates a user typing 1234 into a textbox'");
            when("test.page.(input#anotherTextInput).clicked => true");		
            EventLogging.verifyEventsUnordered([ 
                EventLogging.evt('input#textInput', 'change'),
                EventLogging.evt('input#anotherTextInput', 'focusin')
            ])
            then("test.page.(input#textInput).focused = false");
                and("test.page.(input#anotherTextInput).focused = true");
        });
        
        it("simulates a user typing into a textbox, tabbing to another element and typing more", function() {
            given("test.continuesFrom = 'simulates a user typing 1234 into a textbox'");
            when("test.page.(input#textInput).typedValue => '\t'");	
                and("test.page.(input#anotherTextInput).typedValue => '5'");		
                EventLogging.verifyEventsUnordered([ 
                EventLogging.evt('input#textInput', 'keydown'),
                EventLogging.evt('input#textInput', 'keypress'),
                EventLogging.evt('input#textInput', 'keyup'),
                EventLogging.evt('input#textInput', 'change'),
                EventLogging.evt('input#anotherTextInput', 'focusin'),
                EventLogging.evt('input#anotherTextInput', 'keydown'),
                EventLogging.evt('input#anotherTextInput', 'keypress'),
                EventLogging.evt('input#anotherTextInput', 'keyup')
            ])
            then("test.page.(input#textInput).focused = false");
                and("test.page.(input#anotherTextInput).focused = true");
        });
        
        it("simulates a user typing multiline text into a text area", function() {
            given("page.loaded = true");
                and("test.page.(textArea#textArea).value = ''");
                and("test.page.(textArea#textArea).focused = true");
            EventLogging.clearEvents()
            when("test.page.(textArea#textArea).typedValue => '12'");
                and("test.page.(textArea#textArea).typedValue => '\n'");		
                and("test.page.(textArea#textArea).typedValue => '34'");		
                EventLogging.verifyEvents([ 
                // user types '1'
                EventLogging.evt('textArea#textArea', 'keydown'),EventLogging.evt('textArea#textArea', 'keypress'),
                EventLogging.evt('textArea#textArea', 'keyup'),
                // user types '2'
                EventLogging.evt('textArea#textArea', 'keydown'),EventLogging.evt('textArea#textArea', 'keypress'),
                EventLogging.evt('textArea#textArea', 'keyup'),
                // user presses enter
                EventLogging.evt('textArea#textArea', 'keydown'),EventLogging.evt('textArea#textArea', 'keypress'),
                EventLogging.evt('textArea#textArea', 'keyup'),
                // user types '3'
                EventLogging.evt('textArea#textArea', 'keydown'),EventLogging.evt('textArea#textArea', 'keypress'),
                EventLogging.evt('textArea#textArea', 'keyup'),
                // user types '4'
                EventLogging.evt('textArea#textArea', 'keydown'),EventLogging.evt('textArea#textArea', 'keypress'),
                EventLogging.evt('textArea#textArea', 'keyup')
            ]);	
            then("test.page.(textArea#textArea).value = '12\n34'");
                and("test.page.(textArea#textArea).focused = true");
        });
        
    });
})();
