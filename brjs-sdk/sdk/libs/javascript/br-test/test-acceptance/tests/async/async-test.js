var GwtTestRunner = require('br/test/GwtTestRunner');

GwtTestRunner.initialize();

describe("asynchronous testing", function() {
	
var counter, timeCondition;

it("should support async execution of a test", function() {
	
	runs(function() {
		timeCondition = false;
		counter = 0;
	
		setTimeout(function() {
			timeCondition = true;
			}, 10);
		});
		  
		waitsFor(function() {
			counter++;
			return timeCondition;
		}, "counter should be greater that 0", 15);
			  
		runs(function() {
			expect(counter).toBeGreaterThan(0);
		});
	
	});

});
