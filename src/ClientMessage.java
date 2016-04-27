package ca.dioo.java.libmotqueser;

import java.util.Iterator;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import ca.dioo.java.commons.Utils;

public class ClientMessage extends Message implements Iterable<ClientMessage.Request> {
	public static final Version VERSION = new Version(1, 0);
	private static final String XML_ROOT = "client_message";
	private ArrayList<Request> reqList = new ArrayList<Request>();
	private StateMachine sm;


	public static String getXmlRootName() {
		return XML_ROOT;
	}


	public String getXmlRoot() {
		return XML_ROOT;
	}


	private enum StateMachine {
		INIT,
		ACTION,
		END
	}


	abstract public static class Request {
		abstract public List<Attribute<String, String>> getAttributeList();

		abstract public String getType();

		public String toString() {
			return Message.joinAttributeList(" ", ":", getAttributeList());
		}

		public String toString(int indent) {
			return Utils.repeat("  ", indent) + toString();
		}

		void writeXmlString(XmlStringWriter xsw) {
			xsw.writeTag("action", getAttributeList());
			xsw.writeEndTag();
		}
	}


	public static class ItemListRequest extends Request {
		private static final String XML_TYPE_NAME = "get_item_list";

		private int prevId;


		public ItemListRequest() {
			this(-1);
		}


		public ItemListRequest(int prevId) {
			setPrevId(prevId);
		}


		public static String getTypeName() {
			return XML_TYPE_NAME;
		}

		public String getType() {
			return getTypeName();
		}


		@SuppressWarnings("unchecked")
		public List<Attribute<String, String>> getAttributeList() {
			return Arrays.asList(
					new Attribute<String, String>("type", getType()),
					new Attribute<String, String>("prev_id", Integer.toString(prevId)));
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
	}


	public static class ItemRequest extends Request {
		private static final String XML_TYPE_NAME = "get_item";

		private int id;
		private MediaType mType;


		public ItemRequest() {
			this(-1);
		}

		public ItemRequest(int id) {
			this(id, MediaType.VID);
		}


		public ItemRequest(int id, MediaType type) {
			setId(id);
			setMediaType(type);
		}


		public static String getTypeName() {
			return XML_TYPE_NAME;
		}

		public String getType() {
			return getTypeName();
		}


		@SuppressWarnings("unchecked")
		public List<Attribute<String, String>> getAttributeList() {
			return Arrays.asList(
					new Attribute<String, String>("type", getType()),
					new Attribute<String, String>("id", Integer.toString(id)),
					new Attribute<String, String>("media", mType.toString()));
		}


		public void setId(int id) {
			if (id >= 0) {
				this.id = id;
			} else {
				this.id = -1;
			}
		}


		public int getId() {
			return id;
		}


		public void setMediaType(MediaType type) {
			mType = type;
		}


		public MediaType getMediaType() {
			return mType;
		}
	}


	public static class ItemDeletionRequest extends Request {
		private static final String XML_TYPE_NAME = "delete_item";

		private int id;


		public ItemDeletionRequest() {
			this(-1);
		}


		public ItemDeletionRequest(int id) {
			setId(id);
		}


		public static String getTypeName() {
			return XML_TYPE_NAME;
		}

		public String getType() {
			return getTypeName();
		}


		@SuppressWarnings("unchecked")
		public List<Attribute<String, String>> getAttributeList() {
			return Arrays.asList(
					new Attribute<String, String>("type", getType()),
					new Attribute<String, String>("id", Integer.toString(id)));
		}


		public void setId(int id) {
			if (id >= 0) {
				this.id = id;
			} else {
				this.id = -1;
			}
		}


		public int getId() {
			return id;
		}
	}


	public static class ItemPreservationRequest extends Request {
		private static final String XML_TYPE_NAME = "keep_item";

		private int id;


		public ItemPreservationRequest() {
			this(-1);
		}


		public ItemPreservationRequest(int id) {
			setId(id);
		}


		public static String getTypeName() {
			return XML_TYPE_NAME;
		}

		public String getType() {
			return getTypeName();
		}


		@SuppressWarnings("unchecked")
		public List<Attribute<String, String>> getAttributeList() {
			return Arrays.asList(
					new Attribute<String, String>("type", getType()),
					new Attribute<String, String>("id", Integer.toString(id)));
		}


		public void setId(int id) {
			if (id >= 0) {
				this.id = id;
			} else {
				this.id = -1;
			}
		}


		public int getId() {
			return id;
		}
	}


	public static class SnoozeRequest extends Request {
		private static final String XML_TYPE_NAME = "snooze";

		private int interval;


		public SnoozeRequest() {
			this(0);
		}


		public SnoozeRequest(int interval) {
			setInterval(interval);
		}


		public static String getTypeName() {
			return XML_TYPE_NAME;
		}

		public String getType() {
			return getTypeName();
		}


		@SuppressWarnings("unchecked")
		public List<Attribute<String, String>> getAttributeList() {
			return Arrays.asList(
					new Attribute<String, String>("type", getType()),
					new Attribute<String, String>("interval", Integer.toString(interval)));
		}


		public void setInterval(int interval) {
			if (interval > 0) {
				this.interval = interval;
			} else {
				this.interval = 0;
			}
		}


		public int getInterval() {
			return interval;
		}
	}


	public static class UnsnoozeRequest extends Request {
		private static final String XML_TYPE_NAME = "unsnooze";

		public UnsnoozeRequest() {
		}


		public static String getTypeName() {
			return XML_TYPE_NAME;
		}

		public String getType() {
			return getTypeName();
		}


		@SuppressWarnings("unchecked")
		public List<Attribute<String, String>> getAttributeList() {
			return Arrays.asList(
					new Attribute<String, String>("type", getType()));
		}
	}


