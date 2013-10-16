package org.bladerunnerjs.specutil.engine;

import org.bladerunnerjs.core.console.ConsoleWriter;

public class ConsoleStoreWriter implements ConsoleWriter {
	private final ConsoleMessageStore consoleMessageStore;
	
	public ConsoleStoreWriter(ConsoleMessageStore consoleMessageStore) {
		this.consoleMessageStore = consoleMessageStore;
	}
	
	@Override
	public void println(String message, Object... params) {
		consoleMessageStore.add(message, params);
	}
	
	@Override
	public void println() {
		println("");
	}
	
	@Override
	public void flush() {
		// do nothing
	}
}
