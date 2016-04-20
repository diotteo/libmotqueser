package ca.dioo.java.libmotqueser;

import java.util.ArrayList;
import java.util.List;

import ca.dioo.java.commons.Utils;

public abstract class BaseServerMessage extends Message {
	public static final Version VERSION = new Version(1, 0);

	public static class Item {
		private static final String XML_TYPE_NAME = "item";
		private int id;
		private String name;
		private int imgSize;
		private int vidSize;
		private String vidLen;


		public Item() {
			this(-1, null, -1, -1, null);
		}

		public Item(int id) {
			this(id, null, -1, -1, null);
		}

		public Item(int id, String name) {
			this(id, name, -1, -1, null);
		}

		public Item(int id, String name, int imgSize, int vidSize, String vidLen) {
			setId(id);
			setName(name);
			setImgSize(imgSize);
			setVidSize(vidSize);
			setVidLen(vidLen);
		}


		public static String getTypeName() {
			return XML_TYPE_NAME;
		}

		public String getType() {
			return getTypeName();
		}


		public int getId() {
			return id;
		}

		public void setId(int id) {
			if (id < 0) {
				this.id = -1;
			} else {
				this.id = id;
			}
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getImgSize() {
			return imgSize;
		}

		public void setImgSize(int imgSize) {
			if (imgSize < 0) {
				this.imgSize = -1;
			} else {
				this.imgSize = imgSize;
			}
		}

		public int getVidSize() {
			return vidSize;
		}

		public void setVidSize(int vidSize) {
			if (vidSize < 0) {
				this.vidSize = -1;
			} else {
				this.vidSize = vidSize;
			}
		}

		public String getVidLen() {
			return vidLen;
		}

		public void setVidLen(String vidLen) {
			this.vidLen = vidLen;
		}


		public List<Attribute<String, String>> getAttributeList() {
			ArrayList<Attribute<String, String>> al = new ArrayList<Attribute<String, String>>();
			al.add(new Attribute<String, String>("id", Integer.toString(id)));
			al.add(new Attribute<String, String>("name", name));
			al.add(new Attribute<String, String>("img_size", Integer.toString(imgSize)));
			al.add(new Attribute<String, String>("vid_size", Integer.toString(vidSize)));
			if (vidLen != null) {
				al.add(new Attribute<String, String>("vid_len", vidLen));
			}
			return al;
		}


		void writeXmlString(XmlStringWriter xsw) {
			xsw.writeTag(getType(), getAttributeList());
			xsw.writeEndTag();
		}

		public String toString() {
			return Message.joinAttributeList(" ", ":", getAttributeList());
		}

		public String toString(int indent) {
			return Utils.repeat("  ", indent) + toString();
		}
	}


	protected BaseServerMessage() {
		this(VERSION);
	}


	protected BaseServerMessage(Version version) {
		super(version);
	}


	/**
	 * Recommend using MessageFactory.parse()
	 */
	public BaseServerMessage(XmlParser xp) {
		super(xp);
	}


	protected Item processItem(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.START_ELEMENT, Item.getTypeName());

		int id = -1;
		String name = null;
		int imgSize = -1;
		int vidSize = -1;
		String vidLen = null;

		int attrCount = xp.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String attrName = xp.getAttributeName(i).toString();
			String attrVal = xp.getAttributeValue(i);

			boolean isId = false;
			boolean isImgSize = false;
			boolean isVidSize = false;

			if (attrName.equals("id")) {
				isId = true;
			} else if (attrName.equals("img_size")) {
				isImgSize = true;
			} else if (attrName.equals("vid_size")) {
				isVidSize = true;
			}

			if (isId || isImgSize || isVidSize) {
				try {
					int nb = Integer.parseInt(attrVal);
					if (nb < 0) {
						throw new MalformedMessageException(attrName + " lower than 0 not allowed");
					}

					if (isId) {
						id = nb;
					} else if (isImgSize) {
						imgSize = nb;
					} else if (isVidSize) {
						vidSize = nb;
					}
				} catch (NumberFormatException e2) {
					throw new MalformedMessageException("bogus value for attribute " + attrName);
				}
			} else if (attrName.equals("vid_len")) {
				vidLen = attrVal;
			} else if (attrName.equals("name")) {
				name = attrVal;
			}
		}

		return new Item(id, name, imgSize, vidSize, vidLen);
	}
}
