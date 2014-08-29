/**
 * @module br/presenter/util/ErrorMonitor
 */

/**
 * @class
 * @alias module:br/presenter/util/ErrorMonitor
 * 
 * @classdesc
 * ErrorMonitor is responsible for monitoring the status of fields that contain
 * an error and failure message property. It is used to set the tool tip class that
 * the tooltip control will scan in order to create the tool tip box.
 *
 * @param {module:br/presenter/node/ToolTipNode} Node representing the tool tip model.
 */
br.presenter.util.ErrorMonitor = function(oTooltipNode)
{
	if (!oTooltipNode || !(oTooltipNode instanceof br.presenter.node.ToolTipNode))
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_PARAMETERS, "The ErrorMonitor has to be constructed with an instance of a br.presenter.node.ToolTipNode");
	}

	this.m_oPropertyHelper = new br.presenter.property.PropertyHelper();

	this.oTooltipNode = oTooltipNode;

	this.m_pErrorStack = [];
};

/**
 *
 * Filters all nodes of type {@see br.presenter.node.ToolTipField} and monitors them using
 * {@see br.presenter.util.ErrorMonitor#monitorField}
 *
 * @type {br.presenter.node.PresentationNode[]}
 */
br.presenter.util.ErrorMonitor.prototype.addErrorListeners = function(pGroups)
{
	var node;
	for (var i=0; i<pGroups.length; i++)
	{
		node = pGroups[i];
		if(node instanceof br.presenter.node.ToolTipField)
		{
			this.monitorField(node);
		}
	}
};

/**
 * removes all error subscriptions for the given fields
 * @param {Array} pGroups fields we want to forget
 */
br.presenter.util.ErrorMonitor.prototype.removeErrorListeners = function(pGroups)
{
	for (var i = 0; i < pGroups.length; i++)
	{
		if(pGroups[i].hasError && pGroups[i].failureMessage)
		{
			this.forgetField(pGroups[i]);
		}
	}
};

br.presenter.util.ErrorMonitor.prototype.replaceErrorListeners = function(pGroups)
{
	this.removeAllErrors();
	this.addErrorListeners(pGroups);
};

/**
 * Monitors a Field to automatically add it as an error when it enters an error state and remove it once
 * that error is resolved.
 *
 * @param {module:br/presenter/node/ToolTipField} oField
 */
br.presenter.util.ErrorMonitor.prototype.monitorField = function(oField)
{
	if (!(oField instanceof br.presenter.node.ToolTipField))
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_PARAMETERS, "The field to monitor has to be an instance of br.presenter.node.ToolTipField");
	}

	var oTicketErrorProperty = this;
	var fValidationSuccessHandler = function()
	{
		if(!this.hasError.getValue())
		{
			oTicketErrorProperty._removeError(this);
		}
	};

	this.m_oPropertyHelper.addChangeListener(oField.hasError, oField, fValidationSuccessHandler);
	this.m_oPropertyHelper.addValidationErrorListener(oField.value, oField, this._addError.bind(this, oField));

	//We don't put a notify immediately on the add listeners as it triggers a force revalidation which is not needed
	//for async validation
	if(oField.hasError.getValue() && oField.failureMessage.getValue())
	{
		this._addError(oField);
	}
};

/**
 * Removes the automatic monitoring of the supplied Field, and removes it from the list of current errors
 * if the field is currently in an error state.
 *
 * @param {module:br/presenter/node/Field} oField
 */
br.presenter.util.ErrorMonitor.prototype.forgetField = function(oField)
{
	this._removeError(oField);
	this.m_oPropertyHelper.clearProperty(oField.hasError);
	this.m_oPropertyHelper.clearProperty(oField.value);
};

br.presenter.util.ErrorMonitor.prototype.removeAllErrors = function()
{
	this.oTooltipNode.move(false);
	this.m_pErrorStack = [];
	this.m_oPropertyHelper.removeAllListeners();
	this._removeLastError();
};

