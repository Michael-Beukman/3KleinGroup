package com.sd.a3kleingroup;

import com.sd.a3kleingroup.classes.db.dbAgreement;
import com.sd.a3kleingroup.classes.db.dbFile;
import com.sd.a3kleingroup.classes.db.dbPublicFiles;
import com.sd.a3kleingroup.classes.db.dbUser;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@RunWith(JUnit4.class)
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
        String key = "key";
        dbFile f = new dbFile(filepath, name, userID, url, key);
        Assert.assertEquals(new HashMap<String, Object>(){{
            put("userID", userID);
            put("filepath", filepath);
            put("filename", name);
            put("storageURL", url);
            put("encryptionKey", key);
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

    @Test
    public void testDBPublicFileNoEncrypt(){
        String userID = "testUser";
        String fileName = "testName";
        String fileStorage = "testUrl";
        String filePath = "testPath";
        String encryptionKey = " ";
        dbPublicFiles publicFiles = new dbPublicFiles(userID, fileName, fileStorage, filePath, encryptionKey);
        Assert.assertEquals(new HashMap<String, Object>(){{
            put("user_id",userID);
            put("file_name",fileName);
            put("file_storage",fileStorage);
            put("file_path",filePath);
            put("encryptionKey",encryptionKey);
        }}, publicFiles.getHashmap());
    }

}
