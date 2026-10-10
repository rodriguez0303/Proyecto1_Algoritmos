package LogicaJuego;

/**
 * Prueba manual de las operaciones económicas del banco; no pertenece al flujo normal de la interfaz.
 */
public class PruebaBanco {

    /**
     * Ejecuta pruebas manuales de pagos y transferencias del banco.
     */
    public static void main(String[] args) {

        Jugador jose = new Jugador("J001", "Jose", 1500);
        Jugador luis = new Jugador("J002", "Luis", 1000);
        Jugador pruebaObligatoria = new Jugador("J003", "Prueba", 100);

        Banco banco = new Banco(10000);

        System.out.println("=== ESTADO INICIAL ===");
        System.out.println("Banco: " + banco.getSaldo());
        System.out.println("Jose: " + jose.getSaldo());
        System.out.println("Luis: " + luis.getSaldo());

        // Banco paga a Jose
        boolean pagoRealizado = banco.pagar(jose, 500);

        System.out.println("\n=== BANCO PAGA 500 A JOSE ===");
        System.out.println("Pago realizado: " + pagoRealizado);
        System.out.println("Banco: " + banco.getSaldo());
        System.out.println("Jose: " + jose.getSaldo());

        // Jose paga al banco
        boolean recepcionRealizada = banco.recibir(jose, 300);

        System.out.println("\n=== JOSE PAGA 300 AL BANCO ===");
        System.out.println("Recepción realizada: " + recepcionRealizada);
        System.out.println("Banco: " + banco.getSaldo());
        System.out.println("Jose: " + jose.getSaldo());

        // Jose transfiere a Luis
        boolean transferencia = banco.transferir(jose, luis, 400);

        System.out.println("\n=== JOSE TRANSFIERE 400 A LUIS ===");
        System.out.println("Transferencia realizada: " + transferencia);
        System.out.println("Jose: " + jose.getSaldo());
        System.out.println("Luis: " + luis.getSaldo());

        // Prueba de saldo insuficiente
        boolean transferenciaInvalida = banco.transferir(luis, jose, 5000);

        System.out.println("\n=== TRANSFERENCIA INVALIDA ===");
        System.out.println("Transferencia realizada: " + transferenciaInvalida);
        System.out.println("Jose: " + jose.getSaldo());
        System.out.println("Luis: " + luis.getSaldo());
        
        // Prueba de recepción inválida
        boolean recepcionInvalida = banco.recibir(luis, 2000);

        System.out.println("\n=== RECEPCIÓN INVALIDA ===");
        System.out.println("Recepción realizada: " + recepcionInvalida);
        System.out.println("Banco: " + banco.getSaldo());
        System.out.println("Luis: " + luis.getSaldo());

        // Prueba de transferencia inválida
        boolean pagoInvalido = banco.pagar(jose, 20000);

        System.out.println("\n=== PAGO INVALIDO BANCO A JOSE ===");
        System.out.println("Pago realizado: " + pagoInvalido);
        System.out.println("Jose: " + jose.getSaldo());
        System.out.println("Banco: " + banco.getSaldo());

        // Prueba de transferencia inválida (Jugador a sí mismo)
        boolean transferenciaMismoJugador = banco.transferir(jose, jose, 1000);

        System.out.println("\n=== TRANSFERENCIA INVALIDA (Jugador a sí mismo) ===");
        System.out.println("Transferencia realizada: " + transferenciaMismoJugador);
        System.out.println("Jose: " + jose.getSaldo());
        System.out.println("Banco: " + banco.getSaldo());

        // Prueba de pago obligatorio
        boolean transferirPagoObligatorio = banco.recibirPagoObligatorio(pruebaObligatoria, 50);

        System.out.println("\n=== PAGO OBLIGATORIO DE PRUEBA ===");
        System.out.println("Pago obligatorio realizado: " + transferirPagoObligatorio);
        System.out.println("Prueba Obligatoria: " + pruebaObligatoria.getSaldo());
        System.out.println("Banco: " + banco.getSaldo());

        System.out.println("\n=== PAGO OBLIGATORIO SIN SALDO ===");

        boolean pagoObligatorioInvalido = banco.recibirPagoObligatorio(pruebaObligatoria, 200);

        System.out.println("Pago obligatorio realizado: " + pagoObligatorioInvalido);
        System.out.println("Prueba Obligatoria: " + pruebaObligatoria.getSaldo());
        System.out.println("Activo: " + pruebaObligatoria.esActivo());
        System.out.println("Banco: " + banco.getSaldo());

        System.out.println("\n=== TRANSFERENCIA PAGO OBLIGATORIO ===");

        boolean transferenciaPagoObligatorio = banco.transferirPagoObligatorio(jose, luis, 200);

        System.out.println("Transferencia pago obligatorio realizada: " + transferenciaPagoObligatorio);
        System.out.println("Jose: " + jose.getSaldo());
        System.out.println("Luis: " + luis.getSaldo());
        System.out.println("Banco: " + banco.getSaldo());

        System.out.println("\n=== TRANSFERENCIA PAGO OBLIGATORIO SIN SALDO ===");

        boolean transferenciaPagoObligatorioInvalida = banco.transferirPagoObligatorio(luis, jose, 2000);

        System.out.println("Transferencia pago obligatorio realizada: " + transferenciaPagoObligatorioInvalida);
        System.out.println("Jose: " + jose.getSaldo());
        System.out.println("Luis: " + luis.getSaldo());
        System.out.println("Activo Luis: " + luis.esActivo());

    }
}