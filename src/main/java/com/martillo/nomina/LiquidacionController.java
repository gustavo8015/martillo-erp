package com.martillo.nomina;

import com.martillo.nomina.dto.LiquidacionRequest;
import com.martillo.nomina.dto.LiquidacionResponse;
import com.martillo.seguridad.AutorizacionService;
import com.martillo.seguridad.Modulo;
import com.martillo.seguridad.Permiso;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Punto de entrada del motor de liquidacion (HU-07).
 *
 * La autorizacion se verifica en el servidor (HU-09): solo el responsable de
 * nomina alcanza este modulo, aunque la interfaz oculte el menu.
 */
@RestController
@RequestMapping("/api/nomina/liquidaciones")
public class LiquidacionController {

    private final CalculadoraLiquidacion calculadora;
    private final AutorizacionService autorizacion;

    public LiquidacionController(CalculadoraLiquidacion calculadora, AutorizacionService autorizacion) {
        this.calculadora = calculadora;
        this.autorizacion = autorizacion;
    }

    @PostMapping
    public ResponseEntity<LiquidacionResponse> liquidar(
            @RequestHeader(name = "X-Rol", required = false) String rol,
            @Valid @RequestBody LiquidacionRequest peticion) {

        autorizacion.exigir(rol, Modulo.NOMINA, Permiso.TOTAL);

        Contrato contrato = new Contrato(
                peticion.getTipoContrato(),
                peticion.getFechaIngreso(),
                peticion.getFinPactado(),
                peticion.getSalarioMensual());

        Liquidacion liquidacion = calculadora.liquidar(
                contrato, peticion.getFechaRetiro(), peticion.getMotivo());

        return ResponseEntity.ok(LiquidacionResponse.de(liquidacion));
    }
}
