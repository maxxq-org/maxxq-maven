package org.maxxq.maven.dependency;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;

public class DependencyFilter implements IDependencyFilter {
    private boolean keepOptional     = false;
    private boolean keepTest         = false;
    private boolean keepTestRootOnly = true;
    private boolean keepCompile      = true;
    private boolean keepRuntime      = true;
    private boolean keepProvided     = false;

    public boolean isKeepOptional() {
        return keepOptional;
    }

    public DependencyFilter setKeepOptional( boolean keepOptional ) {
        this.keepOptional = keepOptional;
        return this;
    }

    public boolean isKeepTest() {
        return keepTest;
    }

    public DependencyFilter setKeepTest( boolean keepTest ) {
        this.keepTest = keepTest;
        return this;
    }

    public boolean isKeepCompile() {
        return keepCompile;
    }

    public DependencyFilter setKeepCompile( boolean keepCompile ) {
        this.keepCompile = keepCompile;
        return this;
    }

    public boolean isKeepRuntime() {
        return keepRuntime;
    }

    public DependencyFilter setKeepRuntime( boolean keepRuntime ) {
        this.keepRuntime = keepRuntime;
        return this;
    }

    public boolean isKeepProvided() {
        return keepProvided;
    }

    public DependencyFilter setKeepProvided( boolean keepProvided ) {
        this.keepProvided = keepProvided;
        return this;
    }

    public DependencyFilter setKeepTestRootOnly( boolean keepTestRootOnly ) {
        this.keepTestRootOnly = keepTestRootOnly;
        return this;
    }

    @Override
    public boolean keepDependency( Dependency dependency, int depth ) {
        if ( StringUtils.isEmpty( dependency.getScope() ) ) {
            return true;
        }
        if ( !keepOptional && dependency.isOptional() ) {
            return false;
        }
        if ( keepCompile && "compile".equals( dependency.getScope() ) ) {
            return true;
        }
        if ( keepTest && "test".equals( dependency.getScope() ) ) {
            return true;
        }
        if ( keepTestRootOnly && "test".equals( dependency.getScope() ) && isRootPOM( depth ) ) {
            return true;
        }
        if ( keepRuntime && "runtime".equals( dependency.getScope() ) ) {
            return true;
        }
        if ( keepProvided && "provided".equals( dependency.getScope() ) ) {
            return true;
        }
        return false;
    }

    private boolean isRootPOM( int depth ) {
        return depth == 0;
    }

}
