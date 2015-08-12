/**
 * Constructs a new <code>OptionsNodeList</code> instance.
 *
 * @class
 * The <code>OptionsNodeList</code> class is used to store the options available within
 * {@link br.presenter.node.SelectionField} and {@link br.presenter.node.MultiSelectionField} instances.
 *
 * @constructor
 * @param {Object} options The set of available options, either as an array (keys only) or a map (keys to label).
 * @extends br.presenter.node.NodeList
 */
br.presenter.node.OptionsNodeList = function(options) {
	var options = this._getOptionObjects(options);
	br.presenter.node.NodeList.call(this, options, br.presenter.node.Option);
};
br.Core.extend(br.presenter.node.OptionsNodeList, br.presenter.node.NodeList);

/**
 * Retrieve the array of {@link br.presenter.node.Option} instances contained by this object.
 * @returns {Array}
 */
br.presenter.node.OptionsNodeList.prototype.getOptions = function() {
	return this.getPresentationNodesArray();
};

/**
 * Retrieve an array of values for each {@link br.presenter.node.Option} contained within this object.
 * @returns {Array}
 */
br.presenter.node.OptionsNodeList.prototype.getOptionValues = function() {
	var nodes = this.getOptions();
	var result = [];

	for (var i = 0, max = nodes.length; i < max; i++) {
		result.push(nodes[i].value.getValue());
	}

	return result;
};

/**
 * Retrieve an array of labels for each {@link br.presenter.node.Option} contained within this object.
 * @returns {Array}
 */
br.presenter.node.OptionsNodeList.prototype.getOptionLabels = function() {
	var nodes = this.getOptions();
	var result = [];

	for (var i = 0, max = nodes.length; i < max; i++) {
		result.push(nodes[i].label.getValue());
	}

	return result;
};

/**
 * Reset the list of available options using the given array or map.
 * @param {Object} options The set of available options, either as an array (keys only) or a map (keys to label).
 */
br.presenter.node.OptionsNodeList.prototype.setOptions = function(options) {
	this.updateList(options);
};

/**
 * Retrieve the first option in the list &mdash; typically the default option.

 * @returns {br.presenter.node.Option}
 */
br.presenter.node.OptionsNodeList.prototype.getFirstOption = function() {
	var options  = this.getOptions();

	if (options.length == 0) {
		return null;
	}

	return options[0];
};

/**
 * Retrieve the option with the given label. (If there is more than one option which has the given label, the first
 *  instance is returned.)
 *
 * @param {String} label Label to search.
 * @param {Boolean} ignoreCase Controls whether the search should be case sensitive (default: false).
 * @returns {@link br.presenter.node.Option}
 */
br.presenter.node.OptionsNodeList.prototype.getOptionByLabel = function(label, ignoreCase) {
	if (typeof ignoreCase === 'undefined') {
		ignoreCase = false;
	}
	
	if (typeof ignoreCase !== 'boolean') {
		throw new Error("'ignoreCase' argument must be a Boolean value");
	}

	var nodes = this.getOptions();
	var labelToCompareWith = label;

	if (ignoreCase) {
		labelToCompareWith = label.toLowerCase();
	}

	function getNodeValue(node) {
		if (ignoreCase) {
			return node.label.getValue().toLowerCase();
		} else {
			return node.label.getValue();
		}
	}

	for (var i = 0, max = nodes.length; i < max; i++) {
		if (getNodeValue(nodes[i]) === labelToCompareWith) {
			return nodes[i];
		}
	}

	return null;
};

/**
 * Retrieve the option with the given unique value.
 *
 * @param {String} value Value to search.
 * @param {@link br.presenter.node.Option}
 */
br.presenter.node.OptionsNodeList.prototype.getOptionByValue = function(value) {
	var nodes = this.getOptions();

	for (var i = 0, max = nodes.length; i < max; i++) {
		if (nodes[i].value.getValue() === value) {
			return nodes[i];
		}
	}

	return null;
};

/** @private */
br.presenter.node.OptionsNodeList.prototype.updateList = function(options) {
	var optionsObj = this._getOptionObjects(options);
	br.presenter.node.NodeList.prototype.updateList.call(this, optionsObj);
};

/** @private */
br.presenter.node.OptionsNodeList.prototype._getOptionObjects = function(options) {
	var option;

	options = options || [];

	if (options instanceof br.presenter.property.Property) {
		throw new br.Errors.InvalidParametersError('OptionsNodeList only accepts maps or arrays');
	}

	var result = [];

	if (Object.prototype.toString.call(options) === '[object Array]') {
		for (var i = 0, len = options.length; i < len; i++) {
			if (options[i] instanceof br.presenter.node.Option) {
				result.push(options[i]);
			} else {
				option = new br.presenter.node.Option(options[i],options[i]);
				result.push(option);
			}
		}
	} else {
		for (var key in options) {
			option = new br.presenter.node.Option(key, options[key]);
			result.push(option);
		}
	}

	return result;
};
