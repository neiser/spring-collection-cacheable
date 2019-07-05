package com.example.springcollectioncacheable.test;

import com.example.springcollectioncacheable.CollectionCacheable;
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

    @Cacheable(cacheNames = "myCache")
    public MyValue findById(MyId id) {
        LOGGER.info("Getting value for id={}", id);
        return myDbRepository.findById(id);
    }

    @Cacheable(cacheNames = "myCache", key = "#id.id")
    public MyValue findByIdWithKey(MyId id) {
        LOGGER.info("Getting value with key for id={}", id);
        return myDbRepository.findById(id);
    }

    @CollectionCacheable("myCache")
    public Map<MyId, MyValue> findByIds(Collection<MyId> ids) {
        LOGGER.info("Getting mapped values for ids={}", ids);
        return ids.stream().collect(Collectors.toMap(x -> x, myDbRepository::findById));
    }

    @CollectionCacheable(cacheNames = "myCache", condition = "#ids.size() < 3")
    public Map<MyId, MyValue> findByIdsWithCondition(Collection<MyId> ids) {
        LOGGER.info("Getting mapped values with condition for ids={}", ids);
        return ids.stream().collect(Collectors.toMap(x -> x, myDbRepository::findById));
    }

    @CollectionCacheable(cacheNames = "myCache", unless = "#result.size() > 1")
    public Map<MyId, MyValue> findByIdsWithUnless(Collection<MyId> ids) {
        LOGGER.info("Getting mapped values with unless for ids={}", ids);
        return ids.stream().collect(Collectors.toMap(x -> x, myDbRepository::findById));
    }

    @CollectionCacheable(cacheNames = "myCache", key = "#p0.id")
    public Map<MyId, MyValue> findByIdsWithKey(Collection<MyId> ids) {
        LOGGER.info("Getting mapped values with key for ids={}", ids);
        return ids.stream().collect(Collectors.toMap(x -> x, myDbRepository::findById));
    }

    @CollectionCacheable("myCache")
    public Map<MyId, MyValue> findAll() {
        LOGGER.info("Getting all values");
        return myDbRepository.findAll();
    }

    @CollectionCacheable(cacheNames = "myCache", unless = "#result.size() > 1")
    public Map<MyId, MyValue> findAllWithUnless() {
        LOGGER.info("Getting all values with unless");
        return myDbRepository.findAll();
    }

    @CollectionCacheable(cacheNames = "myCache", key = "#result.id")
    public Map<MyId, MyValue> findAllWithKey() {
        LOGGER.info("Getting all values with condition");
        return myDbRepository.findAll();
    }
}