	public static class ConfigRequest extends Request
			implements Iterable<String> {
		private static final String XML_TYPE_NAME = "config";

		private List<String> mParamList;

		public ConfigRequest() {
			mParamList = new ArrayList<String>();
		}


		public static String getTypeName() {
			return XML_TYPE_NAME;
		}

		public String getType() {
			return getTypeName();
		}

		public List<Attribute<String, String>> getAttributeList() {
			List<Attribute<String, String>> attrList = new ArrayList<Attribute<String, String>>();
			attrList.add(new Attribute<String, String>("type", getType()));
			for (String param: mParamList) {
				attrList.add(new Attribute<String, String>("param", param));
			}
			return attrList;
		}

		public List<String> getParamList() {
			return mParamList;
		}

		public boolean add(String param) {
			return mParamList.add(param);
		}

		public boolean addAll(Collection<String> c) {
			return mParamList.addAll(c);
		}

		public Iterator<String> iterator() {
			return mParamList.iterator();
		}
	}


	public ClientMessage() {
		this(VERSION);
	}


	public ClientMessage(Version version) {
		super(version);
	}


	/**
	 * Recommend using MessageFactory.parse()
	 */
	public ClientMessage(XmlParser xp) throws UnsupportedVersionException {
		super(xp);

		if (!mVersion.equals(new Version(1, 0))) {
			throw new UnsupportedVersionException("unsupported version " + mVersion);
		}

		sm = StateMachine.INIT;
	}


	public boolean add(Request e) {
		return reqList.add(e);
	}


	public String toString() {
		return toString(0);
	}


	public String toString(int indent) {
		StringBuffer sb = new StringBuffer(getXmlRoot() + " version:" + mVersion);
		for (Request req: reqList) {
			sb.append("\n" + Utils.repeat("  ", indent + 1) + req.toString(indent + 1));
		}

		return sb.toString();
	}


	public String getXmlString() {
		XmlStringWriter xsw = new XmlStringWriter(getXmlRootName(), getVersion());

		for (Request req: reqList) {
			xsw.writeTag("action", req.getAttributeList());
		}

		return xsw.getXmlString();
	}


	public void processXmlEvent(XmlParser.XmlEvent e) throws MalformedMessageException {
		switch (sm) {
		case INIT:
			processRequest(e);
			sm = StateMachine.ACTION;
			break;
		case ACTION:
			//Allow an empty pair of elements as well (ignore the end element)
			if (e != XmlParser.XmlEvent.END_ELEMENT) {
				processRequest(e);
			}
			break;
		default:
		}
	}


	public void processXmlRootEndTag() {
		//pass
	}


	private void processRequest(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.START_ELEMENT, "action");

		List<String> paramList = new ArrayList<String>();
		Request req = null;
		MediaType type = null;
		int id = -1;
		int interval = -1;
		int prevId = -1;
		int attrCount = xp.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String attrName = xp.getAttributeName(i).toString();
			String attrVal = xp.getAttributeValue(i);

			if (attrName.equals("type")) {
				if (attrVal.equals(ItemListRequest.getTypeName())) {
					req = new ItemListRequest();
				} else if (attrVal.equals(ItemRequest.getTypeName())) {
					req = new ItemRequest();
				} else if (attrVal.equals(ItemDeletionRequest.getTypeName())) {
					req = new ItemDeletionRequest();
				} else if (attrVal.equals(SnoozeRequest.getTypeName())) {
					req = new SnoozeRequest();
				} else if (attrVal.equals(UnsnoozeRequest.getTypeName())) {
					req = new UnsnoozeRequest();
				} else if (attrVal.equals(ItemPreservationRequest.getTypeName())) {
					req = new ItemPreservationRequest();
				} else if (attrVal.equals(ConfigRequest.getTypeName())) {
					req = new ConfigRequest();
				} else {
					throw new MalformedMessageException("bad action type");
				}
			} else if (attrName.equals("prev_id")) {
				int nb = new Integer(attrVal);
				if (nb >= 0) {
					prevId = nb;
				}
			} else if (attrName.equals("id")) {
				int nb = new Integer(attrVal);
				if (nb < 0) {
					throw new MalformedMessageException(attrName + " lower than 0 not allowed");
				}
				id = nb;
			} else if (attrName.equals("interval")) {
				int nb = new Integer(attrVal);
				if (nb >= 0) {
					interval = nb;
				}
			} else if (attrName.equals("media")) {
				if (attrVal.equals("VID")) {
					type = MediaType.VID;
				} else if (attrVal.equals("IMG")) {
					type = MediaType.IMG;
				}
			} else if (attrName.equals("param")) {
				paramList.add(attrVal);
			}
		}

		if (req == null) {
			throw new MalformedMessageException("no request type found");
		}

		if (req instanceof ItemListRequest) {
			((ItemListRequest) req).setPrevId(prevId);
		} else if (req instanceof ItemRequest) {
			ItemRequest ir = (ItemRequest) req;
			ir.setId(id);
			if (type != null) {
				ir.setMediaType(type);
			}
		} else if (req instanceof ItemDeletionRequest) {
			((ItemDeletionRequest) req).setId(id);
		} else if (req instanceof ItemPreservationRequest) {
			((ItemPreservationRequest) req).setId(id);
		} else if (req instanceof SnoozeRequest) {
			((SnoozeRequest) req).setInterval(interval);
		} else if (req instanceof UnsnoozeRequest) {
			//Pass
		} else if (req instanceof ConfigRequest) {
			((ConfigRequest) req).addAll(paramList);
		} else {
			throw new UnsupportedOperationException("unimplemented Request:" + req.getClass().getName());
		}

		reqList.add(req);
	}


	/* Iterable interface */
	public Iterator<Request> iterator() {
		return reqList.iterator();
	}
}
