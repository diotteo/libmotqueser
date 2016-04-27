package ca.dioo.java.libmotqueser;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collection;

import ca.dioo.java.commons.Utils;

public class ServerMessage extends BaseServerMessage {
	public static final Version VERSION = new Version(1, 0);
	protected static final String XML_ROOT = "server_message";

	private Response mResp;
	private StateMachine sm;


	public static String getXmlRootName() {
		return XML_ROOT;
	}


	public String getXmlRoot() {
		return XML_ROOT;
	}


	abstract public static class Response {
		abstract public List<Attribute<String, String>> getAttributeList();

		abstract public String getType();

		void writeXmlString(XmlStringWriter xsw) {
			xsw.writeTag(getType(), getAttributeList());
			xsw.writeEndTag();
		}

		public String toString() {
			return getType() + " " + Message.joinAttributeList(" ", ":", getAttributeList());
		}

		public String toString(int indent) {
			return toString();
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
		CONFIG,
		END
	}


	public static class ConfigResponse extends Response implements Iterable<Attribute<String, String>> {
		private static final String XML_TYPE_NAME = "config_response";

		private List<Attribute<String, String>> attrList;

		public ConfigResponse() {
			this(null);
		}

		public ConfigResponse(Collection<Attribute<String, String>> c) {
			attrList = new ArrayList<Attribute<String, String>>();
			if (c != null) {
				attrList.addAll(c);
			}
		}

		public List<Attribute<String, String>> getAttributeList() {
			return attrList;
		}

		public String getType() {
			return getTypeName();
		}

		public static String getTypeName() {
			return XML_TYPE_NAME;
		}

		public boolean add(Attribute<String, String> at) {
			return attrList.add(at);
		}

		public boolean add(String name, String value) {
			return attrList.add(new Attribute<String, String>(name, value));
		}

		public boolean addAll(Collection<Attribute<String, String>> c) {
			return attrList.addAll(c);
		}

		public boolean addAllParam(Collection<String> c) {
			boolean ret = false;

			for (String s: c) {
				if (attrList.add(new Attribute<String, String>(s, null))) {
					ret = true;
				}
			}
			return ret;
		}

		/**
		 * @return value if found, null otherwise
		 */
		public String getValue(String name) {
			for (Attribute<String, String> at: attrList) {
				if (at.getName().equals(name)) {
					return at.getValue();
				}
			}
			return null;
		}

		public Iterator<Attribute<String, String>> iterator() {
			return attrList.iterator();
		}
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


