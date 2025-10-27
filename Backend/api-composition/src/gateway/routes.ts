import { Router } from 'express';
import Gateway from './index';

const router = Router();
const gateway = new Gateway();

// Define API routes
router.get('/users/:id', gateway.handleGetUser.bind(gateway));
router.get('/orders/:id', gateway.handleGetOrder.bind(gateway));

export default router;