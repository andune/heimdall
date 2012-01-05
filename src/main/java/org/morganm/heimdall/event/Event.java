/**
 * 
 */
package org.morganm.heimdall.event;

/** General event interface that all Heimdall Event types will implement.
 * 
 * @author morganm
 *
 */
public interface Event {
	public enum Type {
		BLOCK_CHANGE,
		INVENTORY_CHANGE,
		CHAT_MESSAGE
	}
	
	/** Clear the event object of any data.
	 * 
	 */
	public void clear();
	
	public Type getType();
	
	/** Visitor pattern.
	 * 
	 */
	public void accept(EventHandler visitor);
}
