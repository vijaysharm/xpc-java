package ca.vijaysharma.apple.xpc;

import ca.vijaysharma.apple.xpc.exceptions.XPCException;
import ca.vijaysharma.apple.xpc.exceptions.XPCInvalidValueException;
import ca.vijaysharma.apple.xpc.exceptions.XPCUnimplementedException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static ca.vijaysharma.apple.xpc.XPCUtilities.roundUp;
import static java.nio.charset.StandardCharsets.UTF_8;

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

    static byte[] bytes(XPCObject object) throws XPCException {
        var stream = new XPCStreamWriter();
        put(object, stream);
        return stream.toByteArray();
    }

    private static void put(XPCObject object, XPCStreamWriter stream) throws XPCException {
        var type = object.type();
        switch (type) {
            case XPC_NULL -> {
                stream.write(type);
            }
            case XPC_BOOL -> {
                var typed = (XPCBool)object;
                stream.write(type);
                stream.writeInt32(typed.value() ? 1 : 0);
            }
            case XPC_INT64 -> {
                var typed = (XPCInt64)object;
                stream.write(type);
                stream.writeInt64(typed.value());
            }
            case XPC_UINT64 -> {
                var typed = (XPCUInt64)object;
                if (typed.value() < 0) {
                    throw new XPCInvalidValueException("XPCUInt64 cannot be negative (" + typed.value() + ")");
                }
                stream.write(type);
                stream.writeInt64(typed.value());
            }
            case XPC_DOUBLE -> {
                var typed = (XPCDouble)object;
                stream.write(type);
                stream.writeDouble(typed.value());
            }
            case XPC_DATE -> {
                var typed = (XPCDate)object;
                var seconds = typed.value().getEpochSecond();
                var nano = TimeUnit.SECONDS.toNanos(seconds);
                stream.write(type);
                stream.writeInt64(nano);
            }
            case XPC_DATA -> {
                var typed = (XPCData)object;
                var data = typed.value();
                var buf = alignedData(data, data.length);
                stream.write(type);
                stream.writeInt32(data.length);
                stream.writeBytes(buf.array());
            }
            case XPC_STRING -> {
                var typed = (XPCString)object;
                var string = typed.value();
                int length = string.length();
                var buf = alignedData(string.getBytes(UTF_8), length + 1);
                stream.write(type);
                stream.writeInt32(length + 1);
                stream.writeBytes(buf.array());
            }
            case XPC_UUID -> {
                var typed = (XPCUUID)object;
                var uuid = typed.value();
                var buf = ByteBuffer.allocate(16)
                    .order(ByteOrder.BIG_ENDIAN)
                    .putLong(uuid.getMostSignificantBits())
                    .putLong(uuid.getLeastSignificantBits());
                stream.write(type);
                stream.writeBytes(buf.array());
            }
            case XPC_ARRAY -> {
                var typed = (XPCArray)object;
                var values = typed.value();
                var byteArrays = new ArrayList<byte[]>();
                for (var item : values) {
                    var itemBuffer = new XPCStreamWriter();
                    put(item, itemBuffer);
                    byteArrays.add(itemBuffer.toByteArray());
                }
                var entries = new XPCStreamWriter();
                entries.writeInt32(byteArrays.size());
                for (var bytes : byteArrays) {
                    entries.writeBytes(bytes);
                }
                var entriesArray = entries.toByteArray();

                stream.write(type);
                stream.writeInt32(entriesArray.length);
                stream.writeBytes(entriesArray);
            }
            case XPC_DICTIONARY -> {
                var typed = (XPCDictionary)object;
                var map = typed.value();
                var entries = new XPCStreamWriter();
                for (var entry : map.entrySet()) {
                    var key = entry.getKey();
                    var buf = alignedData(key.getBytes(UTF_8), key.length() + 1);

                    var valueBuffer = new XPCStreamWriter();
                    put(entry.getValue(), valueBuffer);
                    entries.writeBytes(buf.array());
                    entries.writeBytes(valueBuffer.toByteArray());
                }

                var dictionaryWriter = new XPCStreamWriter();
                dictionaryWriter.writeInt32(map.size());
                dictionaryWriter.writeBytes(entries.toByteArray());

                var dictionaryByteArray = dictionaryWriter.toByteArray();

                stream.write(type);
                stream.writeInt32(dictionaryByteArray.length);
                stream.writeBytes(dictionaryByteArray);
            }
            default -> throw new XPCUnimplementedException(type.name());
        }
    }

    private static ByteBuffer alignedData(byte[] data, int length) {
        var alignedLength = roundUp(length, 4);
        var zeros = new byte[alignedLength - data.length];
        Arrays.fill(zeros, (byte) 0x00);
        return ByteBuffer.allocate(alignedLength)
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(data)
                .put(zeros);
    }
}
