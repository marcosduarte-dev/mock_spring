package br.com.valueprojects.mock_spring.model;

import java.util.Calendar;
import java.util.List;

import infra.JogoDao;
import infra.ServicoSMS;



public class FinalizaJogo {

	private int total = 0;
	private final JogoDao dao;
	private final ServicoSMS sms;

	public FinalizaJogo(JogoDao dao, ServicoSMS sms) {
		this.dao = dao;
		this.sms = sms;
	}

	public void finaliza() {
		List<Jogo> todosJogosEmAndamento = dao.emAndamento();

		for (Jogo jogo : todosJogosEmAndamento) {
			if (iniciouSemanaAnterior(jogo)) {
				double pontuacaoMaxima = 0;
				Participante vencedor = null;
				jogo.finaliza();
				total++;
				dao.atualiza(jogo);
				
				for (Resultado resultado : jogo.getResultados()) {
			        if (resultado.getMetrica() > pontuacaoMaxima) {
			            pontuacaoMaxima = resultado.getMetrica();
			            vencedor = resultado.getParticipante();
			        }
			    }
				
				// Jogo foi atualizado para finalizado no banco
				if(jogo.isFinalizado()) {
					sms.enviarSMS(vencedor);
				}
			}
		}
	}

	private boolean iniciouSemanaAnterior(Jogo jogo) {
		return diasEntre(jogo.getData(), Calendar.getInstance()) >= 7;
	}

	private int diasEntre(Calendar inicio, Calendar fim) {
		Calendar data = (Calendar) inicio.clone();
		int diasNoIntervalo = 0;
		while (data.before(fim)) {
			data.add(Calendar.DAY_OF_MONTH, 1);
			diasNoIntervalo++;
		}

		return diasNoIntervalo;
	}

	public int getTotalFinalizados() {
		return total;
	}
}
