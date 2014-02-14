br.Core.thirdparty("knockout");

/**
 * Constructs a <code>br.presenter.testing.PresenterComponentFixture</code>.
 * 
 * @class
 * 
 * The <code>PresenterComponentFixture</code> serves to create presenter components in order to test the 
 * component behavior.
 *
 * 
 * <p>Tests may use the <code>PresenterComponentFixture</code> to:
 * <ul> 
 * 	<li>create a presenter component to test the model behavior:</br>
 * 		<code>
 *  	given("component.opened = true")<br>
 *  	and(component presentation model is in state A)<br>
 *  	when(component presentation model is modified)<br>
 *  	then(component presentation model is in new state B)<br>
 * 		</code>
 * 	</li>
 * 	<li>create a presenter component and bind it (using knockout) to the view template, to test the model and view behavior:</br>
 * 		<code>
 *  	given("component.viewOpened = true")<br>
 *  	and(component presentation model is in state A)<br>
 *  	and(component view is in state X)<br>
 *  	when(button clicked on component view)<br>
 *  	then(component presentation model is in new state B)<br>
 *  	and(component view is in new state Y)<br>
 * 		</code>
 * 	</li>
 * </ul>
 * </p>
 * 
 * @constructor
 * @param {String} sTemplateId the HTML template id representing the view of the presenter component. Required, not-null.
 * @param {String} sPresentationModel the presentation model class name for the presenter component. Required, not-null.
 * 
 * @extends br.component.testing.ComponentFixture
 */
br.presenter.testing.PresenterComponentFixture = function(sTemplateId, sPresentationModel)
{
	if(!sTemplateId)
	{
		throw new br.Errors.CustomError(br.Errors.LEGACY, "PresenterComponentFixture must be provided with a view template id");
	}
	if(!sPresentationModel)
	{
		throw new br.Errors.CustomError(br.Errors.LEGACY, "PresenterComponentFixture must be provided with a presentation model");
	}
	
	var sPresenterComponentXML = 
		'<br.presenter-component templateId="' + sTemplateId + '" presentationModel="' + sPresentationModel + '"></br.presenter-component>';
	
	//call super constructor
	br.component.testing.ComponentFixture.call(this, sPresenterComponentXML, 
			new br.presenter.testing.PresentationModelFixture());
	
	/**
	 * @private
	 */
	this.m_fBindPresentationModel = ko.applyBindings;
};


br.Core.extend(br.presenter.testing.PresenterComponentFixture, br.component.testing.ComponentFixture);


/**
 * PresenterComponentFixture handles properties 'opened' and 'viewOpened'.
 * 
 * @param {String} sProperty The property to check.
 * 
 * @see br.test.Fixture#canHandleProperty
 */
br.presenter.testing.PresenterComponentFixture.prototype.canHandleProperty = function(sProperty)
{
	return (sProperty == "opened") || (sProperty == "viewOpened");
};

/**
 * This method creates the presenter component (if property = 'opened') and binds it to the view template (if 
 * property = 'viewOpened') using the references to the template Id and presentation model provided in the 
 * constructor.
 * 
 * @param {String} sProperty The property name
 * @param {Variant} vValue The value to check.
 * 
 * @see br.test.Fixture#doGiven
 */
br.presenter.testing.PresenterComponentFixture.prototype.doGiven = function(sProperty, vValue)
{
	if (!(sProperty == "opened" || sProperty == "viewOpened") )
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "PresenterComponentFixture only supports 'opened' or 'viewOpened' as property-value pairs.");
	}
	
	
	if(sProperty == "opened")
	{
		ko.applyBindings = function(){};
	}
	
	try
	{
		// call super method
		br.component.testing.ComponentFixture.prototype.doGiven.call(this, "opened", vValue);
	}
	finally
	{
		ko.applyBindings = this.m_fBindPresentationModel;
	}
};


