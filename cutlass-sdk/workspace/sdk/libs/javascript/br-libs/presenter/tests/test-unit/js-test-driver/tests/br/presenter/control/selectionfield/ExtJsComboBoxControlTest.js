// br.thirdparty('jstestdriverextensions');
// br.thirdparty('jsmockito');

(function() {

	var testCaseName = 'ExtJsComboBoxControlTest';
	var testCase = {
		setUp: function() {

			this.comboBoxControl = new br.presenter.control.selectionfield.ExtJsComboBoxControl();

			JsHamcrest.Integration.JsTestDriver();
			JsMockito.Integration.JsTestDriver();

			this.selectionFieldMock = mock(br.presenter.node.SelectionField);
			this.selectionFieldMock.value   = mock(br.presenter.property.EditableProperty);
			this.selectionFieldMock.options = mock(br.presenter.property.EditableProperty);
			this.selectionFieldMock.enabled = mock(br.presenter.property.EditableProperty);
			this.selectionFieldMock.visible = mock(br.presenter.property.EditableProperty);

			this.optionsListener = { fake: 'optionsListener'};

			when(this.selectionFieldMock.options).addChangeListener().thenReturn(this.optionsListener);

			var fakeElement = {
				type: 'text',
				style: {
					display: ''
				}
			};
			this.comboBoxControl.setElement(fakeElement);

		},

		tearDown: function() {

		},

		'test combo box can be bound to SelectionField': function() {
			assertNoException(function () {
				this.comboBoxControl.setPresentationNode(this.selectionFieldMock);				
			}.bind(this));
		},

		'test combo box throws exception if bound to vanilla PresentationNode': function() {
			var presentationNode = new br.presenter.node.PresentationNode();

			assertException(
				function() {
					this.comboBoxControl.setPresentationNode(presentationNode);
				}.bind(this),
				'InvalidControlModelError'
			);
		},

		'test combo box throws exception if bound to MultiSelectionField': function() {
			var multiSelectionField = new br.presenter.node.MultiSelectionField(['option #1', 'option #2']);

			assertException(
				function() {
					this.comboBoxControl.setPresentationNode(multiSelectionField);
				}.bind(this),
				'InvalidControlModelError'
			);
		},

		'test when combo box is passed a SelectionField, it adds itself as a listener to SelectionField properties and requests to be called immediately.': function() {

			this.comboBoxControl.setPresentationNode(this.selectionFieldMock);

			verify(this.selectionFieldMock.value).addChangeListener(this.comboBoxControl, string(), true);
			verify(this.selectionFieldMock.options).addChangeListener(this.comboBoxControl, string(), true);
			verify(this.selectionFieldMock.enabled).addChangeListener(this.comboBoxControl, string(), true);
			verify(this.selectionFieldMock.visible).addChangeListener(this.comboBoxControl, string(), true);
		},

		// SelectionField.options has addChangeListener and removeListener methods but is not a Property,
		// so it gets missed by removeChildListeners (which calls PresentationNode.properties(), which returns only instanceof Property)
		'test when destroy() is invoked on combo box, then it removes itself as listener to SelectionField options property, and calls removeChildListeners.': function() {

			this.comboBoxControl.setPresentationNode(this.selectionFieldMock);
			this.comboBoxControl.destroy();

			verify(this.selectionFieldMock).removeChildListeners();
			verify(this.selectionFieldMock.options).removeListener(this.optionsListener);
		}

	};

	TestCase(testCaseName, testCase);
}());