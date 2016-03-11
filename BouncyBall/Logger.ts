import chalk = require("chalk");

export default class Logger {
    private name: string;

    constructor(name: string) {
        this.name = name;
    }

    public debug(message: any) {
        console.log(chalk.bold.cyan("[%s] ") + chalk.bold.blue("DEBUG: ") + chalk.bold.white(message), this.name);
    }

    public info(message: string) {
        console.log(chalk.bold.cyan("[%s] ") + chalk.bold.green("INFO: ") + chalk.bold.white(message), this.name);
    }

    public warn(message: string) {
        console.log(chalk.bold.cyan("[%s] ") + chalk.bold.yellow("WARN: ") + chalk.bold.yellow(message), this.name);
    }

    public error(message: string) {
        console.log(chalk.bold.cyan("[%s] ") + chalk.bold.red("ERROR: ") + chalk.bold.red(message), this.name);
    }
}