const functions = require("firebase-functions");
const admin = require("firebase-admin");
const {firestore} = require("firebase-admin");
admin.initializeApp();

exports.removeExpiredDocuments = functions.pubsub.schedule("every 30 minutes")
    .onRun(async (context) => {
      console.log("Cron-Job");
      const db = admin.firestore();
      const now = firestore.Timestamp.now();
      const ts = firestore.Timestamp.fromMillis(now.toMillis() - 7200000);
      // 24 hours in milliseconds = 86400000

      const snap = await db.collection("Foods").where("MilliSec", "<", ts)
          .get();
      const promises = [];
      snap.forEach((snap) => {
        promises.push(snap.ref.delete());
      });
      return Promise.all(promises);
    });

// // Create and deploy your first functions
// // https://firebase.google.com/docs/functions/get-started
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
