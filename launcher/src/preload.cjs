const { contextBridge, ipcRenderer } = require("electron");

contextBridge.exposeInMainWorld("catLauncher", {
  getState: () => ipcRenderer.invoke("cat:state"),
  saveSettings: (settings) => ipcRenderer.invoke("cat:save-settings", settings),
  openGameFolder: () => ipcRenderer.invoke("cat:open-game-folder"),
  login: () => ipcRenderer.invoke("cat:login"),
  launch: () => ipcRenderer.invoke("cat:launch")
});
