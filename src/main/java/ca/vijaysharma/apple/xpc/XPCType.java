package ca.vijaysharma.apple.xpc;

import ca.vijaysharma.apple.xpc.exceptions.XPCInvalidTypeException;

public enum XPCType {
    XPC_NULL(0x00001000),
    XPC_BOOL(0x00002000),
    XPC_INT64(0x00003000),
    XPC_UINT64(0x00004000),
    XPC_DOUBLE(0x00005000),
    XPC_POINTER(0x00006000),
    XPC_DATE(0x00007000),
    XPC_DATA(0x00008000),
    XPC_STRING(0x00009000),
    XPC_UUID(0x0000a000),
    XPC_FD(0x0000b000),
    XPC_SHMEM(0x0000c000),
    XPC_MACH_SEND(0x0000d000),
    XPC_ARRAY(0x0000e000),
    XPC_DICTIONARY(0x0000f000),
    XPC_ERROR(0x00010000),
    XPC_CONNECTION(0x00011000),
    XPC_ENDPOINT(0x00012000),
    XPC_SERIALIZER(0x00013000),
    XPC_PIPE(0x00014000),
    XPC_MACH_RECV(0x00015000),
    XPC_BUNDLE(0x00016000),
    XPC_SERVICE(0x00017000),
    XPC_SERVICE_INSTANCE(0x00018000),
    XPC_ACTIVITY(0x00019000),
    XPC_FILE_TRANSFER(0x0001a000);
    public final int value;
    XPCType(int value) {
        this.value = value;
    }

    static XPCType from(int value) throws XPCInvalidTypeException {
        for (var type : values()) {
            if ((value & type.value) == value) {
                return type;
            }
        }

        throw new XPCInvalidTypeException("Unknown XCPType value = " + value);
    }
}
