/**
 * Creates only style-related functionality...the behaviour remains untouched
 */
live.examples.presenter.Layout = function()
{
	this.m_eModel = $('#model');
	this.m_eCodeContent = $('#view_content');
	this.m_eView = $('#view');
	this.m_eCode = $('#code');
	this.m_eCodeTabArea = $('#code_tabs');
	this.m_eI18nCode = $('#i18n_editor_container');
	this.m_eI18nTab = $('#code_tabs [shows=\'i18n\']');
	this.m_eModelTab = $('#code_tabs [shows=\'model\']');
	this.m_eContent = $('#content');
	this.m_eToc = $('#toc > dl > dd');
	this.m_eMainInner = $('#mainInner');
	this.m_eTutorial = $('#tutorial');
	this.m_eTopicTitle = $("#title");

	this.m_eSplitter = $('#splitter');
	this.m_eTutorialContent = $('#tutorial_content');
	this.m_pTabs = $('#code_tabs .tab');
	this.m_eTocRoot = $('#toc');
	this.m_eChapterNumber = $('#chapter-number');
	this.m_eChapterNumberContent = $('#chapter-number h1');

	this.m_oCodeArea = new live.examples.presenter.ui.CodeArea();

	this.m_oDocument = $(document);
	this.m_oWindow = $(window);
	this.m_oJScrollPane = {};
	this.m_oTutorialJScrollPane = {};
	
	this.m_nTutorialWidth = this.m_eTutorial.width();

	this.bindOnResize();
	this.bindSplitter();
	this.bindTabs();
	this.addTocScroll();
	this.bindVerticalSlider();

	setTimeout(function(){$('#content').addClass('anim');}, 200);
	live.examples.presenter.EVENT_HUB.bind( "SelectedChanged", this.onSelectedChanged , this );
	live.examples.presenter.EVENT_HUB.bind( "TocToggled", this.onTocToggled , this );
};

live.examples.presenter.Layout.prototype.getTocContainer = function()
{
	return this.m_eToc;
};

live.examples.presenter.Layout.prototype.bindVerticalSlider = function()
{
	var oDragListener = new live.examples.presenter.DragListener( "#tutorial_slider" );
	var nMinTutorialWidth = parseInt(this.m_eTutorial.css('min-width'), 10);
	var nTutorialWidth;

	var fOnDragStart = function()
	{
		nTutorialWidth = this.m_eTutorial.width();
	};

	var fOnDrag = function(x)
	{
		var nNewWidth = nTutorialWidth + x;
		if (nNewWidth >= nMinTutorialWidth)
		{
			this.setTutorialWidth( nTutorialWidth + x );
			this.onResize();
		}
	};

	var fOnDragStop = function()
	{
		this.m_nTutorialWidth = this.m_eTutorial.width();
		this.refreshTutorialScroll();
	};
	
	oDragListener.bind( "dragStart", fOnDragStart, this );
	oDragListener.bind( "drag", fOnDrag, this );
	oDragListener.bind( "dragStop", fOnDragStop, this );
};

live.examples.presenter.Layout.prototype.bindSplitter = function()
{
	var oDragListener = new live.examples.presenter.DragListener( "#splitter" );
	var nModelHeight, nViewHeight;

	var fOnDragStart = function()
	{
		nModelHeight = this.m_eModel.height();
		nViewHeight = this.m_eView.height();
	};

	var fOnDrag = function(x,y)
	{
		this.m_eModel.height( nModelHeight + y );
		this.m_eView.height( nViewHeight - y );
		this.resizeCodeEditor();
	};

	oDragListener.bind( "dragStart", fOnDragStart, this );
	oDragListener.bind( "drag", fOnDrag, this );
	oDragListener.bind( "dragStop", this.resizeCodeEditor, this );

};

live.examples.presenter.Layout.prototype.setTutorialWidth = function( nWidth )
{
	this.m_eTutorial.width( nWidth );
	this.m_eMainInner.css({ "padding-left": nWidth });
	this.m_eChapterNumber.css({ "left": nWidth });
};

live.examples.presenter.Layout.prototype.onSelectedChanged = function( sPath, sKey, sText )
{
	this.setPath( sPath );
	this.setTopicTitle( sPath + " " + sText );
};

