package ca.vijaysharma.apple.xpc;

public record XPCData(byte[] value) implements XPCObject {
    @Override
    public XPCType type() {
        return XPCType.XPC_DATA;
    }
}
