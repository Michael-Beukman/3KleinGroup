package com.sd.a3kleingroup.classes.db;

import java.util.HashMap;


public interface dbObject {
    /**
     * Get The hasmap that we can easily upload to firebase
     * @return
     */
    public HashMap<String, Object> getHashmap();
}
