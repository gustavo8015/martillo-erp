package com.martillo.inventario;

import com.martillo.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Control de stock critico del inventario (HU-08).
 *
 * El movimiento y la alerta ocurren en la misma transaccion. Esta es una
 * ventaja concreta del monolito modular: los dos hechos se confirman juntos o
 * no se confirma ninguno, sin coordinar dos servicios.
 *
 * Regla de una sola alerta: el item pasa a estado critico al cruzar el umbral
 * y no vuelve a alertar mientras siga en ese estado, para no saturar al
 * responsable. Cuando una reposicion devuelve el stock al umbral o por encima,
 * el item vuelve a NORMAL y queda habilitado para alertar de nuevo.
 */
@Service
public class InventarioService {

    private final ItemInventarioRepository items;
    private final AlertaStockRepository alertas;
    private final NotificadorAlertas notificador;

    public InventarioService(ItemInventarioRepository items,
                             AlertaStockRepository alertas,
                             NotificadorAlertas notificador) {
        this.items = items;
        this.alertas = alertas;
        this.notificador = notificador;
    }

    @Transactional
    public ItemInventario registrarSalida(String codigo, int cantidad) {
        ItemInventario item = buscar(codigo);
        item.descontar(cantidad);

        if (item.bajoUmbral() && item.getEstado() == EstadoStock.NORMAL) {
            item.setEstado(EstadoStock.CRITICO);
            AlertaStock alerta = alertas.save(new AlertaStock(
                    item.getCodigo(),
                    "El item " + item.getNombre() + " quedo en " + item.getStock()
                            + " unidades, por debajo del umbral critico de " + item.getUmbralCritico(),
                    item.getStock()));
            notificador.notificar(alerta);
        }

        return items.save(item);
    }

    @Transactional
    public ItemInventario registrarEntrada(String codigo, int cantidad) {
        ItemInventario item = buscar(codigo);
        item.reponer(cantidad);

        if (!item.bajoUmbral() && item.getEstado() == EstadoStock.CRITICO) {
            item.setEstado(EstadoStock.NORMAL);
            alertas.findByAtendidaFalseOrderByMomentoDesc().stream()
                    .filter(a -> a.getCodigoItem().equals(item.getCodigo()))
                    .forEach(a -> {
                        a.setAtendida(true);
                        alertas.save(a);
                    });
        }

        return items.save(item);
    }

    @Transactional(readOnly = true)
    public List<ItemInventario> listar() {
        return items.findAll();
    }

    @Transactional(readOnly = true)
    public List<AlertaStock> alertasPendientes() {
        return alertas.findByAtendidaFalseOrderByMomentoDesc();
    }

    @Transactional
    public ItemInventario crear(String codigo, String nombre, int stock, int umbralCritico) {
        return items.save(new ItemInventario(codigo, nombre, stock, umbralCritico));
    }

    private ItemInventario buscar(String codigo) {
        Optional<ItemInventario> item = items.findByCodigo(codigo);
        return item.orElseThrow(() -> new ResourceNotFoundException("No existe el item de inventario " + codigo));
    }
}
