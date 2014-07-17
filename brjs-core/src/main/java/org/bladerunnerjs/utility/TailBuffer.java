package org.bladerunnerjs.utility;

import java.util.Arrays;


/**
 * A "tail buffer". It keeps the finite number of elements that have been added last.
 * Implemented by a ring buffer.
 * TailBuffer can be used to keep the "last X characters that have been read" while reading a character stream. 
 * (Note: the implementation is not thread-safe.)
 */
public final class TailBuffer
{
    // temporary
    private static final int MAX_BUFFER_SIZE = 4096;
    private static final int DEF_BUFFER_SIZE = 256;
    private static final int MIN_BUFFER_SIZE = 8;
    // ...

    // Internal buffer.
    private  char[] buffer;
    // Ring buffer size.
    private  int maxSize;
    
    // We need two indexes.
    // They run in the range [0, maxSize)
    // For example,, if the buffer look like this: [0---h----t------]
    //   then the data is contained in the range from h (inclusive) to t (exclusive).
    // If a new data is added at the tail, the head part can be erased.
    // A more typical example: [0---t*h-------], where * is the one slot that is not being used.
    // When t is incremented, h will be incremented by the same amount.
    private int tailPointer = 0;
    private int headPointer = 0;

    public TailBuffer()
    {
        this(DEF_BUFFER_SIZE);
    }
    public TailBuffer(int maxSize)
    {
        if(maxSize < MIN_BUFFER_SIZE) {
            this.maxSize = MIN_BUFFER_SIZE;
        } else if(maxSize > MAX_BUFFER_SIZE) {
            this.maxSize = MAX_BUFFER_SIZE;
        } else {
            this.maxSize = maxSize + 1;
        }
        buffer = new char[this.maxSize];
    }

    // Circular increment operator.
    // Note that the tail pointer increments when new data is added, but never decrements. 
    // The head pointer merely follows (is pushed by) the tail, and it cannot be moved indepedently.
    private void incrementTail()
    {
        incrementTail(1);
    }
    // 0 < delta < maxSize
    private void incrementTail(int delta)
    {
        int oldMargin = margin();
        tailPointer += delta;
        if(tailPointer >= maxSize) {
            tailPointer %= maxSize;
        }
        if(oldMargin < delta) {
            int push = delta - oldMargin;
            headPointer += push;
            if(headPointer >= maxSize) {
                headPointer %= maxSize;
            }
        }
    }
    // Returns the index of the "last element" (just before the tailPointer).
    private int lastIndex()
    {
        return lastNthIndex(1);
    }
    // 0 < n < maxSize
    private int lastNthIndex(int n)
    {
        if(tailPointer >= n) {
            return tailPointer - n;
        } else {
            int x = n - tailPointer;
            return maxSize - x;
        }
    }

    // Returns the size of the "empty" slots.
    // (Not all empty slots are usable though...)
    // We use one slot as a collision buffering zone.
    public int margin()
    {
        // Note the -1.
        int margin;
        if(tailPointer < headPointer) {
            margin = headPointer - tailPointer - 1;
        } else {
            margin = maxSize - (tailPointer - headPointer) - 1;
        }
        return margin;
    }

    // Because of the one empty slot buffering,
    // the "usable size" is maxSize - 1.
    public int maxCapacity()
    {
        return this.maxSize - 1;
    }

    // Returns the size of the data.
    public int size()
    {
        int size;
        if(tailPointer < headPointer) {
            size = maxSize + tailPointer - headPointer;
        } else {
            size = tailPointer - headPointer;
        }
        return size;
    }

    // Returns true if there is no data in the buffer.
    public boolean isEmpty()
    {
        if(tailPointer == headPointer) {
            return true;
        } else {
            return false;
        }
    }

    // Adds the given char to the ring buffer.
    // If the buffer is full, then it returns false.
    public boolean push(char ch)
    {
        buffer[tailPointer] = ch;
        incrementTail();
        return true;
    }
    public boolean push(char[] c)
    {
        if(c == null || c.length == 0) {
            return false;
        }
        int len = c.length;
        return push(c, len);
    }
    // Adds the char array c to the buffer, up to length, but no more than the c.size().
    public boolean push(char[] c, int length)
    {
        if(c == null || c.length == 0) {
            return false;
        }
        int len = c.length;
        if(len < length) {
            length = len;
        }
        if(tailPointer + length < maxSize) {
            System.arraycopy(c, 0, buffer, tailPointer, length);
        } else {
            int first = maxSize - tailPointer;
            int second = length - first;
            System.arraycopy(c, 0, buffer, tailPointer, first);
            System.arraycopy(c, first, buffer, 0, second);
        }
        incrementTail(length);
        return true;
    }

    // tail() returns the char at the end of the data buffer.
    public char tail()
    {
        if(isEmpty()) {
            return 0;
        }
        char ch = buffer[lastIndex()];
        return ch;
    }
    // Peeks the chars at the tail part of the buffer.
    // If the buffer contains less than length chars, it returns all chars (same as toArray()).
    public char[] tail(int length)
    {
        if(isEmpty()) {
            // return null;
            return new char[]{};
        }
        if(length > size()) {
            length = size();
        }
        char[] tail = new char[length];
        int begin = lastNthIndex(length);
        if(begin + length < maxSize) {
            System.arraycopy(buffer, begin, tail, 0, length);
        } else {
            int first = maxSize - begin;
            int second = length - first;
            System.arraycopy(buffer, begin, tail, 0, first);
            System.arraycopy(buffer, 0, tail, first, second);
        }
        return tail;
    }

    // Returns the copy of the entire data buffer, as a regular array.
    public char[] toArray()
    {
        if(isEmpty()) {
            // return null;
            return new char[]{};
        }
        int length = size();
        char[] copied = new char[length];
        if(headPointer + length < maxSize) {
            System.arraycopy(buffer, headPointer, copied, 0, length);
        } else {
            int first = maxSize - headPointer;
            int second = length - first;
            System.arraycopy(buffer, headPointer, copied, 0, first);
            System.arraycopy(buffer, 0, copied, first, second);
        }
        return copied;
    }

    // Remove the data from the buffer.
    public void clear()
    {
        headPointer = tailPointer = 0;
    }

    // For debugging...
    @Override
    public String toString()
    {
        return "CharBuffer [buffer=" + Arrays.toString(tail(100)) + ", maxSize="
                + maxSize + ", tailPointer=" + tailPointer + ", headPointer="
                + headPointer + "]";
    }
    
}
