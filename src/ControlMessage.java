package ca.dioo.java.libmotqueser;

import java.util.ArrayList;
import java.util.Iterator;

import ca.dioo.java.commons.Utils;

public class ControlMessage extends Message implements Iterable<ControlMessage.Item> {
	public static final int VERSION[] = {1, 0};
	private static final String XML_ROOT = "control_message";

	private ArrayList<Item> itemList = new ArrayList<Item>();
	private StateMachine sm;
	private Item curItem;


	public static String getXmlRootName() {
		return XML_ROOT;
	}


	public String getXmlRoot() {
		return XML_ROOT;
	}


	public static class Item {
		private String id;

		public Item(String id) {
			if (id == null) {
				throw new Error("bogus Item ID");
			}

			this.id = id;
		}


		public String getId() {
			return id;
		}


		public String toString() {
			return toString(0);
		}


		public String toString(int indent) {
			StringBuffer sb = new StringBuffer(Utils.join(" ", ":", getAttributeList()));

			return sb.toString();
		}


		public Attribute<String, String>[] getAttributeList() {
			return new Attribute<String, String>[]{
					new Attribute<String, String>("id", id),
					};
		}
	}


	private enum StateMachine {
		INIT,
		ITEM,
		MEDIA,
		MEDIA_PATH,
		MEDIA_END,
		ITEM_END
	}


	public ControlMessage() {
		this(VERSION);
	}


	public ControlMessage(int[] version) {
		super(version);
	}


	/**
	 * Recommend using MessageFactory.parse()
	 */
	public ControlMessage(XmlParser xp) {
		super(xp);

		if (version[0] != 1 || version[1] != 0) {
			throw new Error("unsupported version " + version[0] + "." + version[1]);
		}

		sm = StateMachine.INIT;
	}


	public boolean add(Item e) {
		return itemList.add(e);
	}


	public String toString() {
		StringBuffer sb = new StringBuffer("version " + version[0] + "." + version[1]);
		for (Item it: itemList) {
			sb.append("\n" + "  " + "item:" + it.toString(1));
		}

		return sb.toString();
	}


	public String getXmlString() {
		XmlStringWriter xsw = new XmlStringWriter(getXmlRootName(), getVersion());

		for (Item it: itemList) {
			xsw.writeTag("item", it.getAttributeList());
			xsw.writeEndTag();
		}

		return xsw.getXmlString();
	}


	public void processXmlEvent(XmlParser.XmlEvent e) throws MalformedMessageException {
		switch (sm) {
		case INIT:
			processItem(e);
			sm = StateMachine.ITEM;
			break;
		case ITEM:
			processItemEnd(e);
			sm = StateMachine.ITEM_END;
			break;
		case ITEM_END:
			processItem(e);
			sm = StateMachine.ITEM;
			break;
		}
	}


	public void processXmlRootEndTag() {
		//pass
	}


	private void processItem(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.START_ELEMENT, "item");

		String id = null;

		int attrCount = xp.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String attrName = xp.getAttributeName(i).toString();
			String attrVal = xp.getAttributeValue(i);

			if (attrName.equals("id")) {
				id = attrVal;
			}
		}

		if (id == null) {
			throw new MalformedMessageException("item without id");
		}

		curItem = new Item(id);
	}


	private void processItemEnd(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.END_ELEMENT, "item");
		itemList.add(curItem);
		curItem = null;
	}


	public Iterator<Item> iterator() {
		return itemList.iterator();
	}
}
