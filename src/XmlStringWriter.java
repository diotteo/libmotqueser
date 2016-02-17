package ca.dioo.java.MonitorLib;

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
		return sw.toString();
	}


	public void writeTag(String localName, String[][] attributes) {
		writeTag(localName, attributes, null);
	}


	public void writeTag(String localName, String[][] attributes, String value) {
		ser.writeStartTag(localName);

		if (attributes != null) {
			for (String[] at: attributes) {
				assert at.length == 2;
				ser.writeAttribute(at[0], at[1]);
			}
		}

		if (value != null) {
			ser.writeText(value);
		}
	}


	public void writeEndTag() {
		ser.writeEndTag();
	}
}
