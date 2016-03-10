package ca.dioo.java.libmotqueser;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.io.InputStream;

public class XmlParser {
	private Object parser;
	private boolean hasMore = true;


	XmlParser(Object parser) {
		this.parser = parser;
	}


	public int getAttributeCount() {
		String methodName;
		Class<?>[] paramTypes = null;
		Object[] args = null;

		switch (XmlFactory.curRt) {
		case ANDROID:
		case JDK_1_6:
			methodName = "getAttributeCount";
			break;
		default:
			throw new ProgrammerBrainNotFoundError();
		}

		return ((Integer)XmlFactory.invokeParserMethod(parser, methodName, paramTypes,
				args)).intValue();
	}


	public String getAttributeName(int index) {
		String methodName;
		Class<?>[] paramTypes = {int.class};
		Object[] args = {index};

		switch (XmlFactory.curRt) {
		case ANDROID:
			methodName = "getAttributeName";
			break;
		case JDK_1_6:
			//TODO: getName().getNamespaceURI() + getName().getLocalPart()
			methodName = "getAttributeLocalName";
			break;
		default:
			throw new ProgrammerBrainNotFoundError();
		}

		return (String)XmlFactory.invokeParserMethod(parser, methodName, paramTypes, args);
	}


	public String getAttributeValue(int index) {
		String methodName;
		Class<?>[] paramTypes = {int.class};
		Object[] args = {index};

		switch (XmlFactory.curRt) {
		case ANDROID:
		case JDK_1_6:
			methodName = "getAttributeValue";
			break;
		default:
			throw new ProgrammerBrainNotFoundError();
		}

		return (String)XmlFactory.invokeParserMethod(parser, methodName, paramTypes, args);
	}


	public String getLocalName() {
		String methodName;
		Class<?>[] paramTypes = null;
		Object[] args = null;

		switch (XmlFactory.curRt) {
		case ANDROID:
			methodName = "getName";
			break;
		case JDK_1_6:
			//TODO: getName().getNamespaceURI() + getName().getLocalPart()
			methodName = "getLocalName";
			break;
		default:
			throw new ProgrammerBrainNotFoundError();
		}

		return (String)XmlFactory.invokeParserMethod(parser, methodName, paramTypes, args);
	}


	public String getText() {
		String methodName;
		Class<?>[] paramTypes = null;
		Object[] args = null;

		switch (XmlFactory.curRt) {
		case ANDROID:
		case JDK_1_6:
			methodName = "getText";
			break;
		default:
			throw new ProgrammerBrainNotFoundError();
		}

		return (String)XmlFactory.invokeParserMethod(parser, methodName, paramTypes, args);
	}


	public XmlEvent next() {
		String methodName;
		Class<?>[] paramTypes = null;
		Object[] args = null;

		switch (XmlFactory.curRt) {
		case ANDROID:
		case JDK_1_6:
			methodName = "next";
			break;
		default:
			throw new ProgrammerBrainNotFoundError();
		}

		int i = ((Integer)XmlFactory.invokeParserMethod(parser, methodName, paramTypes,
				args)).intValue();
		XmlEvent e = XmlEvent.getEventFromValue(i);
		if (XmlFactory.curRt == XmlFactory.Runtime.ANDROID &&
				e == XmlEvent.END_DOCUMENT) {
			hasMore = false;
		}

		return e;
	}


	public boolean hasNext() {
		String methodName;
		Class<?>[] paramTypes = null;
		Object[] args = null;

		switch (XmlFactory.curRt) {
		case ANDROID:
			return hasMore;
		case JDK_1_6:
			methodName = "hasNext";
			break;
		default:
			throw new ProgrammerBrainNotFoundError();
		}

		return ((Boolean)XmlFactory.invokeParserMethod(parser, methodName, paramTypes,
				args)).booleanValue();
	}


