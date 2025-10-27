export class HttpClient {
    async get(url: string, headers: Record<string, string> = {}): Promise<any> {
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                ...headers,
            },
        });
        return this.handleResponse(response);
    }

    async post(url: string, body: any, headers: Record<string, string> = {}): Promise<any> {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...headers,
            },
            body: JSON.stringify(body),
        });
        return this.handleResponse(response);
    }

    private async handleResponse(response: Response): Promise<any> {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    }
}