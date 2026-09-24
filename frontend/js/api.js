/* Capa de acceso a la API REST de Martillo ERP.
   Separada de la lógica de pantalla (SRP): si cambia la URL o el transporte,
   solo se modifica este archivo. */
const API = (function () {
  const BASE = (window.MARTILLO_API_URL || 'http://localhost:8080') + '/api';
  let modoDemo = false;

  // Datos de respaldo para poder demostrar la interfaz sin el backend levantado.
  const demo = {
    piezas: [
      { id: 1, nombre: 'Jarrón de porcelana', descripcion: 'Jarrón chino del siglo XIX', categoria: 'Antigüedades', precioReserva: 1500000, estado: 'DISPONIBLE', fechaRegistro: '2026-09-01T09:15:00' },
      { id: 2, nombre: 'Óleo sobre lienzo, paisaje', descripcion: 'Escuela colombiana, firmado', categoria: 'Arte', precioReserva: 4200000, estado: 'DISPONIBLE', fechaRegistro: '2026-09-01T10:02:00' },
      { id: 3, nombre: 'Reloj de bolsillo en oro', descripcion: 'Movimiento suizo, con certificado', categoria: 'Joyería', precioReserva: 2800000, estado: 'DISPONIBLE', fechaRegistro: '2026-09-02T08:40:00' }
    ],
    consignantes: [
      { id: 1, nombreCompleto: 'Laura Gómez', tipoDocumento: 'CC', numeroDocumento: '123456789', telefono: '3001234567', email: 'laura@example.com', direccion: 'Calle 10 # 20-30, Bogotá', fechaRegistro: '2026-09-01T09:00:00' },
      { id: 2, nombreCompleto: 'Fundación Casa Antigua', tipoDocumento: 'NIT', numeroDocumento: '900123456-7', telefono: '6017654321', email: 'contacto@casaantigua.org', direccion: 'Carrera 7 # 45-12, Bogotá', fechaRegistro: '2026-09-01T11:30:00' }
    ],
    // Sprint 3: insumos de bodega con su umbral crítico (HU-08)
    inventario: [
      { id: 1, codigo: 'EMB-001', nombre: 'Cajas de embalaje', stock: 40, umbralCritico: 25, estado: 'NORMAL' },
      { id: 2, codigo: 'EMB-002', nombre: 'Protecciones de espuma', stock: 120, umbralCritico: 40, estado: 'NORMAL' },
      { id: 3, codigo: 'CAT-001', nombre: 'Catálogos impresos', stock: 300, umbralCritico: 100, estado: 'NORMAL' },
      { id: 4, codigo: 'ETQ-001', nombre: 'Etiquetas de lote', stock: 60, umbralCritico: 50, estado: 'NORMAL' }
    ],
    alertas: [],
    auditoria: [],
    // Parámetros legales con fecha de vigencia (ADR-005)
    vigencias: [
      { desde: '2025-01-01', smmlv: 1423500, auxilio: 200000 },
      { desde: '2026-01-01', smmlv: 1750905, auxilio: 249095 }
    ],
    usuarios: { admin: 'ADMINISTRADOR', nomina: 'RESPONSABLE_NOMINA', catalogo: 'CATALOGADOR', auditor: 'AUDITOR' },
    matriz: {
      ADMINISTRADOR:      { ADMINISTRACION: 'TOTAL', CONSIGNANTES_Y_CATALOGO: 'TOTAL', INVENTARIO: 'TOTAL' },
      RESPONSABLE_NOMINA: { NOMINA: 'TOTAL' },
      CATALOGADOR:        { CONSIGNANTES_Y_CATALOGO: 'TOTAL', INVENTARIO: 'TOTAL' },
      AUDITOR:            { CONSIGNANTES_Y_CATALOGO: 'LECTURA', INVENTARIO: 'LECTURA', AUDITORIA: 'TOTAL' }
    }
  };

  /* ---------- Cálculo local de respaldo para el modo demostración ---------- */
  const NIVEL = { NINGUNO: 0, LECTURA: 1, TOTAL: 2 };

  function permitir(rol, modulo, requerido) {
    const permiso = (demo.matriz[rol] || {})[modulo] || 'NINGUNO';
    const ok = NIVEL[permiso] >= NIVEL[requerido];
    demo.auditoria.unshift({
      momento: new Date().toISOString(), usuario: rol || 'desconocido',
      evento: ok ? 'ACCESO_PERMITIDO' : 'ACCESO_DENEGADO', detalle: modulo, permitido: ok
    });
    if (!ok) throw new Error('El rol no tiene acceso al modulo ' + modulo);
  }

  function dia(f) {
    const ultimo = new Date(f.getFullYear(), f.getMonth() + 1, 0).getDate();
    return f.getDate() === ultimo ? 30 : f.getDate();
  }

  function dias360(a, b) {
    if (b < a) return 0;
    return (b.getFullYear() - a.getFullYear()) * 360
      + (b.getMonth() - a.getMonth()) * 30
      + (dia(b) - dia(a)) + 1;
  }

  function vigenciaDe(fecha) {
    return demo.vigencias.filter((v) => new Date(v.desde) <= fecha).pop() || demo.vigencias[0];
  }

  /* Mismas reglas que el motor en Java; permite demostrar sin el backend. */
  function liquidarLocal(p) {
    const ingreso = new Date(p.fechaIngreso + 'T00:00:00');
    const retiro = new Date(p.fechaRetiro + 'T00:00:00');
    if (retiro < ingreso) throw new Error('La fecha de retiro no puede ser anterior al ingreso');

    const v = vigenciaDe(retiro);
    const salario = Number(p.salarioMensual);
    const base = salario <= v.smmlv * 2 ? salario + v.auxilio : salario;

    const inicioAnio = new Date(retiro.getFullYear(), 0, 1);
    const inicioSemestre = new Date(retiro.getFullYear(), retiro.getMonth() < 6 ? 0 : 6, 1);
    const diasTotal = dias360(ingreso, retiro);
    const diasAnio = dias360(ingreso > inicioAnio ? ingreso : inicioAnio, retiro);
    const diasSemestre = dias360(ingreso > inicioSemestre ? ingreso : inicioSemestre, retiro);

    const cesantias = Math.round(base * diasAnio / 360);
    const intereses = Math.round(cesantias * diasAnio * 0.12 / 360);
    const prima = Math.round(base * diasSemestre / 360);
    const vacaciones = Math.round(salario * diasTotal / 720);

    let diasIndemnizacion = 0;
    if (p.motivo === 'SIN_JUSTA_CAUSA') {
      const fin = p.finPactado ? new Date(p.finPactado + 'T00:00:00') : null;
      const siguiente = new Date(retiro.getTime() + 86400000);
      if (p.tipoContrato === 'FIJO') diasIndemnizacion = Math.max(0, fin ? dias360(siguiente, fin) : 0);
      else if (p.tipoContrato === 'OBRA_LABOR') diasIndemnizacion = Math.max(15, fin ? dias360(siguiente, fin) : 0);
      else {
        const alto = salario >= v.smmlv * 10;
        diasIndemnizacion = alto ? 20 : 30;
        if (diasTotal > 360) diasIndemnizacion += (alto ? 15 : 20) * (diasTotal - 360) / 360;
      }
    }
    const indemnizacion = Math.round(salario * diasIndemnizacion / 30);

    return {
      diasTrabajados: diasTotal, diasAnio: diasAnio, diasSemestre: diasSemestre,
      baseCesantias: base, cesantias: cesantias, interesesCesantias: intereses,
      prima: prima, vacaciones: vacaciones, indemnizacion: indemnizacion,
      total: cesantias + intereses + prima + vacaciones + indemnizacion
    };
  }

  function siguienteId(lista) {
    return lista.reduce((max, e) => Math.max(max, e.id), 0) + 1;
  }

  /* El rol viaja en una cabecera y el servidor lo verifica; la interfaz
     nunca decide por su cuenta si el acceso procede (HU-09). */
  function cabeceras(rol) {
    return { 'Content-Type': 'application/json', 'X-Rol': rol || '' };
  }

  async function pedir(ruta, opciones) {
    const respuesta = await fetch(BASE + ruta, Object.assign({
      headers: { 'Content-Type': 'application/json' }
    }, opciones));
    if (!respuesta.ok) {
      let detalle = 'Error ' + respuesta.status;
      try {
        const cuerpo = await respuesta.json();
        detalle = cuerpo.mensaje || cuerpo.message || (cuerpo.errores && cuerpo.errores.join(', ')) || detalle;
      } catch (e) { /* la respuesta no traía cuerpo JSON */ }
      throw new Error(detalle);
    }
    return respuesta.status === 204 ? null : respuesta.json();
  }

  return {
    get enDemo() { return modoDemo; },

    async verificarConexion() {
      try {
        await pedir('/consignantes', { method: 'GET' });
        modoDemo = false;
      } catch (e) {
        modoDemo = true;
      }
      return !modoDemo;
    },

    async listarPiezas() {
      if (modoDemo) return demo.piezas.slice();
      return pedir('/piezas', { method: 'GET' });
    },

    async crearPieza(datos) {
      if (modoDemo) {
        const nueva = Object.assign({}, datos, {
          id: siguienteId(demo.piezas), estado: 'DISPONIBLE', fechaRegistro: new Date().toISOString()
        });
        demo.piezas.push(nueva);
        return nueva;
      }
      return pedir('/piezas', { method: 'POST', body: JSON.stringify(datos) });
    },

    async listarConsignantes() {
      if (modoDemo) return demo.consignantes.slice();
      return pedir('/consignantes', { method: 'GET' });
    },

    async crearConsignante(datos) {
      if (modoDemo) {
        const nuevo = Object.assign({}, datos, {
          id: siguienteId(demo.consignantes), fechaRegistro: new Date().toISOString()
        });
        demo.consignantes.push(nuevo);
        return nuevo;
      }
      return pedir('/consignantes', { method: 'POST', body: JSON.stringify(datos) });
    },

    /* ---------- HU-07 · Liquidación ---------- */
    async liquidar(peticion, rol) {
      if (modoDemo) {
        permitir(rol, 'NOMINA', 'TOTAL');
        return liquidarLocal(peticion);
      }
      return pedir('/nomina/liquidaciones', {
        method: 'POST', headers: cabeceras(rol), body: JSON.stringify(peticion)
      });
    },

    /* ---------- HU-08 · Inventario ---------- */
    async listarItems(rol) {
      if (modoDemo) { permitir(rol, 'INVENTARIO', 'LECTURA'); return demo.inventario.slice(); }
      return pedir('/inventario/items', { method: 'GET', headers: cabeceras(rol) });
    },

    async listarAlertas(rol) {
      if (modoDemo) { permitir(rol, 'INVENTARIO', 'LECTURA'); return demo.alertas.slice(); }
      return pedir('/inventario/alertas', { method: 'GET', headers: cabeceras(rol) });
    },

    async registrarSalida(codigo, cantidad, rol) {
      if (modoDemo) {
        permitir(rol, 'INVENTARIO', 'TOTAL');
        const item = demo.inventario.find((i) => i.codigo === codigo);
        if (!item) throw new Error('No existe el item de inventario ' + codigo);
        if (cantidad > item.stock) throw new Error('No hay stock suficiente de ' + codigo);
        item.stock -= cantidad;
        if (item.stock < item.umbralCritico && item.estado === 'NORMAL') {
          item.estado = 'CRITICO';
          demo.alertas.unshift({
            codigoItem: item.codigo, stockAlGenerar: item.stock, momento: new Date().toISOString(),
            mensaje: 'El item ' + item.nombre + ' quedo en ' + item.stock
              + ' unidades, por debajo del umbral critico de ' + item.umbralCritico
          });
        }
        return Object.assign({}, item);
      }
      return pedir('/inventario/salidas', {
        method: 'POST', headers: cabeceras(rol), body: JSON.stringify({ codigo, cantidad })
      });
    },

    async registrarEntrada(codigo, cantidad, rol) {
      if (modoDemo) {
        permitir(rol, 'INVENTARIO', 'TOTAL');
        const item = demo.inventario.find((i) => i.codigo === codigo);
        if (!item) throw new Error('No existe el item de inventario ' + codigo);
        item.stock += cantidad;
        if (item.stock >= item.umbralCritico && item.estado === 'CRITICO') {
          item.estado = 'NORMAL';
          demo.alertas = demo.alertas.filter((a) => a.codigoItem !== item.codigo);
        }
        return Object.assign({}, item);
      }
      return pedir('/inventario/entradas', {
        method: 'POST', headers: cabeceras(rol), body: JSON.stringify({ codigo, cantidad })
      });
    },

    /* ---------- HU-09 y HU-11 · Seguridad ---------- */
    async tokenRecaptcha(accion, puntuacionSimulada) {
      // En el sistema publicado: grecaptcha.execute(claveSitio, { action: accion }).
      // Mientras no haya claves registradas, se emite un token de demostración
      // que el servidor interpreta con la misma regla de puntuación.
      if (window.grecaptcha && window.MARTILLO_RECAPTCHA_SITIO) {
        return window.grecaptcha.execute(window.MARTILLO_RECAPTCHA_SITIO, { action: accion });
      }
      return 'demo:' + accion + ':' + puntuacionSimulada;
    },

    async login(credenciales) {
      if (modoDemo) {
        const partes = String(credenciales.tokenRecaptcha || '').split(':');
        const accion = partes[1];
        const puntuacion = Number(partes[2]);
        if (accion !== 'login' || !(puntuacion >= 0.5)) {
          demo.auditoria.unshift({
            momento: new Date().toISOString(), usuario: credenciales.usuario,
            evento: 'LOGIN_RECHAZADO', detalle: 'Verificacion reCAPTCHA con puntuacion ' + puntuacion, permitido: false
          });
          throw new Error('No fue posible iniciar sesion con los datos proporcionados');
        }
        const rol = demo.usuarios[String(credenciales.usuario || '').toLowerCase()];
        if (!rol || credenciales.contrasena !== 'martillo2026') {
          demo.auditoria.unshift({
            momento: new Date().toISOString(), usuario: credenciales.usuario,
            evento: 'LOGIN_RECHAZADO', detalle: 'Credenciales invalidas', permitido: false
          });
          throw new Error('No fue posible iniciar sesion con los datos proporcionados');
        }
        demo.auditoria.unshift({
          momento: new Date().toISOString(), usuario: credenciales.usuario,
          evento: 'LOGIN_ACEPTADO', detalle: 'Puntuacion ' + puntuacion, permitido: true
        });
        return { usuario: credenciales.usuario, rol: rol, menu: demo.matriz[rol] };
      }
      return pedir('/seguridad/login', { method: 'POST', body: JSON.stringify(credenciales) });
    },

    async auditoria(rol) {
      if (modoDemo) { permitir(rol, 'AUDITORIA', 'TOTAL'); return demo.auditoria.slice(0, 50); }
      return pedir('/seguridad/auditoria', { method: 'GET', headers: cabeceras(rol) });
    }
  };
})();
