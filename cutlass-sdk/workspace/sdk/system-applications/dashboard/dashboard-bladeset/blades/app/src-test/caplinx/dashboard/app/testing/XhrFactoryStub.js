caplinx.dashboard.app.testing.XhrFactoryStub = function()
{
	this.m_pXhrRequestQueue = [];
	this.m_pXhrResponseQueue = [];
};

caplinx.dashboard.app.testing.XhrFactoryStub.prototype.getRequestObject = function()
{
	var oXhrStub = new caplinx.dashboard.app.testing.TestXhrStub();
	
	this.m_pXhrRequestQueue.push(oXhrStub);
	this.m_pXhrResponseQueue.push(oXhrStub);
	
	return oXhrStub;
};

caplinx.dashboard.app.testing.XhrFactoryStub.prototype.shiftRequestXhr = function()
{
	return this.m_pXhrRequestQueue.shift();
};

caplinx.dashboard.app.testing.XhrFactoryStub.prototype.popResponseXhr = function()
{
	return this.m_pXhrResponseQueue.pop();
};
