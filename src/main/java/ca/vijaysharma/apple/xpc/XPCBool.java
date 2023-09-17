package ca.vijaysharma.apple.xpc;

public record XPCBool(boolean value) implements XPCObject {
    @Override
    public XPCType type() {
        return XPCType.XPC_BOOL;
    }
}
