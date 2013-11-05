br.thirdparty("jquery");

/**
 * @class
 * <p>The <code>ViewFixture</code> enables interacting with the rendered view via <code>ViewFixtureHandlers</code>.
 * An element in the view can be selected with jQuery selectors. In Given and When phases the selected element
 * in the view as well as its desired value will be passed as arguments to the <code>set()</code> method of a 
 * <code>ViewFixtureHandler</code> which will update the element accordingly. In the Then phase the same arguments will be 
 * passed to the <code>get()</code> method of a <code>ViewFixtureHandler</code>, which will then inspect the selected view 
 * element and return a value of a particular property of this element to the <code>ViewFixture</code>. The <code>ViewFixture</code>
 * should mainly be used to check that the bindings between view elements in templates and the corresponding presentation
 * model properties have been specified correctly. A test might set a value on the view element in the Given or When phases and
 * then check in the Then phase that this value has been updated after updating the relevant presentation model property.</p>
 * 
 * <p>Assuming that the <code>ViewFixture</code> has been added with the identifier <code>view</code> as a subfixture
 * of the <code>ComponentFixture</code> which has the identifier <code>form</code>, then the <code>ViewFixture</code>
 * can be used in the following way in a test:
 * </p>
 * <p>
 * <code>then("form.view.(.orderSummary [identifier=\'orderStatus\']).text = 'complete'");</code>
 * </p>
 * <p>
 * In the above example the jQuery selector for the element in the view is
 * <code>.spotGeneralSummary [identifier=\'dealSubmittedFor\']</code> and it must be specified within parentheses.
 * The following part of the statement, <code>.text = 'test phrase'</code>, specifies the ViewFixtureHandler
 * (<code>Text</code>) and the value (<code>'test phrase'</code>) which will be passed to it. The <code>Text</code>
 * <code>ViewFixtureHandler</code> will then get the text value of the selected view element and return this value
 * to the <code>ViewFixture</code>. The test will pass if the text value of the selected view element is indeed equal
 * to <code>'test phrase'</code>.
 * </p>
 * 
 * @implements br.test.Fixture
 * 
 * @constructor 
 * Constructs a <code>br.test.ViewFixture</code>.
 * @param {String} sViewSelector (optional) CSS selector to identify the parent view element for this fixture
 *
 */
br.test.ViewFixture = function(sViewSelector)
{
	this.m_sViewSelector = sViewSelector || null;
	this.m_mViewHandlers = {
		blurred: new br.test.viewhandler.Blurred(),
		checked: new br.test.viewhandler.Checked(),
		childrenCount: new br.test.viewhandler.ChildrenCount(),
		className: new br.test.viewhandler.ClassName(),
		clicked: new br.test.viewhandler.Clicked(),
		backgroundImage: new br.test.viewhandler.BackgroundImage(),
		doesNotHaveClass: new br.test.viewhandler.DoesNotHaveClass(),
		enabled: new br.test.viewhandler.Enabled(),
		focusIn: new br.test.viewhandler.FocusIn(),
		focusOut: new br.test.viewhandler.FocusOut(),
		focused: new br.test.viewhandler.Focused(),
		hasClass: new br.test.viewhandler.HasClass(),
		height: new br.test.viewhandler.Height(),
		isVisible: new br.test.viewhandler.IsVisible(),
		mouseDown: new br.test.viewhandler.MouseDown(),
		mouseMove: new br.test.viewhandler.MouseMove(),
		mouseOut: new br.test.viewhandler.MouseOut(),
		mouseOver: new br.test.viewhandler.MouseOver(),
		mouseUp: new br.test.viewhandler.MouseUp(),
		onKeyUp: new br.test.viewhandler.OnKeyUp(),
		options: new br.test.viewhandler.Options(),
		readonly: new br.test.viewhandler.Readonly(),
		rightClicked: new br.test.viewhandler.RightClicked(),
		scrolledHorizontal: new br.test.viewhandler.ScrolledHorizontal(),
		scrolledVertical: new br.test.viewhandler.ScrolledVertical(),
		selected: new br.test.viewhandler.Selected(),
		text: new br.test.viewhandler.Text(),
		typedValue: new br.test.viewhandler.TypedValue(),
		value: new br.test.viewhandler.Value(),
		width: new br.test.viewhandler.Width(),
		borderWidth: new br.test.viewhandler.BorderWidth(),
		borderColor: new br.test.viewhandler.BorderColor(),
		topMarginWidth: new br.test.viewhandler.TopMarginWidth(),
		bottomMarginWidth: new br.test.viewhandler.BottomMarginWidth(),
		rightMarginWidth: new br.test.viewhandler.RightMarginWidth(),
		leftMarginWidth: new br.test.viewhandler.LeftMarginWidth(),
		color: new br.test.viewhandler.Color(),
		onKeyDown: new br.test.viewhandler.OnKeyDown(),
		top: new br.test.viewhandler.Top()
	};
};

