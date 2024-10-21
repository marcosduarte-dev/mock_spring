package br.com.valueprojects.mock_spring;




import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.com.valueprojects.mock_spring.builder.CriadorDeJogo;
import br.com.valueprojects.mock_spring.model.FinalizaJogo;
import br.com.valueprojects.mock_spring.model.Jogo;
import br.com.valueprojects.mock_spring.model.Participante;
import br.com.valueprojects.mock_spring.model.Resultado;
import infra.JogoDao;
import infra.ServicoSMS;




public class FinalizaJogoTest {
	
	 @Test
	    public void deveFinalizarJogosDaSemanaAnterior() {

	        Calendar antiga = Calendar.getInstance();
	        antiga.set(1999, 1, 20);

	        Jogo jogo1 = new CriadorDeJogo().para("Ca�a moedas")
	            .naData(antiga).constroi();
	        Jogo jogo2 = new CriadorDeJogo().para("Derruba barreiras")
	            .naData(antiga).constroi();

	        // mock no lugar de dao falso
	        
	        List<Jogo> jogosAnteriores = Arrays.asList(jogo1, jogo2);

	        JogoDao daoFalso = mock(JogoDao.class);
	        ServicoSMS sms = mock(ServicoSMS.class);

	        when(daoFalso.emAndamento()).thenReturn(jogosAnteriores);

	        FinalizaJogo finalizador = new FinalizaJogo(daoFalso, sms);
	        finalizador.finaliza();

	        assertTrue(jogo1.isFinalizado());
	        assertTrue(jogo2.isFinalizado());
	        assertEquals(2, finalizador.getTotalFinalizados());
	    }
	 
	 @Test
		public void deveVerificarSeMetodoAtualizaFoiInvocado() {

			Calendar antiga = Calendar.getInstance();
			antiga.set(1999, 1, 20);

			Jogo jogo1 = new CriadorDeJogo().para("Cata moedas").naData(antiga).constroi();
			Jogo jogo2 = new CriadorDeJogo().para("Derruba barreiras").naData(antiga).constroi();

			// mock no lugar de dao falso

			List<Jogo> jogosAnteriores = Arrays.asList(jogo1, jogo2);

			JogoDao daoFalso = mock(JogoDao.class);
			ServicoSMS sms = mock(ServicoSMS.class);

			when(daoFalso.emAndamento()).thenReturn(jogosAnteriores);

			FinalizaJogo finalizador = new FinalizaJogo(daoFalso, sms);
			finalizador.finaliza();

			verify(daoFalso, times(1)).atualiza(jogo1);
			//Mockito.verifyNoInteractions(daoFalso);
	
					
			
		}
	 
	 @Test
	    public void deveFinalizarEnviarSMSparaJogoFinalizados() {

	        Calendar antiga = Calendar.getInstance();
	        antiga.set(1999, 1, 20);

	        Jogo jogo1 = new CriadorDeJogo().para("Ca�a moedas")
	            .naData(antiga).constroi();
	        Jogo jogo2 = new CriadorDeJogo().para("Derruba barreiras")
	            .naData(antiga).constroi();
	        
	        Resultado resultado1 = new Resultado(new Participante("Jogador1"), 100);
	        Resultado resultado2 = new Resultado(new Participante("Jogador2"), 80);
	        jogo1.anota(resultado1);
	        jogo2.anota(resultado2);

	        // mock no lugar de dao falso
	        
	        List<Jogo> jogosAnteriores = Arrays.asList(jogo1, jogo2);

	        JogoDao daoFalso = mock(JogoDao.class);
	        ServicoSMS sms = mock(ServicoSMS.class);

	        when(daoFalso.emAndamento()).thenReturn(jogosAnteriores);

	        FinalizaJogo finalizador = new FinalizaJogo(daoFalso, sms);
	        finalizador.finaliza();

	        assertTrue(jogo1.isFinalizado());
	        assertTrue(jogo2.isFinalizado());
	        assertEquals(2, finalizador.getTotalFinalizados());
	        
	        // Verificando se o método enviarSMS foi chamado com o vencedor esperado
	        verify(sms).enviarSMS(resultado1.getParticipante());
	        verify(sms).enviarSMS(resultado2.getParticipante());

	        // Verificando que não houve mais interações inesperadas com os mocks
	        verifyNoMoreInteractions(sms);
	    }
	 
	 @Test
	    public void naoEnviaSMSSeNaoFoiSalvoAFinalizacaoDoJogo() {

	        JogoDao dao = mock(JogoDao.class);
	        ServicoSMS sms = mock(ServicoSMS.class);

	        FinalizaJogo finalizaJogo = new FinalizaJogo(dao, sms);
	        
	        Calendar antiga = Calendar.getInstance();
	        antiga.set(1999, 1, 20);

	        Jogo jogo = new CriadorDeJogo().para("Cata moedas")
		            .naData(antiga).constroi();
	        
	        Resultado resultado1 = new Resultado(new Participante("Jogador1"), 100);
	        Resultado resultado2 = new Resultado(new Participante("Jogador2"), 80);
	        jogo.anota(resultado1);
	        jogo.anota(resultado2);
	        
	        Jogo jogoSpy = spy(jogo);

	        when(dao.emAndamento()).thenReturn(List.of(jogoSpy));
	        doReturn(false).when(jogoSpy).isFinalizado();

	        finalizaJogo.finaliza();

	        // Verificando se o método enviarSMS NÃO foi chamado
	        verify(sms, never()).enviarSMS(any());

	        // Verificando se o método atualiza do dao foi chamado
	        verify(dao).atualiza(jogoSpy);
	    }
		 
	}

 

	
	

	
