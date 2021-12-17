package org.chabernac.maven.repository;

import java.io.IOException;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.model.Model;
import org.chabernac.dependency.GAV;
import org.chabernac.dependency.IPOMUtils;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RemoteRepository implements IRepository {
  private final static Logger LOGGER = LogManager.getLogger(RemoteRepository.class);

  private final OkHttpClient client = new OkHttpClient();
  private final IPOMUtils pomUtils;

  public RemoteRepository(IPOMUtils pomUtils) {
    super();
    this.pomUtils = pomUtils;
  }

  @Override
  public Optional<Model> readPom(GAV gav) {
    String endPoint = pomUtils.getPOMUrl(gav);
    LOGGER.debug("Resolving pom.xml from " + endPoint);

    Request request = new Request.Builder()
            .url(endPoint)
            .build();

    Call call = client.newCall(request);

    try {
      Response response = call.execute();
      Model model = null;
      if (response.code() == 404) {
        LOGGER.warn("no pom file found for: " + gav + "in remote repo");
      } else if (response.code() != 200) {
        throw new RepositoryException(
                "Could not retrieve pom with gav '" + gav.toString() + "' http response code '"
                        + response.code() + "'");
      } else {
        model = pomUtils.getModelFromInputStream(response.body().byteStream());
      }
      return Optional.ofNullable(model);
    } catch (IOException e) {
      throw new RepositoryException("Could not retrieve pom with gav '" + gav.toString() + "'", e);
    }
  }

}
