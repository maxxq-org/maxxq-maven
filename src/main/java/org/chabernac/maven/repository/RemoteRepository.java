package org.chabernac.maven.repository;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.model.Model;
import org.chabernac.dependency.GAV;
import org.chabernac.dependency.GetMavenRepoURL;
import org.chabernac.dependency.IModelIO;
import org.chabernac.dependency.ModelIO;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RemoteRepository implements IRepository {
    public static final String MAVEN_CENTRAL = "https://repo1.maven.org/maven2";

    private final static Logger LOGGER = LogManager.getLogger(RemoteRepository.class);

    private final OkHttpClient client = new OkHttpClient();
    private final Function<GAV, String> getMavenURL;
    private final IModelIO modelIO = new ModelIO();


    public RemoteRepository() {
        this(MAVEN_CENTRAL);
    }

    public RemoteRepository(String repository) {
        super();
        this.getMavenURL = new GetMavenRepoURL(repository);
    }

    @Override
    public Optional<Model> readPom(GAV gav) {
        String endPoint = getMavenURL.apply(gav);
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
                model = modelIO.getModelFromInputStream(response.body().byteStream());
            }
            return Optional.ofNullable(model);
        } catch (IOException e) {
            throw new RepositoryException("Could not retrieve pom with gav '" + gav.toString() + "'", e);
        }
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public GAV store(Model model) {
        throw new UnsupportedOperationException("store is not supported on this repository");
    }

}
