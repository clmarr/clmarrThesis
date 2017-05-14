package clmarrThesis;

class IndexOutOfWordBoundsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IndexOutOfWordBoundsException() {
		super("IndexOutOfWordBoundsException: Note that phone indexes must be in [1,(word size)]");
	}

	public IndexOutOfWordBoundsException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public IndexOutOfWordBoundsException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public IndexOutOfWordBoundsException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public IndexOutOfWordBoundsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
