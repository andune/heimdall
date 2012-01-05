/**
 * 
 */
package org.morganm.heimdall.engine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/** Class that implements logging for an engine, so it can spit out details about
 * what it is doing to a log file.
 * 
 * @author morganm
 *
 */
public class EngineLog {
	private File logFile;
	private Writer writer;
	
	public EngineLog(File logFile) {
		if( logFile == null )
			throw new NullPointerException("logFile is null!");
		this.logFile = logFile;
	}

	public void log(String msg) throws IOException {
		try {
			writer.write(msg);
		}
		// if we fail first try, close and re-open the file and try writing again
		// if it fails on this 2nd try, the 2nd exception will be thrown to the caller
		catch (IOException e) {
			if( writer != null )
				writer.close();
			
			init();
			writer.write(msg);
		}
	}
	
	public void init() throws IOException {
		if( !logFile.exists() ) {
			File path = new File(logFile.getParent());
			if( !path.exists() )
				path.mkdirs();
		}
		
//		writer = new BufferedWriter(new FileWriter(logFile));
		writer = new FileWriter(logFile);
	}
}
