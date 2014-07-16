br.component.TestSerializableComponent = function()
{

}
br.Core.implement(br.component.TestSerializableComponent, br.component.Component);
br.Core.implement(br.component.TestSerializableComponent, br.component.Serializable);

br.component.TestSerializableComponent.prototype.setDisplayFrame = function(frame)
{
	// do nothing
};

br.component.TestSerializableComponent.prototype.serialize = function()
{
	// do nothing
};

br.component.TestSerializableComponent.deserialize = function(serializedForm)
{
	return new br.component.TestSerializableComponent();
};


