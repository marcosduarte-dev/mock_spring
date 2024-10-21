package infra;

import br.com.valueprojects.mock_spring.model.Participante;

public class ServicoSMS {
    public void enviarSMS(Participante vencedor) {
        System.out.println("SMS enviado para o vencedor: " + vencedor.getNome());
    }
}