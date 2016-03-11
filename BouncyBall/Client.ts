import dgram = require("dgram");
import ClientManager from "./ClientManager";

export default class Client {
    private address: dgram.AddressInfo;
    private serverAddress: dgram.AddressInfo;
    private manager: ClientManager;

    constructor(manager: ClientManager) {
        this.manager = manager;
    }

    public getAddress(): dgram.AddressInfo {
        return this.address;
    }

    public getAddressAsString(): string {
        return `${this.address.address}:${this.address.port}`;
    }
}