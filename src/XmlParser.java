package ca.dioo.java.MonitorLib;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.io.InputStream;


interface XmlParserFactory {
	void newInstance();
}


public class XmlParser {
	private static Class factoryClass;
	private static Class parserClass;
	private static XmlParserFactory factory;
	private static Method newFactory;
	private static Method newParser;
	private static Runtime curRt;

	private static enum Runtime {
		ANDROID,
		JDK_1_6,
		;
	}

	static {
		try {
			factoryClass = Class.forName("org.xmlpull.v1.XmlPullParserFactory");
			parserClass = Class.forName("org.xmlpull.v1.XmlPullParser");
			newFactory = factoryClass.getDeclaredMethod("newInstance");
			factory = (XmlParserFactory) newFactory.invoke(factoryClass);
			newParser = factoryClass.getDeclaredMethod("newPullParser");
			curRt = Runtime.ANDROID;
		} catch (ClassNotFoundException e) {
			try {
				factoryClass = Class.forName("javax.xml.stream.XMLInputFactory");
				parserClass = Class.forName("javax.xml.stream.XMLStreamReader");
			} catch (ClassNotFoundException e2) {
				throw new Error("This class requires JDK 1.6+ or Android, neither was found");
			}

			try {
				newFactory = factoryClass.getDeclaredMethod("newInstance");
				factory = (XmlParserFactory) newFactory.invoke(factoryClass);
				Method newParser = factoryClass.
						getDeclaredMethod("createXMLStreamReader", InputStream.class);
			} catch (NoSuchMethodException|IllegalAccessException|
					InvocationTargetException e2) {
				throw new Error("Programmer is stupid, please report: " + e2.toString() +
						" msg:" + e2.getMessage());
			}

			curRt = Runtime.JDK_1_6;
		} catch (NoSuchMethodException|IllegalAccessException|
				InvocationTargetException e) {
			throw new Error("Programmer is stupid, please report: " + e.toString() +
					" msg:" + e.getMessage());
		}
	}


	public static XmlParser newInstance(InputStream is) {
		XmlParser xp;

		try {
			xp = (XmlParser) newParser.invoke(factory);
			Method setInput = Class.forName("org.xmlpull.v1.XmlPullParser").
					getDeclaredMethod("setInput", InputStream.class, String.class);
			setInput.invoke(xp, is, null);

		} catch (ClassNotFoundException e) {
			try {
				xp = (XmlParser) newParser.invoke(factory, is);
			} catch (IllegalAccessException|InvocationTargetException e2) {
				throw new Error("Programmer is stupid, please report: " + e2.toString() +
						" msg:" + e2.getMessage());
			}

		} catch (NoSuchMethodException|IllegalAccessException|
				InvocationTargetException e) {
			throw new Error("Programmer is stupid, please report: " + e.toString() +
					" msg:" + e.getMessage());
		}

		return xp;
	}


	private XmlParser() {
	}



	private static int getFieldIntValue(String s) {
		Field f;
		int i;

		try {
			f = parserClass.getDeclaredField(s);
			i = f.getInt(parserClass);
		} catch (NoSuchFieldException|IllegalAccessException e) {
			throw new Error("Programmer error: field " + s + " exception " +
					e.toString() + ": " + e.getMessage());
		}

		return i;
	}


	private static int getStartEleEnum() {
		Field f;
		String s;

		switch (curRt) {
		case ANDROID:
			s = "START_TAG";
			break;
		case JDK_1_6:
			s = "START_ELEMENT";
			break;
		default:
			throw new Error("Programmer error: " + curRt.name());
		}

		return getFieldIntValue(s);
	}


	private static int getEndEleEnum() {
		Field f;
		String s;

		switch (curRt) {
		case ANDROID:
			s = "END_TAG";
			break;
		case JDK_1_6:
			s = "END_ELEMENT";
			break;
		default:
			throw new Error("Programmer error: " + curRt.name());
		}

		return getFieldIntValue(s);
	}


	private static int getCharEnum() {
		Field f;
		String s;

		switch (curRt) {
		case ANDROID:
			s = "TEXT";
			break;
		case JDK_1_6:
			s = "CHARACTERS";
			break;
		default:
			throw new Error("Programmer error: " + curRt.name());
		}

		return getFieldIntValue(s);
	}


	private static int getCommentEnum() {
		Field f;
		String s;

		switch (curRt) {
		case ANDROID:
			s = "COMMENT";
			break;
		case JDK_1_6:
			s = "COMMENT";
			break;
		default:
			throw new Error("Programmer error: " + curRt.name());
		}

		return getFieldIntValue(s);
	}


	private static int getStartDocEnum() {
		Field f;
		String s;

		switch (curRt) {
		case ANDROID:
			s = "START_DOCUMENT";
			break;
		case JDK_1_6:
			s = "START_DOCUMENT";
			break;
		default:
			throw new Error("Programmer error: " + curRt.name());
		}

		return getFieldIntValue(s);
	}


	private static int getEndDocEnum() {
		Field f;
		String s;

		switch (curRt) {
		case ANDROID:
			s = "END_DOCUMENT";
			break;
		case JDK_1_6:
			s = "END_DOCUMENT";
			break;
		default:
			throw new Error("Programmer error: " + curRt.name());
		}

		return getFieldIntValue(s);
	}


	private static int getProcInstrEnum() {
		Field f;
		String s;

		switch (curRt) {
		case ANDROID:
			s = "PROCESSING_INSTRUCTION";
			break;
		case JDK_1_6:
			s = "PROCESSING_INSTRUCTION";
			break;
		default:
			throw new Error("Programmer error: " + curRt.name());
		}

		return getFieldIntValue(s);
	}


	private static int getEntRefEnum() {
		Field f;
		String s;

		switch (curRt) {
		case ANDROID:
			s = "ENTITY_REF";
			break;
		case JDK_1_6:
			s = "ENTITY_REFERENCE";
			break;
		default:
			throw new Error("Programmer error: " + curRt.name());
		}

		return getFieldIntValue(s);
	}


	private static int getDtdEnum() {
		Field f;
		String s;

		switch (curRt) {
		case ANDROID:
			s = "DOCDECL";
			break;
		case JDK_1_6:
			s = "DTD";
			break;
		default:
			throw new Error("Programmer error: " + curRt.name());
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
