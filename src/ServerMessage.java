package ca.dioo.java.MonitorLib;

import java.util.ArrayList;
import java.util.Iterator;

//FIXME: Make sure toString() works correctly for all SubMessages
public class ServerMessage extends Message {
	public static final int VERSION[] = {1, 0};
	protected static final String XML_ROOT = "server_message";

	private SubMessage sub;
	private StateMachine sm;


	public static String getXmlRootName() {
		return XML_ROOT;
	}


	abstract public static class SubMessage {
		abstract public String[][] getAttributeList();


		public String toString() {
			return Utils.join(" ", ":", getAttributeList());
		}


		public String toString(int indent) {
			return Utils.repeat("  ", indent) + toString();
		}


		abstract void writeXmlString(XmlStringWriter xsw);
	}


	private enum StateMachine {
		INIT,
		ITEM_LIST,
		ITEM_LIST_ITEM,
		ITEM_LIST_END,
		ITEM,
		SNOOZE_ACK,
		END
	}


	public class SnoozeAck extends SubMessage {
		private long interval;

		public SnoozeAck() {
		}


		public SnoozeAck(long interval) {
			//FIXME
			//received = <current timestamp>

			setSnoozeInterval(interval);
		}


		public void setSnoozeInterval(long interval) {
			this.interval = interval;
		}


		public long getSnoozeInterval() {
			return interval;
		}


		public String[][] getAttributeList() {
			return new String[][]{{"interval", Long.toString(interval)}};
		}


		void writeXmlString(XmlStringWriter xsw) {
			xsw.writeTag("snooze_ack", getAttributeList());
			xsw.writeEndTag();
		}


	}


	public class ItemList extends SubMessage implements Iterable<Item> {
		private ArrayList<Item> itemList = new ArrayList<Item>();
		private int prevId;


		public ItemList() {
			this(-1);
		}


		public ItemList(int prevId) {
			setPrevId(prevId);
		}


		public void setPrevId(int prevId) {
			if (prevId >= 0) {
				this.prevId = prevId;
			} else {
				this.prevId = -1;
			}
		}


		public int getPrevId() {
			return prevId;
		}


		public boolean add(Item it) {
			return itemList.add(it);
		}


		public String[][] getAttributeList() {
			return new String[][]{{"prev_id", Integer.toString(prevId)}};
		}


		public String toString(int indent) {
			StringBuffer sb = new StringBuffer();
			sb.append("\n" + Utils.repeat("  ", indent) + "item_list" + toString());

			for (Item it: itemList) {
				sb.append("\n" + Utils.repeat("  ", indent + 1) + "item " + it.toString());
			}

			return sb.toString();
		}


		void writeXmlString(XmlStringWriter xsw) {
			xsw.writeTag("item_list", null, null);

			for (Item it: itemList) {
				it.writeXmlString(xsw);
			}
			xsw.writeEndTag();
		}


		/* Iterable interface */
		public Iterator<Item> iterator() {
			return itemList.iterator();
		}
	}


	public static class Item extends SubMessage {
		private int id;


		public Item() {
			id = -1;
		}


		public Item(int id) {
			setId(id);
		}


		public int getId() {
			return id;
		}


		public void setId(int id) {
			if (id >= -1) {
				this.id = id;
			}
		}


		public String[][] getAttributeList() {
			return new String[][]{{"id", Integer.toString(id)}};
		}


		void writeXmlString(XmlStringWriter xsw) {
			xsw.writeTag("item", getAttributeList());
			xsw.writeEndTag();
		}
	}


	public ServerMessage() {
		this(VERSION);
	}


	public ServerMessage(int[] version) {
		super(version);
	}


	/**
	 * Recommend using MessageFactory.parse()
	 */
	public ServerMessage(XmlParser xp) {
		super(xp);

		if (version[0] != 1 || version[1] != 0) {
			throw new Error("unsupported version " + version[0] + "." + version[1]);
		}

		sm = StateMachine.INIT;
	}


	public String toString() {
		return toString(0);
	}


	public String toString(int indent) {
		StringBuffer sb = new StringBuffer("version " + version[0] + "." + version[1]);

		sb.append(sub.toString(indent + 1));

		return sb.toString();
	}


	public String getXmlString() {
		XmlStringWriter xsw = new XmlStringWriter("server_message", getVersion());

		sub.writeXmlString(xsw);

		return xsw.getXmlString();
	}


