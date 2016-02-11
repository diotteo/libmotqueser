package ca.dioo.java.MonitorLib;

import java.util.Vector;
import javax.xml.stream.XMLStreamReader;
import java.util.Iterator;

class ControlMessage extends Message {
	private Vector<Item> itemList;
	private StateMachine sm;
	private Item curItem;
	private Media curMedia;

	class Media {
		private String path;

		Media(String path) {
			this.path = path;
		}


		public String getPath() {
			return path;
		}
	}


	class Item {
		private int id;
		private Vector<Media> mediaList;

		Item(int id) {
			if (id < 1) {
				System.err.println("bogus Item ID");
			}

			this.id = id;
			mediaList = new Vector<Media>();
		}


		int getId() {
			return id;
		}


		void addMedia(Media m) {
			mediaList.add(m);
		}


		int size() {
			return mediaList.size();
		}


		boolean isEmpty() {
			return mediaList.isEmpty();
		}


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


	ControlMessage(XMLStreamReader xsr) {
		super(xsr);

		if (version[0] != 1 || version[1] != 0) {
			System.err.println("unsupported version " + version[0] + "." + version[1]);
		}

		sm = StateMachine.INIT;
	}

	void processXmlEvent(XmlEvent e) {
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


	private void processItem(XmlEvent e) {
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


	private void processMovie(XmlEvent e) {
		validateElement(e, XmlEvent.START_ELEMENT, "movie");
	}


	private void processMoviePath(XmlEvent e) {
		if (e != XmlEvent.CHARACTERS) {
			throw new MalformedMessageError("unexpected XML event");
		}

		curMedia = new Media(xsr.getText());
	}


	private void processMovieEnd(XmlEvent e) {
		validateElement(e, XmlEvent.END_ELEMENT, "movie");

		curItem.addMedia(curMedia);
		curMedia = null;
	}


	private void processItemEnd(XmlEvent e) {
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
