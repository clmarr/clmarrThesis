package clmarrThesis;

public class UnsupportedCharacterError extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; // not totally sure what
														// this is for, to be
														// honest...

	public UnsupportedCharacterError() {
		super("Unsupported Character Error! Please make sure your encoding is correct. ");
	}

	public UnsupportedCharacterError(String arg0) {
		super("Unsupported Character Error: " + arg0 + "! Please make sure your encoding is correct.");
		// TODO Auto-generated constructor stub
	}

	public UnsupportedCharacterError(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public UnsupportedCharacterError(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public UnsupportedCharacterError(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

}
