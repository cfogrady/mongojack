/*
 * Copyright 2011 VZ Netzwerke Ltd
 * Copyright 2014 devbliss GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mongojack;

import java.util.ArrayList;
import java.util.Collection;

import com.mongodb.CommandResult;
import com.mongodb.DBObject;
import com.mongodb.ServerAddress;

/**
 * Represents the result of a Map/Reduce operation
 * 
 * @since 1.4
 * @author James Roper
 */
public class MapReduceOutput<T, K> {
    private final com.mongodb.MapReduceOutput output;
    private final JacksonDBCollection<T, K> outputCollection;
    private final Iterable<T> resultSet;

    MapReduceOutput(JacksonDBCollection<?, ?> sourceCollection,
            com.mongodb.MapReduceOutput output, Class<T> type, Class<K> keyType) {
        this.output = output;
        if (output.getOutputCollection() != null) {
            this.outputCollection = JacksonDBCollection.wrap(
                    output.getOutputCollection(), type, keyType,
                    sourceCollection.getObjectMapper());
            this.resultSet = outputCollection.find();
        } else {
            this.outputCollection = null;
            Collection<T> results = new ArrayList<T>();
            for (DBObject result : output.results()) {
                results.add(sourceCollection.convertFromDbObject(result, type));
            }
            this.resultSet = results;
        }
    }

    /**
     * Returns a cursor to the results of the operation
     * 
     * @return A cursor to the results of the operation
     */
    public Iterable<T> results() {
        return resultSet;
    }

    /**
     * Drops the collection that holds the results
     */
    public void drop() {
        output.drop();
    }

    /**
     * Gets the collection that holds the results (Will return null if results
     * are Inline)
     * 
     * @return The collection that holds the results
     */
    public JacksonDBCollection<T, K> getOutputCollection() {
        return outputCollection;
    }

    /**
     * Get the amount of time, in milliseconds, that it took to run this map reduce.
     *
     * @return an int representing the number of milliseconds it took to run the map reduce operation
     */
    public int getDuration() {
        return output.getDuration();
    }

    /**
     * Get the number of documents that were input into the map reduce operation
     *
     * @return the number of documents that read while processing this map reduce
     */
    public int getInputCount() {
        return output.getInputCount();
    }

    /**
     * Get the number of documents generated as a result of this map reduce
     *
     * @return the number of documents output by the map reduce
     */
    public int getOutputCount() {
        return output.getOutputCount();
    }

    /**
     * Get the number of messages emitted from the provided map function.
     *
     * @return the number of items emitted from the map function
     */
    public int getEmitCount() {
        return output.getEmitCount();
    }

    /**
     * Gets the raw command result of the operation.
     *
     * @return a CommandResult representing the output of the map-reduce in its raw form from the server.
     * @deprecated It is not recommended to access the command result returned by the server as the format can change between releases. This
     * has been replaced with a series of specific getters for the values on the CommandResult (
     * getDuration, getEmitCount, getOutputCount, getInputCount).  The method {@code results()} will continue to return an {@code
     * Iterable<T>}, that should be used to obtain the results of the map-reduce.
     */
    @Deprecated
    public CommandResult getCommandResult() {
        return output.getCommandResult();
    }

    public DBObject getCommand() {
        return output.getCommand();
    }

    /**
     * Get the server that the  command was run on.
     *
     * @return a ServerAddress of the server that the command ran against.
     * @deprecated this method will be removed in a future release.  There is no replacement for it.
     */
    @Deprecated
    public ServerAddress getServerUsed() {
        return output.getServerUsed();
    }

    @Override
    public String toString() {
        return output.toString();
    }

}
