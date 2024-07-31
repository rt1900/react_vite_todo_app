import { useState } from 'react';

export default function NoteModal({ note, onSave, onClose }) {
  const [title, setTitle] = useState(note.title);
  const [text, setText] = useState(note.text);

  const handleSave = () => {
    onSave({ ...note, title, text });
  };

  return (
    <div className="modal">
      <div className="modal-content">
        <h2>Edit Note</h2>
        <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Title"
        />
        <textarea
            value={text}
            onChange={(e) => setText(e.target.value)}
            placeholder="Note"
        ></textarea>

        <button onClick={handleSave}>Save</button>
        <button onClick={onClose}>Close</button>
      </div>
    </div>
  );
}