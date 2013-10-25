package org.bladerunnerjs.specutil.engine;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Joiner;

public class ExceptionPrinter {
	public static String printExceptions(List<Throwable> exceptions) {
		List<String> exceptionMessages = new ArrayList<>();
		
		for(Throwable exception : exceptions) {
			exceptionMessages.add(printCauseChain(exception));
		}
		
		return Joiner.on(", ").join(exceptionMessages);
	}
	
	private static String printCauseChain(Throwable exception)
	{
		StringBuffer stringBuffer = new StringBuffer();
		Throwable cause = exception;
		int indentationSize = 0;
		
		do
		{
			List<String> causeParams = new ArrayList<>();
			String lineIndent = StringUtils.repeat("    ", indentationSize++);
			
			if(hasUniqueCauseMessage(cause)) {
				causeParams.add("\"" + cause.getMessage() + "\"");
			}
			
			if(cause.getCause() != null) {
				causeParams.add("e");
			}
			
			stringBuffer.append("\n" + lineIndent + cause.getClass().getSimpleName() + "(" + Joiner.on(", ").join(causeParams) + ")" +
				((cause.getCause() == null) ? "" : ":"));
			
			cause = cause.getCause();
		} while (cause != null);
		
		return stringBuffer.toString();
	}

	private static boolean hasUniqueCauseMessage(Throwable cause)
	{
		Throwable subCause = cause.getCause();
		String subCauseMessage = (subCause == null) ? null : subCause.getMessage();
		
		return((cause.getMessage() != null) && ((subCauseMessage == null) || !cause.getMessage().endsWith(subCauseMessage)));
	}
}