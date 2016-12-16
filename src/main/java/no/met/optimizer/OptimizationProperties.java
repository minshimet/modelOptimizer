package no.met.optimizer;

import java.util.ArrayList;

public class OptimizationProperties {
	private int evalutationTimes;
	private int parameterNumber;
	private ArrayList<Parameter> parameters;
	private int parameterTemplateFileNumber;
	private ArrayList<String> parameterTemplateFiles;
	private ArrayList<String> parameterTargetFiles;
	private int optimizedObjectiveNumber;
	private ArrayList<String> modelOutputFiles;
	private ArrayList<String> desiredOutputFiles;
	private String runModelShellcommand;
	private ArrayList<String> invaildValues;
	
	public OptimizationProperties() {
		parameters=new ArrayList<Parameter>();
		parameterTemplateFiles= new ArrayList<String>();
		parameterTargetFiles= new ArrayList<String>();
		modelOutputFiles= new ArrayList<String>();
		desiredOutputFiles= new ArrayList<String>();
		invaildValues=new ArrayList<String>();
	}
	
	public int getEvalutationTimes() {
		return evalutationTimes;
	}
	public void setEvalutationTimes(int evalutationTimes) {
		this.evalutationTimes = evalutationTimes;
	}
	public int getParameterNumber() {
		return parameterNumber;
	}
	public void setParameterNumber(int parameterNumber) {
		this.parameterNumber = parameterNumber;
	}
	
	public int getParameterTemplateFileNumber() {
		return parameterTemplateFileNumber;
	}
	public void setParameterTemplateFileNumber(int parameterTemplateFileNumber) {
		this.parameterTemplateFileNumber = parameterTemplateFileNumber;
	}
	public int getOptimizedObjectiveNumber() {
		return optimizedObjectiveNumber;
	}
	public void setOptimizedObjectiveNumber(int optimizedObjectiveNumber) {
		this.optimizedObjectiveNumber = optimizedObjectiveNumber;
	}
	public String getRunModelShellcommand() {
		return runModelShellcommand;
	}
	public void setRunModelShellcommand(String runModelShellcommand) {
		this.runModelShellcommand = runModelShellcommand;
	}

	public ArrayList<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<Parameter> parameters) {
		this.parameters = parameters;
	}

	public ArrayList<String> getParameterTemplateFiles() {
		return parameterTemplateFiles;
	}

	public void setParameterTemplateFiles(ArrayList<String> parameterTemplateFiles) {
		this.parameterTemplateFiles = parameterTemplateFiles;
	}

	public ArrayList<String> getParameterTargetFiles() {
		return parameterTargetFiles;
	}

	public void setParameterTargetFiles(ArrayList<String> parameterTargetFiles) {
		this.parameterTargetFiles = parameterTargetFiles;
	}

	public ArrayList<String> getModelOutputFiles() {
		return modelOutputFiles;
	}

	public void setModelOutputFiles(ArrayList<String> modelOutputFiles) {
		this.modelOutputFiles = modelOutputFiles;
	}

	public ArrayList<String> getDesiredOutputFiles() {
		return desiredOutputFiles;
	}

	public void setDesiredOutputFiles(ArrayList<String> desiredOutputFiles) {
		this.desiredOutputFiles = desiredOutputFiles;
	}

	public ArrayList<String> getInvaildValues() {
		return invaildValues;
	}

	public void setInvaildValues(ArrayList<String> invaildValues) {
		this.invaildValues = invaildValues;
	}
}
