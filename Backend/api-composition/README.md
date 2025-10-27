# API Composition Project

## Overview
This project implements an API composition layer that aggregates responses from multiple services. It serves as a gateway for client applications to interact with various backend services seamlessly.

## Project Structure
```
api-composition
├── src
│   ├── app.ts                # Entry point of the application
│   ├── gateway
│   │   ├── index.ts          # Gateway class for handling requests
│   │   └── routes.ts         # API routes for the gateway
│   ├── orchestrator
│   │   └── index.ts          # Orchestrator class for composing responses
│   ├── services
│   │   ├── usersService.ts    # Service for user-related data
│   │   └── ordersService.ts    # Service for order-related data
│   ├── clients
│   │   ├── httpClient.ts      # HTTP client for external API requests
│   │   └── graphqlClient.ts   # GraphQL client for external services
│   ├── schemas
│   │   └── index.ts          # Data validation schemas
│   └── types
│       └── index.ts          # TypeScript interfaces and types
├── tests
│   ├── unit
│   │   └── orchestrator.test.ts # Unit tests for the Orchestrator
│   └── integration
│       └── gateway.test.ts    # Integration tests for the Gateway
├── package.json               # npm configuration file
├── tsconfig.json              # TypeScript configuration file
├── .env.example               # Example environment variables
├── Dockerfile                 # Docker image build instructions
└── README.md                  # Project documentation
```

## Setup Instructions
1. Clone the repository:
   ```
   git clone <repository-url>
   cd api-composition
   ```

2. Install dependencies:
   ```
   npm install
   ```

3. Set up environment variables:
   - Copy `.env.example` to `.env` and fill in the required values.

4. Run the application:
   ```
   npm start
   ```

## Usage
- The API gateway listens for incoming requests and routes them to the appropriate services via the orchestrator.
- You can access the API endpoints defined in `src/gateway/routes.ts`.

## Testing
- Unit tests can be run using:
  ```
  npm run test:unit
  ```
- Integration tests can be run using:
  ```
  npm run test:integration
  ```

## Contributing
Contributions are welcome! Please open an issue or submit a pull request for any enhancements or bug fixes.