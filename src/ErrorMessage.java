package ca.dioo.java.libmotqueser;

import ca.dioo.java.commons.Utils;

public class ErrorMessage extends Message {
	public static final Version VERSION = new Version(1, 0);
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


	public ErrorMessage(Version version) {
		this(version, null);
	}


	public ErrorMessage(Version version, String errMsg) {
		super(version);
		errorMsg = errMsg;
	}


	/**
	 * Recommend using MessageFactory.parse()
	 */
	public ErrorMessage(XmlParser xp) throws UnsupportedVersionException {
		super(xp);

		if (!mVersion.equals(new Version(1, 0))) {
			throw new UnsupportedVersionException("unsupported version " + mVersion);
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
		return toString(0);
	}


	public String toString(int indent) {
		return getXmlRoot() + " version:" + mVersion + " text:" + errorMsg;
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
