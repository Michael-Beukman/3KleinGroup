package com.sd.a3kleingroup;

import com.sd.a3kleingroup.classes.FileModel;
import com.sd.a3kleingroup.classes.db.dbAgreement;
import com.sd.a3kleingroup.classes.db.dbFile;
import com.sd.a3kleingroup.classes.db.dbUser;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class FileModelTest {
    @Test
    public void test(){
        FileModel f = new FileModel();
        dbAgreement ag = new dbAgreement("", "", null, "");
        dbFile file = new dbFile("", "", "", "", "", "");
        dbUser user = new dbUser("1", "", "");
        f.setAgreement(ag);
        f.setFile(file);
        f.setOwner(user);
        Assert.assertEquals(true, f.isAllDataRetrieved());
        Assert.assertEquals(ag, f.getAgreement());
        Assert.assertEquals(file, f.getFile());
        Assert.assertEquals(user, f.getOwner());
    }
}
