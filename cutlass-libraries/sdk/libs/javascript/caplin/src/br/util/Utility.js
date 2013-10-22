/**
 * This is a static utility class and does not need to be instantiated.
 * @private
 */
br.util.Utility = function()
{
};

/**
 * @private
 * Used when creating a unique ID
 */
br.util.Utility.UNIQUE_ID = 0;

/**
 * Creates an ID in the form of a GUID that is unique for a single application
 * instance. This ID is NOT globally unique so multiple users of the same application
 * could create the same ID.
 * @type String
 * @returns a unique ID for the the present instance of the application
 * @private
 */
br.util.Utility.createApplicationInstanceUniqueId = function()
{
	return "ApplicationInstanceUniqueId_" + (++br.util.Utility.UNIQUE_ID);
};

br.util.Utility.nextObjectIdentifier = 1;


/**
 * @private
 * @throws br.util.Error Always throws exception when called
 * @param {String} sClassName
 * @param {String} sMethodName
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
 * Allows you to execute a method called several times just once. 
 * This is useful when you need several actions to be executed as an
 * atomic operation.
 * 
 * @param {Object} The caller object
 * @param {String} The method name to be executed
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