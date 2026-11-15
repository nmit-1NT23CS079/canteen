import { useEffect, useState } from 'react';
import { api } from '../api';

export default function Orders() {
  const [orders, setOrders] = useState([]);
  const [error, setError] = useState('');

  const load = () => api.myOrders().then(setOrders).catch((e) => setError(e.message));

  useEffect(() => {
    load();
    const t = setInterval(load, 5000); // poll for status updates
    return () => clearInterval(t);
  }, []);

  return (
    <section>
      <h2>My orders</h2>
      {error && <div className="err">{error}</div>}
      {orders.length === 0 && <p>No orders yet.</p>}
      {orders.map((o) => (
        <div key={o.id} className="order">
          <div className="order-head">
            <strong>{o.reference}</strong>
            <span className={`badge ${o.status}`}>{o.status}</span>
          </div>
          <div>Pickup: {o.pickupSlot} · {new Date(o.createdAt).toLocaleString()}</div>
          <ul>
            {o.items.map((i) => <li key={i.id}>{i.menuItem.name} × {i.quantity}</li>)}
          </ul>
          <div>Subtotal ₹{o.subtotal} · Discount ₹{o.discount} · GST ₹{o.tax} · <strong>Total ₹{o.total.toFixed(2)}</strong></div>
        </div>
      ))}
    </section>
  );
}
