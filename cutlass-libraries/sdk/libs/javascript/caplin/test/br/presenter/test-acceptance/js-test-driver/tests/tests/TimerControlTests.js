br.test.GwtTestRunner.initialize();

"br.presenter.component.PresenterComponentFactory";

describe("Timer Fixtures use for manually and automatically executing time delayed functions", function(){
  fixtures("TimerControlFixtureFactory");
  
  /*
   * Next Step Mode
   */
  
  it("automatically executes a delayed function at the end of the current step", function(){
	given("component.opened = true");
		and("time.timeMode = 'NextStep'");
		and("component.model.counter = 0");
	when("component.model.addOneToCounterAfterOneSecond.invoked => true");
	then("component.model.counter = 1");	  
  
  })
  
   it("fires delayed functions within a step at the end of each current step in nextStep mode", function(){
	given("component.opened = true");
		and("time.timeMode = 'NextStep'");
		and("component.model.name = 'Caplin'");	 
	when("component.model.addTheLetterCtoNameAfter10Minutes.invoked => true");
		and("component.model.addTheLetterBtoNameAfter5Minutes.invoked => true");
		and("component.model.addTheLetterAtoNameAfter1Minute.invoked => true");
	then("component.model.name = 'CaplinCBA'");	
  
  })
  
   it("fires a sequence of delayed functions within a step in the correct time order", function(){
	given("component.opened = true");
		and("time.timeMode = 'NextStep'");
		and("component.model.name = 'Caplin'");	 
	when("component.model.addsTheLetterAafter1MinuteThenBafter2Minutes.invoked => true");
	then("component.model.name = 'CaplinAB'");	
  
  })  
  
  /*
   * Manual Mode
   */
  //it does not execute delayed functions, when time is not manually advanced, in manual mode
  it("it does not execute delayed functions, when time is not manually advanced, in manual mode", function(){
	given("component.opened = true");
		and("time.timeMode = 'Manual'");
		and("component.model.counter = 0");
	when("component.model.addOneToCounterAfterOneSecond.invoked => true");
	then("component.model.counter = 0");	  
  
  })
  
  it("it does not execute delayed functions, when the delay time is not yet reached, in manual mode", function(){
	given("component.opened = true");
		and("time.timeMode = 'Manual'");
		and("component.model.counter = 0");
	when("component.model.addOneToCounterAfterOneSecond.invoked => true");
		and("time.passedBy => 500"); //milliseconds
	then("component.model.counter = 0");	  
	  
  })    
  
  
  it("executes delayed functions in manual mode when the required delay time passes by", function(){
	given("component.opened = true");	 
		and("time.timeMode = 'Manual'");
		and("component.model.counter = 0");	
	when("component.model.addOneToCounterAfterOneSecond.invoked => true");
		and("time.passedBy => 1000"); 	
	then("component.model.counter = 1");
					
  });  
  
  //this is to ensure continuesFrom is supported
  it("can continue to execute a delayed function in manual mode following on from a previous execution", function(){
	given("test.continuesFrom = 'executes delayed functions in manual mode when the required delay time passes by'");
	when("component.model.addOneToCounterAfterOneSecond.invoked => true");
		and("time.passedBy => 1000");
	then("component.model.counter = 2");
  
  })
  
   it("fires delayed functions in the correct time order in Manual mode even when executions occur in seperate steps", function(){
	given("component.opened = true");
		and("time.timeMode = 'Manual'");
		and("component.model.name = 'Caplin'");	 
	when("component.model.addTheLetterCtoNameAfter10Minutes.invoked => true");
		and("component.model.addTheLetterBtoNameAfter5Minutes.invoked => true");
		and("component.model.addTheLetterAtoNameAfter1Minute.invoked => true");
		and("time.passedBy => 610000");
	then("component.model.name = 'CaplinABC'");	
  
  })  

  it("can execute interleaved delayed functions in manual mode", function(){
	given("component.opened = true");
		and("time.timeMode = 'Manual'");
		and("component.model.status = 'Up'");
		and("component.model.counter = 0");	
	when("component.model.addOneToCounterAfterOneSecond.invoked => true");	
		and("component.model.changeStatusToDownAfter5Seconds.invoked => true");	
		and("time.passedBy => 2000");
	then("component.model.status = 'Up'");
		and("component.model.counter = 1");
  })
  
})