package com.example.springcollectioncacheable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.cache.interceptor.CacheOperationSource;

import java.lang.reflect.Method;
import java.util.Collection;

public class CollectionCacheInterceptor extends CacheInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionCacheInterceptor.class);

    @Override
    protected Object execute(CacheOperationInvoker invoker, Object target, Method method, Object[] args) {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
        CacheOperationSource cacheOperationSource = getCacheOperationSource();
        if (cacheOperationSource != null) {
            Collection<CacheOperation> operations = cacheOperationSource.getCacheOperations(method, targetClass);
            if (hasCollectionCacheableOperation(operations)) {
                LOGGER.debug("Found CollectionCacheable operation");
                return invoker.invoke();
            }
        }
        return super.execute(invoker, target, method, args);
    }

    private boolean hasCollectionCacheableOperation(Collection<CacheOperation> operations) {
        return operations.stream().anyMatch(o -> o instanceof CollectionCacheableOperation);
    }
}
