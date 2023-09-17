package ca.vijaysharma.apple.xpc;

public record XPCDouble(double value) implements XPCObject {
    @Override
    public XPCType type() {
        return XPCType.XPC_DOUBLE;
    }
}
