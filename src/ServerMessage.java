package ca.dioo.java.MonitorLib;

import java.util.ArrayList;

public class ServerMessage extends Message {
	private ArrayList<Item> itemList = new ArrayList<Item>();
	private StateMachine sm;


	private enum StateMachine {
		INIT,
		ITEM_LIST,
		ITEM_LIST_ITEM,
		ITEM_LIST_END,
		ITEM,
		SNOOZE_ACK
	}


	public static class Item {
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


		public String toString() {
			return Utils.join(" ", ":", getAttributeList());
		}
	}



	public ServerMessage(int[] version) {
		super(version);
	}


	ServerMessage(XmlParser xp) {
		super(xp);

		if (version[0] != 1 || version[1] != 0) {
			throw new Error("unsupported version " + version[0] + "." + version[1]);
		}
	}


	public boolean add(Item e) {
		return itemList.add(e);
	}


	public String toString() {
		return toString(0);
	}


	public String toString(int indent) {
		StringBuffer sb = new StringBuffer("version " + version[0] + "." + version[1]);
		sb.append("\n" + Utils.repeat("  ", indent + 1) + "item_list");
		for (Item it: itemList) {
			sb.append("\n" + Utils.repeat("  ", indent + 2) + "item " + it.toString());
		}

		return sb.toString();
	}


	public String getXmlString() {
		XmlStringWriter xsw = new XmlStringWriter("server_message", new int[]{1, 0});

		xsw.writeTag("item_list", null, null);
		for (Item it: itemList) {
			xsw.writeTag("item", it.getAttributeList());
		}

		return xsw.getXmlString();
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
			processItemListItem(e);
			sm = StateMachine.ITEM_LIST_ITEM;
			break;
		case ITEM_LIST_ITEM:
			//Ignore both </item> and </item_list>
			if (e == XmlParser.XmlEvent.END_ELEMENT) {
				//ITEM_LIST_END
			} else {
				processItemListItem(e);
			}
			break;
		default:
		}
	}


	private void processItem(XmlParser.XmlEvent e) throws MalformedMessageException {
		//FIXME: Stub
		throw new UnsupportedOperationException("not implemented");
	}


	private void processItemList(XmlParser.XmlEvent e) {
		//Pass
	}


	private void processSnoozeAck(XmlParser.XmlEvent e) throws MalformedMessageException {
		//FIXME: Stub
		throw new UnsupportedOperationException("not implemented");
	}


	private void processItemListItem(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.START_ELEMENT, "item");

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
		itemList.add(new Item(id));
	}
}
