const admin = require('firebase-admin');
const serviceAccount = require('./key.json'); 

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  projectId: serviceAccount.project_id 
});

const db = admin.firestore();
db.settings({ ignoreUndefinedProperties: true });
const auth = admin.auth();

module.exports = { db, auth };
