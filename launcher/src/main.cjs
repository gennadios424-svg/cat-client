const { app, BrowserWindow, ipcMain, shell } = require("electron");
const path = require("node:path");
const fs = require("node:fs/promises");

const ROOT = path.join(app.getPath("appData"), "CatClient");
const GAME_DIR = path.join(ROOT, "instances", "main");
const CONFIG = path.join(ROOT, "launcher-settings.json");

async function ensureFolders() {
  await fs.mkdir(path.join(GAME_DIR, "mods"), { recursive: true });
  await fs.mkdir(path.join(GAME_DIR, "resourcepacks"), { recursive: true });
  await fs.mkdir(path.join(GAME_DIR, "screenshots"), { recursive: true });
}

async function readSettings() {
  try { return JSON.parse(await fs.readFile(CONFIG, "utf8")); }
  catch { return { ramGb: 4, gameVersion: "1.21.1", theme: "midnight" }; }
}

async function saveSettings(settings) {
  const safe = {
    ramGb: Math.max(2, Math.min(8, Number(settings.ramGb) || 4)),
    gameVersion: "1.21.1",
    theme: settings.theme === "light" ? "light" : "midnight"
  };
  await fs.mkdir(ROOT, { recursive: true });
  await fs.writeFile(CONFIG, JSON.stringify(safe, null, 2));
  return safe;
}

function createWindow() {
  const win = new BrowserWindow({
    width: 1120, height: 720, minWidth: 900, minHeight: 600,
    backgroundColor: "#0c1019",
    title: "Cat Client",
    autoHideMenuBar: true,
    webPreferences: {
      preload: path.join(__dirname, "preload.cjs"),
      contextIsolation: true,
      nodeIntegration: false,
      sandbox: true
    }
  });
  win.loadFile(path.join(__dirname, "index.html"));
}

app.whenReady().then(async () => {
  await ensureFolders();
  ipcMain.handle("cat:state", async () => ({
    settings: await readSettings(),
    gameDir: GAME_DIR,
    ready: false,
    account: null
  }));
  ipcMain.handle("cat:save-settings", async (_event, settings) => saveSettings(settings));
  ipcMain.handle("cat:open-game-folder", async () => shell.openPath(GAME_DIR));
  ipcMain.handle("cat:login", async () => ({
    ok: false,
    message: "Microsoft sign-in is being connected. No password is requested or stored by Cat Client."
  }));
  ipcMain.handle("cat:launch", async () => ({
    ok: false,
    message: "Game launching will be enabled after official Microsoft sign-in and Fabric installation are connected."
  }));
  createWindow();
  app.on("activate", () => { if (BrowserWindow.getAllWindows().length === 0) createWindow(); });
});

app.on("window-all-closed", () => { if (process.platform !== "darwin") app.quit(); });
