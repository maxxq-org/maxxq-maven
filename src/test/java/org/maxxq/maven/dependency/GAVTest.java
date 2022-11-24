package org.maxxq.maven.dependency;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith( MockitoJUnitRunner.class )
public class GAVTest {
    @Mock
    private Model  model;

    @Mock
    private Parent parent;

    @Test( expected = IllegalArgumentException.class )
    public void GAVAllNull() {
        new GAV( null, null, null );
    }

    @Test( expected = IllegalArgumentException.class )
    public void GAVAllEmpty() {
        new GAV( null, null, null );
    }

    @Test( expected = IllegalArgumentException.class )
    public void GAVGroupIdEmpty() {
        new GAV( null, "artifactid", "version" );
    }

    @Test( expected = IllegalArgumentException.class )
    public void GAVArtifactIdEmpty() {
        new GAV( "groupId", null, "version" );
    }

    @Test( expected = IllegalArgumentException.class )
    public void GAVVersionEmpty() {
        new GAV( "groupId", "artifactid", null );
    }

    @Test
    public void GAV() {
        GAV result = new GAV( "groupId", "artifactid", "version" );

        Assert.assertEquals( "artifactid", result.getArtifactId() );
        Assert.assertEquals( "groupId", result.getGroupId() );
        Assert.assertEquals( "version", result.getVersion() );
    }

    @Test
    public void GAVFromModel() {
        Mockito.when( model.getArtifactId() ).thenReturn( "artifactid" );
        Mockito.when( model.getGroupId() ).thenReturn( "groupId" );
        Mockito.when( model.getVersion() ).thenReturn( "version" );

        GAV result = GAV.fromModel( model );

        Assert.assertEquals( "artifactid", result.getArtifactId() );
        Assert.assertEquals( "groupId", result.getGroupId() );
        Assert.assertEquals( "version", result.getVersion() );
    }

    @Test
    public void GAVFromParent() {
        Mockito.when( parent.getArtifactId() ).thenReturn( "artifactid" );
        Mockito.when( parent.getGroupId() ).thenReturn( "groupId" );
        Mockito.when( parent.getVersion() ).thenReturn( "version" );

        GAV result = GAV.fromParent( parent );

        Assert.assertEquals( "artifactid", result.getArtifactId() );
        Assert.assertEquals( "groupId", result.getGroupId() );
        Assert.assertEquals( "version", result.getVersion() );
    }

    @Test
    public void GAVFromModelWithParent() {
        Mockito.when( model.getArtifactId() ).thenReturn( "artifactid" );
        Mockito.when( model.getParent() ).thenReturn( parent );
        Mockito.when( parent.getGroupId() ).thenReturn( "groupId" );
        Mockito.when( parent.getVersion() ).thenReturn( "version" );

        GAV result = GAV.fromModel( model );

        Assert.assertEquals( "artifactid", result.getArtifactId() );
        Assert.assertEquals( "groupId", result.getGroupId() );
        Assert.assertEquals( "version", result.getVersion() );
    }

    @Test
    public void equals() {
        Assert.assertTrue( new GAV( "groupid", "artifactid", "version" ).equals( new GAV( "groupid", "artifactid", "version" ) ) );
        Assert.assertFalse( new GAV( "groupid", "artifactid", "version" ).equals( new GAV( "other", "artifactid", "version" ) ) );
        Assert.assertFalse( new GAV( "groupid", "artifactid", "version" ).equals( new GAV( "groupid", "other", "version" ) ) );
        Assert.assertFalse( new GAV( "groupid", "artifactid", "version" ).equals( new GAV( "groupid", "artifactid", "other" ) ) );
    }

    @Test
    public void gavToString() {
        Assert.assertEquals( "GAV [groupId=groupid, artifactId=artifactid, version=version]", new GAV( "groupid", "artifactid", "version" ).toString() );
    }

}
