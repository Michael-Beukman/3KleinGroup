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

exports.createAgreement = functions.firestore
  .document("Agreements/{agreementID}")
  .onCreate((snap, context) => {
    console.log("starting onCreate");
    // get the data from the agreement

    // Get an object representing the document
    // e.g. {'fileID': '..', 'userID': '...', validUntil: ...}
    const newValue = snap.data();
    console.log("got data from snap ", {newValue});
    // the user that should get the notification
    const userID = newValue.userID;
    console.log("got user id ", {userID});
    const db = admin.firestore();
    db.collection("Users")
      .doc(userID)
      .get()
      .then((snapshot) => {
          console.log("got snapshot from users ", {snapshot}, {data: snapshot.data()});
        const notifToken = snapshot.data().notificationToken;
        // Notification details.
        console.log("got snapshot from users notiftoekn ", {notifToken});
        const payload = {
          notification: {
            title: "New File received!",
            body: `File id is: ${newValue.fileID}`
          } 
        };
        console.log("payload ", {payload});

        // Listing all tokens as an array.
        // tokens = Object.keys(tokensSnapshot.val());
        // Send notifications to all tokens.
        admin.messaging().sendToDevice(notifToken, payload).then(
            
            r => {console.log('error1', r)
            console.log("sent message")}
        ).catch(xx => {
            console.log('error4', xx)
        });
      }).catch(e => {
          console.log('error2', e);
      })
      .catch((x) => {
        // todo
        console.log('error3', x);
      });

    // perform desired operations ...

    console.log("done");
  });
