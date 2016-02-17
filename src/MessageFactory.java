package ca.dioo.java.MonitorLib;

import java.util.Hashtable;
import javax.xml.stream.XMLStreamReader;
import java.lang.reflect.Constructor;
import javax.xml.stream.XMLStreamException;
import java.lang.reflect.InvocationTargetException;

public class MessageFactory {
	private MessageFactory() {}


	public static Message parse(XMLStreamReader xsr) throws MalformedMessageException {
		Hashtable<String,Class<? extends Message>> ht = new Hashtable<String,Class<? extends Message>>();
		ht.put("client_message", ClientMessage.class);
		ht.put("control_message", ControlMessage.class);
		ht.put("server_message", ServerMessage.class);

		try {
			return parse(ht, xsr);
		} catch (
				NoSuchMethodException|
				IllegalAccessException|
				IllegalArgumentException|
				InstantiationException|
				InvocationTargetException|
				ExceptionInInitializerError e) {
			throw new Error(e.getClass().getName() + ":" + e.getMessage());
		}
	}


	public static Message parse(Hashtable<String,Class<? extends Message>> map,
			XMLStreamReader xsr)
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

		try {
			while (xsr.hasNext() && !is_bogus) {
				int evt = xsr.next();
				XmlParser.XmlEvent e = XmlParser.XmlEvent.getEventFromValue(evt);
				System.out.println(e.value() + " : " + e.toString());

				switch (e) {
				case START_ELEMENT:
					String name = xsr.getLocalName();

					if (message == null) {
						Class<? extends Message> mc = map.get(name);
						if (mc == null) {
							is_bogus = true;
							errMsg = "unknown root element \"" + name + "\"";
						} else {
							Constructor<? extends Message> c = mc.getConstructor(XMLStreamReader.class);
							message = c.newInstance(xsr);
						}
						/*
						switch (name) {
						case "client_message":
							message = new ClientMessage(xsr);
							break;
						case "control_message":
							message = new ControlMessage(xsr);
							break;
						case "server_message":
							message = new ServerMessage(xsr);
							break;
						default:
							break;
						}
						*/
					} else {
						message.processXmlEvent(e);
					}

					/*
					int attrCount = xsr.getAttributeCount();
					System.out.println(" " + xsr.getLocalName() + " " + attrCount);
					for (int i = 0; i < attrCount; i++) {
						System.out.println("  ATTR " + i + " : " + xsr.getAttributeName(i) +
								"=" + xsr.getAttributeValue(i));
					}
					*/
					break;
				case CHARACTERS:
					//System.out.println(" " + xsr.getText());
					message.processXmlEvent(e);
					break;

				case END_ELEMENT:
					//System.out.println(" " + xsr.getLocalName());
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
		} catch (XMLStreamException e) {
			throw new Error(e.getMessage());
		}

		return message;
	}
}
