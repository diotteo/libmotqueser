package ca.dioo.java.MonitorLib;

import java.util.Hashtable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class MessageFactory {
	private MessageFactory() {}


	public static Message parse(XmlParser xp) throws MalformedMessageException {
		Hashtable<String,Class<? extends Message>> ht = new Hashtable<String,Class<? extends Message>>();
		ht.put("client_message", ClientMessage.class);
		ht.put("control_message", ControlMessage.class);
		ht.put("server_message", ServerMessage.class);

		try {
			return parse(ht, xp);
		} catch (InvocationTargetException e) {
			Throwable t = e.getCause();
			System.err.println(Utils.getPrettyStackTrace(t));
			throw new Error(t + " : " + t.getMessage());
		} catch (
				NoSuchMethodException|
				IllegalAccessException|
				IllegalArgumentException|
				InstantiationException|
				ExceptionInInitializerError e) {
			throw new Error(e.getClass().getName() + ":" + e.getMessage());
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
		boolean is_bogus = false;
		String errMsg = null;
		Message message = null;

		while (xp.hasNext() && !is_bogus) {
			XmlParser.XmlEvent e = xp.next();
			//System.out.println(e.value() + " : " + e.toString());

			switch (e) {
			case START_ELEMENT:
				String name = xp.getLocalName();

				if (message == null) {
					Class<? extends Message> mc = map.get(name);
					if (mc == null) {
						is_bogus = true;
						errMsg = "unknown root element \"" + name + "\"";
					} else {
						Constructor<? extends Message> c = mc.getConstructor(XmlParser.class);
						message = c.newInstance(xp);
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
				break;
			case CHARACTERS:
				//System.out.println(" " + xp.getText());
				message.processXmlEvent(e);
				break;

			case END_ELEMENT:
				//System.out.println(" " + xp.getLocalName());
				message.processXmlEvent(e);
				break;
			case COMMENT:
				//Ignore
				break;
			default:
				errMsg = "disallowed token type " + e.toString();
				is_bogus = true;
				break;
			}
		}

		return message;
	}
}
