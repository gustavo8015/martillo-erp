const { test, expect } = require('@playwright/test');
const { pestana, iniciarSesion, capturar } = require('./apoyo');

// Regresion del defecto D-01 encontrado en la Guia 8: cuando el servidor
// responde 400 con el detalle por campo, la interfaz mostraba solo
// "Error de validacion". La respuesta simulada es la misma que devuelve el
// backend en la prueba de integracion CP-07.4.
test('CP-E2E-13 (D-01) La interfaz muestra el detalle de validacion que envia el servidor', async ({ page }) => {
  await page.route('**/api/**', async (ruta) => {
    const url = ruta.request().url();
    const json = (status, body) => ruta.fulfill({ status, contentType: 'application/json', body: JSON.stringify(body) });
    if (url.endsWith('/api/consignantes')) return json(200, []);
    if (url.endsWith('/api/piezas')) return json(200, []);
    if (url.endsWith('/api/inventario/items')) return json(200, [
      { id: 1, codigo: 'EMB-001', nombre: 'Cajas de embalaje', stock: 40, umbralCritico: 25, estado: 'NORMAL' }]);
    if (url.endsWith('/api/inventario/alertas')) return json(200, []);
    if (url.endsWith('/api/seguridad/auditoria')) return json(403, { mensaje: 'El rol no tiene acceso al modulo AUDITORIA' });
    if (url.endsWith('/api/seguridad/login')) return json(200, { usuario: 'nomina', rol: 'RESPONSABLE_NOMINA', menu: { NOMINA: 'TOTAL' } });
    if (url.endsWith('/api/nomina/liquidaciones')) return json(400, {
      mensaje: 'Error de validacion',
      errores: { fechaIngreso: 'La fecha de ingreso es obligatoria' }
    });
    return json(404, { mensaje: 'No encontrado' });
  });

  await page.goto('/index.html');
  await page.locator('#cuerpoInventario tr').first().waitFor({ state: 'attached' });
  await iniciarSesion(page, 'nomina', 'martillo2026', 0.9);
  await pestana(page, 'panelNomina');
  await page.fill('#liqIngreso', '');
  await page.locator('#formLiquidacion button[type="submit"]').click();

  await expect(page.locator('#resultadoLiquidacion')).toContainText('La fecha de ingreso es obligatoria');
  await capturar(page, 'cp-e2e-13-detalle-validacion');
});
