package ca.dioo.java.MonitorLib;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.io.Reader;
import java.io.Writer;

import ca.dioo.java.commons.Utils;

public class XmlFactory {
	private static Class parserFactoryClass;
	private static Class serFactoryClass;
	static Class parserClass;
	private static Class serClass;
	private static Object parserFactory;
	private static Object serFactory;
	private static Method newParser;
	private static Method newSer;

	static final Runtime curRt;

	static enum Runtime {
		ANDROID,
		JDK_1_6,
		;
	}

	static {
		Method newParserFactory;
		Method newSerFactory;
		Runtime rt;

		try {
			parserFactoryClass = Class.forName("org.xmlpull.v1.XmlPullParserFactory");
			serFactoryClass = parserFactoryClass;

			parserClass = Class.forName("org.xmlpull.v1.XmlPullParser");
			serClass = Class.forName("org.xmlpull.v1.XmlSerializer");

			newParserFactory = parserFactoryClass.getDeclaredMethod("newInstance");
			newSerFactory = newParserFactory;

			parserFactory = newParserFactory.invoke(parserFactoryClass);
			serFactory = newSerFactory.invoke(serFactoryClass);

			newParser = parserFactoryClass.getMethod("newPullParser");
			newSer = serFactoryClass.getMethod("newSerializer");

			rt = Runtime.ANDROID;
		} catch (ClassNotFoundException e) {
			try {
				parserFactoryClass = Class.forName("javax.xml.stream.XMLInputFactory");
				serFactoryClass = Class.forName("javax.xml.stream.XMLOutputFactory");

				parserClass = Class.forName("javax.xml.stream.XMLStreamReader");
				serClass = Class.forName("javax.xml.stream.XMLStreamWriter");

				newParserFactory = parserFactoryClass.getDeclaredMethod("newInstance");
				newSerFactory = serFactoryClass.getDeclaredMethod("newInstance");

				parserFactory = newParserFactory.invoke(parserFactoryClass);
				serFactory = newSerFactory.invoke(serFactoryClass);

				newParser = parserFactoryClass.
						getDeclaredMethod("createXMLStreamReader", Reader.class);
				newSer = serFactoryClass.
						getDeclaredMethod("createXMLStreamWriter", Writer.class);

				rt = Runtime.JDK_1_6;
			} catch (ClassNotFoundException e2) {
				throw new Error("This class requires JDK 1.6+ or Android, neither was found");
			} catch (InvocationTargetException e2) {
				Throwable t = e2.getCause();
				System.err.println(Utils.getPrettyStackTrace(t));
				throw new Error(t + " : " + t.getMessage());
			} catch (NoSuchMethodException e2) {
				throw new Error("Programmer is stupid, please report: " + e2.toString() +
						" msg:" + e2.getMessage());
			} catch (IllegalAccessException e2) {
				throw new Error(e.getMessage());
			}
		} catch (InvocationTargetException e) {
			Throwable t = e.getCause();
			System.err.println(Utils.getPrettyStackTrace(t));
			throw new Error(t + " : " + t.getMessage());
		} catch (NoSuchMethodException e) {
			throw new Error("Programmer is stupid, please report: " + e.toString() +
					" msg:" + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new Error(e.getMessage());
		}

		curRt = rt;
	}


	public static XmlParser newXmlParser(Reader r) {
		Object parser = null;

		try {
			switch (curRt) {
			case ANDROID:
				parser = newParser.invoke(parserFactory);
				Method setInput = parserClass.getDeclaredMethod("setInput", Reader.class);
				setInput.invoke(parser, r);
				break;
			case JDK_1_6:
				parser = newParser.invoke(parserFactory, r);
				break;
			}
		} catch (InvocationTargetException e) {
			Throwable t = e.getCause();
			System.err.println(Utils.getPrettyStackTrace(t));
			throw new Error(t + " : " + t.getMessage());
		} catch (NoSuchMethodException e) {
			throw new Error("Programmer is stupid, please report: " + e.toString() +
					" msg:" + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new Error(e.getMessage());
		}

		return new XmlParser(parser);
	}


	public static XmlSerializer newXmlSerializer(Writer w) {
		Object ser = null;

		try {
			switch (curRt) {
			case ANDROID:
				ser = newSer.invoke(serFactory);
				Method setOutput = serClass.getDeclaredMethod("setOutput", Writer.class);
				setOutput.invoke(ser, w);
				break;
			case JDK_1_6:
				ser = newSer.invoke(serFactory, w);
				break;
			}
		} catch (InvocationTargetException e) {
			Throwable t = e.getCause();
			System.err.println(Utils.getPrettyStackTrace(t));
			throw new Error(t + " : " + t.getMessage());
		} catch (NoSuchMethodException e) {
			throw new Error("Programmer is stupid, please report: " + e.toString() +
					" msg:" + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new Error(e.getMessage());
		}

		return new XmlSerializer(ser);
	}


	static Object invokeSerMethod(Object obj, String methodName, Class<?>[] parameterTypes, Object[] args) {
		return invokeMethod(serClass, obj, methodName, parameterTypes, args);
	}


	static Object invokeParserMethod(Object obj, String methodName, Class<?>[] parameterTypes, Object[] args) {
		return invokeMethod(parserClass, obj, methodName, parameterTypes, args);
	}


	private static Object invokeMethod(Class clazz, Object obj, String methodName, Class<?>[] parameterTypes, Object[] args) {

		Method m;
		Object o;

		try {
			m = clazz.getDeclaredMethod(methodName, parameterTypes);
			o = m.invoke(obj, args);
		} catch (InvocationTargetException e) {
			Throwable t = e.getCause();
			System.err.println(Utils.getPrettyStackTrace(t));
			throw new Error(t + " : " + t.getMessage());
		} catch (NoSuchMethodException e) {
			throw new Error("Programmer is stupid, please report: " + e.toString() +
					" msg:" + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new Error(e.getMessage());
		}

		return o;
	}
}
