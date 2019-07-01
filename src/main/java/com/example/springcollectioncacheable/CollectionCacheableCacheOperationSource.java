package com.example.springcollectioncacheable;

import org.springframework.cache.annotation.AnnotationCacheOperationSource;

public class CollectionCacheableCacheOperationSource extends AnnotationCacheOperationSource {
    public CollectionCacheableCacheOperationSource() {
        super(new CollectionCacheableCacheAnnotationParser());
    }
}
