live.examples.presenter.ui.CodeArea = function()
{
	this.m_sView = "";
	this.m_sModel = "";
	this.m_sI18n = "";

	this.m_sEditedView = "";
	this.m_sEditedModel = "";
	this.m_sEditedI18n = "";

	this.m_nAvailableContentHeight = 300;

	this.m_eViewContainer = $('#view_editor_container');
	this.m_eModelContainer = $('#model_editor_container');
	this.m_eI18nContainer = $('#i18n_editor_container');
	this.m_oViewEditor = this._createEditor('view_editor_container', "text/html");
	this.m_oModelEditor = this._createEditor('model_editor_container', "text/javascript");
	this.m_oI18nEditor = this._createEditor('i18n_editor_container', "text/javascript");
	this.m_eViewScrollContainer = this.m_eViewContainer.find(".CodeMirror-scroll");
	this.m_eModelScrollContainer = this.m_eModelContainer.find(".CodeMirror-scroll");
	this.m_eI18nScrollContainer = this.m_eI18nContainer.find(".CodeMirror-scroll");
	this.m_eResultPane = $("#view #result");
	this.m_eErrorPane = $("#model #error");
	this.m_eErrorCover = $('#view #error_cover');
	this.m_eView = $('#view');
	this.m_eModel = $('#model');
	this.m_eI18n = $('#i18n');
	this.m_eErrorPaneContent = $("#model #error .content");
	this.m_eCodeControls = $("#code_controls");
	this.m_eResetButton = $('#reset-button');
	$('#code').bind("keydown keypress keyup",function(e){e.stopPropagation()})
	this._hideError();
	this.m_eResetButton.click( this.resetCode.bind(this) );
};

live.examples.presenter.ui.CodeArea.prototype.updateEditors = function()
{
	this.m_oViewEditor.setValue( this.m_sView );
	this.m_oModelEditor.setValue( this.m_sModel );
	this.m_oI18nEditor.setValue( this.m_sI18n );
};

live.examples.presenter.ui.CodeArea.prototype.update = function( sView, sModel, sI18n )
{
	this.m_eResetButton.hide();
	this.m_sView = sView || "";
	this.m_sModel = sModel || "";
	this.m_sI18n = sI18n || "";

	var fUpdate = function()
	{
		this.updateEditors();
		this._updateResultPane();
	}

	setTimeout( fUpdate.bind(this),0);

};

live.examples.presenter.ui.CodeArea.prototype.showPanel = function( sSelection )
{
	if( sSelection === "i18n" )
	{
		this.m_eViewContainer.hide();
		this.m_eModelContainer.hide();
		this.m_eI18nContainer.show();
	}
	else if( sSelection === "view" )
	{
		this.m_eModelContainer.hide();
		this.m_eI18nContainer.hide();
		this.m_eViewContainer.show();
	}
	else
	{
		this.m_eViewContainer.hide();
		this.m_eI18nContainer.hide();
		this.m_eModelContainer.show();
	}
	this.update( this.m_sEditedView, this.m_sEditedModel, this.m_sEditedI18n );
};

/**
 * @param {int} nAvailableHeight The available height for elements the CodeArea \
 *                               is responsible for (#code_content) to expand into.
 */
live.examples.presenter.ui.CodeArea.prototype.setHeight = function( nAvailableHeight )
{
	this.m_nAvailableContentHeight = nAvailableHeight;
	this.resize();
};

/**
 * Resize the inner components based on size of #code_controls
 */
live.examples.presenter.ui.CodeArea.prototype.resize = function()
{
	var nScrollContainerHeight = this.m_nAvailableContentHeight - this.m_eCodeControls.outerHeight(true);

	if (this.m_eModel.hasClass("error") || this.m_eI18n.hasClass("error")) {
		nScrollContainerHeight = nScrollContainerHeight - this.m_eErrorPane.outerHeight(true);
	}

	this.m_eViewScrollContainer.height( nScrollContainerHeight );
	this.m_eModelScrollContainer.height( nScrollContainerHeight );
	this.m_eI18nScrollContainer.height( nScrollContainerHeight );
};

