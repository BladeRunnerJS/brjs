br.thirdparty("jquery");

/**
 * @class
 * <code>RightMarginWidth ViewFixtureHandler</code> can be used to test the right margin width of an element.
 * Example usage:
 * <p>
 * <code>and("tile.view.([identifier=\'FxTileSpot\'] .fxtile_amount .fx_tile_amount_input input).rightMarginWidth = '10'");</code>
 * </p>
 * 
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.RightMarginWidth = function()
{
};

br.implement(br.test.viewhandler.RightMarginWidth, br.test.viewhandler.ViewFixtureHandler);

br.test.viewhandler.RightMarginWidth.prototype.set = function(eElement)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "RightMarginWidth can't be used in a Given or When clause.");
};

br.test.viewhandler.RightMarginWidth.prototype.get = function(eElement)
{ 
	var sMargin = jQuery(eElement)[0].style.margin;
	
	var	pWidthValues = sMargin.match(/\d+/g);
	
	return pWidthValues.length == 4 ? pWidthValues[1] : pWidthValues.length == 2 ? pWidthValues[1] : pWidthValues[0] ;
};
