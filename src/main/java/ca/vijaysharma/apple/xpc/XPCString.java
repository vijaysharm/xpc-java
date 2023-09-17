package ca.vijaysharma.apple.xpc;

public record XPCString(String value) implements XPCObject {
    @Override
    public XPCType type() {
        return XPCType.XPC_STRING;
    }
}
