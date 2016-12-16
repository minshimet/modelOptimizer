package no.met.optimizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

public class ModelOptimizer extends AbstractProblem {
	private static final Logger LOGGER = Logger.getLogger(ModelOptimizer.class.getName());

	private OptimizationProperties optimizationProperties;
	int evaluationTimes = 0;
	int maximumEvaluaion = Integer.MAX_VALUE;
	int popNum=0;

	public ModelOptimizer(String propertiesFile) {
		super(Integer.parseInt(Utils.loadProperties(propertiesFile).getProperty("parameter_numbers")),
				Integer.parseInt(Utils.loadProperties(propertiesFile).getProperty("optimized_objective_number")));

		try {
			Properties p = Utils.loadProperties(propertiesFile);
			if (p == null) {
				LOGGER.log(Level.SEVERE, "Error to load model optimizer properties file.");
				System.exit(1);
			}

			optimizationProperties = new OptimizationProperties();

			optimizationProperties.setParameterNumber(Integer.parseInt(p.getProperty("parameter_numbers")));
			for (int i = 0; i < optimizationProperties.getParameterNumber(); i++) {
				String paraString = p.getProperty("parameter" + (i+1));
				optimizationProperties.getParameters().add(i, new Parameter(i, paraString));
			}

			optimizationProperties
					.setParameterTemplateFileNumber(Integer.parseInt(p.getProperty("parameter_template_file_number")));
			for (int i = 0; i < optimizationProperties.getParameterTemplateFileNumber(); i++) {
				optimizationProperties.getParameterTemplateFiles().add(i, p.getProperty("parameter_template_file" + (i + 1)));
				optimizationProperties.getParameterTargetFiles().add(i, p.getProperty("parameter_target_file" + (i + 1)));
			}

			optimizationProperties
					.setOptimizedObjectiveNumber(Integer.parseInt(p.getProperty("optimized_objective_number")));
			for (int i = 0; i < optimizationProperties.getOptimizedObjectiveNumber(); i++) {
				optimizationProperties.getModelOutputFiles().add(i, p.getProperty("model_output_file" + (i + 1)));
				optimizationProperties.getDesiredOutputFiles().add(i, p.getProperty("desired_output_file" + (i + 1)));
			}

			optimizationProperties.setRunModelShellcommand(p.getProperty("run_model_shellcommand"));
			optimizationProperties
					.setInvaildValues(new ArrayList<String>(Arrays.asList(p.getProperty("invalid_values").split(","))));

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error to parser model optimizer properties file." + e);
			System.exit(1);
		}

	}

	@Override
	public void evaluate(Solution solution) {
		System.out.println("Population number="+popNum);
		popNum=0;
		double[] x = EncodingUtils.getReal(solution);
		for (int i = 0; i < getNumberOfVariables(); i++) {
			optimizationProperties.getParameters().get(i).setValue(x[i]);
			System.out.print(x[i] + ",");
		}
		performEvaluate(solution);
	}
	
	public void evaluate(String[] values) {
		for (int i=0;i<values.length;i++) {
			optimizationProperties.getParameters().get(i).setValue(Double.parseDouble(values[i]));
			System.out.print(values[i] + ",");
		}
		performEvaluate(null);
	}
	
	private void performEvaluate(Solution solution) {
		for (int i = 0; i < optimizationProperties.getParameterTemplateFileNumber(); i++) {
			generateTargetParameterFile(optimizationProperties.getParameters(),
					optimizationProperties.getParameterTemplateFiles().get(i),
					optimizationProperties.getParameterTargetFiles().get(i));
		}

		// Remove old model file
		for (int i = 0; i < optimizationProperties.getOptimizedObjectiveNumber(); i++) {
			Utils.removeFile(optimizationProperties.getModelOutputFiles().get(i));
		}

		executeModel();

		System.out.print("evaluationTimes " + evaluationTimes + ": ");
		for (int i = 0; i < optimizationProperties.getOptimizedObjectiveNumber(); i++) {
			double result = maximumEvaluaion;
			if (Utils.fileExist(optimizationProperties.getModelOutputFiles().get(i))) {
				result = evaluateResult(optimizationProperties.getModelOutputFiles().get(i),
						optimizationProperties.getDesiredOutputFiles().get(i));
			}
			if (solution!=null) {
				solution.setObjective(i, result);
			}
			System.out.print(result + ",");
		}
		evaluationTimes++;
		System.out.println();
	}

