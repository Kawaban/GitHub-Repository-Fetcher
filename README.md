# Repository API

## Overview
The **Repository API** provides an endpoint to fetch repositories for a given user. It retrieves repository information, including branches and their last commit SHA, and returns the data in JSON format. If the user does not exist, it returns an appropriate error response.


## Endpoint Details
### Get Repositories by User
**Endpoint:**
```
GET /api/repos/{user}
```

**Description:**
Retrieves a list of repositories for a specified GitHub user.

**Path Parameters:**
| Parameter | Type   | Description                     |
|-----------|--------|---------------------------------|
| `user`    | String | The GitHub username to search for |

**Response Format:**
Returns a JSON array of repositories, where each repository includes its name, owner, and associated branches with their last commit SHA.

**Response Example:**
```json
[
    {
        "name": "example-repo",
        "owner": "example-user",
        "branches": [
            {
                "name": "main",
                "lastCommitSha": "abc123def456"
            },
            {
                "name": "develop",
                "lastCommitSha": "789xyz123uvw"
            }
        ]
    }
]
```

## Error Handling
The API returns meaningful error responses in case of failures.

| HTTP Status | Description                 | Response Example |
|-------------|-----------------------------|------------------|
| `404`       | User not found               | `{ "message": "User doesn't exist", "status": 404 }` |
| `500`       | Internal server error        | `{ "message": "Internal server error", "status": 500 }` |


## Technology Stack
- Java
- Quarkus 3
- Mutiny for reactive programming

## Notes
- The API uses **reactive programming** with `Uni<T>` to handle asynchronous calls efficiently.
- It properly handles errors, returning appropriate HTTP status codes with descriptive error messages.
- The endpoint supports JSON responses only (`MediaType.APPLICATION_JSON`).
