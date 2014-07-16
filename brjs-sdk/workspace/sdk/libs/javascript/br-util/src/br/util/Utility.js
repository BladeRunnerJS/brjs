/**
 * @private
 */
br.util.Utility = function()
{
};

/**
 * @private
 */
br.util.Utility.UNIQUE_ID = 0;

/**
 * @private
 */
br.util.Utility.createApplicationInstanceUniqueId = function()
{
	return "ApplicationInstanceUniqueId_" + (++br.util.Utility.UNIQUE_ID);
};

br.util.Utility.nextObjectIdentifier = 1;


/**
 * @private
 */
br.util.Utility.interfaceMethod = function(sClassName, sMethodName) {
	var fell = require('fell');
	var Errors = require('br/Errors');

	var sErrorMsg = "Error in " + sClassName + " base class: " + sMethodName + "() has not been implemented.";

	// log the problem
	fell.error(sErrorMsg);

	// since we cannot recover, throw an exception
	throw new Errors.UnimplementedInterfaceError(sErrorMsg);
};

/**
 * @private
 */
br.util.Utility.performOnce = function(oObj, sMethod)
{
 if (!this.m_mObjects) {
		this.m_mObjects = {};

		var oThis = this;
		window.setTimeout(function() {

			for ( var sObjId in oThis.m_mObjects)
			{
				var mMethods = oThis.m_mObjects[sObjId];

				for ( var sMethod in mMethods)
				{
					var fCallback = mMethods[sMethod];
					fCallback();
				}
			}
			oThis.m_mObjects = null;
		}, 0);
	}

	if (!oObj.__objId) {
		oObj.__objId = br.util.Utility.nextObjectIdentifier++;
	}

	if (!this.m_mObjects[oObj.__objId]) {
		this.m_mObjects[oObj.__objId] = {};
	}

	this.m_mObjects[oObj.__objId][sMethod] = oObj[sMethod].bind(oObj);
};
