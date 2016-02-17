package ca.dioo.java.MonitorLib;

public abstract class Message {
	protected XmlParser xp;
	protected int[] version;


	public Message(int[] version) {
		assert version.length == 2;
		this.version = version;
	}


	public Message(XmlParser xp) {
		this.xp = xp;
		version = null;

		int attrCount = xp.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String attrName = xp.getAttributeName(i).toString();
			String attrVal = xp.getAttributeValue(i);

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


	public abstract void processXmlEvent(XmlParser.XmlEvent e) throws MalformedMessageException;


	protected boolean compareElement(XmlParser.XmlEvent e, XmlParser.XmlEvent type, String name) {
		String n;

		if (e != type) {
			return false;
		} else if (!(n = xp.getLocalName()).equals(name)) {
			return false;
		}

		return true;
	}


	protected void validateElement(XmlParser.XmlEvent e, XmlParser.XmlEvent type, String name) throws MalformedMessageException {
		String n;

		if (e != type) {
			throw new MalformedMessageException("unexpected XML event");
		} else if (!(n = xp.getLocalName()).equals(name)) {
			throw new MalformedMessageException("bad tag name (" + n + ")");
		}
	}
}


