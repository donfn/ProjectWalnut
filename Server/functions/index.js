const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
exports.breach = functions.region('europe-west1').https.onRequest((request, response) => {
    
    response.send(request.body)
    // response.send("Hello from Firebase!");
});
