package ca.dioo.java.libmotqueser;

import java.util.Stack;

public class XmlSerializer {
	private Object ser;
	private Stack<String> tagStack;
	private boolean is_closed;


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
			XmlFactory.invokeSerMethod(ser, "startDocument",
					new Class<?>[]{String.class, Boolean.class},
					new Object[]{encoding, null});
			break;
		case JDK_1_6:
			XmlFactory.invokeSerMethod(ser, "writeStartDocument",
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
			XmlFactory.invokeSerMethod(ser, "startTag",
					new Class<?>[]{String.class, String.class},
					new Object[]{null, localName});
			tagStack.push(localName);
			break;
		case JDK_1_6:
			XmlFactory.invokeSerMethod(ser, "writeStartElement",
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
			XmlFactory.invokeSerMethod(ser, "attribute",
					new Class<?>[]{String.class, String.class, String.class},
					new Object[]{null, localName, value});
			break;
		case JDK_1_6:
			XmlFactory.invokeSerMethod(ser, "writeAttribute",
					new Class<?>[]{String.class, String.class},
					new Object[]{localName, value});
			break;
		default:
			throw new ProgrammerBrainNotFoundError();
		}
	}


	public boolean isClosed() {
		return is_closed;
	}


	public void flush() {
		switch (XmlFactory.curRt) {
		case ANDROID:
		case JDK_1_6:
			XmlFactory.invokeSerMethod(ser, "flush", null, null);
			break;
		default:
			throw new ProgrammerBrainNotFoundError();
		}
	}


	public void writeEndDocument() {
		switch (XmlFactory.curRt) {
		case ANDROID:
			flush();
			XmlFactory.invokeSerMethod(ser, "endDocument", null, null);
			tagStack = null;
			break;
		case JDK_1_6:
			XmlFactory.invokeSerMethod(ser, "writeEndDocument", null, null);
			XmlFactory.invokeSerMethod(ser, "close", null, null);
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
			XmlFactory.invokeSerMethod(ser, "endTag",
					new Class<?>[]{String.class, String.class},
					new Object[]{null, localName});
			break;
		case JDK_1_6:
			XmlFactory.invokeSerMethod(ser, "writeEndElement", null, null);
			break;
		default:
			throw new ProgrammerBrainNotFoundError();
		}
	}


	public void writeText(String text) {
		switch (XmlFactory.curRt) {
		case ANDROID:
			XmlFactory.invokeSerMethod(ser, "text",
					new Class<?>[]{String.class},
					new Object[]{text});
			break;
		case JDK_1_6:
			XmlFactory.invokeSerMethod(ser, "writeCharacters",
					new Class<?>[]{String.class},
					new Object[]{text});
			break;
		default:
			throw new ProgrammerBrainNotFoundError();
		}
	}
}
