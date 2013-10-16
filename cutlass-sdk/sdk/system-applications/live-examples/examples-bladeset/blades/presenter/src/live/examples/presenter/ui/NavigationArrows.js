live.examples.presenter.ui.NavigationArrows = function(oModel)
{

	this.m_oSelectedItem = null;

	this.m_oModel = oModel;
	this.m_mMapOfKeysToText = oModel.getMap();
	this.m_pListOfKeys = oModel.getListOfKeys();

	this.m_sShowTocClass = 'show_toc';
	this.m_sTocHoverText = "Table of Contents";

	this.m_eToolTip = $("#tooltip");
	this.m_eTocSwitch = $('.ctrl li[calls="toc"]');
	this.m_ePrevious = $("#controller .ctrl li[calls='back']");
	this.m_eNext = $("#controller .ctrl li[calls='fwd']");

	this.bindEvents();

	this.toggleToc();
};

live.examples.presenter.ui.NavigationArrows.prototype.bindEvents = function()
{
	$(document).hover( this.hoverToc.bind(this) )

	this.m_ePrevious.click( this.setPrevious.bind(this) );
	this.m_eNext.click( this.setNext.bind(this) );

	this.m_ePrevious.hover( this.hoverPrevious.bind(this) );
	this.m_eNext.hover( this.hoverNext.bind(this) );

	this.m_eTocSwitch.click( this.toggleToc.bind(this) );
	this.m_eTocSwitch.hover( this.hoverTocSwitch.bind(this) );
};

live.examples.presenter.ui.NavigationArrows.prototype.hoverToc = function()
{
	var self = this;
	$(document).keydown(function(e)
	{
	    if (e.keyCode == 37)
	    {
	    	self.setPrevious();
	    	return false;
	    }
	    else if(e.keyCode == 39)
	    {
	    	self.setNext();
	    	return false;
	    }
	 });
};

live.examples.presenter.ui.NavigationArrows.prototype.toggleToc = function()
{
	$('#content').toggleClass( this.m_sShowTocClass );
	live.examples.presenter.EVENT_HUB.emit("TocToggled");
};

live.examples.presenter.ui.NavigationArrows.prototype.hoverTocSwitch = function()
{
	this.m_eToolTip[0].className = "toc";
	this.m_eToolTip.text(this.m_sTocHoverText).toggle();
};

live.examples.presenter.ui.NavigationArrows.prototype.hoverPrevious = function()
{
	this.m_eToolTip[0].className = "prev";
	var sNewKey = this._getKeyFromOffset(-1);
	this.m_eToolTip.text(this._getToolTip(sNewKey)).toggle();
};

live.examples.presenter.ui.NavigationArrows.prototype.hoverNext = function()
{
	this.m_eToolTip[0].className = "next";
	var sNewKey = this._getKeyFromOffset(1);
	this.m_eToolTip.text(this._getToolTip(sNewKey)).toggle();
};

live.examples.presenter.ui.NavigationArrows.prototype.setPrevious = function()
{
	this.m_eToolTip.text(this._getToolTip(this._getKeyFromOffset(-2)));
	live.examples.presenter.EVENT_HUB.emit( "RequestTopic", this._getKeyFromOffset(-1) );
};

live.examples.presenter.ui.NavigationArrows.prototype.setNext = function()
{
	this.m_eToolTip.text(this._getToolTip(this._getKeyFromOffset(2)));
	live.examples.presenter.EVENT_HUB.emit( "RequestTopic", this._getKeyFromOffset(1) );
};

live.examples.presenter.ui.NavigationArrows.prototype._getToolTip = function( sKey )
{
	var oItem = this.m_mMapOfKeysToText[sKey];
	return oItem.path + " " + oItem.text;
};

live.examples.presenter.ui.NavigationArrows.prototype._getKeyFromOffset = function( nOffset )
{
	var sSelected = this.m_oModel.getSelected();
	return this._getKeyFromList(sSelected,nOffset);
};

live.examples.presenter.ui.NavigationArrows.prototype._getKeyFromList = function( sKey, nOffset )
{
	var nIndex = $.inArray( sKey , this.m_pListOfKeys );
	var nResultIndex;

	if( nIndex + nOffset < 0 )
	{
		nResultIndex = this.m_pListOfKeys.length - 1;
	}
	else if(  nIndex + nOffset > this.m_pListOfKeys.length - 1 )
	{
		nResultIndex = 0;
	}
	else
	{
		nResultIndex = nIndex + nOffset;
	}

	if( $.inArray( this.m_pListOfKeys[ nResultIndex ], live.examples.presenter.Config.ELEMENTS_WITHOUT_CONTENT ) !== -1 )
	{
		if( nOffset > 0 )
		{
			nResultIndex++;
		}
		else
		{
			if(nResultIndex-1 < 0)
				nResultIndex = this.m_pListOfKeys.length - 1;

			nResultIndex--;
		}
	}
	return this.m_pListOfKeys[ nResultIndex ];
}