live.examples.presenter.Layout.prototype.setPath = function( sPath )
{
	this.m_eChapterNumberContent.html( sPath );
};

live.examples.presenter.Layout.prototype.setTopicTitle = function( sTopicTitle )
{
	this.m_eTopicTitle.html( sTopicTitle );
};

live.examples.presenter.Layout.prototype.bindOnResize = function()
{
	this.m_oWindow.resize( this.onResize.bind(this) );
	this.onResize();
};

live.examples.presenter.Layout.prototype.addTocScroll = function()
{
	this.m_eTocRoot.jScrollPane();
	this.m_oJScrollPane = this.m_eTocRoot.data("jsp");

//	this.m_eTutorialContent.jScrollPane();
//	this.m_oTutorialJScrollPane = this.m_eTutorialContent.data("jsp");

};

live.examples.presenter.Layout.prototype.refreshTocScroll = function()
{
	var mScrollPaneSettings = {
		animateScroll : true,
		animateEase : "easeOutBounce"
	};
	if( this.m_oJScrollPane && this.m_oJScrollPane.reinitialise )
	{
		this.m_oJScrollPane.reinitialise( mScrollPaneSettings );
	}
};

live.examples.presenter.Layout.prototype.scrollToTocItem = function(eTocItem)
{
	this.m_oJScrollPane.scrollToElement(eTocItem, false, false);
};

live.examples.presenter.Layout.prototype.showContent = function(mData)
{
	this.showTutorial( mData.summary );
	
	var nLevel = live.examples.presenter.HASH.split('/').length;
	if (mData.view && mData.model) { // Has live code
		this._showCodeArea();
		if (mData.i18n) {
			this._showI18nArea();
		}
		else {
			this._hideI18nArea();
			
			//show model tab
			if (this.m_eI18nTab.hasClass('active'))
			{
				this.m_pTabs.removeClass("active");
				this.m_eModelTab.addClass("active");
				this.m_eCodeTabArea.attr("class","").addClass( 'model' );
				this.m_oCodeArea.showPanel( 'model' );
			}
		}
		this._narrowTutorial();
		this.m_oCodeArea.update( mData.view, mData.model, mData.i18n );
	}
	else if (nLevel > 2) { // No live code & level 3 section = full screen tutorial
		this._hideCodeArea();
		this._widenTutorial();
	}
	else { // No live code & level 2 section = show chapter number
		this._narrowTutorial();
		this._hideCodeArea();
	}

	var eImg;
	eImg = this.m_eTutorial.find("img");
	//nTimeout = setTimeout(this.onResize.bind(this), 100); //if image already in cache "load" may not fire
	eImg.on("load", this.onResize.bind(this) );
	this.onResize();
};

live.examples.presenter.Layout.prototype._showCodeArea = function()
{
	this.m_eCode.css('opacity', 1);
	this.m_eTutorial.removeClass('top');
	this.m_eCodeTabArea.css('opacity', 1);
};

live.examples.presenter.Layout.prototype._hideCodeArea = function()
{
	this.m_eCode.css('opacity', 0);
	this.m_eTutorial.addClass('top');
	this.m_eCodeTabArea.css('opacity', 0);
};

live.examples.presenter.Layout.prototype._showI18nArea = function()
{
	this.m_eI18nCode.css('display', 'inline');
	this.m_eI18nTab.css('display', 'inline');
};

live.examples.presenter.Layout.prototype._hideI18nArea = function()
{
	this.m_eI18nCode.css('display', 'none');
	this.m_eI18nTab.css('display', 'none');
};

live.examples.presenter.Layout.prototype._narrowTutorial = function()
{
	$('#tutorial').css("width",this.m_nTutorialWidth);
}

live.examples.presenter.Layout.prototype._widenTutorial = function()
{
	$('#tutorial').css("width","100%");
}

live.examples.presenter.Layout.prototype.showTutorial = function( sHtml )
{
	this.m_sTutorialHtml = this._preprocessHtml( sHtml );

	this.updateTutorialArea();
};

live.examples.presenter.Layout.prototype._removeTutorialScrollPanes = function()
{
	// Remove existing jsp
	if( $('#tutorial_content').data("jsp") )
	{
		$('#tutorial_content').data("jsp").destroy();
	}
	$('#tutorial_content .codesample').each(function(){
		if( $(this).data("jsp") )
		{
			$(this).data("jsp").destroy();
		}
	});
};

