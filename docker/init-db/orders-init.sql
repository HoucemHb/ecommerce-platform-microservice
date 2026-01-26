CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_created_at ON orders(created_at);

-- Insert sample data (optional)
-- Note: In Event Sourcing, data comes from events
-- This is just for testing direct database queries

COMMENT ON TABLE orders IS 'Read model for orders - projected from events';
COMMENT ON COLUMN orders.order_id IS 'Unique identifier for the order (aggregate ID)';
COMMENT ON COLUMN orders.status IS 'Current status: PENDING, CONFIRMED, CANCELLED';