	public SubMessage getSubMessage() {
		return sub;
	}


	//FIXME: rename ClientMessage 'Msg' to 'Item'
	public void buildAsResponse(ClientMessage cm) {
		ClientMessage.Action a = cm.iterator().next();
		switch (a.getActionType()) {
		case GET_MSG_LIST:
			sub = new ItemList(a.getPrevId());
			break;
		case GET_MSG:
			sub = new Item(a.getId());
			break;
		case SNOOZE:
			//FIXME: figure out the semantics here
			//sub = new SnoozeAck(a.getMinutes());
if (true) {
	throw new UnsupportedOperationException("server_message response not implemented");
} else {
			break;
}
		case DEL_MSG:
		default:
			throw new UnsupportedOperationException("server_message response not implemented");
		}
	}


	public void processXmlEvent(XmlParser.XmlEvent e) throws MalformedMessageException {
		switch (sm) {
		case INIT:
			if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, "item")) {
				processItem(e);
				sm = StateMachine.ITEM;
			} else if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, "item_list")) {
				processItemList(e);
				sm = StateMachine.ITEM_LIST;
			} else if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, "snooze_ack")) {
				processSnoozeAck(e);
				sm = StateMachine.SNOOZE_ACK;
			}
			break;
		case ITEM_LIST:
			if (compareElement(e, XmlParser.XmlEvent.END_ELEMENT, "item_list")) {
				sm = StateMachine.END;
			} else {
				processItemListItem(e);
				sm = StateMachine.ITEM_LIST_ITEM;
			}
			break;
		case ITEM_LIST_ITEM:
			if (e == XmlParser.XmlEvent.END_ELEMENT) {
				String name = xp.getLocalName();
				if (name.equals("item")) {
					//Pass
				} else if (name.equals("item_list")) {
					sm = StateMachine.END;
				} else {
					throw new MalformedMessageException("bogus end tag in server_message: " + name);
				}
			} else {
				processItemListItem(e);
			}
			break;
		case ITEM:
			break;
		case SNOOZE_ACK:
			break;
		case END:
			throw new MalformedMessageException("tag found after ITEM_LIST_END in server_message");
		default:
			throw new Error("unknown state machine state");
		}
	}


	public void processXmlRootEndTag() {
		//pass
	}


	private void processItem(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.START_ELEMENT, "item");
		if (sub != null) {
			throw new Error("Bogus item in server_message");
		}

		sub = _subprocessItem(e);
	}


	private Item _subprocessItem(XmlParser.XmlEvent e) throws MalformedMessageException {
		int id = -1;
		int attrCount = xp.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String attrName = xp.getAttributeName(i).toString();
			String attrVal = xp.getAttributeValue(i);

			if (attrName.equals("id")) {
				int nb = new Integer(attrVal);
				if (nb < 0) {
					throw new Error(attrName + " lower than 0 not allowed");
				}
				id = nb;
			}
		}

		return new Item(id);
	}


	private void processItemListItem(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.START_ELEMENT, "item");
		Item it = _subprocessItem(e);
		((ItemList)sub).add(it);
	}


	private void processItemList(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.START_ELEMENT, "item_list");
		if (sub != null) {
			throw new Error("Bogus item_list in server_message");
		}

		int prevId = -1;
		int attrCount = xp.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String attrName = xp.getAttributeName(i).toString();
			String attrVal = xp.getAttributeValue(i);

			if (attrName.equals("prev_id")) {
				int nb = new Integer(attrVal);
				if (nb < -1) {
					nb = -1;
				}
				prevId = nb;
			}
		}

		sub = new ItemList(prevId);
	}


	private void processSnoozeAck(XmlParser.XmlEvent e) throws MalformedMessageException {
		if (sub != null) {
			throw new Error("Bogus snooze_ack in server_message");
		}

		long interval = -1;
		int attrCount = xp.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String attrName = xp.getAttributeName(i).toString();
			String attrVal = xp.getAttributeValue(i);

			//FIXME: Figure out exact semantics needed here, using current timestamp and all
			//Maybe we shouldn't use exact timestamps in server responses for security reasons
			//Better idea: let client figure out lag by timing sending request to recv'ing response
			if (attrName.equals("interval")) {
				long nb = new Long(attrVal);
				if (nb < 0) {
					throw new Error(attrName + " lower than 0 not allowed");
				}
				interval = nb;
			}
		}

		sub = new SnoozeAck(interval);
	}
}
