function ClockClass() {
	this.timeoutsMade = 0;
	this.scheduledFunctions = {};
	this.nowMillis = 0;
}

ClockClass.prototype.install = function() {
	this.origSetTimeout = setTimeout;
	this.origSetInterval = setInterval;
	this.origClearTimeout = clearTimeout;
	this.origClearInteval = clearInterval;
	
	setTimeout = function(funcToCall, millis) {
			Clock.timeoutsMade = Clock.timeoutsMade + 1;
			Clock.scheduleFunction(Clock.timeoutsMade, funcToCall, millis, false);
			return Clock.timeoutsMade;
	};

	setInterval = function(funcToCall, millis) {
			Clock.timeoutsMade = Clock.timeoutsMade + 1;
			Clock.scheduleFunction(Clock.timeoutsMade, funcToCall, millis, true);
			return Clock.timeoutsMade;
	};

	clearTimeout = function(timeoutKey) {
			Clock.scheduledFunctions[timeoutKey] = undefined;
	};

	clearInterval = function(timeoutKey) {
			Clock.scheduledFunctions[timeoutKey] = undefined;
	};
};

ClockClass.prototype.uninstall = function() {
	setTimeout = this.origSetTimeout;
	setInterval= this.origSetInterval;
	clearTimeout = this.origClearTimeout;
	clearInterval = this.origClearInteval;
};

ClockClass.prototype.reset = function() {
		this.scheduledFunctions = {};
		this.nowMillis = 0;
		this.timeoutsMade = 0;
};


ClockClass.prototype.tick = function(millis) {
		var oldMillis = this.nowMillis;
		var newMillis = oldMillis + millis;
		this.runFunctionsWithinRange(oldMillis, newMillis);
		this.nowMillis = newMillis;
};

ClockClass.prototype.runFunctionsWithinRange = function(oldMillis, nowMillis) {
		var scheduledFunc;
		var funcsToRun = [];
		for (var timeoutKey in this.scheduledFunctions) {
				scheduledFunc = this.scheduledFunctions[timeoutKey];
				if (scheduledFunc != undefined &&
						scheduledFunc.runAtMillis >= oldMillis &&
						scheduledFunc.runAtMillis <= nowMillis) {
						funcsToRun.push(scheduledFunc);
						this.scheduledFunctions[timeoutKey] = undefined;
				}
		}

		if (funcsToRun.length > 0) {
				funcsToRun.sort(function(a, b) {
						return a.runAtMillis - b.runAtMillis;
				});
				for (var i = 0; i < funcsToRun.length; ++i) {
						try {
								this.nowMillis = funcsToRun[i].runAtMillis;
								funcsToRun[i].funcToCall();
								if (funcsToRun[i].recurring) {
										Clock.scheduleFunction(funcsToRun[i].timeoutKey,
														funcsToRun[i].funcToCall,
														funcsToRun[i].millis,
														true);
								}
						} catch(e) {
						}
				}
				this.runFunctionsWithinRange(oldMillis, nowMillis);
		}
};

ClockClass.prototype.scheduleFunction = function(timeoutKey, funcToCall, millis, recurring) {
		Clock.scheduledFunctions[timeoutKey] = {
				runAtMillis: Clock.nowMillis + millis,
				funcToCall: funcToCall,
				recurring: recurring,
				timeoutKey: timeoutKey,
				millis: millis
		};
};

var Clock = new ClockClass();

