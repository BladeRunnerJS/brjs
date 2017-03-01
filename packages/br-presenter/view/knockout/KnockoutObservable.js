'use strict';

var ArrayUtility = require('br-util/ArrayUtility');
var KnockoutSubscription = require('br-presenter/view/knockout/KnockoutSubscription');
var Core = require('br/Core');

/**
 * @module br/presenter/view/knockout/KnockoutObservable
 */

var presenter_knockout = require('presenter-knockout');

/**
 * @private
 * @class
 * @alias module:br/presenter/view/knockout/KnockoutObservable
 */
function KnockoutObservable() {
	/** @private */
	this._subscriptions = {};

	/** @private */
	this.__ko_proto__ = presenter_knockout.observable;
}

/**
 * @private
 */
KnockoutObservable.prototype.updateView = function(vValue) {
	this.notifySubscribers(vValue);
};

/**
 * @private
 */
KnockoutObservable.prototype.subscribe = function(callback, callbackTarget, event) {
	var boundCallback = callbackTarget ? callback.bind(callbackTarget) : callback;
	event = event || 'change';

	if (!this._subscriptions[event]) {
		this._subscriptions[event] = [];
	}

	this._subscriptions[event].push(boundCallback);

	return new KnockoutSubscription(boundCallback, this, event);
};

/**
 * @private
 */
KnockoutObservable.prototype.notifySubscribers = function(valueToNotify, event) {
	event = event || 'change';

	if (this._subscriptions[event]) {
		presenter_knockout.dependencyDetection.ignore(function() {
			presenter_knockout.utils.arrayForEach(this._subscriptions[event].slice(0), function(subscription) {
				// In case a subscription was disposed during the arrayForEach cycle, check
				// for isDisposed on each subscription before invoking its callback
				if (subscription && (subscription.isDisposed !== true)) {
					subscription(valueToNotify);
				}
			});
		}, this);
	}
};

/**
 * @private
 */
KnockoutObservable.prototype.getSubscriptionsCount = function() {
	var total = 0;

	for (var eventName in this._subscriptions) {
		if (this._subscriptions.hasOwnProperty(eventName)) {
			total += this._subscriptions[eventName].length;
		}
	}

	return total;
};

/**
 * @private
 */
KnockoutObservable.prototype.removeKnockoutListener = function(boundCallback, event) {
	ArrayUtility.removeItem(this._subscriptions[event], boundCallback);
};

module.exports = KnockoutObservable;
