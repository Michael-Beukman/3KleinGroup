package com.sd.a3kleingroup.classes;

import java.util.ArrayList;

public abstract class Filter<T> {
    /**
     * Filters this arr. Changes in place
     * @param arr
     */
    public abstract void filter(ArrayList<T> arr);
}
