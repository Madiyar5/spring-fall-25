CREATE TABLE projects (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          status VARCHAR(50) DEFAULT 'PLANNED',
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);