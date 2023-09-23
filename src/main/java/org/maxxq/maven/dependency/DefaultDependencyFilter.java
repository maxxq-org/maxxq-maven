package org.maxxq.maven.dependency;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;

public class DefaultDependencyFilter implements IDependencyFilter {

    @Override
    public boolean keepDependency( Dependency dependency, boolean isRootPOM ) {
        if ( StringUtils.isEmpty( dependency.getScope() ) ) {
            return true;
        }
        if ( "compile".equals( dependency.getScope() ) ) {
            return true;
        }
        if ( "test".equals( dependency.getScope() ) && isRootPOM ) {
            return true;
        }
        if ( "runtime".equals( dependency.getScope() ) ) {
            return true;
        }
        return false;
    }

}
