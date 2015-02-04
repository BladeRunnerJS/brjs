package org.bladerunnerjs.utility.reader;

import java.util.Stack;

public class CharBufferPool {
	
	private static Stack<char[]> pool = new Stack<char[]>();
	
	/* 
	 * I know we hate static state but passing this around from class to class makes for some horrendous interfaces. 
	 * Given this is a utility to prevent us using more memory than absolutely necessary this seems like the lesser of two evils. 
	 */
	public static synchronized char[] getBuffer(){
		char[] result = null;
		if(pool.isEmpty()){
			result = new char[4096];
		}else{
			result = pool.pop();
		}
		return result;
	}
	
	public static synchronized void returnBuffer(char[] buffer){
		pool.push(buffer);
	}
}


