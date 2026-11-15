import { useEffect, useState } from 'react';
import { api } from '../api';

export default function Login({ onLogin }) {
  const [mode, setMode] = useState('login');
  const [form, setForm] = useState({ name: '', email: '', password: '' });
  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState('');

  const update = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  // Live validation each time the input changes (useEffect + useState)
  useEffect(() => {
    const e = {};
    if (mode === 'register' && form.name && form.name.trim().length < 2) e.name = 'Name is too short';
    if (form.email && !/^\S+@\S+\.\S+$/.test(form.email)) e.email = 'Enter a valid email';
    if (form.password && form.password.length < 6) e.password = 'Minimum 6 characters';
    setErrors(e);
  }, [form, mode]);

  const submit = async (ev) => {
    ev.preventDefault();
    setServerError('');
    if (Object.keys(errors).length || !form.email || !form.password || (mode === 'register' && !form.name)) {
      setServerError('Please fill all fields correctly');
      return;
    }
    try {
      if (mode === 'register') await api.register(form);
      onLogin(await api.login({ email: form.email, password: form.password }));
    } catch (err) {
      setServerError(err.message);
    }
  };

  return (
    <div className="login-box">
      <h1>🍽️ Smart Canteen</h1>
      <h2>{mode === 'login' ? 'Login' : 'Create account'}</h2>
      <form onSubmit={submit}>
        {mode === 'register' && (
          <>
            <input name="name" placeholder="Full name" value={form.name} onChange={update} />
            {errors.name && <small className="err">{errors.name}</small>}
          </>
        )}
        <input name="email" placeholder="Email" value={form.email} onChange={update} />
        {errors.email && <small className="err">{errors.email}</small>}
        <input name="password" type="password" placeholder="Password" value={form.password} onChange={update} />
        {errors.password && <small className="err">{errors.password}</small>}
        {serverError && <div className="err">{serverError}</div>}
        <button type="submit" className="primary">{mode === 'login' ? 'Login' : 'Register'}</button>
      </form>
      <p className="link" onClick={() => setMode(mode === 'login' ? 'register' : 'login')}>
        {mode === 'login' ? 'New here? Register' : 'Already have an account? Login'}
      </p>
      <p className="hint">Demo: student@canteen.com / student123 &nbsp;|&nbsp; admin@canteen.com / admin123</p>
    </div>
  );
}
