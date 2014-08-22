PresentationModelFixtureTest = TestCase("PresentationModelFixtureTest");

/* -----------		 Properties	   --------------*/

PresentationModelFixtureTest.prototype.test_editablePropertyNodesCanBeReadFromAndWrittenTo = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});
	
	oPresentationModel.editableProperty = new br.presenter.property.EditableProperty();
	assertEquals("1a", undefined, oPresentationModel.editableProperty.getValue());
	
	oPresentationModelFixture.doGiven("editableProperty", "Hello World!");
	assertEquals("2a", "Hello World!", oPresentationModel.editableProperty.getValue());
	
	oPresentationModelFixture.doWhen("editableProperty", 42);
	assertEquals("3a", 42, oPresentationModel.editableProperty.getValue());
	
	oPresentationModelFixture.doThen("editableProperty", 42);
	
	assertAssertError(function(){
		oPresentationModelFixture.doThen("editableProperty", 99);
	});
};

PresentationModelFixtureTest.prototype.test_nonEditablePropertyNodesCanBeReadFrom = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});
	
	oPresentationModel.property = new br.presenter.property.WritableProperty("Hello World!");
	assertEquals("1a", "Hello World!", oPresentationModel.property.getValue());
	
	oPresentationModelFixture.doThen("property", "Hello World!");
	
	assertAssertError(function(){
		oPresentationModelFixture.doThen("property", "some other text");
	});
};

PresentationModelFixtureTest.prototype.test_readOnlyPropertyNodesCanBeWrittenToViaFixture = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});

	oPresentationModel.readOnlyProperty = new br.presenter.property.Property("Hello World!");

	assertEquals("1a", "Hello World!", oPresentationModel.readOnlyProperty.getValue());
	oPresentationModelFixture.doGiven("readOnlyProperty", "Hello World!");
	oPresentationModelFixture.doWhen("readOnlyProperty", "some other text");
	oPresentationModelFixture.doThen("readOnlyProperty", "some other text");
	assertEquals("1b", "some other text", oPresentationModel.readOnlyProperty.getValue());
};

PresentationModelFixtureTest.prototype.test_nonPropertyNodesCantBeReadFromOrWrittenTo = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});
	
	oPresentationModel.property = "Hello World!";
	
	assertException(function(){
		oPresentationModelFixture.doGiven("property", "new value");
	}, br.Errors.INVALID_TEST);
	
	assertException(function(){
		oPresentationModelFixture.doWhen("property", "new value");
	}, br.Errors.INVALID_TEST);
	
	assertException(function(){
		oPresentationModelFixture.doThen("property", "Hello World!");
	}, br.Errors.INVALID_TEST);
};

/* -----------		Options Node Lists		--------------*/

PresentationModelFixtureTest.prototype.test_tryingToReadANonExistentOptionFromAnOptionsNodeListThrowsException = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});

	var oOption1 = new br.presenter.node.Option("a", "a-label");

	oPresentationModel.optionsNodeList = new br.presenter.node.OptionsNodeList([oOption1]);

	oPresentationModelFixture.doThen("optionsNodeList[0].label", "a-label");
	assertException(function(){
		oPresentationModelFixture.doThen("optionsNodeList[1].label", "no-value");
	}, br.Errors.INVALID_TEST);
};

PresentationModelFixtureTest.prototype.test_optionsNodeListsCanUpdatedThroughGranularUpdatesOnItsIndividualOptions = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});

	var oOption1 = new br.presenter.node.Option("a", "a-label");
	var oOption2 = new br.presenter.node.Option("b", "b-label");

	oPresentationModel.optionsNodeList = new br.presenter.node.OptionsNodeList([oOption1, oOption2]);

	oPresentationModelFixture.doThen("optionsNodeList[0].label", "a-label");
	oPresentationModelFixture.doThen("optionsNodeList[1].value", "b");

	oPresentationModelFixture.doGiven("optionsNodeList[0].label", "another-label");
	oPresentationModelFixture.doGiven("optionsNodeList[1].value", "another-value");
	oPresentationModelFixture.doThen("optionsNodeList[0].label", "another-label");
	oPresentationModelFixture.doThen("optionsNodeList[1].value", "another-value");

	assertAssertError(function(){
		oPresentationModelFixture.doThen("optionsNodeList[0].label", "a-label");
	});
	assertAssertError(function(){
		oPresentationModelFixture.doThen("optionsNodeList[1].value", "b");
	});
};

