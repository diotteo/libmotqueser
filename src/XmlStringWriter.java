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
			System.err.println(e.getMessage());
		}
	}


	public String getXmlString() {
		if (!is_closed) {
			is_closed = true;
			try {
				xsw.writeEndDocument();
				xsw.close();
			} catch (XMLStreamException e) {
				System.err.println(e.getMessage());
			}
		}
		return sw.toString();
	}


	public void writeTag(String localName, String[][] attributes) {
		writeTag(localName, attributes, null);
	}

	public void writeTag(String localName, String[][] attributes, String value) {
		try {
			if (value == null) {
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

			if (value != null) {
				xsw.writeCharacters(value);
				xsw.writeEndElement();
			}
		} catch (XMLStreamException e) {
			System.err.println(e.getMessage());
		}
	}
}
