require('br-presenter/_resources-test-at/html/test-form.html');
'use strict';

/**
 * @module br/test/viewhandler/MouseWheel
 */

var br = require('br/Core');
var Errors = require('br/Errors');
var ViewFixtureHandler = require('br-test/viewhandler/ViewFixtureHandler');
var Utils = require('br-test/Utils');

/**
 * @class
 * @alias module:br/test/viewhandler/MouseWheel
 * @implements module:br/test/viewhandler/ViewFixtureHandler
 * 
 * @classdesc
 * <code>MouseWheel</code> instances of <code>ViewFixtureHandler</code> can be used to trigger <code>mousewheel</code> event for a view element.
 * Example usage:
 * 
 * <pre>when("test.page.(#aRealButton).mouseWheel => true");</pre>
 */
function MouseWheel() {
}
br.implement(MouseWheel, ViewFixtureHandler);

MouseWheel.prototype.set = function(eElement, mValues) {
	var event = "onwheel" in document.createElement("div") ? "wheel" : // Modern browsers support "wheel"
              document.onmousewheel !== undefined ? "mousewheel" : // Webkit and IE support at least "mousewheel"
              "DOMMouseScroll"; // let's assume that remaining browsers are older Firefox

	Utils.fireMouseEvent(eElement, 'wheel', mValues);
};

MouseWheel.prototype.get = function(eElement) {
	throw new Errors.InvalidTestError("The mouseWheel event cannot be used in a doGiven or doThen");
};

module.exports = MouseWheel;
