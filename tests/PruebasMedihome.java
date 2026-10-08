import java.time.LocalDateTime;

/** Pruebas del ciclo de servicio y de las asociaciones, sin librerías externas. */
public class PruebasMedihome {
    private static int comprobaciones;

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
        comprobaciones++;
    }

    private static void rechazar(Class<? extends RuntimeException> tipo, Runnable accion) {
        try {
            accion.run();
        } catch (RuntimeException error) {
            verificar(tipo.isInstance(error), "Excepción inesperada: " + error);
            return;
        }
        throw new AssertionError("Se esperaba " + tipo.getSimpleName());
    }

    public static void main(String[] args) {
        LocalDateTime fecha = LocalDateTime.of(2026, 10, 7, 9, 0);
        Empresa empresa = new Empresa("E1", "Medihome", "empresa@example.com", "123", "Centro");
        Paciente paciente = new Paciente("P1", "Paciente", "paciente@example.com", "123", "Casa");
        Paciente otro = new Paciente("P2", "Otro", "otro@example.com", "456", "Otra casa");
        ProfesionalSalud profesional = new ProfesionalSalud("M1", "Profesional", "medico@example.com", "R1", "General");
        empresa.registrarPaciente(paciente);
        empresa.registrarPaciente(otro);
        empresa.registrarProfesional(profesional);
        paciente.setNombre("Paciente actualizado");
        verificar(paciente.getNombre().equals("Paciente actualizado"), "Getter y setter heredados");

        EquipoMedico equipo1 = new EquipoMedico("EQ1", "Uno", "Norte");
        EquipoMedico equipo2 = new EquipoMedico("EQ2", "Dos", "Sur");
        equipo1.agregarProfesional(profesional);
        equipo2.agregarProfesional(profesional);
        verificar(equipo1.getProfesionales().isEmpty(), "Desvinculación del equipo anterior");
        verificar(equipo2.getProfesionales().contains(profesional), "Vinculación del nuevo equipo");
        equipo2.quitarProfesional(profesional);
        verificar(profesional.getEquipo() == null, "El profesional existe fuera del equipo");

        ServicioDomiciliario servicio = new ServicioDomiciliario("S1", fecha, "Casa", "Control", paciente);
        empresa.registrarServicio(servicio);
        verificar(servicio.getEstado() == EstadoServicio.SOLICITADO, "Estado inicial");
        verificar(paciente.getServicios().contains(servicio), "Relación paciente-servicio");
        verificar(servicio.getAtencion() == null, "Servicio solicitado sin atención");
        rechazar(IllegalStateException.class, () -> servicio.iniciarAtencion(fecha));
        rechazar(IllegalStateException.class, () -> servicio.setEstado(EstadoServicio.FINALIZADO));
        rechazar(UnsupportedOperationException.class, () -> paciente.getServicios().clear());
        servicio.programar(profesional, fecha);
        verificar(profesional.getServicios().contains(servicio), "Relación profesional-servicio");

        ServicioDomiciliario coincidente = new ServicioDomiciliario("S2", fecha, "Casa", "Control", paciente);
        empresa.registrarServicio(coincidente);
        rechazar(IllegalArgumentException.class, () -> coincidente.programar(profesional, fecha));
        verificar(coincidente.getEstado() == EstadoServicio.SOLICITADO
                && coincidente.getProfesional() == null, "Programación rechazada sin asignación parcial");
        coincidente.programar(profesional, fecha.plusDays(1));
        verificar(profesional.getServicios().size() == 2, "Servicios en fechas diferentes");
        rechazar(IllegalArgumentException.class, () -> coincidente.setFechaHora(fecha));

        ServicioDomiciliario duplicado = new ServicioDomiciliario("S1", fecha, "Otra casa", "Control", otro);
        rechazar(IllegalArgumentException.class, () -> empresa.registrarServicio(duplicado));
        rechazar(IllegalArgumentException.class, () -> coincidente.setCodigoUnico("S1"));

        AtencionMedica atencion = servicio.iniciarAtencion(fecha.plusMinutes(5));
        verificar(atencion.getServicio() == servicio && servicio.getAtencion() == atencion, "Composición de atención");
        rechazar(IllegalStateException.class, () -> servicio.iniciarAtencion(fecha.plusMinutes(6)));
        rechazar(IllegalArgumentException.class, () -> atencion.registrarMedicion(fecha, 36.7, 70, 120, 80, 98));
        rechazar(IllegalArgumentException.class, () -> atencion.registrarMedicion(fecha.plusMinutes(10), 36.7, 70, 120, 80, 101));
        verificar(atencion.getMediciones().isEmpty(), "Mediciones inválidas no se agregan");
        MedicionSignos medicion = atencion.registrarMedicion(fecha.plusMinutes(10), 36.7, 70, 120, 80, 98);
        verificar(medicion.getAtencion() == atencion, "Composición de medición");
        medicion.setTemperatura(37.0);
        verificar(medicion.getTemperatura() == 37.0, "Getter y setter de medición");
        rechazar(IllegalArgumentException.class, () -> medicion.setTemperatura(Double.NaN));
        rechazar(IllegalArgumentException.class, () -> atencion.setFechaHoraInicio(fecha.plusMinutes(11)));
        rechazar(IllegalArgumentException.class, () -> atencion.finalizar(fecha.plusMinutes(9)));
        atencion.setObservaciones("Observaciones de prueba");
        atencion.setRecomendaciones("Recomendaciones de prueba");
        atencion.finalizar(fecha.plusMinutes(30));
        verificar(servicio.getEstado() == EstadoServicio.FINALIZADO, "Estado final");
        rechazar(IllegalStateException.class, () -> atencion.registrarMedicion(fecha.plusMinutes(15), 36.7, 70, 120, 80, 98));
        rechazar(IllegalStateException.class, servicio::cancelar);
        rechazar(IllegalArgumentException.class, () -> medicion.setFechaHora(fecha.plusHours(1)));
        String reporte = servicio.generarReporte();
        verificar(reporte.contains("Paciente actualizado") && reporte.contains("Observaciones de prueba")
                && reporte.contains("120/80") && reporte.contains("Finalizado"), "Contenido del reporte");

        ServicioDomiciliario sinMediciones = new ServicioDomiciliario("S3", fecha.plusDays(2), "Casa", "Control", paciente);
        empresa.registrarServicio(sinMediciones);
        sinMediciones.programar(profesional, fecha.plusDays(2));
        AtencionMedica vacia = sinMediciones.iniciarAtencion(fecha.plusDays(2));
        vacia.finalizar(fecha.plusDays(2).plusMinutes(20));
        verificar(vacia.getMediciones().isEmpty(), "Atención válida con cero mediciones");

        ServicioDomiciliario cancelado = new ServicioDomiciliario("S4", fecha.plusDays(3), "Casa", "Control", paciente);
        cancelado.cancelar();
        verificar(cancelado.getEstado() == EstadoServicio.CANCELADO && cancelado.getAtencion() == null, "Cancelación sin atención");
        rechazar(IllegalStateException.class, () -> cancelado.programar(profesional, fecha.plusDays(3)));
        coincidente.cancelar();
        verificar(profesional.estaDisponible(fecha.plusDays(1), null), "Cancelar libera la fecha reservada");
        System.out.println("PRUEBAS CORRECTAS: " + comprobaciones + " comprobaciones.");
    }
}
