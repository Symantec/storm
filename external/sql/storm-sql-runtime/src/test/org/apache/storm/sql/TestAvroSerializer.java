/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.sql;

import org.apache.storm.sql.runtime.serde.avro.AvroScheme;
import org.apache.storm.sql.runtime.serde.avro.AvroSerializer;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;

public class TestAvroSerializer {
  private static final String schemaString = "{\"type\":\"record\"," +
          "\"name\":\"avrotest\"," +
          "\"fields\":[{\"name\":\"ID\",\"type\":\"int\"}," +
          "{ \"name\":\"val\", \"type\":\"string\" }]}";

  private static final String schemaString1 = "{\"type\":\"record\"," +
          "\"name\":\"avrotest\"," +
          "\"fields\":[{\"name\":\"ID\",\"type\":\"int\"}," +
          "{ \"type\":{\"type\":\"map\",\"values\": \"long\"}, \"name\":\"val1\" }," +
          "{ \"type\":{\"type\":\"array\",\"items\": \"string\"}, \"name\":\"val2\" }]}";

  @Test
  public void testAvroSchemeAndSerializer() {
    List<String> fields = new ArrayList<>();
    fields.add("ID");
    fields.add("val");
    List<Object> o = new ArrayList<>();
    o.add(1);
    o.add("2");

    AvroSerializer serializer = new AvroSerializer(schemaString, fields);
    ByteBuffer byteBuffer = serializer.write(o, null);

    AvroScheme scheme = new AvroScheme(schemaString, fields);
    assertArrayEquals(o.toArray(), scheme.deserialize(byteBuffer).toArray());
  }

  @Test
  public void testAvroComplexSchemeAndSerializer() {
    List<String> fields = new ArrayList<>();
    fields.add("ID");
    fields.add("val1");
    fields.add("val2");

    Map<String,Long> mp = new HashMap<>();
    mp.put("l1",1234L);
    mp.put("l2",56789L);
    List<String> ls = new ArrayList<>();
    ls.add("s1");
    ls.add("s2");
    List<Object> o = new ArrayList<>();
    o.add(1);
    o.add(mp);
    o.add(ls);

    AvroSerializer serializer = new AvroSerializer(schemaString1, fields);
    ByteBuffer byteBuffer = serializer.write(o, null);

    AvroScheme scheme = new AvroScheme(schemaString1, fields);
    assertArrayEquals(o.toArray(), scheme.deserialize(byteBuffer).toArray());
  }
}
