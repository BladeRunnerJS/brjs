package org.bladerunnerjs.legacy.command.test.testrunner;

import java.io.File;
import java.util.Formatter;

public class CmdCreator {
	public static String[] cmd(File sdkDir, String cmd, Object... parameters) {
		StringBuilder stringBuilder = new StringBuilder();
		Formatter formatter = new Formatter(stringBuilder);
		formatter.format(cmd.replaceAll(" ", "\\$\\$"), parameters);
		formatter.close();
		String[] cmdArgs = stringBuilder.toString().split("\\$\\$");
		
		for(int i = 0; i < cmdArgs.length; ++i) {
			String cmdArg = cmdArgs[i];
			
			if(cmdArg.startsWith("../")) {
				cmdArgs[i] = new File(sdkDir, cmdArg).getAbsolutePath();
			}
		}
		
		return cmdArgs;
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
