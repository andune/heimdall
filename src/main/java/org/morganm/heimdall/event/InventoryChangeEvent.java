/**
 * 
 */
package org.morganm.heimdall.event;

/**
 * @author morganm
 *
 */
public class InventoryChangeEvent implements Event {

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return null;
	}

	/** Visitor pattern.
	 * 
	 */
	@Override
	public void accept(EventHandler visitor) {
		visitor.processEvent(this);
	}

}
