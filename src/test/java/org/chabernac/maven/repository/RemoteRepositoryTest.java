package org.chabernac.maven.repository;

import java.io.IOException;
import java.util.Optional;

import org.apache.maven.model.Model;
import org.chabernac.dependency.GAV;
import org.chabernac.dependency.POMUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Assert;
import org.junit.Test;

public class RemoteRepositoryTest {
	private RemoteRepository remoteRepository = new RemoteRepository(new POMUtils("https://artifacts.axa.be/artifactory/maven-all/"));

	@Test
	public void readPom() throws IOException {
		Optional<Model> result = remoteRepository.readPom(new GAV("com.squareup.okhttp3", "okhttp", "4.9.3"));

		Assert.assertTrue( result.isPresent() );
		System.out.println(result.get());
	}

	@Test
	public void readPomApacheMaven() throws IOException, XmlPullParserException {
	    Optional<Model> result = remoteRepository.readPom(new GAV("org.apache.maven", "maven", "3.3.9"));
	    
	    Assert.assertTrue( result.isPresent() );
		System.out.println(result.get());
	}
}
