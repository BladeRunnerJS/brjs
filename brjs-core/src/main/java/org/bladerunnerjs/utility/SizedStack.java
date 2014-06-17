package org.bladerunnerjs.utility;

import java.util.Stack;


public class SizedStack<T> extends Stack<T> {
	private static final long serialVersionUID = 7486740786792610215L;
	private int maxSize;

    public SizedStack(int size) {
        super();
        this.maxSize = size;
    }

    @Override
    public T push(T object) {
        while (this.size() > maxSize) {
            this.remove(0);
        }
        return super.push((T) object);
    }
    
	@Override
    public synchronized String toString()
    {
    	StringBuilder buffer = new StringBuilder();
    	for (T t : this) {
    		buffer.append(t.toString());
    	}
    	return buffer.toString();
    }
}