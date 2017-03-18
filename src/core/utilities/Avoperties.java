package core.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Avoperties extends Properties {
	private static final long serialVersionUID = 1L;

	public Avoperties(String fileName) {
		String propFileName = fileName;
		
		File properties = new File(fileName);
		if(!properties.exists()) {
			try {
				properties.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try(InputStream inputStream = new FileInputStream(propFileName)) {
			load(inputStream);
		} catch(IOException e) {
			System.err.println("Property file '" + propFileName + "' not found in the classpath");
			e.printStackTrace();
		}
	}
	
}
