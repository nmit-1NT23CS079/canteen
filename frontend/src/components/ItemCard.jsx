export default function ItemCard({ item, quantity = 0, onAdd, onQty }) {
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
        {!item.available ? (
          <button className="primary" disabled>Unavailable</button>
        ) : quantity === 0 ? (
          <button className="primary" onClick={() => onAdd(item)}>Add</button>
        ) : (
          <div className="qty-control">
            <button onClick={() => onQty(item.id, -1)}>−</button>
            <span className="qty-num">{quantity}</span>
            <button onClick={() => onQty(item.id, 1)}>+</button>
          </div>
        )}
      </div>
    </div>
  );
}