br.inherit(br.test.ViewFixture, br.test.Fixture);

br.test.ViewFixture.prototype.setUp = function()
{
	if(this.m_sViewSelector)
	{
		var pViewElements = jQuery(this.m_sViewSelector);
		this._verifyOnlyOneElementSelected(pViewElements, this.m_sViewSelector);
		this.setViewElement(pViewElements[0]);
	}
};

br.test.ViewFixture.prototype.tearDown = function()
{
	this.m_eViewElement = null;
	
	if(this.m_oBlurHandler)
	{
		this.m_oBlurHandler.destroy();
	}
};

br.test.ViewFixture.prototype.setViewElement = function(eViewElement)
{
	this.m_eViewElement = eViewElement;
	this.m_oBlurHandler = new br.test.viewhandler.BlurHandler(eViewElement);
};

br.test.ViewFixture.prototype.getViewElement = function()
{
	return this.m_eViewElement;
};

br.test.ViewFixture.prototype.setViewElementWithoutAttachingBlurHandler = function(eViewElement)
{
	this.m_eViewElement = eViewElement;
};

br.test.ViewFixture.prototype.setComponent = function(oComponent)
{
	this.m_oComponent = oComponent;
};

br.test.ViewFixture.prototype.getComponent = function()
{
	return this.m_oComponent;
};

// *** Fixture interface ***

br.test.ViewFixture.prototype.canHandleProperty = function(sProperty)
{
	return true;
};

br.test.ViewFixture.prototype.canHandleExactMatch = function()
{
	return false;
};

br.test.ViewFixture.prototype.doGivenAndDoWhen = function(sProperty, vValue)
{
	var oHandler = this._getHandler(sProperty, vValue);
	
	if(oHandler.property == "count")
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "The 'count' property can only be used in then statements.");
	}
	else
	{
		oHandler.viewFixtureHandler.set(oHandler.selectedElement, vValue);
	}
};
br.test.ViewFixture.prototype.doGiven = br.test.ViewFixture.prototype.doGivenAndDoWhen;
br.test.ViewFixture.prototype.doWhen = br.test.ViewFixture.prototype.doGivenAndDoWhen;

br.test.ViewFixture.prototype.doThen = function(sProperty, vValue)
{
	var oHandler = this._getHandler(sProperty, vValue);
	
	if(oHandler.property == "count")
	{
		assertEquals("'count' should be " + vValue, vValue, oHandler.elements.length);
	}
	else
	{
		assertEquals("'" + oHandler.property + "' should be " + vValue,
			vValue, oHandler.viewFixtureHandler.get(oHandler.selectedElement, vValue));
	}
};

br.test.ViewFixture.prototype._getHandler = function(sProperty, vValue)
{
	var oHandler = {};
	
	oHandler.property = this._getPropertyName(sProperty);
	oHandler.elements = this._getViewElements(sProperty);
	
	if(oHandler.property != "count")
	{
		oHandler.viewFixtureHandler = this._getViewHandler(oHandler.property);
		
		if(oHandler.elements.length == 1)
		{
			oHandler.selectedElement = oHandler.elements[0];
		}
		else
		{
			this._verifyOnlyOneElementSelected(oHandler.elements, oHandler.property);
		}
	}
	
	return oHandler;
};

br.test.ViewFixture.prototype._getPropertyName = function (sProperty)
{	
	return sProperty.match(/[^\.]*$/)[0];
};

br.test.ViewFixture.prototype._getViewElements = function(sProperty)
{
	var sSelector = sProperty.match(/\((.*)\)\.[^.]+/)[1];
	return jQuery(this.m_eViewElement).find(sSelector);
};

br.test.ViewFixture.prototype._verifyOnlyOneElementSelected = function(pElements, sSelector)
{
	if (pElements.length === 0)
	{
		throw "No view element found for '" + sSelector + "'";
	}
	else if (pElements.length > 1)
	{
		throw "More than one view element found for '" + sSelector + "'";
	}
};

br.test.ViewFixture.prototype._getViewHandler = function(sPropertyName)
{
	var oHandler = this.m_mViewHandlers[sPropertyName];
	
	if(!oHandler)
	{
		throw new br.Errors.CustomError(br.Errors.INVALID_TEST, "Undefined view fixture handler '" + sPropertyName + "'");
	}
	
	return oHandler;
};
