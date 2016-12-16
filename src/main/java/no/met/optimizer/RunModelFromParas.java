package no.met.optimizer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RunModelFromParas {
	private static final Logger LOGGER = Logger.getLogger(RunModelFromParas.class.getName());
	public static void runModelFromParameters(String propertiesFile, String paras) {
		
		String[] values=paras.split(",");
		ModelOptimizer modelOptimizer=new ModelOptimizer(propertiesFile);
		modelOptimizer.evaluate(values);
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		if (args.length<2) {
			LOGGER.log(Level.SEVERE, "Please give model optimizer properties file and parameters.");
			System.exit(1);
		}
		runModelFromParameters(args[0],args[1]);
		System.out.println("done!");
	}
}
