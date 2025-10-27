import { Orchestrator } from '../../src/orchestrator';
import { UsersService } from '../../src/services/usersService';
import { OrdersService } from '../../src/services/ordersService';

describe('Orchestrator', () => {
    let orchestrator: Orchestrator;
    let usersService: UsersService;
    let ordersService: OrdersService;

    beforeEach(() => {
        usersService = new UsersService();
        ordersService = new OrdersService();
        orchestrator = new Orchestrator(usersService, ordersService);
    });

    it('should fetch user details and order details', async () => {
        const userId = '123';
        const orderId = '456';

        jest.spyOn(usersService, 'getUserDetails').mockResolvedValue({ id: userId, name: 'John Doe' });
        jest.spyOn(ordersService, 'getOrderDetails').mockResolvedValue({ id: orderId, total: 100 });

        const result = await orchestrator.composeResponse(userId, orderId);

        expect(result).toEqual({
            user: { id: userId, name: 'John Doe' },
            order: { id: orderId, total: 100 },
        });
    });

    it('should handle errors when fetching user details', async () => {
        const userId = '123';
        const orderId = '456';

        jest.spyOn(usersService, 'getUserDetails').mockRejectedValue(new Error('User not found'));

        await expect(orchestrator.composeResponse(userId, orderId)).rejects.toThrow('User not found');
    });

    it('should handle errors when fetching order details', async () => {
        const userId = '123';
        const orderId = '456';

        jest.spyOn(ordersService, 'getOrderDetails').mockRejectedValue(new Error('Order not found'));

        await expect(orchestrator.composeResponse(userId, orderId)).rejects.toThrow('Order not found');
    });
});