const express = require('express');
const router = express.Router();
const { saveHistory, getHistory } = require('../controllers/history');

router.route('/').post(saveHistory);
router.route('/:uid').get(getHistory);

module.exports = router;