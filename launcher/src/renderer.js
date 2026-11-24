const $ = (id) => document.getElementById(id);
let state;
function setStatus(message) { $("status").textContent = message; }
async function init() {
  state = await window.catLauncher.getState();
  $("ram").value = state.settings.ramGb;
  $("ram-value").textContent = state.settings.ramGb;
  $("ram-label").textContent = state.settings.ramGb;
  $("ram").addEventListener("input", e => {
    $("ram-value").textContent = e.target.value;
    $("ram-label").textContent = e.target.value;
  });
}
$("save").addEventListener("click", async () => {
  const settings = await window.catLauncher.saveSettings({ ramGb: Number($("ram").value) });
  $("ram-value").textContent = settings.ramGb;
  setStatus("Settings saved to your separate Cat Client folder.");
});
$("login").addEventListener("click", async () => {
  const result = await window.catLauncher.login();
  setStatus(result.message);
});
$("play").addEventListener("click", async () => {
  const result = await window.catLauncher.launch();
  setStatus(result.message);
});
$("open-folder").addEventListener("click", () => window.catLauncher.openGameFolder());
$("open-folder-2").addEventListener("click", () => window.catLauncher.openGameFolder());
$("nav-settings").addEventListener("click", () => $("settings-panel").scrollIntoView({ behavior: "smooth", block: "center" }));
init().catch(() => setStatus("Could not initialize launcher settings."));
