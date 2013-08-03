/**
 *
 */
package com.andune.heimdall.log;

import java.io.IOException;

/**
 * @author andune
 */
public interface LogInterface {
    public void close();

    public void flush() throws IOException;
}
