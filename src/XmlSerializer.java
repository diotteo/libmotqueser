package ca.dioo.java.MonitorLib;

import java.util.Stack;

public class XmlSerializer {
	private Object ser;
	private Stack<String> tagStack;
	private boolean is_closed;


	//TODO: Put in some wrapper "simple" class
	public XmlSerializer(String rootElement, int[] version) {
		is_closed = false;

		//sw = new StringWriter();
		//XmlSerializer.newInstance(sw);
		writeStartDocument("utf-8");
		writeStartTag(rootElement);
		writeAttribute("version", version[0] + "." + version[1]);
	}


	XmlSerializer(Object ser) {
		this.ser = ser;

		switch (XmlFactory.curRt) {
		case ANDROID:
			tagStack = new Stack<String>();
			break;
		case JDK_1_6:
			tagStack = null;
			break;
		default:
			throw new ProgrammerBrainNotFoundError();
		}
	}


	public void writeStartDocument(String encoding) {
		switch (XmlFactory.curRt) {
		case ANDROID:
			XmlFactory.invokeMethod(ser, "setProperty",
					new Class<?>[]{String.class, String.class},
					new Object[]{"version", "1.0"});
			XmlFactory.invokeMethod(ser, "startDocument",
					new Class<?>[]{String.class, Boolean.class},
					new Object[]{encoding, null});
			break;
		case JDK_1_6:
			XmlFactory.invokeMethod(ser, "writeStartDocument",
					new Class<?>[]{String.class, String.class},
					new Object[]{encoding, "1.0"});
			break;
		default:
			throw new ProgrammerBrainNotFoundError();
		}
	}


	public void writeStartTag(String localName) {
		switch (XmlFactory.curRt) {
		case ANDROID:
			XmlFactory.invokeMethod(ser, "startTag",
					new Class<?>[]{String.class, String.class},
					new Object[]{null, localName});
			tagStack.push(localName);
			break;
		case JDK_1_6:
			XmlFactory.invokeMethod(ser, "writeStartElement",
					new Class<?>[]{String.class},
					new Object[]{localName});
			break;
		default:
			throw new ProgrammerBrainNotFoundError();
		}
	}


	public void writeAttribute(String localName, String value) {
		switch (XmlFactory.curRt) {
		case ANDROID:
			XmlFactory.invokeMethod(ser, "attribute",
					new Class<?>[]{String.class, String.class, String.class},
					new Object[]{null, localName, value});
			break;
		case JDK_1_6:
			XmlFactory.invokeMethod(ser, "writeAttribute",
					new Class<?>[]{String.class, String.class},
					new Object[]{localName, value});
			break;
		default:
			throw new ProgrammerBrainNotFoundError();
		}
	}


	public void writeEndDocument() {
		switch (XmlFactory.curRt) {
		case ANDROID:
			XmlFactory.invokeMethod(ser, "endDocument", null, null);
			tagStack = null;
			break;
		case JDK_1_6:
			XmlFactory.invokeMethod(ser, "writeEndDocument", null, null);
			XmlFactory.invokeMethod(ser, "close", null, null);
			break;
		default:
			throw new ProgrammerBrainNotFoundError();
		}
		is_closed = true;
	}


	public void writeEndTag() {
		switch (XmlFactory.curRt) {
		case ANDROID:
			String localName = tagStack.pop();
			XmlFactory.invokeMethod(ser, "endTag",
					new Class<?>[]{String.class, String.class},
					new Object[]{null, localName});
			break;
		case JDK_1_6:
			XmlFactory.invokeMethod(ser, "writeEndElement", null, null);
			break;
		default:
			throw new ProgrammerBrainNotFoundError();
		}
	}


	public void writeText(String text) {
		switch (XmlFactory.curRt) {
		case ANDROID:
			XmlFactory.invokeMethod(ser, "text",
					new Class<?>[]{String.class},
					new Object[]{text});
			break;
		case JDK_1_6:
			XmlFactory.invokeMethod(ser, "writeCharacters",
					new Class<?>[]{String.class},
					new Object[]{text});
			break;
		default:
			throw new ProgrammerBrainNotFoundError();
		}
	}


	public String getXmlString() {
		if (!is_closed) {
			writeEndDocument();
		}
		//return sw.toString();
		return "";
	}


	public void writeTag(String localName, String[][] attributes) {
		writeTag(localName, attributes, null);
	}


	public void writeTag(String localName, String[][] attributes, String value) {
		writeStartTag(localName);

		if (attributes != null) {
			for (String[] at: attributes) {
				assert at.length == 2;
				writeAttribute(at[0], at[1]);
			}
		}

		writeText(value);
	}
}
