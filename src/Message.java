package ca.dioo.java.MonitorLib;

import javax.xml.stream.XMLStreamReader;

public abstract class Message {
	protected XMLStreamReader xsr;
	protected int[] version;


	public Message(int[] version) {
		assert version.length == 2;
		this.version = version;
	}


	public Message(XMLStreamReader xsr) {
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


	public abstract void processXmlEvent(XmlEvent e) throws MalformedMessageException;


	protected boolean compareElement(XmlEvent e, XmlEvent type, String name) {
		String n;

		if (e != type) {
			return false;
		} else if (!(n = xsr.getLocalName()).equals(name)) {
			return false;
		}

		return true;
	}


	protected void validateElement(XmlEvent e, XmlEvent type, String name) throws MalformedMessageException {
		String n;

		if (e != type) {
			throw new MalformedMessageException("unexpected XML event");
		} else if (!(n = xsr.getLocalName()).equals(name)) {
			throw new MalformedMessageException("bad tag name (" + n + ")");
		}
	}
}


