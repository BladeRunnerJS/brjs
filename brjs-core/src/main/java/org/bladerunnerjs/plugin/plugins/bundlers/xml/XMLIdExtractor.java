package org.bladerunnerjs.plugin.plugins.bundlers.xml;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple utility that extracts a list of id's from XML without the overhead of a full XML parser.
 * @author jamest
 *
 */
public class XMLIdExtractor  {

	private static final int OUTSIDE = 1;
	private static final int GOT_AN_I = 2;
	private static final int GOT_AN_ID = 3;
	private static final int GOT_AN_ID_DOUBLEQUOTE = 4;
	private static final int GOT_AN_ID_SINGLEQUOTE = 5;
	
	private int state = OUTSIDE;
	private StringBuffer buffer = new StringBuffer();
	
	
	public List<String> getXMLIds(Reader reader) 
	{
		List<String> result = new ArrayList<String>();
		buffer.delete(0, buffer.length());
		state = OUTSIDE;
		
		int nextInt;
		try {
			while( (nextInt =  reader.read()) != -1 ){
				char nextChar = (char)nextInt;
				processNextChar(nextChar, result);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	
	//TODO throw an exception if we get a line break inside an id ?
	private void processNextChar(char nextChar, List<String> result) {
		
		switch(state)
		{
			case OUTSIDE:
				if(nextChar == 'I' || nextChar == 'i' ) {state = GOT_AN_I;}
				break;
			case GOT_AN_I:
				if(nextChar == 'D' || nextChar == 'd') {state = GOT_AN_ID;}
				break;
			case GOT_AN_ID:
				if(nextChar == '\'') {state = GOT_AN_ID_SINGLEQUOTE;}
				if(nextChar == '"') {state = GOT_AN_ID_DOUBLEQUOTE;}
				break;
			case GOT_AN_ID_SINGLEQUOTE:
				if(nextChar == '\'' ) {
					state = OUTSIDE;
					result.add(buffer.toString());
					buffer.delete(0, buffer.length());
				}else{
					buffer.append(nextChar);
				}
				break;
			case GOT_AN_ID_DOUBLEQUOTE:
				if(nextChar == '"' ) {
					state = OUTSIDE;
					result.add(buffer.toString());
					buffer.delete(0, buffer.length());
				}else{
					buffer.append(nextChar);
				}
				break;
		}
		
	}
}
