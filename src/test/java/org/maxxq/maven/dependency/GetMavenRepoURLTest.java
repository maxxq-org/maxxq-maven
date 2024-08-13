package org.maxxq.maven.dependency;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.maxxq.maven.repository.RemoteRepository;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GetMavenRepoURLTest {
    private GetMavenRepoURL getMavenRepoURL = new GetMavenRepoURL( RemoteRepository.MAVEN_CENTRAL );

    @Mock
    private GAV             dependency;

    @Test
    void apply() {
        Mockito.when( dependency.getArtifactId() ).thenReturn( "maven-model" );
        Mockito.when( dependency.getGroupId() ).thenReturn( "org.apache.maven" );
        Mockito.when( dependency.getVersion() ).thenReturn( "3.8.4" );

        assertEquals(
                "https://repo1.maven.org/maven2/org/apache/maven/maven-model/3.8.4/maven-model-3.8.4.pom",
                getMavenRepoURL.apply( dependency ) );
    }

}
