
module.exports = {
	'br.presenter.tooltip-helper': function() {
		return require('./control/tooltip/DefaultTooltipHelper');
	},
	'br.date-picker': function() {
		return require('./control/datefield/JQueryDatePickerControl');
	},
	'br.toggle-switch': function() {
		return require('./control/selectionfield/ToggleSwitchControl');
	},
	'br.autocomplete-box': function() {
		return require('./control/selectionfield/JQueryAutoCompleteControl');
	},
	'br.tooltip': function() {
		return require('./control/tooltip/TooltipControl');
	}
};
