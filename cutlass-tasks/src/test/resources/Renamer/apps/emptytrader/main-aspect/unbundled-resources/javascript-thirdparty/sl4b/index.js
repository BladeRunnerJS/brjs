SL_BF = function(){this.REMOVAL_LIMIT=1000;this.m_bBrowserMapsLeakMemory=(navigator.userAgent.match(/MSIE/)!=undefined);};
SL_BF.prototype.createMap = function(){if(this.m_bBrowserMapsLeakMemory){var fClass=new Function();
fClass.deleteCount=0;return new fClass();
}else 
{return {};
}};
SL_BF.prototype.removeItem = function(B,A){delete B[A];if(this.m_bBrowserMapsLeakMemory){if(B.constructor.deleteCount++>=this.REMOVAL_LIMIT){var mNewMap=this.createMap();
for(sItem in B){mNewMap[sItem]=B[sItem];}B=mNewMap;}}return B;
};
SL_BF=new SL_BF();var SL4B_RttpProviderFactory=function(){};
if(false){function SL4B_RttpProviderFactory(){}
}SL4B_RttpProviderFactory = function(){this.m_oLastProvider=null;};
SL4B_RttpProviderFactory.prototype.createRttpProvider = SL_MG;function SL_MG(A){var l_oConfiguration=SL4B_Accessor.getConfiguration();
var l_oProvider=null;
if(l_oConfiguration.getRttpProvider()==SL4B_ScriptLoader.const_APPLET_RTTP_PROVIDER){l_oProvider=new SL4B_AppletRttpProvider(A);}else 
if(l_oConfiguration.getRttpProvider()==SL4B_ScriptLoader.const_OBJECT_RTTP_PROVIDER){alert("Object RTTP Provider support is not available in this version of SL4B");}else 
if(l_oConfiguration.getRttpProvider()==SL4B_ScriptLoader.const_JAVASCRIPT_RTTP_PROVIDER){l_oProvider=SL4B_JavaScriptRttpProvider.createProvider(A,this.m_oLastProvider);}else 
if(l_oConfiguration.getRttpProvider()==SL4B_ScriptLoader.const_TEST_RTTP_PROVIDER){l_oProvider=new SL4B_TestRttpProvider();}if(this.m_oLastProvider!==null){SL4B_WindowEventHandler.removeListener(this.m_oLastProvider);}this.m_oLastProvider=l_oProvider;return l_oProvider;
}
var SL4B_Accessor=function(){};
if(false){function SL4B_Accessor(){}
}SL4B_Accessor = new function(){this.m_oExceptionHandler=null;this.m_oConfiguration=null;this.m_oBrowserAdapter=null;this.m_oCredentialsProvider=null;this.m_oRttpProvider=null;this.m_oSnapshotProvider=null;this.m_oUnderlyingRttpProvider=null;this.m_oStatistics=null;this.m_oRttpProviderFactory=new SL4B_RttpProviderFactory();this.setExceptionHandler = function(A){this.m_oExceptionHandler=A;};
this.getExceptionHandler = function(){return this.m_oExceptionHandler;
};
this.getConfiguration = function(){return this.m_oConfiguration;
};
this.setConfiguration = function(A){if(this.m_oConfiguration!=null){throw new SL4B_Exception("The SL4B_Configuration has already been set and cannot be changed.");
}else 
{this.m_oConfiguration=A;}};
this.getBrowserAdapter = function(){if(this.m_oBrowserAdapter==null){throw new SL4B_Exception("BrowserAdapter has not been set");
}return this.m_oBrowserAdapter;
};
this.setBrowserAdapter = function(A){this.m_oBrowserAdapter=A;};
this.getCredentialsProvider = function(){if(this.m_oCredentialsProvider==null){throw new SL4B_Exception("CredentialsProvider has not been set");
}return this.m_oCredentialsProvider;
};
this.setCredentialsProvider = function(A){this.m_oCredentialsProvider=A;};
this.getRttpProvider = function(){if(this.m_oRttpProvider==null){throw new SL4B_Exception("RttpProvider has not been set");
}return this.m_oRttpProvider;
};
this.getUnderlyingRttpProvider = function(){if(this.m_oUnderlyingRttpProvider==null){throw new SL4B_Exception("UnderlyingRttpProvider has not been set");
}return this.m_oUnderlyingRttpProvider;
};
this.setRttpProvider = function(A){this.m_oRttpProvider=A;A.internalInitialise();};
this.setUnderlyingRttpProvider = function(A){this.m_oUnderlyingRttpProvider=A;};
this.setSnapshotProvider = function(A){this.m_oSnapshotProvider=A;};
this.getSnapshotProvider = function(){if(this.m_oSnapshotProvider==null){throw new SL4B_Exception("SnapshotProvider has not been set");
}return this.m_oSnapshotProvider;
};
this.getLogger = function(){return SL4B_Logger;
};
this.getRttpProviderFactory = function(){return this.m_oRttpProviderFactory;
};
this.setRttpProviderFactory = function(A){this.m_oRttpProviderFactory=A;};
this.setStatistics = function(A){if(A){this.m_oStatistics=A;}};
this.getStatistics = function(){var master=SL4B_WindowRegistrar.getMasterWindow();
if(SL4B_WindowRegistrar.isMaster()||master===null){if(this.m_oStatistics===null){throw new SL4B_Exception("Statistics has not been set");
}return this.m_oStatistics;
}else 
{
try {return master.SL4B_Accessor.getStatistics();
}catch(e){return this.m_oStatistics;
}
}};
this.setCapabilities = function(A){this.m_oCapabilities=A;};
this.getCapabilities = function(){if(this.m_oCapabilities==null){throw new SL4B_Exception("Capabilities has not been set");
}return this.m_oCapabilities;
};
this.resetConfig = function(A){this.m_oConfiguration=null;this.setConfiguration(A);};
this.resetRttpProvider = function(A){this.m_oRttpProvider=A;this.m_oUnderlyingRttpProvider=A;};
};
var SL4B_StringEncoder=function(){this.m_oCharactersToEncodeRegExp=/[<>&"]/g;this.m_mDecodedToEncodedValueMap={"<":"&lt;", ">":"&gt;", "&":"&amp;", "\"":"&quot;"};};
if(false){function SL4B_StringEncoder(){}
}SL4B_StringEncoder.prototype.encodeValue = function(A){if(this.m_oCharactersToEncodeRegExp.test(A)){return A.replace(this.m_oCharactersToEncodeRegExp,this._encodeCharacter);
}return A;
};
SL4B_StringEncoder.prototype._encodeCharacter = function(A){return SL4B_StringEncoder.m_mDecodedToEncodedValueMap[A];
};
SL4B_StringEncoder=new SL4B_StringEncoder();function SL_BD(A){var i=function(){};
i.prototype = A.prototype;return new i();
}
var GF_SlidingWindow=function(A){if((A>0)==false){throw new SL4B_Exception("Sliding window cannot be created with a size of "+A+".");
}if(Math.floor(A)!=A){throw new SL4B_Exception("Sliding window cannot be created with a non integer size ("+A+").");
}this.m_nMaxsize=A;this.clear();};
if(false){function GF_SlidingWindow(){}
}GF_SlidingWindow.prototype.clear = function(){this.m_pBuffer=new Array(this.m_nMaxsize);this.m_nNext=0;this.m_bFilled=false;};
GF_SlidingWindow.prototype.newest = function(){var oNewest=null;
var nIndex=(this.m_nNext+this.m_nMaxsize-1)%this.m_nMaxsize;
if(this.m_bFilled||nIndex<this.m_nNext){oNewest=this.m_pBuffer[nIndex];}return oNewest;
};
GF_SlidingWindow.prototype.oldest = function(){var oOldest=null;
if(this.m_bFilled){oOldest=this.m_pBuffer[this.m_nNext];}else 
if(this.m_nNext>0){oOldest=this.m_pBuffer[0];}return oOldest;
};
GF_SlidingWindow.prototype._hasJustFilled = function(){return this.m_bFilled==false&&this.m_nNext==0;
};
GF_SlidingWindow.prototype.add = function(A){var oOusted=null;
if(this.m_bFilled){oOusted=this.oldest();}this.changeWindow(A,oOusted);if(this._hasJustFilled()){this.windowFilled();}return oOusted;
};
GF_SlidingWindow.prototype.setSize = function(B){if(this.m_nMaxsize==B||B==null||isNaN(B)||B<0){return;
}var tmpWindow=new GF_SlidingWindow(B);
this.iterate(function(A){tmpWindow.add(A);});this.m_nMaxsize=tmpWindow.m_nMaxsize;this.m_pBuffer=tmpWindow.m_pBuffer;this.m_nNext=tmpWindow.m_nNext;this.m_bFilled=tmpWindow.m_bFilled;};
GF_SlidingWindow.prototype.changeWindow = function(B,A){this.m_pBuffer[this.m_nNext]=B;this.m_nNext=(this.m_nNext+1)%this.m_nMaxsize;};
GF_SlidingWindow.prototype.windowFilled = function(){this.m_bFilled=true;};
GF_SlidingWindow.prototype.iterate = function(A){var end=this.m_nMaxsize;
if(this.m_bFilled==false){end=this.m_nNext;}for(var i=0;i<end;++i){var j=i;
if(this.m_bFilled==true){j=(this.m_nNext+i)%this.m_nMaxsize;}A(this.m_pBuffer[j]);}};
GF_SlidingWindow.prototype.toString = function(){var result=["{sidingwindow start="];
result.push(this.m_nNext);result.push(" values=[");result.push(this.m_pBuffer.join(","));result.push("] }");return result.join("");
};
GF_SlidingWindow.prototype.getLength = function(){return this.m_bFilled ? this.m_nMaxsize : this.m_nNext;
};
var SL4B_MD5=(function(){var MD5_T=[0x00000000,0xd76aa478,0xe8c7b756,0x242070db,0xc1bdceee,0xf57c0faf,0x4787c62a,0xa8304613,0xfd469501,0x698098d8,0x8b44f7af,0xffff5bb1,0x895cd7be,0x6b901122,0xfd987193,0xa679438e,0x49b40821,0xf61e2562,0xc040b340,0x265e5a51,0xe9b6c7aa,0xd62f105d,0x02441453,0xd8a1e681,0xe7d3fbc8,0x21e1cde6,0xc33707d6,0xf4d50d87,0x455a14ed,0xa9e3e905,0xfcefa3f8,0x676f02d9,0x8d2a4c8a,0xfffa3942,0x8771f681,0x6d9d6122,0xfde5380c,0xa4beea44,0x4bdecfa9,0xf6bb4b60,0xbebfbc70,0x289b7ec6,0xeaa127fa,0xd4ef3085,0x04881d05,0xd9d4d039,0xe6db99e5,0x1fa27cf8,0xc4ac5665,0xf4292244,0x432aff97,0xab9423a7,0xfc93a039,0x655b59c3,0x8f0ccc92,0xffeff47d,0x85845dd1,0x6fa87e4f,0xfe2ce6e0,0xa3014314,0x4e0811a1,0xf7537e82,0xbd3af235,0x2ad7d2bb,0xeb86d391];
var round1=new Array(16);
var mid=7;
for(var i=0;i<round1.length;++i){round1[i]=[i,mid,i+1];mid=mid+5;if(mid>22)mid=7;}var round2=[new Array(1,5,17),new Array(6,9,18),new Array(11,14,19),new Array(0,20,20),new Array(5,5,21),new Array(10,9,22),new Array(15,14,23),new Array(4,20,24),new Array(9,5,25),new Array(14,9,26),new Array(3,14,27),new Array(8,20,28),new Array(13,5,29),new Array(2,9,30),new Array(7,14,31),new Array(12,20,32)];
var round3=[new Array(5,4,33),new Array(8,11,34),new Array(11,16,35),new Array(14,23,36),new Array(1,4,37),new Array(4,11,38),new Array(7,16,39),new Array(10,23,40),new Array(13,4,41),new Array(0,11,42),new Array(3,16,43),new Array(6,23,44),new Array(9,4,45),new Array(12,11,46),new Array(15,16,47),new Array(2,23,48)];
var round4=[new Array(0,6,49),new Array(7,10,50),new Array(14,15,51),new Array(5,21,52),new Array(12,6,53),new Array(3,10,54),new Array(10,15,55),new Array(1,21,56),new Array(8,6,57),new Array(15,10,58),new Array(6,15,59),new Array(13,21,60),new Array(4,6,61),new Array(11,10,62),new Array(2,15,63),new Array(9,21,64)];
function SL_SR(C,B,A){return (C&B)|(~C&A);
}
function SL_SQ(C,B,A){return (C&A)|(B&~A);
}
function SL_SM(C,B,A){return C^B^A;
}
function SL_SJ(C,B,A){return B^(C|~A);
}
var rounds=[[SL_SR,round1],[SL_SQ,round2],[SL_SM,round3],[SL_SJ,round4]];
function SL_SO(A){return String.fromCharCode(A&0xff)+String.fromCharCode((A>>>8)&0xff)+String.fromCharCode((A>>>16)&0xff)+String.fromCharCode((A>>>24)&0xff);
}
function SL_SP(A){return A.charCodeAt(0)|(A.charCodeAt(1)<<8)|(A.charCodeAt(2)<<16)|(A.charCodeAt(3)<<24);
}
function SL_SL(A){while(A<0)A+=4.294967296E9;while(A>4.294967295E9)A-=4.294967296E9;return A;
}
var PACK0=SL_SO(0);
function SL_SK(D,E,C,B,A){var a,b,c,d;
var kk,ss,ii;
var t,u;
a=B[0];b=B[1];c=B[2];d=B[3];kk=A[0];ss=A[1];ii=A[2];u=C(E[b],E[c],E[d]);t=E[a]+u+D[kk]+MD5_T[ii];t=SL_SL(t);t=((t<<ss)|(t>>>(32-ss)));t+=E[b];E[a]=SL_SL(t);}
function SL_SN(A){var abcd,x,state,s;
var len,index,padLen,f,r;
var i,j,k;
var tmp;
state=new Array(0x67452301,0xefcdab89,0x98badcfe,0x10325476);len=A.length;index=len&0x3f;padLen=(index<56) ? (56-index) : (120-index);if(padLen>0){A+="\x80";for(i=0;i<padLen-1;i++)A+="\x00";}A+=SL_SO(len*8);A+=PACK0;len+=padLen+8;abcd=new Array(0,1,2,3);x=new Array(16);s=new Array(4);for(k=0;k<len;k+=64){for(i=0, j=k;i<16;i++, j+=4){x[i]=A.charCodeAt(j)|(A.charCodeAt(j+1)<<8)|(A.charCodeAt(j+2)<<16)|(A.charCodeAt(j+3)<<24);}for(i=0;i<4;i++)s[i]=state[i];for(i=0;i<4;i++){f=rounds[i][0];r=rounds[i][1];for(j=0;j<16;j++){SL_SK(x,s,f,abcd,r[j]);tmp=abcd[0];abcd[0]=abcd[3];abcd[3]=abcd[2];abcd[2]=abcd[1];abcd[1]=tmp;}}for(i=0;i<4;i++){state[i]+=s[i];state[i]=SL_SL(state[i]);}}return SL_SO(state[0])+SL_SO(state[1])+SL_SO(state[2])+SL_SO(state[3]);
}
function SL_SI(A){var i,out,c;
var bit128;
bit128=SL_SN(A);out="";for(i=0;i<16;i++){c=bit128.charCodeAt(i);out+="0123456789abcdef".charAt((c>>4)&0xf);out+="0123456789abcdef".charAt(c&0xf);}return out;
}
return SL_SI;
})();
var SL4B_DebugLevel=function(){};
if(false){function SL4B_DebugLevel(){}
}SL4B_DebugLevel = new function(){this.CRITICAL="critical";this.RTTP_ERROR="rttp-error";this.ERROR="error";this.NOTIFY="notify";this.WARN="warn";this.INFO="info";this.DEBUG="debug";this.RTTP_FINE="rttp-fine";this.RTTP_FINER="rttp-finer";this.RTTP_FINEST="rttp-finest";this.FINE="fine";this.FINER="finer";this.FINEST="finest";this.const_CRITICAL_INT=0;this.const_RTTP_ERROR_INT=1;this.const_ERROR_INT=2;this.const_NOTIFY_INT=3;this.const_WARN_INT=4;this.const_INFO_INT=5;this.const_DEBUG_INT=6;this.const_RTTP_FINE_INT=7;this.const_RTTP_FINER_INT=8;this.const_RTTP_FINEST_INT=9;this.const_FINE_INT=this.const_RTTP_FINE_INT;this.const_FINER_INT=this.const_RTTP_FINER_INT;this.const_FINEST_INT=this.const_RTTP_FINEST_INT;this.m_pDebugLevelNames=new Object();this.m_pNumericDebugLevels=new Object();this.addDebugLevel = function(B,A){this.m_pDebugLevelNames[B]=A;this.m_pNumericDebugLevels[A]=B;};
this.getNumericDebugLevel = function(A){var l_nNumericDebugLevel=this._convertToNumericDebugLevel(A);
if(this._isInvalidNumericDebugLevel(l_nNumericDebugLevel)){throw new SL4B_Exception("Illegal debug level \""+A+"\" specified");
}return l_nNumericDebugLevel;
};
this._convertToNumericDebugLevel = function(A){if(isNaN(A)){return this.m_pNumericDebugLevels[(A+"").toLowerCase()];
}else 
{return parseInt(A,10);
}};
this._isInvalidNumericDebugLevel = function(A){return (this.m_pDebugLevelNames[A]===undefined);
};
this.getDebugLevelName = function(A){var nDebugLevel=this._convertToNumericDebugLevel(A);
if(this._isInvalidNumericDebugLevel(nDebugLevel)){return undefined;
}return this.m_pDebugLevelNames[nDebugLevel];
};
this.addDebugLevel(this.const_CRITICAL_INT,this.CRITICAL);this.addDebugLevel(this.const_RTTP_ERROR_INT,this.RTTP_ERROR);this.addDebugLevel(this.const_ERROR_INT,this.ERROR);this.addDebugLevel(this.const_NOTIFY_INT,this.NOTIFY);this.addDebugLevel(this.const_WARN_INT,this.WARN);this.addDebugLevel(this.const_INFO_INT,this.INFO);this.addDebugLevel(this.const_DEBUG_INT,this.DEBUG);this.addDebugLevel(this.const_RTTP_FINE_INT,this.RTTP_FINE);this.addDebugLevel(this.const_RTTP_FINER_INT,this.RTTP_FINER);this.addDebugLevel(this.const_RTTP_FINEST_INT,this.RTTP_FINEST);this.addDebugLevel(this.const_FINE_INT,this.FINE);this.addDebugLevel(this.const_FINER_INT,this.FINER);this.addDebugLevel(this.const_FINEST_INT,this.FINEST);};
var SL4B_LogMessageListener=function(){};
if(false){function SL4B_LogMessageListener(){}
}SL4B_LogMessageListener = function(){};
SL4B_LogMessageListener.prototype.logMessage = function(B,A){};
SL4B_DumpLogMessageListener = function(){this.m_oBrowserAdapter=null;};
SL4B_DumpLogMessageListener.prototype = new SL4B_LogMessageListener();SL4B_DumpLogMessageListener.prototype.logMessage = function(B,A){if(this.m_oBrowserAdapter==null){this.m_oBrowserAdapter=SL4B_Accessor.getBrowserAdapter();}this.m_oBrowserAdapter.dump(B+"\n");};
SL4B_LogWindowMessageListener = function(A){this.oLogger=A;};
SL4B_LogWindowMessageListener.prototype = new SL4B_LogMessageListener();SL4B_LogWindowMessageListener.prototype.logMessage = function(B,A){this.oLogger.m_pMessageQueue.push(B);
try {if(this.oLogger.m_bDebugConsoleOpened){this.oLogger.printMessagesToDebugConsole();}}catch(e){SL4B_Accessor.getExceptionHandler().processException(e);}
};
var SL4B_Logger=function(){};
if(false){function SL4B_Logger(){}
}SL4B_Logger = new function(){this.const_HTML="html";this.const_ABOUT_BLANK="about:blank";this.m_hDebugConsole=null;this.m_bDebugConsoleOpening=false;this.m_bDebugConsoleOpened=false;this.m_bClosing=false;this.m_pMessageQueue=new Array();this.m_oSlidingLogMessageWindow=new GF_SlidingWindow(200);this.m_pAlertMessageQueue=new Array();this.m_bAlertDisplayed=false;this.m_nConfiguredDebugLevel=-1;this.m_oBrowserAdapter=null;this.m_pMessageListeners=[];this.m_oLogWindowMessageListener=new SL4B_LogWindowMessageListener(this);this.m_pMessageListeners.push([new SL4B_DumpLogMessageListener(),null]);this.m_pMessageListeners.push([this.m_oLogWindowMessageListener,null]);this.m_pParameterRegularExpressions=new Array(/\{0\}/g,/\{1\}/g,/\{2\}/g,/\{3\}/g,/\{4\}/g,/\{5\}/g,/\{6\}/g,/\{7\}/g);this.replaceMessageParameters = function(C,A,B){var l_nNumberOfParameters=A.length;
for(var l_nParam=B;l_nParam<l_nNumberOfParameters;++l_nParam){C=C.replace(this.m_pParameterRegularExpressions[(l_nParam-B)],A[l_nParam]);}return C;
};
this.addMessageListener = function(B,A){if(B==null||typeof B.logMessage!="function"){throw new SL4B_Exception("SL4B_Logger.addMessageListener: specified listener is null or does not implement a logMessage() method");
}this.m_pMessageListeners.push([B,A]);};
this.removeLogWindowMessageListener = function(){for(var i=0;i<this.m_pMessageListeners.length;i++){if(this.m_pMessageListeners[i][0]==this.m_oLogWindowMessageListener){this.m_pMessageListeners.splice(i,1);break;
}}this.m_oLogWindowMessageListener=null;};
this.clearMessageListeners = function(){this.m_pMessageListeners=new Array();};
this._logSlidingLogWindow = function(B,A){this.m_oSlidingLogMessageWindow.add(arguments);};
this.log = function(B,A){var formattedMsg=null;
var date=null;
if(typeof (B)!="number"){B=SL4B_DebugLevel.getNumericDebugLevel(B);}if(B<=SL4B_DebugLevel.const_RTTP_FINER_INT){date=new Date();this._logSlidingLogWindow(date,arguments);}for(var l_nListener=0,l_nLength=this.m_pMessageListeners.length;l_nListener<l_nLength;++l_nListener){var l_oListener=this.m_pMessageListeners[l_nListener][0];
var l_nListenerDebugLevel=this.m_pMessageListeners[l_nListener][1];
if(l_nListenerDebugLevel==undefined||l_nListenerDebugLevel==null){
try {l_nListenerDebugLevel=SL4B_DebugLevel.getNumericDebugLevel(SL4B_Accessor.getConfiguration().getDebugLevel());this.m_pMessageListeners[l_nListener][1]=l_nListenerDebugLevel;}catch(e){l_nListenerDebugLevel=SL4B_DebugLevel.getNumericDebugLevel(SL4B_DebugLevel.ERROR);this.m_pMessageListeners[l_nListener][1]=l_nListenerDebugLevel;}
}if(l_nListenerDebugLevel>=B){if(formattedMsg==null){if(date==null){date=new Date();}formattedMsg=SL_HV.formatDateStamp(date)+" - "+SL4B_DebugLevel.getDebugLevelName(B)+":   "+this.replaceMessageParameters(A,arguments,2);}l_oListener.logMessage(formattedMsg,B);}}};
this.printMessage = function(B,A){A=(A===undefined ? SL4B_DebugLevel.INFO : A);this.log(A,B);};
this.getRecentLogMessages = function(){var pLogMessages=[];
var self=this;
this.m_oSlidingLogMessageWindow.iterate(function(A){var oDate=A[0];
var args=A[1];
var nDebugLevel=args[0];
var sMessage=args[1];
var sReplacedMessage=self.replaceMessageParameters(sMessage,args,2);
pLogMessages.push(self.formatMessage(oDate,sReplacedMessage,nDebugLevel));});return pLogMessages;
};
this.formatMessage = function(C,B,A){if(C==null){return B;
}return SL_HV.formatDateStamp(C)+" - "+SL4B_DebugLevel.getDebugLevelName(A)+":   "+B;
};
this.setNumberOfLogMessagesToRetain = function(A){this.m_oSlidingLogMessageWindow.setSize(A);};
this.logConnectionMessage = function(A){arguments[0]=(A ? SL4B_DebugLevel.const_NOTIFY_INT : SL4B_DebugLevel.const_INFO_INT);arguments[1]="[CONNECTION] "+arguments[1];this.log.apply(this,arguments);};
this.alert = function(A){var l_sFormattedMessage=this.replaceMessageParameters(A,this.alert.arguments,1);
this.printMessage(l_sFormattedMessage);
try {if(this.mustDebug(SL4B_DebugLevel.NOTIFY)){this.synchronizedAlert(l_sFormattedMessage);}}catch(e){SL4B_Accessor.getExceptionHandler().processException(e);}
};
this.synchronizedAlert = function(A){this.m_pAlertMessageQueue.push(A);if(!this.m_bAlertDisplayed){this.m_bAlertDisplayed=true;while(this.m_pAlertMessageQueue.length>0){this.log(this.m_pAlertMessageQueue.shift());}this.m_bAlertDisplayed=false;}};
this.printMessagesToDebugConsole = function(){while(this.m_pMessageQueue.length>0){var l_sNextMessage=this.m_pMessageQueue.shift();
this.printMessageToDebugConsole(l_sNextMessage);}};
this.printMessageToDebugConsole = function(A){if(this.m_hDebugConsole!=null){
try {this.m_hDebugConsole.GF_LogMessage(A);}catch(e){}
}};
this.useHtmlDebugWindow = function(){return (SL4B_Accessor.getConfiguration().getDebugWindowType()==this.const_HTML);
};
this.openDebugConsoleOnStartUp = function(A){if((A&&this.useHtmlDebugWindow())||(!A&&!this.useHtmlDebugWindow())){if(SL4B_Accessor.getConfiguration().isLogWindowNeeded()){this.openDebugConsole();}}};
this.getDebugWindowName = function(){if(SL4B_WindowRegistrar.isMaster()){return "_sl4b_debug_"+SL4B_Accessor.getConfiguration().getFrameId();
}else 
{var l_oNow=new Date();
return "_sl4b_debug_"+SL4B_Accessor.getConfiguration().getFrameId()+l_oNow.getTime();
}};
this.openDebugConsole = function(){if(!this.m_bDebugConsoleOpening&&!this.m_bClosing){if(this.m_oLogWindowMessageListener==null){this.m_oLogWindowMessageListener=new SL4B_LogWindowMessageListener(this);this.m_pMessageListeners.push([this.m_oLogWindowMessageListener,null]);}this.m_bDebugConsoleOpening=true;var l_sDomain=SL4B_Accessor.getConfiguration().getCommonDomain();
var l_sDebugWindowName=this.getDebugWindowName();
var l_sDebugWindowParameters="height=400,width=600,status=no,toolbar=no,menubar=no,location=no,resizable=yes,scrollbars=yes";
if(this.useHtmlDebugWindow()){var l_sUrl=SL4B_ScriptLoader.getRootUrl()+"sl4b/logger/log-window.html?level="+SL4B_Accessor.getConfiguration().getDebugLevel()+((l_sDomain!=null) ? "&domain="+l_sDomain : "");
this.m_hDebugConsole=window.open(l_sUrl,l_sDebugWindowName,l_sDebugWindowParameters);}else 
{this.m_hDebugConsole=window.open("",l_sDebugWindowName,l_sDebugWindowParameters);var l_sHtml="<html><head><title>SL4B Debug Log [Debug level "+SL4B_StringEncoder.encodeValue(SL4B_Accessor.getConfiguration().getDebugLevel())+"]</title>";
l_sHtml+="<style type=\"text/css\"> body {font-family:Verdana;background-color:Black;color:#00ff00;} table {font-size:10px;}</style></head>";l_sHtml+="<table width='100%'><tbody id=\"tblMessageLog\" cellpadding=\"0\" cellspacing=\"0\"></tbody></table>";l_sHtml+="<scr"+"ipt type=\"text/javascript\" src=\""+SL4B_ScriptLoader.getRootUrl()+"sl4b/logger/log-window.js\"></scr"+"ipt>";l_sHtml+="</body></html>";
try {var l_oDocument=this.m_hDebugConsole.document;
l_oDocument.open();l_oDocument.write(l_sHtml);l_oDocument.close();}catch(e){SL4B_Logger.alert("Your browser appears to have a popup blocker enabled which is preventing the SL4B debug console window from opening.\nPlease disable the popup blocker for this web site to view the console.");}
}}};
this.closeDebugConsole = function(){if(this.m_hDebugConsole!=null){this.m_bClosing=true;this.m_hDebugConsole.close();}};
this.debugConsoleOpened = function(){this.m_bDebugConsoleOpened=true;if(SL4B_WindowRegistrar.isMaster()){this.printMessageToDebugConsole("Debug Console for frame \""+SL4B_Accessor.getConfiguration().getFrameId()+"\"");}else 
{this.printMessageToDebugConsole("Debug Console for a slave frame");}this.printMessagesToDebugConsole();SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINE_INT,"Logger.debugConsoleOpened: Adding logger as listener for close event.");
try {SL4B_WindowEventHandler.addListener(this);}catch(e){SL4B_Accessor.getExceptionHandler().processException(e);}
};
this.debugConsoleClosed = function(){this.m_bDebugConsoleOpened=false;this.m_hDebugConsole=null;this.removeLogWindowMessageListener();};
this.getDebugConsole = function(){return this.m_hDebugConsole;
};
this.mustDebug = function(A){return (this.getConfiguredDebugLevel()>=SL4B_DebugLevel.getNumericDebugLevel(A));
};
this.getConfiguredDebugLevel = function(){if(this.m_nConfiguredDebugLevel==-1){
try {this.m_nConfiguredDebugLevel=SL4B_DebugLevel.getNumericDebugLevel(SL4B_Accessor.getConfiguration().getDebugLevel());}catch(e){this.m_nConfiguredDebugLevel=SL4B_DebugLevel.getNumericDebugLevel(SL4B_DebugLevel.ERROR);}
}return this.m_nConfiguredDebugLevel;
};
this._$setConfiguredDebugLevel = function(A){var l_nLevel=A;
if(typeof A=="string"){l_nLevel=SL4B_DebugLevel.getNumericDebugLevel(A);}this.m_nConfiguredDebugLevel=l_nLevel;};
this.onUnload = function(A){if(A!=null&&typeof A!="undefined"&&!(A.ctrlKey&&A.altKey)){
try {if(this.m_hDebugConsole){this.m_hDebugConsole.close();}}catch(e){}
}};
};
SL_HV = new function(){this.m_pMonths=new Array("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec");this.m_pZeroPadding=new Array("","0","00","000");this.createDateStamp = function(){return this.formatDateStamp(new Date());
};
this.formatDateStamp = function(A){var l_nTimezoneOffset=A.getTimezoneOffset();
var l_nAbsoluteTimezoneOffset=Math.abs(l_nTimezoneOffset);
var l_sOffsetSign=(l_nTimezoneOffset<=0) ? "+" : "-";
var l_sOffsetHours=this.padZeros(Math.floor(Math.abs(l_nAbsoluteTimezoneOffset)/60),2);
var l_sOffsetMinutes=this.padZeros(l_nAbsoluteTimezoneOffset%60,2);
return this.m_pMonths[A.getMonth()]+" "+this.padZeros(A.getDate(),2)+" "+this.padZeros(A.getHours(),2)+":"+this.padZeros(A.getMinutes(),2)+":"+this.padZeros(A.getSeconds(),2)+"."+this.padZeros(A.getMilliseconds(),3)+" "+l_sOffsetSign+l_sOffsetHours+l_sOffsetMinutes;
};
this.padZeros = function(B,A){var l_nValueLength=(B+"").length;
var l_nDifference=A-l_nValueLength;
return this.m_pZeroPadding[l_nDifference]+B;
};
};
var SL4B_ConnectionMethod=function(){};
if(false){function SL4B_ConnectionMethod(){}
}SL4B_ConnectionMethod = function(D,C,A,B){this.m_sName=D;this.m_sConnectionClass=A;this.m_nRTTPType=C;this.m_bIsPolling=B;};
SL4B_ConnectionMethod.AllMethods={};SL4B_ConnectionMethod.prototype.createConnection = function(B,A){var oClass=window[this.m_sConnectionClass];
var oInstance=new oClass(B,A);
return oInstance;
};
SL4B_ConnectionMethod.prototype.isStreaming = function(){return !this.m_bIsPolling;
};
SL4B_ConnectionMethod.prototype.isPolling = function(){return this.m_bIsPolling;
};
SL4B_ConnectionMethod.prototype.getRTTPType = function(){return this.m_nRTTPType;
};
SL4B_ConnectionMethod.prototype.toString = function(){return this.m_sName;
};
SL4B_ConnectionMethod.XHR_STREAMING=new SL4B_ConnectionMethod("XHR Streaming",2,"SL4B_Type2Connection",false);SL4B_ConnectionMethod.POLLING=new SL4B_ConnectionMethod("Polling",3,"SL4B_Type3Connection",true);SL4B_ConnectionMethod.MULTIPART_REPLACE=new SL4B_ConnectionMethod("XHR Multipart",4,"SL4B_Type4Connection",false);SL4B_ConnectionMethod.FOREVER_FRAME=new SL4B_ConnectionMethod("Forever Frame",5,"SL4B_Type5Connection",false);SL4B_ConnectionMethod.HTML_FILE=new SL4B_ConnectionMethod("HTML File",5,"SL4B_Type6Connection",false);SL4B_ConnectionMethod.WEBSOCKET=new SL4B_ConnectionMethod("Websocket",8,"SL4B_WebsocketConnection",false);SL4B_ConnectionMethod.FLASH=new SL4B_ConnectionMethod("Flash",9,"SL4B_FlashConnection",false);SL4B_ConnectionMethod.XDOMAINREQUEST=new SL4B_ConnectionMethod("XDomainRequest Streaming",11,"SL4B_TypeXDomainRequestConnection",false);SL4B_ConnectionMethod.XDR_XMLHTTPREQUEST=new SL4B_ConnectionMethod("XDR XMLHttpRequest Streaming",12,"SL4B_TypeXDRXMLHttpRequestConnection",false);SL4B_ConnectionMethod.TEST=new SL4B_ConnectionMethod("TEST",99,"SL4B_TestConnection",false);SL4B_ConnectionMethod.AllMethods[2]=SL4B_ConnectionMethod.XHR_STREAMING;SL4B_ConnectionMethod.AllMethods[3]=SL4B_ConnectionMethod.POLLING;SL4B_ConnectionMethod.AllMethods[4]=SL4B_ConnectionMethod.MULTIPART_REPLACE;SL4B_ConnectionMethod.AllMethods[5]=SL4B_ConnectionMethod.FOREVER_FRAME;SL4B_ConnectionMethod.AllMethods[6]=SL4B_ConnectionMethod.HTML_FILE;SL4B_ConnectionMethod.AllMethods[8]=SL4B_ConnectionMethod.WEBSOCKET;SL4B_ConnectionMethod.AllMethods[9]=SL4B_ConnectionMethod.FLASH;SL4B_ConnectionMethod.AllMethods[11]=SL4B_ConnectionMethod.XDOMAINREQUEST;SL4B_ConnectionMethod.AllMethods[12]=SL4B_ConnectionMethod.XDR_XMLHTTPREQUEST;SL4B_ConnectionMethod.AllMethods[99]=SL4B_ConnectionMethod.TEST;var SL4B_Browser=function(){};
if(false){function SL4B_Browser(){}
}SL4B_Browser={};SL4B_Browser.UNKNOWN="UNKNOWN";SL4B_Browser.MSIE="MSIE";SL4B_Browser.SAFARI="Safari";SL4B_Browser.FIREFOX="Firefox";SL4B_Browser.CHROME="Chrome";SL4B_Browser.AllBrowsers=[];SL4B_Browser.AllBrowsers.push(SL4B_Browser.MSIE);SL4B_Browser.AllBrowsers.push(SL4B_Browser.SAFARI);SL4B_Browser.AllBrowsers.push(SL4B_Browser.FIREFOX);SL4B_Browser.AllBrowsers.push(SL4B_Browser.CHROME);SL4B_Browser.isKnownBrowser = function(A){for(var i=0,len=SL4B_Browser.AllBrowsers.length;i<len;++i){if(SL4B_Browser.AllBrowsers[i]===A){return true;
}}return false;
};
var SL4B_AbstractBrowserAdapter=function(){};
if(false){function SL4B_AbstractBrowserAdapter(){}
}SL4B_AbstractBrowserAdapter.prototype.isFirefox = function(){throw new SL4B_Error("isFirefox method not implemented");
};
SL4B_AbstractBrowserAdapter.prototype.isInternetExplorer = function(){throw new SL4B_Error("isInternetExplorer method not implemented");
};
SL4B_AbstractBrowserAdapter.prototype.getBrowserVersion = function(){throw new SL4B_Error("getBrowserVersion method not implemented");
};
SL4B_AbstractBrowserAdapter.prototype.getElementById = function(A){return document.getElementById(A);
};
SL4B_AbstractBrowserAdapter.prototype.getFrameWindow = function(A){throw new SL4B_Error("getFrameWindow method not implemented");
};
SL4B_AbstractBrowserAdapter.prototype.getPollingConnectionTypes = function(){throw new SL4B_Error("getPollingConnectionTypes method not implemented");
};
SL4B_AbstractBrowserAdapter.prototype.getStreamingConnectionTypes = function(){throw new SL4B_Error("getStreamingConnectionTypes method not implemented");
};
SL4B_AbstractBrowserAdapter.prototype.addEventListener = function(A,C,B){throw new SL4B_Error("addEventListener method not implemented");
};
SL4B_AbstractBrowserAdapter.prototype.dump = function(A){};
SL4B_AbstractBrowserAdapter.prototype.convertExceptionToString = function(A){var l_sValue=null;
if(typeof A.getClass!="undefined"){l_sValue=A.getClass()+": "+A.getMessage();}else 
if(typeof A.stack!="undefined"){l_sValue=A.name+": "+A.message+" ("+A.fileName+" [line "+A.lineNumber+"])";}else 
if(typeof A.description!="undefined"){l_sValue=A.name+": "+A.message;}else 
if(typeof A.toString!="undefined"){l_sValue=A.toString();}else 
{l_sValue=A+"";}return l_sValue;
};
var SL4B_BrowserAdapter=function(){};
if(false){function SL4B_BrowserAdapter(){}
}SL4B_BrowserAdapter = function(){this.bIsIE=false;this.bIsChrome=false;this.bIsWebKit=false;this.bIsSafari=false;this.bIsFirefox=false;this.oBrowser=SL4B_Browser.UNKNOWN;this.sBrowserVersion=SL4B_BrowserAdapter.VALUE_NOT_KNOWN;this._detectBrowsers(navigator);if(this.isInternetExplorer()==true){if(window.ActiveXObject){
try {this.m_oTracer.name="SL4B";}catch(e){}
}}};
SL4B_BrowserAdapter.prototype = new SL4B_AbstractBrowserAdapter;SL4B_BrowserAdapter.VALUE_NOT_KNOWN="UNKNOWN";SL4B_BrowserAdapter.prototype._resetBrowserDetection = function(){this.bIsIE=false;this.bIsChrome=false;this.bIsSafari=false;this.bIsWebKit=false;this.bIsFirefox=false;this.oBrowser=SL4B_Browser.UNKNOWN;this.sBrowserVersion=SL4B_BrowserAdapter.VALUE_NOT_KNOWN;};
SL4B_BrowserAdapter.prototype._detectBrowsers = function(A){this.bIsIE=/msie/.test(A.userAgent.toLowerCase());this.bIsChrome=/chrome/.test(A.userAgent.toLowerCase());if(this.bIsChrome==false){this.bIsSafari=/safari/.test(A.userAgent.toLowerCase());this.bIsFirefox=/firefox/.test(A.userAgent.toLowerCase());}this.bIsWebKit=/webkit/.test(A.userAgent.toLowerCase());this.oBrowser=this.getBrowser();this.sBrowserVersion=this._getBrowserVersion(A);};
SL4B_BrowserAdapter.prototype._getBrowserVersion = function(A){var sVersion=SL4B_Browser.UNKNOWN;
if(this.isInternetExplorer()==true){var l_sFullDescription=A.userAgent;
var l_nStartPos=l_sFullDescription.indexOf(this.getBrowser().toString()+" ");
sVersion=l_sFullDescription.substring(l_nStartPos+5,l_sFullDescription.indexOf(";",l_nStartPos));sVersion=sVersion.toLowerCase();}else 
if(this.isSafari()==true){var l_sFullDescription=A.userAgent;
var oRegMatch=new RegExp("(Version\\/)([\\d\\.]*)");
var oMatch=l_sFullDescription.match(oRegMatch);
if(oMatch!=null&&oMatch.length==3){sVersion=oMatch[2];}}else 
if(this.isFirefox()==true||this.isChrome()==true){sVersion=A.vendorSub;if(!sVersion||sVersion==""){var l_sFullDescription=A.userAgent;
var oRegMatch=new RegExp("("+this.getBrowser()+"\\/)([\\d\\.]*)");
var oMatch=l_sFullDescription.match(oRegMatch);
if(oMatch!=null&&oMatch.length==3){sVersion=oMatch[2];}}}else 
if(this.isWebKit()){var l_sFullDescription=A.userAgent;
sVersion=parseFloat(l_sFullDescription.replace(/^.*webkit\/(\d+(\.\d+)?).*$/,"$1")).toString();}return sVersion;
};
SL4B_BrowserAdapter.prototype.getBrowser = function(){if(this.oBrowser==SL4B_Browser.UNKNOWN){if(this.isInternetExplorer()==true){this.oBrowser=SL4B_Browser.MSIE;}else 
if(this.isSafari()==true){this.oBrowser=SL4B_Browser.SAFARI;}else 
if(this.isFirefox()==true){this.oBrowser=SL4B_Browser.FIREFOX;}else 
if(this.isChrome()==true){this.oBrowser=SL4B_Browser.CHROME;}}return this.oBrowser;
};
SL4B_BrowserAdapter.prototype.isFirefox = function(){return this.bIsFirefox;
};
SL4B_BrowserAdapter.prototype.isSafari = function(){return this.bIsSafari;
};
SL4B_BrowserAdapter.prototype.isInternetExplorer = function(){return this.bIsIE;
};
SL4B_BrowserAdapter.prototype.isChrome = function(){return this.bIsChrome;
};
SL4B_BrowserAdapter.prototype.isWebKit = function(){return this.bIsWebKit;
};
SL4B_BrowserAdapter.prototype.getBrowserVersion = function(){return this.sBrowserVersion;
};
SL4B_BrowserAdapter.prototype.addEventListener = function(A,C,B){if(A.addEventListener){A.addEventListener(C,B,false);}else 
if(A.attachEvent){A.attachEvent("on"+C,B);}else 
{throw new SL4B_Error("addEventListener failed");
}};
SL4B_BrowserAdapter.prototype.dump = function(A){if(SL4B_Accessor.getConfiguration().isConsolelogging()){if(window.console&&window.console["info"]){window.console["info"](A);}else 
if(window.console&&window.console["log"]){window.console["log"](A);}else 
if(this.isInternetExplorer()==true){if(this.m_oTracer){this.m_oTracer.trace(A.substr(0,256));}}else 
if(dump){dump(A);}else 
{throw new SL4B_Error("dump failed");
}}};
SL4B_BrowserAdapter.prototype.getFrameWindow = function(A){var oIFrameElement=null;
if(this.isInternetExplorer()==true){oIFrameElement=document.frames[A];oIFrameElement=((typeof oIFrameElement=="undefined") ? null : oIFrameElement);}else 
{oIFrameElement=this.getElementById(A);oIFrameElement=((oIFrameElement==null) ? null : oIFrameElement.contentWindow);}return oIFrameElement;
};
SL4B_BrowserAdapter.prototype.getPollingConnectionTypes = function(){return [SL4B_ConnectionMethod.POLLING];
};
SL4B_BrowserAdapter.prototype.getStreamingConnectionTypes = function(){var l_pStreamingConnectionTypes=[SL4B_ConnectionMethod.FOREVER_FRAME];
if(this.isInternetExplorer()==true){l_pStreamingConnectionTypes=[SL4B_ConnectionMethod.HTML_FILE,SL4B_ConnectionMethod.FOREVER_FRAME];}else 
if(this.isFirefox()==true){l_pStreamingConnectionTypes=[SL4B_ConnectionMethod.MULTIPART_REPLACE,SL4B_ConnectionMethod.FOREVER_FRAME];}return l_pStreamingConnectionTypes;
};
SL4B_Accessor.setBrowserAdapter(new SL4B_BrowserAdapter());var SL4B_WindowEventHandler=function(){};
if(false){function SL4B_WindowEventHandler(){}
}SL4B_WindowEventHandler = new function(){this.m_pRegisteredListeners=new Array();this.m_bLoaded=false;this.addListener = function(A){if(typeof A.onLoad=="function"||typeof A.onUnload=="function"||typeof A.onBeforeUnload=="function"){this.m_pRegisteredListeners.push(A);}else 
{throw new SL4B_Exception("addListener: listener object must implement the onLoad, onBeforeUnload or onUnload methods");
}};
this.removeListener = function(A){for(var i=0,l_nLength=this.m_pRegisteredListeners.length;i<l_nLength;++i){if(this.m_pRegisteredListeners[i]===A){this.m_pRegisteredListeners.splice(i,1);return true;
}}return false;
};
this.initialise = function(){if(this.m_bLoaded){setTimeout(SL_NT,0);}else 
{if(SL4B_Accessor.getConfiguration().isAutoLoadingEnabled()){SL4B_Accessor.getBrowserAdapter().addEventListener(window,"load",function(A){SL_NT(A);});}}SL4B_Accessor.getBrowserAdapter().addEventListener(window,"unload",SL_OS);SL4B_Accessor.getBrowserAdapter().addEventListener(window,"beforeunload",SL_QJ);};
this.onLoad = function(A){SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"WindowEventHandler.onLoad: method invoked");A=this.getEvent(A);for(var l_nListener=0;l_nListener<this.m_pRegisteredListeners.length;l_nListener++){var l_oListener=this.m_pRegisteredListeners[l_nListener];
if(typeof l_oListener.onLoad=="function"){l_oListener.onLoad(A);}}};
this.onUnload = function(A){SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"WindowEventHandler.onUnload: method invoked");A=this.getEvent(A);for(var l_nListener=0;l_nListener<this.m_pRegisteredListeners.length;l_nListener++){var l_oListener=this.m_pRegisteredListeners[l_nListener];
if(typeof l_oListener.onUnload=="function"){l_oListener.onUnload(A);}}};
this.onBeforeUnload = function(A){SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"WindowEventHandler.onBeforeUnload: method invoked");A=this.getEvent(A);for(var l_nListener=0;l_nListener<this.m_pRegisteredListeners.length;l_nListener++){var l_oListener=this.m_pRegisteredListeners[l_nListener];
if(typeof l_oListener.onBeforeUnload=="function"){l_oListener.onBeforeUnload(A);}}};
this.getEvent = function(A){return ((typeof A=="undefined") ? ((typeof event!="undefined") ? event : null) : A);
};
};
function SL_NT(A){SL4B_WindowEventHandler.onLoad(A);}
function SL_OS(A){SL4B_WindowEventHandler.onUnload(A);}
function SL_QJ(A){SL4B_WindowEventHandler.onBeforeUnload(A);}
SL4B_Accessor.getBrowserAdapter().addEventListener(window,"load",function(A){SL4B_WindowEventHandler.m_bLoaded=true;});var TransferSubscription=function(){};
if(false){function TransferSubscription(){}
}TransferSubscription = function(F,E,D,B,C,A){this.m_oSubscription=F;this.m_sObjectName=E;this.m_sFieldList=D;this.m_nCtrStart=B;this.m_nCtrEnd=C;this.m_nCtrId=A;};
TransferSubscription.prototype.getSubscription = function(){return this.m_oSubscription;
};
TransferSubscription.prototype.getObjectName = function(){return this.m_sObjectName;
};
TransferSubscription.prototype.getFields = function(){return this.m_sFieldList;
};
TransferSubscription.prototype.getCtrStart = function(){return this.m_nCtrStart;
};
TransferSubscription.prototype.getCtrEnd = function(){return this.m_nCtrEnd;
};
TransferSubscription.prototype.getCtrId = function(){return this.m_nCtrId;
};
var TransferObject=function(){};
if(false){function TransferObject(){}
}TransferObject = function(){this.m_nId=0;this.m_pSlaves=[];this.m_oSubscriptionManagers={};this.m_pTransferSubscriptions=[];this.m_nLastContainerId=0;};
TransferObject.prototype.set = function(B,D,E,C,A){this.m_nId=B;this.m_pSlaves=D;this.m_oSubscriptionManagers=E;this.m_pTransferSubscriptions=C;this.m_nLastContainerId=A;};
TransferObject.prototype.load = function(A,B){this.m_nId=B.m_oRegistryWindowHandle.getNextSlaveId();this.m_pSlaves=[];for(var i=1;i<B.m_oRegistryWindowHandle.getSlaveWindows().length;i++){
try {var l_oSlave=B.m_oRegistryWindowHandle.getSlaveWindows()[i];
if(!l_oSlave.closed){this.m_pSlaves.push(l_oSlave);}}catch(e){}
}var l_oOldSubscriptionManagerAccessor=A.SL4B_SubscriptionManagerAccessor;
this.m_oSubscriptionManagers=this.copySubscriptionManagers(l_oOldSubscriptionManagerAccessor.getSubscriptionManagers(),A);this.m_pTransferSubscriptions=this.copySubscriptions(A);this.m_nLastContainerId=A.SL4B_ContainerKey.getNextKeyId();};
TransferObject.prototype.copy = function(B,A){this.m_nId=B.m_nId;this.m_pSlaves=[];for(var i=1;i<B.m_pSlaves.length;i++){
try {var l_oSlave=B.m_pSlaves[i];
if(!l_oSlave.closed){this.m_pSlaves.push(l_oSlave);}}catch(e){}
}this.m_oSubscriptionManagers=this.copySubscriptionManagers(B.m_oSubscriptionManagers,A);this.m_pTransferSubscriptions=[];for(var i=0;i<B.m_pTransferSubscriptions.length;i++){var l_oOldTransferObject=B.m_pTransferSubscriptions[i];

try {if(l_oOldTransferObject.getSubscription().getWindow()!=A){this.m_pTransferSubscriptions.push(new TransferSubscription(l_oOldTransferObject.getSubscription(),l_oOldTransferObject.getObjectName(),l_oOldTransferObject.getFields(),l_oOldTransferObject.getCtrStart(),l_oOldTransferObject.getCtrEnd(),l_oOldTransferObject.getCtrId()));}}catch(e){}
}this.m_nLastContainerId=B.m_nLastContainerId;};
TransferObject.prototype.copySubscriptionManagers = function(B,A){var l_oSubscriptionManagers={};
for(l_sSubscriptionMangerId in B){
try {var l_oOldSubscriptionManager=B[l_sSubscriptionMangerId];
if(l_oOldSubscriptionManager.getWindow()!=A){var slaveWindow=l_oOldSubscriptionManager.getWindow();
if(!slaveWindow.closed&&!slaveWindow.SL4B_WindowRegistrar.isUnloading()){l_oSubscriptionManagers[l_sSubscriptionMangerId]=l_oOldSubscriptionManager;}}}catch(e){}
}return l_oSubscriptionManagers;
};
TransferObject.prototype.copySubscriptions = function(A){var l_pTranferSubscriptions=[];
var l_oOldJavaScriptProvider=A.SL4B_Accessor.getUnderlyingRttpProvider();
var l_pOldObjectSubscriptionManager=l_oOldJavaScriptProvider.getSubscriptionManager();
var l_pOldObjectSubscriptions=l_pOldObjectSubscriptionManager.getObjectSubscriptions();
var l_oOldContainerIdToRequestDataMap=l_pOldObjectSubscriptionManager.getContainerIdToRequestDataMap();
var l_pObjectSubscriptions={};
var l_mSubscriptionIdToContainerRequestData={};
for(l_nContainerId in l_oOldContainerIdToRequestDataMap){var l_oContainerRequestData=l_oOldContainerIdToRequestDataMap[l_nContainerId];
l_mSubscriptionIdToContainerRequestData[l_oContainerRequestData.getSubscriberId()]=l_oContainerRequestData;}for(l_sObjectKey in l_pOldObjectSubscriptions){var l_pListenerMap=l_pOldObjectSubscriptions[l_sObjectKey];
for(l_oListenerId in l_pListenerMap){
try {var l_oObjectSubscription=l_pListenerMap[l_oListenerId];
if(l_oObjectSubscription.getSubscriber().getWindow()!=A){var slaveWindow=l_oObjectSubscription.getSubscriber().getWindow();
if(!slaveWindow.closed&&!slaveWindow.SL4B_WindowRegistrar.isUnloading()){var l_sFieldList=SL4B_ObjectCache.createFilterAndFieldList(l_oObjectSubscription.getFilter(),l_oObjectSubscription.getFieldMask().getFieldList_deprecated());
l_sFieldList=unescape(l_sFieldList.replace(/^;/,""));var l_nCtrStart=-1;
var l_nCtrEnd=-1;
var l_nCtrId=-1;
var l_oContainerRequestData=l_mSubscriptionIdToContainerRequestData[l_oObjectSubscription.getSubscriptionId()];
if(l_oContainerRequestData!=null){l_nCtrStart=l_oContainerRequestData.getWindowStart();l_nCtrEnd=l_oContainerRequestData.getWindowEnd();l_sFieldList=l_oContainerRequestData.getFieldList();l_nCtrId=l_oContainerRequestData.getContainerId();}l_pTranferSubscriptions.push(new TransferSubscription(l_oObjectSubscription.getSubscriber(),l_oObjectSubscription.getObjectName(),l_sFieldList,l_nCtrStart,l_nCtrEnd,l_nCtrId));}}}catch(e){}
}}return l_pTranferSubscriptions;
};
TransferObject.prototype.updateSlaveList = function(){var l_pSlaveWindows=[];
for(var i=0;i<this.m_pSlaves.length;i++){
try {if(this.m_pSlaves[i]!=null&&!this.m_pSlaves[i].closed&&!this.m_pSlaves[i].SL4B_WindowRegistrar.isUnloading()){l_pSlaveWindows.push(this.m_pSlaves[i]);}}catch(e){}
}this.m_pSlaves=l_pSlaveWindows;};
TransferObject.prototype.getId = function(){return this.m_nId;
};
TransferObject.prototype.getSlaves = function(){return this.m_pSlaves;
};
TransferObject.prototype.getSubscriptionManagers = function(){return this.m_oSubscriptionManagers;
};
TransferObject.prototype.getTransferSubscriptions = function(){return this.m_pTransferSubscriptions;
};
TransferObject.prototype.getLastContainerId = function(){return this.m_nLastContainerId;
};
var SL4B_FrameRegistrarAccessor = new function(){this.m_oFrameRegistrar=null;this.setCommonContainerDomain = function(A){SL4B_Accessor.getLogger().log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"SL4B_FrameRegistrarAccessor.setCommonContainerDomain: domain \"{0}\"",A);var l_sContainerFrameLocation=SL4B_Accessor.getConfiguration().getContainerFrameLocation();
var l_oContainerFrame=eval(l_sContainerFrameLocation);
if(window!=l_oContainerFrame){
try {l_oContainerFrame.document.domain=A;SL4B_Accessor.getLogger().log(SL4B_DebugLevel.const_INFO_INT,"SL4B_FrameRegistrarAccessor.setCommonContainerDomain: container frame domain set to \"{0}\"",A);}catch(e){SL4B_Accessor.getLogger().log(SL4B_DebugLevel.const_INFO_INT,"SL4B_FrameRegistrarAccessor.setCommonContainerDomain: failed to set container frame domain to \"{0}\"",A);}
}else 
{SL4B_Accessor.getLogger().log(SL4B_DebugLevel.const_INFO_INT,"SL4B_FrameRegistrarAccessor.setCommonContainerDomain: container frame domain was not set, this page is the container frame");}};
this.getFrameRegistrar = function(){if(this.m_oFrameRegistrar==null){var l_sContainerFrameLocation=SL4B_Accessor.getConfiguration().getContainerFrameLocation();
var l_oContainerFrame=eval(l_sContainerFrameLocation);
if(typeof l_oContainerFrame.SL4B_FrameRegistrar=="undefined"){l_oContainerFrame.SL4B_FrameRegistrar=new SL_LG();}this.m_oFrameRegistrar=l_oContainerFrame.SL4B_FrameRegistrar;}return this.m_oFrameRegistrar;
};
this.setMasterFrame = function(A){this.getFrameRegistrar().setMasterFrame(A);};
this.removeMasterFrame = function(){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"FrameRegistrarAccessor.removeMasterFrame");this.getFrameRegistrar().removeMasterFrame();};
this.registerSlaveFrame = function(B,A){this.getFrameRegistrar().registerSlaveFrame(B,A);};
this.deregisterSlaveFrame = function(A){
try {this.getFrameRegistrar().deregisterSlaveFrame(A);}catch(e){}
};
};
function SL_LG(){this.m_oMasterFrameRttpProvider=null;this.m_pRegisteredSlaveFrames=new Object();this.setMasterFrame = function(A){this.m_oMasterFrameRttpProvider=A;for(l_sFrameId in this.m_pRegisteredSlaveFrames){var l_oSlave=this.m_pRegisteredSlaveFrames[l_sFrameId];
l_oSlave.masterRegistered(this.m_oMasterFrameRttpProvider);}};
this.removeMasterFrame = function(){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"FrameRegistrarAccessor.removeMasterFrame");this.m_oMasterFrameRttpProvider=null;for(l_sFrameId in this.m_pRegisteredSlaveFrames){var l_oSlave=this.m_pRegisteredSlaveFrames[l_sFrameId];
l_oSlave.masterClosing();}};
this.registerSlaveFrame = function(B,A){this.m_pRegisteredSlaveFrames[B]=A;if(this.m_oMasterFrameRttpProvider!=null){A.masterRegistered(this.m_oMasterFrameRttpProvider);}};
this.deregisterSlaveFrame = function(A){delete (this.m_pRegisteredSlaveFrames[A]);};
}
if(false){function SL4B_WindowRegistrar(){}
}{SL4B_WindowRegistrar = function(){this.initialiseVariables();};
SL4B_WindowRegistrar.prototype.initialiseVariables = function(){this.m_bIsMaster=null;this.m_bBecomingMaster=false;this.m_bLoggedIn=false;this.m_bUnLoaded=false;this.m_bMasterRegistered=false;this.m_bUnloading=false;this.m_sRegistrationWindowName=null;this.m_oRegistryWindowHandle=null;this.m_sWindowId=null;this.m_eFrame=null;this.m_nRetries=0;this.m_oTransferObject=new TransferObject();this.m_sSlaveName="";this.m_nAwaitingSlaves=0;};
SL4B_WindowRegistrar.prototype.ready = function(){SL4B_Accessor.getRttpProvider().addConnectionListener(this);};
SL4B_WindowRegistrar.prototype.connectionWarning = function(C,D,B,A){};
SL4B_WindowRegistrar.prototype.connectionInfo = function(A){};
SL4B_WindowRegistrar.prototype.connectionAttempt = function(B,A){};
SL4B_WindowRegistrar.prototype.connectionOk = function(C,A,B){};
SL4B_WindowRegistrar.prototype.reconnectionOk = function(){};
SL4B_WindowRegistrar.prototype.fileDownloadError = function(B,A){};
SL4B_WindowRegistrar.prototype.loginOk = function(){};
SL4B_WindowRegistrar.prototype.loginError = function(A){};
SL4B_WindowRegistrar.prototype.credentialsRetrieved = function(A){};
SL4B_WindowRegistrar.prototype.credentialsProviderSessionError = function(A,D,C,B){};
SL4B_WindowRegistrar.prototype.message = function(A,B){};
SL4B_WindowRegistrar.prototype.connectionError = function(A){};
SL4B_WindowRegistrar.prototype.serviceMessage = function(A,D,B,C){};
SL4B_WindowRegistrar.prototype.sourceMessage = function(A,C,D,B){};
SL4B_WindowRegistrar.prototype.statistics = function(A,D,E,B,C){};
SL4B_WindowRegistrar.prototype.sessionEjected = function(A,B){if(this.m_bIsMaster){if(this.m_oCookieUpdateTimer){clearInterval(this.m_oCookieUpdateTimer);}this.log("SL4B_WindowRegistrar.sessionEjected(): Session ejected, clear cookie update timer and clear cookies");this.set_Cookie(SL4B_WindowRegistrar.MASTER_WINDOW_ALLOCATED,0);this.set_Cookie(SL4B_WindowRegistrar.REGISTRATION_FRAME_HREF,SL4B_WindowRegistrar.COOKIE_VALUE_FALSE);document.body.removeChild(this.m_eFrame);SL4B_WindowEventHandler.removeListener(this);this.m_oRegistryWindowHandle=null;this.m_eFrame=null;}};
SL4B_WindowRegistrar.prototype.initialise = function(){this.m_sRegistrationWindowName=SL4B_Accessor.getConfiguration().getRegistrationWindowName();SL4B_WindowEventHandler.addListener(this);};
SL4B_WindowRegistrar.prototype.onLoad = function(){if(this.m_sRegistrationWindowName){if(this.isMaster()){this._createIFrame();}else 
{this._registerSlaveWindow(null);}}};
SL4B_WindowRegistrar.prototype._isMultiWindowingSupported = function(){if(document.mimeType&&document.mimeType=="application/xhtml+xml"){return false;
}if(document.contentType&&document.contentType=="application/xhtml+xml"){return false;
}return true;
};
SL4B_WindowRegistrar.prototype.isMaster = function(){if(this.m_sRegistrationWindowName&&!SL4B_Accessor.getBrowserAdapter().isChrome()&&this._isMultiWindowingSupported()){if(this.m_bIsMaster===null){SL4B_WindowRegistrar.MASTER_WINDOW_ALLOCATED+=this.m_sRegistrationWindowName;SL4B_WindowRegistrar.REGISTRATION_FRAME_HREF+=this.m_sRegistrationWindowName;this._setMaster(false);var l_bMaster=this.get_Cookie(SL4B_WindowRegistrar.MASTER_WINDOW_ALLOCATED);
if(l_bMaster==null||this._masterCookieExpired(l_bMaster)){this._setMaster(true);this._updateMasterCookie();}}return this.m_bIsMaster;
}else 
{return true;
}};
SL4B_WindowRegistrar.prototype._masterCookieExpired = function(A){var l_nCookieTime=parseInt(A);
var l_nNowTime=(new Date()).getTime();
var l_nDiffTime=l_nNowTime-l_nCookieTime;
return (l_nDiffTime>(SL4B_WindowRegistrar.MASTER_COOKIE_KEEP_ALIVE_INTERVAL+500));
};
SL4B_WindowRegistrar.prototype._setMaster = function(A){this.m_bIsMaster=A;if(A==true){var self=this;
this.m_oCookieUpdateTimer=setInterval(function(){self._updateMasterCookie();},SL4B_WindowRegistrar.MASTER_COOKIE_KEEP_ALIVE_INTERVAL);}};
SL4B_WindowRegistrar.prototype._updateMasterCookie = function(){this.set_Cookie(SL4B_WindowRegistrar.MASTER_WINDOW_ALLOCATED,(new Date()).getTime());this.set_Cookie(SL4B_WindowRegistrar.REGISTRATION_FRAME_HREF,escape(this.m_sHref));};
SL4B_WindowRegistrar.prototype._createIFrame = function(){if(this._isMultiWindowingSupported()){this.log("SL4B_WindowRegistrar._createIFrame()");this.m_eFrame=document.createElement("div");var sFragmentId=this._getFragmentIdSymbols();
var l_sUrl="<iframe id=\""+SL4B_WindowRegistrar.FRAME_ID+"\" name=\""+this.m_sRegistrationWindowName+"\" src=\""+SL4B_ScriptLoader.getRootUrl()+"sl4b/rttp-provider/window-registry.html"+sFragmentId+"register?domain="+document.domain+"\"></iframe>";
this.m_eFrame.innerHTML=l_sUrl;var showMasterFrameConfig=SL4B_Accessor.getConfiguration().getParameterFromQueryString(SL4B_WindowRegistrar.SHOW_MASTER_IFRAME_QUERY_STRING);
if(showMasterFrameConfig===null){this.m_eFrame.style.visibility="hidden";}document.body.appendChild(this.m_eFrame);}};
SL4B_WindowRegistrar.prototype._getFragmentIdSymbols = function(){if(SL4B_Accessor.getConfiguration().isRedirectionEnabled()&&SL4B_Accessor.getBrowserAdapter().isInternetExplorer()||SL4B_Accessor.getBrowserAdapter().isSafari()){return "%23";
}else 
{return "#";
}};
SL4B_WindowRegistrar.prototype.onUnload = function(){if(this.m_bUnloading==false){this._$handleUnloading();}};
SL4B_WindowRegistrar.prototype.onBeforeUnload = function(){if(SL4B_Accessor.getConfiguration().getCleanupOnBeforeUnload()===true&&SL4B_Accessor.getBrowserAdapter().isFirefox()){this._$handleUnloading();}};
SL4B_WindowRegistrar.prototype._$handleUnloading = function(){this.m_bUnloading=true;this.log("SL4B_WindowRegistrar.onUnload()");if(this.m_bIsMaster){this.log("SL4B_WindowRegistrar.onUnload(): Passing master responsiblity to any available slave");this.set_Cookie(SL4B_WindowRegistrar.MASTER_WINDOW_ALLOCATED,0);this.set_Cookie(SL4B_WindowRegistrar.REGISTRATION_FRAME_HREF,SL4B_WindowRegistrar.COOKIE_VALUE_FALSE);var l_pSlaveWindows=[];
if(this.m_bBecomingMaster){this.m_oTransferObject.updateSlaveList();l_pSlaveWindows=this.m_oTransferObject.getSlaves();}else 
{if(this.m_oRegistryWindowHandle){l_pSlaveWindows=this.m_oRegistryWindowHandle.getSlaveWindows();}}if(l_pSlaveWindows.length>0){var l_oNewMaster=l_pSlaveWindows[0];
var l_oNewMasterFrameRegistrar=l_oNewMaster.SL4B_WindowRegistrar;
this.clearContainers();l_oNewMasterFrameRegistrar.becomeMasterWindow(this,window);}}else 
{this.log("SL4B_WindowRegistrar.onUnload(): Deregistering myself from the master as a slave");
try {if(this.m_oRegistryWindowHandle){this.m_oRegistryWindowHandle.removeWindow(window,this.m_sSlaveName);}}catch(e){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_ERROR_INT,"SL4B_WindowRegistrar.onUnload(): An error occured in deregistering myself from the master - ({0})",e);}
}this.m_bUnLoaded=true;};
SL4B_WindowRegistrar.prototype.becomeMasterWindow = function(B,A){this.log("SL4B_WindowRegistrar.becomeMasterWindow()");this.m_bBecomingMaster=true;if(!this.m_bUnLoaded){this.log("SL4B_WindowRegistrar.becomeMasterWindow(): Setting Master_Window_Allocated cookie");this._updateMasterCookie();}this.copySL4BState(B,A);SL4B_ContainerKey.setNextKeyId(this.m_oTransferObject.getLastContainerId());this._setMaster(true);this._createIFrame();var l_oSlaveFrameWrapperProvider=SL4B_Accessor.getRttpProvider();
l_oSlaveFrameWrapperProvider.becomeMasterProvider(A);};
SL4B_WindowRegistrar.prototype.loginOK = function(){this.log("SL4B_WindowRegistrar.loginOK()");if(this.m_bBecomingMaster){this.copySubscriptionManagerAccessor(this.m_oTransferObject.getSubscriptionManagers());this.m_bLoggedIn=true;this.registerSlaves();this.requestObjects(window);}};
SL4B_WindowRegistrar.prototype.requestObjects = function(A){this.log("SL4B_WindowRegistrar.requestObjects()");var l_oSlaveFrameWrapperProvider=SL4B_Accessor.getRttpProvider();
var l_pSubscriptions=this.m_oTransferObject.getTransferSubscriptions();
for(var i=0;i<l_pSubscriptions.length;i++){var l_oTransferSubscription=l_pSubscriptions[i];
var l_oSubscriptionWindow=null;

try {l_oSubscriptionWindow=l_oTransferSubscription.getSubscription().getWindow();}catch(e){continue;
}
if(l_oSubscriptionWindow==A){if(l_oTransferSubscription.getCtrStart()!=-1){var l_oSubscriptionManager=SL4B_JavaScriptRttpProvider.oObjectSubscriptionManager;
l_oSubscriptionManager.getContainer(l_oTransferSubscription.getSubscription(),l_oTransferSubscription.getObjectName(),l_oTransferSubscription.getFields(),l_oTransferSubscription.getCtrStart(),l_oTransferSubscription.getCtrEnd(),l_oTransferSubscription.getCtrId());this.log("\trequest container "+l_oTransferSubscription.getObjectName()+" "+l_oTransferSubscription.getFields()+l_oTransferSubscription.getCtrStart()+", "+l_oTransferSubscription.getCtrEnd());}else 
{l_oSlaveFrameWrapperProvider.getObject(l_oTransferSubscription.getSubscription(),l_oTransferSubscription.getObjectName(),l_oTransferSubscription.getFields());this.log("\trequest "+l_oTransferSubscription.getObjectName()+" "+l_oTransferSubscription.getFields());}}}};
SL4B_WindowRegistrar.prototype.registerSlaves = function(){this.log("SL4B_WindowRegistrar.registerSlaves()");if(this.m_bMasterRegistered&&this.m_bLoggedIn){for(var i=0;i<this.m_oTransferObject.getSlaves().length;i++){
try {var l_oSlaveWindow=this.m_oTransferObject.getSlaves()[i];
l_oSlaveFrameRegistrar=l_oSlaveWindow.SL4B_WindowRegistrar;l_oSlaveFrameRegistrar._registerSlaveWindow(window);}catch(e){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_ERROR_INT,"SL4B_WindowRegistrar.registerSlaves(): An error occured in registering slave with new master - ({0})",e);}
}if(this.m_oTransferObject.getSlaves().length==0){this.m_bBecomingMaster=false;}else 
{var self=this;
setTimeout(function(){if(self.m_bBecomingMaster){self.m_bBecomingMaster=false;self.log("SL4B_WindowRegistrar.registerSlaves(): Set to master after "+SL4B_WindowRegistrar.TIME_TO_WAIT_FOR_SLAVES_TO_RECONNECT+"ms");}},SL4B_WindowRegistrar.TIME_TO_WAIT_FOR_SLAVES_TO_RECONNECT);}}};
SL4B_WindowRegistrar.prototype.copySubscriptionManagerAccessor = function(A){this.log("SL4B_WindowRegistrar.copySubscriptionManagerAccessor()");for(l_sSubscriptionMangerId in A){SL4B_SubscriptionManagerAccessor.getSubscriptionManagers()[l_sSubscriptionMangerId]=A[l_sSubscriptionMangerId];}};
SL4B_WindowRegistrar.prototype.copySL4BState = function(B,A){this.log("SL4B_WindowRegistrar.copySL4BState()");this.m_oTransferObject=new TransferObject();if(B.m_bBecomingMaster){this.m_oTransferObject.copy(B.m_oTransferObject,A);}else 
{this.m_oTransferObject.load(A,B);}this.m_nAwaitingSlaves=this.m_oTransferObject.getSlaves().length;};
SL4B_WindowRegistrar.prototype.clearContainers = function(){this.log("SL4B_WindowRegistrar.clearContainers()");var l_oJavaScriptProvider=SL4B_Accessor.getUnderlyingRttpProvider();
var l_oCache=l_oJavaScriptProvider.getObjectCache();
l_oCache.clearContainer();};
SL4B_WindowRegistrar.prototype.set_Cookie = function(A,B){this.log("SL4B_WindowRegistrar.set_Cookie(): Setting cookie "+A+"="+B);document.cookie=A+"="+B+"; path=/; domain="+document.domain+";";};
SL4B_WindowRegistrar.prototype.get_Cookie = function(A){this.log("SL4B_WindowRegistrar.get_Cookie(): Getting cookie: "+A);var l_sCookieValue=document.cookie.match('(^|;) ?'+A+'=([^;]*)(;|$)');
if(l_sCookieValue){return (unescape(l_sCookieValue[2]));
}else 
{return null;
}};
SL4B_WindowRegistrar.prototype.delete_Cookie = function(A){this.log("SL4B_WindowRegistrar.delete_Cookie(): Deleting cookie: "+A);if(this.get_Cookie(A)){this.log("WindowRegistrar: Deleted cookie: "+A);document.cookie=A+"="+"; path=/; domain="+document.domain+"; expires=Thu, 01-Jan-1970 00:00:01 GMT";}};
SL4B_WindowRegistrar.prototype._registerSlaveWindow = function(A){this.log("SL4B_WindowRegistrar._registerSlaveWindow()");if(!this.m_bIsMaster){var self=this;
setTimeout(function(){self.onIFrameLoaded(A);},SL4B_WindowRegistrar.TIME_TO_WAIT_BEFORE_REGISTERING_WITH_REGISTRAR_FRAME);}};
SL4B_WindowRegistrar.prototype.onIFrameLoaded = function(A){this.log("SL4B_WindowRegistrar.onIFrameLoaded()");
try {var l_sHref=this.get_Cookie(SL4B_WindowRegistrar.REGISTRATION_FRAME_HREF);
if(l_sHref!=null&&l_sHref!=SL4B_WindowRegistrar.COOKIE_VALUE_FALSE){this.m_oRegistryWindowHandle=this._openWindow(l_sHref,this.m_sRegistrationWindowName);var self=this;
setTimeout(function(){self.onIFrameReady(A);},SL4B_WindowRegistrar.TIME_TO_WAIT_BEFORE_REGISTERING_WITH_REGISTRAR_FRAME);}else 
{this.m_nRetries++;if(this.m_nRetries<61){this.log("SL4B_WindowRegistrar.onIFrameLoaded(): Href of Iframe is not available - retrying for "+this.m_nRetries+" time.");var self=this;
setTimeout(function(){self.onIFrameLoaded(A);},SL4B_WindowRegistrar.TIME_TO_WAIT_BEFORE_REGISTERING_WITH_REGISTRAR_FRAME);}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_ERROR_INT,"SL4B_WindowRegistrar:onIFrameLoaded(): The maximum number of retries have been reached. Could not connect to the Master.");}}}catch(e){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_ERROR_INT,"SL4B_WindowRegistrar:onIFrameLoaded(): An error occured - ({0})",e);}
};
SL4B_WindowRegistrar.prototype.onIFrameReady = function(A){this.log("SL4B_WindowRegistrar.onIFrameReady()");
try {if(this.m_oRegistryWindowHandle&&this.m_oRegistryWindowHandle.registerWindow){var l_sSlaveName=this.m_oRegistryWindowHandle.registerWindow(window,A);
if(l_sSlaveName!=null){if(A!=null){A.SL4B_WindowRegistrar.requestObjects(window);}this.m_sSlaveName=l_sSlaveName;SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINER_INT,"Window name is: "+l_sSlaveName);var master=this.m_oRegistryWindowHandle.getOwner().SL4B_WindowRegistrar;
var l_bIsSlaveWindowBeingWaitedFor=this.arrayContains(master.m_oTransferObject.getSlaves(),window);
if(master.m_bBecomingMaster&&l_bIsSlaveWindowBeingWaitedFor){master.m_nAwaitingSlaves--;if(master.m_nAwaitingSlaves==0){master.m_bBecomingMaster=false;}}this.log("WindowRegistrar.onIFrameReady(): Registered slave SL4B: "+window.location.href);}}else 
{if(!this.m_oRegistryWindowHandle){this.log("WindowRegistrar.onIFrameReady(): this.m_oRegistryWindowHandle == null - retrying");}if(!this.m_oRegistryWindowHandle.registerWindow){this.log("WindowRegistrar.onIFrameReady(): this.m_oRegistryWindowHandle.registerWindow == null - retrying");}var self=this;
setTimeout(function(){self.onIFrameReady(A);},SL4B_WindowRegistrar.TIME_TO_WAIT_BEFORE_REGISTERING_WITH_REGISTRAR_FRAME);}}catch(e){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_ERROR_INT,"SL4B_WindowRegistrar.onIFrameReady(): An error occured - ({0})",e);}
};
SL4B_WindowRegistrar.prototype.arrayContains = function(B,A){for(var i=0;i<B.length;i++){if(B[i]===A){return true;
}}return false;
};
SL4B_WindowRegistrar.prototype.registryWindowLoaded = function(){this.log("SL4B_WindowRegistrar.registryWindowLoaded()");this.m_sHref=this._getHRef();this.m_oRegistryWindowHandle=this._openWindow(this.m_sHref,this.m_sRegistrationWindowName);this.set_Cookie(SL4B_WindowRegistrar.REGISTRATION_FRAME_HREF,escape(this.m_sHref));this.m_oRegistryWindowHandle.setOwner(window,this.m_oTransferObject.getId());this.m_bMasterRegistered=true;this.registerSlaves();};
SL4B_WindowRegistrar.prototype._getHRef = function(){this.log("SL4B_WindowRegistrar._getHRef()");var l_sHref="";
if(document.frames){l_sHref=document.frames[SL4B_WindowRegistrar.FRAME_ID].location.href;}else 
{l_sHref=document.getElementById(SL4B_WindowRegistrar.FRAME_ID).contentDocument.location.href;}return l_sHref;
};
SL4B_WindowRegistrar.prototype._openWindow = function(B,A){this.log("WindowRegistrar._openWindow(): "+window.location.href+" for RegistrationWindowName: "+A);return window.open(B,A);
};
SL4B_WindowRegistrar.prototype.setMasterWindow = function(A,B){this.log("WindowRegistrar.setMasterWindow()");this.m_sWindowId=B;SL4B_Accessor.getRttpProvider().setMasterWindow(A,B);};
SL4B_WindowRegistrar.prototype.getWindowId = function(){this.log("WindowRegistrar.getWindowId()");if(this.m_sRegistrationWindowName===null){return SL4B_Accessor.getConfiguration().getFrameId();
}return this.m_sWindowId;
};
SL4B_WindowRegistrar.prototype.log = function(A){if(typeof (dump)!="undefined"){dump((new Date())+" "+this.m_sSlaveName+" "+A+"\n");}var m_oLogElement=document.getElementById("SL4B_WindowRegistrar_Logger");
if(m_oLogElement!=null){m_oLogElement.value+=A+"\n";}SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,this.m_sSlaveName+" "+A);};
SL4B_WindowRegistrar.prototype._$getPageName = function(){return (this.m_sSlaveName==="") ? "master" : this.m_sSlaveName;
};
SL4B_WindowRegistrar.prototype.getMasterWindow = function(){if(this.m_oRegistryWindowHandle){return this.m_oRegistryWindowHandle.getOwner();
}else 
{return null;
}};
SL4B_WindowRegistrar.prototype.isUnloading = function(){return this.m_bUnloading;
};
SL4B_WindowRegistrar=new SL4B_WindowRegistrar();SL4B_WindowRegistrar.MASTER_WINDOW_ALLOCATED="com.caplin.masterwindowallocated";SL4B_WindowRegistrar.REGISTRATION_FRAME_HREF="com.caplin.registrationframehref";SL4B_WindowRegistrar.COOKIE_VALUE_TRUE="true";SL4B_WindowRegistrar.COOKIE_VALUE_FALSE="false";SL4B_WindowRegistrar.TIME_TO_WAIT_FOR_SLAVES_TO_RECONNECT="5000";SL4B_WindowRegistrar.TIME_TO_WAIT_BEFORE_REGISTERING_WITH_REGISTRAR_FRAME="500";SL4B_WindowRegistrar.FRAME_ID="comcaplinregisterframe";SL4B_WindowRegistrar.SHOW_MASTER_IFRAME_QUERY_STRING="showregistrationwindow";SL4B_WindowRegistrar.MASTER_COOKIE_KEEP_ALIVE_INTERVAL=5000;}if(false){function SL4B_RttpUtils(){}
}SL4B_RttpUtils = function(){};
SL4B_RttpUtils.prototype.createFieldListForContainer = function(A,C,D,B){if(A===null||A===undefined){A="default";}var l_sCombinedFieldList="ctrid="+A;
if(D!==undefined&&B!==undefined){l_sCombinedFieldList+=",ctrstart="+D+",ctrend="+B;}if(C&&C!=""){l_sCombinedFieldList+=","+C;}return l_sCombinedFieldList;
};
SL4B_RttpUtils.prototype.getListener = function(A){var l_sListener;
if(A==null||typeof A=="undefined"){throw new SL4B_Exception("Subscriber cannot be null or undefined");
}else 
if(typeof A=="object"&&A.getIdentifier!==undefined){l_sListener=A.getIdentifier();}else 
if(typeof A=="string"){l_sListener=A;}else 
{throw new SL4B_Exception("Illegal Subscriber \""+A+"\" defined");
}return l_sListener;
};
SL4B_RttpUtils=new SL4B_RttpUtils();var SL4B_AbstractRttpProvider=function(){};
if(false){function SL4B_AbstractRttpProvider(){}
}SL4B_AbstractRttpProvider = function(){this.m_pConnectionListeners=new Array();this.m_oLiberatorConfiguration=null;this.m_fOnBeforeCloseEventHandler=null;};
SL4B_AbstractRttpProvider.prototype.const_OK_CONNECTION_EVENT = "connectionOk";SL4B_AbstractRttpProvider.prototype.const_RECONNECTION_OK_CONNECTION_EVENT = "reconnectionOk";SL4B_AbstractRttpProvider.prototype.const_WARNING_CONNECTION_EVENT = "connectionWarning";SL4B_AbstractRttpProvider.prototype.const_ERROR_CONNECTION_EVENT = "connectionError";SL4B_AbstractRttpProvider.prototype.const_INFO_CONNECTION_EVENT = "connectionInfo";SL4B_AbstractRttpProvider.prototype.const_ATTEMPT_CONNECTION_EVENT = "connectionAttempt";SL4B_AbstractRttpProvider.prototype.const_FILE_DOWNLOAD_ERROR_CONNECTION_EVENT = "fileDownloadError";SL4B_AbstractRttpProvider.prototype.const_LOGIN_OK_CONNECTION_EVENT = "loginOk";SL4B_AbstractRttpProvider.prototype.const_LOGIN_ERROR_CONNECTION_EVENT = "loginError";SL4B_AbstractRttpProvider.prototype.const_CREDENTIALS_RETRIEVED_CONNECTION_EVENT = "credentialsRetrieved";SL4B_AbstractRttpProvider.prototype.const_CREDENTIALS_PROVIDER_SESSION_ERROR_CONNECTION_EVENT = "credentialsProviderSessionError";SL4B_AbstractRttpProvider.prototype.const_MESSAGE_CONNECTION_EVENT = "message";SL4B_AbstractRttpProvider.prototype.const_SOURCE_MESSAGE_CONNECTION_EVENT = "sourceMessage";SL4B_AbstractRttpProvider.prototype.const_SERVICE_MESSAGE_CONNECTION_EVENT = "serviceMessage";SL4B_AbstractRttpProvider.prototype.const_SESSION_EJECTED_CONNECTION_EVENT = "sessionEjected";SL4B_AbstractRttpProvider.prototype.const_STATISTICS_CONNECTION_EVENT = "statistics";SL4B_AbstractRttpProvider.const_OBJECT_NAME_DELIMITER=" ";SL4B_AbstractRttpProvider.prototype.getListener = SL_AP;SL4B_AbstractRttpProvider.prototype.internalInitialise = SL_MT;SL4B_AbstractRttpProvider.prototype.initialise = function(){throw new SL4B_Error("initialise method not implemented");
};
SL4B_AbstractRttpProvider.prototype.internalStop = SL_QR;SL4B_AbstractRttpProvider.prototype.stop = SL_DA;SL4B_AbstractRttpProvider.prototype.register = SL_JD;SL4B_AbstractRttpProvider.prototype.registerSlave = SL_AE;SL4B_AbstractRttpProvider.prototype.deregisterSlave = SL_EM;SL4B_AbstractRttpProvider.prototype.getLiberatorConfiguration = function(){return this.m_oLiberatorConfiguration;
};
SL4B_AbstractRttpProvider.prototype.connect = function(){throw new SL4B_Error("connect method not implemented");
};
SL4B_AbstractRttpProvider.prototype.reconnect = function(){throw new SL4B_Error("reconnect method not implemented");
};
SL4B_AbstractRttpProvider.prototype.connected = function(){SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"Initial connection to the Liberator established.");
try {SL4B_Accessor.getCredentialsProvider().getCredentials(this);}catch(e){SL4B_Accessor.getExceptionHandler().processException(e);}
};
SL4B_AbstractRttpProvider.prototype.login = function(A,B){throw new SL4B_Error("login method not implemented");
};
SL4B_AbstractRttpProvider.prototype.loggedIn = SL_QA;SL4B_AbstractRttpProvider.prototype.credentialsRetrieved = SL_MF;SL4B_AbstractRttpProvider.prototype.getObject = function(C,B,A){throw new SL4B_Error("getObject method not implemented");
};
SL4B_AbstractRttpProvider.prototype.getObjects = function(C,A,B){throw new SL4B_Error("getObjects method not implemented");
};
SL4B_AbstractRttpProvider.prototype.removeObject = function(C,B,A){throw new SL4B_Error("removeObject method not implemented");
};
SL4B_AbstractRttpProvider.prototype.removeObjects = function(C,A,B){throw new SL4B_Error("removeObjects method not implemented");
};
SL4B_AbstractRttpProvider.prototype.removeSubscriber = function(A){throw new SL4B_Error("removeSubscriber method not implemented");
};
SL4B_AbstractRttpProvider.prototype.getFilteredObject = function(E,C,B,D,A){this.getObject(E,C,this.createFieldListWithFilter(B,D,A));};
SL4B_AbstractRttpProvider.prototype.getFilteredObjects = function(E,A,C,D,B){this.getObjects(E,A,this.createFieldListWithFilter(C,D,B));};
SL4B_AbstractRttpProvider.prototype.removeFilteredObject = function(E,C,B,D,A){this.removeObject(E,C,this.createFieldListWithFilter(B,D,A));};
SL4B_AbstractRttpProvider.prototype.removeFilteredObjects = function(E,A,C,D,B){this.removeObjects(E,A,this.createFieldListWithFilter(C,D,B));};
SL4B_AbstractRttpProvider.prototype.createFieldListWithFilter = function(B,C,A){if(typeof C=="undefined"||C==null){C=true;}var l_sCombinedFieldList=(C ? "filter=" : "imagefilter=")+B;
if(A&&A!=""){l_sCombinedFieldList+=","+A;}return l_sCombinedFieldList;
};
SL4B_AbstractRttpProvider.prototype.createFieldListForAutoDirectory = function(B,C,A){var l_sCombinedFieldList="auto=1,monitor=1";
if(B&&B!=""){l_sCombinedFieldList+=","+this.createFieldListWithFilter(B,C,A);}else 
if(A&&A!=""){l_sCombinedFieldList+=","+A;}return l_sCombinedFieldList;
};
SL4B_AbstractRttpProvider.prototype.createFieldListForContainer = function(A,C,D,B){return SL4B_RttpUtils.createFieldListForContainer(A,C,D,B);
};
SL4B_AbstractRttpProvider.prototype.getFilteredNewsHeadline = function(C,B,A){this.getFilteredObject(C,B,A,true,A);};
SL4B_AbstractRttpProvider.prototype.getFilteredNewsHeadlines = function(C,A,B){this.getFilteredObjects(C,A,B,true,B);};
SL4B_AbstractRttpProvider.prototype.removeFilteredNewsHeadline = function(C,B,A){this.removeFilteredObject(C,B,A,true,A);};
SL4B_AbstractRttpProvider.prototype.removeFilteredNewsHeadlines = function(C,A,B){this.removeFilteredObjects(C,A,B,true,B);};
SL4B_AbstractRttpProvider.prototype.getAutoDirectory = function(E,D,A,B,C){throw new SL4B_Error("getAutoDirectory method not implemented");
};
SL4B_AbstractRttpProvider.prototype.removeAutoDirectory = function(E,D,A,B,C){throw new SL4B_Error("removeAutoDirectory method not implemented");
};
SL4B_AbstractRttpProvider.prototype.getContainer = function(E,A,C,D,B){throw new SL4B_Error("getContainer method not implemented");
};
SL4B_AbstractRttpProvider.prototype._$getContainerSnapshot = function(D,A,C,B){throw new SL4B_Error("_$getContainerSnapshot method not implemented");
};
SL4B_AbstractRttpProvider.prototype.setContainerWindow = function(B,C,A){throw new SL4B_Error("setContainerWindow method not implemented");
};
SL4B_AbstractRttpProvider.prototype.clearContainerWindow = function(A){throw new SL4B_Error("clearContainerWindow method not implemented");
};
SL4B_AbstractRttpProvider.prototype.removeContainer = function(B,A){throw new SL4B_Error("removeContainer method not implemented");
};
SL4B_AbstractRttpProvider.prototype.getObjectType = function(B,A){throw new SL4B_Error("getObjectType method not implemented");
};
SL4B_AbstractRttpProvider.prototype.setThrottleObject = function(A,B){throw new SL4B_Error("setThrottleObject method not implemented");
};
SL4B_AbstractRttpProvider.prototype.setThrottleObjects = function(A,B){throw new SL4B_Error("setThrottleObjects method not implemented");
};
SL4B_AbstractRttpProvider.prototype.setGlobalThrottle = function(A){throw new SL4B_Error("setGlobalThrottle method not implemented");
};
SL4B_AbstractRttpProvider.prototype.disableWTStatsTimeout = function(A){throw new SL4B_Error("disableWTStatsTimeout method not implemented");
};
SL4B_AbstractRttpProvider.prototype.clearObjectListeners = function(B,A){throw new SL4B_Error("clearObjectListeners method not implemented");
};
SL4B_AbstractRttpProvider.prototype.blockObjectListeners = function(B,A){throw new SL4B_Error("blockObjectListeners method not implemented");
};
SL4B_AbstractRttpProvider.prototype.unblockObjectListeners = function(B,A){throw new SL4B_Error("unblockObjectListeners method not implemented");
};
SL4B_AbstractRttpProvider.prototype.cancelPersistedAction = function(A){throw new SL4B_Error("cancelPersistedAction method not implemented");
};
SL4B_AbstractRttpProvider.prototype.resendPersistedAction = function(A){throw new SL4B_Error("resendPersistedAction method not implemented");
};
SL4B_AbstractRttpProvider.prototype.createObject = function(D,B,C,A){throw new SL4B_Error("createObject method not implemented");
};
SL4B_AbstractRttpProvider.prototype.contribObject = function(D,B,C,A){throw new SL4B_Error("contribObject method not implemented");
};
SL4B_AbstractRttpProvider.prototype.deleteObject = function(C,B,A){throw new SL4B_Error("deleteObject method not implemented");
};
SL4B_AbstractRttpProvider.prototype.getFieldNames = function(){throw new SL4B_Error("getFieldNames method not implemented");
};
SL4B_AbstractRttpProvider.prototype.logout = function(){throw new SL4B_Error("logout method not implemented");
};
SL4B_AbstractRttpProvider.prototype.debug = function(B,A){throw new SL4B_Error("debug method not implemented");
};
SL4B_AbstractRttpProvider.prototype.setDebugLevel = function(A){throw new SL4B_Error("setDebugLevel method not implemented");
};
SL4B_AbstractRttpProvider.prototype.getVersion = function(){throw new SL4B_Error("getVersion method not implemented");
};
SL4B_AbstractRttpProvider.prototype.getVersionInfo = function(){throw new SL4B_Error("getVersionInfo method not implemented");
};
SL4B_AbstractRttpProvider.prototype.getSessionId = function(){throw new SL4B_Error("getVersionInfo method not implemented");
};
SL4B_AbstractRttpProvider.prototype.addConnectionListener = SL_FO;SL4B_AbstractRttpProvider.prototype.removeConnectionListener = SL_FH;SL4B_AbstractRttpProvider.prototype.notifyConnectionListeners = SL_NR;SL4B_AbstractRttpProvider.prototype.onUnload = SL_OU;function SL_AP(A){return SL4B_RttpUtils.getListener(A);
}
function SL_MT(){this.initialise();this.register();
try {SL4B_WindowEventHandler.addListener(this);}catch(e){SL4B_Accessor.getExceptionHandler().processException(e);}
if(!SL4B_Accessor.getConfiguration().isNoBrowserStatus()){this.addConnectionListener(new SL_EC());}}
function SL_QR(){this.stop();if(this.m_fOnBeforeCloseEventHandler!=null){this.m_fOnBeforeCloseEventHandler();}}
function SL_DA(){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"AbstractRttpProvider.stop");this.logout();
try {SL4B_FrameRegistrarAccessor.removeMasterFrame();}catch(e){}
}
function SL_JD(){
try {SL4B_FrameRegistrarAccessor.setMasterFrame(this);}catch(e){}
SL4B_SubscriptionManagerAccessor.addSubscriptionManager(SL4B_SubscriptionManager);}
function SL_AE(B,A,C){SL4B_SubscriptionManagerAccessor.addSubscriptionManager(C);}
function SL_EM(B,A,C){SL4B_SubscriptionManagerAccessor.removeSubscriptionManager(C);}
function SL_QA(){C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",this.const_LOGIN_OK_CONNECTION_EVENT));SL4B_SubscriptionManagerAccessor.ready();}
function SL_MF(){C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",this.const_CREDENTIALS_RETRIEVED_CONNECTION_EVENT,SL4B_Accessor.getCredentialsProvider().getUsername()));}
function SL_FO(A){this.m_pConnectionListeners.push(A);}
function SL_FH(A){var l_nMatchIndex=-1;
for(var l_nListener=0,l_nLength=this.m_pConnectionListeners.length;l_nListener<l_nLength;++l_nListener){if(this.m_pConnectionListeners[l_nListener]==A){l_nMatchIndex=l_nListener;break;
}}if(l_nMatchIndex!=-1){this.m_pConnectionListeners.splice(l_nMatchIndex,1);}return (l_nMatchIndex!=-1);
}
function SL_NR(A){var l_pArguments=SL_NR.arguments;
var l_aCopy=this.m_pConnectionListeners.slice();
for(var l_nListener=0,l_nLength=l_aCopy.length;l_nListener<l_nLength;++l_nListener){
try {switch(A){
case this.const_ERROR_CONNECTION_EVENT:l_aCopy[l_nListener].connectionError(l_pArguments[1]);break;
case this.const_WARNING_CONNECTION_EVENT:l_aCopy[l_nListener].connectionWarning(l_pArguments[1],l_pArguments[2],l_pArguments[3],l_pArguments[4]);break;
case this.const_INFO_CONNECTION_EVENT:l_aCopy[l_nListener].connectionInfo(l_pArguments[1]);break;
case this.const_ATTEMPT_CONNECTION_EVENT:l_aCopy[l_nListener].connectionAttempt(l_pArguments[1],l_pArguments[2]);break;
case this.const_OK_CONNECTION_EVENT:l_aCopy[l_nListener].connectionOk(l_pArguments[1],l_pArguments[2],l_pArguments[3]);break;
case this.const_RECONNECTION_OK_CONNECTION_EVENT:l_aCopy[l_nListener].reconnectionOk();break;
case this.const_FILE_DOWNLOAD_ERROR_CONNECTION_EVENT:l_aCopy[l_nListener].fileDownloadError(l_pArguments[1],l_pArguments[2]);break;
case this.const_LOGIN_ERROR_CONNECTION_EVENT:l_aCopy[l_nListener].loginError(l_pArguments[1]);break;
case this.const_LOGIN_OK_CONNECTION_EVENT:l_aCopy[l_nListener].loginOk();break;
case this.const_CREDENTIALS_RETRIEVED_CONNECTION_EVENT:l_aCopy[l_nListener].credentialsRetrieved(l_pArguments[1]);break;
case this.const_MESSAGE_CONNECTION_EVENT:l_aCopy[l_nListener].message(l_pArguments[1],l_pArguments[2]);break;
case this.const_SERVICE_MESSAGE_CONNECTION_EVENT:l_aCopy[l_nListener].serviceMessage(l_pArguments[1],l_pArguments[2],l_pArguments[3],l_pArguments[4]);break;
case this.const_SESSION_EJECTED_CONNECTION_EVENT:l_aCopy[l_nListener].sessionEjected(l_pArguments[1],l_pArguments[2]);break;
case this.const_SOURCE_MESSAGE_CONNECTION_EVENT:l_aCopy[l_nListener].sourceMessage(l_pArguments[1],l_pArguments[2],l_pArguments[3],l_pArguments[4]);break;
case this.const_STATISTICS_CONNECTION_EVENT:l_aCopy[l_nListener].statistics(l_pArguments[1],l_pArguments[2],l_pArguments[3],l_pArguments[4],l_pArguments[5]);break;
case this.const_CREDENTIALS_PROVIDER_SESSION_ERROR_CONNECTION_EVENT:l_aCopy[l_nListener].credentialsProviderSessionError(l_pArguments[1],l_pArguments[2],l_pArguments[3],l_pArguments[4]);break;
default :SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"AbstractRttpProvider.notifyConnectionListeners: "+"Received an unknown connection event '"+A+"'. Ignoring event.");}}catch(e){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"AbstractRttpProvider.notifyConnectionListeners: Exception thrown by a listener whilst processing a \"{0}\" event; exception was {1}",A,SL4B_Accessor.getBrowserAdapter().convertExceptionToString(e));}
}}
function SL_OU(A){SL4B_Accessor.getRttpProvider().internalStop();}
var SL4B_ConnectionListener=function(){};
if(false){function SL4B_ConnectionListener(){}
}SL4B_ConnectionListener = function(){};
SL4B_ConnectionListener.prototype.connectionWarning = function(C,D,B,A){};
SL4B_ConnectionListener.prototype.connectionInfo = function(A){};
SL4B_ConnectionListener.prototype.connectionAttempt = function(B,A){this.connectionInfo("Trying connection type "+A);};
SL4B_ConnectionListener.prototype.connectionOk = function(C,A,B){};
SL4B_ConnectionListener.prototype.reconnectionOk = function(){};
SL4B_ConnectionListener.prototype.fileDownloadError = function(B,A){};
SL4B_ConnectionListener.prototype.loginOk = function(){};
SL4B_ConnectionListener.prototype.loginError = function(A){};
SL4B_ConnectionListener.prototype.credentialsRetrieved = function(A){};
SL4B_ConnectionListener.prototype.credentialsProviderSessionError = function(A,D,C,B){};
SL4B_ConnectionListener.prototype.message = function(A,B){};
SL4B_ConnectionListener.prototype.connectionError = function(A){};
SL4B_ConnectionListener.prototype.serviceMessage = function(A,D,B,C){};
SL4B_ConnectionListener.prototype.sessionEjected = function(A,B){};
SL4B_ConnectionListener.prototype.sourceMessage = function(A,C,D,B){};
SL4B_ConnectionListener.prototype.statistics = function(A,D,E,B,C){};
var SL4B_ConnectionWarningReason=function(){};
if(false){function SL4B_ConnectionWarningReason(){}
}SL4B_ConnectionWarningReason = new function(){this.CONNECTION_FAILED="Connection failed";this.LIBERATOR_UNAVAILABLE="Liberator unavailable";this.CONNECTION_LOST="Connection lost";};
var SL4B_ThrottleLevel=function(){};
if(false){function SL4B_ThrottleLevel(){}
}SL4B_ThrottleLevel = new function(){this.MINIMUM="min";this.DOWN="down";this.UP="up";this.MAXIMUM="max";this.STOP="stop";this.START="start";this.DEFAULT="def";this.isValid = function(A){return A==this.UP||A==this.DOWN||A==this.DEFAULT||A==this.MINIMUM||A==this.MAXIMUM||A==this.STOP||A==this.START;
};
};
var SL4B_ObjectType=function(){};
if(false){function SL4B_ObjectType(){}
}SL4B_ObjectType = new function(){this.DIRECTORY="220";this.PAGE="221";this.RECORD="222";this.NEWS_HEADLINE="223";this.NEWS_STORY="224";this.CHAT="227";this.CONTAINER="228";this.AUTO_DIRECTORY="229";this.m_pObjectTypeToNameMap=new Object();this.m_pObjectTypeToNameMap[this.DIRECTORY]="directory";this.m_pObjectTypeToNameMap[this.PAGE]="page";this.m_pObjectTypeToNameMap[this.RECORD]="record";this.m_pObjectTypeToNameMap[this.NEWS_HEADLINE]="news headline";this.m_pObjectTypeToNameMap[this.NEWS_STORY]="news story";this.m_pObjectTypeToNameMap[this.CHAT]="chat";this.m_pObjectTypeToNameMap[this.CONTAINER]="container";this.m_pObjectTypeToNameMap[this.AUTO_DIRECTORY]="auto directory";this.getName = function(A){var l_sName=this.m_pObjectTypeToNameMap[A];
return ((typeof l_sName=="undefined") ? null : l_sName);
};
};
var SL4B_FileType=function(){};
if(false){function SL4B_FileType(){}
}SL4B_FileType = new function(){this.URL_CHECK="URL check";this.APPLET_CHECK="Applet check";this.RTTP_APPLET="RTTP applet";};
var SL4B_ContributionFieldData=function(){};
if(false){function SL4B_ContributionFieldData(){}
}SL4B_ContributionFieldData = function(){this.m_pFieldNameAndValues=new Array();};
SL4B_ContributionFieldData.prototype.addField = function(B,A){this.m_pFieldNameAndValues.push(new GF_Field(B,A));};
SL4B_ContributionFieldData.prototype.size = function(){return this.m_pFieldNameAndValues.length;
};
SL4B_ContributionFieldData.prototype.getField = function(A){if(A>=this.m_pFieldNameAndValues.length||A<0){throw new SL4B_Exception("getField: specified field index \""+A+"\" is out of bounds");
}return this.m_pFieldNameAndValues[A];
};
SL4B_ContributionFieldData.prototype.toString = function(){return this.m_pFieldNameAndValues.join(" ");
};
function GF_Field(B,A){this.m_sName=B;this.m_sValue=A;}
GF_Field.prototype.toString = function(){return "["+this.m_sName+"="+this.m_sValue+"]";
};
function SL_EC(){this.showStatus = function(A){window.status=A;};
}
SL_EC.prototype = new SL4B_ConnectionListener;SL_EC.prototype.connectionWarning = function(A){this.showStatus("Connection Warning: "+A);};
SL_EC.prototype.connectionInfo = function(A){this.showStatus("Connection Information: "+A);};
SL_EC.prototype.connectionOk = function(C,A,B){this.showStatus("Connection OK: "+C+", "+A+", "+B);};
SL_EC.prototype.reconnectionOk = function(C,A,B){this.showStatus("Reconnection OK");};
SL_EC.prototype.fileDownloadError = function(B,A){this.showStatus("File Download Error: "+B+", "+A);};
SL_EC.prototype.loginError = function(A){this.showStatus("Login Error: "+A);};
SL_EC.prototype.loginOk = function(){this.showStatus("Login Ok");};
SL_EC.prototype.message = function(A,B){this.showStatus("Message :"+A+", "+B);};
SL_EC.prototype.connectionError = function(){this.showStatus("Connection Error: connection lost");};
SL_EC.prototype.serviceMessage = function(A,D,B,C){};
SL_EC.prototype.sessionEjected = function(A,B){this.showStatus("Session Ejected: "+A+", "+B);};
SL_EC.prototype.sourceMessage = function(A,C,D,B){};
SL_EC.prototype.statistics = function(A,D,E,B,C){};
var SL4B_ResilientRttpProvider=function(){};
if(false){function SL4B_ResilientRttpProvider(){}
}var SL4B_ResilientRttpProvider=function(){this.m_oBaseRttpProvider=null;this.m_bProviderReady=false;this.m_oRttpCommandQueue=new GF_RttpCommandQueue();this.m_oProxyKeyArray=[];this.m_oContainerKeyMap={};this.m_sCachedFieldNames=null;this.m_sCachedVersion=null;this.m_sCachedVersionInfo=null;this.m_sCachedSessionId=null;this.m_mPersistentActionKeyMap=SL_BF.createMap();this.m_oSessionIdConnectionListener=new SL_FM(this);};
SL4B_ResilientRttpProvider.prototype = new SL4B_AbstractRttpProvider;SL4B_ResilientRttpProvider.prototype._$setBaseRttpProvider = function(A){this.m_oBaseRttpProvider=A;A.addConnectionListener(this.m_oSessionIdConnectionListener);};
SL4B_ResilientRttpProvider.prototype._$cacheRttpProviderData = function(){this.m_sCachedFieldNames=this.m_oBaseRttpProvider.getFieldNames();this.m_sCachedVersion=this.m_oBaseRttpProvider.getVersion();this.m_sCachedVersionInfo=this.m_oBaseRttpProvider.getVersionInfo();this.m_sCachedSessionId=this.m_oBaseRttpProvider.getSessionId();};
SL4B_ResilientRttpProvider.prototype._sessionConnected = function(){this.m_sCachedSessionId=this.m_oBaseRttpProvider.getSessionId();};
SL4B_ResilientRttpProvider.prototype._sessionLost = function(){this.m_sCachedSessionId=null;};
SL4B_ResilientRttpProvider.prototype._$clearBaseRttpProvider = function(){this.m_oBaseRttpProvider=null;this.m_bProviderReady=false;this.m_sCachedSessionId=null;};
SL4B_ResilientRttpProvider.prototype._$sendQueuedCommands = function(){this.m_bProviderReady=true;while(!this.m_oRttpCommandQueue.isEmpty()){this.m_oRttpCommandQueue.sendNext(this.m_oBaseRttpProvider,this.m_oProxyKeyArray,this.m_oContainerKeyMap);}};
SL4B_ResilientRttpProvider.prototype._isBaseRttpProviderReady = function(){return this.m_bProviderReady;
};
SL4B_ResilientRttpProvider.prototype.getObject = function(B,A,C){if(this._isBaseRttpProviderReady()){this.m_oBaseRttpProvider.getObject(B,A,C);}else 
{this.m_oRttpCommandQueue.getObject(B,A,C);}};
SL4B_ResilientRttpProvider.prototype.getObjects = function(A,C,B){if(this.m_bProviderReady){this.m_oBaseRttpProvider.getObjects(A,C,B);}else 
{this.m_oRttpCommandQueue.getObjects(A,C,B);}};
SL4B_ResilientRttpProvider.prototype.removeObject = function(B,A,C){if(this.m_bProviderReady){this.m_oBaseRttpProvider.removeObject(B,A,C);}else 
{this.m_oRttpCommandQueue.removeObject(B,A,C);}};
SL4B_ResilientRttpProvider.prototype.removeObjects = function(A,C,B){if(this.m_bProviderReady){this.m_oBaseRttpProvider.removeObjects(A,C,B);}else 
{this.m_oRttpCommandQueue.removeObjects(A,C,B);}};
SL4B_ResilientRttpProvider.prototype.getContainer = function(B,A,D,C,E){if(this.m_bProviderReady){return this.m_oBaseRttpProvider.getContainer(B,A,D,C,E);
}else 
{var oProxyContainerKey=new SL4B_ContainerKey();
this.m_oProxyKeyArray.push(oProxyContainerKey);this.m_oRttpCommandQueue.getContainer(B,A,D,C,E);return oProxyContainerKey;
}};
SL4B_ResilientRttpProvider.prototype._$getContainerSnapshot = function(D,A,C,B){if(this.m_bProviderReady){this.m_oBaseRttpProvider._$getContainerSnapshot(D,A,C,B);}else 
{this.m_oRttpCommandQueue.getContainerSnapshot(D,A,C,B);}};
SL4B_ResilientRttpProvider.prototype.setContainerWindow = function(A,B,C){if(this.m_bProviderReady){if(this.m_oContainerKeyMap[A]!=null){A=this.m_oContainerKeyMap[A];}this.m_oBaseRttpProvider.setContainerWindow(A,B,C);}else 
{this.m_oRttpCommandQueue.setContainerWindow(A,B,C);}};
SL4B_ResilientRttpProvider.prototype.clearContainerWindow = function(A){if(this.m_bProviderReady){if(this.m_oContainerKeyMap[A]!=null){A=this.m_oContainerKeyMap[A];}this.m_oBaseRttpProvider.clearContainerWindow(A);}else 
{this.m_oRttpCommandQueue.clearContainerWindow(A);}};
SL4B_ResilientRttpProvider.prototype.removeContainer = function(B,A){if(this.m_bProviderReady){if(this.m_oContainerKeyMap[A]!=null){A=this.m_oContainerKeyMap[A];}this.m_oBaseRttpProvider.removeContainer(B,A);}else 
{this.m_oRttpCommandQueue.removeContainer(B,A);}};
SL4B_ResilientRttpProvider.prototype.getAutoDirectory = function(A,C,B,E,D){if(this.m_bProviderReady){this.m_oBaseRttpProvider.getAutoDirectory(A,C,B,E,D);}else 
{this.m_oRttpCommandQueue.getAutoDirectory(A,C,B,E,D);}};
SL4B_ResilientRttpProvider.prototype.removeAutoDirectory = function(A,C,B,E,D){if(this.m_bProviderReady){this.m_oBaseRttpProvider.removeAutoDirectory(A,C,B,E,D);}else 
{this.m_oRttpCommandQueue.removeAutoDirectory(A,C,B,E,D);}};
SL4B_ResilientRttpProvider.prototype.getObjectType = function(B,A){if(this.m_bProviderReady){this.m_oBaseRttpProvider.getObjectType(B,A);}else 
{this.m_oRttpCommandQueue.getObjectType(B,A);}};
SL4B_ResilientRttpProvider.prototype.setThrottleObject = function(A,B){if(this.m_bProviderReady){this.m_oBaseRttpProvider.setThrottleObject(A,B);}else 
{this.m_oRttpCommandQueue.setThrottleObject(A,B);}};
SL4B_ResilientRttpProvider.prototype.setThrottleObjects = function(B,A){if(this.m_bProviderReady){this.m_oBaseRttpProvider.setThrottleObjects(B,A);}else 
{this.m_oRttpCommandQueue.setThrottleObjects(B,A);}};
SL4B_ResilientRttpProvider.prototype.setGlobalThrottle = function(A){if(this.m_bProviderReady){this.m_oBaseRttpProvider.setGlobalThrottle(A);}else 
{this.m_oRttpCommandQueue.setGlobalThrottle(A);}};
SL4B_ResilientRttpProvider.prototype.disableWTStatsTimeout = function(A){if(this.m_bProviderReady){this.m_oBaseRttpProvider.disableWTStatsTimeout(A);}else 
{this.m_oRttpCommandQueue.disableWTStatsTimeout(A);}};
SL4B_ResilientRttpProvider.prototype.clearObjectListeners = function(A,B){if(this.m_bProviderReady){this.m_oBaseRttpProvider.clearObjectListeners(A,B);}else 
{this.m_oRttpCommandQueue.clearObjectListeners(A,B);}};
SL4B_ResilientRttpProvider.prototype.blockObjectListeners = function(A,B){if(this.m_bProviderReady){this.m_oBaseRttpProvider.blockObjectListeners(A,B);}else 
{this.m_oRttpCommandQueue.blockObjectListeners(A,B);}};
SL4B_ResilientRttpProvider.prototype.unblockObjectListeners = function(A,B){if(this.m_bProviderReady){this.m_oBaseRttpProvider.unblockObjectListeners(A,B);}else 
{this.m_oRttpCommandQueue.unblockObjectListeners(A,B);}};
SL4B_ResilientRttpProvider.prototype.removeSubscriber = function(A){if(this.m_bProviderReady){this.m_oBaseRttpProvider.removeSubscriber(A);}else 
{this.m_oRttpCommandQueue.removeSubscriber(A);}};
SL4B_ResilientRttpProvider.prototype.createObject = function(B,A,D,C){var oPersistentActionKey;
if(this.m_bProviderReady){oPersistentActionKey=this.m_oBaseRttpProvider.createObject(B,A,D,C);}else 
{oPersistentActionKey=this._createPersistentActionKeyWrapper(C);this.m_oRttpCommandQueue.createObject(B,A,D,C,oPersistentActionKey);}this._addPersistentActionKeyToMap(oPersistentActionKey);return oPersistentActionKey;
};
SL4B_ResilientRttpProvider.prototype.contribObject = function(B,A,D,C){var oPersistentActionKey;
if(this.m_bProviderReady){oPersistentActionKey=this.m_oBaseRttpProvider.contribObject(B,A,D,C);}else 
{oPersistentActionKey=this._createPersistentActionKeyWrapper(C);this.m_oRttpCommandQueue.contribObject(B,A,D,C,oPersistentActionKey);}this._addPersistentActionKeyToMap(oPersistentActionKey);return oPersistentActionKey;
};
SL4B_ResilientRttpProvider.prototype.deleteObject = function(B,A,C){var oPersistentActionKey;
if(this.m_bProviderReady){oPersistentActionKey=this.m_oBaseRttpProvider.deleteObject(B,A,C);}else 
{oPersistentActionKey=this._createPersistentActionKeyWrapper(C);this.m_oRttpCommandQueue.deleteObject(B,A,C,oPersistentActionKey);}this._addPersistentActionKeyToMap(oPersistentActionKey);return oPersistentActionKey;
};
SL4B_ResilientRttpProvider.prototype._createPersistentActionKeyWrapper = function(A){if(A===SL4B_ActionPersistence.PERSIST_BEFORE_RESUBSCRIBING||A===SL4B_ActionPersistence.PERSIST_AFTER_RESUBSCRIBING){return new GF_PersistentActionKeyWrapper();
}return null;
};
SL4B_ResilientRttpProvider.prototype.cancelPersistedAction = function(A){if(!this._isPersistentActionKeyInMap(A)){return false;
}if(this.m_bProviderReady){this._removePersistentActionKeyFromMap(A);return this.m_oBaseRttpProvider.cancelPersistedAction(A);
}else 
{this.m_oRttpCommandQueue.cancelPersistedAction(A);return this._removePersistentActionKeyFromMap(A);
}};
SL4B_ResilientRttpProvider.prototype._isPersistentActionKeyInMap = function(A){return (A&&this.m_mPersistentActionKeyMap[A.getId()]);
};
SL4B_ResilientRttpProvider.prototype._removePersistentActionKeyFromMap = function(A){if(this._isPersistentActionKeyInMap(A)){SL_BF.removeItem(this.m_mPersistentActionKeyMap,A.getId());return true;
}return false;
};
SL4B_ResilientRttpProvider.prototype._addPersistentActionKeyToMap = function(A){if(A){this.m_mPersistentActionKeyMap[A.getId()]=A;}};
SL4B_ResilientRttpProvider.prototype.resendPersistedAction = function(A){if(this._isPersistentActionKeyInMap(A)){if(this.m_bProviderReady){this.m_oBaseRttpProvider.resendPersistedAction(A);}else 
{this.m_oRttpCommandQueue.resendPersistedAction(A);}}};
SL4B_ResilientRttpProvider.prototype.getFieldNames = function(){if(this.m_bProviderReady){return this.m_oBaseRttpProvider.getFieldNames();
}else 
{return this.m_sCachedFieldNames;
}};
SL4B_ResilientRttpProvider.prototype.logout = function(){if(this.m_bProviderReady){this.m_oBaseRttpProvider.logout();}else 
{this.m_oRttpCommandQueue.logout();}};
SL4B_ResilientRttpProvider.prototype.debug = function(A,B){if(this.m_bProviderReady){this.m_oBaseRttpProvider.debug(A,B);}else 
{this.m_oRttpCommandQueue.debug(A,B);}};
SL4B_ResilientRttpProvider.prototype.setDebugLevel = function(A){if(this.m_bProviderReady){this.m_oBaseRttpProvider.setDebugLevel(A);}else 
{this.m_oRttpCommandQueue.setDebugLevel(A);}};
SL4B_ResilientRttpProvider.prototype.getVersion = function(){if(this.m_bProviderReady){return this.m_oBaseRttpProvider.getVersion();
}else 
{return this.m_sCachedVersion;
}};
SL4B_ResilientRttpProvider.prototype.getVersionInfo = function(){if(this.m_bProviderReady){return this.m_oBaseRttpProvider.getVersionInfo();
}else 
{return this.m_sCachedVersionInfo;
}};
SL4B_ResilientRttpProvider.prototype.getSessionId = function(){if(this.m_bProviderReady){return this.m_oBaseRttpProvider.getSessionId();
}else 
{return this.m_sCachedSessionId;
}};
function SL_FM(A){this.m_oResilientRttpProvider=A;}
SL_FM.prototype = new SL4B_ConnectionListener;SL_FM.prototype.connectionOk = function(){this.m_oResilientRttpProvider._sessionConnected();};
SL_FM.prototype.connectionWarning = function(){this.m_oResilientRttpProvider._sessionLost();};
SL4B_ActionPersistence = new function(){this.NO_PERSISTENCE="No Persistence";this.PERSIST_BEFORE_RESUBSCRIBING="Persist Before Resubscribing";this.PERSIST_AFTER_RESUBSCRIBING="Persist After Resubscribing";};
function SL4B_PersistentActionKey(B,A){this.m_sId=SL4B_PersistentActionKey._generateUniqueId();this.m_oActionPersistence=B;this.m_oActionContext=A;}
SL4B_PersistentActionKey.nCount=0;SL4B_PersistentActionKey._generateUniqueId = function(){return SL4B_WindowRegistrar._$getPageName()+"_"+SL4B_PersistentActionKey.nCount++;
};
SL4B_PersistentActionKey._resetCount = function(){SL4B_PersistentActionKey.nCount=0;};
SL4B_PersistentActionKey.prototype.getId = function(){return this.m_sId;
};
SL4B_PersistentActionKey.prototype._$getActionPersistence = function(){return this.m_oActionPersistence;
};
SL4B_PersistentActionKey.prototype._$getActionContext = function(){return this.m_oActionContext;
};
SL4B_PersistentActionKey.prototype._$getPersistentActionKey = function(){return this;
};
function GF_PersistentActionKeyWrapper(){SL4B_PersistentActionKey.call(this,null,null);this.m_oWrappedPersistentActionKey=null;this.m_sId+="_wrapper";}
GF_PersistentActionKeyWrapper.prototype = new SL4B_PersistentActionKey;GF_PersistentActionKeyWrapper.prototype._$getPersistentActionKey = function(){return this.m_oWrappedPersistentActionKey;
};
GF_PersistentActionKeyWrapper.prototype._$setPersistentActionKey = function(A){this.m_oWrappedPersistentActionKey=A;};
function SL4B_LogConnectionListener(A){this.m_oLogger=A;this.m_mSourceRttpCodeToDescriptionMap={511.0:"is up", 512.0:"is down"};this.m_mServiceRttpCodeToDescriptionMap={515.0:"up, all DataSources are up", 516.0:"down, one or more required DataSources are down", 517.0:"limited, one or more non-required DataSources are down"};}
SL4B_LogConnectionListener.prototype = new SL4B_ConnectionListener;SL4B_LogConnectionListener.prototype.connectionAttempt = function(A,B){this.m_oLogger.logConnectionMessage(true,"Attempting a type {0} connection to Liberator {1}",B,A);};
SL4B_LogConnectionListener.prototype.connectionInfo = function(A){this.m_oLogger.logConnectionMessage(false,"Connection information \"{0}\"",A);};
SL4B_LogConnectionListener.prototype.connectionWarning = function(B,D,A,C){var sLogDescription;
if(D===SL4B_ConnectionWarningReason.CONNECTION_FAILED){sLogDescription="Failed to establish type {0} connection to Liberator {1} - {2}";}else 
if(D===SL4B_ConnectionWarningReason.CONNECTION_LOST){sLogDescription="Lost type {0} connection to Liberator {1} - {2}";}else 
if(D===SL4B_ConnectionWarningReason.LIBERATOR_UNAVAILABLE){sLogDescription="Type {0} connection to Liberator {1} failed due to url-check.gif download failure";}else 
{sLogDescription="Unknown connection issue with type {0} connection to Liberator {1} - {2}";}this.m_oLogger.logConnectionMessage(true,sLogDescription,C,A,B);};
SL4B_LogConnectionListener.prototype.connectionError = function(A){this.m_oLogger.logConnectionMessage(true,"Connection error: {0}",A);};
SL4B_LogConnectionListener.prototype.connectionOk = function(A,B,C){this.m_oLogger.logConnectionMessage(true,"Successfully established type {0} connection to {1}",B,A);};
SL4B_LogConnectionListener.prototype.credentialsRetrieved = function(A){this.m_oLogger.logConnectionMessage(true,"Credentials retrieved for user {0}",A);};
SL4B_LogConnectionListener.prototype.loginOk = function(){this.m_oLogger.logConnectionMessage(true,"Log in successful");};
SL4B_LogConnectionListener.prototype.reconnectionOk = function(){this.m_oLogger.logConnectionMessage(true,"Log in has successfully reconnected to the previous session");};
SL4B_LogConnectionListener.prototype.loginError = function(A){this.m_oLogger.logConnectionMessage(true,"Log in failed - {0}",A);};
SL4B_LogConnectionListener.prototype.sessionEjected = function(B,A){this.m_oLogger.logConnectionMessage(true,"User session ejected by the server - {0}",A);};
SL4B_LogConnectionListener.prototype.message = function(B,A){this.m_oLogger.logConnectionMessage(false,"Generic message \"{0}\" received from Liberator - {1}",B,A);};
SL4B_LogConnectionListener.prototype.serviceMessage = function(B,D,C,A){this.m_oLogger.logConnectionMessage(false,"Data service \"{0}\" is {1}",D,this.m_mServiceRttpCodeToDescriptionMap[B]);};
SL4B_LogConnectionListener.prototype.sourceMessage = function(D,C,B,A){this.m_oLogger.logConnectionMessage(false,"DataSource \"{0}\" {1}",B,this.m_mSourceRttpCodeToDescriptionMap[D]||A);};
SL4B_LogConnectionListener.prototype.fileDownloadError = function(A,B){this.m_oLogger.logConnectionMessage(false,"Failed to load file {0}",B);};
SL4B_LogConnectionListener.prototype.statistics = function(C,D,E,A,B){};
var SL4B_ContainerKey=function(){this.m_nId=SL4B_ContainerKey.g_nNextId++;};
SL4B_ContainerKey.prototype.getId = function(){return this.m_nId;
};
SL4B_ContainerKey.prototype._$getContainerKey = function(){return this;
};
SL4B_ContainerKey.prototype.toString = function(){return this.m_nId+"";
};
SL4B_ContainerKey.getNextKeyId = function(){return SL4B_ContainerKey.g_nNextId++;
};
SL4B_ContainerKey.setNextKeyId = function(A){SL4B_ContainerKey.g_nNextId=A;};
SL4B_ContainerKey.g_nNextId=0;var SL4B_ContainerRequestData=function(A,E,B,D,F,C){this.m_sSubscriberId=A;this.m_sContainerId=E;this.m_sContainerName=B;this.m_sFieldList=D;this.m_nWindowStart=F;this.m_nWindowEnd=C;};
SL4B_ContainerRequestData.prototype.getSubscriberId = function(){return this.m_sSubscriberId;
};
SL4B_ContainerRequestData.prototype.getContainerId = function(){return this.m_sContainerId;
};
SL4B_ContainerRequestData.prototype.getContainerName = function(){return this.m_sContainerName;
};
SL4B_ContainerRequestData.prototype.getFieldList = function(){return this.m_sFieldList;
};
SL4B_ContainerRequestData.prototype.getWindowStart = function(){return this.m_nWindowStart;
};
SL4B_ContainerRequestData.prototype.getWindowEnd = function(){return this.m_nWindowEnd;
};
SL4B_ContainerRequestData.prototype.setWindowStart = function(A){this.m_nWindowStart=A;};
SL4B_ContainerRequestData.prototype.setWindowEnd = function(A){this.m_nWindowEnd=A;};
SL4B_ContainerRequestData.prototype.toString = function(){return "{ContainerRequestData "+this.m_sSubscriberId+" "+this.m_sContainerId+" "+this.m_sContainerName+" "+this.m_sFieldList+" "+this.m_nWindowStart+","+this.m_nWindowEnd+" }";
};
SL4B_ContainerRequestData.prototype.clone = function(){return new SL4B_ContainerRequestData(this.m_sSubscriberId,this.m_sContainerId,this.m_sContainerName,this.m_sFieldList,this.m_nWindowStart,this.m_nWindowEnd);
};
var SL4B_AbstractSubscriber=function(){this.m_oWindow=window;this.m_sListenerId=null;this.m_pMultiUpdateQueue=null;};
if(false){function SL4B_AbstractSubscriber(){}
}SL4B_AbstractSubscriber.prototype.initialise = SL_AX;SL4B_AbstractSubscriber.prototype.getIdentifier = function(){return SL4B_SubscriptionManager.getListenerPrefix()+this.m_sListenerId;
};
SL4B_AbstractSubscriber.prototype.getLocalIdentifier = function(){return SL4B_SubscriptionManager.getLocalListenerPrefix()+this.m_sListenerId;
};
SL4B_AbstractSubscriber.prototype.ready = function(){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"AbstractSubscriber.ready invoked but not overridden");};
SL4B_AbstractSubscriber.prototype.WTChat = SL_JQ;SL4B_AbstractSubscriber.prototype.WTContribOk = SL_FX;SL4B_AbstractSubscriber.prototype.WTContribFailed = SL_KX;SL4B_AbstractSubscriber.prototype.WTCreateOk = SL_AL;SL4B_AbstractSubscriber.prototype.WTCreateFailed = SL_IQ;SL4B_AbstractSubscriber.prototype.WTDeleteOk = SL_GV;SL4B_AbstractSubscriber.prototype.WTDeleteFailed = SL_LS;SL4B_AbstractSubscriber.prototype.WTDirUpdated = SL_HY;SL4B_AbstractSubscriber.prototype.WTFieldDeleted = SL_CW;SL4B_AbstractSubscriber.prototype.WTType2Clear = SL_SH;SL4B_AbstractSubscriber.prototype.WTType3Clear = SL_BV;SL4B_AbstractSubscriber.prototype.WTNewsUpdated = SL_NX;SL4B_AbstractSubscriber.prototype.WTObjectDeleted = SL_IW;SL4B_AbstractSubscriber.prototype.WTObjectInfo = SL_EP;SL4B_AbstractSubscriber.prototype.WTObjectNotFound = SL_HJ;SL4B_AbstractSubscriber.prototype.WTObjectNotStale = SL_AN;SL4B_AbstractSubscriber.prototype.WTObjectReadDenied = SL_KH;SL4B_AbstractSubscriber.prototype.WTObjectStale = SL_RF;SL4B_AbstractSubscriber.prototype.WTObjectStatus = SL_AT;SL4B_AbstractSubscriber.prototype.WTObjectType = SL_PN;SL4B_AbstractSubscriber.prototype.WTObjectUnavailable = SL_AH;SL4B_AbstractSubscriber.prototype.WTObjectUpdated = SL_LC;SL4B_AbstractSubscriber.prototype.WTObjectWriteDenied = SL_JH;SL4B_AbstractSubscriber.prototype.WTPageUpdated = SL_CS;SL4B_AbstractSubscriber.prototype.WTRecordMultiUpdated = SL_IH;SL4B_AbstractSubscriber.prototype.WTRecordUpdated = SL_DT;SL4B_AbstractSubscriber.prototype.WTStoryReset = SL_NP;SL4B_AbstractSubscriber.prototype.WTStoryUpdated = SL_NF;SL4B_AbstractSubscriber.prototype.WTStructureChange = SL_CQ;SL4B_AbstractSubscriber.prototype.WTStructureMultiChange = SL_BO;SL4B_AbstractSubscriber.prototype.WTPermissionUpdated = SL_DZ;SL4B_AbstractSubscriber.prototype.WTPermissionDeleted = SL_LK;SL4B_AbstractSubscriber.prototype.dequeueNextMultiUpdate = SL_PW;SL4B_AbstractSubscriber.prototype.chat = function(C,B,D,A,E){this.methodNotImplemented("chat");};
SL4B_AbstractSubscriber.prototype.contribOk = function(B,A){this.methodNotImplemented("contribOk");};
SL4B_AbstractSubscriber.prototype.contribFailed = function(B,D,C,A){this.methodNotImplemented("contribFailed");};
SL4B_AbstractSubscriber.prototype.createOk = function(B,A){this.methodNotImplemented("createOk");};
SL4B_AbstractSubscriber.prototype.createFailed = function(B,D,C,A){this.methodNotImplemented("createFailed");};
SL4B_AbstractSubscriber.prototype.deleteOk = function(B,A){this.methodNotImplemented("deleteOk");};
SL4B_AbstractSubscriber.prototype.deleteFailed = function(B,D,C,A){this.methodNotImplemented("deleteFailed");};
SL4B_AbstractSubscriber.prototype.directoryUpdated = function(D,C,A,B){this.methodNotImplemented("directoryUpdated");};
SL4B_AbstractSubscriber.prototype.directoryMultiUpdated = function(B,A){this.methodNotImplemented("directoryMultiUpdated");};
SL4B_AbstractSubscriber.prototype.fieldDeleted = function(A,C,B){this.methodNotImplemented("fieldDeleted");};
SL4B_AbstractSubscriber.prototype.type2Clear = function(A){this.methodNotImplemented("type2Clear");};
SL4B_AbstractSubscriber.prototype.type3Clear = function(A){this.methodNotImplemented("type3Clear");};
SL4B_AbstractSubscriber.prototype.newsUpdated = function(A,D,C,B){this.methodNotImplemented("newsUpdated");};
SL4B_AbstractSubscriber.prototype.objectDeleted = function(A){this.methodNotImplemented("objectDeleted");};
SL4B_AbstractSubscriber.prototype.objectInfo = function(C,B,D,A){this.methodNotImplemented("objectInfo");};
SL4B_AbstractSubscriber.prototype.objectNotFound = function(A){this.methodNotImplemented("objectNotFound");};
SL4B_AbstractSubscriber.prototype.objectNotStale = function(A,B){this.methodNotImplemented("objectNotStale");};
SL4B_AbstractSubscriber.prototype.objectReadDenied = function(A,B){this.methodNotImplemented("objectReadDenied");};
SL4B_AbstractSubscriber.prototype.objectStale = function(A,B){this.methodNotImplemented("objectStale");};
SL4B_AbstractSubscriber.prototype.objectStatus = function(D,B,A,C){this.methodNotImplemented("objectStatus");};
SL4B_AbstractSubscriber.prototype.objectType = function(C,B,A){this.methodNotImplemented("objectType");};
SL4B_AbstractSubscriber.prototype.objectUnavailable = function(A){this.methodNotImplemented("objectUnavailable");};
SL4B_AbstractSubscriber.prototype.objectUpdated = function(A){this.methodNotImplemented("objectUpdated");};
SL4B_AbstractSubscriber.prototype.objectWriteDenied = function(A,B){this.methodNotImplemented("objectWriteDenied");};
SL4B_AbstractSubscriber.prototype.pageUpdated = function(E,D,B,A,C){this.methodNotImplemented("pageUpdated");};
SL4B_AbstractSubscriber.prototype.recordMultiUpdated = function(A,B,C){for(var l_nCount=0,l_nSize=B.size();l_nCount<l_nSize;l_nCount++){SL4B_MethodInvocationProxy.invoke(this,"recordUpdated",[A,B.getFieldName(l_nCount),B.getFieldValue(l_nCount)]);}};
SL4B_AbstractSubscriber.prototype.recordUpdated = function(B,C,A){this.methodNotImplemented("recordUpdated");};
SL4B_AbstractSubscriber.prototype.storyReset = function(A){this.methodNotImplemented("storyReset");};
SL4B_AbstractSubscriber.prototype.storyUpdated = function(B,A){this.methodNotImplemented("storyUpdated");};
SL4B_AbstractSubscriber.prototype.structureChange = function(A,C,D,B){this.methodNotImplemented("structureChange");};
SL4B_AbstractSubscriber.prototype.structureMultiChange = function(B,A,D,C,E){this.methodNotImplemented("structureChange");};
SL4B_AbstractSubscriber.prototype.permissionUpdated = function(A,B,C){this.methodNotImplemented("permissionUpdated");};
SL4B_AbstractSubscriber.prototype.permissionDeleted = function(A,B){this.methodNotImplemented("permissionDeleted");};
SL4B_AbstractSubscriber.prototype.methodNotImplemented = function(A){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"AbstractSubscriber.{0} invoked but not overridden",A);};
function SL_AX(){this.m_sListenerId=SL4B_SubscriptionManager.addSubscriber(this);this.m_pMultiUpdateQueue=new Array();if(SL4B_SubscriptionManager.isReady()){C_CallbackQueue.addCallback(new Array(this,"ready"));}}
function SL_JQ(C,B,D,A,E){C_CallbackQueue.addCallback(new Array(this,"chat",C,B,D,A,E));}
function SL_FX(B,A){C_CallbackQueue.addCallback(new Array(this,"contribOk",B,A));}
function SL_KX(B,D,C,A){C_CallbackQueue.addCallback(new Array(this,"contribFailed",B,D,C,A));}
function SL_AL(B,A){C_CallbackQueue.addCallback(new Array(this,"createOk",B,A));}
function SL_IQ(B,D,C,A){C_CallbackQueue.addCallback(new Array(this,"createFailed",B,D,C,A));}
function SL_GV(B,A){C_CallbackQueue.addCallback(new Array(this,"deleteOk",B,A));}
function SL_LS(B,D,C,A){C_CallbackQueue.addCallback(new Array(this,"deleteFailed",B,D,C,A));}
function SL_HY(D,C,A,B){C_CallbackQueue.addCallback(new Array(this,"directoryUpdated",D,C,A,B));}
SL4B_AbstractSubscriber.prototype.WTDirMultiUpdated = function(B,A){C_CallbackQueue.addCallback(new Array(this,"directoryMultiUpdated",B,A));};
function SL_CW(A,C,B){C_CallbackQueue.addCallback(new Array(this,"fieldDeleted",A,C,B));}
function SL_SH(A){C_CallbackQueue.addCallback(new Array(this,"type2Clear",A));}
function SL_BV(A){C_CallbackQueue.addCallback(new Array(this,"type3Clear",A));}
function SL_NX(A,D,C,B){C_CallbackQueue.addCallback(new Array(this,"newsUpdated",A,D,C,B));}
function SL_IW(A){C_CallbackQueue.addCallback(new Array(this,"objectDeleted",A));}
function SL_EP(C,B,D,A){C_CallbackQueue.addCallback(new Array(this,"objectInfo",C,B,D,A));}
function SL_HJ(A){C_CallbackQueue.addCallback(new Array(this,"objectNotFound",A));}
function SL_AN(A,B){C_CallbackQueue.addCallback(new Array(this,"objectNotStale",A,B));}
function SL_KH(A,B){C_CallbackQueue.addCallback(new Array(this,"objectReadDenied",A,B));}
function SL_RF(A,B){C_CallbackQueue.addCallback(new Array(this,"objectStale",A,B));}
function SL_AT(D,B,A,C){C_CallbackQueue.addCallback(new Array(this,"objectStatus",D,B,A,C));}
function SL_PN(C,B,A){C_CallbackQueue.addCallback(new Array(this,"objectType",C,B,A));}
function SL_AH(A){C_CallbackQueue.addCallback(new Array(this,"objectUnavailable",A));}
function SL_LC(A){C_CallbackQueue.addCallback(new Array(this,"objectUpdated",A));}
function SL_JH(A,B){C_CallbackQueue.addCallback(new Array(this,"objectWriteDenied",A,B));}
function SL_CS(E,D,B,A,C){C_CallbackQueue.addCallback(new Array(this,"pageUpdated",E,D,B,A,C));}
function SL_IH(A,B){this.m_pMultiUpdateQueue.push(new SL_HT(A,B,SL_HT.const_RECORD_UPDATE_TYPE,null));C_CallbackQueue.addCallback(new Array(this,"dequeueNextMultiUpdate"));}
function SL_DT(B,C,A){C_CallbackQueue.addCallback(new Array(this,"recordUpdated",B,C,A));}
function SL_NP(A){C_CallbackQueue.addCallback(new Array(this,"storyReset",A));}
function SL_NF(B,A){C_CallbackQueue.addCallback(new Array(this,"storyUpdated",B,A));}
function SL_CQ(A,C,D,B){C_CallbackQueue.addCallback(new Array(this,"structureChange",A,C,D,B));}
function SL_BO(B,A,D,C,E){this.m_pMultiUpdateQueue.push(new SL_HT(B,null,SL_HT.const_STRUCTURE_CHANGE_TYPE,new Array(A,D,C,E)));C_CallbackQueue.addCallback(new Array(this,"dequeueNextMultiUpdate"));}
function SL_DZ(A,B,C){this.m_pMultiUpdateQueue.push(new SL_HT(A,C,SL_HT.const_PERMISSION_UPDATE_TYPE,new Array(B)));C_CallbackQueue.addCallback(new Array(this,"dequeueNextMultiUpdate"));}
function SL_LK(A,B){C_CallbackQueue.addCallback(new Array(this,"permissionDeleted",A,((!B) ? null : B)));}
function SL_PW(){var l_oMultiUpdate=this.m_pMultiUpdateQueue.shift();
if(l_oMultiUpdate!=null){if(l_oMultiUpdate.m_nType==SL_HT.const_RECORD_UPDATE_TYPE){this.recordMultiUpdated(l_oMultiUpdate.m_sObjectName,l_oMultiUpdate.m_oFieldData);}else 
if(l_oMultiUpdate.m_nType==SL_HT.const_PERMISSION_UPDATE_TYPE){this.permissionUpdated(l_oMultiUpdate.m_sObjectName,l_oMultiUpdate.m_pExtraData[0],l_oMultiUpdate.m_oFieldData);}else 
if(l_oMultiUpdate.m_nType==SL_HT.const_STRUCTURE_CHANGE_TYPE){this.structureMultiChange(l_oMultiUpdate.m_sObjectName,l_oMultiUpdate.m_pExtraData[0],l_oMultiUpdate.m_pExtraData[1],l_oMultiUpdate.m_pExtraData[2],l_oMultiUpdate.m_pExtraData[3]);}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"AbstractSubscriber.dequeueNextMultiUpdate: unknown update type: {0}.",l_oMultiUpdate.m_nType);}}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"AbstractSubscriber.dequeueNextMultiUpdate: method invoked with no more multi updates left in the queue.");}}
SL4B_AbstractSubscriber.prototype.getWindow = function(){return this.m_oWindow;
};
function SL_HT(C,D,B,A){this.m_sObjectName=C;this.m_oFieldData=D;this.m_nType=B;this.m_pExtraData=A;}
SL_HT.const_RECORD_UPDATE_TYPE=1;SL_HT.const_PERMISSION_UPDATE_TYPE=2;SL_HT.const_STRUCTURE_CHANGE_TYPE=3;var SL4B_RecordFieldData=function(){};
if(false){function SL4B_RecordFieldData(){}
}SL4B_RecordFieldData = function(){this.m_pFieldNames=new Array();this.m_mFieldNameToValue={};this.m_oField=new GF_Field();};
SL4B_RecordFieldData.prototype.size = function(){return this.m_pFieldNames.length;
};
SL4B_RecordFieldData.prototype.getField = function(A){var sFieldName=this.m_pFieldNames[A];
this.m_oField.m_sName=sFieldName;this.m_oField.m_sValue=this.m_mFieldNameToValue[sFieldName];return this.m_oField;
};
SL4B_RecordFieldData.prototype.getFieldMap = function(){return this.m_mFieldNameToValue;
};
SL4B_RecordFieldData.prototype.getFieldName = function(A){return this.m_pFieldNames[A];
};
SL4B_RecordFieldData.prototype.getFieldValue = function(A){var sFieldName=this.m_pFieldNames[A];
return this.m_mFieldNameToValue[sFieldName];
};
SL4B_RecordFieldData.prototype.add = function(B,A){this.m_pFieldNames.push(B);this.m_mFieldNameToValue[B]=A;};
var SL4B_SubscriptionManager = new function(){this.m_nUniqueId=0;this.m_oWindow=window;this.m_bIsReady=false;this.m_pSubscribers=new Array();this.m_sId=null;this.m_sListenerPrefix=null;this.getSubscribers = function(){return this.m_pSubscribers;
};
this.getSubscriber = function(A){return this.m_pSubscribers[A];
};
this.addSubscriber = function(A){var l_nUniqueId=this.getUniqueId();
this.m_pSubscribers[l_nUniqueId]=A;return "getSubscriber("+l_nUniqueId+")";
};
this.getId = function(){if(this.m_sId==null){this.m_sId=SL4B_WindowRegistrar.getWindowId();}return this.m_sId;
};
this.getWindow = function(){return this.m_oWindow;
};
this.getListenerPrefix = function(){if(this.m_sListenerPrefix==null){this.m_sListenerPrefix="SL4B_SubscriptionManagerAccessor.getSubscriptionManager(\""+this.getId()+"\").";}return this.m_sListenerPrefix;
};
this.getLocalListenerPrefix = function(){return "SL4B_SubscriptionManager.";
};
this.getUniqueId = function(){return this.m_nUniqueId++;
};
this.ready = function(){if(!this.m_bIsReady){this.m_bIsReady=true;SL4B_WindowRegistrar.ready();for(var l_nSubscriberId=0;l_nSubscriberId<this.m_pSubscribers.length;++l_nSubscriberId){SL4B_MethodInvocationProxy.invoke(this.m_pSubscribers[l_nSubscriberId],"ready",[]);}}};
this.isReady = function(){return this.m_bIsReady;
};
};
function SL_RK(){}
SL_RK.prototype = new SL4B_AbstractSubscriber;SL_RK.prototype.getSubscriber = function(A){return this;
};
SL_RK.prototype.ready = function(){};
SL_RK.prototype.WTChat = function(C,B,D,A,E){};
SL_RK.prototype.WTContribOk = function(A){};
SL_RK.prototype.WTContribFailed = function(A,B){};
SL_RK.prototype.WTdirUpdated = function(D,C,A,B){};
SL_RK.prototype.WTDirMultiUpdated = function(A,B){};
SL_RK.prototype.WTFieldDeleted = function(A,C,B){};
SL_RK.prototype.WTType2Clear = function(A){};
SL_RK.prototype.WTType3Clear = function(A){};
SL_RK.prototype.WTNewsUpdated = function(A,D,C,B){};
SL_RK.prototype.WTObjectDeleted = function(A){};
SL_RK.prototype.WTObjectInfo = function(C,B,D,A){};
SL_RK.prototype.WTObjectNotFound = function(A){};
SL_RK.prototype.WTObjectNotStale = function(A,B){};
SL_RK.prototype.WTObjectReadDenied = function(A,B){};
SL_RK.prototype.WTObjectStale = function(A,B){};
SL_RK.prototype.WTObjectStatus = function(D,B,A,C){};
SL_RK.prototype.WTObjectType = function(C,B,A){};
SL_RK.prototype.WTObjectUnavailable = function(A){};
SL_RK.prototype.WTObjectUpdated = function(A){};
SL_RK.prototype.WTObjectWriteDenied = function(A,B){};
SL_RK.prototype.WTPageUpdated = function(E,D,B,A,C){};
SL_RK.prototype.WTRecordMultiUpdated = function(A,B){};
SL_RK.prototype.WTRecordUpdated = function(B,C,A){};
SL_RK.prototype.WTStoryReset = function(A){};
SL_RK.prototype.WTStoryUpdated = function(B,A){};
SL_RK.prototype.WTStructureChange = function(A,C,D,B){};
SL_RK.prototype.WTPermissionUpdated = function(B,A,C){};
SL_RK.prototype.WTPermissionDeleted = function(B,A){};
var SL4B_SubscriptionManagerAccessor = new function(){this.m_bIsReady=false;this.m_pSubscriptionManagers=new Object();this.m_oNullSubscriptionManager=new SL_RK();this.getSubscriptionManager = function(A){var l_oSubscriptionManager=this.m_pSubscriptionManagers[A];
if(typeof l_oSubscriptionManager=="undefined"){SL4B_Logger.log(SL4B_DebugLevel.WARN,"AbstractSubscriber.getSubscriptionManager: No subscription manager has been registered with the id \"{0}\". Using null subscription manager.",A);l_oSubscriptionManager=this.m_oNullSubscriptionManager;}return l_oSubscriptionManager;
};
this.addSubscriptionManager = function(A){if(typeof this.m_pSubscriptionManagers[A.getId()]!="undefined"){SL4B_Logger.log(SL4B_DebugLevel.const_ERROR_INT,"AbstractSubscriber.addSubscriptionManager: A subscription manager with the identifier \"{0}\" has already been registered",A.getId());}else 
{this.m_pSubscriptionManagers[A.getId()]=A;if(this.m_bIsReady){var l_oProvider=A.m_oWindow.SL4B_Accessor.getRttpProvider();
if(l_oProvider.registerSubscriptionManager){l_oProvider.registerSubscriptionManager(A);}else 
{A.ready();}}}};
this.removeSubscriptionManager = function(A){delete (this.m_pSubscriptionManagers[A.getId()]);};
this.ready = function(){if(!this.m_bIsReady){this.m_bIsReady=true;for(l_sManagerId in this.m_pSubscriptionManagers){var slaveWindow=this.m_pSubscriptionManagers[l_sManagerId].getWindow();
var bSlaveWindowIsClosedOrUnloading=false;

try {bSlaveWindowIsClosedOrUnloading=slaveWindow.closed||slaveWindow.SL4B_WindowRegistrar.isUnloading();}catch(e){}
if(!bSlaveWindowIsClosedOrUnloading){this.m_pSubscriptionManagers[l_sManagerId].ready();}}}};
this.getSubscriptionManagers = function(){return this.m_pSubscriptionManagers;
};
};
var SL4B_ObjectStatus=function(){};
if(false){function SL4B_ObjectStatus(){}
}SL4B_ObjectStatus = new function(){this.OK=3;this.STALE=1;this.LIMITED=5;this.REMOVED=2;this.INFO=0;this.m_pStatuses=new Array(this.OK,this.STALE,this.LIMITED,this.REMOVED,this.INFO);this.RTTP_STATUS_CODE_BASELINE=415;this.getObjectStatusFromRttpCode = function(A){var l_nArrayIndex=A-this.RTTP_STATUS_CODE_BASELINE;
if(l_nArrayIndex>=0&&l_nArrayIndex<this.m_pStatuses.length){return this.m_pStatuses[l_nArrayIndex];
}else 
{throw new SL4B_Exception("Invalid RTTP status code. Code was "+A);
}};
};
var SL4B_ChatStatus=function(){};
if(false){function SL4B_ChatStatus(){}
}SL4B_ChatStatus = new function(){this.MESSAGE=0;this.USER_SUBSCRIBED=1;this.USER_UNSUBSCRIBED=2;this.SUBSCRIBED=3;};
var SL4B_ContainerOrderChange=function(){};
if(false){function SL4B_ContainerOrderChange(){}
}SL4B_ContainerOrderChange = function(A,C,B){this.m_sObjectName=A;this.m_nObjectId=C;this.m_nPosition=B;};
SL4B_ContainerOrderChange.prototype.getObjectName = function(){return this.m_sObjectName;
};
SL4B_ContainerOrderChange.prototype.getObjectId = function(){return this.m_nObjectId;
};
SL4B_ContainerOrderChange.prototype.getPosition = function(){return this.m_nPosition;
};
var SL4B_ContainerStructureChange=function(){};
if(false){function SL4B_ContainerStructureChange(){}
}SL4B_ContainerStructureChange = function(B,C,A){this.m_sObjectName=B;this.m_sObjectType=C;this.m_bAdded=A;};
SL4B_ContainerStructureChange.prototype.getObjectName = function(){return this.m_sObjectName;
};
SL4B_ContainerStructureChange.prototype.getObjectType = function(){return this.m_sObjectType;
};
SL4B_ContainerStructureChange.prototype.getAdded = function(){return this.m_bAdded;
};
var SL4B_DirectoryStructureChange=function(){};
if(false){function SL4B_DirectoryStructureChange(){}
}SL4B_DirectoryStructureChange = function(B,C,A){this.m_sObjectName=B;this.m_sObjectType=C;this.m_bAdded=A;};
SL4B_DirectoryStructureChange.prototype.getObjectName = function(){return this.m_sObjectName;
};
SL4B_DirectoryStructureChange.prototype.getObjectType = function(){return this.m_sObjectType;
};
SL4B_DirectoryStructureChange.prototype.getAdded = function(){return this.m_bAdded;
};
if(false){function SL4B_ConnectionEventRecorder(){}
}SL4B_ConnectionEventRecorder = function(){this.clear();};
SL4B_ConnectionEventRecorder.prototype = new SL4B_ConnectionListener();SL4B_ConnectionEventRecorder.prototype.clear = function(){this.m_pConnectionEvents=[];};
SL4B_ConnectionEventRecorder.prototype.connectionAttempt = function(B,A){this._removeRedundantConnectionEvents([SL4B_AbstractRttpProvider.prototype.const_ATTEMPT_CONNECTION_EVENT,SL4B_AbstractRttpProvider.prototype.const_OK_CONNECTION_EVENT,SL4B_AbstractRttpProvider.prototype.const_RECONNECTION_OK_CONNECTION_EVENT,SL4B_AbstractRttpProvider.prototype.const_CREDENTIALS_RETRIEVED_CONNECTION_EVENT,SL4B_AbstractRttpProvider.prototype.const_CREDENTIALS_PROVIDER_SESSION_ERROR_CONNECTION_EVENT,SL4B_AbstractRttpProvider.prototype.const_LOGIN_OK_CONNECTION_EVENT,SL4B_AbstractRttpProvider.prototype.const_LOGIN_ERROR_CONNECTION_EVENT,SL4B_AbstractRttpProvider.prototype.const_WARNING_CONNECTION_EVENT,SL4B_AbstractRttpProvider.prototype.const_ERROR_CONNECTION_EVENT]);this._addConnectionEvent({fCallback:SL4B_AbstractRttpProvider.prototype.const_ATTEMPT_CONNECTION_EVENT, pArguments:arguments});};
SL4B_ConnectionEventRecorder.prototype.connectionWarning = function(C,D,B,A){this._removeRedundantConnectionEvents([SL4B_AbstractRttpProvider.prototype.const_OK_CONNECTION_EVENT]);this._addConnectionEvent({fCallback:SL4B_AbstractRttpProvider.prototype.const_WARNING_CONNECTION_EVENT, pArguments:arguments});};
SL4B_ConnectionEventRecorder.prototype.connectionError = function(A){this._removeRedundantConnectionEvents([SL4B_AbstractRttpProvider.prototype.const_OK_CONNECTION_EVENT]);this._addConnectionEvent({fCallback:SL4B_AbstractRttpProvider.prototype.const_ERROR_CONNECTION_EVENT, pArguments:arguments});};
SL4B_ConnectionEventRecorder.prototype.connectionOk = function(C,A,B){this._addConnectionEvent({fCallback:SL4B_AbstractRttpProvider.prototype.const_OK_CONNECTION_EVENT, pArguments:arguments});};
SL4B_ConnectionEventRecorder.prototype.reconnectionOk = function(){this._removeRedundantConnectionEvents([SL4B_AbstractRttpProvider.prototype.const_LOGIN_OK_CONNECTION_EVENT]);this._addConnectionEvent({fCallback:SL4B_AbstractRttpProvider.prototype.const_LOGIN_OK_CONNECTION_EVENT, pArguments:arguments});};
SL4B_ConnectionEventRecorder.prototype.loginOk = function(){this._removeRedundantConnectionEvents([SL4B_AbstractRttpProvider.prototype.const_LOGIN_OK_CONNECTION_EVENT]);this._addConnectionEvent({fCallback:SL4B_AbstractRttpProvider.prototype.const_LOGIN_OK_CONNECTION_EVENT, pArguments:arguments});};
SL4B_ConnectionEventRecorder.prototype.loginError = function(A){this._addConnectionEvent({fCallback:SL4B_AbstractRttpProvider.prototype.const_LOGIN_ERROR_CONNECTION_EVENT, pArguments:arguments});};
SL4B_ConnectionEventRecorder.prototype.credentialsRetrieved = function(A){this._addConnectionEvent({fCallback:SL4B_AbstractRttpProvider.prototype.const_CREDENTIALS_RETRIEVED_CONNECTION_EVENT, pArguments:arguments});};
SL4B_ConnectionEventRecorder.prototype.credentialsProviderSessionError = function(A,D,C,B){this._addConnectionEvent({fCallback:SL4B_AbstractRttpProvider.prototype.const_CREDENTIALS_PROVIDER_SESSION_ERROR_CONNECTION_EVENT, pArguments:arguments});};
SL4B_ConnectionEventRecorder.prototype.sessionEjected = function(A,B){this._removeRedundantConnectionEvents([SL4B_AbstractRttpProvider.prototype.const_LOGIN_OK_CONNECTION_EVENT]);this._addConnectionEvent({fCallback:SL4B_AbstractRttpProvider.prototype.const_SESSION_EJECTED_CONNECTION_EVENT, pArguments:arguments});};
SL4B_ConnectionEventRecorder.prototype.message = function(A,B){};
SL4B_ConnectionEventRecorder.prototype.serviceMessage = function(A,D,B,C){};
SL4B_ConnectionEventRecorder.prototype.sourceMessage = function(A,C,D,B){};
SL4B_ConnectionEventRecorder.prototype.statistics = function(A,D,E,B,C){this._removeRedundantConnectionEvents([SL4B_AbstractRttpProvider.prototype.const_STATISTICS_CONNECTION_EVENT]);this._addConnectionEvent({fCallback:SL4B_AbstractRttpProvider.prototype.const_STATISTICS_CONNECTION_EVENT, pArguments:arguments});};
SL4B_ConnectionEventRecorder.prototype.sendCurrentConnectionEvents = function(A){var l_oCopyOfConnectionEvents=[];
var l_nConnectionEvents=this.m_pConnectionEvents.length;
for(var i=0;i<l_nConnectionEvents;++i){l_oCopyOfConnectionEvents.push(this.m_pConnectionEvents[i]);}for(var i=0;i<l_nConnectionEvents;++i){this.callOnTimeout(l_oCopyOfConnectionEvents,i,A);}};
SL4B_ConnectionEventRecorder.prototype.callOnTimeout = function(C,A,B){var self=this;
setTimeout(function(){SL4B_MethodInvocationProxy._invokeWithTryCatch(B,C[A]["fCallback"],C[A]["pArguments"]);},0);};
SL4B_ConnectionEventRecorder.prototype._addConnectionEvent = function(A){this.m_pConnectionEvents.push(A);};
SL4B_ConnectionEventRecorder.prototype._removeRedundantConnectionEvents = function(A){var l_nConnectionEvents=this.m_pConnectionEvents.length-1;
for(var i=l_nConnectionEvents;i>=0;i--){var l_sIthConnectionEvent=this.m_pConnectionEvents[i]["fCallback"];
var l_nEventsToRemove=A.length;
for(var j=0;j<l_nEventsToRemove;++j){if(l_sIthConnectionEvent===A[j]){this.m_pConnectionEvents.splice(i,1);break;
}}}};
SL4B_ConnectionEventRecorder=new SL4B_ConnectionEventRecorder();var SL4B_SnapshotProvider=function(){};
if(false){function SL4B_SnapshotProvider(){}
}SL4B_SnapshotProvider = function(){};
SL4B_SnapshotProvider.prototype.getContainer = function(D,A,C,B){SL4B_Accessor.getRttpProvider()._$getContainerSnapshot(D,A,C,B);};
var SL4B_ExceptionHandler=function(){};
if(false){function SL4B_ExceptionHandler(){}
}SL4B_ExceptionHandler = function(){};
SL4B_ExceptionHandler.prototype.processError = SL_JG;SL4B_ExceptionHandler.prototype.processException = SL_JF;function SL_JG(A){throw (A);
}
function SL_JF(A){throw (A);
}
var SL4B_SilentExceptionHandler=function(){};
if(false){function SL4B_SilentExceptionHandler(){}
}SL4B_SilentExceptionHandler = function(){};
SL4B_SilentExceptionHandler.prototype.processError = SL_FT;SL4B_SilentExceptionHandler.prototype.processException = SL_FL;function SL_FT(A){SL4B_Logger.log(SL4B_DebugLevel.const_CRITICAL_INT,A.toString());}
function SL_FL(A){SL4B_Logger.log(SL4B_DebugLevel.const_CRITICAL_INT,A.toString());}
function SL_LZ(){SL4B_Accessor.setExceptionHandler(new SL4B_ExceptionHandler());}
C_CallbackQueue = new function(){this.m_pQueue=new Array();this.m_nCallbackBatchFrequency=0;this.m_nMaximumCallbacksInBatch=5000;this.start = function(){var l_oConfiguration=SL4B_Accessor.getConfiguration();
this.m_nCallbackBatchFrequency=l_oConfiguration.getCallbackBatchFrequency();if(this.m_nCallbackBatchFrequency>0){this.m_nMaximumCallbacksInBatch=l_oConfiguration.getMaximumCallbackBatchSize();setTimeout("C_CallbackQueue.processCallbackBatch();",this.m_nCallbackBatchFrequency);}};
this.addCallback = function(A){if(this.m_nCallbackBatchFrequency>0){this.m_pQueue.push(A);}else 
{this.invokeCallback(A);}};
this.processCallbackBatch = function(){var l_nBatchStartTime=(new Date()).valueOf();
var l_nNumberOfUpdatesProcessed=0;
while(this.m_pQueue.length>0&&l_nNumberOfUpdatesProcessed<this.m_nMaximumCallbacksInBatch){++l_nNumberOfUpdatesProcessed;var l_pCallback=this.m_pQueue.shift();
this.invokeCallback(l_pCallback);}var l_nTimeTaken=(new Date()).valueOf()-l_nBatchStartTime;
setTimeout("C_CallbackQueue.processCallbackBatch();",Math.max(0,this.m_nCallbackBatchFrequency-l_nTimeTaken));};
this.invokeCallback = function(A){if(!A||typeof A!="object"||typeof A.length!="number"){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"CallbackQueue.invokeCallback: Error processing callback \"{0}\"; Specified callback parameter is not an array",((A&&typeof A.join=="function") ? A.join() : A));}else 
if(A.length<2){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"CallbackQueue.invokeCallback: Error processing callback \"{0}\"; Unexpected number of callback arguments ({1})",((A&&typeof A.join=="function") ? A.join() : A),A.length);}else 
{var l_oCallbackObject=A.shift();
var l_sCallbackMethod=A.shift();
SL4B_MethodInvocationProxy.invoke(l_oCallbackObject,l_sCallbackMethod,A);}};
};
var SL4B_XmlResponseRetriever=function(){};
if(false){function SL4B_XmlResponseRetriever(){}
}SL4B_XmlResponseRetriever = new function(){this.getXmlDocumentElement = function(A){var l_oDocElement=null;

try {l_oDocElement=A.responseXML.documentElement;if(!l_oDocElement){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"documentElement is null: The server did not set the content type. Parsing from .reponseText instead");A.responseXML.loadXML(A.responseText);l_oDocElement=A.responseXML.documentElement;}}catch(e){if(e.match(/Permission/)&&A.responseText&&document.implementation.createDocument){var l_oParser=new DOMParser();
var l_oDomDoc=l_oParser.parseFromString(A.responseText,"text/xml");
l_oDocElement=l_oDomDoc.documentElement;}}
return l_oDocElement;
};
};
var SL4B_JsUnit = new function(){this.isUnobfuscated = function(){var l_sObfuscatedFunction=eval("typeof(IS_UNOBFUSCATED)");
return l_sObfuscatedFunction!="undefined";
};
};
var SL_MZ = new function(){return true;
};
GF_CommonDomainExtractor = new function(){this.m_bDomainSet=false;this.m_sCommonDomain=null;this.getCommonDomain=SL_AR;this.getLiberatorUrl=SL_OT;this.determineCommonDomain=SL_OC;this.getCommonDomainForDifferentPort=SL_RG;this.getDocumentDomain=SL_IS;};
function SL_AR(){if(!this.m_bDomainSet){this.m_sCommonDomain=this.determineCommonDomain(this.getDocumentDomain(),window.location.href);this.m_bDomainSet=true;}return this.m_sCommonDomain;
}
function SL_OT(){var l_sUrl=SL4B_Accessor.getConfiguration().getJsContainerUrl();
if(l_sUrl==null){l_sUrl=SL4B_Accessor.getConfiguration().getServerUrl();}return l_sUrl;
}
function SL_OC(A,B){if(SL4B_Accessor.getConfiguration().allowCORS()){return null;
}var l_oLiberatorDomainMatch=this.getLiberatorUrl().match(/https?:\/\/([^\/:]+)/);
if(l_oLiberatorDomainMatch==null){SL4B_Logger.logConnectionMessage(true,"CommonDomainExtractor.determineCommonDomain: Relative url to Liberator detected - document.domain will not be set");return null;
}else 
if(A==l_oLiberatorDomainMatch[1]){if(SL4B_Accessor.getBrowserAdapter().isFirefox()){var l_pLibPortMatch=this.getLiberatorUrl().match(/^https?:\/\/([^:]+)(:)?(\d+)/);
if(B==null){B="http://localhost:80/";}var l_pServerPortMatch=B.match(/^https?:\/\/([^:]+)(:)?(\d+)/);
if((l_pLibPortMatch!=null)){if((l_pServerPortMatch!=null&&l_pLibPortMatch[3]!=l_pServerPortMatch[3])||(l_pServerPortMatch==null&&l_pLibPortMatch[3]!="80")){return this.getCommonDomainForDifferentPort(A);
}}else 
if(l_pServerPortMatch!=null&&l_pServerPortMatch[3]!="80"){return this.getCommonDomainForDifferentPort(A);
}}SL4B_Logger.logConnectionMessage(true,"JavaScriptRttpProvider.determineCommonDomain: Web page and Liberator share the same domain ({0}) - document.domain will not be set",A);return null;
}var l_pWebPageDomainParts=A.split(".");
var l_pLiberatorDomainParts=l_oLiberatorDomainMatch[1].split(".");
var l_nWebPageIndex=l_pWebPageDomainParts.length-1;
var l_nLiberatorIndex=l_pLiberatorDomainParts.length-1;
if(l_nWebPageIndex<1||l_nLiberatorIndex<1){SL4B_Logger.logConnectionMessage(true,"JavaScriptRttpProvider.determineCommonDomain: Web page and Liberator do not share the common domain (web page domain = {0}, liberator domain = {1})",A,l_oLiberatorDomainMatch[1]);throw new SL4B_Exception("determineCommonDomain [1]: web page and Liberator do not share a common domain, connection to Liberator cannot be established");
}for(var l_nMatchingParts=0;l_nWebPageIndex>=0&&l_nLiberatorIndex>=0;--l_nWebPageIndex, --l_nLiberatorIndex){if(l_pWebPageDomainParts[l_nWebPageIndex]==l_pLiberatorDomainParts[l_nLiberatorIndex]){++l_nMatchingParts;}else 
{break;
}}if(l_nMatchingParts<2){SL4B_Logger.logConnectionMessage(true,"CommonDomainExtractor.determineCommonDomain: Web page and Liberator do not share the common domain (web page domain = {0}, liberator domain = {1})",A,l_oLiberatorDomainMatch[1]);throw new SL4B_Exception("determineCommonDomain [2]: web page and Liberator do not share a common domain, connection to Liberator cannot be established");
}var l_pCommonDomain=l_pWebPageDomainParts.splice(l_pWebPageDomainParts.length-l_nMatchingParts,l_nMatchingParts);
var l_sCommonDomain=l_pCommonDomain.join(".");
SL4B_Logger.logConnectionMessage(true,"CommonDomainExtractor.determineCommonDomain: document.domain will be set to {0}",l_sCommonDomain);return l_sCommonDomain;
}
function SL_RG(A){SL4B_Logger.logConnectionMessage(true,"CommonDomainExtractor.getCommonDomainForDifferentPorts: web page and Liberator share the same domain ({0}), but their ports differ and domain needs to be changed.",A);var l_sCommonDomain=A.substring(A.indexOf(".")+1);
SL4B_Logger.logConnectionMessage(true,"CommonDomainExtractor.getCommonDomainForDifferentPorts: document.domain will be set to {0}.",l_sCommonDomain);return l_sCommonDomain;
}
function SL_IS(){return document.domain;
}
var SL4B_Capabilities=function(){};
if(false){function SL4B_Capabilities(){}
}SL4B_Capabilities = function(){this.m_nRttpVersion=2;this.m_oCapabilities={};this.m_oCapabilities["HttpBodyLength"]=65536;this.m_oCapabilities["HttpRequestLineLength"]=128;this.m_oCapabilities["MergedCommands"]=false;};
SL4B_Capabilities.prototype.getRttpVersion = function(){return this.m_nRttpVersion;
};
SL4B_Capabilities.prototype.setRttpVersion = function(A){this.m_nRttpVersion=A;};
SL4B_Capabilities.prototype.getHttpBodyLength = function(){return this.m_oCapabilities["HttpBodyLength"];
};
SL4B_Capabilities.prototype.getMergedCommands = function(){return this.m_oCapabilities["MergedCommands"];
};
SL4B_Capabilities.prototype.getHttpRequestLineLength = function(){return 0;
};
SL4B_Capabilities.prototype.toString = function(){var s="";
for(key in this.m_oCapabilities){s+=key+"="+this.m_oCapabilities[key]+", ";}return s;
};
SL4B_Capabilities.prototype.add = function(A,B){this.m_oCapabilities[A]=B;return B;
};
function SL4B_Capabilities_scriptLoaded(){SL4B_Accessor.setCapabilities(new SL4B_Capabilities());}
SL4B_Capabilities_scriptLoaded();var SL4B_Statistics=function(){};
if(false){function SL4B_Statistics(){}
}SL4B_Statistics = function(){this.m_nClockOffset=0;this.m_nLatency=0;this.m_nAverageLatency=0;this.m_nHeartbeatLatency=0;this.m_nHeartbeatAverageLatency=0;};
SL4B_Statistics.prototype.getClockOffset = function(){return this.m_nClockOffset;
};
SL4B_Statistics.prototype.setClockOffset = function(A){this.m_nClockOffset=A;};
SL4B_Statistics.prototype.getLatency = function(){return this.m_nLatency;
};
SL4B_Statistics.prototype.setLatency = function(A){this.m_nLatency=A;};
SL4B_Statistics.prototype.getAverageLatency = function(){return this.m_nAverageLatency;
};
SL4B_Statistics.prototype.getResponseQueueStatistics = function(){return SL4B_ConnectionProxy.getInstance().getResponseQueueStatistics();
};
SL4B_Statistics.prototype.addResponseQueueStatisticsListener = function(A){return SL4B_ConnectionProxy.getInstance().addResponseQueueStatisticsListener(A);
};
SL4B_Statistics.prototype.setAverageLatency = function(A){this.m_nAverageLatency=A;};
SL4B_Statistics.prototype.getHeartbeatLatency = function(){return this.m_nHeartbeatLatency;
};
SL4B_Statistics.prototype.setHeartbeatLatency = function(A){this.m_nHeartbeatLatency=A;};
SL4B_Statistics.prototype.getHeartbeatAverageLatency = function(){return this.m_nHeartbeatAverageLatency;
};
SL4B_Statistics.prototype.setHeartbeatAverageLatency = function(A){this.m_nHeartbeatAverageLatency=A;};
function SL4B_Statistics_scriptLoaded(){SL4B_Accessor.setStatistics(new SL4B_Statistics());}
SL4B_Statistics_scriptLoaded();var SL4B_ResponseQueueStatistics=function(){this.m_nQueuedMessageCount=0;this.m_pQueuedBatches=[];this.m_nProcessedMessageCount=0;};
if(false){function SL4B_ResponseQueueStatistics(){}
}SL4B_ResponseQueueStatistics.prototype.addMessageBatch = function(A){this.m_nQueuedMessageCount+=A;this.m_pQueuedBatches[this.m_pQueuedBatches.length]=this._createMessageBatchInfo(A);};
SL4B_ResponseQueueStatistics.prototype._createMessageBatchInfo = function(A){var oCurrentTime=this._getTime();
SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"SL4B_ResponseQueueStatistics._createMessageBatchInfo(): creating batch info ({0}, {1})",A,oCurrentTime.getTime());return {numberOfMessages:A, batchQueuedTime:oCurrentTime, batchStartedTime:null};
};
SL4B_ResponseQueueStatistics.prototype._getTime = function(){return new Date();
};
SL4B_ResponseQueueStatistics.prototype.setMessageBatchStarted = function(){var oNow=this._getTime();
SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"SL4B_ResponseQueueStatistics.setMessageBatchStarted(): starting batch ({0}, {1}) at {2}",this.m_pQueuedBatches[0].numberOfMessages,this.m_pQueuedBatches[0].batchQueuedTime.getTime(),oNow.getTime());this.m_pQueuedBatches[0].batchStartedTime=oNow;};
SL4B_ResponseQueueStatistics.prototype.setMessageBatchEnded = function(){var oQueuedBatch=this.m_pQueuedBatches.shift();
if(oQueuedBatch){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"SL4B_ResponseQueueStatistics.setMessageBatchEnded(): finishing batch ({0}, {1})",oQueuedBatch.numberOfMessages,oQueuedBatch.batchQueuedTime.getTime());}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"SL4B_ResponseQueueStatistics.setMessageBatchEnded(): Queued batches has been emptied during message processing.");}};
SL4B_ResponseQueueStatistics.prototype.reset = function(){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"SL4B_ResponseQueueStatistics.reset()");this.m_nQueuedMessageCount=0;this.m_pQueuedBatches=[];};
SL4B_ResponseQueueStatistics.prototype.incrementProcessedMessageCount = function(){--this.m_nQueuedMessageCount;++this.m_nProcessedMessageCount;};
SL4B_ResponseQueueStatistics.prototype.getQueuedMessageCount = function(){return this.m_nQueuedMessageCount;
};
SL4B_ResponseQueueStatistics.prototype.getProcessedMessageCount = function(){return this.m_nProcessedMessageCount;
};
SL4B_ResponseQueueStatistics.prototype.getQueuedBatchCount = function(){return this.m_pQueuedBatches.length;
};
SL4B_ResponseQueueStatistics.prototype.getTimeOldestBatchWasQueued = function(){if(this.m_pQueuedBatches.length===0){return null;
}return this.m_pQueuedBatches[0].batchQueuedTime;
};
SL4B_ResponseQueueStatistics.prototype.getTimeOldestBatchBeganExecution = function(){if(this.m_pQueuedBatches.length===0){return null;
}return this.m_pQueuedBatches[0].batchStartedTime;
};
SL4B_ResponseQueueStatistics.prototype.toString = function(){return "SL4B_ResponseQueueStatistics: batchCount="+this.m_pQueuedBatches.length+", queuedMessageCount="+this.m_nQueuedMessageCount+", processedMessageCount="+this.m_nProcessedMessageCount;
};
var SL4B_ResponseQueueStatisticsListener=function(){};
if(false){function SL4B_ResponseQueueStatisticsListener(){}
}SL4B_ResponseQueueStatisticsListener.prototype.onBatchQueued = function(A){};
SL4B_ResponseQueueStatisticsListener.prototype.onBeforeBatchProcessed = function(A){};
SL4B_ResponseQueueStatisticsListener.prototype.onAfterBatchProcessed = function(A){};
var SL4B_Configuration=function(){};
if(false){function SL4B_Configuration(){}
}SL4B_Configuration = function(){this.m_oIndexScript=SL4B_ScriptLoader.getIndexScript();this.m_bUsingFile=false;this.m_bFileAttributeSet=false;this.m_DEFAULT_JAVA_CONNECTION_METHODS=new Array(SL4B_ConnectionMethod.JAVAHTTP,SL4B_ConnectionMethod.JAVAPOLLING);this.m_pBrowserStreamingConnectionMethodOverrides={};this.m_pBrowserPollingConnectionMethodOverrides={};this.m_pDefaultAttributes=new Object();this._getDefaultServerUrl = function(B,A){var l_sDefaultServerUrl;
var l_oMatch=B.match(/(^https?:\/\/[^\/]+\/)/);
if(l_oMatch){l_sDefaultServerUrl=l_oMatch[0];}else 
{l_sDefaultServerUrl="/";}return l_sDefaultServerUrl;
};
this.m_pDefaultAttributes["appletjar"]="applet.jar";this.m_pDefaultAttributes["user"]="demouser";this.m_pDefaultAttributes["password"]="demopass";this.m_pDefaultAttributes["credentialsprovider"]=SL4B_ScriptLoader.const_STANDARD_CREDENTIALS_PROVIDER;this.m_pDefaultAttributes["rttpprovider"]=SL4B_ScriptLoader.const_JAVASCRIPT_RTTP_PROVIDER;this.m_pDefaultAttributes["appletchecktimeout"]=15000;this.m_pDefaultAttributes["rttpapplettimeout"]=120000;this.m_pDefaultAttributes["debug"]=SL4B_DebugLevel.ERROR;this.m_pDefaultAttributes["logwindow"]=null;this.m_pDefaultAttributes["loglevel"]=null;this.m_pDefaultAttributes["retainlogmessages"]=200;this.m_pDefaultAttributes["decodefields"]=false;this.m_pDefaultAttributes["cleanuponbeforeunload"]=false;this.m_pDefaultAttributes["suppressexceptions"]="true";this.m_pDefaultAttributes["rttpdebuglevel"]=SL4B_DebugLevel.CRITICAL;this.m_pDefaultAttributes["consolelogging"]=true;this.m_pDefaultAttributes["debugwindowtype"]=SL4B_Logger.const_HTML;this.m_pDefaultAttributes["applicationid"]=null;this.m_pDefaultAttributes["discarddelay"]=10;this.m_pDefaultAttributes["jvmfileurl"]="http://www.caplin.com/products/jvm-list.js";this.m_pDefaultAttributes["jvmfiledownloadtimeout"]=10;this.m_pDefaultAttributes["logoutdelay"]=1;this.m_pDefaultAttributes["objectnamedelimiter"]=" ";this.m_pDefaultAttributes["statsinterval"]=5;this.m_pDefaultAttributes["statsreset"]=5000;this.m_pDefaultAttributes["statstimeout"]=30000;this.m_pDefaultAttributes["useconfig"]="server_config.js";this.m_pDefaultAttributes["updatebatchfreq"]=250;this.m_pDefaultAttributes["maxupdatebatchsize"]=5000;this.m_pDefaultAttributes["gcfreq"]=200;this.m_pDefaultAttributes["jscontainerpath"]="sl4b/javascript-rttp-provider";this.m_pDefaultAttributes["appletpath"]="applet";this.m_pDefaultAttributes["type3pollperiod"]=1000;this.m_pDefaultAttributes["type5reconnectcount"]=10000;this.m_pDefaultAttributes["type5padlength"]=1024*4;this.m_pDefaultAttributes["maxgetlength"]=null;this.m_pDefaultAttributes["connectiontimeout"]=15000;this.m_pDefaultAttributes["noopinterval"]=5000;this.m_pDefaultAttributes["nooptimeout"]=5000;this.m_pDefaultAttributes["requestchannelresponsetimeout"]=15000;this.m_pDefaultAttributes["logintimeout"]=10000;this.m_pDefaultAttributes["service"]=null;this.m_pDefaultAttributes["keymasterurl"]=null;this.m_pDefaultAttributes["keymasterattempts"]=3;this.m_pDefaultAttributes["keymasterconnectiontimeout"]=10000;this.m_pDefaultAttributes["keymasterkeepaliveattempts"]=1;this.m_pDefaultAttributes["keymasterkeepaliveinterval"]=30000;this.m_pDefaultAttributes["keymasterxhrurl"]=null;this.m_pDefaultAttributes["commondomain"]=null;this.m_pDefaultAttributes["enableautoloading"]=true;this.m_pDefaultAttributes["yieldbeforemessageprocessing"]=true;this.m_pDefaultAttributes["enablelatency"]=false;this.m_pDefaultAttributes["clocksyncbatchsize"]=5;this.m_pDefaultAttributes["clocksyncspacing"]=1000;this.m_pDefaultAttributes["clocksyncperiod"]=60000;this.m_pDefaultAttributes["clocksyncstrategy"]="GF_BatchingClockSyncStrategy";this.m_pDefaultAttributes["slidingsyncsize"]=12;this.m_pDefaultAttributes["slidingsyncspacing"]=5000;this.m_pDefaultAttributes["slidingsyncinitialspacing"]=500;this.m_pDefaultAttributes["heartbeatinterval"]="5000";this.m_pDefaultAttributes["timestampfield"]="INITIAL_TIMESTAMP";this.m_pDefaultAttributes["disablepolling"]=false;this.m_pDefaultAttributes["disablestreaming"]=false;this.m_pDefaultAttributes["flashtime"]=1000;this.m_pDefaultAttributes["bg"]=null;this.m_pDefaultAttributes["bgchange"]='rel';this.m_pDefaultAttributes["bgdn"]='red';this.m_pDefaultAttributes["bgeq"]='green';this.m_pDefaultAttributes["bgup"]='blue';this.m_pDefaultAttributes["fg"]=null;this.m_pDefaultAttributes["fgchange"]=null;this.m_pDefaultAttributes["fgdn"]='red';this.m_pDefaultAttributes["fgeq"]='black';this.m_pDefaultAttributes["fgflash"]='white';this.m_pDefaultAttributes["fgup"]='blue';this.m_pDefaultAttributes["plus"]=null;this.m_pDefaultAttributes["fractionhandling"]=null;this.m_pDefaultAttributes["gfxup"]=null;this.m_pDefaultAttributes["gfxdn"]=null;this.m_pDefaultAttributes["gfxeq"]=null;this.m_pDefaultAttributes["isencoded"]=0;this.m_pDefaultAttributes["todp"]=0;this.m_pDefaultAttributes["round"]='default';this.m_pDefaultAttributes["addcommas"]=0;this.m_pDefaultAttributes["tosf"]=null;this.m_pDefaultAttributes["serverurl"]=this._getDefaultServerUrl(this.m_oIndexScript.src,window.location.href);this.m_pDefaultAttributes["allowcors"]=null;this.m_pDefaultAttributes["flashconnectiondirpath"]=null;};
SL4B_Configuration.prototype.getScriptTagAttribute = SL_AD;SL4B_Configuration.prototype.getIntegerScriptTagAttribute = SL_DQ;SL4B_Configuration.prototype.getPositiveIntegerScriptTagAttribute = SL_PL;SL4B_Configuration.prototype.getIntegerScriptTagAttributeInMilliseconds = SL_LL;SL4B_Configuration.prototype.unaryScriptTagAttributeExists = function(A){return (this.getScriptTagAttribute(A)!=null);
};
SL4B_Configuration.prototype.setAttribute = function(B,A){this.m_bFileAttributeSet=true;this.m_pDefaultAttributes[B.toLowerCase()]=A;};
SL4B_Configuration.prototype.getAttribute = function(A){A=A.toLowerCase();return ((typeof this.m_pDefaultAttributes[A]=="undefined") ? null : this.m_pDefaultAttributes[A]);
};
SL4B_Configuration.prototype.getAppletPath = function(){var l_sValue=this.m_oIndexScript.getAttribute("appletpath");
if(l_sValue==null){l_sValue=this.getAttribute("appletpath");}return l_sValue.replace(/(^\/)/,"").replace(/\/$/,"");
};
SL4B_Configuration.prototype.getAppletUrl = function(){var l_sValue=this.m_oIndexScript.getAttribute("appleturl");
if(l_sValue==null){l_sValue=this.getAttribute("appleturl");}return ((l_sValue==null) ? null : l_sValue.replace(/\/$/,""));
};
SL4B_Configuration.prototype.getJsContainerPath = function(){var l_sValue=this.m_oIndexScript.getAttribute("jscontainerpath");
if(l_sValue==null){l_sValue=this.getAttribute("jscontainerpath");}return l_sValue.replace(/(^\/)/,"").replace(/\/$/,"");
};
SL4B_Configuration.prototype.getJsContainerUrl = function(){var l_sValue=this.m_oIndexScript.getAttribute("jscontainerurl");
if(l_sValue==null){l_sValue=this.getAttribute("jscontainerurl");}return ((l_sValue==null) ? null : l_sValue.replace(/\/$/,""));
};
SL4B_Configuration.prototype.getServerUrl = function(){var l_sServerUrl=this.getScriptTagAttribute("serverurl");
if(l_sServerUrl.match(/\/$/)==null){l_sServerUrl+="/";}return l_sServerUrl;
};
SL4B_Configuration.prototype.getAppletJarName = function(){return this.getScriptTagAttribute("appletjar");
};
SL4B_Configuration.prototype.getUsername = function(){return this.getScriptTagAttribute("user");
};
SL4B_Configuration.prototype.getPassword = function(){return this.getScriptTagAttribute("password");
};
SL4B_Configuration.prototype.includeRtsl = function(){return this.unaryScriptTagAttributeExists("includertsl");
};
SL4B_Configuration.prototype.includeRtml = function(){return this.unaryScriptTagAttributeExists("includertml");
};
SL4B_Configuration.prototype.isMasterFrame = function(){return !this.unaryScriptTagAttributeExists("usemasterframe");
};
SL4B_Configuration.prototype.getRegistrationWindowName = function(){var sName=SL4B_Accessor.getConfiguration().getScriptTagAttribute("registrationwindowname");
if(sName===undefined||sName===""){return null;
}return sName;
};
SL4B_Configuration.prototype.getCleanupOnBeforeUnload = function(){var b_CleanupOnBeforeUnload;
var l_sCleanupOnBeforeUnload=this.getScriptTagAttribute("cleanuponbeforeunload")+"";
if(l_sCleanupOnBeforeUnload===undefined||l_sCleanupOnBeforeUnload===""||l_sCleanupOnBeforeUnload===null){b_CleanupOnBeforeUnload=false;}else 
{b_CleanupOnBeforeUnload=(l_sCleanupOnBeforeUnload==="true") ? true : false;}return b_CleanupOnBeforeUnload;
};
SL4B_Configuration.prototype._$isRegistrationWindowEnabled = function(){return (this.getRegistrationWindowName()!==null);
};
SL4B_Configuration.prototype.isRedirectionEnabled = function(){return this.unaryScriptTagAttributeExists("enableredirection");
};
SL4B_Configuration.prototype.isMultiSource = function(){return this.unaryScriptTagAttributeExists("multisource");
};
SL4B_Configuration.prototype.getService = function(){return this.getScriptTagAttribute("service");
};
SL4B_Configuration.prototype.getKeyMasterConnectionTimeout = function(){return this.getScriptTagAttribute("keymasterconnectiontimeout");
};
SL4B_Configuration.prototype.getKeyMasterKeepAliveInterval = function(){return this.getScriptTagAttribute("keymasterkeepaliveinterval");
};
SL4B_Configuration.prototype.isEnableLatency = function(){var rtn=this.getScriptTagAttribute("enablelatency");
if(typeof rtn=="string"){rtn=(rtn.toLowerCase()=="true");}return rtn;
};
SL4B_Configuration.prototype.isConsolelogging = function(){var rtn=this.getParameterFromQueryString("consolelogging");
if(rtn==null){rtn=this.getScriptTagAttribute("consolelogging");}if(typeof rtn=="string"){rtn=(rtn.toLowerCase()=="true");}return rtn;
};
SL4B_Configuration.prototype.isPollingDisabled = function(){return this.getScriptTagAttribute("disablepolling");
};
SL4B_Configuration.prototype.isStreamingDisabled = function(){return this.getScriptTagAttribute("disablestreaming");
};
SL4B_Configuration.prototype.getClockSyncStrategy = function(){return this.getScriptTagAttribute("clocksyncstrategy");
};
SL4B_Configuration.prototype.getSlidingSyncSize = function(){return this.getScriptTagAttribute("slidingsyncsize");
};
SL4B_Configuration.prototype.getSlidingSyncSpacing = function(){return this.getScriptTagAttribute("slidingsyncspacing");
};
SL4B_Configuration.prototype.getSlidingSyncInitialSpacing = function(){return this.getScriptTagAttribute("slidingsyncinitialspacing");
};
SL4B_Configuration.prototype.getClocksyncBatchSize = function(){return this.getScriptTagAttribute("clocksyncbatchsize");
};
SL4B_Configuration.prototype.getClocksyncSpacing = function(){return this.getScriptTagAttribute("clocksyncspacing");
};
SL4B_Configuration.prototype.getClocksyncPeriod = function(){return this.getScriptTagAttribute("clocksyncperiod");
};
SL4B_Configuration.prototype.getTimestampField = function(){return this.getScriptTagAttribute("timestampfield");
};
SL4B_Configuration.prototype.getHeartbeatInterval = function(){return this.getScriptTagAttribute("heartbeatinterval");
};
SL4B_Configuration.prototype.getFrameId = function(){var l_sId;
if(this.isMasterFrame()){l_sId="master";}else 
{l_sId=this.getScriptTagAttribute("frameid");if(l_sId==null){l_sId=this.getFrameLocation();if(l_sId==null){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"Configuration.getFrameId: The slave frame did not have a id defined on it. This identifier is required for the master/slave functionality to work.");}}}return l_sId;
};
SL4B_Configuration.prototype.getFrameLocation = function(){return this.getScriptTagAttribute("thisframe");
};
SL4B_Configuration.prototype.getMasterFrameLocation = function(){return this.getScriptTagAttribute("usemasterframe");
};
SL4B_Configuration.prototype.getCredentialsProvider = function(){if(this.getKeyMasterUrl()==null||this.getKeyMasterUrl()==""){return this.getScriptTagAttribute("credentialsprovider");
}else 
{return SL4B_ScriptLoader.const_KEYMASTER_CREDENTIALS_PROVIDER;
}};
SL4B_Configuration.prototype.getParameterFromQueryString = function(A){var l_sParameterValue=null;

try {var l_oNextWindow;

do{l_oNextWindow=(l_oNextWindow) ? l_oNextWindow.parent : window;l_sParameterValue=this.getParameterFromProvidedQueryString(l_oNextWindow.location.href,A);}while((!l_sParameterValue)&&(l_oNextWindow.parent!=l_oNextWindow));}catch(e){}
return l_sParameterValue;
};
SL4B_Configuration.prototype.getParameterFromProvidedQueryString = function(B,A){var l_oMatches=B.match(new RegExp("[?&]"+A+"=([^&#]+)"));
return (l_oMatches) ? l_oMatches[1] : null;
};
SL4B_Configuration.prototype.getDebugLevel = function(){var l_sLevel;
var l_sLogLevel=this.getScriptTagAttribute("loglevel");
if(l_sLogLevel!=null){l_sLevel=l_sLogLevel;}else 
{var l_sDebug=this.getParameterFromQueryString("debug");
if(l_sDebug!=null){l_sLevel=l_sDebug;}else 
{l_sLevel=this.getScriptTagAttribute("debug");}}return l_sLevel;
};
SL4B_Configuration.prototype.isLogWindowNeeded = function(){var logWindowNeeded=this.getScriptTagAttribute("logwindow");
if(logWindowNeeded==null){var l_sLevel;
var l_sDebug=this.getParameterFromQueryString("debug");
if(l_sDebug!=null){l_sLevel=l_sDebug;}else 
{var debugAttribute=this.getScriptTagAttribute("debug");

try {SL4B_DebugLevel.getNumericDebugLevel(debugAttribute);l_sLevel=debugAttribute;}catch(e){}
}if(l_sLevel!=null&&l_sLevel!=SL4B_DebugLevel.ERROR){logWindowNeeded=true;}else 
{logWindowNeeded=false;}}else 
{logWindowNeeded=logWindowNeeded=="true";}return logWindowNeeded;
};
SL4B_Configuration.prototype.getNumberOfLogMessagesToRetain = function(){return this.getPositiveIntegerScriptTagAttribute("retainlogmessages");
};
SL4B_Configuration.prototype.getDecodeFields = function(){var bDecodeFields=this.m_pDefaultAttributes["decodefields"];
var sDecodeFields=this.getParameterFromQueryString("decodefields");
if(sDecodeFields!==null){bDecodeFields=(sDecodeFields==="true") ? true : false;}else 
{sDecodeFields=this.getScriptTagAttribute("decodefields");if(sDecodeFields!==null){bDecodeFields=(sDecodeFields==="true") ? true : false;}}return bDecodeFields;
};
SL4B_Configuration.prototype.isSuppressExceptions = function(){var l_sSuppressExceptions=this.getParameterFromQueryString("suppressexceptions");
return (((l_sSuppressExceptions) ? l_sSuppressExceptions : this.getScriptTagAttribute("suppressexceptions"))==="true");
};
SL4B_Configuration.prototype.getRttpDebugLevel = function(){var l_sDebugLevel=this.getParameterFromQueryString("rttpdebuglevel");
return (l_sDebugLevel) ? l_sDebugLevel : this.getScriptTagAttribute("rttpdebuglevel");
};
SL4B_Configuration.prototype.getAppletCheckTimeout = function(){return this.getIntegerScriptTagAttribute("appletchecktimeout");
};
SL4B_Configuration.prototype.getRttpAppletTimeout = function(){return this.getIntegerScriptTagAttribute("rttpapplettimeout");
};
SL4B_Configuration.prototype.includeFlash = SL_KF;SL4B_Configuration.prototype.getRttpProvider = SL_NV;SL4B_Configuration.prototype.getDebugWindowType = SL_DD;SL4B_Configuration.prototype.isAutoLoadingEnabled = SL_OA;SL4B_Configuration.prototype.yieldBeforeMessageProcessing = SL_LU;SL4B_Configuration.prototype.getCommonDomain = SL_QB;SL4B_Configuration.prototype.loadConfigurationFile = SL_BA;SL4B_Configuration.prototype.loaded = SL_RJ;SL4B_Configuration.prototype.getConnectionMethods = SL_PS;SL4B_Configuration.prototype.setPollingConnectionMethods = SL_PF;SL4B_Configuration.prototype.setStreamingConnectionMethods = SL_GO;SL4B_Configuration.prototype.getPollingConnectionMethods = SL_CP;SL4B_Configuration.prototype.getStreamingConnectionMethods = SL_RO;SL4B_Configuration.prototype.getType3PollPeriod = SL_QF;SL4B_Configuration.prototype.getType5ReconnectCount = SL_HA;SL4B_Configuration.prototype.getType5PadLength = SL_FI;SL4B_Configuration.prototype.getConnectionTimeout = SL_II;SL4B_Configuration.prototype.getNOOPInterval = SL_IB;SL4B_Configuration.prototype.getNOOPTimeout = SL_FC;SL4B_Configuration.prototype.getRequestChannelResponseTimeout = SL_GH;SL4B_Configuration.prototype.getLoginTimeout = SL_MO;SL4B_Configuration.prototype.getMaxGetLength = SL_QK;SL4B_Configuration.prototype.getApplicationId = function(){var l_sClientApplicationId=this.getScriptTagAttribute("applicationid");
return "SL4B"+((l_sClientApplicationId==null||l_sClientApplicationId=="") ? "" : ":"+l_sClientApplicationId);
};
SL4B_Configuration.prototype.getDiscardDelay = function(){return this.getIntegerScriptTagAttribute("discarddelay");
};
SL4B_Configuration.prototype.getJvmFileUrl = function(){return this.getScriptTagAttribute("jvmfileurl");
};
SL4B_Configuration.prototype.getJvmFileDownloadTimeout = function(){return this.getIntegerScriptTagAttribute("jvmfiledownloadtimeout");
};
SL4B_Configuration.prototype.getLogoutDelay = function(){return this.getIntegerScriptTagAttribute("logoutdelay");
};
SL4B_Configuration.prototype.getObjectNameDelimiter = function(){return this.getScriptTagAttribute("objectnamedelimiter");
};
SL4B_Configuration.prototype.getStatsInterval = function(){return this.getIntegerScriptTagAttributeInMilliseconds("statsinterval");
};
SL4B_Configuration.prototype.getStatsReset = function(){return this.getIntegerScriptTagAttributeInMilliseconds("statsreset");
};
SL4B_Configuration.prototype.getStatsTimeout = function(){return this.getIntegerScriptTagAttributeInMilliseconds("statstimeout");
};
SL4B_Configuration.prototype.getKeyMasterUrl = function(){return this.getScriptTagAttribute("keymasterurl");
};
SL4B_Configuration.prototype.getKeyMasterAttempts = function(){return this.getIntegerScriptTagAttribute("keymasterattempts");
};
SL4B_Configuration.prototype.getKeyMasterKeepAliveAttempts = function(){return this.getIntegerScriptTagAttribute("keymasterkeepaliveattempts");
};
SL4B_Configuration.prototype.getKeyMasterXHRUrl = function(){return this.getScriptTagAttribute("keymasterxhrurl");
};
SL4B_Configuration.prototype.getUseConfig = function(){return this.getScriptTagAttribute("useconfig");
};
SL4B_Configuration.prototype.getGarbageCollectionFrequency = function(){return this.getPositiveIntegerScriptTagAttribute("gcfreq");
};
SL4B_Configuration.prototype.getMicrosoftGarbageCollectionFrequency = function(){return this.getPositiveIntegerScriptTagAttribute("gcfreqms");
};
SL4B_Configuration.prototype.getSunGarbageCollectionFrequency = function(){return this.getPositiveIntegerScriptTagAttribute("gcfreqsun");
};
SL4B_Configuration.prototype.getCallbackBatchFrequency = function(){return ((this.getRttpProvider()==SL4B_ScriptLoader.const_JAVASCRIPT_RTTP_PROVIDER) ? 0 : this.getIntegerScriptTagAttribute("updatebatchfreq"));
};
SL4B_Configuration.prototype.getMaximumCallbackBatchSize = function(){return this.getPositiveIntegerScriptTagAttribute("maxupdatebatchsize");
};
SL4B_Configuration.prototype.isNoStaleNotify = function(){return this.unaryScriptTagAttributeExists("nostalenotify");
};
SL4B_Configuration.prototype.isNoBrowserStatus = function(){return this.unaryScriptTagAttributeExists("nobrowserstatus");
};
SL4B_Configuration.prototype.isNoStats = function(){return this.unaryScriptTagAttributeExists("nostats");
};
SL4B_Configuration.prototype.isPortlet = function(){return this.unaryScriptTagAttributeExists("portlet");
};
SL4B_Configuration.prototype.isDisableJvmDownload = function(){return this.unaryScriptTagAttributeExists("disablejvmdownload");
};
SL4B_Configuration.prototype.isMultiUpdates = function(){return this.unaryScriptTagAttributeExists("multiupdates");
};
SL4B_Configuration.prototype.isUseStaticHtmlFailoverContainers = function(){return this.unaryScriptTagAttributeExists("usestatichtmlfailovercontainers");
};
SL4B_Configuration.prototype.isDirectEnable = function(){return this.unaryScriptTagAttributeExists("directEnable");
};
SL4B_Configuration.prototype.isHttpEnable = function(){return this.unaryScriptTagAttributeExists("httpEnable");
};
SL4B_Configuration.prototype.isRefreshEnable = function(){return this.unaryScriptTagAttributeExists("refreshEnable");
};
SL4B_Configuration.prototype.allowCORS = function(){return this.unaryScriptTagAttributeExists("allowcors");
};
SL4B_Configuration.prototype.getContainerFrameLocation = function(){var l_sCommonContainerFrame=this.getScriptTagAttribute("containerframelocation");
if(l_sCommonContainerFrame==null){var l_sMasterFrameLocation=this.getMasterFrameLocation();
if(l_sMasterFrameLocation!=null&&l_sMasterFrameLocation!=""){var l_oMatch=l_sMasterFrameLocation.match(/(((window)|(self)|(parent)|(top)|(opener))(\.|$))*/);
if(l_oMatch!=null&&l_oMatch[0]!=""){l_sCommonContainerFrame=l_oMatch[0].replace(/\.$/,"");}else 
{l_sCommonContainerFrame="top";}}else 
{l_sCommonContainerFrame="top";}}return l_sCommonContainerFrame;
};
function SL_AD(A){A=A.toLowerCase();var l_sValue=this.m_oIndexScript.getAttribute(A);
if(l_sValue==null){l_sValue=this.getAttribute(A);}return l_sValue;
}
function SL_DQ(A){A=A.toLowerCase();var l_nIntegerValue=this.getAttribute(A);
var l_sValue=this.m_oIndexScript.getAttribute(A);
if(this.checkInteger(l_sValue)==true){l_nIntegerValue=parseInt(l_sValue);}return l_nIntegerValue;
}
SL4B_Configuration.prototype.checkInteger = function(A){var isValid=false;
if(A!==null&&!isNaN(A)){var l_nTestValue=parseInt(A,10);
if(l_nTestValue>=0){isValid=true;}}return isValid;
};
function SL_PL(A){A=A.toLowerCase();var l_nPositiveIntegerValue=this.getAttribute(A);
var l_sValue=this.m_oIndexScript.getAttribute(A);
if(this.checkPositiveInteger(l_sValue)==true){l_nPositiveIntegerValue=parseInt(l_sValue);}return l_nPositiveIntegerValue;
}
SL4B_Configuration.prototype.checkPositiveInteger = function(A){var isValid=false;
if(this.checkInteger(A)==true&&parseInt(A)>0){isValid=true;}return isValid;
};
function SL_LL(A){A=A.toLowerCase();var l_nAttributeValue=this.getIntegerScriptTagAttribute(A);
if(l_nAttributeValue==null){l_nAttributeValue=this.getAttribute(A);}else 
if(l_nAttributeValue<500){l_nAttributeValue=l_nAttributeValue*1000;}return l_nAttributeValue;
}
function SL_KF(){return (this.includeRtml()||this.unaryScriptTagAttributeExists("enableflash"));
}
function SL_PF(A,B){if(!SL4B_Browser.isKnownBrowser(A)){throw new SL4B_Error("Configuration method setPollingConnectionMethods called with invalid SL4B_Browser parameter: "+A);
}if(B==null){delete this.m_pBrowserPollingConnectionMethodOverrides[A];}else 
{var l_pConnectionMethods=this.m_pBrowserPollingConnectionMethodOverrides[A];
if(l_pConnectionMethods==null){l_pConnectionMethods=[];this.m_pBrowserPollingConnectionMethodOverrides[A.toString()]=l_pConnectionMethods;}for(var i=0;i<B.length;i++){var l_oConnectionMethod=B[i];
if(!(l_oConnectionMethod instanceof SL4B_ConnectionMethod)){throw new SL4B_Error("Configuration method setPollingConnectionMethods called with invalid SL4B_ConnectionMethod parameter: "+l_oConnectionMethod);
}if(!l_oConnectionMethod.isPolling()){throw new SL4B_Error("Configuration method setPollingConnectionMethods called with non-polling SL4B_ConnectionMethod parameter: "+l_oConnectionMethod);
}l_pConnectionMethods.push(l_oConnectionMethod);}}}
function SL_CP(){if(this.isPollingDisabled()){return [];
}else 
{var l_oBrowser=SL4B_Accessor.getBrowserAdapter().getBrowser();
var l_pPolling=this.m_pBrowserPollingConnectionMethodOverrides[l_oBrowser.toString()];
if(l_pPolling==null){l_pPolling=SL4B_Accessor.getBrowserAdapter().getPollingConnectionTypes();}return l_pPolling;
}}
function SL_GO(A,B){if(!SL4B_Browser.isKnownBrowser(A)){throw new SL4B_Error("Configuration method setStreamingConnectionMethods called with invalid SL4B_Browser parameter: "+A);
}if(B==null){delete this.m_pBrowserStreamingConnectionMethodOverrides[A];}else 
{var l_pConnectionMethods=this.m_pBrowserStreamingConnectionMethodOverrides[A];
if(l_pConnectionMethods==null){l_pConnectionMethods=[];this.m_pBrowserStreamingConnectionMethodOverrides[A.toString()]=l_pConnectionMethods;}for(var i=0;i<B.length;i++){var l_oConnectionMethod=B[i];
if(!(l_oConnectionMethod instanceof SL4B_ConnectionMethod)){throw new SL4B_Error("Configuration method setStreamingConnectionMethods called with invalid SL4B_ConnectionMethod parameter: "+l_oConnectionMethod);
}if(!l_oConnectionMethod.isStreaming()){throw new SL4B_Error("Configuration method setStreamingConnectionMethods called with non-streaming SL4B_ConnectionMethod parameter: "+l_oConnectionMethod);
}l_pConnectionMethods.push(l_oConnectionMethod);}}}
function SL_RO(){if(this.isStreamingDisabled()){return [];
}else 
{var l_oBrowser=SL4B_Accessor.getBrowserAdapter().getBrowser();
var l_pStreaming=this.m_pBrowserStreamingConnectionMethodOverrides[l_oBrowser.toString()];
if(l_pStreaming==null){l_pStreaming=SL4B_Accessor.getBrowserAdapter().getStreamingConnectionTypes();}return l_pStreaming;
}}
function SL_PS(){var l_sConnectionTypes=this.getScriptTagAttribute("connectiontypes");
var l_oConnectionMethods=[];
if(l_sConnectionTypes!=null){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"The Configuration attribute 'connectiontypes' has been deprecated, "+"use the methods SL4B_Configuration.SetPollingConnectionMethods() and SL4B_Configuration.SetStreamingConnectionMethods() "+" to change the default connection methods.");var l_pTypes=l_sConnectionTypes.split(",");
for(var i=0;i<l_pTypes.length;i++){var l_nType=l_pTypes[i];
if(SL4B_ConnectionMethod.AllMethods[l_nType]==null){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"Configuration attribute 'connectiontypes' contains invalid connection type: "+l_nType);}else 
{l_oConnectionMethods.push(SL4B_ConnectionMethod.AllMethods[l_nType]);}}}else 
{if(this.isDirectEnable()||this.isHttpEnable()||this.isRefreshEnable()){if(this.isRefreshEnable()){l_oConnectionMethods.push(SL4B_ConnectionMethod.POLLING);}else 
{l_oConnectionMethods.push(SL4B_ConnectionMethod.FOREVER_FRAME);l_oConnectionMethods.push(SL4B_ConnectionMethod.POLLING);}}}return l_oConnectionMethods;
}
function SL_QF(){return this.getIntegerScriptTagAttribute("type3pollperiod");
}
function SL_HA(){return this.getIntegerScriptTagAttribute("type5reconnectcount");
}
function SL_FI(){return this.getIntegerScriptTagAttribute("type5padlength");
}
function SL_II(){return this.getPositiveIntegerScriptTagAttribute("connectiontimeout");
}
function SL_IB(){return this.getPositiveIntegerScriptTagAttribute("noopinterval");
}
function SL_FC(){return this.getPositiveIntegerScriptTagAttribute("nooptimeout");
}
function SL_GH(){return this.getPositiveIntegerScriptTagAttribute("requestchannelresponsetimeout");
}
function SL_MO(){return this.getPositiveIntegerScriptTagAttribute("logintimeout");
}
function SL_QK(){return this.getIntegerScriptTagAttribute("maxgetlength");
}
function SL_NV(){return this.getScriptTagAttribute("rttpprovider").toLowerCase();
}
function SL_DD(){return this.getScriptTagAttribute("debugwindowtype");
}
function SL_OA(){var l_vEnableAutoLoading=this.getScriptTagAttribute("enableautoloading");
if(typeof l_vEnableAutoLoading=="string"){l_vEnableAutoLoading=(l_vEnableAutoLoading.toLowerCase()=="true");}return l_vEnableAutoLoading;
}
function SL_LU(){var l_vYield=this.getScriptTagAttribute("yieldbeforemessageprocessing");
if(typeof l_vYield=="string"){l_vYield=(l_vYield.toLowerCase()=="true");}return l_vYield;
}
function SL_QB(){var l_sCommonDomain=this.getScriptTagAttribute("commondomain");
if(l_sCommonDomain==null){l_sCommonDomain=GF_CommonDomainExtractor.getCommonDomain();}return l_sCommonDomain;
}
function SL_BA(){var l_sConfigurationFileUrl=this.getScriptTagAttribute("configurationfile");
if(l_sConfigurationFileUrl!=null){this.m_bUsingFile=true;if(typeof XMLHttpRequest!='undefined'){l_oXmlHttpRequest=new XMLHttpRequest();}else 
{
try {l_oXmlHttpRequest=new ActiveXObject("Msxml2.XMLHTTP");}catch(e){l_oXmlHttpRequest=new ActiveXObject("Microsoft.XMLHTTP");}
}l_oXmlHttpRequest.open("GET",l_sConfigurationFileUrl,false);l_oXmlHttpRequest.send(null);if(l_oXmlHttpRequest.status==200){eval(l_oXmlHttpRequest.responseText);SL4B_Accessor.getConfiguration().loaded();}else 
{this.loaded();}}else 
{this.loaded();}}
function SL_RJ(){if(this.m_bUsingFile&&!this.m_bFileAttributeSet){SL4B_Accessor.getLogger().log(SL4B_DebugLevel.const_WARN_INT,"Configuration.loaded: Specified configuration file was empty. There may have been an error loading the file!");}SL4B_Accessor.getLogger().printMessage(SL4B_Version.getVersionInfo());SL4B_Accessor.getLogger().printMessage("SL4B running in browser: "+navigator.userAgent);var l_pMethodNamesToIgnoreSet=new Object();
l_pMethodNamesToIgnoreSet["getIntegerScriptTagAttributeInMilliseconds"]=true;l_pMethodNamesToIgnoreSet["getPositiveIntegerScriptTagAttribute"]=true;l_pMethodNamesToIgnoreSet["getIntegerScriptTagAttribute"]=true;l_pMethodNamesToIgnoreSet["getScriptTagAttribute"]=true;l_pMethodNamesToIgnoreSet["getAttribute"]=true;l_pMethodNamesToIgnoreSet["getParameterFromProvidedQueryString"]=true;l_pMethodNamesToIgnoreSet["getParameterFromQueryString"]=true;var l_oConfigurationMethodRegExp=/(^get)|(^is)|(^include)/;
var l_pMethodNames=new Array();
for(l_sMethod in this){if(l_sMethod.match(l_oConfigurationMethodRegExp)&&!l_pMethodNamesToIgnoreSet[l_sMethod]&&typeof this[l_sMethod]=="function"){l_pMethodNames.push(l_sMethod);}}l_pMethodNames.sort();for(var l_nMethod=0,l_nLength=l_pMethodNames.length;l_nMethod<l_nLength;++l_nMethod){var l_sMethod=l_pMethodNames[l_nMethod];
SL4B_Accessor.getLogger().printMessage("SL4B configuration ["+l_sMethod+"()]: "+this[l_sMethod]());}SL4B_Accessor.getLogger().printMessage("SL4B configuration [yieldBeforeMessageProcessing()]: "+this.yieldBeforeMessageProcessing());SL4B_ScriptLoader.loadConfiguredScripts(this);}
function SL_KJ(){
try {SL4B_Accessor.setConfiguration(new SL4B_Configuration());}catch(e){SL4B_Accessor.getExceptionHandler().processException(e);}
SL4B_Accessor.getConfiguration().loadConfigurationFile();SL4B_Accessor.getCapabilities().add("HttpRequestLineLength",SL4B_Accessor.getConfiguration().getMaxGetLength());if(!SL4B_Accessor.getConfiguration().isLogWindowNeeded()){SL4B_Accessor.getLogger().removeLogWindowMessageListener();}}
var SL4B_MethodInvocationProxy=function(){};
if(false){function SL4B_MethodInvocationProxy(){}
}SL4B_MethodInvocationProxy = function(){if(SL4B_Accessor.getConfiguration().isSuppressExceptions()===true&&!SL4B_JsUnit.isUnobfuscated()){SL4B_MethodInvocationProxy.prototype.invoke = SL4B_MethodInvocationProxy.prototype._invokeWithTryCatch;}else 
{SL4B_MethodInvocationProxy.prototype.invoke = SL4B_MethodInvocationProxy.prototype._invoke;}};
SL4B_MethodInvocationProxy.prototype.invoke = function(B,A,D,C){throw new SL4B_Error("MethodInvocationProxy.invoke: method not implemented");
};
SL4B_MethodInvocationProxy.prototype._invoke = function(B,A,C){B[A].call(B,C[0],C[1],C[2],C[3],C[4],C[5],C[6],C[7],C[8],C[9]);};
SL4B_MethodInvocationProxy.prototype._invokeWithTryCatch = function(B,A,D,C){
try {this._invoke(B,A,D);}catch(e){if(C!=null){C(e);}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_ERROR_INT,"MethodInvocationProxy._invokeWithTryCatch: Error processing callback for object \"{0}\" (method: \"{1}\", parameters: \"{2}\"; Unexpected exception ({3})",B,A,D.join(),SL4B_Accessor.getBrowserAdapter().convertExceptionToString(e));}}
};
function SL_HO(){SL4B_MethodInvocationProxy=new SL4B_MethodInvocationProxy();}
var SL4B_AbstractCredentialsProvider=function(){};
if(false){function SL4B_AbstractCredentialsProvider(){}
}SL4B_AbstractCredentialsProvider = function(){};
SL4B_AbstractCredentialsProvider.prototype.getCredentials = function(A){throw new SL4B_Error("getCredentials method not implemented");
};
SL4B_AbstractCredentialsProvider.prototype.getUsername = function(){throw new SL4B_Error("getUsername method not implemented");
};
var SL4B_StandardCredentialsProvider=function(){};
if(false){function SL4B_StandardCredentialsProvider(){}
}SL4B_StandardCredentialsProvider = function(){};
SL4B_StandardCredentialsProvider.prototype = new SL4B_AbstractCredentialsProvider;SL4B_StandardCredentialsProvider.prototype.getCredentials = SL_BL;SL4B_StandardCredentialsProvider.prototype.getUsername = SL_MV;function SL_BL(A){var l_oConfiguration=SL4B_Accessor.getConfiguration();
A.login(l_oConfiguration.getUsername(),l_oConfiguration.getPassword());}
function SL_MV(){return SL4B_Accessor.getConfiguration().getUsername();
}
function SL_RW(){SL4B_Accessor.setCredentialsProvider(new SL4B_StandardCredentialsProvider());}
if(false){function SL_HL(){}
}C_DynamicIFrameLoader = new function(){var iframeLoader=null;
var l_oIFrameIdentifierToElementMap=new Object();
var l_oIFrameIdentifierToTimeoutMap=new Object();
function SL_SS(){if(iframeLoader==null){iframeLoader=document.createElement('div');iframeLoader.setAttribute("id","iframeloader");document.body.appendChild(iframeLoader);}return iframeLoader;
}
this.loadIFrame = function(C,D,B,A){if(typeof B.scriptLoadTimeout!="function"){throw new SL4B_Exception("C_DynamicIFrameLoader.loadIFrame: l_oErrorCallback parameter must implement scriptLoadTimeout() ");
}D+=((D.indexOf("?")==-1) ? "?" : "&")+"time="+(new Date()).valueOf();var l_sCommonDomain=SL4B_Accessor.getConfiguration().getCommonDomain();
if(l_sCommonDomain!=null){D+="&"+SL4B_JavaScriptRttpProviderConstants.const_DOMAIN_PARAMETER+"="+l_sCommonDomain;}var l_oIFrameElement=l_oIFrameIdentifierToElementMap[C];
if(typeof l_oIFrameElement!="undefined"){SL_SS().removeChild(l_oIFrameElement);}SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"Creating IFRAME for {0} at URL {1} with a timeout of {2}ms",C,D,A);l_oIFrameElement=document.createElement('iframe');l_oIFrameElement.src=D;l_oIFrameElement.style.display="none";l_oIFrameIdentifierToElementMap[C]=l_oIFrameElement;SL_SS().appendChild(l_oIFrameElement);var self=this;
var l_nTimeout=setTimeout(function(){self.checkLoaded(C,B);},A);
l_oIFrameIdentifierToTimeoutMap[C]=l_nTimeout;};
this.checkLoaded = function(B,A){var l_nTimeout=l_oIFrameIdentifierToTimeoutMap[B];
if(typeof l_nTimeout!="undefined"){this.clearIFrame(B);A.scriptLoadTimeout(B);}};
this.iframeLoaded = function(A){var l_nTimeout=l_oIFrameIdentifierToTimeoutMap[A];
if(typeof l_nTimeout!="undefined"){clearTimeout(l_nTimeout);this.clearIFrame(A);}};
this.clearIFrame = function(A){var l_oIFrameElement=l_oIFrameIdentifierToElementMap[A];
if(typeof l_oIFrameElement!="undefined"){SL_SS().removeChild(l_oIFrameElement);}delete (l_oIFrameIdentifierToElementMap[A]);delete (l_oIFrameIdentifierToTimeoutMap[A]);};
};
var SL4B_KeymasterIFrameManager=function(){};
if(false){function SL4B_KeymasterIFrameManager(){}
}SL4B_KeymasterIFrameManager = function(A,B){this.m_sKeyMasterUrl=B;this.m_sLastLoadedUrl="";this.m_oListener=A;this.nKeepAliveTimer=null;SL4B_Logger.logConnectionMessage(true,"Using deprecated iframe loading method to access keymaster. Use XHRKeymaster instead of StandardKeyMaster : {0}",this.m_sKeyMasterUrl);};
SL4B_KeymasterIFrameManager.prototype.getCredentials = function(A){SL4B_Logger.logConnectionMessage(true,"KeyMasterCredentialsProvider.getCredentials: Requesting credentials");var nConnectionTimeout=SL4B_Accessor.getConfiguration().getKeyMasterConnectionTimeout();
var l_sSeparator=((this.m_sKeyMasterUrl.indexOf("?")!=-1) ? "&" : "?");
this.m_sLastLoadedUrl=this.m_sKeyMasterUrl+l_sSeparator+"type=html&sourceid="+SL4B_KeymasterIFrameManager.escapeParameter(A);SL4B_Logger.logConnectionMessage(true,"SL4B_KeyMaster.requestToken: Credentials being requested from {0}",this.m_sLastLoadedUrl);C_DynamicIFrameLoader.loadIFrame("keymaster",this.m_sLastLoadedUrl,this,nConnectionTimeout);};
SL4B_KeymasterIFrameManager.prototype.startKeepAlive = function(C,A,B){if(this.nKeepAliveTimer==null){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINE_INT,"KeyMaster: Starting keep-alive polling");var oThis=this;
this.nKeepAliveTimer=setInterval(function(){oThis.sendKeepAlive();},A);}};
SL4B_KeymasterIFrameManager.prototype.stopKeepAlive = function(){if(this.nKeepAliveTimer!=null){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINE_INT,"KeyMaster: Stopping keep-alive polling");clearInterval(this.nKeepAliveTimer);this.nKeepAliveTimer=null;}};
SL4B_KeymasterIFrameManager.prototype.sendKeepAlive = function(){var l_sUrl="";
var l_oMatch=this.m_sKeyMasterUrl.match(/((^|\/)servlet\/)/);
if(l_oMatch!=null){l_sUrl=this.m_sKeyMasterUrl.replace(/((^|\/)servlet\/.*)/,l_oMatch[2]+"poll.jsp");}else 
{l_sUrl=this.m_sKeyMasterUrl.replace(/[^\/]+$/,"poll.jsp");}SL4B_Logger.log(SL4B_DebugLevel.const_DEBUG_INT,"KeyMaster: Sending session keep-alive at URL {0}",l_sUrl);C_DynamicIFrameLoader.loadIFrame("keymaster-poll",l_sUrl,this,SL4B_Accessor.getConfiguration().getKeyMasterConnectionTimeout());};
SL4B_KeymasterIFrameManager.prototype.scriptLoadTimeout = function(A){if(A=="keymaster"){this.m_oListener.failed("Could not load keymaster script",this.m_sLastLoadedUrl,null,0);}else 
if(A=="keymaster-poll"){var l_sUrl="";
var l_oMatch=this.m_sKeyMasterUrl.match(/((^|\/)servlet\/)/);
if(l_oMatch!=null){l_sUrl=this.m_sKeyMasterUrl.replace(/((^|\/)servlet\/.*)/,l_oMatch[2]+"poll.jsp");}else 
{l_sUrl=this.m_sKeyMasterUrl.replace(/[^\/]+$/,"poll.jsp");}this.m_oListener.keepAliveFailure("Keep-alive polling timed out.",l_sUrl,null,0);}};
SL4B_KeymasterIFrameManager.escapeParameter = function(A){return escape(A).replace(/[.]/g,"%2E").replace(/@/g,"%40");
};
function SigGen_Signature(B,A,C){
try {C_DynamicIFrameLoader.iframeLoaded("keymaster");SL4B_Accessor.getCredentialsProvider().success(A,C);}catch(e){SL4B_Logger.logConnectionMessage(true,"SigGen_Signature: failed to retrieve SL4B_CredentialsProvider "+e.message);SL4B_Accessor.getExceptionHandler().processException(e);}
}
function SigGen_AuthenticationFailed(B,A){
try {C_DynamicIFrameLoader.iframeLoaded("keymaster");SL4B_Accessor.getCredentialsProvider().failed(A,SL4B_Accessor.getCredentialsProvider().m_oRequestManager.m_sLastLoadedUrl,null,200);}catch(e){SL4B_Logger.logConnectionMessage(true,"SigGen_AuthenticationFailed: failed to retrieve SL4B_CredentialsProvider "+e.message);SL4B_Accessor.getExceptionHandler().processException(e);}
}
function SigGen_KeepAliveLoaded(){C_DynamicIFrameLoader.iframeLoaded("keymaster-poll");SL4B_Accessor.getCredentialsProvider().keepAliveSuccess();}
var SL4B_KeymasterXHRManager=function(){};
if(false){function SL4B_KeymasterXHRManager(A,B){}
}SL4B_KeymasterXHRManager = function(A,B){this.m_sKeyMasterUrl=B;var sCommonDomain=SL4B_Accessor.getConfiguration().getCommonDomain();
if(sCommonDomain!=null){this.m_sKeyMasterUrl=SL4B_KeymasterXHRManager.appendParameter(this.m_sKeyMasterUrl,"domain",sCommonDomain);}this.m_oListener=A;this.storedRequests=[];this.iFrameLoaded=false;var oThis=this;
this._iframeLoadedFunction = function(){oThis.xhrIframeLoaded();};
var keymasterIframe=document.createElement("div");
keymasterIframe.id='keymaster';document.body.appendChild(keymasterIframe);};
SL4B_KeymasterXHRManager.prototype.getCredentials = function(A){var oListener=this.m_oListener;
var oThis=this;
var loadCredentials=function(){var nConnectionTimeout=SL4B_Accessor.getConfiguration().getKeyMasterConnectionTimeout();
SL4B_Logger.logConnectionMessage(true,"KeyMasterCredentialsProvider.getCredentials: Requesting credentials");var requestTokenFunction=oThis.m_oEmbeddedKeyMaster.requestToken;
if(requestTokenFunction.length==3){requestTokenFunction(oListener,A,nConnectionTimeout);}else 
{requestTokenFunction(oListener,A,null,nConnectionTimeout);}};
this.removeFunctionFromQueue("load credentials");this.withIFrameLoaded(loadCredentials,"load credentials");};
SL4B_KeymasterXHRManager.prototype.startKeepAlive = function(C,A,B){var oThis=this;
this.withIFrameLoaded(function(){oThis.m_oEmbeddedKeyMaster.startKeepAlive(C,A,B);},"start keep alive");};
SL4B_KeymasterXHRManager.prototype.stopKeepAlive = function(){var oThis=this;
this.withIFrameLoaded(function(){oThis.m_oEmbeddedKeyMaster.stopKeepAlive();},"stop keep alive");};
SL4B_KeymasterXHRManager.prototype.withIFrameLoaded = function(B,A){if(this.m_oEmbeddedKeyMaster!=null){B();}else 
{SL4B_Logger.logConnectionMessage(true,"KeyMasterCredentialsProvider.getCredentials: {0} waiting for iframe to load.",A);B.tag=A;this.storedRequests.push(B);}if(this.iFrameLoaded==false){this.iFrameLoaded=true;this.loadXhrIframe();}};
SL4B_KeymasterXHRManager.prototype.loadXhrIframe = function(){var oThis=this;
this.m_nIFrameLoadingTimeout=null;this.m_oIFrameContainer=document.getElementById('keymaster');this.m_oIFrame=document.createElement('iframe');this.m_oIFrame.src=this.m_sKeyMasterUrl;this.m_oIFrame.style.display="none";if(this.m_oIFrame.addEventListener!=null){this.m_oIFrame.addEventListener("load",this._iframeLoadedFunction,false);}else 
{this.m_oIFrame.attachEvent("onload",this._iframeLoadedFunction);}this.m_nIFrameLoadingTimeout=setTimeout(function(){oThis.xhrIframeLoadingTimedOut();},SL4B_Accessor.getConfiguration().getKeyMasterConnectionTimeout());SL4B_Logger.log(SL4B_DebugLevel.const_DEBUG_INT,"SL4B_KeyMasterCredentialsProvider: Loading initial KeyMaster page");this.m_oIFrameContainer.appendChild(this.m_oIFrame);};
SL4B_KeymasterXHRManager.prototype.processStoredRequests = function(){for(var i=0;i<this.storedRequests.length;++i){this.storedRequests[i]();}};
SL4B_KeymasterXHRManager.prototype.removeFunctionFromQueue = function(A){for(var i=this.storedRequests.length-1;i>=0;--i){if(this.storedRequests[i].tag==A){this.storedRequests.splice(i,1);}}};
SL4B_KeymasterXHRManager.prototype.xhrIframeLoaded = function(){if(this.m_nIFrameLoadingTimeout!=null){clearTimeout(this.m_nIFrameLoadingTimeout);}
try {this.m_oEmbeddedKeyMaster=this.m_oIFrame.contentWindow.KeyMaster;if(this.m_oEmbeddedKeyMaster==null||this.m_oIFrame.contentWindow.KeyMasterListener==null){var iframeDoc=this.m_oIFrame.contentWindow.document;
var contentType=null;
if(iframeDoc.contentType==null){contentType=iframeDoc.mimeType;}else 
{contentType=iframeDoc.contentType;}var sContents=iframeDoc.documentElement.innerHTML;
SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"SL4B_KeyMasterCredentialsProvider: *** Bad Keymaster page");this._reset();this.m_oListener.failed("Unexpected initial KeyMaster page.  Type='"+contentType+"'",this.m_sKeyMasterUrl,sContents,200);return;
}}catch(e){this._reset();var errMsg="Error accessing initial KeyMaster page: "+e.message+" The page probably did not set document.domain";
this.m_oListener.failed(errMsg,this.m_sKeyMasterUrl,null,200);return;
}
SL4B_Logger.log(SL4B_DebugLevel.const_DEBUG_INT,"SL4B_KeyMasterCredentialsProvider: Initial KeyMaster page loaded");this.processStoredRequests();};
SL4B_KeymasterXHRManager.prototype._reset = function(){if(this.m_oIFrame){this._deleteIframe();}this.m_oEmbeddedKeyMaster=undefined;this.iFrameLoaded=false;this.storedRequests=[];};
SL4B_KeymasterXHRManager.prototype._deleteIframe = function(){if(this.m_oIFrame.removeEventListener!=null){this.m_oIFrame.removeEventListener("load",this._iframeLoadedFunction,false);}else 
if(this.m_oIFrame.detachEvent!=null){this.m_oIFrame.detachEvent("onload",this._iframeLoadedFunction);}this.m_oIFrameContainer.removeChild(this.m_oIFrame);};
SL4B_KeymasterXHRManager.prototype.xhrIframeLoadingTimedOut = function(){this._reset();SL4B_Logger.logConnectionMessage(true,"SL4B_KeyMasterCredentialsProvider: Could not load initial KeyMaster page: Timeout");this.m_oListener.failed("Could not load initial KeyMaster page: Timeout after "+SL4B_Accessor.getConfiguration().getKeyMasterConnectionTimeout()+"ms",this.m_sKeyMasterUrl,null,0);};
SL4B_KeymasterXHRManager.appendParameter = function(A,C,B){if(A==null||C==null){return A;
}if(B==null){B="";}var sAppendChar="?";
if(A.indexOf("?")==A.length-1){sAppendChar="";}else 
if(A.indexOf("?")>=0){sAppendChar="&";}A+=sAppendChar+C+"="+B;return A;
};
var SL4B_KeyMasterCredentialsProvider=function(){};
if(false){function SL4B_KeyMasterCredentialsProvider(){}
}SL4B_KeyMasterCredentialsProvider = function(){this.m_sSourceId="default";this.m_oRequestManager=null;this.m_sUserName="";this.m_oRttpProvider=null;this.reconnectAttempt=0;this.keepAliveAttempt=1;this.doneBackwardsCompatibleCheck=false;this.storedRequests=[];this.m_sKeyMasterToken=null;this.iFrameLoaded=false;this.m_bPreFetching=false;this.m_bPreFetchAvailable=false;this.m_sPreFetchUsername=null;this.m_sPreFetchPassword=null;this.m_oPreFetchTimestamp=null;this.m_oPreFetchCallback=null;if(SL4B_Accessor.getConfiguration().getKeyMasterUrl()==null){SL4B_Logger.log(SL4B_DebugLevel.const_ERROR_INT,"SL4B_KeyMasterCredentialsProvider: KeyMaster URL has not been defined");}};
SL4B_KeyMasterCredentialsProvider.prototype = new SL4B_AbstractCredentialsProvider;SL4B_KeyMasterCredentialsProvider.prototype.getCredentials = SL_SC;SL4B_KeyMasterCredentialsProvider.prototype.getKeyMasterToken = SL_IN;SL4B_KeyMasterCredentialsProvider.prototype.getUsername = SL_GW;SL4B_KeyMasterCredentialsProvider.prototype.setPreFetching = function(){this.m_bPreFetching=true;};
SL4B_KeyMasterCredentialsProvider.prototype.isPreFetching = function(){return this.m_bPreFetching;
};
SL4B_KeyMasterCredentialsProvider.prototype.stopKeepAlive = function(){if(this.m_oRequestManager==null){SL4B_Logger.log(SL4B_DebugLevel.const_ERROR_INT,"SL4B_KeyMasterCredentialsProvider.stopKeepAlive: KeyMaster URL was not been defined.  Unable to stop keepalive.");return;
}this.m_oRequestManager.stopKeepAlive();};
SL4B_KeyMasterCredentialsProvider.prototype.startKeepAlive = function(){if(this.m_oRequestManager==null){SL4B_Logger.log(SL4B_DebugLevel.const_ERROR_INT,"SL4B_KeyMasterCredentialsProvider.startKeepAlive: KeyMaster URL was not been defined.  Unable to start keepalive.");return;
}this.keepAliveAttempt=1;var l_nKeepAliveInterval=SL4B_Accessor.getConfiguration().getKeyMasterKeepAliveInterval();
if(l_nKeepAliveInterval>0){var oThis=this;
this.m_oRequestManager.startKeepAlive({success:function(){oThis.keepAliveSuccess();}, failed:function(B,A,C,D){oThis.keepAliveFailure(B,A,C,D);}, timedOut:function(A,B){oThis.keepAliveTimedOut(A,B);}},l_nKeepAliveInterval,SL4B_Accessor.getConfiguration().getKeyMasterConnectionTimeout());}};
SL4B_KeyMasterCredentialsProvider.prototype._internalGetCredentials = function(){var sKeyMasterUrl=SL4B_Accessor.getConfiguration().getKeyMasterUrl();
var sXhrKeymaster=SL4B_Accessor.getConfiguration().getKeyMasterXHRUrl();
if(this.doneBackwardsCompatibleCheck==false){this.doneBackwardsCompatibleCheck=true;if(sXhrKeymaster==null){if(sKeyMasterUrl!=null){this.m_oRequestManager=new SL4B_KeymasterIFrameManager(this,sKeyMasterUrl);}else 
{}}else 
if(this.usingFirefoxAndForeverFrame()==true){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"SL4B_KeyMasterCredentialsProvider: Firefox and Forever Frame detected");if(sKeyMasterUrl!=null){this.m_oRequestManager=new SL4B_KeymasterIFrameManager(this,sKeyMasterUrl);}else 
{}}else 
{this.m_oRequestManager=new SL4B_KeymasterXHRManager(this,sXhrKeymaster);}}if(this.m_oRequestManager==null){this.logConnectionError("KeyMaster error - KeyMaster URL has not been defined.");return;
}this.m_sUserName="";this.stopKeepAlive();this.m_oRequestManager.getCredentials(this.m_sSourceId);};
function SL_SC(A){SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"SL4B_KeyMasterCredentialsProvider: Getting credentials.");if(this.m_bPreFetching){this.m_oPreFetchCallback=A;}this.m_oRttpProvider=A;this.reconnectAttempt=0;if(this.m_bPreFetchAvailable){this.success(this.m_sPreFetchUsername,this.m_sPreFetchPassword);this.m_bPreFetchAvailable=false;}else 
{this._internalGetCredentials();}}
function SL_IN(){return this.m_sKeyMasterToken;
}
function SL_GW(){return this.m_sUserName;
}
SL4B_KeyMasterCredentialsProvider.prototype.logConnectionError = function(A){C_CallbackQueue.addCallback(new Array(this.m_oRttpProvider,"notifyConnectionListeners",this.m_oRttpProvider.const_LOGIN_ERROR_CONNECTION_EVENT,A));};
SL4B_KeyMasterCredentialsProvider.prototype.usingFirefoxAndForeverFrame = function(){var result=false;
if(SL4B_Accessor.getBrowserAdapter().isFirefox()){var connections=this.m_oRttpProvider.getLiberatorConfiguration().getAllConnectionData();
for(var i=0;i<connections.length;++i){if(connections[i].getMethod()==SL4B_ConnectionMethod.FOREVER_FRAME){result=true;break;
}}}return result;
};
SL4B_KeyMasterCredentialsProvider.prototype.success = function(B,A){if(this.m_bPreFetching){this.m_sPreFetchUsername=B;this.m_sPreFetchPassword=A;this.m_bPreFetchAvailable=true;this.m_bPreFetching=false;this.m_oPreFetchTimestamp=new Date();this.m_oPreFetchCallback.credentialsPreFetched();}else 
{this.m_sUserName=B;this.m_sKeyMasterToken=A;C_CallbackQueue.addCallback(new Array(this.m_oRttpProvider,"notifyConnectionListeners",this.m_oRttpProvider.const_INFO_CONNECTION_EVENT,"KeyMaster authentication success: Attempting Liberator login... "));window.setTimeout("SL4B_Accessor.getRttpProvider().login('"+this.m_sUserName+"','"+A+"')",0);this.startKeepAlive();}};
SL4B_KeyMasterCredentialsProvider.prototype.failed = function(D,A,B,C){this.m_sKeyMasterToken=null;var message="KeyMaster error - "+D;
if(A!=null){message+=" when loading "+A;}if(C!=200&&C!=null&&C!=0){message+=" (code:"+C+")";}if(B!=null&&B.length>0){message+=" response="+B;}SL4B_Logger.logConnectionMessage(true,"KeyMasterCredentialsProvider.failed: {0}",message);var maximumRetries=SL4B_Accessor.getConfiguration().getKeyMasterAttempts();
var bRetry=++this.reconnectAttempt<maximumRetries;
var bRetriable=C==0||C==404||C>=500;
if(bRetry&&bRetriable){SL4B_Logger.logConnectionMessage(true,"KeyMasterCredentialsProvider: Retrying Keymaster");this._internalGetCredentials();}else 
{this.logConnectionError(message);}};
SL4B_KeyMasterCredentialsProvider.prototype.timedOut = function(A,B){this.failed("Timed out after "+B+"ms",A,"",0);};
SL4B_KeyMasterCredentialsProvider.prototype.keepAliveSuccess = function(){SL4B_Logger.log(SL4B_DebugLevel.const_DEBUG_INT,"Keymaster server session alive");this.keepAliveAttempt==1;};
SL4B_KeyMasterCredentialsProvider.prototype.keepAliveFailure = function(B,A,C,D){SL4B_Logger.log(SL4B_DebugLevel.const_DEBUG_INT,"Keymaster polling failed to access {0}: {1} code={2} {3}",A,B,D,C);var keepAliveAttempts=SL4B_Accessor.getConfiguration().getKeyMasterKeepAliveAttempts();
if(this.keepAliveAttempt>=keepAliveAttempts){C_CallbackQueue.addCallback(new Array(this.m_oRttpProvider,"notifyConnectionListeners",this.m_oRttpProvider.const_CREDENTIALS_PROVIDER_SESSION_ERROR_CONNECTION_EVENT,"Keymaster polling failed: "+B,A,C,D));}this.keepAliveAttempt++;};
SL4B_KeyMasterCredentialsProvider.prototype.keepAliveTimedOut = function(A,B){SL4B_Logger.log(SL4B_DebugLevel.const_DEBUG_INT,"Keymaster polling timed out after {0} contacting {1}",B,A);C_CallbackQueue.addCallback(new Array(this.m_oRttpProvider,"notifyConnectionListeners",this.m_oRttpProvider.const_CREDENTIALS_PROVIDER_SESSION_ERROR_CONNECTION_EVENT,"Keymaster polling timed out after "+B+"ms",A,null,null));};
function SL_CX(){SL4B_Accessor.setCredentialsProvider(new SL4B_KeyMasterCredentialsProvider());}
var SL4B_LiberatorConfiguration=function(){};
if(false){function SL4B_LiberatorConfiguration(){}
}SL4B_LiberatorConfiguration = function(){this.m_nCurrentConnection=-1;this.m_nSuccessfulConnection=-1;this.m_nIgnoreConnection=-1;this.m_pConnectionDatas=new Array();};
SL4B_LiberatorConfiguration.prototype.initialise = function(){this.m_nCurrentConnection=-1;this.m_nSuccessfulConnection=-1;this.m_nIgnoreConnection=-1;this.m_pConnectionDatas=new Array();};
SL4B_LiberatorConfiguration.prototype.addConnection = function(A){this.m_pConnectionDatas.push(A);};
SL4B_LiberatorConfiguration.prototype.getNextConnection = function(){var l_oConfigurationData=null;
if(this.m_nSuccessfulConnection!=-1){l_oConfigurationData=this.m_pConnectionDatas[this.m_nSuccessfulConnection];this.m_nIgnoreConnection=this.m_nSuccessfulConnection;this.m_nSuccessfulConnection=-1;this.m_nCurrentConnection=-1;}else 
{++this.m_nCurrentConnection;if(this.m_nCurrentConnection==this.m_nIgnoreConnection){++this.m_nCurrentConnection;}if(this.m_nCurrentConnection<this.m_pConnectionDatas.length){l_oConfigurationData=this.m_pConnectionDatas[this.m_nCurrentConnection];}}return l_oConfigurationData;
};
SL4B_LiberatorConfiguration.prototype.peekAtNextConnection = function(){var l_oConfigurationData=null;
if(this.m_nSuccessfulConnection!=-1){l_oConfigurationData=this.m_pConnectionDatas[this.m_nSuccessfulConnection];}else 
{var l_nCurrentConnection=this.m_nCurrentConnection+1;
if(l_nCurrentConnection==this.m_nIgnoreConnection){++l_nCurrentConnection;}if(l_nCurrentConnection<this.m_pConnectionDatas.length){l_oConfigurationData=this.m_pConnectionDatas[l_nCurrentConnection];}}return l_oConfigurationData;
};
SL4B_LiberatorConfiguration.prototype.getAllConnectionData = function(){return this.m_pConnectionDatas;
};
SL4B_LiberatorConfiguration.prototype.resetConnections = function(){this.m_nCurrentConnection=-1;this.m_nSuccessfulConnection=-1;this.m_nIgnoreConnection=-1;};
SL4B_LiberatorConfiguration.prototype.connectionOk = function(){this.m_nSuccessfulConnection=this.m_nCurrentConnection;};
SL4B_LiberatorConfiguration.prototype.toString = function(){var l_sValue="LiberatorConfiguration:\n";
for(var l_nCount=0;l_nCount<this.m_pConnectionDatas.length;l_nCount++){var l_oConnectionData=this.m_pConnectionDatas[l_nCount];
l_sValue+=l_oConnectionData.toString()+"\n";}return l_sValue;
};
SL4B_LiberatorConfiguration.prototype.toSimpleString = function(){var l_sValue="";
for(var l_nCount=0;l_nCount<this.m_pConnectionDatas.length;l_nCount++){var l_oConnectionData=this.m_pConnectionDatas[l_nCount];
l_sValue+=l_oConnectionData.m_sProtocol+" ";l_sValue+=l_oConnectionData.m_sAddress+" ";l_sValue+=l_oConnectionData.m_sPort+" ";l_sValue+=l_oConnectionData.m_oMethod+", ";}return l_sValue;
};
var SL4B_SimpleLiberatorConfiguration=function(){};
if(false){function SL4B_SimpleLiberatorConfiguration(){}
}SL4B_SimpleLiberatorConfiguration = function(){this.initialise();var l_oConfiguration=SL4B_Accessor.getConfiguration();
var l_sServerUrl=l_oConfiguration.getServerUrl();
var l_sProtocol=null;
var l_sAddress=null;
var l_sPort=null;
if(l_sServerUrl.match(/^http/)){l_sProtocol=l_sServerUrl.match(/^(https?)/)[1];l_sAddress=l_sServerUrl.match(/^https?:\/\/([^:\/]+)/)[1];var l_oMatch=l_sServerUrl.match(/:(\d+)/);
l_sPort=((l_oMatch==null) ? null : l_oMatch[1]);}var l_pMethods=l_oConfiguration.getConnectionMethods();
if(l_pMethods.length==0){var l_pStreamingConnectionMethods=l_oConfiguration.getStreamingConnectionMethods();
if(l_pStreamingConnectionMethods.length!=0){l_pMethods.push(l_pStreamingConnectionMethods[0]);}var l_pPollingConnectionMethods=l_oConfiguration.getPollingConnectionMethods();
if(l_pPollingConnectionMethods.length!=0){l_pMethods.push(l_pPollingConnectionMethods[0]);}}for(var l_oMethod=0,l_nLength=l_pMethods.length;l_oMethod<l_nLength;++l_oMethod){var l_oConnectionMethod=l_pMethods[l_oMethod];
this.addConnection(new SL4B_ConnectionData(l_sProtocol,l_sAddress,l_sPort,l_oConnectionMethod));}};
SL4B_SimpleLiberatorConfiguration.prototype = new SL4B_LiberatorConfiguration;var SL4B_ConnectionData=function(){};
if(false){function SL4B_ConnectionData(){}
}SL4B_ConnectionData = function(D,A,B,C){this.m_sProtocol=D;this.m_sAddress=A;this.m_sPort=B;this.m_oMethod=C;this.getProtocol = function(){return this.m_sProtocol;
};
this.getAddress = function(){return this.m_sAddress;
};
this.getPort = function(){return this.m_sPort;
};
this.getMethod = function(){return this.m_oMethod;
};
this.toString = function(){var l_sValue="ConnectionData(";
l_sValue+="Protocol = "+this.m_sProtocol+", ";l_sValue+="Address = "+this.m_sAddress+", ";l_sValue+="Port = "+this.m_sPort+", ";l_sValue+="Method = "+this.m_oMethod+")";return l_sValue;
};
};
SL4B_ConnectionData.prototype.getServerUrl = function(){var l_sServerUrl="";
if(this.m_sProtocol!=null){l_sServerUrl=this.m_sProtocol+"://"+this.m_sAddress+((this.m_sPort!=null) ? ":"+this.m_sPort : "");}else 
{l_sServerUrl=SL4B_Accessor.getConfiguration().getServerUrl().replace(/\/$/,"");}return l_sServerUrl;
};
function GF_RttpCommandQueue(){this.m_pQueuedCommands=new Array();}
GF_RttpCommandQueue.prototype.sendNext = GF_RttpCommandQueue_SendNext;GF_RttpCommandQueue.prototype.queueCommand = GF_RttpCommandQueue_QueueCommand;GF_RttpCommandQueue.prototype.checkArguments = GF_RttpCommandQueue_CheckArguments;GF_RttpCommandQueue.prototype.isEmpty = function(){return (this.m_pQueuedCommands.length==0);
};
GF_RttpCommandQueue.prototype.getObject = function(C,B,A){this.queueCommand(SL_FD.const_GET_OBJECT,C,B,A);};
GF_RttpCommandQueue.prototype.getObjects = function(C,A,B){this.queueCommand(SL_FD.const_GET_OBJECTS,C,A,B);};
GF_RttpCommandQueue.prototype.removeObject = function(C,B,A){this.queueCommand(SL_FD.const_REMOVE_OBJECT,C,B,A);};
GF_RttpCommandQueue.prototype.removeObjects = function(C,A,B){this.queueCommand(SL_FD.const_REMOVE_OBJECTS,C,A,B);};
GF_RttpCommandQueue.prototype.getObjectType = function(B,A){this.queueCommand(SL_FD.const_GET_OBJECT_TYPE,B,A);};
GF_RttpCommandQueue.prototype.setThrottleObject = function(A,B){this.queueCommand(SL_FD.const_SET_THROTTLE_OBJECT,A,B);};
GF_RttpCommandQueue.prototype.setThrottleObjects = function(A,B){this.queueCommand(SL_FD.const_SET_THROTTLE_OBJECTS,A,B);};
GF_RttpCommandQueue.prototype.setGlobalThrottle = function(A){this.queueCommand(SL_FD.const_SET_GLOBAL_THROTTLE,A);};
GF_RttpCommandQueue.prototype.disableWTStatsTimeout = function(A){this.queueCommand(SL_FD.const_DISABLE_WTSTATS_TIMEOUT,A);};
GF_RttpCommandQueue.prototype.clearObjectListeners = function(B,A){this.queueCommand(SL_FD.const_CLEAR_OBJECT_LISTENERS,B,A);};
GF_RttpCommandQueue.prototype.blockObjectListeners = function(B,A){this.queueCommand(SL_FD.const_BLOCK_OBJECT_LISTENERS,B,A);};
GF_RttpCommandQueue.prototype.unblockObjectListeners = function(B,A){this.queueCommand(SL_FD.const_UNBLOCK_OBJECT_LISTENERS,B,A);};
GF_RttpCommandQueue.prototype.getFieldNames = function(){this.queueCommand(SL_FD.const_GET_FIELD_NAMES);};
GF_RttpCommandQueue.prototype.logout = function(){this.queueCommand(SL_FD.const_LOGOUT);};
GF_RttpCommandQueue.prototype.debug = function(B,A){this.queueCommand(SL_FD.const_DEBUG,B,A);};
GF_RttpCommandQueue.prototype.setDebugLevel = function(A){this.queueCommand(SL_FD.const_SET_DEBUG_LEVEL,A);};
GF_RttpCommandQueue.prototype.getVersion = function(){this.queueCommand(SL_FD.const_GET_VERSION);};
GF_RttpCommandQueue.prototype.getVersionInfo = function(){this.queueCommand(SL_FD.const_GET_VERSION_INFO);};
GF_RttpCommandQueue.prototype.addConnectionListener = function(A){this.queueCommand(SL_FD.const_ADD_CONNECTION_LISTENER,A);};
GF_RttpCommandQueue.prototype.removeConnectionListener = function(A){this.queueCommand(SL_FD.const_REMOVE_CONNECTION_LISTENER,A);};
GF_RttpCommandQueue.prototype.removeSubscriber = function(A){this.queueCommand(SL_FD.const_REMOVE_SUBSCRIBER,A);};
GF_RttpCommandQueue.prototype.createObject = function(E,C,D,B,A){this.queueCommand(SL_FD.const_CREATE_OBJECT,E,C,D,B,A);};
GF_RttpCommandQueue.prototype.contribObject = function(E,C,D,B,A){this.queueCommand(SL_FD.const_CONTRIB_OBJECT,E,C,D,B,A);};
GF_RttpCommandQueue.prototype.deleteObject = function(D,C,B,A){this.queueCommand(SL_FD.const_DELETE_OBJECT,D,C,B,A);};
GF_RttpCommandQueue.prototype.cancelPersistedAction = function(A){this.queueCommand(SL_FD.const_CANCEL_PERSISTED_ACTION,A);};
GF_RttpCommandQueue.prototype.resendPersistedAction = function(A){this.queueCommand(SL_FD.const_RESEND_PERSISTED_ACTION,A);};
GF_RttpCommandQueue.prototype.getContainer = function(E,A,C,D,B){this.queueCommand(SL_FD.const_GET_CONTAINER,E,A,C,D,B);};
GF_RttpCommandQueue.prototype.getContainerSnapshot = function(D,A,C,B){this.queueCommand(SL_FD.const_GET_CONTAINER_SNAPSHOT,D,A,C,B);};
GF_RttpCommandQueue.prototype.setContainerWindow = function(B,C,A){this.queueCommand(SL_FD.const_SET_CONTAINER_WINDOW,B,C,A);};
GF_RttpCommandQueue.prototype.clearContainerWindow = function(A){this.queueCommand(SL_FD.const_CLEAR_CONTAINER,A);};
GF_RttpCommandQueue.prototype.removeContainer = function(B,A){this.queueCommand(SL_FD.const_REMOVE_CONTAINER,B,A);};
GF_RttpCommandQueue.prototype.getAutoDirectory = function(E,D,A,B,C){this.queueCommand(SL_FD.const_GET_AUTO_DIRECTORY,E,D,A,B,C);};
GF_RttpCommandQueue.prototype.removeAutoDirectory = function(E,D,A,B,C){this.queueCommand(SL_FD.const_REMOVE_AUTO_DIRECTORY,E,D,A,B,C);};
function GF_RttpCommandQueue_SendNext(C,A,B){var l_oNextCommand=this.m_pQueuedCommands.shift();
var l_nCommandType=l_oNextCommand.m_nCommandType;
var l_pArguments=l_oNextCommand.m_pArguments;
switch(l_nCommandType){
case SL_FD.const_GET_OBJECT:if(this.checkArguments(l_pArguments,3)){C.getObject(l_pArguments[1],l_pArguments[2],l_pArguments[3]);}break;
case SL_FD.const_GET_OBJECTS:if(this.checkArguments(l_pArguments,3)){C.getObjects(l_pArguments[1],l_pArguments[2],l_pArguments[3]);}break;
case SL_FD.const_REMOVE_OBJECT:if(this.checkArguments(l_pArguments,3)){C.removeObject(l_pArguments[1],l_pArguments[2],l_pArguments[3]);}break;
case SL_FD.const_GET_OBJECT_TYPE:if(this.checkArguments(l_pArguments,2)){C.getObjectType(l_pArguments[1],l_pArguments[2]);}break;
case SL_FD.const_SET_THROTTLE_OBJECT:if(this.checkArguments(l_pArguments,2)){C.setThrottleObject(l_pArguments[1],l_pArguments[2]);}break;
case SL_FD.const_SET_THROTTLE_OBJECTS:if(this.checkArguments(l_pArguments,2)){C.setThrottleObjects(l_pArguments[1],l_pArguments[2]);}break;
case SL_FD.const_SET_GLOBAL_THROTTLE:if(this.checkArguments(l_pArguments,1)){C.setGlobalThrottle(l_pArguments[1]);}break;
case SL_FD.const_DISABLE_WTSTATS_TIMEOUT:if(this.checkArguments(l_pArguments,1)){C.disableWTStatsTimeout(l_pArguments[1]);}break;
case SL_FD.const_CLEAR_OBJECT_LISTENERS:if(this.checkArguments(l_pArguments,2)){C.clearObjectListeners(l_pArguments[1],l_pArguments[2]);}break;
case SL_FD.const_BLOCK_OBJECT_LISTENERS:if(this.checkArguments(l_pArguments,2)){C.blockObjectListeners(l_pArguments[1],l_pArguments[2]);}break;
case SL_FD.const_UNBLOCK_OBJECT_LISTENERS:if(this.checkArguments(l_pArguments,2)){C.unblockObjectListeners(l_pArguments[1],l_pArguments[2]);}break;
case SL_FD.const_CREATE_OBJECT:if(this.checkArguments(l_pArguments,5)){var oPersistentActionKey=C.createObject(l_pArguments[1],l_pArguments[2],l_pArguments[3],l_pArguments[4]);
var oPersistentActionKeyWrapper=l_pArguments[5];
this._addPersistentActionKeyToWrapper(oPersistentActionKeyWrapper,oPersistentActionKey);}break;
case SL_FD.const_CONTRIB_OBJECT:if(this.checkArguments(l_pArguments,5)){var oPersistentActionKey=C.contribObject(l_pArguments[1],l_pArguments[2],l_pArguments[3],l_pArguments[4]);
var oPersistentActionKeyWrapper=l_pArguments[5];
this._addPersistentActionKeyToWrapper(oPersistentActionKeyWrapper,oPersistentActionKey);}break;
case SL_FD.const_DELETE_OBJECT:if(this.checkArguments(l_pArguments,4)){var oPersistentActionKey=C.deleteObject(l_pArguments[1],l_pArguments[2],l_pArguments[3]);
var oPersistentActionKeyWrapper=l_pArguments[4];
this._addPersistentActionKeyToWrapper(oPersistentActionKeyWrapper,oPersistentActionKey);}break;
case SL_FD.const_CANCEL_PERSISTED_ACTION:if(this.checkArguments(l_pArguments,1)){var oPersistentActionKey=l_pArguments[1]._$getPersistentActionKey();
C.cancelPersistedAction(oPersistentActionKey);}break;
case SL_FD.const_RESEND_PERSISTED_ACTION:if(this.checkArguments(l_pArguments,1)){var oPersistentActionKey=l_pArguments[1]._$getPersistentActionKey();
C.resendPersistedAction(oPersistentActionKey);}break;
case SL_FD.const_GET_FIELD_NAMES:if(this.checkArguments(l_pArguments,0)){C.getFieldNames();}break;
case SL_FD.const_LOGOUT:if(this.checkArguments(l_pArguments,0)){C.logout();}break;
case SL_FD.const_DEBUG:if(this.checkArguments(l_pArguments,0)){C.debug();}break;
case SL_FD.const_SET_DEBUG_LEVEL:if(this.checkArguments(l_pArguments,1)){C.setDebugLevel(l_pArguments[1]);}break;
case SL_FD.const_GET_VERSION:if(this.checkArguments(l_pArguments,0)){C.getVersion();}break;
case SL_FD.const_GET_VERSION_INFO:if(this.checkArguments(l_pArguments,0)){C.getVersionInfo();}break;
case SL_FD.const_ADD_CONNECTION_LISTENER:if(this.checkArguments(l_pArguments,1)){C.addConnectionListener(l_pArguments[1]);}break;
case SL_FD.const_REMOVE_CONNECTION_LISTENER:if(this.checkArguments(l_pArguments,1)){C.removeConnectionListener(l_pArguments[1]);}break;
case SL_FD.const_GET_CONTAINER:var l_oContainerKey=null;
if(this.checkArguments(l_pArguments,5)){l_oContainerKey=C.getContainer(l_pArguments[1],l_pArguments[2],l_pArguments[3],l_pArguments[4],l_pArguments[5]);}else 
if(this.checkArguments(l_pArguments,3)){l_oContainerKey=C.getContainer(l_pArguments[1],l_pArguments[2],l_pArguments[3]);}var oProxyKey=A.shift();
B[oProxyKey]=l_oContainerKey;break;
case SL_FD.const_GET_CONTAINER_SNAPSHOT:if(this.checkArguments(l_pArguments,4)){C._$getContainerSnapshot(l_pArguments[1],l_pArguments[2],l_pArguments[3],l_pArguments[4]);}else 
if(this.checkArguments(l_pArguments,2)){C._$getContainerSnapshot(l_pArguments[1],l_pArguments[2]);}break;
case SL_FD.const_REMOVE_CONTAINER:if(this.checkArguments(l_pArguments,2)){var l_oContainerKey=this.getContainerKeyFromContainerKeyMap(l_pArguments[2],B);
C.removeContainer(l_pArguments[1],l_oContainerKey);}break;
case SL_FD.const_CLEAR_CONTAINER:if(this.checkArguments(l_pArguments,1)){var l_oContainerKey=this.getContainerKeyFromContainerKeyMap(l_pArguments[1],B);
C.clearContainerWindow(l_oContainerKey);}break;
case SL_FD.const_SET_CONTAINER_WINDOW:if(this.checkArguments(l_pArguments,3)){var l_oContainerKey=this.getContainerKeyFromContainerKeyMap(l_pArguments[1],B);
C.setContainerWindow(l_oContainerKey,l_pArguments[2],l_pArguments[3]);}break;
case SL_FD.const_REMOVE_SUBSCRIBER:if(this.checkArguments(l_pArguments,1)){C.removeSubscriber(l_pArguments[1]);}break;
case SL_FD.const_GET_AUTO_DIRECTORY:if(this.checkArguments(l_pArguments,5)){C.getAutoDirectory(l_pArguments[1],l_pArguments[2],l_pArguments[3],l_pArguments[4],l_pArguments[5]);}break;
case SL_FD.const_REMOVE_AUTO_DIRECTORY:if(this.checkArguments(l_pArguments,5)){C.removeAutoDirectory(l_pArguments[1],l_pArguments[2],l_pArguments[3],l_pArguments[4],l_pArguments[5]);}break;
default :SL4B_Logger.log(SL4B_DebugLevel.const_ERROR_INT,"RttpCommandQueue.sendNext: unknown command ({0}) - {1}",l_nCommandType,l_oNextCommand);break;
}}
GF_RttpCommandQueue.prototype._addPersistentActionKeyToWrapper = function(B,A){if(B){B._$setPersistentActionKey(A);}};
GF_RttpCommandQueue.prototype.getContainerKeyFromContainerKeyMap = function(A,B){if(B[A]!=null){A=B[A];}return A;
};
function GF_RttpCommandQueue_QueueCommand(A){this.m_pQueuedCommands.push(new SL_HE(A,GF_RttpCommandQueue_QueueCommand.arguments));}
function GF_RttpCommandQueue_CheckArguments(B,A){return B.length>A;
}
function SL_HE(A,B){this.m_nCommandType=A;this.m_pArguments=B;}
SL_FD = new function(){this.const_GET_OBJECT=1;this.const_GET_OBJECTS=2;this.const_REMOVE_OBJECT=3;this.const_REMOVE_OBJECTS=4;this.const_GET_OBJECT_TYPE=5;this.const_SET_THROTTLE_OBJECT=6;this.const_SET_THROTTLE_OBJECTS=7;this.const_SET_GLOBAL_THROTTLE=8;this.const_DISABLE_WTSTATS_TIMEOUT=9;this.const_CLEAR_OBJECT_LISTENERS=10;this.const_BLOCK_OBJECT_LISTENERS=11;this.const_UNBLOCK_OBJECT_LISTENERS=12;this.const_CREATE_OBJECT=13;this.const_CONTRIB_OBJECT=14;this.const_DELETE_OBJECT=15;this.const_GET_FIELD_NAMES=16;this.const_LOGOUT=17;this.const_DEBUG=18;this.const_SET_DEBUG_LEVEL=19;this.const_GET_VERSION=20;this.const_GET_VERSION_INFO=21;this.const_ADD_CONNECTION_LISTENER=22;this.const_REMOVE_CONNECTION_LISTENER=23;this.const_GET_CONTAINER=24;this.const_REMOVE_CONTAINER=25;this.const_REMOVE_SUBSCRIBER=26;this.const_SET_CONTAINER_WINDOW=27;this.const_CLEAR_CONTAINER_WINDOW=28;this.const_SET_CONTAINER_WINDOW=29;this.const_CLEAR_CONTAINER=30;this.const_GET_AUTO_DIRECTORY=31;this.const_REMOVE_AUTO_DIRECTORY=32;this.const_CANCEL_PERSISTED_ACTION=33;this.const_RESEND_PERSISTED_ACTION=34;this.const_GET_CONTAINER_SNAPSHOT=35;};
var SL4B_SlaveFrameRttpProvider=function(){};
if(false){function SL4B_SlaveFrameRttpProvider(){}
}SL4B_SlaveFrameRttpProvider = function(){SL4B_AbstractRttpProvider.apply(this);this.CLASS_NAME="SL4B_SlaveFrameRttpProvider";this.m_oMasterFrameRttpProvider=null;this.m_oRttpCommandQueue=new GF_RttpCommandQueue();this.m_bMasterRefresh=false;this.m_pConnectionListeners=[];this.m_sSlaveFrameId=SL4B_Accessor.getConfiguration().getFrameId();};
SL4B_SlaveFrameRttpProvider.prototype = new SL4B_AbstractRttpProvider;SL4B_SlaveFrameRttpProvider.prototype.setMasterWindow = function(B,A){this.m_sSlaveFrameId=A;this.masterRegistered(B.SL4B_Accessor.getRttpProvider());};
SL4B_SlaveFrameRttpProvider.prototype.register = SL_PO;SL4B_SlaveFrameRttpProvider.prototype.masterRegistered = SL_KS;SL4B_SlaveFrameRttpProvider.prototype.masterClosing = SL_HM;SL4B_SlaveFrameRttpProvider.prototype.connect = function(){};
SL4B_SlaveFrameRttpProvider.prototype.login = function(A,B){};
SL4B_SlaveFrameRttpProvider.prototype.reconnect = function(){};
SL4B_SlaveFrameRttpProvider.prototype.getObject = SL_EG;SL4B_SlaveFrameRttpProvider.prototype.getObjects = SL_OW;SL4B_SlaveFrameRttpProvider.prototype.removeObject = SL_OE;SL4B_SlaveFrameRttpProvider.prototype.removeObjects = SL_EX;SL4B_SlaveFrameRttpProvider.prototype.removeSubscriber = SL_GK;SL4B_SlaveFrameRttpProvider.prototype.getObjectType = SL_QO;SL4B_SlaveFrameRttpProvider.prototype.setThrottleObject = SL_JI;SL4B_SlaveFrameRttpProvider.prototype.setThrottleObjects = SL_QM;SL4B_SlaveFrameRttpProvider.prototype.setGlobalThrottle = SL_RR;SL4B_SlaveFrameRttpProvider.prototype.createObject = SL_GS;SL4B_SlaveFrameRttpProvider.prototype.contribObject = SL_CM;SL4B_SlaveFrameRttpProvider.prototype.deleteObject = SL_DV;SL4B_SlaveFrameRttpProvider.prototype.cancelPersistedAction = SL_DO;SL4B_SlaveFrameRttpProvider.prototype.resendPersistedAction = SL_RX;SL4B_SlaveFrameRttpProvider.prototype.getFieldNames = SL_HR;SL4B_SlaveFrameRttpProvider.prototype.logout = function(){};
SL4B_SlaveFrameRttpProvider.prototype.initialise = function(){};
SL4B_SlaveFrameRttpProvider.prototype.stop = SL_DP;SL4B_SlaveFrameRttpProvider.prototype.disableWTStatsTimeout = function(A){this.m_oMasterFrameRttpProvider.disableWTStatsTimeout(A);};
SL4B_SlaveFrameRttpProvider.prototype.clearObjectListeners = function(B,A){this.m_oMasterFrameRttpProvider.clearObjectListeners(B,A);};
SL4B_SlaveFrameRttpProvider.prototype.blockObjectListeners = function(B,A){this.m_oMasterFrameRttpProvider.blockObjectListeners(B,A);};
SL4B_SlaveFrameRttpProvider.prototype.unblockObjectListeners = function(B,A){this.m_oMasterFrameRttpProvider.unblockObjectListeners(B,A);};
SL4B_SlaveFrameRttpProvider.prototype.debug = function(B,A){this.m_oMasterFrameRttpProvider.debug(B,A);};
SL4B_SlaveFrameRttpProvider.prototype.setDebugLevel = function(A){return this.m_oMasterFrameRttpProvider.setDebugLevel(A);
};
SL4B_SlaveFrameRttpProvider.prototype.getVersion = function(){return this.m_oMasterFrameRttpProvider.getVersion();
};
SL4B_SlaveFrameRttpProvider.prototype.getVersionInfo = function(){return this.m_oMasterFrameRttpProvider.getVersionInfo();
};
SL4B_SlaveFrameRttpProvider.prototype.getSessionId = function(){return this.m_oMasterFrameRttpProvider.getSessionId();
};
SL4B_SlaveFrameRttpProvider.prototype._$getContainerSnapshot = function(D,A,C,B){this.m_oMasterFrameRttpProvider._$getContainerSnapshot(D,A,C,B);};
SL4B_SlaveFrameRttpProvider.prototype.getAutoDirectory = function(E,D,A,B,C){
try {this.m_oMasterFrameRttpProvider.getAutoDirectory(E,D,A,B,C);}catch(e){}
};
SL4B_SlaveFrameRttpProvider.prototype.removeAutoDirectory = function(E,D,A,B,C){
try {this.m_oMasterFrameRttpProvider.removeAutoDirectory(E,D,A,B,C);}catch(e){}
};
SL4B_SlaveFrameRttpProvider.prototype.getContainer = function(E,A,C,D,B){
try {return this.m_oMasterFrameRttpProvider.getContainer(E,A,C,D,B);
}catch(e){}
};
SL4B_SlaveFrameRttpProvider.prototype.setContainerWindow = function(B,C,A){
try {this.m_oMasterFrameRttpProvider.setContainerWindow(B,C,A);}catch(e){}
};
SL4B_SlaveFrameRttpProvider.prototype.clearContainerWindow = function(A){
try {this.m_oMasterFrameRttpProvider.clearContainerWindow(A);}catch(e){}
};
SL4B_SlaveFrameRttpProvider.prototype.removeContainer = function(B,A){
try {this.m_oMasterFrameRttpProvider.removeContainer(B,A);}catch(e){}
};
SL4B_SlaveFrameRttpProvider.prototype.addConnectionListener = function(A){this.m_pConnectionListeners.push(A);if(this.m_oMasterFrameRttpProvider!=null){
try {this.m_oMasterFrameRttpProvider.addConnectionListener(A);}catch(e){}
}};
SL4B_SlaveFrameRttpProvider.prototype.removeConnectionListener = function(A){
try {this.m_oMasterFrameRttpProvider.removeConnectionListener(A);}catch(e){}
};
SL4B_SlaveFrameRttpProvider.prototype.onUnload = function(){this.stop();};
function SL_EG(C,B,A){if(this.m_oMasterFrameRttpProvider!=null){
try {this.m_oMasterFrameRttpProvider.getObject(C,B,A);}catch(e){}
}else 
{this.m_oRttpCommandQueue.getObject(C,B,A);}}
function SL_OW(C,A,B){
try {this.m_oMasterFrameRttpProvider.getObjects(C,A,B);}catch(e){}
}
function SL_OE(C,B,A){
try {this.m_oMasterFrameRttpProvider.removeObject(C,B,A);}catch(e){}
}
function SL_EX(C,A,B){
try {this.m_oMasterFrameRttpProvider.removeObjects(C,A,B);}catch(e){}
}
function SL_GK(A){if(this.m_oMasterFrameRttpProvider!=null){
try {this.m_oMasterFrameRttpProvider.removeSubscriber(A);}catch(e){}
}}
function SL_QO(B,A){
try {this.m_oMasterFrameRttpProvider.getObjectType(B,A);}catch(e){}
}
function SL_JI(A,B){
try {this.m_oMasterFrameRttpProvider.setThrottleObject(A,B);}catch(e){}
}
function SL_QM(A,B){
try {this.m_oMasterFrameRttpProvider.setThrottleObjects(A,B);}catch(e){}
}
function SL_RR(A){
try {this.m_oMasterFrameRttpProvider.setGlobalThrottle(A);}catch(e){}
}
function SL_GS(D,B,C,A){
try {return this.m_oMasterFrameRttpProvider.createObject(D,B,C,A);
}catch(e){return null;
}
}
function SL_CM(D,B,C,A){
try {return this.m_oMasterFrameRttpProvider.contribObject(D,B,C,A);
}catch(e){return null;
}
}
function SL_DV(C,B,A){
try {return this.m_oMasterFrameRttpProvider.deleteObject(C,B,A);
}catch(e){return null;
}
}
function SL_DO(A){
try {return this.m_oMasterFrameRttpProvider.cancelPersistedAction(A);
}catch(e){return false;
}
}
function SL_RX(A){
try {return this.m_oMasterFrameRttpProvider.resendPersistedAction(A);
}catch(e){}
}
function SL_HR(){
try {return this.m_oMasterFrameRttpProvider.getFieldNames();
}catch(e){return null;
}
}
function SL_PO(){if(!SL4B_Accessor.getConfiguration()._$isRegistrationWindowEnabled()){setTimeout(this._getRegisterSlaveFrameFunction(),0);}}
SL4B_SlaveFrameRttpProvider.prototype._getRegisterSlaveFrameFunction = function(){return function(){SL4B_FrameRegistrarAccessor.registerSlaveFrame(SL4B_Accessor.getConfiguration().getFrameId(),SL4B_Accessor.getRttpProvider());};
};
function SL_DP(){SL4B_FrameRegistrarAccessor.deregisterSlaveFrame(this.m_sSlaveFrameId);if(this.m_oMasterFrameRttpProvider!=null){
try {this.m_oMasterFrameRttpProvider.deregisterSlave(this.m_sSlaveFrameId,this,SL4B_SubscriptionManager);var l_nConnectionListeners=this.m_pConnectionListeners.length;
for(var l_nListener=0;l_nListener<l_nConnectionListeners;++l_nListener){this.m_oMasterFrameRttpProvider.removeConnectionListener(this.m_pConnectionListeners[l_nListener]);}}catch(e){}
}for(var i=0;i<SL4B_SubscriptionManager.m_pSubscribers.length;i++){this.removeSubscriber(SL4B_SubscriptionManager.m_pSubscribers[i]);}}
function SL_KS(A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"SlaveFrameRttpProvider.masterRegistered: {0}",A);this.m_oMasterFrameRttpProvider=A;var l_nConnectionListeners=this.m_pConnectionListeners.length;
for(var l_nListener=0;l_nListener<l_nConnectionListeners;++l_nListener){
try {A.addConnectionListener(this.m_pConnectionListeners[l_nListener]);}catch(e){}
}
try {A.registerSlave(this.m_sSlaveFrameId,this,SL4B_SubscriptionManager);}catch(e){}
if(this.m_bMasterRefresh==true){window.location.replace(window.location.href);}}
function SL_HM(){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"SlaveFrameRttpProvider.masterClosing");this.m_oMasterFrameRttpProvider=null;this.m_bMasterRefresh=true;}
var SL4B_SlaveFrameWrapperRttpProvider=function(){};
if(false){function SL4B_SlaveFrameWrapperRttpProvider(){}
}SL4B_SlaveFrameWrapperRttpProvider = function(){SL4B_ResilientRttpProvider.apply(this);this.CLASS_NAME="SL4B_SlaveFrameWrapperRttpProvider";this.m_oBaseRttpProvider=null;this.m_bProviderReady=false;this.m_oConnectionListener=new SL_MU(this);this.setUnderlyingRttpProvider = function(A){if(this.m_oBaseRttpProvider!=null){
try {this.m_oBaseRttpProvider.removeConnectionListener(this.m_oConnectionListener);}catch(e){}
}SL4B_Accessor.setUnderlyingRttpProvider(A);this._$setBaseRttpProvider(A);this.m_oConnectionListener.setRttpProvider(A);A.addConnectionListener(this.m_oConnectionListener);A.initialise();};
this.createRealRttpProvider = function(){this._$clearBaseRttpProvider();var l_oConfiguration=SL4B_Accessor.getConfiguration();
var l_oProvider=null;
if(l_oConfiguration.getService()!=null){l_oProvider=new SL4B_FailoverRttpProvider();l_oProvider.internalInitialise();this._$setBaseRttpProvider(l_oProvider);this.m_oBaseRttpProvider.addConnectionListener(this.m_oConnectionListener);this.m_oConnectionListener.setRttpProvider(l_oProvider);this._$sendQueuedCommands();}else 
{l_oProvider=SL4B_Accessor.getRttpProviderFactory().createRttpProvider(new SL4B_SimpleLiberatorConfiguration());this.setUnderlyingRttpProvider(l_oProvider);}return;
};
this.createSlaveRttpProvider = function(){var l_oLiberatorConfiguration=new SL4B_SimpleLiberatorConfiguration();
var l_oRttpProvider=new SL4B_SlaveFrameRttpProvider();
this.setUnderlyingRttpProvider(l_oRttpProvider);this._$sendQueuedCommands();return;
};
this.registerSubscriptionManager = function(A){this.m_oConnectionListener.registerSubscriptionManager(A);};
this.initialise();};
function SL_MU(B){this.m_pConnectionListeners=new Array();this.m_oSlaveProviderWrapper=B;this.m_oRttpProvider=null;this.m_oSubscriptionManager=null;this.setRttpProvider = function(A){this.m_oRttpProvider=A;};
}
SL_MU.prototype = new SL4B_ConnectionListener;SL_MU.prototype.registerSubscriptionManager = function(A){this.m_oSubscriptionManager=A;};
SL_MU.prototype.addConnectionListener = function(A){this.m_pConnectionListeners.push(A);};
SL_MU.prototype.removeConnectionListener = function(A){var l_nMatchIndex=-1;
for(var l_nListener=0,l_nLength=this.m_pConnectionListeners.length;l_nListener<l_nLength;++l_nListener){if(this.m_pConnectionListeners[l_nListener]==A){l_nMatchIndex=l_nListener;break;
}}if(l_nMatchIndex!=-1){this.m_pConnectionListeners.splice(l_nMatchIndex,1);}return (l_nMatchIndex!=-1);
};
SL_MU.prototype.notifyConnectionListeners = function(A,B){for(var l_nListener=0,l_nLength=this.m_pConnectionListeners.length;l_nListener<l_nLength;++l_nListener){if(B.length>0&&B[0]===undefined){B=[];}SL4B_MethodInvocationProxy._invokeWithTryCatch(this.m_pConnectionListeners[l_nListener],A,B);}};
SL_MU.prototype.connectionAttempt = function(B,A){this.notifyConnectionListeners("connectionAttempt",[B,A]);};
SL_MU.prototype.connectionWarning = function(C,D,B,A){this.notifyConnectionListeners("connectionWarning",[C,D,B,A]);};
SL_MU.prototype.connectionError = function(A){this.notifyConnectionListeners("connectionError",[A]);};
SL_MU.prototype.connectionInfo = function(A){this.notifyConnectionListeners("connectionInfo",[A]);};
SL_MU.prototype.connectionOk = function(C,A,B){this.notifyConnectionListeners("connectionOk",[C,A,B]);};
SL_MU.prototype.reconnectionOk = function(C,A,B){this.notifyConnectionListeners("reconnectionOk",[C,A,B]);};
SL_MU.prototype.fileDownloadError = function(B,A){this.notifyConnectionListeners("fileDownloadError",[B,A]);};
SL_MU.prototype.credentialsRetrieved = function(A){this.notifyConnectionListeners("credentialsRetrieved",[A]);};
SL_MU.prototype.loginError = function(A){this.notifyConnectionListeners("loginError",[A]);};
SL_MU.prototype.credentialsProviderSessionError = function(A,D,C,B){this.notifyConnectionListeners("credentialsProviderSessionError",[A,D,C,B]);};
SL_MU.prototype.loginOk = function(){this.notifyConnectionListeners("loginOk",[]);SL4B_WindowRegistrar.loginOK();setTimeout(this._invokeSendQueuedCommandsOnTimeout(),0);if(this.m_oSubscriptionManager!=null){this.m_oSubscriptionManager.ready();this.m_oSubscriptionManager=null;}};
SL_MU.prototype._invokeSendQueuedCommandsOnTimeout = function(){return function(){SL4B_Accessor.getRttpProvider()._$sendQueuedCommands();};
};
SL_MU.prototype.message = function(A,B){this.notifyConnectionListeners("message",[A,B]);};
SL_MU.prototype.serviceMessage = function(A,D,B,C){this.notifyConnectionListeners("serviceMessage",[A,D,B,C]);};
SL_MU.prototype.sessionEjected = function(A,B){this.notifyConnectionListeners("sessionEjected",[A,B]);};
SL_MU.prototype.sourceMessage = function(A,C,D,B){this.notifyConnectionListeners("sourceMessage",[A,C,D,B]);};
SL_MU.prototype.statistics = function(A,D,E,B,C){this.notifyConnectionListeners("statistics",[A,D,E,B,C]);};
SL4B_SlaveFrameWrapperRttpProvider.prototype = new SL4B_ResilientRttpProvider;SL4B_SlaveFrameWrapperRttpProvider.prototype.initialise = function(){if(!this.m_bConnectionListenerAdded){this.m_bConnectionListenerAdded=true;SL4B_AbstractRttpProvider.prototype.addConnectionListener.apply(this,[this.m_oConnectionListener]);}var l_oConnectionManager=new SL4B_ConnectionManager(null,null,null);
l_oConnectionManager.initialise();this.createSlaveRttpProvider();this.m_oBaseRttpProvider.initialise();};
SL4B_SlaveFrameWrapperRttpProvider.prototype.super_internalInitialise = SL4B_AbstractRttpProvider.prototype.internalInitialise;SL4B_SlaveFrameWrapperRttpProvider.prototype.super_register = SL4B_AbstractRttpProvider.prototype.register;SL4B_SlaveFrameWrapperRttpProvider.prototype.internalInitialise = function(){if(this.m_oBaseRttpProvider==null){this.super_internalInitialise();}else 
{this.m_oBaseRttpProvider.internalInitialise();}};
SL4B_SlaveFrameWrapperRttpProvider.prototype.onLoad = function(A){if(this.m_oBaseRttpProvider.onLoad){this.m_oBaseRttpProvider.onLoad(A);}};
SL4B_SlaveFrameWrapperRttpProvider.prototype.internalStop = function(){
try {this.m_oBaseRttpProvider.internalStop();}catch(e){}
};
SL4B_SlaveFrameWrapperRttpProvider.prototype.stop = function(){this.m_oBaseRttpProvider.stop();this.m_oConnectionListener.notifyConnectionListeners("connectionError",["Connection stopped"]);};
SL4B_SlaveFrameWrapperRttpProvider.prototype.addConnectionListener = function(A){this.m_oConnectionListener.addConnectionListener(A);};
SL4B_SlaveFrameWrapperRttpProvider.prototype.removeConnectionListener = function(A){return this.m_oConnectionListener.removeConnectionListener(A);
};
SL4B_SlaveFrameWrapperRttpProvider.prototype.register = function(){if(this.m_oBaseRttpProvider==null){this.super_register();}else 
{this.m_oBaseRttpProvider.register();}};
SL4B_SlaveFrameWrapperRttpProvider.prototype.registerSlave = function(B,A,C){this.m_oBaseRttpProvider.registerSlave(B,A,C);};
SL4B_SlaveFrameWrapperRttpProvider.prototype.deregisterSlave = function(B,A,C){this.m_oBaseRttpProvider.deregisterSlave(B,A,C);};
SL4B_SlaveFrameWrapperRttpProvider.prototype.connect = function(){this.m_oBaseRttpProvider.connect();};
SL4B_SlaveFrameWrapperRttpProvider.prototype.reconnect = function(){this.m_oBaseRttpProvider.reconnect();};
SL4B_SlaveFrameWrapperRttpProvider.prototype.connected = function(){this.m_oBaseRttpProvider.connected();};
SL4B_SlaveFrameWrapperRttpProvider.prototype.login = function(A,B){this.m_oBaseRttpProvider.login(A,B);};
SL4B_SlaveFrameWrapperRttpProvider.prototype.loggedIn = function(){this.m_oBaseRttpProvider.loggedIn();};
SL4B_SlaveFrameWrapperRttpProvider.prototype.onUnload = function(){this.m_oBaseRttpProvider.onUnload();};
SL4B_SlaveFrameWrapperRttpProvider.prototype.setMasterWindow = function(B,A){this.m_oBaseRttpProvider.setMasterWindow(B,A);this._$cacheRttpProviderData();};
SL4B_SlaveFrameWrapperRttpProvider.prototype.register = function(){if(this.m_oBaseRttpProvider){this.m_oBaseRttpProvider.register();}};
SL4B_SlaveFrameWrapperRttpProvider.prototype.masterRegistered = function(A){this.m_oBaseRttpProvider.masterRegistered(A);};
SL4B_SlaveFrameWrapperRttpProvider.prototype.masterClosing = function(){this.m_oBaseRttpProvider.masterClosing();};
SL4B_SlaveFrameWrapperRttpProvider.prototype.becomeMasterProvider = function(A){
try {this.createRealRttpProvider();this.onLoad(null);}catch(e){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"SlaveFrameWrapperRttpProvider.becomeMasterProvider() Exception: "+e);}
};
SL4B_SlaveFrameWrapperRttpProvider.prototype.reRequestObjects = function(A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINE_INT,"SlaveFrameWrapperRttpProvider.reRequestObjects()");var l_oProvider=SL4B_Accessor.getRttpProvider();
for(l_sObjectKey in A){var l_pListenerMap=A[l_sObjectKey];
for(l_oListenerId in l_pListenerMap){var l_oObjectSubscription=l_pListenerMap[l_oListenerId];
if(!l_oObjectSubscription.m_oSubscriber.m_bIsProxySubscriber){if(l_oObjectSubscription.isSubscribedToAllFields()){l_oProvider.getObject(l_oObjectSubscription.m_oSubscriber,l_oObjectSubscription.m_sObjectName,l_oObjectSubscription.m_sFilter);}else 
{for(var l_nFieldList=0,l_nLength=l_oObjectSubscription.m_pFieldLists.length;l_nFieldList<l_nLength;++l_nFieldList){var l_sFieldList=l_oObjectSubscription.m_pFieldLists[l_nFieldList];
l_oProvider.getObject(l_oObjectSubscription.m_oSubscriber,l_oObjectSubscription.m_sObjectName,l_oObjectSubscription.m_sFilter+";"+l_sFieldList.replace(/^,/,"").replace(/,$/,""));}}}}}};
var SL4B_MultiSourceRttpProvider=function(){};
var SL4B_FailoverAlgorithm=function(){};
if(false){function SL4B_FailoverAlgorithm(){}
}SL4B_FailoverAlgorithm = function(){this.nextLiberatorConfiguration = function(){};
this.setConnected = function(){};
};
var SL4B_ZunAlgorithm=function(){};
if(false){function SL4B_ZunAlgorithm(){}
}SL4B_ZunAlgorithm = function(){this.initialise = function(A){this.m_oFailoverConfiguration=new SL4B_FailoverConfiguration();this.m_oFailoverConfiguration.load(A);this.m_oCurrentServer=null;this.m_oConnectedServer=null;this.m_bReset=true;this.m_bFirstPass=true;this.m_sAlgorithm=this.m_oFailoverConfiguration.getAlgorithm();var l_oConfiguration=SL4B_Accessor.getConfiguration();
var l_sServerUrl=document.location.toString();
this.m_sProtocol="http";if(l_sServerUrl.match(/^http/)){this.m_sProtocol=l_sServerUrl.match(/^(https?)/)[1];}};
this.setAlgorithm = function(A){this.m_sAlgorithm=A;};
this.setServerWeights = function(A){SL4B_Failover_Log("SL4B_ZunAlgorithm.setServerWeights");var l_nWeight=0;
for(var i=0;i<A.length;i++){var l_oServer=A[i];
if(!l_oServer.isTried()){l_oServer.setWeightStart(l_nWeight);l_nWeight+=l_oServer.getWeight();l_oServer.setWeightEnd(l_nWeight);}}return l_nWeight;
};
this.clearTries = function(A,B){SL4B_Failover_Log("SL4B_ZunAlgorithm.clearTries");for(var i=0;i<A.length;i++){var l_oServer=A[i];
if(l_oServer==B){l_oServer.setTried(true);}else 
{l_oServer.setTried(false);}}};
this.chooseRandomServer = function(A){SL4B_Failover_Log("SL4B_ZunAlgorithm.chooseRandomServer");var l_nWeightMax=this.setServerWeights(A);
var l_nRandom=this.random(l_nWeightMax);
for(var i=0;i<A.length;i++){var l_oServer=A[i];
if(!l_oServer.isTried()){SL4B_Failover_Log("\t"+l_oServer.getAddress()+" "+l_oServer.getWeightStart()+" "+l_oServer.getWeightEnd()+" "+l_nRandom);if(l_oServer.getWeightStart()<=l_nRandom&&l_nRandom<l_oServer.getWeightEnd()){SL4B_Failover_Log("\t\tchosen");return l_oServer;
}}}return null;
};
this.random = function(A){SL4B_Failover_Log("SL4B_ZunAlgorithm.random");return l_nValue=Math.random()*100000%A;
};
this.chooseRandomPrimaryServer = function(){SL4B_Failover_Log("SL4B_ZunAlgorithm.chooseRandomPrimaryServer");return this.chooseRandomServer(this.m_oFailoverConfiguration.getPrimaryServers());
};
this.chooseRandomBackupServer = function(){SL4B_Failover_Log("SL4B_ZunAlgorithm.chooseRandomBackupServer");return this.chooseRandomServer(this.m_oFailoverConfiguration.getBackupServers());
};
this.addFirstMatchingConnectionMethod = function(C,B,A){for(var i=0;i<A.length;i++){var l_oConnectionMethod=A[i];
var l_pConnections=B.getConnections();
for(var j=0;j<l_pConnections.length;j++){var failoverServerConnection=l_pConnections[j];
if(failoverServerConnection.getType()==l_oConnectionMethod.getRTTPType()&&failoverServerConnection.getProtocol()==this.m_sProtocol){var l_sPort=B.getPorts()[this.m_sProtocol];
if(l_sPort==null){l_sPort=((this.m_sProtocol=="http") ? "80" : "443");}var l_oConnection=new SL4B_ConnectionData(this.m_sProtocol,B.getAddress(),l_sPort,l_oConnectionMethod);
C.addConnection(l_oConnection);return;
}}}};
this.nextLiberatorConfiguration = function(){SL4B_Failover_Log("SL4B_ZunAlgorithm.nextServer");if(this.m_bReset==true){if(this.m_oCurrentServer!=this.m_oConnectedServer){this.m_oCurrentServer=null;}this.clearTries(this.m_oFailoverConfiguration.getPrimaryServers(),this.m_oCurrentServer);this.clearTries(this.m_oFailoverConfiguration.getBackupServers(),this.m_oCurrentServer);this.m_bReset=false;this.m_bFirstPass=true;this.m_oConnectedServer=null;}var l_oServer=null;
var l_oLiberatorConfiguration=null;
switch(this.m_sAlgorithm){
case "Z":l_oServer=this.nextZServer();break;
case "U":l_oServer=this.nextUServer();break;
case "N":l_oServer=this.nextNServer();break;
}if(l_oServer!=null){l_oServer.setTried(true);this.m_oCurrentServer=l_oServer;l_oLiberatorConfiguration=new SL4B_LiberatorConfiguration();l_oLiberatorConfiguration.initialise();var l_oConfiguration=SL4B_Accessor.getConfiguration();
this.addFirstMatchingConnectionMethod(l_oLiberatorConfiguration,l_oServer,l_oConfiguration.getStreamingConnectionMethods());this.addFirstMatchingConnectionMethod(l_oLiberatorConfiguration,l_oServer,l_oConfiguration.getPollingConnectionMethods());}else 
{this.m_bReset=true;}return l_oLiberatorConfiguration;
};
this.nextZServer = function(){SL4B_Failover_Log("SL4B_ZunAlgorithm.nextZServer");var l_oServer=null;
l_oServer=this.chooseRandomPrimaryServer();if(l_oServer!=null){return l_oServer;
}l_oServer=this.chooseRandomBackupServer();if(l_oServer!=null){return l_oServer;
}return null;
};
this.nextUServer = function(){SL4B_Failover_Log("SL4B_ZunAlgorithm.nextUServer");var l_oServer=null;
if(this.m_bFirstPass==true){this.m_bFirstPass=false;l_oServer=this.chooseRandomPrimaryServer();if(l_oServer!=null){return l_oServer;
}}if(!this.m_oCurrentServer.isBackup()){l_oServer=this.m_oCurrentServer.getPairedServer();if(l_oServer!=null&&l_oServer.isTried()==false){return l_oServer;
}}l_oServer=this.chooseRandomBackupServer();if(l_oServer!=null){return l_oServer;
}l_oServer=this.chooseRandomPrimaryServer();if(l_oServer!=null){return l_oServer;
}return null;
};
this.nextNServer = function(){SL4B_Failover_Log("SL4B_ZunAlgorithm.nextUServer");var l_oServer=null;
if(this.m_bFirstPass==true){this.m_bFirstPass=false;l_oServer=this.chooseRandomPrimaryServer();if(l_oServer!=null){return l_oServer;
}}if(this.m_oCurrentServer!=null&&!this.m_oCurrentServer.isBackup()){l_oServer=this.m_oCurrentServer.getPairedServer();if(l_oServer!=null){return l_oServer;
}}l_oServer=this.chooseRandomPrimaryServer();if(l_oServer!=null){return l_oServer;
}l_oServer=this.chooseRandomBackupServer();if(l_oServer!=null){return l_oServer;
}return null;
};
this.setConnected = function(){this.m_oConnectedServer=this.m_oCurrentServer;this.m_bReset=true;};
};
function SL_JW(){SL4B_ZunAlgorithm.prototype = new SL4B_FailoverAlgorithm;}
var SL4B_Failover_Log=function(A){};
var SL4B_FailoverConfiguration=function(){this.m_sName="";this.m_sFailOverAlgorithm="Z";this.m_pPrimaryServers=new Array();this.m_pBackupServers=new Array();this.getName = function(){return this.m_sName;
};
this.getAlgorithm = function(){return this.m_sFailOverAlgorithm;
};
this.getPrimaryServers = function(){return this.m_pPrimaryServers;
};
this.getBackupServers = function(){return this.m_pBackupServers;
};
this.load = function(A){l_oXmlHttpRequest=null;
try {if(typeof XMLHttpRequest!='undefined'){l_oXmlHttpRequest=new XMLHttpRequest();}else 
{
try {l_oXmlHttpRequest=new ActiveXObject("Msxml2.XMLHTTP");}catch(e){l_oXmlHttpRequest=new ActiveXObject("Microsoft.XMLHTTP");}
}l_oXmlHttpRequest.open("GET",A,false);l_oXmlHttpRequest.send(null);if(l_oXmlHttpRequest.status==200){SL4B_Failover_Log("SL4B_FailoverConfiguration: Loaded file "+A+", status="+l_oXmlHttpRequest.status+" message: "+l_oXmlHttpRequest.statusText);var l_oDocElement=SL4B_XmlResponseRetriever.getXmlDocumentElement(l_oXmlHttpRequest);
if(l_oDocElement==null){SL4B_Logger.log(SL4B_DebugLevel.const_CRITICAL_INT,"Error parsing XML configuration ("+A+"): "+e.toString());SL4B_Failover_Log("SL4B_FailoverConfiguration: Error parsing XML configuration ("+A+"): "+e.message);throw new SL4B_Exception("SL4B_FailoverConfiguration: Error parsing XML configuration ("+A+"): "+e.message);
}this.parseRttpService(l_oDocElement);if(this.m_pPrimaryServers.length==0&&this.m_pBackupServers.length==0){SL4B_Logger.log(SL4B_DebugLevel.const_CRITICAL_INT,"SL4B_FailoverConfiguration: Error in file "+A+", no primary or backup servers found!");throw new SL4B_Exception("SL4B_FailoverConfiguration: Error in file "+A+", no primary or backup servers found!");
}SL4B_Failover_Log(this.toString());}else 
{SL4B_Failover_Log("SL4B_FailoverConfiguration: Failed to load file "+A+", status="+l_oXmlHttpRequest.status+" message: "+l_oXmlHttpRequest.statusText);throw new SL4B_Exception("SL4B_FailoverConfiguration: Failed to load file "+A+", status="+l_oXmlHttpRequest.status+" message: "+l_oXmlHttpRequest.statusText);
}}catch(e){SL4B_Logger.log(SL4B_DebugLevel.const_CRITICAL_INT,e.toString());SL4B_Failover_Log("SL4B_FailoverConfiguration: Exception: "+e.message);throw (e);
}
};
this.parseRttpService = function(A){SL4B_Failover_Log("\tparseRttpService "+A.tagName);
try {var l_pChildren=A.childNodes;
for(var i=0;i<l_pChildren.length;i++){var l_oChild=l_pChildren[i];
if(l_oChild.tagName=="name"){this.m_sName=l_oChild.firstChild.nodeValue;}else 
if(l_oChild.tagName=="failoveralgorithm"){this.m_sFailOverAlgorithm=l_oChild.firstChild.nodeValue;}else 
if(l_oChild.tagName=="rttpsite"){this.parseRttpSite(l_oChild);}}}catch(e){SL4B_Failover_Log("SL4B_FailoverConfiguration: Failed to parse rttpservice");throw (e);
}
};
this.parseRttpSite = function(A){SL4B_Failover_Log("\tparseRttpSite "+A.tagName);
try {var l_pChildren=A.childNodes;
for(var i=0;i<l_pChildren.length;i++){var l_oChild=l_pChildren[i];
if(l_oChild.tagName=="serverpair"){this.parseServerPair(l_oChild);}else 
if(l_oChild.tagName=="primaryserver"){var l_oServer=new SL4B_FailoverServer();
l_oServer.parseServer(l_oChild);this.m_pPrimaryServers.push(l_oServer);}else 
if(l_oChild.tagName=="backupserver"){var l_oServer=new SL4B_FailoverServer();
l_oServer.parseServer(l_oChild);this.m_pBackupServers.push(l_oServer);}}}catch(e){SL4B_Failover_Log("SL4B_FailoverConfiguration: Failed to parse RttpSite");throw (e);
}
};
this.parseServerPair = function(A){
try {SL4B_Failover_Log("\tparseServerPair "+A.tagName);var l_pChildren=A.childNodes;
var l_oPrimaryServer=null;
var l_oBackupServer=null;
for(var i=0;i<l_pChildren.length;i++){var l_oChild=l_pChildren[i];
if(l_oChild.tagName=="primaryserver"){l_oPrimaryServer=new SL4B_FailoverServer();l_oPrimaryServer.parseServer(l_oChild);this.m_pPrimaryServers.push(l_oPrimaryServer);}else 
if(l_oChild.tagName=="backupserver"){l_oBackupServer=new SL4B_FailoverServer();l_oBackupServer.parseServer(l_oChild);this.m_pBackupServers.push(l_oBackupServer);}}l_oPrimaryServer.setPairedServer(l_oBackupServer,false);l_oBackupServer.setPairedServer(l_oPrimaryServer,true);}catch(e){SL4B_Failover_Log("SL4B_FailoverConfiguration: Failed to parse ServerPair");throw (e);
}
};
this.toString = function(){var l_sValue="";
l_sValue+="Name = "+this.m_sName+", ";l_sValue+="\nFailOverAlgorithm = "+this.m_sFailOverAlgorithm+", ";l_sValue+="\nPrimary Servers:";for(var i=0;i<this.m_pPrimaryServers.length;i++){l_sValue+=this.m_pPrimaryServers[i].toString();}l_sValue+="\nBackup Servers:";for(var i=0;i<this.m_pBackupServers.length;i++){l_sValue+=this.m_pBackupServers[i].toString();}return l_sValue;
};
};
var SL4B_FailoverServer=function(){this.m_sAddress=null;this.m_sName=null;this.m_pConnections=new Array();this.m_pPorts=new Object();this.m_nWeight=1;this.m_oPairedServer=null;this.m_bBackup=false;this.m_bTried=false;this.m_nWeightStart=0;this.m_nWeightEnd=0;this.getName = function(){return this.m_sName;
};
this.getAddress = function(){return this.m_sAddress;
};
this.getConnections = function(){return this.m_pConnections;
};
this.getPorts = function(){return this.m_pPorts;
};
this.getPairedServer = function(){return this.m_oPairedServer;
};
this.setPairedServer = function(B,A){this.m_oPairedServer=B;this.m_bBackup=A;};
this.isBackup = function(){return this.m_bBackup;
};
this.getWeight = function(){return this.m_nWeight;
};
this.getWeightStart = function(){return this.m_nWeightStart;
};
this.getWeightEnd = function(){return this.m_nWeightEnd;
};
this.setWeightStart = function(A){this.m_nWeightStart=A;};
this.setWeightEnd = function(A){this.m_nWeightEnd=A;};
this.isTried = function(){return this.m_bTried;
};
this.setTried = function(A){this.m_bTried=A;};
this.parseServer = function(A){
try {SL4B_Failover_Log("\tparseServer "+A.tagName);var l_pChildren=A.childNodes;
for(var i=0;i<l_pChildren.length;i++){var l_oChild=l_pChildren[i];
if(l_oChild.tagName=="address"){this.m_sAddress=l_oChild.firstChild.nodeValue;}else 
if(l_oChild.tagName=="name"){this.m_sName=l_oChild.firstChild.nodeValue;}else 
if(l_oChild.tagName=="connections"){this.parseConnections(l_oChild);}else 
if(l_oChild.tagName=="ports"){this.parsePorts(l_oChild);}else 
if(l_oChild.tagName=="weight"){this.m_nWeight=parseInt(l_oChild.firstChild.nodeValue);}}}catch(e){SL4B_Failover_Log("SL4B_FailoverConfiguration: Failed to parse Server");throw (e);
}
};
this.parseConnections = function(A){SL4B_Failover_Log("\tparseConnections "+A.tagName);
try {var l_oServerConnection=null;
var l_pChildren=A.childNodes;
for(var i=0;i<l_pChildren.length;i++){var l_oChild=l_pChildren[i];
var l_sTagName=l_oChild.tagName;
if(l_sTagName=="type3"){this.addConnectionType(3,l_oChild.firstChild.nodeValue.toLowerCase());}else 
if(l_sTagName=="type4"){this.addConnectionType(4,l_oChild.firstChild.nodeValue.toLowerCase());}else 
if(l_sTagName=="type5"){this.addConnectionType(5,l_oChild.firstChild.nodeValue.toLowerCase());}else 
if(l_sTagName=="type8"){this.addConnectionType(8,l_oChild.firstChild.nodeValue.toLowerCase());}else 
if(l_sTagName=="type2"){this.addConnectionType(2,l_oChild.firstChild.nodeValue.toLowerCase());}else 
if(l_sTagName=="type6"){this.addConnectionType(6,l_oChild.firstChild.nodeValue.toLowerCase());}else 
{SL4B_Failover_Log("SL4B_FailoverConfiguration: Unknown connection type");}}}catch(e){SL4B_Failover_Log("SL4B_FailoverConfiguration: Failed to parse Connections");throw (e);
}
};
this.addConnectionType = function(A,B){l_oServerConnection=new SL4B_FailoverServerConnection(A,B);this.m_pConnections.push(l_oServerConnection);};
this.parsePorts = function(A){SL4B_Failover_Log("\tparsePorts "+A.tagName);
try {var l_pChildren=A.childNodes;
for(var i=0;i<l_pChildren.length;i++){var l_oChild=l_pChildren[i];
if(l_oChild.tagName=="http"){this.m_pPorts[l_oChild.tagName]=l_oChild.firstChild.nodeValue;}else 
if(l_oChild.tagName=="https"){this.m_pPorts[l_oChild.tagName]=l_oChild.firstChild.nodeValue;}else 
if(l_oChild.tagName=="direct"){this.m_pPorts[l_oChild.tagName]=l_oChild.firstChild.nodeValue;}}}catch(e){SL4B_Failover_Log("SL4B_FailoverConfiguration: Failed to parse Ports");throw (e);
}
};
this.toString = function(){var l_sValue="";
l_sValue+="\n\tAddress = "+this.m_sAddress;l_sValue+="\n\tName = "+this.m_sName;l_sValue+="\n\tWeight = "+this.m_nWeight;if(this.m_oPairedServer!=null){l_sValue+="\n\tPairedServer = "+this.m_oPairedServer.m_sAddress;}l_sValue+="\n\tConnections:";for(var i=0;i<this.m_pConnections.length;i++){l_sValue+=this.m_pConnections[i].toString();}l_sValue+="\n\tPorts:";for(sKey in this.m_pPorts){var sValue=this.m_pPorts[sKey];
l_sValue+="\n\t\t"+sKey+"="+sValue;}return l_sValue;
};
};
var SL4B_FailoverServerConnection=function(B,A){this.m_nType=5;if(B!=null){this.m_nType=B;}this.m_sProtocol="http";if(A!=null){this.m_sProtocol=A;}this.getType = function(){return this.m_nType;
};
this.getProtocol = function(){return this.m_sProtocol;
};
this.toString = function(){var l_sValue="";
l_sValue+="\n\t\tConnection:";l_sValue+="\n\t\t\tType = "+this.m_nType;l_sValue+="\n\t\t\tProtocol = "+this.m_sProtocol;return l_sValue;
};
};
var SL4B_FailoverRttpProvider=function(){};
if(false){function SL4B_FailoverRttpProvider(){}
}SL4B_FailoverRttpProvider = function(){SL4B_ResilientRttpProvider.apply(this);this.CLASS_NAME="SL4B_FailoverRttpProvider";this.m_bConnectionListenerAdded=false;this.m_oConnectionListener=new SL_IK(this);this.m_bAttemptReconnections=true;this.m_bConnectionLost=false;var l_sUrl=SL4B_Accessor.getConfiguration().getService();
this.m_oFailoverAlgorithm=new SL4B_ZunAlgorithm();this.m_oFailoverAlgorithm.initialise(l_sUrl);this.setUnderlyingRttpProvider = function(A){if(this.m_oBaseRttpProvider!=null){this.m_oBaseRttpProvider.removeConnectionListener(this.m_oConnectionListener);}SL4B_Accessor.setUnderlyingRttpProvider(A);this._$setBaseRttpProvider(A);this._$cacheRttpProviderData();this.m_oConnectionListener.setRttpProvider(A);this.m_oBaseRttpProvider.addConnectionListener(this.m_oConnectionListener);A.initialise();};
this.nextRttpProvider = function(){var l_bAnotherServerIsAvailable=true;
this._$clearBaseRttpProvider();var l_oLiberatorConfiguration=this.m_oFailoverAlgorithm.nextLiberatorConfiguration();
while(l_oLiberatorConfiguration==null){l_bAnotherServerIsAvailable=false;C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",this.const_INFO_CONNECTION_EVENT,"Failover cycle complete, recycling"));SL4B_Failover_Log("**** no next liberator configuration, restarting sequence *****");l_oLiberatorConfiguration=this.m_oFailoverAlgorithm.nextLiberatorConfiguration();}C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",this.const_INFO_CONNECTION_EVENT,"Failover trying: "+l_oLiberatorConfiguration.toSimpleString()));var l_oRttpProvider=this.createRttpProvider(l_oLiberatorConfiguration);
this.setUnderlyingRttpProvider(l_oRttpProvider);return l_bAnotherServerIsAvailable;
};
this.createRttpProvider = function(A){return SL4B_Accessor.getRttpProviderFactory().createRttpProvider(A);
};
this.isFailoverProvider=true;this.initialise();};
function SL_IK(B){this.m_pConnectionListeners=new Array();this.m_oFailoverProvider=B;this.m_oRttpProvider=null;this.setRttpProvider = function(A){this.m_oRttpProvider=A;};
}
SL_IK.prototype = new SL4B_ConnectionListener;SL_IK.prototype.addConnectionListener = function(A){this.m_pConnectionListeners.push(A);};
SL_IK.prototype.removeConnectionListener = function(A){var l_nMatchIndex=-1;
for(var l_nListener=0,l_nLength=this.m_pConnectionListeners.length;l_nListener<l_nLength;++l_nListener){if(this.m_pConnectionListeners[l_nListener]==A){l_nMatchIndex=l_nListener;break;
}}if(l_nMatchIndex!=-1){this.m_pConnectionListeners.splice(l_nMatchIndex,1);}return (l_nMatchIndex!=-1);
};
SL_IK.prototype.notifyConnectionListeners = function(A,B){for(var l_nListener=0,l_nLength=this.m_pConnectionListeners.length;l_nListener<l_nLength;++l_nListener){if(B.length>0&&B[0]===undefined){B=[];}SL4B_MethodInvocationProxy._invokeWithTryCatch(this.m_pConnectionListeners[l_nListener],A,B);}};
SL_IK.prototype.connectionAttempt = function(B,A){this.notifyConnectionListeners("connectionAttempt",[B,A]);};
SL_IK.prototype.connectionWarning = function(C,D,B,A){this.notifyConnectionListeners("connectionWarning",[C,D,B,A]);};
SL_IK.prototype.connectionError = function(A){if(this.m_oFailoverProvider.m_bAttemptReconnections){var l_bIsAnotherServerAvailable=this.m_oFailoverProvider.nextRttpProvider();
if(!l_bIsAnotherServerAvailable){this.m_oFailoverProvider.m_bConnectionLost=true;this.notifyConnectionListeners("connectionError",[A]);}this.m_oFailoverProvider.onLoad(null);}};
SL_IK.prototype.connectionInfo = function(A){this.notifyConnectionListeners("connectionInfo",[A]);};
SL_IK.prototype.connectionOk = function(C,A,B){this.notifyConnectionListeners("connectionOk",[C,A,B]);};
SL_IK.prototype.reconnectionOk = function(C,A,B){this.notifyConnectionListeners("reconnectionOk",[C,A,B]);};
SL_IK.prototype.fileDownloadError = function(B,A){this.notifyConnectionListeners("fileDownloadError",[B,A]);};
SL_IK.prototype.credentialsRetrieved = function(A){this.notifyConnectionListeners("credentialsRetrieved",[A]);};
SL_IK.prototype.loginError = function(A){this.notifyConnectionListeners("loginError",[A]);};
SL_IK.prototype.credentialsProviderSessionError = function(A,D,C,B){this.notifyConnectionListeners("credentialsProviderSessionError",[A,D,C,B]);};
SL_IK.prototype.loginOk = function(){this.notifyConnectionListeners("loginOk",[]);var self=this;
setTimeout(function(){self.m_oFailoverProvider._$sendQueuedCommands();},0);};
SL_IK.prototype.message = function(A,B){this.notifyConnectionListeners("message",[A,B]);};
SL_IK.prototype.serviceMessage = function(A,D,B,C){this.notifyConnectionListeners("serviceMessage",[A,D,B,C]);};
SL_IK.prototype.sessionEjected = function(A,B){this.notifyConnectionListeners("sessionEjected",[A,B]);};
SL_IK.prototype.sourceMessage = function(A,C,D,B){this.notifyConnectionListeners("sourceMessage",[A,C,D,B]);};
SL_IK.prototype.statistics = function(A,D,E,B,C){this.notifyConnectionListeners("statistics",[A,D,E,B,C]);};
SL4B_FailoverRttpProvider.prototype = new SL4B_ResilientRttpProvider;SL4B_FailoverRttpProvider.prototype.super_internalInitialise = SL4B_AbstractRttpProvider.prototype.internalInitialise;SL4B_FailoverRttpProvider.prototype.super_register = SL4B_AbstractRttpProvider.prototype.register;SL4B_FailoverRttpProvider.prototype.internalInitialise = function(){if(this.m_oBaseRttpProvider==null){this.super_internalInitialise();this.nextRttpProvider();}else 
{this.m_oBaseRttpProvider.internalInitialise();}};
SL4B_FailoverRttpProvider.prototype.initialise = function(){if(!this.m_bConnectionListenerAdded){this.m_bConnectionListenerAdded=true;SL4B_AbstractRttpProvider.prototype.addConnectionListener.apply(this,[this.m_oConnectionListener]);}if(this.m_oBaseRttpProvider!=null){this.m_oBaseRttpProvider.initialise();}};
SL4B_FailoverRttpProvider.prototype.onLoad = function(A){if(this.m_oBaseRttpProvider!=null&&this.m_bAttemptReconnections){this.m_bConnectionLost=false;this.m_oBaseRttpProvider.onLoad(A);}};
SL4B_FailoverRttpProvider.prototype.internalStop = function(){this.m_oBaseRttpProvider.internalStop();};
SL4B_FailoverRttpProvider.prototype.stop = function(){this.m_bAttemptReconnections=false;this.m_oBaseRttpProvider.stop();if(this.m_bConnectionLost===false){this.m_bConnectionLost=true;this.m_oConnectionListener.notifyConnectionListeners("connectionError",["Connection stopped"]);}};
SL4B_FailoverRttpProvider.prototype.addConnectionListener = function(A){this.m_oConnectionListener.addConnectionListener(A);};
SL4B_FailoverRttpProvider.prototype.removeConnectionListener = function(A){return this.m_oConnectionListener.removeConnectionListener(A);
};
SL4B_FailoverRttpProvider.prototype.register = function(){if(this.m_oBaseRttpProvider==null){this.super_register();}else 
{this.m_oBaseRttpProvider.register();}};
SL4B_FailoverRttpProvider.prototype.registerSlave = function(B,A,C){this.m_oBaseRttpProvider.registerSlave(B,A,C);};
SL4B_FailoverRttpProvider.prototype.deregisterSlave = function(B,A,C){this.m_oBaseRttpProvider.deregisterSlave(B,A,C);};
SL4B_FailoverRttpProvider.prototype.connect = function(){this.m_oBaseRttpProvider.connect();};
SL4B_FailoverRttpProvider.prototype.reconnect = function(){this.m_bAttemptReconnections=true;this.m_oBaseRttpProvider.reconnect();};
SL4B_FailoverRttpProvider.prototype.connected = function(){this.m_oBaseRttpProvider.connected();};
SL4B_FailoverRttpProvider.prototype.login = function(A,B){this.m_oBaseRttpProvider.login(A,B);};
SL4B_FailoverRttpProvider.prototype.loggedIn = function(){this.m_oBaseRttpProvider.loggedIn();};
SL4B_FailoverRttpProvider.prototype.onUnload = function(){this.m_oBaseRttpProvider.onUnload();};
var bURLValid=false;
var SL4B_AbstractJavaRttpProvider=function(){};
if(false){function SL4B_AbstractJavaRttpProvider(){}
}SL4B_AbstractJavaRttpProvider = function(){SL4B_AbstractRttpProvider.apply(this);this.m_sAppletDirectoryUrl=null;};
SL4B_AbstractJavaRttpProvider.prototype = new SL4B_AbstractRttpProvider;SL4B_AbstractJavaRttpProvider.prototype.connect = SL_FZ;SL4B_AbstractJavaRttpProvider.prototype.login = SL_FP;SL4B_AbstractJavaRttpProvider.prototype.reconnect = SL_EB;SL4B_AbstractJavaRttpProvider.prototype.getObject = function(C,B,A){this.m_oRttpApplet.getObject(this.getListener(C),B,this.validateFieldList(A));};
SL4B_AbstractJavaRttpProvider.prototype.getObjects = function(C,A,B){this.m_oRttpApplet.getObjects(this.getListener(C),A,this.validateFieldList(B));};
SL4B_AbstractJavaRttpProvider.prototype.removeObject = function(C,B,A){this.m_oRttpApplet.removeObject(this.getListener(C),B,this.validateFieldList(A));};
SL4B_AbstractJavaRttpProvider.prototype.removeObjects = function(C,A,B){this.m_oRttpApplet.removeObjects(this.getListener(C),A,this.validateFieldList(B));};
SL4B_AbstractJavaRttpProvider.prototype.getObjectType = function(B,A){this.m_oRttpApplet.getObjectType(this.getListener(B),A);};
SL4B_AbstractJavaRttpProvider.prototype.setThrottleObject = function(A,B){this.m_oRttpApplet.setThrottleObject(A,B);};
SL4B_AbstractJavaRttpProvider.prototype.setThrottleObjects = function(A,B){this.m_oRttpApplet.setThrottleObjects(A,B);};
SL4B_AbstractJavaRttpProvider.prototype.setGlobalThrottle = function(A){this.m_oRttpApplet.setGlobalThrottle(A);};
SL4B_AbstractJavaRttpProvider.prototype.disableWTStatsTimeout = function(A){this.m_oRttpApplet.disableWTStatsTimeout(A);};
SL4B_AbstractJavaRttpProvider.prototype.clearObjectListeners = SL_NC;SL4B_AbstractJavaRttpProvider.prototype.blockObjectListeners = SL_DH;SL4B_AbstractJavaRttpProvider.prototype.unblockObjectListeners = SL_DE;SL4B_AbstractJavaRttpProvider.prototype.createObject = function(A,B){return this.m_oRttpApplet.createObject(A,B);
};
SL4B_AbstractJavaRttpProvider.prototype.contribObject = SL_FA;SL4B_AbstractJavaRttpProvider.prototype.deleteObject = function(A){return this.m_oRttpApplet.deleteObject(A);
};
SL4B_AbstractJavaRttpProvider.prototype.getFieldNames = function(){return this.m_oRttpApplet.getFieldNames();
};
SL4B_AbstractJavaRttpProvider.prototype.logout = function(){this.m_oRttpApplet.logout();};
SL4B_AbstractJavaRttpProvider.prototype.setDebugLevel = function(A){return this.m_oRttpApplet.setDebugLevel(A);
};
SL4B_AbstractJavaRttpProvider.prototype.getVersion = function(){return this.m_oRttpApplet.getVersion()+"-"+this.m_oRttpApplet.getBuildVersion();
};
SL4B_AbstractJavaRttpProvider.prototype.getVersionInfo = function(){return this.m_oRttpApplet.getVersionInfo();
};
SL4B_AbstractJavaRttpProvider.prototype.debug = function(B,A){if(typeof A=="undefined"){this.m_oRttpApplet.debug(B);}else 
{this.m_oRttpApplet.debug(B,A);}};
SL4B_AbstractJavaRttpProvider.prototype.m_oRttpApplet = null;SL4B_AbstractJavaRttpProvider.prototype.m_nAppletCheckTimeoutHandle = null;SL4B_AbstractJavaRttpProvider.prototype.m_nRttpAppletTimeoutHandle = null;SL4B_AbstractJavaRttpProvider.prototype.m_pUpdateQueue = new Array();SL4B_AbstractJavaRttpProvider.prototype.m_bConnectionEstablished = false;SL4B_AbstractJavaRttpProvider.prototype.const_ALL_FIELDS = 0;SL4B_AbstractJavaRttpProvider.prototype.getRttpAppletId = function(){return "appRttpApplet";
};
SL4B_AbstractJavaRttpProvider.prototype.createParameterHtml = function(B,A){return "<param name=\""+B+"\" value=\""+A+"\"></param>";
};
SL4B_AbstractJavaRttpProvider.prototype.validateFieldList = function(A){return ((A==null||typeof A=="undefined") ? this.const_ALL_FIELDS : A);
};
SL4B_AbstractJavaRttpProvider.prototype.setRttpApplet = SL_GC;SL4B_AbstractJavaRttpProvider.prototype.getAppletParameterHtml = SL_GR;SL4B_AbstractJavaRttpProvider.prototype.writeAppletToDocument = SL_NI;SL4B_AbstractJavaRttpProvider.prototype.startAppletCheckTimeout = SL_KL;SL4B_AbstractJavaRttpProvider.prototype.startRttpAppletTimeout = SL_IM;SL4B_AbstractJavaRttpProvider.prototype.appletCheckDownloadComplete = SL_NZ;SL4B_AbstractJavaRttpProvider.prototype.rttpAppletDownloadComplete = SL_JV;SL4B_AbstractJavaRttpProvider.prototype.urlCheckDownloadFailed = SL_DK;SL4B_AbstractJavaRttpProvider.prototype.appletCheckDownloadFailed = SL_RV;SL4B_AbstractJavaRttpProvider.prototype.rttpAppletDownloadFailed = SL_LD;SL4B_AbstractJavaRttpProvider.prototype.queueUpdates = SL_GL;SL4B_AbstractJavaRttpProvider.prototype.dequeueUpdates = SL_PM;SL4B_AbstractJavaRttpProvider.prototype.WTConnectionError = SL_FV;SL4B_AbstractJavaRttpProvider.prototype.WTConnectionInfo = SL_AA;SL4B_AbstractJavaRttpProvider.prototype.WTConnectionOk = SL_BZ;SL4B_AbstractJavaRttpProvider.prototype.WTMessage = SL_GF;SL4B_AbstractJavaRttpProvider.prototype.WTReconnect = SL_JP;SL4B_AbstractJavaRttpProvider.prototype.WTServiceMessage = SL_HI;SL4B_AbstractJavaRttpProvider.prototype.WTSessionEjected = SL_GN;SL4B_AbstractJavaRttpProvider.prototype.WTSourceMessage = SL_KU;SL4B_AbstractJavaRttpProvider.prototype.WTStats = SL_AW;function SL_FZ(){this.m_oRttpApplet.addStatsListener('SL4B_Accessor.getRttpProvider()');this.m_oRttpApplet.addConnectionListener("SL4B_Accessor.getRttpProvider()");this.m_oRttpApplet.addReconnectListener('SL4B_Accessor.getRttpProvider()');this.m_oRttpApplet.addMessagesListener('SL4B_Accessor.getRttpProvider()');this.m_oRttpApplet.connect(false);}
function SL_EB(){this.m_oRttpApplet.reconnect(false);}
function SL_FP(A,B){this.credentialsRetrieved();var l_sLoginResponse=this.m_oRttpApplet.login(A,B);
if(typeof l_sLoginResponse!="undefined"&&l_sLoginResponse!=null&&l_sLoginResponse!=""){C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",this.const_LOGIN_ERROR_CONNECTION_EVENT,l_sLoginResponse));}else 
{this.loggedIn();}}
function SL_NC(B,A){if(typeof A=="undefined"){this.m_oRttpApplet.clearObjectListeners(this.getListener(B));}else 
{this.m_oRttpApplet.clearObjectListeners(this.getListener(B),A);}}
function SL_DH(B,A){if(typeof A=="undefined"){this.m_oRttpApplet.blockObjectListeners(this.getListener(B));}else 
{this.m_oRttpApplet.blockObjectListeners(this.getListener(B),A);}}
function SL_DE(B,A){if(typeof A=="undefined"){this.m_oRttpApplet.unblockObjectListeners(this.getListener(B));}else 
{this.m_oRttpApplet.unblockObjectListeners(this.getListener(B),A);}}
function SL_FA(C,A,B){for(var l_nFieldIndex=0,l_nLength=B.size();l_nFieldIndex<l_nLength;++l_nFieldIndex){var l_oField=B.getField(l_nFieldIndex);
if(l_nFieldIndex==(l_nLength-1)){this.m_oRttpApplet.contribObject(A,l_oField.m_sName,l_oField.m_sValue,this.getListener(C));}else 
{this.m_oRttpApplet.contribObject(A,l_oField.m_sName,l_oField.m_sValue,null);}}}
function SL_GC(A){this.m_oRttpApplet=A;this.connect();}
function SL_GR(){var l_oConfiguration=SL4B_Accessor.getConfiguration();
var l_sHtml="";
l_sHtml+=this.createParameterHtml('scriptable','true');l_sHtml+=this.createParameterHtml('loaded.callback','1');var l_sDirectPort=null;
var l_sDirectEnable='0';
var l_sHttpEnable='0';
var l_sRefreshEnable='0';
var l_oConnectionData;
while((l_oConnectionData=this.m_oLiberatorConfiguration.getNextConnection())!=null){switch(l_oConnectionData.m_oMethod){
case SL4B_ConnectionMethod.JAVADIRECT:l_sDirectEnable='1';l_sDirectPort=l_oConnectionData.m_sPort;break;
case SL4B_ConnectionMethod.JAVAHTTP:l_sHttpEnable='1';break;
case SL4B_ConnectionMethod.JAVAPOLLING:l_sRefreshEnable='1';break;
}}l_sHtml+=this.createParameterHtml('rttp.direct.enable',l_sDirectEnable);l_sHtml+=this.createParameterHtml('rttp.http.enable',l_sHttpEnable);l_sHtml+=this.createParameterHtml('rttp.refresh.enable',l_sRefreshEnable);if(l_sDirectPort!=null){l_sHtml+=this.createParameterHtml('rttp.server.port',l_sDirectPort);}l_sHtml+=this.createParameterHtml('rttp.cookie.enable','1');l_sHtml+=this.createParameterHtml('rttp.response.dequeue.timeout','120000');l_sHtml+=this.createParameterHtml('rttp.debuglevel',l_oConfiguration.getRttpDebugLevel());if(l_oConfiguration.isMultiUpdates()){l_sHtml+=this.createParameterHtml('record.updates','2');}else 
{l_sHtml+=this.createParameterHtml('record.updates','1');}if(l_oConfiguration.getApplicationId()!=null){l_sHtml+=this.createParameterHtml('rttp.applicationid',l_oConfiguration.getApplicationId());}if(l_oConfiguration.getObjectNameDelimiter()!=SL4B_AbstractRttpProvider.const_OBJECT_NAME_DELIMITER){l_sHtml+=this.createParameterHtml('objectname.delimiter',l_oConfiguration.getObjectNameDelimiter());}if(l_oConfiguration.isNoStaleNotify()!=null){l_sHtml+=this.createParameterHtml('notify.stale',l_oConfiguration.isNoStaleNotify());}if(l_oConfiguration.getGarbageCollectionFrequency()!=null){l_sHtml+=this.createParameterHtml('rttp.gc.force',l_oConfiguration.getGarbageCollectionFrequency());}if(l_oConfiguration.getMicrosoftGarbageCollectionFrequency()!=null){l_sHtml+=this.createParameterHtml('rttp.gc.force.microsoft',l_oConfiguration.getMicrosoftGarbageCollectionFrequency());}if(l_oConfiguration.getSunGarbageCollectionFrequency()!=null){l_sHtml+=this.createParameterHtml('rttp.gc.force.sun',l_oConfiguration.getSunGarbageCollectionFrequency());}if(l_oConfiguration.getType3PollPeriod()!=null){l_sHtml+=this.createParameterHtml('rttp.refresh.time',l_oConfiguration.getType3PollPeriod());}l_sHtml+=this.createParameterHtml('discard.delay',l_oConfiguration.getDiscardDelay());l_sHtml+=this.createParameterHtml('logout.delay',l_oConfiguration.getLogoutDelay());l_sHtml+=this.createParameterHtml('stats.interval',l_oConfiguration.getStatsInterval());l_sHtml+=this.createParameterHtml('stats.reset',l_oConfiguration.getStatsReset());l_sHtml+=this.createParameterHtml('stats.timeout',l_oConfiguration.getStatsTimeout());return l_sHtml;
}
function SL_NI(A){var l_sHtmlToWrite="<div style=\"height:0px;width:0px;overflow:hidden;\">";
l_sHtmlToWrite+=A;l_sHtmlToWrite+="</div>";document.write(l_sHtmlToWrite);}
function SL_KL(){this.m_nAppletCheckTimeoutHandle=setTimeout("SL4B_Accessor.getRttpProvider().appletCheckDownloadFailed()",SL4B_Accessor.getConfiguration().getAppletCheckTimeout());}
function SL_IM(){this.m_nRttpAppletTimeoutHandle=setTimeout("SL4B_Accessor.getRttpProvider().rttpAppletDownloadFailed()",SL4B_Accessor.getConfiguration().getRttpAppletTimeout());}
function SL_NZ(){if(this.m_nAppletCheckTimeoutHandle!==null){clearTimeout(this.m_nAppletCheckTimeoutHandle);this.m_nAppletCheckTimeoutHandle=null;}}
function SL_JV(){if(this.m_nRttpAppletTimeoutHandle!==null){clearTimeout(this.m_nRttpAppletTimeoutHandle);this.m_nRttpAppletTimeoutHandle=null;}var l_oApplet=SL4B_Accessor.getBrowserAdapter().getElementById(this.getRttpAppletId());
this.setRttpApplet(l_oApplet);}
function SL_DK(){this.notifyConnectionListeners(this.const_FILE_DOWNLOAD_ERROR_CONNECTION_EVENT,SL4B_FileType.URL_CHECK,this.m_sAppletDirectoryUrl+"/urlcheck.js");}
function SL_RV(){if(this.m_nAppletCheckTimeoutHandle!==null){this.m_nAppletCheckTimeoutHandle=null;this.notifyConnectionListeners(this.const_FILE_DOWNLOAD_ERROR_CONNECTION_EVENT,SL4B_FileType.APPLET_CHECK,this.m_sAppletDirectoryUrl+"/AppletCheck.class");}}
function SL_LD(){if(this.m_nRttpAppletTimeoutHandle!==null){this.m_nRttpAppletTimeoutHandle=null;var l_oConfiguration=SL4B_Accessor.getConfiguration();
this.notifyConnectionListeners(this.const_FILE_DOWNLOAD_ERROR_CONNECTION_EVENT,SL4B_FileType.RTTP_APPLET,this.m_sAppletDirectoryUrl+l_oConfiguration.getAppletJarName());}}
function SL_GL(A){this.m_pUpdateQueue.push(A);setTimeout("SL4B_Accessor.getRttpProvider().dequeueUpdates()",0);}
function SL_PM(){var l_oQueue=this.m_pUpdateQueue.shift();
l_oQueue.doAllUpdates();}
function SL_FV(A){C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",this.const_WARNING_CONNECTION_EVENT,A));}
function SL_AA(A){C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",this.const_INFO_CONNECTION_EVENT,A));}
function SL_BZ(C,A,B){if(!this.m_bConnectionEstablished){this.m_bConnectionEstablished=true;setTimeout("SL4B_Accessor.getRttpProvider().connected();",0);}C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",this.const_OK_CONNECTION_EVENT,C,A,B));}
function SL_GF(A,B){C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",this.const_MESSAGE_CONNECTION_EVENT,A,B));}
function SL_JP(){C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",this.const_ERROR_CONNECTION_EVENT));}
function SL_HI(A,D,B,C){C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",this.const_SERVICE_MESSAGE_CONNECTION_EVENT,A,D,B,C));}
function SL_GN(A,B){C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",this.const_SESSION_EJECTED_CONNECTION_EVENT,A,B));}
function SL_KU(A,C,D,B){C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",this.const_SOURCE_MESSAGE_CONNECTION_EVENT,A,C,D,B));}
function SL_AW(A,D,E,B,C){C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",this.const_STATISTICS_CONNECTION_EVENT,A,D,E,B,C));}
function WTAppletLoaded(){setTimeout("SL4B_Accessor.getRttpProvider().rttpAppletDownloadComplete()",0);}
function WTWrapper(){eval(arguments[0]+"."+arguments[1]+"(arguments[2], arguments[3])");}
function WTQueue(A){SL4B_Accessor.getRttpProvider().queueUpdates(A);}
function AppletCheckLoaded(){setTimeout("SL4B_Accessor.getRttpProvider().appletCheckDownloadComplete()",0);}
var SL4B_AppletRttpProvider=function(){};
if(false){function SL4B_AppletRttpProvider(){}
}SL4B_AppletRttpProvider = function(A){this.m_oLiberatorConfiguration=A;var l_sAppletUrl=SL4B_Accessor.getConfiguration().getAppletUrl();
if(l_sAppletUrl==null){var l_oConnectionData=this.m_oLiberatorConfiguration.peekAtNextConnection();
l_sAppletUrl=((l_oConnectionData==null) ? "" : l_oConnectionData.getServerUrl())+"/"+SL4B_Accessor.getConfiguration().getAppletPath();}this.m_sAppletDirectoryUrl=l_sAppletUrl;};
SL4B_AppletRttpProvider.prototype = new SL4B_AbstractJavaRttpProvider;SL4B_AppletRttpProvider.prototype.initialise = SL_KP;SL4B_AppletRttpProvider.prototype.loadUrlCheck = SL_HD;SL4B_AppletRttpProvider.prototype.loadAppletCheck = SL_OR;SL4B_AppletRttpProvider.prototype.loadRttpApplet = SL_LB;function SL_KP(){this.loadUrlCheck();this.loadAppletCheck();this.loadRttpApplet();}
function SL_HD(){SL4B_ScriptLoader.loadScript(this.m_sAppletDirectoryUrl+"/urlcheck.js?"+(new Date()).valueOf());SL4B_ScriptLoader.loadSl4bScript("applet-rttp-provider/js-check-file-loaded.js");}
function SL_OR(){var l_sHtml="<applet width=\"0\" height=\"0\"";
l_sHtml+="codebase=\""+this.m_sAppletDirectoryUrl+"\" ";l_sHtml+="code=\"AppletCheck.class\" ";l_sHtml+="mayscript=\"mayscript\" ";l_sHtml+="></applet>";this.startAppletCheckTimeout();this.writeAppletToDocument(l_sHtml);}
function SL_LB(){var l_oConfiguration=SL4B_Accessor.getConfiguration();
var l_sHtml="<applet id=\""+this.getRttpAppletId()+"\" width=\"0\" height=\"0\" ";
l_sHtml+="codebase=\""+this.m_sAppletDirectoryUrl+"\" ";if(SL4B_Accessor.getBrowserAdapter().isFirefox()){l_sHtml+="code=\"com.rttp.applet.FirefoxRTTPApplet.class\" ";}else 
{l_sHtml+="code=\"com.rttp.applet.RTTPApplet.class\" ";}l_sHtml+="archive=\""+l_oConfiguration.getAppletJarName()+"\" ";l_sHtml+="mayscript=\"mayscript\" ";l_sHtml+=">";l_sHtml+=this.getAppletParameterHtml();l_sHtml+="</applet>";this.startRttpAppletTimeout();this.writeAppletToDocument(l_sHtml);}
var SL4B_AbstractObjectRttpProvider=function(){};
if(false){function SL4B_AbstractObjectRttpProvider(){}
}SL4B_AbstractObjectRttpProvider = function(A){SL4B_AbstractRttpProvider.apply(this);this.m_oLiberatorConfiguration=A;};
function SL_GQ(){SL4B_AbstractObjectRttpProvider.prototype = new SL4B_AbstractRttpProvider;}
var SL4B_JavaScriptRttpProviderConstants = new function(){this.const_TYPE_PARAMETER="type";this.const_DOMAIN_PARAMETER="domain";this.const_URL_PARAMETER="url";this.const_UNIQUEID_PARAMETER="uniqueid";this.const_MAX_GET_LENGTH_PARAMETER="maxget";this.const_INIT_PARAMETER="init";this.const_TYPE4_PARAMETER="type4";this.const_REQUEST_TYPE="request";this.const_RESPONSE_TYPE="response";};
var SL4B_RttpCodes = new function(){this.const_REQUEST_ACK=0;this.const_CONNECT_OK=1;this.const_FIELD_MAP=50;this.const_SESSION_EJECTED=90;this.const_LOGIN_OK=101;this.const_RECON_OK=111;this.const_RESP_UNKNOWN=200;this.const_DIRECTORY_RESP=220;this.const_PAGE_RESP=221;this.const_RECORD_RESP=222;this.const_NEWS_RESP=223;this.const_STORY_RESP=224;this.const_CHAT_RESP=227;this.const_CONT_RESP=228;this.const_AUTODIR_RESP=229;this.const_PERM_RESP=230;this.const_CONTRIB_OK=301;this.const_DISCARD_OK=302;this.const_CREATE_OK=303;this.const_DELETE_OK=304;this.const_NOOP_OK=305;this.const_LOGOUT_OK=306;this.const_THROTTLE_OK=307;this.const_SYNC_OK=309;this.const_CONTRIB_WAIT=311;this.const_CONTRIB_OK_DELAY=351;this.const_OBJECT_UPDATES=400;this.const_STATUS_OK=415;this.const_STATUS_STALE=416;this.const_STATUS_LIMITED=417;this.const_STATUS_REMOVED=418;this.const_STATUS_INFO=419;this.const_DIR_UPD=420;this.const_PAGE_UPD=421;this.const_REC1_UPD=422;this.const_NEWS_UPD=423;this.const_STORY_UPD=424;this.const_REC2_UPD=425;this.const_REC3_UPD=426;this.const_CHAT_UPD=427;this.const_CONT_UPD=428;this.const_AUTODIR_UPD=429;this.const_PERM_UPD=430;this.const_PERM_IMG=450;this.const_PERM_CLR=470;this.const_REC1_IMG=472;this.const_NEWS_IMG=473;this.const_STORY_IMG=474;this.const_REC2_IMG=475;this.const_REC3_IMG=476;this.const_CONT_IMG=478;this.const_AUTODIR_IMG=479;this.const_REC1_CLR=482;this.const_NEWS_CLR=463;this.const_REC2_CLR=485;this.const_REC3_CLR=486;this.const_PERM_DEL=490;this.const_REC1_DEL=492;this.const_REC2_DEL=495;this.const_REC3_DEL=496;this.const_MISC_HEARTBEAT=501;this.const_INIT_HEARTBEAT=502;this.const_TIMED_HEARTBEAT=503;this.const_SOURCE_UP=511;this.const_SOURCE_DOWN=512;this.const_SOURCE_WARN=513;this.const_SERVICE_OK=515;this.const_SERVICE_DOWN=516;this.const_SERVICE_LIMITED=517;this.const_STATUS_MSG=520;this.const_WARNING_MSG=530;this.const_ERROR_MSG=540;this.const_NOT_FOUND_DELAY=550;this.const_READ_DENY_DELAY=560;this.const_WRITE_DENY_DELAY=570;this.const_UNAVAILABLE_DELAY=580;this.const_INVALID_PARAMETERS=581;this.const_DELETED_DELAY=590;this.const_NOT_FOUND=600;this.const_READ_DENY=610;this.const_WRITE_DENY=620;this.const_UNAVAILABLE=630;this.const_INVALID_PARAMETERS_DELAY=631;this.const_CONTRIB_FAILED=650;this.const_THROTTLE_FAILED=660;this.const_THROTTLE_UP_FAILED=661;this.const_THROTTLE_DOWN_FAILED=662;this.const_THROTTLE_MIN_FAILED=663;this.const_THROTTLE_MAX_FAILED=664;this.const_THROTTLE_STOP_FAILED=665;this.const_THROTTLE_START_FAILED=666;this.const_THROTTLE_DEF_FAILED=667;this.const_THROTTLE_NL_FAILED=668;this.const_LOGIN_FAILURES=700;this.const_INVALID_USER=701;this.const_INVALID_PASS=702;this.const_INVALID_IP=703;this.const_ACCT_EXPIRED=704;this.const_ALREADY_LOGGED_IN=705;this.const_LICENSE_SITE=711;this.const_LICENSE_USER=712;this.const_GOTO_URL=721;this.const_AUTH_ERROR_STR=731;this.const_NOT_LOGGED_IN=800;this.const_REQUEST_ERROR=810;this.const_INVALID_REQUEST=811;this.const_SERVER_ERROR=820;this.const_INVALID_SESSION_ID=832;this.const_MULTI_RESPONSE=900;this.isResponseCode = function(A){return ((A>=this.const_LOGIN_OK&&A<this.const_CONTRIB_OK_DELAY)||(A>=this.const_NOT_FOUND&&A<this.const_LOGIN_FAILURES)||(A>=this.const_NOT_LOGGED_IN&&A<this.const_MULTI_RESPONSE));
};
this.isErrorCode = function(A){return ((A>=this.const_NOT_LOGGED_IN)&&(A<=this.const_INVALID_SESSION_ID));
};
this.isImageCode = function(A){return ((A>=this.const_DIRECTORY_RESP&&A<this.const_CONTRIB_OK)||(A>=this.const_REC1_IMG&&A<=this.const_AUTODIR_IMG)||(A>=this.const_PERM_IMG&&A<this.const_PERM_CLR));
};
this.isEventCode = function(A){return ((A>=this.const_CONTRIB_OK_DELAY&&A<this.const_MISC_HEARTBEAT)||(A>=this.const_NOT_FOUND_DELAY&&A<this.const_NOT_FOUND));
};
this.isFinalEventCode = function(A){return ((A>=this.const_CONTRIB_OK_DELAY&&A<this.const_OBJECT_UPDATES)||(A>=this.const_NOT_FOUND_DELAY&&A<this.const_NOT_FOUND));
};
};
var GF_Base64Decoder=function(){};
if(false){function GF_Base64Decoder(){}
}GF_Base64Decoder = function(){this.m_mBase64CharacterToBase10ValueMap={};this.m_nTwoDigitBase64Limit=4096;var sBase64Encoding="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_";
for(var nDecodedValue=0,nLength=sBase64Encoding.length;nDecodedValue<nLength;++nDecodedValue){this.m_mBase64CharacterToBase10ValueMap[sBase64Encoding.charAt(nDecodedValue)]=nDecodedValue;}};
GF_Base64Decoder.prototype.decodeRttpCode = function(A){return this._decodeTwoDigitBase64Value(A);
};
GF_Base64Decoder.prototype.decodeSequenceNumber = function(A){return this._decodeTwoDigitBase64Value(A);
};
GF_Base64Decoder.prototype.decodeNextExpectedSequenceNumber = function(A){if(A===null||A.length===0){return 0;
}var nNextSequenceNumber=this.decodeSequenceNumber(A)+1;
if(nNextSequenceNumber<this.m_nTwoDigitBase64Limit){return nNextSequenceNumber;
}return 0;
};
GF_Base64Decoder.prototype._decodeTwoDigitBase64Value = function(A){if(A.length!==2){this._throwIllegalEncodingException(A);}var n64s=this.m_mBase64CharacterToBase10ValueMap[A.charAt(0)];
var nUnits=this.m_mBase64CharacterToBase10ValueMap[A.charAt(1)];
if(n64s===undefined||nUnits===undefined){this._throwIllegalEncodingException(A);}return (n64s*64)+nUnits;
};
GF_Base64Decoder.prototype._throwIllegalEncodingException = function(A){throw new SL4B_Exception("Illegal base 64 encoded value \""+A+"\"");
};
GF_Base64Decoder=new GF_Base64Decoder();function SL4B_FieldMask(){this.CLASSNAME="SL4B_FieldMask";this.bHaveSubscribedToAll=false;this.mExceptions={};this.nNumberOfExceptions=0;}
SL4B_FieldMask.prototype.add = function(A){if(this.bHaveSubscribedToAll){return this._dontMakeExceptionForField(A);
}else 
{return this._makeExceptionForField(A);
}return false;
};
SL4B_FieldMask.prototype.addAll = function(){if(this.bHaveSubscribedToAll==false||this.nNumberOfExceptions>0){this.bHaveSubscribedToAll=true;this.mExceptions={};this.nNumberOfExceptions=0;return true;
}return false;
};
SL4B_FieldMask.prototype.remove = function(A){if(this.bHaveSubscribedToAll){return this._makeExceptionForField(A);
}else 
{return this._dontMakeExceptionForField(A);
}return false;
};
SL4B_FieldMask.prototype.removeAll = function(){if(this.bHaveSubscribedToAll||this.nNumberOfExceptions>0){this.bHaveSubscribedToAll=false;this.mExceptions={};this.nNumberOfExceptions=0;return true;
}return false;
};
SL4B_FieldMask.prototype.isEmpty = function(){return this.bHaveSubscribedToAll==false&&this.nNumberOfExceptions==0;
};
SL4B_FieldMask.prototype.isSubscribedToAll = function(){return this.bHaveSubscribedToAll&&this.nNumberOfExceptions==0;
};
SL4B_FieldMask.prototype.contains = function(A){if(this.bHaveSubscribedToAll){return this.mExceptions[A]!=true;
}return this.mExceptions[A]==true;
};
SL4B_FieldMask.prototype.getFieldList_deprecated = function(){if(this.bHaveSubscribedToAll!=true){var pFields=[];
for(sField in this.mExceptions){if(this.mExceptions[sField]==true){pFields.push(sField);}}return pFields.join(",");
}return "";
};
SL4B_FieldMask.prototype.isSupersetOf = function(A){if(this.bHaveSubscribedToAll==false&&A.bHaveSubscribedToAll==false){for(sField in A.mExceptions){if(!this.mExceptions[sField]){return false;
}}return true;
}else 
if(this.bHaveSubscribedToAll&&A.bHaveSubscribedToAll){for(sField in this.mExceptions){if(!A.mExceptions[sField]){return false;
}}return true;
}else 
if(this.bHaveSubscribedToAll){for(sField in A.mExceptions){if(this.mExceptions[sField]){return false;
}}return true;
}return false;
};
SL4B_FieldMask.prototype.union = function(A){if(this.isSupersetOf(A))return false;
if(this.bHaveSubscribedToAll==false&&A.bHaveSubscribedToAll==false){for(sField in A.mExceptions){if(A.mExceptions[sField]==true){this._makeExceptionForField(sField);}}}else 
if(this.bHaveSubscribedToAll&&A.bHaveSubscribedToAll){var mMyExceptions=this.mExceptions;
this.mExceptions={};this.nNumberOfExceptions=0;for(sField in mMyExceptions){if(mMyExceptions[sField]==true&&A.mExceptions[sField]==true){this._makeExceptionForField(sField);}}}else 
{var mExclusions=this.bHaveSubscribedToAll ? this.mExceptions : A.mExceptions;
var mInclusions=this.bHaveSubscribedToAll ? A.mExceptions : this.mExceptions;
this.mExceptions={};this.nNumberOfExceptions=0;this.bHaveSubscribedToAll=true;for(sField in mExclusions){if(mExclusions[sField]==true&&mInclusions[sField]!=true){this._makeExceptionForField(sField);}}}return true;
};
SL4B_FieldMask.prototype.subtract = function(A){var bChanged=false;
if(this.bHaveSubscribedToAll==false&&A.bHaveSubscribedToAll==false){for(sField in A.mExceptions){if(A.mExceptions[sField]==true){bChanged=this._dontMakeExceptionForField(sField)||bChanged;}}}else 
if(this.bHaveSubscribedToAll&&A.bHaveSubscribedToAll){var mOldExclusions=this.mExceptions;
this.mExceptions={};this.nNumberOfExceptions=0;this.bHaveSubscribedToAll=false;for(sField in A.mExceptions){if(A.mExceptions[sField]==true&&mOldExclusions[sField]!=true){bChanged=this._makeExceptionForField(sField)||bChanged;}}}else 
if(this.bHaveSubscribedToAll){for(sField in A.mExceptions){if(A.mExceptions[sField]==true){bChanged=this._makeExceptionForField(sField)||bChanged;}}}else 
{for(sField in this.mExceptions){if(A.mExceptions[sField]!=true){bChanged=this._dontMakeExceptionForField(sField)||bChanged;}}}return bChanged;
};
SL4B_FieldMask.prototype.subscribe = function(B,A,C){var pFields=new Array(this.nNumberOfExceptions);
var nCount=0;
for(sField in this.mExceptions){pFields[nCount]=sField;nCount++;}var sFieldList=pFields.join(",");
if(this.bHaveSubscribedToAll){B.requestObject(A,C,"");}else 
if(this.nNumberOfExceptions>0){B.requestObject(A,C,sFieldList);}};
SL4B_FieldMask.prototype.clone = function(){var oClone=new SL4B_FieldMask();
oClone.bHaveSubscribedToAll=this.bHaveSubscribedToAll;for(sField in this.mExceptions){oClone.mExceptions[sField]=this.mExceptions[sField];}oClone.nNumberOfExceptions=this.nNumberOfExceptions;return oClone;
};
SL4B_FieldMask.prototype.toString = function(){var pResult=[];
var sJoinChars=", ";
if(this.bHaveSubscribedToAll){pResult.push("ALL");sJoinChars=", -";}for(sField in this.mExceptions){pResult.push(sField);}return "["+pResult.join(sJoinChars)+"]";
};
SL4B_FieldMask.prototype._makeExceptionForField = function(A){if(this.mExceptions[A]!=true){this.mExceptions[A]=true;this.nNumberOfExceptions++;return true;
}return false;
};
SL4B_FieldMask.prototype._dontMakeExceptionForField = function(A){if(this.mExceptions[A]==true){delete this.mExceptions[A];this.nNumberOfExceptions--;return true;
}return false;
};
var SL4B_RequestRecord=function(){};
if(false){function SL4B_RequestRecord(){}
}SL4B_RequestRecord = function(A,C,B,E,D){this.sMessage=A;this.oOriginalMessage=C;this.oListener=B;this.bHighPriority=E;this.oContext=D;this.sObjectName="";this.mParameters={};this.pFields=[];if(this.sMessage!=null){var pParms=A.split(/[;,]/);
var sRequest=pParms.shift();
this.sObjectName=sRequest.split(" ")[1];for(var i=0;i<pParms.length;i++){var pKeyval=pParms[i].split("=");
if(pKeyval.length>1){this.mParameters[pKeyval[0]]=pKeyval[1];}else 
{this.pFields.push(pKeyval[0]);}}}};
SL4B_RequestRecord.prototype.getObjectName = function(){return this.sObjectName;
};
SL4B_RequestRecord.prototype.getParameters = function(){return this.mParameters;
};
SL4B_RequestRecord.prototype.getFields = function(){return this.pFields;
};
SL4B_RequestRecord.prototype.getFilterContent = function(A){var result={};
if(A!==null&&A!==undefined&&A.length>0){var filterStart=A.indexOf(';');
if(filterStart>=0){var filterMsg=A.substring(filterStart+1);
while(filterMsg!=""){filterMsg=this.getNextNameValuePair(filterMsg,result);}}}return result;
};
SL4B_RequestRecord.prototype.getMessage = function(){return this.sMessage;
};
SL4B_RequestRecord.prototype.getOriginalMessage = function(){return this.oOriginalMessage;
};
SL4B_RequestRecord.prototype.getListener = function(){return this.oListener;
};
SL4B_RequestRecord.prototype.isHighPriority = function(){return this.bHighPriority;
};
SL4B_RequestRecord.prototype.getContext = function(){return this.oContext;
};
SL4B_RequestRecord.prototype.toString = function(){return "{RequestRecord: "+this.sMessage+" "+this.oOriginalMessage+" "+this.oListener+" "+this.bHighPriority+" "+this.oContext+"}";
};
var SL4B_ManagedConnection=function(){};
if(false){function SL4B_ManagedConnection(){}
}SL4B_ManagedConnection = function(){this.CLASS_NAME="SL4B_ManagedConnection";this.m_pRequestQueue=new Array();this.m_pPriorityRequestQueue=new Array();this.m_pResponseQueue=new Array();this.m_mObjectNumberToRequestRecord=new Object();this.m_oSystemEventReceiverRecord=null;this.m_oObjectNumberMap=new SL4B_ObjectNumberMap(this);this.m_oConnectionManager=null;this.m_sSessionId=null;};
SL4B_ManagedConnection.MESSAGES={REQUEST_CONFLICT:"{0}: Conflict between message listeners for object number {1}. Two different listeners have been registered to process the message.", UNEXPECTED_RESPONSE:"{0}: Received response when none were expected.  message={1}", UNKNOWN_OBJECT_NUMBER:"{0}: Received update for an unknown object number.  message={1}", IGNORING_RESPONSE_QUEUE:"{0}: ignoring expected response queue queue for message={1}", RESPONSE_QUEUED:"{0}: queued expected response for message={1}", RESPONSE_DEQUEUED:"{0}: dequeued reponse for message={1}, response={2}", ADDED_OBJECT_NUMBER_EVENT_LISTENER:"{0}: Added event listener for object number {1}", REMOVED_OBJECT_NUMBER_EVENT_LISTENER:"{0}: Removed event listener for object number {1}", CLEARING_STATE:"{0}: Clearing state.", INCOMING_MESSAGES_EXPECTED_AFTER_LOGIN:"{0}: The expected incoming should be empty immediately after receiving a login, but instead has {1} messages.  First message is '{2}'", QUEUED_HIGH_PRIORITY_MESSAGE:"{0}: Queued high priority message {1}", QUEUED_MESSAGE:"{0}: Queued message {1}", SESSION_ID_NULL_FOR_HIGH_PRIORITY_MESSAGE:"{0}: not connected at the moment, queuing high priority message {1}", SESSION_ID_NULL_FOR_MESSAGE:"{0}: not connected at the moment, queuing send message {1}", DONT_SEND_NEXT_MESSAGE:"{0}: Didn't send next message - {1}"};SL4B_ManagedConnection.prototype._$setConnectionManager = function(A){this.m_oConnectionManager=A;};
SL4B_ManagedConnection.prototype.sendMessage = function(A,B,C){this._sendPriorityMessage(A,B,false,C);};
SL4B_ManagedConnection.prototype.setSystemEventReceiver = function(A){this.m_oSystemEventReceiverRecord=new SL4B_RequestRecord(null,null,A,false,null);};
SL4B_ManagedConnection.prototype._setSessionId = function(A){this.m_sSessionId=A;SL4B_Logger.logConnectionMessage(true,"Session Id: {0}",this.m_sSessionId);};
SL4B_ManagedConnection.prototype._getSessionId = function(){return this.m_sSessionId;
};
SL4B_ManagedConnection.prototype._hasSessionId = function(){return this.m_sSessionId!==null;
};
SL4B_ManagedConnection.prototype.receiveMessage = function(A){var oNormalisedMessage=A;
var oMsgRecord=null;
var nRttpCode=oNormalisedMessage.getRttpCode();
if(SL4B_RttpCodes.isResponseCode(nRttpCode)){oMsgRecord=this._getNextResponseListener(oNormalisedMessage);}else 
{oMsgRecord=this._getEventListener(oNormalisedMessage);}if(SL4B_RttpCodes.isErrorCode(nRttpCode)){this._triggerReconnect("Received RTTP error message : "+oNormalisedMessage.toString(),true);}else 
{if(oMsgRecord){this.m_oObjectNumberMap._setMsgRecord(oMsgRecord);oMsgRecord.getListener().receiveMessage(A,oMsgRecord,this.m_oObjectNumberMap);}}};
SL4B_ManagedConnection.prototype._getEventListener = function(A){var oListener=null;
var nObjectNumber=A.getObjectNumber();
var oMsgRecord=null;
if(nObjectNumber!==null){oMsgRecord=this.m_mObjectNumberToRequestRecord[nObjectNumber];if(oMsgRecord===undefined){this._log(SL4B_DebugLevel.const_RTTP_ERROR_INT,SL4B_ManagedConnection.MESSAGES.UNKNOWN_OBJECT_NUMBER,"_getEventListener",A);return null;
}else 
{if(SL4B_RttpCodes.isFinalEventCode(A.getRttpCode())){this._log(SL4B_DebugLevel.const_RTTP_FINEST_INT,SL4B_ManagedConnection.MESSAGES.REMOVED_OBJECT_NUMBER_EVENT_LISTENER,"_getEventListener",nObjectNumber);delete this.m_mObjectNumberToRequestRecord[nObjectNumber];}}}else 
{oMsgRecord=this.m_oSystemEventReceiverRecord;}return oMsgRecord;
};
SL4B_ManagedConnection.prototype.getRequestRecord = function(A){return this.m_mObjectNumberToRequestRecord[A];
};
SL4B_ManagedConnection.prototype._getNextResponseListener = function(A){var oListener=null;
var oMsgRecord=null;
var nObjectNumber=A.getObjectNumber();
if(this.m_pResponseQueue.length>0){oMsgRecord=this.m_pResponseQueue.shift();this._log(SL4B_DebugLevel.const_RTTP_FINEST_INT,SL4B_ManagedConnection.MESSAGES.RESPONSE_DEQUEUED,"_getNextResponseListener",oMsgRecord,A.toString());oListener=oMsgRecord.getListener();if(nObjectNumber!==null){this._log(SL4B_DebugLevel.const_RTTP_FINEST_INT,SL4B_ManagedConnection.MESSAGES.ADDED_OBJECT_NUMBER_EVENT_LISTENER,"_getNextResponseListener",nObjectNumber);var oOrigRecord=this.m_mObjectNumberToRequestRecord[nObjectNumber];
if(oOrigRecord!==undefined&&oOrigRecord.getListener()!==oMsgRecord.getListener()){this._log(SL4B_DebugLevel.const_WARN_INT,SL4B_ManagedConnection.MESSAGES.REQUEST_CONFLICT,"_getNextResponseListener",nObjectNumber);}else 
{this.m_mObjectNumberToRequestRecord[nObjectNumber]=oMsgRecord;}}}else 
{this._log(SL4B_DebugLevel.const_RTTP_ERROR_INT,SL4B_ManagedConnection.MESSAGES.UNEXPECTED_RESPONSE,"_getNextResponseListener",A);this._triggerReconnect("Received response when none was expected",true);return null;
}return oMsgRecord;
};
SL4B_ManagedConnection.prototype._triggerReconnect = function(A,B){SL4B_Accessor.getUnderlyingRttpProvider().createConnectionLost(A,B);};
SL4B_ManagedConnection.prototype._$isFullReconnectRequired = function(){var bFullReconnect=false;
for(var i=0,nLength=this.m_pResponseQueue.length;i<nLength;++i){if(this.m_pResponseQueue[i].getMessage().match(/^REQUEST|^DISCARD|^CREATE|^DELETE|^CONTRIB|^THROTTLE|^GLOBAL_THROTTLE|^LOGIN/)){bFullReconnect=true;break;
}}return bFullReconnect;
};
SL4B_ManagedConnection.prototype.clearResponseQueue = function(){this._log(SL4B_DebugLevel.const_RTTP_FINER_INT,SL4B_ManagedConnection.MESSAGES.CLEARING_STATE,"clearResponseQueue");this.m_pResponseQueue=new Array();};
SL4B_ManagedConnection.prototype.clear = function(){this._log(SL4B_DebugLevel.const_RTTP_FINER_INT,SL4B_ManagedConnection.MESSAGES.CLEARING_STATE,"clear");this.m_pRequestQueue=new Array();this.m_pPriorityRequestQueue=new Array();if(this.m_pResponseQueue.length>0){this._log(SL4B_DebugLevel.const_WARN_INT,SL4B_ManagedConnection.MESSAGES.INCOMING_MESSAGES_EXPECTED_AFTER_LOGIN,"clear",this.m_pResponseQueue.length,this.m_pResponseQueue[0]);}this.m_pResponseQueue=new Array();this.m_mObjectNumberToRequestRecord=new Object();};
SL4B_ManagedConnection.prototype._sendPriorityMessage = function(A,B,D,C){if(B==null){throw new SL4B_Exception("SL4B_ManagedConnection._sendPriorityMessage: Listener may not be null or undefined.");
}if(B.receiveMessage==null){throw new SL4B_Exception("SL4B_ManagedConnection._sendPriorityMessage: Listener must implement receiveMessage.");
}var oMsgRecord=new SL4B_RequestRecord(A,null,B,D,C);
if(D){this._log(SL4B_DebugLevel.const_RTTP_FINEST_INT,SL4B_ManagedConnection.MESSAGES.QUEUED_HIGH_PRIORITY_MESSAGE,"_sendPriorityMessage",A);this.m_pPriorityRequestQueue.push(oMsgRecord);}else 
{this._log(SL4B_DebugLevel.const_RTTP_FINEST_INT,SL4B_ManagedConnection.MESSAGES.QUEUED_MESSAGE,"_sendPriorityMessage",A);this.m_pRequestQueue.push(oMsgRecord);}this._sendNextRttpMessage();};
SL4B_ManagedConnection.prototype._sendNextRttpMessage = function(){if(this.m_oConnectionManager.readyToRequest()&&this._hasMessages()){if(this.m_sSessionId!==null){var oMsgRecord=this._peekMessage();
if(oMsgRecord===null){this._log(SL4B_DebugLevel.const_RTTP_FINEST_INT,SL4B_ManagedConnection.MESSAGES.DONT_SEND_NEXT_MESSAGE,"_sendNextRttpMessage","not logged in");return;
}var bMergedCommands=(SL4B_Accessor.getCapabilities().getMergedCommands()==1);
var nBodyLength=SL4B_Accessor.getCapabilities().getHttpBodyLength();
var sMsg="";
var sMsgLine="";
var sEncodedMsg="";
var sEncodedMsgLine="";

do{var sSimpleMsg=oMsgRecord.getMessage();
sMsgLine=sSimpleMsg;if(sSimpleMsg=="SYNC"){sMsgLine=this.m_oConnectionManager.getConnectionProxy().getNextSyncMessage();}sMsgLine=this.m_sSessionId+((sMsgLine=="") ? "" : (" "+sMsgLine));sEncodedMsgLine=GF_RequestEncoder.encodeUrl(sMsgLine);if(sEncodedMsg.length+sEncodedMsgLine.length+1>=nBodyLength){break;
}if(oMsgRecord.getListener()!==SL4B_ManagedConnection.NO_RESPONSE_EXPECTED_MESSAGE_RECEIVER){this._addResponses(oMsgRecord);}else 
{this._log(SL4B_DebugLevel.const_RTTP_FINEST_INT,SL4B_ManagedConnection.MESSAGES.IGNORING_RESPONSE_QUEUE,"_sendNextRttpMessage",oMsgRecord);}if(sMsg.length!=0){sMsg+="\n";sEncodedMsg+="\n";}sMsg+=sMsgLine;sEncodedMsg+=sEncodedMsgLine;this._dequeueMessage();oMsgRecord=this._peekMessage();}while(oMsgRecord&&bMergedCommands&&sSimpleMsg!="LOGOUT");this.m_oConnectionManager.makingRequest();this._sendRttpMessage(sEncodedMsg,sMsg);}else 
{if(this.m_pPriorityRequestQueue.length>0){this._log(SL4B_DebugLevel.const_DEBUG_INT,SL4B_ManagedConnection.MESSAGES.SESSION_ID_NULL_FOR_HIGH_PRIORITY_MESSAGE,"_sendNextRttpMessage",this.m_pPriorityRequestQueue[0]);}else 
{this._log(SL4B_DebugLevel.const_DEBUG_INT,SL4B_ManagedConnection.MESSAGES.SESSION_ID_NULL_FOR_MESSAGE,"_sendNextRttpMessage",this.m_pRequestQueue[0]);}}}else 
{var sMessage;
if(!this._hasMessages()){sMessage="no messages remaining in queue";}else 
{sMessage="an acknowledgement for the last message has not been received";}this._log(SL4B_DebugLevel.const_RTTP_FINEST_INT,SL4B_ManagedConnection.MESSAGES.DONT_SEND_NEXT_MESSAGE,"_sendNextRttpMessage",sMessage);}};
SL4B_ManagedConnection.prototype._sendRttpMessage = function(A,B){this.m_oConnectionManager.getConnectionProxy().send(A);SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINER_INT,"> {0}",B);};
SL4B_ManagedConnection.prototype._addResponse = function(A){this._log(SL4B_DebugLevel.const_RTTP_FINEST_INT,SL4B_ManagedConnection.MESSAGES.RESPONSE_QUEUED,"_addResponse",A);this.m_pResponseQueue.push(A);};
SL4B_ManagedConnection.prototype._addResponses = function(A){var sMsg=A.getMessage();
var nEndOfCommand=sMsg.indexOf(" ");
if(nEndOfCommand>0){var sCommand=sMsg.substring(0,nEndOfCommand);
if(this._mayHaveMultipleObjects(sCommand)){var pObjects=sMsg.substring(nEndOfCommand+1).split(SL4B_AbstractRttpProvider.const_OBJECT_NAME_DELIMITER);
var start=0;
if(pObjects[0].substr(0,1)==";"){start=1;}for(var i=start,len=pObjects.length;i<len;i++){var oSyntheticRequest=new SL4B_RequestRecord(sCommand+" "+pObjects[i],A,A.getListener(),A.isHighPriority(),A.getContext());
this._addResponse(oSyntheticRequest);}}else 
{this._addResponse(A);}}else 
{this._addResponse(A);}};
SL4B_ManagedConnection.prototype._mayHaveMultipleObjects = function(A){return A=="THROTTLE"||A=="REQUEST"||A=="DISCARD";
};
SL4B_ManagedConnection.prototype._dequeueMessage = function(){var oMsgRecord=null;
if(this.m_pPriorityRequestQueue.length>0){oMsgRecord=this.m_pPriorityRequestQueue.shift();}else 
if(this.m_oConnectionManager.isLoggedIn()){oMsgRecord=this.m_pRequestQueue.shift();}return oMsgRecord;
};
SL4B_ManagedConnection.prototype._peekMessage = function(){var oMsgRecord=null;
if(this.m_pPriorityRequestQueue.length>0){oMsgRecord=this.m_pPriorityRequestQueue[0];}else 
if(this.m_pRequestQueue.length>0&&this.m_oConnectionManager.isLoggedIn()){oMsgRecord=this.m_pRequestQueue[0];}return oMsgRecord;
};
SL4B_ManagedConnection.prototype._hasMessages = function(){return this.m_pPriorityRequestQueue.length>0||this.m_pRequestQueue.length>0;
};
SL4B_ManagedConnection.prototype._log = function(B,A){arguments[1]="SL4B_ManagedConnection."+arguments[1];SL4B_Logger.log.apply(SL4B_Logger,arguments);};
SL4B_ManagedConnection.NULL_MESSAGE_RECEIVER = function(){};
SL4B_ManagedConnection.NULL_MESSAGE_RECEIVER = new function(){this.receiveMessage = function(){};
};
SL4B_ManagedConnection.NO_RESPONSE_EXPECTED_MESSAGE_RECEIVER = function(){};
SL4B_ManagedConnection.NO_RESPONSE_EXPECTED_MESSAGE_RECEIVER = new function(){this.receiveMessage = function(){};
};
function SL4B_ObjectNumberMap(A){this.m_oManagedConnection=A;this.m_oMsgRecord=null;}
SL4B_ObjectNumberMap.prototype._setMsgRecord = function(A){this.m_oMsgRecord=A;};
SL4B_ObjectNumberMap.prototype.addObjectNumber = function(A){this.m_oManagedConnection._log(SL4B_DebugLevel.const_RTTP_FINEST_INT,SL4B_ManagedConnection.MESSAGES.ADDED_OBJECT_NUMBER_EVENT_LISTENER,"SL4B_ObjectNumberMap.addObjectNumber",A);this.m_oManagedConnection.m_mObjectNumberToRequestRecord[A]=this.m_oMsgRecord;};
SL4B_ObjectNumberMap.prototype.removeObjectNumber = function(A){this.m_oManagedConnection._log(SL4B_DebugLevel.const_RTTP_FINEST_INT,SL4B_ManagedConnection.MESSAGES.REMOVED_OBJECT_NUMBER_EVENT_LISTENER,"SL4B_ObjectNumberMap.removeObjectNumber",A);delete this.m_oManagedConnection.m_mObjectNumberToRequestRecord[A];};
function GF_IFrameLoader(A){this.m_sFrameId=A;this.m_eContainerDiv=null;}
GF_IFrameLoader.prototype.createImage = function(){return document.createElement("img");
};
GF_IFrameLoader.prototype.getContainerDiv = function(){if(this.m_eContainerDiv==null){this.m_eContainerDiv=document.createElement('div');this.m_eContainerDiv.setAttribute("id","SL4B_URL_CHECK");this.m_eContainerDiv.style.height="1px";this.m_eContainerDiv.style.width="1px";this.m_eContainerDiv.style.display="none";document.body.appendChild(this.m_eContainerDiv);}return this.m_eContainerDiv;
};
GF_IFrameLoader.prototype.loadUrl = function(D,A,C,B){if(typeof (C.liberatorUnavailable)!='function'){throw new SL4B_Exception("Handler parameter must have a liberatorUnavailable function");
}if(B){SL4B_Logger.logConnectionMessage(true,"LiberatorUrlCheck skipped for message limit reconnection");var l_oFrameWindow=SL4B_Accessor.getBrowserAdapter().getFrameWindow(this.m_sFrameId);
l_oFrameWindow.location.replace(D);C.liberatorAvailable();}else 
{var oCheckDiv=this.getContainerDiv();
var sTestImageUrl=A+"/url-check.gif?"+((new Date()).valueOf());
var sFrameId=this.m_sFrameId;
var eImg=this.createImage();
var bImgRemoved=false;
eImg.setAttribute("src",sTestImageUrl);oCheckDiv.appendChild(eImg);eImg.onload = function(){if(bImgRemoved==false){oCheckDiv.removeChild(eImg);clearTimeout(nTimeoutId);bImgRemoved=true;SL4B_Logger.logConnectionMessage(true,"LiberatorUrlCheck.onLoad[{0}]: Liberator available, loading {1}",sFrameId,D);var l_oFrameWindow=SL4B_Accessor.getBrowserAdapter().getFrameWindow(sFrameId);
l_oFrameWindow.location.replace(D);C.liberatorAvailable();}};
eImg.onerror = function(){if(bImgRemoved==false){oCheckDiv.removeChild(eImg);clearTimeout(nTimeoutId);bImgRemoved=true;SL4B_Logger.logConnectionMessage(true,"LiberatorUrlCheck.onerror[{0}]: Liberator not available, when trying to load {1}",sFrameId,D);C.liberatorUnavailable();}};
SL4B_Logger.logConnectionMessage(true,"LiberatorUrlCheck.testLiberatorIsAvailable [{0}]: Checking Liberator availability: {1}",sFrameId,sTestImageUrl);var nTimeoutId=setTimeout(eImg.onerror,10000);
}};
function C_LiberatorUrlCheck(){this.m_pFrameIdToLiberatorUrlCheckMap={};}
C_LiberatorUrlCheck.prototype.createLiberatorUrlCheck = function(A){this.m_pFrameIdToLiberatorUrlCheckMap[A]=new GF_IFrameLoader(A);};
C_LiberatorUrlCheck.prototype.getLiberatorUrlCheck = function(A){return this.m_pFrameIdToLiberatorUrlCheckMap[A];
};
C_LiberatorUrlCheck=new C_LiberatorUrlCheck();var SL4B_AbstractConnection=function(){};
if(false){function SL4B_AbstractConnection(){}
function SL_AC(){}
}SL4B_AbstractConnection = function(){this.CLASS_NAME="SL4B_AbstractConnection";this.m_oRttpProvider=null;this.m_sUrlPrefix="";this.m_oResponseHttpRequest=null;this.m_sResponseUniqueId=null;this.m_sRequestUniqueId=null;this.m_oRequestHttpRequest=null;this.m_oMessageReceiver=null;this.m_sLastSequenceNumber="";this.m_oRttpMessage=null;this.m_bConnectionStopped=false;this.m_oResponseQueueStatistics=this._createResponseQueueStatistics();this.m_pResponseQueueStatisticsListeners=[];this.m_pPendingWork=[];this.m_oConnectionManager=null;this.m_bDecodeFields=null;this.m_bSkipURLCheck=false;};
SL4B_AbstractConnection.prototype.setSkipURLCheck = function(A){this.m_bSkipURLCheck=A;};
SL4B_AbstractConnection.prototype.setDecodeFields = function(A){this.m_bDecodeFields=A;};
SL4B_AbstractConnection.prototype._createResponseQueueStatistics = function(){return new SL4B_ResponseQueueStatistics();
};
SL4B_AbstractConnection.prototype._$setConnectionManager = function(A){this.m_oConnectionManager=A;};
SL4B_AbstractConnection.prototype.initialise = function(A,B){this.m_oRttpProvider=A;this.m_sUrlPrefix=B;this.m_oResponseHttpRequest=null;this.m_oRequestHttpRequest=null;this.m_oRttpMessage=new SL4B_RttpMessage();this.m_oMessageReceiver=null;};
SL4B_AbstractConnection.prototype.getResponseQueueStatistics = function(){return this.m_oResponseQueueStatistics;
};
SL4B_AbstractConnection.prototype.getUrlPrefix = function(){return this.m_sUrlPrefix;
};
SL4B_AbstractConnection.prototype.toString = function(){return "AbstractConnection(url = "+this.m_sUrlPrefix+")";
};
SL4B_AbstractConnection.prototype.connect = function(){throw new SL4B_Error("connect method not implemented");
};
SL4B_AbstractConnection.prototype.send = SL_JE;SL4B_AbstractConnection.prototype.setRequestHttpRequest = SL_JS;SL4B_AbstractConnection.prototype.setResponseHttpRequest = SL_MQ;SL4B_AbstractConnection.prototype.getLastSequenceNumber = function(){return this.m_sLastSequenceNumber;
};
SL4B_AbstractConnection.prototype.start = function(){throw new SL4B_Error("start method not implemented");
};
SL4B_AbstractConnection.prototype.stop = function(){this.m_bConnectionStopped=true;this._clearAllPendingTimeouts();};
SL4B_AbstractConnection.prototype.setMessageReceiver = SL_QW;SL4B_AbstractConnection.prototype.parseRttpMessage = SL_PA;SL4B_AbstractConnection.prototype.createChannel = SL_QG;SL4B_AbstractConnection.prototype.createStandardRequestChannel = SL_LO;SL4B_AbstractConnection.prototype.createStandardResponseChannel = SL_IJ;SL4B_AbstractConnection.prototype.getResponseUniqueId = SL_QP;SL4B_AbstractConnection.prototype.getRequestUniqueId = SL_PC;function SL_QP(){return this.m_sResponseUniqueId;
}
function SL_PC(){return this.m_sRequestUniqueId;
}
function SL_JE(A){if(!this.m_bConnectionStopped){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"AbstractConnection.send({0}?{1})",this.m_sUrlPrefix,A);
try {this.m_oRequestHttpRequest.send(this.m_sUrlPrefix+"?"+A);}catch(e){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"AbstractConnection.send: failed to send \"{0}\" due to exception {1}",A,e);}
}}
function SL_MQ(A){this.m_oResponseHttpRequest=A;}
function SL_JS(A){this.m_oRequestHttpRequest=A;}
function SL_QW(A){if(A!==null&&typeof A.receiveMessage!="function"){throw new SL4B_Exception("Specified listener does not define the receiveMessage method");
}this.m_oMessageReceiver=A;}
SL4B_AbstractConnection.prototype.httpRequestError = function(C,B,A){if(B=="request"){this.createStandardRequestChannel();}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_ERROR_INT,"AbstractConnection: HTTP request error on {0} channel, message={1}, error={2}:{3}",B,A,C.name,C.message);}};
SL4B_AbstractConnection.prototype._yieldThenProcessRttpMessageBlock = function(A){this.m_oResponseQueueStatistics.addMessageBatch(A.length);this._notifyResponseQueueStatisticsListeners("onBatchQueued");var oThis=this;
var nTimeoutId=setTimeout(function(){if(oThis.m_pPendingWork.length>0){var oWorkPackage=oThis.m_pPendingWork.shift();
oThis._processRttpMessageBlock(oWorkPackage.work);}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"*** worker expected messages to process : [{0}]",A.join(","));}},0,"SL4B-message-loop");
this.m_pPendingWork.push({timeoutId:nTimeoutId, work:A, toString:function(){return "{id: "+this.timeoutId+" messages=["+this.work.join(",")+"]}";
}});};
SL4B_AbstractConnection.prototype._processRttpMessageBlockImmediately = function(A){this.m_oResponseQueueStatistics.addMessageBatch(A.length);this._notifyResponseQueueStatisticsListeners("onBatchQueued");this._processRttpMessageBlock(A);};
SL4B_AbstractConnection.prototype.processRttpMessageBlock = function(A){if(SL4B_Accessor.getConfiguration().yieldBeforeMessageProcessing()){this.processRttpMessageBlock=this._yieldThenProcessRttpMessageBlock;}else 
{this.processRttpMessageBlock=this._processRttpMessageBlockImmediately;}this.processRttpMessageBlock(A);};
SL4B_AbstractConnection.prototype._clearAllPendingTimeouts = function(){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"SL4B_AbstractConnection._clearAllPendingTimeouts");while(this.m_pPendingWork.length>0){var workPackage=this.m_pPendingWork.shift();
clearTimeout(workPackage.timeoutId);}this.m_oResponseQueueStatistics.reset();};
SL4B_AbstractConnection.prototype._processRttpMessageBlock = function(A){if(this.m_oResponseQueueStatistics.getQueuedBatchCount()<1){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_ERROR_INT,"SL4B_AbstractConnection._processRttpMessageBlock: queued batch count is out of sync, initiating full reconnect ({0})",this.m_oResponseQueueStatistics);this.m_oRttpProvider.createConnectionLost("Queued batch count is out of sync",true);}else 
{this.m_oResponseQueueStatistics.setMessageBatchStarted();this._notifyResponseQueueStatisticsListeners("onBeforeBatchProcessed");for(var i=0,nLength=A.length;i<nLength;++i){SL4B_MethodInvocationProxy.invoke(this,"parseRttpMessage",[A[i]]);this.m_oResponseQueueStatistics.incrementProcessedMessageCount();}this.m_oResponseQueueStatistics.setMessageBatchEnded();this._notifyResponseQueueStatisticsListeners("onAfterBatchProcessed");this.messageBlockComplete();}};
SL4B_AbstractConnection.prototype.messageBlockComplete = function(){};
SL4B_AbstractConnection.prototype.addResponseQueueStatisticsListener = function(A){this.m_pResponseQueueStatisticsListeners.push(A);};
SL4B_AbstractConnection.prototype._notifyResponseQueueStatisticsListeners = function(A){for(var i=0,nLength=this.m_pResponseQueueStatisticsListeners.length;i<nLength;++i){var oListener=this.m_pResponseQueueStatisticsListeners[i];
SL4B_MethodInvocationProxy.invoke(oListener,A,[this.m_oResponseQueueStatistics]);}};
SL4B_AbstractConnection.prototype.processInvalidServerResponse = function(B,A){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"AbstractConnection.processInvalidServerResponse: Bad response code {0} received from Liberator. Response text was {1}",A,B);if(A==404){this.m_oRttpProvider.createConnectionLost("Resource not found response from Liberator: probable Session Timeout.",true);}else 
{this.m_oRttpProvider.createConnectionLost("Invalid response from Liberator, code "+A+", response '"+B+"'.",false);}};
SL4B_AbstractConnection.prototype._nonClockSyncMessage = function(A,B){A=this.m_oRttpMessage.setMessage(B,A);if(this.m_oRttpMessage.isMessageComplete()){if(this.m_oMessageReceiver!==null){SL4B_MethodInvocationProxy.invoke(this.m_oMessageReceiver,"receiveMessage",[this.m_oRttpMessage]);}if(this.m_oRttpMessage.m_sSequenceNumber!=null){this.m_sLastSequenceNumber=this.m_oRttpMessage.m_sSequenceNumber;}}};
SL4B_AbstractConnection.prototype.replaceFieldCodes = function(B){var oRttpProvider=SL4B_Accessor.getUnderlyingRttpProvider();
function SL_ST(A){var sCode=A.slice(0,A.length-1);
var sField=oRttpProvider.getFieldName(sCode);
return (sField) ? sField+"=" : A;
}
B=B.replace(/\w+=/g,SL_ST);return B;
};
function SL_PA(A){if(!this.m_bConnectionStopped){if(this.m_bDecodeFields===null){var bDecodeFields=SL4B_Accessor.getConfiguration().getDecodeFields();
this.setDecodeFields(bDecodeFields);}if(this.m_bDecodeFields)A=this.replaceFieldCodes(A);SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"AbstractConnection.parseRttpMessage({0})",A);var l_pMessageLines=A.split("\n");
for(var l_nLine=0,l_nSize=l_pMessageLines.length;l_nLine<l_nSize;++l_nLine){if(l_pMessageLines[l_nLine]!=""){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINER_INT,"< {0}",l_pMessageLines[l_nLine]);if(l_pMessageLines[l_nLine].substr(0,2)=="4r"){if(SL4B_Accessor.getConfiguration().isSuppressExceptions()){
try {SL4B_ConnectionProxy.getInstance().receiveSync(A);}catch(e){SL4B_Accessor.getExceptionHandler().processException(e);}
}else 
{SL4B_ConnectionProxy.getInstance().receiveSync(A);}}else 
if(l_pMessageLines[l_nLine].substr(0,2)=="7t"){var line=l_pMessageLines[l_nLine];
var words=line.split(" ");
for(var w=0;w<words.length;w++){var word=words[w];
if(word.indexOf("=")!=-1){var pair=word.split("=");
if(pair.length==2){if(pair[0]=="TimeStamp"){var l_sTimeStamp=pair[1];
SL4B_ConnectionProxy.getInstance().recordHeartbeatLatency(l_sTimeStamp);}}}}}else 
{if(SL4B_Accessor.getConfiguration().isSuppressExceptions()){
try {this._nonClockSyncMessage(l_nLine,l_pMessageLines);}catch(e){this.processInvalidServerResponse(A,200);SL4B_Accessor.getExceptionHandler().processException(e);}
}else 
{this._nonClockSyncMessage(l_nLine,l_pMessageLines);}}}}}}
function SL_QG(B,A){C_CallbackQueue.addCallback(new Array(SL4B_Accessor.getUnderlyingRttpProvider(),"notifyConnectionListeners",this.m_oRttpProvider.const_INFO_CONNECTION_EVENT,"Establishing "+B.replace(/^frm/,"").toLowerCase()+" channel (URL: "+A+")"));var l_sUniqueId=(new Date()).valueOf();
var l_sCommonDomain=SL4B_Accessor.getConfiguration().getCommonDomain();
if(l_sCommonDomain!=null){A+="&"+SL4B_JavaScriptRttpProviderConstants.const_DOMAIN_PARAMETER+"="+l_sCommonDomain;}A+="&"+SL4B_JavaScriptRttpProviderConstants.const_UNIQUEID_PARAMETER+"="+l_sUniqueId;A+="&"+SL4B_JavaScriptRttpProviderConstants.const_MAX_GET_LENGTH_PARAMETER+"="+SL4B_Accessor.getConfiguration().getMaxGetLength();C_LiberatorUrlCheck.getLiberatorUrlCheck(B).loadUrl(A,this.m_oRttpProvider.getJsContainerUrl(),this,this.m_bSkipURLCheck);this.m_bSkipURLCheck=false;return l_sUniqueId;
}
SL4B_AbstractConnection.prototype.liberatorUnavailable = function(){if(this.m_bConnectionStopped==false){this.m_bConnectionStopped=true;this.m_oConnectionManager.liberatorUnavailable();}};
SL4B_AbstractConnection.prototype.liberatorAvailable = function(){this.m_oConnectionManager.liberatorAvailable();};
function SL_LO(){var l_sUrlPrefix=this.m_oRttpProvider.getJsContainerUrl()+"/";
var l_sRequestFrameUrl=l_sUrlPrefix+SL4B_JavaScriptRttpProvider.const_REQUEST_FRAME_URL;
l_sRequestFrameUrl+="&"+SL4B_JavaScriptRttpProviderConstants.const_INIT_PARAMETER+"=true";this.m_sRequestUniqueId=this.createChannel(SL4B_JavaScriptRttpProvider.const_REQUEST_FRAME_ID,l_sRequestFrameUrl);}
function SL_IJ(){var l_sUrlPrefix=this.m_oRttpProvider.getJsContainerUrl()+"/";
var l_sResponseFrameUrl=l_sUrlPrefix+SL4B_JavaScriptRttpProvider.const_RESPONSE_FRAME_URL;
l_sResponseFrameUrl+="&"+SL4B_JavaScriptRttpProviderConstants.const_INIT_PARAMETER+"=true";this.m_sResponseUniqueId=this.createChannel(SL4B_JavaScriptRttpProvider.const_RESPONSE_FRAME_ID,l_sResponseFrameUrl);}
var SL4B_CacheEntry=function(){};
if(false){function SL4B_CacheEntry(){}
}SL4B_CacheEntry = function(A){this.CLASSNAME="SL4B_CacheEntry";this.m_sObjectKey=A;this.m_pFieldList=new Object();this.m_bAllFields=false;this.m_oObjectStatus=GF_CachedObjectStatus.const_SUBSCRIBING;this.m_nReferenceCount=0;};
SL4B_CacheEntry.prototype.addFields = SL4B_CacheEntry_AddFields;function SL4B_CacheEntry_AddFields(A){var l_sFieldsToRequest=null;
if(!this.m_bAllFields){if(A==""||A==null||typeof A=="undefined"){this.m_bAllFields=true;l_sFieldsToRequest="";}else 
{var l_pFields=A.split(SL4B_ObjectCache.const_FIELD_NAME_DELIMITER);
for(var l_nField=0,l_nLength=l_pFields.length;l_nField<l_nLength;++l_nField){if(typeof this.m_pFieldList[l_pFields[l_nField]]=="undefined"){this.m_pFieldList[l_pFields[l_nField]]=true;if(l_sFieldsToRequest==null){l_sFieldsToRequest=l_pFields[l_nField];}else 
{l_sFieldsToRequest+=SL4B_ObjectCache.const_FIELD_NAME_DELIMITER+l_pFields[l_nField];}}}}}return l_sFieldsToRequest;
}
SL4B_CacheEntry.prototype.setObjectStatus = function(B,A,C){this.m_oObjectStatus=new GF_CachedObjectStatus(B,A,C);};
SL4B_CacheEntry.prototype.getObjectStatus = function(){return this.m_oObjectStatus;
};
SL4B_CacheEntry.prototype.sendCurrentObjectStatus = function(C,A,B){C.objectStatusForListener(A,this.m_oObjectStatus.m_nType,this.m_oObjectStatus.m_nCode,this.m_oObjectStatus.m_sMessage,B);};
SL4B_CacheEntry.prototype.resetFields = function(){this.m_pFieldList=new Object();this.m_bAllFields=false;};
SL4B_CacheEntry.prototype.incrementReferenceCount = function(){this.m_nReferenceCount++;};
SL4B_CacheEntry.prototype.decrementReferenceCount = function(){this.m_nReferenceCount--;};
SL4B_CacheEntry.prototype.hasReferences = function(){return (this.m_nReferenceCount>0);
};
var GF_ClockSyncStrategy=function(){this.m_nClockOffset=null;};
if(false){function GF_ClockSyncStrategy(){}
}GF_ClockSyncStrategy.prototype.startClockSync = function(){};
GF_ClockSyncStrategy.prototype.stopClockSync = function(){};
GF_ClockSyncStrategy.prototype.getNextSyncMessage = function(A){};
GF_ClockSyncStrategy.prototype.receiveSync = function(A){};
GF_ClockSyncStrategy.prototype.getServerTime = function(A){if(this.m_nClockOffset==null){return null;
}return parseInt(A,10)+this.m_nClockOffset;
};
GF_ClockSyncStrategy.prototype.getClientTime = function(A){if(this.m_nClockOffset==null){return null;
}return A-this.m_nClockOffset;
};
GF_ClockSyncStrategy.prototype._$setClockOffset = function(A){this.m_nClockOffset=A;SL4B_Accessor.getStatistics().setClockOffset(this.m_nClockOffset);};
GF_ClockSyncStrategy.prototype._$getClockOffset = function(){return this.m_nClockOffset;
};
var GF_SlidingClockSyncStrategy=function(A){if(A==null){throw new SL4B_Exception("GF_SlidingClockSyncStrategy: oManagedConnection cannot be null.");
}this.m_oManagedConnection=A;this.m_pSyncTimesWindow=[];this.m_nIndex=0;this.m_bWindowFilled=false;var oConfiguration=SL4B_Accessor.getConfiguration();
this.m_nSlidingWindowSize=oConfiguration.getSlidingSyncSize();this.m_nSyncSpacingTime=oConfiguration.getSlidingSyncSpacing();this.m_nFastSyncSpacingTime=oConfiguration.getSlidingSyncInitialSpacing();this.m_nSyncTimeoutId=null;this.m_nT1=null;this.m_oBestTime=null;};
if(false){function GF_SlidingClockSyncStrategy(){}
}GF_SlidingClockSyncStrategy.prototype = new GF_ClockSyncStrategy();GF_SlidingClockSyncStrategy.prototype.startClockSync = function(){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ClockOffset = {0}",this._$getClockOffset());this._sendSyncPlaceHolder();};
GF_SlidingClockSyncStrategy.prototype.stopClockSync = function(){if(this.m_nSyncTimeoutId!=null){clearTimeout(this.m_nSyncTimeoutId);this.m_nSyncTimeoutId=null;}};
GF_SlidingClockSyncStrategy.prototype._sendSyncPlaceHolder = function(){this.m_oManagedConnection.sendMessage("SYNC",SL4B_ManagedConnection.NO_RESPONSE_EXPECTED_MESSAGE_RECEIVER);};
GF_SlidingClockSyncStrategy.prototype._getTime = function(){return new Date().getTime();
};
GF_SlidingClockSyncStrategy.prototype.getNextSyncMessage = function(A){var nLatencyToSend=Math.ceil(A);
if(isNaN(nLatencyToSend)){nLatencyToSend=-1;}var nClockOffset=Math.ceil(this._$getClockOffset());
if(nClockOffset===null){nClockOffset=0;}this.m_nT1=this._getTime();var sMessage="SYNC "+this.m_nT1+" "+nClockOffset+" "+nLatencyToSend;
return sMessage;
};
GF_SlidingClockSyncStrategy.prototype.receiveSync = function(A){var oCurrentRoundTrip=this._extractRoundTripFromMessage(A);
var nSpacing=this.m_nSyncSpacingTime;
if(this.m_bWindowFilled==false){nSpacing=this.m_nFastSyncSpacingTime;}var self=this;
var nTimeout=Math.max(nSpacing-oCurrentRoundTrip.roundTrip,0);
var nSyncTimeoutId=setTimeout(function(){self._sendSyncPlaceHolder();},nTimeout);
this.m_nSyncTimeoutId=nSyncTimeoutId;if(oCurrentRoundTrip!==null){if(oCurrentRoundTrip.roundTrip>=0){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"GF_SlidingClockSyncStrategy.receiveSync(): Sync received: Round trip time {0}",oCurrentRoundTrip.roundTrip);this._processSyncRoundTrip(oCurrentRoundTrip);}}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINE_INT,"GF_SlidingClockSyncStrategy.receiveSync(): Invalid sync message {0}",A);}};
GF_SlidingClockSyncStrategy.prototype._extractRoundTripFromMessage = function(A){var pTokens=A.split(" ");
if(pTokens.length==4){var nT4=SL4B_Accessor.getStatistics().getResponseQueueStatistics().getTimeOldestBatchWasQueued().getTime();
var nRoundTrip=nT4-this.m_nT1;
var nT3=parseInt(pTokens[2],10);
var nT2MinusT1=parseInt(pTokens[3],10);
var nClockOffset=(nT2MinusT1-(nT4-nT3))/2;
if(nRoundTrip<0){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"GF_SlidingClockSyncStrategy._extractRoundTripFromMessage(): incorrect round trip time {0} t1={1} t4={2}",nRoundTrip,this.m_nT1,nT4);}return {roundTrip:nRoundTrip, offset:nClockOffset};
}return null;
};
GF_SlidingClockSyncStrategy.prototype._isThisTheBestRoundTrip = function(A){return this.m_oBestTime==null||A.roundTrip<=this.m_oBestTime.roundTrip;
};
GF_SlidingClockSyncStrategy.prototype._areWeRemovingTheBestStoredRoundTrip = function(){return this.m_bWindowFilled&&this.m_pSyncTimesWindow[this.m_nIndex].roundTrip==this.m_oBestTime.roundTrip;
};
GF_SlidingClockSyncStrategy.prototype._findBestRemainingRoundTrip = function(A){var oBestRoundTrip=A;
for(var i=0;i<(this.m_nSlidingWindowSize-1);++i){var j=(this.m_nIndex+i+1)%this.m_nSlidingWindowSize;
if(this.m_pSyncTimesWindow[j].roundTrip<oBestRoundTrip.roundTrip){oBestRoundTrip=this.m_pSyncTimesWindow[j];}}return oBestRoundTrip;
};
GF_SlidingClockSyncStrategy.prototype._processSyncRoundTrip = function(A){if(this._isThisTheBestRoundTrip(A)){this.m_oBestTime=A;if(this.m_bWindowFilled==true){this._$setClockOffset(this.m_oBestTime.offset);}}else 
{if(this._areWeRemovingTheBestStoredRoundTrip()){this.m_oBestTime=this._findBestRemainingRoundTrip(A);this._$setClockOffset(this.m_oBestTime.offset);}}this.m_pSyncTimesWindow[this.m_nIndex]=A;this.m_nIndex=(this.m_nIndex+1)%this.m_nSlidingWindowSize;if(this.m_bWindowFilled===false&&this.m_nIndex==0){this.m_bWindowFilled=true;this._$setClockOffset(this.m_oBestTime.offset);}};
var GF_BatchingClockSyncStrategy=function(A){if(A==null){throw new SL4B_Exception("GF_BatchingClockSyncStrategy: oManagedConnection cannot be null.");
}this.m_oManagedConnection=A;this.m_nSyncTimeoutId=-1;this.m_nT1=0;this.m_nBestRoundTrip=9999999;this.m_nBestClockOffset=null;this.m_nBatchCount=0;};
if(false){function GF_BatchingClockSyncStrategy(){}
}GF_BatchingClockSyncStrategy.prototype = new GF_ClockSyncStrategy();GF_BatchingClockSyncStrategy.prototype.startClockSync = function(){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"GF_BatchingClockSyncStrategy.startClockSync()");this.m_nBatchCount=0;this.m_nBestRoundTrip=9999999;SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ClockOffset = {0}",this._$getClockOffset());this._sendSyncPlaceHolder();};
GF_BatchingClockSyncStrategy.prototype.stopClockSync = function(){if(this.m_nSyncTimeoutId!=-1){clearTimeout(this.m_nSyncTimeoutId);this.m_nSyncTimeoutId=-1;}};
GF_BatchingClockSyncStrategy.prototype._sendSyncPlaceHolder = function(){this.m_oManagedConnection.sendMessage("SYNC",SL4B_ManagedConnection.NO_RESPONSE_EXPECTED_MESSAGE_RECEIVER);};
GF_BatchingClockSyncStrategy.prototype._getTime = function(){return new Date().getTime();
};
GF_BatchingClockSyncStrategy.prototype.getNextSyncMessage = function(A){var nLatencyToSend=Math.ceil(A);
if(isNaN(nLatencyToSend)){nLatencyToSend=-1;}this.m_nBatchCount++;var l_nClockOffset=Math.ceil(this._$getClockOffset());
if(l_nClockOffset===null){l_nClockOffset=0;}this.m_nT1=this._getTime();var l_sMessage="SYNC "+this.m_nT1+" "+l_nClockOffset+" "+nLatencyToSend;
return l_sMessage;
};
GF_BatchingClockSyncStrategy.prototype.receiveSync = function(A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"GF_BatchingClockSyncStrategy.receiveSync({0})",A);var l_nT4=SL4B_Accessor.getStatistics().getResponseQueueStatistics().getTimeOldestBatchWasQueued().getTime();
var l_nRoundtrip=l_nT4-this.m_nT1;
if(l_nRoundtrip<this.m_nBestRoundTrip){this.m_nBestRoundTrip=l_nRoundtrip;var l_pTokens=A.split(" ");
if(l_pTokens.length==4){var l_nT3=parseInt(l_pTokens[2],10);
var l_nT2MinusT1=parseInt(l_pTokens[3],10);
this.m_nBestClockOffset=(l_nT2MinusT1-(l_nT4-l_nT3))/2;}}if(this.m_nBatchCount==SL4B_Accessor.getConfiguration().getClocksyncBatchSize()){this._$setClockOffset(this.m_nBestClockOffset);}var self=this;
if(this.m_nBatchCount<SL4B_Accessor.getConfiguration().getClocksyncBatchSize()){this.m_nSyncTimeoutId=setTimeout(function(){return self._sendSyncPlaceHolder();
},SL4B_Accessor.getConfiguration().getClocksyncSpacing());}else 
{this.m_nSyncTimeoutId=setTimeout(function(){return self.startClockSync();
},SL4B_Accessor.getConfiguration().getClocksyncPeriod());}};
var GF_SlidingStatisticsWindow=function(A){GF_SlidingWindow.call(this,A);};
if(false){function GF_SlidingStatisticsWindow(){}
}GF_SlidingStatisticsWindow.prototype = SL_BD(GF_SlidingWindow);GF_SlidingStatisticsWindow.prototype.clear = function(){GF_SlidingWindow.prototype.clear.call(this);this.m_nMin=NaN;this.m_nMax=NaN;this.m_nTotal=0;this.m_nSquaredTotal=0;};
GF_SlidingStatisticsWindow.prototype.changeWindow = function(B,A){GF_SlidingWindow.prototype.changeWindow.call(this,B,A);if(A==undefined){A=NaN;}this._updateMinMax(B,A);this._updateTotal(B,A);};
GF_SlidingStatisticsWindow.prototype._updateMinMaxBySearching = function(B){var min=B;
var max=B;
this.iterate(function(A){min=Math.min(min,A);max=Math.max(max,A);});this.m_nMin=min;this.m_nMax=max;};
GF_SlidingStatisticsWindow.prototype._updateMinMax = function(B,A){var minmaxsearch=false;
if((this.m_nMin<B)==false){this.m_nMin=B;}else 
if(this.m_nMin==A){minmaxsearch=true;}if((this.m_nMax>B)==false){this.m_nMax=B;}else 
if(this.m_nMax==A){minmaxsearch=true;}if(minmaxsearch==true){this._updateMinMaxBySearching(B);}};
GF_SlidingStatisticsWindow.prototype._updateTotal = function(B,A){var outgoingValueAsNumber=isNaN(A) ? 0 : A;
this.m_nTotal=this.m_nTotal-outgoingValueAsNumber+B;this.m_nSquaredTotal=this.m_nSquaredTotal-outgoingValueAsNumber*outgoingValueAsNumber+B*B;};
GF_SlidingStatisticsWindow.prototype.toString = function(){var result=["{ values=["];
result.push(this.m_pBuffer.join(","));result.push("] n=");result.push(this.getLength());result.push(" total=");result.push(this.getTotal());result.push(" mean=");result.push(this.getMean());result.push(" min=");result.push(this.m_nMin);result.push(" max=");result.push(this.m_nMax);result.push(" var=");result.push(this.getVariance());result.push(" std=");result.push(this.getStandardDeviation());result.push(" std%=");result.push(this.getStandardDeviationPercentage());result.push(" }");return result.join("");
};
GF_SlidingStatisticsWindow.prototype.getLength = function(){return this.m_bFilled ? this.m_nMaxsize : this.m_nNext;
};
GF_SlidingStatisticsWindow.prototype.getTotal = function(){return this.m_nTotal;
};
GF_SlidingStatisticsWindow.prototype.getMean = function(){return this.getTotal()/this.getLength();
};
GF_SlidingStatisticsWindow.prototype.getMin = function(){return this.m_nMin;
};
GF_SlidingStatisticsWindow.prototype.getMax = function(){return this.m_nMax;
};
GF_SlidingStatisticsWindow.prototype.getVariance = function(){var mean=this.getMean();
var n=this.getLength();
return mean*mean-(2*mean*this.m_nTotal-this.m_nSquaredTotal)/n;
};
GF_SlidingStatisticsWindow.prototype.getStandardDeviation = function(){return Math.sqrt(this.getVariance());
};
GF_SlidingStatisticsWindow.prototype.getStandardDeviationPercentage = function(){return Math.sqrt(this.getVariance())*100/this.getMean();
};
var GF_LatencyCalculator=function(A){this.m_oClockSyncStrategy=A;var nWindowSize=10;
var oConfiguration=SL4B_Accessor.getConfiguration();
if(oConfiguration.getClockSyncStrategy()=="GF_SlidingStatisticsWindow"){nWindowSize=oConfiguration.getSlidingSyncSize();}else 
{nWindowSize=Math.floor(oConfiguration.getClocksyncPeriod()/1000);}this.m_oStatistics=new GF_SlidingStatisticsWindow(nWindowSize);this.m_nLatency=-1;this.m_oHeartbeatStatistics=new GF_SlidingStatisticsWindow(nWindowSize);this.m_nHeartbeatLatency=-1;};
if(false){function GF_LatencyCalculator(){}
}GF_LatencyCalculator.prototype.getAverageLatency = function(){return this.m_oStatistics.getMean();
};
GF_LatencyCalculator.prototype.getLatency = function(){return this.m_nLatency;
};
GF_LatencyCalculator.prototype.getHeartbeatAverageLatency = function(){return this.m_oHeartbeatStatistics.getMean();
};
GF_LatencyCalculator.prototype.getHeartbeatLatency = function(){return this.m_nHeartbeatLatency;
};
GF_LatencyCalculator.prototype.recordLatency = function(A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ConnectionProxy.recordLatency({0})",A);var nComparableServerTimeStamp=this.m_oClockSyncStrategy.getClientTime(A);
if(nComparableServerTimeStamp!=null){var nBatchTime=SL4B_Accessor.getStatistics().getResponseQueueStatistics().getTimeOldestBatchWasQueued().getTime();
this.m_nLatency=Math.max(0,(nBatchTime-nComparableServerTimeStamp));this.m_oStatistics.add(this.m_nLatency);SL4B_Accessor.getStatistics().setLatency(this.m_nLatency);SL4B_Accessor.getStatistics().setAverageLatency(this.m_oStatistics.getMean());SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"Latency: {0}",this.m_nLatency);}};
GF_LatencyCalculator.prototype.recordHeartbeatLatency = function(A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ConnectionProxy.recordHeartbeatLatency({0})",A);var nComparableServerTimeStamp=this.m_oClockSyncStrategy.getClientTime(A);
if(nComparableServerTimeStamp!=null){var nBatchTime=SL4B_Accessor.getStatistics().getResponseQueueStatistics().getTimeOldestBatchWasQueued().getTime();
this.m_nHeartbeatLatency=Math.max(0,(nBatchTime-nComparableServerTimeStamp));this.m_oHeartbeatStatistics.add(this.m_nHeartbeatLatency);SL4B_Accessor.getStatistics().setHeartbeatLatency(this.m_nHeartbeatLatency);SL4B_Accessor.getStatistics().setHeartbeatAverageLatency(this.m_oHeartbeatStatistics.getMean());SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"Heartbeat Latency: {0}",this.m_nHeartbeatLatency);}};
var SL4B_ConnectionProxy=function(){};
if(false){function SL4B_ConnectionProxy(){}
}SL4B_ConnectionProxy = function(){this.CLASS_NAME="SL4B_ConnectionProxy";this.m_sConnectionState=SL4B_ConnectionProxy.const_CONNECTION_STATE_INITIALISING;this.m_sLastSentMessage=null;this.m_bRetryRequest=false;this.m_nRequestRetryAttempt=0;this.m_oConnection=null;this.m_oMessageReceiver=null;this.m_pResponseQueueStatisticsListeners=[];this.m_oClockSyncStrategy=null;this.m_oLatencyCalculator=null;this.m_bOldMethodCallWarningLogged=false;};
SL4B_ConnectionProxy.prototype = new SL4B_AbstractConnection;SL4B_ConnectionProxy.prototype.sendSync = function(A){this.m_oClockSyncStrategy.startClockSync();};
SL4B_ConnectionProxy.prototype.getNextSyncMessage = function(){return this.m_oClockSyncStrategy.getNextSyncMessage(this.m_oLatencyCalculator.getAverageLatency());
};
SL4B_ConnectionProxy.prototype.stopClockSync = function(){this.m_oClockSyncStrategy.stopClockSync();};
SL4B_ConnectionProxy.prototype.receiveSync = function(A){this.m_oClockSyncStrategy.receiveSync(A);};
SL4B_ConnectionProxy.prototype.recordLatency = function(A){this.m_oLatencyCalculator.recordLatency(A);};
SL4B_ConnectionProxy.prototype.recordHeartbeatLatency = function(A){this.m_oLatencyCalculator.recordHeartbeatLatency(A);};
SL4B_ConnectionProxy.const_CONNECTION_STATE_INITIALISING="initialising";SL4B_ConnectionProxy.const_CONNECTION_STATE_CONNECTING="connecting";SL4B_ConnectionProxy.const_CONNECTION_STATE_RECONNECTING="reconnecting";SL4B_ConnectionProxy.const_CONNECTION_STATE_CONNECTED="connected";SL4B_ConnectionProxy.const_CONNECTION_STATE_DISCONNECTED="disconnected";SL4B_ConnectionProxy.m_oInstance=new SL4B_ConnectionProxy();SL4B_ConnectionProxy.getInstance = function(){return SL4B_ConnectionProxy.m_oInstance;
};
SL4B_ConnectionProxy.prototype.initialise = function(){};
SL4B_ConnectionProxy.prototype.verifyConnection = function(){if(this.m_oConnection==null){throw new SL4B_Exception("A connection has not been set");
}};
SL4B_ConnectionProxy.prototype.getConnection = function(){return this.m_oConnection;
};
SL4B_ConnectionProxy.prototype.isDisconnected = function(){return (this.m_sConnectionState==SL4B_ConnectionProxy.const_CONNECTION_STATE_INITIALISING||this.m_sConnectionState==SL4B_ConnectionProxy.const_CONNECTION_STATE_DISCONNECTED);
};
SL4B_ConnectionProxy.prototype.connect = SL_BJ;SL4B_ConnectionProxy.prototype.send = SL_EU;SL4B_ConnectionProxy.prototype.setRequestHttpRequest = SL_PE;SL4B_ConnectionProxy.prototype.setResponseHttpRequest = SL_LX;SL4B_ConnectionProxy.prototype.start = SL_PH;SL4B_ConnectionProxy.prototype.stop = SL_CN;SL4B_ConnectionProxy.prototype.setMessageReceiver = SL_DN;SL4B_ConnectionProxy.prototype.setConnection = SL_MX;SL4B_ConnectionProxy.prototype.parseRttpMessage = SL_DG;SL4B_ConnectionProxy.prototype.getResponseUniqueId = SL_IA;SL4B_ConnectionProxy.prototype.getRequestUniqueId = SL_KQ;function SL_IA(){this.verifyConnection();return this.m_oConnection.getResponseUniqueId();
}
function SL_KQ(){this.verifyConnection();return this.m_oConnection.getRequestUniqueId();
}
SL4B_ConnectionProxy.prototype.getConnectionState = function(){return this.m_sConnectionState;
};
SL4B_ConnectionProxy.prototype.setConnectionState = function(A){this.m_sConnectionState=A;};
function SL_BJ(){SL4B_Logger.logConnectionMessage(false,"ConnectionProxy.connect()");this.verifyConnection();this.m_oConnection.connect();}
function SL_EU(A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ConnectionProxy.send({0})",A);this.verifyConnection();this.m_sLastSentMessage=A;this.m_bRetryRequest=false;this.m_oConnection.send(A);}
function SL_PE(B,A){
try {SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINER_INT,"ConnectionProxy.setRequestHttpRequest({0}) - {1}",A,this.getRequestUniqueId());if(A==this.getRequestUniqueId()){this.verifyConnection();this.m_oConnection.setRequestHttpRequest(B);if(this.m_bRetryRequest==true){this.m_nRequestRetryAttempt++;SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINER_INT,"Resending failed request {0}",this.m_sLastSentMessage);this.send(this.m_sLastSentMessage);}else 
{this.m_nRequestRetryAttempt=0;this.m_oConnection.m_oRttpProvider.requestHttpRequestReady();}}}catch(e){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINE_INT,"ConnectionProxy.setRequestHttpRequest: exception caught: {0}",e);}
}
function SL_LX(B,A){
try {SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINER_INT,"ConnectionProxy.setResponseHttpRequest({0}) - {1}",A,this.getResponseUniqueId());if(A==this.getResponseUniqueId()){this.verifyConnection();this.m_oConnection.setResponseHttpRequest(B);this.m_oConnection.m_oRttpProvider.responseHttpRequestReady();}}catch(e){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINE_INT,"ConnectionProxy.setResponseHttpRequest: exception caught: {0}",e);}
}
function SL_PH(){SL4B_Logger.logConnectionMessage(false,"ConnectionProxy.start()");this.verifyConnection();this.m_oConnection.start();}
function SL_CN(){this.verifyConnection();this.setConnectionState(SL4B_ConnectionProxy.const_CONNECTION_STATE_DISCONNECTED);this.m_oConnection.stop();this.stopClockSync();}
function SL_DN(A){if(A!==null&&typeof A.receiveMessage!="function"){throw new SL4B_Exception("Specified listener does not define the receiveMessage method");
}this.m_oMessageReceiver=A;if(this.m_oConnection!=null){this.m_oConnection.setMessageReceiver(A);}}
function SL_MX(A){if(this.m_oConnection!=null){this.m_oConnection.stop();this.stopClockSync();}SL4B_Logger.logConnectionMessage(false,"ConnectionProxy.setConnection: {0}",A);this.m_oConnection=A;this.m_oConnection.setMessageReceiver(this.m_oMessageReceiver);this._addExistingResponseQueueStatisticsListenerToNewConnection(A);var sClockSyncStrategyClass=SL4B_Accessor.getConfiguration().getClockSyncStrategy();
SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINE_INT,"ConnectionProxy.setConnection: Creating clock sync strategy {0}",sClockSyncStrategyClass);var ClockSyncStrategyClass=null;
var oException=null;

try {ClockSyncStrategyClass=eval(sClockSyncStrategyClass);}catch(e){oException=e;}
if(ClockSyncStrategyClass==null||typeof (ClockSyncStrategyClass)!="function"){SL4B_Logger.log(SL4B_DebugLevel.const_ERROR_INT,"ConnectionProxy.setConnection: Problem creating clock sync strategy {0} : {1}",sClockSyncStrategyClass,oException);SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"ConnectionProxy.setConnection: Using default clock sync strategy GF_BatchingClockSyncStrategy");ClockSyncStrategyClass=GF_BatchingClockSyncStrategy;}this.m_oClockSyncStrategy=new ClockSyncStrategyClass(A.m_oRttpProvider.getManagedConnection());this.m_oLatencyCalculator=new GF_LatencyCalculator(this.m_oClockSyncStrategy);}
function SL_DG(A){if(!this.m_bOldMethodCallWarningLogged){this.m_bOldMethodCallWarningLogged=true;SL4B_Logger.log(SL4B_DebugLevel.const_ERROR_INT,"ConnectionProxy.parseRttpMessage: old API method was invoked, SL4B version on the Liberator is out of date");}if(A!=''){this.processRttpMessageBlock(A.split("\n"));}}
SL4B_ConnectionProxy.prototype.httpRequestError = function(C,B,A){if(B=='request'){this.m_bRetryRequest=true;}this.verifyConnection();this.m_oConnection.httpRequestError(C,B,A);};
SL4B_ConnectionProxy.prototype.getResponseQueueStatistics = function(){this.verifyConnection();return this.m_oConnection.getResponseQueueStatistics();
};
SL4B_ConnectionProxy.prototype.addResponseQueueStatisticsListener = function(A){this.m_pResponseQueueStatisticsListeners.push(A);if(this.m_oConnection!==null){this.m_oConnection.addResponseQueueStatisticsListener(A);}};
SL4B_ConnectionProxy.prototype._addExistingResponseQueueStatisticsListenerToNewConnection = function(A){for(var i=0,nLength=this.m_pResponseQueueStatisticsListeners.length;i<nLength;++i){var oListener=this.m_pResponseQueueStatisticsListeners[i];
A.addResponseQueueStatisticsListener(oListener);}};
SL4B_ConnectionProxy.prototype.processInvalidServerResponse = function(B,A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_ERROR_INT,"ConnectionProxy.processInvalidServerResponse: Invalid server response in response to message {0}",this.m_sLastSentMessage);if(this.m_sConnectionState!=SL4B_ConnectionProxy.const_CONNECTION_STATE_DISCONNECTED){if(this.m_oConnection!=null){this.m_oConnection.processInvalidServerResponse(B,A);}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_ERROR_INT,"ConnectionProxy.processInvalidServerResponse: Invalid server response \"{0}\" received, but underlying connection is null",B);}}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_ERROR_INT,"ConnectionProxy.processInvalidServerResponse: Unexpected server response \"{0}\" received while disconnected.",B);}};
SL4B_ConnectionProxy.prototype.processRttpMessageBlock = function(A){if(A.length==1&&A[0]==""){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINER_INT,"ConnectionProxy.processRttpMessageBlock: Empty message block received.");}else 
{if(this.m_oConnection!=null){this.m_oConnection.processRttpMessageBlock(A);}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_ERROR_INT,"ConnectionProxy.processRttpMessageBlock: RTTP message block size \"{0}\" received, but underlying connection is null",A.length);}}};
var SL4B_ObjectCache=function(){};
if(false){function SL4B_ObjectCache(){}
}SL4B_ObjectCache = function(B,A){this.CLASS_NAME="SL4B_ObjectCache";this.m_oRttpProvider=B;this.m_oSubscriptionManager=A;A.setObjectCache(this);this.m_pObjects=new Object();this.m_pObjectDataCache=new Object();this.m_pObjectDataCacheType2=new Object();this.m_pObjectDataCacheType3=new Object();this.m_pObjectNumberToObjectKeyMap=new Object();this.m_pContainerObjectKeyToObjectNumberMap=new Object();this.m_pDirectoryCache=new Object();this.m_pStoryCache=new Object();this.m_mChatCache=new Object();this.m_bIsFireFox1_0=SL4B_Accessor.getBrowserAdapter().isFirefox()&&SL4B_Accessor.getBrowserAdapter().getBrowserVersion().indexOf("1.0")==0;this.m_nMaxRequestDiscardLength=800;this.m_pContainerCache=new Object();this.m_pNewsHeadlineCache=new Object();this.m_pPermissionCache=new Object();this.m_oSnapshotSet=new Object();};
SL4B_ObjectCache.const_FIELD_NAME_DELIMITER=",";SL4B_ObjectCache.const_OBJECT_NAME_AND_FIELD_LIST_DELIMITER=";";SL4B_ObjectCache.const_BLANK_STRING="";SL4B_ObjectCache.SUBJECT_DELETED="deleted";SL4B_ObjectCache.SUBJECT_ADDED="added";SL4B_ObjectCache.prototype.receiveMessage = SL_QS;SL4B_ObjectCache.prototype.processRttpMessage = SL4B_ObjectCache.prototype.receiveMessage;SL4B_ObjectCache.prototype.requestObject = SL_EH;SL4B_ObjectCache.prototype.sendCachedFields = SL_DX;SL4B_ObjectCache.prototype.requestObjects = SL_EN;SL4B_ObjectCache.prototype.discardObject = SL_HU;SL4B_ObjectCache.prototype.discardObjects = SL_OP;SL4B_ObjectCache.prototype.cacheObject = SL_JM;SL4B_ObjectCache.prototype.cacheType1Record = SL_DR;SL4B_ObjectCache.prototype.cacheType2Record = SL_OZ;SL4B_ObjectCache.prototype.cacheType3Record = SL_PT;SL4B_ObjectCache.prototype.deleteType2RecordLevel = SL_SE;SL4B_ObjectCache.prototype.type2Clear = SL_FB;SL4B_ObjectCache.prototype.type3Clear = SL_PZ;SL4B_ObjectCache.prototype.notFound = SL_EE;SL4B_ObjectCache.prototype.cacheDirectory = SL_AS;SL4B_ObjectCache.prototype.statusUpdated = SL_QQ;SL4B_ObjectCache.prototype.sendObjectAction = SL_QC;SL4B_ObjectCache.prototype.createObjectArray = SL_EI;SL4B_ObjectCache.prototype.cacheContainer = SL_SD;SL4B_ObjectCache.prototype.cacheNewsHeadline = SL_DS;SL4B_ObjectCache.prototype.cacheNewsStory = SL_RB;SL4B_ObjectCache.prototype.cachePermission = SL_OV;SL4B_ObjectCache.prototype.clearPermission = SL_MD;SL4B_ObjectCache.prototype.deletePermissionEntry = SL_CF;SL4B_ObjectCache.prototype.parseOrderChanges = SL_JL;SL4B_ObjectCache.prototype.isActive = SL_DW;SL4B_ObjectCache.prototype.clearContainer = SL_RA;SL4B_ObjectCache.prototype.clear = function(){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectCache.clear()");this.m_pObjects=new Object();this.m_pObjectDataCache=new Object();this.m_pObjectDataCacheType2=new Object();this.m_pObjectDataCacheType3=new Object();this.m_pObjectNumberToObjectKeyMap=new Object();this.m_pContainerObjectKeyToObjectNumberMap=new Object();this.m_pDirectoryCache=new Object();this.m_pStoryCache=new Object();this.m_pNewsHeadlineCache=new Object();this.m_pContainerCache=new Object();this.m_pPermissionCache=new Object();this.m_mChatCache=new Object();};
function SL_QS(C,A,B){var l_nRttpCode=C.getRttpCode();
switch(l_nRttpCode){
case SL4B_RttpCodes.const_RECORD_RESP:case SL4B_RttpCodes.const_RESP_UNKNOWN:case SL4B_RttpCodes.const_REC1_UPD:case SL4B_RttpCodes.const_REC1_IMG:case SL4B_RttpCodes.const_REC2_UPD:case SL4B_RttpCodes.const_REC2_IMG:case SL4B_RttpCodes.const_REC2_CLR:case SL4B_RttpCodes.const_REC2_DEL:case SL4B_RttpCodes.const_REC3_UPD:case SL4B_RttpCodes.const_REC3_IMG:case SL4B_RttpCodes.const_REC3_CLR:case SL4B_RttpCodes.const_REC3_DEL:case SL4B_RttpCodes.const_DIRECTORY_RESP:case SL4B_RttpCodes.const_DIR_UPD:case SL4B_RttpCodes.const_CONT_RESP:case SL4B_RttpCodes.const_AUTODIR_RESP:case SL4B_RttpCodes.const_CONT_UPD:case SL4B_RttpCodes.const_AUTODIR_UPD:case SL4B_RttpCodes.const_CONT_IMG:case SL4B_RttpCodes.const_AUTODIR_IMG:case SL4B_RttpCodes.const_NEWS_RESP:case SL4B_RttpCodes.const_NEWS_IMG:case SL4B_RttpCodes.const_NEWS_UPD:case SL4B_RttpCodes.const_STORY_RESP:case SL4B_RttpCodes.const_STORY_UPD:case SL4B_RttpCodes.const_STORY_IMG:case SL4B_RttpCodes.const_PERM_RESP:case SL4B_RttpCodes.const_PERM_IMG:case SL4B_RttpCodes.const_PERM_UPD:case SL4B_RttpCodes.const_PERM_CLR:case SL4B_RttpCodes.const_PERM_DEL:case SL4B_RttpCodes.const_CHAT_RESP:case SL4B_RttpCodes.const_CHAT_UPD:{this.cacheObject(C,A,B);break;
}case SL4B_RttpCodes.const_STATUS_OK:case SL4B_RttpCodes.const_STATUS_STALE:case SL4B_RttpCodes.const_STATUS_LIMITED:case SL4B_RttpCodes.const_STATUS_REMOVED:case SL4B_RttpCodes.const_STATUS_INFO:{this.statusUpdated(C);break;
}case SL4B_RttpCodes.const_NEWS_CLR:{var sObjKey=this.m_pObjectNumberToObjectKeyMap[C.getObjectNumber()];
delete this.m_pNewsHeadlineCache[sObjKey];break;
}case SL4B_RttpCodes.const_DISCARD_OK:{this._discarded(C,A,B);break;
}default :this.notFound(C,A);}}
SL4B_ObjectCache.prototype._discarded = function(B,A,C){this.m_oSubscriptionManager._discarded();};
SL4B_ObjectCache.prototype.sendSubscriptionMessage = function(A,B){this.m_oRttpProvider.getManagedConnection().sendMessage(A,this,B);};
SL4B_ObjectCache.prototype._createObjectKey = function(A,B){return A+B;
};
SL4B_ObjectCache.prototype._extractObjectKeyFromSentMessage = function(A){if(A===null||A.getContext()==null){throw new SL4B_Exception("SL4B_ObjectCache._extractObjectKeyFromSentMessage: context could not be extracted from the specified request ("+oRequest+")");
}var sEncodedObjectName=A.getMessage().match(/^[A-Z]+ ([^;]+)/)[1];
return GF_ResponseDecoder.decodeRttpData(sEncodedObjectName)+A.getContext().filter;
};
function SL_EH(E,A,C,B,D){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectCache.requestObject({0}, {1}, {2}, {3})",E,A,C,D);var l_sObjectKey=this._createObjectKey(E,A);
this._addCacheEntryIfNotPresent(l_sObjectKey);if(D){this.m_oSnapshotSet[l_sObjectKey]=l_sObjectKey;}var l_sFieldsToRequest=this.m_pObjects[l_sObjectKey].addFields(C);
if(l_sFieldsToRequest!=null){var l_sFieldMessage=SL4B_ObjectCache.createFilterAndFieldList(A,C);
var l_sCommand="REQUEST";
if(D!=undefined&&D==true){l_sCommand="SNAPSHOT";}this.sendObjectAction(l_sCommand,this.createObjectArray(GF_RequestEncoder.encodeRttpData(E),l_sFieldMessage),[E],A);}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"\tnothing to request, send cached update");this.sendCachedFields(E,A,C,B);}}
SL4B_ObjectCache.prototype._addCacheEntryIfNotPresent = function(A){var oCacheEntry=this.m_pObjects[A];
if(oCacheEntry===undefined){oCacheEntry=new SL4B_CacheEntry(A);this.m_pObjects[A]=oCacheEntry;}oCacheEntry.incrementReferenceCount();};
SL4B_ObjectCache.prototype._removeCacheEntryIfNoMoreReferences = function(A,B){var oCacheEntry=this.m_pObjects[A];
if(oCacheEntry!==undefined){oCacheEntry.decrementReferenceCount();if(!oCacheEntry.hasReferences()){delete this.m_pObjects[A];}else 
if(B){oCacheEntry.resetFields();}}};
SL4B_ObjectCache.prototype.removeObjectFromCacheIfNoMoreReferences = function(A,B){var sObjectKey=this._createObjectKey(A,B);
this._removeCacheEntryIfNoMoreReferences(sObjectKey,false);};
SL4B_ObjectCache.prototype._getCacheEntryForObjectKey = function(A){return this.m_pObjects[A];
};
SL4B_ObjectCache.buildRTTPRequestMessage = function(C,B,A){var l_sObjectRequestMessage="REQUEST "+GF_RequestEncoder.encodeRttpData(C);
l_sObjectRequestMessage+=SL4B_ObjectCache.createFilterAndFieldList(B,A);return l_sObjectRequestMessage;
};
SL4B_ObjectCache.createFilterAndFieldList = function(B,A){var l_sFilterAndFieldList="";
var l_bFilterSet=false;
if(typeof B=="string"&&B!=""){l_sFilterAndFieldList+=SL4B_ObjectCache.const_OBJECT_NAME_AND_FIELD_LIST_DELIMITER;l_sFilterAndFieldList+=GF_RequestEncoder.encodeFieldList(B,SL4B_ObjectCache.const_FIELD_NAME_DELIMITER);l_bFilterSet=true;}if(typeof A=="string"&&A!=""){if(l_bFilterSet){l_sFilterAndFieldList+=SL4B_ObjectCache.const_FIELD_NAME_DELIMITER;}else 
{l_sFilterAndFieldList+=SL4B_ObjectCache.const_OBJECT_NAME_AND_FIELD_LIST_DELIMITER;}l_sFilterAndFieldList+=GF_RequestEncoder.encodeFieldList(A,SL4B_ObjectCache.const_FIELD_NAME_DELIMITER);}return l_sFilterAndFieldList;
};
function SL_DX(D,A,C,B){var l_sObjectKey=this._createObjectKey(D,A);
var l_oCacheEntry=this.m_pObjects[l_sObjectKey];
if(l_oCacheEntry!==undefined&&l_oCacheEntry!=null){var l_pCacheFieldList=l_oCacheEntry.m_pFieldList;
var l_bCacheAllFields=l_oCacheEntry.m_bAllFields;
var l_bAllFields=false;
if(C==""||C==null||C===undefined){l_bAllFields=true;}if(this.m_pObjectDataCache[l_sObjectKey]!==undefined){var l_oType1RecordCache=this.m_pObjectDataCache[l_sObjectKey];
var l_oRecordFieldData=new SL4B_RecordFieldData();
if(l_bAllFields){for(l_sField in l_oType1RecordCache.m_pFieldCache){var l_sValue=l_oType1RecordCache.m_pFieldCache[l_sField];
l_oRecordFieldData.add(l_sField,l_sValue);}}else 
{var l_pFields=C.split(SL4B_ObjectCache.const_FIELD_NAME_DELIMITER);
for(var l_nField=0,l_nLength=l_pFields.length;l_nField<l_nLength;++l_nField){var l_sField=l_pFields[l_nField];
if(l_bCacheAllFields||l_pCacheFieldList[l_sField]!==undefined){var l_sValue=l_oType1RecordCache.m_pFieldCache[l_sField];
if(l_sValue){l_oRecordFieldData.add(l_sField,l_sValue);}}}}if(l_oRecordFieldData.size()>0){this.m_oSubscriptionManager.recordMultiUpdated(l_sObjectKey,l_oRecordFieldData,true,B);}}if(this.m_pObjectDataCacheType2[l_sObjectKey]!==undefined){var l_oType2RecordCache=this.m_pObjectDataCacheType2[l_sObjectKey];
var l_sLevelField=l_oType2RecordCache.m_sIndexFieldName;
for(l_sLevel in l_oType2RecordCache.m_pLevelCache){var l_oRecordFieldData=new SL4B_RecordFieldData();
var l_pNameValues=l_oType2RecordCache.m_pLevelCache[l_sLevel];
var l_sLevelValue=l_sLevel;
l_oRecordFieldData.add(l_sLevelField,l_sLevelValue);if(l_bAllFields){for(l_sField in l_pNameValues){var l_sValue=l_pNameValues[l_sField];
l_oRecordFieldData.add(l_sField,l_sValue);}}else 
{var l_pFields=C.split(SL4B_ObjectCache.const_FIELD_NAME_DELIMITER);
for(var l_nField=0,l_nLength=l_pFields.length;l_nField<l_nLength;++l_nField){var l_sField=l_pFields[l_nField];
if(l_bCacheAllFields||l_pNameValues[l_sField]!==undefined){var l_sValue=l_pNameValues[l_sField];
l_oRecordFieldData.add(l_sField,l_sValue);}}}if(l_oRecordFieldData.size()>0){this.m_oSubscriptionManager.recordMultiUpdated(l_sObjectKey,l_oRecordFieldData,true,B);}}}if(this.m_pObjectDataCacheType3[l_sObjectKey]!==undefined){var l_oType3RecordCache=this.m_pObjectDataCacheType3[l_sObjectKey];
for(var l_nLevel=0;l_nLevel<l_oType3RecordCache.m_pLevelCache.length;l_nLevel++){var l_oRecordFieldData=new SL4B_RecordFieldData();
var l_pNameValues=l_oType3RecordCache.m_pLevelCache[l_nLevel];
if(l_bAllFields){for(l_sField in l_pNameValues){var l_sValue=l_pNameValues[l_sField];
l_oRecordFieldData.add(l_sField,l_sValue);}}else 
{var l_pFields=C.split(SL4B_ObjectCache.const_FIELD_NAME_DELIMITER);
for(var l_nField=0,l_nLength=l_pFields.length;l_nField<l_nLength;++l_nField){var l_sField=l_pFields[l_nField];
if(l_bCacheAllFields||l_pNameValues[l_sField]!==undefined){var l_sValue=l_pNameValues[l_sField];
l_oRecordFieldData.add(l_sField,l_sValue);}}}if(l_oRecordFieldData.size()>0){this.m_oSubscriptionManager.recordMultiUpdated(l_sObjectKey,l_oRecordFieldData,true,B);}}}if(this.m_pDirectoryCache[l_sObjectKey]!==undefined){var l_oDirCache=this.m_pDirectoryCache[l_sObjectKey];
var l_pListing=l_oDirCache.m_pListing;
var l_pDirectoryChanges=new Array();
for(l_sName in l_pListing){var l_sType=l_pListing[l_sName];
this.m_oSubscriptionManager.dirUpdated(l_sObjectKey,l_sName,l_sType,true,B);l_pDirectoryChanges.push(new SL4B_DirectoryStructureChange(l_sName,l_sType,true));}this.m_oSubscriptionManager.dirMultiUpdated(l_sObjectKey,l_pDirectoryChanges);}if(this.m_pContainerCache[l_sObjectKey]!==undefined){var l_oContCache=this.m_pContainerCache[l_sObjectKey];
if(l_oContCache.isDirectory()){var l_oObjectNameToObjectTypeMap=l_oContCache.getObjectNameToObjectTypeMap();
var l_pStructureChanges=new Array();
for(l_sName in l_oObjectNameToObjectTypeMap){var l_nObjectNumber=l_oContCache.getObjectNumber(l_sName);
this.m_oSubscriptionManager.structureChange(l_sObjectKey,l_sName,l_oObjectNameToObjectTypeMap[l_sName],true,B,l_nObjectNumber);l_pStructureChanges.push(new SL4B_ContainerStructureChange(l_sName,l_oObjectNameToObjectTypeMap[l_sName],true));}var l_pOrderChanges=new Array();
var l_oOrdering=l_oContCache.m_oOrdering;
for(l_sSubscriptionId in l_oOrdering){l_pOrderChanges.push(new SL4B_ContainerOrderChange(this.m_pObjectNumberToObjectKeyMap[l_sSubscriptionId],l_sSubscriptionId,l_oOrdering[l_sSubscriptionId]));}var l_sProxyListenerId=this.m_oSubscriptionManager.getProxySubscriber(B,l_sObjectKey,"");
if(l_sProxyListenerId!=null){for(l_sName in l_oObjectNameToObjectTypeMap){l_nObjectNumber=l_oContCache.getObjectNumber(l_sName);l_sName=l_nObjectNumber+":"+l_sName;this.sendCachedFields(l_sName,"","",l_sProxyListenerId);}}}}if(this.m_pNewsHeadlineCache[l_sObjectKey]!==undefined){var l_oNewsHeadlineCache=this.m_pNewsHeadlineCache[l_sObjectKey];
var l_nNumberOfHeadlines=l_oNewsHeadlineCache.getSize();
this.m_oSubscriptionManager.objectInfo(l_sObjectKey,SL4B_ObjectType.NEWS_HEADLINE,SL4B_NewsHeadlineCache.const_SIZE_FIELD,l_oNewsHeadlineCache.getSize()+"");for(var l_nHeadline=0;l_nHeadline<l_nNumberOfHeadlines;++l_nHeadline){var l_oHeadline=l_oNewsHeadlineCache.getHeadline(l_nHeadline);
this.m_oSubscriptionManager.newsUpdated(l_sObjectKey,l_oHeadline.m_sStoryCode,l_oHeadline.m_sHeadline,l_oHeadline.m_sDate);}}if(this.m_pStoryCache[l_sObjectKey]!==undefined){var l_oNewsStoryCache=this.m_pStoryCache[l_sObjectKey];
var l_aStoryText=l_oNewsStoryCache.getTextLines();
this.m_oSubscriptionManager.storyUpdated(l_sObjectKey,l_aStoryText);}if(this.m_pPermissionCache[l_sObjectKey]!==undefined){var l_oPermissionCache=this.m_pPermissionCache[l_sObjectKey];
for(l_sKey in l_oPermissionCache.m_pLevelCache){var l_oFieldData=new SL4B_RecordFieldData();
if(l_bAllFields){for(l_sField in l_pNameValues){var l_sValue=l_pNameValues[l_sField];
l_oFieldData.add(l_sField,l_sValue);}}else 
{var l_pFields=C.split(SL4B_ObjectCache.const_FIELD_NAME_DELIMITER);
for(var l_nField=0,l_nLength=l_pFields.length;l_nField<l_nLength;++l_nField){var l_sField=l_pFields[l_nField];
if(l_bCacheAllFields||l_pNameValues[l_sField]!==undefined){var l_sValue=l_pNameValues[l_sField];
l_oFieldData.add(l_sField,l_sValue);}}}if(l_oFieldData.size()>0){this.m_oSubscriptionManager.permissionUpdated(l_sObjectKey,l_sKey,l_oFieldData,B);}}}if(this.m_mChatCache[l_sObjectKey]!==undefined){this.m_mChatCache[l_sObjectKey].sendCachedData(this,B);}if(B!=null){this.m_pObjects[l_sObjectKey].sendCurrentObjectStatus(this.m_oSubscriptionManager,l_sObjectKey,B);}}}
function SL_EI(A,B){var l_pObjectsToReturn=new Array();
if(A!=undefined&&A.length>0){var l_pObjects=A.split(SL4B_AbstractRttpProvider.const_OBJECT_NAME_DELIMITER);
if(SL4B_Accessor.getCapabilities().getRttpVersion()>=2.1){if(l_pObjects.length==1){l_pObjectsToReturn.push(l_pObjects[0]+B);}else 
{if(B!=undefined&&B.length>0){l_pObjectsToReturn.push(B);}for(var l_nObject=0,l_nLength=l_pObjects.length;l_nObject<l_nLength;l_nObject++){l_pObjectsToReturn.push(l_pObjects[l_nObject]);}}}else 
{for(var l_nObject=0,l_nLength=l_pObjects.length;l_nObject<l_nLength;l_nObject++){l_pObjectsToReturn.push(l_pObjects[l_nObject]+B);}}}return l_pObjectsToReturn;
}
function SL_QC(C,B,A,D){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectCache.sendObjectAction({0}, {1})",C,B);if(B.length==0){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINE_INT,"ObjectCache.sendObjectAction. Not performing {0} action as there are no subjects to send to Liberator, not sending any messages.",C);return;
}var l_sObjectRequestMessage=C+" "+B.join(" ");
this.sendSubscriptionMessage(l_sObjectRequestMessage,{filter:D});}
function SL_EN(C,A,D,B){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectCache.requestObjects({0}, {1}, {2})",C,A,D);var l_pObjects=C.split(SL4B_AbstractRttpProvider.const_OBJECT_NAME_DELIMITER);
var l_pObjectsToRequest=new Array();
var l_sObjectRequestList="";
for(var l_nObject=0,l_nLength=l_pObjects.length;l_nObject<l_nLength;++l_nObject){var l_sObjectName=l_pObjects[l_nObject];
var l_sObjectKey=this._createObjectKey(l_sObjectName,A);
this._addCacheEntryIfNotPresent(l_sObjectKey);if(this.m_pObjects[l_sObjectKey].addFields(D)!=null){l_pObjectsToRequest.push(l_sObjectName);l_sObjectRequestList+=((l_sObjectRequestList=="") ? "" : SL4B_AbstractRttpProvider.const_OBJECT_NAME_DELIMITER)+GF_RequestEncoder.encodeRttpData(l_sObjectName);}else 
{this.sendCachedFields(l_sObjectName,A,D,B);}}if(l_pObjectsToRequest.length>0){var l_sFieldMessage=SL4B_ObjectCache.createFilterAndFieldList(A,D);
this.sendObjectAction("REQUEST",this.createObjectArray(l_sObjectRequestList,l_sFieldMessage),l_pObjectsToRequest,A);}}
function SL_HU(B,A){var l_sSendFilter=A;
if(l_sSendFilter!=""){l_sSendFilter=SL4B_ObjectCache.const_OBJECT_NAME_AND_FIELD_LIST_DELIMITER+GF_RequestEncoder.encodeFieldList(l_sSendFilter,SL4B_ObjectCache.const_FIELD_NAME_DELIMITER);}this.sendObjectAction("DISCARD",this.createObjectArray(GF_RequestEncoder.encodeRttpData(B),l_sSendFilter),[B],A);var l_sObjectKey=this._createObjectKey(B,A);
this.clearCacheForObjectKey(l_sObjectKey);}
function SL_OP(A,B){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectCache.discardObjects({0}, {1})",A,B);var l_sSendFilter=B;
if(l_sSendFilter!=""){l_sSendFilter=SL4B_ObjectCache.const_OBJECT_NAME_AND_FIELD_LIST_DELIMITER+GF_RequestEncoder.encodeFieldList(l_sSendFilter,SL4B_ObjectCache.const_FIELD_NAME_DELIMITER);}var l_pObjects=A.split(SL4B_AbstractRttpProvider.const_OBJECT_NAME_DELIMITER);
this.sendObjectAction("DISCARD",this.createObjectArray(A,l_sSendFilter),l_pObjects,B);for(var l_nObject=0,l_nLength=l_pObjects.length;l_nObject<l_nLength;++l_nObject){var l_sObjectName=l_pObjects[l_nObject];
var l_sObjectKey=this._createObjectKey(l_sObjectName,B);
this.clearCacheForObjectKey(l_sObjectKey);}}
function SL_EE(A,B){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectCache.notFound: {0}",A);var l_sObjectKey=null;
var l_sObjectNumber=A.getObjectNumber();
if(l_sObjectNumber!==null){l_sObjectKey=(l_sObjectNumber ? this.m_pObjectNumberToObjectKeyMap[l_sObjectNumber] : A.getMessageContent().split(" ")[0]);}if(B!==null&&l_sObjectKey==null){l_sObjectKey=this._extractObjectKeyFromSentMessage(B);}this.clearCacheForObjectKey(l_sObjectKey);this.m_oSubscriptionManager.clearAllSubscriptions(l_sObjectKey,A.getRttpCode());}
SL4B_ObjectCache.prototype.clearCacheForObjectKey = function(A){if(A!==undefined){this._removeCacheEntryIfNoMoreReferences(A,true);if(this.m_pObjectDataCache[A]!==undefined){delete this.m_pObjectDataCache[A];}if(this.m_pObjectDataCacheType2[A]!==undefined){delete this.m_pObjectDataCacheType2[A];}if(this.m_pObjectDataCacheType3[A]!==undefined){delete this.m_pObjectDataCacheType3[A];}if(this.m_pDirectoryCache[A]!==undefined){delete this.m_pDirectoryCache[A];}if(this.m_pContainerCache[A]!==undefined){delete this.m_pContainerCache[A];}if(this.m_pNewsHeadlineCache[A]!==undefined){delete this.m_pNewsHeadlineCache[A];}if(this.m_pStoryCache[A]!==undefined){delete this.m_pStoryCache[A];}if(this.m_pPermissionCache[A]!==undefined){delete this.m_pPermissionCache[A];}if(this.m_mChatCache[A]!==undefined){delete this.m_mChatCache[A];}}};
SL4B_ObjectCache.prototype.validateResponseMatchesExpectation = function(A,B){if(SL4B_RttpCodes.isResponseCode(A.getRttpCode())){var sReceivedObjectName=A.getMessageContent().match(/([^ ]+)/)[1];
var sSentObjectName=B.getMessage().match(/ ([^;]+)/)[1];
if(sReceivedObjectName!==sSentObjectName&&GF_ResponseDecoder.decodeRttpData(sReceivedObjectName)!==GF_ResponseDecoder.decodeRttpData(sSentObjectName)){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_ERROR_INT,"ObjectCache.validateResponseMatchesExpectation: Received a message for {0} when a message for {1} was expected.",sReceivedObjectName,sSentObjectName);return false;
}}return true;
};
function SL_DW(A,B,C){switch(A){
case SL4B_RttpCodes.const_RESP_UNKNOWN:case SL4B_RttpCodes.const_DIRECTORY_RESP:case SL4B_RttpCodes.const_CONT_UPD:case SL4B_RttpCodes.const_CONT_IMG:case SL4B_RttpCodes.const_CONT_RESP:case SL4B_RttpCodes.const_AUTODIR_RESP:case SL4B_RttpCodes.const_AUTODIR_UPD:case SL4B_RttpCodes.const_AUTODIR_IMG:{return true;
}default :{if(C!=null){var mParameters=C.getParameters();
if(mParameters.ctrid!==undefined){oUserRequestData=this.m_oSubscriptionManager.getContainerRequestData(mParameters.ctrid);if(oUserRequestData!==undefined&&(mParameters.ctrstart!=oUserRequestData.getWindowStart()||mParameters.ctrend!=oUserRequestData.getWindowEnd())){var oContainerRequest=C;
if(C.getOriginalMessage()){oContainerRequest=C.getOriginalMessage();}var l_sContainerObjectKey=this._extractObjectKeyFromSentMessage(oContainerRequest);
if(B&&l_sContainerObjectKey&&this.m_pContainerCache[l_sContainerObjectKey]){var l_nObjPosition=this.m_pContainerCache[l_sContainerObjectKey].getOrdering(B);
if(l_nObjPosition>=oUserRequestData.getWindowStart()&&l_nObjPosition<=oUserRequestData.getWindowEnd()){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINER_INT,"ObjectCache.cacheObject: Item {0} is from an old request, but it is inside the current window {1} - {2}: processing updates",l_nObjPosition,oUserRequestData.getWindowStart(),oUserRequestData.getWindowEnd());}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINER_INT,"ObjectCache.cacheObject: Item {0} is outside the current window {1} - {2}: will not process non container updates",l_nObjPosition,oUserRequestData.getWindowStart(),oUserRequestData.getWindowEnd());return false;
}}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINER_INT,"ObjectCache.cacheObject: windows don't match - not processing non container updates: expected window start = {0} end = {1} this update for window start = {2}, end = {3}",oUserRequestData.getWindowStart(),oUserRequestData.getWindowEnd(),mParameters.ctrstart,mParameters.ctrend);return false;
}}}}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ObjectCache.cacheObject: no request record stored for message {0}",B);}}}return true;
}
function SL_JM(C,A,B){var oNormalisedRttpMessage=C;
if(this.validateResponseMatchesExpectation(oNormalisedRttpMessage,A)==false){this.m_oRttpProvider.getManagedConnection()._triggerReconnect("incorrect response");return;
}var oUserRequestData;
var l_nRttpCode=oNormalisedRttpMessage.getRttpCode();
var l_sObjectNumber=oNormalisedRttpMessage.getObjectNumber();
var bProcessUpdates=this.isActive(l_nRttpCode,l_sObjectNumber,A);
if(bProcessUpdates==false){return;
}switch(l_nRttpCode){
case SL4B_RttpCodes.const_RECORD_RESP:case SL4B_RttpCodes.const_RESP_UNKNOWN:case SL4B_RttpCodes.const_DIRECTORY_RESP:case SL4B_RttpCodes.const_CONT_RESP:case SL4B_RttpCodes.const_AUTODIR_RESP:case SL4B_RttpCodes.const_NEWS_RESP:case SL4B_RttpCodes.const_STORY_RESP:case SL4B_RttpCodes.const_PERM_RESP:case SL4B_RttpCodes.const_CHAT_RESP:{var l_sObjectKey=this._extractObjectKeyFromSentMessage(A);
this.m_pObjectNumberToObjectKeyMap[l_sObjectNumber]=l_sObjectKey;if(l_nRttpCode==SL4B_RttpCodes.const_RESP_UNKNOWN&&l_sObjectKey.indexOf("ctrid=")!=-1){if(this.m_pContainerObjectKeyToObjectNumberMap[l_sObjectKey]){this.m_oRttpProvider.getManagedConnection()._triggerReconnect("duplicate container response",true);return;
}this.m_pContainerObjectKeyToObjectNumberMap[l_sObjectKey]=l_sObjectNumber;}break;
}}switch(l_nRttpCode){
case SL4B_RttpCodes.const_RECORD_RESP:case SL4B_RttpCodes.const_RESP_UNKNOWN:{var l_nIndex=C.getMessageContent().indexOf(" ");
this.cacheType1Record(C.getObjectNumber(),C.getMessageContent().substr(l_nIndex+1),(l_nRttpCode==SL4B_RttpCodes.const_RESP_UNKNOWN),SL4B_RttpCodes.isImageCode(l_nRttpCode));break;
}case SL4B_RttpCodes.const_REC1_UPD:case SL4B_RttpCodes.const_REC1_IMG:{this.cacheType1Record(C.getObjectNumber(),C.getMessageContent(),false,SL4B_RttpCodes.isImageCode(l_nRttpCode));break;
}case SL4B_RttpCodes.const_REC2_UPD:case SL4B_RttpCodes.const_REC2_IMG:{this.cacheType2Record(C.getObjectNumber(),C.getMessageContent(),SL4B_RttpCodes.isImageCode(l_nRttpCode));break;
}case SL4B_RttpCodes.const_REC2_CLR:{this.type2Clear(C.getObjectNumber());break;
}case SL4B_RttpCodes.const_REC2_DEL:{this.deleteType2RecordLevel(C.getObjectNumber(),C.getMessageContent());break;
}case SL4B_RttpCodes.const_REC3_UPD:case SL4B_RttpCodes.const_REC3_IMG:{this.cacheType3Record(C.getObjectNumber(),C.getMessageContent(),SL4B_RttpCodes.isImageCode(l_nRttpCode));break;
}case SL4B_RttpCodes.const_REC3_CLR:{this.type3Clear(C.getObjectNumber());break;
}case SL4B_RttpCodes.const_DIRECTORY_RESP:case SL4B_RttpCodes.const_DIR_UPD:{this.cacheDirectory(C.getObjectNumber(),C.getMessageContent(),l_nRttpCode);break;
}case SL4B_RttpCodes.const_CONT_RESP:case SL4B_RttpCodes.const_CONT_UPD:case SL4B_RttpCodes.const_CONT_IMG:case SL4B_RttpCodes.const_AUTODIR_RESP:case SL4B_RttpCodes.const_AUTODIR_UPD:case SL4B_RttpCodes.const_AUTODIR_IMG:{this.cacheContainer(C.getObjectNumber(),C.getMessageContent(),l_nRttpCode,B,A);break;
}case SL4B_RttpCodes.const_NEWS_IMG:case SL4B_RttpCodes.const_NEWS_UPD:{this.cacheNewsHeadline(C.getObjectNumber(),C.getMessageContent());break;
}case SL4B_RttpCodes.const_STORY_UPD:case SL4B_RttpCodes.const_STORY_RESP:case SL4B_RttpCodes.const_STORY_IMG:{this.cacheNewsStory(C.getObjectNumber(),C.getMultipleLineContents(),C.getMessageContent());break;
}case SL4B_RttpCodes.const_PERM_IMG:case SL4B_RttpCodes.const_PERM_UPD:{this.cachePermission(C.getObjectNumber(),C.getMessageContent());break;
}case SL4B_RttpCodes.const_PERM_CLR:{this.clearPermission(C.getObjectNumber());break;
}case SL4B_RttpCodes.const_PERM_DEL:{this.deletePermissionEntry(C.getObjectNumber(),C.getMessageContent());break;
}case SL4B_RttpCodes.const_CHAT_RESP:case SL4B_RttpCodes.const_CHAT_UPD:{this.cacheChat(C.getObjectNumber(),C.getMessageContent(),(l_nRttpCode===SL4B_RttpCodes.const_CHAT_RESP));break;
}}}
var g_rePlus=new RegExp("\\+","g");
function SL_DR(A,B,C,D){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectCache.cacheType1Record({0}, {1}, {2}, {3})",A,B,C,D);var l_sObjectKey=this.m_pObjectNumberToObjectKeyMap[A];
if(l_sObjectKey!==undefined){var l_oType1Cache=this.m_pObjectDataCache[l_sObjectKey];
if(l_oType1Cache===undefined){l_oType1Cache=new SL4B_Type1RecordCache();this.m_pObjectDataCache[l_sObjectKey]=l_oType1Cache;}if(!C){var l_oRecordFieldData;
l_oRecordFieldData=this.getFieldDataFromString(l_oType1Cache,B,D);this.m_oSubscriptionManager.recordMultiUpdated(l_sObjectKey,l_oRecordFieldData,D);}}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ObjectCache.cacheType1Record: No object name is "+"available that corresponds to the object number {0}",A);}}
SL4B_ObjectCache.prototype.getFieldDataFromString = function(B,A,C){var l_pFields=A.split(" ");
var l_oRecordFieldData=new SL4B_RecordFieldData();
for(var l_nField=0,l_nLength=l_pFields.length;l_nField<l_nLength;++l_nField){var l_nIndex=l_pFields[l_nField].indexOf("=");
var l_sRawFieldName=l_pFields[l_nField].substring(0,l_nIndex);
var l_sRawFieldValue=l_pFields[l_nField].substring(l_nIndex+1);
this.addType1FieldData(B,l_oRecordFieldData,l_sRawFieldName,l_sRawFieldValue,C);}return l_oRecordFieldData;
};
SL4B_ObjectCache.prototype.addType1FieldData = function(A,D,C,B,E){var l_sFieldName=this.m_oRttpProvider.getFieldName(C);
var l_sFieldValue=GF_ResponseDecoder.decodeRttpData(B);
A.addField(l_sFieldName,l_sFieldValue);D.add(l_sFieldName,l_sFieldValue);if(!E&&SL4B_Accessor.getConfiguration().isEnableLatency()&&l_sFieldName==SL4B_Accessor.getConfiguration().getTimestampField()){SL4B_ConnectionProxy.getInstance().recordLatency(l_sFieldValue);}};
function SL_OZ(A,C,B){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectCache.cacheType2Record({0}, {1}, {2})",A,C,B);var l_sObjectKey=this.m_pObjectNumberToObjectKeyMap[A];
if(l_sObjectKey!==undefined){var l_pFields=C.split(" ");
var l_nIndex=l_pFields[0].indexOf("=");
var l_sLevelFieldName=this.m_oRttpProvider.getFieldName(l_pFields[0].substring(0,l_nIndex));
var l_sLevelFieldValue=l_pFields[0].substring(l_nIndex+1);
l_sLevelFieldValue=GF_ResponseDecoder.decodeRttpData(l_sLevelFieldValue);if(this.m_pObjectDataCacheType2[l_sObjectKey]===undefined){this.m_pObjectDataCacheType2[l_sObjectKey]=new SL4B_Type2RecordCache();this.m_pObjectDataCacheType2[l_sObjectKey].m_sIndexFieldName=l_sLevelFieldName;}var l_oRecordFieldData=new SL4B_RecordFieldData();
l_oRecordFieldData.add(l_sLevelFieldName,l_sLevelFieldValue);for(var l_nField=1,l_nLength=l_pFields.length;l_nField<l_nLength;++l_nField){l_nIndex=l_pFields[l_nField].indexOf("=");var l_sFieldName=this.m_oRttpProvider.getFieldName(l_pFields[l_nField].substring(0,l_nIndex));
var l_sFieldValue=l_pFields[l_nField].substring(l_nIndex+1);
if(l_sFieldName!==undefined){var l_sFieldValue=GF_ResponseDecoder.decodeRttpData(l_sFieldValue);
this.m_pObjectDataCacheType2[l_sObjectKey].addField(l_sLevelFieldValue,l_sFieldName,l_sFieldValue);l_oRecordFieldData.add(l_sFieldName,l_sFieldValue);if(!B&&SL4B_Accessor.getConfiguration().isEnableLatency()&&l_sFieldName==SL4B_Accessor.getConfiguration().getTimestampField()){SL4B_ConnectionProxy.getInstance().recordLatency(l_sFieldValue);}}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ObjectCache.cacheType2Record: No field is "+"available that corresponds to the field {0}",l_pFields[l_nField].substring(0,l_nIndex));}}this.m_oSubscriptionManager.recordMultiUpdated(l_sObjectKey,l_oRecordFieldData,B);}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ObjectCache.cacheType2Record: No object name is "+"available that corresponds to the object number {0}",A);}}
function SL_FB(A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectCache.type2Clear: {0}",A);var l_sObjectKey=this.m_pObjectNumberToObjectKeyMap[A];
if(l_sObjectKey!==undefined){if(this.m_pObjectDataCacheType2[l_sObjectKey]!==undefined){delete this.m_pObjectDataCacheType2[l_sObjectKey];this.m_oSubscriptionManager.type2Clear(l_sObjectKey);}}}
function SL_PT(A,C,B){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectCache.cacheType3Record: {0}, {1}, {2}",A,C,B);var l_sObjectKey=this.m_pObjectNumberToObjectKeyMap[A];
if(l_sObjectKey!==undefined){if(this.m_pObjectDataCacheType3[l_sObjectKey]===undefined){this.m_pObjectDataCacheType3[l_sObjectKey]=new SL_OB();}var l_pFields=C.split(" ");
var l_oRecordFieldData=new SL4B_RecordFieldData();
var l_pNameValuePairs=new Array();
this.m_pObjectDataCacheType3[l_sObjectKey].m_pLevelCache.push(l_pNameValuePairs);for(var l_nField=0,l_nLength=l_pFields.length;l_nField<l_nLength;++l_nField){l_nIndex=l_pFields[l_nField].indexOf("=");var l_sFieldName=this.m_oRttpProvider.getFieldName(l_pFields[l_nField].substring(0,l_nIndex));
var l_sFieldValue=l_pFields[l_nField].substring(l_nIndex+1);
if(l_sFieldName!==undefined){var l_sFieldValue=GF_ResponseDecoder.decodeRttpData(l_sFieldValue);
l_pNameValuePairs[l_sFieldName]=l_sFieldValue;l_oRecordFieldData.add(l_sFieldName,l_sFieldValue);if(!B&&SL4B_Accessor.getConfiguration().isEnableLatency()&&l_sFieldName==SL4B_Accessor.getConfiguration().getTimestampField()){SL4B_ConnectionProxy.getInstance().recordLatency(l_sFieldValue);}}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ObjectCache.cacheType3Record: No field is "+"available that corresponds to the field {0}",l_pFields[l_nField].substring(0,l_nIndex));}}this.m_oSubscriptionManager.recordMultiUpdated(l_sObjectKey,l_oRecordFieldData,B);}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ObjectCache.cacheType3Record: No object name is "+"available that corresponds to the object number {0}",A);}}
function SL_PZ(A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectCache.type3Clear({0})",A);var l_sObjectKey=this.m_pObjectNumberToObjectKeyMap[A];
if(l_sObjectKey!==undefined){if(this.m_pObjectDataCacheType3[l_sObjectKey]!==undefined){delete this.m_pObjectDataCacheType3[l_sObjectKey];this.m_oSubscriptionManager.type3Clear(l_sObjectKey);}}}
function SL_SE(A,B){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectCache.deleteType2RecordLevel: {0}; {1}",A,B);var l_sObjectKey=this.m_pObjectNumberToObjectKeyMap[A];
if(l_sObjectKey!==undefined){if(this.m_pObjectDataCacheType2[l_sObjectKey]!==undefined){var l_pFields=B.split(" ");
var l_nIndex=l_pFields[0].indexOf("=");
var l_sLevelFieldName=this.m_oRttpProvider.getFieldName(l_pFields[0].substring(0,l_nIndex));
var l_sLevelFieldValue=l_pFields[0].substring(l_nIndex+1);
l_sLevelFieldValue=GF_ResponseDecoder.decodeRttpData(l_sLevelFieldValue);this.m_pObjectDataCacheType2[l_sObjectKey].deleteLevel(l_sLevelFieldValue);this.m_oSubscriptionManager.deleteType2Level(l_sObjectKey,l_sLevelFieldName,l_sLevelFieldValue);}}}
function SL_AS(B,C,A){var l_sObjectKey=this.m_pObjectNumberToObjectKeyMap[B];
if(l_sObjectKey!==undefined){var l_oCache=this.m_pDirectoryCache[l_sObjectKey];
if(l_oCache===undefined){l_oCache=new SL4B_DirectoryCache();this.m_pDirectoryCache[l_sObjectKey]=l_oCache;}var l_pDirs=C.split(" ");
var l_nDir=1;
if(A==SL4B_RttpCodes.const_DIR_UPD){l_nDir=0;}var l_pDirectoryChanges=new Array();
for(var l_nLength=l_pDirs.length;l_nDir<l_nLength;++l_nDir){var l_pDirAndType=l_pDirs[l_nDir].split(";");
var l_sObjectName=l_pDirAndType[0];
var l_nObjectType=l_pDirAndType[1];
if(l_nObjectType==0){delete l_oCache.m_pListing[l_sObjectName];l_pDirectoryChanges.push(new SL4B_DirectoryStructureChange(l_sObjectName,l_nObjectType,false));}else 
{l_oCache.m_pListing[l_sObjectName]=l_nObjectType;l_pDirectoryChanges.push(new SL4B_DirectoryStructureChange(l_sObjectName,l_nObjectType,true));}this.m_oSubscriptionManager.dirUpdated(l_sObjectKey,GF_ResponseDecoder.decodeRttpData(l_pDirAndType[0]),l_pDirAndType[1],(l_pDirAndType[1]!=0));}this.m_oSubscriptionManager.dirMultiUpdated(l_sObjectKey,l_pDirectoryChanges);}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ObjectCache.cacheDirectory: No object name is "+"available that corresponds to the object number {0}",B);}}
function SL_SD(A,E,C,B,D){var l_sObjectKey=this.m_pObjectNumberToObjectKeyMap[A];
if(l_sObjectKey===undefined){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ObjectCache.cacheContainer: No object name is "+"available that corresponds to the object number {0}",A);return;
}var l_oCache=this.m_pContainerCache[l_sObjectKey];
if(l_oCache===undefined){l_oCache=new SL4B_ContainerCache();this.m_pContainerCache[l_sObjectKey]=l_oCache;}if(C==SL4B_RttpCodes.const_AUTODIR_RESP||C==SL4B_RttpCodes.const_AUTODIR_IMG){l_oCache.setDirectory(true);}var l_pWords=E.split(" ");
var l_pStructureChanges=new Array();
var l_pOrderChanges=new Array();
var mObjectNamesRemovedFromContainer={};
var l_nPos=0;
var l_bParseOrderFromElements=false;
if(C==SL4B_RttpCodes.const_CONT_RESP||C==SL4B_RttpCodes.const_AUTODIR_RESP||C==SL4B_RttpCodes.const_CONT_IMG||C==SL4B_RttpCodes.const_AUTODIR_IMG){l_bParseOrderFromElements=true;if(C==SL4B_RttpCodes.const_CONT_RESP||C==SL4B_RttpCodes.const_AUTODIR_RESP){l_nPos=2;}var l_oObjectNameToObjectTypeMap=l_oCache.getObjectNameToObjectTypeMap();
for(l_sName in l_oObjectNameToObjectTypeMap){this.m_oSubscriptionManager.structureChange(l_sObjectKey,l_sName,l_oObjectNameToObjectTypeMap[l_sName],false,undefined,l_oCache.m_mObjectNameToNumberMap[l_sName]);l_pStructureChanges.push(new SL4B_ContainerStructureChange(l_sName,l_oObjectNameToObjectTypeMap[l_sName],false));B.removeObjectNumber(l_oCache.m_mObjectNameToNumberMap[l_sName]);l_oCache.removeEntry(l_sName);l_oCache.removeOrdering(l_oCache.m_oObjectNameToIdMap[l_sName]);l_oCache.removeFromNameIdMap(l_sName);mObjectNamesRemovedFromContainer[l_sName]=SL4B_ObjectCache.SUBJECT_DELETED;}}var l_nElementPosition=this._getWindowStart(C,D);
if(l_nElementPosition>=2147483647){return;
}var l_sOrderingString="";
for(var l_nLength=l_pWords.length;l_nPos<l_nLength;++l_nPos){var l_sWord=l_pWords[l_nPos];
if(l_sWord.match(/^size=/)!=null){var l_pItems=l_sWord.split("=");
l_oCache.setSize(parseInt(l_pItems[1]));}else 
if(l_sWord.match(/^order=/)!=null){var l_pItems=l_sWord.split("=");
l_sOrderingString=l_pItems[1];}else 
if(l_sWord.match(/^.+;.+;.+/)!=null){var l_pItems=l_sWord.split(";");
var l_sName=l_pItems[0];
l_sName=GF_ResponseDecoder.decodeRttpData(l_sName);var l_bAdded=(l_pItems[1]!=0);
var l_sSubscriptionId=l_pItems[l_pItems.length-1];
var l_bIsSnapshot=(this.m_oSnapshotSet[l_sObjectKey]!=undefined);
if(!l_bIsSnapshot){if(!l_bAdded){l_oCache.removeEntry(l_sName);l_oCache.removeOrdering(l_oCache.m_oObjectNameToIdMap[l_sName]);l_oCache.removeFromNameIdMap(l_sName);B.removeObjectNumber(l_sSubscriptionId);mObjectNamesRemovedFromContainer[l_sName]=SL4B_ObjectCache.SUBJECT_DELETED;}else 
{if(mObjectNamesRemovedFromContainer[l_sName]===undefined){this._addCacheEntryIfNotPresent(l_sName);}this.m_pObjectNumberToObjectKeyMap[l_sSubscriptionId]=l_sSubscriptionId+":"+l_sName;l_oCache.addEntry(l_sName,l_pItems[1],l_sSubscriptionId);B.addObjectNumber(l_sSubscriptionId);mObjectNamesRemovedFromContainer[l_sName]=SL4B_ObjectCache.SUBJECT_ADDED;}if(l_bParseOrderFromElements){l_pOrderChanges.push(new SL4B_ContainerOrderChange(l_sName,l_sSubscriptionId,l_nElementPosition));l_oCache.addToNameIdMap(l_sName,l_sSubscriptionId);l_oCache.addOrdering(l_sSubscriptionId,l_nElementPosition);}}this.m_oSubscriptionManager.structureChange(l_sObjectKey,l_sName,l_pItems[1],l_bAdded,undefined,l_sSubscriptionId);l_pStructureChanges.push(new SL4B_ContainerStructureChange(l_sName,l_pItems[1],l_bAdded));l_nElementPosition++;}}if(l_sOrderingString!=""){l_pOrderChanges=this.parseOrderChanges(l_sOrderingString,l_oCache);}this._dereferenceRemovedContentsInCache(mObjectNamesRemovedFromContainer);if(C==SL4B_RttpCodes.const_CONT_RESP||C==SL4B_RttpCodes.const_CONT_IMG||C==SL4B_RttpCodes.const_CONT_UPD){var l_bIsCachedImage=(C==SL4B_RttpCodes.const_CONT_RESP||C==SL4B_RttpCodes.const_CONT_IMG);
this.m_oSubscriptionManager.structureMultiChange(l_sObjectKey,l_pStructureChanges,l_pOrderChanges,l_oCache.getSize(),l_bIsCachedImage);}}
function SL_RA(){for(l_sObjectKey in this.m_pContainerCache){var l_oCache=this.m_pContainerCache[l_sObjectKey];
var l_oObjectNameToObjectTypeMap=l_oCache.getObjectNameToObjectTypeMap();
var l_pStructureChanges=[];
var l_pOrderChanges=[];
for(l_sName in l_oObjectNameToObjectTypeMap){this.m_oSubscriptionManager.structureChange(l_sObjectKey,l_sName,l_oObjectNameToObjectTypeMap[l_sName],false,undefined,l_oCache.m_mObjectNameToNumberMap[l_sName]);l_pStructureChanges.push(new SL4B_ContainerStructureChange(l_sName,l_oObjectNameToObjectTypeMap[l_sName],false));}this.m_oSubscriptionManager.structureMultiChange(l_sObjectKey,l_pStructureChanges,l_pOrderChanges,l_oCache.getSize(),false);}}
SL4B_ObjectCache.prototype._getWindowStart = function(A,B){var l_nElementPosition=0;
if(A==SL4B_RttpCodes.const_CONT_RESP||A==SL4B_RttpCodes.const_CONT_IMG){var l_nWindowStart;
if(B!==undefined){l_nWindowStart=parseInt(B.getParameters()['ctrstart']);if(isNaN(l_nWindowStart)==false){l_nElementPosition=l_nWindowStart;}}}return l_nElementPosition;
};
SL4B_ObjectCache.prototype._dereferenceRemovedContentsInCache = function(A){for(sDeletedSubject in A){if(A[sDeletedSubject]===SL4B_ObjectCache.SUBJECT_DELETED){this._removeCacheEntryIfNoMoreReferences(sDeletedSubject,false);}}};
function SL_JL(A,B){var l_sParsedItems=new Array();
if(typeof A!="string"||A==null||A.length==0){return l_sParsedItems;
}var l_pOrderItems=A.split(",");
for(var i=0;i<l_pOrderItems.length;i++){var l_pOrderMessage=l_pOrderItems[i].split(":");
if(l_pOrderMessage.length!=2){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ObjectCache.parseOrderChanges: Unable to parse order message: \"{0}\" from \"{1}\"",l_pOrderItems[i],A);break;
}var l_sObjectKey=this.m_pObjectNumberToObjectKeyMap[l_pOrderMessage[0]];
if(l_sObjectKey===undefined){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ObjectCache.parseOrderChanges: Unable to parse order message: \"{0}\" from \"{1}\" could not find subscriptionId",l_pOrderItems[i],A);break;
}l_sObjectKey=l_sObjectKey.replace(/^[A-Za-z0-9_-]{4}:/,"");var order=parseInt(l_pOrderMessage[1]);
l_sParsedItems.push(new SL4B_ContainerOrderChange(l_sObjectKey,l_pOrderMessage[0],order));B.addToNameIdMap(l_sObjectKey,l_pOrderMessage[0]);B.addOrdering(l_pOrderMessage[0],order);}return l_sParsedItems;
}
function SL_DS(A,B){var l_sObjectKey=this.m_pObjectNumberToObjectKeyMap[A];
if(l_sObjectKey!==undefined){var l_oCache=this.m_pNewsHeadlineCache[l_sObjectKey];
if(l_oCache===undefined||typeof l_oCache.addField=="function"){l_oCache=new SL4B_NewsHeadlineCache();this.m_pNewsHeadlineCache[l_sObjectKey]=l_oCache;}var l_pFields=B.split(" ");
if(l_pFields.length==1){var l_nIndex=l_pFields[0].indexOf("=");
l_sFieldName=l_pFields[0].substring(0,l_nIndex);l_sFieldValue=l_pFields[0].substring(l_nIndex+1);if(l_sFieldName==SL4B_NewsHeadlineCache.const_SIZE_FIELD){this.m_oSubscriptionManager.objectInfo(l_sObjectKey,SL4B_ObjectType.NEWS_HEADLINE,l_sFieldName,l_sFieldValue);}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ObjectCache.cacheNewsHeadline: update did not contain the \"size\" field: {0} - {1}",A,B);}}else 
if(l_pFields.length==3){var l_sHeadline;
var l_sCode;
var l_sDate;
var l_bError=false;
for(var l_nField=0;l_nField<3;++l_nField){var l_nIndex=l_pFields[l_nField].indexOf("=");
l_sFieldName=this.m_oRttpProvider.getFieldName(l_pFields[l_nField].substring(0,l_nIndex));l_sFieldValue=l_pFields[l_nField].substring(l_nIndex+1);l_sFieldValue=GF_ResponseDecoder.decodeRttpData(l_sFieldValue);switch(l_sFieldName){
case "headline":l_sHeadline=l_sFieldValue;break;
case "code":l_sCode=l_sFieldValue;break;
case "date":l_sDate=l_sFieldValue;break;
default :l_bError=true;break;
}}if(!l_bError){l_oCache.addHeadline(l_sCode,l_sHeadline,l_sDate);this.m_oSubscriptionManager.newsUpdated(l_sObjectKey,l_sCode,l_sHeadline,l_sDate);}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ObjectCache.cacheNewsHeadline: news headline message did not contain the expected fields: {0} - {1}",A,B);}}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ObjectCache.cacheNewsHeadline: unexpected news headline message received: {0} - {1}",A,B);}}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ObjectCache.cacheNewsHeadline: No object name is available that corresponds to the object number {0}",A);}}
function SL_RB(A,B,C){var l_sObjectKey=this.m_pObjectNumberToObjectKeyMap[A];
if(l_sObjectKey!==undefined){var l_oStoryCache=this.m_pStoryCache[l_sObjectKey]||this.m_pObjectDataCache[l_sObjectKey];
if(l_oStoryCache===undefined||!l_oStoryCache.isNewsStory){if(l_oStoryCache!==undefined){delete this.m_pObjectDataCache[l_sObjectKey];}l_oStoryCache=new SL4B_NewsStoryCache();this.m_pStoryCache[l_sObjectKey]=l_oStoryCache;}if(C.match(/reset/)){l_oStoryCache.addTextLines(B);}else 
{l_oStoryCache.setTextLines(B);}}var l_aTextLines=l_oStoryCache.getTextLines();
this.m_oSubscriptionManager.storyUpdated(l_sObjectKey,B);}
function SL_OV(A,B){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectCache.cachePermission({0}, {1})",A,B);var l_sObjectKey=this.m_pObjectNumberToObjectKeyMap[A];
if(l_sObjectKey!==undefined){var l_oCache=this.m_pPermissionCache[l_sObjectKey];
var l_pFields=null;
if(B==null){l_pFields=new Array();}else 
{l_pFields=B.split(" ");var l_nIndex=l_pFields[0].indexOf("=");
var l_sKeyFieldName=this.m_oRttpProvider.getFieldName(l_pFields[0].substring(0,l_nIndex));
var l_sKeyFieldValue=GF_ResponseDecoder.decodeRttpData(l_pFields[0].substring(l_nIndex+1));
if(l_oCache===undefined){l_oCache=new SL4B_Type2RecordCache();l_oCache.m_sIndexFieldName=l_sKeyFieldValue;this.m_pPermissionCache[l_sObjectKey]=l_oCache;}}var l_oFieldData=new SL4B_RecordFieldData();
for(var l_nField=1,l_nLength=l_pFields.length;l_nField<l_nLength;++l_nField){l_nIndex=l_pFields[l_nField].indexOf("=");var l_sFieldName=this.m_oRttpProvider.getFieldName(l_pFields[l_nField].substring(0,l_nIndex));
var l_sFieldValue=GF_ResponseDecoder.decodeRttpData(l_pFields[l_nField].substring(l_nIndex+1));
this.m_pPermissionCache[l_sObjectKey].addField(l_sKeyFieldValue,l_sFieldName,l_sFieldValue);l_oFieldData.add(l_sFieldName,l_sFieldValue);}this.m_oSubscriptionManager.permissionUpdated(l_sObjectKey,l_sKeyFieldValue,l_oFieldData);}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ObjectCache.cachePermission: No object name is available that corresponds to the object number {0}",A);}}
function SL_MD(A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectCache.clearPermission: {0}",A);var l_sObjectKey=this.m_pObjectNumberToObjectKeyMap[A];
if(l_sObjectKey!==undefined){if(this.m_pPermissionCache[l_sObjectKey]!==undefined){delete this.m_pPermissionCache[l_sObjectKey];this.m_oSubscriptionManager.deleteAllPermissionEntries(l_sObjectKey);}}}
function SL_CF(A,B){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectCache.deletePermissionEntry: {0}; {1}",A,B);var l_sObjectKey=this.m_pObjectNumberToObjectKeyMap[A];
if(l_sObjectKey!==undefined){if(this.m_pPermissionCache[l_sObjectKey]!==undefined){var l_pFields=B.split(" ");
var l_nIndex=l_pFields[0].indexOf("=");
var l_sKeyFieldName=this.m_oRttpProvider.getFieldName(l_pFields[0].substring(0,l_nIndex));
var l_sKeyFieldValue=GF_ResponseDecoder.decodeRttpData(l_pFields[0].substring(l_nIndex+1));
this.m_pPermissionCache[l_sObjectKey].deleteLevel(l_sKeyFieldValue);this.m_oSubscriptionManager.deletePermissionEntry(l_sObjectKey,l_sKeyFieldValue);}}}
SL4B_ObjectCache.prototype.cacheChat = function(B,C,A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectCache.cacheChat: {0}; {1}",B,C);var l_sObjectKey=this.m_pObjectNumberToObjectKeyMap[B];
if(l_sObjectKey!==undefined){var mFields=this._extractFieldsAsMap(C,A);
var oCache=this.m_mChatCache[l_sObjectKey];
if(oCache===undefined){oCache=new GF_ChatCache(l_sObjectKey);this.m_mChatCache[l_sObjectKey]=oCache;}oCache.addToCache(mFields);this._$sendChat(l_sObjectKey,mFields);}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ObjectCache.cacheChat: No object name is available that corresponds to the object number {0}",B);}};
SL4B_ObjectCache.prototype._$sendChat = function(A,C,B){this.m_oSubscriptionManager.chat(A,C[GF_ChatFields.TIME]||SL4B_ObjectCache.const_BLANK_STRING,C[GF_ChatFields.USER]||SL4B_ObjectCache.const_BLANK_STRING,C[GF_ChatFields.MESSAGE]||SL4B_ObjectCache.const_BLANK_STRING,parseInt(C[GF_ChatFields.STATUS]||0),B);};
SL4B_ObjectCache.prototype._extractFieldsAsArray = function(B,A){return this._extractFields(B,A,this._addFieldToArray,[]);
};
SL4B_ObjectCache.prototype._addFieldToArray = function(B,A,C){B.push({name:A, value:C});};
SL4B_ObjectCache.prototype._extractFieldsAsMap = function(B,A){return this._extractFields(B,A,this._addFieldToMap,{});
};
SL4B_ObjectCache.prototype._addFieldToMap = function(C,A,B){C[A]=B;};
SL4B_ObjectCache.prototype._extractFields = function(C,A,B,D){var pFieldPairs=C.split(" ");
for(var nField=(A ? 1 : 0),nLength=pFieldPairs.length;nField<nLength;++nField){var nIndex=pFieldPairs[nField].indexOf("=");
if(nIndex>=0){var sFieldName=this.m_oRttpProvider.getFieldName(pFieldPairs[nField].substring(0,nIndex));
var sFieldValue=pFieldPairs[nField].substring(nIndex+1);
B(D,sFieldName,GF_ResponseDecoder.decodeRttpData(sFieldValue));}else 
{this._logParsingError("Illegal field definition found for \""+pFieldPairs[nField]+"\" whilst parsing message body \""+C+"\"");}}return D;
};
SL4B_ObjectCache.prototype._logParsingError = function(A){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,A);};
function SL_QQ(A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectCache.statusUpdated: {0}",A);var l_sObjectNumber=A.getObjectNumber();
var l_sObjectKey=this.m_pObjectNumberToObjectKeyMap[l_sObjectNumber];
var l_pFields=A.getMessageContent().split(" ");
var l_nCode=0;
var l_sMessage="";
for(var l_nField=0,l_nLength=l_pFields.length;l_nField<l_nLength;++l_nField){var l_nIndex=l_pFields[l_nField].indexOf("=");
var l_sFieldName=this.m_oRttpProvider.getFieldName(l_pFields[l_nField].substring(0,l_nIndex));
var l_sFieldValue=GF_ResponseDecoder.decodeRttpData(l_pFields[l_nField].substring(l_nIndex+1));
switch(l_sFieldName){
case "code":l_nCode=parseInt(l_sFieldValue,10);break;
case "status":l_sMessage=l_sFieldValue;break;
}}
try {this.m_oSubscriptionManager.objectStatus(l_sObjectKey,SL4B_ObjectStatus.getObjectStatusFromRttpCode(A.getRttpCode()),l_nCode,l_sMessage);var l_bIsSnapshot=(this.m_oSnapshotSet[l_sObjectKey]!=undefined);
if(l_bIsSnapshot){this.clearCacheForObjectKey(l_sObjectKey);delete this.m_oSnapshotSet[l_sObjectKey];this.m_oSubscriptionManager.clearSnapshot(l_sObjectKey);}else 
{if(this.m_pObjects[l_sObjectKey]===undefined){l_sObjectKey=l_sObjectKey.replace(/^[A-Za-z0-9_-]{4}:/,"");}if(this.m_pObjects[l_sObjectKey]){this.m_pObjects[l_sObjectKey].setObjectStatus(SL4B_ObjectStatus.getObjectStatusFromRttpCode(A.getRttpCode()),l_nCode,l_sMessage);}}}catch(e){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ObjectCache.statusUpdated: Problem occurred while setting the status of objects. {0}: {1} message=({2}) objectkey='{3}' cache='{4}' .",e.name,e.message,A,l_sObjectKey,this.m_pObjects[l_sObjectKey]);}
}
SL4B_ObjectCache.prototype.getObjectStatus = function(A){return ((this.m_pObjects[A]!==undefined) ? this.m_pObjects[A].getObjectStatus() : null);
};
GF_ChatFields = new function(){this.STATUS="status";this.USER="user";this.MESSAGE="msg";this.TIME="time";};
var SL4B_ObjectSubscriptionManager=function(){};
if(false){function SL4B_ObjectSubscriptionManager(){}
}SL4B_ObjectSubscriptionManager = function(A){this.CLASS_NAME="SL4B_ObjectSubscriptionManager";this.m_pObjectSubscriptions=new Object();this.m_oObjectCache=null;this.m_oContainerProxySubscribers=new Object();this.m_oContainerObjects=new Object();this.m_oContainerIdToRequestDataMap=new Object();this.m_bStaleStatusSent=false;this.m_oConnectionListener=new LF_ObjectSubscriptionManagerConnectionListener(this);this.setRttpProvider(A);};
SL4B_ObjectSubscriptionManager.LOGMESSAGES={};SL4B_ObjectSubscriptionManager.LOGMESSAGES.OBJECT_STATUS_WAS_NULL="ObjectStatus was null for key {0}";SL4B_ObjectSubscriptionManager.prototype.isValidObject = function(A){return ((typeof A=="string")&&A!="");
};
SL4B_ObjectSubscriptionManager.prototype.isSubscribed = function(A){return this.m_pObjectSubscriptions[A]!=null;
};
SL4B_ObjectSubscriptionManager.prototype.requestObject = SL_BT;SL4B_ObjectSubscriptionManager.prototype.requestObjects = SL_OO;SL4B_ObjectSubscriptionManager.prototype.reRequestObjects = SL_BS;SL4B_ObjectSubscriptionManager.prototype.discardObject = SL_DM;SL4B_ObjectSubscriptionManager.prototype.discardObjects = SL_MA;SL4B_ObjectSubscriptionManager.prototype.addObject = SL_LW;SL4B_ObjectSubscriptionManager.prototype.removeObject = SL_QY;SL4B_ObjectSubscriptionManager.prototype.removeSubscriber = SL_CO;SL4B_ObjectSubscriptionManager.prototype.setObjectCache = SL_AM;SL4B_ObjectSubscriptionManager.prototype.recordMultiUpdated = SL_GJ;SL4B_ObjectSubscriptionManager.prototype.recordMultiUpdated2 = SL_IV;SL4B_ObjectSubscriptionManager.prototype.deleteType2Level = SL_DF;SL4B_ObjectSubscriptionManager.prototype.type2Clear = SL_GM;SL4B_ObjectSubscriptionManager.prototype.type3Clear = SL_DU;SL4B_ObjectSubscriptionManager.prototype.clearAllSubscriptions = SL_JX;SL4B_ObjectSubscriptionManager.prototype.dirUpdated = SL_FE;SL4B_ObjectSubscriptionManager.prototype.dirMultiUpdated = SL_ON;SL4B_ObjectSubscriptionManager.prototype.newsUpdated = SL_PU;SL4B_ObjectSubscriptionManager.prototype.objectInfo = SL_IO;SL4B_ObjectSubscriptionManager.prototype.storyUpdated = SL_BN;SL4B_ObjectSubscriptionManager.prototype.permissionUpdated = SL_NK;SL4B_ObjectSubscriptionManager.prototype.deleteAllPermissionEntries = SL_FR;SL4B_ObjectSubscriptionManager.prototype.deletePermissionEntry = SL_EA;SL4B_ObjectSubscriptionManager.prototype.objectStatus = SL_MY;SL4B_ObjectSubscriptionManager.prototype.splitFilter = SL_KY;SL4B_ObjectSubscriptionManager.prototype.sendStaleToAll = SL_KD;SL4B_ObjectSubscriptionManager.prototype.sendCurrentObjectStatusToAll = SL_QX;SL4B_ObjectSubscriptionManager.prototype.sendObjectStatusToAll = SL_MP;SL4B_ObjectSubscriptionManager.prototype.structureChange = SL_MI;SL4B_ObjectSubscriptionManager.prototype.structureChange2 = SL_QI;SL4B_ObjectSubscriptionManager.prototype.structureMultiChange = SL_PV;SL4B_ObjectSubscriptionManager.prototype.registerProxySubscriber = SL_RQ;SL4B_ObjectSubscriptionManager.prototype.getProxySubscriber = SL_IL;SL4B_ObjectSubscriptionManager.prototype.addContainerRequestData = SL_RY;SL4B_ObjectSubscriptionManager.prototype.getContainerRequestData = SL_DI;SL4B_ObjectSubscriptionManager.prototype.getObjectSubscriptions = SL_JN;SL4B_ObjectSubscriptionManager.prototype.getContainerIdToRequestDataMap = SL_EO;SL4B_ObjectSubscriptionManager.prototype.getContainer = function(B,C,E,F,D,A){SL4B_JavaScriptRttpProvider.checkWindowRange(F,D);var l_oListener=SL4B_AbstractRttpProvider.prototype.getListener(B);
var l_oProxyListener=SL4B_AbstractRttpProvider.prototype.getListener(new SL4B_ProxySubscriber(B));
if(A==undefined){var l_oContainerKey=new SL4B_ContainerKey();
A=l_oContainerKey.getId();}this.addContainerRequestData(l_oListener,A,C,E,F,D);var l_sCombinedFieldList=SL4B_AbstractRttpProvider.prototype.createFieldListForContainer(A,E,F,D);
this.registerProxySubscriber(l_oListener,l_oProxyListener,C,l_sCombinedFieldList);this.requestObject(l_oListener,C,l_sCombinedFieldList,false);return A;
};
SL4B_ObjectSubscriptionManager.prototype.getContainerSnapshot = function(A,B,D,C){SL4B_JavaScriptRttpProvider.checkWindowRange(D,C);var l_oListener=SL4B_AbstractRttpProvider.prototype.getListener(A);
var l_oProxyListener=SL4B_AbstractRttpProvider.prototype.getListener(new SL4B_ProxySubscriber(A));
var l_oContainerKey=new SL4B_ContainerKey();
var l_nContainerId=l_oContainerKey.getId();
var l_sCombinedFieldList=SL4B_AbstractRttpProvider.prototype.createFieldListForContainer(l_nContainerId,"",D,C);
this.registerProxySubscriber(l_oListener,l_oProxyListener,B,l_sCombinedFieldList);this.requestObject(l_oListener,B,l_sCombinedFieldList,true);};
SL4B_ObjectSubscriptionManager.prototype.setContainerWindow = function(A,C,B){SL4B_JavaScriptRttpProvider.checkWindowRange(C,B);var l_oRequestData=this.getContainerRequestData(A);
var l_sOldCombinedFieldList=SL4B_AbstractRttpProvider.prototype.createFieldListForContainer(A,l_oRequestData.getFieldList(),l_oRequestData.getWindowStart(),l_oRequestData.getWindowEnd());
var l_oOldFilterAndFieldList=this.splitFilter(l_sOldCombinedFieldList);
this.removeObject(l_oRequestData.getSubscriberId(),l_oRequestData.getContainerName(),l_oOldFilterAndFieldList.m_sFilter,l_oOldFilterAndFieldList.m_sFieldList);l_oRequestData.setWindowStart(C);l_oRequestData.setWindowEnd(B);var l_sCombinedFieldList=SL4B_AbstractRttpProvider.prototype.createFieldListForContainer(A,l_oRequestData.getFieldList(),C,B);
var l_oFilterAndFieldList=this.splitFilter(l_sCombinedFieldList);
var l_sObjectKey=l_oRequestData.getContainerName()+l_oFilterAndFieldList.m_sFilter;
this.addObject(l_oRequestData.getSubscriberId(),l_oRequestData.getContainerName(),l_oFilterAndFieldList.m_sFilter,l_oFilterAndFieldList.m_sFieldList);var l_sObjectRequestMessage=SL4B_ObjectCache.buildRTTPRequestMessage(l_oRequestData.getContainerName(),l_oFilterAndFieldList.m_sFilter,l_oFilterAndFieldList.m_sFieldList);
this.m_oObjectCache.sendSubscriptionMessage(l_sObjectRequestMessage,{filter:l_oFilterAndFieldList.m_sFilter});};
function SL_RY(F,C,A,D,E,B){var l_oContainerRequestData=new SL4B_ContainerRequestData(F,C,A,D,E,B);
this.m_oContainerIdToRequestDataMap[C]=l_oContainerRequestData;}
function SL_DI(A){return this.m_oContainerIdToRequestDataMap[A];
}
function SL_RQ(D,C,A,B){var l_oFilterAndFieldList=this.splitFilter(B);
var l_sObjectKey=A+l_oFilterAndFieldList.m_sFilter;
if(this.m_oContainerProxySubscribers[l_sObjectKey]==null){this.m_oContainerProxySubscribers[l_sObjectKey]=new Object();}this.m_oContainerProxySubscribers[l_sObjectKey][D]=C;}
function SL_IL(C,A,B){var l_oFilterAndFieldList=this.splitFilter(B);
var l_sObjectKey=A+l_oFilterAndFieldList.m_sFilter;
if(this.m_oContainerProxySubscribers[l_sObjectKey]!=null){return this.m_oContainerProxySubscribers[l_sObjectKey][C];
}return null;
}
function SL_MI(B,D,E,C,F,A){if(typeof (F)!="undefined"){this.structureChange2(B,D,E,C,F,A);}else 
{for(l_sListenerId in this.m_pObjectSubscriptions[B]){this.structureChange2(B,D,E,C,l_sListenerId,A);}}}
function SL_QI(B,D,E,C,F,A){if(this.m_oContainerProxySubscribers[B]!=null){var l_sProxyListenerId=this.m_oContainerProxySubscribers[B][F];
if(l_sProxyListenerId!=null){var sObjectKey=A+":"+D;
if(C){this.addObject(l_sProxyListenerId,sObjectKey,"","");if(this.m_oContainerObjects[B]==null){this.m_oContainerObjects[B]=new Object();}this.m_oContainerObjects[B][sObjectKey]=sObjectKey;}else 
{this.removeObject(l_sProxyListenerId,sObjectKey,"","");if(this.m_oContainerObjects[B]!=null){delete this.m_oContainerObjects[B][sObjectKey];}}}var l_oSubscription=this.m_pObjectSubscriptions[B][F];
l_oSubscription.m_oSubscriber.WTStructureChange(l_oSubscription.m_sObjectName,D,E,C);}}
function SL_PV(B,A,D,C,E){for(l_sListenerId in this.m_pObjectSubscriptions[B]){if(this.m_oContainerProxySubscribers[B]!=null){var l_oSubscription=this.m_pObjectSubscriptions[B][l_sListenerId];
l_oSubscription.m_oSubscriber.WTStructureMultiChange(l_oSubscription.m_sObjectName,A,D,C,E);}}}
function SL_BS(){this.m_bStaleStatusSent=false;SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINE_INT,"ObjectSubscriptionManager.reRequestObjects()");var l_pSubscriptionsToRemove=[];
for(l_sObjectKey in this.m_pObjectSubscriptions){var l_pListenerMap=this.m_pObjectSubscriptions[l_sObjectKey];
var l_bRealSubscription=false;
for(l_oListenerId in l_pListenerMap){var l_oObjectSubscription=l_pListenerMap[l_oListenerId];
if(!l_oObjectSubscription.isProxySubscription()){var sFieldList=l_oObjectSubscription.getFieldMask().getFieldList_deprecated();
this.m_oObjectCache.requestObject(l_oObjectSubscription.m_sObjectName,l_oObjectSubscription.m_sFilter,sFieldList);l_bRealSubscription=true;}}if(!l_bRealSubscription){l_pSubscriptionsToRemove.push(l_sObjectKey);}}for(var i=0;i<l_pSubscriptionsToRemove.length;i++){l_sObjectKey=l_pSubscriptionsToRemove[i];delete this.m_pObjectSubscriptions[l_sObjectKey];}}
function SL_KD(){if(this.m_bStaleStatusSent===false){this.m_bStaleStatusSent=true;SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINE_INT,"ObjectSubscriptionManager.sendStaleToAll()");this.sendObjectStatusToAll(GF_CachedObjectStatus.const_CONNECTION_LOST);}}
function SL_QX(){this.m_bStaleStatusSent=false;SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINE_INT,"ObjectSubscriptionManager.sendCurrentObjectStatusToAll()");this.sendObjectStatusToAll(null);}
function SL_MP(A){for(l_sObjectKey in this.m_pObjectSubscriptions){var l_pListenerMap=this.m_pObjectSubscriptions[l_sObjectKey];
for(l_oListenerId in l_pListenerMap){var l_oObjectSubscription=l_pListenerMap[l_oListenerId];
var l_sObjectName=l_sObjectKey.replace(/^[A-Za-z0-9_-]{4}:/,"");
var l_oObjectStatus=((A===null) ? this.m_oObjectCache.getObjectStatus(l_sObjectName) : A);
if(l_oObjectStatus!=null){l_sObjectName=l_oObjectSubscription.m_sObjectName.replace(/^[A-Za-z0-9_-]{4}:/,"");l_oObjectSubscription.m_oSubscriber.WTObjectStatus(l_sObjectName,l_oObjectStatus.m_nType,l_oObjectStatus.m_nCode,l_oObjectStatus.m_sMessage);}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_ERROR_INT,"ObjectSubscriptionManager.sendObjectStatusToAll(): "+SL4B_ObjectSubscriptionManager.LOGMESSAGES.OBJECT_STATUS_WAS_NULL,l_sObjectKey);}}}}
function SL_GT(){this.m_sFilter="";this.m_sFieldList="";}
function SL_KY(A){var l_oReturn=new SL_GT();
var l_pFilter=new Array();
var l_sFilter="";
var l_sNewFieldList=A;
if(A!=null){var l_pNames=["imagefilter=","filter=","auto=","monitor=","ctrid="];
for(l_nName in l_pNames){var l_nFilterPos=l_sNewFieldList.indexOf(l_pNames[l_nName]);
if(l_nFilterPos!=-1){var l_nEndFilterPos=l_sNewFieldList.indexOf(",",l_nFilterPos);
if(l_nEndFilterPos!=-1){l_pFilter.push(l_sNewFieldList.substring(l_nFilterPos,l_nEndFilterPos));}else 
{l_pFilter.push(l_sNewFieldList.substring(l_nFilterPos));}var l_sOldFieldList=l_sNewFieldList;
l_sNewFieldList="";if(l_nFilterPos>0){l_sNewFieldList+=l_sOldFieldList.substring(0,l_nFilterPos);}if(l_nEndFilterPos!=-1&&l_nEndFilterPos<l_sOldFieldList.length){l_sNewFieldList+=l_sOldFieldList.substring(l_nEndFilterPos+1);}if(l_sNewFieldList.substring(l_sNewFieldList.length-1,l_sNewFieldList.length)==","){l_sNewFieldList=l_sNewFieldList.substring(0,l_sNewFieldList.length-1);}}}l_sFilter=l_pFilter.join(",");}else 
{l_sNewFieldList="";}l_oReturn.m_sFieldList=l_sNewFieldList;l_oReturn.m_sFilter=l_sFilter;return l_oReturn;
}
function SL_BT(D,C,B,A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectSubscriptionManager.requestObject: {0}, {1}, {2}, {3}",D,C,B,A);var l_oFilterAndFieldList=this.splitFilter(B);
var l_bIsValidObject=this.addObject(D,C,l_oFilterAndFieldList.m_sFilter,l_oFilterAndFieldList.m_sFieldList);
if(l_bIsValidObject){this.m_oObjectCache.requestObject(C,l_oFilterAndFieldList.m_sFilter,l_oFilterAndFieldList.m_sFieldList,D,A);}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ObjectSubscriptionManager.requestObject: attempt to request illegal object \"{0}\" ignored",C);}}
function SL_OO(C,A,B){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectSubscriptionManager.requestObjects: {0}, {1}, {2}",C,A,B);var l_oFilterAndFieldList=this.splitFilter(B);
var l_sValidatedObjectList="";
var l_pObjectNames=A.split(SL4B_AbstractRttpProvider.const_OBJECT_NAME_DELIMITER);
for(var l_nObject=0,l_nSize=l_pObjectNames.length;l_nObject<l_nSize;++l_nObject){var l_bIsValidObject=this.addObject(C,l_pObjectNames[l_nObject],l_oFilterAndFieldList.m_sFilter,l_oFilterAndFieldList.m_sFieldList);
if(l_bIsValidObject){l_sValidatedObjectList+=((l_sValidatedObjectList=="") ? "" : SL4B_AbstractRttpProvider.const_OBJECT_NAME_DELIMITER)+l_pObjectNames[l_nObject];}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ObjectSubscriptionManager.requestObjects: attempt to request illegal object \"{0}\" ignored",l_pObjectNames[l_nObject]);}}if(l_sValidatedObjectList!=""){this.m_oObjectCache.requestObjects(l_sValidatedObjectList,l_oFilterAndFieldList.m_sFilter,l_oFilterAndFieldList.m_sFieldList,C);}}
function SL_DM(A,C,B){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectSubscriptionManager.discardObject: {0}, {1}, {2}",A,C,B);var l_oFilterAndFieldList=this.splitFilter(B);
var l_sObjectKey=C+l_oFilterAndFieldList.m_sFilter;
var l_oContainedObjects=this.m_oContainerObjects[l_sObjectKey];
if(l_oContainedObjects!=null){var nCtridloc=B.indexOf("ctrid=");
if(nCtridloc>=0){var sCtrid=B.substring(nCtridloc+6);
if(sCtrid.indexOf(",")){sCtrid=sCtrid.substring(0,sCtrid.indexOf(","));}SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINER_INT,"ObjectSubscriptionManager.discardObject: ctrid={0}",sCtrid);delete this.m_oContainerIdToRequestDataMap[sCtrid];}var l_sProxyListenerId=this.m_oContainerProxySubscribers[l_sObjectKey][A];
if(l_sProxyListenerId!=null){for(l_sContainedObjectName in l_oContainedObjects){this.removeObject(l_sProxyListenerId,l_sContainedObjectName,"");var l_sOriginalContainedObjectName=l_sContainedObjectName.replace(/^[A-Za-z0-9_-]{4}:/,"");
this.m_oObjectCache.removeObjectFromCacheIfNoMoreReferences(l_sOriginalContainedObjectName,"");}}delete this.m_oContainerProxySubscribers[l_sObjectKey][A];}var l_bDiscardFromServer=this.removeObject(A,C,l_oFilterAndFieldList.m_sFilter,l_oFilterAndFieldList.m_sFieldList);
if(l_bDiscardFromServer==true){this.m_oObjectCache.discardObject(C,l_oFilterAndFieldList.m_sFilter);delete this.m_oContainerObjects[l_sObjectKey];delete this.m_oContainerProxySubscribers[C];}else 
{this.m_oObjectCache.removeObjectFromCacheIfNoMoreReferences(C,l_oFilterAndFieldList.m_sFilter);}}
SL4B_ObjectSubscriptionManager.prototype._discarded = function(){};
function SL_MA(A,B,C){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectSubscriptionManager.discardObjects: {0}, {1}, {2}",A,B,C);var l_oFilterAndFieldList=this.splitFilter(C);
var l_sValidatedObjectList="";
var l_pObjectNames=B.split(SL4B_AbstractRttpProvider.const_OBJECT_NAME_DELIMITER);
for(var l_nObject=0,l_nSize=l_pObjectNames.length;l_nObject<l_nSize;++l_nObject){var l_sObjectName=l_pObjectNames[l_nObject];
var l_sObjectKey=l_sObjectName+l_oFilterAndFieldList.m_sFilter;
var l_oContainedObjects=this.m_oContainerObjects[l_sObjectKey];
if(l_oContainedObjects!=null){var l_sProxyListenerId=this.m_oContainerProxySubscribers[l_sObjectKey][A];
if(l_sProxyListenerId!=null){for(l_sContainedObjectName in l_oContainedObjects){this.removeObject(l_sProxyListenerId,l_sContainedObjectName,"");}}delete this.m_oContainerProxySubscribers[l_sObjectKey][A];}var l_bDiscardFromServer=this.removeObject(A,l_sObjectName,l_oFilterAndFieldList.m_sFilter,l_oFilterAndFieldList.m_sFieldList);
if(l_bDiscardFromServer==true){l_sValidatedObjectList+=((l_sValidatedObjectList=="") ? "" : SL4B_AbstractRttpProvider.const_OBJECT_NAME_DELIMITER)+l_pObjectNames[l_nObject];delete this.m_oContainerObjects[l_sObjectKey];delete this.m_oContainerProxySubscribers[l_sObjectName];}}if(l_sValidatedObjectList!=""){this.m_oObjectCache.discardObjects(l_sValidatedObjectList,l_oFilterAndFieldList.m_sFilter);}}
function SL_LW(D,C,B,A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectSubscriptionManager.addObject: {0}, {1}, {2}, {3}",D,C,B,A);var l_bIsValidObject=this.isValidObject(C);
if(l_bIsValidObject){var l_sObjectKey=C+B;
if(this.m_pObjectSubscriptions[l_sObjectKey]===undefined){this.m_pObjectSubscriptions[l_sObjectKey]=new Object();}if(this.m_pObjectSubscriptions[l_sObjectKey][D]===undefined){this.m_pObjectSubscriptions[l_sObjectKey][D]=new SL4B_ObjectSubscription(D,C,B);}this.m_pObjectSubscriptions[l_sObjectKey][D].addFields(A);}return l_bIsValidObject;
}
function SL_CO(A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectSubscriptionManager.removeSubscriber: {0}",A);for(l_sObject in this.m_pObjectSubscriptions){for(l_sCurrListenerId in this.m_pObjectSubscriptions[l_sObject]){if(l_sCurrListenerId==A){this.discardObject(A,l_sObject,SL4B_ObjectSubscription.const_ALL_FIELDS);}}}}
SL4B_ObjectSubscriptionManager.prototype._isMapEmpty = function(A){for(sKey in A){return false;
}return true;
};
function SL_QY(D,C,B,A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectSubscriptionManager.removeObject: {0}, {1}, {2}",D,C,A);if(!this.isValidObject(C)){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ObjectSubscriptionManager.removeObject: attempt to remove an invalid object ({0}).",C);return false;
}var l_sObjectKey=C+B;
if(this.m_pObjectSubscriptions[l_sObjectKey]===undefined){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ObjectSubscriptionManager.removeObject: attempt to remove an object ({0}) that is not currently subscribed to",C);return false;
}else 
if(this.m_pObjectSubscriptions[l_sObjectKey][D]===undefined){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ObjectSubscriptionManager.removeObject: attempt to remove an object ({0}) that is not currently subscribed to by the specified listener ({1})",C,D);return false;
}var l_oObjectSubscription=this.m_pObjectSubscriptions[l_sObjectKey][D];
var oThisSubscriptionFields=l_oObjectSubscription.getFieldMask().clone();
var bChanged=l_oObjectSubscription.removeFields(A);
if(l_oObjectSubscription.hasNoFields()){delete this.m_pObjectSubscriptions[l_sObjectKey][D];if(this._isMapEmpty(this.m_pObjectSubscriptions[l_sObjectKey])){delete this.m_pObjectSubscriptions[l_sObjectKey];}if(!l_oObjectSubscription.isProxySubscription()){if(this.m_pObjectSubscriptions[l_sObjectKey]){for(l_oListenerId in this.m_pObjectSubscriptions[l_sObjectKey]){var l_oSubscriber=this.m_pObjectSubscriptions[l_sObjectKey][l_oListenerId].m_oSubscriber;
if(!l_oSubscriber.m_bIsProxySubscriber){return false;
}}}return true;
}}return false;
}
function SL_AM(A){this.m_oObjectCache=A;}
function SL_GJ(A,C,D,B){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectSubscriptionManager.recordMultiUpdated: {0}, {1}, {2}",A,C,D);if(typeof (B)!="undefined"){this.recordMultiUpdated2(A,C,D,B);}else 
{for(l_sListenerId in this.m_pObjectSubscriptions[A]){this.recordMultiUpdated2(A,C,D,l_sListenerId);}}}
function SL_IV(D,A,C,B){var l_oSubscription=this.m_pObjectSubscriptions[D][B];
var l_bAllFields=l_oSubscription.isSubscribedToAllFields();
var l_oRecordFieldData=null;
if(l_bAllFields){l_oRecordFieldData=A;}else 
{l_oRecordFieldData=new SL4B_RecordFieldData();for(var l_nCount=0,l_nSize=A.size();l_nCount<l_nSize;l_nCount++){var l_sFieldName=A.getFieldName(l_nCount);
if(l_oSubscription.isSubscribedToField(l_sFieldName)){var l_sFieldValue=A.getFieldValue(l_nCount);
l_oRecordFieldData.add(l_sFieldName,l_sFieldValue);}}}if(l_oRecordFieldData.size()!=0){var sObjectName=l_oSubscription.m_sObjectName.replace(/^[A-Za-z0-9_-]{4}:/,"");
SL4B_MethodInvocationProxy.invoke(l_oSubscription.m_oSubscriber,"recordMultiUpdated",[sObjectName,l_oRecordFieldData,C]);}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectSubscriptionManager.recordMultiUpdated2: didn't invoke record multiupdated, as there were no updated fields {0}",l_oSubscription.m_sObjectName);}}
function SL_DF(B,A,C){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectSubscriptionManager.deleteType2Level: {0}, {1}, {2}",B,A,C);for(l_sListenerId in this.m_pObjectSubscriptions[B]){var l_oSubscription=this.m_pObjectSubscriptions[B][l_sListenerId];
l_oSubscription.m_oSubscriber.WTFieldDeleted(l_oSubscription.m_sObjectName,A,C);}}
function SL_GM(A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectSubscriptionManager.type2Clear: {0}",A);for(l_sListenerId in this.m_pObjectSubscriptions[A]){var l_oSubscription=this.m_pObjectSubscriptions[A][l_sListenerId];
l_oSubscription.m_oSubscriber.WTType2Clear(l_oSubscription.m_sObjectName);}}
function SL_DU(A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectSubscriptionManager.type3Clear: {0}",A);for(l_sListenerId in this.m_pObjectSubscriptions[A]){var l_oSubscription=this.m_pObjectSubscriptions[A][l_sListenerId];
l_oSubscription.m_oSubscriber.WTType3Clear(l_oSubscription.m_sObjectName);}}
function SL_JX(A,B){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectSubscriptionManager.clearAllSubscriptions: {0}",A);for(l_sListenerId in this.m_pObjectSubscriptions[A]){var l_oSubscription=this.m_pObjectSubscriptions[A][l_sListenerId];
if(B==SL4B_RttpCodes.const_NOT_FOUND_DELAY||B==SL4B_RttpCodes.const_NOT_FOUND){l_oSubscription.m_oSubscriber.WTObjectNotFound(l_oSubscription.m_sObjectName);}else 
if(B==SL4B_RttpCodes.const_READ_DENY_DELAY||B==SL4B_RttpCodes.const_READ_DENY){l_oSubscription.m_oSubscriber.WTObjectReadDenied(l_oSubscription.m_sObjectName);}else 
if((B>SL4B_RttpCodes.const_READ_DENY_DELAY&&B<SL4B_RttpCodes.const_WRITE_DENY_DELAY)||(B>SL4B_RttpCodes.const_READ_DENY&&B<SL4B_RttpCodes.const_WRITE_DENY)){l_oSubscription.m_oSubscriber.WTObjectReadDenied(l_oSubscription.m_sObjectName,(B%10));}else 
if(B==SL4B_RttpCodes.const_WRITE_DENY_DELAY||B==SL4B_RttpCodes.const_WRITE_DENY){l_oSubscription.m_oSubscriber.WTObjectWriteDenied(l_oSubscription.m_sObjectName);}else 
if((B>SL4B_RttpCodes.const_WRITE_DENY_DELAY&&B<SL4B_RttpCodes.const_UNAVAILABLE_DELAY)||(B>SL4B_RttpCodes.const_WRITE_DENY&&B<SL4B_RttpCodes.const_UNAVAILABLE)){l_oSubscription.m_oSubscriber.WTObjectWriteDenied(l_oSubscription.m_sObjectName,(B%10));}else 
if(B==SL4B_RttpCodes.const_UNAVAILABLE_DELAY||B==SL4B_RttpCodes.const_UNAVAILABLE){l_oSubscription.m_oSubscriber.WTObjectUnavailable(l_oSubscription.m_sObjectName);}else 
if(B==SL4B_RttpCodes.const_DELETED_DELAY){l_oSubscription.m_oSubscriber.WTObjectDeleted(l_oSubscription.m_sObjectName);}}this._removeAllSubscriptionListenersForObjectKey(A);}
SL4B_ObjectSubscriptionManager.prototype._removeAllSubscriptionListenersForObjectKey = function(A){delete this.m_pObjectSubscriptions[A];if(this._isContainerObjectKey(A)){delete this.m_oContainerObjects[A];delete this.m_oContainerProxySubscribers[A];delete this.m_oContainerIdToRequestDataMap[this._extractContainerIdFromObjectKey(A)];}};
SL4B_ObjectSubscriptionManager.prototype._isContainerObjectKey = function(A){return (A!==null&&A.match(/ctrid=(\d+)/)!==null);
};
SL4B_ObjectSubscriptionManager.prototype._extractContainerIdFromObjectKey = function(A){var oMatch=A.match(/ctrid=(\d+)/);
return ((oMatch==null) ? null : oMatch[1]);
};
function SL_FE(C,D,A,B,E){if(typeof (E)!="undefined"){var l_oSubscription=this.m_pObjectSubscriptions[C][E];
l_oSubscription.m_oSubscriber.WTDirUpdated(l_oSubscription.m_sObjectName,D,A,B);}else 
{for(l_sListenerId in this.m_pObjectSubscriptions[C]){var l_oSubscription=this.m_pObjectSubscriptions[C][l_sListenerId];
l_oSubscription.m_oSubscriber.WTDirUpdated(l_oSubscription.m_sObjectName,D,A,B);}}}
function SL_ON(A,B){for(l_sListenerId in this.m_pObjectSubscriptions[A]){var l_oSubscription=this.m_pObjectSubscriptions[A][l_sListenerId];
l_oSubscription.m_oSubscriber.WTDirMultiUpdated(l_oSubscription.m_sObjectName,B);}}
function SL_PU(A,D,C,B){for(l_sListenerId in this.m_pObjectSubscriptions[A]){var l_oSubscription=this.m_pObjectSubscriptions[A][l_sListenerId];
var sObjectName=l_oSubscription.m_sObjectName.replace(/^[A-Za-z0-9_-]{4}:/,"");
l_oSubscription.m_oSubscriber.WTNewsUpdated(sObjectName,D,C,B);}}
function SL_IO(B,C,D,A){for(l_sListenerId in this.m_pObjectSubscriptions[B]){var l_oSubscription=this.m_pObjectSubscriptions[B][l_sListenerId];
l_oSubscription.m_oSubscriber.WTObjectInfo(l_oSubscription.m_sObjectName,C,D,A);}}
function SL_BN(B,A){for(l_sListenerId in this.m_pObjectSubscriptions[B]){var l_oSubscription=this.m_pObjectSubscriptions[B][l_sListenerId];
for(var i=0;i<A.length;i++){if(i||A[i]){l_oSubscription.m_oSubscriber.WTStoryUpdated(B,A[i]||"\\n");}}}}
function SL_NK(A,B,C){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectSubscriptionManager.permissionUpdated: {0}, {1}, {2}",A,B,C);for(l_sListenerId in this.m_pObjectSubscriptions[A]){var l_oSubscription=this.m_pObjectSubscriptions[A][l_sListenerId];
var l_bAllFields=l_oSubscription.isSubscribedToAllFields();
var l_oSubscribedFieldData=null;
if(l_bAllFields){l_oSubscribedFieldData=C;}else 
{l_oSubscribedFieldData=new SL4B_RecordFieldData();for(var l_nCount=0,l_nSize=C.size();l_nCount<l_nSize;l_nCount++){var l_sFieldName=C.getFieldName(l_nCount);
var l_sFieldValue=C.getFieldValue(l_nCount);
if(l_oSubscription.isSubscribedToField(l_sFieldName)){l_oSubscribedFieldData.add(l_sFieldName,l_sFieldValue);}}}if(l_oSubscribedFieldData.size()!=0){l_oSubscription.m_oSubscriber.WTPermissionUpdated(l_oSubscription.m_sObjectName,B,l_oSubscribedFieldData);}}}
function SL_FR(A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectSubscriptionManager.deleteAllPermissionEntries: {0}",A);for(l_sListenerId in this.m_pObjectSubscriptions[A]){var l_oSubscription=this.m_pObjectSubscriptions[A][l_sListenerId];
l_oSubscription.m_oSubscriber.WTPermissionDeleted(l_oSubscription.m_sObjectName,null);}}
function SL_EA(A,B){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectSubscriptionManager.deleteAllPermissionEntries: {0}, {1}",A,B);for(l_sListenerId in this.m_pObjectSubscriptions[A]){var l_oSubscription=this.m_pObjectSubscriptions[A][l_sListenerId];
l_oSubscription.m_oSubscriber.WTPermissionDeleted(l_oSubscription.m_sObjectName,B);}}
function SL_MY(B,C,A,D){for(l_sListenerId in this.m_pObjectSubscriptions[B]){this.objectStatusForListener(B,C,A,D,l_sListenerId);}}
SL4B_ObjectSubscriptionManager.prototype.objectStatusForListener = function(B,C,A,D,E){var l_oSubscription=this.m_pObjectSubscriptions[B][E];
var l_sObjectName=l_oSubscription.m_sObjectName.replace(/^[A-Za-z0-9_-]{4}:/,"");
l_oSubscription.m_oSubscriber.WTObjectStatus(l_sObjectName,C,A,D);};
SL4B_ObjectSubscriptionManager.prototype.clearSnapshot = function(A){var l_oContainedObjects=this.m_oContainerObjects[A];
for(l_sListenerId in this.m_pObjectSubscriptions[A]){var l_sProxyListenerId=this.m_oContainerProxySubscribers[A][l_sListenerId];
if(l_sProxyListenerId!=null){for(l_sContainedObjectName in l_oContainedObjects){this.removeObject(l_sProxyListenerId,l_sContainedObjectName,"");var l_sOriginalContainedObjectName=l_sContainedObjectName.replace(/^[A-Za-z0-9_-]{4}:/,"");
this.m_oObjectCache.removeObjectFromCacheIfNoMoreReferences(l_sOriginalContainedObjectName,"");}}}delete this.m_oContainerProxySubscribers[A];delete this.m_pObjectSubscriptions[A];delete this.m_oContainerObjects[A];};
SL4B_ObjectSubscriptionManager.prototype.chat = function(A,C,D,B,E,F){if(F===undefined){for(l_sStoredListenerId in this.m_pObjectSubscriptions[A]){this.chat(A,C,D,B,E,l_sStoredListenerId);}}else 
{var l_oSubscription=this.m_pObjectSubscriptions[A][F];
l_oSubscription.m_oSubscriber.WTChat(l_oSubscription.m_sObjectName,C,D,B,E);}};
SL4B_ObjectSubscriptionManager.prototype._getConnectionManager = function(){return this.m_oConnectionListener;
};
SL4B_ObjectSubscriptionManager.prototype.setRttpProvider = function(A){A.addConnectionListener(this.m_oConnectionListener);};
var LF_ObjectSubscriptionManagerConnectionListener=function(A){this.m_oObjectSubscriptionManager=A;};
if(false){function LF_ObjectSubscriptionManagerConnectionListener(){}
}LF_ObjectSubscriptionManagerConnectionListener.prototype = new SL4B_ConnectionListener;LF_ObjectSubscriptionManagerConnectionListener.prototype.connectionWarning = function(C,D,B,A){this.m_oObjectSubscriptionManager.sendStaleToAll();};
function SL_JN(){return this.m_pObjectSubscriptions;
}
function SL_EO(){return this.m_oContainerIdToRequestDataMap;
}
function GF_ActionSubscriptionManager(){this.CLASS_NAME="GF_ActionSubscriptionManager";this.m_pPreSubscriptionPersistedActions=[];this.m_pPostSubscriptionPersistedActions=[];}
SL4B_ActionSubscriptionManager=GF_ActionSubscriptionManager;GF_ActionSubscriptionManager.prototype.sendActionMessage = SL_KW;GF_ActionSubscriptionManager.prototype.contribObject = SL_RZ;GF_ActionSubscriptionManager.prototype.createObject = SL_LI;GF_ActionSubscriptionManager.prototype.deleteObject = SL_RP;GF_ActionSubscriptionManager.prototype.throttleObjects = SL_MK;GF_ActionSubscriptionManager.prototype.throttleGlobal = SL_KN;GF_ActionSubscriptionManager.NULL_SUBSCRIBER=new SL4B_AbstractSubscriber();GF_ActionSubscriptionManager.NULL_SUBSCRIBER.ID="SL4B_ActionSubscriptionManager.NULL_SUBSCRIBER";GF_ActionSubscriptionManager.NULL_SUBSCRIBER.contribOk = function(){};
GF_ActionSubscriptionManager.NULL_SUBSCRIBER.contribFailed = function(){};
GF_ActionSubscriptionManager.NULL_SUBSCRIBER.createOk = function(){};
GF_ActionSubscriptionManager.NULL_SUBSCRIBER.createFailed = function(){};
GF_ActionSubscriptionManager.NULL_SUBSCRIBER.deleteOk = function(){};
GF_ActionSubscriptionManager.NULL_SUBSCRIBER.deleteFailed = function(){};
function SL_KW(A,B){SL4B_Accessor.getUnderlyingRttpProvider().getManagedConnection().sendMessage(B,A);}
SL4B_ActionSubscriptionManager.prototype.cancelPersistedAction = function(A){if(this._isValidPersistentActionKey(A)){var pList=this._getPersistentActionList(A._$getActionPersistence());
return this._removePersistedActionFromList(pList,A);
}return false;
};
SL4B_ActionSubscriptionManager.prototype._isValidPersistentActionKey = function(A){return (A&&A._$getActionPersistence);
};
SL4B_ActionSubscriptionManager.prototype._removePersistedActionFromList = function(B,A){for(var i=0,nLength=B.length;i<nLength;++i){if(B[i]===A){B=B.splice(i,1);return true;
}}return false;
};
SL4B_ActionSubscriptionManager.prototype.resendPersistedAction = function(A){if(this._isValidPersistentActionKey(A)&&this._isActivePersistentActionKey(A)){this._resendPersistedAction(A);}};
SL4B_ActionSubscriptionManager.prototype._isActivePersistentActionKey = function(A){var pList=this._getPersistentActionList(A._$getActionPersistence());
for(var i=0,nLength=pList.length;i<nLength;++i){if(pList[i]===A){return true;
}}return false;
};
function SL_RZ(B,C,D,A){var l_nLength=D.size();
var pContribMessage=new Array(2+l_nLength);
pContribMessage[0]="CONTRIB ";pContribMessage[1]=GF_RequestEncoder.encodeRttpData(C);for(var l_nField=0;l_nField<l_nLength;++l_nField){var l_oField=D.getField(l_nField);
pContribMessage[2+l_nField]=" "+GF_RequestEncoder.encodeRttpData(l_oField.m_sName)+"="+GF_RequestEncoder.encodeRttpData(l_oField.m_sValue);}var l_oActionSubscription=new SL_CA(B,C,D);
var sMessage=pContribMessage.join("");
this.sendActionMessage(l_oActionSubscription,sMessage);return this._persistAction("contrib",l_oActionSubscription,sMessage,A);
}
function SL_LI(D,B,C,A){if(this._isInvocationWithoutSubscriberArugment(D)){return this._createObject(GF_ActionSubscriptionManager.NULL_SUBSCRIBER.ID,D,B,C);
}else 
{return this._createObject(D,B,C,A);
}}
SL4B_ActionSubscriptionManager.prototype._isInvocationWithoutSubscriberArugment = function(A){return (A&&A.charAt&&A.charAt(0)==="/");
};
SL4B_ActionSubscriptionManager.prototype._createObject = function(C,A,D,B){var sCreationMessage="CREATE "+GF_RequestEncoder.encodeRttpData(A)+";"+D;
var oActionSubscription=new SL_MS(C,A,D);
this.sendActionMessage(oActionSubscription,sCreationMessage);return this._persistAction("create",oActionSubscription,sCreationMessage,B);
};
function SL_RP(C,B,A){if(this._isInvocationWithoutSubscriberArugment(C)){return this._deleteObject(GF_ActionSubscriptionManager.NULL_SUBSCRIBER.ID,C,B);
}else 
{return this._deleteObject(C,B,A);
}}
SL4B_ActionSubscriptionManager.prototype._deleteObject = function(C,A,B){var sDeletionMessage="DELETE "+GF_RequestEncoder.encodeRttpData(A);
var oActionSubscription=new SL_KG(C,A);
this.sendActionMessage(oActionSubscription,sDeletionMessage);return this._persistAction("delete",oActionSubscription,sDeletionMessage,B);
};
SL4B_ActionSubscriptionManager.prototype._persistAction = function(C,B,D,A){if(A===SL4B_ActionPersistence.PERSIST_BEFORE_RESUBSCRIBING||A===SL4B_ActionPersistence.PERSIST_AFTER_RESUBSCRIBING){var oActionContext={type:C, subscription:B, message:D};
var oKey=new SL4B_PersistentActionKey(A,oActionContext);
this._getPersistentActionList(A).push(oKey);B._setPersistentActionKey(oKey);return oKey;
}B._setPersistentActionKey(null);return null;
};
SL4B_ActionSubscriptionManager.prototype._getPersistentActionList = function(A){if(A===SL4B_ActionPersistence.PERSIST_BEFORE_RESUBSCRIBING){return this.m_pPreSubscriptionPersistedActions;
}return this.m_pPostSubscriptionPersistedActions;
};
function SL_MK(A,B){if(SL4B_ThrottleLevel.isValid(B)===false){throw new SL4B_Exception("'"+B+"' is not a valid throttle level.");
}var l_pObjectNames=A.split(SL4B_AbstractRttpProvider.const_OBJECT_NAME_DELIMITER);
for(var i=0;i<l_pObjectNames.length;i++){l_pObjectNames[i]=GF_RequestEncoder.encodeRttpData(l_pObjectNames[i]);}var l_sThrottleMessage="THROTTLE "+l_pObjectNames.join(";"+B+" ")+";"+B;
this.sendActionMessage(new SL_DC(B,false),l_sThrottleMessage);}
function SL_KN(A){if(SL4B_ThrottleLevel.isValid(A)===false){throw new SL4B_Exception("'"+A+"' is not a valid throttle level.");
}this.sendActionMessage(new SL_DC(A,true),"GLOBAL_THROTTLE "+A);}
SL4B_ActionSubscriptionManager.prototype.resendPersistedActionsBeforeResubscription = function(){this._resendPersistedActions(this._getPersistentActionList(SL4B_ActionPersistence.PERSIST_BEFORE_RESUBSCRIBING));};
SL4B_ActionSubscriptionManager.prototype.resendPersistedActionsAfterResubscription = function(){this._resendPersistedActions(this._getPersistentActionList(SL4B_ActionPersistence.PERSIST_AFTER_RESUBSCRIBING));};
SL4B_ActionSubscriptionManager.prototype._resendPersistedActions = function(A){for(var i=0,nLength=A.length;i<nLength;++i){this._resendPersistedAction(A[i]);}};
SL4B_ActionSubscriptionManager.prototype._resendPersistedAction = function(A){var mAction=A._$getActionContext();
this.sendActionMessage(mAction.subscription,mAction.message);};
function SL_NM(A){this.m_oSubscriber=this._getSubscriberFromId(A);this.m_oPersistentActionKey=null;}
SL_NM.prototype._getSubscriberFromId = function(A){if(A){return eval(A);
}else 
{return GF_ActionSubscriptionManager.NULL_SUBSCRIBER;
}};
SL_NM.prototype.receiveMessage = function(A,B){throw new SL4B_Error("AbstractActionSubscription.receiveMessage: method not implemented");
};
SL_NM.prototype._setPersistentActionKey = function(A){this.m_oPersistentActionKey=A;};
SL_NM.prototype.getFailureDescription = function(A){var l_sDescription;
switch(A){
case SL4B_RttpCodes.const_NOT_FOUND:case SL4B_RttpCodes.const_NOT_FOUND_DELAY:l_sDescription="Object not found";break;
case SL4B_RttpCodes.const_UNAVAILABLE:case SL4B_RttpCodes.const_UNAVAILABLE_DELAY:l_sDescription="Object unavailable";break;
case SL4B_RttpCodes.const_DELETED_DELAY:case SL4B_RttpCodes.const_CONTRIB_FAILED:default :if((A>=SL4B_RttpCodes.const_READ_DENY_DELAY&&A<SL4B_RttpCodes.const_WRITE_DENY_DELAY)||(A>=SL4B_RttpCodes.const_READ_DENY&&A<SL4B_RttpCodes.const_WRITE_DENY)){l_sDescription="Read denied";}else 
if((A>=SL4B_RttpCodes.const_WRITE_DENY_DELAY&&A<SL4B_RttpCodes.const_UNAVAILABLE_DELAY)||(A>=SL4B_RttpCodes.const_WRITE_DENY&&A<SL4B_RttpCodes.const_UNAVAILABLE)){l_sDescription="Write denied";}else 
{l_sDescription="Unknown failure code ("+A+")";}}return l_sDescription;
};
SL_NM.prototype.getAuthCode = function(A){var l_nAuthCode;
if((A>SL4B_RttpCodes.const_READ_DENY_DELAY&&A<SL4B_RttpCodes.const_WRITE_DENY_DELAY)||(A>SL4B_RttpCodes.const_READ_DENY&&A<SL4B_RttpCodes.const_WRITE_DENY)||(A>SL4B_RttpCodes.const_WRITE_DENY_DELAY&&A<SL4B_RttpCodes.const_UNAVAILABLE_DELAY)||(A>SL4B_RttpCodes.const_WRITE_DENY&&A<SL4B_RttpCodes.const_UNAVAILABLE)){l_nAuthCode=A%10;}return l_nAuthCode;
};
function SL_CA(C,A,B){SL_NM.call(this,C);this.m_sObjectName=A;this.m_oFieldData=B;}
SL_CA.prototype = new SL_NM;SL_CA.prototype.receiveMessage = function(B,A){var l_nRttpCode=B.getRttpCode();
switch(l_nRttpCode){
case SL4B_RttpCodes.const_CONTRIB_OK:case SL4B_RttpCodes.const_CONTRIB_OK_DELAY:this.m_oSubscriber.WTContribOk(this.m_sObjectName,this.m_oPersistentActionKey);break;
case SL4B_RttpCodes.const_CONTRIB_WAIT:break;
default :var nAuthCode=this.getAuthCode(l_nRttpCode);
this.m_oSubscriber.WTContribFailed(this.m_sObjectName,this.getFailureDescription(l_nRttpCode),nAuthCode,this.m_oPersistentActionKey);break;
}};
function SL_MS(C,A,B){SL_NM.call(this,C);this.m_sObjectName=A;this.m_sRttpType=B;}
SL_MS.prototype = new SL_NM;SL_MS.prototype.receiveMessage = function(B,A){var l_nRttpCode=B.getRttpCode();
switch(l_nRttpCode){
case SL4B_RttpCodes.const_CREATE_OK:this.m_oSubscriber.WTCreateOk(this.m_sObjectName,this.m_oPersistentActionKey);break;
default :var nAuthCode=this.getAuthCode(l_nRttpCode);
this.m_oSubscriber.WTCreateFailed(this.m_sObjectName,this.getFailureDescription(l_nRttpCode),nAuthCode,this.m_oPersistentActionKey);break;
}};
function SL_DC(A,B){this.m_sThrottleStatus=A;this.m_bIsGlobal=B;}
SL_DC.prototype = new SL_NM;SL_DC.prototype.receiveMessage = function(B,A){var l_nRttpCode=B.getRttpCode();
switch(l_nRttpCode){
case SL4B_RttpCodes.const_THROTTLE_OK:break;
default :break;
}};
function SL_KG(B,A){SL_NM.call(this,B);this.m_sObjectName=A;}
SL_KG.prototype = new SL_NM;SL_KG.prototype.receiveMessage = function(B,A){var l_nRttpCode=B.getRttpCode();
switch(l_nRttpCode){
case SL4B_RttpCodes.const_DELETE_OK:this.m_oSubscriber.WTDeleteOk(this.m_sObjectName,this.m_oPersistentActionKey);break;
default :var nAuthCode=this.getAuthCode(l_nRttpCode);
this.m_oSubscriber.WTDeleteFailed(this.m_sObjectName,this.getFailureDescription(l_nRttpCode),nAuthCode,this.m_oPersistentActionKey);break;
}};
var SL4B_ObjectSubscription=function(){};
if(false){function SL4B_ObjectSubscription(){}
}SL4B_ObjectSubscription = function(A,D,C,B){this.CLASSNAME="SL4B_ObjectSubscription";this.m_sSubscriptionId=A;this.m_oSubscriber=eval(A);this.m_sObjectName=D;this.m_sFilter=C;this.m_oFieldMask=new SL4B_FieldMask();if(typeof (B)!="undefined"){this.addFields(B);}};
SL4B_ObjectSubscription.prototype.toString = function(){return "{obj="+this.m_sObjectName+" fields="+this.m_oFieldMask+" filter="+this.m_sFilter+" }";
};
SL4B_ObjectSubscription.const_ALL_FIELDS="";SL4B_ObjectSubscription.isAllFields = function(A){return (typeof A=="undefined"||A==null||A==SL4B_ObjectSubscription.const_ALL_FIELDS);
};
SL4B_ObjectSubscription.prototype.addFields = SL_AQ;SL4B_ObjectSubscription.prototype.removeFields = SL_BK;SL4B_ObjectSubscription.prototype.isSubscribedToField = SL_MN;SL4B_ObjectSubscription.prototype.isSubscribedToAllFields = function(){return this.m_oFieldMask.isSubscribedToAll();
};
SL4B_ObjectSubscription.prototype.hasNoFields = function(){return this.m_oFieldMask.isEmpty();
};
SL4B_ObjectSubscription.prototype.isProxySubscription = function(){return this.m_oSubscriber.m_bIsProxySubscriber;
};
SL4B_ObjectSubscription.prototype.getFieldMask = function(){return this.m_oFieldMask;
};
SL4B_ObjectSubscription.prototype.getSubscriptionId = SL_AO;SL4B_ObjectSubscription.prototype.getFilter = SL_CY;SL4B_ObjectSubscription.prototype.getSubscriber = SL_QV;SL4B_ObjectSubscription.prototype.getObjectName = SL_LH;function SL_AQ(A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectSubscription.addFields: {0}",A);if(SL4B_ObjectSubscription.isAllFields(A)){this.m_oFieldMask.addAll();}else 
{var pFields=A.split(",");
for(var i=0;i<pFields.length;++i){var sField=pFields[i];
this.m_oFieldMask.add(sField);}}}
function SL_BK(A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ObjectSubscription.removeFields: {0}",A);var bChanged=false;
if(SL4B_ObjectSubscription.isAllFields(A)){return this.m_oFieldMask.removeAll();
}else 
{var pFields=A.split(",");
for(var i=0;i<pFields.length;++i){var sField=pFields[i];
bChanged=this.m_oFieldMask.remove(sField)||bChanged;}}return bChanged;
}
function SL_MN(A){return this.m_oFieldMask.contains(A);
}
function SL_CI(){return this.m_oFieldMask.isSubscribedToAll();
}
function GF_CachedObjectStatus(E,D,F){this.m_nType=E;this.m_nCode=D;this.m_sMessage=F;this.updateStatus = function(B,A,C){this.m_nType=B;this.m_nCode=A;this.m_sMessage=C;};
}
GF_CachedObjectStatus.const_CONNECTION_LOST=new GF_CachedObjectStatus(SL4B_ObjectStatus.STALE,0,"Liberator connection lost");GF_CachedObjectStatus.const_SUBSCRIBING=new GF_CachedObjectStatus(SL4B_ObjectStatus.STALE,0,"Subscribing to object");function SL_AO(){return this.m_sSubscriptionId;
}
function SL_CY(){return this.m_sFilter;
}
function SL_QV(){return this.m_oSubscriber;
}
function SL_LH(){return this.m_sObjectName;
}
var SL4B_RttpMessage=function(){};
if(false){function SL4B_RttpMessage(){}
}SL4B_RttpMessage = function(){this.m_nRttpCode=null;this.m_sSequenceNumber=null;this.m_sObjectNumber=null;this.m_sMessageContent=null;this.m_pMultipleLineContents=null;this.m_bIsMessageComplete=true;};
SL4B_RttpMessage.const_RTTP_CODE_SIZE=2;SL4B_RttpMessage.const_SEQUENCE_NUMBER_SIZE=2;SL4B_RttpMessage.const_OBJECT_NUMBER_SIZE=4;SL4B_RttpMessage.const_MAXIMUM_MESSAGE_CODE_SIZE=SL4B_RttpMessage.const_RTTP_CODE_SIZE+SL4B_RttpMessage.const_SEQUENCE_NUMBER_SIZE+SL4B_RttpMessage.const_OBJECT_NUMBER_SIZE;SL4B_RttpMessage.const_MESSAGE_CODE_END=" ";SL4B_RttpMessage.const_NO_MESSAGE_CONTENT="";SL4B_RttpMessage.const_MULTIPLE_LINE_MESSAGE_START="-";SL4B_RttpMessage.const_MULTIPLE_LINE_MESSAGE_END=".";SL4B_RttpMessage.prototype.setMessage = SL_CG;SL4B_RttpMessage.prototype.internalConstructor = SL_NH;SL4B_RttpMessage.prototype.isMessageComplete = function(){return this.m_bIsMessageComplete;
};
SL4B_RttpMessage.prototype.getRttpCode = function(){return this.m_nRttpCode;
};
SL4B_RttpMessage.prototype.getSequenceNumber = function(){return this.m_sSequenceNumber;
};
SL4B_RttpMessage.prototype.getObjectNumber = function(){return this.m_sObjectNumber;
};
SL4B_RttpMessage.prototype.getMessageContent = function(){return this.m_sMessageContent;
};
SL4B_RttpMessage.prototype.isMultipleLineMessage = function(){return (this.m_pMultipleLineContents!=null);
};
SL4B_RttpMessage.prototype.getMultipleLineContents = function(){return this.m_pMultipleLineContents;
};
SL4B_RttpMessage.prototype.toString = function(){return this.m_nRttpCode+((this.m_sSequenceNumber==null) ? "" : " {"+this.m_sSequenceNumber+"}")+((this.m_sObjectNumber==null) ? "" : " ["+this.m_sObjectNumber+"]")+": "+this.m_sMessageContent;
};
function SL_CG(B,A){if(this.m_bIsMessageComplete){this.m_nRttpCode=null;this.m_sSequenceNumber=null;this.m_sObjectNumber=null;this.m_sMessageContent=SL4B_RttpMessage.const_NO_MESSAGE_CONTENT;this.m_pMultipleLineContents=null;}return this.internalConstructor(B,A);
}
function SL_NH(B,A){if(A>=B.length){throw new SL4B_Exception("An illegal message index ("+A+") was received");
}var l_sRttpMessage=B[A];
if(l_sRttpMessage==null){throw new SL4B_Exception("A null RTTP message was received");
}var l_bIsMultipleLineMessage=false;
if(!this.m_bIsMessageComplete){l_bIsMultipleLineMessage=true;}else 
{var l_nMessageCodeEnd=l_sRttpMessage.indexOf(SL4B_RttpMessage.const_MESSAGE_CODE_END);
if(l_nMessageCodeEnd==-1){var l_nMessageLength=l_sRttpMessage.length;
if(l_sRttpMessage.charAt(l_nMessageLength-1)==SL4B_RttpMessage.const_MULTIPLE_LINE_MESSAGE_START&&l_nMessageLength!=2&&l_nMessageLength!=4&&l_nMessageLength!=6&&l_nMessageLength!=8){l_bIsMultipleLineMessage=true;l_nMessageCodeEnd=l_sRttpMessage.length-1;}else 
{l_nMessageCodeEnd=l_sRttpMessage.length;}}if(l_nMessageCodeEnd>SL4B_RttpMessage.const_MAXIMUM_MESSAGE_CODE_SIZE){if(l_sRttpMessage.charAt(SL4B_RttpMessage.const_MAXIMUM_MESSAGE_CODE_SIZE)==SL4B_RttpMessage.const_MULTIPLE_LINE_MESSAGE_START){l_nMessageCodeEnd=SL4B_RttpMessage.const_MAXIMUM_MESSAGE_CODE_SIZE;l_bIsMultipleLineMessage=true;}else 
if(l_sRttpMessage.charAt(6)==SL4B_RttpMessage.const_MULTIPLE_LINE_MESSAGE_START){l_nMessageCodeEnd=6;l_bIsMultipleLineMessage=true;}else 
if(l_sRttpMessage.charAt(4)==SL4B_RttpMessage.const_MULTIPLE_LINE_MESSAGE_START){l_nMessageCodeEnd=4;l_bIsMultipleLineMessage=true;}else 
if(l_sRttpMessage.charAt(2)==SL4B_RttpMessage.const_MULTIPLE_LINE_MESSAGE_START){l_nMessageCodeEnd=2;l_bIsMultipleLineMessage=true;}}var l_nPosition=SL4B_RttpMessage.const_RTTP_CODE_SIZE;
switch(l_nMessageCodeEnd){
case 2:break;
case 4:this.m_sSequenceNumber=l_sRttpMessage.substr(l_nPosition,SL4B_RttpMessage.const_SEQUENCE_NUMBER_SIZE);l_nPosition+=SL4B_RttpMessage.const_SEQUENCE_NUMBER_SIZE;break;
case 6:this.m_sObjectNumber=l_sRttpMessage.substr(l_nPosition,SL4B_RttpMessage.const_OBJECT_NUMBER_SIZE);l_nPosition+=SL4B_RttpMessage.const_OBJECT_NUMBER_SIZE;break;
case 8:this.m_sSequenceNumber=l_sRttpMessage.substr(l_nPosition,SL4B_RttpMessage.const_SEQUENCE_NUMBER_SIZE);l_nPosition+=SL4B_RttpMessage.const_SEQUENCE_NUMBER_SIZE;this.m_sObjectNumber=l_sRttpMessage.substr(l_nPosition,SL4B_RttpMessage.const_OBJECT_NUMBER_SIZE);l_nPosition+=SL4B_RttpMessage.const_OBJECT_NUMBER_SIZE;break;
default :throw new SL4B_Exception("Unknown RTTP message received '"+l_sRttpMessage+"'");
}this.m_nRttpCode=GF_Base64Decoder.decodeRttpCode(l_sRttpMessage.substr(0,SL4B_RttpMessage.const_RTTP_CODE_SIZE));if(l_sRttpMessage.length>=l_nPosition+1){this.m_sMessageContent=l_sRttpMessage.substr(l_nPosition+1);}}if(l_bIsMultipleLineMessage){if(this.m_bIsMessageComplete){this.m_pMultipleLineContents=new Array();}var l_nStartPos=((this.m_bIsMessageComplete) ? A+1 : A);
this.m_bIsMessageComplete=false;for(var l_nLine=l_nStartPos,l_nLength=B.length;l_nLine<l_nLength;++l_nLine){if(l_nLine>A){++A;}if(B[l_nLine]==SL4B_RttpMessage.const_MULTIPLE_LINE_MESSAGE_END){this.m_bIsMessageComplete=true;break;
}else 
{this.m_pMultipleLineContents.push(B[l_nLine]);}}}else 
{this.m_bIsMessageComplete=true;}return A;
}
var SL4B_Type1RecordCache=function(){};
if(false){function SL4B_Type1RecordCache(){}
}SL4B_Type1RecordCache = function(){this.m_pFieldCache=new Object();};
SL4B_Type1RecordCache.prototype.addField = SL4B_Type1RecordCache_AddField;SL4B_Type1RecordCache.prototype.getField = SL4B_Type1RecordCache_GetField;function SL4B_Type1RecordCache_AddField(B,A){this.m_pFieldCache[B]=A;}
function SL4B_Type1RecordCache_GetField(A){return this.m_pFieldCache[A];
}
var SL4B_Type2RecordCache=function(){};
if(false){function SL4B_Type2RecordCache(){}
}SL4B_Type2RecordCache = function(){this.m_sIndexFieldName=null;this.m_pLevelCache=new Object();};
SL4B_Type2RecordCache.prototype.addField = SL_NE;SL4B_Type2RecordCache.prototype.getField = SL_LY;SL4B_Type2RecordCache.prototype.deleteLevel = SL_HK;SL4B_Type2RecordCache.prototype.dump = SL_JY;function SL_NE(B,C,A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"Type2RecordCache.addField: {0}, {1}, {2}",B,C,A);if(typeof this.m_pLevelCache[B]=="undefined"){this.m_pLevelCache[B]=new Object();}this.m_pLevelCache[B][C]=A;}
function SL_HK(A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"Type2RecordCache.deleteLevel: {0}",A);if(typeof this.m_pLevelCache[A]!="undefined"){delete this.m_pLevelCache[A];}}
function SL_JY(){for(l_sLevel in this.m_pLevelCache){var l_oFields=this.m_pLevelCache[l_sLevel];
SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"Level: {0}",l_sLevel);for(l_sFieldName in l_oFields){var l_sValue=l_oFields[l_sFieldName];
SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"\t{0}={1}",l_sFieldName,l_sValue);}}}
function SL_LY(A,B){var l_oFieldCache=this.m_pLevelCache[A];
var l_sValue=null;
if(typeof l_oFieldCache=="undefined"){l_sValue=null;}else 
{l_sValue=l_oFieldCache[B];if(typeof l_sValue=="undefined"){l_sValue=null;}}return l_sValue;
}
function SL_OB(){this.m_pLevelCache=new Array();}
SL_OB.prototype.getField = SL_IF;SL_OB.prototype.dump = SL_BP;function SL_IF(A,B){var l_oFieldCache=this.m_pLevelCache[A];
var l_sValue=null;
if(typeof l_oFieldCache=="undefined"){l_sValue=null;}else 
{l_sValue=l_oFieldCache[B];if(typeof l_sValue=="undefined"){l_sValue=null;}}return l_sValue;
}
function SL_BP(){for(l_sLevel in this.m_pLevelCache){var l_oFields=this.m_pLevelCache[l_sLevel];
SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"Level: {0}",l_sLevel);for(l_sFieldName in l_oFields){var l_sValue=l_oFields[l_sFieldName];
SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"\t{0}={1}",l_sFieldName,l_sValue);}}}
var SL4B_NewsStoryCache=function(){};
if(false){function SL4B_NewsStoryCache(){}
}SL4B_NewsStoryCache = function(){this.m_pTextLines=new Array();};
SL4B_NewsStoryCache.prototype.isNewsStory = true;SL4B_NewsStoryCache.prototype.setTextLines = SL4B_NewsStoryCache_SetTextLines;SL4B_NewsStoryCache.prototype.addTextLines = SL4B_NewsStoryCache_AddTextLines;SL4B_NewsStoryCache.prototype.getTextLines = SL4B_NewsStoryCache_GetTextLines;function SL4B_NewsStoryCache_SetTextLines(A){if(!A||typeof A!="object"||typeof A.length=="undefined"){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"SL4B_NewsStoryCache.setTextLines: attempt to set invalid value for l_pTextLines");}else 
{this.m_pTextLines=new Array();for(var l_nLine=0,l_nLength=A.length;l_nLine<l_nLength;++l_nLine){this.m_pTextLines[l_nLine]=GF_ResponseDecoder.decodeRttpData(A[l_nLine]);}}}
function SL4B_NewsStoryCache_GetTextLines(){return this.m_pTextLines;
}
function SL4B_NewsStoryCache_AddTextLines(A){for(var i=0;i<A.length;i++){if(A[i]==""){if(this.m_pTextLines.length){this.m_pTextLines.push("\\n");}}else 
{this.m_pTextLines.push(GF_ResponseDecoder.decodeRttpData(A[i]));}}}
var SL4B_DirectoryCache=function(){};
if(false){function SL4B_DirectoryCache(){}
}SL4B_DirectoryCache = function(){this.m_pListing=new Object();};
SL4B_DirectoryCache.prototype.dump = SL_HG;function SL_HG(){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"DirectoryCache.dump()");for(l_sObject in this.m_pListing){var l_sType=this.m_pListing[l_sObject];
SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"\t{0} ({1})",l_sObject,l_sType);}}
var SL4B_ContainerCache=function(){};
if(false){function SL4B_ContainerCache(){}
}SL4B_ContainerCache = function(){this.m_bisDirectory=false;this.m_oListing=new Object();this.m_mObjectNameToNumberMap=new Object();this.m_oOrdering=new Object();this.m_oObjectNameToIdMap=new Object();this.m_nSize=0;};
SL4B_ContainerCache.prototype.addEntry = function(B,C,A){this.m_oListing[B]=C;this.m_mObjectNameToNumberMap[B]=A;};
SL4B_ContainerCache.prototype.removeEntry = function(A){delete this.m_oListing[A];delete this.m_mObjectNameToNumberMap[A];};
SL4B_ContainerCache.prototype.addOrdering = function(B,A){this.m_oOrdering[B]=A;};
SL4B_ContainerCache.prototype.getOrdering = function(A){return this.m_oOrdering[A];
};
SL4B_ContainerCache.prototype.removeOrdering = function(A){delete this.m_oOrdering[A];};
SL4B_ContainerCache.prototype.addToNameIdMap = function(A,B){this.m_oObjectNameToIdMap[A]=B;};
SL4B_ContainerCache.prototype.removeFromNameIdMap = function(A){delete this.m_oObjectNameToIdMap[A];};
SL4B_ContainerCache.prototype.getObjectNameToObjectTypeMap = function(){return this.m_oListing;
};
SL4B_ContainerCache.prototype.dump = SL_EV;SL4B_ContainerCache.prototype.getSize = function(){return this.m_nSize;
};
SL4B_ContainerCache.prototype.setSize = function(A){this.m_nSize=A;};
SL4B_ContainerCache.prototype.setDirectory = function(A){this.m_bIsDirectory=A;};
SL4B_ContainerCache.prototype.isDirectory = function(){return this.m_bIsDirectory;
};
SL4B_ContainerCache.prototype.getObjectNumber = function(A){return this.m_mObjectNameToNumberMap[A];
};
function SL_EV(){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ContainerCache.dump()");for(l_sObject in this.m_oListing){var l_sType=this.m_oListing[l_sObject];
SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"\t{0} ({1})",l_sObject,l_sType);}}
var SL4B_NewsHeadlineCache=function(){};
if(false){function SL4B_NewsHeadlineCache(){}
}SL4B_NewsHeadlineCache = function(){this.m_pHeadlines=new Array();};
SL4B_NewsHeadlineCache.m_HEADLINE_CACHE_LIMIT=200;SL4B_NewsHeadlineCache.const_SIZE_FIELD="size";SL4B_NewsHeadlineCache.prototype.addHeadline = function(C,B,A){if(this.m_pHeadlines.length>=SL4B_NewsHeadlineCache.m_HEADLINE_CACHE_LIMIT){this.m_pHeadlines.shift();}this.m_pHeadlines.push(new SL_HN(C,B,A));};
SL4B_NewsHeadlineCache.prototype.getHeadline = function(A){return ((A<this.m_pHeadlines.length) ? this.m_pHeadlines[A] : null);
};
SL4B_NewsHeadlineCache.prototype.getSize = function(){return this.m_pHeadlines.length;
};
function SL_HN(C,B,A){this.m_sStoryCode=C;this.m_sHeadline=B;this.m_sDate=A;}
function GF_ChatCache(A){this.m_sObjectKey=A;this.m_pCachedData=[];}
GF_ChatCache.prototype.addToCache = function(A){if(A[GF_ChatFields.STATUS]==SL4B_ChatStatus.SUBSCRIBED){this.m_pCachedData=[];this.m_pCachedData.push(A);}else 
if(A[GF_ChatFields.STATUS]==SL4B_ChatStatus.USER_SUBSCRIBED){if(this.m_pCachedData.length>0&&this.m_pCachedData[0][GF_ChatFields.USER]===A[GF_ChatFields.USER]){this.m_pCachedData.push(A);}}};
GF_ChatCache.prototype.sendCachedData = function(B,A){for(var i=0,nLength=this.m_pCachedData.length;i<nLength;++i){B._$sendChat(this.m_sObjectKey,this.m_pCachedData[i],A);}};
var SL4B_BaseStreamingConnection=function(){};
if(false){function SL4B_BaseStreamingConnection(){}
}SL4B_BaseStreamingConnection = function(D,C,E,B,A){this.CLASS_NAME="SL4B_BaseStreamingConnection";this.m_nConnectedState=0;this.m_nMessageCount=0;this.m_oCurrentConnectionData=C;this.m_nReconnectCount=A;this.m_sResponseFrameUrl=B;this.initialise(D,"/"+E);};
SL4B_BaseStreamingConnection.prototype = new SL4B_AbstractConnection;SL4B_BaseStreamingConnection.prototype.connect = SL_KK;SL4B_BaseStreamingConnection.prototype.getScriptPathUrl = SL_JO;SL4B_BaseStreamingConnection.prototype.reconnect = SL_LV;SL4B_BaseStreamingConnection.prototype.setResponseHttpRequest = SL_KR;SL4B_BaseStreamingConnection.prototype.start = SL_GE;SL4B_BaseStreamingConnection.prototype.super_stop = SL4B_BaseStreamingConnection.prototype.stop;SL4B_BaseStreamingConnection.prototype.stop = SL_FK;SL4B_BaseStreamingConnection.prototype.super_parseRttpMessage = SL4B_BaseStreamingConnection.prototype.parseRttpMessage;SL4B_BaseStreamingConnection.prototype.parseRttpMessage = SL_AI;SL4B_BaseStreamingConnection.prototype.super_processRttpMessageBlock = SL4B_AbstractConnection.prototype.processRttpMessageBlock;SL4B_BaseStreamingConnection.RTTP_MESSAGE_REG_EXP=/^[0-9A-Za-z-_]{2,2}([0-9A-Za-z-_]{2,2})[0-9A-Za-z-_]{4,4}/;function SL_KK(){SL4B_Logger.logConnectionMessage(false,"BaseStreamingConnection.connect");this.m_nMessageCount=0;if(this.m_nConnectedState==0&&this.m_oRequestHttpRequest!=null){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"  connecting...");var l_sResponseFrameUrl;
var l_oMatch=this.m_oCurrentConnectionData.getServerUrl().match(/(https?:\/\/[^\/]+)/);
if(l_oMatch!=null){l_sResponseFrameUrl=l_oMatch[1]+this.m_sResponseFrameUrl;}else 
{l_sResponseFrameUrl=this.m_sResponseFrameUrl;}l_sResponseFrameUrl+="?X-RTTP-Type5-Pad-Length="+SL4B_Accessor.getConfiguration().getType5PadLength();if(!SL4B_Accessor.getConfiguration().isSuppressExceptions()||SL4B_JsUnit.isUnobfuscated()){l_sResponseFrameUrl+="&suppressexceptions=false";}this.m_sResponseUniqueId=this.createChannel(SL4B_JavaScriptRttpProvider.const_RESPONSE_FRAME_ID,l_sResponseFrameUrl);this.m_nConnectedState=1;SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINE_INT,"BaseStreamingConnection.connect: Connecting to Liberator {0}",l_sResponseFrameUrl);C_CallbackQueue.addCallback(new Array(SL4B_Accessor.getUnderlyingRttpProvider(),"notifyConnectionListeners",this.m_oRttpProvider.const_INFO_CONNECTION_EVENT,"Establishing streaming connection (URL: "+l_sResponseFrameUrl+")"));}}
function SL_JO(A,B){var l_sRootUrl=SL4B_ScriptLoader.getRootUrl();
if(!l_sRootUrl.match(/^https?:\/\/./)){if(l_sRootUrl.charAt(0)=="/"){l_sRootUrl=B.match(/^https?:\/\/[^\/]*/)[0]+l_sRootUrl;}else 
{var l_sPageRoot=B.replace(/\?.*$/,"").replace(/\/[^\/]+$/,"");
l_sRootUrl=l_sPageRoot.replace(/\/$/,"")+"/"+l_sRootUrl.replace(/(^\/)/,"");}var l_oRegExp=new RegExp("\\/[^/.]+\\/\\.\\.");
var l_oMatch;
while((l_oMatch=l_sRootUrl.match(l_oRegExp))){l_sRootUrl=l_sRootUrl.replace(l_oRegExp,"");}}return l_sRootUrl+"sl4b/javascript-rttp-provider";
}
function SL_AI(A){if(this.m_nConnectedState==2){this.super_parseRttpMessage(A);this.m_nMessageCount++;}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_ERROR_INT,"RTTP message ignored due to connection state. State: {0}, Message: {1}",this.m_nConnectedState,A);}}
SL4B_BaseStreamingConnection.prototype.processRttpMessageBlock = function(A){if(this._isValidMessageBlock(A)){this.super_processRttpMessageBlock(A);}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_ERROR_INT,"Received {0} message(s) unexpectedly before logged in, first message: {1}",A.length,A[0]);}};
SL4B_BaseStreamingConnection.prototype._isValidMessageBlock = function(A){var bIsConnectedAndLoggedIn=(this.m_nConnectedState===2);
return (bIsConnectedAndLoggedIn||this._messageBlockContainsExpectedSequenceNumber(A));
};
SL4B_BaseStreamingConnection.prototype._messageBlockContainsExpectedSequenceNumber = function(A){var sCurrentEncodedSequenceNumber=this.getLastSequenceNumber();
var nNextExpectedSequenceNumber=GF_Base64Decoder.decodeNextExpectedSequenceNumber(sCurrentEncodedSequenceNumber);
for(var i=0,nLength=A.length;i<nLength;++i){var oMatch=A[i].match(SL4B_BaseStreamingConnection.RTTP_MESSAGE_REG_EXP);
if(oMatch!==null){var nMessageSequenceNumber=GF_Base64Decoder.decodeSequenceNumber(oMatch[1]);
return (nMessageSequenceNumber===nNextExpectedSequenceNumber);
}}return true;
};
SL4B_BaseStreamingConnection.prototype.messageBlockComplete = function(){if(this.m_nConnectedState==2&&this.m_nMessageCount>this.m_nReconnectCount){this.setSkipURLCheck(true);if(typeof SL4B_Accessor.getCredentialsProvider().setPreFetching=="function"){if(!SL4B_Accessor.getCredentialsProvider().isPreFetching()){
try {SL4B_Accessor.getCredentialsProvider().setPreFetching();SL4B_Accessor.getCredentialsProvider().getCredentials(this);}catch(e){SL4B_Accessor.getExceptionHandler().processException(e);}
}}else 
{this.reconnect();}}};
SL4B_BaseStreamingConnection.prototype.credentialsPreFetched = function(){this.reconnect();};
function SL_LV(){SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"BaseStreamingConnection.reconnect()");var oConnectionManager=this.m_oRttpProvider.m_oConnectionManager;
var oManagedConnection=this.m_oRttpProvider.getManagedConnection();
if(oManagedConnection._$isFullReconnectRequired()){SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"BaseStreamingConnection.reconnect: not attempting to reconnect as there are stateful responses outstanding");}else 
{SL4B_Logger.logConnectionMessage(true,"Message limit {0} has been reached (message count {1}), attempting session reconnect",this.m_nReconnectCount,this.m_nMessageCount);this._$stopResponseStream();this._clearAllPendingTimeouts();SL4B_ConnectionProxy.getInstance().stopClockSync();SL4B_ConnectionProxy.getInstance().setConnectionState(SL4B_ConnectionProxy.const_CONNECTION_STATE_RECONNECTING);this.m_nConnectedState=0;oConnectionManager.m_sPreviousSessionId=this.m_oRttpProvider.getSessionId()+"/"+this.getLastSequenceNumber();SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINE_INT,"BaseStreamingConnection.reconnect: {0}",oConnectionManager.m_sPreviousSessionId);oManagedConnection._setSessionId(null);oConnectionManager.setLoggedIn(false);this.connect();}}
function SL_KR(A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINE_INT,"BaseStreamingConnection.setResponseHttpRequest");this.m_oResponseHttpRequest=A;if(this.m_nConnectedState==1){this.m_nConnectedState=2;}}
function SL_GE(){SL4B_Logger.logConnectionMessage(false,"BaseStreamingConnection.start");this.createStandardRequestChannel();}
function SL_FK(){this.super_stop();this._$stopResponseStream();}
SL4B_BaseStreamingConnection.prototype._$stopResponseStream = function(){if(this._$getConnectionState()!==3){SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"Invoking stop on streaming frame.");var l_oFrameWindow=SL4B_Accessor.getBrowserAdapter().getFrameWindow(SL4B_JavaScriptRttpProvider.const_RESPONSE_FRAME_ID);

try {l_oFrameWindow.stop();}catch(e){SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"Attempt to invoke stop on type 5 streaming frame failed: {0}, connection state {1}",SL4B_Accessor.getBrowserAdapter().convertExceptionToString(e),this.m_nConnectedState);}
this._$setConnectionState(3);}};
SL4B_BaseStreamingConnection.prototype._$setConnectionState = function(A){this.m_nConnectedState=3;};
SL4B_BaseStreamingConnection.prototype._$getConnectionState = function(){return this.m_nConnectedState;
};
if(window.addEventListener){window.addEventListener("keypress",SL_GG,true);}else 
{document.attachEvent("onkeydown",SL_GG);}function SL_GG(A){A=A||window.event;if(A.keyCode==27){if(A.preventDefault){A.preventDefault();}else 
{A.returnValue=false;}}}
var SL4B_Type2Connection=function(){};
if(false){function SL4B_Type2Connection(){}
}SL4B_Type2Connection = function(B,A){var l_nReconnectCount=10000;
SL4B_BaseStreamingConnection.apply(this,[B,A,"RTTP-TYPE2","/sl4b/javascript-rttp-provider/streaming-type2.html",l_nReconnectCount]);this.CLASS_NAME="SL4B_Type2Connection";};
SL4B_Type2Connection.prototype = new SL4B_BaseStreamingConnection;var SL4B_Type3Connection=function(){};
if(false){function SL4B_Type3Connection(){}
}SL4B_Type3Connection = function(B,A){this.CLASS_NAME="SL4B_Type3Connection";this.m_nPollTimeout=null;this.m_oCurrentConnectionData=A;this.m_nRefreshPeriod=SL4B_Accessor.getConfiguration().getType3PollPeriod();this.initialise(B,"/RTTP-TYPE3");};
SL4B_Type3Connection.prototype = new SL4B_AbstractConnection;SL4B_Type3Connection.prototype.connect = SL_RN;SL4B_Type3Connection.prototype.super_send = SL4B_Type3Connection.prototype.send;SL4B_Type3Connection.prototype.send = SL_RE;SL4B_Type3Connection.prototype.setRequestHttpRequest = SL_NN;SL4B_Type3Connection.prototype.clearPollTimeout = SL_DY;SL4B_Type3Connection.prototype.pollServer = SL_RH;SL4B_Type3Connection.prototype.start = SL_NW;SL4B_Type3Connection.prototype.super_stop = SL4B_Type3Connection.prototype.stop;SL4B_Type3Connection.prototype.stop = SL_MM;function SL_RN(){SL4B_Logger.logConnectionMessage(false,"Type3Connection.connect");if(this.m_oRequestHttpRequest!=null&&!this.m_bConnectionStopped){this.m_oRequestHttpRequest.send(this.m_sUrlPrefix);var l_oMatch=this.m_oCurrentConnectionData.getServerUrl().match(/(https?:\/\/[^\/]+)/);
var l_sFullType3Url=((l_oMatch!=null) ? l_oMatch[1] : "")+this.m_sUrlPrefix;
C_CallbackQueue.addCallback(new Array(SL4B_Accessor.getUnderlyingRttpProvider(),"notifyConnectionListeners",this.m_oRttpProvider.const_INFO_CONNECTION_EVENT,"Establishing polling connection (URL: "+l_sFullType3Url+")"));}}
function SL_RE(A){this.clearPollTimeout();this.super_send(A);}
function SL_NN(A){this.m_oRequestHttpRequest=A;this.clearPollTimeout();this.m_nPollTimeout=setTimeout("SL4B_ConnectionProxy.getInstance().getConnection().pollServer()",this.m_nRefreshPeriod);}
function SL_DY(){if(this.m_nPollTimeout!==null){clearTimeout(this.m_nPollTimeout);this.m_nPollTimeout=null;}}
function SL_RH(){if(this.m_nPollTimeout!=null&&this.m_oRttpProvider.getSessionId()!=null){this.m_oRttpProvider.getManagedConnection()._sendPriorityMessage("",SL4B_ManagedConnection.NO_RESPONSE_EXPECTED_MESSAGE_RECEIVER,true);}}
function SL_NW(){SL4B_Logger.logConnectionMessage(false,"Type3Connection.start");this.createStandardRequestChannel();}
function SL_MM(){this.super_stop();this.clearPollTimeout();}
var SL4B_Type4Connection=function(){};
if(false){function SL4B_Type4Connection(){}
}SL4B_Type4Connection = function(B,A){this.CLASS_NAME="SL4B_Type4Connection";this.m_oCurrentConnectionData=A;var l_sUrlPrefix=B.getJsContainerUrl()+"/";
this.sResponseFrameUrl=l_sUrlPrefix+SL4B_JavaScriptRttpProvider.const_RESPONSE_FRAME_URL;this.sResponseFrameUrl+="&"+SL4B_JavaScriptRttpProviderConstants.const_INIT_PARAMETER+"=true";this.sResponseFrameUrl+="&"+SL4B_JavaScriptRttpProviderConstants.const_TYPE4_PARAMETER+"=true";this.bNeedToSendPrefix=true;this.initialise(B,"/RTTP-TYPE4");};
SL4B_Type4Connection.prototype = new SL4B_AbstractConnection();SL4B_Type4Connection.prototype.super_stop = SL4B_AbstractConnection.prototype.stop;SL4B_Type4Connection.prototype.stop = function(){if(SL4B_Accessor.getBrowserAdapter().getFrameWindow(SL4B_JavaScriptRttpProvider.const_RESPONSE_FRAME_ID).stop!==undefined){SL4B_Accessor.getBrowserAdapter().getFrameWindow(SL4B_JavaScriptRttpProvider.const_RESPONSE_FRAME_ID).stop();}this.super_stop();};
SL4B_Type4Connection.prototype.setResponseHttpRequest = function(A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINER_INT,"Type4Connection.setResponseHttpRequest");SL4B_AbstractConnection.prototype.setResponseHttpRequest.call(this,A);if(this.bNeedToSendPrefix&&(this.m_oResponseHttpRequest!=null&&!this.m_bConnectionStopped)){this.bNeedToSendPrefix=false;this.m_oResponseHttpRequest.send(this.m_sUrlPrefix);}};
SL4B_Type4Connection.prototype.connect = function(){SL4B_Logger.logConnectionMessage(false,"Type4Connection.connect");};
SL4B_Type4Connection.prototype.start = function(){SL4B_Logger.logConnectionMessage(false,"Type4Connection.start");this.m_sResponseUniqueId=this.createChannel(SL4B_JavaScriptRttpProvider.const_RESPONSE_FRAME_ID,this.sResponseFrameUrl);this.createStandardRequestChannel();};
var SL4B_Type5Connection=function(){};
if(false){function SL4B_Type5Connection(){}
}SL4B_Type5Connection = function(B,A){var l_nReconnectCount=SL4B_Accessor.getConfiguration().getType5ReconnectCount();
SL4B_BaseStreamingConnection.apply(this,[B,A,"RTTP-TYPE5","/RTTP-TYPE5",l_nReconnectCount]);this.CLASS_NAME="SL4B_Type5Connection";};
SL4B_Type5Connection.prototype = new SL4B_BaseStreamingConnection;var SL4B_Type6Connection=function(){};
if(false){function SL4B_Type6Connection(){}
}SL4B_Type6Connection = function(B,A){var l_nReconnectCount=SL4B_Accessor.getConfiguration().getType5ReconnectCount();
SL4B_BaseStreamingConnection.apply(this,[B,A,"RTTP-TYPE5","/sl4b/javascript-rttp-provider/streaming-type6.html",l_nReconnectCount]);this.CLASS_NAME="SL4B_Type6Connection";};
SL4B_Type6Connection.prototype = new SL4B_BaseStreamingConnection;var SL4B_Type7Connection=function(){};
if(false){function SL4B_Type7Connection(){}
}SL4B_Type7Connection = function(B,A){var l_nReconnectCount=SL4B_Accessor.getConfiguration().getType5ReconnectCount();
SL4B_BaseStreamingConnection.apply(this,[B,A,"RTTP-TYPE5","/sl4b/javascript-rttp-provider/streaming-type7.html",l_nReconnectCount]);this.CLASS_NAME="SL4B_Type7Connection";};
SL4B_Type7Connection.prototype = new SL4B_BaseStreamingConnection;var SL4B_WebsocketConnection=function(){};
if(false){function SL4B_WebsocketConnection(){}
}SL4B_WebsocketConnection = function(B,A){this.CLASS_NAME="SL4B_WebsocketConnection";this.socket=null;this.m_oCurrentConnectionData=A;this.m_bConnected=false;this.initialise(B,"/RTTP-TYPE8");};
SL4B_WebsocketConnection.prototype = new SL4B_AbstractConnection();SL4B_WebsocketConnection.prototype.super_stop = SL4B_AbstractConnection.prototype.stop;SL4B_WebsocketConnection.prototype.start = function(){SL4B_Logger.logConnectionMessage(false,"WebsocketConnection.start");var l_sUrl=this.m_oCurrentConnectionData.getServerUrl()+"/sl4b/javascript-rttp-provider/wscontainer.html";
C_CallbackQueue.addCallback(new Array(SL4B_Accessor.getUnderlyingRttpProvider(),"notifyConnectionListeners",this.m_oRttpProvider.const_INFO_CONNECTION_EVENT,"Establishing "+SL4B_JavaScriptRttpProvider.const_REQUEST_FRAME_ID.replace(/^frm/,"").toLowerCase()+" channel (URL: "+l_sUrl+")"));var l_sUniqueId=(new Date()).valueOf();
var l_sCommonDomain=SL4B_Accessor.getConfiguration().getCommonDomain();
if(l_sCommonDomain!=null){l_sUrl+="?"+SL4B_JavaScriptRttpProviderConstants.const_DOMAIN_PARAMETER+"="+l_sCommonDomain;}C_LiberatorUrlCheck.getLiberatorUrlCheck(SL4B_JavaScriptRttpProvider.const_REQUEST_FRAME_ID).loadUrl(l_sUrl,this.m_oRttpProvider.getJsContainerUrl(),this,this.m_bSkipURLCheck);};
SL4B_WebsocketConnection.prototype.connect = function(){SL4B_Logger.logConnectionMessage(false,"WebsocketConnection.connect");var serverUrl=this.m_oCurrentConnectionData.getServerUrl()+this.m_sUrlPrefix;
serverUrl="ws"+serverUrl.substring(4);this.socket=WSProxy;if(WSProxy==null){throw new Error("WSProxy didn't load from liberator.");
}this.socket.connect(serverUrl);var oThis=this;
this.socket.onmessage = function(A){oThis.processRttpMessageBlock(A.data.split("\n"));};
this.socket.onopen = function(){oThis.m_bConnected=true;SL4B_Accessor.getUnderlyingRttpProvider().requestHttpRequestReady();};
this.socket.onclose = function(){if(oThis.m_bConnected){SL4B_Accessor.getUnderlyingRttpProvider().createConnectionLost("Websocket connection lost.",true);oThis.m_bConnected=false;}};
};
SL4B_WebsocketConnection.prototype.send = function(A){this.socket.send(A);SL4B_Accessor.getUnderlyingRttpProvider().requestHttpRequestReady();};
SL4B_WebsocketConnection.prototype.stop = function(){this.m_bConnected=false;if(this.socket!=null){this.socket.close();}this.super_stop();};
var LF_Type5RttpMessageParserProxy=function(){this.m_oConnectionProxy=null;this.initialise = function(){this.m_oConnectionProxy=SL4B_ConnectionProxy.getInstance();};
this.processRttpMessageBlock = function(A){this.m_oConnectionProxy.processRttpMessageBlock(A);};
this.initialise();};

try {LF_Type5RttpMessageParserProxy=new LF_Type5RttpMessageParserProxy();}catch(e){}
function SL4B_StreamingType5(){this.m_pEmptyArray=new Array();this.reset();}
SL4B_StreamingType5.prototype.reset = function(){this.m_nBufferSize=0;this.m_pMessageBuffer=new Array();};
SL4B_StreamingType5.prototype.processMessagesInBuffer = function(){var l_pMessageBuffer=this.m_pMessageBuffer;
this.reset();LF_Type5RttpMessageParserProxy.processRttpMessageBlock(l_pMessageBuffer);};
SL4B_StreamingType5.prototype.processException = function(B,A){SL4B_Accessor.getLogger().log("SL4B_StreamingType5.processException: message = "+A+", exception = "+B,SL4B_DebugLevel.const_CRITICAL_INT);};
SL4B_StreamingType5=new SL4B_StreamingType5();function a(A){SL4B_StreamingType5.m_pMessageBuffer[SL4B_StreamingType5.m_nBufferSize]=A;++SL4B_StreamingType5.m_nBufferSize;}
function z(){if(SL4B_StreamingType5.m_nBufferSize>0){SL4B_MethodInvocationProxy.invoke(SL4B_StreamingType5,"processMessagesInBuffer",[],function(A){SL4B_StreamingType5.processException(A,"Exception occured whilst processing message buffer");});}}
var SL4B_TestConnection=function(){};
if(false){function SL4B_TestConnection(){}
}SL4B_TestConnection = function(B,A){this.CLASS_NAME="SL4B_TestConnection";this.m_oCurrentConnectionData=A;this.loggingEnabled=false;this.initialise(B,"/TEST");SL4B_TestConnection_Instance=this;};
SL4B_TestConnection.prototype = new SL4B_AbstractConnection();SL4B_TestConnection.prototype.super_stop = SL4B_AbstractConnection.prototype.stop;SL4B_TestConnection.prototype.start = function(){SL4B_Logger.logConnectionMessage(false,"TestConnection.start");SL4B_Accessor.getUnderlyingRttpProvider().requestHttpRequestReady();};
SL4B_TestConnection.prototype.connect = function(){SL4B_Logger.logConnectionMessage(false,"TestConnection.connect");if(window.SL4B_TestConnection_Listener_Instance){SL4B_TestConnection_Listener_Instance.onConnect();}SL4B_Accessor.getUnderlyingRttpProvider().requestHttpRequestReady();};
SL4B_TestConnection.prototype.send = function(A){var decodedMsg=GF_ResponseDecoder.decodeRttpData(A);
this.log("> "+decodedMsg);if(window.SL4B_TestConnection_Listener_Instance){SL4B_TestConnection_Listener_Instance.onSendMessage(decodedMsg);}SL4B_Accessor.getUnderlyingRttpProvider().requestHttpRequestReady();};
SL4B_TestConnection.prototype.receiveMessage = function(A,B){if(B){var self=this;
setTimeout(function(){self.log("< "+A);self.processRttpMessageBlock(A.split("\n"));},B);}else 
{this.log("< "+A);this.processRttpMessageBlock(A.split("\n"));}};
SL4B_TestConnection.prototype.setLoggingEnabled = function(A){this.loggingEnabled=A;};
SL4B_TestConnection.prototype.log = function(A){if(this.loggingEnabled){
try {console.log(A);}catch(e){}
}}, SL4B_TestConnection.prototype.disconnect = function(){SL4B_Accessor.getUnderlyingRttpProvider().createConnectionLost("Test connection lost.",true);};SL4B_TestConnection.prototype.stop = function(){this.super_stop();};
function SL4B_TestConnectionListener(){}
SL4B_TestConnectionListener.prototype.onConnect = function(){};
SL4B_TestConnectionListener.prototype.onSendMessage = function(A){};
GF_RequestEncoder = new function(){this.const_FIELD_SEPARATOR=",";this.const_NON_ENCODED_CHARACTERS="A-Za-z0-9_\\x2D.*\/:";this.m_oFilterFieldRegExp=new RegExp("(^imagefilter=|^filter=|^auto=|^monitor=|^ctrid=|^ctrstart=|^ctrend=)(.*)");this.m_pFieldSeparatorToRegExpMap=new Object();this.encodeRttpData = function(A){return encodeURIComponent(A).replace(/%20/g,"+").replace(/%2F/g,"/").replace(/%3A/g,":").replace(/\~/g,"%7E").replace(/\!/g,"%21").replace(/\(/g,"%28").replace(/\)/g,"%29").replace(/\'/g,"%27");
};
this.encodeUrl = function(A){return escape(A).replace(/(\/)/g,"%2F").replace(/\+/g,"%2B").replace(/%20/g,"+");
};
this.encodeFieldList = function(A,B){var l_sEncodedFieldList=A;
if(A&&A!=""&&A.match(this.getFieldListRegExp(B))!=null){l_sEncodedFieldList="";var l_pFields=A.split(B);
for(var l_nField=0,l_nLength=l_pFields.length;l_nField<l_nLength;++l_nField){var l_sField;
var l_oMatch=l_pFields[l_nField].match(this.m_oFilterFieldRegExp);
if(l_oMatch){l_sField=l_oMatch[1]+GF_RequestEncoder.encodeRttpData(l_oMatch[2]);}else 
{l_sField=GF_RequestEncoder.encodeRttpData(l_pFields[l_nField]);}l_sEncodedFieldList+=this.const_FIELD_SEPARATOR+l_sField;}l_sEncodedFieldList=l_sEncodedFieldList.substring(1);}return l_sEncodedFieldList;
};
this.getFieldListRegExp = function(A){var l_oRegExp=this.m_pFieldSeparatorToRegExpMap[A];
if(!l_oRegExp){if(A==this.const_FIELD_SEPARATOR){l_oRegExp=new RegExp("[^"+this.const_NON_ENCODED_CHARACTERS+this.const_FIELD_SEPARATOR+"]");}else 
{l_oRegExp=new RegExp("[^"+this.const_NON_ENCODED_CHARACTERS+"]");}this.m_pFieldSeparatorToRegExpMap[A]=l_oRegExp;}return l_oRegExp;
};
};
GF_ResponseDecoder = new function(){this.m_fUnescape=decodeURIComponent;this.m_oDecodeRegExp=/\+/g;this.decodeRttpData = function(A){return this.m_fUnescape(A.replace(this.m_oDecodeRegExp," ").replace(/%(?![0-9a-fA-F][0-9a-fA-F])/g,"%25"));
};
};
var g_nConnectionManagerDebugId=0;
var SL4B_ConnectionManager=function(){};
if(false){function SL4B_ConnectionManager(){}
}SL4B_ConnectionManager = function(B,C,A){this.const_CAPABILITIES="HttpRequestLineLength=,HttpBodyLength=,MergedCommands=,FieldHash=";this.CLASS_NAME="ConnectionManager (instance: "+(g_nConnectionManagerDebugId++)+")";this.m_fConnectionCreator=B;this.m_oLiberatorConfiguration=C;this.m_oManagedConnection=A;this.m_oConnectionProxy=SL4B_ConnectionProxy.getInstance();this.m_sPreviousSessionId=SL4B_JavaScriptRttpProvider.const_NEW_SESSION_ID;this.m_bIsStopped=true;this.m_oCurrentConnectionData=null;this.m_pConnectionListeners=new Array();this.m_bRequestWindowReady=false;this.m_bLoggedIn=false;this.m_bLogInMessageProcessing=false;this.m_nConnectionCheckInterval=SL4B_Accessor.getConfiguration().getConnectionTimeout();this.m_nConnectionCheckTimeoutId=null;this.m_nLoginTimeout=SL4B_Accessor.getConfiguration().getLoginTimeout();this.m_nHeartbeatInterval=SL4B_Accessor.getConfiguration().getHeartbeatInterval();this.m_nLoginTimeoutId=null;this.m_bConnectionLost=false;this.m_oMessageReceiver=null;this.m_bFieldMapAvailable=false;this.m_pFieldCodeToFieldNameMap=new Object();var nTimeout=SL4B_Accessor.getConfiguration().getRequestChannelResponseTimeout();
this.m_oRequestChannelMessageTimer=new SL4B_Timer(nTimeout,this);this.m_sFieldHash="";var l_sAppendText="";
var l_sCommonDomain=GF_CommonDomainExtractor.getCommonDomain();
if(l_sCommonDomain){l_sAppendText="?domain="+l_sCommonDomain;}this.m_sEmptyPageUrl=SL4B_ScriptLoader.getRootUrl()+"sl4b/javascript-rttp-provider/empty.html"+l_sAppendText;SL4B_NoopScheduler.setConnectionManager(this);};
SL4B_ConnectionManager.prototype.getSessionId = function(){return this.m_oManagedConnection._getSessionId();
};
SL4B_ConnectionManager.prototype.setSessionIdFromMessage = function(A){this.m_oManagedConnection._setSessionId(this.parseSessionId(A));};
SL4B_ConnectionManager.prototype.parseSessionId = function(A){SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"SL4B_ConnectionManager: Parsing SessionId from message {0}.",A);return A.substr(0,A.indexOf(" "));
};
SL4B_ConnectionManager.prototype.getConnectionProxy = function(){return this.m_oConnectionProxy;
};
SL4B_ConnectionManager.prototype.connect = function(){this.m_bIsStopped=false;if(this.m_oManagedConnection._hasSessionId()==false&&this.m_oConnectionProxy.isDisconnected()){this.m_oConnectionProxy.setConnectionState(SL4B_ConnectionProxy.const_CONNECTION_STATE_CONNECTING);this.m_oConnectionProxy.connect();}};
SL4B_ConnectionManager.prototype.getCurrentConnectionData = function(){return this.m_oCurrentConnectionData;
};
SL4B_ConnectionManager.prototype.addConnectionListener = function(A){this.m_pConnectionListeners.push(A);};
SL4B_ConnectionManager.prototype.removeConnectionListener = function(A){var l_nMatchIndex=-1;
for(var l_nListener=0,l_nLength=this.m_pConnectionListeners.length;l_nListener<l_nLength;++l_nListener){if(this.m_pConnectionListeners[l_nListener]==A){l_nMatchIndex=l_nListener;break;
}}if(l_nMatchIndex!=-1){this.m_pConnectionListeners.splice(l_nMatchIndex,1);}return (l_nMatchIndex!=-1);
};
SL4B_ConnectionManager.prototype.notifyConnectionListeners = function(A){var l_pArguments=arguments;
var l_aCopy=this.m_pConnectionListeners.slice();
for(var l_nListener=0,l_nLength=l_aCopy.length;l_nListener<l_nLength;++l_nListener){
try {switch(A){
case SL4B_AbstractRttpProvider.prototype.const_ERROR_CONNECTION_EVENT:l_aCopy[l_nListener].connectionError(l_pArguments[1]);break;
case SL4B_AbstractRttpProvider.prototype.const_WARNING_CONNECTION_EVENT:l_aCopy[l_nListener].connectionWarning(l_pArguments[1],l_pArguments[2],l_pArguments[3],l_pArguments[4]);break;
case SL4B_AbstractRttpProvider.prototype.const_INFO_CONNECTION_EVENT:l_aCopy[l_nListener].connectionInfo(l_pArguments[1]);break;
case SL4B_AbstractRttpProvider.prototype.const_ATTEMPT_CONNECTION_EVENT:l_aCopy[l_nListener].connectionAttempt(l_pArguments[1],l_pArguments[2]);break;
case SL4B_AbstractRttpProvider.prototype.const_OK_CONNECTION_EVENT:l_aCopy[l_nListener].connectionOk(l_pArguments[1],l_pArguments[2],l_pArguments[3]);break;
case SL4B_AbstractRttpProvider.prototype.const_RECONNECTION_OK_CONNECTION_EVENT:l_aCopy[l_nListener].reconnectionOk();break;
case SL4B_AbstractRttpProvider.prototype.const_FILE_DOWNLOAD_ERROR_CONNECTION_EVENT:l_aCopy[l_nListener].fileDownloadError(l_pArguments[1],l_pArguments[2]);break;
case SL4B_AbstractRttpProvider.prototype.const_LOGIN_ERROR_CONNECTION_EVENT:l_aCopy[l_nListener].loginError(l_pArguments[1]);break;
case SL4B_AbstractRttpProvider.prototype.const_LOGIN_OK_CONNECTION_EVENT:l_aCopy[l_nListener].loginOk();break;
case SL4B_AbstractRttpProvider.prototype.const_CREDENTIALS_RETRIEVED_CONNECTION_EVENT:l_aCopy[l_nListener].credentialsRetrieved(l_pArguments[1]);break;
case SL4B_AbstractRttpProvider.prototype.const_MESSAGE_CONNECTION_EVENT:l_aCopy[l_nListener].message(l_pArguments[1],l_pArguments[2]);break;
case SL4B_AbstractRttpProvider.prototype.const_SERVICE_MESSAGE_CONNECTION_EVENT:l_aCopy[l_nListener].serviceMessage(l_pArguments[1],l_pArguments[2],l_pArguments[3],l_pArguments[4]);break;
case SL4B_AbstractRttpProvider.prototype.const_SESSION_EJECTED_CONNECTION_EVENT:l_aCopy[l_nListener].sessionEjected(l_pArguments[1],l_pArguments[2]);break;
case SL4B_AbstractRttpProvider.prototype.const_SOURCE_MESSAGE_CONNECTION_EVENT:l_aCopy[l_nListener].sourceMessage(l_pArguments[1],l_pArguments[2],l_pArguments[3],l_pArguments[4]);break;
case SL4B_AbstractRttpProvider.prototype.const_STATISTICS_CONNECTION_EVENT:l_aCopy[l_nListener].statistics(l_pArguments[1],l_pArguments[2],l_pArguments[3],l_pArguments[4],l_pArguments[5]);break;
case SL4B_AbstractRttpProvider.prototype.const_CREDENTIALS_PROVIDER_SESSION_ERROR_CONNECTION_EVENT:l_aCopy[l_nListener].credentialsProviderSessionError(l_pArguments[1],l_pArguments[2],l_pArguments[3],l_pArguments[4]);break;
default :SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ConnectionManager.notifyConnectionListeners: "+"Received an unknown connection event '"+A+"'. Ignoring event.");}}catch(e){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ConnectionManager.notifyConnectionListeners: Exception thrown by a listener whilst processing a \"{0}\" event; exception was {1}",A,SL4B_Accessor.getBrowserAdapter().convertExceptionToString(e));}
}if(A==SL4B_AbstractRttpProvider.prototype.const_RECONNECTION_OK_CONNECTION_EVENT||A==SL4B_AbstractRttpProvider.prototype.const_LOGIN_OK_CONNECTION_EVENT){this.m_oManagedConnection._sendNextRttpMessage();}};
SL4B_ConnectionManager.prototype.createConnection = function(A){SL4B_Logger.logConnectionMessage(false,"ConnectionManager.createConnection({0})",A);this.m_oConnectionProxy.setConnectionState(SL4B_ConnectionProxy.const_CONNECTION_STATE_DISCONNECTED);if(!A){var l_oConnectionData=this.m_oLiberatorConfiguration.getNextConnection();
this.m_oCurrentConnectionData=l_oConnectionData;SL4B_Logger.logConnectionMessage(false,"Next connection information: {0}",l_oConnectionData);if(l_oConnectionData==null){this.connectionLost();return false;
}}var l_oConnectionData=this.m_oCurrentConnectionData;
var l_oConnectionMethod=l_oConnectionData.getMethod();
var l_oConnection=this.m_fConnectionCreator(l_oConnectionData);
this.m_oConnectionProxy.setConnection(l_oConnection);l_oConnection._$setConnectionManager(this);this.startRttpMessageListening();if(l_oConnection.constructor==SL4B_Type3Connection){l_oConnectionMethod=SL4B_ConnectionMethod.POLLING;}C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",SL4B_AbstractRttpProvider.prototype.const_ATTEMPT_CONNECTION_EVENT,l_oConnectionData.getServerUrl(),l_oConnectionMethod));this.m_oConnectionProxy.start();return true;
};
SL4B_ConnectionManager.prototype.isLoggedIn = function(){return this.m_bLoggedIn;
};
SL4B_ConnectionManager.prototype.setLoggedIn = function(A){if(A==false){SL4B_NoopScheduler.reset();}else 
{this.m_bLogInMessageProcessing=false;}this.m_bLoggedIn=A;};
SL4B_ConnectionManager.prototype.startConnectionChecking = function(){if(this.m_nConnectionCheckTimeoutId==null){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"SL4B_ConnectionManager: Starting connection checking.");this.m_nConnectionCheckTimeoutId=setTimeout("SL4B_Accessor.getUnderlyingRttpProvider().checkConnected()",this.m_nConnectionCheckInterval);}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"SL4B_ConnectionManager: Connection checking already started.");}};
SL4B_ConnectionManager.prototype.clearConnectionCheckTimeout = function(){if(this.m_nConnectionCheckTimeoutId!=null){clearTimeout(this.m_nConnectionCheckTimeoutId);this.m_nConnectionCheckTimeoutId=null;}};
SL4B_ConnectionManager.prototype.clearLoginTimeout = function(){if(this.m_nLoginTimeoutId!=null){clearTimeout(this.m_nLoginTimeoutId);this.m_nLoginTimeoutId=null;}};
SL4B_ConnectionManager.prototype.reconnectInternal = function(A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ConnectionManager.reconnectInternal({0})",A);this.stopConnection();this.m_oConnectionProxy.setConnectionState(SL4B_ConnectionProxy.const_CONNECTION_STATE_RECONNECTING);this.m_sPreviousSessionId=A;this.m_bRequestWindowReady=false;this.m_oManagedConnection._setSessionId(null);this.setLoggedIn(false);this.m_oRequestChannelMessageTimer.reset();if(this.createConnection(A!=SL4B_JavaScriptRttpProvider.const_NEW_SESSION_ID)){this.clearConnectionCheckTimeout();}};
SL4B_ConnectionManager.prototype.liberatorAvailable = function(){this.startConnectionChecking();};
SL4B_ConnectionManager.prototype.liberatorUnavailable = function(){C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",SL4B_JavaScriptRttpProvider.prototype.const_WARNING_CONNECTION_EVENT,"Liberator unavailable",SL4B_ConnectionWarningReason.LIBERATOR_UNAVAILABLE,this.m_oCurrentConnectionData.getServerUrl(),this.m_oCurrentConnectionData.getMethod()));this.connectionLost();};
SL4B_ConnectionManager.prototype.connectionLost = function(){this.m_oLiberatorConfiguration.resetConnections();this.m_bConnectionLost=true;this.clearConnectionCheckTimeout();this.stopConnection();SL4B_Logger.log(SL4B_DebugLevel.const_DEBUG_INT,"JavaScriptRttpProvider.createConnection: failed to reconnect - no more connection types to try");C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",SL4B_AbstractRttpProvider.prototype.const_ERROR_CONNECTION_EVENT,"Failed to connect"));};
SL4B_ConnectionManager.prototype.setMessageReceiver = function(A){if(A!==null&&typeof A.receiveMessage!="function"){throw new SL4B_Exception("Specified listener does not define the receiveMessage method");
}this.m_oMessageReceiver=A;};
SL4B_ConnectionManager.prototype.receiveMessage = function(A){if(this.m_oMessageReceiver!==null){SL4B_MethodInvocationProxy.invoke(this.m_oMessageReceiver,"receiveMessage",[A]);}};
SL4B_ConnectionManager.prototype.stopRttpMessageListening = function(){this.m_oConnectionProxy.setMessageReceiver(null);};
SL4B_ConnectionManager.prototype.startRttpMessageListening = function(){this.m_oConnectionProxy.setMessageReceiver(this);};
SL4B_ConnectionManager.prototype.reconnect = function(){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ConnectionManager.reconnect()");if(this.m_bConnectionLost){this.m_bConnectionLost=false;}this.m_bFieldMapAvailable=false;this.reconnectInternal(SL4B_JavaScriptRttpProvider.const_NEW_SESSION_ID);};
SL4B_ConnectionManager.prototype.createConnectionLost = function(B,A){SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"ConnectionManager.createConnectionLost: Connection lost: "+B);this.clearLoginTimeout();if(this.m_oCurrentConnectionData==null){SL4B_Logger.log(SL4B_DebugLevel.const_WARN,"ConnectionManager.createConnectionLost: The current connection data is not set.");return;
}C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",SL4B_JavaScriptRttpProvider.prototype.const_WARNING_CONNECTION_EVENT,B,SL4B_ConnectionWarningReason.CONNECTION_LOST,this.m_oCurrentConnectionData.getServerUrl(),this.m_oCurrentConnectionData.getMethod()));var sSessionId=SL4B_JavaScriptRttpProvider.const_NEW_SESSION_ID;
if(!A&&!this.m_oManagedConnection._$isFullReconnectRequired()){sSessionId=this.m_oManagedConnection._getSessionId()+"/"+this.m_oConnectionProxy.getConnection().getLastSequenceNumber();SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"ConnectionManager.createConnectionLost: attempting session reconnect");}else 
{var sMessage="attempting full reconnect"+(A ? "" : " due to indeterminate client/server state");
SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"ConnectionManager.createConnectionLost: {0}",sMessage);}this.reconnectInternal(sSessionId);};
SL4B_ConnectionManager.prototype.checkConnected = function(){var l_sConnectionState=this.m_oConnectionProxy.getConnectionState();
SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ConnectionManager.checkConnected: {0}",l_sConnectionState);this.m_nConnectionCheckTimeoutId=null;if(l_sConnectionState!=SL4B_ConnectionProxy.const_CONNECTION_STATE_CONNECTED){var l_currentConnectionData=this.m_oCurrentConnectionData;
C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",SL4B_JavaScriptRttpProvider.prototype.const_WARNING_CONNECTION_EVENT,"Connection timed out - "+l_currentConnectionData,SL4B_ConnectionWarningReason.CONNECTION_FAILED,l_currentConnectionData.getServerUrl(),l_currentConnectionData.getMethod()));this.reconnect();}};
SL4B_ConnectionManager.prototype.setFieldCodeMapping = function(A){this.m_sFieldHash=SL4B_MD5(A);this.m_bFieldMapAvailable=true;var l_pFieldNameValuePairs=A.split(" ");
for(var l_nFieldPair=0,l_nLength=l_pFieldNameValuePairs.length;l_nFieldPair<l_nLength;++l_nFieldPair){var l_nIndex=l_pFieldNameValuePairs[l_nFieldPair].indexOf("=");
this.m_pFieldCodeToFieldNameMap[l_pFieldNameValuePairs[l_nFieldPair].substr(0,l_nIndex)]=GF_ResponseDecoder.decodeRttpData(l_pFieldNameValuePairs[l_nFieldPair].substr(l_nIndex+1));}};
SL4B_ConnectionManager.prototype.getFieldName = function(A){var l_sFieldName=this.m_pFieldCodeToFieldNameMap[A];
return ((l_sFieldName===undefined) ? GF_ResponseDecoder.decodeRttpData(A) : l_sFieldName);
};
SL4B_ConnectionManager.prototype.responseHttpRequestReady = function(){this.connect();};
SL4B_ConnectionManager.prototype.requestHttpRequestReady = function(){this.m_oRequestChannelMessageTimer.reset();this.m_bRequestWindowReady=true;this.connect();this.m_oManagedConnection._sendNextRttpMessage();};
SL4B_ConnectionManager.prototype.startConnection = function(){setTimeout("SL4B_Accessor.getUnderlyingRttpProvider().m_oConnectionManager.startConnectionInternal();",0);};
SL4B_ConnectionManager.prototype.startConnectionInternal = function(){this.createConnection(false);};
SL4B_ConnectionManager.prototype.login = function(A,B){this.clearLoginTimeout();if(this.m_bLogInMessageProcessing===false){SL4B_Logger.logConnectionMessage(true,"ConnectionManager.login: username = {0}; password = {1}",A,B);var l_sLoginSession=this.m_sPreviousSessionId;
if(this.m_bFieldMapAvailable==false){l_sLoginSession="0";}var l_nVersion=SL4B_Accessor.getCapabilities().getRttpVersion();
var l_sLoginMessage="LOGIN "+l_sLoginSession+" "+GF_RequestEncoder.encodeRttpData(SL4B_Accessor.getConfiguration().getApplicationId())+"/ RTTP/"+l_nVersion.toFixed(1)+" "+GF_RequestEncoder.encodeRttpData(A)+" "+GF_RequestEncoder.encodeRttpData(B);
if(l_nVersion>=2.1){l_sLoginMessage+=" "+this.const_CAPABILITIES+this.m_sFieldHash;if(SL4B_Accessor.getConfiguration().isEnableLatency()){if(this.m_nHeartbeatInterval>0){l_sLoginMessage+=",HeartbeatInterval="+this.m_nHeartbeatInterval;}}}C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",SL4B_AbstractRttpProvider.prototype.const_CREDENTIALS_RETRIEVED_CONNECTION_EVENT,SL4B_Accessor.getCredentialsProvider().getUsername()));this.m_oManagedConnection.clearResponseQueue();this.m_bLogInMessageProcessing=true;this.m_oManagedConnection._sendPriorityMessage(l_sLoginMessage,this.m_oManagedConnection.m_oSystemEventReceiverRecord.getListener(),true);}else 
{SL4B_Logger.logConnectionMessage(true,"ConnectionManager.login: Attempt to login again, even though a login is currently in progress.");}SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"Login message sent. Waiting {0}ms for login OK response.",this.m_nLoginTimeout);this.m_nLoginTimeoutId=setTimeout("SL4B_Accessor.getUnderlyingRttpProvider().createConnectionLost(\"Login Timeout\", true)",this.m_nLoginTimeout);};
SL4B_ConnectionManager.prototype.createStreamingFrame = function(A){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINE_INT,"ConnectionManager.createStreamingFrame: frameid = {0}; url = {1}",A,this.m_sEmptyPageUrl);var l_sHtml="<iframe id=\""+A+"\" style=\"display:none;\" src=\""+this.m_sEmptyPageUrl+"\"></iframe>";
var tag=document.createElement('iframe');
tag.setAttribute("id",A);tag.setAttribute("style",'display:none;');tag.setAttribute("src",this.m_sEmptyPageUrl);var head=document.getElementsByTagName('head')[0];
head.appendChild(tag);var self=this;
setTimeout(function(){if(SL4B_Accessor.getUnderlyingRttpProvider().m_oConnectionManager){SL4B_Accessor.getUnderlyingRttpProvider().m_oConnectionManager.replaceStreamingFrame(A,self.m_sEmptyPageUrl);}},0);SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ConnectionManager.createStreamingFrame: html = {0}",l_sHtml);C_LiberatorUrlCheck.createLiberatorUrlCheck(A);};
SL4B_ConnectionManager.prototype.replaceStreamingFrame = function(B,A){var l_oFrameWindow=SL4B_Accessor.getBrowserAdapter().getFrameWindow(B);
if(l_oFrameWindow&&l_oFrameWindow.location){l_oFrameWindow.location.replace(A);}};
SL4B_ConnectionManager.prototype.cleanUpStreamingFrames = function(){this.replaceStreamingFrame(SL4B_JavaScriptRttpProvider.const_REQUEST_FRAME_ID,this.m_sEmptyPageUrl);this.replaceStreamingFrame(SL4B_JavaScriptRttpProvider.const_RESPONSE_FRAME_ID,this.m_sEmptyPageUrl);SL4B_Logger.log(SL4B_DebugLevel.const_DEBUG_INT,"ConnectionManager.cleanUpStreamingFrames()");};
SL4B_ConnectionManager.prototype.logout = function(){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINE_INT,"JavaScriptRttpProvider.logout()");if(this.m_bLoggedIn){this.m_oManagedConnection._sendPriorityMessage("LOGOUT",this.m_oManagedConnection.m_oSystemEventReceiverRecord.getListener(),true);}this.m_sPreviousSessionId=SL4B_JavaScriptRttpProvider.const_NEW_SESSION_ID;this.m_bFieldMapAvailable=false;this.clearConnectionCheckTimeout();this.getConnectionProxy().setConnectionState(SL4B_ConnectionProxy.const_CONNECTION_STATE_DISCONNECTED);this.cleanUpStreamingFrames();};
SL4B_ConnectionManager.prototype.initialise = function(){
try {if(SL4B_JavaScriptRttpProvider.bFramesCreatedFlag==false){SL4B_JavaScriptRttpProvider.bFramesCreatedFlag=true;this.createStreamingFrame(SL4B_JavaScriptRttpProvider.const_RESPONSE_FRAME_ID);this.createStreamingFrame(SL4B_JavaScriptRttpProvider.const_REQUEST_FRAME_ID);}}catch(e){return false;
}
return true;
};
SL4B_ConnectionManager.prototype.noop = function(){if(this.m_bLoggedIn){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"ConnectionManager.noop: {0}",this.getConnectionProxy().getConnectionState());switch(this.getConnectionProxy().getConnectionState()){
case SL4B_ConnectionProxy.const_CONNECTION_STATE_INITIALISING:break;
case SL4B_ConnectionProxy.const_CONNECTION_STATE_CONNECTED:this.m_oManagedConnection.sendMessage("NOOP",this.m_oManagedConnection.m_oSystemEventReceiverRecord.getListener());SL4B_NoopScheduler.startNoopTimeout();break;
case SL4B_ConnectionProxy.const_CONNECTION_STATE_RECONNECTING:break;
case SL4B_ConnectionProxy.const_CONNECTION_STATE_CONNECTING:break;
}}};
SL4B_ConnectionManager.prototype.stop = function(){if(this.m_bIsStopped==false){this.m_bIsStopped=true;SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"AbstractRttpProvider.stop");this.logout();
try {SL4B_FrameRegistrarAccessor.removeMasterFrame();}catch(e){}
this.m_pRequestQueue=new Array();this.m_pPriorityRequestQueue=new Array();this.m_bRequestWindowReady=true;this.clearConnectionCheckTimeout();this.stopConnection();if(!this.m_bConnectionLost){C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",SL4B_AbstractRttpProvider.prototype.const_ERROR_CONNECTION_EVENT,"Connection stopped"));}}};
SL4B_ConnectionManager.prototype.stopConnection = function(){this.stopRttpMessageListening();this.m_oConnectionProxy.stop();this.m_bLogInMessageProcessing=false;};
SL4B_ConnectionManager.prototype.sendSync = function(A){this.m_oConnectionProxy.sendSync(A);};
SL4B_ConnectionManager.prototype.getJsContainerUrl = function(){var l_sContainerUrl=SL4B_Accessor.getConfiguration().getJsContainerUrl();
if(l_sContainerUrl==null){var l_oConnectionData=this.getCurrentConnectionData();
if(l_oConnectionData==null){l_oConnectionData=this.m_oLiberatorConfiguration.peekAtNextConnection();}l_sContainerUrl=l_oConnectionData.getServerUrl()+"/"+SL4B_Accessor.getConfiguration().getJsContainerPath();}return l_sContainerUrl.replace(/\/$/,"");
};
SL4B_ConnectionManager.prototype.readyToRequest = function(){return this.m_bRequestWindowReady;
};
SL4B_ConnectionManager.prototype.makingRequest = function(){this.m_bRequestWindowReady=false;this.m_oRequestChannelMessageTimer.start();};
SL4B_ConnectionManager.prototype.onExpired = function(){this.createConnectionLost("A Request Channel response to a message sent on the Request Channel has not been received.",false);};
function SL4B_Timer(B,A){this.m_nCountDown=B;this.m_oListener=A;this.m_nTimeoutId=null;}
SL4B_Timer.prototype.start = function(){if(this.m_nTimeoutId!==null){throw new SL4B_Exception("The Timer is already running: start() was called more than once.");
}this.m_nTimeoutId=window.setTimeout(this._notifyObservers(),this.m_nCountDown);};
SL4B_Timer.prototype.reset = function(){window.clearTimeout(this.m_nTimeoutId);this.m_nTimeoutId=null;};
SL4B_Timer.prototype._notifyObservers = function(){oSelf=this;return function(){oSelf.m_oListener.onExpired();};
};
SL4B_Timer.prototype._stop = function(){};
SL4B_Timer.prototype._getElapsedTime = function(){};
SL4B_Timer.prototype._getRemainingTime = function(){};
function SL4B_NoopScheduler(){SL4B_Accessor.getStatistics().addResponseQueueStatisticsListener(this);this.m_oConnectionManager=null;this.m_nNoopSendTimeoutId=null;this.m_nNoopReceivedTimeoutId=null;this.m_nNOOPInterval=10000;this.m_nNOOPTimeout=1000;}
SL4B_NoopScheduler.prototype = new SL4B_ResponseQueueStatisticsListener();SL4B_NoopScheduler.prototype.m_fConstructor = SL4B_NoopScheduler;SL4B_NoopScheduler.prototype.setConnectionManager = function(A){if(this.m_oConnectionManager!=null){this.reset();}this.m_oConnectionManager=A;this.m_nNOOPInterval=SL4B_Accessor.getConfiguration().getNOOPInterval();this.m_nNOOPTimeout=SL4B_Accessor.getConfiguration().getNOOPTimeout();};
SL4B_NoopScheduler.prototype.onBatchQueued = function(A){this.reset();};
SL4B_NoopScheduler.prototype.onAfterBatchProcessed = function(A){if((this.m_oConnectionManager.getConnectionProxy().getConnectionState()==SL4B_ConnectionProxy.const_CONNECTION_STATE_CONNECTED)&&this.m_oConnectionManager.isLoggedIn()){this.startNoopSending();}};
SL4B_NoopScheduler.prototype.reset = function(){this._clearNoopSending();this._clearNoopTimeout();};
SL4B_NoopScheduler.prototype._clearNoopSending = function(){if(this.m_nNoopSendTimeoutId!=null){clearTimeout(this.m_nNoopSendTimeoutId);this.m_nNoopSendTimeoutId=null;}};
SL4B_NoopScheduler.prototype._clearNoopTimeout = function(){if(this.m_nNoopReceivedTimeoutId!=null){clearTimeout(this.m_nNoopReceivedTimeoutId);this.m_nNoopReceivedTimeoutId=null;}};
SL4B_NoopScheduler.prototype.startNoopSending = function(){if(this.m_nNoopSendTimeoutId==null){this.m_nNoopSendTimeoutId=setTimeout("SL4B_Accessor.getUnderlyingRttpProvider().m_oConnectionManager.noop()",this.m_nNOOPInterval);}};
SL4B_NoopScheduler.prototype.startNoopTimeout = function(){if(this.m_nNoopReceivedTimeoutId!=null){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"SL4B_NoopScheduler.startNoopTimeout while a noop timeout is already set.)");}this.m_nNoopReceivedTimeoutId=setTimeout("SL4B_Accessor.getUnderlyingRttpProvider().createConnectionLost(\"Request Timeout\", false)",this.m_nNOOPTimeout);};
SL4B_NoopScheduler=new SL4B_NoopScheduler();var SL4B_JavaScriptRttpProvider=function(){};
if(false){function SL4B_JavaScriptRttpProvider(){}
}SL4B_JavaScriptRttpProvider = function(B,C){this.CLASS_NAME="SL4B_JavaScriptRttpProvider";SL4B_AbstractRttpProvider.apply(this);this.m_oLiberatorConfiguration=B;var thisProvider=this;
var l_fConnectionCreator=function(A){var l_oConnectionMethod=A.getMethod();
if(l_oConnectionMethod!=null){return l_oConnectionMethod.createConnection(thisProvider,A);
}SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"JavaScriptRttpProvider.createConnection: invalid connection type ({0}) using type 3",l_oConnectionMethod);return new SL4B_Type3Connection(this);
};
this.m_oManagedConnection=new SL4B_ManagedConnection();this.m_oManagedConnection.setSystemEventReceiver(this);this.m_oConnectionManager=new SL4B_ConnectionManager(l_fConnectionCreator,B,this.m_oManagedConnection);this.m_oManagedConnection._$setConnectionManager(this.m_oConnectionManager);this.m_bFatalError=false;if(SL4B_JavaScriptRttpProvider.oObjectSubscriptionManager==null){SL4B_JavaScriptRttpProvider.oObjectSubscriptionManager=new SL4B_ObjectSubscriptionManager(this);}else 
{SL4B_JavaScriptRttpProvider.oObjectSubscriptionManager.setRttpProvider(this);}this.m_oObjectSubscriptionManager=SL4B_JavaScriptRttpProvider.oObjectSubscriptionManager;this.m_oActionSubscriptionManager=C;this.m_oObjectCache=new SL4B_ObjectCache(this,this.m_oObjectSubscriptionManager);};
SL4B_JavaScriptRttpProvider.prototype = new SL4B_AbstractRttpProvider;SL4B_JavaScriptRttpProvider.createProvider = function(B,A){var oProvider=null;
if(A==null){oProvider=new SL4B_JavaScriptRttpProvider(B,new GF_ActionSubscriptionManager());}else 
{oProvider=new SL4B_JavaScriptRttpProvider(B,A.m_oActionSubscriptionManager);}return oProvider;
};
SL4B_JavaScriptRttpProvider.prototype.getManagedConnection = function(){return this.m_oManagedConnection;
};
SL4B_JavaScriptRttpProvider.const_NEW_SESSION_ID="0";SL4B_JavaScriptRttpProvider.const_REQUEST_FRAME_ID="frmRequest";SL4B_JavaScriptRttpProvider.const_RESPONSE_FRAME_ID="frmResponse";SL4B_JavaScriptRttpProvider.const_REQUEST_FRAME_URL="container.html?"+SL4B_JavaScriptRttpProviderConstants.const_TYPE_PARAMETER+"="+SL4B_JavaScriptRttpProviderConstants.const_REQUEST_TYPE;SL4B_JavaScriptRttpProvider.const_RESPONSE_FRAME_URL="container.html?"+SL4B_JavaScriptRttpProviderConstants.const_TYPE_PARAMETER+"="+SL4B_JavaScriptRttpProviderConstants.const_RESPONSE_TYPE;SL4B_JavaScriptRttpProvider.bFramesCreatedFlag=false;SL4B_JavaScriptRttpProvider.oObjectSubscriptionManager=null;SL4B_JavaScriptRttpProvider.prototype.getSessionId = function(){return this.m_oConnectionManager.getSessionId();
};
SL4B_JavaScriptRttpProvider.prototype.receiveMessage = SL_PB;SL4B_JavaScriptRttpProvider.prototype.initialise = SL_HW;SL4B_JavaScriptRttpProvider.prototype.onLoad = SL_CU;SL4B_JavaScriptRttpProvider.prototype.responseHttpRequestReady = SL_CE;SL4B_JavaScriptRttpProvider.prototype.requestHttpRequestReady = SL_BB;SL4B_JavaScriptRttpProvider.prototype.createConnection = SL_GI;SL4B_JavaScriptRttpProvider.prototype.createConnectionLost = SL_FF;SL4B_JavaScriptRttpProvider.prototype.connect = SL_OY;SL4B_JavaScriptRttpProvider.prototype.login = SL_OD;SL4B_JavaScriptRttpProvider.prototype.logout = SL_RD;SL4B_JavaScriptRttpProvider.prototype.super_stop = SL4B_JavaScriptRttpProvider.prototype.stop;SL4B_JavaScriptRttpProvider.prototype.stop = SL_BC;SL4B_JavaScriptRttpProvider.prototype.getVersion = SL_ES;SL4B_JavaScriptRttpProvider.prototype.getVersionInfo = SL_QU;SL4B_JavaScriptRttpProvider.prototype.getObject = SL_LT;SL4B_JavaScriptRttpProvider.prototype.getObjects = SL_LM;SL4B_JavaScriptRttpProvider.prototype.removeObject = SL_LR;SL4B_JavaScriptRttpProvider.prototype.removeObjects = SL_CR;SL4B_JavaScriptRttpProvider.prototype.removeSubscriber = SL_JJ;SL4B_JavaScriptRttpProvider.prototype.contribObject = SL_RI;SL4B_JavaScriptRttpProvider.prototype.createObject = SL_HB;SL4B_JavaScriptRttpProvider.prototype.deleteObject = SL_KI;SL4B_JavaScriptRttpProvider.prototype.reconnect = SL_NA;SL4B_JavaScriptRttpProvider.prototype.checkConnected = SL_FU;SL4B_JavaScriptRttpProvider.prototype.getJsContainerUrl = SL_JT;SL4B_JavaScriptRttpProvider.prototype.getObjectCache = SL_SA;SL4B_JavaScriptRttpProvider.prototype.getSubscriptionManager = SL_BU;SL4B_JavaScriptRttpProvider.prototype.getAutoDirectory = SL_NY;SL4B_JavaScriptRttpProvider.prototype.removeAutoDirectory = SL_FW;SL4B_JavaScriptRttpProvider.prototype.getContainer = SL_EK;SL4B_JavaScriptRttpProvider.prototype._$getContainerSnapshot = SL_GY;SL4B_JavaScriptRttpProvider.prototype.setContainerWindow = SL_ED;SL4B_JavaScriptRttpProvider.prototype.clearContainerWindow = SL_NL;SL4B_JavaScriptRttpProvider.prototype.removeContainer = SL_OL;SL4B_JavaScriptRttpProvider.prototype.getFieldName = SL_CB;SL4B_JavaScriptRttpProvider.prototype.getFieldNames = function(){return "";
};
SL4B_JavaScriptRttpProvider.prototype.setGlobalThrottle = SL_IU;SL4B_JavaScriptRttpProvider.prototype.setThrottleObject = SL_AZ;SL4B_JavaScriptRttpProvider.prototype.setThrottleObjects = SL_AG;SL4B_JavaScriptRttpProvider.prototype.addConnectionListener = function(A){this.m_oConnectionManager.addConnectionListener(A);};
SL4B_JavaScriptRttpProvider.prototype.removeConnectionListener = function(A){this.m_oConnectionManager.removeConnectionListener(A);};
SL4B_JavaScriptRttpProvider.prototype.notifyConnectionListeners = function(){SL4B_MethodInvocationProxy.invoke(this.m_oConnectionManager,"notifyConnectionListeners",arguments);};
SL4B_JavaScriptRttpProvider.prototype.getObjectCache = function(){return this.m_oObjectCache;
};
function SL_GI(A){this.m_oConnectionManager.createConnection(A);}
function SL_FF(B,A){this.m_oConnectionManager.createConnectionLost(B,A);}
function SL_FU(){this.m_oConnectionManager.checkConnected();}
function SL_PB(A,C,B){if(SL4B_Accessor.getRttpProvider()!=this&&SL4B_Accessor.getUnderlyingRttpProvider()!=this){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"JavaScriptRttpProvider: Received a message when not the current RttpProvider. {0}",A);return;
}var l_nRttpCode=A.getRttpCode();
if(l_nRttpCode==SL4B_RttpCodes.const_NOOP_OK||l_nRttpCode==SL4B_RttpCodes.const_REQUEST_ACK||l_nRttpCode==SL4B_RttpCodes.const_MISC_HEARTBEAT){}else 
if(l_nRttpCode==SL4B_RttpCodes.const_CONNECT_OK){SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"JavaScriptRttpProvider: Received initial connection message.");this.m_oLiberatorConfiguration.connectionOk();this.m_oConnectionManager.setSessionIdFromMessage(A.getMessageContent());setTimeout("SL4B_Accessor.getRttpProvider().connected()",0);var l_sHostName=A.getMessageContent().match(/host=(.*)\sversion=/)[1];
var l_sTime=A.getMessageContent().match(/time=(\d*)/)[1];
var l_sVersion=A.getMessageContent().match(/version=(.*)\s/)[1];
var l_nVersion=parseFloat(l_sVersion);
SL4B_Accessor.getCapabilities().setRttpVersion(l_nVersion);this.m_oConnectionManager.getConnectionProxy().setConnectionState(SL4B_ConnectionProxy.const_CONNECTION_STATE_CONNECTED);C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",this.const_OK_CONNECTION_EVENT,l_sHostName,this.m_oConnectionManager.getCurrentConnectionData().getMethod(),l_sTime));}else 
if(l_nRttpCode==SL4B_RttpCodes.const_LOGIN_OK){this.m_oConnectionManager.setLoggedIn(true);this.m_oManagedConnection.clear();this.m_oActionSubscriptionManager.resendPersistedActionsBeforeResubscription();this.m_oObjectCache.clear();this.m_oObjectSubscriptionManager.reRequestObjects();this.m_oActionSubscriptionManager.resendPersistedActionsAfterResubscription();var pWords=A.getMessageContent().split(" ");
if(pWords.length==2){this.storeCapabilities(pWords[1]);}this.loggedIn();}else 
if(l_nRttpCode==SL4B_RttpCodes.const_RECON_OK){this.m_oConnectionManager.setLoggedIn(true);C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",this.const_RECONNECTION_OK_CONNECTION_EVENT));this.loggedIn();this.m_oObjectSubscriptionManager.sendCurrentObjectStatusToAll();}else 
if(l_nRttpCode>=SL4B_RttpCodes.const_INVALID_USER&&l_nRttpCode<SL4B_RttpCodes.const_NOT_LOGGED_IN){C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",this.const_LOGIN_ERROR_CONNECTION_EVENT,A.getMessageContent()));this.m_oConnectionManager.stopConnection();this.m_oConnectionManager.clearConnectionCheckTimeout();}else 
if(l_nRttpCode==SL4B_RttpCodes.const_FIELD_MAP){this.m_oConnectionManager.setFieldCodeMapping(A.getMessageContent());}else 
if(l_nRttpCode>=SL4B_RttpCodes.const_SOURCE_UP&&l_nRttpCode<=SL4B_RttpCodes.const_SOURCE_WARN){var l_sWords=A.getMessageContent().split(" ");
C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",this.const_SOURCE_MESSAGE_CONNECTION_EVENT,l_nRttpCode,l_sWords[0],l_sWords[1],l_sWords[2]));}else 
if(l_nRttpCode==SL4B_RttpCodes.const_SESSION_EJECTED){C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",this.const_SESSION_EJECTED_CONNECTION_EVENT,A.getMessageContent()));this.m_oConnectionManager.stopConnection();this.m_oConnectionManager.clearConnectionCheckTimeout();}else 
if(l_nRttpCode>=SL4B_RttpCodes.const_SERVICE_OK&&l_nRttpCode<=SL4B_RttpCodes.const_SERVICE_LIMITED){var l_sWords=A.getMessageContent().split(" ");
C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",this.const_SERVICE_MESSAGE_CONNECTION_EVENT,l_nRttpCode,l_sWords[0],l_sWords[1],""));}else 
if(l_nRttpCode>=SL4B_RttpCodes.const_STATUS_MSG&&l_nRttpCode<=SL4B_RttpCodes.const_ERROR_MSG){var l_sWords=A.getMessageContent().split(" ");
C_CallbackQueue.addCallback(new Array(this,"notifyConnectionListeners",this.const_MESSAGE_CONNECTION_EVENT,l_nRttpCode,l_sWords[0],l_sWords[1]));}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"JavaScriptRttpProvider: Received a message with an unknown rttp code, {0}: {1}",l_nRttpCode,A);}}
SL4B_JavaScriptRttpProvider.prototype.storeCapabilities = function(A){var pCapabilityKVPairs=A.split(",");
for(var i=0,nLength=pCapabilityKVPairs.length;i<nLength;++i){var sCapabilityKVPair=pCapabilityKVPairs[i];
var pWords=sCapabilityKVPair.split("=");
if(pWords.length==2){SL4B_Accessor.getCapabilities().add(pWords[0],pWords[1]);}}};
SL4B_JavaScriptRttpProvider.prototype.loggedIn = function(){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINEST_INT,"Login successful, starting NOOP messages");this.m_oConnectionManager.clearLoginTimeout();SL4B_NoopScheduler.startNoopSending();if(SL4B_Accessor.getConfiguration().isEnableLatency()){this.m_oConnectionManager.sendSync(true);}SL4B_AbstractRttpProvider.prototype.loggedIn.apply(this,[]);};
function SL_HW(){SL4B_Logger.logConnectionMessage(false,"JavaScriptRttpProvider.initialise");this.m_oConnectionManager.setMessageReceiver(this.m_oManagedConnection);this.m_bFatalError=(this.m_oConnectionManager.initialise()==false);}
function SL_JT(){return this.m_oConnectionManager.getJsContainerUrl();
}
function SL_SA(){return this.m_oObjectCache;
}
function SL_BU(){return this.m_oObjectSubscriptionManager;
}
function SL_CE(){this.m_oConnectionManager.responseHttpRequestReady();}
function SL_BB(){this.m_oConnectionManager.requestHttpRequestReady();}
function SL_CU(A){SL4B_Logger.logConnectionMessage(false,"JavaScriptRttpProvider.onLoad()");if(!this.m_bFatalError){this.m_oConnectionManager.startConnection();}}
SL4B_JavaScriptRttpProvider.prototype.onUnload = function(){this._removeStreamingFrame(SL4B_JavaScriptRttpProvider.const_REQUEST_FRAME_ID);this._removeStreamingFrame(SL4B_JavaScriptRttpProvider.const_RESPONSE_FRAME_ID);};
SL4B_JavaScriptRttpProvider.prototype._removeStreamingFrame = function(A){
try {var eFrameElement=SL4B_Accessor.getBrowserAdapter().getElementById(A);
eFrameElement.parentNode.removeChild(eFrameElement);}catch(e){}
};
function SL_OY(){this.m_oConnectionManager.connect();}
function SL_NA(){this.m_oConnectionManager.reconnect();}
function SL_OD(A,B){this.m_oConnectionManager.login(A,B);}
function SL_IU(A){this.m_oActionSubscriptionManager.throttleGlobal(A);}
function SL_AG(A,B){this.m_oActionSubscriptionManager.throttleObjects(A,B);}
function SL_AZ(A,B){this.m_oActionSubscriptionManager.throttleObjects(A,B);}
function SL_RD(){this.m_oConnectionManager.logout();}
function SL_BC(){this.m_oConnectionManager.stop();}
function SL_ES(){return SL4B_Version.getVersion();
}
function SL_QU(){return SL4B_Version.getVersionInfo();
}
function SL_LT(C,B,A){this.m_oObjectSubscriptionManager.requestObject(this.getListener(C),B,A);}
function SL_LM(C,A,B){this.m_oObjectSubscriptionManager.requestObjects(this.getListener(C),A,B);}
function SL_LR(C,B,A){this.m_oObjectSubscriptionManager.discardObject(this.getListener(C),B,A);}
function SL_CR(C,A,B){this.m_oObjectSubscriptionManager.discardObjects(this.getListener(C),A,B);}
function SL_JJ(A){this.m_oObjectSubscriptionManager.removeSubscriber(this.getListener(A));}
SL4B_JavaScriptRttpProvider.prototype.cancelPersistedAction = function(A){return this.m_oActionSubscriptionManager.cancelPersistedAction(A);
};
SL4B_JavaScriptRttpProvider.prototype.resendPersistedAction = function(A){this.m_oActionSubscriptionManager.resendPersistedAction(A);};
function SL_RI(D,B,C,A){if(C==null||C.size()==0){throw new SL4B_Exception("JavaScriptRttpProvider.contribObject: field data object cannot be null or empty");
}return this.m_oActionSubscriptionManager.contribObject(this.getListener(D),B,C,A);
}
function SL_HB(D,B,C,A){return this.m_oActionSubscriptionManager.createObject(this.getListener(D),B,C,A);
}
function SL_KI(C,B,A){return this.m_oActionSubscriptionManager.deleteObject(this.getListener(C),B,A);
}
function SL_NY(E,D,A,B,C){var l_sCombinedFieldList=this.createFieldListForAutoDirectory(B,C,A);
this.m_oObjectSubscriptionManager.registerProxySubscriber(this.getListener(E),this.getListener(new SL4B_ProxySubscriber(E)),D,l_sCombinedFieldList);this.m_oObjectSubscriptionManager.requestObject(this.getListener(E),D,l_sCombinedFieldList);}
function SL_FW(E,D,A,B,C){var l_sCombinedFieldList=this.createFieldListForAutoDirectory(B,C,A);
this.m_oObjectSubscriptionManager.discardObject(this.getListener(E),D,l_sCombinedFieldList);}
function SL_EK(E,A,C,D,B){return this.m_oObjectSubscriptionManager.getContainer(E,A,C,D,B);
}
function SL_GY(D,A,C,B){return this.m_oObjectSubscriptionManager.getContainerSnapshot(D,A,C,B);
}
function SL_ED(B,C,A){this.m_oObjectSubscriptionManager.setContainerWindow(B,C,A);}
function SL_NL(A){this.setContainerWindow(A);}
function SL_OL(B,A){var l_oRequestData=this.m_oObjectSubscriptionManager.getContainerRequestData(A);
if(l_oRequestData==undefined||l_oRequestData==null){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_ERROR_INT,"SL4B_JavaScriptRttpProvider.prototype.removeContainer(): An error occured in removing container. Container Object key: ({0}) cannot be found.",A);}else 
{var l_sCombinedFieldList=this.createFieldListForContainer(A,l_oRequestData.getFieldList(),l_oRequestData.getWindowStart(),l_oRequestData.getWindowEnd());
this.m_oObjectSubscriptionManager.discardObject(this.getListener(B),l_oRequestData.getContainerName(),l_sCombinedFieldList);}}
SL4B_JavaScriptRttpProvider.checkWindowRange = function(B,A){if(typeof (B)!="undefined"){if(typeof (A)=="undefined"){throw new SL4B_Exception("JavaScriptRttpProvider.getContainer: When specifying a window, both start and end must be passed");
}B=parseInt(B);A=parseInt(A);if(isNaN(B)||isNaN(A)){throw new SL4B_Exception("JavaScriptRttpProvider.getContainer: Both window parameters must be an integer");
}if(0>B||B>A){throw new SL4B_Exception("JavaScriptRttpProvider.getContainer: Window start must be non-negative and window end must be more than window start");
}}};
function SL_CB(A){return this.m_oConnectionManager.getFieldName(A);
}
var SL4B_ProxySubscriber=function(){};
if(false){function SL4B_ProxySubscriber(){}
}SL4B_ProxySubscriber = function(A){this.m_oProxy=A;this.m_bIsProxySubscriber=true;this.initialise();};
SL4B_ProxySubscriber.prototype = new SL4B_AbstractSubscriber;SL4B_ProxySubscriber.prototype.ready = function(){};
SL4B_ProxySubscriber.prototype.structureChange = function(A,D,C,B){this.m_oProxy.structureChange(A,D,C,B);};
SL4B_ProxySubscriber.prototype.chat = function(C,B,D,A,E){this.m_oProxy.chat(l_sObjectName,B,D,A,E);};
SL4B_ProxySubscriber.prototype.contribOk = function(A){this.m_oProxy.contribOk(A);};
SL4B_ProxySubscriber.prototype.contribFailed = function(A,B){this.m_oProxy.contribFailed(A,B);};
SL4B_ProxySubscriber.prototype.directoryUpdated = function(D,C,A,B){this.m_oProxy.directoryUpdated(D,C,A,B);};
SL4B_ProxySubscriber.prototype.directoryMultiUpdated = function(A,B){this.m_oProxy.directoryMultiUpdated(A,B);};
SL4B_ProxySubscriber.prototype.fieldDeleted = function(A,C,B){this.m_oProxy.fieldDeleted(A,C,B);};
SL4B_ProxySubscriber.prototype.type2Clear = function(A){this.m_oProxy.type2Clear(A);};
SL4B_ProxySubscriber.prototype.type3Clear = function(A){this.m_oProxy.type3Clear(A);};
SL4B_ProxySubscriber.prototype.newsUpdated = function(A,D,C,B){this.m_oProxy.newsUpdated(A,D,C,B);};
SL4B_ProxySubscriber.prototype.objectDeleted = function(A){this.m_oProxy.objectDeleted(A);};
SL4B_ProxySubscriber.prototype.objectInfo = function(C,B,D,A){this.m_oProxy.objectInfo(C,B,D,A);};
SL4B_ProxySubscriber.prototype.objectNotFound = function(A){this.m_oProxy.objectNotFound(A);};
SL4B_ProxySubscriber.prototype.objectNotStale = function(A,B){this.m_oProxy.objectNotStale(A,B);};
SL4B_ProxySubscriber.prototype.objectReadDenied = function(A){this.m_oProxy.objectReadDenied(A);};
SL4B_ProxySubscriber.prototype.objectStale = function(A,B){this.m_oProxy.objectStale(A,B);};
SL4B_ProxySubscriber.prototype.objectStatus = function(D,B,A,C){this.m_oProxy.objectStatus(D,B,A,C);};
SL4B_ProxySubscriber.prototype.objectType = function(C,B,A){this.m_oProxy.objectType(C,B,A);};
SL4B_ProxySubscriber.prototype.objectUnavailable = function(A){this.m_oProxy.objectUnavailable(A);};
SL4B_ProxySubscriber.prototype.objectUpdated = function(A){this.m_oProxy.objectUpdated(A);};
SL4B_ProxySubscriber.prototype.objectWriteDenied = function(A){this.m_oProxy.objectWriteDenied(A);};
SL4B_ProxySubscriber.prototype.pageUpdated = function(E,D,B,A,C){this.m_oProxy.pageUpdated(E,D,B,A,C);};
SL4B_ProxySubscriber.prototype.recordMultiUpdated = function(A,B){this.m_oProxy.recordMultiUpdated(A,B);};
SL4B_ProxySubscriber.prototype.recordUpdated = function(B,C,A){this.m_oProxy.recordUpdated(B,l_oFieldData);};
SL4B_ProxySubscriber.prototype.storyReset = function(A){this.m_oProxy.storyReset(A);};
SL4B_ProxySubscriber.prototype.storyUpdated = function(A,B){this.m_oProxy.storyUpdated(A,B);};
SL4B_ProxySubscriber.prototype.permissionUpdated = function(A,B,C){this.m_oProxy.permissionUpdated(A,B,C);};
SL4B_ProxySubscriber.prototype.permissionDeleted = function(A,B){this.m_oProxy.permissionDeleted(A,B);};
function SL4B_FieldCheck(){this.m_mFieldSet={};}
SL4B_FieldCheck.prototype.addFields = function(A){if(SL4B_ObjectSubscription.isAllFields(A)){this.m_mFieldSet[SL4B_ObjectSubscription.const_ALL_FIELDS]=true;}else 
{var l_pFields=A.split(SL4B_ObjectCache.const_FIELD_NAME_DELIMITER);
for(var i=0,l_nLength=l_pFields.length;i<l_nLength;++i){this.m_mFieldSet[l_pFields[i]]=true;}}};
SL4B_FieldCheck.prototype.contains = function(A){var l_pFields=A.split(SL4B_ObjectCache.const_FIELD_NAME_DELIMITER);
var l_bContainsFields=true;
if(this.m_mFieldSet[SL4B_ObjectSubscription.const_ALL_FIELDS]!==true){if(SL4B_ObjectSubscription.isAllFields(A)){l_bContainsFields=false;}else 
{for(var i=0,l_nLength=l_pFields.length;i<l_nLength;++i){if(this.m_mFieldSet[l_pFields[i]]!==true){l_bContainsFields=false;break;
}}}}return l_bContainsFields;
};
function SL4B_XDRBase(){}
SL4B_XDRBase.prototype.open = function(C,A,B){throw new SL4B_Error("open method not implemented");
};
SL4B_XDRBase.prototype.send = function(A){throw new SL4B_Error("send method not implemented");
};
SL4B_XDRBase.prototype.setOnProgressChangeHandler = function(A){throw new SL4B_Error("setOnProgressChangeHandler method not implemented");
};
SL4B_XDRBase.prototype.setOnLoadHandler = function(A){throw new SL4B_Error("setOnLoadHandler method not implemented");
};
SL4B_XDRBase.prototype.setOnErrorHandler = function(A){throw new SL4B_Error("setOnErrorHandler method not implemented");
};
SL4B_XDRBase.prototype.getResponseText = function(){throw new SL4B_Error("getResponseText method not implemented");
};
function SL4B_XDomainRequest(A){this.m_oLogger=A;this.m_oRequest=null;this.m_fOnProgressHandler=null;this.m_fOnLoadHandler=null;this.m_fOnErrorHandler=null;this.m_nEndPosition=0;this.m_bInitialised=false;}
SL4B_XDomainRequest.prototype = new SL4B_XDRBase;SL4B_XDomainRequest.prototype._initialiseCheck = function(){if(!this.m_bInitialised){this.m_oRequest=this._createInternalXDR();this.m_oRequest.onload=this._wrapMethodCall(this,this._onLoad);this.m_oRequest.onprogress=this._wrapMethodCall(this,this._onProgressChange);this.m_oRequest.onerror=this._wrapMethodCall(this,this._onError);this.m_oRequest.ontimeout=this._wrapMethodCall(this,this._onError);this.m_bInitialised=true;}};
SL4B_XDomainRequest.prototype._wrapMethodCall = function(B,A){return function(){A.apply(B);};
};
SL4B_XDomainRequest.prototype._onLoad = function(){this.m_fOnLoadHandler();};
SL4B_XDomainRequest.prototype._onProgressChange = function(){this.m_fOnProgressHandler();};
SL4B_XDomainRequest.prototype._onError = function(){this.m_oLogger.log(SL4B_DebugLevel.const_FINEST_INT,"SL4B_XDomainRequest._onError");
try {this.m_oRequest.abort();}catch(e){}
this.m_fOnErrorHandler();};
SL4B_XDomainRequest.prototype._createInternalXDR = function(){if(window.XDomainRequest===undefined){alert("XDomainRequest not supported in this browser");}return new XDomainRequest();
};
SL4B_XDomainRequest.prototype.open = function(C,A,B){this._initialiseCheck();this.m_oLogger.log(SL4B_DebugLevel.const_FINEST_INT,"SL4B_XDomainRequest.open({0}, {1}, {2})",C,A,B);this.m_oRequest.open(C,A,B);};
SL4B_XDomainRequest.prototype.send = function(A){this.m_oLogger.log(SL4B_DebugLevel.const_FINEST_INT,"SL4B_XDomainRequest.send({0})",A);this.m_bInitialised=false;this.m_oRequest.send(A);};
SL4B_XDomainRequest.prototype.setOnProgressChangeHandler = function(A){this.m_fOnProgressHandler=A;};
SL4B_XDomainRequest.prototype.setOnLoadHandler = function(A){this.m_fOnLoadHandler=A;};
SL4B_XDomainRequest.prototype.setOnErrorHandler = function(A){this.m_fOnErrorHandler=A;};
SL4B_XDomainRequest.prototype.getResponseText = function(){var l_sMessage=this.m_oRequest.responseText;
var l_sPacket=l_sMessage.substring(this.m_nEndPosition);
this.m_nEndPosition=l_sMessage.length;return l_sPacket;
};
function SL4B_XDRXMLHttpRequest(A){this.m_oLogger=A;this.m_oRequest=null;this.m_fOnProgressHandler=null;this.m_fOnLoadHandler=null;this.m_fOnErrorHandler=null;this.m_nEndPosition=0;this.m_oRequest=this._createInternalXDR();}
SL4B_XDRXMLHttpRequest.prototype = new SL4B_XDRBase;SL4B_XDRXMLHttpRequest.prototype._initialiseCheck = function(){this.m_oRequest=this._createInternalXDR();this.m_oRequest.onreadystatechange=this._wrapMethodCall(this,this._onProgressChange);};
SL4B_XDRXMLHttpRequest.prototype._wrapMethodCall = function(B,A){return function(){A.apply(B);};
};
SL4B_XDRXMLHttpRequest.prototype._onLoad = function(){this._onProgressChange();};
SL4B_XDRXMLHttpRequest.prototype._onProgressChange = function(){if(this.m_oRequest.readyState==3){this.m_fOnProgressHandler();}else 
if(this.m_oRequest.readyState==4){if(this.m_oRequest.status!=200){
try {this.m_oRequest.abort();}catch(e){}
this.m_fOnErrorHandler();}else 
{this.m_fOnLoadHandler();}}};
SL4B_XDRXMLHttpRequest.prototype._onError = function(){this.m_fOnErrorHandler();};
SL4B_XDRXMLHttpRequest.prototype._createInternalXDR = function(){if(window.XMLHttpRequest===undefined){alert("XMLHttpRequest not supported in this browser");}return new XMLHttpRequest();
};
SL4B_XDRXMLHttpRequest.prototype.open = function(C,A,B){this._initialiseCheck();this.m_oLogger.log(SL4B_DebugLevel.const_FINEST_INT,"SL4B_XDRXMLHttpRequest.open({0}, {1}, {2})",C,A,B);this.m_oRequest.open(C,A,B);};
SL4B_XDRXMLHttpRequest.prototype.send = function(A){this.m_oLogger.log(SL4B_DebugLevel.const_FINEST_INT,"SL4B_XDRXMLHttpRequest.send({0})",A);this.m_oRequest.send(A);};
SL4B_XDRXMLHttpRequest.prototype.setOnProgressChangeHandler = function(A){this.m_fOnProgressHandler=A;};
SL4B_XDRXMLHttpRequest.prototype.setOnLoadHandler = function(A){this.m_fOnLoadHandler=A;};
SL4B_XDRXMLHttpRequest.prototype.setOnErrorHandler = function(A){this.m_fOnErrorHandler=A;};
SL4B_XDRXMLHttpRequest.prototype.getResponseText = function(){var l_sMessage=this.m_oRequest.responseText;
var l_sPacket=l_sMessage.substring(this.m_nEndPosition);
this.m_nEndPosition=l_sMessage.length;return l_sPacket;
};
var SL4B_TypeXDomainRequestConnection=function(){};
if(false){function SL4B_TypeXDomainRequestConnection(){}
}SL4B_TypeXDomainRequestConnection = function(B,A){var l_nReconnectCount=10000;
SL4B_BaseStreamingConnection.apply(this,[B,A,"RTTP-TYPE2","/sl4b/javascript-rttp-provider/empty.html",l_nReconnectCount]);this.m_oRequestChannel;this.m_oResponseChannel;this.CLASS_NAME="SL4B_TypeXDomainRequestConnection";};
SL4B_TypeXDomainRequestConnection.prototype = new SL4B_BaseStreamingConnection;SL4B_TypeXDomainRequestConnection.prototype.start = function(){this.m_oRequestChannel=new SL4B_StreamingXDomainRequest(this.m_oCurrentConnectionData,this.getUrlPrefix(),SL4B_JavaScriptRttpProviderConstants.const_REQUEST_TYPE);this.m_oRequestChannel.initialise();this.m_oResponseChannel=new SL4B_StreamingXDomainRequest(this.m_oCurrentConnectionData,this.getUrlPrefix(),SL4B_JavaScriptRttpProviderConstants.const_RESPONSE_TYPE);this.m_oResponseChannel.initialise();this.m_oResponseChannel.start();};
SL4B_TypeXDomainRequestConnection.prototype._$stopResponseStream = function(){if(this._$getConnectionState()!==3){SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"Invoking stop on streaming connection.");
try {this.m_oResponseChannel.stop();}catch(e){SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"Attempt to invoke stop on type response stream failed: {0}, connection state {1}",SL4B_Accessor.getBrowserAdapter().convertExceptionToString(e),this._$getConnectionState());}
this._$setConnectionState(3);}};
SL4B_TypeXDomainRequestConnection.prototype.createChannel = function(){return null;
};
var SL4B_TypeXDRXMLHttpRequestConnection=function(){};
if(false){function SL4B_TypeXDRXMLHttpRequestConnection(){}
}SL4B_TypeXDRXMLHttpRequestConnection = function(B,A){var l_nReconnectCount=10000;
SL4B_BaseStreamingConnection.apply(this,[B,A,"RTTP-TYPE2","/sl4b/javascript-rttp-provider/empty.html",l_nReconnectCount]);this.m_oRequestChannel;this.m_oResponseChannel;this.CLASS_NAME="SL4B_TypeXDRXMLHttpRequestConnection";};
SL4B_TypeXDRXMLHttpRequestConnection.prototype = new SL4B_BaseStreamingConnection;SL4B_TypeXDRXMLHttpRequestConnection.prototype.start = function(){this.m_oRequestChannel=new SL4B_StreamingXDRXMLHttpRequest(this.m_oCurrentConnectionData,this.getUrlPrefix(),SL4B_JavaScriptRttpProviderConstants.const_REQUEST_TYPE);this.m_oRequestChannel.initialise();this.m_oResponseChannel=new SL4B_StreamingXDRXMLHttpRequest(this.m_oCurrentConnectionData,this.getUrlPrefix(),SL4B_JavaScriptRttpProviderConstants.const_RESPONSE_TYPE);this.m_oResponseChannel.initialise();this.m_oResponseChannel.start();};
SL4B_TypeXDRXMLHttpRequestConnection.prototype._$stopResponseStream = function(){if(this._$getConnectionState()!==3){SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"Invoking stop on streaming connection.");
try {this.m_oResponseChannel.stop();}catch(e){SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"Attempt to invoke stop on type response stream failed: {0}, connection state {1}",SL4B_Accessor.getBrowserAdapter().convertExceptionToString(e),this._$getConnectionState());}
this._$setConnectionState(3);}};
SL4B_TypeXDRXMLHttpRequestConnection.prototype.createChannel = function(){return null;
};
SL4B_StreamingXDRBase = function(B,A,C){this.m_oConnectionData=B;this.m_sUrlPrefix=A;this.m_sChannelType=C;this.m_oHttpRequest=null;this.m_oSL4B_Logger=null;this.m_oSL4B_DebugLevel;this.m_bReady=false;this.m_oHttpRequest=null;this.m_sXmlHttpRequestType="";this.m_sPartialMessage=null;this.m_bStopped=false;};
SL4B_StreamingXDRBase.prototype.initialise = function(){this.initialiseLogger();this.initialiseXDR();this.ready();};
SL4B_StreamingXDRBase.prototype.getClassName = function(){throw new SL4B_Error("getClassName method not implemented");
};
SL4B_StreamingXDRBase.prototype.initialiseXDR = function(){throw new SL4B_Error("initialiseXDR method not implemented");
};
SL4B_StreamingXDRBase.prototype.getPaddingLengthQueryNameAndValue = function(){throw new SL4B_Error("getPaddingLengthQueryNameAndValue method not implemented");
};
SL4B_StreamingXDRBase.prototype.setHttpRequest = function(A){this.m_oHttpRequest=A;var oThis=this;
var fOnProgressHandler=function(){oThis.onProgressChange();};
var fOnLoadHandler=function(){oThis.onLoad();};
var fOnErrorHandler=function(){SL4B_Accessor.getUnderlyingRttpProvider().createConnectionLost("Connection Error",true);};
this.m_oHttpRequest.setOnProgressChangeHandler(fOnProgressHandler);this.m_oHttpRequest.setOnLoadHandler(fOnLoadHandler);this.m_oHttpRequest.setOnErrorHandler(fOnErrorHandler);};
SL4B_StreamingXDRBase.prototype.initialiseLogger = function(){if(this.m_oSL4B_Logger==null){this.m_oSL4B_Logger=SL4B_Logger;this.m_oSL4B_DebugLevel=SL4B_DebugLevel;this.getLogger().log(this.m_oSL4B_DebugLevel.const_INFO_INT,this.getClassName()+"initialiseLogger: using "+this.m_sXmlHttpRequestType+" "+this.getClassName()+" object");}};
SL4B_StreamingXDRBase.prototype.getLogger = function(){return this.m_oSL4B_Logger;
};
SL4B_StreamingXDRBase.prototype.onProgressChange = function(){var l_sPacket=this.m_oHttpRequest.getResponseText();
this.getLogger().log(this.m_oSL4B_DebugLevel.const_RTTP_FINEST_INT,this.getClassName()+".onProgressChange: data received < "+l_sPacket);if(!this.m_bStopped){var l_pPackets=l_sPacket.split('\n');
if(l_pPackets[l_pPackets.length-1]==""){l_pPackets.splice(l_pPackets.length-1,1);}if(this.m_sPartialMessage!=null){l_pPackets[0]=this.m_sPartialMessage+l_pPackets[0];this.m_sPartialMessage=null;}if(l_sPacket.substring(l_sPacket.length-1)!="\n"){this.m_sPartialMessage=l_pPackets.splice(l_pPackets.length-1,1)[0];}if(l_pPackets.length>0){SL4B_ConnectionProxy.getInstance().processRttpMessageBlock(l_pPackets);}}};
SL4B_StreamingXDRBase.prototype.onLoad = function(){this.onProgressChange();this.ready();};
SL4B_StreamingXDRBase.prototype.ready = function(){this.m_bReady=true;if(this.m_oSL4B_DebugLevel){
try {if(this.m_sChannelType==SL4B_JavaScriptRttpProviderConstants.const_RESPONSE_TYPE){SL4B_ConnectionProxy.getInstance().setResponseHttpRequest(this,null);}else 
{SL4B_ConnectionProxy.getInstance().setRequestHttpRequest(this,null);}}catch(e){this.log(this.m_oSL4B_DebugLevel.const_WARN_INT,"SL4B_StreamingXDomainRequest.ready: error {0}",e);}
}};
SL4B_StreamingXDRBase.prototype.start = function(){this.send(this.m_sUrlPrefix+"?"+this.getPaddingLengthQueryNameAndValue(),true);};
SL4B_StreamingXDRBase.prototype.stop = function(){this.m_bStopped=true;this.m_oHttpRequest.stop();};
SL4B_StreamingXDRBase.prototype.send = function(B,A){if(this.m_bReady){this.m_bReady=false;this.log(9,"send>"+B+"<\n");var l_nPos=B.indexOf("?");
var l_sUrl=B;
var l_sMessage="";
if(l_nPos!=-1){l_sUrl=B.substring(0,l_nPos);l_sMessage=B.substring(l_nPos+1);}
try {var l_sUrlPrefix=this.m_oConnectionData.getServerUrl().match(/(https?:\/\/[^\/]+)/)[1];
var l_bMakeAsynchronousRequest=(A!==undefined ? A : true);
if(l_sUrl.indexOf("LOGOUT")!=-1){l_bMakeAsynchronousRequest=false;}if(SL4B_Accessor&&SL4B_Accessor.getCapabilities!==undefined){this.m_nMaxGetLength=SL4B_Accessor.getCapabilities().getHttpRequestLineLength();}this.lastRequest=B;var sMethod="GET";
var sRequest=l_sUrlPrefix+B;
if(l_sMessage.length>this.m_nMaxGetLength){sMethod="POST";sRequest=l_sUrlPrefix+l_sUrl;}else 
{l_sMessage=null;}this.m_oHttpRequest.open(sMethod,sRequest,l_bMakeAsynchronousRequest);this.m_oHttpRequest.send(l_sMessage);}catch(e){this.log(this.m_oSL4B_DebugLevel.const_ERROR_INT,this.getClassName()+".send: Problem in channel {1}, sending: {0}",e.name+": "+e.message,this.m_sChannelType);if(SL4B_ConnectionProxy.getInstance().httpRequestError){SL4B_ConnectionProxy.getInstance().httpRequestError(e,this.m_sChannelType,B);}else 
{this.log(this.m_oSL4B_DebugLevel.const_ERROR_INT,"An error occcurred while sending on the {0} channel, {1}: {2}. The version of SL4B on the client may be out of date.",this.m_sChannelType,e.name,e.message);}}
}else 
{alert("not ready");}};
SL4B_StreamingXDRBase.prototype.log = function(){if(this.m_oSL4B_Logger!=null){this.getLogger().log.apply(this.m_oSL4B_Logger,arguments);}};
SL4B_StreamingXDomainRequest = function(B,A,C){SL4B_StreamingXDRBase.call(this,B,A,C);};
SL4B_StreamingXDomainRequest.prototype = new SL4B_StreamingXDRBase;SL4B_StreamingXDomainRequest.prototype.getClassName = function(){return "SL4B_StreamingXDomainRequest";
};
SL4B_StreamingXDomainRequest.prototype.initialiseXDR = function(){var oRequest=new SL4B_XDomainRequest(this.getLogger());
this.setHttpRequest(oRequest);};
SL4B_StreamingXDomainRequest.prototype.getPaddingLengthQueryNameAndValue = function(){return "X-RTTP-Type2-Pad-Length=4096";
};
SL4B_StreamingXDRXMLHttpRequest = function(B,A,C){SL4B_StreamingXDRBase.call(this,B,A,C);};
SL4B_StreamingXDRXMLHttpRequest.prototype = new SL4B_StreamingXDRBase;SL4B_StreamingXDRXMLHttpRequest.prototype.getClassName = function(){return "SL4B_StreamingXDRXMLHttpRequest";
};
SL4B_StreamingXDRXMLHttpRequest.prototype.initialiseXDR = function(){var oRequest=new SL4B_XDRXMLHttpRequest(this.getLogger());
this.setHttpRequest(oRequest);};
SL4B_StreamingXDRXMLHttpRequest.prototype.getPaddingLengthQueryNameAndValue = function(){return "X-RTTP-Type2-Pad-Length=2000";
};
var SL4B_FlashConnection=function(){};
if(false){function SL4B_FlashConnection(){}
}SL4B_FlashConnection = function(B,A){var l_nReconnectCount=10000;
SL4B_BaseStreamingConnection.apply(this,[B,A,"RTTP-TYPE2","/sl4b/javascript-rttp-provider/empty.html",l_nReconnectCount]);this.m_oRequestChannel;this.m_oResponseChannel;this.CLASS_NAME="SL4B_FlashConnection";};
SL4B_FlashConnection.prototype = new SL4B_BaseStreamingConnection;SL4B_FlashConnection.prototype.start = function(){this.m_oRequestChannel=new SL4B_StreamingFlashRequest(this.m_oCurrentConnectionData,this.getUrlPrefix(),SL4B_JavaScriptRttpProviderConstants.const_REQUEST_TYPE);this.m_oRequestChannel.initialise();this.m_oResponseChannel=new SL4B_StreamingFlashRequest(this.m_oCurrentConnectionData,this.getUrlPrefix(),SL4B_JavaScriptRttpProviderConstants.const_RESPONSE_TYPE);this.m_oResponseChannel.initialise();this.m_oResponseChannel.start();};
SL4B_FlashConnection.prototype._$stopResponseStream = function(){if(this._$getConnectionState()!==3){SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"Invoking stop on streaming connection.");
try {this.m_oResponseChannel.stop();}catch(e){SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"Attempt to invoke stop on type response stream failed: {0}, connection state {1}",SL4B_Accessor.getBrowserAdapter().convertExceptionToString(e),this._$getConnectionState());}

try {this.m_oRequestChannel.stop();}catch(e){SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"Attempt to invoke stop on type request channel failed: {0}, connection state {1}",SL4B_Accessor.getBrowserAdapter().convertExceptionToString(e),this._$getConnectionState());}
this._$setConnectionState(3);}};
SL4B_FlashConnection.prototype.createChannel = function(){return null;
};
SL4B_StreamingFlashRequest = function(B,A,C){SL4B_StreamingXDRBase.call(this,B,A,C);};
SL4B_StreamingFlashRequest.prototype = new SL4B_StreamingXDRBase;SL4B_StreamingFlashRequest.prototype.getClassName = function(){return "SL4B_StreamingFlashRequest";
};
SL4B_StreamingFlashRequest.prototype.initialiseXDR = function(){var oRequest=new SL4B_FlashRequest(this.getLogger());
this.setHttpRequest(oRequest);};
SL4B_StreamingFlashRequest.prototype.getPaddingLengthQueryNameAndValue = function(){return "X-RTTP-Type2-Pad-Length=4096";
};
eval(function(F,D,E,C,G,B){G = function(A){return (A<D ? "" : G(parseInt(A/D)))+((A=A%D)>35 ? String.fromCharCode(A+29) : A.toString(36));
};
if(!''.replace(/^/,String)){while(E--)B[G(E)]=C[E]||G(E);C=[function(A){return B[A];
}];G = function(){return '\\w+';
};
E=1;}while(E--)if(C[E])F=F.replace(new RegExp('\\b'+G(E)+'\\b','g'),C[E]);return F;
}, ('5 1V=11(){5 D="2M",r="2b",S="3O 2q",W="2I.2I",q="2Y/x-2W-2U",R="2V",x="32",O=2u,j=31,t=2Z,T=1c,U=[h],o=[],N=[],I=[],l,Q,E,B,J=1c,a=1c,n,G,m=1e,M=11(){5 9=15 j.2v!=D&&15 j.1v!=D&&15 j.2x!=D,19=t.2T.1t(),Y=t.2Q.1t(),12=Y?/1g/.1m(Y):/1g/.1m(19),17=Y?/1M/.1m(Y):/1M/.1m(19),1b=/2t/.1m(19)?3j(19.1I(/^.*2t\\/(\\d+(\\.\\d+)?).*$/,"$1")):1c,X=!+"\\3l",1a=[0,0,0],7=1f;3(15 t.24!=D&&15 t.24[S]==r){7=t.24[S].3v;3(7&&!(15 t.2c!=D&&t.2c[q]&&!t.2c[q].3a)){T=1e;X=1c;7=7.1I(/^.*\\s+(\\S+\\s+\\S+$)/,"$1");1a[0]=1h(7.1I(/^(.*)\\..*$/,"$1"),10);1a[1]=1h(7.1I(/^.*\\.(.*)\\s.*$/,"$1"),10);1a[2]=/[a-2N-Z]/.1m(7)?1h(7.1I(/^.*[a-2N-Z]+(.*)$/,"$1"),10):0}}13{3(15 O.2B!=D){21{5 14=37 2B(W);3(14){7=14.2n("$2C");3(7){X=1e;7=7.1B(" ")[1].1B(",");1a=[1h(7[0],10),1h(7[1],10),1h(7[2],10)]}}}1U(Z){}}}16{1s:9,1D:1a,1o:1b,1k:X,1g:12,1M:17}}(),k=11(){3(!M.1s){16}3((15 j.1n!=D&&j.1n=="2i")||(15 j.1n==D&&(j.1v("1N")[0]||j.1N))){f()}3(!J){3(15 j.1E!=D){j.1E("3f",f,1c)}3(M.1k&&M.1g){j.1Q(x,11(){3(j.1n=="2i"){j.2o(x,1z.1y);f()}});3(O==3g){(11(){3(J){16}21{j.3h.3c("3d")}1U(X){1K(1z.1y,0);16}f()})()}}3(M.1o){(11(){3(J){16}3(!/3e|2i/.1m(j.1n)){1K(1z.1y,0);16}f()})()}s(f)}}();11 f(){3(J){16}21{5 Z=j.1v("1N")[0].1F(C("2X"));Z.1l.1J(Z)}1U(9){16}J=1e;5 X=U.1i;1d(5 Y=0;Y<X;Y++){U[Y]()}}11 K(X){3(J){X()}13{U[U.1i]=X}}11 s(Y){3(15 O.1E!=D){O.1E("2G",Y,1c)}13{3(15 j.1E!=D){j.1E("2G",Y,1c)}13{3(15 O.1Q!=D){i(O,"1L",Y)}13{3(15 O.1L=="11"){5 X=O.1L;O.1L=11(){X();Y()}}13{O.1L=Y}}}}}11 h(){3(T){V()}13{H()}}11 V(){5 X=j.1v("1N")[0];5 9=C(r);9.1q("2f",q);5 Z=X.1F(9);3(Z){5 Y=0;(11(){3(15 Z.2n!=D){5 7=Z.2n("$2C");3(7){7=7.1B(" ")[1].1B(",");M.1D=[1h(7[0],10),1h(7[1],10),1h(7[2],10)]}}13{3(Y<10){Y++;1K(1z.1y,10);16}}X.1J(9);Z=1f;H()})()}13{H()}}11 H(){5 1a=o.1i;3(1a>0){1d(5 1b=0;1b<1a;1b++){5 Y=o[1b].1j;5 7=o[1b].2K;5 9={1C:1c,1j:Y};3(M.1D[0]>0){5 12=c(Y);3(12){3(F(o[1b].2s)&&!(M.1o&&M.1o<1Y)){w(Y,1e);3(7){9.1C=1e;9.2g=z(Y);7(9)}}13{3(o[1b].27&&A()){5 18={};18.1R=o[1b].27;18.1w=12.1r("1w")||"0";18.1x=12.1r("1x")||"0";3(12.1r("1Z")){18.2a=12.1r("1Z")}3(12.1r("25")){18.25=12.1r("25")}5 19={};5 X=12.1v("2k");5 17=X.1i;1d(5 14=0;14<17;14++){3(X[14].1r("1P").1t()!="2l"){19[X[14].1r("1P")]=X[14].1r("22")}}P(18,19,Y,7)}13{p(12);3(7){7(9)}}}}}13{w(Y,1e);3(7){5 Z=z(Y);3(Z&&15 Z.2E!=D){9.1C=1e;9.2g=Z}7(9)}}}}}11 z(9){5 X=1f;5 Y=c(9);3(Y&&Y.1O=="29"){3(15 Y.2E!=D){X=Y}13{5 Z=Y.1v(r)[0];3(Z){X=Z}}}16 X}11 A(){16!a&&F("6.0.3b")&&(M.1g||M.1M)&&!(M.1o&&M.1o<1Y)}11 P(9,7,X,Z){a=1e;E=Z||1f;B={1C:1c,1j:X};5 12=c(X);3(12){3(12.1O=="29"){l=g(12);Q=1f}13{l=12;Q=X}9.1j=R;3(15 9.1w==D||(!/%$/.1m(9.1w)&&1h(9.1w,10)<2D)){9.1w="2D"}3(15 9.1x==D||(!/%$/.1m(9.1x)&&1h(9.1x,10)<2J)){9.1x="2J"}j.2m=j.2m.36(0,35)+" - 2q 39 38";5 14=M.1k&&M.1g?"3i":"3s",17="3r="+O.23.3q().1I(/&/g,"%26")+"&3u="+14+"&3t="+j.2m;3(15 7.1G!=D){7.1G+="&"+17}13{7.1G=17}3(M.1k&&M.1g&&12.1n!=4){5 Y=C("2j");X+="3p";Y.1q("1j",X);12.1l.2z(Y,12);12.1H.1X="2e";(11(){3(12.1n==4){12.1l.1J(12)}13{1K(1z.1y,10)}})()}u(9,7,X)}}11 p(Y){3(M.1k&&M.1g&&Y.1n!=4){5 X=C("2j");Y.1l.2z(X,Y);X.1l.1T(g(Y),X);Y.1H.1X="2e";(11(){3(Y.1n==4){Y.1l.1J(Y)}13{1K(1z.1y,10)}})()}13{Y.1l.1T(g(Y),Y)}}11 g(7){5 9=C("2j");3(M.1g&&M.1k){9.2w=7.2w}13{5 Y=7.1v(r)[0];3(Y){5 14=Y.3k;3(14){5 X=14.1i;1d(5 Z=0;Z<X;Z++){3(!(14[Z].2L==1&&14[Z].1O=="3o")&&!(14[Z].2L==8)){9.1F(14[Z].3n(1e))}}}}}16 9}11 u(18,1a,Y){5 X,9=c(Y);3(M.1o&&M.1o<1Y){16 X}3(9){3(15 18.1j==D){18.1j=Y}3(M.1k&&M.1g){5 19="";1d(5 12 1p 18){3(18[12]!=1S.20[12]){3(12.1t()=="1R"){1a.2l=18[12]}13{3(12.1t()=="2a"){19+=\' 1Z="\'+18[12]+\'"\'}13{3(12.1t()!="2h"){19+=" "+12+\'="\'+18[12]+\'"\'}}}}}5 1b="";1d(5 14 1p 1a){3(1a[14]!=1S.20[14]){1b+=\'<2k 1P="\'+14+\'" 22="\'+1a[14]+\'" />\'}}9.3m=\'<2b 2h="34:2R-2P-2S-30-33"\'+19+">"+1b+"</2b>";N[N.1i]=18.1j;X=c(18.1j)}13{5 Z=C(r);Z.1q("2f",q);1d(5 17 1p 18){3(18[17]!=1S.20[17]){3(17.1t()=="2a"){Z.1q("1Z",18[17])}13{3(17.1t()!="2h"){Z.1q(17,18[17])}}}}1d(5 7 1p 1a){3(1a[7]!=1S.20[7]&&7.1t()!="2l"){e(Z,7,1a[7])}}9.1l.1T(Z,9);X=Z}}16 X}11 e(Z,X,Y){5 9=C("2k");9.1q("1P",X);9.1q("22",Y);Z.1F(9)}11 y(Y){5 X=c(Y);3(X&&X.1O=="29"){3(M.1k&&M.1g){X.1H.1X="2e";(11(){3(X.1n==4){b(Y)}13{1K(1z.1y,10)}})()}13{X.1l.1J(X)}}}11 b(Z){5 Y=c(Z);3(Y){1d(5 X 1p Y){3(15 Y[X]=="11"){Y[X]=1f}}Y.1l.1J(Y)}}11 c(Z){5 X=1f;21{X=j.2v(Z)}1U(Y){}16 X}11 C(X){16 j.2x(X)}11 i(Z,X,Y){Z.1Q(X,Y);I[I.1i]=[Z,X,Y]}11 F(Z){5 Y=M.1D,X=Z.1B(".");X[0]=1h(X[0],10);X[1]=1h(X[1],10)||0;X[2]=1h(X[2],10)||0;16(Y[0]>X[0]||(Y[0]==X[0]&&Y[1]>X[1])||(Y[0]==X[0]&&Y[1]==X[1]&&Y[2]>=X[2]))?1e:1c}11 v(17,Y,14,7){3(M.1k&&M.1M){16}5 9=j.1v("3Z")[0];3(!9){16}5 X=(14&&15 14=="41")?14:"3Y";3(7){n=1f;G=1f}3(!n||G!=X){5 Z=C("1H");Z.1q("2f","3X/3M");Z.1q("3B",X);n=9.1F(Z);3(M.1k&&M.1g&&15 j.1W!=D&&j.1W.1i>0){n=j.1W[j.1W.1i-1]}G=X}3(M.1k&&M.1g){3(n&&15 n.2p==r){n.2p(17,Y)}}13{3(n&&15 j.2y!=D){n.1F(j.2y(17+" {"+Y+"}"))}}}11 w(Z,X){3(!m){16}5 Y=X?"3x":"3z";3(J&&c(Z)){c(Z).1H.2A=Y}13{v("#"+Z,"2A:"+Y)}}11 L(Y){5 Z=/[\\\\\\"<>\\.;]/;5 X=Z.3J(Y)!=1f;16 X&&15 2r!=D?2r(Y):Y}5 d=11(){3(M.1k&&M.1g){2u.1Q("3K",11(){5 17=I.1i;1d(5 7=0;7<17;7++){I[7][0].2o(I[7][1],I[7][2])}5 Z=N.1i;1d(5 9=0;9<Z;9++){y(N[9])}1d(5 Y 1p M){M[Y]=1f}M=1f;1d(5 X 1p 1V){1V[X]=1f}1V=1f})}}();16{3G:11(7,X,9,Z){3(M.1s&&7&&X){5 Y={};Y.1j=7;Y.2s=X;Y.27=9;Y.2K=Z;o[o.1i]=Y;w(7,1c)}13{3(Z){Z({1C:1c,1j:7})}}},3H:11(X){3(M.1s){16 z(X)}},3A:11(7,19,12,1a,Y,9,Z,14,1b,17){5 X={1C:1c,1j:19};3(M.1s&&!(M.1o&&M.1o<1Y)&&7&&19&&12&&1a&&Y){w(19,1c);K(11(){12+="";1a+="";5 1u={};3(1b&&15 1b===r){1d(5 2d 1p 1b){1u[2d]=1b[2d]}}1u.1R=7;1u.1w=12;1u.1x=1a;5 1A={};3(14&&15 14===r){1d(5 28 1p 14){1A[28]=14[28]}}3(Z&&15 Z===r){1d(5 18 1p Z){3(15 1A.1G!=D){1A.1G+="&"+18+"="+Z[18]}13{1A.1G=18+"="+Z[18]}}}3(F(Y)){5 2H=u(1u,1A,19);3(1u.1j==19){w(19,1e)}X.1C=1e;X.2g=2H}13{3(9&&A()){1u.1R=9;P(1u,1A,19,17);16}13{w(19,1e)}}3(17){17(X)}})}13{3(17){17(X)}}},3S:11(){m=1c},3R:M,3w:11(){16{3I:M.1D[0],3L:M.1D[1],3F:M.1D[2]}},3P:F,3D:11(Z,Y,X){3(M.1s){16 u(Z,Y,X)}13{16 2M}},3E:11(Z,9,X,Y){3(M.1s&&A()){P(Z,9,X,Y)}},3y:11(X){3(M.1s){y(X)}},3C:11(9,Z,Y,X){3(M.1s){v(9,Z,Y,X)}},3W:K,40:s,3N:11(9){5 Z=j.23.3Q||j.23.3T;3(Z){3(/\\?/.1m(Z)){Z=Z.1B("?")[1]}3(9==1f){16 L(Z)}5 Y=Z.1B("&");1d(5 X=0;X<Y.1i;X++){3(Y[X].2O(0,Y[X].2F("="))==9){16 L(Y[X].2O((Y[X].2F("=")+1)))}}}16""},3V:11(){3(a){5 X=c(R);3(X&&l){X.1l.1T(l,X);3(Q){w(Q,1e);3(M.1k&&M.1g){l.1H.1X="3U"}}3(E){E(B)}}a=1c}}}}();', 62, 250, '|||if||var||ab||aa||||||||||||||||||||||||||||||||||||||||||||||||||||||function|ae|else|ad|typeof|return|ac|ai|ah|ag|af|false|for|true|null|win|parseInt|length|id|ie|parentNode|test|readyState|wk|in|setAttribute|getAttribute|w3|toLowerCase|aj|getElementsByTagName|width|height|callee|arguments|am|split|success|pv|addEventListener|appendChild|flashvars|style|replace|removeChild|setTimeout|onload|mac|body|nodeName|name|attachEvent|data|Object|replaceChild|catch|swfobject|styleSheets|display|312|class|prototype|try|value|location|plugins|align||expressInstall|ak|OBJECT|styleclass|object|mimeTypes|al|none|type|ref|classid|complete|div|param|movie|title|GetVariable|detachEvent|addRule|Flash|encodeURIComponent|swfVersion|webkit|window|getElementById|innerHTML|createElement|createTextNode|insertBefore|visibility|ActiveXObject|version|310|SetVariable|indexOf|load|an|ShockwaveFlash|137|callbackFn|nodeType|undefined|zA|substring|AE6D|platform|D27CDB6E|11cf|userAgent|flash|SWFObjectExprInst|shockwave|span|application|navigator|96B8|document|onreadystatechange|444553540000|clsid|47|slice|new|Installation|Player|enabledPlugin|65|doScroll|left|loaded|DOMContentLoaded|top|documentElement|ActiveX|parseFloat|childNodes|v1|outerHTML|cloneNode|PARAM|SWFObjectNew|toString|MMredirectURL|PlugIn|MMdoctitle|MMplayerType|description|getFlashPlayerVersion|visible|removeSWF|hidden|embedSWF|media|createCSS|createSWF|showExpressInstall|release|registerObject|getObjectById|major|exec|onunload|minor|css|getQueryParamValue|Shockwave|hasFlashPlayerVersion|search|ua|switchOffAutoHideShow|hash|block|expressInstallCallback|addDomLoadEvent|text|screen|head|addLoadEvent|string'.split('|'), 0, {}));function SL4B_FlashRequest(A){this.m_nId=SL4B_FlashRequest._instanceId++;SL4B_FlashRequest._instances[this.m_nId]=this;this.m_oPendingOpen=null;this.m_oPendingSend=null;this.m_sLastResponseText=null;this.m_sFlashContainerElId=null;this.m_sFlashObjectElId=null;this.m_oFlashObject=null;this.m_oLogger=A;this.m_fOnProgressHandler=null;this.m_fOnLoadHandler=null;this.m_fOnErrorHandler=null;this.m_oFlashObject=this._createInternalXDR();this.m_bInitialised=false;SL4B_WindowEventHandler.addListener(this);}
SL4B_FlashRequest.prototype = new SL4B_XDRBase;SL4B_FlashRequest._instanceId=0;SL4B_FlashRequest._instances={};SL4B_FlashRequest.call = function(C,B,A){
try {SL4B_FlashRequest._log("SL4B_FlashRequest.call("+C+", "+B+", "+A+")");var instance=SL4B_FlashRequest._instances[C];
instance[B].apply(instance,A||[]);}catch(e){SL4B_FlashRequest._log("Error in SL4B_FlashRequest.call: "+e,SL4B_DebugLevel.const_ERROR_INT);}
};
SL4B_FlashRequest.global = function(B,A){
try {SL4B_FlashRequest._log("SL4B_FlashRequest.global("+B+", "+A+")");SL4B_FlashRequest[B].apply(SL4B_FlashRequest,A||[]);}catch(e){SL4B_FlashRequest._log("Error in SL4B_FlashRequest.global: "+e,SL4B_DebugLevel.const_ERROR_INT);}
};
SL4B_FlashRequest._log = function(A,B){B=(B===undefined ? SL4B_DebugLevel.const_FINE_INT : B);SL4B_Logger.log(B,A);};
SL4B_FlashRequest.prototype._createInternalXDR = function(){var sFlashFilesDirPath=SL4B_Accessor.getConfiguration().getAttribute("flashconnectiondirpath");
if(!sFlashFilesDirPath){var sMsg="flashconnectiondirpath configuration must be defined to se the Flash connection type";
this.m_oLogger.log(SL4B_DebugLevel.const_ERROR_INT,sMsg);throw new SL4B_Exception(sMsg);
}var flashvars={instanceId:this.m_nId};
var params={menu:"false", scale:"noScale", allowFullscreen:"true", allowScriptAccess:"always", bgcolor:"#FFFFFF"};
this.m_sFlashContainerElId="SL4B_FlashRequest_"+this.m_nId+"_container";this.m_sFlashObjectElId="SL4B_FlashRequest_"+this.m_nId+"_object";var el=document.createElement("div");
el.id=this.m_sFlashContainerElId;document.body.appendChild(el);var attributes={id:this.m_sFlashObjectElId};
swfobject.embedSWF(sFlashFilesDirPath+"sl4bflashconnection.swf",this.m_sFlashContainerElId,"0%","0%","10.0.0",sFlashFilesDirPath+"expressInstall.swf",flashvars,params,attributes);return document.getElementById(this.m_sFlashObjectElId);
};
SL4B_FlashRequest.prototype.onBeforeUnload = function(){this.stop();};
SL4B_FlashRequest.prototype.onUnload = function(){this.stop();};
SL4B_FlashRequest.prototype._checkInitialised = function(){return this.m_bInitialised;
};
SL4B_FlashRequest.prototype._flashReady = function(){this.m_bInitialised=true;if(this.m_oPendingOpen){this.m_oLogger.log(SL4B_DebugLevel.const_FINEST_INT,"Making pending open call");this.open(this.m_oPendingOpen.method,this.m_oPendingOpen.url,this.m_oPendingOpen.async);this.m_oPendingOpen=null;}if(this.m_oPendingSend){this.m_oLogger.log(SL4B_DebugLevel.const_FINEST_INT,"Making pending send call");this.send(this.m_oPendingSend.body);this.m_oPendingSend=null;}};
SL4B_FlashRequest.prototype._onLoad = function(){this._onProgressChange();};
SL4B_FlashRequest.prototype._onProgressChange = function(A,C,B){this.m_sLastResponseText=B||null;A=(A===undefined ? this.m_oFlashObject.getReadyState() : A);C=(C===undefined ? this.m_oFlashObject.getStatus() : C);if(A==3){this.m_fOnProgressHandler();}else 
if(A==4){if(C!=200){
try {this.stop();}catch(e){}
this.m_fOnErrorHandler();}else 
{this.m_fOnLoadHandler();}}};
SL4B_FlashRequest.prototype._onError = function(){this.m_fOnErrorHandler();};
SL4B_FlashRequest.prototype.open = function(C,A,B){if(this._checkInitialised()){this.m_oLogger.log(SL4B_DebugLevel.const_FINEST_INT,"SL4B_FlashRequest.open({0}, {1}, {2})",C,A,B);this.m_oFlashObject.open(C,A,B);}else 
{this.m_oPendingOpen={method:C, url:A, async:B};}};
SL4B_FlashRequest.prototype.send = function(A){if(this._checkInitialised()){this.m_oLogger.log(SL4B_DebugLevel.const_FINEST_INT,"SL4B_FlashRequest.send({0})",A);this.m_oFlashObject.send(A);}else 
{this.m_oPendingSend={body:A};}};
SL4B_FlashRequest.prototype.stop = function(){
try {if(this.m_oFlashObject){this.m_oFlashObject._$stop();swfobject.removeSWF(this.m_sFlashObjectElId);}delete SL4B_FlashRequest._instances[this.m_nId];}catch(e){}
};
SL4B_FlashRequest.prototype.setOnProgressChangeHandler = function(A){this.m_fOnProgressHandler=A;};
SL4B_FlashRequest.prototype.setOnLoadHandler = function(A){this.m_fOnLoadHandler=A;};
SL4B_FlashRequest.prototype.setOnErrorHandler = function(A){this.m_fOnErrorHandler=A;};
SL4B_FlashRequest.prototype.getResponseText = function(){var sResponseText=this.m_sLastResponseText;
if(sResponseText===null){sResponseText=this.m_oFlashObject.getResponseText();}return sResponseText;
};
var SL4B_TestRttpProvider=function(){};
if(false){function SL4B_TestRttpProvider(){}
}SL4B_TestRttpProvider = function(){SL4B_AbstractRttpProvider.apply(this);this.m_pObjects=new Object();this.m_pFields=new Object();this.m_pSubscriptions=new Object();this.m_nTickCount=1;this.addDecimalField("ASK",1.53,3.23);this.addDecimalField("BID",1.53,3.23);this.addDecimalField("OPEN",1.53,3.23);this.addDecimalField("CLOSE",1.53,3.23);this.addIntegerField("TRDPRC_1",10,23);this.addIntegerField("LAST",1,5);this.addObject("/I/VOD.L",1500,1);this.addObject("/I/BP.L",5000,4);this.addObject("/I/BARC.L",3000,133);this.addObject("/I/GBP=",12500,234);};
SL4B_TestRttpProvider.prototype = new SL4B_AbstractRttpProvider;SL4B_TestRttpProvider.prototype.tick = SL_CT;SL4B_TestRttpProvider.prototype.addDecimalField = SL_CV;SL4B_TestRttpProvider.prototype.addIntegerField = SL_JB;SL4B_TestRttpProvider.prototype.addStringField = SL_BR;SL4B_TestRttpProvider.prototype.addObject = SL_EQ;SL4B_TestRttpProvider.prototype.getNextValue = SL_LN;SL4B_TestRttpProvider.prototype.initialise = SL_GA;SL4B_TestRttpProvider.prototype.connect = SL_EY;SL4B_TestRttpProvider.prototype.login = SL_FQ;SL4B_TestRttpProvider.prototype.getObject = SL_DJ;SL4B_TestRttpProvider.prototype.getObjects = SL_PG;SL4B_TestRttpProvider.prototype.removeObject = SL_FS;SL4B_TestRttpProvider.prototype.removeObjects = SL_EF;SL4B_TestRttpProvider.prototype.getObjectType = SL_MR;SL4B_TestRttpProvider.prototype.setThrottleObject = SL_LQ;SL4B_TestRttpProvider.prototype.setThrottleObjects = SL_RS;SL4B_TestRttpProvider.prototype.setGlobalThrottle = SL_QT;SL4B_TestRttpProvider.prototype.disableWTStatsTimeout = SL_RL;SL4B_TestRttpProvider.prototype.clearObjectListeners = SL_KB;SL4B_TestRttpProvider.prototype.blockObjectListeners = SL_HF;SL4B_TestRttpProvider.prototype.unblockObjectListeners = SL_LP;SL4B_TestRttpProvider.prototype.createObject = SL_OF;SL4B_TestRttpProvider.prototype.contribObject = SL_AV;SL4B_TestRttpProvider.prototype.deleteObject = SL_AU;SL4B_TestRttpProvider.prototype.getFieldNames = SL_BQ;SL4B_TestRttpProvider.prototype.logout = SL_QE;SL4B_TestRttpProvider.prototype.debug = SL_JC;SL4B_TestRttpProvider.prototype.setDebugLevel = SL_OH;SL4B_TestRttpProvider.prototype.getVersion = SL_MJ;SL4B_TestRttpProvider.prototype.getVersionInfo = SL_PJ;function SL_EY(){setTimeout("SL4B_Accessor.getRttpProvider().connected()",100);}
function SL_GA(){this.connect();}
function SL_FQ(A,B){this.credentialsRetrieved();this.loggedIn();this.m_hSetInterval=setInterval("SL4B_Accessor.getRttpProvider().tick()",200);}
function SL_DJ(C,B,A){var l_oObject=this.m_pObjects[B];
if(typeof l_oObject=="undefined"){setTimeout(C.WTObjectNotFound(B),500);return;
}var l_oLiberatorSubscription=this.m_pSubscriptions[this.getListener(C)];
if(typeof l_oLiberatorSubscription=="undefined"){l_oLiberatorSubscription=new SL_JA();l_oLiberatorSubscription.m_oSubscriber=C;this.m_pSubscriptions[this.getListener(C)]=l_oLiberatorSubscription;}var l_oSubscriptionObject=l_oLiberatorSubscription.m_pObjects[B];
if(typeof l_oSubscriptionObject=="undefined"){l_oSubscriptionObject=new SL_CL();l_oSubscriptionObject.m_sName=B;l_oLiberatorSubscription.m_pObjects[B]=l_oSubscriptionObject;}l_oSubscriptionObject.add(A.split(","));}
function SL_PG(C,A,B){l_pObjects=A.split(" ");for(var l_nCount=0;l_nCount<l_pObjects.length;l_nCount++){var l_sObjectName=l_pObjects[l_nCount];
this.getObject(C,l_sObjectName,B);}}
function SL_FS(C,B,A){throw new SL4B_Error("removeObject method not implemented in TestRttpProvider");
}
function SL_EF(C,A,B){throw new SL4B_Error("removeObjects method not implemented in TestRttpProvider");
}
function SL_MR(B,A){throw new SL4B_Error("getObjectType method not implemented in TestRttpProvider");
}
function SL_LQ(A,B){var l_oObject=this.m_pObjects[A];
if(typeof l_oObject!="undefined"){}throw new SL4B_Error("setThrottleObject method not implemented in TestRttpProvider");
}
function SL_RS(A,B){throw new SL4B_Error("setThrottleObjects method not implemented in TestRttpProvider");
}
function SL_QT(A){throw new SL4B_Error("setGlobalThrottle method not implemented in TestRttpProvider");
}
function SL_RL(A){throw new SL4B_Error("disableWTStatsTimeout method not implemented in TestRttpProvider");
}
function SL_KB(B,A){if(typeof A=="undefined"){}else 
{}}
function SL_HF(B,A){if(typeof A=="undefined"){}else 
{}}
function SL_LP(B,A){if(typeof A=="undefined"){}else 
{}}
function SL_OF(C,A,B){throw new SL4B_Error("createObject method not implemented in TestRttpProvider");
}
function SL_AV(C,A,B){for(var l_nFieldIndex=0,l_nLength=B.size();l_nFieldIndex<l_nLength;++l_nFieldIndex){var l_oField=B.getField(l_nFieldIndex);
if(l_nFieldIndex==(l_nLength-1)){this.m_oRttpApplet.contribObject(A,l_oField.m_sName,l_oField.m_sValue,this.getListener(C));}else 
{this.m_oRttpApplet.contribObject(A,l_oField.m_sName,l_oField.m_sValue,null);}}}
function SL_AU(B,A){throw new SL4B_Error("deleteObject method not implemented in TestRttpProvider");
}
function SL_BQ(){var l_sNames="";
for(var l_nCount=0;l_nCount<l_pFieldNames.length;l_nCount++){if(l_nCount!=0){l_sNames+=",";}l_sNames+=l_pFieldNames[l_nCount];}return l_sNames;
}
function SL_QE(){clearInterval(this.m_hSetInterval);}
function SL_JC(B,A){throw new SL4B_Error("debug method not implemented in TestRttpProvider");
}
function SL_OH(A){throw new SL4B_Error("setDebugLevel method not implemented in TestRttpProvider");
}
function SL_MJ(){throw new SL4B_Error("getVersion method not implemented in TestRttpProvider");
}
function SL_PJ(){throw new SL4B_Error("getVersionInfo method not implemented in TestRttpProvider");
}
function SL_JZ(){this.m_sName="";this.m_sType="numeric";this.m_sDefaultValue="?";this.m_nMax;this.m_nMin;}
function SL_GB(){this.m_sName="";this.m_sDirectory="";this.m_nFreqency=1000;this.m_nLastUpdateTime=0;this.m_nUpdateId=0;this.m_nOffset;this.m_pFields=new Object();}
function SL_CC(){this.m_sName="";this.m_sValue="";this.m_nUpdateId=0;}
function SL_JA(){this.m_oSubscriber=null;this.m_pObjects=new Object();this.toString = function(){var l_sRtn="";
for(l_sObjectName in this.m_pObjects){l_sRtn+="\t"+this.m_pObjects[l_sObjectName]+"\n";}return l_sRtn;
};
}
function SL_CL(){this.m_sName=null;this.m_pFields=new Object();this.m_nUpdateId=0;this.toString = function(){var l_sRtn=this.m_sName+": ";
for(l_sField in this.m_pFields){l_sRtn+=l_sField+", ";}return l_sRtn;
};
this.add = function(A){for(var l_nCount=0;l_nCount<A.length;l_nCount++){var l_sField=A[l_nCount];
if(typeof this.m_pFields[l_sField]=="undefined"){this.m_pFields[l_sField]=l_sField;}}};
}
function SL_IE(){this.m_sName=null;this.m_sValue=null;}
function SL_HH(){this.m_pData=new Array();}
SL_HH.prototype = new SL4B_RecordFieldData;SL_HH.prototype.size = function(){return this.m_pData.length;
};
SL_HH.prototype.getFieldName = function(A){this.checkIndex(A);return this.m_pData[A].m_sName;
};
SL_HH.prototype.getFieldValue = function(A){this.checkIndex(A);return this.m_pData[A].m_sValue;
};
SL_HH.prototype.checkIndex = function(A){if(A<0||A>=this.m_pData.length){throw new SL4B_Exception("Index "+A+" is out of bounds");
}};
function SL_CV(C,B,A){var l_oField=new SL_JZ();
l_oField.m_sName=C;l_oField.m_sType="decimal";l_oField.m_nMin=B;l_oField.m_nMax=A;this.m_pFields[C]=l_oField;}
function SL_JB(C,B,A){var l_oField=new SL_JZ();
l_oField.m_sName=C;l_oField.m_sType="integer";l_oField.m_nMin=B;l_oField.m_nMax=A;this.m_pFields[C]=l_oField;}
function SL_BR(B,A){var l_oField=new SL_JZ();
l_oField.m_sName=B;l_oField.m_sType="string";l_oField.m_sDefaultValue=A;this.m_pFields[B]=l_oField;}
function SL_EQ(C,A,B){var l_nNow=(new Date()).valueOf();
l_oObject=new SL_GB();l_oObject.m_sName=C;l_oObject.m_nOffset=B;l_oObject.m_nLastUpdateTime=0;l_oObject.m_nUpdateId=1;l_oObject.m_nFreqency=A;l_oObject.m_sDirectory=C.substring(0,C.lastIndexOf("/")+1);for(l_sFieldName in this.m_pFields){var l_oField=this.m_pFields[l_sFieldName];
var l_oFieldValue=new SL_CC();
l_oFieldValue.m_sName=l_oField.m_sName;l_oFieldValue.m_sValue=this.getNextValue(l_oObject,l_oField);l_oFieldValue.m_nUpdateId=1;l_oObject.m_pFields[l_oFieldValue.m_sName]=l_oFieldValue;}this.m_pObjects[C]=l_oObject;}
function SL_LN(B,A){var l_oReturn="";
if(A.m_sType=="decimal"){l_oReturn=(Math.random()*(A.m_nMax-A.m_nMin))+A.m_nMin+B.m_nOffset;l_oReturn=Math.floor(l_oReturn*100)/100;}if(A.m_sType=="integer"){l_oReturn=(Math.random()*(A.m_nMax-A.m_nMin))+A.m_nMin+B.m_nOffset;l_oReturn=Math.floor(l_oReturn);}return l_oReturn;
}
function SL_CT(){this.m_nTickCount++;var l_nNow=(new Date()).valueOf();
for(l_sObjectName in this.m_pObjects){var l_oObject=this.m_pObjects[l_sObjectName];
if((l_oObject.m_nLastUpdateTime+l_oObject.m_nFreqency)<l_nNow){for(l_sFieldName in this.m_pFields){var l_oField=this.m_pFields[l_sFieldName];
var l_oFieldValue=l_oObject.m_pFields[l_oField.m_sName];
if(l_oField.m_sType=="decimal"||l_oField.m_sType=="integer"){if(Math.random()>0.5){l_oFieldValue.m_sValue=this.getNextValue(l_oObject,l_oField);l_oFieldValue.m_nUpdateId=this.m_nTickCount;l_oObject.m_nUpdateId=this.m_nTickCount;l_oObject.m_nLastUpdateTime=l_nNow;}}}}}for(l_sSubscriber in this.m_pSubscriptions){var l_oLiberatorSubscription=this.m_pSubscriptions[l_sSubscriber];
for(l_sObjectName in l_oLiberatorSubscription.m_pObjects){var l_oLiberatorSubsciptionObject=l_oLiberatorSubscription.m_pObjects[l_sObjectName];
var l_oObject=this.m_pObjects[l_sObjectName];
if(typeof l_oObject!="undefined"&&l_oObject.m_nUpdateId>l_oLiberatorSubsciptionObject.m_nUpdateId){var l_oData=new SL_HH();
for(l_sField in l_oLiberatorSubsciptionObject.m_pFields){var l_oFieldValue=l_oObject.m_pFields[l_sField];
if(typeof l_oFieldValue!="undefined"&&l_oFieldValue.m_nUpdateId>l_oLiberatorSubsciptionObject.m_nUpdateId){var l_oNotificationFieldValue=new SL_IE();
l_oNotificationFieldValue.m_sName=l_sField;l_oNotificationFieldValue.m_sValue=l_oFieldValue.m_sValue;l_oData.m_pData.push(l_oNotificationFieldValue);}}l_oLiberatorSubscription.m_oSubscriber.WTRecordMultiUpdated(l_sObjectName,l_oData);l_oLiberatorSubsciptionObject.m_nUpdateId=l_oObject.m_nUpdateId;}}}}
function SL_FG(){SL4B_ScriptLoader.createRttpProvider();SL4B_ScriptLoader.createSnapshotProvider();}
function SL_LE(){this.m_nSliceSize=20;this.m_pRtmlObjectContainers=new Object();this.initialise();}
SL_LE.prototype = new SL4B_AbstractSubscriber;SL_LE.prototype.ready = SL_KO;SL_LE.prototype.getRequiredSymbols = SL_ME;SL_LE.prototype.optimiseAndRequestObjects = SL_KE;SL_LE.prototype.recordUpdated = SL_BE;SL_LE.prototype.objectStatus = SL_OX;function SL_KO(){SL4B_Logger.log(SL4B_DebugLevel.const_DEBUG_INT,"RtmlSubscriber.ready: called.");var l_pElements=this.getRequiredSymbols();
SL4B_Logger.log(SL4B_DebugLevel.const_DEBUG_INT,"RtmlSubscriber.ready: found {0} quote tags.",l_pElements.length);this.optimiseAndRequestObjects(l_pElements);}
function SL_ME(){SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"RtmlSubscriber: Finding quote tags...");var l_oQuoteTag=null;
var l_nCount=1;
var l_pListOfQuoteElements=new Array();
var l_oBrowserAdapter=SL4B_Accessor.getBrowserAdapter();
if(l_oBrowserAdapter.isInternetExplorer()){for(var i=0;i<document.all.length;i++){if(typeof (document.all[i].id)!='undefined'){if(document.all[i].id.toLowerCase()=="quote"){var l_oQuoteTag=document.all[i];
SL4B_Logger.log(SL4B_DebugLevel.const_DEBUG_INT,"RtmlSubscriber: Found quote tag... ("+l_nCount+") : "+l_oQuoteTag+"("+l_oQuoteTag.innerHTML+")");l_pListOfQuoteElements.push(l_oQuoteTag);l_oQuoteTag.id="QUOTE"+l_nCount++;}}}}else 
{
do{l_oQuoteTag=l_oBrowserAdapter.getElementById("QUOTE");if(l_oQuoteTag==null){l_oQuoteTag=l_oBrowserAdapter.getElementById("quote");if(l_oQuoteTag==null){l_oQuoteTag=l_oBrowserAdapter.getElementById("Quote");}}if(l_oQuoteTag!=null){SL4B_Logger.log(SL4B_DebugLevel.const_DEBUG_INT,"RtmlSubscriber: Found quote tag... ("+l_nCount++, +") : "+l_oQuoteTag+"("+l_oQuoteTag.innerHTML+")");l_pListOfQuoteElements.push(l_oQuoteTag);l_oQuoteTag.setAttribute("id","QUOTE"+l_pListOfQuoteElements.length);}}while(l_oQuoteTag!=null);}return l_pListOfQuoteElements;
}
function SL_KE(A){for(var l_i=0;l_i<A.length;l_i++){var l_oQuoteElement=new SL_AF(A[l_i]);
if(this.m_pRtmlObjectContainers[l_oQuoteElement.m_sObjectName]==null){this.m_pRtmlObjectContainers[l_oQuoteElement.m_sObjectName]=new SL_CJ(l_oQuoteElement.m_sObjectName);}this.m_pRtmlObjectContainers[l_oQuoteElement.m_sObjectName].addRtmlQuoteElement(l_oQuoteElement);}var l_pTagList=new Array();
for(l_sObjectName in this.m_pRtmlObjectContainers){l_pTagList=l_pTagList.concat(this.m_pRtmlObjectContainers[l_sObjectName].getUniqueFields());}l_pTagList.sort(SL_PP);for(var l_i=l_pTagList.length-1;l_i>0;l_i--){if((l_pTagList[l_i].m_sFieldName==l_pTagList[l_i-1].m_sFieldName)){l_pTagList[l_i-1].m_sObjectName+=SL4B_AbstractRttpProvider.const_OBJECT_NAME_DELIMITER+l_pTagList[l_i].m_sObjectName;l_pTagList=l_pTagList.slice(0,l_i).concat(l_pTagList.slice(l_i+1));}}l_pTagList.sort(SL_RM);for(var l_i=l_pTagList.length-1;l_i>0;l_i--){if((l_pTagList[l_i].m_sObjectName==l_pTagList[l_i-1].m_sObjectName)){l_pTagList[l_i-1].m_sFieldName+=","+l_pTagList[l_i].m_sFieldName;l_pTagList=l_pTagList.slice(0,l_i).concat(l_pTagList.slice(l_i+1));}}var l_nRequests=0;
if(this.m_nSliceSize>0){for(var l_i=0;l_i<l_pTagList.length;l_i++){var l_sSymbolsArray=l_pTagList[l_i].m_sObjectName.split(SL4B_AbstractRttpProvider.const_OBJECT_NAME_DELIMITER);
var l_nStart=0;
var l_nEnd=this.m_nSliceSize;
while(l_nEnd<l_sSymbolsArray.length){var l_sObjectName=(l_sSymbolsArray.slice(l_nStart,l_nEnd)).join(SL4B_AbstractRttpProvider.const_OBJECT_NAME_DELIMITER);
SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINE_INT,"RtmlSubscriber.optimiseAndRequestObjects: Requesting object(s) {0} with fields {1}.",l_sObjectName,l_pTagList[l_i].m_sFieldName);SL4B_Accessor.getRttpProvider().getObjects(this,l_sObjectName,l_pTagList[l_i].m_sFieldName);l_nRequests++;l_nStart=l_nEnd;l_nEnd=l_nEnd+this.m_nSliceSize;}var l_sObjectName=(l_sSymbolsArray.slice(l_nStart,l_sSymbolsArray.length)).join(SL4B_AbstractRttpProvider.const_OBJECT_NAME_DELIMITER);
SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINE_INT,"RtmlSubscriber.optimiseAndRequestObjects: Requesting object(s) {0} with fields {1}.",l_sObjectName,l_pTagList[l_i].m_sFieldName);SL4B_Accessor.getRttpProvider().getObjects(this,l_sObjectName,l_pTagList[l_i].m_sFieldName);l_nRequests++;}}else 
{for(var l_i=0;l_i<l_pTagList.length;l_i++){SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_FINE_INT,"RtmlSubscriber.optimiseAndRequestObjects: Requesting object(s) {0} with fields {1}.",l_pTagList[l_i].m_sObjectName,l_pTagList[l_i].m_sFieldName);SL4B_Accessor.getRttpProvider().getObjects(this,l_pTagList[l_i].m_sObjectName,l_pTagList[l_i].m_sFieldName);l_nRequests++;}}SL4B_Logger.log(SL4B_DebugLevel.const_DEBUG_INT,"RTML Subscriber: Made {0} requests after optimisation.",l_nRequests);}
function SL_PP(A,B){if(A.m_sFieldName>B.m_sFieldName){return 1;
}if(A.m_sFieldName<B.m_sFieldName){return -1;
}if(A.m_sObjectName>B.m_sObjectName){return 1;
}if(A.m_sObjectName<B.m_sObjectName){return -1;
}return 0;
}
function SL_RM(A,B){if(A.m_sObjectName>B.m_sObjectName){return 1;
}if(A.m_sObjectName<B.m_sObjectName){return -1;
}return 0;
}
function SL_BE(B,C,A){this.m_pRtmlObjectContainers[B].updateRtmlQuoteElements(C,A);}
function SL_OX(D,B,A,C){if(B==SL4B_ObjectStatus.STALE){this.m_pRtmlObjectContainers[D].updateRtmlQuoteElementsStatus(true);}else 
if(B==SL4B_ObjectStatus.OK||B==SL4B_ObjectStatus.LIMITED){this.m_pRtmlObjectContainers[D].updateRtmlQuoteElementsStatus(false);}}
function SL_AF(A){this.m_sObjectName=A.getAttribute("symbol");this.m_sFieldName=A.getAttribute("field");if(this.m_sFieldName==null){this.m_sFieldName=A.getAttribute("fid");}this.m_oHtmlElement=A;this.m_dFlashTime=this.getQuoteAttribute("flashtime",SL_AF.const_INTEGER);this.m_sBgChange=this.getQuoteAttribute("bgchange",SL_AF.const_STRING);this.m_sBgDn=this.getQuoteAttribute("bgdn",SL_AF.const_BGCOLOR);this.m_sBgUp=this.getQuoteAttribute("bgup",SL_AF.const_BGCOLOR);this.m_sBgEq=this.getQuoteAttribute("bgeq",SL_AF.const_BGCOLOR);this.m_sFgChange=this.getQuoteAttribute("fgchange",SL_AF.const_STRING);this.m_sFgDn=this.getQuoteAttribute("fgdn",SL_AF.const_FGCOLOR);this.m_sFgUp=this.getQuoteAttribute("fgup",SL_AF.const_FGCOLOR);this.m_sFgEq=this.getQuoteAttribute("fgeq",SL_AF.const_FGCOLOR);this.m_sFgFlash=this.getQuoteAttribute("fgflash",SL_AF.const_STRING);this.m_sPlus=this.getQuoteAttribute("plus",SL_AF.const_INTEGER);this.m_dFractionHandling=this.getQuoteAttribute("fractionhandling",SL_AF.const_INTEGER);this.m_dToDp=this.getQuoteAttribute("todp",SL_AF.const_INTEGER);this.m_sRound=this.getQuoteAttribute("round",SL_AF.const_STRING);this.m_dAddCommas=this.getQuoteAttribute("addcommas",SL_AF.const_INTEGER);this.m_dToSf=this.getQuoteAttribute("tosf",SL_AF.const_POSITIVE_INTEGER);this.m_sGfxEq=this.getQuoteAttribute("gfxeq",SL_AF.const_STRING);this.m_sRootUrl=SL4B_ScriptLoader.getRelativeUrlPrefix();this.m_sGfxUp=this.getQuoteAttribute("gfxup",SL_AF.const_STRING);if(this.m_sGfxUp==null){this.m_sGfxUp=this.m_sRootUrl+'rtml/img/up.gif';}this.m_sGfxDn=this.getQuoteAttribute("gfxdn",SL_AF.const_STRING);if(this.m_sGfxDn==null){this.m_sGfxDn=this.m_sRootUrl+'rtml/img/down.gif';}this.m_fTransformFunction=null;
try {this.m_fTransformFunction=eval(this.getQuoteAttribute("transform",SL_AF.const_STRING));}catch(e){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"RtmlQuoteElement.constructor: Transform function '{0}' could not be found!",this.getQuoteAttribute("transform",SL_AF.const_STRING));}
this.m_oTransformParam=null;
try {this.m_oTransformParam=eval("RTML_transformParam="+this.getQuoteAttribute("transformparam",SL_AF.const_STRING));}catch(e){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"RtmlQuoteElement.constructor: Transform param {0} could not be parsed!",this.getQuoteAttribute("transformparam",SL_AF.const_STRING));}
this.m_bIsIndicator=(A.getAttribute("indicator")!=null);l_bContainsChange=false;if(this.m_sFieldName!=null){var l_bContainsChange=((this.m_sFieldName.toLowerCase().indexOf('chng')!=-1)||(this.m_sFieldName.toLowerCase().indexOf('change')!=-1));
}if(this.m_sFgChange==null&&l_bContainsChange==true){this.m_sFgChange="abs";}if(this.m_sPlus==null&&l_bContainsChange==true){this.m_sPlus=1;}if(this.m_sBgChange=="rel"&&this.m_sFgChange=="abs"){this.m_sChangedType="RtmlAbsFgRelBg"+this.m_oHtmlElement.getAttribute("id");this.m_fChangedFunction=RTML_AbsFgRelBgUpdate;}else 
if(this.m_sBgChange=="abs"&&this.m_sFgChange=="abs"){this.m_sChangedType="RtmlAbsolute"+this.m_oHtmlElement.getAttribute("id");this.m_fChangedFunction=RTML_AbsoluteUpdate;}else 
if(this.m_sBgChange=="abs"){this.m_sChangedType="RtmlRelFgAbsBg"+this.m_oHtmlElement.getAttribute("id");this.m_fChangedFunction=RTML_RelFgAbsBgUpdate;}else 
if(this.m_sBgChange=="rel"){this.m_sChangedType="RtmlRelative"+this.m_oHtmlElement.getAttribute("id");this.m_fChangedFunction=RTML_RelativeUpdate;}if(this.m_dFlashTime<=0||this.m_sBgUp.toLowerCase().indexOf("none")!=-1||this.m_sBgDn.toLowerCase().indexOf("none")!=-1||this.m_sBgEq.toLowerCase().indexOf("none")!=-1){this.m_sBgUp=this.m_sBgDn=this.m_sBgEq="";}if(this.m_sFgUp.toLowerCase().indexOf("none")!=-1||this.m_sFgDn.toLowerCase().indexOf("none")!=-1||this.m_sFgEq.toLowerCase().indexOf("none")!=-1){this.m_sFgUp=this.m_sFgDn=this.m_sFgEq=this.m_sFgFlash="";}RTSL_AddFlashType(this.m_sChangedType,this.m_fChangedFunction,this.m_sBgUp,this.m_sBgDn,this.m_sBgEq,'',this.m_sFgFlash,this.m_sFgFlash,this.m_sFgFlash,'black',this.m_sFgUp,this.m_sFgDn,this.m_sFgEq,'black');}
SL_AF.prototype.updateQuoteValue = SL_PD;SL_AF.prototype.updateQuoteStatus = SL_QZ;SL_AF.prototype.getQuoteAttribute = SL_KC;SL_AF.prototype.getParentAttribute = SL_MW;SL_AF.const_INTEGER=1;SL_AF.const_POSITIVE_INTEGER=2;SL_AF.const_STRING=3;SL_AF.const_FGCOLOR=4;SL_AF.const_BGCOLOR=5;function RTML_RelativeUpdate(A,C,D,G,B,E,F){var l_oUpdateFlash=SL_DB(A,C,D.value,G,B);
return (new GF_Update(C,D.displayValue,l_oUpdateFlash.m_sFlashClr,l_oUpdateFlash.m_sFlashText,l_oUpdateFlash.m_sFinalText,l_oUpdateFlash.m_sFinalClr,G,B,'innerText',F));
}
function RTML_AbsoluteUpdate(D,B,C,G,A,E,F){var l_sPrevVal=((typeof (g_pPreviousValue[B])!='undefined') ? g_pPreviousValue[B] : '');
g_pPreviousValue[B]=C.value;var l_oFinalUpdate=SL_NU(D,0,GF_ConvertToDecimal(C.value),'0.00');
return (new GF_Update(B,C.displayValue,l_oFinalUpdate.m_sFlashClr,l_oFinalUpdate.m_sFlashText,l_oFinalUpdate.m_sFinalText,l_oFinalUpdate.m_sFinalClr,G,A,'innerText',F));
}
function RTML_AbsFgRelBgUpdate(D,B,C,G,A,E,F){var l_sPrevVal=((typeof (g_pPreviousValue[B])!='undefined') ? g_pPreviousValue[B] : '');
g_pPreviousValue[B]=C.value;var l_nOld=GF_ConvertToDecimal(l_sPrevVal);
var l_nNew=GF_ConvertToDecimal(C.value);
var l_oRelUpdate=SL_NU(D,l_nOld,l_nNew,l_sPrevVal);
var l_oAbsUpdate=SL_NU(D,0,l_nNew,'0.00');
return (new GF_Update(B,C.displayValue,l_oRelUpdate.m_sFlashClr,l_oAbsUpdate.m_sFlashText,l_oAbsUpdate.m_sFinalText,l_oRelUpdate.m_sFinalClr,G,A,'innerText',F));
}
function RTML_RelFgAbsBgUpdate(D,B,C,G,A,E,F){var l_sPrevVal=((typeof (g_pPreviousValue[B])!='undefined') ? g_pPreviousValue[B] : '');
g_pPreviousValue[B]=C.value;var l_nOld=GF_ConvertToDecimal(l_sPrevVal);
var l_nNew=GF_ConvertToDecimal(C.value);
var l_oRelUpdate=SL_NU(D,l_nOld,l_nNew,l_sPrevVal);
var l_oAbsUpdate=SL_NU(D,0,l_nNew,'0.00');
return (new GF_Update(B,C.displayValue,l_oAbsUpdate.m_sFlashClr,l_oRelUpdate.m_sFlashText,l_oRelUpdate.m_sFinalText,l_oAbsUpdate.m_sFinalClr,G,A,'innerText',F));
}
function SL_PD(A){if(!this.m_bIsIndicator){var l_dNewComparisonValue=A;
switch(this.m_dFractionHandling){
case 1:A=RTSL_FractionToDecimal(A);l_dNewComparisonValue=A;break;
case 2:A=RTSL_FractionSimplify(A);l_dNewComparisonValue=RTSL_FractionToDecimal(A);break;
case 3:l_dNewComparisonValue=RTSL_FractionToDecimal(A);break;
}if(this.m_fTransformFunction!=null){var l_oTransformResult=this.m_fTransformFunction(A,this.m_oTransformParam,this.m_sObjectName,this.m_sFieldName);
if(l_oTransformResult instanceof RTML_TransformResult){l_dNewComparisonValue=l_oTransformResult.value;A=l_oTransformResult.displayValue;}else 
{l_dNewComparisonValue=l_oTransformResult;A=l_oTransformResult;}}if(this.m_dToSf>0){A=RTSL_ToSignificantFigures(A,this.m_dToSf);}if(this.m_dToDp>0){A=RTSL_ToDecimalPlaces(A,this.m_dToDp,false,this.m_sRound);}if(this.m_dAddCommas==1){A=RTSL_AddCommas(A);}if((this.m_sPlus==1)&&(parseFloat(A)>0)){A='+'+A;}var l_oUpdateValue=new RTML_TransformResult(l_dNewComparisonValue,A);
var l_oUpdate=RTSL_CreateUpdate(this.m_oHtmlElement.getAttribute("id"),this.m_sChangedType,l_oUpdateValue,false,false,null,this.m_dFlashTime);
}else 
{var l_sImgId=this.m_oHtmlElement.id+"Img";
if(this.m_oHtmlElement.innerHTML.indexOf("IMG")==-1&&this.m_oHtmlElement.innerHTML.indexOf("img")==-1){this.m_oHtmlElement.innerHTML="<i"+"mg id="+l_sImgId+" src='"+this.m_sRootUrl+"rtml/img/blank.gif'"+"></i"+"mg>";}var l_oImageArray=new MOD_CreateImages(0,8,8);
l_oImageArray.addImage(-1,this.m_sGfxDn);(this.m_sGfxEq==null) ? l_oImageArray.addImage(0,this.m_sRootUrl+'rtml/img/blank.gif') : l_oImageArray.addImage(0,this.m_sGfxEq);l_oImageArray.addImage(1,this.m_sGfxUp);var l_oUpdate=null;
if(this.m_sFgChange=='rel'||this.m_sFgChange==null){if(this.m_sGfxEq==null){l_oUpdate=RTSL_CreateUpdate(l_sImgId,'UpDownRelIndicator',A,false,false,l_oImageArray);}else 
{l_oUpdate=RTSL_CreateUpdate(l_sImgId,'Indicator',A,false,false,l_oImageArray);}}else 
{l_oUpdate=RTSL_CreateUpdate(l_sImgId,'UpDownAbsIndicator',A,false,false,l_oImageArray);}}RTSL_DisplayUpdate(l_oUpdate);}
function SL_QZ(A){if(!this.m_bIsIndicator){if(A){RTSL_FlushUpdate(this.m_oHtmlElement.getAttribute("id"));this.m_oHtmlElement.style.textDecoration="line-through";}else 
{this.m_oHtmlElement.style.textDecoration="none";}}}
function SL_KC(B,A){var isError=false;
var l_vAttributeValue=this.m_oHtmlElement.getAttribute(B);
if(l_vAttributeValue==null){switch(A){
case SL_AF.const_FGCOLOR:l_vAttributeValue=this.getQuoteAttribute("fg",SL_AF.const_STRING);break;
case SL_AF.const_BGCOLOR:l_vAttributeValue=this.getQuoteAttribute("bg",SL_AF.const_STRING);break;
}}if(l_vAttributeValue==null){l_vAttributeValue=this.getParentAttribute(B);}if(l_vAttributeValue!=null){switch(A){
case SL_AF.const_INTEGER:isError=!SL4B_Accessor.getConfiguration().checkInteger(l_vAttributeValue);l_vAttributeValue=parseInt(l_vAttributeValue);break;
case SL_AF.const_POSITIVE_INTEGER:isError=!SL4B_Accessor.getConfiguration().checkPositiveInteger(l_vAttributeValue);l_vAttributeValue=parseInt(l_vAttributeValue);break;
case SL_AF.const_STRING:default :break;
}}if(isError){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"LF_RtmlQuoteElement_GetQuoteAttribute: Attribute Value: {0} for Attribute {1} is Incorrect, Using Defaults",l_vAttributeValue,B);}if(l_vAttributeValue==null||isError){switch(A){
case SL_AF.const_INTEGER:l_vAttributeValue=SL4B_Accessor.getConfiguration().getIntegerScriptTagAttribute(B);break;
case SL_AF.const_POSITIVE_INTEGER:l_vAttributeValue=SL4B_Accessor.getConfiguration().getPositiveIntegerScriptTagAttribute(B);break;
case SL_AF.const_STRING:default :l_vAttributeValue=SL4B_Accessor.getConfiguration().getScriptTagAttribute(B);break;
}}return l_vAttributeValue;
}
function SL_MW(A){var l_vAttributeValue=null;
var l_oCurrentElement=this.m_oHtmlElement.parentNode;
while(l_oCurrentElement!=window.document&&l_oCurrentElement!=null&&typeof (l_oCurrentElement.getAttribute)!='undefined'){l_vAttributeValue=l_oCurrentElement.getAttribute(A);if(l_vAttributeValue!=null){break;
}l_oCurrentElement=l_oCurrentElement.parentNode;}return l_vAttributeValue;
}
function SL_CJ(A){this.m_sObjectName=A;this.m_pRtmlQuoteElements=new Object();}
SL_CJ.prototype.addRtmlQuoteElement = SL_JU;SL_CJ.prototype.getUniqueFields = SL_LF;SL_CJ.prototype.updateRtmlQuoteElements = SL_FY;SL_CJ.prototype.updateRtmlQuoteElementsStatus = SL_GP;function SL_JU(A){if(this.m_pRtmlQuoteElements[A.m_sFieldName]==null){this.m_pRtmlQuoteElements[A.m_sFieldName]=new Array();}this.m_pRtmlQuoteElements[A.m_sFieldName].push(A);}
function SL_LF(){var l_pUniqueFields=new Array();
for(l_sFieldName in this.m_pRtmlQuoteElements){l_pUniqueFields.push(this.m_pRtmlQuoteElements[l_sFieldName][0]);}return l_pUniqueFields;
}
function SL_FY(B,A){var l_pElements=this.m_pRtmlQuoteElements[B];
for(l_oQuoteElement in l_pElements){l_pElements[l_oQuoteElement].updateQuoteValue(A);}}
function SL_GP(A){for(l_sFieldName in this.m_pRtmlQuoteElements){var l_pElements=this.m_pRtmlQuoteElements[l_sFieldName];
for(l_oQuoteElement in l_pElements){l_pElements[l_oQuoteElement].updateQuoteStatus(A);}}}
RTML_RtmlSubscriberAccessor = new function(){this.m_oRtmlSubscriber=new SL_LE();this.getRtmlSubscriber = function(A){return ((typeof A=="undefined") ? this.m_oRtmlSubscriber : A);
};
};
RTML_TransformResult = function(A,B){this.value=A;this.displayValue=B;};
function RTSL_ToDecimalPlaces(A,B,C,D){if(isNaN(A)){return A;
}var l_sRoundType=RTSL_DefaultValue(D,"default");
var l_nPower=Math.pow(10,B);
var l_nVal;
if(l_sRoundType=="up"){l_nVal=(Math.ceil(A*l_nPower))/l_nPower;}else 
if(l_sRoundType=="down"){l_nVal=(Math.floor(A*l_nPower))/l_nPower;}else 
{l_nVal=(Math.round(A*l_nPower))/l_nPower;}var l_sVal=l_nVal.toString(10);
if(l_nVal==0){l_sVal+=".";for(var l_nCount=0;l_nCount<B;l_nCount++){l_sVal+="0";}}if(C){var l_nNumZeros=l_sVal.search(/\./);
var l_bNeedsDot=false;
if(l_nNumZeros==-1){l_nNumZeros=B;l_bNeedsDot=true;}else 
{l_nNumZeros=B-(l_sVal.length-l_nNumZeros-1);l_nNumZeros=((l_nNumZeros<0) ? (0) : (l_nNumZeros));}if(l_nNumZeros>0){if(l_bNeedsDot){l_sVal+=".";}}for(var l_nCount=0;l_nCount<l_nNumZeros;l_nCount++){l_sVal+="0";}}return l_sVal;
}
function RTSL_AddCommas(A){if(isNaN(A)){return A;
}var l_sAbsVal=new String(Math.abs(A));
l_sAbsVal=l_sAbsVal.split('.');var l_sComma='';
var l_nPosition;

do{l_sComma=l_sAbsVal[0].substr(((l_sAbsVal[0].length-3>=0) ? l_sAbsVal[0].length-3 : 0))+((l_sComma!='') ? ',' : '')+l_sComma;l_sAbsVal[0]=l_sAbsVal[0].substr(0,l_sAbsVal[0].length-3);}while(l_sAbsVal[0].length>0);var l_sVal=new String(A);
l_sVal=l_sVal.split('.');if(typeof (l_sVal[1])!='undefined'&&l_sVal[1].length>0){l_sComma+='.'+l_sVal[1];}var l_nDir=((A==0) ? 1 : (A/Math.abs(A)));
l_sComma=((l_nDir==1) ? l_sComma : ('-'+l_sComma));return l_sComma;
}
function RTSL_FractionToDecimal(A){if(!SL_MH(A)){return A;
}A=A.toString();var l_nVal=new Number(A);
var l_nInt=SL_CZ(A);
if(isNaN(l_nInt)){return A;
}var l_nNum=SL_LJ(A);
if(isNaN(l_nNum)){return A;
}var l_nDen=SL_BI(A);
if(isNaN(l_nDen)){return A;
}var l_nDec=(l_nDen==0) ? (0) : (SL_EZ(A)*l_nNum/l_nDen);
l_nVal=l_nInt+l_nDec;return l_nVal;
}
function RTSL_FractionSimplify(A){if(!SL_MH(A)){return A;
}var l_nInt=SL_CZ(A);
if(isNaN(l_nInt)){return A;
}var l_nNum=SL_LJ(A);
if(isNaN(l_nNum)){return A;
}else 
if(l_nNum==0){return l_nInt.toString();
}var l_nDen=SL_BI(A);
if(isNaN(l_nDen)){return A;
}else 
if(l_nDen==0){return A;
}var l_nHcm=SL_RU(l_nNum,l_nDen);
l_nNum/=l_nHcm;l_nDen/=l_nHcm;var l_sStr=((l_nInt==0) ? '' : (l_nInt.toString()+' '));
return (l_sStr+l_nNum.toString()+'/'+l_nDen.toString());
}
function RTSL_FractionToHTML(A){var l_sMatch;
if(l_sMatch=A.match(/\-?[0-9]+\s[0-9]+\/[0-9]+/)){var l_sInt=(l_sMatch.toString().match(/^\-?[0-9]+/)).toString();
var l_sFraction=(l_sMatch.toString().match(/[0-9]+\/[0-9]+$/)).toString();
A=l_sInt+SL_LA(l_sFraction);}else 
if(l_sMatch=A.match(/[0-9]+\/[0-9]+$/)){var l_sFraction=(l_sMatch.toString().match(/[0-9]+\/[0-9]+$/)).toString();
A=SL_LA(l_sFraction);if(A.charAt(0)==0){A=A.substring(1);}}return A;
}
function SL_LA(A){var l_sResult;
var l_nFrac=eval(A);
switch(l_nFrac){
case 0.25:l_sResult='&frac14;';break;
case 0.5:l_sResult='&frac12;';break;
case 0.75:l_sResult='&frac34;';break;
default :l_sResult=' '+A;}return l_sResult;
}
function RTSL_ToSignificantFigures(B,A){if(B==null||isNaN(B)||A<=0||B=="0"){return B;
}B=B.toString();var l_bSign=(B.indexOf("-")==0);
if(l_bSign){B=B.substr(1);}var l_oRegExp=/^[^1-9]*/;
var l_sBegin=B.match(l_oRegExp).toString();
var l_sEnd=B.replace(l_oRegExp,"");
l_sEnd=SL_IR(l_sEnd,A);if(l_sBegin.length){if(l_sEnd.length<A){for(var l_nCount=l_sEnd.length;l_nCount<A;l_nCount++){l_sEnd+="0";}}else 
if(l_sEnd.length>A){l_sEnd=l_sEnd.substring(0,A);}}return ((l_bSign) ? ("-") : (""))+l_sBegin+l_sEnd;
}
function SL_IR(B,A){var l_bPadZeros=true;
var l_nLengthToUse=Math.round(B).toString().length;
var l_nPower=Math.pow(10,-(l_nLengthToUse-A));
var l_nVal;
l_nVal=(Math.round(B*l_nPower))/l_nPower;var l_sVal=l_nVal.toString(10);
return l_sVal;
}
function SL_MH(A){return (A.toString().indexOf("/",1)==-1) ? false : true;
}
function SL_CZ(A){var l_nSpacePos=A.indexOf(" ",1);
var l_vResult;
if(l_nSpacePos==-1){l_vResult=0;}else 
if('u'+parseInt(A.substring(0,l_nSpacePos))=='uNaN'){l_vResult=A;}else 
{l_vResult=parseInt(A.substring(0,l_nSpacePos));}return l_vResult;
}
function SL_LJ(A){var l_nSpacePos=A.indexOf(" ",1);
var l_nDivPos=A.indexOf('/');
return parseInt(A.substring(l_nSpacePos,l_nDivPos),10);
}
function SL_BI(A){var l_nDivPos=A.indexOf('/');
return new Number(A.substr(l_nDivPos+1));
}
function SL_EZ(A){return (A.charAt(0)=='-') ? -1 : 1;
}
function SL_RU(A,B){var l_nResult;
l_nResult=A%B;if(l_nResult==0){return B;
}return SL_RU(B,l_nResult);
}
LF_ElementCache = new function(){this.m_pElementLookup=new Object();this.m_pHtmlElements=new Object();this.getElement = function(A){var l_oElement=this.m_pElementLookup[A];
if(typeof l_oElement=="undefined"||l_oElement==null){if(typeof RTSL_GetElementFromDOM=="function"){l_oElement=RTSL_GetElementFromDOM(A);}else 
{l_oElement=SL4B_Accessor.getBrowserAdapter().getElementById(A);}this.addElement(A,l_oElement);}return l_oElement;
};
this.addElement = function(B,A){var l_bAdded=(typeof A!="undefined"&&A!=null&&typeof B!="undefined"&&B!=null);
if(l_bAdded){this.m_pElementLookup[B]=A;}return l_bAdded;
};
this.removeElement = function(A){var l_bRemoved=(typeof this.m_pElementLookup[A]!="undefined");
if(l_bRemoved){delete (this.m_pElementLookup[A]);}return l_bRemoved;
};
this.reset = function(){this.m_pElementLookup=new Array();};
};
function RTSL_GetElementFromCache(A){return LF_ElementCache.getElement(A);
}
function RTSL_AddElementToCache(B,A){return LF_ElementCache.addElement(B,A);
}
function RTSL_RemoveElementFromCache(A){return LF_ElementCache.removeElement(A);
}
function RTSL_ResetElementCache(){LF_ElementCache.reset();}
function RTSL_CreateElement(A,B,C){var l_oElement;
if(typeof document.createElement==null){l_oElement=null;}else 
{var l_oTemplate=LF_ElementCache.m_pHtmlElements[A.toLowerCase()];
if(l_oTemplate){l_oElement=l_oTemplate.cloneNode(true);}else 
{LF_ElementCache.m_pHtmlElements[A.toLowerCase()]=document.createElement(A);l_oElement=RTSL_CreateElement(A,B,C);}if(typeof B!="undefined"&&B!=null){l_oElement.id=B;RTSL_AddElementToCache(B,l_oElement);}if(C&&typeof C!="undefined"){C.appendChild(l_oElement);}}return l_oElement;
}
function SL_HS(){}
SL_HS.prototype = new SL4B_ConnectionListener;SL_HS.prototype.loginOk = SL_SB;SL_HS.prototype.loginError = SL_QN;SL_HS.prototype.connectionOk = SL_AY;SL_HS.prototype.connectionError = SL_IX;function SL_SB(){if(typeof RTSL_LoginOk=="function"){RTSL_LoginOk(null);}}
function SL_QN(A){if(typeof RTSL_LoginFailed=="function"){RTSL_LoginFailed(null,A);}else 
{SL4B_Logger.synchronizedAlert('Sorry. You do not have the authorisation to view live data from this source.\nPlease contact the web site host if you have received this message in error.');}}
function SL_AY(C,A,B){if(typeof RTSL_StatusConnected=="function"){RTSL_StatusConnected(null);}}
function SL_IX(){if(typeof RTSL_ConnectionLost=="function"){RTSL_ConnectionLost(null);}else 
{SL4B_Logger.synchronizedAlert("Connection lost - reconnection failed");}}
function SL_IY(){SL4B_Accessor.getRttpProvider().addConnectionListener(new SL_HS());}
function SL_EW(){this.initialise();}
SL_EW.prototype = new SL4B_AbstractSubscriber;SL_EW.prototype.ready = SL_CK;SL_EW.prototype.directoryUpdated = SL_KM;SL_EW.prototype.newsUpdated = SL_KV;SL_EW.prototype.objectUpdated = SL_PI;SL_EW.prototype.pageUpdated = SL_BG;SL_EW.prototype.super_recordMultiUpdated = SL_EW.prototype.recordMultiUpdated;SL_EW.prototype.recordMultiUpdated = SL_OK;SL_EW.prototype.recordUpdated = SL_PY;SL_EW.prototype.storyReset = SL_BH;SL_EW.prototype.storyUpdated = SL_OI;SL_EW.prototype.chat = SL_CD;SL_EW.prototype.contribFailed = SL_QD;SL_EW.prototype.contribOk = SL_KA;SL_EW.prototype.fieldDeleted = SL_HC;SL_EW.prototype.type2Clear = SL_NJ;SL_EW.prototype.type3Clear = SL_PR;SL_EW.prototype.objectDeleted = SL_ET;SL_EW.prototype.objectInfo = SL_EL;SL_EW.prototype.objectNotFound = SL_FJ;SL_EW.prototype.objectStatus = SL_KT;SL_EW.prototype.objectType = SL_JR;SL_EW.prototype.objectUnavailable = SL_IZ;SL_EW.prototype.objectNotStale = SL_NS;SL_EW.prototype.objectStale = SL_ID;SL_EW.prototype.objectReadDenied = SL_NG;SL_EW.prototype.objectWriteDenied = SL_IC;SL_EW.prototype.clientMethodUndefined = SL_IP;function SL_CK(){if(typeof RTSL_Ready=="function"){RTSL_Ready();}else 
{if(SL4B_Accessor.getConfiguration().includeRtsl()){this.clientMethodUndefined("RTSL_Ready","This method must be present in your script in order to use RTSL!");SL4B_Accessor.getRttpProvider().stop();}}}
function SL_KM(D,C,A,B){if(typeof RTSL_DirUpdated=="function"){RTSL_DirUpdated(D,C,A,B);}else 
{this.clientMethodUndefined("RTSL_DirUpdated");}}
function SL_KV(C,D,B,A){if(typeof RTSL_NewsUpdated=="function"){RTSL_NewsUpdated(C,D,B,A);}else 
{this.clientMethodUndefined("RTSL_NewsUpdated");}}
function SL_PI(A){if(typeof RTSL_ObjectUpdated=="function"){RTSL_ObjectUpdated(A);}else 
{this.clientMethodUndefined("RTSL_ObjectUpdated");}}
function SL_BG(D,B,A,E,C){if(typeof RTSL_PageUpdated=="function"){RTSL_PageUpdated(D,B,A,E,C);}else 
{this.clientMethodUndefined("RTSL_PageUpdated");}}
function SL_OK(A,B){if(typeof RTSL_RecordMultiUpdated=="function"){RTSL_RecordMultiUpdated(A,B);}else 
{this.super_recordMultiUpdated(A,B);}}
function SL_PY(B,C,A){if(typeof RTSL_RecordUpdated=="function"){RTSL_RecordUpdated(B,C,A);}else 
{this.clientMethodUndefined("RTSL_RecordUpdated");}}
function SL_BH(A){if(typeof RTSL_StoryReset=="function"){RTSL_StoryReset(A);}else 
{this.clientMethodUndefined("RTSL_StoryReset");}}
function SL_OI(B,A){if(typeof RTSL_StoryUpdated=="function"){RTSL_StoryUpdated(B,A);}else 
{this.clientMethodUndefined("RTSL_StoryUpdated");}}
function SL_CD(){}
function SL_QD(B,A,C){if(typeof RTSL_ContribFailed=="function"){RTSL_ContribFailed(B,A,C);}else 
{this.clientMethodUndefined("RTSL_ContribFailed");}}
function SL_KA(B,A){if(typeof RTSL_ContribOk=="function"){RTSL_ContribOk(B,A);}else 
{this.clientMethodUndefined("RTSL_ContribOk");}}
function SL_HC(C,A,B){if(typeof RTSL_FieldDeleted=="function"){RTSL_FieldDeleted(C,A,B);}else 
{this.clientMethodUndefined("RTSL_FieldDeleted");}}
function SL_NJ(A){if(typeof RTSL_Type2Clear=="function"){RTSL_Type2Clear(A);}else 
{this.clientMethodUndefined("RTSL_Type2Clear");}}
function SL_PR(A){if(typeof RTSL_Type3Clear=="function"){RTSL_Type3Clear(A);}else 
{this.clientMethodUndefined("RTSL_Type3Clear");}}
function SL_ET(A){if(typeof RTSL_ObjectDeleted=="function"){RTSL_ObjectDeleted(A);}else 
{this.clientMethodUndefined("RTSL_ObjectDeleted");}}
function SL_EL(B,A,C,D){if(typeof RTSL_ObjectInfo=="function"){RTSL_ObjectInfo(B,A,C,D);}else 
{this.clientMethodUndefined("RTSL_ObjectInfo");}}
function SL_FJ(A){if(typeof RTSL_ObjectNotFound=="function"){RTSL_ObjectNotFound(A);}else 
{this.clientMethodUndefined("RTSL_ObjectNotFound");}}
function SL_KT(C,B,A,D){if(typeof RTSL_ObjectStatus=="function"){RTSL_ObjectStatus(C,B,A,D);}else 
{this.clientMethodUndefined("RTSL_ObjectStatus");}}
function SL_JR(C,B,A){if(typeof RTSL_ObjectType=="function"){RTSL_ObjectType(C,B,A);}else 
{this.clientMethodUndefined("RTSL_ObjectType");}}
function SL_IZ(A){if(typeof RTSL_ObjectUnavailable=="function"){RTSL_ObjectUnavailable(A);}else 
{this.clientMethodUndefined("RTSL_ObjectUnavailable");}}
function SL_NS(A){}
function SL_ID(){}
function SL_NG(A){if(typeof RTSL_ObjectReadDenied=="function"){RTSL_ObjectReadDenied(A);}else 
{this.clientMethodUndefined("RTSL_ObjectReadDenied");}}
function SL_IC(A){if(typeof RTSL_ObjectWriteDenied=="function"){RTSL_ObjectWriteDenied(A);}else 
{this.clientMethodUndefined("RTSL_ObjectWriteDenied");}}
function SL_IP(B,A){SL4B_Logger.alert(B+" is undefined."+((typeof A!="undefined") ? " "+A : ""));}
RTSL_RtslSubscriberAccessor = new function(){this.m_oRtslSubscriber=new SL_EW();this.getRtslSubscriber = function(A){return ((typeof A=="undefined") ? this.m_oRtslSubscriber : A);
};
};
function RTSL_BlockObjectListeners(A,B){SL4B_Accessor.getRttpProvider().blockObjectListeners(RTSL_RtslSubscriberAccessor.getRtslSubscriber(B),A);}
function RTSL_ClearObjectListeners(A,B){SL4B_Accessor.getRttpProvider().clearObjectListeners(RTSL_RtslSubscriberAccessor.getRtslSubscriber(B),A);}
function RTSL_GetObject(B,A,C){SL4B_Accessor.getRttpProvider().getObject(RTSL_RtslSubscriberAccessor.getRtslSubscriber(C),B,A);}
function RTSL_GetObjects(A,B,C){SL4B_Accessor.getRttpProvider().getObjects(RTSL_RtslSubscriberAccessor.getRtslSubscriber(C),A,B);}
function RTSL_Reconnect(){SL4B_Accessor.getRttpProvider().reconnect();}
function RTSL_RemoveObject(D,C,F,A,B,E){SL4B_Accessor.getRttpProvider().removeObject(RTSL_RtslSubscriberAccessor.getRtslSubscriber(F),D,C);}
function RTSL_RemoveObjects(C,D,F,A,B,E){SL4B_Accessor.getRttpProvider().removeObjects(RTSL_RtslSubscriberAccessor.getRtslSubscriber(F),C,D);}
function RTSL_SetGlobalThrottle(A){SL4B_Accessor.getRttpProvider().setGlobalThrottle(A);}
function RTSL_SetThrottleObject(A,B){SL4B_Accessor.getRttpProvider().setThrottleObject(A,B);}
function RTSL_SetThrottleObjects(A,B){SL4B_Accessor.getRttpProvider().setThrottleObjects(A,B);}
function RTSL_Stop(){SL4B_Accessor.getRttpProvider().stop();}
function RTSL_UnblockObjectListeners(A,B){SL4B_Accessor.getRttpProvider().unblockObjectListeners(RTSL_RtslSubscriberAccessor.getRtslSubscriber(B),A);}
function RTSL_CreateObject(B,A){SL4B_Accessor.getRttpProvider().createObject(B,A);}
function RTSL_DeleteObject(A){SL4B_Accessor.getRttpProvider().deleteObject(A);}
function RTSL_ContribObject(C,D,A,B){var l_oContributionFieldData=SL_PK.getContributionFieldData(C);
l_oContributionFieldData.addField(D,A);if(B){var l_oContribSubscriber=new SL_IT(C,l_oContributionFieldData);
SL_PK.removeObjectFromQueue(C);return l_oContribSubscriber.m_nIdentifier;
}return null;
}
function RTSL_GetObjectType(A,B){SL4B_Accessor.getRttpProvider().getObjectType(RTSL_RtslSubscriberAccessor.getRtslSubscriber(B),A);}
function RTSL_GetFieldNames(){return SL4B_Accessor.getRttpProvider().getFieldNames();
}
function RTSL_GetObjectNameDelimiter(){var l_oConfiguration=SL4B_Accessor.getConfiguration();
return l_oConfiguration.getObjectNameDelimiter();
}
function RTSL_GetVersion(){return SL4B_Accessor.getRttpProvider().getVersion();
}
function RTSL_GetVersionInfo(){return SL4B_Accessor.getRttpProvider().getVersionInfo();
}
function RTSL_Version(){return SL4B_Version.getVersion();
}
function RTSL_VersionInfo(){return SL4B_Version.getVersionInfo();
}
SL_PK = new function(){this.m_pContributionDataQueue=new Object();this.getContributionFieldData = function(A){if(typeof this.m_pContributionDataQueue[A]=="undefined"){this.m_pContributionDataQueue[A]=new SL4B_ContributionFieldData();}return this.m_pContributionDataQueue[A];
};
this.removeObjectFromQueue = function(A){if(typeof this.m_pContributionDataQueue[A]!="undefined"){delete (this.m_pContributionDataQueue[A]);}};
};
SL_HQ = new function(){this.m_nCurrentUniqueIdentifier=1;this.getUniqueIdentifier = function(){return this.m_nCurrentUniqueIdentifier++;
};
};
function SL_IT(B,A){this.m_nIdentifier=SL_HQ.getUniqueIdentifier();this.m_sObjectName=B;this.m_oContributionFieldData=A;this.initialise();}
SL_IT.prototype = new SL4B_AbstractSubscriber;SL_IT.prototype.ready = SL_RC;SL_IT.prototype.getIdentifier = function(){return this.m_nIdentifier;
};
SL_IT.prototype.contribOk = SL_BX;SL_IT.prototype.contribFailed = SL_MC;function SL_RC(){SL4B_Accessor.getRttpProvider().contribObject(this,this.m_sObjectName,this.m_oContributionFieldData);}
function SL_BX(A){if(typeof RTSL_ContribOk!="undefined"){RTSL_ContribOk(this.m_nIdentifier,A);}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ContributionSubscriber.contribOk: The required method RTSL_ContribOk was not defined in the users code. This is required to receive successful contribution messages.");}}
function SL_MC(A,B){if(typeof RTSL_ContribFailed!="undefined"){RTSL_ContribFailed(this.m_nIdentifier,A,B);}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"ContributionSubscriber.contribFailed: The required method RTSL_ContribFailed was not defined in the users code. This is required to receive failed contribution messages.");}}
SL_IT.prototype.getContributionIdentifier = function(){return this.m_nIdentifier;
};
function RTSL_Assert(A){var l_oConfiguration=SL4B_Accessor.getConfiguration();

try {if(l_oConfiguration.getDebugLevel()>SL4B_DebugLevel.getNumericDebugLevel(SL4B_DebugLevel.CRITICAL)&&!A){RTSL_CallStack();eval("\nd"+"ebugger;");}}catch(e){SL4B_Logger.log(SL4B_DebugLevel.const_WARN_INT,"Unable to launch debugger as an invalid debug level was specified");SL4B_Accessor.getExceptionHandler().processException(e);}
}
function RTSL_CallStack(){var l_fFunc=RTSL_CallStack;
var l_nCount=1;
var l_sMsg='';
while(l_fFunc.caller!=null){var l_sFuncName=l_fFunc.toString();
var l_nFuncNameStart=9;
var l_nFuncNameEnd=l_sFuncName.search(/\(/)-l_nFuncNameStart;
l_sFuncName=l_sFuncName.substr(l_nFuncNameStart,l_nFuncNameEnd);l_sMsg+=((l_sMsg=='') ? '' : '\n')+(l_nCount++)+': '+l_sFuncName;l_sMsg+=' ( ';var l_pArgs=l_fFunc.arguments;
for(var l_nArg=0;l_nArg<l_pArgs.length;++l_nArg){l_sMsg+=((l_nArg==0) ? '' : ' , ')+l_pArgs[l_nArg];}l_sMsg+=' )';l_fFunc=l_fFunc.caller;}SL4B_Accessor.getLogger().printMessage('JavaScript call stack follows\n----------------------------------------\n'+l_sMsg+'\n----------------------------------------');}
function SL_RT(B,A){var l_oRttpProvider=SL4B_Accessor.getRttpProvider();
if(typeof A=="undefined"){l_oRttpProvider.debug(B);}else 
{l_oRttpProvider.debug(A,B);}}
function RTSL_Alert(A){SL4B_Logger.synchronizedAlert(A);}
var g_oUpdateFlash=null;
var g_pUpdateClearQueue=new SL_CH();
var g_pPreviousValue=new Object();
var g_fDecimalConvertor=null;
RTSL_SetUpdateFlash();RTSL_AddFlashType('Relative',RTSL_RelativeUpdate,'blue','red','yellow','yellow','white','white','black','black','blue','red','','');RTSL_AddFlashType('Absolute',RTSL_AbsoluteUpdate,'blue','red','yellow','yellow','white','white','black','black','blue','red','green','green');RTSL_AddFlashType('Image',RTSL_ImageUpdate);RTSL_AddFlashType('Indicator',RTSL_IndicatorUpdate);RTSL_AddFlashType('NoFlash',RTSL_NoFlashUpdate);RTSL_AddFlashType('Text',RTSL_TextUpdate,'','','','yellow','','','','black','','','','');RTSL_AddFlashType('HTML',RTSL_HTMLUpdate,'','','','yellow','','','','black','','','','');RTSL_AddFlashType('UpDownRelIndicator',RTSL_UpDownRelIndicatorUpdate);RTSL_AddFlashType('UpDownAbsIndicator',RTSL_UpDownAbsIndicatorUpdate);function RTSL_SetUpdateFlash(B,A){if(g_oUpdateFlash==null){g_oUpdateFlash=new Object();g_oUpdateFlash.m_sFlashTypes=new Object();g_oUpdateFlash.m_pCurrHighlights=new Object();}g_oUpdateFlash.m_nFlashTime=RTSL_DefaultValue(B,2000);g_oUpdateFlash.m_nUpdateFreq=RTSL_DefaultValue(A,500);}
function RTSL_AddFlashType(G,E,J,R,K,A,O,D,H,B,L,P,Q,I,N,C,F,M){g_oUpdateFlash.m_sFlashTypes[G]=new SL_OG(E,J,R,K,A,O,D,H,B,L,P,Q,I,N,C,F,M);}
function RTSL_SetDecimalConvertor(A){g_fDecimalConvertor=A;}
function RTSL_UpdateFlashTypes(A){var l_sFlashType;
for(l_sFlashType in g_oUpdateFlash.m_sFlashTypes){if(typeof g_oUpdateFlash.m_sFlashTypes[l_sFlashType]!='function'){g_oUpdateFlash.m_sFlashTypes[l_sFlashType].m_oUp.m_RestoreDefaults();g_oUpdateFlash.m_sFlashTypes[l_sFlashType].m_oDown.m_RestoreDefaults();g_oUpdateFlash.m_sFlashTypes[l_sFlashType].m_oEqual.m_RestoreDefaults();g_oUpdateFlash.m_sFlashTypes[l_sFlashType].m_oFresh.m_RestoreDefaults();}}for(l_sUpdatedFlashType in A){if(typeof g_oUpdateFlash.m_sFlashTypes[l_sUpdatedFlashType]!='undefined'&&typeof A[l_sUpdatedFlashType]!='function'){g_oUpdateFlash.m_sFlashTypes[l_sUpdatedFlashType].m_oUp.m_SetColours(A[l_sUpdatedFlashType].m_oUp);g_oUpdateFlash.m_sFlashTypes[l_sUpdatedFlashType].m_oDown.m_SetColours(A[l_sUpdatedFlashType].m_oDown);g_oUpdateFlash.m_sFlashTypes[l_sUpdatedFlashType].m_oEqual.m_SetColours(A[l_sUpdatedFlashType].m_oEqual);g_oUpdateFlash.m_sFlashTypes[l_sUpdatedFlashType].m_oFresh.m_SetColours(A[l_sUpdatedFlashType].m_oFresh);}}}
function SL_OG(E,I,Q,J,A,N,D,G,B,K,O,P,H,M,C,F,L){this.m_fEvalUpdate=E;this.m_oUp=new SL_BY(I,N,K,M);this.m_oDown=new SL_BY(Q,D,O,C);this.m_oEqual=new SL_BY(J,G,P,F);this.m_oFresh=new SL_BY(A,B,H,L);}
function SL_BY(D,B,A,C){this.m_sFlashClr=RTSL_DefaultValue(D,'');this.m_sFlashText=RTSL_DefaultValue(B,'white');this.m_sFinalText=RTSL_DefaultValue(A,this.m_sFlashClr);this.m_sFinalClr=RTSL_DefaultValue(C,'');this.m_sDefaultFlashClr=this.m_sFlashClr;this.m_sDefaultFlashText=this.m_sFlashText;this.m_sDefaultFinalText=this.m_sFinalText;this.m_sDefaultFinalClr=this.m_sFinalClr;}
SL_BY.prototype.m_RestoreDefaults = SL_PX;SL_BY.prototype.m_SetColours = SL_MB;function SL_PX(){this.m_sFlashClr=this.m_sDefaultFlashClr;this.m_sFlashText=this.m_sDefaultFlashText;this.m_sFinalText=this.m_sDefaultFinalText;this.m_sFinalClr=this.m_sDefaultFinalClr;}
function SL_MB(A){this.m_sFlashClr=A.m_sFlashClr;this.m_sFlashText=A.m_sFlashText;this.m_sFinalText=A.m_sFinalText;this.m_sFinalClr=A.m_sFinalClr;}
function SL_NU(A,C,B,D){var l_oUpdateFlash=g_oUpdateFlash.m_sFlashTypes[A].m_oFresh;
if(D==''||D==' '){l_oUpdateFlash=g_oUpdateFlash.m_sFlashTypes[A].m_oFresh;}else 
if(B==C){l_oUpdateFlash=g_oUpdateFlash.m_sFlashTypes[A].m_oEqual;}else 
if(B<C){l_oUpdateFlash=g_oUpdateFlash.m_sFlashTypes[A].m_oDown;}else 
if(B>C){l_oUpdateFlash=g_oUpdateFlash.m_sFlashTypes[A].m_oUp;}return l_oUpdateFlash;
}
function SL_DB(B,D,A,E,C){var l_sPrevVal=((typeof (g_pPreviousValue[D])!='undefined') ? g_pPreviousValue[D] : '');
g_pPreviousValue[D]=A;l_nOld=GF_ConvertToDecimal(l_sPrevVal);l_nNew=GF_ConvertToDecimal(A);return SL_NU(B,l_nOld,l_nNew,l_sPrevVal);
}
function RTSL_RelativeUpdate(B,D,A,G,C,E,F){var l_oUpdateFlash=SL_DB(B,D,A,G,C);
return (new GF_Update(D,A,l_oUpdateFlash.m_sFlashClr,l_oUpdateFlash.m_sFlashText,l_oUpdateFlash.m_sFinalText,l_oUpdateFlash.m_sFinalClr,G,C,'innerText',F));
}
function RTSL_ImageUpdate(A,D,C,F,B,E){C=((typeof (E.m_pImageMappings[C])=='undefined') ? E.m_vDefaultValue : C);var l_sImgSrc=E.m_pImageMappings[C].src;
var l_vPrevSrc=((typeof (g_pPreviousValue[D])!='undefined') ? g_pPreviousValue[D] : E.m_vDefaultValue);
g_pPreviousValue[D]=C;return ((l_vPrevSrc==C) ? null : (new GF_Update(D,l_sImgSrc,'','','','',true,false,'src')));
}
function RTSL_IndicatorUpdate(C,B,D,F,A,E){var l_nPrevVal=((typeof (g_pPreviousValue[B])!='undefined') ? g_pPreviousValue[B][0] : null);
var l_nPrevDir=((typeof (g_pPreviousValue[B])!='undefined') ? g_pPreviousValue[B][1] : null);
var l_nDir=0;
if(l_nPrevVal!=null){if(D>l_nPrevVal){l_nDir=1;}else 
if(D<l_nPrevVal){l_nDir=-1;}}if(typeof (g_pPreviousValue[B])=='undefined'){g_pPreviousValue[B]=new Array();}g_pPreviousValue[B][0]=D;g_pPreviousValue[B][1]=l_nDir;var l_sImgSrc=E.m_pImageMappings[l_nDir].src;
return ((l_nPrevVal==null||l_nPrevDir==l_nDir) ? null : (new GF_Update(B,l_sImgSrc,'','','','',true,false,'src')));
}
function RTSL_UpDownRelIndicatorUpdate(C,B,D,F,A,E){var l_nPrevVal=((typeof (g_pPreviousValue[B])!='undefined') ? g_pPreviousValue[B][0] : null);
var l_nPrevDir=((typeof (g_pPreviousValue[B])!='undefined') ? g_pPreviousValue[B][1] : null);
var l_nDir=0;
if(l_nPrevVal!=null){if(D>l_nPrevVal){l_nDir=1;}else 
if(D<l_nPrevVal){l_nDir=-1;}}if(l_nDir==0&&l_nPrevDir!=null){l_nDir=l_nPrevDir;}if(typeof (g_pPreviousValue[B])=='undefined'){g_pPreviousValue[B]=new Array();}g_pPreviousValue[B][0]=D;g_pPreviousValue[B][1]=l_nDir;var l_sImgSrc=E.m_pImageMappings[l_nDir].src;
return ((l_nPrevVal==null||l_nPrevDir==l_nDir) ? null : (new GF_Update(B,l_sImgSrc,'','','','',true,false,'src')));
}
function RTSL_UpDownAbsIndicatorUpdate(C,B,D,F,A,E){var l_nPrevVal=((typeof (g_pPreviousValue[B])!='undefined') ? g_pPreviousValue[B][0] : null);
var l_nPrevDir=((typeof (g_pPreviousValue[B])!='undefined') ? g_pPreviousValue[B][1] : null);
var l_nDir=0;
if(D>0){l_nDir=1;}else 
if(D<0){l_nDir=-1;}if(typeof (g_pPreviousValue[B])=='undefined'){g_pPreviousValue[B]=new Array();}g_pPreviousValue[B][0]=D;g_pPreviousValue[B][1]=l_nDir;var l_sImgSrc=E.m_pImageMappings[l_nDir].src;
return ((l_nPrevDir==l_nDir) ? null : (new GF_Update(B,l_sImgSrc,'','','','',true,false,'src')));
}
function RTSL_AbsoluteUpdate(C,B,D,G,A,E,F){var l_oFlashUpdate=SL_DB(C,B,D,G,A);
var l_oFinalUpdate=SL_NU(C,0,GF_ConvertToDecimal(D),'0.00');
if(GF_ConvertToDecimal(D)>0){if(D.substring(0,1)!='+'){D='+'+D;}}return (new GF_Update(B,D,l_oFlashUpdate.m_sFlashClr,l_oFlashUpdate.m_sFlashText,l_oFinalUpdate.m_sFinalText,l_oFinalUpdate.m_sFinalClr,G,A,'innerText',F));
}
function RTSL_NoFlashUpdate(B,D,A,F,C,E){return (new GF_Update(D,A,'','','','',true,C,'innerText'));
}
function RTSL_TextUpdate(B,D,A,G,C,E,F){var l_oUpdate=g_oUpdateFlash.m_sFlashTypes[B].m_oFresh;
return (new GF_Update(D,A,l_oUpdate.m_sFlashClr,l_oUpdate.m_sFlashText,l_oUpdate.m_sFinalText,'',G,C,'innerText',F));
}
function RTSL_HTMLUpdate(B,D,A,G,C,E,F){var l_oUpdate=g_oUpdateFlash.m_sFlashTypes[B].m_oFresh;
return (new GF_Update(D,A,l_oUpdate.m_sFlashClr,l_oUpdate.m_sFlashText,l_oUpdate.m_sFinalText,'',G,C,'innerHTML',F));
}
function GF_ConvertToDecimal(A){var l_nConverted=A;
if(g_fDecimalConvertor){l_nConverted=g_fDecimalConvertor(A);}else 
{
try {if((window.RTML_TransformResult)&&(A instanceof RTML_TransformResult)){l_nConverted=A.value;}else 
if(typeof A=="Object"){l_nConverted=A.toString();}l_nConverted=parseFloat(l_nConverted);}catch(e){l_nConverted=A;}
}return l_nConverted;
}
function RTSL_CreateUpdate(B,C,D,G,A,E,F){G=RTSL_DefaultValue(G,false);A=RTSL_DefaultValue(A,false);E=RTSL_DefaultValue(E,null);var l_oUpdate=null;
var l_nNewFlashTime=RTSL_DefaultValue(F,g_oUpdateFlash.m_nFlashTime);
RTSL_GetElementFromCache(B);if(typeof (g_oUpdateFlash.m_sFlashTypes[C])!='undefined'){l_oUpdate=(g_oUpdateFlash.m_sFlashTypes[C].m_fEvalUpdate(C,B,D,G,A,E,l_nNewFlashTime));}else 
{var l_oRttpProvider=SL4B_Accessor.getRttpProvider();
l_oRttpProvider.debug('Update flash type '+C+' not found','error');}return l_oUpdate;
}
function GF_Update(D,C,J,G,E,I,H,B,A,F){this.m_sDiv=D;this.m_sValue=C;this.m_sFlashClr=J;this.m_sFlashText=G;this.m_sFinalText=E;this.m_sFinalClr=I;this.m_bNoFlash=H;this.m_bDisplayCommas=B;this.m_sDivElement=A;this.m_nFlashTime=F;return this;
}
function RTSL_DisplayUpdate(A){if(A!=null){var l_oElement=RTSL_GetElementFromCache(A.m_sDiv);
l_oElement.value=A.m_sValue;if(A.m_sDivElement=='src'){l_oElement.src=A.m_sValue;}else 
if(l_oElement.type=='button'){l_oElement.value=(A.m_bDisplayCommas) ? (SL_QH(A.m_sValue)) : (A.m_sValue);}else 
{switch(A.m_sDivElement){
case 'innerHTML':l_oElement.innerHTML=(A.m_bDisplayCommas) ? (SL_QH(A.m_sValue)) : (A.m_sValue);break;
case 'nodeValue':case 'innerText':l_oElement.childNodes[0].nodeValue=(A.m_bDisplayCommas) ? (SL_QH(A.m_sValue)) : (A.m_sValue);break;
default :l_oElement.innerText=(A.m_bDisplayCommas) ? (SL_QH(A.m_sValue)) : (A.m_sValue);break;
}if(A.m_bNoFlash==false){
try {l_oElement.style.color=A.m_sFlashText;l_oElement.style.backgroundColor=A.m_sFlashClr;}catch(e){}
l_oElement.style.color=A.m_sFlashText;l_oElement.style.backgroundColor=A.m_sFlashClr;g_pUpdateClearQueue.m_Add(A.m_sDiv,A.m_sFinalText,A.m_nFlashTime);}else 
{l_oElement.style.color=A.m_sFinalText;g_pUpdateClearQueue.m_Remove(A.m_sDiv);}}}}
function RTSL_FlushUpdate(A){var l_oUpdate=g_pUpdateClearQueue.m_Remove(A);
if(l_oUpdate!=null){var l_oElement=RTSL_GetElementFromCache(A);
l_oElement.style.color=l_oUpdate.m_sColour;l_oElement.style.backgroundColor='';}}
function SL_CH(){this.m_pQueue=new Array();this.m_pLookup=new Object();}
SL_CH.prototype.m_Add = SL_DL;SL_CH.prototype.m_Remove = SL_NQ;SL_CH.timeoutID=null;function SL_DL(A,C,B){if(typeof (this.m_pQueue[B])=='undefined'){this.m_pQueue[B]=new Array();}var l_nIndex=this.m_pQueue[B].length;
if(l_nIndex==0&&SL_CH.timeoutID==null){SL_CH.timeoutID=setTimeout('C_ClearUpdateFlashes('+B+')',B);}else 
{this.m_Remove(A);}this.m_pQueue[B][l_nIndex]=new SL_AK(A,C,B);this.m_pLookup[A]=new SL_EJ(B,l_nIndex);}
function SL_NQ(A){if(typeof (this.m_pLookup[A])!='undefined'){var l_nFlashTime=this.m_pLookup[A].m_nFlashTime;
for(var l_nOldIndex=this.m_pLookup[A].m_nIndex;l_nOldIndex>=0;--l_nOldIndex){if(typeof (this.m_pQueue[l_nFlashTime][l_nOldIndex])!='undefined'&&this.m_pQueue[l_nFlashTime][l_nOldIndex].m_sElementId==A){this.m_pQueue[l_nFlashTime][l_nOldIndex].m_bActiveUpdate=false;return this.m_pQueue[l_nFlashTime][l_nOldIndex];
}}}return null;
}
function SL_AK(A,C,B){this.m_sElementId=A;this.m_sColour=C;this.m_dtExpiryTime=new Date().valueOf()+B;this.m_bActiveUpdate=true;}
function C_ClearUpdateFlashes(A){SL_CH.timeoutID=null;var l_dtCurrentTime=new Date().valueOf();
if(g_pUpdateClearQueue.m_pQueue[A]!=null){while(g_pUpdateClearQueue.m_pQueue[A].length&&g_pUpdateClearQueue.m_pQueue[A][0].m_dtExpiryTime<=l_dtCurrentTime){var l_sElementId=g_pUpdateClearQueue.m_pQueue[A][0].m_sElementId;
if(g_pUpdateClearQueue.m_pQueue[A][0].m_bActiveUpdate){var l_oElement=RTSL_GetElementFromCache(l_sElementId);
if(l_oElement!=null){l_oElement.style.color=g_pUpdateClearQueue.m_pQueue[A][0].m_sColour;l_oElement.style.backgroundColor='';}delete (g_pUpdateClearQueue.m_pLookup[l_sElementId]);}g_pUpdateClearQueue.m_pQueue[A]=g_pUpdateClearQueue.m_pQueue[A].slice(1);}if(g_pUpdateClearQueue.m_pQueue[A].length>0&&SL_CH.timeoutID==null){var l_nTimeoutPeriod=g_pUpdateClearQueue.m_pQueue[A][0].m_dtExpiryTime-(new Date().valueOf());
if(l_nTimeoutPeriod<0){l_nTimeoutPeriod=0;}else 
if(l_nTimeoutPeriod>A){l_nTimeoutPeriod=A;}SL_CH.timeoutID=setTimeout('C_ClearUpdateFlashes('+A+')',l_nTimeoutPeriod);}}}
function SL_EJ(B,A){this.m_nFlashTime=B;this.m_nIndex=A;}
function RTSL_ClearPreviousValue(A){if(typeof g_pPreviousValue[A]!='undefined'){delete (g_pPreviousValue[A]);}}
function MOD_CreateImages(B,A,C){this.m_vDefaultValue=B;this.m_nWidth=A;this.m_nHeight=C;this.m_pImageMappings=new Object();}
MOD_CreateImages.prototype.addImage = SL_JK;function SL_JK(A,B){this.m_pImageMappings[A]=new Image();this.m_pImageMappings[A].src=B;}
function SL_QH(A){if(isNaN(A)){return A;
}var l_sAbsVal=new String(Math.abs(A));
l_sAbsVal=l_sAbsVal.split('.');var l_sComma='';
var l_nPosition;

do{l_sComma=l_sAbsVal[0].substr(((l_sAbsVal[0].length-3>=0) ? l_sAbsVal[0].length-3 : 0))+((l_sComma!='') ? ',' : '')+l_sComma;l_sAbsVal[0]=l_sAbsVal[0].substr(0,l_sAbsVal[0].length-3);}while(l_sAbsVal[0].length>0);var l_sVal=new String(A);
l_sVal=l_sVal.split('.');if(typeof (l_sVal[1])!='undefined'&&l_sVal[1].length>0){l_sComma+='.'+l_sVal[1];}var l_nDir=((A==0) ? 1 : (A/Math.abs(A)));
l_sComma=((l_nDir==1) ? l_sComma : ('-'+l_sComma));return l_sComma;
}
var SL4B_UpdateBatcher=function(){};
SL4B_UpdateBatcher = new function(){this.initializeMemberVariables = function(){this.m_nQuantisation=100;this.m_nMaxProcessorUtilisation=0.5;this.m_mBinIdToUpdateBinMap=SL_BF.createMap();this.m_mClearedElementIdToBinIdMap=SL_BF.createMap();this.m_bStarted=false;this.m_bIsRunning=false;};
this.initializeMemberVariables();this.addUpdate = function(A){if(!(A instanceof SL4B_Update)){throw new SL4B_Exception("Update was invalid");
}else 
{if(!this.m_bStarted){this.m_bStarted=true;this.m_bIsRunning=true;this.m_nNextBinId=this.getBinId(this.getTimeStamp());this.startProccessingBins();}var l_pNextUpdateBin=this.getNextUpdateBin();
var l_nUpdateId=A.getIdentifer();
if(this.m_mClearedElementIdToBinIdMap[l_nUpdateId]){this.removeClearUpdate(l_nUpdateId);}l_pNextUpdateBin[l_nUpdateId]=A;}};
this.start = function(){if(!this.m_bStarted){throw new SL4B_Exception("The addUpdate() method has not yet been called and so the batcher was not yet running.");
}else 
if(this.m_bIsRunning){throw new SL4B_Exception("The update batcher was already running.");
}else 
{this.m_bIsRunning=true;this.startProccessingBins();}};
this.stop = function(){if(!this.m_bIsRunning){throw new SL4B_Exception("The update batcher was not currently running.");
}else 
{this.m_bIsRunning=false;}};
this.getTimeStamp = function(){return (new Date()).valueOf();
};
this.getQuantisationPeriod = function(){return this.m_nQuantisation;
};
this.startProccessingBins = function(){this.processPendingBins();};
this.flushUpdates = function(){clearTimeout(this.m_nTimeoutId);delete this.m_nTimeoutId;var l_nStartTime=this.getTimeStamp();
var l_nPresentBinId=this.getBinId(l_nStartTime);
var l_nBinId;
for(l_nBinId in this.m_mBinIdToUpdateBinMap){l_pUpdateBin=this.m_mBinIdToUpdateBinMap[l_nBinId];this.processUpdateBin(l_pUpdateBin,l_nPresentBinId);this.deleteUpdateBin(l_nBinId);}this.m_pClearedElementIdToBinIdMap={};this.startProccessingBins();};
this.processPendingBins = function(){if(this.m_bIsRunning){var l_nStartTime=this.getTimeStamp();
var l_nFirstBinId=this.m_nNextBinId;
var l_nPresentBinId=this.getBinId(l_nStartTime);
this.m_nNextBinId=l_nPresentBinId+1;for(var l_nBinId=l_nFirstBinId;l_nBinId<=l_nPresentBinId;++l_nBinId){var l_pUpdateBin=this.m_mBinIdToUpdateBinMap[l_nBinId];
if(l_pUpdateBin){this.processUpdateBin(l_pUpdateBin,l_nPresentBinId);this.deleteUpdateBin(l_nBinId);}}var l_nTimeTaken=this.getTimeStamp()-l_nStartTime;
var l_nMinTimeTakenIncludingSleep=l_nTimeTaken/this.m_nMaxProcessorUtilisation;
var l_nSleepTime;
if(l_nMinTimeTakenIncludingSleep<=this.m_nQuantisation){l_nSleepTime=this.m_nQuantisation-l_nTimeTaken;}else 
{l_nSleepTime=l_nMinTimeTakenIncludingSleep-l_nTimeTaken;}this.setTimeout(l_nSleepTime);}};
this.setTimeout = function(A){this.m_nTimeoutId=window.setTimeout(this.m_fProcessPendingBins,A,"SL4B update batcher");};
this.generateProcessPendingBinsFunction = function(){var l_nThis=this;
return function(){l_nThis.processPendingBins();};
};
this.m_fProcessPendingBins=this.generateProcessPendingBinsFunction();this.processUpdateBin = function(A,B){for(l_sUpdateId in A){var l_oUpdate=A[l_sUpdateId];
var l_oClearUpdate=l_oUpdate.drawUpdate();
this.removeClearUpdate(l_sUpdateId);if(l_oClearUpdate){var l_nFlashTime=l_oClearUpdate.getFlashTime();
var l_nFlashBinId=B+Math.ceil(l_nFlashTime/this.m_nQuantisation);
var l_pFlashUpdateBin=this.getUpdateBin(l_nFlashBinId);
l_pFlashUpdateBin[l_sUpdateId]=l_oUpdate;if(this.m_mClearedElementIdToBinIdMap[l_sUpdateId]){this.m_mClearedElementIdToBinIdMap[l_sUpdateId].push(l_nFlashBinId);}else 
{this.m_mClearedElementIdToBinIdMap[l_sUpdateId]=[l_nFlashBinId];}}}};
this.removeClearUpdate = function(A){var l_nBinId=this.m_mClearedElementIdToBinIdMap[A];
if(l_nBinId){this.m_mClearedElementIdToBinIdMap=SL_BF.removeItem(this.m_mClearedElementIdToBinIdMap,A);for(var x=0,l=l_nBinId.length;x<l;x++){if(l_nBinId[x]!==undefined&&this.m_mBinIdToUpdateBinMap[l_nBinId[x]]!==undefined){delete this.m_mBinIdToUpdateBinMap[l_nBinId[x]][A];}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_RTTP_ERROR_INT,"SL4B_UpdateBatcher.removeClearUpdate(l_sUpdateId): An error occured, an object is undefined. l_nBinId[x] is ({0}) and this.m_mBinIdToUpdateBinMap[l_nBinId[x]] is ({1}).",l_nBinId[x],this.m_mBinIdToUpdateBinMap[l_nBinId[x]]);}}}};
this.getBinId = function(A){return Math.floor(A/this.m_nQuantisation);
};
this.getNextBinId = function(){return this.m_nNextBinId;
};
this.setNextBinId = function(A){this.m_nNextBinId=A;};
this.getUpdateBin = function(A){var l_pUpdateBin=this.m_mBinIdToUpdateBinMap[A];
if(!l_pUpdateBin){l_pUpdateBin={};this.m_mBinIdToUpdateBinMap[A]=l_pUpdateBin;}return l_pUpdateBin;
};
this.getNextUpdateBin = function(){return this.getUpdateBin(this.m_nNextBinId);
};
this.deleteUpdateBin = function(A){this.m_mBinIdToUpdateBinMap=SL_BF.removeItem(this.m_mBinIdToUpdateBinMap,A);};
this.getBinIdForClearedElement = function(A){return this.m_mClearedElementIdToBinIdMap[A];
};
};
var SL4B_Update=function(){};
if(false){function SL4B_Update(){}
}SL4B_Update = function(B,A){this.m_sIdentifier=B;this.m_vValue=A;this.m_nFlashTime=1000;};
SL4B_Update.prototype.getIdentifer = function(){return this.m_sIdentifier;
};
SL4B_Update.prototype.toString = function(){return this.m_sIdentifier+": "+this.m_vValue;
};
SL4B_Update.prototype.getFlashTime = function(){return this.m_nFlashTime;
};
SL4B_Update.prototype.setFlashTime = function(A){this.m_nFlashTime=A;};
SL4B_Update.prototype.drawUpdate = function(){return null;
};
var SL4B_ElementCache={};
var SL4B_TestUpdate=function(){};
if(false){function SL4B_TestUpdate(){}
}SL4B_TestUpdate = function(D,A,C,B){SL4B_Update.apply(this,[D,A]);this.m_sUpdateType=C;this.m_bFirstDraw=true;this.m_bCacheValue=(B===undefined ? true : B);};
SL4B_TestUpdate.prototype = new SL4B_Update();SL4B_TestUpdate.classPrefixes={};SL4B_TestUpdate.values={};SL4B_TestUpdate.prototype.drawUpdate = function(){var l_oElem=SL4B_ElementCache[this.m_sIdentifier];
var l_bHasOldValue=false;
if(!l_oElem){l_oElem=document.getElementById(this.m_sIdentifier);SL4B_ElementCache[this.m_sIdentifier]=l_oElem;}if(l_oElem){var l_sClassPrefix=SL4B_TestUpdate.classPrefixes[this.m_sIdentifier];
if(!l_sClassPrefix){SL4B_TestUpdate.classPrefixes[this.m_sIdentifier]=SL4B_TestUpdate.createClassPrefix(l_oElem.className);l_sClassPrefix=SL4B_TestUpdate.classPrefixes[this.m_sIdentifier];}var l_vOldValue=SL4B_TestUpdate.values[this.m_sIdentifier];
var l_sClass="";
var l_bFirstDraw=this.m_bFirstDraw;
if(this.m_bFirstDraw){this.m_bFirstDraw=false;var l_oTextNode=l_oElem.firstChild;
if(l_oTextNode){l_oTextNode.nodeValue=this.m_vValue;if(this.callbackFn!==undefined){this.callbackFn();}}if(this.m_bCacheValue){SL4B_TestUpdate.values[this.m_sIdentifier]=this.m_vValue;}l_bHasOldValue=(l_vOldValue!==undefined&&l_vOldValue!=="");if(l_bHasOldValue){this.m_sClassSuffix=this.getDirection(this.m_sUpdateType,l_vOldValue,this.m_vValue);l_sClass="recent"+this.m_sClassSuffix;}}else 
{l_sClass="old"+this.m_sClassSuffix;}l_sClass=l_sClassPrefix+l_sClass;l_oElem.className=l_sClass;}return (l_bFirstDraw&&l_bHasOldValue) ? this : null;
};
SL4B_TestUpdate.createClassPrefix = function(A){l_sClassPrefix=(A) ? A.replace(/((recent)|(old))((Flat)|(Up)|(Down)) ?/,"","g") : null;l_sClassPrefix=(l_sClassPrefix) ? l_sClassPrefix+" " : "";return l_sClassPrefix;
};
SL4B_TestUpdate.prototype.getDirection = function(C,B,A){var l_sDirection;
if(g_fDecimalConvertor!=null){if(B){B=g_fDecimalConvertor(B);}if(A){A=g_fDecimalConvertor(A);}}if(C=="Relative"){if(!B||(A==B)){l_sDirection="Flat";}else 
if(A>B){l_sDirection="Up";}else 
if(A<B){l_sDirection="Down";}}else 
if(C=="Absolute"){if(this.m_vValue==0){l_sDirection="Flat";}else 
if(this.m_vValue>0){l_sDirection="Up";}else 
{l_sDirection="Down";}}else 
if(C=="Text"){l_sDirection="Flat";}return l_sDirection;
};
SL4B_TestUpdate.prototype.setCacheValueFlag = function(A){this.m_bCacheValue=A;};
SL4B_TestUpdate.prototype.getFlashTime = function(){return 500;
};
SL4B_TestUpdate.updateElementClassName = function(B,A){SL4B_TestUpdate.classPrefixes[B]=SL4B_TestUpdate.createClassPrefix(A);};
var SL4B_SingleFile=true;
var SL4B_ScriptLoader = new function(){this.const_STANDARD_CREDENTIALS_PROVIDER="standard";this.const_KEYMASTER_CREDENTIALS_PROVIDER="keymaster";this.m_oIndexScript=null;this.m_sRelativeUrlPrefix="";this.m_bLogFileNames=false;this.findIndexScript=SL_ML;this.getRelativeUrlPrefix=SL_OQ;this.getRootUrl = function(){return this.m_sRelativeUrlPrefix;
};
this.loadRelativeScript=SL_HX;this.loadScript=SL_BM;this.loadRequiredScripts=SL_HZ;this.loadConfiguredScripts=SL_HP;this.setCommonDomain=SL_SG;this.createRttpProvider=SL_AJ;this.createSnapshotProvider=SL_QL;this.getIndexScript = function(){return this.m_oIndexScript;
};
this.loadSl4bScript = function(A){this.loadRelativeScript("sl4b/"+A);};
this.loadRtmlScript = function(A){this.loadRelativeScript("rtml/"+A);};
this.loadRtslScript = function(A){this.loadRelativeScript("rtsl/"+A);};
this.getElementById = function(A){return document.getElementById(A);
};
this.isInternetExplorer = function(){return (navigator.userAgent.toLowerCase().match(/msie/)!=null);
};
this._mustLoadCredentialsScripts = function(A){return A.isMasterFrame()||A._$isRegistrationWindowEnabled();
};
this._mustLoadSlaveFrameProvider = function(A){if(this._mustLoadSlaveFrameWrapperProvider(A)){return true;
}return !A.isMasterFrame();
};
this._mustLoadSlaveFrameWrapperProvider = function(A){if(A._$isRegistrationWindowEnabled()){return !SL4B_WindowRegistrar.isMaster();
}return false;
};
this.m_oIndexScript=this.findIndexScript();this.m_sRelativeUrlPrefix=this.getRelativeUrlPrefix();};
SL4B_ScriptLoader.const_OBJECT_RTTP_PROVIDER="object";SL4B_ScriptLoader.const_APPLET_RTTP_PROVIDER="applet";SL4B_ScriptLoader.const_JAVASCRIPT_RTTP_PROVIDER="javascript";SL4B_ScriptLoader.const_TEST_RTTP_PROVIDER="test";function SL_ML(){var l_oIndexScript=null;
if(this.getElementById("sl4b")){l_oIndexScript=this.getElementById("sl4b");}else 
if(this.getElementById("rtml")){l_oIndexScript=this.getElementById("rtml");}else 
if(this.getElementById("rtsl")){l_oIndexScript=this.getElementById("rtsl");}else 
{var l_pScripts=document.getElementsByTagName("script");
if(l_pScripts.length==1){l_oIndexScript=l_pScripts[0];l_oIndexScript.id="sl4b";}else 
{for(var l_nScript=l_pScripts.length-1;l_nScript>=0;--l_nScript){if(l_pScripts[l_nScript].src.match(/(^|\/)(sl4b|rt[ms]l)($|\/$|\/index2?.js$)/)){l_oIndexScript=l_pScripts[l_nScript];l_oIndexScript.id="sl4b";break;
}}if(l_oIndexScript==null){throw new SL4B_Error("ScriptLoader unable to locate SL4B/RTML/RTSL script tag in DOM");
}}}return l_oIndexScript;
}
function SL_OQ(){var l_sRelativeUrlPrefix="";
var l_oMatch=this.m_oIndexScript.src.match(/(.*)(sl4b|rt[ms]l)($|\/$|\/index2?.js$)/);
if(l_oMatch==null){l_oMatch=this.m_oIndexScript.src.match(/(([.][.]\/)*)/);l_sRelativeUrlPrefix="../"+l_oMatch[1];}else 
{l_sRelativeUrlPrefix=l_oMatch[1];}return l_sRelativeUrlPrefix;
}
function SL_HX(A){this.loadScript(this.m_sRelativeUrlPrefix+A);}
function SL_BM(A,B){if(this.m_bLogFileNames){SL4B_Logger.log(SL4B_DebugLevel.const_DEBUG_INT,"ScriptLoader.loadScript: Loading {0}",A);}var tag=document.createElement('script');
tag.setAttribute('src',A);tag.setAttribute('type','text/javascript');if(B){tag.onload=B;tag.onreadystatechange = function(){if(this.readyState=='loaded'||this.readyState=='complete'){tag.onreadystatechange=null;B();}};
}document.getElementsByTagName('head')[0].appendChild(tag);}
function SL_HZ(){SL_LZ();SL_KJ();}
function SL_HP(A){SL4B_WindowEventHandler.initialise();SL4B_WindowRegistrar.initialise();SL_HO();SL4B_Accessor.getLogger().setNumberOfLogMessagesToRetain(A.getNumberOfLogMessagesToRetain());SL4B_Accessor.getLogger().openDebugConsoleOnStartUp(false);if(A.getService()!=null&&A.getScriptTagAttribute("commondomain")==null){alert("FATAL SL4B ERROR:\nThe \"commondomain\" configuration attribute must be explicitly set when the \"service\" attribute is used");return;
}if(!this.setCommonDomain(A)){alert("FATAL SL4B ERROR:\nThe host name for the web page you have entered must end with \""+A.getCommonDomain()+"\".\nUnable to connect to establish a streaming connection.");return;
}SL4B_Accessor.getLogger().openDebugConsoleOnStartUp(true);C_CallbackQueue.start();this.m_bLogFileNames=true;if(this._mustLoadCredentialsScripts(A)){if(A.getCredentialsProvider()==this.const_STANDARD_CREDENTIALS_PROVIDER){SL_RW();}else 
if(A.getCredentialsProvider()==this.const_KEYMASTER_CREDENTIALS_PROVIDER){SL_CX();}else 
{this.loadScript(A.getCredentialsProvider());}}if(this._mustLoadSlaveFrameProvider(A)){}if(this._mustLoadSlaveFrameWrapperProvider(A)){}if(false){}if(A.getService()!=null){SL_JW();}if(A.getRttpProvider()==SL4B_ScriptLoader.const_APPLET_RTTP_PROVIDER){}else 
if(A.getRttpProvider()==SL4B_ScriptLoader.const_OBJECT_RTTP_PROVIDER){SL_GQ();}else 
if(A.getRttpProvider()==SL4B_ScriptLoader.const_JAVASCRIPT_RTTP_PROVIDER){}else 
if(A.getRttpProvider()==SL4B_ScriptLoader.const_TEST_RTTP_PROVIDER){}else 
{this.loadScript(A.getRttpProvider());}SL_FG();if(A.includeRtml()){}if(A.includeRtsl()||A.includeFlash()){}if(A.includeRtsl()){SL_IY();}if(A.includeFlash()){}}
function SL_SG(A){if(window.G_UNIT_TESTING){return true;
}var l_bDomainSet=true;
var l_sCommonDomain=A.getCommonDomain();
SL4B_FrameRegistrarAccessor.setCommonContainerDomain(l_sCommonDomain);if(l_sCommonDomain!=null){
try {SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"ScriptLoader.setCommonDomain: Setting document.domain to: {0}",l_sCommonDomain);document.domain=l_sCommonDomain;}catch(e){SL4B_Logger.log(SL4B_DebugLevel.const_ERROR_INT,"ScriptLoader.setCommonDomain: Failed to set the common domain to: {0}",l_sCommonDomain);l_bDomainSet=false;}
}else 
{SL4B_Logger.log(SL4B_DebugLevel.const_INFO_INT,"ScriptLoader.setCommonDomain: Ignoring common domain.");}return l_bDomainSet;
}
function SL_AJ(){var l_oConfiguration=SL4B_Accessor.getConfiguration();
var l_oProvider=null;
var l_bSetUnderlyingRttpProvider=true;
if(this._mustLoadSlaveFrameWrapperProvider(l_oConfiguration)){l_oProvider=new SL4B_SlaveFrameWrapperRttpProvider();l_bSetUnderlyingRttpProvider=false;}else 
if(this._mustLoadSlaveFrameProvider(l_oConfiguration)){l_oProvider=new SL4B_SlaveFrameRttpProvider();}else 
if(l_oConfiguration.getService()!=null){l_oProvider=new SL4B_FailoverRttpProvider();l_bSetUnderlyingRttpProvider=false;}else 
{l_oProvider=SL4B_Accessor.getRttpProviderFactory().createRttpProvider(new SL4B_SimpleLiberatorConfiguration());}if(l_oProvider!=null){SL4B_Accessor.setRttpProvider(l_oProvider);if(l_bSetUnderlyingRttpProvider){SL4B_Accessor.setUnderlyingRttpProvider(l_oProvider);}l_oProvider.addConnectionListener(new SL4B_LogConnectionListener(SL4B_Logger));SL_PQ(l_oProvider);}}
function SL_QL(){var l_oSnapshotProvider=new SL4B_SnapshotProvider();
SL4B_Accessor.setSnapshotProvider(l_oSnapshotProvider);}
function SL_PQ(B){B.addConnectionListener(SL4B_ConnectionEventRecorder);var fAddConnectionListener=B.addConnectionListener;
B.addConnectionListener = function(A){SL4B_ConnectionEventRecorder.sendCurrentConnectionEvents(A);fAddConnectionListener.call(B,A);};
}
var SL4B_Throwable=function(){};
if(false){function SL4B_Throwable(){}
}SL4B_Throwable = function(A){this.m_sClassName=null;this.m_sMessage=null;this.m_pStackTrace=new Array();this.initialise("SL4B_Throwable",A);};
SL4B_Throwable.prototype.initialise = SL_KZ;SL4B_Throwable.prototype.getClass = function(){return this.m_sClassName;
};
SL4B_Throwable.prototype.getMessage = function(){return this.m_sMessage;
};
SL4B_Throwable.prototype.getStackTrace = function(){return this.m_pStackTrace;
};
SL4B_Throwable.prototype.toString = function(){return this.m_sClassName+":\n   message="+this.m_sMessage+"\n   stack trace:\n      "+this.getStackTrace().join("\n      ");
};
function SL_KZ(A,B){this.m_sClassName=A;this.m_sMessage=B;var l_fCaller=SL_KZ.caller;
while((l_fCaller=l_fCaller.caller)!=null){if(this.m_pStackTrace.length>20){this.m_pStackTrace.push("...");break;
}else 
{var l_sFunctionInfo="";
var l_oMatch=l_fCaller.toString().match(/function\x20([^_]*_[^_(]*)_([^(]*)/);
if(l_oMatch==null){l_oMatch=l_fCaller.toString().match(/function\x20([^_]*_[^(]*)/);if(l_oMatch==null){l_sFunctionInfo+="{anonymous method call}";}else 
{l_sFunctionInfo+=""+l_oMatch[1]+".<init>";}}else 
{l_sFunctionInfo+=""+l_oMatch[1]+"."+l_oMatch[2].charAt(0).toLowerCase()+l_oMatch[2].substr(1);}l_sFunctionInfo+=" (";for(var l_nArgument=0,l_nLength=l_fCaller.arguments.length;l_nArgument<l_nLength;++l_nArgument){var l_vArgValue=l_fCaller.arguments[l_nArgument];
l_sFunctionInfo+=((l_nArgument==0) ? "" : ", ")+((typeof l_vArgValue=="string") ? "\""+l_vArgValue+"\"" : l_vArgValue);}l_sFunctionInfo+=")";this.m_pStackTrace.push(l_sFunctionInfo);}}}
var SL4B_Error=function(){};
if(false){function SL4B_Error(){}
}SL4B_Error = function(A){this.initialise(SL4B_Error.const_ERROR_CLASS,A);};
SL4B_Error.prototype = new SL4B_Throwable;SL4B_Error.const_ERROR_CLASS="SL4B_Error";var SL4B_Exception=function(){};
if(false){function SL4B_Exception(){}
}SL4B_Exception = function(A){this.initialise(SL4B_Exception.const_EXCEPTION_CLASS,A);};
SL4B_Exception.prototype = new SL4B_Throwable;SL4B_Exception.const_EXCEPTION_CLASS="SL4B_Exception";var SL4B_Version=function(){};
if(false){function SL4B_Version(){}
}SL4B_Version = new function(){this.m_sVersionNumber="4.5.20";this.m_sBuildNumber="216576";this.m_sBuildDate="19-Dec-2011";this.m_sCopyright="Copyright 1995-2010 Caplin Systems Ltd";this.getVersion = function(){return this.m_sVersionNumber;
};
this.getBuildNumber = function(){return this.m_sBuildNumber;
};
this.getBuildDate = function(){return this.m_sBuildDate;
};
this.getVersionInfo = function(){return "SL4B "+this.m_sVersionNumber+"-"+this.m_sBuildNumber+" (Built "+this.m_sBuildDate+"), "+this.m_sCopyright;
};
};
function RTSL_DefaultValue(A,B){return ((A==null)||(typeof (A)=="undefined") ? B : A);
}
SL4B_ScriptLoader.loadRequiredScripts();