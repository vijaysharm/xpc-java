package ca.vijaysharma.apple.xpc;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class XPCStreamWriter {
    private final ByteArrayOutputStream stream;

    public XPCStreamWriter() {
        this.stream = new ByteArrayOutputStream();;
    }


    public byte[] toByteArray() {
        return stream.toByteArray();
    }

    public void write(XPCType type) {
        writeInt32(type.value);
    }

    public void writeInt32(int value) {
        stream.writeBytes(ByteBuffer.allocate(4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(value)
            .array()
        );
    }

    public void writeInt64(long value) {
        stream.writeBytes(ByteBuffer.allocate(8)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putLong(value)
            .array()
        );
    }

    public void writeDouble(double value) {
        stream.writeBytes(ByteBuffer.allocate(8)
            .order(ByteOrder.BIG_ENDIAN)
            .putDouble(value)
            .array()
        );
    }

    public void writeBytes(byte[] bytes) {
        stream.writeBytes(bytes);
    }
}
