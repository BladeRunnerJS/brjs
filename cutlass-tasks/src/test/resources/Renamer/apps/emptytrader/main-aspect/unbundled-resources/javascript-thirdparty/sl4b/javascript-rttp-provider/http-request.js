var SL4B_HttpRequest = new function(){this.m_oXmlHttpRequest=null;this.m_nMaxGetLength;this.m_sXmlHttpRequestType;this.m_oSL4B_Logger=null;this.m_oRttpServerAckRegExp=/^00/;this.m_oSL4B_DebugLevel;this.m_bLegacyMessageInterface=false;this.log = function(){if(this.m_oSL4B_Logger!=null){
try {this.m_oSL4B_Logger.log.apply(this.m_oSL4B_Logger,arguments);}catch(e){}
}};
this.initialiseLogger = function(){if(this.m_oSL4B_Logger==null&&parent.SL4B_Logger!=null){this.m_oSL4B_Logger=parent.SL4B_Logger;this.m_oSL4B_DebugLevel=parent.SL4B_DebugLevel;this.log(this.m_oSL4B_DebugLevel.const_INFO_INT,"SL4B_HttpRequest.initialiseLogger: using "+this.m_sXmlHttpRequestType+" XmlHttpRequest object");}};
this.getParameter = function(A){var l_oMatch=window.location.search.match(new RegExp("[?&]"+A+"=([^&]*)"));
return ((l_oMatch==null) ? null : l_oMatch[1]);
};
this.initialise = function(){this.setDomain();if(window.XMLHttpRequest!==undefined){this.m_oXmlHttpRequest=new XMLHttpRequest();this.m_sXmlHttpRequestType="native";}else 
{var l_pXMLParserList=["MSXML2.XMLHttp.6.0","MSXML2.XMLHttp.3.0","Microsoft.XMLHTTP"];
for(var i=0,l=l_pXMLParserList.length;i<l;++i){
try {var l_sXmlParserName=l_pXMLParserList[i];
this.m_oXmlHttpRequest=new ActiveXObject(l_sXmlParserName);this.m_sXmlHttpRequestType=l_sXmlParserName;break;
}catch(e){}
}}this.m_nMaxGetLength=this.getParameter(SL4B_JavaScriptRttpProviderConstants.const_MAX_GET_LENGTH_PARAMETER);this.channelType="REQUEST";if(this.getParameter(SL4B_JavaScriptRttpProviderConstants.const_TYPE_PARAMETER)==SL4B_JavaScriptRttpProviderConstants.const_RESPONSE_TYPE){this.channelType="RESPONSE";}this.initialiseLogger();};
this.onReadyStateChangeWrapper = function(){if(window.SL4B_HttpRequest!==undefined){SL4B_HttpRequest.onReadyStateChange();}};
this.onReadyStateChange = function(){if(this.m_oXmlHttpRequest.readyState==4){this.setDomain();var l_bInvokeReady=false;
if(!parent.closed){
try {this._processServerResponse();this.ready();this.log(this.m_oSL4B_DebugLevel.const_RTTP_FINEST_INT,"SL4B_HttpRequest.onReadyStateChange: ready invoked");}catch(e){this.log(this.m_oSL4B_DebugLevel.const_ERROR_INT,"SL4B_HttpRequest.onReadyStateChange: error {0} {1}",e.name,e.message);}
}}};
this._processServerResponse = function(){this.log(this.m_oSL4B_DebugLevel.const_RTTP_FINEST_INT,"SL4B_HttpRequest._processServerResponse: data received");var sResponseText=this.m_oXmlHttpRequest.responseText;
if(this.m_bLegacyMessageInterface===true){parent.SL4B_ConnectionProxy.getInstance().parseRttpMessage(sResponseText);}else 
{if(this.m_oXmlHttpRequest.status!=200){parent.SL4B_ConnectionProxy.getInstance().processInvalidServerResponse(sResponseText,this.m_oXmlHttpRequest.status);}else 
{var sResponseHeaders="";

try {if(this.m_oXmlHttpRequest.getAllResponseHeaders!=null){sResponseHeaders=this.m_oXmlHttpRequest.getAllResponseHeaders();}}catch(e){}
this._processValidServerResponse(sResponseText,sResponseHeaders);}}};
this._processValidServerResponse = function(A,B){if(this.getParameter(SL4B_JavaScriptRttpProviderConstants.const_TYPE_PARAMETER)!=SL4B_JavaScriptRttpProviderConstants.const_RESPONSE_TYPE){if(!this._isType3Connection()){if(A==""){parent.SL4B_ConnectionProxy.getInstance().processInvalidServerResponse("Empty response, headers: "+B,this.m_oXmlHttpRequest.status);return;
}if(A.match(this.m_oRttpServerAckRegExp)){this.log(this.m_oSL4B_DebugLevel.const_RTTP_FINEST_INT,"SL4B_HttpRequest._processValidServerResponse: received 00 OK");return;
}}}parent.SL4B_ConnectionProxy.getInstance().processRttpMessageBlock(A.split("\n"));};
this._isType3Connection = function(){return (this.lastRequest.match(/\/RTTP-TYPE3/)!==null);
};
this.ready = function(){if(!parent.closed&&this.m_oSL4B_DebugLevel){
try {if(this.getParameter(SL4B_JavaScriptRttpProviderConstants.const_TYPE_PARAMETER)==SL4B_JavaScriptRttpProviderConstants.const_RESPONSE_TYPE){parent.SL4B_ConnectionProxy.getInstance().setResponseHttpRequest(this,this.getParameter(SL4B_JavaScriptRttpProviderConstants.const_UNIQUEID_PARAMETER));}else 
{parent.SL4B_ConnectionProxy.getInstance().setRequestHttpRequest(this,this.getParameter(SL4B_JavaScriptRttpProviderConstants.const_UNIQUEID_PARAMETER));}}catch(e){this.log(this.m_oSL4B_DebugLevel.const_WARN_INT,"SL4B_HttpRequest.ready: error {0}",e);}
}};
this.send = function(A){var l_nPos=A.indexOf("?");
var l_sUrl=A;
var l_sMessage="";
if(l_nPos!=-1){l_sUrl=A.substring(0,l_nPos);l_sMessage=A.substring(l_nPos+1);}
try {var l_oXmlHttpRequest=this.m_oXmlHttpRequest;
if(this.getParameter(SL4B_JavaScriptRttpProviderConstants.const_TYPE4_PARAMETER)!=null){l_oXmlHttpRequest.multipart=true;}var l_sUrlPrefix=window.location.href.match(/(https?:\/\/[^\/]+)/)[1];
var l_bMakeAsynchronousRequest=true;
if(l_sUrl.indexOf("LOGOUT")!=-1){l_bMakeAsynchronousRequest=false;}if(parent&&parent.SL4B_Accessor&&parent.SL4B_Accessor.getCapabilities!==undefined){this.m_nMaxGetLength=parent.SL4B_Accessor.getCapabilities().getHttpRequestLineLength();}this.lastRequest=A;if(l_sMessage.length>this.m_nMaxGetLength){l_oXmlHttpRequest.open("POST",l_sUrlPrefix+l_sUrl,l_bMakeAsynchronousRequest);l_oXmlHttpRequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');l_oXmlHttpRequest.onreadystatechange=this.onReadyStateChangeWrapper;l_oXmlHttpRequest.send(l_sMessage);}else 
{l_oXmlHttpRequest.open("GET",l_sUrlPrefix+A,l_bMakeAsynchronousRequest);l_oXmlHttpRequest.onreadystatechange=this.onReadyStateChangeWrapper;l_oXmlHttpRequest.send(null);}}catch(e){this.log(this.m_oSL4B_DebugLevel.const_ERROR_INT,"SL4B_HttpRequest.send: Problem in channel {1}, sending: {0}",e.name+": "+e.message,this.channelType);if(parent.SL4B_ConnectionProxy.getInstance().httpRequestError){parent.SL4B_ConnectionProxy.getInstance().httpRequestError(e,this.getParameter(SL4B_JavaScriptRttpProviderConstants.const_TYPE_PARAMETER),A);}else 
{this.log(this.m_oSL4B_DebugLevel.const_ERROR_INT,"An error occcurred while sending on the {0} channel, {1}: {2}. The version of SL4B on the client may be out of date.",this.channelType,e.name,e.message);}}
};
this.loaded = function(){this.initialise();this.ready();};
this.setDomain = function(){var l_sCommonDomain=this.getParameter(SL4B_JavaScriptRttpProviderConstants.const_DOMAIN_PARAMETER);
if(l_sCommonDomain!=null){document.domain=l_sCommonDomain;}this.m_bLegacyMessageInterface=this._shouldUseLegacyInterface();};
this._shouldUseLegacyInterface = function(){if(parent.SL4B_ConnectionProxy){
try {return (parent.SL4B_ConnectionProxy.getInstance().processRttpMessageBlock===undefined);
}catch(e){}
}return false;
};
this.initialise();};
SL4B_HttpRequest.loaded();