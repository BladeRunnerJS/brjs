package org.bladerunnerjs.utility;


public final class FixedLengthStringBuilder
{
    private static final int DEFAULT_BUFFER_SIZE = 256;

    private StringBuilder buffer = new StringBuilder();
    private int maxSize;
    
    public FixedLengthStringBuilder()
    {
        this(DEFAULT_BUFFER_SIZE);
    }
    
    public FixedLengthStringBuilder(int maxSize)
    {
    	this.maxSize = maxSize;
    }

    public void append(char ch)
    {
    	buffer.append(ch);
    	if (buffer.length() > maxSize) {
    		buffer.deleteCharAt(0);
    	}
    }
    
    public String toString() {
    	return buffer.toString();
    }
    
}
