package ca.dioo.java.libmotqueser;

import ca.dioo.java.commons.Utils;

public class ErrorMessage extends Message {
	public static final int VERSION[] = {1, 0};
	private static final String XML_ROOT = "error_message";

	private StateMachine sm;
	private String errorMsg;


	public static String getXmlRootName() {
		return XML_ROOT;
	}


	public String getXmlRoot() {
		return XML_ROOT;
	}


	private enum StateMachine {
		INIT,
		ERR_MSG,
		END
	}


	public ErrorMessage() {
		this(VERSION, null);
	}


	public ErrorMessage(String errMsg) {
		this(VERSION, errMsg);
	}


	public ErrorMessage(int[] version) {
		this(version, null);
	}


	public ErrorMessage(int[] version, String errMsg) {
		super(version);
		errorMsg = errMsg;
	}


	/**
	 * Recommend using MessageFactory.parse()
	 */
	public ErrorMessage(XmlParser xp) {
		super(xp);

		if (version[0] != 1 || version[1] != 0) {
			throw new Error("unsupported version " + version[0] + "." + version[1]);
		}

		sm = StateMachine.INIT;
	}


	public void setErrorMessage(String errMsg) {
		errorMsg = errMsg;
	}


	public String getErrorMessage() {
		return errorMsg;
	}


	public String toString() {
		return ErrorMessage.class.getSimpleName()
				+ "version " + version[0] + "." + version[1] + " text:" + errorMsg;
	}


	public String getXmlString() {
		XmlStringWriter xsw = new XmlStringWriter(getXmlRootName(), getVersion());

		xsw.writeText(errorMsg);

		return xsw.getXmlString();
	}


	public void processXmlEvent(XmlParser.XmlEvent e) throws MalformedMessageException {
		switch (sm) {
		case INIT:
			processErrorMessage(e);
			sm = StateMachine.ERR_MSG;
			break;
		case ERR_MSG:
			sm = StateMachine.END;
			break;
		}
	}


	public void processXmlRootEndTag() {
		//pass
	}


	private void processErrorMessage(XmlParser.XmlEvent e) throws MalformedMessageException {
		validateElement(e, XmlParser.XmlEvent.CHARACTERS, null);
		errorMsg = xp.getText();
	}
}
