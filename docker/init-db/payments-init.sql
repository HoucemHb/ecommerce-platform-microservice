CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payments(order_id);
CREATE INDEX IF NOT EXISTS idx_payments_customer_id ON payments(customer_id);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);
CREATE INDEX IF NOT EXISTS idx_payments_validated_at ON payments(validated_at);

COMMENT ON TABLE payments IS 'Read model for payments - projected from events';
COMMENT ON COLUMN payments.status IS 'Payment status: PENDING, VALIDATED, FAILED, REFUNDED';