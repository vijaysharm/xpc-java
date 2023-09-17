package ca.vijaysharma.apple.xpc;

class XPCUtilities {
    private XPCUtilities() {}

    public static int roundUp(int number, int multiple) {
        var remainder = (number % multiple);
        return remainder == 0 ? number : number + (multiple - remainder);
    }
}
