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
        <h2 className="justDoIt">Just write it!</h2>
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

        <button onClick={handleSave} className="save">Save</button>
        <button onClick={onClose} className="close">Close</button>
      </div>
    </div>
  );
}