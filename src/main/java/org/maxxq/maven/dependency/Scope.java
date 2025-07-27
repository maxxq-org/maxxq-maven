package org.maxxq.maven.dependency;

import java.util.Arrays;

public enum Scope {
        COMPILE( "compile" ),
        TEST( "test" ),
        RUNTIME( "runtime" ),
        PROVIDED( "provided" ),
        SYSTEM( "system" ),
        IMPORT( "import" );

    private final String scope;

    private Scope( String scope ) {
        this.scope = scope;
    }

    public String getScope() {
        return scope;
    }

    public static Scope fromScope( String scopeString ) {
        return Arrays.stream( Scope.values() )
            .filter( scope -> scope.getScope().equalsIgnoreCase( scopeString ) )
            .findFirst()
            .orElse( COMPILE );
    }

    public Scope transitiveScope( Scope scope ) {
        if ( this == Scope.TEST || scope == Scope.TEST ) {
            return Scope.TEST;
        }

        switch ( this ) {
            case TEST :
            case RUNTIME :
            case PROVIDED :
                return this;
        }

        switch ( scope ) {
            case TEST :
            case RUNTIME :
            case PROVIDED :
                return scope;
        }

        return this;
    }
}
