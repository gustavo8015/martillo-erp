// Utilidades compartidas por las pruebas de interfaz.
const fs = require('fs');
const path = require('path');

const CAPTURAS = path.join(__dirname, '..', 'resultados', 'capturas');

async function abrir(page) {
  // Sin backend: la conexion al puerto 8080 se corta de inmediato para que la
  // interfaz entre en modo demostracion sin esperar el tiempo de espera de red.
  await page.route('http://localhost:8080/**', (ruta) => ruta.abort('connectionrefused'));
  await page.goto('/index.html');
  // La comprobacion de la API termina cuando la tabla de inventario se llena.
  await page.locator('#cuerpoInventario tr').first().waitFor({ state: 'attached' });
}

async function pestana(page, panel) {
  await page.locator(`.pestana[data-panel="${panel}"]`).click();
}

async function iniciarSesion(page, usuario, clave, puntuacion) {
  await pestana(page, 'panelSeguridad');
  await page.fill('#logUsuario', usuario);
  await page.fill('#logClave', clave);
  await page.locator('#logPuntuacion').fill(String(puntuacion));
  await page.locator('#formLogin button[type="submit"]').click();
}

async function capturar(page, nombre) {
  fs.mkdirSync(CAPTURAS, { recursive: true });
  await page.screenshot({ path: path.join(CAPTURAS, nombre + '.png') });
}

// Relacion de contraste de WCAG 2.2 entre dos colores rgb() calculados.
function luminancia(rgb) {
  const [r, g, b] = rgb.match(/\d+(\.\d+)?/g).slice(0, 3).map(Number).map((c) => {
    const s = c / 255;
    return s <= 0.03928 ? s / 12.92 : Math.pow((s + 0.055) / 1.055, 2.4);
  });
  return 0.2126 * r + 0.7152 * g + 0.0722 * b;
}

function contraste(a, b) {
  const [l1, l2] = [luminancia(a), luminancia(b)].sort((x, y) => y - x);
  return (l1 + 0.05) / (l2 + 0.05);
}

module.exports = { abrir, pestana, iniciarSesion, capturar, contraste };
