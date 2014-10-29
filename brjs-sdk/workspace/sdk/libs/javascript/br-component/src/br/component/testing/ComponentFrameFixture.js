var br = require('br/Core');
var Fixture = require('br/test/Fixture');

/**
 * @private
 */
function ComponentFrameFixture(oComponentFrame) {
	this.m_oComponentFrame = oComponentFrame;
};

br.extend(ComponentFrameFixture, Fixture);

ComponentFrameFixture.prototype.canHandleProperty = function(sProperty) {
	return sProperty === "isDirty";
};

ComponentFrameFixture.prototype.doThen = function(sPropertyName, vValue) {
	assertEquals('Component frame modified state not as expected.', this.m_oComponentFrame.getComponentModified(), vValue);
};

module.exports = ComponentFrameFixture;
