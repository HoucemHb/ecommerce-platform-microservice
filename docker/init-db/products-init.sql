CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);
CREATE INDEX IF NOT EXISTS idx_products_available_stock ON products(available_stock);

-- Insert sample products for testing
-- In production, these would be created via API
INSERT INTO products (product_id, name, description, price, currency, available_stock)
VALUES
    ('prod-laptop-001', 'Gaming Laptop', 'High-performance gaming laptop with RTX 4080', 1999.99, 'USD', 50),
    ('prod-mouse-001', 'Wireless Mouse', 'Ergonomic wireless mouse', 49.99, 'USD', 200),
    ('prod-keyboard-001', 'Mechanical Keyboard', 'RGB mechanical keyboard', 129.99, 'USD', 150),
    ('prod-monitor-001', '4K Monitor', '27-inch 4K gaming monitor', 599.99, 'USD', 75),
    ('prod-headset-001', 'Gaming Headset', 'Noise-cancelling gaming headset', 149.99, 'USD', 120)
ON CONFLICT (product_id) DO NOTHING;

COMMENT ON TABLE products IS 'Read model for products - projected from events';
COMMENT ON COLUMN products.available_stock IS 'Current available stock (not reserved)';
