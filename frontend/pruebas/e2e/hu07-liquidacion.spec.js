const { test, expect } = require('@playwright/test');
const { abrir, pestana, iniciarSesion, capturar } = require('./apoyo');

test.describe('HU-07 Liquidar un contrato de trabajo segun su tipo', () => {
  test('CP-E2E-01 El responsable de nomina liquida el caso de referencia por $6.372.159', async ({ page }) => {
    await abrir(page);
    await iniciarSesion(page, 'nomina', 'martillo2026', 0.9);
    await expect(page.locator('#sesionActual')).toHaveText('nomina · RESPONSABLE_NOMINA');

    await pestana(page, 'panelNomina');
    await page.locator('#formLiquidacion button[type="submit"]').click();

    const total = page.locator('#resultadoLiquidacion .total dd');
    await expect(total).toContainText('6.372.159');
    await expect(page.locator('#resultadoLiquidacion')).toContainText('2.061.821');
    await capturar(page, 'cp-e2e-01-liquidacion-indefinido');
  });

  test('CP-E2E-02 El contrato a termino fijo muestra el vencimiento pactado y paga $11.372.159', async ({ page }) => {
    await abrir(page);
    await iniciarSesion(page, 'nomina', 'martillo2026', 0.9);
    await pestana(page, 'panelNomina');

    await page.selectOption('#liqTipo', 'FIJO');
    await expect(page.locator('#cajaFinPactado')).toBeVisible();
    await page.locator('#formLiquidacion button[type="submit"]').click();

    await expect(page.locator('#resultadoLiquidacion .total dd')).toContainText('11.372.159');
    await capturar(page, 'cp-e2e-02-liquidacion-fijo');
  });

  test('CP-E2E-03 Un salario en cero se detiene en el formulario con su mensaje', async ({ page }) => {
    await abrir(page);
    await iniciarSesion(page, 'nomina', 'martillo2026', 0.9);
    await pestana(page, 'panelNomina');

    await page.fill('#liqSalario', '0');
    await page.locator('#formLiquidacion button[type="submit"]').click();
    await expect(page.locator('#errLiqSalario')).toHaveText('El salario debe ser mayor a 0');
  });
});
