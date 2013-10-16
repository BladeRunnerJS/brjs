package org.bladerunnerjs.specutil.logging;

import org.bladerunnerjs.specutil.engine.Command;
import org.bladerunnerjs.specutil.engine.ModelCommander;
import org.bladerunnerjs.specutil.engine.SpecTest;

public class ModelLoggingCommander extends ModelCommander {
	private static final String LOGGER_NAME = "undefined";
	
	public ModelLoggingCommander(SpecTest modelTest) {
		super(modelTest);
		// TODO Auto-generated constructor stub
	}
	
	public void whenNoMessagesLogged() {
		// do nothing
	}
	
	public void whenErrorMessageLogged(final String errorMessage, final Object... params) {
		call(new Command() {
			@Override
			public void call() throws Exception {
				modelTest.logging.addError(LOGGER_NAME, errorMessage, params);
			}
		});
	}
	
	public void whenInfoMessageLogged(final String errorMessage, final Object... params) {
		call(new Command() {
			@Override
			public void call() throws Exception {
				modelTest.logging.addInfo(LOGGER_NAME, errorMessage, params);
			}
		});
	}
	
	public void whenDebugMessageLogged(final String errorMessage, final Object... params) {
		call(new Command() {
			@Override
			public void call() throws Exception {
				modelTest.logging.addDebug(LOGGER_NAME, errorMessage, params);
			}
		});
	}
}