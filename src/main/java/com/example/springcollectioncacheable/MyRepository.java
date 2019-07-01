package com.example.springcollectioncacheable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class MyRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyRepository.class);

    private final MyDbRepository myDbRepository;

    @Autowired
    public MyRepository(MyDbRepository myDbRepository) {
        this.myDbRepository = myDbRepository;
    }

    @Cacheable("myCache")
    public MyValue findById(MyId id) {
        LOGGER.info("Getting value for id={}", id);
        return myDbRepository.findById(id);
    }

    @CollectionCacheable("myCache")
    public Map<MyId, MyValue> findByIds(Collection<MyId> ids) {
        LOGGER.info("Getting mapped values for ids={}", ids);
        return ids.stream().collect(Collectors.toMap(x -> x, myDbRepository::findById));
    }
}
