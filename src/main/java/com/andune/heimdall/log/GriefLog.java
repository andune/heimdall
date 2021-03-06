/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2013 Andune (andune.alleria@gmail.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
/**
 *
 */
package com.andune.heimdall.log;

import com.andune.heimdall.Heimdall;
import com.andune.heimdall.util.Debug;
import com.andune.heimdall.util.General;
import org.bukkit.Location;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Logger;

/**
 * @author andune
 */
public class GriefLog implements LogInterface {
    // though this should be externally flushed for consistent performance, at the very
    // least we will flush every TIME_BETWEEN_FLUSH seconds at each call.
    private static final int TIME_BETWEEN_FLUSH = 10000;

    private static final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.FULL);

    private static final String HEADER = "# time|timestamp|activity|playerName|activityGriefPoints|totalGriefPoints|location|blockOwner|additionalData";

    private final Heimdall plugin;
    private final Logger log;
    private final String logPrefix;
    private final File logFile;
    private BufferedWriter writer;
    private boolean isInitialized = false;
    //	private boolean flushScheduled = false;
//	private final LogFlusher logFlusher = new LogFlusher();
    private long lastFlush = 0;

    public GriefLog(final Heimdall plugin, final File logFile) {
        this.plugin = plugin;
        this.log = this.plugin.getLogger();
        this.logPrefix = this.plugin.getLogPrefix();
        this.logFile = logFile;

        this.plugin.addLogger(this);
    }

    public void init() throws IOException {
        boolean fileExists = true;
        if (!logFile.exists()) {
            fileExists = false;
            File path = new File(logFile.getParent());
            if (!path.exists())
                path.mkdirs();
        }

        writer = new BufferedWriter(new FileWriter(logFile, true));
        isInitialized = true;
        if (!fileExists)
            writeHeader();
    }

    public void close() {
        this.plugin.removeLogger(this);

        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void flush() throws IOException {
        if (writer != null) {
            writer.flush();
        }
        lastFlush = System.currentTimeMillis();
    }

    private void writeHeader() throws IOException {
        writer.write(HEADER);
        writer.newLine();
    }

    public void writeEntry(final GriefEntry entry) throws IOException {
        if (!isInitialized)
            init();

        StringBuilder sb = new StringBuilder(20);
        sb.append(dateFormat.format(new Date(entry.getTime())));
        sb.append("|");
        sb.append(entry.getTime());
        sb.append("|");
        sb.append(entry.getActivity().toString());
        sb.append("|");
        sb.append(entry.getPlayerName());
        sb.append("|");
        sb.append(entry.getGriefPoints());
        sb.append("|");
        sb.append(entry.getTotalGriefPoints());
        sb.append("|");
        if (entry.getLocation() != null)
            sb.append(General.getInstance().shortLocationString(entry.getLocation()));
        sb.append("|");
        if (entry.getBlockOwner() != null)
            sb.append(entry.getBlockOwner());
        sb.append("|");
        if (entry.getAdditionalData() != null)
            sb.append(entry.getAdditionalData());

        try {
            writer.write(sb.toString());
        }
        // if we fail first try, close and re-open the file and try writing again
        // if it fails on this 2nd try, the 2nd exception will be thrown to the caller
        catch (IOException e) {
            if (writer != null)
                writer.close();

            init();
            writer.write(sb.toString());
        }

        // newLine happens outside try/catch so we avoid possibility of corrupt double
        // entry, and generally if the error was that the file was closed and we
        // recovered, then this is either going to work or not, no reason to retry again.
        writer.newLine();

        if ((System.currentTimeMillis() - lastFlush) > TIME_BETWEEN_FLUSH)
            flush();

//		if( !flushScheduled ) {
//			flushScheduled = true;
//			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, logFlusher, 100);
//		}
    }

    /**
     * Read an string written by writeEntry() and return the GriefEntry object.
     *
     * @param entryString
     * @return
     */
    public GriefEntry readEntry(final String entryString) {
        GriefEntry entry = null;
        if (entryString.startsWith("#"))
            return null;
        // has to contain at least one | to be valid, otherwise skip the line
        if (!entryString.contains("|"))
            return null;

        String[] parts = entryString.split("\\|");
        if (Debug.getInstance().isDevDebug()) {
            for (int i = 0; i < parts.length; i++) {
                Debug.getInstance().devDebug("i=", i, ", parts[i]=", parts[i]);
            }
        }
        try {
            int i = 0;
            i++;            // date/time string not used
            long time = Long.valueOf(parts[i++]);
            String activityString = parts[i++];
            Debug.getInstance().debug("GriefLog:readEntry() activityString=", activityString);
            GriefEntry.Type activity = GriefEntry.Type.valueOf(activityString);
//			GriefEntry.Type activity = GriefEntry.Type.valueOf(GriefEntry.Type.class, parts[i++]);
            String playerName = parts[i++];
            float griefPoints = Float.parseFloat(parts[i++]);
            float totalGriefPoints = Float.parseFloat(parts[i++]);
            String locationString = parts[i++];
            Location location = null;
            if (locationString != null)
                location = General.getInstance().readShortLocationString(locationString);

            String blockOwner = null;
            if (++i < parts.length)
                blockOwner = parts[i];

            String additionalData = null;
            if (++i < parts.length)
                additionalData = parts[i];

            entry = new GriefEntry(activity, time, playerName, griefPoints, totalGriefPoints, location, blockOwner, additionalData);
        } catch (Exception e) {
            log.warning(logPrefix + "readEntry(): Error reading GriefEntry line: " + e.getMessage() + ", entryString=" + entryString);
            e.printStackTrace();
        }
        return entry;
    }

    private static final GriefEntry[] emtpyGriefEntryArray = new GriefEntry[]{};

    public GriefEntry[] getAllEntries() throws IOException {
        ArrayList<GriefEntry> entries = new ArrayList<GriefEntry>(10);

        // be sure to flush the writer before we begin reading
        if (writer != null) {
            try {
                writer.flush();
            } catch (IOException e) {
            }    // ignore any error
        }

        BufferedReader reader = new BufferedReader(new FileReader(logFile));
        String line = null;
        while ((line = reader.readLine()) != null) {
            GriefEntry entry = readEntry(line);
            if (entry != null)
                entries.add(entry);
        }
        reader.close();
        return entries.toArray(emtpyGriefEntryArray);
    }

    public GriefEntry[] getLastNEntries(int n) throws IOException {
        GriefEntry[] entries = new GriefEntry[n];

        // be sure to flush the writer before we begin reading
        if (writer != null) {
            try {
                writer.flush();
            } catch (IOException e) {
            }    // ignore any error
        }

        BufferedReader reader = new BufferedReader(new FileReader(logFile));
        String line = null;

		/* Read in all entries, writing them into the N elements we have,
         * wrapping back to 0 if there are more than N entries.
		 * 
		 */
        int i = 0;
        boolean moreThanN = false;
        while ((line = reader.readLine()) != null) {
            if (i == n) {
                i = 0;
                moreThanN = true;
            }

            GriefEntry entry = readEntry(line);
            if (entry != null)
                entries[i++] = entry;
        }

		/* If there were less than N, create an array of the correct size and
		 * copy the elements there. This assures that entries.length will be
		 * accurate on return.
		 * 
		 */
        if (!moreThanN) {
            entries = Arrays.copyOf(entries, i);
        }
		/* If there were N or more entries, then the order is not necessarily
		 * accurate, so sort the entries.
		 */
        else
            Arrays.sort(entries);

        reader.close();
        return entries;
    }


//	private class LogFlusher implements Runnable {
//		public void run() {
//			try { 
//				writer.flush();
//			} catch(IOException e) {}
//			flushScheduled = false;
//		}
//	}
}
