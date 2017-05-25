'use strict';

var MappedNodeList = require('br/presenter/node/MappedNodeList');
var NodeListLengthProperty = require('br/presenter/testing/NodeListLengthProperty');
var BrErrors = require('br/Errors');
var WritableProperty = require('br/presenter/property/WritableProperty');
var EditableProperty = require('br/presenter/property/EditableProperty');
var NodeList = require('br/presenter/node/NodeList');
var OptionsNodeList = require('br/presenter/node/OptionsNodeList');
var Property = require('br/presenter/property/Property');
var KnockoutInvocationCountPlugin = require('br/presenter/testing/KnockoutInvocationCountPlugin');
var ComponentModelFixture = require('br/component/testing/ComponentModelFixture');
var Core = require('br/Core');
var topiarist = require('topiarist');

/**
 * @module br/presenter/testing/PresentationModelFixture
 */

/**
 * Constructs a <code>br.presenter.testing.PresentationModelFixture</code>.
 * 
 * @class
 * @alias module:br/presenter/testing/PresentationModelFixture
 * @implements module:br/component/testing/ComponentModelFixture
 * 
 * @classdesc
 * The <code>PresentationModelFixture</code> serves to manipulate and verify the state of the presentation
 * model of a presenter component. 
 */
function PresentationModelFixture() {
	this._initializePlugins();
}

Core.inherit(PresentationModelFixture, ComponentModelFixture);

/**
 * @private
 */
PresentationModelFixture.prototype._initializePlugins = function() {
	presenter_knockout.bindingHandlers.event = new KnockoutInvocationCountPlugin();
};

// * **********************************************************************************
// *						 ComponentModelFixture interface
// ************************************************************************************

PresentationModelFixture.prototype.setComponent = function(oComponent) {
	this.m_oPresentationModel = oComponent.getPresentationModel();
};

// ***********************************************************************************
// *							  Fixture interface
// ************************************************************************************

PresentationModelFixture.prototype.tearDown = function() {
	delete this.m_oPresentationModel;
};

