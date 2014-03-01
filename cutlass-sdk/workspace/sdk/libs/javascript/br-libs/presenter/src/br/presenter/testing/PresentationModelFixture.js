/**
 * Constructs a <code>br.presenter.testing.PresentationModelFixture</code>.
 * 
 * @class
 * 
 * The <code>PresentationModelFixture</code> serves to manipulate and verify the state of the presentation
 * model of a presenter component. 
 * 
 * @constructor
 * @implements br.component.testing.ComponentModelFixture
 */
br.presenter.testing.PresentationModelFixture = function()
{
	this._initializePlugins();
};
br.Core.inherit(br.presenter.testing.PresentationModelFixture, br.component.testing.ComponentModelFixture);

/**
 * @private
 */
br.presenter.testing.PresentationModelFixture.prototype._initializePlugins = function()
{
	presenter_ko.bindingHandlers.event = new br.presenter.testing.KnockoutInvocationCountPlugin();
};

// * **********************************************************************************
// *						 ComponentModelFixture interface
// ************************************************************************************

br.presenter.testing.PresentationModelFixture.prototype.setComponent = function(oComponent)
{
	this.m_oPresentationModel = oComponent.getPresentationModel();
};

// ***********************************************************************************
// *							  Fixture interface
// ************************************************************************************

br.presenter.testing.PresentationModelFixture.prototype.canHandleExactMatch = function()
{
	return false;
};

/**
 * The PresentationModelFixture handles all valid properties and methods within the presentation model
 * of a presenter component. Nested property nodes in a presentation model can be referenced by dotted 
 * notation.
 * 
 * @param {String} sProperty The property name to check.
 * 
 * @see br.test.Fixture#canHandleProperty
 */
br.presenter.testing.PresentationModelFixture.prototype.canHandleProperty = function(sProperty)
{
	return true;
};

/**
 * This method enables the fixture to set the value on a presentation node or property within the 
 * presentation model of a presenter component. In addition, it is also possible to trigger the 
 * invocation of a method defined within the presentation model, using the 'invoked' property.
 * 
 * @param {String} sProperty The property name
 * @param {Variant} vValue The value to check.
 * 
 * @see br.test.Fixture#doGiven
 */
br.presenter.testing.PresentationModelFixture.prototype.doGiven = function(sProperty, vValue)
{
	this._doGivenAndDoWhen(sProperty, vValue);
};

/**
 * This method enables the fixture to set the value on a presentation node or property within the 
 * presentation model of a presenter component. In addition, it is also possible to trigger the 
 * invocation of a method defined within the presentation model, using the 'invoked' property.
 * @param {String} sProperty The property name
 * @param {Variant} vValue The value to set.
 * @see br.test.Fixture#doWhen
 */
br.presenter.testing.PresentationModelFixture.prototype.doWhen = function(sProperty, vValue)
{
	this._doGivenAndDoWhen(sProperty, vValue);
};

/**
 * This method enables the fixture to verify the values on the properties of the presentation 
 * model of a presenter component, including NodeLists. It is also possible to check the 
 * 'invocationCount' on a method within the presentation model.   
 * 
 * @param {String} sProperty The property name
 * @param {Variant} vValue The value to set.
 * 
 * @see br.test.Fixture#doThen
 */
br.presenter.testing.PresentationModelFixture.prototype.doThen = function(sProperty, vValue) {
	var Errors = require('br/Errors');

	var oItem = this._getItem(sProperty);

	if(oItem instanceof br.presenter.property.Property)
	{
		assertEquals("'" + sProperty + "' should equal '" + vValue + "'", vValue, oItem.getFormattedValue());
	}
	else if(oItem instanceof br.presenter.node.OptionsNodeList)
	{
		assertEquals("'" + sProperty + "' should equal '" + vValue + "'", vValue, oItem.getOptionLabels());
	}
	else if(oItem instanceof br.presenter.node.NodeList)
	{
		if(!Array.isArray(vValue)) {
			throw new Errors.InvalidTestError("Validating a NodeList must supply an array of options as the test value.");
		}
		oItem.peek().forEach(function(oNode, nIndex) {
			if(!oNode.value && !oNode.label)
			{
				throw new Errors.InvalidTestError("PresentationNode in NodeList must have a `value` or `label` property.");
			}

			var vExpected = vValue[nIndex];
			var vActual = (oNode.value || oNode.label).getValue();
			var sErrorMessage = sProperty + "' index " + nIndex + " : '" + vActual + "' should equal '" + vValue + "'";
			assertEquals(sErrorMessage, vExpected, vActual);
		});
	}
	else if(oItem instanceof br.presenter.testing.PresentationModelFixture.MethodInvocation)
	{
		throw new Errors.InvalidTestError("the 'invoked' property can only be used in given and when clauses");
	}
	else if(oItem instanceof br.presenter.testing.PresentationModelFixture.InvocationCountSetter)
	{
		oItem.verifyInvocationCount(vValue);
	}
	else
	{
		throw new Errors.InvalidTestError("unable to handle: " + sProperty + " = " + vValue);
	}
};

// **********************************************************************************
// *							  Private methods
// **********************************************************************************

/**
 * @private
 */
