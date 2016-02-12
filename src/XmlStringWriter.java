package ca.dioo.java.MonitorLib;

import javax.xml.stream.XMLOutputFactory;
import java.io.StringWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;

public class XmlStringWriter {
	private XMLOutputFactory xof = XMLOutputFactory.newInstance();
	private StringWriter sw;
	private XMLStreamWriter xsw;
	private boolean is_closed;

	public XmlStringWriter(String rootElement, int[] version) {
		is_closed = false;

		sw = new StringWriter();
		try {
			xsw = xof.createXMLStreamWriter(sw);
			xsw.writeStartDocument("utf-8", "1.0");
			xsw.writeStartElement(rootElement);
			xsw.writeAttribute("version", version[0] + "." + version[1]);
		} catch (XMLStreamException e) {
			throw new Error(e.getMessage());
		}
	}


	public String getXmlString() {
		if (!is_closed) {
			is_closed = true;
			try {
				xsw.writeEndDocument();
				xsw.close();
			} catch (XMLStreamException e) {
				throw new Error(e.getMessage());
			}
		}
		return sw.toString();
	}


	public void writeTag(String localName, String[][] attributes) {
		writeTag(localName, attributes, null, false);
	}


	public void writeTag(String localName, String[][] attributes, String value) {
		writeTag(localName, attributes, value, false);
	}


	public void writeEmptyTag(String localName, String[][] attributes) {
		writeTag(localName, attributes, null, true);
	}


	public void writeTag(String localName, String[][] attributes, String value, boolean isEmptyTag) {
		try {
			if (isEmptyTag) {
				xsw.writeEmptyElement(localName);
			} else {
				xsw.writeStartElement(localName);
			}

			if (attributes != null) {
				for (String[] at: attributes) {
					assert at.length == 2;
					xsw.writeAttribute(at[0], at[1]);
				}
			}

			if (value != null && !isEmptyTag) {
				xsw.writeCharacters(value);
			}
		} catch (XMLStreamException e) {
			throw new Error(e.getMessage());
		}
	}


	public void writeEndTag() {
		try {
			xsw.writeEndElement();
		} catch (XMLStreamException e) {
			throw new Error(e.getMessage());
		}
	}
}
