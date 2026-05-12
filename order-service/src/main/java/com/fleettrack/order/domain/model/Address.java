package com.fleettrack.order.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

// Value Object — representa um endereço de entrega.
// Não tem identidade própria (sem id). Dois endereços com
// os mesmos valores são considerados equivalentes.
// É imutável por design — sem setters, nasce completo pelo Builder.
@Getter
@Builder
// Construtor vazio protegido exigido pelo JPA para instanciar
// o objeto via reflection ao ler do banco. PROTECTED impede
// que código de negócio crie um Address vazio e inútil.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// Construtor completo gerado para uso interno do Builder.
// PRIVATE porque ninguém deve instanciar diretamente com new —
// o Builder é o único caminho público de construção.
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Address {

    private String country;
    private String state;
    private String city;
    private String zipCode;
    private String street;
    private String number;

    // Getter padrão suprimido para esse campo — o Lombok geraria
    // um getComplement() retornando String, mas queremos forçar
    // o chamador a tratar a ausência via Optional.
    @Getter(AccessLevel.NONE)
    private String complement;

    // Retorna Optional para tornar explícito que complement pode
    // não existir. O chamador é obrigado a tratar os dois casos.
    public Optional<String> getComplement() {
        return Optional.ofNullable(complement);
    }
}