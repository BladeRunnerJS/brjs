package com.caplin.cutlass.command.test.testrunner;

import java.util.Formatter;

public class CmdCreator {
	public static String[] cmd(String cmd, Object... parameters) {
		System.out.println(">>> command: " + cmd);
		if(parameters.length > 0) {
			System.out.println(">>> parameters[0]: " + parameters[0]);
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		Formatter formatter = new Formatter(stringBuilder);
		formatter.format(cmd, parameters);
		formatter.close();
	  return stringBuilder.toString().replaceAll("\\$\\$", " ").split(" ");
	}
	
	public static String printCmd(String[] args) {
		StringBuffer stringBuffer = new StringBuffer();
		
		for (int i = 0, l = args.length; i < l; ++i) {
			if(i > 0) {
				stringBuffer.append(" ");
			}
			
			stringBuffer.append(args[i]);
		}
		
		return stringBuffer.toString();
	}
}
