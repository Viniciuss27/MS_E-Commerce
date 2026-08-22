package vinix.services;

import vinix.events.EstoqueFalhouEvent;
import vinix.events.EstoqueReservadoEvent;

public interface NotificacaoService {
    void notificarEstoqueReservado(EstoqueReservadoEvent event);
    void notificarEstoqueFalhou(EstoqueFalhouEvent event);
}