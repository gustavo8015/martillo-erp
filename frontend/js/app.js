/* Lógica de pantalla: validación, render de listados y manejo de eventos. */
(function () {
  const $ = (sel) => document.querySelector(sel);
  const pesos = new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 });

  let piezas = [];
  let consignantes = [];

  /* ---------- Avisos ---------- */
  let temporizador;
  function avisar(texto, tipo) {
    const caja = $('#aviso');
    caja.textContent = texto;
    caja.className = 'aviso visible ' + (tipo || '');
    clearTimeout(temporizador);
    temporizador = setTimeout(() => { caja.className = 'aviso ' + (tipo || ''); }, 3800);
  }

  /* ---------- Pestañas ---------- */
  document.querySelectorAll('.pestana').forEach((boton) => {
    boton.addEventListener('click', () => {
      document.querySelectorAll('.pestana').forEach((b) => {
        b.classList.remove('activa'); b.setAttribute('aria-selected', 'false');
      });
      document.querySelectorAll('.panel').forEach((p) => p.classList.remove('activo'));
      boton.classList.add('activa'); boton.setAttribute('aria-selected', 'true');
      $('#' + boton.dataset.panel).classList.add('activo');
    });
  });

  /* ---------- Validación ---------- */
  function marcar(campo, cajaError, mensaje) {
    campo.classList.toggle('invalido', Boolean(mensaje));
    if (cajaError) cajaError.textContent = mensaje || '';
    return !mensaje;
  }

  function validarPieza() {
    let ok = true;
    ok = marcar($('#piezaNombre'), $('#errPiezaNombre'),
      $('#piezaNombre').value.trim() ? '' : 'El nombre de la pieza es obligatorio') && ok;
    ok = marcar($('#piezaCategoria'), $('#errPiezaCategoria'),
      $('#piezaCategoria').value ? '' : 'La categoría es obligatoria') && ok;
    const precio = Number($('#piezaPrecio').value);
    ok = marcar($('#piezaPrecio'), $('#errPiezaPrecio'),
      precio > 0 ? '' : 'El precio de reserva debe ser mayor a 0') && ok;
    return ok;
  }

  function validarConsignante() {
    let ok = true;
    ok = marcar($('#conNombre'), $('#errConNombre'),
      $('#conNombre').value.trim() ? '' : 'El nombre completo es obligatorio') && ok;
    ok = marcar($('#conNumDoc'), $('#errConNumDoc'),
      $('#conNumDoc').value.trim() ? '' : 'El número de documento es obligatorio') && ok;
    ok = marcar($('#conTelefono'), $('#errConTelefono'),
      $('#conTelefono').value.trim() ? '' : 'El teléfono es obligatorio') && ok;
    const correo = $('#conEmail').value.trim();
    ok = marcar($('#conEmail'), $('#errConEmail'),
      (!correo || /^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(correo)) ? '' : 'El correo no tiene un formato válido') && ok;
    return ok;
  }

  /* ---------- Render ---------- */
  function texto(valor) {
    const div = document.createElement('div');
    div.textContent = valor == null ? '' : String(valor);
    return div.innerHTML;
  }

  function pintarPiezas() {
    const filtro = $('#buscarPieza').value.trim().toLowerCase();
    const visibles = piezas.filter((p) =>
      !filtro || (p.nombre + ' ' + p.categoria).toLowerCase().includes(filtro));
    $('#cuerpoPiezas').innerHTML = visibles.map((p) =>
      '<tr data-id="' + p.id + '">' +
        '<td class="id">' + texto(p.id) + '</td>' +
        '<td>' + texto(p.nombre) + '</td>' +
        '<td>' + texto(p.categoria) + '</td>' +
        '<td class="num">' + pesos.format(Number(p.precioReserva)) + '</td>' +
        '<td><span class="etiqueta">' + texto(p.estado || 'DISPONIBLE') + '</span></td>' +
      '</tr>').join('');
    $('#vacioPiezas').hidden = visibles.length > 0;
  }

  function pintarConsignantes() {
    const filtro = $('#buscarConsignante').value.trim().toLowerCase();
    const visibles = consignantes.filter((c) =>
      !filtro || (c.nombreCompleto + ' ' + c.numeroDocumento).toLowerCase().includes(filtro));
    $('#cuerpoConsignantes').innerHTML = visibles.map((c) =>
      '<tr data-id="' + c.id + '">' +
        '<td class="id">' + texto(c.id) + '</td>' +
        '<td>' + texto(c.nombreCompleto) + '</td>' +
        '<td>' + texto(c.tipoDocumento + ' ' + c.numeroDocumento) + '</td>' +
        '<td>' + texto(c.telefono) + '</td>' +
        '<td>' + texto(c.email || '') + '</td>' +
      '</tr>').join('');
    $('#vacioConsignantes').hidden = visibles.length > 0;
  }

  function destacar(contenedor, id) {
    const fila = document.querySelector(contenedor + ' tr[data-id="' + id + '"]');
    if (fila) fila.classList.add('nueva');
  }

  /* ---------- Carga ---------- */
  async function cargarPiezas() {
    try { piezas = await API.listarPiezas(); pintarPiezas(); }
    catch (e) { avisar('No se pudo cargar el catálogo: ' + e.message, 'fallo'); }
  }

  async function cargarConsignantes() {
    try { consignantes = await API.listarConsignantes(); pintarConsignantes(); }
    catch (e) { avisar('No se pudo cargar el listado: ' + e.message, 'fallo'); }
  }

  /* ---------- Envío de formularios ---------- */
  $('#formPieza').addEventListener('submit', async (evento) => {
    evento.preventDefault();
    if (!validarPieza()) { avisar('Revise los campos señalados', 'fallo'); return; }
    const datos = {
      nombre: $('#piezaNombre').value.trim(),
      descripcion: $('#piezaDescripcion').value.trim(),
      categoria: $('#piezaCategoria').value,
      precioReserva: Number($('#piezaPrecio').value)
    };
    try {
      const creada = await API.crearPieza(datos);
      await cargarPiezas();
      destacar('#cuerpoPiezas', creada.id);
      $('#formPieza').reset();
      avisar('Pieza registrada con el número ' + creada.id, 'exito');
    } catch (e) { avisar('No se pudo registrar la pieza: ' + e.message, 'fallo'); }
  });

  $('#formConsignante').addEventListener('submit', async (evento) => {
    evento.preventDefault();
    if (!validarConsignante()) { avisar('Revise los campos señalados', 'fallo'); return; }
    const datos = {
      nombreCompleto: $('#conNombre').value.trim(),
      tipoDocumento: $('#conTipoDoc').value,
      numeroDocumento: $('#conNumDoc').value.trim(),
      telefono: $('#conTelefono').value.trim(),
      email: $('#conEmail').value.trim(),
      direccion: $('#conDireccion').value.trim()
    };
    try {
      const creado = await API.crearConsignante(datos);
      await cargarConsignantes();
      destacar('#cuerpoConsignantes', creado.id);
      $('#formConsignante').reset();
      avisar('Consignante registrado con el número ' + creado.id, 'exito');
    } catch (e) { avisar('No se pudo registrar el consignante: ' + e.message, 'fallo'); }
  });

  $('#recargarPiezas').addEventListener('click', cargarPiezas);
  $('#recargarConsignantes').addEventListener('click', cargarConsignantes);
  $('#buscarPieza').addEventListener('input', pintarPiezas);
  $('#buscarConsignante').addEventListener('input', pintarConsignantes);

  /* ---------- Arranque ---------- */
  (async function iniciar() {
    const conectado = await API.verificarConexion();
    const estado = $('#estadoApi');
    estado.dataset.estado = conectado ? 'conectado' : 'demo';
    $('#estadoTexto').textContent = conectado
      ? 'API conectada'
      : 'Modo demostración (sin API)';
    if (!conectado) {
      $('#urlApi').textContent = 'sin conexión, datos de ejemplo';
    }
    await cargarPiezas();
    await cargarConsignantes();
  })();
})();
