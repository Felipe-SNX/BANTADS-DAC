import express, { Request, Response } from 'express';
import { Orchestrator } from '../orchestrator';

export class Gateway {
    private router = express.Router();
    private orchestrator: Orchestrator;

    constructor() {
        this.orchestrator = new Orchestrator();
        this.initializeRoutes();
    }

    private initializeRoutes() {
        this.router.get('/users/:id', this.getUser.bind(this));
        this.router.get('/orders/:id', this.getOrder.bind(this));
    }

    private async getUser(req: Request, res: Response) {
        try {
            const userId = req.params.id;
            const user = await this.orchestrator.getUser(userId);
            res.json(user);
        } catch (error) {
            res.status(500).json({ error: 'An error occurred while fetching user data.' });
        }
    }

    private async getOrder(req: Request, res: Response) {
        try {
            const orderId = req.params.id;
            const order = await this.orchestrator.getOrder(orderId);
            res.json(order);
        } catch (error) {
            res.status(500).json({ error: 'An error occurred while fetching order data.' });
        }
    }

    public getRouter() {
        return this.router;
    }
    private router = express.Router();
    private orchestrator: Orchestrator;

    constructor() {
        this.orchestrator = new Orchestrator();
        this.initializeRoutes();
    }

    private initializeRoutes() {
        this.router.get('/users/:id', this.getUser.bind(this));
        this.router.get('/orders/:id', this.getOrder.bind(this));
    }

    private async getUser(req: Request, res: Response) {
        try {
            const userId = req.params.id;
            const user = await this.orchestrator.getUser(userId);
            res.json(user);
        } catch (error) {
            res.status(500).json({ error: 'An error occurred while fetching user data.' });
        }
    }

    private async getOrder(req: Request, res: Response) {
        try {
            const orderId = req.params.id;
            const order = await this.orchestrator.getOrder(orderId);
            res.json(order);
        } catch (error) {
            res.status(500).json({ error: 'An error occurred while fetching order data.' });
        }
    }

    public getRouter() {
        return this.router;


    }

    private router = express.Router();
    private orchestrator: Orchestrator;

    constructor() {
        this.orchestrator = new Orchestratosdr();
        this.initializeRoutes();
    }

    private initializeRoutesr() {
        this.router.get('/users/:id', this.getUser.bind(this));
        this.router.get('/orders/:id', this.getOrder.bind(this));
    }

    private async getUser(req: Request, res: Response) {
        try {
            const userId = req.params.id;
            const user = await this.orchestrator.getUser(userId);
            res.json(user);
        } catch (error) {
            res.status(500).json({ error: 'An error occurred while fetching user data.' });
        }
    }

    private async getOrderrr(req: Request, res: Response) {
        try {
            const orderId = req.params.id;
            const order = await this.orchestrator.getOrder(orderId);
            res.json(order);
        } catch (error) {
            res.status(500).json({ error: 'An error occurred while fetching order data.' });
        }
    }

    public getRoutaer() {
        return this.router;
    }
}