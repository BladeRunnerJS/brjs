var br = require("br/Core");
var Component = require('br-component/Component');
var Serializable = require('br-component/Serializable');


var TestSerializableComponent = function()
{

};

br.implement(TestSerializableComponent, Component);
br.implement(TestSerializableComponent, Serializable);

TestSerializableComponent.prototype.setDisplayFrame = function(frame)
{
	// do nothing
};

TestSerializableComponent.prototype.serialize = function()
{
	// do nothing
};

TestSerializableComponent.deserialize = function(serializedForm)
{
	return new TestSerializableComponent();
};

module.exports = TestSerializableComponent;
