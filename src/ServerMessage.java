package ca.dioo.java.MonitorLib;

import javax.xml.stream.XMLStreamReader;

class ServerMessage extends Message {
	public ServerMessage(XMLStreamReader xsr) {
		super(xsr);

		if (version[0] != 1 || version[1] != 0) {
			throw new Error("unsupported version " + version[0] + "." + version[1]);
		}
	}


	void processXmlEvent(XmlEvent e) throws MalformedMessageError {
	}
}
