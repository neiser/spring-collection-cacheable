package com.example.springcollectioncacheable;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectionCacheableCacheInterceptor extends CacheInterceptor {

    private static final Object NO_RESULT = new Object();

    @Override
    protected Object execute(CacheOperationInvoker invoker, Object target, Method method, Object[] invocationArgs) {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
        CacheOperationSource cacheOperationSource = getCacheOperationSource();
        if (cacheOperationSource != null) {
            Collection<CacheOperation> operations = cacheOperationSource.getCacheOperations(method, targetClass);

            CollectionCacheableOperation collectionCacheableOperation = findCollectionCacheableOperation(operations);
            if (collectionCacheableOperation != null) {
                return handleCollectionCacheable(collectionCacheableOperation, targetClass, invoker, target, method, invocationArgs);
            }
        }
        return super.execute(invoker, target, method, invocationArgs);
    }

    private Object handleCollectionCacheable(CollectionCacheableOperation operation, Class<?> targetClass, CacheOperationInvoker invoker, Object target, Method method, Object[] invocationArgs) {
        CollectionCacheableOperationContext context = getCollectionCacheableOperationContext(operation, method, target, targetClass);

        if (operation.isFindAll()) {
            Map<?, ?> uncachedResult = invokeMethod(invoker);
            putUncachedResultToCache(uncachedResult, context);
            return uncachedResult;
        }

        Collection idsArgument = injectCollectionArgument(invocationArgs);
        if (!context.isConditionPassingWithArgument(idsArgument)) {
            return invokeMethod(invoker);
        }

        Map<Object, Object> result = new HashMap<>();
        Iterator idIterator = idsArgument.iterator();
        while (idIterator.hasNext()) {
            Object id = idIterator.next();
            Object key = context.generateKeyFromSingleArgument(id);
            Cache.ValueWrapper cacheHit = findInCaches(context, key);
            if (cacheHit != null) {
                result.put(id, cacheHit.get());
                idIterator.remove();
            }
        }
        if (!idsArgument.isEmpty()) {
            Map<?, ?> uncachedResult = invokeMethod(invoker);
            result.putAll(uncachedResult);
            putUncachedResultToCache(uncachedResult, context);
        }
        return result;
    }

    private void putUncachedResultToCache(Map<?, ?> uncachedResult, CollectionCacheableOperationContext context) {
        if (context.canPutToCache(uncachedResult)) {
            for (Map.Entry<?, ?> entry : uncachedResult.entrySet()) {
                Object key = context.generateKeyFromSingleArgument(entry.getKey());
                for (Cache cache : context.getCaches()) {
                    doPut(cache, key, entry.getValue());
                }
            }
        }
    }

    private Map invokeMethod(CacheOperationInvoker invoker) {
        Object result = invoker.invoke();
        if (result instanceof Map) {
            return (Map) result;
        }
        throw new IllegalStateException("Expecting result of invocation to be a Map");
    }

    @Nullable
    private Cache.ValueWrapper findInCaches(CollectionCacheableOperationContext context, Object key) {
        for (Cache cache : context.getCaches()) {
            Cache.ValueWrapper wrapper = doGet(cache, key);
            if (wrapper != null) {
                return wrapper;
            }
        }
        return null;
    }

    private Collection injectCollectionArgument(Object[] invocationArgs) {
        if (invocationArgs.length == 1 && invocationArgs[0] instanceof Collection) {
            Collection foundCollection = new LinkedList<>((Collection<?>) invocationArgs[0]);
            invocationArgs[0] = foundCollection;
            return foundCollection;
        }
        throw new IllegalStateException("Did not find exactly one Collection argument");
    }

    @Nullable
    private CollectionCacheableOperation findCollectionCacheableOperation(@Nullable Collection<CacheOperation> operations) {
        if (operations == null) {
            return null;
        }
        List<CollectionCacheableOperation> collectionCacheableOperations = operations.stream()
                .filter(o -> o instanceof CollectionCacheableOperation)
                .map(o -> (CollectionCacheableOperation) o)
                .collect(Collectors.toList());
        if (collectionCacheableOperations.isEmpty()) {
            return null;
        }
        if (collectionCacheableOperations.size() == 1) {
            return collectionCacheableOperations.get(0);
        }
        throw new IllegalStateException("Found more than one @CollectionCacheable annotation");
    }

    protected CollectionCacheableOperationContext getCollectionCacheableOperationContext(
            CacheOperation operation, Method method, Object target, Class<?> targetClass) {
        CacheOperationMetadata metadata = getCacheOperationMetadata(operation, method, targetClass);
        Object[] currentArgs = new Object[]{null};
        return new CollectionCacheableOperationContext(metadata, currentArgs, target);
    }

    protected class CollectionCacheableOperationContext extends CacheOperationContext {
        private final Object[] currentArgs;

        public CollectionCacheableOperationContext(CacheOperationMetadata metadata, Object[] currentArgs, Object target) {
            super(metadata, currentArgs, target);
            this.currentArgs = currentArgs;
        }

        public Object generateKeyFromSingleArgument(Object arg) {
            currentArgs[0] = arg;
            return generateKey(arg); // provide arg as result as well for findAll case
        }

        @Override
        public boolean isConditionPassing(Object result) {
            return super.isConditionPassing(result);
        }

        @Override
        protected boolean canPutToCache(Object result) {
            currentArgs[0] = null;
            return super.canPutToCache(result);
        }

        public boolean isConditionPassingWithArgument(Object arg) {
            currentArgs[0] = arg;
            return super.isConditionPassing(NO_RESULT);
        }

        @Override
        public Collection<? extends Cache> getCaches() {
            return super.getCaches();
        }
    }
}
