package org.bladerunnerjs.testing.specutility.engine;

public class ModelCommander {
	public final SpecTest modelTest;
	
	public ModelCommander(SpecTest modelTest) {
		this.modelTest = modelTest;
	}
	
	protected <T extends Object> T call(ValueCommand<T> valueCommand) {
		T returnValue = null;
		
		try {
			modelTest.logging.storeLogsIfEnabled();
			returnValue = valueCommand.call();
			modelTest.logging.stopStoringLogs();
		}
		catch(Throwable e) {
			if (modelTest.catchAndVerifyExceptions) 
			{
				modelTest.exceptions.add(e);
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
