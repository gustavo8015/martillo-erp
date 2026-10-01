const { test, expect } = require('@playwright/test');
const { abrir, pestana, iniciarSesion, capturar } = require('./apoyo');

test.describe('HU-09 Control de acceso por rol y HU-11 reCAPTCHA v3', () => {
  test('CP-E2E-06 Sin sesion de nomina la liquidacion responde acceso denegado', async ({ page }) => {
    await abrir(page);
    await pestana(page, 'panelNomina');
    await page.locator('#formLiquidacion button[type="submit"]').click();
    await expect(page.locator('#resultadoLiquidacion')).toContainText('Acceso denegado');
    await capturar(page, 'cp-e2e-06-acceso-denegado');
  });

  test('CP-E2E-07 Una puntuacion de 0,2 rechaza el inicio de sesion y no abre sesion', async ({ page }) => {
    await abrir(page);
    await iniciarSesion(page, 'nomina', 'martillo2026', 0.2);
    await expect(page.locator('#aviso')).toContainText('No fue posible iniciar sesión');
    await expect(page.locator('#sesionActual')).toHaveText('Sin sesión');
    await capturar(page, 'cp-e2e-07-recaptcha-rechazado');
  });

  test('CP-E2E-08 Una contrasena incorrecta con puntuacion alta tambien se rechaza', async ({ page }) => {
    await abrir(page);
    await iniciarSesion(page, 'nomina', 'clave-incorrecta', 0.9);
    await expect(page.locator('#sesionActual')).toHaveText('Sin sesión');
  });

  test('CP-E2E-09 El auditor ve la pista de auditoria con los rechazos registrados', async ({ page }) => {
    await abrir(page);
    await iniciarSesion(page, 'nomina', 'martillo2026', 0.2);
    await iniciarSesion(page, 'auditor', 'martillo2026', 0.9);
    await expect(page.locator('#sesionActual')).toHaveText('auditor · AUDITOR');

    const pista = page.locator('#pistaAuditoria');
    await expect(pista).toContainText('LOGIN_RECHAZADO');
    await expect(pista).toContainText('LOGIN_ACEPTADO');
    await capturar(page, 'cp-e2e-09-pista-auditoria');
  });
});
