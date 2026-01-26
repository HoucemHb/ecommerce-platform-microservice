import React, { useState, useEffect } from 'react';
import { ShoppingCart, Package, CreditCard, Bell, Search, Filter, Plus, X, Check, AlertCircle, TrendingUp, Users, DollarSign, Activity } from 'lucide-react';
import '../../styles.css'; // ou le chemin vers ton fichier Tailwind buildé

const API_BASE = 'http://localhost:8080/api';

const EcommerceDashboard = () => {
  const [activeTab, setActiveTab] = useState('dashboard');
  const [orders, setOrders] = useState([]);
  const [products, setProducts] = useState([]);
  const [payments, setPayments] = useState([]);
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [filterStatus, setFilterStatus] = useState('all');

  // Fetch data
  useEffect(() => {
    fetchAllData();
  }, []);

  const fetchAllData = async () => {
    setLoading(true);
    try {
      const [ordersRes, productsRes, paymentsRes, notificationsRes] = await Promise.all([
        fetch(`${API_BASE}/orders`).then(r => r.json()).catch(() => []),
        fetch(`${API_BASE}/products`).then(r => r.json()).catch(() => []),
        fetch(`${API_BASE}/payments`).then(r => r.json()).catch(() => []),
        fetch(`${API_BASE}/notifications`).then(r => r.json()).catch(() => [])
      ]);
      setOrders(ordersRes.reverse());
      setProducts(productsRes.reverse());
      setPayments(paymentsRes.reverse());
      setNotifications(notificationsRes.reverse());
    } catch (error) {
      console.error('Error fetching data:', error);
    }
    setLoading(false);
  };

  const createOrder = async (orderData) => {
    try {
      const response = await fetch(`${API_BASE}/orders`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(orderData)
      });
      const orderId = await response.text();
      fetchAllData();
      return orderId;
    } catch (error) {
      console.error('Error creating order:', error);
    }
  };

  const deleteProduct = async (productId) => {
  if (!window.confirm("Voulez-vous vraiment supprimer ce produit ?")) return;

  try {
    await fetch(`${API_BASE}/products/${productId}`, { method: 'DELETE' });
    // Mettre à jour la liste des produits après suppression
    setProducts(products.filter(p => p.productId !== productId));
    alert("Produit supprimé avec succès !");
  } catch (error) {
    console.error("Erreur lors de la suppression du produit :", error);
    alert("Impossible de supprimer le produit.");
  }
};


  const confirmOrder = async (orderId) => {
    try {
      await fetch(`${API_BASE}/orders/${orderId}/confirm`, { method: 'POST' });
      fetchAllData();
    } catch (error) {
      console.error('Error confirming order:', error);
    }
  };

  const cancelOrder = async (orderId, reason) => {
    try {
      await fetch(`${API_BASE}/orders/${orderId}/cancel?reason=${encodeURIComponent(reason)}`, { method: 'POST' });
      fetchAllData();
    } catch (error) {
      console.error('Error canceling order:', error);
    }
  };

  const createProduct = async (productData) => {
    try {
      await fetch(`${API_BASE}/products`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(productData)
      });
      fetchAllData();
    } catch (error) {
      console.error('Error creating product:', error);
    }
  };

  const validatePayment = async (paymentData) => {
    try {
      await fetch(`${API_BASE}/payments/validate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(paymentData)
      });
      fetchAllData();
    } catch (error) {
      console.error('Error validating payment:', error);
    }
  };

  const [paymentFormData, setPaymentFormData] = useState({
  orderId: '',
  customerId: '',
  amount: 0,
  paymentMethod: 'CREDIT_CARD'
});


  // Dashboard Stats
  const stats = {
    totalOrders: orders.length,
    totalRevenue: payments.reduce((sum, p) => sum + (p.amount || 0), 0),
    activeProducts: products.filter(p => p.availableStock > 0).length,
    pendingNotifications: notifications.filter(n => n.status === 'PENDING').length
  };

  const StatCard = ({ title, value, icon: Icon, color, trend }) => (
    <div className="bg-white rounded-2xl shadow-lg p-6 hover:shadow-xl transition-all duration-300 border border-gray-100">
      <div className="flex items-center justify-between mb-4">
        <div className={`p-3 rounded-xl bg-gradient-to-br ${color}`}>
          <Icon className="text-white" size={24} />
        </div>
        {trend && (
          <div className="flex items-center text-green-600 text-sm font-semibold">
            <TrendingUp size={16} className="mr-1" />
            {trend}
          </div>
        )}
      </div>
      <h3 className="text-gray-500 text-sm font-medium mb-1">{title}</h3>
      <p className="text-3xl font-bold text-gray-800">{value}</p>
    </div>
  );

  const OrderCard = ({ order }) => {
    const [formData, setFormData] = useState({
      orderId: '',
      customerId: '',
      amount: 0,
      paymentMethod: 'CREDIT_CARD'
    });
    const statusColors = {
      PENDING: 'bg-yellow-100 text-yellow-800 border-yellow-200',
      CONFIRMED: 'bg-blue-100 text-blue-800 border-blue-200',
      SHIPPED: 'bg-purple-100 text-purple-800 border-purple-200',
      DELIVERED: 'bg-green-100 text-green-800 border-green-200',
      CANCELLED: 'bg-red-100 text-red-800 border-red-200'
    };

    return (
      <div className="bg-white rounded-xl shadow-md hover:shadow-xl transition-all duration-300 p-6 border border-gray-100">
        <div className="flex justify-between items-start mb-4">
          <div>
            <h3 className="text-lg font-bold text-gray-800 mb-1">Order #{order.orderId?.substring(0, 8)}</h3>
            <p className="text-sm text-gray-500">Customer: {order.customerId}</p>
          </div>
          <span className={`px-3 py-1 rounded-full text-xs font-semibold border ${statusColors[order.status] || statusColors.PENDING}`}>
            {order.status}
          </span>
        </div>

        <div className="space-y-2 mb-4">
          <div className="flex justify-between text-sm">
            <span className="text-gray-600">Items:</span>
            <span className="font-semibold">{order.items?.length || 0}</span>
          </div>
          <div className="flex justify-between text-sm">
            <span className="text-gray-600">Total:</span>
            <span className="font-bold text-green-600">${order.totalAmount?.toFixed(2) || '0.00'}</span>
          </div>
          <div className="text-xs text-gray-500 mt-2">
            {order.shippingAddress?.street}, {order.shippingAddress?.city}
          </div>
        </div>

    <div className="flex gap-2">
  {order.status === 'PENDING' && (
    <>
      <button
  onClick={() => {
    console.log(order.orderId);
    console.log(order.customerId);
    console.log(order.totalAmount);

   setPaymentFormData({
      orderId: order.orderId,
      customerId: order.customerId,
      amount: order.totalAmount,
      paymentMethod: 'CREDIT_CARD'
    });
    setModalType('validate-payment');
    setShowModal(true);
  }}
  className="flex-1 bg-gradient-to-r from-green-500 to-green-600 text-white px-4 py-2 rounded-lg text-sm font-semibold hover:from-green-600 hover:to-green-700 transition-all"
>
  <Check size={16} className="inline mr-1" /> Pay
</button>

      <button
        onClick={() => cancelOrder(order.orderId, 'User requested')}
        className="flex-1 bg-gradient-to-r from-red-500 to-red-600 text-white px-4 py-2 rounded-lg text-sm font-semibold hover:from-red-600 hover:to-red-700 transition-all"
      >
        <X size={16} className="inline mr-1" /> Cancel
      </button>
    </>
  )}
</div>

      </div>
    );
  };

const ProductCard = ({ product }) => (
  <div className="bg-white rounded-xl shadow-md hover:shadow-xl transition-all duration-300 p-6 border border-gray-100 flex flex-col justify-between">
    <div className="flex justify-between items-start mb-4">
      <div className="flex-1">
        <h3 className="text-lg font-bold text-gray-800 mb-1">{product.name}</h3>
        <p className="text-sm text-gray-500 line-clamp-2">{product.description}</p>
      </div>
      <div className={`ml-4 px-3 py-1 rounded-full text-xs font-semibold ${product.availableStock > 10 ? 'bg-green-100 text-green-800' : product.availableStock > 0 ? 'bg-yellow-100 text-yellow-800' : 'bg-red-100 text-red-800'}`}>
        {product.availableStock > 0 ? 'In Stock' : 'Out of Stock'}
      </div>
    </div>

    <div className="space-y-2 mb-4">
      <div className="flex justify-between items-center">
        <span className="text-2xl font-bold text-gray-800">${product.price?.toFixed(2) || '0.00'}</span>
        <span className="text-sm text-gray-600">Stock: {product.availableStock}</span>
      </div>

      <div className="w-full bg-gray-200 rounded-full h-2">
        <div
          className="bg-gradient-to-r from-blue-500 to-purple-500 h-2 rounded-full transition-all duration-300"
          style={{ width: `${Math.min((product.availableStock / 100) * 100, 100)}%` }}
        />
      </div>
    </div>

    {/* Bouton Supprimer centré */}

  </div>
);



  const CreateProductModal = () => {
    const [formData, setFormData] = useState({
      name: '',
      description: '',
      price: 0,
      initialStock: 0
    });

    const handleSubmit = async () => {
      await createProduct(formData);
      setShowModal(false);
      setFormData({ name: '', description: '', price: 0, initialStock: 0 });
    };

    return (
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
        <div className="bg-white rounded-2xl shadow-2xl max-w-lg w-full">
          <div className="bg-gradient-to-r from-purple-600 to-pink-600 text-white p-6 rounded-t-2xl">
            <div className="flex justify-between items-center">
              <h2 className="text-2xl font-bold">Add New Product</h2>
              <button onClick={() => setShowModal(false)} className="hover:bg-white hover:bg-opacity-20 rounded-full p-2 transition-all">
                <X size={24} />
              </button>
            </div>
          </div>

          <div className="p-6 space-y-4">
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-2">Product Name</label>
              <input
                type="text"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                placeholder="Enter product name"
              />
            </div>

            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-2">Description</label>
              <textarea
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                placeholder="Product description"
                rows="3"
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Price (USD)</label>
                <input
                  type="number"
                  step="0.01"
                  value={formData.price}
                  onChange={(e) => setFormData({ ...formData, price: parseFloat(e.target.value) })}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500"
                  placeholder="0.00"
                />
              </div>
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Initial Stock</label>
                <input
                  type="number"
                  value={formData.initialStock}
                  onChange={(e) => setFormData({ ...formData, initialStock: parseInt(e.target.value) })}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500"
                  placeholder="0"
                />
              </div>
            </div>

            <button
              onClick={handleSubmit}
              className="w-full bg-gradient-to-r from-purple-600 to-pink-600 text-white py-4 rounded-xl font-bold text-lg hover:from-purple-700 hover:to-pink-700 transition-all shadow-lg"
            >
              Add Product
            </button>
          </div>
        </div>
      </div>
    );
  };

  const ValidatePaymentModal = () => {
    const [formData, setFormData] = useState({
      orderId: '',
      customerId: '',
      amount: 0,
      paymentMethod: 'CREDIT_CARD'
    });

    const handleSubmit = async () => {
    await validatePayment(paymentFormData);
    setShowModal(false);
    setPaymentFormData({
      orderId: '',
      customerId: '',
      amount: 0,
      paymentMethod: 'CREDIT_CARD'
    });
  };

    return (
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
        <div className="bg-white rounded-2xl shadow-2xl max-w-lg w-full">
          <div className="bg-gradient-to-r from-green-600 to-emerald-600 text-white p-6 rounded-t-2xl">
            <div className="flex justify-between items-center">
              <h2 className="text-2xl font-bold">Validate Payment</h2>
              <button onClick={() => setShowModal(false)} className="hover:bg-white hover:bg-opacity-20 rounded-full p-2 transition-all">
                <X size={24} />
              </button>
            </div>
          </div>

          <div className="p-6 space-y-4">
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-2">Order ID</label>
              <select
                value={paymentFormData.orderId}
                onChange={(e) => setPaymentFormData({ ...paymentFormData, orderId: e.target.value })}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
              >
                <option value="">Select an order</option>
                {orders.map(order => (
                  <option key={order.orderId} value={order.orderId}>
                    Order #{order.orderId?.substring(0, 8)} - {order.customerId}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-2">Customer ID</label>
              <input
                type="text"
                value={paymentFormData.customerId}
                onChange={(e) => setPaymentFormData({ ...paymentFormData, customerId: e.target.value })}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
                placeholder="Customer ID"
              />
            </div>

            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-2">Amount (USD)</label>
              <input
                type="number"
                step="0.01"
                value={paymentFormData.amount}
                onChange={(e) => setPaymentFormData({ ...paymentFormData, amount: parseFloat(e.target.value) })}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
                placeholder="0.00"
              />
            </div>

            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-2">Payment Method</label>
              <select
                value={paymentFormData.paymentMethod}
                onChange={(e) => setPaymentFormData({ ...paymentFormData, paymentMethod: e.target.value })}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
              >
                <option value="CREDIT_CARD">Credit Card</option>
                <option value="DEBIT_CARD">Debit Card</option>
                <option value="PAYPAL">PayPal</option>
                <option value="BANK_TRANSFER">Bank Transfer</option>
              </select>
            </div>

            <button
              onClick={handleSubmit}
              className="w-full bg-gradient-to-r from-green-600 to-emerald-600 text-white py-4 rounded-xl font-bold text-lg hover:from-green-700 hover:to-emerald-700 transition-all shadow-lg"
            >
              Validate Payment
            </button>
          </div>
        </div>
      </div>
    );
  };

  const RefundPaymentModal = () => {
    const [formData, setFormData] = useState({
      paymentId: '',
      orderId: '',
      amount: 0
    });

    const handleSubmit = async () => {
      try {
        await fetch(`${API_BASE}/payments/${formData.paymentId}/refund`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            orderId: formData.orderId,
            amount: formData.amount
          })
        });
        fetchAllData();
        setShowModal(false);
        setFormData({ paymentId: '', orderId: '', amount: 0 });
      } catch (error) {
        console.error('Error refunding payment:', error);
      }
    };

    return (
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
        <div className="bg-white rounded-2xl shadow-2xl max-w-lg w-full">
          <div className="bg-gradient-to-r from-red-600 to-orange-600 text-white p-6 rounded-t-2xl">
            <div className="flex justify-between items-center">
              <h2 className="text-2xl font-bold">Refund Payment</h2>
              <button onClick={() => setShowModal(false)} className="hover:bg-white hover:bg-opacity-20 rounded-full p-2 transition-all">
                <X size={24} />
              </button>
            </div>
          </div>

          <div className="p-6 space-y-4">
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-2">Payment ID</label>
              <select
                value={formData.paymentId}
                onChange={(e) => setFormData({ ...formData, paymentId: e.target.value })}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500"
              >
                <option value="">Select a payment</option>
                {payments.map(payment => (
                  <option key={payment.paymentId} value={payment.paymentId}>
                    Payment #{payment.paymentId?.substring(0, 8)} - ${payment.amount}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-2">Order ID</label>
              <input
                type="text"
                value={formData.orderId}
                onChange={(e) => setFormData({ ...formData, orderId: e.target.value })}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500"
                placeholder="Order ID"
              />
            </div>

            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-2">Refund Amount (USD)</label>
              <input
                type="number"
                step="0.01"
                value={formData.amount}
                onChange={(e) => setFormData({ ...formData, amount: parseFloat(e.target.value) })}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500"
                placeholder="0.00"
              />
            </div>

            <button
              onClick={handleSubmit}
              className="w-full bg-gradient-to-r from-red-600 to-orange-600 text-white py-4 rounded-xl font-bold text-lg hover:from-red-700 hover:to-orange-700 transition-all shadow-lg"
            >
              Process Refund
            </button>
          </div>
        </div>
      </div>
    );
  };

  const CreateOrderModal = () => {
    const [formData, setFormData] = useState({
      customerId: '',
      items: [{ productId: '', quantity: 1, unitPrice: 0 }],
      shippingAddress: { street: '', city: '', zipCode: '', country: '' },
      totalAmount: 0
    });

    const addItem = () => {
      setFormData({
        ...formData,
        items: [...formData.items, { productId: '', quantity: 1, unitPrice: 0 }]
      });
    };

    const removeItem = (index) => {
      const newItems = formData.items.filter((_, i) => i !== index);
      setFormData({ ...formData, items: newItems });
    };

    const updateItem = (index, field, value) => {
      const newItems = [...formData.items];
      newItems[index][field] = value;
      setFormData({ ...formData, items: newItems });
    };

    const handleSubmit = async () => {
      await createOrder(formData);
      setShowModal(false);
      setFormData({
        customerId: '',
        items: [{ productId: '', quantity: 1, unitPrice: 0 }],
        shippingAddress: { street: '', city: '', zipCode: '', country: '' },
        totalAmount: 0
      });
    };

  //   return (
  //     <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
  //       <div className="bg-white rounded-2xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
  //         <div className="sticky top-0 bg-gradient-to-r from-blue-600 to-purple-600 text-white p-6 rounded-t-2xl">
  //           <div className="flex justify-between items-center">
  //             <h2 className="text-2xl font-bold">Create New Order</h2>
  //             <button onClick={() => setShowModal(false)} className="hover:bg-white hover:bg-opacity-20 rounded-full p-2 transition-all">
  //               <X size={24} />
  //             </button>
  //           </div>
  //         </div>
  //
  //         <div className="p-6 space-y-6">
  //           <div>
  //             <label className="block text-sm font-semibold text-gray-700 mb-2">Customer ID</label>
  //             <input
  //               type="text"
  //               value={formData.customerId}
  //               onChange={(e) => setFormData({ ...formData, customerId: e.target.value })}
  //               className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
  //               placeholder="Enter customer ID"
  //             />
  //           </div>
  //
  //           <div>
  //             <div className="flex justify-between items-center mb-3">
  //               <label className="text-sm font-semibold text-gray-700">Order Items</label>
  //               <button onClick={addItem} className="bg-blue-500 text-white px-3 py-1 rounded-lg text-sm hover:bg-blue-600 transition-all">
  //                 <Plus size={16} className="inline mr-1" /> Add Item
  //               </button>
  //             </div>
  //             {formData.items.map((item, index) => (
  //               <div key={index} className="grid grid-cols-4 gap-3 mb-3 p-4 bg-gray-50 rounded-lg">
  //                 <select
  //                   value={item.productId}
  //                   onChange={(e) => {
  //                     const product = products.find(p => p.productId === e.target.value);
  //                     updateItem(index, 'productId', e.target.value);
  //                     if (product) updateItem(index, 'unitPrice', product.price || 0);
  //                   }}
  //                   className="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500"
  //                 >
  //                   <option value="">Select Product</option>
  //                   {products.map(p => (
  //                     <option key={p.productId} value={p.productId}>
  //                       {p.name} (${p.price})
  //                     </option>
  //                   ))}
  //                 </select>
  //                 <input
  //                   type="number"
  //                   placeholder="Quantity"
  //                   value={item.quantity}
  //                   onChange={(e) => updateItem(index, 'quantity', parseInt(e.target.value))}
  //                   className="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500"
  //                 />
  //                 <input
  //                   type="number"
  //                   placeholder="Price"
  //                   value={item.unitPrice}
  //                   onChange={(e) => updateItem(index, 'unitPrice', parseFloat(e.target.value))}
  //                   className="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500"
  //                 />
  //                 <button
  //                   onClick={() => removeItem(index)}
  //                   className="bg-red-500 text-white px-3 py-2 rounded-lg hover:bg-red-600 transition-all"
  //                 >
  //                   <X size={16} />
  //                 </button>
  //               </div>
  //             ))}
  //           </div>
  //
  //           <div className="grid grid-cols-2 gap-4">
  //             <input
  //               type="text"
  //               placeholder="Street"
  //               value={formData.shippingAddress.street}
  //               onChange={(e) => setFormData({ ...formData, shippingAddress: { ...formData.shippingAddress, street: e.target.value } })}
  //               className="px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
  //             />
  //             <input
  //               type="text"
  //               placeholder="City"
  //               value={formData.shippingAddress.city}
  //               onChange={(e) => setFormData({ ...formData, shippingAddress: { ...formData.shippingAddress, city: e.target.value } })}
  //               className="px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
  //             />
  //             <input
  //               type="text"
  //               placeholder="ZIP Code"
  //               value={formData.shippingAddress.zipCode}
  //               onChange={(e) => setFormData({ ...formData, shippingAddress: { ...formData.shippingAddress, zipCode: e.target.value } })}
  //               className="px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
  //             />
  //             <input
  //               type="text"
  //               placeholder="Country"
  //               value={formData.shippingAddress.country}
  //               onChange={(e) => setFormData({ ...formData, shippingAddress: { ...formData.shippingAddress, country: e.target.value } })}
  //               className="px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
  //             />
  //           </div>
  //
  //           <div>
  //             <label className="block text-sm font-semibold text-gray-700 mb-2">Total Amount</label>
  //             <input
  //               type="number"
  //               value={formData.totalAmount}
  //               onChange={(e) => setFormData({ ...formData, totalAmount: parseFloat(e.target.value) })}
  //               className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
  //               placeholder="0.00"
  //             />
  //           </div>
  //
  //           <button
  //             onClick={handleSubmit}
  //             className="w-full bg-gradient-to-r from-blue-600 to-purple-600 text-white py-4 rounded-xl font-bold text-lg hover:from-blue-700 hover:to-purple-700 transition-all shadow-lg"
  //           >
  //             Create Order
  //           </button>
  //         </div>
  //       </div>
  //     </div>
  //   );
    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">

            {/* Header */}
            <div className="sticky top-0 bg-gradient-to-r from-blue-600 to-purple-600 text-white p-6 rounded-t-2xl">
              <div className="flex justify-between items-center">
                <h2 className="text-2xl font-bold">Create New Order</h2>
                <button
                    onClick={() => setShowModal(false)}
                    className="hover:bg-white hover:bg-opacity-20 rounded-full p-2 transition-all"
                >
                  <X size={24} />
                </button>
              </div>
            </div>

            {/* Body */}
            <div className="p-6 space-y-6">

              {/* Customer ID */}
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Customer ID</label>
                <input
                    type="text"
                    value={formData.customerId}
                    onChange={(e) => setFormData({ ...formData, customerId: e.target.value })}
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                    placeholder="Enter customer ID"
                />
              </div>

              {/* Order Items */}
              <div>
                <div className="flex justify-between items-center mb-3">
                  <label className="text-sm font-semibold text-gray-700">Order Items</label>
                  <button
                      onClick={addItem}
                      className="bg-blue-500 text-white px-3 py-1 rounded-lg text-sm hover:bg-blue-600 transition-all"
                  >
                    <Plus size={16} className="inline mr-1" /> Add Item
                  </button>
                </div>

                {/* Calcul des sous-totaux et du total */}
                {formData.items.map((item, index) => {
                  const subtotal = (item.quantity || 0) * (item.unitPrice || 0);
                  return (
                      <div key={index} className="grid grid-cols-5 gap-3 mb-3 p-4 bg-gray-50 rounded-lg items-center">
                        <select
                            value={item.productId}
                            onChange={(e) => {
                              const product = products.find(p => p.productId === e.target.value);
                              updateItem(index, 'productId', e.target.value);
                              if (product) updateItem(index, 'unitPrice', product.price || 0);
                            }}
                            className="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500"
                        >
                          <option value="">Select Product</option>
                          {products.map(p => (
                              <option key={p.productId} value={p.productId}>
                                {p.name} (${p.price})
                              </option>
                          ))}
                        </select>

                        {/* Quantity */}
                        <input
                            type="number"
                            placeholder="Quantity"
                            value={item.quantity}
                            onChange={(e) => updateItem(index, 'quantity', parseInt(e.target.value))}
                            className="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500"
                        />

                        {/* Unit Price (text) */}
                        <span className="px-3 py-2 border border-gray-300 rounded-lg text-sm flex items-center">
                  ${item.unitPrice?.toFixed(2) || '0.00'}
                </span>

                        {/* Subtotal (text) */}
                        <span className="px-3 py-2 border border-gray-300 rounded-lg text-sm flex items-center">
                  ${subtotal.toFixed(2)}
                </span>

                        {/* Remove button */}
                        <button
                            onClick={() => removeItem(index)}
                            className="bg-red-500 text-white px-3 py-2 rounded-lg hover:bg-red-600 transition-all"
                        >
                          <X size={16} />
                        </button>
                      </div>
                  );
                })}
              </div>

              {/* Shipping Address */}
              <div className="grid grid-cols-2 gap-4">
                <input
                    type="text"
                    placeholder="Street"
                    value={formData.shippingAddress.street}
                    onChange={(e) => setFormData({
                      ...formData,
                      shippingAddress: { ...formData.shippingAddress, street: e.target.value }
                    })}
                    className="px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
                <input
                    type="text"
                    placeholder="City"
                    value={formData.shippingAddress.city}
                    onChange={(e) => setFormData({
                      ...formData,
                      shippingAddress: { ...formData.shippingAddress, city: e.target.value }
                    })}
                    className="px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
                <input
                    type="text"
                    placeholder="ZIP Code"
                    value={formData.shippingAddress.zipCode}
                    onChange={(e) => setFormData({
                      ...formData,
                      shippingAddress: { ...formData.shippingAddress, zipCode: e.target.value }
                    })}
                    className="px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
                <input
                    type="text"
                    placeholder="Country"
                    value={formData.shippingAddress.country}
                    onChange={(e) => setFormData({
                      ...formData,
                      shippingAddress: { ...formData.shippingAddress, country: e.target.value }
                    })}
                    className="px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>

              {/* Total Amount */}
              <div className="text-right font-bold text-xl text-gray-800 mt-4">
                Total: ${formData.items.reduce((sum, item) => sum + ((item.quantity || 0) * (item.unitPrice || 0)), 0).toFixed(2)}
              </div>

              {/* Submit */}
              <button
                  onClick={handleSubmit}
                  className="w-full bg-gradient-to-r from-blue-600 to-purple-600 text-white py-4 rounded-xl font-bold text-lg hover:from-blue-700 hover:to-purple-700 transition-all shadow-lg"
              >
                Create Order
              </button>
            </div>
          </div>
        </div>
    );
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100">
      {/* Header */}
      <header className="bg-white shadow-lg border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-6 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-3">
              <div className="bg-gradient-to-r from-blue-600 to-purple-600 p-3 rounded-xl">
                <ShoppingCart className="text-white" size={28} />
              </div>
              <div>
                <h1 className="text-2xl font-bold bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
                  E-Commerce Manager
                </h1>
                <p className="text-sm text-gray-500">Microservices Architecture</p>
              </div>
            </div>

            <div className="flex items-center space-x-4">
              <div className="relative">
                <input
                  type="text"
                  placeholder="Search..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent w-64"
                />
                <Search className="absolute left-3 top-2.5 text-gray-400" size={20} />
              </div>

              <button className="relative p-2 hover:bg-gray-100 rounded-lg transition-all">
                <Bell size={24} className="text-gray-600" />
                {notifications.filter(n => n.status === 'PENDING').length > 0 && (
                  <span className="absolute top-0 right-0 bg-red-500 text-white text-xs w-5 h-5 flex items-center justify-center rounded-full font-bold">
                    {notifications.filter(n => n.status === 'PENDING').length}
                  </span>
                )}
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Navigation */}
      <nav className="bg-white shadow-md border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-6">
          <div className="flex space-x-1">
            {[
              { id: 'dashboard', label: 'Dashboard', icon: Activity },
              { id: 'orders', label: 'Orders', icon: ShoppingCart },
              { id: 'products', label: 'Products', icon: Package },
              { id: 'payments', label: 'Payments', icon: CreditCard },
              { id: 'notifications', label: 'Notifications', icon: Bell }
            ].map(tab => {
              const Icon = tab.icon;
              return (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={`flex items-center space-x-2 px-6 py-4 font-semibold transition-all ${
                    activeTab === tab.id
                      ? 'border-b-4 border-blue-600 text-blue-600'
                      : 'text-gray-600 hover:text-blue-600 hover:bg-gray-50'
                  }`}
                >
                  <Icon size={20} />
                  <span>{tab.label}</span>
                </button>
              );
            })}
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-6 py-8">
        {loading ? (
          <div className="flex items-center justify-center h-64">
            <div className="animate-spin rounded-full h-16 w-16 border-b-4 border-blue-600"></div>
          </div>
        ) : (
          <>
            {activeTab === 'dashboard' && (
              <div className="space-y-8">
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                  <StatCard
                    title="Total Orders"
                    value={stats.totalOrders}
                    icon={ShoppingCart}
                    color="from-blue-500 to-blue-600"
                    trend="+12%"
                  />
                  <StatCard
                    title="Revenue"
                    value={`$${stats.totalRevenue.toFixed(2)}`}
                    icon={DollarSign}
                    color="from-green-500 to-green-600"
                    trend="+23%"
                  />
                  <StatCard
                    title="Active Products"
                    value={stats.activeProducts}
                    icon={Package}
                    color="from-purple-500 to-purple-600"
                    trend="+8%"
                  />
                  <StatCard
                    title="Notifications"
                    value={stats.pendingNotifications}
                    icon={Bell}
                    color="from-orange-500 to-orange-600"
                  />
                </div>

                <div className="bg-white rounded-2xl shadow-lg p-6 border border-gray-100">
                  <h2 className="text-xl font-bold text-gray-800 mb-4">Recent Orders</h2>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {orders.slice(0, 4).map(order => (
                      <OrderCard key={order.orderId} order={order} />
                    ))}
                  </div>
                </div>
              </div>
            )}

            {activeTab === 'orders' && (
              <div className="space-y-6">
                <div className="flex justify-between items-center">
                  <h2 className="text-2xl font-bold text-gray-800">Orders Management</h2>
                  <button
                    onClick={() => { setShowModal(true); setModalType('order'); }}
                    className="bg-gradient-to-r from-blue-600 to-purple-600 text-white px-6 py-3 rounded-xl font-semibold hover:from-blue-700 hover:to-purple-700 transition-all shadow-lg"
                  >
                    <Plus size={20} className="inline mr-2" />
                    New Order
                  </button>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                  {orders.map(order => (
                    <OrderCard key={order.orderId} order={order} />
                  ))}
                </div>
              </div>
            )}

            {activeTab === 'products' && (
              <div className="space-y-6">
                <div className="flex justify-between items-center">
                  <h2 className="text-2xl font-bold text-gray-800">Products Catalog</h2>
                  <button
                    onClick={() => { setShowModal(true); setModalType('product'); }}
                    className="bg-gradient-to-r from-blue-600 to-purple-600 text-white px-6 py-3 rounded-xl font-semibold hover:from-blue-700 hover:to-purple-700 transition-all shadow-lg"
                  >
                    <Plus size={20} className="inline mr-2" />
                    New Product
                  </button>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                  {products.map(product => (
                    <ProductCard key={product.productId} product={product} />
                  ))}
                </div>
              </div>
            )}

            {activeTab === 'payments' && (
              <div className="space-y-6">
                <div className="flex justify-between items-center">
                  <h2 className="text-2xl font-bold text-gray-800">Payments History</h2>
                  <div className="flex gap-3">

                    {/* <button
                      onClick={() => { setShowModal(true); setModalType('refund-payment'); }}
                      className="bg-gradient-to-r from-red-600 to-orange-600 text-white px-6 py-3 rounded-xl font-semibold hover:from-red-700 hover:to-orange-700 transition-all shadow-lg"
                    >
                      <X size={20} className="inline mr-2" />
                      Refund Payment
                    </button> */}
                  </div>
                </div>
                <div className="bg-white rounded-2xl shadow-lg overflow-hidden border border-gray-100">
                  <table className="w-full">
                    <thead className="bg-gradient-to-r from-blue-600 to-purple-600 text-white">
                      <tr>
                        <th className="px-6 py-4 text-left font-semibold">Payment ID</th>
                        <th className="px-6 py-4 text-left font-semibold">Order ID</th>
                        <th className="px-6 py-4 text-left font-semibold">Customer</th>
                        <th className="px-6 py-4 text-left font-semibold">Amount</th>
                        <th className="px-6 py-4 text-left font-semibold">Method</th>
                        <th className="px-6 py-4 text-left font-semibold">Status</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-200">
                      {payments.map((payment, idx) => (
                        <tr key={idx} className="hover:bg-gray-50 transition-colors">
                          <td className="px-6 py-4 text-sm text-gray-800 font-mono">{payment.paymentId?.substring(0, 8)}</td>
                          <td className="px-6 py-4 text-sm text-gray-600 font-mono">{payment.orderId?.substring(0, 8)}</td>
                          <td className="px-6 py-4 text-sm text-gray-600">{payment.customerId}</td>
                          <td className="px-6 py-4 text-sm font-bold text-green-600">${payment.amount?.toFixed(2)}</td>
                          <td className="px-6 py-4 text-sm text-gray-600">{payment.paymentMethod}</td>
                          <td className="px-6 py-4">
                            <span className="px-3 py-1 rounded-full text-xs font-semibold bg-green-100 text-green-800">
                              {payment.status || 'COMPLETED'}
                            </span>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            )}

            {activeTab === 'notifications' && (
              <div className="space-y-6">
                <h2 className="text-2xl font-bold text-gray-800">Notifications Center</h2>
                <div className="space-y-4">
                  {notifications.map((notif, idx) => (
                    <div key={idx} className="bg-white rounded-xl shadow-md p-6 border-l-4 border-blue-500 hover:shadow-lg transition-all">
                      <div className="flex items-start justify-between">
                        <div className="flex items-start space-x-4">
                          <div className="bg-blue-100 p-3 rounded-lg">
                            <Bell className="text-blue-600" size={24} />
                          </div>
                          <div>
                            <h3 className="font-bold text-gray-800 mb-1">{notif.type}</h3>
                            <p className="text-sm text-gray-600 mb-2">{notif.message}</p>
                            <p className="text-xs text-gray-400">Order: {notif.orderId?.substring(0, 8)}</p>
                          </div>
                        </div>
                        <span className={`px-3 py-1 rounded-full text-xs font-semibold ${
                          notif.status === 'SENT' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'
                        }`}>
                          {notif.status}
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </>
        )}
      </main>

      {showModal && modalType === 'order' && <CreateOrderModal />}
      {showModal && modalType === 'product' && <CreateProductModal />}
      {showModal && modalType === 'validate-payment' && <ValidatePaymentModal />}
      {showModal && modalType === 'refund-payment' && <RefundPaymentModal />}
    </div>
  );
};

export default EcommerceDashboard;
