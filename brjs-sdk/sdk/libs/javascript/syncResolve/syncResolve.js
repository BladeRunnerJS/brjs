'use strict';

var bluebird = require('bluebird');

function syncResolve(code) {
	var scheduledFuncs = [];
	var origScheduler = bluebird.setScheduler(function(fn) {
		return scheduledFuncs.push(fn);
	});
	var origPromise = window.Promise;

	try {
		window.Promise = bluebird;
		var promise = code();

		var _iteratorNormalCompletion = true;
		var _didIteratorError = false;
		var _iteratorError = undefined;

		try {
			for(var i = 0, l = scheduledFuncs.length; i < l; ++i) {
				var fn = scheduledFuncs[i];
				fn();
			}
		} catch (err) {
			_didIteratorError = true;
			_iteratorError = err;
		} finally {
			try {
				if (!_iteratorNormalCompletion && _iterator.return) {
					_iterator.return();
				}
			} finally {
				if (_didIteratorError) {
					throw _iteratorError;
				}
			}
		}

		return promise.value();
	} finally {
		bluebird.setScheduler(origScheduler);
		window.Promise = origPromise;
	}
}