live.examples.presenter.ui.CodeArea.prototype.resetCode = function()
{
	this.update( this.m_sView, this.m_sModel, this.m_sI18n );
};

live.examples.presenter.ui.CodeArea.prototype._onChange = function()
{

	this.m_sEditedView = this.m_oViewEditor.getValue();
	this.m_sEditedModel = this.m_oModelEditor.getValue();
	this.m_sEditedI18n = this.m_oI18nEditor.getValue();
	
	this._updateResultPane();
};

live.examples.presenter.ui.CodeArea.prototype._onCodeMirrorType = function()
{
	this.m_eResetButton.show();
}

live.examples.presenter.ui.CodeArea.prototype._createEditor = function(sEditorId, sEditorType)
{
	var mEditorSettings =
	{
		mode: sEditorType,
		lineNumbers: true,
		matchBrackets: true,
		indentUnit: 4,
		indentWithTabs: true,
		enterMode: "keep",
		tabMode: "shift",
		onChange: this._onChange.bind(this),
		onKeyEvent: this._onCodeMirrorType.bind(this)
	};

	return CodeMirror(document.getElementById(sEditorId), mEditorSettings);
};

/*
 * Overwrite the strings in the translator. 
 * This can be removed when we convert Live examples to use bundlers
 */
live.examples.presenter.ui.CodeArea.prototype._resetTranslator = function()
{
	var pTokens = this.m_sEditedI18n.split("\n");
	var mMessages = {};
	for (var i=0; i < pTokens.length; i++) {
		var sToken = pTokens[i];
		if (sToken.trim() != '')
		{
			var pToken = sToken.split(":", 2);
			mMessages[pToken[0]] = pToken[1];
		}
	}
	caplin.i18n.Translator.getTranslator()._setMessages(mMessages);
};

live.examples.presenter.ui.CodeArea.prototype._updateResultPane = function()
{
	if( !(this.m_sEditedModel && this.m_sEditedView) )
	{
		return;
	}
	this._resetTranslator();
	var sTranslatedHtml = caplin.i18n.Translator.getTranslator().translate(this.m_sEditedView,"html");
	var eTemplate = $( "<div>" + sTranslatedHtml + "</div>");

	var oHTMLResourceService = caplin.core.ServiceRegistry.getService("br.html-service");
	oHTMLResourceService.getHTMLTemplate = function( sTemplateId )
	{

		if(sTemplateId === "bogus-template-id"){
			var eElement = eTemplate.find(":first")[0];
		}else{
			var eElement = eTemplate.find( "#" + sTemplateId )[0];
		}

		return eElement;
	};

	try
	{
		eval(this.m_sEditedModel);
		var oPresenterComponent = new caplin.presenter.component.PresenterComponent("bogus-template-id", "novobank.example.DemoPresentationModel");
		this._showPresenterComponent(oPresenterComponent);

	}
	catch(e)
	{
		if( e instanceof TypeError )
		{
			var sMsg = e.message;
			//var sMsg = e.m_oMessageStack[0].message;
		}
		else if( e.message )
		{
			var sMsg = e.message;
		}
		else
		{
			var sMsg = e.toString();
		}
		this._showError( sMsg );
	}
};

live.examples.presenter.ui.CodeArea.prototype._showPresenterComponent = function( oPresenterComponent )
{
	this._hideError();
	oPresenterComponent.setFrame({});
	this.m_eResultPane.html( oPresenterComponent.getElement() );
	oPresenterComponent.onOpen(1000, 1000);
};

live.examples.presenter.ui.CodeArea.prototype._hideError = function()
{
	this.m_eErrorPaneContent.html("");
	this.m_eResultPane.removeClass('error');
	this.m_eModel.removeClass('error');
	this.m_eErrorCover.hide();
	this.resize();
};

live.examples.presenter.ui.CodeArea.prototype._showError = function( sMessage )
{
	this.m_eErrorPaneContent.html( sMessage );
	this.m_eResultPane.addClass('error');
	this.m_eModel.addClass('error');
	this.m_eErrorCover.show();
	this.resize();
};
