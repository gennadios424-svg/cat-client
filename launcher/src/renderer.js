const $ = (id) => document.getElementById(id);
let state;
function setStatus(message) { $("status").textContent = message; }
function showPage(page) {
  document.querySelectorAll(".page-view").forEach(v => v.classList.toggle("active", v.id === "page-" + page));
  document.querySelectorAll(".nav[data-page]").forEach(b => b.classList.toggle("active", b.dataset.page === page));
  const labels = {overview:"OVERVIEW",game:"GAME SETUP",appearance:"APPEARANCE",settings:"SETTINGS"};
  document.querySelector(".crumb").innerHTML = "CAT CLIENT <span>/</span> " + labels[page];
}
async function saveRam(value) {
  const settings = await window.catLauncher.saveSettings({ ramGb: Number(value) });
  $("ram-value").textContent = settings.ramGb;
  $("ram-label").textContent = settings.ramGb;
  $("ram-label-page").textContent = settings.ramGb;
  $("ram").value = settings.ramGb;
  $("ram-page").value = settings.ramGb;
  setStatus("Cat Client settings saved successfully.");
}
async function init() {
  state = await window.catLauncher.getState();
  for (const id of ["ram","ram-page"]) $(id).value = state.settings.ramGb;
  $("ram-value").textContent = state.settings.ramGb;
  $("ram-label").textContent = state.settings.ramGb;
  $("ram-label-page").textContent = state.settings.ramGb;
  $("ram").addEventListener("input", e => { $("ram-value").textContent=e.target.value; $("ram-label").textContent=e.target.value; $("ram-page").value=e.target.value; $("ram-label-page").textContent=e.target.value; });
  $("ram-page").addEventListener("input", e => { $("ram").value=e.target.value; $("ram-value").textContent=e.target.value; $("ram-label").textContent=e.target.value; $("ram-label-page").textContent=e.target.value; });
}
document.querySelectorAll(".nav[data-page]").forEach(b => b.addEventListener("click",()=>showPage(b.dataset.page)));
$("save").addEventListener("click",()=>saveRam($("ram").value));
$("save-page").addEventListener("click",()=>saveRam($("ram-page").value));
$("login").addEventListener("click", async () => { const result=await window.catLauncher.login(); setStatus(result.message); });
$("play").addEventListener("click", async () => { const result=await window.catLauncher.launch(); setStatus(result.message); });
$("open-folder").addEventListener("click",()=>window.catLauncher.openGameFolder());
$("open-folder-2").addEventListener("click",()=>window.catLauncher.openGameFolder());
$("game-folder").addEventListener("click",()=>window.catLauncher.openGameFolder());
document.querySelectorAll(".theme-choice").forEach(b=>b.addEventListener("click",async()=>{
  document.querySelectorAll(".theme-choice").forEach(x=>x.classList.toggle("selected",x===b));
  document.body.dataset.theme=b.dataset.theme;
  setStatus("Cat Client appearance updated.");
  await window.catLauncher.saveSettings({ramGb:Number($("ram").value),theme:b.dataset.theme});
}));
init().catch(()=>setStatus("Cat Client could not load its settings."));
