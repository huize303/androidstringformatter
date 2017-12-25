
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Compare {
	public Config config;

	public static final String STR_IP1 = "strings1.xml";
	public static final String STR_IP2 = "strings2.xml";
	
	public static final String STR_OP1 = "stringop1.xml";
	public static final String STR_OP2 = "stringop2.xml";

	public Compare(Config config) {
		this.config = config;
	}

	public void start() {
		if (config == null) {
			System.out.println("config is null");
			return;
		}

		if (!checkFile()) {
			System.out.println("file not exsist");
			return;
		}
		deleteOldFile();

		try {
			HashMap<String, StringEntry> string1 = new LinkedHashMap<>();
			HashMap<String, StringArryEntry> stringArry1 = new LinkedHashMap<>();
			paserFile(config.getPath1(), string1, stringArry1);

			HashMap<String, StringEntry> string2 = new LinkedHashMap<>();
			HashMap<String, StringArryEntry> stringArry2 = new LinkedHashMap<>();
			paserFile(config.getPath2(), string2, stringArry2);

			ArrayList<StringEntry> opStrSame1 = new ArrayList<>();
			ArrayList<StringEntry> opStrSame2 = new ArrayList<>();

			ArrayList<StringArryEntry> opStrArrySame1 = new ArrayList<>();
			ArrayList<StringArryEntry> opStrArrySame2 = new ArrayList<>();

			ArrayList<StringEntry> opStrDiff1 = new ArrayList<>();
			ArrayList<StringEntry> opStrDiff2 = new ArrayList<>();

			ArrayList<StringArryEntry> opStrArryDiff1 = new ArrayList<>();
			ArrayList<StringArryEntry> opStrArryDiff2 = new ArrayList<>();

			for (Entry<String, StringEntry> entry : string1.entrySet()) {
				String key = entry.getKey();

				if (string2.containsKey(key)) {
					opStrSame1.add(entry.getValue());
					opStrSame2.add(string2.get(key));
				} else {
					opStrDiff1.add(entry.getValue());
				}

			}

			for (Entry<String, StringEntry> entry : string2.entrySet()) {
				if (!opStrSame2.contains(entry.getValue())) {
					opStrDiff2.add(entry.getValue());
				}
			}

			for (Entry<String, StringArryEntry> entry : stringArry1.entrySet()) {
				String key = entry.getKey();
				if (stringArry2.containsKey(key)) {
					opStrArrySame1.add(entry.getValue());
					opStrArrySame2.add(stringArry2.get(key));
				} else {
					opStrArryDiff1.add(entry.getValue());
				}
			}

			for (Entry<String, StringArryEntry> entry : stringArry2.entrySet()) {
				if (!opStrArrySame2.contains(entry.getValue())) {
					opStrArryDiff2.add(entry.getValue());
				}
			}

			if (config.isCopyEachOther()) {
				ArrayList<StringEntry> cpdiff = new ArrayList<StringEntry>();
				cpdiff.addAll(opStrDiff1);
				cpdiff.addAll(opStrDiff2);
				opStrDiff1.clear();
				opStrDiff2.clear();
				opStrDiff1 = cpdiff;
				opStrDiff2 = cpdiff;

				ArrayList<StringArryEntry> cpArryDiff = new ArrayList<>();
				cpArryDiff.addAll(opStrArryDiff1);
				cpArryDiff.addAll(opStrArryDiff2);
				opStrArryDiff1.clear();
				opStrArryDiff2.clear();
				opStrArryDiff1 = cpArryDiff;
				opStrArryDiff2 = cpArryDiff;
			}

			System.out.println("opsame1 = " + opStrSame1.size());
			System.out.println("opsame2 = " + opStrSame2.size());
			System.out.println("oparrysame1 = " + opStrArrySame1.size());
			System.out.println("oparrysame2 = " + opStrArrySame2.size());
			System.out.println("opdiff1 = " + opStrDiff1.size());
			System.out.println("opdiff2 = " + opStrDiff2.size());
			System.out.println("oparrydiff1 = " + opStrArryDiff1.size());
			System.out.println("oparrydiff2 = " + opStrArryDiff2.size());

			writeToFile(opStrSame1, opStrDiff1, opStrArrySame1, opStrArryDiff1, STR_OP1);
			writeToFile(opStrSame2, opStrDiff2, opStrArrySame2, opStrArryDiff2, STR_OP2);

			// clear resource
			string1.clear();
			string2.clear();
			opStrArrySame1.clear();
			opStrArrySame2.clear();
			opStrSame1.clear();
			opStrSame2.clear();
			opStrDiff1.clear();
			opStrDiff2.clear();
			opStrArryDiff1.clear();
			opStrArryDiff2.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteOldFile() {
		File file1 = new File(STR_OP1);
		if (file1.exists()) {
			file1.delete();
		}

		File file2 = new File(STR_OP2);
		if (file2.exists()) {
			file2.delete();
		}
	}

	public boolean checkFile() {
		File file1 = new File(config.getPath1());
		File file2 = new File(config.getPath2());
		if (file1.exists() && file1.length() > 0 && file2.exists() && file2.length() > 0) {
			return true;
		}
		return false;
	}

	public void paserFile(String path, HashMap<String, StringEntry> stringEntries,
			HashMap<String, StringArryEntry> stringArryEntries)
			throws ParserConfigurationException, SAXException, FileNotFoundException {

		InputStream is = null;
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		try {
			is = new FileInputStream(path);
			Document document = documentBuilder.parse(is);
			Element element = document.getDocumentElement();
			NodeList nodeStrList = element.getElementsByTagName("string");
			for (int i = 0; i < nodeStrList.getLength(); i++) {
				Node node = nodeStrList.item(i);
				String name = String.valueOf(node.getAttributes().getNamedItem("name").getNodeValue());
				String value = node.getTextContent();
				stringEntries.put(name, new StringEntry(name, value));
			}

			NodeList nodeStrArryList = element.getElementsByTagName("string-array");
			for (int i = 0; i < nodeStrArryList.getLength(); i++) {
				Element elementchild = (Element) nodeStrArryList.item(i);
				StringArryEntry strArryEntry = new StringArryEntry();
				String name = String.valueOf(elementchild.getAttributes().getNamedItem("name").getNodeValue());
				strArryEntry.setKey(name);
				NodeList childNodes = elementchild.getChildNodes();
				for (int j = 0; j < childNodes.getLength(); j++) {
					Node node = nodeStrArryList.item(i);
					if (childNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
						if ("item".equals(childNodes.item(j).getNodeName())) {
							strArryEntry.addValue(childNodes.item(j).getFirstChild().getNodeValue());
						}
					}
				}
				stringArryEntries.put(name, strArryEntry);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void writeToFile(ArrayList<StringEntry> opsame, ArrayList<StringEntry> opdiff,
			ArrayList<StringArryEntry> opArrySame, ArrayList<StringArryEntry> opArryDiff, String fileName)
			throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document dom = documentBuilder.newDocument();
		dom.setXmlStandalone(true);
		Element root = dom.createElement("resources");
		
		root.appendChild(dom.createComment("same string----------------"));
		for (StringEntry entry : opsame) {
			Element element = dom.createElement("string");
			element.setAttribute("name", entry.key);
			element.setTextContent(entry.value);
			root.appendChild(element);
		}
		
		root.appendChild(dom.createComment("diff string----------------"));
		for (StringEntry entry : opdiff) {
			Element element = dom.createElement("string");
			element.setAttribute("name", entry.key);
			element.setTextContent(entry.value);
			root.appendChild(element);
		}

		root.appendChild(dom.createComment("same string array----------------"));
		for (StringArryEntry entry : opArrySame) {
			Element element = dom.createElement("string-array");
			element.setAttribute("name", entry.key);
			for (String itemName : entry.value) {
				Element itemElement = dom.createElement("item");
				itemElement.setTextContent(itemName);
				element.appendChild(itemElement);
			}
			root.appendChild(element);
		}

		root.appendChild(dom.createComment("diff string array----------------"));
		for (StringArryEntry entry : opArryDiff) {
			Element element = dom.createElement("string-array");
			element.setAttribute("name", entry.key);
			for (String itemName : entry.value) {
				Element itemElement = dom.createElement("item");
				itemElement.setTextContent(itemName);
				element.appendChild(itemElement);
			}
			root.appendChild(element);
		}

		dom.appendChild(root);
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		FileOutputStream fo = null;
		try {
			fo = new FileOutputStream(new File(fileName));
			transformer.transform(new DOMSource(dom), new StreamResult(fo));
			fo.close();
		} 
		
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fo != null) {
					fo.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
