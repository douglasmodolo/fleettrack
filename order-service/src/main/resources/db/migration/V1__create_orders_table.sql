CREATE TABLE orders (
    id                      UUID            NOT NULL,
    customer_name           VARCHAR(255)    NOT NULL,

    origin_country          VARCHAR(255)    NOT NULL,
    origin_state            VARCHAR(255)    NOT NULL,
    origin_city             VARCHAR(255)    NOT NULL,
    origin_zip_code         VARCHAR(255)    NOT NULL,
    origin_street           VARCHAR(255)    NOT NULL,
    origin_number           VARCHAR(255)    NOT NULL,
    origin_complement       VARCHAR(255),

    destination_country     VARCHAR(255)    NOT NULL,
    destination_state       VARCHAR(255)    NOT NULL,
    destination_city        VARCHAR(255)    NOT NULL,
    destination_zip_code    VARCHAR(255)    NOT NULL,
    destination_street      VARCHAR(255)    NOT NULL,
    destination_number      VARCHAR(255)    NOT NULL,
    destination_complement  VARCHAR(255),

    driver_id               UUID,
    status                  VARCHAR(50)     NOT NULL,

    created_at              TIMESTAMP       NOT NULL,
    updated_at              TIMESTAMP       NOT NULL,
    estimated_delivery_at   TIMESTAMP       NOT NULL,
    picked_up_at            TIMESTAMP,
    delivered_at            TIMESTAMP,

    version                 BIGINT,

    CONSTRAINT pk_orders PRIMARY KEY (id)
);