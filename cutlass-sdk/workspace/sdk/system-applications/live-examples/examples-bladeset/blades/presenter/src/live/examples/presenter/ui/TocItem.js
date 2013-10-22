live.examples.presenter.ui.TocItem = function( sKey, mData, oModel, oController )
{
	this.m_oData = mData;
	this.m_oModel = oModel;

	this.m_oController = oController;
	this.m_sKey = sKey;
	this.m_pChildren = [];
	this.m_sPath = "";

	this.m_eElement = $('<dl><dt><i></i><span class="number"></span> <b></b> <div class="desc"></div></dt><dd class="content collapsed"></dd></dl>');

	this.m_ePath = this.m_eElement.find("dt span.number");
	this.m_eDescription = this.m_eElement.find("dt div.desc");
	this.m_eTitle = this.m_eElement.find("dt b");
	this.m_eContent = this.m_eElement.find("dd.content");
	this.m_eArrow = this.m_eElement.find("i:first");
	this.setData( mData );
	this.m_eElement.find("dt").click( this.onClick.bind(this) );
};

live.examples.presenter.ui.TocItem.prototype.getPath = function()
{
	 return this.m_sPath;
};

live.examples.presenter.ui.TocItem.prototype.setData = function( mData )
{
	this.m_mData = mData;
	this.m_sPath = mData.path;
	this.m_ePath.html( mData.path );

	if( mData.text.length > 21 && this.getLevel() === 3 )
	{

		var sTitle = mData.text.substr(0, 21) + "&#133;";
	}
	else
	{
		var sTitle = mData.text;
	}

	this.m_eTitle.html( sTitle );

	if( !this.m_mData.hasChildren )
	{
		this.hideArrow();
	}
	this.setLevelClass();

	if( mData.hasSummary )
	{
		this.m_eDescription.html( mData.summary );
	}
	else if( !this.isTopLevel() )
	{
		this.m_eDescription.html( live.examples.presenter.Config.DEFAULT_SUMMARY_TEXT );
	}
};

live.examples.presenter.ui.TocItem.prototype.onClick = function( e )
{
	if( e && e.stopPropagation )
	{
		e.stopPropagation();
	}

	if ( this.m_oData.hasChildren )
	{
		this.toggleChildren();
	}

	this.m_oController.setSelected( this.m_sKey );

	if( !this.isTopLevel() )
	{
		this.m_oController.loadContent( this.m_sKey );
	}

	this.m_oController.refresh();


};

live.examples.presenter.ui.TocItem.prototype.hideArrow = function()
{
	this.m_eArrow.hide();
};

live.examples.presenter.ui.TocItem.prototype.setLevelClass = function()
{
	var sLevelClass = "level_" + this.getLevel();
	this.m_eElement.addClass( sLevelClass );
};

live.examples.presenter.ui.TocItem.prototype.select = function()
{
	if( !this.isTopLevel() )
	{
		this.m_eElement.addClass('selected');
	}
};

live.examples.presenter.ui.TocItem.prototype.deselect = function()
{
	this.m_eElement.removeClass('selected');
};

live.examples.presenter.ui.TocItem.prototype.getElement = function()
{
	return this.m_eElement;
};

live.examples.presenter.ui.TocItem.prototype.toggleChildren = function()
{

	if (this.m_pChildren.length === 0)
	{

		//load children
		var mData = this.m_oModel.getSubSections(this.m_sKey);

		for( var sKey in mData )
		{
			var oChild = new live.examples.presenter.ui.TocItem( sKey, mData[ sKey ], this.m_oModel, this.m_oController );
			this.m_eContent.append( oChild.getElement() );
			this.m_pChildren.push( oChild );
			this.m_oController.registerTocItem( sKey, oChild );
		}

		this.m_oController.refresh();
	}
	this.m_eElement.toggleClass("extended");
	this.m_eContent.toggleClass("collapsed");
};

live.examples.presenter.ui.TocItem.prototype.isCollapsed = function()
{
	return !this.m_eElement.hasClass("extended");
};

live.examples.presenter.ui.TocItem.prototype.getLevel = function()
{
	return this.m_sPath.split(".").length;
};

live.examples.presenter.ui.TocItem.prototype.getKey = function()
{
	return this.m_sKey;
}

live.examples.presenter.ui.TocItem.prototype.isTopLevel = function()
{
	return ( this.m_sPath.indexOf(".") === -1 );
};