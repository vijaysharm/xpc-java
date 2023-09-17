package ca.vijaysharma.apple.xpc;

import java.util.UUID;

public record XPCUUID(UUID value) implements XPCObject {
    @Override
    public XPCType type() {
        return XPCType.XPC_UUID;
    }
}
