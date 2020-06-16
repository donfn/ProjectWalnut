
//-------  Config  ----------// 
process.env.APIPort = 8080

const admin = require('firebase-admin');

var serviceAccount = require("./serviceAccountKey.json");

admin.initializeApp({
credential: admin.credential.cert(serviceAccount),
databaseURL: "https://projectwalnut-720aa.firebaseio.com"
});

const db = admin.firestore()
process.env.db = db


const cloudConfig = db.collection('misc').doc("config")

const brain = require("./core/modules/js/brain")(cloudConfig)
require("./core/modules/js/APIServer")(brain)

process.on('unhandledRejection', r => {})