br.presenter.testing.PresentationModelFixture.prototype._doGivenAndDoWhen = function(sProperty, vValue)
{
	var oItem = this._getItem(sProperty);
	
	if(oItem instanceof br.presenter.property.EditableProperty)
	{
		oItem.setUserEnteredValue(vValue);
	}
	else if(oItem instanceof br.presenter.property.WritableProperty)
	{
		oItem.setValue(vValue);
	}
	else if(oItem instanceof br.presenter.property.Property)
	{
		oItem._$setInternalValue(vValue);
	}
	else if(oItem instanceof br.presenter.node.OptionsNodeList)
	{
		oItem.setOptions(vValue);
	}
	else if(oItem instanceof br.presenter.testing.PresentationModelFixture.MethodInvocation)
	{
		oItem.invokeMethod(vValue);
	}
	else if(oItem instanceof br.presenter.testing.PresentationModelFixture.InvocationCountSetter)
	{
		oItem.setInvocationCount(vValue);
	}
	else
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "unable to handle: " + sProperty + " = " + vValue);
	}
};

/**
 * @private
 */
br.presenter.testing.PresentationModelFixture.prototype._getItem = function(sItemName, nDistanceFromEnd)
{
	nDistanceFromEnd = nDistanceFromEnd || 0;
	var oItem = this.m_oPresentationModel;
	var pParts = sItemName.replace(/\[(\d+)\]/g, ".$1").split(".");
	
	for(var i = 0, l = pParts.length - nDistanceFromEnd; i < l; ++i)
	{
		var sPartName = pParts[i];
		
		if(((sPartName == "invoked") || (sPartName == "invocationCount")) && (oItem instanceof Function))
		{
			var oPresentationNode = this._getItem(sItemName, 2);
			
			if(sPartName == "invoked")
			{
				var fMethod = oItem;
				oItem = new br.presenter.testing.PresentationModelFixture.MethodInvocation(oPresentationNode, fMethod);
			}
			else if(sPartName == "invocationCount")
			{
				var sMethod = pParts[i - 1];
				oItem = new br.presenter.testing.PresentationModelFixture.InvocationCountSetter(oPresentationNode, sMethod);
			}
		}
		else if((sPartName == "length") && (oItem instanceof br.presenter.node.NodeList))
		{
			oItem = new br.presenter.testing.NodeListLengthProperty(oItem);
		}
		else
		{
			if(oItem instanceof br.presenter.node.NodeList && !(oItem instanceof br.presenter.node.MappedNodeList) )
			{
				oItem = oItem.getPresentationNodesArray()[this._getNodeListIndex(sPartName)];
			}
			else
			{
				oItem = oItem[sPartName];
			}
			
			if(!oItem)
			{
				break;
			}
		}
	}
	
	return oItem;
};

/**
 * @private
 */
br.presenter.testing.PresentationModelFixture.prototype._getNodeListIndex = function(sPartName)
{
	var nIndex = Number(sPartName);
	
	if(isNaN(nIndex))
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "attempt to access NodeList without using an ordinal: '" + sPartName + "'");
	}
	
	return nIndex;
};

// ***********************************************************************************
// *	   br.presenter.testing.PresentationModelFixture.MethodInvocation
// ************************************************************************************


br.presenter.testing.PresentationModelFixture.MethodInvocation = function(oPresentationNode, fMethod)
{
	this.m_oPresentationNode = oPresentationNode;
	this.m_fMethod = fMethod;
};

br.presenter.testing.PresentationModelFixture.MethodInvocation.prototype.invokeMethod = function(vValue)
{
	if(vValue !== true)
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "the 'invoked' property can only be set to true: was set to " + vValue);
	}
	
	this.m_fMethod.call(this.m_oPresentationNode);
};

// ***********************************************************************************
// *	br.presenter.testing.PresentationModelFixture.InvocationCountSetter
// ************************************************************************************

br.presenter.testing.PresentationModelFixture.InvocationCountSetter = function(oPresentationNode, sMethod)
{
	this.m_oPresentationNode = oPresentationNode;
	this.m_sMethod = sMethod;
};

br.presenter.testing.PresentationModelFixture.InvocationCountSetter.prototype.setInvocationCount = function(vValue)
{
	var nValue = Number(vValue);
	
	if(isNaN(nValue))
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "the 'invocationCount' property can only be set to a number, and not " + vValue);
	}
	
	if(this._getMethod().invocationCount === undefined)
	{
		this.m_oPresentationNode[this.m_sMethod] = this._getInvocationCountingProxyMethod(nValue);
	}
};

br.presenter.testing.PresentationModelFixture.InvocationCountSetter.prototype.verifyInvocationCount = function(vValue)
{
	var nValue = Number(vValue);

	if(isNaN(nValue))
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "the 'invocationCount' property must be compared to a number");
	}
	else
	{
		var fMethod = this._getMethod();
		
		if(fMethod.invocationCount === undefined)
		{
			throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "the 'invocationCount' property first needs to be set in given or when before it can be verified in then");
		}
		else
		{
			assertEquals("'invocationCount' should be " + nValue, nValue, fMethod.invocationCount);
		}
	}
};


br.presenter.testing.PresentationModelFixture.InvocationCountSetter.prototype._getMethod = function()
{
	return this.m_oPresentationNode[this.m_sMethod];
};


br.presenter.testing.PresentationModelFixture.InvocationCountSetter.prototype._getInvocationCountingProxyMethod = function(nInitialValue)
{
	var fOrigMethod = this._getMethod();
	var fMethod = function()
	{
		fMethod.invocationCount++;
		fOrigMethod.apply(this, arguments);
	};
	fMethod.invocationCount = nInitialValue;
	
	return fMethod;
};
