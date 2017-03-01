package org.bladerunnerjs.api.spec.logging;

import org.bladerunnerjs.api.spec.engine.Command;
import org.bladerunnerjs.api.spec.engine.ModelCommander;
import org.bladerunnerjs.api.spec.engine.SpecTest;

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
				specTest.logging.addError(LOGGER_NAME, errorMessage, params);
			}
		});
	}
	
	public void whenInfoMessageLogged(final String errorMessage, final Object... params) {
		call(new Command() {
			@Override
			public void call() throws Exception {
				specTest.logging.addInfo(LOGGER_NAME, errorMessage, params);
			}
		});
	}
	
	public void whenDebugMessageLogged(final String errorMessage, final Object... params) {
		call(new Command() {
			@Override
			public void call() throws Exception {
				specTest.logging.addDebug(LOGGER_NAME, errorMessage, params);
			}
		});
	}
}