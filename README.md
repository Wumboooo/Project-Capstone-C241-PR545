# WordWarden - Node.js Backend

This repository contains the Node.js backend for the WordWarden project. The backend allows users to perform authentication operations, consumed by our WordWarden application. It is built using Node.js and the Express framework. The application also uses Firebase for authentication and Firestore for data storage.

## API Endpoints Documentation

### Summary

Base URL: https://wordwardenapp.et.r.appspot.com/

| Route              | HTTP Method | Description        | Token Required? |
| ------------------ | ----------- | ------------------ | --------------- |
| /v1/auth/register  | POST        | Sign up a new user | -               |
| /v1/auth/login     | POST        | Login user         | -               |
| /v1/auth/user/:uid | GET         | Get user data      | Yes             |
| /v1/history        | POST        | Save history       | Yes             |
| /v1/history/:uid   | GET         | Get user history   | Yes             |

### Authentication & Authorization (Register, Login)

#### POST `/v1/auth/register` - Sign up a new user

##### Request

- **Method:** **POST**
- **Path:** **`/v1/auth/register`**
- **Body:**
  ```json
  {
    "name": "John Doe",
    "email": "johndoe@example.com",
    "password": "johndoe1"
  }

  ##### Response

- **Status:** **201 Created**
  ```json
  {
  "success": true,
  "message": "User registered successfully",
  "data": {
    "uid": "user-uid",
    "name": "John Doe",
    "email": "johndoe@example.com",
    "createdAt": "2023-06-15T00:00:00.000Z"
  }
}

  ```
