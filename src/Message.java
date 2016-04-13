package ca.dioo.java.libmotqueser;

public abstract class Message {
	protected XmlParser xp;
	protected int[] version;

	protected Message(int[] version) {
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


	public int[] getVersion() {
		return version;
	}


	public abstract String getXmlRoot();
	public abstract void processXmlEvent(XmlParser.XmlEvent e) throws MalformedMessageException;
	public abstract String getXmlString();
	public abstract void processXmlRootEndTag();

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
			throw new MalformedMessageException("unexpected XML event: " + e + " (expected " + type + ")");
		} else if (name != null && !(n = xp.getLocalName()).equals(name)) {
			throw new MalformedMessageException("bad tag name: " + n + " (expected " + name + ")");
		}
	}
}


