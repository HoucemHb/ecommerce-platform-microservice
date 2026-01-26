CREATE INDEX IF NOT EXISTS idx_notifications_order_id ON notifications(order_id);
CREATE INDEX IF NOT EXISTS idx_notifications_customer_id ON notifications(customer_id);
CREATE INDEX IF NOT EXISTS idx_notifications_status ON notifications(status);
CREATE INDEX IF NOT EXISTS idx_notifications_type ON notifications(type);
CREATE INDEX IF NOT EXISTS idx_notifications_sent_at ON notifications(sent_at);

COMMENT ON TABLE notifications IS 'Notification history';
COMMENT ON COLUMN notifications.type IS 'Type: ORDER_CREATED, ORDER_CONFIRMED, etc.';
COMMENT ON COLUMN notifications.channel IS 'Channel: EMAIL, SMS, PUSH, LOG';