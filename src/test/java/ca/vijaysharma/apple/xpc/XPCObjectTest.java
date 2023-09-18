package ca.vijaysharma.apple.xpc;

import ca.vijaysharma.apple.xpc.exceptions.XPCException;
import ca.vijaysharma.apple.xpc.exceptions.XPCInvalidTypeException;
import ca.vijaysharma.apple.xpc.exceptions.XPCInvalidValueException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class XPCObjectTest {
    @Test
    void throws_on_invalid_type() {
        var xpc_invalid_type_bytes = new byte[]{0x10, 0x01, 0x00, 0x00};
        assertThrowsExactly(
                XPCInvalidTypeException.class,
                () -> XPCObject.from(xpc_invalid_type_bytes)
        );
    }

    @Nested
    class xpc_null {
        @Test
        void can_read_xpc_null() throws XPCException {
            var xpc_null_bytes = new byte[]{ 0x00, 0x10, 0x00, 0x00 };
            XPCObject object = XPCObject.from(xpc_null_bytes);
            assertInstanceOf(XPCNull.class, object);
        }
        @Test
        void can_write_xpc_null() throws XPCException {
            byte[] bytes = XPCObject.bytes(new XPCNull());
            assertArrayEquals(new byte[] { 0x00, 0x10, 0x00, 0x00 }, bytes);
        }
    }

    @Nested
    class bool {
        @Test
        void can_read_true() throws XPCException {
            var xpc_bool_true_bytes = new byte[]{
                0x00, 0x20, 0x00, 0x00,
                0x01, 0x00, 0x00, 0x00
            };
            XPCObject object = XPCObject.from(xpc_bool_true_bytes);
            assertInstanceOf(XPCBool.class, object);
            XPCBool bool = (XPCBool) object;
            assertTrue(bool.value());
        }

        @Test
        void can_read_false() throws XPCException {
            var xpc_bool_false_bytes = new byte[]{
                0x00, 0x20, 0x00, 0x00,
                0x01, 0x00, 0x00, 0x00
            };
            XPCObject object = XPCObject.from(xpc_bool_false_bytes);
            assertInstanceOf(XPCBool.class, object);
            XPCBool bool = (XPCBool) object;
            assertTrue(bool.value());
        }

        @Test
        void throws_on_invalid_value() {
            var xpc_bool_invalid_bytes = new byte[]{
                0x00, 0x20, 0x00, 0x00,
                0x02, 0x00, 0x00, 0x00
            };

            assertThrowsExactly(
                    XPCInvalidValueException.class,
                    () -> XPCObject.from(xpc_bool_invalid_bytes)
            );
        }

        @Test
        void can_write_true() throws XPCException {
            assertArrayEquals(new byte[]{
                0x00, 0x20, 0x00, 0x00,
                0x01, 0x00, 0x00, 0x00
            }, XPCObject.bytes(new XPCBool(true)));
        }

        @Test
        void can_write_false() throws XPCException {
            assertArrayEquals(new byte[]{
                0x00, 0x20, 0x00, 0x00,
                0x01, 0x00, 0x00, 0x00
            }, XPCObject.bytes(new XPCBool(true)));
        }
    }

    @Nested
    class int64 {
        @Test
        void can_read_negative() throws XPCException {
            var bytes = new byte[] {
                (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x00,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
            };
            XPCObject object = XPCObject.from(bytes);
            assertInstanceOf(XPCInt64.class, object);
            XPCInt64 int64 = (XPCInt64) object;
            assertEquals(-1, int64.value());
        }

        @Test
        void can_write_negative() throws XPCException {
            assertArrayEquals(new byte[]{
                (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x00,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
            }, XPCObject.bytes(new XPCInt64(-1)));
        }

        @Test
        void can_read_positive() throws XPCException {
            var bytes = new byte[] {
                (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x00,
                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            };
            XPCObject object = XPCObject.from(bytes);
            assertInstanceOf(XPCInt64.class, object);
            XPCInt64 int64 = (XPCInt64) object;
            assertEquals(2, int64.value());
        }

        @Test
        void can_write_positive() throws XPCException {
            assertArrayEquals(new byte[]{
                (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x00,
                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            }, XPCObject.bytes(new XPCInt64(2)));
        }

        @Test
        void can_read_zero() throws XPCException {
            var bytes = new byte[] {
                (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            };
            XPCObject object = XPCObject.from(bytes);
            assertInstanceOf(XPCInt64.class, object);
            XPCInt64 int64 = (XPCInt64) object;
            assertEquals(0, int64.value());
        }

        @Test
        void can_write_zero() throws XPCException {
            assertArrayEquals(new byte[]{
                (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            }, XPCObject.bytes(new XPCInt64(0)));
        }
    }

    @Nested
    class uint64 {
        @Test
        void cannot_read_negative() {
            var bytes = new byte[] {
                (byte) 0x00, (byte) 0x40, (byte) 0x00, (byte) 0x00,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
            };

            assertThrowsExactly(
                XPCInvalidValueException.class,
                () -> XPCObject.from(bytes)
            );
        }

        @Test
        void can_read_positive() throws XPCException {
            var bytes = new byte[] {
                (byte) 0x00, (byte) 0x40, (byte) 0x00, (byte) 0x00,
                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            };
            XPCObject object = XPCObject.from(bytes);
            assertInstanceOf(XPCUInt64.class, object);
            XPCUInt64 uint64 = (XPCUInt64) object;
            assertEquals(2, uint64.value());
        }

        @Test
        void can_read_zero() throws XPCException {
            var bytes = new byte[] {
                (byte) 0x00, (byte) 0x40, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            };
            XPCObject object = XPCObject.from(bytes);
            assertInstanceOf(XPCUInt64.class, object);
            XPCUInt64 uint64 = (XPCUInt64) object;
            assertEquals(0, uint64.value());
        }
    }


    @Nested
    class xpc_double {
        @Test
        void can_read_negative() throws XPCException {
            var bytes = new byte[] {
                (byte) 0x00, (byte) 0x50, (byte) 0x00, (byte) 0x00,
                (byte) 0xBF, (byte) 0xF3, (byte) 0x33, (byte) 0x33,
                (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33
            };
            XPCObject object = XPCObject.from(bytes);
            assertInstanceOf(XPCDouble.class, object);
            XPCDouble doubleValue = (XPCDouble) object;
            assertEquals(-1.2, doubleValue.value());
        }

        @Test
        void can_write_negative() throws XPCException {
            assertArrayEquals(new byte[]{
                (byte) 0x00, (byte) 0x50, (byte) 0x00, (byte) 0x00,
                (byte) 0xBF, (byte) 0xF3, (byte) 0x33, (byte) 0x33,
                (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33
            }, XPCObject.bytes(new XPCDouble(-1.2)));
        }

        @Test
        void can_read_positive() throws XPCException {
            var bytes = new byte[] {
                (byte) 0x00, (byte) 0x50, (byte) 0x00, (byte) 0x00,
                (byte) 0x40, (byte) 0xF, (byte) 0x33, (byte) 0x33,
                (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33
            };
            XPCObject object = XPCObject.from(bytes);
            assertInstanceOf(XPCDouble.class, object);
            XPCDouble doubleValue = (XPCDouble) object;
            assertEquals(3.9, doubleValue.value());
        }

        @Test
        void can_write_positive() throws XPCException {
            assertArrayEquals(new byte[]{
                (byte) 0x00, (byte) 0x50, (byte) 0x00, (byte) 0x00,
                (byte) 0x40, (byte) 0xF, (byte) 0x33, (byte) 0x33,
                (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33
            }, XPCObject.bytes(new XPCDouble(3.9)));
        }

        @Test
        void can_read_zero() throws XPCException {
            var bytes = new byte[] {
                (byte) 0x00, (byte) 0x50, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            };
            XPCObject object = XPCObject.from(bytes);
            assertInstanceOf(XPCDouble.class, object);
            XPCDouble doubleValue = (XPCDouble) object;
            assertEquals(0, doubleValue.value());
        }

        @Test
        void can_write_zero() throws XPCException {
            assertArrayEquals(new byte[]{
                (byte) 0x00, (byte) 0x50, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            }, XPCObject.bytes(new XPCDouble(0)));
        }
    }

    @Nested
    class date {
        @Test
        void can_read_date() throws XPCException {
            var bytes = new byte[] {
                (byte) 0x00, (byte) 0x70, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0xFA, (byte) 0xF8, (byte) 0x00,
                (byte) 0x75, (byte) 0xBD, (byte) 0x85, (byte) 0x17,
            };

            var instant = Instant.ofEpochMilli(TimeUnit.SECONDS.toMillis(1694969145));

            XPCObject object = XPCObject.from(bytes);
            assertInstanceOf(XPCDate.class, object);
            XPCDate date = (XPCDate) object;
            assertEquals(instant, date.value());
        }

        @Test
        void can_write_date() throws XPCException {
            var instant = Instant.ofEpochMilli(TimeUnit.SECONDS.toMillis(1694969145));
            assertArrayEquals(new byte[]{
                (byte) 0x00, (byte) 0x70, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0xFA, (byte) 0xF8, (byte) 0x00,
                (byte) 0x75, (byte) 0xBD, (byte) 0x85, (byte) 0x17,
            }, XPCObject.bytes(new XPCDate(instant)));
        }
    }

    @Nested
    class data {
        @Test
        void can_read_data() throws XPCException {
            var bytes = new byte[] {
                (byte) 0x00, (byte) 0x80, (byte) 0x00, (byte) 0x00,
                (byte) 0x02, (byte) 0x00, (byte) 0x0, (byte) 0x00,
                (byte) 0x23, (byte) 0x32, (byte) 0x0, (byte) 0x00,
            };

            XPCObject object = XPCObject.from(bytes);
            assertInstanceOf(XPCData.class, object);
            XPCData data = (XPCData) object;
            assertArrayEquals(new byte[] { (byte) 0x23, (byte) 0x32 }, data.value());
        }

        @Test
        void can_write_data() throws XPCException {
            assertArrayEquals(new byte[]{
                (byte) 0x00, (byte) 0x80, (byte) 0x00, (byte) 0x00,
                (byte) 0x02, (byte) 0x00, (byte) 0x0, (byte) 0x00,
                (byte) 0x23, (byte) 0x32, (byte) 0x0, (byte) 0x00,
            }, XPCObject.bytes(new XPCData(new byte[] { (byte) 0x23, (byte) 0x32 })));
        }
    }

    @Nested
    class string {
        @Test
        void can_read_string() throws XPCException {
            var bytes = new byte[] {
                (byte) 0x00, (byte) 0x90, (byte) 0x00, (byte) 0x00,
                (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x68, (byte) 0x65, (byte) 0x6C, (byte) 0x6C,
                (byte) 0x6F, (byte) 0x00, (byte) 0x00, (byte) 0x00
            };

            XPCObject object = XPCObject.from(bytes);
            assertInstanceOf(XPCString.class, object);
            XPCString string = (XPCString) object;
            assertEquals("hello", string.value());
        }

        @Test
        void can_write_string() throws XPCException {
            assertArrayEquals(new byte[]{
                (byte) 0x00, (byte) 0x90, (byte) 0x00, (byte) 0x00,
                (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x68, (byte) 0x65, (byte) 0x6C, (byte) 0x6C,
                (byte) 0x6F, (byte) 0x00, (byte) 0x00, (byte) 0x00
            }, XPCObject.bytes(new XPCString("hello")));
        }
    }

    @Nested
    class uuid {
        @Test
        void can_read_uuid() throws XPCException {
            var bytes = new byte[] {
                (byte) 0x00, (byte) 0xA0, (byte) 0x00, (byte) 0x00,
                (byte) 0xBD, (byte) 0x9C, (byte) 0x7F, (byte) 0x32,
                (byte) 0x80, (byte) 0x10, (byte) 0x4C, (byte) 0xFE,
                (byte) 0x97, (byte) 0xC0, (byte) 0x82, (byte) 0x37,
                (byte) 0x1E, (byte) 0x32, (byte) 0x76, (byte) 0xFA,
            };

            XPCObject object = XPCObject.from(bytes);
            assertInstanceOf(XPCUUID.class, object);
            XPCUUID uuid = (XPCUUID) object;
            assertEquals(UUID.fromString("bd9c7f32-8010-4cfe-97c0-82371e3276fa"), uuid.value());
        }

        @Test
        void can_write_uuid() throws XPCException {
            assertArrayEquals(new byte[]{
                (byte) 0x00, (byte) 0xA0, (byte) 0x00, (byte) 0x00,
                (byte) 0xBD, (byte) 0x9C, (byte) 0x7F, (byte) 0x32,
                (byte) 0x80, (byte) 0x10, (byte) 0x4C, (byte) 0xFE,
                (byte) 0x97, (byte) 0xC0, (byte) 0x82, (byte) 0x37,
                (byte) 0x1E, (byte) 0x32, (byte) 0x76, (byte) 0xFA,
            }, XPCObject.bytes(new XPCUUID(UUID.fromString("bd9c7f32-8010-4cfe-97c0-82371e3276fa"))));
        }
    }

    @Nested
    class array {
        @Test
        void can_read_array() throws XPCException {
            byte[] bytes = {
                (byte) 0x00, (byte) 0xe0, (byte) 0x00, (byte) 0x00,
                (byte) 0x24, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x90, (byte) 0x00, (byte) 0x00,
                (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x68, (byte) 0x65, (byte) 0x6C, (byte) 0x6C,
                (byte) 0x6F, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x90, (byte) 0x00, (byte) 0x00,
                (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x77, (byte) 0x6F, (byte) 0x72, (byte) 0x6C,
                (byte) 0x64, (byte) 0x00, (byte) 0x00, (byte) 0x00
            };

            XPCObject object = XPCObject.from(bytes);
            assertInstanceOf(XPCArray.class, object);
            XPCArray array = (XPCArray) object;
            assertEquals(2, array.value().size());

            assertInstanceOf(XPCString.class, array.value().get(0));
            var item0 = (XPCString)array.value().get(0);
            assertEquals("hello", item0.value());

            assertInstanceOf(XPCString.class, array.value().get(1));
            var item1 = (XPCString)array.value().get(1);
            assertEquals("world", item1.value());
        }

        @Test
        void can_write_array() throws XPCException {
            assertArrayEquals(new byte[]{
                (byte) 0x00, (byte) 0xe0, (byte) 0x00, (byte) 0x00,
                (byte) 0x24, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x90, (byte) 0x00, (byte) 0x00,
                (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x68, (byte) 0x65, (byte) 0x6C, (byte) 0x6C,
                (byte) 0x6F, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x90, (byte) 0x00, (byte) 0x00,
                (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x77, (byte) 0x6F, (byte) 0x72, (byte) 0x6C,
                (byte) 0x64, (byte) 0x00, (byte) 0x00, (byte) 0x00
            }, XPCObject.bytes(new XPCArray(
                List.of(
                    new XPCString("hello"),
                    new XPCString("world")
                )
            )));
        }
    }
    @Nested
    class dictionary {
        @Test
        void can_read_dictionary() throws XPCException {
            byte[] bytes = {
                (byte) 0x00, (byte) 0xf0, (byte) 0x00, (byte) 0x00,
                (byte) 0x1c, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x68, (byte) 0x65, (byte) 0x6C, (byte) 0x6C,
                (byte) 0x6F, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x90, (byte) 0x00, (byte) 0x00,
                (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x77, (byte) 0x6F, (byte) 0x72, (byte) 0x6C,
                (byte) 0x64, (byte) 0x00, (byte) 0x00, (byte) 0x00
            };

            XPCObject object = XPCObject.from(bytes);
            assertInstanceOf(XPCDictionary.class, object);
            XPCDictionary dictionary = (XPCDictionary) object;
            assertEquals(1, dictionary.value().size());
            assertTrue(dictionary.value().containsKey("hello"));

            assertInstanceOf(XPCString.class, dictionary.value().get("hello"));
            var value = (XPCString)dictionary.value().get("hello");
            assertEquals("world", value.value());
        }

        @Test
        void can_write_dictionary() throws XPCException {
            assertArrayEquals(new byte[]{
                (byte) 0x00, (byte) 0xf0, (byte) 0x00, (byte) 0x00,
                (byte) 0x1c, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x68, (byte) 0x65, (byte) 0x6C, (byte) 0x6C,
                (byte) 0x6F, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x90, (byte) 0x00, (byte) 0x00,
                (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x77, (byte) 0x6F, (byte) 0x72, (byte) 0x6C,
                (byte) 0x64, (byte) 0x00, (byte) 0x00, (byte) 0x00
            }, XPCObject.bytes(new XPCDictionary(
                Map.of("hello", new XPCString("world"))
            )));
        }
    }
}