'use strict';

Object.defineProperty(exports, "__esModule", {
	value: true
});
exports.default = syncResolve;

var _bluebird = require('bluebird');

var _bluebird2 = _interopRequireDefault(_bluebird);

function _interopRequireDefault(obj) {
	return obj && obj.__esModule ? obj : {
		default: obj
	};
}

function syncResolve(code) {
	var scheduledFuncs = [];
	var origScheduler = _bluebird2.default.setScheduler(function(fn) {
		return scheduledFuncs.push(fn);
	});
	var origPromise = global.Promise;

	try {
		global.Promise = _bluebird2.default;
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
		_bluebird2.default.setScheduler(origScheduler);
		global.Promise = origPromise;
	}
}