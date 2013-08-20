package com.skplanet.rakeflurry.util;

public class Right {
    
    private static Boolean owned = false;
    public synchronized static Boolean ownRight() {
        if(!owned) {
            owned = true;
            return true;
        }
        return false;
    }
    public synchronized static void releaseRight() {
        owned = false;
    }

}
