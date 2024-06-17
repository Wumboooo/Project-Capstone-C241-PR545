# WordWarden - Flask (ML Model Deployment)

This branch includes the deployment of a Machine Learning Model for the WordWarden project, enabling the identification of offensive words through API request methods.

## API Documentation

### Summary

Base URL : https://wordwarden-flask-api-wn2mamywtq-et.a.run.app

| Route        | HTTP Method | Description               |
| ------------ | ----------- | ------------------------- |
| /            | GET         | Health check              |
| /predict     | POST        | Perform Data              |

### Endpoints

#### **GET `/` - Health check**

##### Request

- **Method:** GET
- **Path:** `/`

##### Response

- **Status: 200 OK**
  ```json
    {
      "message": "API is running!",
      "status": true
    }

  ```

#### POST `/predict` - Perform Data

##### Request

- **Method:** POST
- **Path:** `/predict`
- **Body:**

  ```json
    {
    "text": "Selamat pagi dunia"
    }
  ```

##### Response

- **Status: 200 OK**

  ```json
  {
    "status": true,
    "result": "POSITIVE. NOT Hate Speech and NOT Abusive Tweet"
  }
  ```
  
- **Status: 400 Bad Request**
  ```json
  {
    "status": false,
    "error": "No text provided"
  }
  ```

- **Status: 500 Internal Server Error**
  ```json
  {
    "status": false,
    "error": "error message"
  }
  ```
