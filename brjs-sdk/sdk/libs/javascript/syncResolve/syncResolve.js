'use strict';

var bluebird = require('bluebird');

function syncResolve(code) {
	var scheduledFuncs = [];
	var origScheduler = bluebird.setScheduler(function(fn) {
		return scheduledFuncs.push(fn);
	});
	var origPromise = global.Promise;

	try {
		global.Promise = bluebird;
		var promise = code();

		var _iteratorNormalCompletion = true;
		var _didIteratorError = false;
		var _iteratorError = undefined;

		try {
			for (var _iterator = scheduledFuncs[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
				var fn = _step.value;

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
		global.Promise = origPromise;
	}
}

module.exports = syncResolve;