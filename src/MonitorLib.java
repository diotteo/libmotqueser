package ca.dioo.java.MonitorLib;

import javax.xml.stream.XMLStreamReader;

enum XmlEvent {
	START_ELEMENT(XMLStreamReader.START_ELEMENT),
	ATTRIBUTE(XMLStreamReader.ATTRIBUTE),
	NAMESPACE(XMLStreamReader.NAMESPACE),
	END_ELEMENT(XMLStreamReader.END_ELEMENT),
	CHARACTERS(XMLStreamReader.CHARACTERS),
	CDATA(XMLStreamReader.CDATA),
	COMMENT(XMLStreamReader.COMMENT),
	SPACE(XMLStreamReader.SPACE),
	START_DOCUMENT(XMLStreamReader.START_DOCUMENT),
	END_DOCUMENT(XMLStreamReader.END_DOCUMENT),
	PROCESSING_INSTRUCTION(XMLStreamReader.PROCESSING_INSTRUCTION),
	ENTITY_REFERENCE(XMLStreamReader.ENTITY_REFERENCE),
	DTD(XMLStreamReader.DTD);

	private static XmlEvent[] list;
	private int eventType;

	XmlEvent(int eventType) {
		this.eventType = eventType;
	}

	public int value() {
		return eventType;
	}


	public static XmlEvent getEventFromValue(int value) {
		return list[value];
	}

	static {
		list = new XmlEvent[20]; //20 ought to be enough for a while
		for (XmlEvent e: XmlEvent.values()) {
			list[e.value()] = e;
		}
	}
}
