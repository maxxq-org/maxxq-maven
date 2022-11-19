package org.maxxq.maven.dependency;

public class DepencyResolvingException extends RuntimeException {

    private static final long serialVersionUID = 2715766201473664799L;

    public DepencyResolvingException( String message, Throwable cause ) {
        super( message, cause );
    }

    public DepencyResolvingException( String message ) {
        super( message );
    }

}
