/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.springcollectioncacheable;

import org.springframework.cache.interceptor.CacheableOperation;

public class CollectionCacheableOperation extends CacheableOperation {
    private final boolean isFindAll;

    public CollectionCacheableOperation(CollectionCacheableOperation.Builder b) {
        super(b);
        this.isFindAll = b.isFindAll;
    }

    public boolean isFindAll() {
        return isFindAll;
    }

    public static class Builder extends CacheableOperation.Builder {

        private boolean isFindAll;

        public void setFindAll(boolean findAll) {
            isFindAll = findAll;
        }

        @Override
        protected StringBuilder getOperationDescription() {
            StringBuilder sb = super.getOperationDescription();
            sb.append(" | isFindAll ='");
            sb.append(this.isFindAll);
            return sb;
        }

        @Override
        public CollectionCacheableOperation build() {
            return new CollectionCacheableOperation(this);
        }
    }

}
