var brCore = require("br/Core");
var WritableProperty = require("br/presenter/property/WritableProperty");
var PropertyListener = require("br/presenter/property/PropertyListener");

var KnockoutPropertySubclass = function()
{
	// call super constructor
	WritableProperty.call(this);
	
	this.m_nUpdateCounter = 0;
	this.m_vViewValue = null;
	
	this.addListener(this);
};
brCore.extend(KnockoutPropertySubclass, WritableProperty);
brCore.inherit(KnockoutPropertySubclass, PropertyListener);

KnockoutPropertySubclass.prototype.onPropertyUpdated = function()
{
	this.m_nUpdateCounter++;
};

KnockoutPropertySubclass.prototype.notifySubscribers = function(vValue, sEvent)
{
	this.m_vViewValue = vValue;
};

module.exports = KnockoutPropertySubclass;
