brjs.dashboard.app.testing.XhrFactoryStub = function()
{
	this.m_pXhrRequestQueue = [];
	this.m_pXhrResponseQueue = [];
};

brjs.dashboard.app.testing.XhrFactoryStub.prototype.getRequestObject = function()
{
	var oXhrStub = new brjs.dashboard.app.testing.TestXhrStub();
	
	this.m_pXhrRequestQueue.push(oXhrStub);
	this.m_pXhrResponseQueue.push(oXhrStub);
	
	return oXhrStub;
};

brjs.dashboard.app.testing.XhrFactoryStub.prototype.shiftRequestXhr = function()
{
	return this.m_pXhrRequestQueue.shift();
};

brjs.dashboard.app.testing.XhrFactoryStub.prototype.popResponseXhr = function()
{
	return this.m_pXhrResponseQueue.pop();
};
