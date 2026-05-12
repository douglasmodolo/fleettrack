package com.fleettrack.order.infrastructure.persistence;

import com.fleettrack.order.domain.model.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
// @Entity diz pro JPA que essa classe representa uma tabela no banco.
// Essa classe existe APENAS para o JPA — o domínio não a conhece.
@Entity
// "orders" e não "order" porque ORDER é palavra reservada do SQL.
// Sem isso o JPA tentaria criar uma tabela chamada "order" e quebraria.
@Table(name = "orders")
@Getter
// JPA exige construtor vazio para instanciar o objeto via reflection.
// PROTECTED impede que código de negócio crie uma entidade vazia.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderJpaEntity implements Persistable<UUID> {

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Id
    // UUID gerado pela aplicação, não pelo banco.
    // A aplicação controla o ID — não depende de sequence ou auto_increment.
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String customerName;

    // @Embedded diz pro JPA que os campos do Address ficam nessa
    // mesma tabela — sem join, sem tabela separada.
    // @AttributeOverrides renomeia as colunas para evitar conflito
    // entre origin e destination que têm os mesmos nomes de campo.
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "origin_country", nullable = false)),
            @AttributeOverride(name = "state", column = @Column(name = "origin_state", nullable = false)),
            @AttributeOverride(name = "city", column = @Column(name = "origin_city", nullable = false)),
            @AttributeOverride(name = "zipCode", column = @Column(name = "origin_zip_code", nullable = false)),
            @AttributeOverride(name = "street", column = @Column(name = "origin_street", nullable = false)),
            @AttributeOverride(name = "number", column = @Column(name = "origin_number", nullable = false)),
            @AttributeOverride(name = "complement", column = @Column(name = "origin_complement"))
    })
    private AddressEmbeddable originAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "destination_country", nullable = false)),
            @AttributeOverride(name = "state", column = @Column(name = "destination_state", nullable = false)),
            @AttributeOverride(name = "city", column = @Column(name = "destination_city", nullable = false)),
            @AttributeOverride(name = "zipCode", column = @Column(name = "destination_zip_code", nullable = false)),
            @AttributeOverride(name = "street", column = @Column(name = "destination_street", nullable = false)),
            @AttributeOverride(name = "number", column = @Column(name = "destination_number", nullable = false)),
            @AttributeOverride(name = "complement", column = @Column(name = "destination_complement"))
    })
    private AddressEmbeddable destinationAddress;

    // nullable = true — entregador é atribuído depois da criação
    @Column(nullable = true)
    private UUID driverId;

    // @Enumerated(STRING) salva o nome do enum como texto no banco.
    // Sem isso o JPA salvaria o índice numérico (0, 1, 2...) —
    // se você reordenar o enum, todos os registros ficariam errados.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private LocalDateTime estimatedDeliveryAt;

    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;

    // Campo de controle de concorrência — Optimistic Locking.
    // O JPA verifica esse valor antes de salvar. Se mudou desde
    // a leitura, outro processo atualizou antes — lança exceção.
    @Version
    private Long version;
}