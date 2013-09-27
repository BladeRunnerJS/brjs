/*
 * Connection using XmlHttpRequest streaming (readyState == 3), based on type 5 to allow for reconnection
 */
 
// used to stop the connection, like type 5 
var g_bStopped = false;

var stop = function()
{
	g_bStopped = true;
};

/** @private */
SL4B_StreamingType2 = new function() 
{
	this.m_oXmlHttpRequest = null;
	this.m_nEndPosition = 0;
	this.m_bDomainSet = false;
	this.m_oSL4B_Logger = null;
	this.m_oSL4B_DebugLevel;
	this.m_eParent;
	
	this.initialiseLogger = function()
	{
		if (this.m_oSL4B_Logger == null)
		{
			this.m_oSL4B_Logger = window.parent.SL4B_Logger;
			this.m_oSL4B_DebugLevel = window.parent.SL4B_DebugLevel;
			
			this.m_oSL4B_Logger.log(this.m_oSL4B_DebugLevel.const_INFO_INT, "SL4B_StreamingType2.initialiseLogger: using " +
				this.m_sXmlHttpRequestType + " SL4B_StreamingType2 object");
		}
	};
	
	this.getParameter = function(l_sName) 
	{
		var l_oMatch = window.location.search.match(new RegExp("[?&]" + l_sName + "=([^&]*)"));
		return ((l_oMatch == null)?null:l_oMatch[1]);
	};
		
	this.setDomain = function () 
	{
		if (!this.m_bDomainSet)
		{
			var l_sCommonDomain = this.getParameter("domain");
			if (l_sCommonDomain != null)
			{
				this.m_bDomainSet = true;
				document.domain = l_sCommonDomain;
//				alert("domain set");
			}
			
			this.m_eParent = window.parent;
		}
	};

	this.onReadyStateChangeWrapper = function() 
	{
//		alert("onReadyStateChangeWrapper " );
		if (window.SL4B_StreamingType2 !== undefined)
		{
			SL4B_StreamingType2.onReadyStateChange();
		}
	};

	this.onReadyStateChange = function()
	{
//		alert("onReadyStateChange " + this.m_oXmlHttpRequest.readyState);
		if(this.m_oXmlHttpRequest.readyState == 3)
		{
			this.initialiseLogger();
			
			var l_sMessage = this.m_oXmlHttpRequest.responseText;
			var l_sPacket = l_sMessage.substring(this.m_nEndPosition);
			this.m_nEndPosition = l_sMessage.length;
			
			this.m_oSL4B_Logger.log(this.m_oSL4B_DebugLevel.const_RTTP_FINEST_INT, "SL4B_StreamingType2.onReadyStateChange: data received < " + l_sPacket);
			
			if (!g_bStopped)
			{
				var l_pPackets = l_sPacket.split('\n');
				for (var i in l_pPackets)
				{
					var s = l_pPackets[i];
					if (s.length > 0)
					{
						this.m_oSL4B_Logger.log(this.m_oSL4B_DebugLevel.const_RTTP_FINEST_INT, "SL4B_StreamingType2.onReadyStateChange: data received << " + s);
						this.m_eParent.a(s);
					}
				}
				this.m_eParent.z();
			}
		}
	};

	this.loaded = function() 
	{
//		alert("loaded");
		this.setDomain();
		
		try
		{
			var sParameter = this.getParameter("uniqueid");
			window.parent.SL4B_ConnectionProxy.getInstance().setResponseHttpRequest(null, sParameter);
//			alert("setResponseHttpRequest");
		}
		catch (e)
		{
			this.handleResponseHttpRequestError(e, sParameter);
		}		
		
		// try to use a native XHR first for IE7 (http://blogs.msdn.com/ie/archive/2006/06/08/619507.aspx)
		if(window.XMLHttpRequest !== undefined)
		{
			this.m_oXmlHttpRequest = new XMLHttpRequest();
			this.m_sXmlHttpRequestType = "native";
		}
		else
		{
			// in IE6 and below try to use MSXML 6.0 first, then use MSXML 3.0 as a fall-back, and finally accept anything else
			// see (http://blogs.msdn.com/xmlteam/archive/2006/10/23/using-the-right-version-of-msxml-in-internet-explorer.aspx)
			var l_pXMLParserList = ["MSXML2.XMLHttp.6.0", "MSXML2.XMLHttp.3.0", "Microsoft.XMLHTTP"];
			
			for(var i = 0, l = l_pXMLParserList.length; i < l; ++i)
			{
				try 
			 	{
					var l_sXmlParserName = l_pXMLParserList[i];
					
					this.m_oXmlHttpRequest = new ActiveXObject(l_sXmlParserName);
					this.m_sXmlHttpRequestType = l_sXmlParserName;
					break;
				}
				catch(e)
				{
					// keep looping till we find an XMLHttpRequest we can construct
				}
			}
		}

		// request type 2 data
		try
		{
			var l_oXmlHttpRequest = this.m_oXmlHttpRequest;

			l_oXmlHttpRequest.open("GET", "/RTTP-TYPE2", true);
			l_oXmlHttpRequest.setRequestHeader('X-RTTP-MimeType', 'application/x-liberator-event-stream');

			l_oXmlHttpRequest.onreadystatechange = this.onReadyStateChangeWrapper;
			l_oXmlHttpRequest.send(null);
		}
		catch(e)
		{
			alert(e);
		}	
//		alert("loaded end");
	};
	
	this.handleResponseHttpRequestError = function(e, sParameter)
	{
		var bParentClosed = window.parent ? window.parent.closed : "window.parent undefined";
		var bSL4BCP = window.parent ? window.parent.SL4B_ConnectionProxy : false;
		var bSL4BCPI = bSL4BCP ? window.parent.SL4B_ConnectionProxy.getInstance() : false;

		alert("failed to set setResponseHttpRequest \n" + e.toString() + "\n document.domain " + document.domain + "\n window.parent " + window.parent
				+ "\n parent window closed " + bParentClosed + "\n SL4B Connection Proxy " + bSL4BCP + "\n SL4B Connection Proxy Instance " + bSL4BCPI
				+ "\n Parameter " + sParameter);
	};
};

//alert("SL4B_StreamingType2.loaded()");
SL4B_StreamingType2.loaded();
