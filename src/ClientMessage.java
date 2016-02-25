package ca.dioo.java.MonitorLib;

import java.util.Iterator;
import java.util.ArrayList;

import ca.dioo.java.commons.Utils;

public class ClientMessage extends Message implements Iterable<ClientMessage.Request> {
	public static final int VERSION[] = {1, 0};
	private static final String XML_ROOT = "client_message";
	private ArrayList<Request> reqList = new ArrayList<Request>();
	private StateMachine sm;


	public static String getXmlRootName() {
		return XML_ROOT;
	}


	private enum StateMachine {
		INIT,
		ACTION,
		END
	}


	abstract public static class Request {
		abstract public String[][] getAttributeList();

		abstract public String getType();

		public String toString() {
			return Utils.join(" ", ":", getAttributeList());
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


		public String[][] getAttributeList() {
			return new String[][]{{"type", getType()},
					{"prev_id", Integer.toString(prevId)}};
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


		public ItemRequest() {
			this(-1);
		}


		public ItemRequest(int id) {
			setId(id);
		}


		public static String getTypeName() {
			return XML_TYPE_NAME;
		}

		public String getType() {
			return getTypeName();
		}


		public String[][] getAttributeList() {
			return new String[][]{{"type", getType()},
					{"id", Integer.toString(id)}};
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


		public String[][] getAttributeList() {
			return new String[][]{{"type", getType()},
					{"id", Integer.toString(id)}};
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

		private long interval;


		public SnoozeRequest() {
			this(0);
		}


		public SnoozeRequest(long interval) {
			setInterval(interval);
		}


		public static String getTypeName() {
			return XML_TYPE_NAME;
		}

		public String getType() {
			return getTypeName();
		}


		public String[][] getAttributeList() {
			return new String[][]{{"type", getType()},
					{"interval", Long.toString(interval)}};
		}


		public void setInterval(long interval) {
			if (interval > 0) {
				this.interval = interval;
			} else {
				this.interval = 0;
			}
		}


		public long getInterval() {
			return interval;
		}
	}


	public ClientMessage() {
		this(VERSION);
	}


	public ClientMessage(int[] version) {
		super(version);
	}


	/**
	 * Recommend using MessageFactory.parse()
	 */
	public ClientMessage(XmlParser xp) {
		super(xp);

		if (version[0] != 1 || version[1] != 0) {
			throw new Error("unsupported version " + version[0] + "." + version[1]);
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
		StringBuffer sb = new StringBuffer("version " + version[0] + "." + version[1]);
		for (Request a: reqList) {
			sb.append("\n" + Utils.repeat("  ", indent + 1) + "action " + a.toString());
		}

		return sb.toString();
	}


	public String getXmlString() {
		XmlStringWriter xsw = new XmlStringWriter("client_message", getVersion());

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

		Request req = null;
		int id = -1;
		long interval = -1;
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
					throw new Error(attrName + " lower than 0 not allowed");
				}
				id = nb;
			} else if (attrName.equals("interval")) {
				int nb = new Integer(attrVal);
				if (nb >= 0) {
					interval = nb;
				}
			}
		}

		if (req == null) {
			throw new Error("no request type found");
		}

		if (req instanceof ItemListRequest) {
			((ItemListRequest)req).setPrevId(prevId);
		} else if (req instanceof ItemRequest) {
			((ItemRequest)req).setId(id);
		} else if (req instanceof ItemDeletionRequest) {
			((ItemDeletionRequest)req).setId(id);
		} else if (req instanceof SnoozeRequest) {
			((SnoozeRequest)req).setInterval(interval);
		} else {
			throw new Error("unimplemented Request");
		}

		reqList.add(req);
	}


	/* Iterable interface */
	public Iterator<Request> iterator() {
		return reqList.iterator();
	}
}
