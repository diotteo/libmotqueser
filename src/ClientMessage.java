package ca.dioo.java.MonitorLib;

import java.util.Iterator;
import java.util.ArrayList;

public class ClientMessage extends Message implements Iterable<ClientMessage.Action> {
	public static final int VERSION[] = {1, 0};
	private static final String XML_ROOT = "client_message";
	private ArrayList<Action> actionList = new ArrayList<Action>();
	private StateMachine sm;


	public static String getXmlRootName() {
		return XML_ROOT;
	}


	private enum StateMachine {
		INIT,
		ACTION,
		END
	}


	public static class BadActionTypeException extends MalformedMessageException {
		public BadActionTypeException(String msg) {
			super(msg);
		}
	}


	public static class Action {
		private ActionType at;
		private int id = -1;
		private int prevId = -1;
		private int minutes = -1;


		public enum ActionType {
			GET_MSG_LIST("get_message_list"),
			GET_MSG("get_message"),
			DEL_MSG("delete_message"),
			SNOOZE("snooze");

			private String name;


			ActionType(String name) {
				this.name = name;
			}


			public String toString() {
				return name;
			}
		}


		public Action(ActionType at) {
			this.at = at;
		}


		public ActionType getActionType() {
			return at;
		}


		public String[][] getAttributeList() {
			switch (at) {
			case GET_MSG_LIST:
				return new String[][]{{"type", at.toString()}, {"prev_id", Integer.toString(prevId)}};
			case GET_MSG:
			case DEL_MSG:
				return new String[][]{{"type", at.toString()}, {"id", Integer.toString(id)}};
			case SNOOZE:
				return new String[][]{{"type", at.toString()}, {"minutes", Integer.toString(minutes)}};
			default:
				throw new Error("bogus ActionType");
			}
		}


		public String toString() {
			switch (at) {
			case GET_MSG_LIST:
			case GET_MSG:
			case DEL_MSG:
			case SNOOZE:
				return Utils.join(" ", ":", getAttributeList());
			default:
				throw new Error("bogus ActionType");
			}
		}


		int getMinutes() {
			return minutes;
		}


		int getPrevId() {
			return prevId;
		}


		int getId() {
			return id;
		}


		void setMinutes(int min) throws BadActionTypeException {
			if (at != ActionType.SNOOZE) {
				throw new BadActionTypeException("only snooze has minutes");
			} else if (min < 1) {
				throw new Error("can't snooze for less than 1 minute");
			}
			minutes = min;
		}


		void setPrevId(int prevId) throws BadActionTypeException {
			if (at != ActionType.GET_MSG_LIST) {
				throw new BadActionTypeException("only get_message_list has prev_id");
			}

			this.prevId = prevId;
		}


		void setId(int id) throws BadActionTypeException {
			if (at != ActionType.GET_MSG && at != ActionType.DEL_MSG) {
				throw new BadActionTypeException("only get_message and delete_message have id");
			}

			this.id = id;
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


	public boolean add(Action e) {
		return actionList.add(e);
	}


	public String toString() {
		return toString(0);
	}


	public String toString(int indent) {
		StringBuffer sb = new StringBuffer("version " + version[0] + "." + version[1]);
		for (Action a: actionList) {
			sb.append("\n" + Utils.repeat("  ", indent + 1) + "action " + a.toString());
		}

		return sb.toString();
	}


	public String getXmlString() {
		XmlStringWriter xsw = new XmlStringWriter("client_message", getVersion());

		for (Action a: actionList) {
			xsw.writeTag("action", a.getAttributeList());
		}

		return xsw.getXmlString();
	}


	public void processXmlEvent(XmlParser.XmlEvent e) throws MalformedMessageException {
		switch (sm) {
		case INIT:
			processAction(e);
			sm = StateMachine.ACTION;
			break;
		case ACTION:
			//Allow an empty pair of elements as well (ignore the end element)
			if (e != XmlParser.XmlEvent.END_ELEMENT) {
				processAction(e);
			}
			break;
		default:
		}
	}


	public void processXmlRootEndTag() {
		//pass
	}


	private void processAction(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.START_ELEMENT, "action");

		Action.ActionType at = null;
		int id = -1;
		int minutes = -1;
		int prevId = -1;
		int attrCount = xp.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String attrName = xp.getAttributeName(i).toString();
			String attrVal = xp.getAttributeValue(i);

			if (attrName.equals("type")) {
				if (attrVal.equals("get_message_list")) {
					at = Action.ActionType.GET_MSG_LIST;
				} else if (attrVal.equals("get_message")) {
					at = Action.ActionType.GET_MSG;
				} else if (attrVal.equals("delete_message")) {
					at = Action.ActionType.DEL_MSG;
				} else if (attrVal.equals("snooze")) {
					at = Action.ActionType.SNOOZE;
				} else {
					throw new MalformedMessageException("bad action type");
				}
			} else if (attrName.equals("prev_id")) {
				int nb = new Integer(attrVal);
				if (nb >= 0) {
					prevId = nb;
					//throw new Error(attrName + " lower than 0 not allowed");
				}
			} else if (attrName.equals("id")) {
				int nb = new Integer(attrVal);
				if (nb < 0) {
					throw new Error(attrName + " lower than 0 not allowed");
				}
				id = nb;
			} else if (attrName.equals("minutes")) {
				int nb = new Integer(attrVal);
				if (nb >= 0) {
					minutes = nb;
					//throw new Error(attrName + " lower than 0 not allowed");
				}
			}
		}

		Action a;
		switch (at) {
		case GET_MSG_LIST:
			a = new Action(at);
			a.setPrevId(prevId);
			break;
		case GET_MSG:
		case DEL_MSG:
			a = new Action(at);
			a.setId(id);
			break;
		case SNOOZE:
			a = new Action(at);
			a.setMinutes(minutes);
			break;
		default:
			throw new Error("Invalid action");
		}

		actionList.add(a);
	}


	/* Iterable interface */
	public Iterator<Action> iterator() {
		return actionList.iterator();
	}
}
