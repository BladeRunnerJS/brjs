br.thirdparty('jsunitextensions');
br.thirdparty('mock4js');

PresentationModelTest = TestCase("PresentationModelTest");

PresentationModelTest.prototype.setUp = function()
{
	Mock4JS.addMockSupport(window);
	Mock4JS.clearMocksToVerify();
};

PresentationModelTest.prototype.tearDown = function()
{
	Mock4JS.verifyAllMocks();
};

PresentationModelTest.prototype.test_PrivateMemberVariablesDontHavePathSet = function()
{
	var oPresentationModel = new br.presenter.testing.node.RootPresentationNode();
	
	oPresentationModel._$setPath("");
	if(br.presenter.node.PresentationNode.IGNORE_PRIVATE === true){
		assertEquals("1a", undefined, oPresentationModel.m_oPrivateProperty1.getPath());
	}else{
		assertEquals("1b", "m_oPrivateProperty1", oPresentationModel.m_oPrivateProperty1.getPath());
	}
	assertEquals("1c", "child.grandchild", oPresentationModel.child.grandchild.getPath());
};

PresentationModelTest.prototype.test_FailIfWeHaveTwoPublicReferenceToTheSamePresentationNodeInAPresentationModel = function()
{
	var oPresentationModel = new br.presenter.testing.node.RootPresentationNode();
	
	oPresentationModel.publicCopyOfPresentationNode = oPresentationModel.child.grandchild;
	
	assertFails("Expected exception for double public reference of same Presentation Node." , function() {
		//When
		oPresentationModel._$setPath("");
	} );
};

PresentationModelTest.prototype.test_getAndSetComponentFrame = function()
{
	var oMockComponentFrame = mock(br.component.Frame);
	var oPresentationModel = new br.presenter.testing.node.RootPresentationNode();
	
	assertUndefined(oPresentationModel.getComponentFrame());
	
	oPresentationModel.setComponentFrame(oMockComponentFrame);
	
	assertEquals(oMockComponentFrame, oPresentationModel.getComponentFrame());
};
