import express from 'express';
import { Gateway } from './gateway/index';

const app = express();
const port = process.env.PORT || 3000;

// Middleware
app.use(express.json());

// Initialize Gateway
const gateway = new Gateway();

// Set up routes
app.use('/api', gateway.router);

// Start the server
app.listen(port, () => {
    console.log(`Server is running on http://localhost:${port}`);
});