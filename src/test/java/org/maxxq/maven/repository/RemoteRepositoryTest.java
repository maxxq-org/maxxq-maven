package org.maxxq.maven.repository;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.maxxq.maven.dependency.GAV;
import org.maxxq.maven.model.MavenModel;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import okhttp3.Request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class RemoteRepositoryTest {
    private RemoteRepository                remoteRepository = new RemoteRepository();

    @Mock
    private IRemoteRepositoryRequestBuilder remoteRepositoryRequestBuilder;

    @Test
    void readPom() throws IOException {
        Optional<Model> result = remoteRepository.readPom( new GAV( "com.squareup.okhttp3", "okhttp", "4.9.3" ) );

        assertTrue( result.isPresent() );
        assertTrue( result.get() instanceof MavenModel );
        SimpleDateFormat format = new SimpleDateFormat( "dd/MM/yyyy" );
        assertEquals( "22/11/2021", format.format( ( (MavenModel) result.get() ).getCreationDate() ) );
        System.out.println( result.get() );
    }

    @Test
    void readPomWithCustomRequestBuilder() throws IOException {
        remoteRepository = new RemoteRepository( RemoteRepository.MAVEN_CENTRAL, remoteRepositoryRequestBuilder );
        String url = RemoteRepository.MAVEN_CENTRAL + "/com/squareup/okhttp3/okhttp/4.9.3/okhttp-4.9.3.pom";
        Mockito.when( remoteRepositoryRequestBuilder.apply( url ) )
            .thenReturn(
                new Request.Builder()
                    .url( url )
                    .build() );

        Optional<Model> result = remoteRepository.readPom( new GAV( "com.squareup.okhttp3", "okhttp", "4.9.3" ) );

        assertTrue( result.isPresent() );
        Mockito.verify( remoteRepositoryRequestBuilder, Mockito.times( 1 ) ).apply( url );
    }

    @Test
    void readPomApacheMaven() throws IOException, XmlPullParserException {
        Optional<Model> result = remoteRepository.readPom( new GAV( "org.apache.maven", "maven", "3.3.9" ) );

        assertTrue( result.isPresent() );
        System.out.println( result.get() );
    }

    @Test
    void readMetaDataApacheMaven() throws IOException, XmlPullParserException {
        Optional<Metadata> result = remoteRepository.getMetaData( "org.apache.maven", "maven" );

        assertTrue( result.isPresent() );
        assertTrue( result.get().getVersioning().getVersions().size() >= 62 );
    }

    @Test
    void readMetaAndCreationDates() {
        Optional<Metadata> result = remoteRepository.getMetaData( "commons-logging", "commons-logging" );

        assertTrue( result.isPresent() );

        Metadata metaData = result.get();
        Versioning versioning = metaData.getVersioning();
        versioning.getVersions().stream().forEach( version -> printVersionAndCreationDate( new GAV( "commons-logging", "commons-logging", version ) ) );
    }

    private void printVersionAndCreationDate( GAV gav ) {
        remoteRepository.readPom( gav ).ifPresent( model -> printModel( model ) );
    }

    private void printModel( Model model ) {
        if ( model instanceof MavenModel ) {
            System.out.println( GAV.fromModel( model ) + ": creation date: " + ( (MavenModel) model ).getCreationDate() );
        }
    }
}
