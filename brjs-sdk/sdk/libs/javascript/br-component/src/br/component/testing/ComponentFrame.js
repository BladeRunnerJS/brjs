var br = require('br/Core');
var Frame = require('br/component/Frame');

/**
 * @private
 */
function ComponentFrame(fixture) {
	this.m_bIsModified = false;
	this._fixture = fixture;
	Frame.call(this);
};

br.extend(ComponentFrame, Frame);

ComponentFrame.prototype.setContent = function(element) {
	this._fixture._setElement(element);
};

ComponentFrame.prototype.setComponentModified = function(bIsModified) {
	this.m_bIsModified = bIsModified;
};

ComponentFrame.prototype.getComponentModified = function() {
	return this.m_bIsModified;
};

module.exports = ComponentFrame;
