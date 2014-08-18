/**
* @module br/component/SimpleFrame
*/

var br = require('br/Core');
var Frame = require('br/component/Frame');

/**
 * @class
 * @alias module:br/component/SimpleFrame
 */
function SimpleFrame(component, width, height) {
	this.width = width;
	this.height = height;
	this.frameElement = document.createElement("div");
	this.frameElement.className = "component-frame simple";
	component.setDisplayFrame(this);
}
br.extend(SimpleFrame, Frame);

SimpleFrame.prototype.setContent = function(contentElement) {
	this.frameElement.appendChild(contentElement);
};

SimpleFrame.prototype.getElement = function() {
	return this.frameElement;
};

module.exports = SimpleFrame;
