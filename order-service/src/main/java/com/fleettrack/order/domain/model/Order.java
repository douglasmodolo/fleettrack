package com.fleettrack.order.domain.model;

import com.fleettrack.order.domain.exception.InvalidOrderStatusTransitionException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// Entidade de domínio — representa um pedido de entrega.
// Não tem @Entity nem @Table porque esse objeto existe para o negócio,
// não para o banco. A camada de infrastructure tem seu próprio objeto
// JPA separado que mapeia essa entidade para o PostgreSQL.
@Getter
// Construtor privado gerado pelo Lombok — força a criação exclusivamente
// pelo factory method Order.create(). Impede que objetos nasçam em estado inválido.
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Order {

    private UUID id;
    private Long version;

    private String customerName;

    // Endereços são Value Objects — não têm identidade própria,
    // são definidos pelos seus valores. Ficam na mesma tabela do pedido
    // via @Embedded no JPA, sem join e sem tabela extra.
    private Address originAddress;
    private Address destinationAddress;

    // Referência ao entregador por ID — não pelo objeto completo.
    // O entregador pertence a outro microservice. Guardar só o ID
    // evita acoplamento entre domínios. Começa nulo — o entregador
    // é atribuído depois da criação do pedido.
    private UUID driverId;

    private OrderStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Calculada na criação como createdAt + 3 dias.
    // Pode evoluir para um cálculo baseado em distância no futuro.
    private LocalDateTime estimatedDeliveryAt;

    // Preenchidas automaticamente pelo updateStatus() conforme
    // o pedido evolui — nunca pelo chamador diretamente.
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;

    private static final Map<OrderStatus, List<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            OrderStatus.PENDING,   List.of(OrderStatus.PICKED_UP, OrderStatus.IN_TRANSIT, OrderStatus.DELIVERED, OrderStatus.CANCELLED),
            OrderStatus.PICKED_UP, List.of(OrderStatus.IN_TRANSIT, OrderStatus.DELIVERED, OrderStatus.CANCELLED),
            OrderStatus.IN_TRANSIT, List.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED),
            OrderStatus.DELIVERED,  List.of(),
            OrderStatus.CANCELLED,  List.of()
    );

    // Factory method — único ponto de criação de um novo pedido.
    // Centraliza todas as regras de inicialização: status sempre começa
    // como PENDING, id é gerado aqui, datas são definidas aqui.
    // O chamador não tem controle sobre esses valores.
    public static Order create(String customerName,
                               Address originAddress,
                               Address destinationAddress) {
        Order order = new Order();
        order.id = UUID.randomUUID();
        order.customerName = customerName;
        order.originAddress = originAddress;
        order.destinationAddress = destinationAddress;
        order.status = OrderStatus.PENDING;
        order.createdAt = LocalDateTime.now();
        // estimatedDeliveryAt usa createdAt em vez de LocalDateTime.now()
        // para garantir consistência — ambas as datas representam o mesmo instante.
        order.estimatedDeliveryAt = order.createdAt.plusDays(3);
        order.markAsUpdated();
        return order;
    }

    // Factory method de reconstrução — usado exclusivamente pela camada
    // de infrastructure para reconstituir um Order a partir do banco.
    // Não aplica regras de inicialização — os valores já existem e
    // devem ser preservados exatamente como estão.
    public static Order reconstitute(UUID id,
                                     Long version,
                                     String customerName,
                                     Address originAddress,
                                     Address destinationAddress,
                                     UUID driverId,
                                     OrderStatus status,
                                     LocalDateTime createdAt,
                                     LocalDateTime updatedAt,
                                     LocalDateTime estimatedDeliveryAt,
                                     LocalDateTime pickedUpAt,
                                     LocalDateTime deliveredAt) {
        Order order = new Order();
        order.id = id;
        order.version = version;
        order.customerName = customerName;
        order.originAddress = originAddress;
        order.destinationAddress = destinationAddress;
        order.driverId = driverId;
        order.status = status;
        order.createdAt = createdAt;
        order.updatedAt = updatedAt;
        order.estimatedDeliveryAt = estimatedDeliveryAt;
        order.pickedUpAt = pickedUpAt;
        order.deliveredAt = deliveredAt;
        return order;
    }

    // Método de domínio — a regra de transição de status fica aqui,
    // não no service nem no controller. O domínio é dono das suas regras.
    // Preenche automaticamente pickedUpAt e deliveredAt quando o status
    // muda para os estados correspondentes.
    public void updateStatus(OrderStatus newStatus) {
        // Valida se a transição é permitida antes de qualquer mudança.
        // A regra fica no domain — qualquer camada que chamar esse método
        // recebe a proteção automaticamente.
        List<OrderStatus> allowed = ALLOWED_TRANSITIONS.get(this.status);
        if (!allowed.contains(newStatus)) {
            throw new InvalidOrderStatusTransitionException(this.status, newStatus);
        }

        this.status = newStatus;
        markAsUpdated();

        if (newStatus == OrderStatus.PICKED_UP) {
            this.pickedUpAt = LocalDateTime.now();
        }

        if (newStatus == OrderStatus.DELIVERED) {
            this.deliveredAt = LocalDateTime.now();
        }
    }

    // Método de domínio — expressa a intenção de negócio de atribuir
    // um entregador ao pedido. Preferível a um setter genérico setDriverId()
    // porque o nome revela o que está acontecendo no negócio.
    public void assignDriver(UUID driverId) {
        this.driverId = driverId;
        markAsUpdated();
    }

    // Centraliza a atualização do updatedAt — evita repetir
    // LocalDateTime.now() em cada método que muda o estado do pedido.
    // Privado porque é um detalhe de implementação, não parte da API pública.
    private void markAsUpdated() {
        this.updatedAt = LocalDateTime.now();
    }
}