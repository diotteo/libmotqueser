package ca.dioo.java.MonitorLib;

public class MalformedMessageException extends Exception {
	public MalformedMessageException(String msg) {
		super(msg);
	}


	public MalformedMessageException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
