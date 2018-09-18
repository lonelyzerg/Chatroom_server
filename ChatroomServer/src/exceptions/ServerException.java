package exceptions;

public class ServerException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServerException() {
        // TODO Auto-generated constructor stub
    }

    public ServerException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public ServerException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
}
