br.test.GwtTestRunner.initialize();

describe("Focus Tests",function() {
	fixtures("FocusFixtureFactory");
		
	xit("test the focus presentation model works",function() {
		given("focusform.viewOpened = true");
		when ("focusform.model.inputA.hasFocus => true");
		then("focusform.model.inputA.hasFocus = true");
	});
	
	xit("binds correctly to the DOM",function() {
		given("test.continuesFrom = 'test the focus presentation model works'");
		then ("focusform.view.(#inputA input).focused = true");
	});
	
	xit("gives focus to second input when clicked and removes focus from first",function() {
		given("test.continuesFrom = 'test the focus presentation model works'");
		when ("focusform.view.(#inputB input).clicked => true");
		then ("focusform.model.inputB.hasFocus = true");
			and("focusform.model.inputA.hasFocus = false");
			and("focusform.view.(#inputB input).focused = true");
	});
	
	xit("clicks second input removing focus from first one",function() {
		given("test.continuesFrom = 'test the focus presentation model works'");
		when ("focusform.view.(#inputB input).clicked => true");
		then ("focusform.model.inputB.hasFocus = true");
			and("focusform.model.inputA.hasFocus = false");  
	});	
	
	xit("doesnt remove focus when typing keys on an already-focused field",function() {
		given("test.continuesFrom = 'test the focus presentation model works'");
		when ("focusform.view.(#inputA input).typedValue => 'hello'");
		then("focusform.model.inputA.hasFocus = true");
			and("focusform.view.(#inputA input).value = 'hello'");
			and("focusform.view.(#inputA input).focused = true");
	});	
	
	
	xit("changes focus correctly when typing keys on a different input",function() {
		given("test.continuesFrom = 'test the focus presentation model works'");
		when ("focusform.view.(#inputB input).typedValue => 'B'");
		then("focusform.model.inputA.hasFocus = false");
			and("focusform.model.inputB.hasFocus = true");
			and("focusform.view.(#inputB input).focused = true");
	});	

	
});
