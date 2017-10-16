package org.alan.gen;

import com.dyuproject.protostuff.Input;
import com.dyuproject.protostuff.Output;
import com.dyuproject.protostuff.Pipe;
import com.dyuproject.protostuff.WireFormat;
import com.dyuproject.protostuff.runtime.Delegate;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created on 2017/8/28.
 *
 * @author Alan
 * @since 1.0
 */
public class AtomicLongDelegate implements Delegate<AtomicLong> {
    public WireFormat.FieldType getFieldType() {
        return WireFormat.FieldType.INT64;
    }

    public Class<?> typeClass() {
        return AtomicLong.class;
    }

    public AtomicLong readFrom(Input input) throws IOException {
        return new AtomicLong(input.readInt64());
    }

    public void writeTo(Output output, int number, AtomicLong value,
                        boolean repeated) throws IOException {
        output.writeInt64(number, value.longValue(), repeated);
    }

    public void transfer(Pipe pipe, Input input, Output output, int number,
                         boolean repeated) throws IOException {
        output.writeInt64(number, input.readInt64(), repeated);
    }
}
