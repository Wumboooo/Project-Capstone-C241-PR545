const instance = require('../config/firebase');
const response = require('../middleware/response');
const db = instance.db;

// @desc Save History
// @route POST /v1/history
// @access public
const saveHistory = async (req, res) => {
    try {
        const { uid, word, isOffensive } = req.body;

        if (!uid || !word || isOffensive === undefined) {
            return response(400, "UID, word, and isOffensive are required.", null, res);
        }

        const createdAt = new Date().toISOString();

        const historyData = {
            uid,
            word,
            isOffensive,
            createdAt
        };

        await db.collection('history').add(historyData);

        response(201, "History Saved", historyData, res);
    } catch (error) {
        response(400, "Failed to Save History", error.message, res);
    }
};

// @desc Get History by UID
// @route GET /v1/history/:uid
// @access public
const getHistory = async (req, res) => {
    try {
        const { uid } = req.params;
        const historyRef = db.collection('history').where('uid', '==', uid);
        const snapshot = await historyRef.get();

        if (snapshot.empty) {
            return response(404, "No matching documents.", null, res);
        }

        let history = [];
        snapshot.forEach(doc => {
            history.push(doc.data());
        });

        response(200, "History fetched successfully", history, res);
    } catch (error) {
        response(400, "Failed to fetch history", error.message, res);
    }
};

module.exports = {
    saveHistory,
    getHistory
};