PresentationModelFixture.prototype.canHandleExactMatch = function() {
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
PresentationModelFixture.prototype.canHandleProperty = function(sProperty) {
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
PresentationModelFixture.prototype.doGiven = function(sProperty, vValue) {
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
PresentationModelFixture.prototype.doWhen = function(sProperty, vValue) {
	this._doGivenAndDoWhen(sProperty, vValue);
};

function _assertEquals(msg, expected, actual) {
	if (isDefinedAndInstanceOf(expected, Array)) {
		assertEquals(msg, expected, actual);
	} else {
		assertSame(msg, expected, actual);
	}
}

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
PresentationModelFixture.prototype.doThen = function(sProperty, vValue) {
	var Errors = require('br/Errors');

	var oItem = this._getItem(sProperty);

	if (isDefinedAndInstanceOf(oItem, Property)) {
		_assertEquals("'" + sProperty + "' should equal '" + vValue + "'", vValue, oItem.getFormattedValue());
	} else if (isDefinedAndInstanceOf(oItem, OptionsNodeList)) {
		_assertEquals("'" + sProperty + "' should equal '" + vValue + "'", vValue, oItem.getOptionLabels());
	} else if (isDefinedAndInstanceOf(oItem, NodeList)) {
		if (!Array.isArray(vValue)) {
			throw new Errors.InvalidTestError('Validating a NodeList must supply an array of options as the test value.');
		}
		oItem.peek().forEach(function(oNode, nIndex) {
			if (!oNode.value && !oNode.label) {
				throw new Errors.InvalidTestError('PresentationNode in NodeList must have a `value` or `label` property.');
			}

			var vExpected = vValue[nIndex];
			var vActual = (oNode.value || oNode.label).getValue();
			var sErrorMessage = sProperty + "' index " + nIndex + " : '" + vActual + "' should equal '" + vValue + "'";
			_assertEquals(sErrorMessage, vExpected, vActual);
		});
	} else if (isDefinedAndInstanceOf(oItem, PresentationModelFixture.MethodInvocation)) {
		throw new Errors.InvalidTestError("the 'invoked' property can only be used in given and when clauses");
	} else if (isDefinedAndInstanceOf(oItem, PresentationModelFixture.InvocationCountSetter)) {
		oItem.verifyInvocationCount(vValue);
	} else {
		throw new Errors.InvalidTestError('unable to handle: ' + sProperty + ' = ' + vValue);
	}
};

// **********************************************************************************
// *							  Private methods
// **********************************************************************************

function isDefinedAndInstanceOf(item) {
	return item !== undefined && item !== null && topiarist.fulfills.apply(null, arguments);
}

/**
 * @private
 */
PresentationModelFixture.prototype._doGivenAndDoWhen = function(sProperty, vValue) {
	var oItem = this._getItem(sProperty);

	if (isDefinedAndInstanceOf(oItem, EditableProperty)) {
		oItem.setUserEnteredValue(vValue);
	} else if (isDefinedAndInstanceOf(oItem, WritableProperty)) {
		oItem.setValue(vValue);
	} else if (isDefinedAndInstanceOf(oItem, Property)) {
		oItem._$setInternalValue(vValue);
	} else if (isDefinedAndInstanceOf(oItem, OptionsNodeList)) {
		oItem.setOptions(vValue);
	} else if (isDefinedAndInstanceOf(oItem, PresentationModelFixture.MethodInvocation)) {
		oItem.invokeMethod(vValue);
	} else if (isDefinedAndInstanceOf(oItem, PresentationModelFixture.InvocationCountSetter)) {
		oItem.setInvocationCount(vValue);
	} else {
		throw new BrErrors.CustomError(BrErrors.INVALID_TEST, 'unable to handle: ' + sProperty + ' = ' + vValue);
	}
};



/**
 * @private
 */
PresentationModelFixture.prototype._getItem = function(sItemName, nDistanceFromEnd) {
	nDistanceFromEnd = nDistanceFromEnd || 0;
	var oItem = this.m_oPresentationModel;
	var pParts = sItemName.replace(/\[(\d+)\]/g, '.$1').split('.');

	for (var i = 0, l = pParts.length - nDistanceFromEnd; i < l; ++i) {
		var sPartName = pParts[i];

		if (((sPartName == 'invoked') || (sPartName == 'invocationCount')) && (isDefinedAndInstanceOf(oItem, Function))) {
			var oPresentationNode = this._getItem(sItemName, 2);

			if (sPartName == 'invoked') {
				var fMethod = oItem;
				oItem = new PresentationModelFixture.MethodInvocation(oPresentationNode, fMethod);
			} else if (sPartName == 'invocationCount') {
				var sMethod = pParts[i - 1];
				oItem = new PresentationModelFixture.InvocationCountSetter(oPresentationNode, sMethod);
			}
		} else if ((sPartName == 'length') && (isDefinedAndInstanceOf(oItem, NodeList))) {
			oItem = new NodeListLengthProperty(oItem);
		} else if (this._mappings && this._mappings[sPartName]) {
			oItem = this._getItem(this._mappings[sPartName], 0, oItem);
		} else {
			if (isDefinedAndInstanceOf(oItem, NodeList) && !(isDefinedAndInstanceOf(oItem, MappedNodeList))) {
				oItem = oItem.getPresentationNodesArray()[this._getNodeListIndex(sPartName)];
			} else {
				oItem = oItem[sPartName];
			}

			if (!oItem) {
				break;
			}
		}
	}

	return oItem;
};

/**
 * @private
 */
PresentationModelFixture.prototype._getNodeListIndex = function(sPartName) {
	var nIndex = Number(sPartName);

	if (isNaN(nIndex)) {
		throw new BrErrors.CustomError(BrErrors.INVALID_TEST, "attempt to access NodeList without using an ordinal: '" + sPartName + "'");
	}

	return nIndex;
};

// ***********************************************************************************
// *	   br.presenter.testing.PresentationModelFixture.MethodInvocation
// ************************************************************************************


PresentationModelFixture.MethodInvocation = function(oPresentationNode, fMethod) {
	this.m_oPresentationNode = oPresentationNode;
	this.m_fMethod = fMethod;
};

PresentationModelFixture.MethodInvocation.prototype.invokeMethod = function(vValue) {
	if (vValue !== true) {
		throw new BrErrors.CustomError(BrErrors.INVALID_TEST, "the 'invoked' property can only be set to true: was set to " + vValue);
	}

	this.m_fMethod.call(this.m_oPresentationNode);
};

// ***********************************************************************************
// *	br.presenter.testing.PresentationModelFixture.InvocationCountSetter
// ************************************************************************************

PresentationModelFixture.InvocationCountSetter = function(oPresentationNode, sMethod) {
	this.m_oPresentationNode = oPresentationNode;
	this.m_sMethod = sMethod;
};

PresentationModelFixture.InvocationCountSetter.prototype.setInvocationCount = function(vValue) {
	var nValue = Number(vValue);

	if (isNaN(nValue)) {
		throw new BrErrors.CustomError(BrErrors.INVALID_TEST, "the 'invocationCount' property can only be set to a number, and not " + vValue);
	}

	if (this._getMethod().invocationCount === undefined) {
		this.m_oPresentationNode[this.m_sMethod] = this._getInvocationCountingProxyMethod(nValue);
	}
};

PresentationModelFixture.InvocationCountSetter.prototype.verifyInvocationCount = function(vValue) {
	var nValue = Number(vValue);

	if (isNaN(nValue)) {
		throw new BrErrors.CustomError(BrErrors.INVALID_TEST, "the 'invocationCount' property must be compared to a number");
	} else {
		var fMethod = this._getMethod();

		if (fMethod.invocationCount === undefined) {
			throw new BrErrors.CustomError(BrErrors.INVALID_TEST, "the 'invocationCount' property first needs to be set in given or when before it can be verified in then");
		} else {
			assertEquals("'invocationCount' should be " + nValue, nValue, fMethod.invocationCount);
		}
	}
};


PresentationModelFixture.InvocationCountSetter.prototype._getMethod = function() {
	return this.m_oPresentationNode[this.m_sMethod];
};

PresentationModelFixture.prototype.setPMMappings = function(mappings) {
	this._mappings = mappings;
};

PresentationModelFixture.InvocationCountSetter.prototype._getInvocationCountingProxyMethod = function(nInitialValue) {
	var fOrigMethod = this._getMethod();
	var fMethod = function() {
		fMethod.invocationCount++;
		return fOrigMethod.apply(this, arguments);
	};
	fMethod.invocationCount = nInitialValue;

	return fMethod;
};

module.exports = PresentationModelFixture;
