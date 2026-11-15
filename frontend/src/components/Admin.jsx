import { useEffect, useState } from 'react';
import { api } from '../api';

const STATUSES = ['PLACED', 'PREPARING', 'READY', 'COMPLETED', 'CANCELLED'];
const EMPTY = { name: '', category: 'Snacks', price: '', veg: true, spicy: false, available: true, description: '' };

export default function Admin() {
  const [stats, setStats] = useState(null);
  const [orders, setOrders] = useState([]);
  const [menu, setMenu] = useState([]);
  const [form, setForm] = useState(EMPTY);
  const [error, setError] = useState('');

  const load = () =>
    Promise.all([api.stats(), api.allOrders(), api.menu()])
      .then(([s, o, m]) => { setStats(s); setOrders(o); setMenu(m); })
      .catch((e) => setError(e.message));

  useEffect(() => { load(); }, []);

  const changeStatus = async (id, status) => { await api.setStatus(id, status); load(); };

  const addItem = async (e) => {
    e.preventDefault();
    try {
      await api.addItem({ ...form, price: Number(form.price) });
      setForm(EMPTY);
      load();
    } catch (err) { setError(err.message); }
  };

  const toggle = async (item) => { await api.updateItem(item.id, { ...item, available: !item.available }); load(); };
  const remove = async (id) => { await api.deleteItem(id); load(); };

  return (
    <section>
      {error && <div className="err">{error}</div>}
      {stats && (
        <div className="stats">
          <div className="stat"><h3>{stats.totalOrders}</h3><span>Orders</span></div>
          <div className="stat"><h3>₹{stats.totalSales}</h3><span>Sales</span></div>
          {Object.entries(stats.byStatus).map(([k, v]) => (
            <div key={k} className="stat"><h3>{v}</h3><span>{k}</span></div>
          ))}
        </div>
      )}

      <h2>Orders</h2>
      <table>
        <thead><tr><th>Ref</th><th>Student</th><th>Items</th><th>Slot</th><th>Total</th><th>Status</th></tr></thead>
        <tbody>
          {orders.map((o) => (
            <tr key={o.id}>
              <td>{o.reference}</td>
              <td>{o.user?.name}</td>
              <td>{o.items.map((i) => `${i.menuItem.name}×${i.quantity}`).join(', ')}</td>
              <td>{o.pickupSlot}</td>
              <td>₹{o.total.toFixed(2)}</td>
              <td>
                <select value={o.status} onChange={(e) => changeStatus(o.id, e.target.value)}>
                  {STATUSES.map((s) => <option key={s}>{s}</option>)}
                </select>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <h2>Menu management</h2>
      <form className="admin-form" onSubmit={addItem}>
        <input placeholder="Name" value={form.name} required onChange={(e) => setForm({ ...form, name: e.target.value })} />
        <select value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })}>
          {['Breakfast', 'Meals', 'Snacks', 'Beverages'].map((c) => <option key={c}>{c}</option>)}
        </select>
        <input type="number" placeholder="Price" value={form.price} required onChange={(e) => setForm({ ...form, price: e.target.value })} />
        <input placeholder="Description" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
        <label><input type="checkbox" checked={form.veg} onChange={(e) => setForm({ ...form, veg: e.target.checked })} /> Veg</label>
        <label><input type="checkbox" checked={form.spicy} onChange={(e) => setForm({ ...form, spicy: e.target.checked })} /> Spicy</label>
        <button className="primary" type="submit">Add item</button>
      </form>
      <table>
        <thead><tr><th>Name</th><th>Category</th><th>Price</th><th>Available</th><th></th></tr></thead>
        <tbody>
          {menu.map((m) => (
            <tr key={m.id}>
              <td>{m.name}</td><td>{m.category}</td><td>₹{m.price}</td>
              <td><button onClick={() => toggle(m)}>{m.available ? 'Yes' : 'No'}</button></td>
              <td><button onClick={() => remove(m.id)}>Delete</button></td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}
