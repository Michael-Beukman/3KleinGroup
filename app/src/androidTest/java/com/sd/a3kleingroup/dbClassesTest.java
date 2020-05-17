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
        dbFile f = new dbFile(filepath, name, userID, url, key, "images/jpeg");
        Assert.assertEquals(new HashMap<String, Object>(){{
            put("userID", userID);
            put("filepath", filepath);
            put("filename", name);
            put("storageURL", url);
            put("encryptionKey", key);
            put("fileType", "images/jpeg");

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

    //Welcome to the Matthew zone
    @Test
    public void testDBPublicFileNoEncrypt(){
        String userID = "testUser";
        String fileName = "testName";
        String fileStorage = "testUrl";
        String filePath = "testPath";
        String encryptionKey = " ";
        dbPublicFiles publicFiles = new dbPublicFiles(encryptionKey,fileName,  filePath,fileStorage,  userID);
        Assert.assertEquals(new HashMap<String, Object>(){{
            put("user_id",userID);
            put("file_name",fileName);
            put("file_storage",fileStorage);
            put("file_path",filePath);
            put("encryption_key",encryptionKey);
            put("fileType", dbFile.PDF_TYPE);
        }}, publicFiles.getHashmap());
    }

    //Test instance for other functions
    private dbPublicFiles publicFiles = new dbPublicFiles("encryptionKey","fileName",  "filePath","fileStorage",  "userID" );
    @Test
    public void testDBPublicUserID(){
        String userID = "userID";
        Assert.assertEquals(publicFiles.getUserID(), userID);
    }
    @Test
    public void testDBPublicFileName(){
        String fileName = "fileName";
        Assert.assertEquals(publicFiles.getFileName(), fileName);
    }
    @Test
    public void testDBPublicFileStorage(){
        String fileStorage = "fileStorage";
        Assert.assertEquals(publicFiles.getFileStorage(), fileStorage);
    }
    @Test
    public void testDBPublicFilePath(){
        String filePath = "filePath";
        Assert.assertEquals(publicFiles.getFilePath(), filePath);
    }
    @Test
    public void testDBPublicFileEncryptionKey(){
        String encryptionKey = "encryptionKey";
        Assert.assertEquals(publicFiles.getEncryptionKey(), encryptionKey);
    }

}
