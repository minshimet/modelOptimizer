package no.met.optimizer;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.moeaframework.Executor;
import org.moeaframework.analysis.plot.Plot;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;

public class RunModelOptimizer {
	private static final Logger LOGGER = Logger.getLogger(RunModelOptimizer.class.getName());
	public static void main(String[] args) {
		if (args.length<1) {
			LOGGER.log(Level.SEVERE, "Please give model optimizer properties file.");
			System.exit(1);
		}
		Properties p = Utils.loadProperties(args[0]);
		if (p == null) {
			LOGGER.log(Level.SEVERE, "Error to load model optimizer properties file.");
			System.exit(1);
		}
		int evalutationTimes = Integer.parseInt(p.getProperty("evaluation_times", "100"));
		String algorithm=p.getProperty("algorithm","NSGAII");
		NondominatedPopulation result = new Executor().withAlgorithm(algorithm).withProblemClass(ModelOptimizer.class,args[0])
				.withMaxEvaluations(evalutationTimes).distributeOnAllCores().run();
		System.out.println("=====================Final results from optimizer:=====================");
		for (Solution solution : result) {
			for (int i=0;i<solution.getNumberOfVariables();i++) {
				System.out.print(EncodingUtils.getReal(solution.getVariable(i))+", ");
			}
			System.out.printf("=> %.5f, %.5f\n",
					solution.getObjective(0), solution.getObjective(1));
		}
		System.out.println("Done!");
		//new Plot().add("NSGAII", result).show();
	}
}
