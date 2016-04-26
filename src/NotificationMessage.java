package ca.dioo.java.libmotqueser;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.dioo.java.commons.Utils;

public class NotificationMessage extends BaseServerMessage {
	public static final Version VERSION = new Version(1, 0);
	protected static final String XML_ROOT = "notification_message";

	private NotificationItem it;
	private StateMachine sm;


	protected enum StateMachine {
		INIT,
		NEW_ITEM,
		NEW_ITEM_END,
		ITEM,
		ITEM_END,
		REMOVED_ITEM,
		REMOVED_ITEM_END,
		SNOOZE_CHANGE,
		SNOOZE_CHANGE_END,
		END,
	}


	public static abstract class NotificationItem {
		abstract public List<Attribute<String, String>> getAttributeList();
		abstract public String getType();

		void writeXmlString(XmlStringWriter xsw) {
			xsw.writeTag(getType(), getAttributeList());
			xsw.writeEndTag();
		}

		public String toString() {
			return getType() + " " + Message.joinAttributeList(" ", ":", getAttributeList());
		}

		public String toString(int indent) {
			return toString();
		}
	}


	public static class NewItem extends NotificationItem {
		private static final String XML_TYPE_NAME = "new_item";
		private Item it;

		public NewItem() {
			this(null);
		}

		public NewItem(Item it) {
			setItem(it);
		}


		public void setItem(Item it) {
			this.it = it;
		}

		public Item getItem() {
			return it;
		}

		public List<Attribute<String, String>> getAttributeList() {
			return null;
		}

		public static String getTypeName() {
			return XML_TYPE_NAME;
		}

		public String getType() {
			return getTypeName();
		}

		@Override
		void writeXmlString(XmlStringWriter xsw) {
			xsw.writeTag(getType(), getAttributeList());
			it.writeXmlString(xsw);
			xsw.writeEndTag();
		}
	}


	public static class SnoozeChange extends NotificationItem {
		private static final String XML_TYPE_NAME = "snooze_change";
		private int interval;

		public SnoozeChange() {
			this(-1);
		}

		public SnoozeChange(int interval) {
			setInterval(interval);
		}


		public void setInterval(int interval) {
			this.interval = interval;
		}

		public int getInterval() {
			return interval;
		}

		@SuppressWarnings("unchecked")
		public List<Attribute<String, String>> getAttributeList() {
			return Arrays.asList(
					new Attribute<String, String>("interval", Integer.toString(interval)));
		}

		public static String getTypeName() {
			return XML_TYPE_NAME;
		}

		public String getType() {
			return getTypeName();
		}
	}


	public static class RemovedItem extends NotificationItem {
		private static final String XML_TYPE_NAME = "removed_item";
		private int itemId;

		public RemovedItem() {
			this(-1);
		}

		public RemovedItem(int itemId) {
			setItemId(itemId);
		}


		public void setItemId(int itemId) {
			this.itemId = itemId;
		}

		public int getItemId() {
			return itemId;
		}

		@SuppressWarnings("unchecked")
		public List<Attribute<String, String>> getAttributeList() {
			return Arrays.asList(
					new Attribute<String, String>("id", Integer.toString(itemId)));
		}

		public static String getTypeName() {
			return XML_TYPE_NAME;
		}

		public String getType() {
			return getTypeName();
		}
	}


	public static String getXmlRootName() {
		return XML_ROOT;
	}

	public String getXmlRoot() {
		return XML_ROOT;
	}


	public NotificationMessage() {
		this(VERSION);
	}


	public NotificationMessage(Version version) {
		super(version);
	}


	/**
	 * Recommend using MessageFactory.parse()
	 */
	public NotificationMessage(XmlParser xp) throws UnsupportedVersionException {
		super(xp);

		if (!mVersion.equals(new Version(1, 0))) {
			throw new UnsupportedVersionException("unsupported version " + mVersion);
		}

		sm = StateMachine.INIT;
	}


	public void setItem(NotificationItem it) {
		this.it = it;
	}


	public NotificationItem getItem() {
		return it;
	}


	public String toString() {
		return toString(0);
	}


	public String toString(int indent) {
		return getXmlRoot() + " version:" + mVersion
				+ "\n" + Utils.repeat("  ", indent + 1) + it.toString(indent + 1);
	}


	public String getXmlString() {
		XmlStringWriter xsw = new XmlStringWriter(getXmlRoot(), getVersion());

		it.writeXmlString(xsw);

		return xsw.getXmlString();
	}


