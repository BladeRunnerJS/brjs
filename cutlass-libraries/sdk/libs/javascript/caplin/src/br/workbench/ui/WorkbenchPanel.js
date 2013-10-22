br.thirdparty("jquery");

/**
 * @class
 * A <code>WorkbenchPanel</code> is the main container for displaying components
 * within a workbench. Workbench panels can be added to a {@link br.workbench.ui.Workbench},
 * either to the left or right side of the screen.
 * 
 * @param String sOrientation Either "left" or "right"
 * @param int nWidth The width of the WorkbenchPanel in Pixels
 * @param boolean bXResizable If True, the panel will be resizable.
 * 
 * @constructor
 */
br.workbench.ui.WorkbenchPanel = function(sOrientation, nWidth, bXResizable)
{
	bXResizable = (bXResizable || true);
	this.m_sOrientation = sOrientation;
	this.m_nWidth = nWidth;
	this.m_eElement = jQuery('<div class="workbench-panel"></div>')
	
	this.m_eComponentContainer = document.createElement('ul');
	this.m_eComponentContainer.id = 'workbench-panel-' + br.workbench.ui.WorkbenchPanel.ID++;
	
	jQuery(this.m_eComponentContainer).sortable({
		placeholder: "sortable-placeholder",
		handle: '.header'
	});
	
	this.m_eElement.append(this.m_eComponentContainer);

	jQuery('body').append(this.m_eElement);

	this.setWidth(nWidth);
	this._bindYResize();

	if (bXResizable !== false)
	{
		this._bindXResize();
	}
};

/**
 * @private
 */
br.workbench.ui.WorkbenchPanel.ID = 0;

/**
 * Returns the id of the element that represents the container which components 
 * will be added to within this <code>WorkbenchPanel</code>
 * 
 * @type String
 * @returns the container id
 */
br.workbench.ui.WorkbenchPanel.prototype.getComponentContainerId = function()
{
	return this.m_eComponentContainer.id;
};

/**
 * Adds a {@link cpalin.workbench.ui.WorkbenchComponent} to this panel. 
 * 
 * @param {br.workbench.ui.WorkbenchComponent} oWorkbenchComponent The component to add.
 * @param {String} sTitle The title to display for the component.
 * @param {boolean} bCollapsed if True, the initial state of the component will be collapsed.
 */
br.workbench.ui.WorkbenchPanel.prototype.add = function(oWorkbenchComponent, sTitle, bCollapsed)
{
	var ePanelElement = this.getElement()[0];
	var eWrapper = document.createElement("li");
	eWrapper.className = 'workbench-component';
	var eHeader = document.createElement('div');
	eHeader.innerHTML = '<div class="arrow"></div><div class="title">' + sTitle + '</div>';
	eHeader.className = 'header';
	
	eWrapper.appendChild(eHeader);
	var eContainer = document.createElement('div');
	eContainer.className = 'container';
	eContainer.appendChild(oWorkbenchComponent.getElement());
	eWrapper.appendChild(eContainer);
	
	var ojQueryHeader = jQuery(eHeader);
	
	if (bCollapsed)
	{
		ojQueryHeader.next().hide();
		jQuery(eWrapper).addClass('collapsed');
	}
	
	ojQueryHeader.click(function() {
		ojQueryHeader.next().slideToggle();
		jQuery(eWrapper).toggleClass('collapsed');
		return false;
	});
	
	this.m_eComponentContainer.appendChild(eWrapper);
	
	if (oWorkbenchComponent.render)
	{
		oWorkbenchComponent.render(this.m_eComponentContainer);
	}
	return this;
};

/**
 * Sets the width of this panel.
 * 
 * @param {int} nWidth The width of the WorkbenchPanel in Pixels
 *
 */
br.workbench.ui.WorkbenchPanel.prototype.setWidth = function(nWidth)
{
	this.m_nWidth = nWidth;

	this.m_eElement.css("width", nWidth);

	if (this.m_sOrientation === 'left')
	{
		this.m_eElement.css("left", 0);
	}
	else
	{
		var self = this;
		this.m_eElement.css({
			"left":"100%",
			"margin-left": self.getOuterWidth() * -1
		});
	}
};

/**
 * Returns the outer width of this workbench panel.
 * 
 * @type int
 * @returns The total width of the WorkbenchPanel including padding and borders in pixels
 */
br.workbench.ui.WorkbenchPanel.prototype.getOuterWidth = function()
{
	var pWidthAspects = [
		'padding-left',
		'padding-right',
		'border-left-width',
		'border-right-width'
	];
	var nTotalWidth = 0;

	for (var i = 0; i < pWidthAspects.length; i++)
	{
		var nValue = parseInt( this.m_eElement.css( pWidthAspects[i] ), 10);

		if (!isNaN(nValue))
		{
			nTotalWidth += nValue;
		}
	};

	return nTotalWidth + this.m_nWidth;
};

/**
 * Returns the width of this panel.
 * 
 * @type int
 * @returns nWidth The inner width of the WorkbenchPanel in pixels
 */
br.workbench.ui.WorkbenchPanel.prototype.getWidth = function()
{
	return this.m_nWidth;
};

/**
 * Returns the offset height of this panel
 * 
 * @returns The total height of the WorkbenchPanel including padding and borders in pixels.
 * @type int
 */
br.workbench.ui.WorkbenchPanel.prototype.getHeightOffset = function()
{
	var pHeightAspects = [
		'padding-top',
		'padding-bottom',
		'border-left-top',
		'border-right-top'
	];
	var nTotalHeight = 0;

	for (var i = 0; i < pHeightAspects.length; i++)
	{
		var nValue = parseInt( this.m_eElement.css( pHeightAspects[i] ), 10);

		if( !isNaN(nValue) ){
			nTotalHeight += nValue;
		}
	};

	return nTotalHeight;
};

br.workbench.ui.WorkbenchPanel.prototype.getElement = function()
{
	return this.m_eElement;
};

/**
 * @private
 */
br.workbench.ui.WorkbenchPanel.prototype._bindYResize = function()
{
	var self = this;
	var fResize = function() {
		self.m_eElement.css('height', jQuery(window).height() - self.getHeightOffset());
	};
	jQuery(window).resize(fResize);
	fResize();
};

/**
 * @private
 */
br.workbench.ui.WorkbenchPanel.prototype._bindXResize = function()
{
	var self = this;
	this.m_eDragHandle = jQuery('<div class="drag-handle"></div>');
	this.m_eElement.append( this.m_eDragHandle );
	/**
	 * Add the handle to the left or the right side of the WorkbenchPanel
	 */
	if (this.m_sOrientation === 'left')
	{
		this.m_eDragHandle.css({
			"right": 0,
			"left": "auto"
		});
	}
	/**
	 * Bind Drag and Drop Behaviour
	 */
	this.m_eDragHandle.mousedown(function(e) {
		/**
		 * Keep the Start Values
		 */
		e.preventDefault();
		var nStartX = e.clientX;
		var nStartWidth = self.m_nWidth;
		/**
		 * Drag
		 */
		jQuery(document).mousemove(function(e) {
			if( self.m_sOrientation === 'left')
			{
				var nNewWidth = nStartWidth - (nStartX - e.clientX);
			}
			else
			{
				var nNewWidth = nStartWidth + (nStartX - e.clientX);
			}
			self.setWidth(nNewWidth);
		})
		/**
		 * Drop
		 */
		.mouseup(function() {
			jQuery(document).unbind('mousemove mouseup');
		})
	})
};
