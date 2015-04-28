function ClockClass() {
	this.timeoutsMade = 0;
	this.scheduledFunctions = {};
	this.nowMillis = 0;
}

ClockClass.prototype.install = function() {
	this.installed = true;
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
	this.installed = false;
	setTimeout = this.origSetTimeout;
	setInterval= this.origSetInterval;
	clearTimeout = this.origClearTimeout;
	clearInterval = this.origClearInteval;
};

ClockClass.prototype.reset = function() {
		this._verifyInstalled();
		
		this.scheduledFunctions = {};
		this.nowMillis = 0;
		this.timeoutsMade = 0;
};


ClockClass.prototype.tick = function(millis) {
		this._verifyInstalled();
		
		var oldMillis = this.nowMillis;
		var newMillis = oldMillis + millis;
		this.runFunctionsWithinRange(oldMillis, newMillis);
		this.nowMillis = newMillis;
};

ClockClass.prototype.runFunctionsWithinRange = function(oldMillis, nowMillis) {
		this._verifyInstalled();

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
		this._verifyInstalled();

		Clock.scheduledFunctions[timeoutKey] = {
				runAtMillis: Clock.nowMillis + millis,
				funcToCall: funcToCall,
				recurring: recurring,
				timeoutKey: timeoutKey,
				millis: millis
		};
};

ClockClass.prototype._verifyInstalled = function() {
	if(!this.installed) throw new Error("Clock cannot be used until you've installed it. You should install it in your set-up method, and uninstall it in your tear-down method.");
};

var Clock = new ClockClass();

