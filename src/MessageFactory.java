package ca.dioo.java.MonitorLib;

import java.util.Hashtable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ca.dioo.java.commons.Utils;

public class MessageFactory {
	private MessageFactory() {}


	public static Message parse(XmlParser xp) throws MalformedMessageException {
		Hashtable<String,Class<? extends Message>> ht = new Hashtable<String,Class<? extends Message>>();
		ht.put(ClientMessage.getXmlRootName(), ClientMessage.class);
		ht.put(ControlMessage.getXmlRootName(), ControlMessage.class);
		ht.put(ServerMessage.getXmlRootName(), ServerMessage.class);

		try {
			return parse(ht, xp);
		} catch (InvocationTargetException e) {
			Throwable t = e.getCause();
			System.err.println(Utils.getPrettyStackTrace(t));
			throw new Error(t + " : " + t.getMessage());
		} catch (NoSuchMethodException e) {
			Throwable t = e.getCause();
			System.err.println(Utils.getPrettyStackTrace(t));
			throw new Error(e.getClass().getName() + ":" + e.getMessage(), e);
		} catch ( IllegalAccessException e) {
			throw new Error(e.getClass().getName() + ":" + e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new Error(e.getClass().getName() + ":" + e.getMessage(), e);
		} catch (InstantiationException e) {
			throw new Error(e.getClass().getName() + ":" + e.getMessage(), e);
		} catch (ExceptionInInitializerError e) {
			throw new Error(e.getClass().getName() + ":" + e.getMessage(), e);
		}
	}


	public static Message parse(Hashtable<String,Class<? extends Message>> map,
			XmlParser xp)
			throws NoSuchMethodException,
			IllegalAccessException,
			IllegalArgumentException,
			InstantiationException,
			InvocationTargetException,
			MalformedMessageException
			{
		Message message = null;
		String rootName = null;
		boolean is_complete = false;

		while (!is_complete && xp.hasNext()) {
			XmlParser.XmlEvent e = xp.next();
			//System.out.println(e.value() + " : " + e.toString());

			switch (e) {
			case START_ELEMENT:
				{
					String name = xp.getLocalName();

					if (message == null) {
						Class<? extends Message> mc = map.get(name);
						if (mc == null) {
							throw new MalformedMessageException("unknown root element \"" + name + "\"");
						} else {
							Constructor<? extends Message> c = mc.getConstructor(XmlParser.class);
							message = c.newInstance(xp);
							rootName = name;
						}
					} else {
						message.processXmlEvent(e);
					}

					/*
					int attrCount = xp.getAttributeCount();
					System.out.println(" " + xp.getLocalName() + " " + attrCount);
					for (int i = 0; i < attrCount; i++) {
						System.out.println("  ATTR " + i + " : " + xp.getAttributeName(i) +
								"=" + xp.getAttributeValue(i));
					}
					*/
				}
				break;
			case CHARACTERS:
				//System.out.println(" " + xp.getText());
				message.processXmlEvent(e);
				break;

			case END_ELEMENT:
				{
					String name = xp.getLocalName();
					//System.out.println(" " + xp.getLocalName());
					if (name.equals(rootName)) {
						message.processXmlRootEndTag();
						is_complete = true;
					} else {
						message.processXmlEvent(e);
					}
				}
				break;
			case COMMENT:
				//Ignore
				break;
			default:
				throw new MalformedMessageException("disallowed token type " + e.toString());
			}
		}

		return message;
	}
}
