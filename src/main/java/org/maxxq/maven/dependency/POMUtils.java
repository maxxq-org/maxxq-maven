package org.maxxq.maven.dependency;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Model;

public class POMUtils implements IPOMUtils {
    private static final Pattern VERSION_PATTERN = Pattern.compile( "(\\$\\{([^}]+)\\})" );

    @Override
    public boolean isPropertyValue( String value ) {
        if ( StringUtils.isEmpty( value ) ) {
            return false;
        }
        return VERSION_PATTERN.matcher( value ).find();
    }

    @Override
    public String resolveProperty( String propertyValue, Model model ) {
        Matcher matcher = VERSION_PATTERN.matcher( propertyValue );
        while ( matcher.find() ) {
            String property = matcher.group( 1 );
            String propertyKey = matcher.group( 2 );
            if ( propertyKey.equals( "project.version" ) || propertyKey.equals( "pom.version" ) ) {
                propertyValue = propertyValue.replace( property, GAV.fromModel( model ).getVersion() );
            }
            if ( propertyKey.equals( "project.groupId" ) || propertyKey.equals( "pom.groupId" ) ) {
                propertyValue = propertyValue.replace( property, GAV.fromModel( model ).getGroupId() );
            }
            if ( propertyKey.equals( "project.artifactId" ) || propertyKey.equals( "pom.artifactId" ) ) {
                propertyValue = propertyValue.replace( property, GAV.fromModel( model ).getArtifactId() );
            }
            if ( model.getParent() != null && ( propertyKey.equals( "project.parent.version" ) || propertyKey.equals( "pom.parent.version" ) ) ) {
                propertyValue = propertyValue.replace( property, GAV.fromParent( model.getParent() ).getVersion() );
            }
            if ( model.getParent() != null && ( propertyKey.equals( "project.parent.groupId" ) || propertyKey.equals( "pom.parent.groupId" ) ) ) {
                propertyValue = propertyValue.replace( property, GAV.fromParent( model.getParent() ).getGroupId() );
            }
            if ( model.getParent() != null && ( propertyKey.equals( "project.parent.artifactId" ) || propertyKey.equals( "pom.parent.artifactId" ) ) ) {
                propertyValue = propertyValue.replace( property, GAV.fromParent( model.getParent() ).getArtifactId() );
            }
            if ( model.getProperties() != null && model.getProperties().containsKey( propertyKey ) ) {
                propertyValue = propertyValue.replace( property, model.getProperties().getProperty( propertyKey ) );
            }
        }
        return propertyValue;
    }

}
