package ca.dioo.java.libmotqueser;

import java.util.ArrayList;
import java.util.Iterator;

import ca.dioo.java.commons.Utils;

public class ServerMessage extends BaseServerMessage {
	public static final int VERSION[] = {1, 0};
	protected static final String XML_ROOT = "server_message";

	private Response resp;
	private StateMachine sm;


	public static String getXmlRootName() {
		return XML_ROOT;
	}


	public String getXmlRoot() {
		return XML_ROOT;
	}


	abstract public static class Response {
		abstract public Attribute<String, String>[] getAttributeList();

		abstract public String getType();

		void writeXmlString(XmlStringWriter xsw) {
			xsw.writeTag(getType(), getAttributeList());
			xsw.writeEndTag();
		}

		public String toString() {
			return Utils.join(" ", ":", getAttributeList());
		}

		public String toString(int indent) {
			return Utils.repeat("  ", indent) + toString();
		}
	}


	protected enum StateMachine {
		INIT,
		ITEM_LIST,
		ITEM_LIST_ITEM,
		ITEM_LIST_END,
		ITEM,
		ITEM_DEL,
		ITEM_KEEP,
		SNOOZE_ACK,
		UNSNOOZE_ACK,
		END
	}


	public static class SnoozeResponse extends Response {
		private static final String XML_TYPE_NAME = "snooze_response";

		private int interval;

		public SnoozeResponse() {
			this(-1);
		}


		public SnoozeResponse(int interval) {
			//FIXME
			//received = <current timestamp>

			setSnoozeInterval(interval);
		}


		public static String getTypeName() {
			return XML_TYPE_NAME;
		}

		public String getType() {
			return getTypeName();
		}


		public void setSnoozeInterval(int interval) {
			this.interval = interval;
		}


		public int getSnoozeInterval() {
			return interval;
		}


		public Attribute<String, String>[] getAttributeList() {
			return new Attribute<String, String>[]{
					new Attribute<String, String>("interval", Integer.toString(interval)),
					};
		}
	}


	public static class UnsnoozeResponse extends Response {
		private static final String XML_TYPE_NAME = "unsnooze_response";

		public UnsnoozeResponse() {
		}


		public static String getTypeName() {
			return XML_TYPE_NAME;
		}

		public String getType() {
			return getTypeName();
		}


		public Attribute<String, String>[] getAttributeList() {
			return null;
		}
	}


	public static class ItemListResponse extends Response implements Iterable<Item> {
		private static final String XML_TYPE_NAME = "item_list_response";

		private ArrayList<Item> itemList = new ArrayList<Item>();
		private int prevId;


		public ItemListResponse() {
			this(-1);
		}


		public ItemListResponse(int prevId) {
			setPrevId(prevId);
		}


		public static String getTypeName() {
			return XML_TYPE_NAME;
		}

