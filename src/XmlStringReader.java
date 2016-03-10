package ca.dioo.java.libmotqueser;

import java.io.StringReader;

public class XmlStringReader {
	public static XmlParser getFromString(String s) {
		XmlParser xp = XmlFactory.newXmlParser(new StringReader(s));
		return xp;
	}
}
