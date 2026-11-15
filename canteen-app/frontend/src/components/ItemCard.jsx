export default function ItemCard({ item, onAdd }) {
  return (
    <div className="card">
      <div className="card-head">
        <span className={`dot ${item.veg ? 'veg' : 'nonveg'}`} title={item.veg ? 'Veg' : 'Non-veg'} />
        <h3>{item.name}</h3>
        {item.spicy && <span title="Spicy">🌶️</span>}
      </div>
      <p className="desc">{item.description}</p>
      <div className="card-foot">
        <strong>₹{item.price}</strong>
        <button className="primary" disabled={!item.available} onClick={() => onAdd(item)}>
          {item.available ? 'Add' : 'Unavailable'}
        </button>
      </div>
    </div>
  );
}
