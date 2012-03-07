package de.ruemar.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class SmbProperties {

	private Properties prop;

	public SmbProperties() throws IOException {
		
		URL url =  ClassLoader.getSystemResource("smb.properties");
		Properties p = new Properties();
		p.load(new FileInputStream(new File(url.getFile())));

		
		this.prop = p;
	}

	public String getPropertyValue(String key) {
		String value = null;
		value = this.prop.getProperty(key);
		return value;
	}

}