		@SuppressWarnings("unchecked")
		public List<Attribute<String, String>> getAttributeList() {
			return Arrays.asList(
					new Attribute<String, String>("interval", Integer.toString(interval)));
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


		public List<Attribute<String, String>> getAttributeList() {
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


		@SuppressWarnings("unchecked")
		public List<Attribute<String, String>> getAttributeList() {
			return Arrays.asList(
					new Attribute<String, String>("prev_id", Integer.toString(prevId)));
		}


		public String toString() {
			return toString(0);
		}


		public String toString(int indent) {
			StringBuffer sb = new StringBuffer(super.toString());

			for (Item it: itemList) {
				sb.append("\n" + Utils.repeat("  ", indent + 1) + it.toString(indent + 1));
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


		public List<Attribute<String, String>> getAttributeList() {
			ArrayList<Attribute<String, String>> al = new ArrayList<Attribute<String, String>>();

			al.add(new Attribute<String, String>("id", Integer.toString(id)));
			if (type != null) {
				al.add(new Attribute<String, String>("media", type.toString()));
			}
			al.add(new Attribute<String, String>("media_size", Long.toString(mediaSize)));
			return al;
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


		@SuppressWarnings("unchecked")
		public List<Attribute<String, String>> getAttributeList() {
			return Arrays.asList(
					new Attribute<String, String>("id", Integer.toString(id)));
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


		@SuppressWarnings("unchecked")
		public List<Attribute<String, String>> getAttributeList() {
			return Arrays.asList(
					new Attribute<String, String>("id", Integer.toString(id)));
		}
	}


	public ServerMessage() {
		this(VERSION);
	}


	public ServerMessage(Version version) {
		super(version);
	}


	/**
	 * Recommend using MessageFactory.parse()
	 */
	public ServerMessage(XmlParser xp) throws UnsupportedVersionException {
		super(xp);

		if (!mVersion.equals(new Version(1, 0))) {
			throw new UnsupportedVersionException("unsupported version " + mVersion);
		}

		sm = StateMachine.INIT;
	}


	public String toString() {
		return toString(0);
	}


	public String toString(int indent) {
		return getXmlRoot() + " version:" + mVersion
				+ "\n" + Utils.repeat("  ", indent + 1) + mResp.toString(indent + 1);
	}


	public String getXmlString() {
		XmlStringWriter xsw = new XmlStringWriter(getXmlRoot(), mVersion);

		mResp.writeXmlString(xsw);

		return xsw.getXmlString();
	}


	public void setResponse(Response resp) {
		mResp = resp;
	}


	public Response getResponse() {
		return mResp;
	}


	public void buildAsResponse(ClientMessage cm) throws UnsupportedOperationException {
		ClientMessage.Request req = cm.iterator().next();

		if (req instanceof ClientMessage.ItemListRequest) {
			ClientMessage.ItemListRequest r = (ClientMessage.ItemListRequest) req;
			mResp = new ItemListResponse(r.getPrevId());

		} else if (req instanceof ClientMessage.ItemRequest) {
			ClientMessage.ItemRequest r = (ClientMessage.ItemRequest) req;
			mResp = new ItemResponse(r.getId(), r.getMediaType());

		} else if (req instanceof ClientMessage.ItemDeletionRequest) {
			ClientMessage.ItemDeletionRequest r = (ClientMessage.ItemDeletionRequest) req;
			mResp = new ItemDeletionResponse(r.getId());

		} else if (req instanceof ClientMessage.ItemPreservationRequest) {
			ClientMessage.ItemPreservationRequest r = (ClientMessage.ItemPreservationRequest) req;
			mResp = new ItemPreservationResponse(r.getId());

		} else if (req instanceof ClientMessage.SnoozeRequest) {
			ClientMessage.SnoozeRequest r = (ClientMessage.SnoozeRequest) req;
			mResp = new SnoozeResponse(r.getInterval());

		} else if (req instanceof ClientMessage.UnsnoozeRequest) {
			ClientMessage.UnsnoozeRequest r = (ClientMessage.UnsnoozeRequest) req;
			mResp = new UnsnoozeResponse();

		} else if (req instanceof ClientMessage.ConfigRequest) {
			ClientMessage.ConfigRequest r = (ClientMessage.ConfigRequest) req;
			mResp = new ConfigResponse();
			((ConfigResponse) mResp).addAllParam(r.getParamList());

		} else {
			throw new UnsupportedOperationException("unimplemented " + getXmlRoot() + " to " + req.getType());
		}
	}


	public void processXmlEvent(XmlParser.XmlEvent e) throws MalformedMessageException {
		switch (sm) {
		case INIT:
			if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, ItemResponse.getTypeName())) {
				mResp = processItemResponse(e);
				sm = StateMachine.ITEM;
			} else if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, ItemListResponse.getTypeName())) {
				mResp = processItemListResponse(e);
				sm = StateMachine.ITEM_LIST;
			} else if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, SnoozeResponse.getTypeName())) {
				mResp = processSnoozeResponse(e);
				sm = StateMachine.SNOOZE_ACK;
			} else if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, UnsnoozeResponse.getTypeName())) {
				mResp = processUnsnoozeResponse(e);
				sm = StateMachine.UNSNOOZE_ACK;
			} else if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, ItemDeletionResponse.getTypeName())) {
				mResp = processItemDeletionResponse(e);
				sm = StateMachine.ITEM_DEL;
			} else if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, ItemPreservationResponse.getTypeName())) {
				mResp = processItemPreservationResponse(e);
				sm = StateMachine.ITEM_KEEP;
			} else if (compareElement(e, XmlParser.XmlEvent.START_ELEMENT, ConfigResponse.getTypeName())) {
				mResp = processConfigResponse(e);
				sm = StateMachine.CONFIG;
			} else {
				//FIXME: give informative message
				throw new UnsupportedOperationException("unimplemented");
			}
			break;
		case ITEM_LIST:
			if (compareElement(e, XmlParser.XmlEvent.END_ELEMENT, ItemListResponse.getTypeName())) {
				sm = StateMachine.END;
			} else {
				((ItemListResponse) mResp).add(processItem(e));
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
				((ItemListResponse) mResp).add(processItem(e));
			}
			break;

		case CONFIG:
			//FIXME: handle multiple <config></config>
			break;

		//FIXME: verify that current tag is the proper end tag
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
		if (mResp != null) {
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
		if (mResp != null) {
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
		if (mResp != null) {
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
		if (mResp != null) {
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
		if (mResp != null) {
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


	protected ConfigResponse processConfigResponse(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.START_ELEMENT, ConfigResponse.getTypeName());
		if (mResp == null) {
			mResp = new ConfigResponse();
		}

		List<Attribute<String, String>> attrList = new ArrayList<Attribute<String, String>>();
		int attrCount = xp.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String attrName = xp.getAttributeName(i).toString();
			String attrVal = xp.getAttributeValue(i);

			attrList.add(new Attribute<String, String>(attrName, attrVal));
		}
		((ConfigResponse) mResp).addAll(attrList);

		return (ConfigResponse) mResp;
	}
}
