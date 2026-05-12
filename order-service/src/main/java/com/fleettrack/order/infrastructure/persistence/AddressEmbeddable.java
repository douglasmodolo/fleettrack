package com.fleettrack.order.infrastructure.persistence;

import com.fleettrack.order.domain.model.Address;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// @Embeddable diz pro JPA que essa classe pode ser embutida
// em outra entidade — seus campos viram colunas da tabela pai.
// Existe separado do Address do domain para não contaminar
// o domain com anotações de infrastructure.
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AddressEmbeddable {

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String number;

    // Opcional — sem nullable = false
    private String complement;

    // Factory method — converte o Address do domain para o objeto
    // que o JPA conhece. Esse é o único ponto de conversão.
    public static AddressEmbeddable fromDomain(Address address) {
        return new AddressEmbeddable(
                address.getCountry(),
                address.getState(),
                address.getCity(),
                address.getZipCode(),
                address.getStreet(),
                address.getNumber(),
                // Optional.orElse(null) — complement pode não existir
                address.getComplement().orElse(null)
        );
    }

    // Converte de volta para o Address do domain.
    public Address toDomain() {
        return Address.builder()
                .country(country)
                .state(state)
                .city(city)
                .zipCode(zipCode)
                .street(street)
                .number(number)
                .complement(complement)
                .build();
    }
}