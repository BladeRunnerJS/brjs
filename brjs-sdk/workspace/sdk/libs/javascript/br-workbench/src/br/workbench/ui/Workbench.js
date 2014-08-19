/**
 * @module br/workbench/ui/Workbench
 */

var JQuery = require( 'jquery' );
var WorkbenchPanel = require( './WorkbenchPanel' );
var SimpleFrame = require( 'br/component/SimpleFrame' );
var ElementUtility = require( 'br/util/ElementUtility' );

/**
 * @alias module:br/workbench/ui/Workbench
 * @classdesc
 * Represents a UI component for displaying workbenches. A workbench contains
 * left and right {@link WorkbenchPanel}s to which
 * {@link WorkbenchComponent} components can be added.
 * @class
 *
 * @param {int} nLeftWidth The width of the left panel in pixels.
 * @param {int} nRightWidth The width of the right panel in pixels.
 */
function Workbench(nLeftWidth, nRightWidth) {
	this.m_oLeftWing = new WorkbenchPanel("left", nLeftWidth);
	this.m_oRightWing = new WorkbenchPanel("right", nRightWidth);

	var sLeftId = '#' + this.m_oLeftWing.getComponentContainerId();
	var sRightId = '#' + this.m_oRightWing.getComponentContainerId();

	jQuery(sLeftId).sortable("option", "connectWith", sRightId);
	jQuery(sRightId).sortable("option", "connectWith", sLeftId);
}

/**
 * Centers the specified element so that it is evenly spaced between the 2 side panels.
 *
 * @param {DOMElement} eElement The element (usually the workbench component element) to center.
 * @see #displayComponent
 */
Workbench.prototype.center = function(eElement)
{
	ElementUtility.addClassName(eElement, "workbench-centered");
};

Workbench.prototype.displayComponent = function(oComponent, width, height)
{
	var simpleFrame = new SimpleFrame(oComponent, width, height);

	this.center(simpleFrame.getElement());
	document.body.appendChild(simpleFrame.getElement());
};

/**
 * Adds a component to the left wing.
 *
 * @param {WorkbenchComponent} oWorkbenchComponent The component to add.
 * @param {String} sTitle The title to show for the component.
 * @param {boolean} bCollapsed If true, the component will be collapsed.
 */
Workbench.prototype.addToLeftWing = function(oWorkbenchComponent, sTitle, bCollapsed)
{
	this.m_oLeftWing.add(oWorkbenchComponent, sTitle, bCollapsed);
};

/**
 * Adds a component to the right wing.
 *
 * @param {WorkbenchComponent} oWorkbenchComponent The component to add.
 * @param {String} sTitle The title to show for the component.
 * @param {boolean} bCollapsed If true, the component will be collapsed.
 */
Workbench.prototype.addToRightWing = function(oWorkbenchComponent, sTitle, bCollapsed)
{
	this.m_oRightWing.add(oWorkbenchComponent, sTitle, bCollapsed);
};

module.exports = Workbench;
