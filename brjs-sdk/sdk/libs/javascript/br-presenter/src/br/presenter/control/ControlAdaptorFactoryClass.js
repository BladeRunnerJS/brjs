/**
 * @module br/presenter/control/ControlAdaptorFactoryClass
 */

var AliasRegistry = require('br/AliasRegistry');

/**
 * @class
 * @alias module:br/presenter/control/ControlAdaptorFactoryClass
 * 
 * @classdesc
 * Repository for presenter controls referenced in HTML templates.
 * All custom controls are registered here.
 */
var ControlAdaptorFactoryClass = function() {
	this.m_mConfiguredControlAdaptors = {};
};

/**
 * Registers a presenter control {@link module:br/presenter/control/ControlAdaptor}.
 *
 * @param {String} sName The name the control is referred to in the HTML binding attribute.
 * @param {Function} fControlAdaptorConstructor The constructor function of the class that implements the control.
 * @param {Object} mConfigOptions A Map of options used to configure the control.
 */
ControlAdaptorFactoryClass.prototype.registerConfiguredControlAdaptor = function(sName, fControlAdaptorConstructor) {
	this.m_mConfiguredControlAdaptors[sName] = fControlAdaptorConstructor;
};

/**
 *  @private
 *  @param {String} sName
 *  @type br.presenter.control.ControlAdaptor
 */
ControlAdaptorFactoryClass.prototype.createControlAdaptor = function(sName) {
	var fControlAdaptorClass;

	if (AliasRegistry.isAliasAssigned(sName)) {
		fControlAdaptorClass = AliasRegistry.getClass(sName);
	} else {
		if (!this.m_mConfiguredControlAdaptors[sName]) {
			throw "Unknown Control Adaptor: " + sName;
		}
		fControlAdaptorClass = this.m_mConfiguredControlAdaptors[sName];
	}

	return new fControlAdaptorClass();
};

module.exports = ControlAdaptorFactoryClass;

// TODO: delete this line once the package is CommonJs
br.presenter.control.ControlAdaptorFactoryClass = module.exports;
