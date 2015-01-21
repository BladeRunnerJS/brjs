ApiProtector = function()
{
	this.m_pObjects = [];
	this.m_pItems = [];
	
	this.protectApis.apply(this, arguments);
};

ApiProtector.prototype.protectApis = function()
{
	for(var i = 0, l = arguments.length; i < l; ++i)
	{
		var sObject = arguments[i];
		try
		{
			var oObject = caplin.core.ClassUtility.getPackage(sObject);
			this.protectApi(oObject);
			
			if(oObject.prototype)
			{
				this.protectApi(oObject.prototype);
			}
		}
		catch(e)
		{
			// do nothing
		}
	}
};

ApiProtector.prototype.protectApi = function(oObject)
{
	var mItems = {};
	
	for(var sItem in oObject)
	{
		mItems[sItem] = oObject[sItem];
	}
	
	this.m_pObjects.push(oObject);
	this.m_pItems.push(mItems);
};

ApiProtector.prototype.restoreApis = function()
{
	for(var i = 0, l = this.m_pObjects.length; i < l; ++i)
	{
		var vObject = this.m_pObjects[i];
		var mItems = this.m_pItems[i];
		
		for(var sItem in mItems)
		{
			vObject[sItem] = mItems[sItem];
		}
	}
};
