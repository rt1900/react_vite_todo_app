import { useState } from 'react';

export default function NoteModal({ note, onSave, onClose }) {
  const [text, setText] = useState(note.text);

  const handleSave = () => {
    onSave({ ...note, text });
  };

  return (
    <div className="modal">
      <div className="modal-content">
        <h2>Edit Note</h2>
        <textarea value={text} onChange={(e) => setText(e.target.value)}></textarea>
        <button onClick={handleSave}>Save</button>
        <button onClick={onClose}>Close</button>
      </div>
    </div>
  );
}