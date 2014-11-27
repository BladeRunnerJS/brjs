var jQuery = require('jquery');

var sessionItemName = 'brjs-workbench-tools-collapsed';

// Returns whether the workbench tool with the title `title` is collapsed or not. Returns `null` if state is unknown.
function getCollapsedStateFromStorage(title) {
	var storeData = sessionStorage.getItem(sessionItemName);
	var parsedStoreData;

	if (storeData) {
		parsedStoreData = JSON.parse(storeData);

		if (parsedStoreData[title]) {
			return parsedStoreData[title];
		}
	}

	return null;
}

function storeCollapsedStateToStorage(title, collapsed) {
	var storeData = sessionStorage.getItem(sessionItemName);
	var parsedStoreData = {};

	if (storeData) {
		parsedStoreData = JSON.parse(storeData);
	}

	parsedStoreData[title] = collapsed;

	sessionStorage.setItem(sessionItemName, JSON.stringify(parsedStoreData));
}

/**
 * @class
 * @alias module:br/workbench/ui/WorkbenchPanel
 *
 * @classdesc
 * A <code>WorkbenchPanel</code> is the main container for displaying components
 * within a workbench. Workbench panels can be added to a <code>WorkbenchPanel</code>,
 * either to the left or right side of the screen.
 *
 * @param String sOrientation Either "left" or "right"
 * @param int nWidth The width of the WorkbenchPanel in Pixels
 * @param boolean bXResizable If True, the panel will be resizable.
 */
function WorkbenchPanel(orientation, width, xResizable) {
	xResizable = (xResizable || true);
	this.m_sOrientation = orientation;
	this.m_nWidth = width;
	this.m_eElement = jQuery('<div class="workbench-panel"></div>');

	this.m_eComponentContainer = document.createElement('ul');
	this.m_eComponentContainer.id = 'workbench-panel-' + WorkbenchPanel.ID++;

	jQuery(this.m_eComponentContainer).sortable({
		placeholder: "sortable-placeholder",
		handle: '.header'
	});

	this.m_eElement.append(this.m_eComponentContainer);

	jQuery('body').append(this.m_eElement);

	this.setWidth(width);
	this._bindYResize();

	if (xResizable !== false) {
		this._bindXResize();
	}
}

/** @private */
WorkbenchPanel.ID = 0;

/**
 * Returns the id of the element that represents the container which components will be added to within this 
 *  <code>WorkbenchPanel</code>.
 *
 * @type String
 * @returns the container id
 */
WorkbenchPanel.prototype.getComponentContainerId = function() {
	return this.m_eComponentContainer.id;
};

/**
 * Adds a {@link module:br/workbench/ui/WorkbenchComponent} to this panel.
 *
 * @param {WorkbenchPanelComponent} workbenchComponent The component to add.
 * @param {String} title The title to display for the component.
 * @param {boolean} collapsed if True, the initial state of the component will be collapsed.
 */
WorkbenchPanel.prototype.add = function(workbenchComponent, title, collapsed) {
	if (typeof collapsed === 'undefined') {
		collapsed = false;
	}

	var storedCollapsedState = getCollapsedStateFromStorage(title);
	if (storedCollapsedState !== null) {
		collapsed = storedCollapsedState;
	}

	var wrapperEl = document.createElement('LI');
	var jQueryWrapperEl = jQuery(wrapperEl);
	wrapperEl.className = 'workbench-component';

	var headerEl = document.createElement('DIV');
	headerEl.innerHTML = '<div class="arrow"></div><div class="title">' + title + '</div>';
	headerEl.className = 'header';

	wrapperEl.appendChild(headerEl);

	var containerEl = document.createElement('DIV');
	containerEl.className = 'container';
	containerEl.appendChild(workbenchComponent.getElement());
	wrapperEl.appendChild(containerEl);

	var jQueryHeader = jQuery(headerEl);

	if (collapsed) {
		jQueryHeader.next().hide();
		jQueryWrapperEl.addClass('collapsed');
	}

	jQueryHeader.click(function() {
		jQueryHeader.next().slideToggle();
		jQueryWrapperEl.toggleClass('collapsed');

		storeCollapsedStateToStorage(title, jQueryWrapperEl.hasClass('collapsed'));

		return false;
	});

	this.m_eComponentContainer.appendChild(wrapperEl);

	if (workbenchComponent.render) {
		workbenchComponent.render(this.m_eComponentContainer);
	}

	return this;
};

