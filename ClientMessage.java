package ca.dioo.java.MonitorLib;

import java.util.Vector;
import javax.xml.stream.XMLStreamReader;

class Action {
	public enum ActionType {
		GET_MSG_LIST,
		GET_MSG,
		DEL_MSG,
		SNOOZE
	}

	private ActionType at;
	private int id;
	private int prevId;
	private int minutes;

	Action(ActionType at) {
		this.at = at;
	}


	void setMinutes(int min) throws BadActionTypeError, Error {
		if (at != ActionType.SNOOZE) {
			throw new BadActionTypeError("only snooze has minutes");
		} else if (min < 1) {
			throw new Error("can't snooze for less than 1 minute");
		}
		minutes = min;
	}


	void setPrevId(int prevId) throws BadActionTypeError {
		if (at != ActionType.GET_MSG_LIST) {
			throw new BadActionTypeError("only get_message_list has prev_id");
		}

		this.prevId = prevId;
	}


	void setId(int id) throws BadActionTypeError {
		if (at != ActionType.GET_MSG && at != ActionType.DEL_MSG) {
			throw new BadActionTypeError("only get_message and delete_message have id");
		}

		this.id = id;
	}
}


class ClientMessage extends Message {
	private Vector<Action> actionList;

	private enum StateMachine {
		INIT,
		ACTION,
		END
	}
	private XmlEvent expected;
	private StateMachine sm;

	ClientMessage(XMLStreamReader xsr) {
		super(xsr);

		if (version[0] != 1 || version[1] != 0) {
			System.err.println("unsupported version " + version[0] + "." + version[1]);
		}

		sm = StateMachine.INIT;
		actionList = new Vector<Action>();
	}


	void processXmlEvent(XmlEvent e) throws MalformedMessageError, Error {
		switch (sm) {
		case INIT:
			processAction(e);
			sm = StateMachine.ACTION;
			break;
		case ACTION:
			processAction(e);
			break;
		default:
		}
	}

	private void processAction(XmlEvent e) throws MalformedMessageError, Error {
		validateElement(e, XmlEvent.START_ELEMENT, "action");

		Action.ActionType at = null;
		int id = -1;
		int minutes = -1;
		int prevId = -1;
		int attrCount = xsr.getAttributeCount();
		for (int i = 0; i < attrCount; i++) {
			String attrName = xsr.getAttributeName(i).toString();
			String attrVal = xsr.getAttributeValue(i);

			switch (attrName) {
			case "type":
				switch (attrVal) {
				case "get_message_list":
					at = Action.ActionType.GET_MSG_LIST;
					break;
				case "get_message":
					at = Action.ActionType.GET_MSG;
					break;
				case "delete_message":
					at = Action.ActionType.DEL_MSG;
					break;
				case "snooze":
					at = Action.ActionType.SNOOZE;
					break;
				default:
					throw new MalformedMessageError("bad action type");
				}
				break;
			case "prev_id":
				{
					int nb = new Integer(attrVal);
					if (nb < 0) {
						throw new Error(attrName + " lower than 0 not allowed");
					}
					prevId = nb;
				}
				break;
			case "id":
				{
					int nb = new Integer(attrVal);
					if (nb < 0) {
						throw new Error(attrName + " lower than 0 not allowed");
					}
					id = nb;
				}
				break;
			case "minutes":
				{
					int nb = new Integer(attrVal);
					if (nb < 0) {
						throw new Error(attrName + " lower than 0 not allowed");
					}
					minutes = nb;
				}
				break;
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
	}
}