live.examples.presenter.Layout.prototype._addTutorialScrollPanes = function()
{
	$('#tutorial_content .codesample').css("width","auto").each(function(){
		$(this).width( $(this).width() - 20 );
	})
	$('#tutorial_content .codesample').jScrollPane();
	$('#tutorial_content').jScrollPane();
	$('#tutorial_content').css({ visibility: "visible" });
};


live.examples.presenter.Layout.prototype.refreshTutorialScroll = function()
{
	this._removeTutorialScrollPanes();
	this._addTutorialScrollPanes();
};


live.examples.presenter.Layout.prototype.updateTutorialArea = function()
{
	/**
	 * THE FOLLOWING SECTION IS JUST PLAIN HORRIBLE,
	 * BUT SEEMED TO BE THE ONLY WAY TO GET jScrollPane
	 * TO WORK...
	 *
	 * WH
	 */
	/**
	 * The above were the original comments. I've now refactored it so it
	 * "looks" nicer, but it's still horrible we need to destroy scrollpanes first
	 * and that we need to add them after a timeout to allow rendering first
	 * - there must be some way to render into a domFragment with correct dimensions
	 *   and add the scrollpanes outside the document and then add it back. To investigate
	 *   later
	 * Paul G.
	 */
	this._removeTutorialScrollPanes();
	$('#tutorial_content').html("").html( this.m_sTutorialHtml );
	$('#tutorial_content').removeAttr('style').css({ visibility: "hidden" }).height( this.m_oWindow.height() - 132 );
	setTimeout(this._addTutorialScrollPanes.bind(this), 0); //allow rendering to occur first
};



live.examples.presenter.Layout.prototype.onTocToggled = function()
{
	/* currently no need to do anything here (css takes care of resizing) */
};


live.examples.presenter.Layout.prototype.resizeCodeEditor = function()
{
	/**
	 * Area available for the code_content div. CodeArea sets heights of internal elements based
	 * on calculations using the available height
	 */
	var nAvailableHeight = this.m_eModel.height() - this.m_eSplitter.outerHeight(true);

	this.m_oCodeArea.setHeight(nAvailableHeight);
};
/**
 * Sets the witdth of the content element
 */
live.examples.presenter.Layout.prototype.onResize = function()
{
	var nWindowHeight = this.m_oWindow.height(),
	    nContentWidth = this.m_eContent.innerWidth(), //available width for content
	    nContentHeight = nWindowHeight - this.m_eContent.position().top;

	/**
	 * Content
	 */
	this.m_eContent.height( nContentHeight );

	/**
	 * Tutorial Section
	 */
	this.m_eTutorialContent.height( nContentHeight - this.m_eTutorialContent.position().top );

	/**
	 * Code Section
	 */
	var nModelHeight = this.m_eModel.height();
	var nViewHeight = this.m_eView.height();
	var nCodeHeight = this.m_eCode.height();

	var nNewModelHeight = nCodeHeight * ( nModelHeight / ( nModelHeight + nViewHeight ) );
	var nNewViewHeight = nCodeHeight - nNewModelHeight;

	this.m_eModel.height( nNewModelHeight );
	this.m_eView.height( nNewViewHeight );
	this.resizeCodeEditor();

	this.refreshTocScroll();
	this.refreshTutorialScroll();
   //this.updateTutorialArea();
};


live.examples.presenter.Layout.prototype.bindTabs = function()
{
	var self = this;

	this.m_pTabs.click(function(e){

		e.preventDefault();

		self.m_pTabs.removeClass("active");
		$(this).addClass("active");

		var sSelected = $(this).attr("shows");
		self.m_eCodeTabArea.attr("class","").addClass( sSelected );
		self.m_oCodeArea.showPanel( sSelected );
	});
}

live.examples.presenter.Layout.prototype._preprocessHtml = function( sHtml )
{
	var eHtml = $("<div class='tutorial_content_inner'></div>");

	eHtml.html( sHtml );

	eHtml = live.examples.presenter.HtmlPreProcessors.process( eHtml );

	return eHtml;
};
