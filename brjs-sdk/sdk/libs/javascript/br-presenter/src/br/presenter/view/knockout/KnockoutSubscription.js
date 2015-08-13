'use strict';

/**
 * @module br/presenter/view/knockout/KnockoutSubscription
 */

/**
 * @private
 * @class
 * @alias module:br/presenter/view/knockout/KnockoutSubscription
 */
function KnockoutSubscription(fBoundCallback, oKnockoutObservable, event) {
	this.event = event;
	this.target = oKnockoutObservable;
	this.m_fBoundCallback = fBoundCallback;
}

/**
 * @private
 */
KnockoutSubscription.prototype.dispose = function() {
	this.target.removeKnockoutListener(this.m_fBoundCallback, this.event);
};

module.exports = KnockoutSubscription;

