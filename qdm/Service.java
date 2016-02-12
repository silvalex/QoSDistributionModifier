package qdm;

import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Service implements Cloneable {
	private String name;
	private double[] qos;
	private Set<String> inputs;
	private Set<String> outputs;

	public Service(String name, double[] qos, Set<String> inputs, Set<String> outputs) {
		this.name = name;
		this.qos = qos;
		this.inputs = inputs;
		this.outputs = outputs;
	}

	public double[] getQos() {
		return qos;
	}

	public Set<String> getInputs() {
		return inputs;
	}

	public Set<String> getOutputs() {
		return outputs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString(){
		return name;

	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Service) {
			Service o = (Service) other;
			return name.equals(o.name);
		}
		else
			return false;
	}

	public org.w3c.dom.Node toXml(Document doc) {
		Element service = doc.createElement("service");

		service.setAttribute("name", name);
		service.setAttribute("Res",  String.valueOf(qos[DatasetTransformer.TIME]));
		service.setAttribute("Ava",  String.valueOf(qos[DatasetTransformer.AVAILABILITY]));
		service.setAttribute("Suc",  String.valueOf(0.0));
		service.setAttribute("Rel",  String.valueOf(qos[DatasetTransformer.RELIABILITY]));
		service.setAttribute("Lat",  String.valueOf(0.0));
		service.setAttribute("Pri",  String.valueOf(qos[DatasetTransformer.COST]));

		service.appendChild(inputsToXml(doc));
		service.appendChild(outputsToXml(doc));

		return service;
	}

	private org.w3c.dom.Node inputsToXml(Document doc) {
		Element inputsNode = doc.createElement("inputs");

		for (String input : inputs) {
			Element inputNode = doc.createElement("instance");
			inputNode.setAttribute("name", input);
			inputsNode.appendChild(inputNode);
		}
		return inputsNode;
	}

	private org.w3c.dom.Node outputsToXml(Document doc) {
		Element outputsNode = doc.createElement("outputs");

		for (String output : outputs) {
			Element outputNode = doc.createElement("instance");
			outputNode.setAttribute("name", output);
			outputsNode.appendChild(outputNode);
		}

		return outputsNode;
	}
}
