import java.time.LocalDateTime;

/** Ejemplo de la atención domiciliaria solicitada en la actividad. */
public class Main {
    public static void main(String[] args) {
        // Datos ficticios de demostración.
        Empresa empresa = new Empresa("900123456", "Medihome", "contacto@medihome.example",
                "6021234567", "Calle 10 # 20-30");
        Paciente paciente = new Paciente("1001001001", "María Torres",
                "maria@example.com", "3001234567", "Carrera 5 # 12-40");
        ProfesionalSalud profesional = new ProfesionalSalud("2002002002", "Carlos Pérez",
                "carlos@example.com", "RM-12345", "Medicina general");
        EquipoMedico equipo = new EquipoMedico("EQ-001", "Equipo Centro", "Zona centro");

        empresa.registrarPaciente(paciente);
        empresa.registrarProfesional(profesional);
        empresa.agregarEquipo(equipo);
        equipo.agregarProfesional(profesional);

        // Ejemplo de uso de los setters y de los getters heredados de Usuario.
        paciente.setTelefono("3109876543");
        profesional.setEspecialidad("Medicina familiar");
        System.out.println("Paciente registrado: " + paciente.getNombre());
        System.out.println("Equipo del profesional: " + profesional.getEquipo().getNombre());

        LocalDateTime fechaProgramada = LocalDateTime.of(2026, 10, 7, 9, 0);
        ServicioDomiciliario servicio = new ServicioDomiciliario("SD-001", fechaProgramada,
                paciente.getDireccionPrincipal(), "Valoración domiciliaria de seguimiento", paciente);
        empresa.registrarServicio(servicio);
        servicio.programar(profesional, fechaProgramada);

        AtencionMedica atencion = servicio.iniciarAtencion(fechaProgramada.plusMinutes(5));
        atencion.setObservaciones("Paciente consciente y orientada. Se registran signos vitales.");
        atencion.setRecomendaciones("Seguir las indicaciones recibidas durante la consulta.");

        MedicionSignos medicion = atencion.registrarMedicion(fechaProgramada.plusMinutes(10),
                36.7, 76, 120, 80, 98.0);
        medicion.setTemperatura(36.8);
        atencion.setFechaHoraFinalizacion(fechaProgramada.plusMinutes(40));

        // La última salida es el reporte de la atención prestada al paciente.
        System.out.println(servicio.generarReporte());
    }
}
