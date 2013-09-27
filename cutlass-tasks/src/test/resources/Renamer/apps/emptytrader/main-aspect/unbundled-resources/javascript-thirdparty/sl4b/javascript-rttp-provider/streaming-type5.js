var g_eParent;
var g_sSearch=window.location.search;
var const_SCRIPT_CLEANUP_LIMIT=100;
var g_nMessageCount=0;
var g_pScriptElements=document.getElementsByTagName("SCRIPT");
var g_nProcessedNumberOfScriptTags=0;
var g_bStopped=false;
function SL_OJ(){SL_AB();
try {var l_bIsSetResponseHttpRequest=false;
if(!g_eParent.SL4B_WindowRegistrar){l_bIsSetResponseHttpRequest=true;}else 
if(g_eParent.SL4B_WindowRegistrar&&g_eParent.SL4B_WindowRegistrar.isMaster()){l_bIsSetResponseHttpRequest=true;}if(l_bIsSetResponseHttpRequest){g_eParent.SL4B_ConnectionProxy.getInstance().setResponseHttpRequest(null,SL_ER("uniqueid"));}}catch(e){}
}
function SL_ER(A){var l_oMatch=g_sSearch.match(new RegExp("[?&]"+A+"=([^&]*)"));
return ((l_oMatch==null) ? null : l_oMatch[1]);
}
function SL_AB(){var l_sDomain=SL_ER("domain");
if(l_sDomain!==null){document.domain=l_sDomain;}var l_sconnectionType=SL_ER("connectiontype");
if(l_sconnectionType!==null&&l_sconnectionType=="6"){g_eParent=parent.document;}else 
{g_eParent=window.parent;}}
var stop=function(){g_bStopped=true;};
var _reset=function(){g_bStopped=false;};
function a(A){if(!g_bStopped){g_nProcessedNumberOfScriptTags=g_pScriptElements.length;++g_nMessageCount;
try {g_eParent.a(A);}catch(e){}
}}
var z=function(){};
if(SL_ER("suppressexceptions")!==null){z = function(){if(!g_bStopped){g_eParent.z();}if(window._cleanUpScriptElements!==undefined){SL_NB();}};
}else 
{z = function(){if(!g_bStopped){
try {g_eParent.z();}catch(e){}
if(window._cleanUpScriptElements!==undefined){SL_NB();}}};
}function SL_NB(){if(g_nMessageCount>=const_SCRIPT_CLEANUP_LIMIT){g_nMessageCount=0;var l_nLength=g_nProcessedNumberOfScriptTags-1;
for(var l_nScript=l_nLength;l_nScript>=2;--l_nScript){g_pScriptElements[l_nScript].parentNode.removeChild(g_pScriptElements[l_nScript]);}}}
SL_OJ();