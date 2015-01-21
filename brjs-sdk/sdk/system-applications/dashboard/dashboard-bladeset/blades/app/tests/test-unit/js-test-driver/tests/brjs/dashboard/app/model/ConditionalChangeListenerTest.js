ConditionalChangeListenerTest = TestCase("ConditionalChangeListenerTest");

ConditionalChangeListenerTest.prototype.setUp = function()
{
	this.m_oChangeProperty = new br.presenter.property.WritableProperty(99);
	this.m_oChangeNodeList = new br.presenter.node.NodeList([new br.presenter.node.PresentationNode()]);
	this.m_oConditionProperty = new br.presenter.property.WritableProperty(true);
	this.m_nChangeInvocationCount = 0;
};

ConditionalChangeListenerTest.prototype.getListener = function(vConditionValue)
{
	return new brjs.dashboard.app.model.ConditionalChangeListener(this, "onChange", this.m_oConditionProperty, vConditionValue);
};

ConditionalChangeListenerTest.prototype.onChange = function()
{
	this.m_nChangeInvocationCount++;
};

ConditionalChangeListenerTest.prototype.testThatInvocationCountIsZeroToBeginWith = function()
{
	assertEquals("1a", 0, this.m_nChangeInvocationCount);
};

ConditionalChangeListenerTest.prototype.testAddingAChangeListenerDoesntCauseAChangeInvocation = function()
{
	this.m_oChangeProperty.addListener(this.getListener(true));
	assertEquals(0, this.m_nChangeInvocationCount);
};

ConditionalChangeListenerTest.prototype.testAddingANodeListChangeListenerDoesntCauseAChangeInvocation = function()
{
	this.m_oChangeNodeList.addListener(this.getListener(true));
	assertEquals(0, this.m_nChangeInvocationCount);
};

ConditionalChangeListenerTest.prototype.testThatAnImmediateChangeInvocationOccursIfTheInvokeImmediatelyFlagIsSet = function()
{
	this.m_oChangeProperty.addListener(this.getListener(true), true);
	assertEquals(1, this.m_nChangeInvocationCount);
};

ConditionalChangeListenerTest.prototype.testThatAnImmediateChangeInvocationHasNoEffectIfTheConditionPropertyIsNotMet = function()
{
	this.m_oConditionProperty.setValue(false);
	this.m_oChangeProperty.addListener(this.getListener(true), true);
	assertEquals(0, this.m_nChangeInvocationCount);
};

ConditionalChangeListenerTest.prototype.testThatSettingTheSameValueAsBeforeHasNoEffect = function()
{
	this.m_oChangeProperty.addListener(this.getListener(true));
	
	this.m_oChangeProperty.setValue(99);
	assertEquals(0, this.m_nChangeInvocationCount);
};

ConditionalChangeListenerTest.prototype.testThatChangesToThePropertyWhileTheConditionPropertyIsMetIncreasesInvocationCount = function()
{
	this.m_oChangeProperty.addListener(this.getListener(true));
	
	this.m_oChangeProperty.setValue(100);
	assertEquals(1, this.m_nChangeInvocationCount);
};

ConditionalChangeListenerTest.prototype.testThatChangesToTheNodeListWhileTheConditionPropertyIsMetIncreasesInvocationCount = function()
{
	this.m_oChangeNodeList.addListener(this.getListener(true));
	
	this.m_oChangeNodeList.updateList([]);
	assertEquals(1, this.m_nChangeInvocationCount);
};

ConditionalChangeListenerTest.prototype.testThatChangesToThePropertyWhileTheConditionPropertyAreNotMetAreIgnored = function()
{
	this.m_oChangeProperty.addListener(this.getListener(true));
	
	this.m_oConditionProperty.setValue(false);
	this.m_oChangeProperty.setValue(100);
	assertEquals(0, this.m_nChangeInvocationCount);
};

ConditionalChangeListenerTest.prototype.testThatIgnoredChangesAreSentOnceTheConitionIsMetAgain = function()
{
	this.m_oChangeProperty.addListener(this.getListener(true));
	
	this.m_oConditionProperty.setValue(false);
	this.m_oChangeProperty.setValue(100);
	this.m_oConditionProperty.setValue(true);
	assertEquals(1, this.m_nChangeInvocationCount);
};

ConditionalChangeListenerTest.prototype.testThatOfflineChangeInvocationStillFiresForValuesThatCanBeCooercedToTheSameValue = function()
{
	this.m_oChangeProperty.setValue(null);
	this.m_oChangeProperty.addListener(this.getListener(true));
	
	this.m_oConditionProperty.setValue(false);
	this.m_oChangeProperty.setValue(undefined);
	this.m_oConditionProperty.setValue(true);
	assertEquals(1, this.m_nChangeInvocationCount);
};

