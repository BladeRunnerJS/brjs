/**
 * @fileoverview
 * Defines the emptycorp.a.grid.RightClickMenuDecorator class.
 */
caplin.namespace("emptycorp.a.grid");

caplin.include("caplin.grid.decorator.GridDecorator", true);
caplin.include("caplin.grid.GridViewListener", true);
caplin.include("caplin.dom.Utility");
caplin.include("caplin.dom.event.Event");
caplin.include("caplin.chart.menu.ContextMenu");

/**
 * Constructs a <code>emptycorp.a.grid.RightClickMenuDecorator</code> instance &mdash; end-users
 * will never need to do this themselves since grids are fully constructed based on their XML
 * definition files by the {@link caplin.grid.GridGenerator GridGenerator} class.
 * 
 * @class
 * The <code>RightClickMenuDecorator</code> class provides immediate visual feedback of a rows pending
 * removal until the server round-trip has completed, and the row is actually removed from the view.
 * The remove row decorator is not required for the
 * {@link caplin.element.handler.RemoveGridRowOnClickHandler} class to function correctly, but
 * merely improves the responsiveness of the UI during such an operation.
 * 
 * @implements caplin.grid.decorator.GridDecorator
 * @implements caplin.grid.GridViewListener
 * @constructor
 */
emptycorp.a.grid.RightClickMenuDecorator = function(mDecoratorConfig)
{
	this.m_oContextMenu = this._createContextMenu(mDecoratorConfig);
};
caplin.implement(emptycorp.a.grid.RightClickMenuDecorator, caplin.grid.decorator.GridDecorator);
caplin.implement(emptycorp.a.grid.RightClickMenuDecorator, caplin.grid.GridViewListener);

//************************ GridDecorator Interface Methods ************************

/**
 * @private
 * @see caplin.grid.decorator.GridDecorator#setGridView
 */
emptycorp.a.grid.RightClickMenuDecorator.prototype.setGridView = function(oGridView)
{
	this.m_oGridView = oGridView;
	oGridView.addGridViewListener(this);
	this._setupContextMenu();
};

//************************ GridDecorator Interface Methods ************************

/**
 * @private
 * @see caplin.grid.GridViewListener#onContainerHtmlRendered
 */
emptycorp.a.grid.RightClickMenuDecorator.prototype.onContainerHtmlRendered = function(oGridView)
{
	// Listen to right click event and fire _onRightClick
	var eElement = this.m_oGridView.getElement();
	this.m_nRightClickListenerId = caplin.dom.Utility.addEventListener(eElement, "mousedown", this._getRightClickHandler());
};

/**
 * @private
 * @see caplin.grid.GridViewListener#onClose
 */
emptycorp.a.grid.RightClickMenuDecorator.prototype.onClose = function()
{
	this.m_oContextMenu.finalize();
};



/**
 * @private
 * @see caplin.grid.GridViewListener#onRowStructureChanged
 */
emptycorp.a.grid.RightClickMenuDecorator.prototype.onRowStructureChanged = function(pIndicies)
{
	for(var i = 0, l = pIndicies.length; i < l; i++) 
	{
		var nRowIndex = pIndicies[i];
		this._makeRowClickable(this.m_oGridView.getRowElement(nRowIndex), nRowIndex);
	}
};

/**
 * @private
 * @param {Object} oRowElem
 * @param {Object} nRowIndex
 */
emptycorp.a.grid.RightClickMenuDecorator.prototype._makeRowClickable = function(oRowElem,nRowIndex)
{
	oRowElem.rowIndex = nRowIndex;
};

//************************ Private Methods ************************


/**
 * @private
 */
emptycorp.a.grid.RightClickMenuDecorator.prototype._getRightClickHandler = function() 
{
	var self = this;
	return function(oEvent) {
		self._onRightClick(oEvent);
	};
};

emptycorp.a.grid.RightClickMenuDecorator.prototype._getInstrumentFromSubject = function(sSubjectId, nRowIndex) {
	if(sSubjectId.match("^/FX/.*")) {
		return this.m_oGridView.getGridRowModel().getRowData(nRowIndex).InstrumentDescription;
	} else if(sSubjectId.match("^/FI/.*")) {
		return this.m_oGridView.getGridRowModel().getRowData(nRowIndex).Description;
	}
};

/**
 * @private
 */
