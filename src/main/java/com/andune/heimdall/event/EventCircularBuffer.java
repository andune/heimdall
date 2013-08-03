/**
 * 
 */
package com.andune.heimdall.event;

import com.andune.heimdall.util.CircularBuffer;


/** Circular buffer that only allows objects that implement the Event interface.
 * 
 * @author andune
 *
 */
public class EventCircularBuffer<E extends Event> extends CircularBuffer<E> {
	/*
	private final E[] events;
	private final boolean nullOnPop;
	private final int bufferSize;
	private final Class<E> eventClass;
	
	private int start = 0;
    private int end = 0;
    */
	
    /** Create an event CircularBuffer of the given size
     * 
     * @param bufferSize the number of elements the buffer should hold
     * @param nullOnPop if true, when pop is called, the array element will be nulled. This allows GC to cleanup
     * the object (once all other references are gone) by not holding onto the reference indefinitely.
     */
	public EventCircularBuffer(Class<E> eventClass, int bufferSize, boolean nullOnPop) {
		super(eventClass, bufferSize, nullOnPop);
	}
	public EventCircularBuffer(Class<E> eventClass, int bufferSize, boolean nullOnPop, boolean bufferWrap) {
		super(eventClass, bufferSize, nullOnPop, bufferWrap);
	}
	/*
	@SuppressWarnings("unchecked")
	public EventCircularBuffer(Class<E> eventClass, int bufferSize, boolean nullOnPop) {
		this.eventClass = eventClass;
		this.bufferSize = bufferSize;
		this.nullOnPop = nullOnPop;
		this.events = (E[]) Array.newInstance(eventClass, bufferSize);
//		this.events = new E[bufferSize];
	}
	*/

	/** Pop an event out of the buffer.
	 * This actually just moves circular buffer
	 * pointers, since we are not nulling out the underlying object.
	 * 
	 * @return
	 */
	/*
	public E pop() {
		// empty buffer
		if( start == end )
			return null;
		
		synchronized (this) {
			if( ++start >= bufferSize )
				start = 0;
		}
		
		E event = events[start];
		if( nullOnPop )
			events[start] = null;
		
		return event;
	}
	*/
	
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
		E event = super.getNextObject();
		// make sure the event has been cleared
		if( !event.isCleared() )
			event.clear();
		return event;
	}
	/*
	public E getNextObject() throws InstantiationException, IllegalAccessException {
		if( ++end >= bufferSize )
			end = 0;
		
		// if the buffer is full, increment the start (essentially loosing that object)
		// TODO: allow buffer overflow
		if( end == start ) {
			synchronized (this) {
				if( ++start >= bufferSize )
					start = 0;
			}
			// TODO: consider logging or throwing error here, this means we just wrapped a full buffer
		}

		if( events[end] == null )
			events[end] = eventClass.newInstance();

		return events[end];
	}
	*/

	/** If this circular buffer is being used in "nullOnPop" mode (meaning objects are not being
	 * re-used), then you probably want to use this method to push new event references into the
	 * buffer. On the other hand, if you intend to re-use existing objects, you should use
	 * getNextObject() method instead.
	 * 
	 * @param event
	 */
	/*
	public void push(E event) {
		if( ++end >= bufferSize )
			end = 0;
		
		// if the buffer is full, increment the start (essentially loosing that object)
		// TODO: allow buffer overflow
		if( end == start ) {
			synchronized (this) {
				if( ++start >= bufferSize )
					start = 0;
			}
			// TODO: consider logging or throwing error here, this means we just wrapped a full buffer
		}

		events[end] = event;
	}
	*/
}