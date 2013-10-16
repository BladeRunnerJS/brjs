package org.bladerunnerjs


class OSCalculator
{

	static String getOS()
	{
		def os = System.getProperty('os.name').split(' ')[0].toLowerCase()
		if (os == 'windows') { os = 'win32' }
		if (os == 'osx') { os = 'mac' }
		
		return os
	}
	
	static boolean isWin32()
	{
		return getOS().equals("win32")
	}
	
	static ArrayList getOSSpecificCommand(String command)
	{
		return (isWin32()) ? ['cmd','/c',command] : ['./'+command]
	}

}
