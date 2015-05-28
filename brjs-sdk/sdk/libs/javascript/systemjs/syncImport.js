// described in <https://github.com/systemjs/systemjs/issues/469#issuecomment-106307372>
System.syncImport = function(name) {
	var scheduledFuncs = [];
	var origScheduler = Promise.setScheduler(function(fn) {
		scheduledFuncs.push(fn);
	});
	
	try {
		var promise = this.import(name);
		
		for(var fn of scheduledFuncs) {
			fn();
		}
		
		var value = promise.value();
	}
	finally {
		Promise.setScheduler(origScheduler);
	}
	
	return value;
};
