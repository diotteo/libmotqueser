package ca.dioo.java.libmotqueser;

import java.util.List;

public abstract class Message {
	protected XmlParser xp;
	protected Version mVersion;


	public static class Version {
		public static final int NB_FIELDS = 2;
		private int mMajor;
		private int mMinor;


		public static Version fromArray(int v[]) {
			switch (v.length) {
			case 1:
				return new Version(v[0]);
			case 2:
				return new Version(v[0], v[1]);
			default:
				throw new UnsupportedOperationException("v.length = " + v.length);
			}
		}

		public Version(int major) {
			this(major, 0);
		}

		public Version(int major, int minor) {
			mMajor = major;
			mMinor = minor;
		}

		public String toString() {
			return mMajor + "." + mMinor;
		}

		@Override
		public int hashCode() {
			return mMajor ^ mMinor;
		}

		public boolean equals(Version v) {
			return mMajor == v.mMajor && mMinor == v.mMinor;
		}
	}


	public static String joinAttributeList(String eleDelim, String attrDelim, List<Attribute<String, String>> l) {
		if (l == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();

		String d = "";
		for (Attribute<String,String> at: l) {
			sb.append(d + at.getName() + attrDelim + at.getValue());
			d = eleDelim;
		}
		return sb.toString();
	}


	protected Message(Version version) {
		mVersion = version;
	}


	public Message(XmlParser xp) {
		this.xp = xp;
		mVersion = null;

		int attrCount = xp.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String attrName = xp.getAttributeName(i).toString();
			String attrVal = xp.getAttributeValue(i);

			if (attrName.equals("version")) {
				String[] s = attrVal.split("\\.");
				int[] ver = new int[Math.min(s.length, Version.NB_FIELDS)];
				for (int j = 0; j < s.length && j < Version.NB_FIELDS; j++) {
					ver[j] = new Integer(s[j]);
				}
				mVersion = Version.fromArray(ver);
				break;
			}
		}
		assert mVersion != null;
	}


	public Version getVersion() {
		return mVersion;
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


