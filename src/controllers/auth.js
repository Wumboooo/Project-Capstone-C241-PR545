const bcrypt = require('bcrypt');
const { validationResult } = require('express-validator');
const instance = require('../config/firebase');
const response = require('../middleware/response');

// firestore and auth
const db = instance.db;
const auth = instance.auth;

// @desc Register Account
// @route POST /v1/auth/register
// @access public
const register = async (req, res) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
        return response(400, "Validation errors", errors.array(), res);
    }

    try {
        const { name, email, password } = req.body;

        if (!name || !email || !password) {
            return response(400, "Name, email, and password are required.", null, res);
        }

        const sendUserAuth = await auth.createUser({
            email,
            password,
            emailVerified: false,
            disabled: false,
        });

        const salt = await bcrypt.genSalt(10);
        const hashedPassword = await bcrypt.hash(password, salt);

        const createdAt = new Date().toISOString();

        const userData = {
            uid: sendUserAuth.uid,
            name,
            email,
            password: hashedPassword,
            createdAt: createdAt
        };

        await db.collection('users').doc(sendUserAuth.uid).set(userData);

        response(201, "User Created", userData, res);
    } catch (error) {
        response(400, "Failed To Create User", error.message, res);
    }
};

// @desc Login
// @route POST /v1/auth/login
// @access public
const login = async (req, res) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
        return response(400, "Validation errors", errors.array(), res);
    }

    try {
        const { email, password } = req.body;

        const userRecord = await auth.getUserByEmail(email);
        const userDoc = await db.collection('users').doc(userRecord.uid).get();

        if (!userDoc.exists) {
            return response(401, "Invalid credentials", null, res);
        }

        const hashedPassword = userDoc.data().password;
        const isPasswordValid = await bcrypt.compare(password, hashedPassword || '');

        if (!isPasswordValid) {
            return response(401, "Invalid credentials", null, res);
        }

        const token = await auth.createCustomToken(userRecord.uid);

        const loginResult = {
            userId: userRecord.uid,
            name: userDoc.data().name,
            token,
        };

        response(200, "success", loginResult, res);
    } catch (error) {
        response(400, error.message, null, res);
    }
};

// @desc Get User by UID
// @route GET /v1/auth/user/:uid
// @access public
const getUser = async (req, res) => {
    try {
        const { uid } = req.params;
        const userDoc = await db.collection('users').doc(uid).get();

        if (!userDoc.exists) {
            return response(404, "User not found", null, res);
        }

        const userData = userDoc.data();
        const userResponse = {
            status_code: 200,
            name: userData.name,
            email: userData.email,
            createdAt: userData.createdAt
        };

        response(200, "User fetched successfully", userResponse, res);
    } catch (error) {
        response(400, "Failed to fetch user", error.message, res);
    }
};

module.exports = {
    register,
    login,
    getUser
};