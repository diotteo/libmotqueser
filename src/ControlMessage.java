package ca.dioo.java.MonitorLib;

import java.util.ArrayList;
import java.util.Iterator;

public class ControlMessage extends Message implements Iterable<ControlMessage.Item> {
	public static final int VERSION[] = {1, 0};
	private ArrayList<Item> itemList = new ArrayList<Item>();
	private StateMachine sm;
	private Item curItem;
	private Media curMedia;

	public static class Media {
		private String path;

		public Media(String path) {
			this.path = path;
		}


		public String getPath() {
			return path;
		}


		public String toString() {
			return path;
		}
	}


	public static class Item implements Iterable<Media> {
		private int id;
		private ArrayList<Media> mediaList;

		public Item(int id) {
			if (id < 1) {
				throw new Error("bogus Item ID");
			}

			this.id = id;
			mediaList = new ArrayList<Media>();
		}


		public int getId() {
			return id;
		}


		public boolean add(Media m) {
			return mediaList.add(m);
		}


		public int size() {
			return mediaList.size();
		}


		public boolean isEmpty() {
			return mediaList.isEmpty();
		}


		public String toString() {
			return toString(0);
		}


		public String toString(int indent) {
			StringBuffer sb = new StringBuffer(Utils.join(" ", ":", getAttributeList()));

			for (Media m: mediaList) {
				sb.append("\n" + Utils.repeat("  ", indent + 1) + "media:" + m);
			}

			return sb.toString();
		}


		public String[][] getAttributeList() {
			return new String[][]{{"id", Integer.toString(id)}};
		}


		/* Iterable interface methods */
		public Iterator<Media> iterator() {
			return mediaList.iterator();
		}
	}


	private enum StateMachine {
		INIT,
		ITEM,
		MOVIE,
		MOVIE_PATH,
		MOVIE_END,
		ITEM_END
	}


	public ControlMessage() {
		this(VERSION);
	}


	public ControlMessage(int[] version) {
		super(version);
	}


	ControlMessage(XmlParser xp) {
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
		XmlStringWriter xsw = new XmlStringWriter("control_message", getVersion());

		for (Item it: itemList) {
			xsw.writeTag("item", it.getAttributeList());
			for (Media m: it) {
				xsw.writeTag("media", null, m.getPath());
			}
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
			processMovie(e);
			sm = StateMachine.MOVIE;
			break;
		case MOVIE:
			processMoviePath(e);
			sm = StateMachine.MOVIE_PATH;
			break;
		case MOVIE_PATH:
			processMovieEnd(e);
			sm = StateMachine.MOVIE_END;
			break;
		case MOVIE_END:
			processItemEnd(e);
			sm = StateMachine.ITEM_END;
			break;
		case ITEM_END:
			processItem(e);
			sm = StateMachine.ITEM;
			break;
		}
	}


	private void processItem(XmlParser.XmlEvent e) throws MalformedMessageException {
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

		if (id < 0) {
			throw new MalformedMessageException("item without id");
		}

		curItem = new Item(id);
	}


	private void processMovie(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.START_ELEMENT, "movie");
	}


	private void processMoviePath(XmlParser.XmlEvent e) throws MalformedMessageException {
		if (e != XmlParser.XmlEvent.CHARACTERS) {
			throw new MalformedMessageException("unexpected XML event");
		}

		curMedia = new Media(xp.getText());
	}


	private void processMovieEnd(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.END_ELEMENT, "movie");

		curItem.add(curMedia);
		curMedia = null;
	}


	private void processItemEnd(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.END_ELEMENT, "item");
		if (curItem.isEmpty()) {
			throw new MalformedMessageException("empty item");
		}
		curItem = null;
	}


	public Iterator<Item> iterator() {
		return itemList.iterator();
	}
}
