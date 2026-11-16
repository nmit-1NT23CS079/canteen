import { useState } from 'react';
import { setToken } from './api';
import Login from './components/Login';
import Menu from './components/Menu';
import Cart from './components/Cart';
import Orders from './components/Orders';
import Admin from './components/Admin';

export default function App() {
  const [user, setUser] = useState(() => JSON.parse(localStorage.getItem('user') || 'null'));
  const [view, setView] = useState('menu');
  // Cart state is lifted here so Menu, AI search and Cart (siblings) can share it.
  const [cart, setCart] = useState([]); // [{ item, quantity }]

  const addToCart = (item) =>
    setCart((c) => {
      const found = c.find((x) => x.item.id === item.id);
      return found
        ? c.map((x) => (x.item.id === item.id ? { ...x, quantity: x.quantity + 1 } : x))
        : [...c, { item, quantity: 1 }];
    });

  const changeQty = (id, delta) =>
    setCart((c) =>
      c.map((x) => (x.item.id === id ? { ...x, quantity: x.quantity + delta } : x)).filter((x) => x.quantity > 0)
    );

  const handleLogin = ({ token, user }) => {
    setToken(token);
    localStorage.setItem('user', JSON.stringify(user));
    setUser(user);
    setView(user.role === 'ADMIN' ? 'admin' : 'menu');
  };

  const logout = () => {
    setToken(null);
    localStorage.removeItem('user');
    setUser(null);
    setCart([]);
  };

  if (!user) return <Login onLogin={handleLogin} />;

  const cartCount = cart.reduce((n, x) => n + x.quantity, 0);

  return (
    <div className="app">
      <header className="nav">
        <h1>🍽️ Smart Canteen</h1>
        <nav>
          {user.role === 'STUDENT' && (
            <>
              <button className={view === 'menu' ? 'active' : ''} onClick={() => setView('menu')}>Menu</button>
              <button className={view === 'cart' ? 'active' : ''} onClick={() => setView('cart')}>Cart ({cartCount})</button>
              <button className={view === 'orders' ? 'active' : ''} onClick={() => setView('orders')}>My Orders</button>
            </>
          )}
          {user.role === 'ADMIN' && (
            <button className="active" onClick={() => setView('admin')}>Admin Dashboard</button>
          )}
          <span className="user">{user.name} ({user.role})</span>
          <button onClick={logout}>Logout</button>
        </nav>
      </header>

      <main>
        {view === 'menu' && user.role === 'STUDENT' && <Menu cart={cart} onAdd={addToCart} onQty={changeQty} />}
        {view === 'cart' && (
          <Cart cart={cart} onQty={changeQty} onAdd={addToCart} onPlaced={() => { setCart([]); setView('orders'); }} />
        )}
        {view === 'orders' && <Orders />}
        {view === 'admin' && user.role === 'ADMIN' && <Admin />}
      </main>
    </div>
  );
}
