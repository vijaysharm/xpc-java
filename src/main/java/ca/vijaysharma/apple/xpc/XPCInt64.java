package ca.vijaysharma.apple.xpc;

public record XPCInt64(long value) implements XPCObject {
    @Override
    public XPCType type() {
        return XPCType.XPC_INT64;
    }
}
