package org.maxxq.maven.dependency;

import java.util.Arrays;
import java.util.Optional;

import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.maxxq.maven.repository.IRepository;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith( MockitoJUnitRunner.class )
public class ResolveRangeTest {
    private ResolveRange resolveRange;

    @Mock
    private IRepository  repository;

    @Mock
    private GAV          gav;

    @Mock
    private Metadata     metaData;

    @Mock
    private Versioning   versioning;

    @Before
    public void setUp() {
        resolveRange = new ResolveRange( repository );
    }

    @Test
    public void apply() {
        Mockito.when( gav.getGroupId() ).thenReturn( "groupid" );
        Mockito.when( gav.getArtifactId() ).thenReturn( "artifactid" );
        Mockito.when( gav.getVersion() ).thenReturn( "[1.0.0,1.0.1]" );
        Mockito.when( repository.getMetaData( "groupid", "artifactid" ) ).thenReturn( Optional.of( metaData ) );
        Mockito.when( metaData.getVersioning() ).thenReturn( versioning );
        Mockito.when( versioning.getVersions() ).thenReturn( Arrays.asList( "1.0.0", "1.0.1", "1.0.2" ) );

        Optional<String> result = resolveRange.apply( gav );

        Assert.assertTrue( result.isPresent() );
        Assert.assertEquals( "1.0.1", result.get() );
    }

    @Test
    public void applyNoRange() {
        Mockito.when( gav.getGroupId() ).thenReturn( "groupid" );
        Mockito.when( gav.getArtifactId() ).thenReturn( "artifactid" );
        Mockito.when( gav.getVersion() ).thenReturn( "1.0.1" );
        Mockito.when( repository.getMetaData( "groupid", "artifactid" ) ).thenReturn( Optional.of( metaData ) );
        Mockito.when( metaData.getVersioning() ).thenReturn( versioning );
        Mockito.when( versioning.getVersions() ).thenReturn( Arrays.asList( "1.0.0", "1.0.1", "1.0.2" ) );

        Optional<String> result = resolveRange.apply( gav );

        Assert.assertTrue( result.isPresent() );
        Assert.assertEquals( "1.0.1", result.get() );
    }

    @Test( expected = IllegalArgumentException.class )
    public void applyInvalidRange() {
        Mockito.when( gav.getGroupId() ).thenReturn( "groupid" );
        Mockito.when( gav.getArtifactId() ).thenReturn( "artifactid" );
        Mockito.when( gav.getVersion() ).thenReturn( "[1.0.1," );

        resolveRange.apply( gav );
    }

}
