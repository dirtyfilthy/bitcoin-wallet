package net.dirtyfilthy.bitcoin.wallet;

public class InvalidPasswordException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5852291715439914811L;

	public InvalidPasswordException() {
		// TODO Auto-generated constructor stub
	}

	public InvalidPasswordException(String detailMessage) {
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	public InvalidPasswordException(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}

	public InvalidPasswordException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}

}
