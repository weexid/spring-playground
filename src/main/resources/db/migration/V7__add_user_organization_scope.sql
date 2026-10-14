ALTER TABLE users
    ADD COLUMN scope_type VARCHAR(20),
    ADD COLUMN supplier_id BIGINT,
    ADD COLUMN distributor_id BIGINT,
    ADD COLUMN branch_id BIGINT;

ALTER TABLE users
    ADD CONSTRAINT fk_users_supplier
        FOREIGN KEY (supplier_id)
        REFERENCES suppliers(id),
    ADD CONSTRAINT fk_users_distributor
        FOREIGN KEY (distributor_id)
        REFERENCES distributors(id),
    ADD CONSTRAINT fk_users_branch
        FOREIGN KEY (branch_id)
        REFERENCES branches(id),
    ADD CONSTRAINT chk_users_organization_scope
        CHECK (
            (scope_type IS NULL AND supplier_id IS NULL AND distributor_id IS NULL AND branch_id IS NULL)
            OR (scope_type = 'SUPPLIER' AND supplier_id IS NOT NULL AND distributor_id IS NULL AND branch_id IS NULL)
            OR (scope_type = 'DISTRIBUTOR' AND supplier_id IS NULL AND distributor_id IS NOT NULL AND branch_id IS NULL)
            OR (scope_type = 'BRANCH' AND supplier_id IS NULL AND distributor_id IS NULL AND branch_id IS NOT NULL)
        );

CREATE INDEX idx_users_supplier_id
    ON users (supplier_id);

CREATE INDEX idx_users_distributor_id
    ON users (distributor_id);

CREATE INDEX idx_users_branch_id
    ON users (branch_id);
