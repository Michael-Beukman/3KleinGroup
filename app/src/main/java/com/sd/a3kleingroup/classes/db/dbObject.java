package com.sd.a3kleingroup.classes.db;

import java.util.HashMap;
import java.util.Map;


public interface dbObject {
    /**
     * Get The hasmap that we can easily upload to firebase
     * @return
     */
    public Map<String, Object> getHashmap();
}
