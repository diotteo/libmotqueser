package ca.dioo.java.libmotqueser;

import java.util.List;
import java.io.StringWriter;

public class XmlStringWriter {
	private StringWriter sw;
	private XmlSerializer ser;


	public XmlStringWriter(String rootElement, int[] version) {
		sw = new StringWriter();
		ser = XmlFactory.newXmlSerializer(sw);
		ser.writeStartDocument("utf-8");
		ser.writeStartTag(rootElement);
		ser.writeAttribute("version", version[0] + "." + version[1]);
	}


	public String getXmlString() {
		if (!ser.isClosed()) {
			ser.writeEndDocument();
		}
		sw.flush();
		return sw.toString();
	}


	public void writeTag(String localName, List<Attribute<String, String>> attributes) {
		writeTag(localName, attributes, null);
	}


	public void writeTag(String localName, List<Attribute<String, String>> attributes, String value) {
		ser.writeStartTag(localName);

		if (attributes != null) {
			for (Attribute<String, String> at: attributes) {
				ser.writeAttribute(at.getName(), at.getValue());
			}
		}

		if (value != null) {
			writeText(value);
		}
	}


	public void writeEndTag() {
		ser.writeEndTag();
	}


	public void writeText(String value) {
		ser.writeText(value);
	}
}
