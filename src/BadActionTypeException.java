package ca.dioo.java.libmotqueser;

public class BadActionTypeException extends MalformedMessageException {
	public BadActionTypeException(String msg) {
		super(msg);
	}


	public BadActionTypeException(String msg, Throwable cause) {
		super(msg, cause);
	}
}


