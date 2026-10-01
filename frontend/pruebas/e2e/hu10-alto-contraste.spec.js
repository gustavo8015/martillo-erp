const { test, expect } = require('@playwright/test');
const { abrir, pestana, capturar, contraste } = require('./apoyo');

test.describe('HU-10 Activar el modo de alto contraste', () => {
  test('CP-E2E-10 El boton activa el tema, actualiza aria-pressed y se conserva al recargar', async ({ page }) => {
    await abrir(page);
    const boton = page.locator('#btnContraste');
    await expect(boton).toHaveAttribute('aria-pressed', 'false');

    await boton.click();
    await expect(page.locator('html')).toHaveAttribute('data-tema', 'alto-contraste');
    await expect(boton).toHaveAttribute('aria-pressed', 'true');
    await expect(boton).toHaveText('Contraste normal');

    await page.reload();
    await page.locator('#cuerpoInventario tr').first().waitFor({ state: 'attached' });
    await expect(page.locator('html')).toHaveAttribute('data-tema', 'alto-contraste');
    await pestana(page, 'panelInventario');
    await capturar(page, 'cp-e2e-10-alto-contraste');
  });

  test('CP-E2E-11 El texto principal alcanza 7:1 de contraste en el modo de alto contraste (WCAG 1.4.6, AAA)', async ({ page }) => {
    await abrir(page);
    await page.locator('#btnContraste').click();
    await pestana(page, 'panelNomina');

    const pares = await page.evaluate(() => {
      const leer = (sel) => {
        const el = document.querySelector(sel);
        const cs = getComputedStyle(el);
        let fondo = cs.backgroundColor;
        let actual = el;
        while (fondo === 'rgba(0, 0, 0, 0)' && actual.parentElement) {
          actual = actual.parentElement;
          fondo = getComputedStyle(actual).backgroundColor;
        }
        return { sel, color: cs.color, fondo };
      };
      return ['body', '#panelNomina h2', '#panelNomina label', '.pestana.activa', '#panelNomina .ayuda']
        .map(leer);
    });

    for (const p of pares) {
      const relacion = contraste(p.color, p.fondo);
      expect(relacion, `${p.sel}: ${p.color} sobre ${p.fondo}`).toBeGreaterThanOrEqual(7);
    }
  });

  test('CP-E2E-12 Las pestanas se recorren con el teclado y el foco es visible', async ({ page }) => {
    await abrir(page);
    await page.locator('#btnContraste').click();
    await page.locator('.pestana[data-panel="panelPiezas"]').focus();
    await page.keyboard.press('Tab');
    const enfocado = page.locator(':focus');
    await expect(enfocado).toHaveAttribute('data-panel', 'panelConsignantes');
    const contorno = await enfocado.evaluate((el) => getComputedStyle(el).outlineStyle);
    expect(contorno).not.toBe('none');
  });
});
