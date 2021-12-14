package org.chabernac.maven.repository;

import java.io.IOException;
import java.io.InputStream;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.chabernac.dependency.GAV;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Assert;
import org.junit.Test;

public class RemoteRepositoryTest {
	private RemoteRepository remoteRepository = new RemoteRepository("https://repo1.maven.org/maven2");

	@Test
	public void readPom() throws IOException {
		InputStream result = remoteRepository.readPom(new GAV("com.squareup.okhttp3", "okhttp", "4.9.3"));
		
		System.out.println(new String(result.readAllBytes()));
		Assert.assertNotNull(result);
	}
	
	@Test
	public void readPomApacheMaven() throws IOException, XmlPullParserException {
		InputStream result = remoteRepository.readPom(new GAV("org.apache.maven", "maven", "3.3.9"));
		MavenXpp3Reader reader = new MavenXpp3Reader();
		Model model = reader.read(result);
		System.out.println(model);
//		System.out.println(new String(result.readAllBytes()));
		Assert.assertNotNull(result);
	}
}
