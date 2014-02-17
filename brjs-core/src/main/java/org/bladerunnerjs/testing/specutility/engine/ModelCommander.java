package org.bladerunnerjs.testing.specutility.engine;

public class ModelCommander {
	public final SpecTest specTest;
	
	public ModelCommander(SpecTest modelTest) {
		this.specTest = modelTest;
	}
	
	protected <T extends Object> T call(ValueCommand<T> valueCommand) {
		T returnValue = null;
		
		try {
			specTest.logging.storeLogsIfEnabled();
			returnValue = valueCommand.call();
			specTest.logging.stopStoringLogs();
		}
		catch(Throwable e) {
			if (specTest.catchAndVerifyExceptions) 
			{
				specTest.exceptions.add(e);
			}
			else
			{
				throw new RuntimeException(e);
			}
		}
		
		return returnValue;
	}
	
	protected void call(final Command command) {
		call(new ValueCommand<Void>() {
			@Override
			public Void call() throws Exception {
				command.call();
				return null;
			}
		});
	}
}
