/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.processor.internals.namedtopology;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.TaskId;

import java.util.Properties;

public class NamedTopologyStreamsBuilder extends StreamsBuilder {
    final String topologyName;

    /**
     * @param topologyName  any string representing your NamedTopology, all characters allowed except for "__"
     * @throws IllegalArgumentException if the name contains the character sequence "__"
     */
    public NamedTopologyStreamsBuilder(final String topologyName) {
        super();
        this.topologyName = topologyName;
        if (topologyName.contains(TaskId.NAMED_TOPOLOGY_DELIMITER)) {
            throw new IllegalArgumentException("The character sequence '__' is not allowed in a NamedTopology, please select a new name");
        }
    }

    public synchronized NamedTopology buildNamedTopology(final Properties props) {
        super.build(props);
        final NamedTopology namedTopology = (NamedTopology) super.topology;
        namedTopology.setTopologyName(topologyName);
        return namedTopology;
    }

    @Override
    public Topology getNewTopology() {
        return new NamedTopology();
    }
}
