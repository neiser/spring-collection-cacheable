package com.example.springcollectioncacheable;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cache.annotation.AbstractCachingConfiguration;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.annotation.SpringCacheAnnotationParser;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CollectionCacheableProxyCachingConfiguration extends AbstractCachingConfiguration {

    private final CacheOperationSource cacheOperationSource = new AnnotationCacheOperationSource(new SpringCacheAnnotationParser(), new CollectionCacheableCacheAnnotationParser());

    @Bean
    public BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return beanFactory -> beanFactory.addBeanPostProcessor(new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) {
                if (bean instanceof BeanFactoryCacheOperationSourceAdvisor) {
                    BeanFactoryCacheOperationSourceAdvisor advisor = (BeanFactoryCacheOperationSourceAdvisor) bean;
                    advisor.setCacheOperationSource(cacheOperationSource);
                    advisor.setAdvice(collectionCacheInterceptor());
                }
                return bean;
            }
        });
    }

    @Bean
    public CacheInterceptor collectionCacheInterceptor() {
        CacheInterceptor interceptor = new CollectionCacheInterceptor();
        interceptor.configure(this.errorHandler, this.keyGenerator, this.cacheResolver, this.cacheManager);
        interceptor.setCacheOperationSource(cacheOperationSource);
        return interceptor;
    }
}