br.presenter.util.ErrorMonitor.prototype._addError = function(oField)
{
	this._removeFromStack(oField);

	var sFailureMessage = oField.failureMessage.getValue();
	this.m_pErrorStack.push({"field": oField, "failureMessage": sFailureMessage});

	var nTopOfStack = this.m_pErrorStack.length;
	if (nTopOfStack === 1)
	{
		this._addTooltipToTopOfStack();
	}
	else //if (this.m_pErrorStack.length > 1)
	{
		var oFieldWithTooltipToRemove = this.m_pErrorStack[nTopOfStack - 2].field;
		this._moveTooltip(oFieldWithTooltipToRemove);
	}

	this.oTooltipNode.setMessage(sFailureMessage);
	this._notifyObserversOfErrorChange();
};

br.presenter.util.ErrorMonitor.prototype._removeError = function(oField)
{
	var mFieldAndMessage = this._removeFromStack(oField);
	if (mFieldAndMessage)
	{
		var oFieldNoLongerInError = mFieldAndMessage.field;

		if (this.m_pErrorStack.length > 0)
		{
			this._updateTooltipOnRemove(oFieldNoLongerInError);
			this._updateErrorMessage();
		}
		else
		{
			this._removeTooltipFrom(oFieldNoLongerInError);
			this._removeLastError();
		}

		this._notifyObserversOfErrorChange();
	}
};

/**
 *
 * @param oField
 * @returns {*}
 * @private
 */
br.presenter.util.ErrorMonitor.prototype._removeFromStack = function(oField)
{
	var nErrorStackLength = this.m_pErrorStack.length;
	for (var i = 0; i < nErrorStackLength; ++i)
	{
		if (this.m_pErrorStack[i].field == oField)
		{
			return this.m_pErrorStack.splice(i, 1)[0];
		}
	}
};

/**
 *
 * @private
 */
br.presenter.util.ErrorMonitor.prototype._notifyObserversOfErrorChange = function()
{
	this.oTooltipNode.move(false);
	this.oTooltipNode.move(this.m_pErrorStack.length !== 0);
};

/**
 *
 * @private
 */
br.presenter.util.ErrorMonitor.prototype._removeLastError = function()
{
	this.oTooltipNode.setMessage("");
};

/**
 *
 * @private
 */
br.presenter.util.ErrorMonitor.prototype._updateErrorMessage = function()
{
	this.oTooltipNode.setMessage(this._getNextErrorMessage());
};

/**
 * @returns {*}
 * @private
 */
br.presenter.util.ErrorMonitor.prototype._getNextErrorMessage = function()
{
	var nErrorIndex = this.m_pErrorStack.length - 1;
	return this.m_pErrorStack[nErrorIndex].failureMessage;
};

/**
 *
 * @param oField
 * @private
 */
br.presenter.util.ErrorMonitor.prototype._updateTooltipOnRemove = function(oField)
{
	if ( oField.tooltipClassName.getValue() !== "")
	{
		this._moveTooltip(oField);
	}
};

/**
 *
 * @param oField
 * @private
 */
br.presenter.util.ErrorMonitor.prototype._moveTooltip = function(oField)
{
	this._removeTooltipFrom(oField);
	this._addTooltipToTopOfStack();
};

/**
 *
 * @private
 */
br.presenter.util.ErrorMonitor.prototype._addTooltipToTopOfStack = function()
{
	var nTopOfStack = this.m_pErrorStack.length - 1;
	var mTopOfStack = this.m_pErrorStack[nTopOfStack];
	var oFieldAtTopOfStack = mTopOfStack.field;
	this._addTooltipTo(oFieldAtTopOfStack);
};

/**
 *
 * @param oField
 * @private
 */
br.presenter.util.ErrorMonitor.prototype._addTooltipTo = function(oField)
{
	oField.tooltipClassName.setValue(this.oTooltipNode.getTooltipClassName())
};

/**
 *
 * @param oField
 * @private
 */
br.presenter.util.ErrorMonitor.prototype._removeTooltipFrom = function(oField)
{
	oField.tooltipClassName.setValue("")
};
