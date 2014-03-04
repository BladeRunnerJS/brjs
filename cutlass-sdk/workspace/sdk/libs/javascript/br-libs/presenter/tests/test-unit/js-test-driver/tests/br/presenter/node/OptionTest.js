br.Core.thirdparty('mock4js');

OptionTest = TestCase("OptionTest");

OptionTest.prototype.setUp = function()
{
	Mock4JS.addMockSupport(window);
	Mock4JS.clearMocksToVerify();
};

OptionTest.prototype.tearDown = function()
{
	Mock4JS.verifyAllMocks();
};

OptionTest.prototype.test_Construction = function()
{
	var option = new br.presenter.node.Option("key","label");
	
	assertEquals("a1", "key", option.value.getValue());
	assertEquals("a2", "label", option.label.getValue());
	assertEquals("a3", "label", option.toString());
};
