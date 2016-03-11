import Client from "./Client";

export default class ClientManager {
    private clients: { [key: string]: Client; } = {};

    public getClient(address: string): Client {
        return this.clients[address];
    }

    public addClient(client: Client) {
        if (this.getClient(client.getAddressAsString()) !== undefined) {
            this.clients[client.getAddressAsString()] = client;
        }
    }

    protected removeClient(client: Client) {
        if (this.getClient(client.getAddressAsString()) !== undefined) {
            delete this.clients[client.getAddressAsString()];
        }
    }
}