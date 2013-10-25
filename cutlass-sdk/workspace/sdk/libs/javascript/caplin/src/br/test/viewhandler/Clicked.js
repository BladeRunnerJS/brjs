br.thirdparty("jquery");

/**
 * @class
 * <code>Clicked ViewFixtureHandler</code> can be used to trigger a click on a view element.
 * Example usage:
 * <p>
* <code>when("form.view.(.executeOrder [identifier=\'buttonOrder\'] .order_button).clicked => true");</code>
 * </p>
 * @constructor
 * @implements br.test.viewhandler.ViewFixtureHandler
 */
br.test.viewhandler.Clicked = function()
{
};

br.implement(br.test.viewhandler.Clicked, br.test.viewhandler.ViewFixtureHandler);


br.test.viewhandler.Clicked.prototype.set = function(eElement, mArgs)
{
	var jq_Element = jQuery(eElement);
	
	if ( jq_Element.hasClass("disabled") || jq_Element.is(":disabled") ) { 
		return;
	}
	
	if (document.activeElement && document.activeElement != eElement && 
			document.activeElement.tagName &&
			document.activeElement.tagName.toLowerCase() != 'body') {
		jq_activeElement = jQuery(document.activeElement) 
		jq_activeElement.trigger('focusout');
		
		var active_nodeName =  jq_activeElement[0].nodeName.toLowerCase();
		var active_inputType = (jq_activeElement.attr('type')) ? jq_activeElement.attr('type').toLowerCase() : "";
		if ( active_nodeName == 'select' || ( active_nodeName == 'input' && active_inputType != 'submit' ) ) {
			jq_activeElement.trigger('change');
		}
	}
	
	var element_nodeName =  jq_Element[0].nodeName.toLowerCase();
	var element_inputType = (jq_Element.attr('type')) ? jq_Element.attr('type').toLowerCase() : "";
	
	jq_Element.trigger('focusin');
	jq_Element.trigger('focus');
	try {
		document.activeElement = eElement;
	} catch (e) {
		if (eElement.setActive) {
			eElement.setActive()
		}
	}
	
	br.test.Utils.fireMouseEvent(eElement, 'click', mArgs);
	
	if ( element_nodeName == 'select' || 
			( element_nodeName == 'input' && element_inputType != 'submit' ) ) {
		jq_Element.trigger('change');
	}
	
	if ( ( (element_nodeName == 'input' && element_inputType == 'submit') 
				|| element_nodeName == 'button') ) {
		elementParentForm = jq_Element.parents('form')
		if (elementParentForm != null && (elementParentForm.attr('action') != null || elementParentForm.attr('onsubmit') != null)) {
			elementParentForm.trigger('submit');
		}
	}

};

br.test.viewhandler.Clicked.prototype.get = function(eElement)
{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "Clicked can't be used in a then clause.");
};
