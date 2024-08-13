package org.maxxq.maven.dependency;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GAVTest {
    @Mock
    private Model  model;

    @Mock
    private Parent parent;

    @Test
    void GAVAllNull() {
        assertThrows( IllegalArgumentException.class, () -> {
            new GAV( null, null, null );
        } );
    }

    @Test
    void GAVAllEmpty() {
        assertThrows( IllegalArgumentException.class, () -> {
            new GAV( null, null, null );
        } );
    }

    @Test
    void GAVGroupIdEmpty() {
        assertThrows( IllegalArgumentException.class, () -> {
            new GAV( null, "artifactid", "version" );
        } );
    }

    @Test
    void GAVArtifactIdEmpty() {
        assertThrows( IllegalArgumentException.class, () -> {
            new GAV( "groupId", null, "version" );
        } );
    }

    @Test
    void GAVVersionEmpty() {
        assertThrows( IllegalArgumentException.class, () -> {
            new GAV( "groupId", "artifactid", null );
        } );
    }

    @Test
    void GAV() {
        GAV result = new GAV( "groupId", "artifactid", "version" );

        assertEquals( "artifactid", result.getArtifactId() );
        assertEquals( "groupId", result.getGroupId() );
        assertEquals( "version", result.getVersion() );
    }

    @Test
    void GAVFromModel() {
        Mockito.when( model.getArtifactId() ).thenReturn( "artifactid" );
        Mockito.when( model.getGroupId() ).thenReturn( "groupId" );
        Mockito.when( model.getVersion() ).thenReturn( "version" );

        GAV result = GAV.fromModel( model );

        assertEquals( "artifactid", result.getArtifactId() );
        assertEquals( "groupId", result.getGroupId() );
        assertEquals( "version", result.getVersion() );
    }

    @Test
    void GAVFromParent() {
        Mockito.when( parent.getArtifactId() ).thenReturn( "artifactid" );
        Mockito.when( parent.getGroupId() ).thenReturn( "groupId" );
        Mockito.when( parent.getVersion() ).thenReturn( "version" );

        GAV result = GAV.fromParent( parent );

        assertEquals( "artifactid", result.getArtifactId() );
        assertEquals( "groupId", result.getGroupId() );
        assertEquals( "version", result.getVersion() );
    }

    @Test
    void GAVFromModelWithParent() {
        Mockito.when( model.getArtifactId() ).thenReturn( "artifactid" );
        Mockito.when( model.getParent() ).thenReturn( parent );
        Mockito.when( parent.getGroupId() ).thenReturn( "groupId" );
        Mockito.when( parent.getVersion() ).thenReturn( "version" );

        GAV result = GAV.fromModel( model );

        assertEquals( "artifactid", result.getArtifactId() );
        assertEquals( "groupId", result.getGroupId() );
        assertEquals( "version", result.getVersion() );
    }

    @Test
    void equals() {
        assertEquals( new GAV( "groupid", "artifactid", "version" ), new GAV( "groupid", "artifactid", "version" ) );
        assertNotEquals( new GAV( "groupid", "artifactid", "version" ), new GAV( "other", "artifactid", "version" ) );
        assertNotEquals( new GAV( "groupid", "artifactid", "version" ), new GAV( "groupid", "other", "version" ) );
        assertNotEquals( new GAV( "groupid", "artifactid", "version" ), new GAV( "groupid", "artifactid", "other" ) );
    }

    @Test
    void gavToString() {
        assertEquals( "GAV [groupId=groupid, artifactId=artifactid, version=version]", new GAV( "groupid", "artifactid", "version" ).toString() );
    }

}
