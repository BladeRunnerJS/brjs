package org.bladerunnerjs.specutil.engine;


public class ConsoleWriterVerifier
{
	private final VerifierChainer verifierChainer;
	private final ConsoleMessageStore consoleMessageStore;
	
	public ConsoleWriterVerifier(SpecTest modelTest, ConsoleMessageStore consoleMessageStore) {
		this.consoleMessageStore = consoleMessageStore;
		verifierChainer = new VerifierChainer(modelTest);
	}
	
	public VerifierChainer containsLine(String message, Object... params) {
		consoleMessageStore.verifyContainsLine(message, params);
		
		return verifierChainer;
	}
	
	public VerifierChainer doesNotContain(String message, Object... params) {
		consoleMessageStore.verifyDoesNotContain(message, params);
		
		return verifierChainer;
	}
	
	public VerifierChainer containsText(String... text) {
		consoleMessageStore.verifyContainsText(text);
		
		return verifierChainer;
	}
	
	public VerifierChainer noMoreOutput() {
		consoleMessageStore.verifyNoMoreOutput();
		return verifierChainer;
		
	}
}