		public String getType() {
			return getTypeName();
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


		public Attribute<String, String>[] getAttributeList() {
			return new Attribute<String, String>[]{
					new Attribute("prev_id", Integer.toString(prevId)),
					};
		}


		public String toString(int indent) {
			StringBuffer sb = new StringBuffer();
			sb.append("\n" + Utils.repeat("  ", indent) + getType() + toString());

			for (Item it: itemList) {
				sb.append("\n" + Utils.repeat("  ", indent + 1)
						+ Item.getTypeName() + " " + it.toString());
			}

			return sb.toString();
		}


		@Override
		void writeXmlString(XmlStringWriter xsw) {
			xsw.writeTag(getType(), getAttributeList());

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


	public static class ItemResponse extends Response {
		private static final String XML_TYPE_NAME = "item_response";

		private int id;
		private long mediaSize;
		private MediaType type;


		public ItemResponse() {
			this(-1, null, 0);
		}

		public ItemResponse(int id) {
			this(id, null, 0);
		}

		public ItemResponse(int id, MediaType type) {
			this(id, type, 0);
		}

		public ItemResponse(int id, MediaType type, long mediaSize) {
			setId(id);
			setMediaType(type);
			setMediaSize(mediaSize);
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
			if (id >= -1) {
				this.id = id;
			}
		}

		public long getMediaSize() {
			return mediaSize;
		}

		public void setMediaSize(long size) {
			mediaSize = size;
		}

		public void setMediaType(MediaType type) {
			this.type = type;
		}

		public MediaType getMediaType() {
			return type;
		}


		public Attribute<String, String>[] getAttributeList() {
			ArrayList<String[]> al = new ArrayList<String[]>();

			al.add(new String[]{"id", Integer.toString(id)});
			if (type != null) {
				al.add(new String[]{"media", type.toString()});
			}
			al.add(new String[]{"media_size", Long.toString(mediaSize)});
			return al.toArray(new String[0][0]);
		}
	}


	public static class ItemDeletionResponse extends Response {
		private static final String XML_TYPE_NAME = "item_deleted";

		private int id;


		public ItemDeletionResponse() {
			this(-1);
		}


		public ItemDeletionResponse(int id) {
			setId(id);
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
			if (id >= -1) {
				this.id = id;
			}
		}


		public Attribute<String, String>[] getAttributeList() {
			return new Attribute<String, String>[]{
					new Attribute("id", Integer.toString(id)),
					};
		}
	}


	public static class ItemPreservationResponse extends Response {
		private static final String XML_TYPE_NAME = "item_kept";

		private int id;


		public ItemPreservationResponse() {
			this(-1);
		}


		public ItemPreservationResponse(int id) {
			setId(id);
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


		public Attribute<String, String>[] getAttributeList() {
			return new Attribute<String, String>[]{
					new Attribute("id", Integer.toString(id))
					};
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

		sb.append(resp.toString(indent + 1));

		return sb.toString();
	}


	public String getXmlString() {
		XmlStringWriter xsw = new XmlStringWriter(getXmlRoot(), getVersion());

		resp.writeXmlString(xsw);

		return xsw.getXmlString();
	}


	public Response getResponse() {
		return resp;
	}


	public void buildAsResponse(ClientMessage cm) {
		ClientMessage.Request req = cm.iterator().next();

		if (req instanceof ClientMessage.ItemListRequest) {
			ClientMessage.ItemListRequest r = (ClientMessage.ItemListRequest)req;
			resp = new ItemListResponse(r.getPrevId());

		} else if (req instanceof ClientMessage.ItemRequest) {
			ClientMessage.ItemRequest r = (ClientMessage.ItemRequest)req;
			resp = new ItemResponse(r.getId(), r.getMediaType());

		} else if (req instanceof ClientMessage.ItemDeletionRequest) {
			ClientMessage.ItemDeletionRequest r = (ClientMessage.ItemDeletionRequest)req;
			resp = new ItemDeletionResponse(r.getId());

		} else if (req instanceof ClientMessage.ItemPreservationRequest) {
			ClientMessage.ItemPreservationRequest r = (ClientMessage.ItemPreservationRequest)req;
			resp = new ItemPreservationResponse(r.getId());

		} else if (req instanceof ClientMessage.SnoozeRequest) {
			ClientMessage.SnoozeRequest r = (ClientMessage.SnoozeRequest)req;
			resp = new SnoozeResponse(r.getInterval());

		} else if (req instanceof ClientMessage.UnsnoozeRequest) {
			ClientMessage.UnsnoozeRequest r = (ClientMessage.UnsnoozeRequest)req;
			resp = new UnsnoozeResponse();
		} else {
			throw new Error("unimplemented " + getXmlRoot() + " to " + req.getType());
		}
	}


	public void processXmlEvent(XmlParser.XmlEvent e) throws MalformedMessageException {
		switch (sm) {
		case INIT:
			if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, ItemResponse.getTypeName())) {
				resp = processItemResponse(e);
				sm = StateMachine.ITEM;
			} else if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, ItemListResponse.getTypeName())) {
				resp = processItemListResponse(e);
				sm = StateMachine.ITEM_LIST;
			} else if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, SnoozeResponse.getTypeName())) {
				resp = processSnoozeResponse(e);
				sm = StateMachine.SNOOZE_ACK;
			} else if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, UnsnoozeResponse.getTypeName())) {
				resp = processUnsnoozeResponse(e);
				sm = StateMachine.UNSNOOZE_ACK;
			} else if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, ItemDeletionResponse.getTypeName())) {
				resp = processItemDeletionResponse(e);
				sm = StateMachine.ITEM_DEL;
			} else if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, ItemPreservationResponse.getTypeName())) {
				resp = processItemPreservationResponse(e);
				sm = StateMachine.ITEM_KEEP;
			} else {
				throw new Error("unimplemented");
			}
			break;
		case ITEM_LIST:
			if (compareElement(e, XmlParser.XmlEvent.END_ELEMENT, ItemListResponse.getTypeName())) {
				sm = StateMachine.END;
			} else {
				((ItemListResponse)resp).add(processItem(e));
				sm = StateMachine.ITEM_LIST_ITEM;
			}
			break;
		case ITEM_LIST_ITEM:
			if (e == XmlParser.XmlEvent.END_ELEMENT) {
				String name = xp.getLocalName();
				if (name.equals(Item.getTypeName())) {
					//Pass
				} else if (name.equals(ItemListResponse.getTypeName())) {
					sm = StateMachine.ITEM_LIST_END;
				} else {
					throw new MalformedMessageException("bogus end tag in " + getXmlRoot() + ": " + name);
				}
			} else {
				((ItemListResponse)resp).add(processItem(e));
			}
			break;
		case ITEM:
		case ITEM_DEL:
		case ITEM_KEEP:
		case SNOOZE_ACK:
		case UNSNOOZE_ACK:
			//pass
			break;
		case ITEM_LIST_END:
			throw new MalformedMessageException("tag found after " + sm + " in " + getXmlRoot());
		case END:
			throw new Error("Should never happen");
		default:
			throw new Error("unknown state machine state");
		}
	}


	public void processXmlRootEndTag() {
		//pass
	}


	protected ItemResponse processItemResponse(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.START_ELEMENT, ItemResponse.getTypeName());
		if (resp != null) {
			throw new MalformedMessageException("Bogus " + ItemResponse.getTypeName() + " in " + getXmlRoot());
		}

		int id = -1;
		long mediaSize = -1;
		MediaType type = null;
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
			} else if (attrName.equals("media")) {
				if (attrVal.equals("VID")) {
					type = MediaType.VID;
				} else if (attrVal.equals("IMG")) {
					type = MediaType.IMG;
				}
			} else if (attrName.equals("media_size")) {
				try {
					long nb = Long.parseLong(attrVal);
					if (nb < 0) {
						throw new MalformedMessageException(attrName + " lower than 0 not allowed");
					}
					mediaSize = nb;
				} catch (NumberFormatException e2) {
					throw new MalformedMessageException("bogus value for attribute " + attrName);
				}
			} else {
				//Utils.debugLog(1, "ignoring unknown item attribute " + attrName + " in " + getXmlRoot());
			}
		}

		return new ItemResponse(id, type, mediaSize);
	}


	protected ItemListResponse processItemListResponse(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.START_ELEMENT, ItemListResponse.getTypeName());
		if (resp != null) {
			throw new MalformedMessageException("Bogus " + ItemListResponse.getTypeName() + " in " + getXmlRoot());
		}

		int prevId = -1;
		int attrCount = xp.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String attrName = xp.getAttributeName(i).toString();
			String attrVal = xp.getAttributeValue(i);

			if (attrName.equals("prev_id")) {
				try {
					int nb = Integer.parseInt(attrVal);
					if (nb < -1) {
						nb = -1;
					}
					prevId = nb;
				} catch (NumberFormatException e2) {
					throw new MalformedMessageException("bogus value for attribute " + attrName);
				}
			}
		}

		return new ItemListResponse(prevId);
	}


	protected SnoozeResponse processSnoozeResponse(XmlParser.XmlEvent e) throws MalformedMessageException {
		if (resp != null) {
			throw new MalformedMessageException("Bogus " + SnoozeResponse.getTypeName() + " in " + getXmlRoot());
		}

		int interval = -1;
		int attrCount = xp.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String attrName = xp.getAttributeName(i).toString();
			String attrVal = xp.getAttributeValue(i);

			//FIXME: Figure out exact semantics needed here, using current timestamp and all
			//Maybe we shouldn't use exact timestamps in server responses for security reasons
			//Better idea: let client figure out lag by timing sending request to recv'ing response
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
			}
		}

		return new SnoozeResponse(interval);
	}


	protected UnsnoozeResponse processUnsnoozeResponse(XmlParser.XmlEvent e) throws MalformedMessageException {
		return new UnsnoozeResponse();
	}


	protected ItemDeletionResponse processItemDeletionResponse(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.START_ELEMENT, ItemDeletionResponse.getTypeName());
		if (resp != null) {
			throw new MalformedMessageException("Bogus " + ItemDeletionResponse.getTypeName() + " in " + getXmlRoot());
		}

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
			}
		}

		return new ItemDeletionResponse(id);
	}


	protected ItemPreservationResponse processItemPreservationResponse(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.START_ELEMENT, ItemPreservationResponse.getTypeName());
		if (resp != null) {
			throw new MalformedMessageException("Bogus " + ItemPreservationResponse.getTypeName() + " in " + getXmlRoot());
		}

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
			}
		}

		return new ItemPreservationResponse(id);
	}
}
