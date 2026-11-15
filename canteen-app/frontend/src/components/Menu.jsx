import { useEffect, useState } from 'react';
import { api } from '../api';
import ItemCard from './ItemCard';

const CATEGORIES = ['', 'Breakfast', 'Meals', 'Snacks', 'Beverages'];

export default function Menu({ onAdd }) {
  const [items, setItems] = useState([]);
  const [category, setCategory] = useState('');
  const [veg, setVeg] = useState('');
  const [maxPrice, setMaxPrice] = useState('');
  const [aiQuery, setAiQuery] = useState('');
  const [aiInfo, setAiInfo] = useState(null);
  const [error, setError] = useState('');

  // Re-fetch whenever a filter changes
  useEffect(() => {
    if (aiInfo) return; // AI results are being shown
    api.menu({ category, veg, maxPrice }).then(setItems).catch((e) => setError(e.message));
  }, [category, veg, maxPrice, aiInfo]);

  const runAi = async (e) => {
    e.preventDefault();
    if (!aiQuery.trim()) return clearAi();
    try {
      const res = await api.aiSearch(aiQuery);
      setItems(res.results);
      setAiInfo(res.understood);
    } catch (err) {
      setError(err.message);
    }
  };

  const clearAi = () => { setAiInfo(null); setAiQuery(''); };

  return (
    <section>
      <form className="ai-bar" onSubmit={runAi}>
        <input
          placeholder='🤖 Ask AI: "spicy veg snacks under 60" or "cheap coffee"'
          value={aiQuery}
          onChange={(e) => setAiQuery(e.target.value)}
        />
        <button className="primary" type="submit">Search</button>
        {aiInfo && <button type="button" onClick={clearAi}>Clear</button>}
      </form>

      {aiInfo && (
        <p className="hint">
          AI understood →{' '}
          {[
            aiInfo.veg === true && 'veg',
            aiInfo.veg === false && 'non-veg',
            aiInfo.spicy && 'spicy',
            aiInfo.maxPrice && `under ₹${aiInfo.maxPrice}`,
            aiInfo.category && aiInfo.category,
            ...(aiInfo.keywords || []),
          ].filter(Boolean).join(', ') || 'everything'}
        </p>
      )}

      {!aiInfo && (
        <div className="filters">
          <select value={category} onChange={(e) => setCategory(e.target.value)}>
            {CATEGORIES.map((c) => <option key={c} value={c}>{c || 'All categories'}</option>)}
          </select>
          <select value={veg} onChange={(e) => setVeg(e.target.value)}>
            <option value="">Veg & Non-veg</option>
            <option value="true">Veg only</option>
            <option value="false">Non-veg only</option>
          </select>
          <input type="number" placeholder="Max price ₹" value={maxPrice} onChange={(e) => setMaxPrice(e.target.value)} />
        </div>
      )}

      {error && <div className="err">{error}</div>}
      <div className="grid">
        {items.map((i) => <ItemCard key={i.id} item={i} onAdd={onAdd} />)}
        {items.length === 0 && <p>No items found.</p>}
      </div>
    </section>
  );
}
