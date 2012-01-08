/**
 * 
 */
package org.morganm.heimdall.util;

import java.lang.reflect.Array;


/** Circular buffer that holds objects. The intent is that this can be used to
 * hold same-class event types and the objects themselves are then just re-used (they
 * are never deleted).
 * 
 * @author morganm
 *
 */
public class CircularBuffer<E> {
	private final E[] objects;
	private final boolean nullOnPop;
	private final int bufferSize;
	/* If bufferWrap is true, it means this buffer will never be popped from.
	 * 
	 */
	private boolean bufferWrap;
	private final Class<E> objectClass;
	
	private int start = 0;
    private int end = 0;
	
    /** Create a CircularBuffer of the given size
     * 
     * @param bufferSize the number of elements the buffer should hold
     * @param nullOnPop if true, when pop is called, the array element will be nulled. This allows GC to cleanup
     * the object (once all other references are gone) by not holding onto the reference indefinitely.
     */
	@SuppressWarnings("unchecked")
	public CircularBuffer(Class<E> objectClass, int bufferSize, boolean nullOnPop) {
		this.objectClass = objectClass;
		this.bufferSize = bufferSize;
		this.nullOnPop = nullOnPop;
		this.objects = (E[]) Array.newInstance(this.objectClass, bufferSize);
//		this.events = new E[bufferSize];
	}
	public CircularBuffer(Class<E> objectClass, int bufferSize, boolean nullOnPop, boolean bufferWrap) {
		this(objectClass, bufferSize, nullOnPop);
		this.bufferWrap = true;
	}

	/** Pop an object out of the buffer.
	 * This actually just moves circular buffer
	 * pointers, since we are not nulling out the underlying object.
	 * 
	 * @return
	 */
	public E pop() {
		// empty buffer. We also don't allow pop if bufferWrap is true.
		if( start == end || bufferWrap )
			return null;
		
		synchronized (this) {
			if( ++start >= bufferSize )
				start = 0;
		}
		
		E event = objects[start];
		if( nullOnPop )
			objects[start] = null;
		
		return event;
	}
	
	/** Used to get the next object in the buffer, if any. The purpose here is if nullOnPop is false,
	 * the objects in the buffer will just be continually re-used rather than allocating new objects
	 * all the time. This method allows retrieval of the next "available to be used" object. If said
	 * object is null, a new object will be created and returned.
	 * 
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public E getNextObject() throws InstantiationException, IllegalAccessException {
		if( ++end >= bufferSize )
			end = 0;
		
		// if the buffer is full, increment the start (essentially losing that object)
		// TODO: allow buffer overflow
		if( !bufferWrap && end == start ) {
			synchronized (this) {
				if( ++start >= bufferSize )
					start = 0;
			}
			// TODO: consider logging or throwing error here, this means we just wrapped a full buffer
		}

		if( objects[end] == null )
			objects[end] = objectClass.newInstance();

		return objects[end];
	}

	/** If this circular buffer is being used in "nullOnPop" mode (meaning objects are not being
	 * re-used), then you probably want to use this method to push new event references into the
	 * buffer. On the other hand, if you intend to re-use existing objects, you should use
	 * getNextObject() method instead.
	 * 
	 * @param event
	 */
	public void push(E event) {
		if( ++end >= bufferSize )
			end = 0;
		
		// if the buffer is full, increment the start (essentially losing that object)
		// TODO: allow buffer overflow
		if( !bufferWrap &&  end == start ) {
			synchronized (this) {
				if( ++start >= bufferSize )
					start = 0;
			}
			// TODO: consider logging or throwing error here, this means we just wrapped a full buffer
		}

		objects[end] = event;
	}
}