PresentationModelFixtureTest.prototype.test_optionsNodeListsCanUpdatedWithAnArrayGivenToNodeList = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});

	var oOption1 = new br.presenter.node.Option("a", "a-label");
	var oOption2 = new br.presenter.node.Option("b", "b-label");

	oPresentationModel.optionsNodeList = new br.presenter.node.OptionsNodeList([oOption1, oOption2]);

	oPresentationModelFixture.doThen("optionsNodeList", ["a-label", "b-label"]);

	oPresentationModelFixture.doGiven("optionsNodeList", ["c", "d"]);
	oPresentationModelFixture.doThen("optionsNodeList", ["c", "d"]);

	assertAssertError(function(){
		oPresentationModelFixture.doThen("optionsNodeList", ["a-label", "b-label"]);
	});
};

/* -----------		Node Lists		--------------*/

PresentationModelFixtureTest.prototype.test_nodeListsCanBeNavigatedUsingDottedNotation = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});
	
	var oNode1 = new br.presenter.node.PresentationNode();
	oNode1.property = new br.presenter.property.WritableProperty("property #1");
	var oNode2 = new br.presenter.node.PresentationNode();
	oNode2.property = new br.presenter.property.WritableProperty("property #2");
	
	oPresentationModel.nodeList = new br.presenter.node.NodeList([oNode1, oNode2]);
	
	oPresentationModelFixture.doThen("nodeList.0.property", "property #1");
	oPresentationModelFixture.doThen("nodeList.1.property", "property #2");
	
	assertAssertError(function(){
		oPresentationModelFixture.doThen("nodeList.0.property", "property #2");
	});
};

PresentationModelFixtureTest.prototype.test_nodeListsCanBeNavigatedUsingSquareBracketNotation = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});
	
	var oNode1 = new br.presenter.node.PresentationNode();
	oNode1.property = new br.presenter.property.WritableProperty("property #1");
	var oNode2 = new br.presenter.node.PresentationNode();
	oNode2.property = new br.presenter.property.WritableProperty("property #2");
	
	oPresentationModel.nodeList = new br.presenter.node.NodeList([oNode1, oNode2]);
	
	oPresentationModelFixture.doThen("nodeList[0].property", "property #1");
	oPresentationModelFixture.doThen("nodeList[1].property", "property #2");
	
	assertAssertError(function(){
		oPresentationModelFixture.doThen("nodeList[0].property", "property #2");
	});
};

PresentationModelFixtureTest.prototype.test_nodeListLengthCanBeChecked = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});
	
	var oNode1 = new br.presenter.node.PresentationNode();
	oNode1.property = new br.presenter.property.WritableProperty("property #1");
	var oNode2 = new br.presenter.node.PresentationNode();
	oNode2.property = new br.presenter.property.WritableProperty("property #2");
	
	oPresentationModel.nodeList = new br.presenter.node.NodeList([oNode1, oNode2]);
	
	oPresentationModelFixture.doThen("nodeList.length", 2);
	
	assertAssertError(function(){
		oPresentationModelFixture.doThen("nodeList.length", 1);
	});
	
	assertAssertError(function(){
		oPresentationModelFixture.doThen("nodeList.length", 3);
	});
};

PresentationModelFixtureTest.prototype.test_nodeListsCanBeGrownByIncreasingTheNodeListLength = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});
	
	var oField1 = new br.presenter.node.Field();
	var oField2 = new br.presenter.node.Field();
	oPresentationModel.nodeList = new br.presenter.node.NodeList([oField1, oField2], br.presenter.node.Field);
	
	assertEquals("1a", [oField1, oField2], oPresentationModel.nodeList.getPresentationNodesArray());
	
	oPresentationModelFixture.doGiven("nodeList.length", 3);
	
	assertEquals("2a", 3, oPresentationModel.nodeList.getPresentationNodesArray().length);
	assertTrue("2b", oPresentationModel.nodeList.getPresentationNodesArray()[2] instanceof br.presenter.node.Field);
};

PresentationModelFixtureTest.prototype.test_nodeListsCanBeShrunkByDecreasingTheNodeListLength = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});
	
	var oField1 = new br.presenter.node.Field();
	var oField2 = new br.presenter.node.Field();
	oPresentationModel.nodeList = new br.presenter.node.NodeList([oField1, oField2], br.presenter.node.Field);
	
	assertEquals("1a", [oField1, oField2], oPresentationModel.nodeList.getPresentationNodesArray());
	
	oPresentationModelFixture.doGiven("nodeList.length", 1);
	
	assertEquals("2a", [oField1], oPresentationModel.nodeList.getPresentationNodesArray());
};

