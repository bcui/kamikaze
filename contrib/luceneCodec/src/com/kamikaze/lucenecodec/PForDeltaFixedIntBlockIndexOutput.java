package com.kamikaze.lucenecodec;


/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.index.codecs.intblock.FixedIntBlockIndexOutput;
import org.apache.lucene.store.Directory;
import com.kamikaze.pfordelta.LCPForDelta;
import java.io.IOException;

public class PForDeltaFixedIntBlockIndexOutput extends FixedIntBlockIndexOutput {
  private final LCPForDelta compressor;
  private final int blockSize;
  public PForDeltaFixedIntBlockIndexOutput(Directory dir, String fileName, int blockSize) throws IOException {
    super(dir.createOutput(fileName), blockSize);
    this.blockSize = blockSize;
    compressor = new LCPForDelta();
  }

  @Override
  protected void flushBlock() throws IOException {
      int compressedSizeInInts = compressor.compress(buffer, blockSize);
      // write out the compressed size in ints 
      out.writeInt(compressedSizeInInts);
      
      int[] compBuffer = compressor.getCompBuffer(); 
      for(int i=0;i<compressedSizeInInts;i++) {
          out.writeInt(compBuffer[i]);
      }
      compressor.setCompBuffer(null);
  }
}

