package ca.vijaysharma.apple.xpc;

import java.time.Instant;

public record XPCDate(Instant value) implements XPCObject {
    @Override
    public XPCType type() {
        return XPCType.XPC_DATE;
    }
}
