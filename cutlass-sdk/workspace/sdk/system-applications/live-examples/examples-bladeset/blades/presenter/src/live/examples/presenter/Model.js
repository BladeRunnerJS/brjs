live.examples.presenter.Model = function()
{
	this.m_oData = {};
	this.m_mContent = {};
	this.m_sSelectedKey = null;


	var self = this;
	/**
	 * Request the Table of Contents
	 */
	$.get( live.examples.presenter.Config.TOC_URL, function( sXml ){self._onTocResponse( sXml );}, "xml");
};



/**
 * Returns all sections that are direct children
 * of the toc root tag
 *
 * returns an array of objects like this
 *
 * {
			cookbook: {...}
			introduction: {
				hasChildren: true,
				hasSummary: true,
				text: "Introduction [C.3]",
				summary: "blabla"
			},
			tutorial: {...}
		}
 */
live.examples.presenter.Model.prototype.getTocTopLevel = function()
{
	return this._processSections( "toc > section" );
};

/**
 * Returns all sections that are direct children of the section
 * with attribute key = sSectionKey
 *
 * @param {STRING} sSectionKey the key attribute  of the parent section
 */
live.examples.presenter.Model.prototype.getSubSections = function( sSectionKey )
{
	return this._processSections( "section[key='" + sSectionKey + "'] > section" );
};

/**
 * Loads the content ( an object that has the following properties )
 *
 * {
 *		view: "html string",
 *		model: "js string",
 *		summary: "html string"
 * }
 *
 * @param {STRING} sSectionKey the key attribute  of the parent section tag
 * @param {FUNCTION} fCallback a function that will be called once all requests
 * returned, whether successful or not
 */
live.examples.presenter.Model.prototype.loadContent = function( sSectionKey, fCallback )
{
	var sUrl = this._getContentUrl( sSectionKey );
	var mResult = {};

	this._loadFile( sUrl + "/summary.html", mResult, "summary", fCallback, sSectionKey );

	if( this.hasContent( sSectionKey ) )
	{
		this._loadFile( sUrl + "/model.js", mResult, "model", fCallback, sSectionKey );
		this._loadFile( sUrl + "/view.html", mResult, "view", fCallback, sSectionKey );
		if ( this.hasI18nContent( sSectionKey ) ) {
			this._loadFile( sUrl + "/i18n.properties", mResult, "i18n", fCallback, sSectionKey );
		}
	}

};

live.examples.presenter.Model.prototype.setSelected = function( sKey )
{
	if(sKey.match("/"))
	{
		var pKeys = sKey.split("/");
		sKey = pKeys[pKeys.length-1];
	}

	var eSelectedToc = this.m_oData.find("section[key='"+sKey+"']");
	this.m_sSelectedKey = sKey;

	if(eSelectedToc.attr("path") && eSelectedToc.attr("text"))
	{
		live.examples.presenter.EVENT_HUB.emit( "SelectedChanged",  eSelectedToc.attr("path") , sKey, eSelectedToc.attr("text"));
	}
};

live.examples.presenter.Model.prototype.getSelected = function()
{
	return this.m_sSelectedKey;
};

/**
 * Filters out the content in the model on an input and returns the new model
 *
 * @param {STRING} sTextFilter, the filter to apply
 */
live.examples.presenter.Model.prototype.filter = function(sTextFilter)
{
	var sSearchXPath = "section[text*='" + sTextFilter + "'], section summary:contains('" + sTextFilter + "')";
	var pResult = this.m_oData.find( sSearchXPath );
};

live.examples.presenter.Model.prototype.getParentPathKeys = function( sPath )
{
	return this._getParentPathKeys( sPath, this.m_oData, [] );
};

live.examples.presenter.Model.prototype.getMap = function()
{
	var mKeys = {};
	var pSections = this.m_oData.find("section");
	pSections.each(function()
			{
		var sKey = $(this).attr("key");
		var sPath = $(this).attr("path");
		var sText = $(this).attr("text");
		mKeys[sKey] = {path:sPath, text:sText};
			});
	return mKeys;
};

live.examples.presenter.Model.prototype.getListOfKeys = function()
{
	var pKeys = [];
	var pSections = this.m_oData.find("section");
	pSections.each(function()
			{
		pKeys.push($(this).attr("key"));
			});
	return pKeys;
};

