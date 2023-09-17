package ca.vijaysharma.apple.xpc;

public record XPCUInt64(long value) implements XPCObject {
    @Override
    public XPCType type() {
        return XPCType.XPC_UINT64;
    }
}
