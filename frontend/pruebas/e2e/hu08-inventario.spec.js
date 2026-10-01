const { test, expect } = require('@playwright/test');
const { abrir, pestana, capturar } = require('./apoyo');

test.describe('HU-08 Alertar el stock critico del inventario', () => {
  test('CP-E2E-04 Una salida de 16 cajas deja EMB-001 en critico y enciende la campana', async ({ page }) => {
    await abrir(page);
    await pestana(page, 'panelInventario');

    await page.selectOption('#movItem', 'EMB-001');
    await page.fill('#movCantidad', '16');
    await page.locator('#formMovimiento button[type="submit"]').click();

    const fila = page.locator('#cuerpoInventario tr', { hasText: 'EMB-001' });
    await expect(fila).toContainText('24');
    await expect(fila).toContainText('Crítico');
    await expect(page.locator('#cuentaAlertas')).toHaveText('1');
    await expect(page.locator('#listaAlertas')).toContainText('por debajo del umbral');
    await capturar(page, 'cp-e2e-04-stock-critico');
  });

  test('CP-E2E-05 Una segunda salida no repite la alerta y la reposicion la cierra', async ({ page }) => {
    await abrir(page);
    await pestana(page, 'panelInventario');
    await page.selectOption('#movItem', 'EMB-001');

    await page.fill('#movCantidad', '16');
    await page.locator('#formMovimiento button[type="submit"]').click();
    await expect(page.locator('#cuentaAlertas')).toHaveText('1');

    await page.fill('#movCantidad', '2');
    await page.locator('#formMovimiento button[type="submit"]').click();
    await expect(page.locator('#cuerpoInventario tr', { hasText: 'EMB-001' })).toContainText('22');
    await expect(page.locator('#cuentaAlertas')).toHaveText('1');

    await page.fill('#movCantidad', '10');
    await page.locator('#btnEntrada').click();
    const fila = page.locator('#cuerpoInventario tr', { hasText: 'EMB-001' });
    await expect(fila).toContainText('32');
    await expect(fila).toContainText('Normal');
    await expect(page.locator('#cuentaAlertas')).toHaveText('0');
  });
});
