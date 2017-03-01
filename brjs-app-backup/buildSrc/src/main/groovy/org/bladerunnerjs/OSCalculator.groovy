package org.bladerunnerjs


class OSCalculator
{

	static String getOS()
	{
		def os = System.getProperty('os.name').split(' ')[0].toLowerCase()
		def osArch = System.getProperty('os.arch')

		if (os == 'windows') { os = 'win32' }
		if (os == 'osx') { os = 'mac' }
		if ((os == 'linux') && (osArch == 'amd64')) { os = 'linux64' }
		
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
