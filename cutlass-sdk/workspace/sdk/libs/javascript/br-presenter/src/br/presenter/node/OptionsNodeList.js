/**
 * Constructs a new <code>OptionsNodeList</code> instance.
 * 
 * @class
 * The <code>OptionsNodeList</code> class is used to store the options available within
 * {@link br.presenter.node.SelectionField} and {@link br.presenter.node.MultiSelectionField}
 * instances.
 * 
 * @constructor
 * @param {Object} vOptions The set of available options, either as an array (keys only) or a map (keys to label).
 * @extends br.presenter.node.NodeList
 */
br.presenter.node.OptionsNodeList = function(vOptions)
{
	var pOptions = this._getOptionObjects(vOptions);
	br.presenter.node.NodeList.call(this, pOptions, br.presenter.node.Option);
};
br.Core.extend(br.presenter.node.OptionsNodeList, br.presenter.node.NodeList);

/**
 * Retrieve the array of {@link br.presenter.node.Option} instances contained by this object.
 * 
 * @type Array
 */
br.presenter.node.OptionsNodeList.prototype.getOptions = function()
{
	return this.getPresentationNodesArray();
};

/**
 * Retrieve an array of values for each {@link br.presenter.node.Option} contained within this object.
 * 
 * @type Array
 */
br.presenter.node.OptionsNodeList.prototype.getOptionValues = function()
{
	var pNodes = this.getOptions();
	var result = [];
	for (var i = 0, max = pNodes.length; i < max; i++)
	{
		result.push(pNodes[i].value.getValue());
	}
	return result;
};

/**
 * Retrieve an array of labels for each {@link br.presenter.node.Option} contained within this object.
 * 
 * @type Array
 */
br.presenter.node.OptionsNodeList.prototype.getOptionLabels = function()
{
	var pNodes = this.getOptions();
	var result = [];
	for (var i = 0, max = pNodes.length; i < max; i++)
	{
		result.push(pNodes[i].label.getValue());
	}
	return result;
};

/**
 * Reset the list of available options using the given array or map.
 * 
 * @param {Object} vOptions The set of available options, either as an array (keys only) or a map (keys to label).
 */
br.presenter.node.OptionsNodeList.prototype.setOptions = function(vOptions)
{
	this.updateList(vOptions);
};

/**
 * Retrieve the first option in the list &mdash; typically the default option.
 * 
 * @type br.presenter.node.Option
 */
br.presenter.node.OptionsNodeList.prototype.getFirstOption = function()
{
	var pOptions  = this.getOptions();
	if(pOptions.length == 0)
	{
		return null;
	}
	return pOptions[0];
};

/**
 * Retrieve the option with the given label. (If there is more than one option which has the given label, 
 * the first instance is returned.)
 * 
 * @param {String} sLabel Label to search.
 * @type {@link br.presenter.node.Option}
 */
br.presenter.node.OptionsNodeList.prototype.getOptionByLabel = function(sLabel)
{
	var pNodes = this.getOptions();
	for (var i = 0, max = pNodes.length; i < max; i++)
	{
		if(pNodes[i].label.getValue() === sLabel)
		{
			return pNodes[i];
		}
	}
	return null;
};

/**
 * Retrieve the option with the given unique value. 
 * 
 * @param {String} sValue Value to search.
 * @type {@link br.presenter.node.Option}
 */
br.presenter.node.OptionsNodeList.prototype.getOptionByValue = function(sValue)
{
	var pNodes = this.getOptions();
	for (var i = 0, max = pNodes.length; i < max; i++)
	{
		if(pNodes[i].value.getValue() === sValue)
		{
			return pNodes[i];
		}
	}
	return null;
};

/**
 * @private
 * @see br.presenter.node.NodeList#updateList
 */
br.presenter.node.OptionsNodeList.prototype.updateList = function(vOptions)
{
	var pOptions = this._getOptionObjects(vOptions);
	br.presenter.node.NodeList.prototype.updateList.call(this, pOptions);
};

/**
 * @private
 * @param {Object} vOptions
 * @type Array
 */
br.presenter.node.OptionsNodeList.prototype._getOptionObjects = function(vOptions)
{
	vOptions = vOptions || [];
	if(vOptions instanceof br.presenter.property.Property)
	{
		throw new br.Errors.InvalidParametersError("OptionsNodeList only accepts maps or arrays");
	}

	var pResult = [];

	if(vOptions instanceof Array)
	{
		for(var i = 0; i < vOptions.length; i++)
		{
			if(vOptions[i] instanceof br.presenter.node.Option)
			{
				pResult.push(vOptions[i]);
			}
			else
			{
				var option = new br.presenter.node.Option(vOptions[i],vOptions[i]);
				pResult.push(option);
			}
		}
	}
	else
	{
		for(var sKey in vOptions)
		{
			var option = new br.presenter.node.Option(sKey, vOptions[sKey]);
			pResult.push(option);
		}
	}
	return pResult;
};
