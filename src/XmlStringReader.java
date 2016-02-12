package ca.dioo.java.MonitorLib;

import javax.xml.stream.XMLInputFactory;
import java.io.StringReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;


public class XmlStringReader {
	public static XMLStreamReader getFromString(String s) {
		XMLInputFactory xif = XMLInputFactory.newInstance();
		XMLStreamReader xsr;

		try {
			xsr = xif.createXMLStreamReader(new StringReader(s));
		} catch (XMLStreamException e) {
			throw new Error(e.getMessage());
		}

		return xsr;
	}
}
