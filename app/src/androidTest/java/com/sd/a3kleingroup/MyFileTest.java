package com.sd.a3kleingroup;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.sd.a3kleingroup.classes.MyFile;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class MyFileTest {
    @Test
    public void testFile(){
        MyFile f = new MyFile();
        f.setFilename("a");
        Assert.assertEquals("a", f.getFilename());
    }
}
