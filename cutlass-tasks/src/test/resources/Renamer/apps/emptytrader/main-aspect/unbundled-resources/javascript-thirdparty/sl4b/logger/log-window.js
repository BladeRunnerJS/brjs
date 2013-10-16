var g_oMessageLog=null;
var g_bScrollFollow=false;
window.onload = function(){g_oMessageLog=document.getElementById("tblMessageLog");
try {opener.SL4B_Logger.debugConsoleOpened();}catch(e){GF_LogMessage("DebugWindow Error: Exception occurred whilst processing window.onload ("+e.toString()+")");}
};
window.onunload = function(){
try {opener.SL4B_Logger.debugConsoleClosed();}catch(e){GF_LogMessage("DebugWindow Error: Exception occurred whilst processing window.onunload ("+e.toString()+")");}
};
var GF_ChangeDocumentDomain=function(A){
try {document.domain=A;}catch(e){GF_LogMessage("DebugWindow Error: Exception occurred whilst setting the document.domain property ("+e.toString()+")");}
};
function GF_LogMessage(A){g_bScrollFollow=((document.body.scrollTop+document.body.clientHeight)==document.body.scrollHeight);var l_oRow=document.createElement("tr");
var l_oCell=document.createElement("td");
l_oCell.appendChild(document.createTextNode(A));l_oRow.appendChild(l_oCell);g_oMessageLog.appendChild(l_oRow);if(g_bScrollFollow){document.body.scrollTop=document.body.scrollHeight;}}
