caplinx.dashboard.app.testing.TestXhrStub = function()
{
	this.readyState = null;
	this.status = null;
	this.responseText = null;
	
	this.m_sMethod = null;
	this.m_sUrl = null;
	this.m_sRequestBody = null;
};

caplinx.dashboard.app.testing.TestXhrStub.prototype.open = function(sMethod, sUrl)
{
	this.readyState = 1;
	this.m_sMethod = sMethod;
	this.m_sUrl = sUrl;
};

caplinx.dashboard.app.testing.TestXhrStub.prototype.send = function(sRequestBody)
{
	this.readyState = 2;
	this.m_sRequestBody = sRequestBody;
};

caplinx.dashboard.app.testing.TestXhrStub.prototype.getRequestSummary = function()
{
	return this.m_sMethod + " " + this.m_sUrl + this._getParameters(); 
};

caplinx.dashboard.app.testing.TestXhrStub.prototype.injectResponse = function(response)
{
	if(response.indexOf(" ") > 0)
	{
		this.status = response.substring(0, response.indexOf(" "));
		this.responseText = response.substring(response.indexOf(" ")+1);		
	}
	else 
	{
		this.status = response;
		this.responseText = '""';
	}
	
	this.readyState = 4;
};

caplinx.dashboard.app.testing.TestXhrStub.prototype._getParameters = function()
{
	if (this.m_sRequestBody !== "" & this.m_sRequestBody !== null)
	{
		return " " + JSON.stringify(JSON.parse(this.m_sRequestBody)).replace(/['"]/g,'');
	}
	
	return "";
};
