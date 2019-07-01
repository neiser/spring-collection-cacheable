package com.example.springcollectioncacheable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyRepositoryIntTest {

    private static final String SOME_KEY = "some-key";
    private static final String SOME_VALUE = "some-value";

    @Autowired
    private MyRepository sut;

    @MockBean
    private MyDbRepository myDbRepository;


    @Test
    public void findById_caching() throws Exception {
        when(myDbRepository.findById(SOME_KEY)).thenReturn(SOME_VALUE);

        // find it two times, but database is only asked once
        assertThat(sut.findById(SOME_KEY)).isEqualTo(SOME_VALUE);
        assertThat(sut.findById(SOME_KEY)).isEqualTo(SOME_VALUE);

        verify(myDbRepository, times(1)).findById(SOME_KEY);
    }
}