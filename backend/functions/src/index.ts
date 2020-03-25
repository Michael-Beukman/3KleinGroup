import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

admin.initializeApp(functions.config().firebase);
// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript
//
export const helloWorld = functions.https.onRequest((request, response) => {
  response.send("Hello from Firebase! " + JSON.stringify(request));
});

export const helloWorlds = () => {
  return "Hello World!";
};

exports.requestPermission = functions.https.onCall((data, context) => {
  const userName = data.userName;
  const fileName = data.fileName;
  const ownerNotificationToken = data.ownerToken;

  const payload = {
    notification: {
      title: "Someone wants to view your file",
      body: `${userName} requests to view ${fileName}`
    }
  };
  console.log("payload ", { payload });

  admin.messaging()
        .sendToDevice(ownerNotificationToken, payload)
        .then((response) => {
          // Response is a message ID string.
          console.log('Successfully sent message:', response);
        })
        .catch((error) => {
          console.log('Error sending message:', error);
        });
});

exports.createAgreement = functions.firestore
  .document("Agreements/{agreementID}")
  .onCreate((snap, context) => {
    console.log("starting onCreate");
    // get the data from the agreement

    // Get an object representing the document
    // e.g. {'fileID': '..', 'userID': '...', validUntil: ...}
    const newValue = snap.data();
    console.log("got data from snap ", { newValue });
    // the user that should get the notification
    const userID = newValue.userID;
    console.log("got user id ", { userID });
    const db = admin.firestore();

    // the user that receives the file
    const docUser = db.collection("Users").doc(userID);
    // the file itself
    const docFile = db.collection("Files").doc(newValue.fileID);
    const docOwner = db.collection("Users").doc(newValue.ownerID);

    admin
      .firestore()
      .getAll(docUser, docFile, docOwner)
      .then((docs) => {
        const userData = docs[0].data();
        const fileData = docs[1].data();
        const ownerData = docs[2].data();
        console.log("Got data from user, file and owner ", {
          userData,
          fileData,
          ownerData
        });

        const userNotifToken = userData.notificationToken;
        const ownerName = ownerData.name;
        const fileName = fileData.filename;

        const notifToken = userNotifToken
        
        // Notification details.
        console.log("got snapshot from users notifToken ", { notifToken });
        const payload = {
          notification: {
            title: "New File received!",
            body: `You received ${fileName} from ${ownerName}`
          }
        };
        console.log("payload ", { payload });

        // Listing all tokens as an array.
        // tokens = Object.keys(tokensSnapshot.val());
        // Send notifications to all tokens.
        admin
          .messaging()
          .sendToDevice(notifToken, payload)
          .then((r) => {
            console.log("error1", r);
            console.log("sent message");
          })
          .catch((xx) => {
            console.log("error4", xx);
          });
      })
      .catch((error) => {
        console.log(
          "We got an error at getAll in the createAgreement function. ",
          { error }
        );
      });

    // Below is the old code, where the notification was a little bit ugly.
    /*
    db.collection("Users")
      .doc(userID)
      .get()
      .then((snapshot) => {
        console.log(
          "got snapshot from users ",
          { snapshot },
          { data: snapshot.data() }
        );
        const notifToken = snapshot.data().notificationToken;
        // Notification details.
        console.log("got snapshot from users notiftoekn ", { notifToken });
        const payload = {
          notification: {
            title: "New File received!",
            body: `File id is: ${newValue.fileID}`
          }
        };
        console.log("payload ", { payload });

        // Listing all tokens as an array.
        // tokens = Object.keys(tokensSnapshot.val());
        // Send notifications to all tokens.
        admin
          .messaging()
          .sendToDevice(notifToken, payload)
          .then((r) => {
            console.log("error1", r);
            console.log("sent message");
          })
          .catch((xx) => {
            console.log("error4", xx);
          });
      })
      .catch((e) => {
        console.log("error2", e);
      })
      .catch((x) => {
        // todo
        console.log("error3", x);
      });

    // perform desired operations ...
  */
    console.log("done");
  });

exports.changeAgreement = functions.firestore
  .document("Agreements/{agreementID}")
  .onUpdate((change, ctx) => {
    function addDays(date, days) {
      var result = new Date(date);
      result.setDate(result.getDate() + days);
      return result;
    }
    const data = change.after.data();
    console.log(data, "change data");
    const timestamp: Date = data.validUntil.toDate();
    const now = new Date();
    // need to get file and user;
    const fileID = data.fileID;
    const userID = data.userID;
    const ownerID = data.ownerID;

    const db = admin.firestore();
    const docUser1 = db.collection("Users").doc(userID);
    const docUserOwner = db.collection("Users").doc(ownerID);
    const docFile = db.collection("Files").doc(fileID);
    admin
      .firestore()
      .getAll(docUser1, docUserOwner, docFile)
      .then((docs) => {
        const userDoc = docs[0];
        const ownerDoc = docs[1];
        const fileDoc = docs[2];
        const userNotif = userDoc.data().notificationToken;
        const ownerName = ownerDoc.data().name;
        const fileName = fileDoc.data().filename;
        console.log(
          "Obtained from firebase ",
          { docs },
          { userNotif, ownerName, fileName }
        );

        // now we proceed to send the message
        let message;

        if (timestamp < now) {
          // then we have been revoked
          message = `You have been revoked by ${ownerName} for the file ${fileName}`;
        } else if (timestamp < addDays(now, 2)) {
          // less than in two days, i.e. til tomorrow
          message = `You have been given access until tomorrow by ${ownerName} for the file ${fileName}`;
        } else {
          // indefinitely
          message = `You have been given access by ${ownerName} for the file ${fileName}`;
        }
        const payload = {
          notification: {
            title: message,
            body: message
          }
        };
        // send
        admin
          .messaging()
          .sendToDevice(userNotif, payload)
          .then((r) => {
            console.log("sent message change", r);
            console.log("sent message");
          })
          .catch((xx) => {
            console.log("error4", xx);
          });
      })
      .catch((e) => {
        console.log("error here", e);
      })
      .catch((e) => {
        console.log("Error at getAll", e);
      });
  });
