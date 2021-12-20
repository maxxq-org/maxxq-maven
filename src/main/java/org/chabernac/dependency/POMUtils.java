package org.chabernac.dependency;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.model.Model;

public class POMUtils implements IPOMUtils {
    private static final Pattern VERSION_PATTERN = Pattern.compile( "\\$\\{(.*)\\}" );

    @Override
    public boolean isPropertyValue( String value ) {
        return VERSION_PATTERN.matcher( value ).matches();
    }

    @Override
    public String resolveProperty( String propertyValue, Model model ) {
        Matcher matcher = VERSION_PATTERN.matcher( propertyValue );
        if ( matcher.matches() ) {
            String property = matcher.group( 1 );
            if ( property.equals( "project.version" ) ) {
                return GAV.fromModel( model ).getVersion();
            }
            if ( property.equals( "project.groupId" ) ) {
                return GAV.fromModel( model ).getGroupId();
            }
            if ( property.equals( "project.artifactId" ) ) {
                return GAV.fromModel( model ).getArtifactId();
            }
            if ( model.getProperties().containsKey( property ) ) {
                return model.getProperties().getProperty( property );
            }
        }
        return propertyValue;
    }

}
