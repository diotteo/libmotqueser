package ca.dioo.java.MonitorLib;

import java.util.ArrayList;
import java.util.Iterator;

import ca.dioo.java.commons.Utils;

//FIXME: Make sure toString() works correctly for all Responses
public class ServerMessage extends Message {
	public static final int VERSION[] = {1, 0};
	protected static final String XML_ROOT = "server_message";

	private Response resp;
	private StateMachine sm;


	public static String getXmlRootName() {
		return XML_ROOT;
	}


	abstract public static class Response {
		abstract public String[][] getAttributeList();

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


	private enum StateMachine {
		INIT,
		ITEM_LIST,
		ITEM_LIST_ITEM,
		ITEM_LIST_END,
		ITEM,
		ITEM_DEL,
		SNOOZE_ACK,
		END
	}


	public static class SnoozeResponse extends Response {
		private static final String XML_TYPE_NAME = "snooze_response";

		private long interval;

		public SnoozeResponse() {
		}


		public SnoozeResponse(long interval) {
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


		public void setSnoozeInterval(long interval) {
			this.interval = interval;
		}


		public long getSnoozeInterval() {
			return interval;
		}


		public String[][] getAttributeList() {
			return new String[][]{{"interval", Long.toString(interval)}};
		}
	}


	public static class ItemListResponse extends Response implements Iterable<ItemResponse> {
		private static final String XML_TYPE_NAME = "item_list";

		private ArrayList<ItemResponse> itemList = new ArrayList<ItemResponse>();
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


		public boolean add(ItemResponse it) {
			return itemList.add(it);
		}


		public String[][] getAttributeList() {
			return new String[][]{{"prev_id", Integer.toString(prevId)}};
		}


		public String toString(int indent) {
			StringBuffer sb = new StringBuffer();
			sb.append("\n" + Utils.repeat("  ", indent) + getType() + toString());

			for (ItemResponse it: itemList) {
				sb.append("\n" + Utils.repeat("  ", indent + 1)
						+ ItemResponse.getTypeName() + " " + it.toString());
			}

			return sb.toString();
		}


		@Override
		void writeXmlString(XmlStringWriter xsw) {
			xsw.writeTag(getType(), getAttributeList());

			for (ItemResponse it: itemList) {
				it.writeXmlString(xsw);
			}
			xsw.writeEndTag();
		}


		/* Iterable interface */
		public Iterator<ItemResponse> iterator() {
			return itemList.iterator();
		}
	}


	public static class ItemResponse extends Response {
		private static final String XML_TYPE_NAME = "item";

		private int id;
		private ClientMessage.ItemRequest.MediaType type;


		public ItemResponse() {
			id = -1;
		}

		public ItemResponse(int id) {
			this(id, ClientMessage.ItemRequest.MediaType.VID);
		}

		public ItemResponse(int id, ClientMessage.ItemRequest.MediaType type) {
			setId(id);
			this.type = type;
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


		public ClientMessage.ItemRequest.MediaType getMediaType() {
			return type;
		}


		public String[][] getAttributeList() {
			return new String[][]{
					{"id", Integer.toString(id)},
					{"media", type.toString()},
					};
		}
	}


	public static class ItemDeletionResponse extends Response {
		private static final String XML_TYPE_NAME = "item_deleted";

		private int id;


		public ItemDeletionResponse() {
			id = -1;
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


		public String[][] getAttributeList() {
			return new String[][]{{"id", Integer.toString(id)}};
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
		XmlStringWriter xsw = new XmlStringWriter(XML_ROOT, getVersion());

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

		} else if (req instanceof ClientMessage.SnoozeRequest) {
			ClientMessage.SnoozeRequest r = (ClientMessage.SnoozeRequest)req;
			//FIXME: figure out the semantics here
			//resp = new SnoozeResponse(r.getInterval());
throw new UnsupportedOperationException("server_message response not implemented");

		} else {
			throw new Error("unimplemented server_message to Request");
		}
	}


	public void processXmlEvent(XmlParser.XmlEvent e) throws MalformedMessageException {
		switch (sm) {
		case INIT:
			if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, ItemResponse.getTypeName())) {
				processItem(e);
				sm = StateMachine.ITEM;
			} else if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, ItemListResponse.getTypeName())) {
				processItemListResponse(e);
				sm = StateMachine.ITEM_LIST;
			} else if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, SnoozeResponse.getTypeName())) {
				processSnoozeResponse(e);
				sm = StateMachine.SNOOZE_ACK;
			} else if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, ItemDeletionResponse.getTypeName())) {
				processItemDeletionResponse(e);
				sm = StateMachine.ITEM_DEL;
			} else {
				throw new Error("unimplemented");
			}
			break;
		case ITEM_LIST:
			if (compareElement(e, XmlParser.XmlEvent.END_ELEMENT, ItemListResponse.getTypeName())) {
				sm = StateMachine.END;
			} else {
				processItemListItem(e);
				sm = StateMachine.ITEM_LIST_ITEM;
			}
			break;
		case ITEM_LIST_ITEM:
			if (e == XmlParser.XmlEvent.END_ELEMENT) {
				String name = xp.getLocalName();
				if (name.equals(ItemResponse.getTypeName())) {
					//Pass
				} else if (name.equals(ItemListResponse.getTypeName())) {
					sm = StateMachine.ITEM_LIST_END;
				} else {
					throw new MalformedMessageException("bogus end tag in server_message: " + name);
				}
			} else {
				processItemListItem(e);
			}
			break;
		case ITEM:
		case ITEM_DEL:
		case SNOOZE_ACK:
			//pass
			break;
		case ITEM_LIST_END:
			throw new MalformedMessageException("tag found after ITEM_LIST_END in server_message");
		case END:
			throw new Error("Should never happen");
		default:
			throw new Error("unknown state machine state");
		}
	}


	public void processXmlRootEndTag() {
		//pass
	}


	private void processItem(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.START_ELEMENT, ItemResponse.getTypeName());
		if (resp != null) {
			throw new Error("Bogus item in server_message");
		}

		resp = _subprocessItem(e);
	}


	private ItemResponse _subprocessItem(XmlParser.XmlEvent e) throws MalformedMessageException {
		int id = -1;
		ClientMessage.ItemRequest.MediaType type = ClientMessage.ItemRequest.MediaType.VID;
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
			} else if (attrName.equals("media")) {
				if (attrVal.equals("VID")) {
					type = ClientMessage.ItemRequest.MediaType.VID;
				} else if (attrVal.equals("IMG")) {
					type = ClientMessage.ItemRequest.MediaType.IMG;
				}
			}
		}

		return new ItemResponse(id, type);
	}


	private void processItemListItem(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.START_ELEMENT, ItemResponse.getTypeName());
		ItemResponse it = _subprocessItem(e);
		((ItemListResponse)resp).add(it);
	}


	private void processItemListResponse(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.START_ELEMENT, ItemListResponse.getTypeName());
		if (resp != null) {
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

		resp = new ItemListResponse(prevId);
	}


	private void processSnoozeResponse(XmlParser.XmlEvent e) throws MalformedMessageException {
		if (resp != null) {
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

		resp = new SnoozeResponse(interval);
	}


	private void processItemDeletionResponse(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.START_ELEMENT, ItemDeletionResponse.getTypeName());
		if (resp != null) {
			throw new Error("Bogus " + ItemDeletionResponse.getTypeName() + " in " + XML_ROOT);
		}

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

		resp = new ItemDeletionResponse(id);
	}
}
