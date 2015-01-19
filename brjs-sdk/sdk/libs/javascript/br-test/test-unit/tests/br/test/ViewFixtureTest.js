br.Core.thirdparty('jsunitextensions');

var ViewFixtureTest = TestCase("ViewFixtureTest");

ViewFixtureTest.prototype.setUp = function() {
	this.m_oViewFixture = new br.test.ViewFixture();
	this.m_oViewFixture.setViewElement(this.getFormElement());

	this.m_oViewFixtureForFxTicket = new br.test.ViewFixture();
	this.m_oViewFixtureForFxTicket.setViewElement(this.getFxTicketElement());
};

ViewFixtureTest.prototype.getFormElement = function() {
	var eElement = document.createElement('div');
	eElement.innerHTML =
		"<select name='darryl' class='darryl'>" +
			"<option value='1234'>AB12</option>" +
			"<option value='5678'>CD34</option>" +
			"<option value='9012'>EF56</option>" +
			"</select>" +
			"<div id='sal'>The Castle</div>" +
			"<input type='button' class='search' value='Search' disabled />" +
			"<input class='dale' type='checkbox' checked='checked' />"+
			"<label class='seeMe' style='display: none;'></label>"+
			"<div id='parent-element'>" +
			"<span id='child1'></span>" +
			"<span id='child2'></span>" +
			"</div>";
	return eElement;
};

ViewFixtureTest.prototype.getFxTicketElement = function() {
	var fxTicketElement = document.createElement('div');
	fxTicketElement.innerHTML = "<div class='ticketBody'>" +
		"<form data-bind='attr: {class: state}' class='Initial'>" +
		"<header>FX Trade Ticket" +
		"<span class='subheading' id='subheading'></span>" +
		"<a href='#' title='click to close ticket' class='closeTicket'>close</a>" +
		"</header></form></div>";

	return fxTicketElement;
};

ViewFixtureTest.prototype.test_canHandleReturnsTrue = function() {
	assertEquals(true, this.m_oViewFixture.canHandleProperty("arbitrary property"));
};

ViewFixtureTest.prototype.test_canAddANewHandler = function() {
	var assertionFunc = function() {
		// start code under test
		this.m_oViewFixture.doThen('view.([name=\'darryl\']).new-fixture', 'irrelevant');
		// end code under test
	}.bind(this);

	assertException('no existing handler exists', assertionFunc, br.Errors.INVALID_TEST);

	this.m_oViewFixture.addViewHandlers({
		'new-fixture': function() {
			this.get = function() { return 'irrelevant'; };
			this.set = function() {};
		}
	});

	assertionFunc();
};

ViewFixtureTest.prototype.test_cannotOverrideAnExistingHandler = function() {
	// initially, verify that the handler exists
	this.m_oViewFixture.doThen('view.([name=\'darryl\']).value', '1234');

	// set a handler of the same name onto the ViewFixture
	assertException('an exception is thrown when attempting to override an existing handler', function() {
		this.m_oViewFixture.addViewHandlers({
			'value': function() {
				this.get = function() { return 'prevented attempt to override the value handler\'s get'; };
				this.set = function() {};
			},
			'new-value': function() {
				this.get = function() { return 'new (valid) handler get response'; };
				this.set = function() {};
			}
		});
	}.bind(this),
		br.Errors.INVALID_PARAMETERS
	);

	// verify that the original value handler is invoked
	this.m_oViewFixture.doThen('view.([name=\'darryl\']).value', '1234');

	assertException('the valid view handler was not added to the view fixture after an override was detected', function() {
		this.m_oViewFixture.doThen('view.([name=\'darryl\']).new-value', 'new (valid) handler get response');
	}.bind(this),
		br.Errors.INVALID_TEST
	);
};

ViewFixtureTest.prototype.test_getViewElementsFromPropertyString = function() {
	assertEquals("SELECT", this.m_oViewFixture._getViewElements("view.(.darryl).value")[0].nodeName);
};

ViewFixtureTest.prototype.test_getViewElementsFromAMappedSelector = function() {
	this.m_oViewFixture.setSelectorMappings({
		'mapped-selector': '.darryl'
	});

	assertEquals('SELECT', this.m_oViewFixture._getViewElements('view.(mapped-selector).value')[0].nodeName);
};

ViewFixtureTest.prototype.test_canParsePropertyName = function() {
	assertEquals("value", this.m_oViewFixture._getPropertyName("view.(darryl).value"));
};

ViewFixtureTest.prototype.test_canReadPropertyByElementName = function() {
	var oThis = this;

	this.m_oViewFixture.doThen("view.([name='darryl']).value", "1234");
	assertAssertError("1a", function() {
		oThis.m_oViewFixture.doThen("view.([name='darryl']).value", "1234-X");
	});

	this.m_oViewFixture.doThen("view.([name='darryl']).options", ["AB12", "CD34", "EF56"]);
	assertAssertError("1a", function() {
		oThis.m_oViewFixture.doThen("view.([name='darryl']).options", ["AB12", "CD34", "EF56-X"]);
	});

	this.m_oViewFixture.doThen("view.([name='darryl']).enabled", true);
	assertAssertError("1a", function() {
		oThis.m_oViewFixture.doThen("view.([name='darryl']).enabled", false);
	});
};

