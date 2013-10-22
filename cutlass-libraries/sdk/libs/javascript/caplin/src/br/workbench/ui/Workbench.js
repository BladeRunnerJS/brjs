br.thirdparty("jquery");

/**
 * @class 
 * Represents a UI component for displaying workbenches. A workbench contains 
 * left and right {@link br.workbench.ui.WorkbenchPanel}s to which
 * {@link br.workbench.ui.WorkbenchComponent} components can be added.
 * @constructor
 * 
 * @param {int} nLeftWidth The width of the left panel in pixels.
 * @param {int} nRightWidth The width of the right panel in pixels.
 */
br.workbench.ui.Workbench = function(nLeftWidth, nRightWidth)
{
	this.m_oLeftWing = new br.workbench.ui.WorkbenchPanel("left", nLeftWidth);
	this.m_oRightWing = new br.workbench.ui.WorkbenchPanel("right", nRightWidth);
	
	var sLeftId = '#' + this.m_oLeftWing.getComponentContainerId();
	var sRightId = '#' + this.m_oRightWing.getComponentContainerId();
	
	jQuery(sLeftId).sortable("option", "connectWith", sRightId);
	jQuery(sRightId).sortable("option", "connectWith", sLeftId);
};

/**
 * Centers the specified element so that it is evenly spaced between the 2 side panels.
 * 
 * @param {DOMElement} eElement The element (usually the workbench component element) to center.
 */
br.workbench.ui.Workbench.prototype.center = function(eElement)
{
	br.util.ElementUtility.addClassName(eElement, "workbench-centered");
};

/**
 * Adds a component to the left wing. 
 * 
 * @param {br.workbench.ui.WorkbenchComponent} oWorkbenchComponent The component to add.
 * @param {String} sTitle The title to show for the component. 
 * @param {boolean} bCollapsed If true, the component will be collapsed. 
 */
br.workbench.ui.Workbench.prototype.addToLeftWing = function(oWorkbenchComponent, sTitle, bCollapsed)
{
	this.m_oLeftWing.add(oWorkbenchComponent, sTitle, bCollapsed);
};

/**
 * Adds a component to the right wing. 
 * 
 * @param {br.workbench.ui.WorkbenchComponent} oWorkbenchComponent The component to add.
 * @param {String} sTitle The title to show for the component. 
 * @param {boolean} bCollapsed If true, the component will be collapsed. 
 */
br.workbench.ui.Workbench.prototype.addToRightWing = function(oWorkbenchComponent, sTitle, bCollapsed)
{
	this.m_oRightWing.add(oWorkbenchComponent, sTitle, bCollapsed);
};

