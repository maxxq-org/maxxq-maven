package org.chabernac.maven.repository;

import java.util.Optional;
import org.apache.maven.model.Model;
import org.chabernac.dependency.GAV;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VirtualRepositoryTest {
    private VirtualRepository virtualRepository = new VirtualRepository();

    @Mock
    private IRepository repository1;

    @Mock
    private IRepository repository2;

    @Mock
    private GAV gav;

    @Mock
    private Model model;

    @Before
    public void setUp() {
        virtualRepository.addRepository(repository1);
        virtualRepository.addRepository(repository2);
    }

    @Test
    public void readPomRepo1HasModel() {
        Mockito.when(repository1.readPom(gav)).thenReturn(Optional.of(model));
        Mockito.when(repository2.readPom(gav)).thenReturn(Optional.empty());

        Optional<Model> result = virtualRepository.readPom(gav);

        Assert.assertTrue(result.isPresent());
        Assert.assertSame(model, result.get());
    }

    @Test
    public void readPomRepo2HasModel() {
        Mockito.when(repository1.readPom(gav)).thenReturn(Optional.empty());
        Mockito.when(repository2.readPom(gav)).thenReturn(Optional.of(model));

        Optional<Model> result = virtualRepository.readPom(gav);

        Assert.assertTrue(result.isPresent());
        Assert.assertSame(model, result.get());
    }

    @Test
    public void readPomNoneHaveModel() {
        Mockito.when(repository1.readPom(gav)).thenReturn(Optional.empty());
        Mockito.when(repository2.readPom(gav)).thenReturn(Optional.empty());

        Optional<Model> result = virtualRepository.readPom(gav);

        Assert.assertFalse(result.isPresent());
    }
}
