package ca.dioo.java.libmotqueser;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.dioo.java.commons.Utils;

public class ControlMessage extends Message implements Iterable<ControlMessage.Item> {
	public static final Version VERSION = new Version(1, 0);
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
				throw new NullPointerException("bogus Item ID");
			}

			this.id = id;
		}


		public String getId() {
			return id;
		}


		public String toString() {
			return Message.joinAttributeList(" ", ":", getAttributeList());
		}


		public String toString(int indent) {
			return Utils.repeat("  ", indent) + toString();
		}


		@SuppressWarnings("unchecked")
		public List<Attribute<String, String>> getAttributeList() {
			return Arrays.asList(
					new Attribute<String, String>("id", id));
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


	public ControlMessage(Version version) {
		super(version);
	}


	/**
	 * Recommend using MessageFactory.parse()
	 */
	public ControlMessage(XmlParser xp) throws UnsupportedVersionException {
		super(xp);

		if (!mVersion.equals(new Version(1, 0))) {
			throw new UnsupportedVersionException("unsupported version " + mVersion);
		}

		sm = StateMachine.INIT;
	}


	public boolean add(Item e) {
		return itemList.add(e);
	}


	public String toString() {
		return toString(0);
	}


	public String toString(int indent) {
		StringBuffer sb = new StringBuffer(getXmlRoot() + " version:" + mVersion);
		for (Item it: itemList) {
			sb.append("\n" + Utils.repeat("  ", indent + 1) + it.toString(indent + 1));
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