live.examples.presenter.Model.prototype.getPath = function( sSectionKey )
{
	var pParents = this.m_oData.find( "section[key='" + sSectionKey + "']" ).parents( "section" );
	var pUrl = [];

	pParents.each(function(){
		pUrl.push( $(this).attr("key") );
	});

	pUrl = pUrl.reverse();
	pUrl.push( sSectionKey );

	var sUrl = pUrl.join("/");

	return sUrl;
}

live.examples.presenter.Model.prototype.hasContent = function( sKey )
{
	return !this.m_oData.find("section[key='" + sKey + "']").attr("nocontent");
};

live.examples.presenter.Model.prototype.hasI18nContent = function( sKey )
{
	return !!this.m_oData.find("section[key='" + sKey + "']").attr("localised");
};

/**
 * PRIVATE METHODS
 */
live.examples.presenter.Model.prototype._addNumericPaths = function()
{
	var fAddNext = function( eParent, sPath )
	{
		var pElements = eParent.find("> section");

		for( var i = 0, j=1; i < pElements.length; i++, j++)
		{
			var sNewPath = (sPath) ? sPath + "." + j : j;

			$( pElements[i] ).attr('path', sNewPath );

			if( $( pElements[i] ).find("> section").length !== 0 )
			{
				fAddNext( $(pElements[i]), sNewPath );
			}
		}

	};

	fAddNext( this.m_oData.find("toc"), "" );
};

live.examples.presenter.Model.prototype._onTocResponse = function( sXml )
{
	this.m_oData = $( sXml );
	this._addNumericPaths();
	live.examples.presenter.EVENT_HUB.emit( "ModelReady" );
};

live.examples.presenter.Model.prototype._loadFile = function( sUrl ,mResult, sKey, fCallback, sSectionKey )
{
	var fCheckDone = function()
	{
		if(mResult.model !== undefined &&
				mResult.i18n !== undefined &&
				mResult.view !== undefined &&
				mResult.summary !== undefined) {
			fCallback( mResult );
		}
		else if(mResult.summary !== undefined &&
					!this.hasContent(sSectionKey)) {
			mResult.model = null;
			mResult.i18n = null;
			mResult.view = null;
			fCallback( mResult );
		}
		else if(mResult.summary !== undefined &&
					mResult.model !== undefined &&
					mResult.view !== undefined && 
					!this.hasI18nContent(sSectionKey)) {
			mResult.i18n = null;
			fCallback( mResult );
		}
	};

	var fOnSuccess = function( sResult )
	{
		mResult[ sKey ] = sResult;
	};

	var fOnError = function()
	{
		mResult[ sKey ] = null;
	}

	$.ajax({
		dataType: "text",
		url:sUrl,
		success: fOnSuccess,
		error: fOnError,
		complete: fCheckDone.bind(this)
	});
};

live.examples.presenter.Model.prototype._getContentUrl = function( sSectionKey )
{
	return live.examples.presenter.Config.CONTENT_BASE_URL + this.getPath( sSectionKey );
};

live.examples.presenter.Model.prototype._processSections = function( sPath )
{
	var pSections = this.m_oData.find( sPath );

	if( pSections.length === 0 )
	{
		return null;
	}
	else
	{
		var mReturn = {};

		pSections.each(function(){

			var sKey =  $(this).attr("key");

			mReturn[ sKey ] = {};
			mReturn[ sKey ].text = $(this).attr("text");
			mReturn[ sKey ].hasChildren = !!$(this).find("section").length;
			mReturn[ sKey ].path = $(this).attr("path");
			mReturn[ sKey ].hasContent = !$(this).attr('nocontent');

			var eSummary = $(this).find("> summary");
			mReturn[ sKey ].hasSummary = !!eSummary.length;

			if( eSummary.length === 1 )
			{
				mReturn[ sKey ].summary = eSummary.text();
			}

		});

		return mReturn;
	}
};


live.examples.presenter.Model.prototype._getParentPathKeys = function( sFinalPath, eElement, pPaths )
{
	var self = this;
	var pSections = eElement.find("section");

	for(var i=0; i < pSections.length; i++)
	{
		var sPath = $(pSections[i]).attr("path");
		if(sFinalPath === sPath)
		{
			pPaths.push($(pSections[i]).attr("key"));
			return pPaths;
		}
		if(sFinalPath.indexOf(sPath) == 0 )
		{
			pPaths.push($(pSections[i]).attr("key"));
			return self._getParentPathKeys(sFinalPath, $(pSections[i]), pPaths);
		}
	}
};
