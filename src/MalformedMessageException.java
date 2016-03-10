package ca.dioo.java.libmotqueser;

public class MalformedMessageException extends Exception {
	public MalformedMessageException(String msg) {
		super(msg);
	}


	public MalformedMessageException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
