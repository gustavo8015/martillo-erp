/* Sprint 3 · alto contraste (HU-10), inventario (HU-08),
   liquidación (HU-07) y control de acceso con reCAPTCHA (HU-09, HU-11).
   La lógica de pantalla vive aquí; el transporte sigue en api.js. */
(function () {
  const $ = (sel) => document.querySelector(sel);
  const pesos = new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 });

  function avisar(texto, tipo) {
    const caja = $('#aviso');
    if (!caja) return;
    caja.textContent = texto;
    caja.className = 'aviso visible ' + (tipo || '');
    setTimeout(() => { caja.className = 'aviso ' + (tipo || ''); }, 3800);
  }

  /* ================= HU-10 · Modo de alto contraste ================= */
  const CLAVE_TEMA = 'martillo.tema';

  function aplicarTema(tema) {
    const alto = tema === 'alto-contraste';
    if (alto) document.documentElement.setAttribute('data-tema', 'alto-contraste');
    else document.documentElement.removeAttribute('data-tema');

    const boton = $('#btnContraste');
    if (boton) {
      boton.setAttribute('aria-pressed', String(alto));
      boton.textContent = alto ? 'Contraste normal' : 'Alto contraste';
    }
    try { localStorage.setItem(CLAVE_TEMA, alto ? 'alto-contraste' : 'normal'); } catch (e) { /* sin almacenamiento */ }
  }

  function iniciarTema() {
    let guardado = 'normal';
    try { guardado = localStorage.getItem(CLAVE_TEMA) || 'normal'; } catch (e) { /* sin almacenamiento */ }
    aplicarTema(guardado);

    const boton = $('#btnContraste');
    if (!boton) return;
    boton.addEventListener('click', () => {
      const activo = document.documentElement.getAttribute('data-tema') === 'alto-contraste';
      aplicarTema(activo ? 'normal' : 'alto-contraste');
      avisar(activo ? 'Contraste normal activado' : 'Alto contraste activado');
    });
  }

  /* ================= Sesión y rol ================= */
  let sesion = null;

  function rol() {
    // Sin sesión iniciada la interfaz se comporta como el rol administrador,
    // que segun la matriz no alcanza la nomina: es justamente la demostracion
    // de HU-09. Para liquidar hay que iniciar sesion como responsable de nomina.
    return sesion ? sesion.rol : 'ADMINISTRADOR';
  }

  function esAccesoDenegado(mensaje) {
    return /acceso|denegad|403/i.test(String(mensaje));
  }

  function pista(modulo) {
    const usuario = modulo === 'NOMINA' ? 'nomina' : 'admin';
    return 'Acceso denegado. Este modulo solo responde al rol correspondiente: '
      + 'inicie sesion en la pestana Seguridad con el usuario ' + usuario + '.';
  }

  function pintarSesion() {
    const etiqueta = $('#sesionActual');
    if (etiqueta) etiqueta.textContent = sesion ? sesion.usuario + ' · ' + sesion.rol : 'Sin sesión';
  }

  /* ================= HU-08 · Inventario y stock crítico ================= */
  let inventario = [];
  let alertas = [];

  function pintarInventario() {
    const cuerpo = $('#cuerpoInventario');
    if (!cuerpo) return;
    cuerpo.innerHTML = inventario.map((i) => `
      <tr>
        <td class="id">${i.codigo}</td>
        <td>${i.nombre}</td>
        <td class="num">${i.stock}</td>
        <td class="num">${i.umbralCritico}</td>
        <td class="${i.estado === 'CRITICO' ? 'critico' : 'normal'}">${i.estado === 'CRITICO' ? 'Crítico' : 'Normal'}</td>
      </tr>`).join('');

    const selector = $('#movItem');
    if (selector && selector.options.length !== inventario.length) {
      selector.innerHTML = inventario.map((i) => `<option value="${i.codigo}">${i.nombre} (${i.codigo})</option>`).join('');
    }
  }

  function pintarAlertas() {
    const cuenta = $('#cuentaAlertas');
    if (cuenta) cuenta.textContent = String(alertas.length);

    const caja = $('#listaAlertas');
    if (!caja) return;
    caja.innerHTML = alertas.length
      ? '<h3 style="font-size:14px;margin:0 0 8px">Alertas pendientes</h3>'
        + alertas.map((a) => `<p class="critico">${a.mensaje}</p>`).join('')
      : '<p class="vacio">No hay alertas de stock crítico.</p>';
  }

  async function cargarInventario() {
    try {
      // Si el backend no responde, api.js queda en modo demostracion; hay que
      // resolverlo antes de la primera lectura para no mostrar la tabla vacia.
      if (!API.enDemo) await API.verificarConexion();
      inventario = await API.listarItems(rol());
      alertas = await API.listarAlertas(rol());
      pintarInventario();
      pintarAlertas();
    } catch (e) {
      inventario = [];
      alertas = [];
      pintarInventario();
      pintarAlertas();
      const cuerpo = $('#cuerpoInventario');
      if (cuerpo && esAccesoDenegado(e.message)) {
        cuerpo.innerHTML = '<tr><td colspan="5" class="nota-seguridad">' + pista('INVENTARIO') + '</td></tr>';
      } else if (!esAccesoDenegado(e.message)) {
        avisar(e.message, 'fallo');
      }
    }
  }

  async function moverInventario(tipo) {
    const codigo = $('#movItem').value;
    const cantidad = Number($('#movCantidad').value);
    if (!(cantidad > 0)) {
      $('#errMovCantidad').textContent = 'La cantidad debe ser mayor a 0';
      return;
    }
    $('#errMovCantidad').textContent = '';
    try {
      const item = tipo === 'salida'
        ? await API.registrarSalida(codigo, cantidad, rol())
        : await API.registrarEntrada(codigo, cantidad, rol());
      await cargarInventario();
      avisar(item.estado === 'CRITICO'
        ? 'Stock crítico: ' + item.nombre + ' quedó en ' + item.stock + ' unidades'
        : 'Movimiento registrado. Stock actual: ' + item.stock,
        item.estado === 'CRITICO' ? 'fallo' : 'exito');
    } catch (e) {
      avisar(e.message, 'fallo');
    }
  }

  /* ================= HU-07 · Liquidación ================= */
  function pintarLiquidacion(l) {
    $('#resultadoLiquidacion').innerHTML = `
      <dl>
        <dt>Días trabajados</dt><dd>${l.diasTrabajados}</dd>
        <dt>Base de cesantías y prima</dt><dd>${pesos.format(l.baseCesantias)}</dd>
        <dt>Cesantías</dt><dd>${pesos.format(l.cesantias)}</dd>
        <dt>Intereses sobre cesantías</dt><dd>${pesos.format(l.interesesCesantias)}</dd>
        <dt>Prima de servicios</dt><dd>${pesos.format(l.prima)}</dd>
        <dt>Vacaciones</dt><dd>${pesos.format(l.vacaciones)}</dd>
        <dt>Indemnización</dt><dd>${pesos.format(l.indemnizacion)}</dd>
      </dl>
      <dl class="total"><dt>Total a pagar</dt><dd>${pesos.format(l.total)}</dd></dl>`;
  }

  function alternarFinPactado() {
    const tipo = $('#liqTipo').value;
    $('#cajaFinPactado').hidden = tipo === 'INDEFINIDO';
  }

  async function calcularLiquidacion(evento) {
    evento.preventDefault();
    const tipo = $('#liqTipo').value;
    const peticion = {
      tipoContrato: tipo,
      salarioMensual: Number($('#liqSalario').value),
      fechaIngreso: $('#liqIngreso').value,
      fechaRetiro: $('#liqRetiro').value,
      finPactado: tipo === 'INDEFINIDO' ? null : $('#liqFinPactado').value,
      motivo: $('#liqMotivo').value
    };

    if (!(peticion.salarioMensual > 0)) {
      $('#errLiqSalario').textContent = 'El salario debe ser mayor a 0';
      return;
    }
    $('#errLiqSalario').textContent = '';

    if (tipo !== 'INDEFINIDO' && !peticion.finPactado) {
      $('#errLiqFinPactado').textContent = 'El contrato ' + tipo + ' exige vencimiento pactado';
      return;
    }
    $('#errLiqFinPactado').textContent = '';

    try {
      pintarLiquidacion(await API.liquidar(peticion, rol()));
      avisar('Liquidación calculada', 'exito');
    } catch (e) {
      const texto = esAccesoDenegado(e.message) ? pista('NOMINA') : e.message;
      $('#resultadoLiquidacion').innerHTML = '<p class="critico">' + texto + '</p>';
      avisar(esAccesoDenegado(e.message) ? 'Acceso denegado al modulo de nomina' : e.message, 'fallo');
    }
  }

  /* ================= HU-09 · Matriz de acceso ================= */
  const MATRIZ = [
    ['Administración (usuarios y roles)', 'Sí', 'No', 'No', 'No'],
    ['Consignantes y catálogo', 'Sí', 'No', 'Sí', 'Lectura'],
    ['Inventario', 'Sí', 'No', 'Sí', 'Lectura'],
    ['Nómina y liquidación', 'No', 'Sí', 'No', 'No'],
    ['Auditoría', 'No', 'No', 'No', 'Sí']
  ];

  function pintarMatriz() {
    const cuerpo = $('#cuerpoMatriz');
    if (!cuerpo) return;
    cuerpo.innerHTML = MATRIZ.map((fila) => '<tr><td>' + fila[0] + '</td>'
      + fila.slice(1).map((v) => `<td class="${v === 'No' ? 'no' : v === 'Sí' ? 'si' : ''}">${v}</td>`).join('')
      + '</tr>').join('');
  }

  async function pintarAuditoria() {
    const caja = $('#pistaAuditoria');
    if (!caja) return;
    try {
      const registros = await API.auditoria(rol());
      caja.innerHTML = '<h3 style="font-size:14px;margin:0 0 8px">Pista de auditoría</h3>'
        + registros.slice(0, 8).map((r) => `<p style="margin:2px 0;font-size:12.5px">
            ${r.momento ? String(r.momento).replace('T', ' ').slice(0, 19) : ''} ·
            ${r.usuario} · ${r.evento} · ${r.detalle || ''}</p>`).join('');
    } catch (e) {
      caja.innerHTML = '<p class="nota-seguridad">La pista de auditoría solo es visible para el rol auditor.</p>';
    }
  }

  /* ================= HU-11 · Inicio de sesión ================= */
  async function iniciarSesion(evento) {
    evento.preventDefault();
    const usuario = $('#logUsuario').value.trim();
    const contrasena = $('#logClave').value;
    const puntuacion = Number($('#logPuntuacion').value);

    if (!usuario) { $('#errLogUsuario').textContent = 'El usuario es obligatorio'; return; }
    $('#errLogUsuario').textContent = '';
    if (!contrasena) { $('#errLogClave').textContent = 'La contraseña es obligatoria'; return; }
    $('#errLogClave').textContent = '';

    try {
      // En el sistema publicado el token lo emite Google con grecaptcha.execute
      // para la acción "login"; aquí se simula para la demostración.
      const token = await API.tokenRecaptcha('login', puntuacion);
      sesion = await API.login({ usuario, contrasena, tokenRecaptcha: token });
      pintarSesion();
      avisar('Sesión iniciada como ' + sesion.rol, 'exito');
      await cargarInventario();
      await pintarAuditoria();
    } catch (e) {
      sesion = null;
      pintarSesion();
      avisar('No fue posible iniciar sesión con los datos proporcionados', 'fallo');
    }
  }

  /* ================= Arranque ================= */
  document.addEventListener('DOMContentLoaded', () => {
    iniciarTema();
    pintarMatriz();
    pintarSesion();
    cargarInventario().then(pintarAuditoria);

    const formMovimiento = $('#formMovimiento');
    if (formMovimiento) {
      formMovimiento.addEventListener('submit', (e) => { e.preventDefault(); moverInventario('salida'); });
      $('#btnEntrada').addEventListener('click', () => moverInventario('entrada'));
      $('#recargarInventario').addEventListener('click', cargarInventario);
      $('#campanaAlertas').addEventListener('click', () => {
        document.querySelector('.pestana[data-panel="panelInventario"]').click();
        pintarAlertas();
      });
    }

    const formLiquidacion = $('#formLiquidacion');
    if (formLiquidacion) {
      formLiquidacion.addEventListener('submit', calcularLiquidacion);
      $('#liqTipo').addEventListener('change', alternarFinPactado);
      alternarFinPactado();
    }

    const formLogin = $('#formLogin');
    if (formLogin) {
      formLogin.addEventListener('submit', iniciarSesion);
      $('#logPuntuacion').addEventListener('input', (e) => {
        $('#valorPuntuacion').textContent = Number(e.target.value).toFixed(1).replace('.', ',');
      });
      $('#btnSalir').addEventListener('click', () => {
        sesion = null; pintarSesion(); avisar('Sesión cerrada');
      });
    }
  });
})();
