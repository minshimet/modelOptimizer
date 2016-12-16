package no.met.optimizer;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {
	private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());
	public static Properties loadProperties(String filename) {
		Properties p = new Properties();
		InputStream iStream = null;
		try {
			iStream = new FileInputStream(filename);
			p.load(iStream);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "IO exception while reading properties:", e);
			return null;
		} finally {
			if (iStream != null) {
				try {
					iStream.close();
				} catch (IOException e) {
					LOGGER.log(Level.SEVERE, "IO exception while closing property file:", e);
					System.err.println("IO exception while closing:" + e);
					return null;
				}
			}
		}
		return p;
	}
	
	public static void removeFile(String filename) {
		try {

			File file = new File(filename);

			if (!file.delete()) {
				LOGGER.log(Level.SEVERE, "Delete file "+ filename +" operation is failed.");
			}

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Delete file "+ filename +" operation is failed.", e);
		}
	}
	
	public static String readFirstLineFromFile(String file) throws FileNotFoundException, IOException {
		String line;
	    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    line = br.readLine();
		}
		return line;
	}
	
	public static void appendToFile(String text, String fileName){
		if (text.isEmpty())
			return;
		try {
			File file = new File(fileName);

			FileWriter fw = new FileWriter(file, true);
			fw.write(text+"\r\n");
			fw.close();

		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Append to file "+ fileName +" operation is failed.", e);
		}
	}

	public static boolean fileExist(String filePath) {
		File f = new File(filePath);
		if(f.exists() && !f.isDirectory()) { 
		    return true;
		}
		return false;
	}
}