PresentationModelFixtureTest.prototype.test_nodesWithinNodeListsCanBeUpdated = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});
	
	var oNode = new br.presenter.node.PresentationNode();
	oNode.property = new br.presenter.property.WritableProperty("initial value");
	
	oPresentationModel.nodeList = new br.presenter.node.NodeList([oNode]);
	
	oPresentationModelFixture.doThen("nodeList[0].property", "initial value");
	
	oPresentationModelFixture.doGiven("nodeList[0].property", "updated value");
	oPresentationModelFixture.doThen("nodeList[0].property", "updated value");
	
	assertAssertError(function(){
		oPresentationModelFixture.doThen("nodeList[0].property", "initial value");
	});
};

PresentationModelFixtureTest.prototype.test_nodeListsCanBeCheckedWithAnArrayGivenToNodeList = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});

	var oValue1 = new br.presenter.node.DisplayField("value1");
	var oValue2 = new br.presenter.node.DisplayField("value2");

	oPresentationModel.nodeList = new br.presenter.node.NodeList([oValue1, oValue2]);

	oPresentationModelFixture.doThen("nodeList", ["value1", "value2"]);

	assertAssertError(function(){
		oPresentationModelFixture.doThen("nodeList", ["value1", "wrong-value"]);
	});
};

PresentationModelFixtureTest.prototype.test_errorIsThrownIfANodeListIsNotComparedAgainstAnArray = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});

	var oValue1 = new br.presenter.node.DisplayField("value1");
	var oValue2 = new br.presenter.node.DisplayField("value2");

	oPresentationModel.nodeList = new br.presenter.node.NodeList([oValue1, oValue2]);

	assertException(function(){
		oPresentationModelFixture.doThen("nodeList", "value1");
	}, br.Errors.INVALID_TEST);
};

PresentationModelFixtureTest.prototype.test_errorIsThrownIfANodeListContainsAtLeastOneNodeWithoutLabelOrValueProperty = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});

	var oValue1 = new br.presenter.node.PresentationNode();
	var oValue2 = new br.presenter.node.PresentationNode();

	oPresentationModel.nodeList = new br.presenter.node.NodeList([oValue1, oValue2]);

	assertException(function(){
		oPresentationModelFixture.doThen("nodeList", ["value1", "value2"]);
	}, br.Errors.INVALID_TEST);
	
	oValue1 = new br.presenter.node.DisplayField("value1");
	oValue2 = new br.presenter.node.PresentationNode();
	
	oPresentationModel.nodeList = new br.presenter.node.NodeList([oValue1, oValue2]);
	
	assertException(function(){
		oPresentationModelFixture.doThen("nodeList", ["value1", "value2"]);
	}, br.Errors.INVALID_TEST);
};

/* -----------		Mapped Node Lists		--------------*/

PresentationModelFixtureTest.prototype.test_nodesWithinMappedNodeListsCanBeUpdated = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});
	
	var oNode = new br.presenter.node.PresentationNode();
	oNode.property = new br.presenter.property.WritableProperty("initial value");
	
	oPresentationModel.nodeList = new br.presenter.node.MappedNodeList({'node1':oNode});
	
	oPresentationModelFixture.doThen("nodeList.node1.property", "initial value");
	
	oPresentationModelFixture.doGiven("nodeList.node1.property", "updated value");
	oPresentationModelFixture.doThen("nodeList.node1.property", "updated value");
	
	assertAssertError(function(){
		oPresentationModelFixture.doThen("nodeList.node1.property", "initial value");
	});
};

/* -----------		Nested Properties		--------------*/

PresentationModelFixtureTest.prototype.test_nestedPropertyNodesWorkCorrectly = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});
	
	oPresentationModel.property = new br.presenter.node.Field( "the value");
	oPresentationModel.property.label.setValue("the label");
	assertEquals("1a", "the value", oPresentationModel.property.value.getValue());
	
	oPresentationModelFixture.doThen("property.label", "the label");
	oPresentationModelFixture.doThen("property.value", "the value");
	
	oPresentationModelFixture.doGiven("property.value", "new value");
	assertEquals("1a", "the label", oPresentationModel.property.label.getValue());
	assertEquals("1b", "new value", oPresentationModel.property.value.getValue());
	
	oPresentationModelFixture.doThen("property.label", "the label");
	oPresentationModelFixture.doThen("property.value", "new value");
};

/* -----------		invoked	  --------------*/

