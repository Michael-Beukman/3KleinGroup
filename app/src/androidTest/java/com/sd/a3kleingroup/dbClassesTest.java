package com.sd.a3kleingroup;

import com.sd.a3kleingroup.classes.FriendRequest;
import com.sd.a3kleingroup.classes.db.dbAgreement;
import com.sd.a3kleingroup.classes.db.dbFile;
import com.sd.a3kleingroup.classes.db.dbFriends;
import com.sd.a3kleingroup.classes.db.dbPublicFiles;
import com.sd.a3kleingroup.classes.db.dbUser;
import com.sd.a3kleingroup.classes.db.dbUserFriends;

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

        ag.setID("ID");
        Assert.assertEquals("ID", ag.getId());
        Assert.assertEquals(userID, ag.getUserID());
        Assert.assertEquals(ValidUntil, ag.getValidUntil());
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

        Assert.assertEquals(name, user.getName());
        Assert.assertEquals(null, user.getNotificationToken());
        Assert.assertEquals(null, user.getDocID());
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
        Assert.assertEquals(publicFiles.getFilepath(), filePath);
    }
    @Test
    public void testDBPublicFileEncryptionKey(){
        String encryptionKey = "encryptionKey";
        Assert.assertEquals(publicFiles.getEncryptionKey(), encryptionKey);
    }

    @Test
    public void testFriendRequest(){
        FriendRequest r = new FriendRequest();
        Assert.assertEquals(null, r.getPotentialFriend());
        Assert.assertEquals(null, r.getRequestID());

        dbUser u = new dbUser("", "", "", "");
        r.setPotentialFriend(u);
        Assert.assertEquals(u, r.getPotentialFriend());

        r.setRequestID("1");
        Assert.assertEquals("1", r.getRequestID());
    }

    @Test
    public void testDBFriend(){
        String rID = "r", sID = "s";
        boolean accepted = false;
        dbFriends friend = new dbFriends(rID, sID, accepted);

        Assert.assertEquals(
                new HashMap<String, Object>(){{
                    put("recipientID", rID);
                    put("senderID", sID);
                    put("accepted", accepted);
                }}, friend.getHashmap()
        );

    }


    @Test
    public void testDBUserFriends(){
        String rID = "r", sID = "s";
        boolean accepted = false;
        dbUserFriends friend = new dbUserFriends(rID, accepted, sID);
        Assert.assertEquals(rID, friend.getUserID());
        Assert.assertEquals(sID, friend.getFriendID());
        Assert.assertEquals(accepted, friend.getAccepted());
        Assert.assertEquals(
                    new HashMap<String, Object>(){{
                        put("accepted", accepted);
                        put("temp name2", rID);
                        put("temp name3", sID);
                    }}, friend.getHashmap());
    }
}