ViewFixtureTest.prototype.test_canSetPropertyByElementName = function() {
	this.m_oViewFixture.doThen("view.([name='darryl']).value", "1234");
	this.m_oViewFixture.doWhen("view.([name='darryl']).value", "5678");
	this.m_oViewFixture.doThen("view.([name='darryl']).value", "5678");
};

ViewFixtureTest.prototype.test_canReadPropertyByElementId = function() {
	this.m_oViewFixture.doThen("view.([id='sal']).text", "The Castle");

	var oThis = this;
	assertAssertError("1a", function() {
		oThis.m_oViewFixture.doThen("view.([id='sal']).text", "The Castle-X");
	});
};

ViewFixtureTest.prototype.test_canSetPropertyByElementId = function() {
	this.m_oViewFixture.doThen("view.([id='sal']).text", "The Castle");
	this.m_oViewFixture.doWhen("view.([id='sal']).text", "The Dish");
	this.m_oViewFixture.doThen("view.([id='sal']).text", "The Dish");
};

ViewFixtureTest.prototype.test_canReadPropertyByElementClass = function() {
	var oThis = this;

	this.m_oViewFixture.doThen("view.(.dale).value", "on");
	assertAssertError("1a", function() {
		oThis.m_oViewFixture.doThen("view.(.dale).value", "on-X");
	});

	this.m_oViewFixture.doThen("view.(.dale).enabled", true);
	assertAssertError("1a", function() {
		oThis.m_oViewFixture.doThen("view.(.dale).enabled", false);
	});
};

ViewFixtureTest.prototype.test_canSetPropertyByElementClass = function() {
	this.m_oViewFixture.doThen("view.(.dale).value", "on");
	this.m_oViewFixture.doWhen("view.(.dale).value", "off");
	this.m_oViewFixture.doThen("view.(.dale).value", "off");
};

ViewFixtureTest.prototype.test_doGivenMatchingMultipleElementsThrowsAnException = function() {
	var oThis = this;
	assertFails("1a", function() {
		oThis.m_oViewFixture.doGiven("view.(input).enabled", true);
	});
};

ViewFixtureTest.prototype.test_doGivenWithNoValidFixtureThrowsAnException = function() {
	var oThis = this;
	assertException("When a valid view fixture doesn't exist, check the right exception is thrown.", function() {
		oThis.m_oViewFixture.doGiven("view.(input).fred", true);
	}, br.Errors.INVALID_TEST);
};


ViewFixtureTest.prototype.test_doThenMatchingMultipleElementsThrowsAnException = function() {
	var oThis = this;
	assertFails("1a", function() {
		oThis.m_oViewFixture.doThen("view.(input).enabled", true);
	});
};

ViewFixtureTest.prototype.test_doGivenMatchingZeroElementsThrowsAnException = function() {
	var oThis = this;
	assertFails("1a", function() {
		oThis.m_oViewFixture.doGiven("view.(.no-such-class).enabled", true);
	});
};

ViewFixtureTest.prototype.test_doThenMatchingZeroElementsThrowsAnException = function() {
	var oThis = this;
	assertFails("1a", function() {
		oThis.m_oViewFixture.doThen("view.(.no-such-class).enabled", true);
	});
};


/**********************************
 * Tests for the ".count" property
 **********************************/

ViewFixtureTest.prototype.test_canGetCountOfElementMatchedBySelector = function() {
	var sProperty = "view.(div#parent-element span).count";
	var oThis = this;

	assertAssertError("1a", function() {
		oThis.m_oViewFixture.doThen(sProperty, 1);
	});

	this.m_oViewFixture.doThen(sProperty, 2);

	assertAssertError("2a", function() {
		oThis.m_oViewFixture.doThen(sProperty, 3);
	});
};

ViewFixtureTest.prototype.test_countUseSquareBracketsWithinCssSelector = function() {
	this.m_oViewFixture.doThen("view.(input[type=button]).count", 1);
};

ViewFixtureTest.prototype.test_countOfNonExistentElementsIsZero = function() {
	this.m_oViewFixture.doThen("view.(input[type=buttonX]).count", 0);
	this.m_oViewFixture.doThen("view.(form label).count", 0);
	this.m_oViewFixture.doThen("view.(div.disabled).count", 0);
};

ViewFixtureTest.prototype.test_cannotSetCountOfAnything = function() {
	var _self = this;
	assertException(function() {
		_self.m_oViewFixture.doWhen("view.(div#parent-element span).count", 0);
	}, br.Errors.INVALID_TEST);
	assertException(function() {
		_self.m_oViewFixture.doWhen("view.(select option).count", 42);
	}, br.Errors.INVALID_TEST);
	assertException(function() {
		_self.m_oViewFixture.doWhen("view.(input[type=button]).count", 100);
	}, br.Errors.INVALID_TEST);
	assertException(function() {
		_self.m_oViewFixture.doWhen("view.(div.disabled).count", 7);
	}, br.Errors.INVALID_TEST);
};
