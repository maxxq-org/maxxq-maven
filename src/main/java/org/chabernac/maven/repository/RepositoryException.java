package org.chabernac.maven.repository;

public class RepositoryException extends RuntimeException {
    private static final long serialVersionUID = -4749059919857122301L;

    public RepositoryException( String message, Throwable cause ) {
        super( message, cause );
    }

    public RepositoryException( String message ) {
        super( message );
    }

}
