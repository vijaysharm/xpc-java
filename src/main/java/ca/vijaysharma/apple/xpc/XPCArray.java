package ca.vijaysharma.apple.xpc;

import java.util.List;
import java.util.Map;

import static ca.vijaysharma.apple.xpc.XPCType.XPC_ARRAY;
import static ca.vijaysharma.apple.xpc.XPCType.XPC_DICTIONARY;

public record XPCArray(List<XPCObject> value) implements XPCObject {
    @Override
    public XPCType type() {
        return XPC_ARRAY;
    }
}
