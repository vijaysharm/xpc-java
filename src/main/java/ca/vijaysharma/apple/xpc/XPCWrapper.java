package ca.vijaysharma.apple.xpc;

import ca.vijaysharma.apple.xpc.exceptions.XPCException;
import ca.vijaysharma.apple.xpc.exceptions.XPCInvalidValueException;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class XPCWrapper {
    public enum XPCFlags {
        ALWAYS_SET(0x00000001),
        PINT(0x00000002),
        DATA_PRESENT(0x00000100),
        WANTING_REPLY(0x00010000),
        REPLY(0x00020000),
        FILE_TX_STREAM_REQUEST(0x00100000),
        FILE_TX_STREAM_RESPONSE(0x00200000),
        INIT_HANDSHAKE(0x00400000),
        UNDEFINED(0x0201),
        ;

        public final int value;

        XPCFlags(int value) {
            this.value = value;
        }

        public static int or(XPCFlags ...flags) {
            return or(List.of(flags));
        }

        public static int or(Collection<XPCFlags> flags) {
            int result = 0;
            for (var flag : flags) {
                result |= flag.value;
            }

            return result;
        }

        public static Set<XPCFlags> flags(int input) {
            var result = new HashSet<XPCFlags>();
            for (var flag : XPCFlags.values()) {
                if ((input & flag.value) == flag.value) {
                    result.add(flag);
                }
            }
            return result;
        }
    }

    private static final int WRAPPER_MAGIC = 0x29B00B92;
    private static final int PAYLOAD_MAGIC = 0x42133742;
    private static final int PAYLOAD_PROTOCOL_VERSION = 0x00000005;

    public final Set<XPCFlags> flags;
    public final long messageId;
    public final XPCDictionary payload;

    public XPCWrapper(long messageId, Set<XPCFlags> flags, XPCDictionary payload) {
        this.flags = flags;
        this.messageId = messageId;
        this.payload = payload;
    }

    public static XPCWrapper from(byte[] bytes) throws XPCException {
        var reader = new XPCStreamReader(bytes);
        var magic = reader.readInt32();
        if (magic != WRAPPER_MAGIC) {
            throw new XPCInvalidValueException("Unexpected XPC Wrapper magic value " + magic);
        }

        var flags = reader.readInt32();
        var length = reader.readInt64();
        if (length == 0) {
            return new XPCWrapper(-1, XPCFlags.flags(flags), null);
        }
        var message = new XPCStreamReader(reader.readBytes((int)length + 8));
        var messageId = message.readInt64();
        var payload = new XPCStreamReader(message.readBytes((int)length));
        var payLoadMagic = payload.readInt32();
        if (payLoadMagic != PAYLOAD_MAGIC) {
            throw new XPCInvalidValueException("Unexpected XPC Payload magic value " + payLoadMagic);
        }

        var protocolVersion = payload.readInt32();
        if (protocolVersion != PAYLOAD_PROTOCOL_VERSION) {
            throw new XPCInvalidValueException("Unsupported XPC Payload protocol version " + protocolVersion);
        }
        var obj = XPCObject.from(payload.readBytes((int)length - 8));
        if (obj.type() != XPCType.XPC_DICTIONARY) {
            throw new XPCInvalidValueException("Expected XPC Dictionary as payload" + obj.type().name());
        }
        return new XPCWrapper(messageId, XPCFlags.flags(flags), (XPCDictionary) obj);
    }

    public static byte[] flagsOnly(XPCFlags...flags) throws XPCException {
        return bytes(-1, null, flags);
    }

    public static byte[] bytes(long messageId, XPCDictionary payload, XPCFlags...flags) throws XPCException {
        var writer = new XPCStreamWriter();
        writer.writeInt32(WRAPPER_MAGIC);
        writer.writeInt32(XPCFlags.or(flags));

        if (payload == null) {
            writer.writeInt64(0);
            writer.writeInt64(0);
            return writer.toByteArray();
        }

        var payloadWriter = new XPCStreamWriter();
        payloadWriter.writeInt32(PAYLOAD_MAGIC);
        payloadWriter.writeInt32(PAYLOAD_PROTOCOL_VERSION);
        payloadWriter.writeBytes(XPCObject.bytes(payload));

        var messageWriter = new XPCStreamWriter();
        messageWriter.writeInt64(messageId);
        messageWriter.writeBytes(payloadWriter.toByteArray());

        var message = messageWriter.toByteArray();
        writer.writeInt64(message.length - 8);
        writer.writeBytes(message);

        return writer.toByteArray();
    }
}
