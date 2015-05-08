require('jsunitextensions');

ClockUninstallWhenNotPreviouslyInstalledTest = TestCase('ClockUninstallWhenNotPreviouslyInstalledTest').prototype;

ClockUninstallWhenNotPreviouslyInstalledTest.testThatErrorIsThrownWhenClockIsUninstalledWithoutBeingInstalledFirst = function() {
	Clock.uninstall();
	assertException("Clock can no longer be used until you've installed it. You should install it in your set-up method, and uninstall it in your tear-down method.");
};