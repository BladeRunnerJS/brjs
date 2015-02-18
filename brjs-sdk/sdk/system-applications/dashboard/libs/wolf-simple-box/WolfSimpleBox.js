WolfSimpleBox = function()
{
	/**
	* Make this a singleton
	*/
	if( WolfSimpleBox._instance !== null ) return WolfSimpleBox._instance;

	this.m_eElement = this._createElement();
	this.m_eContainer = this.m_eElement.find( ".wsb_container .wsb_inner" );
	this.m_eBackground = this.m_eElement.find( "td.wsb" );
	this.m_eCloseButton = this.m_eElement.find( ".wsb_close" );
	this.m_bClosable = true;
	this.m_bHasContent = false;
	this.m_bHidden = true;
	this.m_pOnCloseFunctions = [];
	this._appendElementToDom();
	this._bindEvents();
	this.m_eContainer.click( this._preventClickPropagation );
	this.m_fCheckEscapePress = this._checkEscapePress.bind( this );
	WolfSimpleBox._instance = this;
};


/**
* Sets the content of wolfsimplebox
* @param vContent STRING or ELEMENT
*/
WolfSimpleBox.prototype.setContent = function( vContent )
{
	this.m_eContainer.html( vContent );
	this.m_bHasContent = true;
};

WolfSimpleBox.prototype.hasContent = function()
{
	return this.m_bHasContent;
};

WolfSimpleBox.prototype.setHasBackground = function( bHasBackground )
{
	var eContainer = this.m_eContainer.parent();

	if( bHasBackground )
	{
		eContainer.removeClass( "noBg" );
	}
	else
	{
		eContainer.addClass( "noBg" );
	}
};

WolfSimpleBox.prototype.callOnClose = function( fOnClose )
{
	this.m_pOnCloseFunctions.push( fOnClose );
};

WolfSimpleBox.prototype.show = function()
{
	if( this.m_bHidden === true )
	{
		this.m_bHidden = false;
		this.m_eElement.addClass( "show" );
		
		if( $.browser.msie && $.browser.versionNumber < 9 )
		{
			this.m_eElement.css( "display", "block" );
		}
		$(window).on( "keyup", this.m_fCheckEscapePress );
		setTimeout( function(){ this.m_eElement.addClass( "show fadeIn" ); }.bind(this), 2 );
	}
};

WolfSimpleBox.prototype.hide = function()
{
	if( this.m_bHidden === false )
	{
		this.m_bHidden = true;
		this.m_eElement.removeClass( "show" );
		setTimeout( function(){ this.m_eElement.removeClass( "fadeIn" ); }.bind(this), 2 );
		$(window).off( "keyup", this.m_fCheckEscapePress );

		for( var i = 0; i < this.m_pOnCloseFunctions.length; i++ )
		{
			this.m_pOnCloseFunctions[ i ]();
		}
	}
};

WolfSimpleBox.prototype.setClosable = function( bClosable )
{
	this.m_bClosable = bClosable;

	if( bClosable === true )
	{
		this.m_eCloseButton.show();
	}
	else
	{
		this.m_eCloseButton.hide();
	}
};
/*************************************
* PRIVATE METHODS					 *
*************************************/
WolfSimpleBox._instance = null;

WolfSimpleBox.prototype._bindEvents = function()
{
	this.m_eCloseButton.click( this._onCloseRequest.bind( this ) );
	this.m_eBackground.click( this._onCloseRequest.bind( this ) );
};

WolfSimpleBox.prototype._onCloseRequest = function()
{
	if( this.m_bClosable === true )
	{
		this.hide();
	}
};

WolfSimpleBox.prototype._checkEscapePress = function( oEvent )
{
	if( oEvent.keyCode === 27 )
	{
		this._onCloseRequest();
	}
};

WolfSimpleBox.prototype._appendElementToDom = function()
{
	$("body").prepend( this.m_eElement );
};

WolfSimpleBox.prototype._createElement = function()
{
	var pHtml =
	[
		'<table id="wsb">',
			'<tr>',
				'<td class="wsb">',
					'<div class="wsb_container">',
						'<div class="wsb_inner"></div>',
						'<div class="wsb_close"></div>',
					'</div>',
				'</td>',
			'</tr>',
		'</table>'
	];

	return $( pHtml.join("") );
};

WolfSimpleBox.prototype._preventClickPropagation = function( oEvent )
{
	if( oEvent.stopPropagation )  oEvent.stopPropagation();
	if( oEvent.cancelBubble )  oEvent.cancelBubble();
};
