import request from 'supertest';
import { app } from '../../src/app'; // Adjust the import based on your app's structure
import { Orchestrator } from '../../src/orchestrator/index';

jest.mock('../../src/orchestrator/index');

describe('Gateway Integration Tests', () => {
    let orchestrator: Orchestrator;

    beforeEach(() => {
        orchestrator = new Orchestrator();
        (orchestrator.composeResponse as jest.Mock).mockImplementation(() => {
            return { data: 'mocked response' };
        });
    });

    it('should route GET requests to the orchestrator', async () => {
        const response = await request(app).get('/api/some-endpoint'); // Adjust the endpoint as needed
        expect(response.status).toBe(200);
        expect(response.body).toEqual({ data: 'mocked response' });
        expect(orchestrator.composeResponse).toHaveBeenCalled();
    });

    it('should handle errors from the orchestrator', async () => {
        (orchestrator.composeResponse as jest.Mock).mockImplementation(() => {
            throw new Error('Orchestrator error');
        });

        const response = await request(app).get('/api/some-endpoint'); // Adjust the endpoint as needed
        expect(response.status).toBe(500);
        expect(response.body).toEqual({ error: 'Orchestrator error' });
    });
});