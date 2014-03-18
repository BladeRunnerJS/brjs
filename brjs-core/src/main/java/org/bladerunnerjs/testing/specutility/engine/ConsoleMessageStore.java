package org.bladerunnerjs.testing.specutility.engine;

import static org.junit.Assert.*;
import static org.bladerunnerjs.testing.utility.BRJSAssertions.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.bladerunnerjs.testing.utility.LogMessage;

import com.google.common.base.Joiner;


public class ConsoleMessageStore {
	private LinkedList<String> messages = new LinkedList<String>();
	private List<String> consoleOutput = new ArrayList<>();
	
	public void verifyContainsLine(String message, Object... params) { 
		boolean messageRemoved = messages.remove( new LogMessage(message, params).toString() );
		assertTrue( String.format("No output line was found for the message %s params(%s), actual output: \n", message, ArrayUtils.toString(params)) + getFormattedOutputMessage(), messageRemoved );
		
	}

	private String getFormattedOutputMessage() {
		String actualMessage = messages.toString();
		actualMessage = actualMessage.replace("[],", "[]\n");
		return actualMessage;
	}
	
	public void verifyDoesNotContain(String message, Object... params) {
		boolean messageRemoved = messages.remove(new LogMessage(message, params).toString());
		assertFalse("Output contains message, but shouldn't, actual output: /n" + getFormattedOutputMessage(),messageRemoved);
	}
	
	public void verifyContainsText(String[] text) {
		String actualConsoleOutput = Joiner.on("\n").join(consoleOutput);
		String expectedConsoleOutput = Joiner.on("\n").join(text);
		
		assertContains(expectedConsoleOutput, actualConsoleOutput);
	}
	
	public void verifyDoesNotContainText(String[] text) {
		String actualConsoleOutput = Joiner.on("\n").join(consoleOutput);
		String expectedConsoleOutput = Joiner.on("\n").join(text);
		
		assertDoesNotContain(expectedConsoleOutput, actualConsoleOutput);
	}
	
	public void add(String message, Object[] params) {
		messages.add(new LogMessage(message, params).toString());
		consoleOutput.add(String.format(message, params));
	}

	public void verifyNoMoreOutput() {
		assertTrue("There are messages on the output stream: " + getFormattedOutputMessage(),messages.isEmpty());
		
	}
}
