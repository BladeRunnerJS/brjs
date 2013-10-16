package com.caplin.cutlass.command.test.testrunner;

import java.util.Formatter;

public class CmdCreator {
	public static String[] cmd(String cmd, Object... parameters) {
		StringBuilder stringBuilder = new StringBuilder();
		Formatter formatter = new Formatter(stringBuilder);
		formatter.format(cmd.replaceAll(" ", "\\$\\$"), parameters);
		formatter.close();
	  return stringBuilder.toString().split("\\$\\$");	
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
