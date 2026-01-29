package org.maxxq.maven.dependency;

import java.util.Arrays;
import java.util.Optional;

import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.maxxq.maven.repository.IRepository;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ResolveRangeTest {
    private ResolveRange resolveRange;

    @Mock
    private IRepository  repository;

    @Mock
    private GAV          gav;

    @Mock
    private Metadata     metaData;

    @Mock
    private Versioning   versioning;

    @BeforeEach
    void setUp() {
        resolveRange = new ResolveRange( repository );
    }

    @Test
    void apply() {
        Mockito.when( gav.getGroupId() ).thenReturn( "groupid" );
        Mockito.when( gav.getArtifactId() ).thenReturn( "artifactid" );
        Mockito.when( gav.getVersion() ).thenReturn( "[1.0.0,1.0.1]" );
        Mockito.when( repository.getMetaData( "groupid", "artifactid" ) ).thenReturn( Optional.of( metaData ) );
        Mockito.when( metaData.getVersioning() ).thenReturn( versioning );
        Mockito.when( versioning.getVersions() ).thenReturn( Arrays.asList( "1.0.0", "1.0.1", "1.0.2" ) );

        Optional<String> result = resolveRange.apply( gav );

        assertTrue( result.isPresent() );
        assertEquals( "1.0.1", result.get() );
    }

    @Test
    void applyNoRange() {
        Mockito.when( gav.getVersion() ).thenReturn( "1.0.1" );

        Optional<String> result = resolveRange.apply( gav );

        assertTrue( result.isPresent() );
        assertEquals( "1.0.1", result.get() );
    }

    @Test
    void applyInvalidRange() {
        Mockito.when( gav.getVersion() ).thenReturn( "[1.0.1," );
        assertThrows( IllegalArgumentException.class, () ->

            resolveRange.apply( gav ) );
    }

    @Test
    void applySingleVersionRange() {
        Mockito.when( gav.getGroupId() ).thenReturn( "groupid" );
        Mockito.when( gav.getArtifactId() ).thenReturn( "artifactid" );
        Mockito.when( gav.getVersion() ).thenReturn( "[1.0.0]" );
        Mockito.when( repository.getMetaData( "groupid", "artifactid" ) ).thenReturn( Optional.of( metaData ) );
        Mockito.when( metaData.getVersioning() ).thenReturn( versioning );
        Mockito.when( versioning.getVersions() ).thenReturn( Arrays.asList( "1.0.0", "1.0.1", "1.0.2" ) );

        Optional<String> result = resolveRange.apply( gav );

        assertTrue( result.isPresent() );
        assertEquals( "1.0.0", result.get() );
    }

    @Test
    void applyOpenEndedRange() {
        Mockito.when( gav.getGroupId() ).thenReturn( "groupid" );
        Mockito.when( gav.getArtifactId() ).thenReturn( "artifactid" );
        Mockito.when( gav.getVersion() ).thenReturn( "[1.0.0,)" );
        Mockito.when( repository.getMetaData( "groupid", "artifactid" ) ).thenReturn( Optional.of( metaData ) );
        Mockito.when( metaData.getVersioning() ).thenReturn( versioning );
        Mockito.when( versioning.getVersions() ).thenReturn( Arrays.asList( "1.0.0", "1.0.1", "1.0.2" ) );

        Optional<String> result = resolveRange.apply( gav );

        assertTrue( result.isPresent() );
        assertEquals( "1.0.2", result.get() );
    }

}
