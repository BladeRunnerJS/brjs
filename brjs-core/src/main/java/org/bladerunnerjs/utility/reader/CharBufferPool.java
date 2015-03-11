package org.bladerunnerjs.utility.reader;

import java.util.Stack;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.model.engine.NodeProperties;

public class CharBufferPool {
	
	private static final String PROPERTY_ID = "BufferPoolInstance";
	private final Stack<char[]> pool = new Stack<char[]>();
	
	public static synchronized char[] getBuffer(BRJS brjs) {
		return getCharBufferPool(brjs).getOrCreateBuffer();
	}
	
	public static synchronized void returnBuffer(BRJS brjs, char[] buffer){
		getCharBufferPool(brjs).pushBuffer(buffer);
	}

	

	private static synchronized CharBufferPool getCharBufferPool(BRJS brjs) {
		NodeProperties nodeProperties = brjs.nodeProperties(CharBufferPool.class.getSimpleName());
		Object property = nodeProperties.getTransientProperty(PROPERTY_ID);
		CharBufferPool nodeBufferPool;
		if (!(property instanceof CharBufferPool)) {
			nodeBufferPool = new CharBufferPool();
			nodeProperties.setTransientProperty(PROPERTY_ID, nodeBufferPool);
		} else {
			nodeBufferPool = (CharBufferPool) property;
		}
		return nodeBufferPool;
	}
	
	
	CharBufferPool() {
	}	
		
		
	private char[] getOrCreateBuffer() {
		char[] result = null;
		if(pool.isEmpty()){
			result = new char[4096];
		}else{
			result = pool.pop();
		}
		return result;
	}

	private void pushBuffer(char[] buffer)
	{
		pool.push(buffer);
	}

}


