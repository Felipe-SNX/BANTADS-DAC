export class Orchestrator {
    constructor(private usersService: UsersService, private ordersService: OrdersService) {}

    async getUserDetails(userId: string) {
        return await this.usersService.fetchUserDetails(userId);
    }

    async getOrderDetails(orderId: string) {
        return await this.ordersService.fetchOrderDetails(orderId);
    }

    async composeResponse(userId: string, orderId: string) {
        const userDetails = await this.getUserDetails(userId);
        const orderDetails = await this.getOrderDetails(orderId);
        
        return {
            user: userDetails,
            order: orderDetails,
        };
    }
}