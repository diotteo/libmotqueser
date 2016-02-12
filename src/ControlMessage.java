package ca.dioo.java.MonitorLib;

import java.util.Vector;
import javax.xml.stream.XMLStreamReader;
import java.util.Iterator;

class ControlMessage extends Message {
	private Vector<Item> itemList = new Vector<Item>();
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


	public static class Item {
		private int id;
		private Vector<Media> mediaList;

		public Item(int id) {
			if (id < 1) {
				throw new Error("bogus Item ID");
			}

			this.id = id;
			mediaList = new Vector<Media>();
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


		public Iterator<Media> iterator() {
			return mediaList.iterator();
		}


		public String toString() {
			return toString(0);
		}


		public String toString(int indent) {
			StringBuffer sb = new StringBuffer("id=" + id);

			for (Media m: mediaList) {
				sb.append("\n" + Utils.repeat("  ", indent + 1) + "media:" + m);
			}

			return sb.toString();
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


	public ControlMessage(int[] version) {
		super(version);
	}


	public ControlMessage(XMLStreamReader xsr) {
		super(xsr);

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


	void processXmlEvent(XmlEvent e) throws MalformedMessageException {
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


	private void processItem(XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlEvent.START_ELEMENT, "item");

		int id = -1;

		int attrCount = xsr.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String attrName = xsr.getAttributeName(i).toString();
			String attrVal = xsr.getAttributeValue(i);

			switch (attrName) {
			case "id":
				{
					int nb = new Integer(attrVal);
					if (nb < 0) {
						throw new Error(attrName + " lower than 0 not allowed");
					}
					id = nb;
				}
				break;
			}
		}

		if (id < 0) {
			throw new MalformedMessageError("item without id");
		}

		curItem = new Item(id);
	}


	private void processMovie(XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlEvent.START_ELEMENT, "movie");
	}


	private void processMoviePath(XmlEvent e) {
		if (e != XmlEvent.CHARACTERS) {
			throw new MalformedMessageError("unexpected XML event");
		}

		curMedia = new Media(xsr.getText());
	}


	private void processMovieEnd(XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlEvent.END_ELEMENT, "movie");

		curItem.add(curMedia);
		curMedia = null;
	}


	private void processItemEnd(XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlEvent.END_ELEMENT, "item");
		if (curItem.isEmpty()) {
			throw new MalformedMessageError("empty item");
		}
		curItem = null;
	}


	public Iterator<Item> iterator() {
		return itemList.iterator();
	}
}
