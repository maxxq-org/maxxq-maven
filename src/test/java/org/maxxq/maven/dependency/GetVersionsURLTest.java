package org.maxxq.maven.dependency;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetVersionsURLTest {
    private GetVersionsURL getVersionsURL = new GetVersionsURL("https://repo1.maven.org/maven2/");

    @Test
    void apply() {
        String result = getVersionsURL.apply("org.mockito", "mockito-all");

        assertEquals("https://repo1.maven.org/maven2/org/mockito/mockito-all/maven-metadata.xml", result);
    }
}
