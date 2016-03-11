import Server from "./Server";
import Logger from "./Logger";
import ClientManager from "./ClientManager";

import readline = require("readline");

export default class BouncyBallProxy {
    public remoteAddress: string;
    public remotePort: number;
    public bindInterface: string;
    public bindPort: number;

    private running: boolean = false;
    private server: Server;
    private clientMgr: ClientManager;
    private logger: Logger;
    private cli: readline.ReadLine;

    constructor(remoteAddress: string, remotePort: number, bindInterface: string, bindPort: number) {
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
        this.bindInterface = bindInterface;
        this.bindPort = bindPort;

        this.logger = new Logger("BouncyBall");
    }

    public run() {
        this.clientMgr = new ClientManager();

        this.server = new Server(this);
        this.server.run();

        this.setupCli();

        this.running = true;
        setTimeout(() => this.update(), 1); //Start ticking
    }

    public update() {
        if (!this.running) return;

        var start: number = new Date().getTime();
        this.tick();
        var elapsed: number = (new Date().getTime()) - start;
        if (elapsed >= 50) {
            this.logger.warn("Can't keep up! (" + elapsed + " > 50)");
            setTimeout(() => this.update(), 1);
        }
        setTimeout(() => this.update(), 50 - elapsed);
    }

    private tick() {
        //TODO
    }

    private onInput(line: string) {
        switch (line) {
            case "stop":
                this.server.shutdown();
                this.running = false;
                this.logger.info("Shutting down...");
                setTimeout(() => process.exit(0), 2000);
                break;
            default:
                this.logger.info(`Unknown command "${line}"`);
                this.cli.prompt();
                break;
        }
    }

    private setupCli() {
        this.cli = readline.createInterface({
            input: process.stdin,
            output: process.stdout
        });

        this.cli.setPrompt("", 0);
        this.cli.prompt();

        this.cli.on("line", (line) => {
            this.onInput(line);
        });
    }

    public getLogger(): Logger {
        return this.logger;
    }
}