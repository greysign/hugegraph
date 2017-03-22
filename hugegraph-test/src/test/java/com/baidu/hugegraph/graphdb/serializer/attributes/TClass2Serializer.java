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

package com.baidu.hugegraph.graphdb.serializer.attributes;

import com.baidu.hugegraph.core.attribute.AttributeSerializer;
import com.baidu.hugegraph.diskstorage.ScanBuffer;
import com.baidu.hugegraph.diskstorage.WriteBuffer;
import com.baidu.hugegraph.graphdb.database.serialize.attribute.StringSerializer;

/**
 */
public class TClass2Serializer implements AttributeSerializer<TClass2> {

    private final StringSerializer strings = new StringSerializer();

    @Override
    public TClass2 read(ScanBuffer buffer) {
        return new TClass2(strings.read(buffer),buffer.getInt());
    }

    @Override
    public void write(WriteBuffer buffer, TClass2 attribute) {
        strings.write(buffer,attribute.getS());
        buffer.putInt(attribute.getI());
    }

}