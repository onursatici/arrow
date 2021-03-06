/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.arrow.adapter.jdbc.consumer;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.arrow.vector.Float8Vector;

/**
 * Consumer which consume double type values from {@link ResultSet}.
 * Write the data to {@link org.apache.arrow.vector.Float8Vector}.
 */
public abstract class DoubleConsumer implements JdbcConsumer<Float8Vector> {

  /**
   * Creates a consumer for {@link Float8Vector}.
   */
  public static DoubleConsumer createConsumer(Float8Vector vector, int index, boolean nullable) {
    if (nullable) {
      return new NullableDoubleConsumer(vector, index);
    } else {
      return new NonNullableDoubleConsumer(vector, index);
    }
  }

  protected Float8Vector vector;
  protected final int columnIndexInResultSet;

  protected int currentIndex;

  /**
   * Instantiate a DoubleConsumer.
   */
  public DoubleConsumer(Float8Vector vector, int index) {
    this.vector = vector;
    this.columnIndexInResultSet = index;
  }

  @Override
  public void close() throws Exception {
    this.vector.close();
  }

  @Override
  public void resetValueVector(Float8Vector vector) {
    this.vector = vector;
    this.currentIndex = 0;
  }

  /**
   * Nullable double consumer.
   */
  static class NullableDoubleConsumer extends DoubleConsumer {

    /**
     * Instantiate a DoubleConsumer.
     */
    public NullableDoubleConsumer(Float8Vector vector, int index) {
      super(vector, index);
    }

    @Override
    public void consume(ResultSet resultSet) throws SQLException {
      double value = resultSet.getDouble(columnIndexInResultSet);
      if (!resultSet.wasNull()) {
        vector.setSafe(currentIndex, value);
      }
      currentIndex++;
    }
  }

  /**
   * Non-nullable double consumer.
   */
  static class NonNullableDoubleConsumer extends DoubleConsumer {

    /**
     * Instantiate a DoubleConsumer.
     */
    public NonNullableDoubleConsumer(Float8Vector vector, int index) {
      super(vector, index);
    }

    @Override
    public void consume(ResultSet resultSet) throws SQLException {
      double value = resultSet.getDouble(columnIndexInResultSet);
      vector.setSafe(currentIndex, value);
      currentIndex++;
    }
  }
}
