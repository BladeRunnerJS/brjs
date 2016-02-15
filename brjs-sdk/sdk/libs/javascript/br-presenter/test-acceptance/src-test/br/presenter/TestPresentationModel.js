var PresentationModel = require("br/presenter/PresentationModel");
var Core = require("br/Core");
var Alias = require("br/presenter/property/Alias");
var LetterShifterParser = require("br/presenter/LetterShifterParser");
var EditableProperty = require("br/presenter/property/EditableProperty");
var UpperCaseFormatter = require("br/presenter/UpperCaseFormatter");
var WritableProperty = require("br/presenter/property/WritableProperty");
var DateField = require("br/presenter/node/DateField");
var MultiSelectionField = require("br/presenter/node/MultiSelectionField");
var ArrayUtility = require("br/util/ArrayUtility");
var AutoCompleteSelectionField = require("br/presenter/node/AutoCompleteSelectionField");
var SelectionField = require("br/presenter/node/SelectionField");
var Field = require("br/presenter/node/Field");
var Translator = require("br/i18n/Translator");
TestPresentationModel = function()
{
    ct = {
        i18n: function(sToken, mTemplateArgs) {
            if(sToken === "br.i18n.date.format.typed") {
                return "d-m-Y";
            }
            else {
                return Translator.getTranslator().getMessage(sToken, mTemplateArgs);
            }
        }
    };

    if(Translator.INSTANCE){
        Translator.getTranslator = function(){
            Translator.INSTANCE.getDateFormat = function(){
                return "d M Y";
            };
            return Translator.INSTANCE;
        };
    }
        
    this.field = new Field("a");
    this.field.controlName.setValue("aField");
    
    this.selectionField = new SelectionField(['a','b'], "a");
    this.selectionField.automaticallyUpdateValueWhenOptionsChange(true);
    this.selectionField.controlName.setValue("aSelectionField");
    this.selectionFieldDefaultedToSecondValue = new SelectionField(['a','b'], "b");
    this.selectionFieldInitiallyDisabledAndHidden = new SelectionField(['a','b'], "a");
    this.selectionFieldInitiallyDisabledAndHidden.enabled.setValue(false);
    this.selectionFieldInitiallyDisabledAndHidden.visible.setValue(false);
    this.labelValueSelectionField = new SelectionField({a:"A-Label", b:"B-Label"});
    this.labelValueSelectionField.enabled.setValue(true);
    this.labelValueSelectionField.visible.setValue(true);
    
    this.jquerySelectionField = new AutoCompleteSelectionField("BB", {
        list: ["AA", "BB", "CC", "FormatMe"],
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
            return ArrayUtility.inArray(this.list, sOption);
        }
    });

    this.jquerySelectionField.value.addFormatter({
        format: function (value) {
            return value === "FormatMe" ? "Formatted" : value;
        }
    })

    this.multiSelectBox = new MultiSelectionField(["a","b","c", "d"], ["a","c"]);
    this.multiSelectBox.controlName.setValue("aMultiSelectionField");

    this.multiSelectBoxMapped = new MultiSelectionField({"a": "aLabel","b": "bLabel", "c" :"cLabel", "d" : "dLabel"}, ["a","c"]);
    
    this.date = new DateField("20111014");
    
    this.dateInitiallyDisabledAndHidden = new DateField("20111014");
    this.dateInitiallyDisabledAndHidden.enabled.setValue(false);
    this.dateInitiallyDisabledAndHidden.visible.setValue(false);

    this.plainProperty = new WritableProperty("a");
    this.formattedProperty = new WritableProperty("a")
        .addFormatter(UpperCaseFormatter, {});

    this.plainEditableProperty = new EditableProperty("a");
    this.formattedEditableProperty = new EditableProperty("a")
        .addFormatter(UpperCaseFormatter, {});
    this.parsedEditableProperty = new EditableProperty("a")
        .addParser(LetterShifterParser, {});
    this.parsedFormattedEditableProperty = new EditableProperty("a")
        .addParser(LetterShifterParser, {})
        .addFormatter(UpperCaseFormatter, {});

    this.aliasPlain = new Alias(this.plainProperty);
    this.aliasFormatted = new Alias(this.formattedProperty);
};
Core.extend(TestPresentationModel, PresentationModel);

module.exports = TestPresentationModel;
