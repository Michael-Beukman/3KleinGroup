package com.sd.a3kleingroup;

import com.sd.a3kleingroup.classes.db.dbAgreement;
import com.sd.a3kleingroup.classes.db.dbFile;
import com.sd.a3kleingroup.classes.db.dbUser;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert.*;
public class dbClassesTest {

    @Test
    public void testDBAgreement(){
        String fileID = "testFileID";
        String userID = "testUserID";
        String owner="owner";
        Date ValidUntil = new Date();
        dbAgreement ag = new dbAgreement(
                fileID, userID, ValidUntil, owner
        );
        Map<String, Object>x =                 new HashMap<String,Object>();
        x.put("fileID", fileID);
        x.put("userID", userID);
        x.put("validUntil", ValidUntil);
        x.put("ownerID", owner);
        Assert.assertEquals(x
                ,ag.getHashmap());
    }

    @Test
    public void testDBFile(){
        String userID = "tstUser";
        String filepath = userID + "/" + "tmpFilename";
        String name = "filename";
        String url = "url";
        dbFile f = new dbFile(filepath, name, userID, url);
        Assert.assertEquals(new HashMap<String, Object>(){{
            put("userID", userID);
            put("filepath", filepath);
            put("filename", name);
            put("storageURL", url);
        }},  f.getHashmap());
    }

    @Test
    public void testDBUser(){
        String email="email", name="name";

        dbUser user = new dbUser(email, name);
        Assert.assertEquals(new HashMap<String, Object>(){{
            put("email", email);
            put("name", name);
            put("notificationToken",null);
        }}, user.getHashmap());
    }
}
