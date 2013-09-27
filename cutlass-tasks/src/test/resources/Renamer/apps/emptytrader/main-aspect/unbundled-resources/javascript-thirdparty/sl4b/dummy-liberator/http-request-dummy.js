document.domain = "caplin.com";

var SL4B_HttpRequest = new function() {
	
	this.m_proxy = null;
	
	this._reply = function(sResponseText)
	{
		var pMessages = sResponseText.split("\n");
		this.m_proxy.processRttpMessageBlock(pMessages);
	};
	
	this.send = function(l_sEncodedUrl) {
		
		alert(l_sEncodedUrl);
	};

	
	this.loaded = function() {
		
		this.m_proxy = parent.SL4B_ConnectionProxy.getInstance();
		var sessionId = "08nD1jTZowZ07uW_t_S_jY";

		this.m_proxy.setRequestHttpRequest(this, this.getParameter("uniqueid"));
			
		//this._reply("01 " + sessionId + " host=jamest.caplin.com version=2.1 server=rttpd/4.5.13 time=1266598458 timezone=0000");
	
	};	
	
	this.getParameter = function(l_sName)
	{
		//if IE then search contains space
		if(window.location.search == ""){
			return null;
		}
	
	   var l_oMatch = window.location.search.match(new RegExp("[?&]" + l_sName + "=([^&]*)"));
	   return l_oMatch[1] || null;
	}
};

// the script file has nowfully loaded, invoked the loaded method
SL4B_HttpRequest.loaded();
