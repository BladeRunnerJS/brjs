require('jsunitextensions');

ClockTest = TestCase('ClockTest').prototype;

ClockTest.setUp = function() {
	Clock.install();
	
	this.timeoutInvoked = false;
	this.intervalInvoked = false;

	setTimeout(function() {
		this.timeoutInvoked = true;
	}.bind(this), 1000);

	setInterval(function() {
		this.intervalInvoked = true;
	}.bind(this), 2000);
};

ClockTest.tearDown = function() {
	Clock.uninstall();
};

ClockTest.testThatTimersDontFireByDefault = function() {
	assertFalse(this.timeoutInvoked);
};

ClockTest.testThatIntervalsDontFireByDefault = function() {
	assertFalse(this.intervalInvoked);
};

ClockTest.testThatTimersFireIfEnoughTimeElapses = function() {
	Clock.tick(1000);
	assertTrue(this.timeoutInvoked);
};

ClockTest.testThatTimersOnlyFireOnce = function() {
	Clock.tick(1000);
	assertTrue(this.timeoutInvoked);
	this.timeoutInvoked = false;
	Clock.tick(1000);
	assertFalse(this.timeoutInvoked);
};

ClockTest.testThatTimersDontFireIfNotEnoughTimeHasElapsed = function() {
	Clock.tick(999);
	assertFalse(this.timeoutInvoked);
};

ClockTest.testThatTimersAccumulateCorrectly = function() {
	Clock.tick(999);
	assertFalse(this.timeoutInvoked);
	Clock.tick(1);
	assertTrue(this.timeoutInvoked);
};

ClockTest.testThatIntervalsAreFiredAtEachInterval = function () {
	Clock.tick(2000);
	assertTrue(this.intervalInvoked);
	this.intervalInvoked = false;
	Clock.tick(2000);
	assertTrue(this.intervalInvoked);
};

ClockTest.testThatIntervalsDontFireIfNotEnoughTimeHasElapsed = function () {
	Clock.tick(1999);
	assertFalse(this.intervalInvoked);
};

ClockTest.testThatTimersAreResetCorrectly = function() {
	Clock.reset();
	Clock.tick(2000);
	assertFalse(this.timeoutInvoked);
	assertFalse(this.intervalInvoked);
};