PresentationModelFixtureTest.prototype.test_methodsCanBeInvoked = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});
	
	var nInvocationCount = 0;
	oPresentationModel.theMethod = function() {
		nInvocationCount++;
	};
	
	assertEquals("1a", 0, nInvocationCount);
	
	oPresentationModelFixture.doGiven("theMethod.invoked", true);
	assertEquals("2a", 1, nInvocationCount);
	
	oPresentationModelFixture.doWhen("theMethod.invoked", true);
	assertEquals("3a", 2, nInvocationCount);
	
};

PresentationModelFixtureTest.prototype.test_nonMethodsCannotBeInvoked = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});
	
	var nInvocationCount = 0;
	oPresentationModel.theMethod = function() {
		nInvocationCount++;
	};
	
	assertException(function(){
		oPresentationModelFixture.doWhen("theOtherMethod.invoked", false);
	}, br.Errors.INVALID_TEST);
	
	assertEquals(0, nInvocationCount);
};

PresentationModelFixtureTest.prototype.test_methodInvocationOnlyWorksIfYouSetTrue = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});
	
	var nInvocationCount = 0;
	oPresentationModel.theMethod = function() {
		nInvocationCount++;
	};
	
	assertException(function(){
		oPresentationModelFixture.doWhen("theMethod.invoked", false);
	}, br.Errors.INVALID_TEST);
	
	assertException(function(){
		oPresentationModelFixture.doWhen("theMethod.invoked", "true");
	}, br.Errors.INVALID_TEST);
	
	assertEquals("2a", 0, nInvocationCount);
	
	oPresentationModelFixture.doWhen("theMethod.invoked", true);
	assertEquals("3a", 1, nInvocationCount);
};

PresentationModelFixtureTest.prototype.test_methodInvocationFailsForThen = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});
	
	oPresentationModel.theMethod = function() {
	};
	
	assertException(function(){
		oPresentationModelFixture.doThen("theMethod.invoked", true);
	}, br.Errors.INVALID_TEST);
	oPresentationModelFixture.doGiven("theMethod.invoked", true);
	oPresentationModelFixture.doWhen("theMethod.invoked", true);
};

/* -----------		invocationCount	  --------------*/


PresentationModelFixtureTest.prototype.test_invocationCountCanBeTracked = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});
	
	oPresentationModel.theMethod = function() {
	};
	
	oPresentationModelFixture.doGiven("theMethod.invocationCount", 0);
	oPresentationModelFixture.doThen("theMethod.invocationCount", 0);
	
	oPresentationModelFixture.doGiven("theMethod.invoked", true);
	oPresentationModelFixture.doThen("theMethod.invocationCount", 1);
	
	oPresentationModelFixture.doGiven("theMethod.invoked", true);
	oPresentationModelFixture.doThen("theMethod.invocationCount", 2);
	
	assertAssertError(function(){
		oPresentationModelFixture.doThen("theMethod.invocationCount", 1);
	});
	
	assertAssertError(function(){
		oPresentationModelFixture.doThen("theMethod.invocationCount", 3);
	});
};

PresentationModelFixtureTest.prototype.test_invocationCountCanBeSetToANonZeroValue = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});
	
	oPresentationModel.theMethod = function() {
	};
	
	oPresentationModelFixture.doGiven("theMethod.invocationCount", -10);
	oPresentationModelFixture.doThen("theMethod.invocationCount", -10);
	
	oPresentationModelFixture.doGiven("theMethod.invoked", true);
	oPresentationModelFixture.doThen("theMethod.invocationCount", -9);
};

PresentationModelFixtureTest.prototype.test_invocationCountMustBeInitializedInGivenOrWhenFirst = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});
	
	oPresentationModel.theMethod = function() {
	};
	
	assertException(function(){
		oPresentationModelFixture.doThen("theMethod.invocationCount", 0);
	}, br.Errors.INVALID_TEST);
};

PresentationModelFixtureTest.prototype.test_invocationCountMustBeInitializedToANumber = function()
{
	var oPresentationModelFixture = new br.presenter.testing.PresentationModelFixture();
	var oPresentationModel = new br.presenter.PresentationModel();
	oPresentationModelFixture.setComponent({getPresentationModel:function(){return oPresentationModel;}});
	
	oPresentationModel.theMethod = function() {
	};
	
	assertException(function(){
		oPresentationModelFixture.doGiven("theMethod.invocationCount", "x10");
	}, br.Errors.INVALID_TEST);
	
	assertException(function(){
		oPresentationModelFixture.doGiven("theMethod.invocationCount", "1x0");
	}, br.Errors.INVALID_TEST);
	
	assertException(function(){
		oPresentationModelFixture.doGiven("theMethod.invocationCount", "10x");
	}, br.Errors.INVALID_TEST);
};
