package org.chabernac.dependency;

import java.util.Properties;
import org.apache.maven.model.Model;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith( MockitoJUnitRunner.class )
public class POMUtilsTest {
    private POMUtils pomUtils = new POMUtils();

    @Mock
    private GAV      dependency;

    @Mock
    private Model    model;

    @Test
    public void isPropertyValue() {
        Assert.assertTrue( pomUtils.isPropertyValue( "${library.version}" ) );
        Assert.assertFalse( pomUtils.isPropertyValue( "1.2.3" ) );
    }

    @Test
    public void resolveProperties() {
        Properties properties = new Properties();
        properties.put( "library.version", "1.2.3" );
        Mockito.when( model.getProperties() ).thenReturn( properties );
        Assert.assertEquals( "1.2.3", pomUtils.resolveProperty( "${library.version}", model ) );
        Assert.assertEquals( "1.2.3", pomUtils.resolveProperty( "1.2.3", model ) );
    }

    @Test
    public void resolvePropertyNotFound() {
        Properties properties = new Properties();
        Mockito.when( model.getProperties() ).thenReturn( properties );
        Assert.assertEquals( "${library.version}", pomUtils.resolveProperty( "${library.version}", model ) );
    }

    @Test
    public void resolveProjectVersion() {
        Mockito.when( model.getVersion() ).thenReturn( "1.2.3" );
        Mockito.when( model.getGroupId() ).thenReturn( "groupId" );
        Mockito.when( model.getArtifactId() ).thenReturn( "artefactId" );
        Assert.assertEquals( "1.2.3", pomUtils.resolveProperty( "${project.version}", model ) );
    }
}