	private double evaluateResult(String modelOutputFile, String desiredOutputFile) {
		try {
			HashMap<String, Double> modelOutputs = loadValues(modelOutputFile);
			HashMap<String, Double> desiredOutputs = loadValues(desiredOutputFile);
			HashMap<Double, Double> pairValues = new HashMap<Double, Double>();
			Iterator it = desiredOutputs.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				if (invalidValue(pair.getValue().toString())) {
					continue;
				}
				if (modelOutputs.get(pair.getKey()) == null) {
					continue;
				}
				if (invalidValue(modelOutputs.get(pair.getKey()).toString())) {
					continue;
				}
				pairValues.put((Double) pair.getValue(), modelOutputs.get(pair.getKey()));
			}
			return this.calculateRMSE(pairValues);
		} catch (IOException e) {
			// return maximum evaluation value
			return this.maximumEvaluaion;
		}
	}

	private boolean invalidValue(String value) {
		return optimizationProperties.getInvaildValues().contains(value);
	}

	private void generateTargetParameterFile(ArrayList<Parameter> parameters, String templateFile, String targetFile) {
		try {
			// Remove old target file
			Utils.removeFile(targetFile);
			// generate new target file
			BufferedReader br = new BufferedReader(new FileReader(templateFile));
			String line = null;
			while ((line = br.readLine()) != null) {
				for (int i = 0; i < parameters.size(); i++) {
					String name = parameters.get(i).getName();
					if (line.indexOf(name) >= 0) {
						line = line.replaceAll(name, parameters.get(i).getValue() + "");
					}
				}
				Utils.appendToFile(line, targetFile);
			}
			br.close();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,
					"Error to generate parameter file " + targetFile + " from template " + templateFile + ". " + e);
			System.exit(1);
		}
	}

	private double calculateRMSE(HashMap<Double, Double> pairValues) {
		double sum_sq = 0;
		double err;
		Iterator it = pairValues.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Double, Double> pair = (Map.Entry) it.next();
			err = (Double) pair.getKey() - (Double) pair.getValue();
			sum_sq += (err * err);
		}
		return (double) Math.sqrt(sum_sq / pairValues.size());
	}

	private HashMap<String, Double> loadValues(String filename) throws IOException {
		HashMap<String, Double> values = new HashMap<String, Double>();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] token = null;
			try {
				token = line.split("\\s+");
				if (!optimizationProperties.getInvaildValues().contains(token[1])) {
					double value = Double.parseDouble(token[1]);
					values.put(token[0], value);
				}
			} catch (NumberFormatException e) {
				LOGGER.log(Level.SEVERE, "Invalid value " + token[1] + " in file " + filename + "." + e);
			}
		}
		br.close();

		return values;
	}

	private void executeModel() {
		// run model from shell command
		try {
			Process proc = Runtime.getRuntime().exec(optimizationProperties.getRunModelShellcommand());
			BufferedReader read = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			try {
				proc.waitFor();
			} catch (InterruptedException e) {
				LOGGER.log(Level.SEVERE, "Error when executing model shell command." + e);
			}
			// while (read.ready()) {
			// System.out.println(read.readLine());
			// }
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error to run model shell command." + e);
		}
	}

	@Override
	public Solution newSolution() {
		
		Solution solution = new Solution(getNumberOfVariables(), getNumberOfObjectives());
		for (int i = 0; i < getNumberOfVariables(); i++) {
			solution.setVariable(i, new RealVariable(optimizationProperties.getParameters().get(i).getLowBound(),
					optimizationProperties.getParameters().get(i).getUpBound()));
		}
		popNum++;
		return solution;
	}
}
