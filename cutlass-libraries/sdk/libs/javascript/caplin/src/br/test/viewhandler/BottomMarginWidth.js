br.thirdparty("jquery");

/**
 * @class
 * <code>BottomMarginWidth ViewFixtureHandler</code> can be used to test the bottom margin width of an element.
 * Example usage:
 * <p>
 * <code>and("tile.view.([identifier=\'FxTileSpot\'] .fxtile_amount .fx_tile_amount_input input).bottomMarginWidth = '10'");</code>
 * </p>
 * 
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.BottomMarginWidth = function()
{
};

br.implement(br.test.viewhandler.BottomMarginWidth, br.test.viewhandler.ViewFixtureHandler);

br.test.viewhandler.BottomMarginWidth.prototype.set = function(eElement)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "BottomMarginWidth can't be used in a Given or When clause.");
};

br.test.viewhandler.BottomMarginWidth.prototype.get = function(eElement)
{ 
	var sMargin = jQuery(eElement)[0].style.margin;
	
	var	pWidthValues = sMargin.match(/\d+/g);
	
	return pWidthValues.length == 4 ? pWidthValues[2] : pWidthValues[0] ;
};
