package org.chabernac.dependency;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.model.Model;

public class POMUtils implements IPOMUtils {
    private static final Pattern VERSION_PATTERN = Pattern.compile("\\$\\{(.*)\\}");

    @Override
    public boolean isPropertyValue(String value) {
        return VERSION_PATTERN.matcher(value).matches();
    }

    @Override
    public String resolveProperty(String propertyValue, Model model) {
        Matcher matcher = VERSION_PATTERN.matcher(propertyValue);
        if (matcher.matches()) {
            if (matcher.group(1).equals("project.version")) {
                return GAV.fromModel(model).getVersion();
            }
            if (matcher.group(1).equals("project.groupId")) {
                return GAV.fromModel(model).getGroupId();
            }
            if (matcher.group(1).equals("project.artifactId")) {
                return GAV.fromModel(model).getArtifactId();
            }
            return model.getProperties().getProperty(matcher.group(1));
        }
        return propertyValue;
    }


}
