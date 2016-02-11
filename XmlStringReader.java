package ca.dioo.java.MonitorLib;

import javax.xml.stream.XMLInputFactory;
import java.io.StringReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;


public class XmlStringReader {
	private XMLInputFactory xif = XMLInputFactory.newInstance();
	private XMLStreamReader xsr;


	public XmlStringReader(String s) {
		try {
			xsr = xif.createXMLStreamReader(new StringReader(s));
		} catch (XMLStreamException e) {
			System.err.println(e.getMessage());
		}
	}


	public void parse() {
		boolean is_bogus = false;
		String errMsg = null;
		int lvl = 0;
		Message message;

		try {
			while (xsr.hasNext() && !is_bogus) {
				int evt = xsr.next();
				XmlEvent e = XmlEvent.getEventFromValue(evt);
				System.out.print(e.value() + " : " + e.toString());

				switch (e) {
				case START_ELEMENT:
					lvl++;

					String name = xsr.getLocalName();

					if (lvl == 1) {
						switch (name) {
						case "client_message":
							message = new ClientMessage(xsr);
							break;
						case "control_message":
							message = new ControlMessage(xsr);
							break;
						case "server_message":
							message = new ServerMessage(xsr);
							break;
						default:
							is_bogus = true;
							errMsg = "unknown root element \"" + name + "\"";
							break;
						}
					}
					int attrCount = xsr.getAttributeCount();
					System.out.println(" " + xsr.getLocalName() + " " + attrCount);
					for (int i = 0; i < attrCount; i++) {
						System.out.println("  ATTR " + i + " : " + xsr.getAttributeName(i) +
								"=" + xsr.getAttributeValue(i));
					}
					break;
				case CHARACTERS:
					System.out.println(" " + xsr.getText());
					break;

				case END_ELEMENT:
					System.out.println(" " + xsr.getLocalName());
					lvl--;
					break;
				case COMMENT:
					//Ignore
					break;
				case ATTRIBUTE:
					//Apparently never used by the default implementation?
				default:
					errMsg = "disallowed token type " + e.toString();
					is_bogus = true;
					break;
				}
			}
		} catch (XMLStreamException e) {
			System.err.println(e.getMessage());
		}
	}
}
