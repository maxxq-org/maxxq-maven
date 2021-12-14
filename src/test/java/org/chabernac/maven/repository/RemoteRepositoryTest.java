package org.chabernac.maven.repository;

import java.io.IOException;

import org.apache.maven.model.Model;
import org.chabernac.dependency.GAV;
import org.chabernac.dependency.POMUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Assert;
import org.junit.Test;

public class RemoteRepositoryTest {
	private RemoteRepository remoteRepository = new RemoteRepository(new POMUtils());

	@Test
	public void readPom() throws IOException {
		Model result = remoteRepository.readPom(new GAV("com.squareup.okhttp3", "okhttp", "4.9.3"));

		System.out.println(result);
		Assert.assertNotNull(result);
	}

	@Test
	public void readPomApacheMaven() throws IOException, XmlPullParserException {
		Model result = remoteRepository.readPom(new GAV("org.apache.maven", "maven", "3.3.9"));
		System.out.println(result);
		Assert.assertNotNull(result);
	}
}
