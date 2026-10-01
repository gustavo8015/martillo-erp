// Pruebas de extremo a extremo de la interfaz del Sprint 3 (Guia 8).
// La interfaz se sirve como archivos estaticos; sin backend en el puerto 8080
// api.js entra en modo demostracion, que aplica las mismas reglas que el servidor.
const { defineConfig } = require('@playwright/test');

module.exports = defineConfig({
  testDir: './e2e',
  timeout: 30000,
  fullyParallel: false,
  workers: 1,
  reporter: [
    ['list'],
    ['junit', { outputFile: 'resultados/junit-interfaz.xml' }],
    ['html', { outputFolder: 'resultados/reporte-html', open: 'never' }]
  ],
  use: {
    baseURL: 'http://127.0.0.1:5173',
    viewport: { width: 1366, height: 820 },
    screenshot: 'only-on-failure',
    locale: 'es-CO'
  },
  webServer: {
    command: 'python3 -m http.server 5173 --bind 127.0.0.1 --directory ..',
    url: 'http://127.0.0.1:5173/index.html',
    reuseExistingServer: true
  }
});
