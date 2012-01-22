/**
 * 
 */
package org.morganm.heimdall.log;

import java.io.IOException;

/**
 * @author morganm
 *
 */
public interface LogInterface {
	public void close();
	public void flush() throws IOException;
}
