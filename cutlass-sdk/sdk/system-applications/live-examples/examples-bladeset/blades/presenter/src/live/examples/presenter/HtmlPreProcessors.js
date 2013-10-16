/**
 * A set of static methods that will be called
 * every time the tutorial text section is updated.
 *
 * Each function receives an html element modifies and returns it
 */
live.examples.presenter.HtmlPreProcessors = {
	/**
	 * {Object} iterable Anything attached to this is iterated.
	 *                   anything else must be manually called.
	 */
  iterable: {}
};

/**
 * Iterates the iterable and processes the html element
 */
live.examples.presenter.HtmlPreProcessors.process = function( eHtml )
{
	for( var sPreprocessor in this.iterable )
	{
		eHtml = this.iterable[ sPreprocessor ]( eHtml );
	}

	return eHtml;
};


/**
 * Rewrites the image Urls to be relative to live.examples.presenter.Config.BASE_URL
 */
live.examples.presenter.HtmlPreProcessors.iterable.rewriteImageUrls = function( eHtml )
{
	/* Not doing this as it was causing 2 requests to be made. 
	 * Using correct src in html instead of postprocessing.
	 
	 
	eHtml.find('img').each(function(){

		var sOriginalUrl =  $(this).attr('src');

		var sNewUrl = live.examples.presenter.Config.BASE_URL + sOriginalUrl;
		$(this).attr('src', sNewUrl);

	});
	*/

	return eHtml;
};

/**
 * Loads internal links
 */
live.examples.presenter.HtmlPreProcessors.iterable.enableInternalLinks = function( eHtml )
{
	eHtml.find('a').click(function(e){

		var sUrl = $(this).attr('href');

		if( sUrl[0] === "#" )
		{
			e.preventDefault();
			live.examples.presenter.EVENT_HUB.emit( "RequestTopic", sUrl.substr(1) );
		}

	});

	return eHtml;
};
/**
 * Removes all styling related attributes from image tags
 */
live.examples.presenter.HtmlPreProcessors.iterable.removeImageStyles = function( eHtml )
{
	eHtml.find('img').removeAttr("style height width");

	return eHtml;
};

/**
 * @private
 */
live.examples.presenter.HtmlPreProcessors._detectCodeMimeType = function( sText )
{
	var
		sHtml = "text/html",
		sJS = "text/javascript",
		sCss = "text/css";

	// trim whitespace on each line to simplify the other regexes
	sText = sText.replace( /(^\s+|\s+(!\n)$)/gm ,"" );

	if (sText.match( /<.*>$/m ))
	{
		return sHtml;
	}

	if (sText.match( /(function|var|novobank\.|caplin\.)/ ))
	{
		return sJS;
	}

	if (sText.match( /^\}$/m ))
	{
		return sCss; //if no other matches but we end in "}" assume CSS
	}

	if (sText.match( /;$/m ))
	{
		return sJS; //if no other matches but we have semicolons before linebreaks
	}

	return "text/plain";

};

/**
 * Adds code mirrors' syntax highlighting to all pre tags without making them
 * editable
 */
live.examples.presenter.HtmlPreProcessors.iterable.addCodeMirrors = function( eHtml )
{
	eHtml.find('pre').each(function(){

		var
			sMimeType,
			sCode = $(this).html();
		sCode = sCode.replace( /\&lt\;/g, "<").replace( /\&gt\;/g, ">");

		sMimeType = live.examples.presenter.HtmlPreProcessors._detectCodeMimeType( sCode );
		switch (sMimeType) {
			case "text/javascript":
				sCode = js_beautify( sCode );
				break;
			case "text/html":
				sCode = style_html( sCode );
				break;
			case "text/css":
				sCode = css_beautify( sCode );
				break;
		}

		$(this).wrap('<div class="cm-s-default codesample"></div>');
		CodeMirror.runMode( sCode, sMimeType, this );

	});

	return eHtml;
};


/**
 * Adds Icons to warning and info paragraphs
 */
live.examples.presenter.HtmlPreProcessors.iterable.addIcons = function( eHtml )
{
	eHtml.find('p.warning').prepend("<div class='icon'>!</div>");
	eHtml.find('p.info').prepend("<div class='icon'>=</div>");

	return eHtml;
};