/**
 * Sets the width of this panel.
 *
 * @param {int} width The width of the WorkbenchPanel in Pixels.
 */
WorkbenchPanel.prototype.setWidth = function(width) {
	this.m_nWidth = width;

	this.m_eElement.css('width', width);

	if (this.m_sOrientation === 'left') {
		this.m_eElement.css("left", 0);
	} else {
		this.m_eElement.css({
			'left': '100%',
			'margin-left': this.getOuterWidth() * -1
		});
	}
};

/**
 * Returns the outer width of this workbench panel.
 *
 * @returns {int} The total width of the WorkbenchPanel including padding and borders in pixels
 */
WorkbenchPanel.prototype.getOuterWidth = function() {
	var widthAspects = [
		'padding-left',
		'padding-right',
		'border-left-width',
		'border-right-width'
	];
	var totalWidth = 0;
	var value;

	for (var i = 0, len = widthAspects.length; i < len; i++) {
		value = parseInt(this.m_eElement.css(widthAspects[i]), 10);

		if (!isNaN(value)) {
			totalWidth += value;
		}
	};

	return totalWidth + this.m_nWidth;
};

/**
 * Returns the width of this panel.
 *
 * @returns {int} The inner width of the WorkbenchPanel in pixels
 */
WorkbenchPanel.prototype.getWidth = function() {
	return this.m_nWidth;
};

/**
 * Returns the offset height of this panel
 *
 * @returns {int} The total height of the WorkbenchPanel including padding and borders in pixels.
 */
WorkbenchPanel.prototype.getHeightOffset = function() {
	var heightAspects = [
		'padding-top',
		'padding-bottom',
		'border-left-top',
		'border-right-top'
	];
	var totalHeight = 0;
	var value;

	for (var i = 0, len = heightAspects.length; i < len; i++) {
		value = parseInt(this.m_eElement.css(heightAspects[i]), 10);

		if (!isNaN(value)){
			totalHeight += value;
		}
	};

	return totalHeight;
};

WorkbenchPanel.prototype.getElement = function() {
	return this.m_eElement;
};

/** @private */
WorkbenchPanel.prototype._bindYResize = function() {
	var self = this;
	var fResize = function() {
		self.m_eElement.css('height', jQuery(window).height() - self.getHeightOffset());
	};

	jQuery(window).resize(fResize);
	fResize();
};

/** @private */
WorkbenchPanel.prototype._bindXResize = function() {
	var self = this;
	this.m_eDragHandle = jQuery('<div class="drag-handle"></div>');
	this.m_eElement.append( this.m_eDragHandle );

	// Add the handle to the left or the right side of the WorkbenchPanel.
	if (this.m_sOrientation === 'left') {
		this.m_eDragHandle.css({
			'right': 0,
			'left': 'auto'
		});
	}

	// Bind Drag and Drop Behaviour
	this.m_eDragHandle.mousedown(function(e) {
		// Keep the Start Values
		e.preventDefault();
		var startX = e.clientX;
		var startWidth = self.m_nWidth;
		var newWidth;

		jQuery(document).mousemove(function(e) { // drag
			if (self.m_sOrientation === 'left') {
				newWidth = startWidth - (startX - e.clientX);
			} else {
				newWidth = startWidth + (startX - e.clientX);
			}
			self.setWidth(newWidth);
		})
		.mouseup(function() { // drop
			jQuery(document).unbind('mousemove mouseup');
		});
	});
};

module.exports = WorkbenchPanel;
