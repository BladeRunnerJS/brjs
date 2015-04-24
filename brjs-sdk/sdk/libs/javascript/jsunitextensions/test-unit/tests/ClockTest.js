require('jsunitextensions');

ClockTest = TestCase('ClockTest').prototype;

ClockTest.setUp = function() {
	this.timeoutInvoked = false;
	
	setTimeout(function() {
		this.timeoutInvoked = true;
	}.bind(this), 1000);
};

ClockTest.testThatTimersDontFireByDefault = function() {
	assertFalse(this.timeoutInvoked);
};

ClockTest.testThatTimersFireIfEnoughTimeElapses = function() {
	Clock.tick(1000);
	assertTrue(this.timeoutInvoked);
};

ClockTest.testThatTimersFireIfNotEnoughTimeHasElapsed = function() {
	Clock.tick(999);
	assertFalse(this.timeoutInvoked);
};
