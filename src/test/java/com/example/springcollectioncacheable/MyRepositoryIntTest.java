package com.example.springcollectioncacheable;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyRepositoryIntTest {

    private static final String SOME_KEY_1 = "some-key-1";
    private static final String SOME_VALUE_1 = "some-value-1";
    private static final String SOME_KEY_2 = "some-key-2";
    private static final String SOME_VALUE_2 = "some-value-2";

    @Autowired
    private MyRepository sut;

    @MockBean
    private MyDbRepository myDbRepository;

    @Autowired
    private CacheManager cacheManager;

    @Before
    public void setUp() throws Exception {
        cacheManager.getCacheNames().stream()
                .map(cacheManager::getCache)
                .forEach(Cache::clear);
    }

    @Test
    public void findById() throws Exception {
        when(myDbRepository.findById(SOME_KEY_1)).thenReturn(SOME_VALUE_1);

        // find it two times, but database is only asked once
        assertThat(sut.findById(SOME_KEY_1)).isEqualTo(SOME_VALUE_1);
        assertThat(sut.findById(SOME_KEY_1)).isEqualTo(SOME_VALUE_1);

        verify(myDbRepository, times(1)).findById(SOME_KEY_1);
    }

    @Test
    public void findByIds() throws Exception {
        when(myDbRepository.findById(SOME_KEY_1)).thenReturn(SOME_VALUE_1);
        when(myDbRepository.findById(SOME_KEY_2)).thenReturn(SOME_VALUE_2);

        // find it two times, but database is only asked once
        assertThat(sut.findByIds(ImmutableSet.of(SOME_KEY_1, SOME_KEY_2)))
                .containsOnly(entry(SOME_KEY_1, SOME_VALUE_1), entry(SOME_KEY_2, SOME_VALUE_2));
        assertThat(sut.findByIds(ImmutableSet.of(SOME_KEY_1, SOME_KEY_2)))
                .containsOnly(entry(SOME_KEY_1, SOME_VALUE_1), entry(SOME_KEY_2, SOME_VALUE_2));

        verify(myDbRepository, times(1)).findById(SOME_KEY_1);
        verify(myDbRepository, times(1)).findById(SOME_KEY_2);
    }

    @Test
    public void findByIdsAfterTwoFindById() throws Exception {
        when(myDbRepository.findById(SOME_KEY_1)).thenReturn(SOME_VALUE_1);
        when(myDbRepository.findById(SOME_KEY_2)).thenReturn(SOME_VALUE_2);

        // find it two times, but database is only asked once
        assertThat(sut.findById(SOME_KEY_1)).isEqualTo(SOME_VALUE_1);
        assertThat(sut.findById(SOME_KEY_2)).isEqualTo(SOME_VALUE_2);
        assertThat(sut.findByIds(ImmutableSet.of(SOME_KEY_1, SOME_KEY_2)))
                .containsOnly(entry(SOME_KEY_1, SOME_VALUE_1), entry(SOME_KEY_2, SOME_VALUE_2));

        verify(myDbRepository, times(1)).findById(SOME_KEY_1);
        verify(myDbRepository, times(1)).findById(SOME_KEY_2);
    }

    @Test
    public void findByIdsAfterOneFindById() throws Exception {
        when(myDbRepository.findById(SOME_KEY_1)).thenReturn(SOME_VALUE_1);
        when(myDbRepository.findById(SOME_KEY_2)).thenReturn(SOME_VALUE_2);

        // find it two times, but database is only asked once
        assertThat(sut.findById(SOME_KEY_1)).isEqualTo(SOME_VALUE_1);
        assertThat(sut.findByIds(ImmutableSet.of(SOME_KEY_1, SOME_KEY_2)))
                .containsOnly(entry(SOME_KEY_1, SOME_VALUE_1), entry(SOME_KEY_2, SOME_VALUE_2));

        verify(myDbRepository, times(1)).findById(SOME_KEY_1);
    }

}