emptycorp.a.grid.RightClickMenuDecorator.prototype._onRightClick = function(oEvent) 
{
	oEvent = caplin.dom.event.Event.getNormalizedEvent(oEvent);
		
	if(oEvent.button === caplin.dom.event.Event.MouseButtons.RIGHT)
	{
		this.m_oGridView.getGridRowModel();
		var pMousePosition = caplin.dom.Utility.getMousePosition(oEvent);
		var eRow = caplin.dom.Utility.getAncestorElementWithClass(oEvent.target, "row");
		var nColumnIndex = this.m_oGridView.getElementIndicies(oEvent.target.parentNode).columnIndex;
		var sColumnName = "";
		if (nColumnIndex)
		{
			sColumnName = this.m_oGridView.getGridColumnModel().getColumnByIndex(nColumnIndex).getPrimaryFieldName();
		}

		if (eRow)
		{
			var sInstrument;
			var nRowIndex = eRow.rowIndex;
			var sSubjectId = this.m_oGridView.getGridRowModel().getSubjectId(nRowIndex);
			var sInstrument = this._getInstrumentFromSubject(sSubjectId, nRowIndex);
			
			if (eRow) {
				this.m_oContextMenu.setEventObject({
					subject: sSubjectId, 
					instrument: sInstrument, 
					field: sColumnName 
				});
				this.m_oContextMenu.showAt(pMousePosition);			
			}
		}
	}
};

/**
 * @private
 */
emptycorp.a.grid.RightClickMenuDecorator.prototype._createContextMenu = function(oMenuConfig)
{
	//TODO Decorators ought to support nested child elements rather than just attributes being passed in as 
	//config. Once this is done the upcoming nasty parsing algorithm can be changed. Also needed to
	//be able to describe sub-menus in the decorator config.
	var mContextMenuConfig = {};
	var nSeparators = 1;
	
	for(var oConfigOption in oMenuConfig) {
		var nConfigOptionLength = oConfigOption.length;
		if(nConfigOptionLength >= 7 && oConfigOption.substr(nConfigOptionLength-7, 7) === "Caption")
		{
			var nMenuOptionIndexLength = nConfigOptionLength-7;
			var sMenuOptionIndex = oConfigOption.substr(0, nMenuOptionIndexLength);
			var mMenuItemConfig = {};
			for(var oOption in oMenuConfig) {
				var nOptionLength = oOption.length;
				if(nOptionLength >= nMenuOptionIndexLength && oOption.substr(0, nMenuOptionIndexLength) === sMenuOptionIndex){
					var sOptionValue = oMenuConfig[oOption];
					if(nOptionLength >= 7 && oOption.substr(nOptionLength-7, 7) === "Caption")
					{
						mMenuItemConfig.text = sOptionValue;
					}
					else if(nOptionLength >= 8 && oOption.substr(nOptionLength-8, 8) === "Disabled")
					{
						mMenuItemConfig.disabled = sOptionValue;
					}
					else if(nOptionLength >= 9 && oOption.substr(nOptionLength-9, 9) === "Separator")
					{
						mMenuItemConfig.separate = true;
					}
					else if(nOptionLength >= 5 && oOption.substr(nOptionLength-5, 5) === "Event")
					{
						var fEvent = caplin.core.Utility.getFunctionPointerFromMethod(this, sOptionValue);
						try
						{
							mMenuItemConfig.event = fEvent.call();
						}
						catch(e)
						{
							throw new caplin.core.Exception("Menu option callback function "+sOptionValue+ " not defined in Decorator.")
						}
					}
				}
			}
			mContextMenuConfig[sMenuOptionIndex] = mMenuItemConfig;
		}
	}

	return new caplin.chart.menu.ContextMenu(mContextMenuConfig);
};

/**
 * @private
 */
emptycorp.a.grid.RightClickMenuDecorator.prototype._setupContextMenu = function()
{		
	this.m_oContextMenu.setComponent(this.m_oGridView);
}; 

/********************************************************************
*                         Menu Selection Event Callbacks            *
********************************************************************/

emptycorp.a.grid.RightClickMenuDecorator.prototype.newAlertForm = function()
{
	var self = this;
	return fCallback = function(e) 
	{
		var mContextData = self.m_oContextMenu.getEventObject();
		self.m_oContextMenu.hide();
		try{
			var alertService = caplin.core.ServiceRegistry.getService("caplin.alerts.AlertService");
			alertService.openAlertCreateDialog(mContextData.instrument, mContextData.field, mContextData.subject);
		}catch(e){
			throw new caplin.core.Exception("Error:"+e+" Check Alert blade has been loaded.");
		};

	};
};

emptycorp.a.grid.RightClickMenuDecorator.prototype.insertBenchmark = function()
{	
	var self = this;
	var fCallback = function(e) {
		self.m_oContextMenu._publishEvent("insertBenchmark");
	};
	return fCallback;
};

emptycorp.a.grid.RightClickMenuDecorator.prototype.insertTimeseries = function()
{
	var self = this;
	var fCallback = function(e) {
		self.m_oContextMenu._publishEvent("insertTimeseries");
	};
	return fCallback;
};












