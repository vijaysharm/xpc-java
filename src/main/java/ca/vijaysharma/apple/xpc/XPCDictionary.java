package ca.vijaysharma.apple.xpc;

import java.util.Map;

import static ca.vijaysharma.apple.xpc.XPCType.XPC_DICTIONARY;

public record XPCDictionary(Map<String, XPCObject> value) implements XPCObject {
    @Override
    public XPCType type() {
        return XPC_DICTIONARY;
    }
}
