package net.dirtyfilthy.bitcoin.core;

public class InvalidBlockException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8091857085395135887L;

	public InvalidBlockException() {
		// TODO Auto-generated constructor stub
	}

	public InvalidBlockException(String detailMessage) {
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	public InvalidBlockException(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}

	public InvalidBlockException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}

}
