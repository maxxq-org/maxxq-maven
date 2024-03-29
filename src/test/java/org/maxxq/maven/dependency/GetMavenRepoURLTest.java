package org.maxxq.maven.dependency;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.maxxq.maven.repository.RemoteRepository;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith( MockitoJUnitRunner.class )
public class GetMavenRepoURLTest {
    private GetMavenRepoURL getMavenRepoURL = new GetMavenRepoURL( RemoteRepository.MAVEN_CENTRAL );

    @Mock
    private GAV             dependency;

    @Test
    public void apply() {
        Mockito.when( dependency.getArtifactId() ).thenReturn( "maven-model" );
        Mockito.when( dependency.getGroupId() ).thenReturn( "org.apache.maven" );
        Mockito.when( dependency.getVersion() ).thenReturn( "3.8.4" );

        Assert
            .assertEquals(
                "https://repo1.maven.org/maven2/org/apache/maven/maven-model/3.8.4/maven-model-3.8.4.pom",
                getMavenRepoURL.apply( dependency ) );
    }

}
