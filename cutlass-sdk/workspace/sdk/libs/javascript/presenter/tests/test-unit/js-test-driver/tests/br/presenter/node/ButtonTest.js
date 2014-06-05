ButtonTest = TestCase("ButtonTest");

ButtonTest.prototype.test_canConstructAButtonWithAStringAsLabel = function()
{
	var oButton = new br.presenter.node.Button("button");
	assertEquals("button", oButton.label.getValue());
};

ButtonTest.prototype.test_canConstructAButtonWithAPropertyAsLabel = function()
{
	var oLabel = new br.presenter.property.WritableProperty("button");
	var oButton = new br.presenter.node.Button(oLabel);
	assertEquals("button", oButton.label.getValue());
};

ButtonTest.prototype.test_canConstructAButtonWithAReadOnlyPropertyAsLabel = function()
{
	var oLabel = new br.presenter.property.Property("button");
	var oButton = new br.presenter.node.Button(oLabel);
	assertEquals("button", oButton.label.getValue());
};


ButtonTest.prototype.test_canConstructAButtonWithAReadOnlyPropertyAsLabel = function()
{
	var oLabel = new br.presenter.property.Property("button");
	var oButton = new br.presenter.node.Button(oLabel);
	assertEquals("button", oButton.label.getValue());
};
