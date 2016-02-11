package qdm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import qdm.Service;

public class DatasetTransformer {
	// Constants with of order of QoS attributes
	public static final int TIME = 0;
	public static final int COST = 1;
	public static final int AVAILABILITY = 2;
	public static final int RELIABILITY = 3;

	public double minAvailability = Double.MAX_VALUE;
	public double maxAvailability = Double.MIN_VALUE;
	public double minReliability = Double.MAX_VALUE;
	public double maxReliability = Double.MIN_VALUE;
	public double minTime = Double.MAX_VALUE;
	public double maxTime = Double.MIN_VALUE;
	public double minCost = Double.MAX_VALUE;
	public double maxCost = Double.MIN_VALUE;

	public Map<String, Service> serviceMap = new HashMap<String, Service>();
	Random random;

	// Name of original file, name of new file, seed
	public static void main(String[] args) {
		new DatasetTransformer(args[0], args[1], Long.parseLong(args[2]));
	}

	public DatasetTransformer(String filename, String newFilename, long seed) {
		random = new Random(seed);

		parseWSCServiceFile(filename);
		modifyQoSValues(serviceMap);
		writeXmlFile(newFilename);
	}

	/**
	 * Parses the WSC Web service file with the given name, creating Web
	 * services based on this information and saving them to the service map.
	 *
	 * @param fileName
	 */
	private void parseWSCServiceFile(String fileName) {
        Set<String> inputs = new HashSet<String>();
        Set<String> outputs = new HashSet<String>();
        double[] qos = new double[4];

        try {
        	File fXmlFile = new File(fileName);
        	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        	Document doc = dBuilder.parse(fXmlFile);

        	NodeList nList = doc.getElementsByTagName("service");

        	for (int i = 0; i < nList.getLength(); i++) {
        		org.w3c.dom.Node nNode = nList.item(i);
        		Element eElement = (Element) nNode;

        		String name = eElement.getAttribute("name");

        		qos[TIME] = Double.valueOf(eElement.getAttribute("Res"));
    			if (qos[TIME] > maxTime)
    				maxTime = qos[TIME];
    			if (qos[TIME] < minTime)
    				minTime = qos[TIME];
        		   qos[COST] = Double.valueOf(eElement.getAttribute("Pri"));
    			if (qos[COST] > maxCost)
    				maxCost = qos[COST];
    			if (qos[COST] < minCost)
    				minCost = qos[COST];
        		   qos[AVAILABILITY] = Double.valueOf(eElement.getAttribute("Ava"));
    			if (qos[AVAILABILITY] > maxAvailability)
    				maxAvailability = qos[AVAILABILITY];
   				if (qos[AVAILABILITY] < minAvailability)
   					minAvailability = qos[AVAILABILITY];
       		    qos[RELIABILITY] = Double.valueOf(eElement.getAttribute("Rel"));
   				if (qos[RELIABILITY] > maxReliability)
   					maxReliability = qos[RELIABILITY];
   				if (qos[RELIABILITY] < minReliability)
   					minReliability = qos[RELIABILITY];


				// Get inputs
				org.w3c.dom.Node inputNode = eElement.getElementsByTagName("inputs").item(0);
				NodeList inputNodes = ((Element)inputNode).getElementsByTagName("instance");
				for (int j = 0; j < inputNodes.getLength(); j++) {
					org.w3c.dom.Node in = inputNodes.item(j);
					Element e = (Element) in;
					inputs.add(e.getAttribute("name"));
				}

				// Get outputs
				org.w3c.dom.Node outputNode = eElement.getElementsByTagName("outputs").item(0);
				NodeList outputNodes = ((Element)outputNode).getElementsByTagName("instance");
				for (int j = 0; j < outputNodes.getLength(); j++) {
					org.w3c.dom.Node out = outputNodes.item(j);
					Element e = (Element) out;
					outputs.add(e.getAttribute("name"));
				}


                Service ws = new Service(name, qos, inputs, outputs);
                serviceMap.put(name, ws);
                inputs = new HashSet<String>();
                outputs = new HashSet<String>();
                qos = new double[4];
        	}
        }
        catch(IOException ioe) {
            System.out.println("Service file parsing failed...");
        }
        catch (ParserConfigurationException e) {
            System.out.println("Service file parsing failed...");
		}
        catch (SAXException e) {
            System.out.println("Service file parsing failed...");
		}
    }

	private void writeXmlFile(String filename) {
		DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder;

        try {
        	icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();
            Element mainRootElement = doc.createElement("services");
            doc.appendChild(mainRootElement);

            // Append child elements to root element
            for (Service s : serviceMap.values()) {
            	mainRootElement.appendChild(s.toXml(doc));
            }

         // output DOM XML to file
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult console = new StreamResult(new File(filename));
            transformer.transform(source, console);

            System.out.printf("Modified file '%s' created successfully.\n", filename);

        }
        catch (Exception e) {
        	e.printStackTrace();
        }
	}

	private void modifyQoSValues(Map<String,Service> serviceMap) {
		for (Service s : serviceMap.values()) {
			double[] qos = s.getQos();
			qos[DatasetTransformer.TIME] = -1.0;
			qos[DatasetTransformer.COST] = -1.0;
			qos[DatasetTransformer.AVAILABILITY] = -1.0;
			qos[DatasetTransformer.RELIABILITY] = -1.0;
		}
	}

}
