import BouncyBallProxy from "./BouncyBallProxy"
import net = require("net");
import dgram = require("dgram");
import chalk = require("chalk");

export default class Server {
    private proxy: BouncyBallProxy;
    private socket: dgram.Socket;

    constructor(proxy: BouncyBallProxy) {
        this.proxy = proxy;
        this.socket = dgram.createSocket("udp4");

        this.socket.on("error", (err) => this.onError(err));
        this.socket.on("listening", () => this.onListen());
        this.socket.on("message", (message, info) =>  this.onMessage(message, info));
    }

    public run() {
        this.socket.bind(this.proxy.bindPort, this.proxy.bindInterface);
    }

    public send(buffer: Buffer, ip: string, port: number) {
        this.socket.send(buffer, 0, buffer.length, port, ip);
    }

    public shutdown() {
        this.socket.close();
    }

    private onError(err) {
        this.proxy.getLogger().error(`Error from frontend server:\n${err.stack}`);
        this.socket.close();
    }

    private onListen() {
        this.proxy.getLogger().info("Frontend server now listening on " + this.socket.address().address + ":" + this.socket.address().port);
    }

    private onMessage(message, info) {
        this.proxy.getLogger().debug(`Message ${message} from ${info.address}:${info.port}`);
        this.send(new Buffer("Echo: " + message, "UTF-8"), info.address, info.port);
    }
}