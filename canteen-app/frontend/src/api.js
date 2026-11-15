// Thin fetch wrapper that attaches the auth token and surfaces backend error messages.
let token = localStorage.getItem('token');

export function setToken(t) {
  token = t;
  if (t) localStorage.setItem('token', t);
  else localStorage.removeItem('token');
}

async function request(path, options = {}) {
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
  if (token) headers.Authorization = `Bearer ${token}`;
  const res = await fetch(path, { ...options, headers });
  if (res.status === 204) return null;
  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(data.error || `Request failed (${res.status})`);
  return data;
}

export const api = {
  register: (body) => request('/api/auth/register', { method: 'POST', body: JSON.stringify(body) }),
  login: (body) => request('/api/auth/login', { method: 'POST', body: JSON.stringify(body) }),
  menu: (params = {}) => {
    const qs = new URLSearchParams(Object.entries(params).filter(([, v]) => v !== '' && v != null)).toString();
    return request(`/api/menu${qs ? `?${qs}` : ''}`);
  },
  aiSearch: (q) => request(`/api/ai/search?q=${encodeURIComponent(q)}`),
  recommend: (itemId, limit = 3) => request(`/api/ai/recommend?${itemId ? `itemId=${itemId}&` : ''}limit=${limit}`),
  placeOrder: (body) => request('/api/orders', { method: 'POST', body: JSON.stringify(body) }),
  myOrders: () => request('/api/orders/my'),
  allOrders: () => request('/api/orders'),
  setStatus: (id, status) => request(`/api/orders/${id}/status?status=${status}`, { method: 'PUT' }),
  stats: () => request('/api/orders/stats'),
  addItem: (item) => request('/api/menu', { method: 'POST', body: JSON.stringify(item) }),
  updateItem: (id, item) => request(`/api/menu/${id}`, { method: 'PUT', body: JSON.stringify(item) }),
  deleteItem: (id) => request(`/api/menu/${id}`, { method: 'DELETE' }),
};
