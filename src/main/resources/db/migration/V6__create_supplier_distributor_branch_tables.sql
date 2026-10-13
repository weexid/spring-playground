CREATE TABLE suppliers (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    supplier_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_suppliers_status
        CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE TABLE distributors (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    legal_name VARCHAR(200),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_distributors_status
        CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE TABLE branches (
    id BIGSERIAL PRIMARY KEY,
    distributor_id BIGINT NOT NULL,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(150) NOT NULL,
    city VARCHAR(100),
    address TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_branches_distributor
        FOREIGN KEY (distributor_id)
        REFERENCES distributors(id),
    CONSTRAINT uq_branches_distributor_code
        UNIQUE (distributor_id, code),
    CONSTRAINT chk_branches_status
        CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE TABLE supplier_branches (
    supplier_id BIGINT NOT NULL,
    branch_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    started_at DATE NOT NULL DEFAULT CURRENT_DATE,
    ended_at DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_supplier_branches
        PRIMARY KEY (supplier_id, branch_id),
    CONSTRAINT fk_supplier_branches_supplier
        FOREIGN KEY (supplier_id)
        REFERENCES suppliers(id),
    CONSTRAINT fk_supplier_branches_branch
        FOREIGN KEY (branch_id)
        REFERENCES branches(id),
    CONSTRAINT chk_supplier_branches_status
        CHECK (status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT chk_supplier_branches_dates
        CHECK (ended_at IS NULL OR ended_at >= started_at)
);

CREATE INDEX idx_branches_distributor_id
    ON branches (distributor_id);

CREATE INDEX idx_supplier_branches_branch_id
    ON supplier_branches (branch_id);

CREATE INDEX idx_supplier_branches_active
    ON supplier_branches (supplier_id, branch_id)
    WHERE status = 'ACTIVE';
