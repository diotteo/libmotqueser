package ca.dioo.java.MonitorLib;

import javax.xml.stream.XMLStreamReader;


class MalformedMessageError extends Error {
	public MalformedMessageError(String msg) {
		super(msg);
	}
}
class BadActionTypeError extends MalformedMessageError {
	public BadActionTypeError(String msg) {
		super(msg);
	}
}


abstract class Message {
	protected XMLStreamReader xsr;
	protected int[] version;


	Message(int[] version) {
		assert version.length == 2;
		this.version = version;
	}


	Message(XMLStreamReader xsr) {
		this.xsr = xsr;
		version = null;

		int attrCount = xsr.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String attrName = xsr.getAttributeName(i).toString();
			String attrVal = xsr.getAttributeValue(i);

			if (attrName.equals("version")) {
				version = new int[2];
				String[] s = attrVal.split("\\.");
				for (int j = 0; j < s.length; j++) {
					version[j] = new Integer(s[j]);
				}
				break;
			}
		}
		assert version != null;
	}


	abstract void processXmlEvent(XmlEvent e) throws MalformedMessageError;


	protected void validateElement(XmlEvent e, XmlEvent type, String name) throws MalformedMessageError, Error {
		String n;

		if (e != type) {
			throw new MalformedMessageError("unexpected XML event");
		} else if (!(n = xsr.getLocalName()).equals(name)) {
			throw new MalformedMessageError("bad tag name (" + n + ")");
		}
	}
}


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
