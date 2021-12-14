package org.chabernac.maven.repository;

import java.io.IOException;
import java.io.InputStream;

import org.chabernac.dependency.GAV;
import org.chabernac.dependency.POMUtils;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RemoteRepository implements IRepository {
	private final OkHttpClient client = new OkHttpClient();
	private final POMUtils pomUtils;

	public RemoteRepository(String centralRepoUrl) {
		super();
		pomUtils = new POMUtils(centralRepoUrl);
	}

	@Override
	public InputStream readPom(GAV gav) {
		String endPoint = pomUtils.getPOMUrl(gav);

		Request request = new Request.Builder()
				.url(endPoint)
				.build();

		Call call = client.newCall(request);

		try {
			Response response = call.execute();
			if (response.code() != 200) {
				throw new RepositoryException(
						"Could not retrieve pom with gav '" + gav.toString() + "' http response code '"
								+ response.code() + "'");
			}
			return response.body().byteStream();
		} catch (IOException e) {
			throw new RepositoryException("Could not retrieve pom with gav '" + gav.toString() + "'", e);
		}
	}

}
