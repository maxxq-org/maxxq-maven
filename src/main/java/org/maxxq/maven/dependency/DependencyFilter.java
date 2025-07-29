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

    public void setKeepOptional( boolean keepOptional ) {
        this.keepOptional = keepOptional;
    }

    public boolean isKeepTest() {
        return keepTest;
    }

    public void setKeepTest( boolean keepTest ) {
        this.keepTest = keepTest;
    }

    public boolean isKeepCompile() {
        return keepCompile;
    }

    public void setKeepCompile( boolean keepCompile ) {
        this.keepCompile = keepCompile;
    }

    public boolean isKeepRuntime() {
        return keepRuntime;
    }

    public void setKeepRuntime( boolean keepRuntime ) {
        this.keepRuntime = keepRuntime;
    }

    public boolean isKeepProvided() {
        return keepProvided;
    }

    public void setKeepProvided( boolean keepProvided ) {
        this.keepProvided = keepProvided;
    }

    @Override
    public boolean keepDependency( Dependency dependency, int depth ) {
        if ( StringUtils.isEmpty( dependency.getScope() ) ) {
            return true;
        }
        if ( keepOptional && dependency.isOptional() ) {
            return true;
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
