package org.javaa.maven.dependency;

import org.apache.maven.model.Model;

public interface IPOMUtils {

    public String resolveProperty( String propertyValue, Model properties );

    public boolean isPropertyValue( String value );

}
