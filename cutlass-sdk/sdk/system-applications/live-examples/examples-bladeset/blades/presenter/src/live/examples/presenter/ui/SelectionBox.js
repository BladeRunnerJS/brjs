live.examples.presenter.ui.SelectionBox = function( oModel, oController )
{
	this.m_oModel = oModel;

	this.m_oController = oController;

	/**
	 * DOM elements
	 */
	this.m_eElement = $('<div class="search_input"></div>');
	this.m_eInput = $('<input type="text" autocomplete="off" />');
	
	/**
	 * Event related properties
	 */
	this.bInputWasFocused = false;
	this.m_eElement.append( this.m_eInput );

	/**
	 * Init
	 */
	this._bindEvents();	
};

live.examples.presenter.ui.SelectionBox.prototype.getElement = function()
{
	return this.m_eElement;
};

live.examples.presenter.ui.SelectionBox.prototype._bindEvents = function()
{
	var self = this;
	this.m_eElement.click(function()
	{
	});
	this.m_eElement.keypress(function(e)
	{
		self.m_oModel.filter(self.m_eInput.val());
	});
};