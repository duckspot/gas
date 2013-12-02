package com.duckspot.gas;

import java.util.Enumeration;

class ArrayEnumeration<T> implements Enumeration {
        
    T[] array;
    int index;

    ArrayEnumeration(T[] array) {
        this.array = array;            
    }        

    @Override
    public boolean hasMoreElements() {
        return index < array.length;
    }

    @Override
    public T nextElement() {
        return array[index++];
    }
}
