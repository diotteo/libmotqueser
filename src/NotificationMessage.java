package ca.dioo.java.libmotqueser;

import java.util.ArrayList;
import java.util.Iterator;

import ca.dioo.java.commons.Utils;

public class NotificationMessage extends BaseServerMessage {
	public static final int VERSION[] = {1, 0};
	protected static final String XML_ROOT = "notification_message";

	private Item it;
	private StateMachine sm;


	public static String getXmlRootName() {
		return XML_ROOT;
	}

	public String getXmlRoot() {
		return XML_ROOT;
	}


	protected enum StateMachine {
		INIT,
		ITEM,
		END
	}


	public NotificationMessage() {
		this(VERSION);
	}


	public NotificationMessage(int[] version) {
		super(version);
	}


	/**
	 * Recommend using MessageFactory.parse()
	 */
	NotificationMessage(XmlParser xp) {
		super(xp);

		if (version[0] != 1 || version[1] != 0) {
			throw new Error("unsupported version " + version[0] + "." + version[1]);
		}

		sm = StateMachine.INIT;
	}


	public String toString(int indent) {
		StringBuffer sb = new StringBuffer("version " + version[0] + "." + version[1]);

		sb.append(it.toString(indent + 1));

		return sb.toString();
	}


	public String getXmlString() {
		XmlStringWriter xsw = new XmlStringWriter(getXmlName(), getVersion());

		it.writeXmlString(xsw);

		return xsw.getXmlString();
	}


	public void processXmlEvent(XmlParser.XmlEvent e) throws MalformedMessageException {
		switch (sm) {
		case INIT:
			if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, Item.getTypeName())) {
				it = processItem(e);
				sm = StateMachine.ITEM;
			} else {
				throw new Error("unimplemented");
			}
			break;
		case ITEM:
			if (e == XmlParser.XmlEvent.END_ELEMENT) {
				String name = xp.getLocalName();
				if (name.equals(Item.getTypeName())) {
					//Pass
				} else {
					throw new MalformedMessageException("bogus end tag in " + getXmlName() + ": " + name);
				}
			} else {
				processItem(e);
			}
			break;
		case END:
			throw new Error("Should never happen");
		default:
			throw new Error("unknown state machine state");
		}
	}
}
