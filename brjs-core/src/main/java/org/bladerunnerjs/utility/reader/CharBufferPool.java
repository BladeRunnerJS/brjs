package org.bladerunnerjs.utility.reader;

import java.util.Stack;

public class CharBufferPool {
	
	private static Stack<char[]> pool = new Stack<char[]>();
	
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
