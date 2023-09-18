package ca.vijaysharma.apple.xpc;

import ca.vijaysharma.apple.xpc.exceptions.XPCException;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static ca.vijaysharma.apple.xpc.XPCType.XPC_DICTIONARY;
import static ca.vijaysharma.apple.xpc.XPCWrapper.XPCFlags.ALWAYS_SET;
import static org.junit.jupiter.api.Assertions.*;

public class XPCWrapperTest {
    @Test
    void can_read_xpc_wrapper_with_no_payload() throws XPCException {
        var bytes = new byte[] {
            (byte) 0x92, (byte) 0x0B, (byte) 0xB0, (byte) 0x29,
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
        };
        var object = XPCWrapper.from(bytes);
        assertEquals(-1, object.messageId);
        assertEquals(1, object.flags.size());
        assertTrue(object.flags.contains(ALWAYS_SET));
        assertNull(object.payload);
    }

    @Test
    void can_read_xpc_wrapper_with_empty_dictionary() throws XPCException {
        var bytes = new byte[] {
            (byte) 0x92, (byte) 0x0B, (byte) 0xB0, (byte) 0x29,
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x14, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x42, (byte) 0x37, (byte) 0x13, (byte) 0x42,
            (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0xF0, (byte) 0x00, (byte) 0x00,
            (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
        };
        var object = XPCWrapper.from(bytes);
        assertEquals(2, object.messageId);
        assertEquals(1, object.flags.size());
        assertTrue(object.flags.contains(ALWAYS_SET));
        assertEquals(XPC_DICTIONARY, object.payload.type());

        assertInstanceOf(XPCDictionary.class, object.payload);
        var dictionary = (XPCDictionary)object.payload;
        assertTrue(dictionary.value().isEmpty());
    }

    @Test
    void can_write_xpc_wrapper_with_empty_dictionary() throws XPCException {
        assertArrayEquals(new byte[]{
            (byte) 0x92, (byte) 0x0B, (byte) 0xB0, (byte) 0x29,
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x14, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x42, (byte) 0x37, (byte) 0x13, (byte) 0x42,
            (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0xF0, (byte) 0x00, (byte) 0x00,
            (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
        }, XPCWrapper.bytes(2, new XPCDictionary(Map.of()), ALWAYS_SET));
    }

    @Test
    void can_write_xpc_wrapper_with_no_payload() throws XPCException {
        assertArrayEquals(new byte[]{
            (byte) 0x92, (byte) 0x0B, (byte) 0xB0, (byte) 0x29,
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
        }, XPCWrapper.flagsOnly(ALWAYS_SET));
    }
}
