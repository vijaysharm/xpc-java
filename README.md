# xpc-java
A small java library that reads and writes Apple's XPC models to and from bytes

## Usage

Creating a XPC Dictionary
```java
var bytes = XPCObject.bytes(new XPCDictionary(
    Map.of("hello", new XPCString("world"))
);

// Output:
// var bytes = new byte[]{
//    (byte) 0x00, (byte) 0xf0, (byte) 0x00, (byte) 0x00,
//    (byte) 0x1c, (byte) 0x00, (byte) 0x00, (byte) 0x00,
//    (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
//    (byte) 0x68, (byte) 0x65, (byte) 0x6C, (byte) 0x6C,
//    (byte) 0x6F, (byte) 0x00, (byte) 0x00, (byte) 0x00,
//    (byte) 0x00, (byte) 0x90, (byte) 0x00, (byte) 0x00,
//    (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00,
//    (byte) 0x77, (byte) 0x6F, (byte) 0x72, (byte) 0x6C,
//    (byte) 0x64, (byte) 0x00, (byte) 0x00, (byte) 0x00
// }
```

Reading binary data for an XPC Dictionary
```java
var object = XPCObject.from({
    (byte) 0x00, (byte) 0xf0, (byte) 0x00, (byte) 0x00,
    (byte) 0x1c, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x68, (byte) 0x65, (byte) 0x6C, (byte) 0x6C,
    (byte) 0x6F, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x90, (byte) 0x00, (byte) 0x00,
    (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x77, (byte) 0x6F, (byte) 0x72, (byte) 0x6C,
    (byte) 0x64, (byte) 0x00, (byte) 0x00, (byte) 0x00
});

// Output: 
// object = XPCDictionary(
//    Map.of("hello", new XPCString("world"))
// );
```