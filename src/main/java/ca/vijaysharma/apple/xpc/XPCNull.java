package ca.vijaysharma.apple.xpc;

import static ca.vijaysharma.apple.xpc.XPCType.XPC_NULL;

public record XPCNull() implements XPCObject {
    @Override
    public XPCType type() {
        return XPC_NULL;
    }
}
