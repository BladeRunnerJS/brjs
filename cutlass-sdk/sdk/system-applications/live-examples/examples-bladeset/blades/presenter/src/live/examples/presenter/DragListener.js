

live.examples.presenter.DragListener = function( eElement, nButtonCode )
{
	live.examples.presenter.EventEmitter.call( this );

	this.m_eElement = $(eElement);
	this.m_oDocument = $(document);
	this.m_nButtonCode = nButtonCode || 0;

	this.m_nX = 0;
	this.m_nY = 0;

	this.m_nOriginalX = 0;
	this.m_nOriginalY = 0;

	this.m_eElement.mousedown( this.onMouseDown.bind( this ) );
};

live.examples.presenter.DragListener.prototype.onMouseDown = function( oEvent )
{
	if( oEvent.button !== this.m_nButtonCode )
	{
		return;
	}
	oEvent.preventDefault();

	this.m_nOriginalX = oEvent.clientX;
	this.m_nOriginalY = oEvent.clientY;

	// Namespace the events ".dragListener" so we can remove the correct handles
	this.m_oDocument.on( 'mousemove.dragging', this.onMouseMove.bind(this) );
	this.m_oDocument.one( 'mouseup.dragging', this.onMouseUp.bind(this) );

	this.emit( "dragStart", this.m_nOriginalX, this.m_nOriginalY );
};

live.examples.presenter.DragListener.prototype.onMouseMove = function( oEvent )
{
	this.m_nX = oEvent.clientX - this.m_nOriginalX;
	this.m_nY = oEvent.clientY - this.m_nOriginalY;

	this.emit( "drag", this.m_nX, this.m_nY );
};

live.examples.presenter.DragListener.prototype.onMouseUp = function( oEvent ) {
	this._endDrag( oEvent );
};

live.examples.presenter.DragListener.prototype._endDrag = function( oEvent )
{
	// using jQuerys event namespacing we can unbind all events namespaced as ".dragging"
	this.m_oDocument.off( ".dragging" );

	this.emit( "dragStop", this.m_nX, this.m_nY );
};
