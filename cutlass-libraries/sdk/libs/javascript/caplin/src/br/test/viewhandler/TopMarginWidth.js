br.thirdparty("jquery");

/**
 * @class
 * <code>TopMarginWidth ViewFixtureHandler</code> can be used to test the top margin width of an element.
 * Example usage:
 * <p>
 * <code>and("tile.view.([identifier=\'FxTileSpot\'] .fxtile_amount .fx_tile_amount_input input).topMarginWidth = '10'");</code>
 * </p>
 * 
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.TopMarginWidth = function()
{
};

br.implement(br.test.viewhandler.TopMarginWidth, br.test.viewhandler.ViewFixtureHandler);

br.test.viewhandler.TopMarginWidth.prototype.set = function(eElement)
{
	throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "TopMarginWidth can't be used in a Given or When clause.");
};

br.test.viewhandler.TopMarginWidth.prototype.get = function(eElement)
{ 
	var sMargin = jQuery(eElement)[0].style.margin;
	
	pWidthValues = /\d+/.exec(sMargin);
	
	return parseInt(pWidthValues);
};
