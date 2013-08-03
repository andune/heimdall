/**
 *
 */
package com.andune.heimdall.engine;

import com.andune.heimdall.Heimdall;
import com.andune.heimdall.log.LogInterface;
import com.andune.heimdall.util.Debug;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class that implements logging for an engine, so it can spit out details about
 * what it is doing to a log file.
 *
 * @author andune
 */
public class EngineLog implements LogInterface {
    // though this should be externally flushed for consistent performance, at the very
    // least we will flush every TIME_BETWEEN_FLUSH seconds at each call.
    private static final int TIME_BETWEEN_FLUSH = 10000;

    private final Heimdall plugin;
    private final File logFile;
    private BufferedWriter writer;
    private boolean isInitialized = false;
    private long lastFlush = 0;

    public EngineLog(final Heimdall plugin, final File logFile) {
        this.plugin = plugin;
        if (logFile == null)
            throw new NullPointerException("logFile is null!");
        this.logFile = logFile;

        this.plugin.addLogger(this);
    }

    public void close() {
        if (writer != null) {
            try {
                writer.close();
                writer = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void log(final String msg) throws IOException {
        Debug.getInstance().debug("log(): msg=", msg);
        if (!isInitialized)
            init();

        try {
            writer.write(msg);
            writer.newLine();

            if ((System.currentTimeMillis() - lastFlush) > TIME_BETWEEN_FLUSH)
                flush();
        }
        // if we fail first try, close and re-open the file and try writing again
        // if it fails on this 2nd try, the 2nd exception will be thrown to the caller
        catch (IOException e) {
            if (writer != null)
                writer.close();

            init();
            writer.write(msg);
        }
    }

    /**
     * Log a message, but if there is an error, just ignore the error.
     *
     * @param msg
     */
    public void logIgnoreError(final String msg) {
        try {
            this.log(msg);
        } catch (IOException e) {
        }
    }

    public void flush() {
        if (writer != null) {
            try {
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        lastFlush = System.currentTimeMillis();
    }

    public void init() throws IOException {
        if (!logFile.exists()) {
            File path = new File(logFile.getParent());
            if (!path.exists())
                path.mkdirs();
        }

        writer = new BufferedWriter(new FileWriter(logFile, true));
//		writer = new FileWriter(logFile);

        Debug.getInstance().debug("EngineLog created for file ", logFile);

        isInitialized = true;
    }
}
