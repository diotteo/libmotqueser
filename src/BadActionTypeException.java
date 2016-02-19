package ca.dioo.java.MonitorLib;

public class BadActionTypeException extends MalformedMessageException {
	public BadActionTypeException(String msg) {
		super(msg);
	}


	public BadActionTypeException(String msg, Throwable cause) {
		super(msg, cause);
	}
}


