// Copyright 2017 HugeGraph Authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.baidu.hugegraph.diskstorage.util;

import com.google.common.base.Preconditions;
import com.baidu.hugegraph.diskstorage.BackendException;
import com.baidu.hugegraph.diskstorage.BaseTransactionConfig;
import com.baidu.hugegraph.diskstorage.BaseTransactionConfigurable;

/**
 */
public class DefaultTransaction implements BaseTransactionConfigurable {

    private final BaseTransactionConfig config;

    public DefaultTransaction(BaseTransactionConfig config) {
        Preconditions.checkNotNull(config);
        this.config = config;
    }

    @Override
    public BaseTransactionConfig getConfiguration() {
        return config;
    }

    @Override
    public void commit() throws BackendException {
    }

    @Override
    public void rollback() throws BackendException {
    }

}