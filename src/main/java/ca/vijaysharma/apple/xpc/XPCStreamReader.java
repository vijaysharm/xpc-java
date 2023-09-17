package ca.vijaysharma.apple.xpc;

import ca.vijaysharma.apple.xpc.exceptions.XPCInvalidTypeException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import static ca.vijaysharma.apple.xpc.XPCUtilities.roundUp;
import static java.nio.charset.StandardCharsets.UTF_8;

class XPCStreamReader {
    private static final byte[] NULL_TERMINATED = { 0x00 };
    private final ByteBuffer bytes;
    public XPCStreamReader(byte[] bytes) {
        this.bytes = ByteBuffer.wrap(bytes);
    }

    byte[] readBytes(int length) {
        var alignedLength = roundUp(length, 4);
        byte[] read = new byte[alignedLength];
        this.bytes.order(ByteOrder.LITTLE_ENDIAN).get(read, 0,  alignedLength);
        return Arrays.copyOf(read, length);
    }

    int readInt32() {
        return this.bytes.order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    long readInt64() {
        return this.bytes.order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    double readDouble() {
        // TODO: I'm not sure if doubles are stored as small or big endian :(
        // pymobiledevice3 treats it as big endian
        // but t2-xpc says its little endian
        return this.bytes.order(ByteOrder.BIG_ENDIAN).getDouble();
    }

    String readAlignedStringLength(int length) {
        var alignedLength = roundUp(length, 4);
        var bytes = readBytes(alignedLength);
        return new String(bytes, UTF_8).trim();
    }

    XPCStreamReader readStream(int length) {
        if (length % 4 != 0) {
            throw new RuntimeException("Can only read byte stream as a multiple of 4");
        }

        return new XPCStreamReader(this.readBytes(length));
    }

    String readDictionaryKey() {
        var position = find(NULL_TERMINATED);
        return readAlignedStringLength(position + 1);
    }

    XPCType readType() throws XPCInvalidTypeException {
        int type = readInt32();
        return XPCType.from(type);
    }

    private int find(byte[] target) {
        int pos = -1;

        for (int i = 0; i < bytes.remaining() - target.length + 1; i++) {
            boolean found = true;

            for (int j = 0; j < target.length; j++) {
                if (bytes.get(bytes.position() + i + j) != target[j]) {
                    found = false;
                    break;
                }
            }

            if (found) {
                pos = i;
                break;
            }
        }

        return pos;
    }
}