	private static int getFieldIntValue(String s) {
		Field f;
		int i;

		try {
			f = XmlFactory.parserClass.getField(s);
			i = f.getInt(XmlFactory.parserClass);
		} catch (NoSuchFieldException e) {
			throw new Error("Programmer error: field " + s + " exception " +
					e.toString() + ": " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new Error(e.getMessage());
		}

		return i;
	}


	private static int getStartEleEnum() {
		Field f;
		String s;

		switch (XmlFactory.curRt) {
		case ANDROID:
			s = "START_TAG";
			break;
		case JDK_1_6:
			s = "START_ELEMENT";
			break;
		default:
			throw new Error("Programmer error: " + XmlFactory.curRt.name());
		}

		return getFieldIntValue(s);
	}


	private static int getEndEleEnum() {
		Field f;
		String s;

		switch (XmlFactory.curRt) {
		case ANDROID:
			s = "END_TAG";
			break;
		case JDK_1_6:
			s = "END_ELEMENT";
			break;
		default:
			throw new Error("Programmer error: " + XmlFactory.curRt.name());
		}

		return getFieldIntValue(s);
	}


	private static int getCharEnum() {
		Field f;
		String s;

		switch (XmlFactory.curRt) {
		case ANDROID:
			s = "TEXT";
			break;
		case JDK_1_6:
			s = "CHARACTERS";
			break;
		default:
			throw new Error("Programmer error: " + XmlFactory.curRt.name());
		}

		return getFieldIntValue(s);
	}


	private static int getCommentEnum() {
		Field f;
		String s;

		switch (XmlFactory.curRt) {
		case ANDROID:
			s = "COMMENT";
			break;
		case JDK_1_6:
			s = "COMMENT";
			break;
		default:
			throw new Error("Programmer error: " + XmlFactory.curRt.name());
		}

		return getFieldIntValue(s);
	}


	private static int getStartDocEnum() {
		Field f;
		String s;

		switch (XmlFactory.curRt) {
		case ANDROID:
			s = "START_DOCUMENT";
			break;
		case JDK_1_6:
			s = "START_DOCUMENT";
			break;
		default:
			throw new Error("Programmer error: " + XmlFactory.curRt.name());
		}

		return getFieldIntValue(s);
	}


	private static int getEndDocEnum() {
		Field f;
		String s;

		switch (XmlFactory.curRt) {
		case ANDROID:
			s = "END_DOCUMENT";
			break;
		case JDK_1_6:
			s = "END_DOCUMENT";
			break;
		default:
			throw new Error("Programmer error: " + XmlFactory.curRt.name());
		}

		return getFieldIntValue(s);
	}


	private static int getProcInstrEnum() {
		Field f;
		String s;

		switch (XmlFactory.curRt) {
		case ANDROID:
			s = "PROCESSING_INSTRUCTION";
			break;
		case JDK_1_6:
			s = "PROCESSING_INSTRUCTION";
			break;
		default:
			throw new Error("Programmer error: " + XmlFactory.curRt.name());
		}

		return getFieldIntValue(s);
	}


	private static int getEntRefEnum() {
		Field f;
		String s;

		switch (XmlFactory.curRt) {
		case ANDROID:
			s = "ENTITY_REF";
			break;
		case JDK_1_6:
			s = "ENTITY_REFERENCE";
			break;
		default:
			throw new Error("Programmer error: " + XmlFactory.curRt.name());
		}

		return getFieldIntValue(s);
	}


	private static int getDtdEnum() {
		Field f;
		String s;

		switch (XmlFactory.curRt) {
		case ANDROID:
			s = "DOCDECL";
			break;
		case JDK_1_6:
			s = "DTD";
			break;
		default:
			throw new Error("Programmer error: " + XmlFactory.curRt.name());
		}

		return getFieldIntValue(s);
	}


	public enum XmlEvent {
		START_ELEMENT(          XmlParser.getStartEleEnum()),
		END_ELEMENT(            XmlParser.getEndEleEnum()),
		CHARACTERS(             XmlParser.getCharEnum()),
		COMMENT(                XmlParser.getCommentEnum()),
		START_DOCUMENT(         XmlParser.getStartDocEnum()),
		END_DOCUMENT(           XmlParser.getEndDocEnum()),
		PROCESSING_INSTRUCTION( XmlParser.getProcInstrEnum()),
		ENTITY_REFERENCE(       XmlParser.getEntRefEnum()),
		DTD(                    XmlParser.getDtdEnum()),
		//ATTRIBUTE(XMLStreamReader.ATTRIBUTE),
		//NAMESPACE(XMLStreamReader.NAMESPACE),
		//CDATA(XMLStreamReader.CDATA),
		//SPACE(XMLStreamReader.SPACE),
		;


		private static XmlEvent[] list;
		private int eventType;


		XmlEvent(int eventType) {
			this.eventType = eventType;
		}

		public int value() {
			return eventType;
		}


		/**
		 * Takes a event type value from the underlying, runtime-dependent parser
		 * and returns the associated enum wrapper-value
		 */
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
}
