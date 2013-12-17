TestPresentationModel = function()
{
	ct = {
		i18n: function(sToken, mTemplateArgs) {
			if(sToken === "ct.i18n.date.format.typed") {
				return "d-m-Y";
			}
			else {
				return br.i18n.Translator.getTranslator().getMessage(sToken, mTemplateArgs);
			}
		}
	};

	if(br.i18n.Translator.INSTANCE){
		br.i18n.Translator.getTranslator = function(){
			var oTranslator = br.i18n.Translator.INSTANCE;
			oTranslator.getDateFormat = function(){
				return "d M Y";
			};
			return oTranslator;
		};
	}
		
	this.field = new br.presenter.node.Field("a");
	this.field.controlName.setValue("aField");
	
	this.selectionField = new br.presenter.node.SelectionField(['a','b'], "a");
	this.selectionField.controlName.setValue("aSelectionField");
	this.selectionFieldDefaultedToSecondValue = new br.presenter.node.SelectionField(['a','b'], "b");
	this.selectionFieldInitiallyDisabledAndHidden = new br.presenter.node.SelectionField(['a','b'], "a");
	this.selectionFieldInitiallyDisabledAndHidden.enabled.setValue(false);
	this.selectionFieldInitiallyDisabledAndHidden.visible.setValue(false);
	this.labelValueSelectionField = new br.presenter.node.SelectionField({a:"A-Label", b:"B-Label"});
	this.labelValueSelectionField.enabled.setValue(true);
	this.labelValueSelectionField.visible.setValue(true);
	
	this.jquerySelectionField = new br.presenter.node.AutoCompleteSelectionField("BB", {
		list: ["AA", "BB", "CC"],
		getList: function(sTerm, fCallback) {
			var pResult = [];
			for (var i = 0; i < this.list.length; i++)
			{
				if (this.list[i].substr(0, sTerm.length) == sTerm)
				{
					pResult.push(this.list[i]);
				}
			}
			fCallback(pResult);
		},
		isValidOption: function(sOption) {
			return br.util.ArrayUtility.inArray(this.list, sOption);
		}
	});
	
	this.multiSelectBox = new br.presenter.node.MultiSelectionField(["a","b","c", "d"], ["a","c"]);
	this.multiSelectBox.controlName.setValue("aMultiSelectionField");

	this.multiSelectBoxMapped = new br.presenter.node.MultiSelectionField({"a": "aLabel","b": "bLabel", "c" :"cLabel", "d" : "dLabel"}, ["a","c"]);
	
	this.date = new br.presenter.node.DateField("20111014");
	
	this.dateInitiallyDisabledAndHidden = new br.presenter.node.DateField("20111014");
	this.dateInitiallyDisabledAndHidden.enabled.setValue(false);
	this.dateInitiallyDisabledAndHidden.visible.setValue(false);

	this.plainProperty = new br.presenter.property.WritableProperty("a");
	this.formattedProperty = new br.presenter.property.WritableProperty("a")
		.addFormatter(br.presenter.testing.UpperCaseFormatter, {});

	this.plainEditableProperty = new br.presenter.property.EditableProperty("a");
	this.formattedEditableProperty = new br.presenter.property.EditableProperty("a")
		.addFormatter(br.presenter.testing.UpperCaseFormatter, {});
	this.parsedEditableProperty = new br.presenter.property.EditableProperty("a")
		.addParser(br.presenter.testing.LetterShifterParser, {});
	this.parsedFormattedEditableProperty = new br.presenter.property.EditableProperty("a")
		.addParser(br.presenter.testing.LetterShifterParser, {})
		.addFormatter(br.presenter.testing.UpperCaseFormatter, {});

	this.aliasPlain = new br.presenter.property.Alias(this.plainProperty);
	this.aliasFormatted = new br.presenter.property.Alias(this.formattedProperty);
};
br.Core.extend(TestPresentationModel, br.presenter.PresentationModel);
