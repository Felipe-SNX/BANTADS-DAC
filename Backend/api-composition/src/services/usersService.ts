export class UsersService {
    private users: { [key: string]: any } = {};

    constructor() {
        // Initialize with some dummy data
        this.users = {
            '1': { id: '1', name: 'John Doe', email: 'john@example.com' },
            '2': { id: '2', name: 'Jane Smith', email: 'jane@example.com' }
        };
    }

    public getUserById(id: string) {
        return this.users[id] || null;
    }

    public getAllUsers() {
        return Object.values(this.users);
    }

    public createUser(user: { id: string; name: string; email: string }) {
        this.users[user.id] = user;
        return user;
    }
}