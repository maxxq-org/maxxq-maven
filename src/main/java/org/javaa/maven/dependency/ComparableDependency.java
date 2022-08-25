package org.javaa.maven.dependency;

import org.apache.maven.model.Dependency;

public class ComparableDependency {

    private final Dependency dependency;
    private final String     gav;

    public ComparableDependency( Dependency dependency ) {
        super();
        this.dependency = dependency;
        this.gav = dependency.getGroupId() + "." + dependency.getArtifactId() + "." + dependency.getVersion();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( gav == null ) ? 0 : gav.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        ComparableDependency other = (ComparableDependency) obj;
        if ( gav == null ) {
            if ( other.gav != null ) return false;
        } else if ( !gav.equals( other.gav ) ) return false;
        return true;
    }

    public Dependency getDependency() {
        return dependency;
    }

    public String getGav() {
        return gav;
    }

}
