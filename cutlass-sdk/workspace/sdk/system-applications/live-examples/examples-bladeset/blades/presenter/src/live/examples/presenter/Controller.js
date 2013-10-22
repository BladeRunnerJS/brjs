live.examples.presenter.Controller = function( oModel, oView )
{
	this.m_mToc = {};
	this.m_oModel = oModel;
	this.m_oView = oView;
	this.m_oSelectedTocItem = null;

	live.examples.presenter.EVENT_HUB.bind( "ModelReady", this.start , this );
	live.examples.presenter.EVENT_HUB.bind( "RequestTopic", this.loadContent , this );
	live.examples.presenter.EVENT_HUB.bind( "SelectedChanged", this.onSelectedChanged , this );
};

live.examples.presenter.Controller.prototype.start = function()
{
	this.createToc();
};

live.examples.presenter.Controller.prototype.registerTocItem = function( sKey, oItem )
{
	this.m_mToc[ sKey ] = oItem;
};

live.examples.presenter.Controller.prototype.getTocItem = function( sKey )
{
	return this.m_mToc[ sKey ];
};

live.examples.presenter.Controller.prototype.createToc = function()
{
	var mToc = this.m_oModel.getTocTopLevel();
	var eToc = this.m_oView.getTocContainer();

	this.m_oNavigationArrow = new live.examples.presenter.ui.NavigationArrows(this.m_oModel);

	for( sKey in mToc )
	{
		var oTocItem = new live.examples.presenter.ui.TocItem( sKey, mToc[ sKey ], this.m_oModel, this );
		eToc.append(oTocItem.getElement());
		this.registerTocItem( sKey, oTocItem );
	}


	this.refresh();
};

live.examples.presenter.Controller.prototype.refresh = function()
{
	this.m_oView.refreshTocScroll();
};

live.examples.presenter.Controller.prototype.onSelectedChanged = function( sPath, sKey, sText )
{
	if(this.getTocItem( sKey ))
	{
		this.setSelected(sKey);
	}
	else if(sPath)
	{
		this.expandTillTocItem(sPath);
		this.setSelected(sKey);
	}
};

live.examples.presenter.Controller.prototype.expandTillTocItem = function( sPath )
{
	var pPathKeys = this.m_oModel.getParentPathKeys(sPath);
	for( var i=0; i < pPathKeys.length; i++)
	{
		var oTocItem = this.getTocItem( pPathKeys[i] );
		if(oTocItem.isCollapsed())
		{
			oTocItem.toggleChildren();
		}
	}
};

live.examples.presenter.Controller.prototype.setSelected = function( sKey )
{
	if( this.m_oSelectedTocItem )
	{
		this.m_oSelectedTocItem.deselect();
	}

	this.getTocItem( sKey ).select();
	this.m_oSelectedTocItem = this.getTocItem( sKey );
	this.m_oView.scrollToTocItem(this.m_oSelectedTocItem.getElement());
};

live.examples.presenter.Controller.prototype.loadContent = function(sKey)
{
	var fOnLoad = function(mData)
	{
		this.m_oModel.setSelected(sKey);
		var sHash = this.m_oModel.getPath(sKey);
		live.examples.presenter.HASH = sHash;
		document.location.hash = sHash;

		this.m_oView.showContent(mData);
	}

	if( $.inArray( sKey, live.examples.presenter.Config.ELEMENTS_WITHOUT_CONTENT ) === -1 )
	{
		this.m_oModel.loadContent(sKey, fOnLoad.bind(this) );
	}

};
