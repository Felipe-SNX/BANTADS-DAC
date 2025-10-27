export class GraphQLClient {
    private endpoint: string;

    constructor(endpoint: string) {
        this.endpoint = endpoint;
    }

    async query(query: string, variables?: Record<string, any>): Promise<any> {
        const response = await fetch(this.endpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                query,
                variables,
            }),
        });

        const json = await response.json();
        if (response.ok) {
            return json.data;
        } else {
            throw new Error(json.errors.map((error: any) => error.message).join(', '));
        }
    }

    async mutate(mutation: string, variables?: Record<string, any>): Promise<any> {
        const response = await fetch(this.endpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                query: mutation,
                variables,
            }),
        });

        const json = await response.json();
        if (response.ok) {
            return json.data;
        } else {
            throw new Error(json.errors.map((error: any) => error.message).join(', '));
        }
    }
}