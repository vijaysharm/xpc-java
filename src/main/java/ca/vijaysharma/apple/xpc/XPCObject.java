package ca.vijaysharma.apple.xpc;

import ca.vijaysharma.apple.xpc.exceptions.XPCException;
import ca.vijaysharma.apple.xpc.exceptions.XPCInvalidValueException;
import ca.vijaysharma.apple.xpc.exceptions.XPCUnimplementedException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public interface XPCObject {
    XPCType type();

    static XPCObject from(byte[] bytes) throws XPCException {
        var reader = new XPCStreamReader(bytes);
        return from(reader);
    }
    private static XPCObject from(XPCStreamReader reader) throws XPCException {

        XPCType type = reader.readType();
        return switch (type) {
            case XPC_NULL -> new XPCNull();
            case XPC_BOOL -> {
                var value = reader.readInt32();
                if (value != 0 && value != 1) {
                    throw new XPCInvalidValueException("Invalid XPCBool value found " + value + " (" + Integer.toHexString(value) + ")");
                }
                yield new XPCBool(value == 1);
            }
            case XPC_INT64 -> {
                var value = reader.readInt64();
                yield new XPCInt64(value);
            }
            case XPC_UINT64 -> {
                var value = reader.readInt64();
                if (value < 0) {
                    throw new XPCInvalidValueException("Read invalid value " + value + " for XPCUInt64");
                }
                yield new XPCUInt64(value);
            }
            case XPC_DOUBLE -> {
                var value = reader.readDouble();
                yield new XPCDouble(value);
            }
            case XPC_DATE -> {
                var nano = reader.readInt64();
                var instant = Instant.ofEpochMilli(TimeUnit.NANOSECONDS.toMillis(nano));
                yield new XPCDate(instant);
            }
            case XPC_DATA -> {
                var length = reader.readInt32();
                var data = reader.readBytes(length);
                yield new XPCData(data);
            }
            case XPC_STRING -> {
                var length = reader.readInt32();
                var string = reader.readAlignedStringLength(length);
                yield new XPCString(string);
            }
            case XPC_UUID -> {
                byte[] data = reader.readBytes(16);
                var buf = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);
                var uuid = new UUID(buf.getLong(), buf.getLong());
                yield new XPCUUID(uuid);
            }
            case XPC_ARRAY -> {
                var length = reader.readInt32();
                var arrayReader = reader.readStream(length);
                var count = arrayReader.readInt32();
                var list = new ArrayList<XPCObject>();
                for (int index = 0; index < count; index++) {
                    var value = from(arrayReader);
                    list.add(value);
                }
                yield new XPCArray(list);
            }
            case XPC_DICTIONARY -> {
                var length = reader.readInt32();
                var dictionaryReader = reader.readStream(length);
                var count = dictionaryReader.readInt32();
                var map = new HashMap<String, XPCObject>();
                for (int index = 0; index < count; index++) {
                    var key = dictionaryReader.readDictionaryKey();
                    var value = from(dictionaryReader);
                    map.put(key, value);
                }
                yield new XPCDictionary(map);
            }
            case XPC_ERROR -> null;
            default -> throw new XPCUnimplementedException(type.name());
        };
    }

    static byte[] bytes(XPCObject object) {
        return null;
    }
}
