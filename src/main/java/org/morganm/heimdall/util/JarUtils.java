/**
 * 
 */
package org.morganm.heimdall.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

/**
 * @author morganm
 *
 */
public class JarUtils {
	// version: 9
	private final Logger log;
	private final String logPrefix;
	private JavaPluginExtensions plugin;
	private File jarFile;
	
	public JarUtils(JavaPluginExtensions plugin, File jarFile, Logger log, String logPrefix) {
		this.plugin = plugin;
		this.jarFile = this.plugin.getFile();
		this.log = this.plugin.getLogger();
		this.logPrefix = this.plugin.getLogPrefix();
	}
//	public JarUtils(JavaPlugin plugin, File jarFile) {
//		this(plugin,jarFile, null, null);
//	}
	
	/** Code adapted from Puckerpluck's MultiInv plugin.
	 * 
	 * @param string
	 * @return
	 */
    public void copyConfigFromJar(String fileName, File outfile) {
        File file = new File(plugin.getDataFolder(), fileName);
        
        if (!outfile.canRead()) {
            try {
            	JarFile jar = new JarFile(jarFile);
            	
                file.getParentFile().mkdirs();
                JarEntry entry = jar.getJarEntry(fileName);
                InputStream is = jar.getInputStream(entry);
                FileOutputStream os = new FileOutputStream(outfile);
                byte[] buf = new byte[(int) entry.getSize()];
                is.read(buf, 0, (int) entry.getSize());
                os.write(buf);
                os.close();
            } catch (Exception e) {
                log.warning(logPrefix + " Could not copy config file "+fileName+" to default location");
            }
        }
    }
    
    public int getBuildNumber() {
    	int buildNum = -1;
    	
        try {
        	JarFile jar = new JarFile(jarFile);
        	
            JarEntry entry = jar.getJarEntry("build.number");
            InputStream is = jar.getInputStream(entry);
        	Properties props = new Properties();
        	props.load(is);
        	is.close();
        	Object o = props.get("build.number");
        	if( o instanceof Integer )
        		buildNum = ((Integer) o).intValue();
        	else if( o instanceof String )
        		buildNum = Integer.parseInt((String) o);
        } catch (Exception e) {
            log.warning(logPrefix + " Could not load build number from JAR");
        }
        
        return buildNum;
    }
}
