import { useEffect, useState } from 'react';
import { api } from '../api';

const SLOTS = ['12:00 PM', '12:30 PM', '1:00 PM', '1:30 PM', '2:00 PM', '4:30 PM', '5:00 PM'];

export default function Cart({ cart, onQty, onAdd, onPlaced }) {
  const [slot, setSlot] = useState('');
  const [error, setError] = useState('');
  const [recs, setRecs] = useState([]);

  const subtotal = cart.reduce((s, x) => s + x.item.price * x.quantity, 0);

  // AI recommendations based on the last item added ("people also ordered")
  const lastId = cart.length ? cart[cart.length - 1].item.id : null;
  useEffect(() => {
    api.recommend(lastId, 3)
      .then((r) => setRecs(r.filter((i) => !cart.some((x) => x.item.id === i.id))))
      .catch(() => setRecs([]));
  }, [lastId, cart.length]);

  const place = async () => {
    setError('');
    try {
      await api.placeOrder({
        pickupSlot: slot,
        items: cart.map((x) => ({ menuItemId: x.item.id, quantity: x.quantity })),
      });
      onPlaced();
    } catch (e) {
      setError(e.message);
    }
  };

  if (cart.length === 0) return <p>Your cart is empty. Add something from the menu!</p>;

  return (
    <section>
      <h2>Your cart</h2>
      <table>
        <thead><tr><th>Item</th><th>Qty</th><th>Price</th></tr></thead>
        <tbody>
          {cart.map(({ item, quantity }) => (
            <tr key={item.id}>
              <td>{item.name}</td>
              <td>
                <button onClick={() => onQty(item.id, -1)}>−</button> {quantity}{' '}
                <button onClick={() => onQty(item.id, 1)}>+</button>
              </td>
              <td>₹{item.price * quantity}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <p><strong>Subtotal: ₹{subtotal}</strong> <span className="hint">(10% off above ₹500, 5% GST added at checkout)</span></p>

      <label>Pickup slot:{' '}
        <select value={slot} onChange={(e) => setSlot(e.target.value)}>
          <option value="">Select slot</option>
          {SLOTS.map((s) => <option key={s}>{s}</option>)}
        </select>
      </label>
      <button className="primary" disabled={!slot} onClick={place}>Place order</button>
      {error && <div className="err">{error}</div>}

      {recs.length > 0 && (
        <div className="recs">
          <h3>🤖 You may also like</h3>
          {recs.map((i) => (
            <span key={i.id} className="chip" onClick={() => onAdd(i)}>+ {i.name} (₹{i.price})</span>
          ))}
        </div>
      )}
    </section>
  );
}
