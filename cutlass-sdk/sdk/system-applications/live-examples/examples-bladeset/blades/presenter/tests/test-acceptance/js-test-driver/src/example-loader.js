function loadExample(sExample, bLocalised)
{
	for(var sPossibleTemplate in jQuery.template)
	{
		if(jQuery.template.hasOwnProperty(sPossibleTemplate))
		{
			delete jQuery.template[sPossibleTemplate];
		}
	}
	loadModel("/test/default-aspect/unbundled-resources/tutorial/contents/" + sExample + "/model.js");
	loadView("/test/default-aspect/unbundled-resources/tutorial/contents/" + sExample + "/view.html");
	if (bLocalised) {
		loadI18n("/test/default-aspect/unbundled-resources/tutorial/contents/" + sExample + "/i18n.properties");
	}
};

function loadModel(sModelUrl)
{
	var sModelJs = caplin.getFileContents(sModelUrl);
	
	eval(sModelJs);
};

function loadView(sViewUrl)
{
	var oHtmlResourceService = caplin.core.ServiceRegistry.getService("br.html-service");
	oHtmlResourceService.m_sUrl = sViewUrl;
	oHtmlResourceService._loadHtml();
};

function resetTranslator(sI18n)
{
	var pTokens = sI18n.split("\n");
	var mMessages = {};
	for (var i=0; i < pTokens.length; i++) {
		var sToken = pTokens[i];
		if (sToken.trim() != '')
		{
			var pToken = sToken.split(":", 2);
			mMessages[pToken[0]] = pToken[1].trim();
		}
	}
	caplin.i18n.Translator.getTranslator().m_mMessages = mMessages;
};


function loadI18n(sI18nUrl)
{
	$.ajax({
		  url: sI18nUrl,
		  success: function( sI18n ){
				resetTranslator(sI18n);
			},
		  dataType: "text",
		  async:false
		});
};