	public void processXmlEvent(XmlParser.XmlEvent e) throws MalformedMessageException {
		switch (sm) {
		case INIT:
			if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, NewItem.getTypeName())) {
				it = processNewItem(e);
				sm = StateMachine.NEW_ITEM;
			} else if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, RemovedItem.getTypeName())) {
				it = processRemovedItem(e);
				sm = StateMachine.REMOVED_ITEM;
			} else if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, SnoozeChange.getTypeName())) {
				it = processSnoozeChange(e);
				sm = StateMachine.SNOOZE_CHANGE;
			} else {
				//FIXME: send informative message string
				throw new MalformedMessageException("received unexpected XML element");
			}
			break;
		case NEW_ITEM:
			if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, Item.getTypeName())) {
				((NewItem) it).setItem(processItem(e));
				sm = StateMachine.ITEM;
			} else {
				String name = xp.getLocalName();
				throw new MalformedMessageException("bogus end tag in " + getXmlRoot() + ": " + name);
			}
			break;
		case REMOVED_ITEM:
			if (compareElement(e, XmlParser.XmlEvent.END_ELEMENT, RemovedItem.getTypeName())) {
				sm = StateMachine.REMOVED_ITEM_END;
			} else {
				String name = xp.getLocalName();
				throw new MalformedMessageException("bogus end tag in " + getXmlRoot() + ": " + name);
			}
			break;
		case SNOOZE_CHANGE:
			if (compareElement(e, XmlParser.XmlEvent.END_ELEMENT, SnoozeChange.getTypeName())) {
				sm = StateMachine.SNOOZE_CHANGE_END;
			} else {
				String name = xp.getLocalName();
				throw new MalformedMessageException("bogus end tag in " + getXmlRoot() + ": " + name);
			}
			break;
		case ITEM:
			if (compareElement(e, XmlParser.XmlEvent.END_ELEMENT, Item.getTypeName())) {
				//only one Item allowed for now
				//((NewItem) it).addItem(processItem(e));

				sm = StateMachine.ITEM_END;
			} else {
				String name = xp.getLocalName();
				throw new MalformedMessageException("bogus end tag in " + getXmlRoot() + ": " + name);
			}
			break;
		case ITEM_END:
			if (compareElement(e, XmlParser.XmlEvent.END_ELEMENT, NewItem.getTypeName())) {
				sm = StateMachine.NEW_ITEM_END;
			} else {
				String name = xp.getLocalName();
				throw new MalformedMessageException("bogus end tag in " + getXmlRoot() + ": " + name);
			}
			break;
		case REMOVED_ITEM_END:
		case SNOOZE_CHANGE_END:
		case NEW_ITEM_END:
			processLastTag(e);
			break;
		case END:
			throw new Error("Should never happen");
		default:
			throw new Error("unknown state machine state");
		}
	}


	protected void processLastTag(XmlParser.XmlEvent e) throws MalformedMessageException {
		if (compareElement(e, XmlParser.XmlEvent.END_ELEMENT, getXmlRoot())) {
			sm = StateMachine.END;
		} else {
			String name = xp.getLocalName();
			throw new MalformedMessageException("bogus end tag in " + getXmlRoot() + ": " + name);
		}
	}


	protected NewItem processNewItem(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.START_ELEMENT, NewItem.getTypeName());

		return new NewItem();
	}

	protected SnoozeChange processSnoozeChange(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.START_ELEMENT, SnoozeChange.getTypeName());

		int interval = -1;
		int attrCount = xp.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String attrName = xp.getAttributeName(i).toString();
			String attrVal = xp.getAttributeValue(i);

			if (attrName.equals("interval")) {
				try {
					int nb = Integer.parseInt(attrVal);
					if (nb < 0) {
						throw new MalformedMessageException(attrName + " lower than 0 not allowed");
					}
					interval = nb;
				} catch (NumberFormatException e2) {
					throw new MalformedMessageException("bogus value for attribute " + attrName);
				}
			} else {
				//Utils.debugLog(1, "ignoring unknown item attribute " + attrName + " in " + getXmlRoot());
			}
		}

		return new SnoozeChange(interval);
	}

	protected RemovedItem processRemovedItem(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.START_ELEMENT, RemovedItem.getTypeName());

		int id = -1;
		int attrCount = xp.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String attrName = xp.getAttributeName(i).toString();
			String attrVal = xp.getAttributeValue(i);

			if (attrName.equals("id")) {
				try {
					int nb = Integer.parseInt(attrVal);
					if (nb < 0) {
						throw new MalformedMessageException(attrName + " lower than 0 not allowed");
					}
					id = nb;
				} catch (NumberFormatException e2) {
					throw new MalformedMessageException("bogus value for attribute " + attrName);
				}
			} else {
				//Utils.debugLog(1, "ignoring unknown item attribute " + attrName + " in " + getXmlRoot());
			}
		}

		return new RemovedItem(id);
	}


	public void processXmlRootEndTag() {
		//Pass
	}
}
